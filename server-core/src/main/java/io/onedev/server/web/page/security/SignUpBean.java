package io.cheeta.server.web.page.security;

import io.cheeta.server.Cheeta;
import io.cheeta.server.annotation.Editable;
import io.cheeta.server.service.SettingService;
import io.cheeta.server.model.User;
import io.cheeta.server.util.patternset.PatternSet;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Editable
public class SignUpBean extends User {

	private static final long serialVersionUID = 1L;

	public static final String PROP_EMAIL_ADDRESS = "emailAddress";
	
	private String emailAddress;

	@Editable(order=1000, descriptionProvider = "getEmailAddressDescription")
	@NotEmpty
	@Email
	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	
	@SuppressWarnings("unused")
	private static String getEmailAddressDescription() {
		var allowedSelfRegisterEmailDomain = Cheeta.getInstance(SettingService.class).getSecuritySetting().getAllowedSelfRegisterEmailDomain();
		if (allowedSelfRegisterEmailDomain != null) {
			var patternSet = PatternSet.parse(allowedSelfRegisterEmailDomain);
			var description = new StringBuilder();
			if (!patternSet.getIncludes().isEmpty())
				description.append("Allowed email domains: " + patternSet.getIncludes());
			if (description.length() != 0)
				description.append(", disallowed email domains: " + patternSet.getExcludes());
			else
				description.append("Disallowed email domains: " + patternSet.getExcludes());				
			return description.toString();
		} else {
			return null;
		}
	}
	
}
