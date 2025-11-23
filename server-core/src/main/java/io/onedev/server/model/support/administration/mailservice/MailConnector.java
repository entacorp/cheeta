package io.cheeta.server.model.support.administration.mailservice;

import java.io.Serializable;
import java.util.Collection;

import org.jspecify.annotations.Nullable;

import io.cheeta.server.annotation.Editable;
import io.cheeta.server.mail.InboxMonitor;

@Editable
public interface MailConnector extends Serializable {

	String getSystemAddress();
	
	void sendMail(Collection<String> toList, Collection<String> ccList, Collection<String> bccList,
				  String subject, String htmlBody, String textBody, @Nullable String replyAddress,
				  @Nullable String senderName, @Nullable String references, boolean testMode);

	@Nullable
	InboxMonitor getInboxMonitor(boolean testMode);

}
