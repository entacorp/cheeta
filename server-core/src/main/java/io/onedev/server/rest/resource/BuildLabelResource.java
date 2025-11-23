package io.cheeta.server.rest.resource;

import io.cheeta.server.service.BuildLabelService;
import io.cheeta.server.model.BuildLabel;
import io.cheeta.server.rest.annotation.Api;
import io.cheeta.server.security.SecurityUtils;
import org.apache.shiro.authz.UnauthorizedException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/build-labels")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Singleton
public class BuildLabelResource {

	private final BuildLabelService buildLabelService;

	@Inject
	public BuildLabelResource(BuildLabelService buildLabelService) {
		this.buildLabelService = buildLabelService;
	}
	
	@Api(order=200, description="Create build label")
	@POST
	public Long createLabel(@NotNull BuildLabel buildLabel) {
		if (!SecurityUtils.canManageBuild(buildLabel.getBuild()))
			throw new UnauthorizedException();
		buildLabelService.create(buildLabel);
		return buildLabel.getId();
	}
	
	@Api(order=300)
	@Path("/{buildLabelId}")
	@DELETE
	public Response deleteLabel(@PathParam("buildLabelId") Long buildLabelId) {
		BuildLabel buildLabel = buildLabelService.load(buildLabelId);
		if (!SecurityUtils.canManageBuild(buildLabel.getBuild()))
			throw new UnauthorizedException();
		buildLabelService.delete(buildLabel);
		return Response.ok().build();
	}
	
}
