package io.cheeta.server.service;

import org.jspecify.annotations.Nullable;

import io.cheeta.server.git.signatureverification.gpg.GpgSigningKey;
import io.cheeta.server.model.GpgKey;

public interface GpgKeyService extends EntityService<GpgKey> {

    @Nullable
	GpgSigningKey findSigningKey(long keyId);

    void create(GpgKey gpgKey);
}
