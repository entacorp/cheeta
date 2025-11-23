package io.cheeta.server.security;

import io.cheeta.server.Cheeta;
import nl.altindag.ssl.SSLFactory;

import javax.net.ssl.SSLSocketFactory;

public abstract class TrustCertsSSLSocketFactory extends SSLSocketFactory {
	
	public static SSLSocketFactory getDefault() {
		return Cheeta.getInstance(SSLFactory.class).getSslSocketFactory();
	}
	
}
