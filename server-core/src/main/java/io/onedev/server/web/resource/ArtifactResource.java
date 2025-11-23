package io.cheeta.server.web.resource;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import io.cheeta.commons.utils.ExplicitException;
import io.cheeta.commons.utils.StringUtils;
import io.cheeta.k8shelper.KubernetesHelper;
import io.cheeta.server.Cheeta;
import io.cheeta.server.cluster.ClusterService;
import io.cheeta.server.service.BuildService;
import io.cheeta.server.service.ProjectService;
import io.cheeta.server.model.Build;
import io.cheeta.server.model.Project;
import io.cheeta.server.security.SecurityUtils;
import io.cheeta.server.util.IOUtils;
import io.cheeta.server.util.artifact.FileInfo;
import io.cheeta.server.web.util.MimeUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.tika.mime.MimeTypes;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.AbstractResource;

import javax.persistence.EntityNotFoundException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static io.cheeta.commons.utils.LockUtils.read;
import static io.cheeta.server.model.Build.getArtifactsLockName;
import static io.cheeta.server.util.IOUtils.BUFFER_SIZE;

public class ArtifactResource extends AbstractResource {

	private static final long serialVersionUID = 1L;

	private static final String PARAM_PROJECT = "project";

	private static final String PARAM_BUILD = "build";

	@Override
	protected ResourceResponse newResourceResponse(Attributes attributes) {
		PageParameters params = attributes.getParameters();

		Long projectId = params.get(PARAM_PROJECT).toLong();
		Long buildNumber = params.get(PARAM_BUILD).toLong();
		
		List<String> pathSegments = new ArrayList<>();

		for (int i = 0; i < params.getIndexedCount(); i++) {
			String pathSegment = params.get(i).toString();
			if (pathSegment.contains(".."))
				throw new ExplicitException("Invalid request path");
			if (pathSegment.length() != 0)
				pathSegments.add(pathSegment);
		}
		
		if (pathSegments.isEmpty())
			throw new ExplicitException("Artifact path has to be specified");
		
		String artifactPath = Joiner.on("/").join(pathSegments);
		
		FileInfo fileInfo = null;
		if (!SecurityUtils.isSystem()) {
			Project project = Cheeta.getInstance(ProjectService.class).load(projectId);
			
			Build build = Cheeta.getInstance(BuildService.class).find(project, buildNumber);

			if (build == null) {
				String message = String.format("Unable to find build (project: %s, build number: %d)", 
						project.getPath(), buildNumber);
				throw new EntityNotFoundException(message);
			}
			
			if (!SecurityUtils.canAccessBuild(build))
				throw new UnauthorizedException();
			
			fileInfo = (FileInfo) getBuildService().getArtifactInfo(build, artifactPath);
		}
		
		ResourceResponse response = new ResourceResponse();
		response.getHeaders().addHeader("X-Content-Type-Options", "nosniff");
		response.disableCaching();

		String fileName = artifactPath;
		if (fileName.contains("/"))
			fileName = StringUtils.substringAfterLast(fileName, "/");
		try {
			response.setFileName(URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		
		if (fileInfo != null) {
			response.setContentLength(fileInfo.getLength());
			response.setContentType(MimeUtils.sanitize(fileInfo.getMediaType()));
		} else {
			response.setContentType(MimeTypes.OCTET_STREAM);
		}
		
		response.setWriteCallback(new WriteCallback() {

			@Override
			public void writeData(Attributes attributes) throws IOException {
				ProjectService projectService = Cheeta.getInstance(ProjectService.class);
				String activeServer = projectService.getActiveServer(projectId, true);
				ClusterService clusterService = Cheeta.getInstance(ClusterService.class);
				if (activeServer.equals(clusterService.getLocalServerAddress())) {
					read(getArtifactsLockName(projectId, buildNumber), () -> {
						File artifactFile = new File(getBuildService().getArtifactsDir(projectId, buildNumber), artifactPath);
						try (
								InputStream is = new FileInputStream(artifactFile);
								OutputStream os = attributes.getResponse().getOutputStream()) {
							IOUtils.copy(is, os, BUFFER_SIZE);
						}
						return null;
					});
				} else {
	    			Client client = ClientBuilder.newClient();
	    			try {
	    				CharSequence path = RequestCycle.get().urlFor(
	    						new ArtifactResourceReference(), 
	    						ArtifactResource.paramsOf(projectId, buildNumber, artifactPath));
	    				String activeServerUrl = clusterService.getServerUrl(activeServer);
	    				
	    				WebTarget target = client.target(activeServerUrl).path(path.toString());
	    				Invocation.Builder builder =  target.request();
	    				builder.header(HttpHeaders.AUTHORIZATION, 
	    						KubernetesHelper.BEARER + " " + clusterService.getCredential());
	    				
	    				try (Response response = builder.get()) {
	    					KubernetesHelper.checkStatus(response);
	    					try (
	    							InputStream is = response.readEntity(InputStream.class);
	    							OutputStream os = attributes.getResponse().getOutputStream()) {
	    						IOUtils.copy(is, os, BUFFER_SIZE);
	    					} 
	    				} 
	    			} finally {
	    				client.close();
	    			}
				}
			}			
			
		});

		return response;
	}
	
	private BuildService getBuildService() {
		return Cheeta.getInstance(BuildService.class);
	}
	
	public static PageParameters paramsOf(Long projectId, Long buildNumber, String path) {
		PageParameters params = new PageParameters();
		params.set(PARAM_PROJECT, projectId);
		params.set(PARAM_BUILD, buildNumber);
		
		int index = 0;
		for (String segment: Splitter.on("/").split(path)) {
			params.set(index, segment);
			index++;
		}
		return params;
	}

}
