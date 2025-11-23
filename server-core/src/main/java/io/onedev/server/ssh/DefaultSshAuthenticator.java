package io.cheeta.server.ssh;

import io.cheeta.server.service.SettingService;
import io.cheeta.server.service.SshKeyService;
import io.cheeta.server.model.SshKey;
import io.cheeta.server.model.User;
import io.cheeta.server.persistence.annotation.Sessional;
import org.apache.sshd.common.AttributeRepository.AttributeKey;
import org.apache.sshd.common.config.keys.KeyUtils;
import org.apache.sshd.common.digest.BuiltinDigests;
import org.apache.sshd.server.auth.AsyncAuthException;
import org.apache.sshd.server.session.ServerSession;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;

@Singleton
public class DefaultSshAuthenticator implements SshAuthenticator {

    private static final AttributeKey<Long> ATTR_PUBLIC_KEY_OWNER_ID = new AttributeKey<>();

	private final SshKeyService sshKeyService;
	
	private final SettingService settingService;

	@Inject
	public DefaultSshAuthenticator(SshKeyService sshKeyService, SettingService settingService) {
		this.sshKeyService = sshKeyService;
		this.settingService = settingService;
	}
	
	@Sessional
	@Override
	public boolean authenticate(String username, PublicKey key, ServerSession session) 
			throws AsyncAuthException {
		try {
			PrivateKey privateKey = settingService.getSshSetting().getPrivateKey();
			if (key.equals(KeyUtils.recoverPublicKey(privateKey))) {
				session.setAttribute(ATTR_PUBLIC_KEY_OWNER_ID, User.SYSTEM_ID);
				return true;
			}
		} catch (GeneralSecurityException e) {
			throw new RuntimeException(e);
		}
		
        String digest = KeyUtils.getFingerPrint(BuiltinDigests.sha256, key);  
        SshKey sshKey = sshKeyService.findByFingerprint(digest);
        if (sshKey != null) {
            session.setAttribute(ATTR_PUBLIC_KEY_OWNER_ID, sshKey.getOwner().getId());
            return true;
        } else {
        	return false;
        }
	}

	@Override
	public Long getPublicKeyOwnerId(ServerSession session) {
		return session.getAttribute(ATTR_PUBLIC_KEY_OWNER_ID);
	}

}
