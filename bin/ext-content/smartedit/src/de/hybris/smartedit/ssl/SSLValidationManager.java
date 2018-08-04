/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.smartedit.ssl;

import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.annotation.PostConstruct;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The SSL validation manager makes it possible to enable or disable SSL validation.
 */
public class SSLValidationManager
{
	public static final Logger LOG = LoggerFactory.getLogger(SSLValidationManager.class);

	private final boolean enableSSLValidation;

	/**
	 * Constructor.
	 * @param enable_ssl_validation true to enable SSL validation, false otherwise
	 */
	public SSLValidationManager(final boolean enable_ssl_validation)
	{
		this.enableSSLValidation = enable_ssl_validation;
	}

	/**
	 * Disables SSL validation.
	 * @throws Exception if an error occurs while disabling SSL validation
	 */
	@PostConstruct
	public void disableSSLValidation() throws Exception
	{
		if (!enableSSLValidation)
		{
			final SSLContext sslc = SSLContext.getInstance("TLS");
			final TrustManager[] trustManagerArray =
			{ new NullX509TrustManager() };
			sslc.init(null, trustManagerArray, null);
			HttpsURLConnection.setDefaultSSLSocketFactory(sslc.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(new NullHostnameVerifier());
		}
	}

	/**
	 * The default X509 certificate trust manager.
	 */
	private static class NullX509TrustManager extends X509ExtendedTrustManager
	{
		@Override
		public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException
		{
			// Intentionally left empty.
		}

		@Override
		public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException
		{
			// Intentionally left empty.
		}

		@Override
		public X509Certificate[] getAcceptedIssuers()
		{
			return new X509Certificate[0];
		}

		@Override
		public void checkClientTrusted(final X509Certificate[] arg0, final String arg1, final Socket arg2)
				throws CertificateException
		{
			// Intentionally left empty.
		}

		@Override
		public void checkClientTrusted(final X509Certificate[] arg0, final String arg1, final SSLEngine arg2)
				throws CertificateException
		{
			// Intentionally left empty.
		}

		@Override
		public void checkServerTrusted(final X509Certificate[] arg0, final String arg1, final Socket arg2)
				throws CertificateException
		{
			// Intentionally left empty.
		}

		@Override
		public void checkServerTrusted(final X509Certificate[] arg0, final String arg1, final SSLEngine arg2)
				throws CertificateException
		{
			// Intentionally left empty.
		}
	}

	/**
	 * The default hostname verifier. It always returns true.
	 */
	private static class NullHostnameVerifier implements HostnameVerifier
	{
		@Override
		public boolean verify(final String hostname, final SSLSession session)
		{
			return true;
		}
	}

}
