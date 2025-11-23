package io.cheeta.server.event.project.pack;

import java.util.Date;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.PackService;
import io.cheeta.server.event.project.ProjectEvent;
import io.cheeta.server.model.Pack;
import io.cheeta.server.model.User;
import io.cheeta.server.web.UrlService;

public abstract class PackEvent extends ProjectEvent {

	private static final long serialVersionUID = 1L;
	
	private final Long packId;
	
	public PackEvent(User user, Date date, Pack pack) {
		super(user, date, pack.getProject());
		packId = pack.getId();
	}

	public Pack getPack() {
		return Cheeta.getInstance(PackService.class).load(packId);
	}

	@Override
	public String getUrl() {
		return Cheeta.getInstance(UrlService.class).urlFor(getPack(), true);
	}
	
}
