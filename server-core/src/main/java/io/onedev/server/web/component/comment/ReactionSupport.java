package io.cheeta.server.web.component.comment;

import java.util.Collection;

import org.apache.wicket.ajax.AjaxRequestTarget;

import io.cheeta.server.model.support.EntityReaction;

public interface ReactionSupport {

    Collection<? extends EntityReaction> getReactions();
	
	void onToggleEmoji(AjaxRequestTarget target, String emoji);

}
