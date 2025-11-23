package io.cheeta.server.web.resource;

import io.cheeta.k8shelper.KubernetesHelper;
import io.cheeta.server.Cheeta;
import io.cheeta.server.cluster.ClusterService;
import io.cheeta.server.service.BuildService;
import io.cheeta.server.service.ProjectService;
import io.cheeta.server.job.log.LogService;
import io.cheeta.server.model.Build;
import io.cheeta.server.model.Project;
import io.cheeta.server.security.SecurityUtils;
import io.cheeta.server.util.IOUtils;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static io.cheeta.server.util.IOUtils.BUFFER_SIZE;

public class BuildLogResource extends AbstractResource {

	private static final long serialVersionUID = 1L;

	private static final String PARAM_PROJECT = "project";
	
	private static final String PARAM_BUILD = "build";
	
	@Override
	protected ResourceResponse newResourceResponse(Attributes attributes) {
		PageParameters params = attributes.getParameters();

		Long projectId = params.get(PARAM_PROJECT).toLong();
		Long buildNumber = params.get(PARAM_BUILD).toOptionalLong();
		if (buildNumber == null)
			throw new IllegalArgumentException("build number has to be specified");

		if (!SecurityUtils.isSystem()) {
			Project project = Cheeta.getInstance(ProjectService.class).load(projectId);
			
			Build build = Cheeta.getInstance(BuildService.class).find(project, buildNumber);

			if (build == null) {
				String message = String.format("Unable to find build (project: %s, build number: %d)", 
						project.getPath(), buildNumber);
				throw new EntityNotFoundException(message);
			}
			
			if (!SecurityUtils.canAccessLog(build))
				throw new UnauthorizedException();
		}
		
		ResourceResponse response = new ResourceResponse();
		response.setContentType(MimeTypes.OCTET_STREAM);
		
		response.disableCaching();
		
		try {
			response.setFileName(URLEncoder.encode("build-log.txt", StandardCharsets.UTF_8.name()));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		response.setWriteCallback(new WriteCallback() {

			@Override
			public void writeData(Attributes attributes) throws IOException {
				ProjectService projectService = Cheeta.getInstance(ProjectService.class);
				String activeServer = projectService.getActiveServer(projectId, true);
				ClusterService clusterService = Cheeta.getInstance(ClusterService.class);
				LogService logService = Cheeta.getInstance(LogService.class);
				if (activeServer.equals(clusterService.getLocalServerAddress())) {
					try (
							InputStream is = logService.openLogStream(projectId, buildNumber);
							OutputStream os = attributes.getResponse().getOutputStream()) {
						IOUtils.copy(is, os, BUFFER_SIZE);
					}
				} else {
	    			Client client = ClientBuilder.newClient();
	    			try {
	    				CharSequence path = RequestCycle.get().urlFor(
	    						new BuildLogResourceReference(), 
	    						BuildLogResource.paramsOf(projectId, buildNumber));
	    				String activeServerUrl = clusterService.getServerUrl(activeServer) + path;
	    				
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

	public static PageParameters paramsOf(Long projectId, Long buildNumber) {
		PageParameters params = new PageParameters();
		params.set(PARAM_PROJECT, projectId);
		params.set(PARAM_BUILD, buildNumber);
		return params;
	}
	
}
