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
package com.hybris.ymkt.recommendation.services;

import de.hybris.bootstrap.annotations.ManualTest;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.security.SecureRandom;

import javax.annotation.Resource;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.hybris.ymkt.common.odata.ODataService;
import com.hybris.ymkt.recommendation.dao.RecommendationScenario;
import com.hybris.ymkt.recommendation.dao.RecommendationScenario.Scenario;


@ManualTest
public class RecommendationServiceTest extends ServicelayerTransactionalTest
{
	private static final String PROD_RECO_RUNTIME_SRV = "/sap/opu/odata/sap/PROD_RECO_RUNTIME_SRV/";
	private static final String SCENARIO_ID = "YUE_SCENARIO_ID";

	@Resource(name = "defaultConfigurationService")
	private ConfigurationService configurationService;

	@Resource(name = "ODataService_PROD_RECO_RUNTIME_SRV")
	private ODataService oDataService;

	private final RecommendationService recoService = new RecommendationService();

	public static void disableCertificates() throws Exception
	{
		final TrustManager[] trustAllCerts =
		{ (TrustManager) Proxy.getProxyClass(X509TrustManager.class.getClassLoader(), X509TrustManager.class)
				.getConstructor(InvocationHandler.class).newInstance((InvocationHandler) (o, m, args) -> null) };

		final SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	}

	@Before
	public void setUp() throws Exception
	{
		disableCertificates();

		oDataService.setRootUrl(
				configurationService.getConfiguration().getString("sapymktcommon.odata.url.root") + PROD_RECO_RUNTIME_SRV);
		oDataService.setUser(configurationService.getConfiguration().getString("sapymktcommon.odata.url.user"));
		oDataService.setPassword(configurationService.getConfiguration().getString("sapymktcommon.odata.url.password"));
		oDataService.setSapClient(configurationService.getConfiguration().getString("sapymktcommon.odata.url.sap-client"));

		recoService.setODataService(oDataService);
	}

	@Test
	public void testWithResult() throws Exception
	{
		final RecommendationScenario recoScenario = new RecommendationScenario("6de4ae57e795a737", "COOKIE_ID");
		recoScenario.getScenarios().add(new Scenario(SCENARIO_ID));
		recoService.executeRecommendationScenario(recoScenario, false);
		Assert.assertNotNull(recoScenario);
		Assert.assertFalse(recoScenario.getScenarioHashes().isEmpty());
		Assert.assertFalse(recoScenario.getResultObjects().isEmpty());
	}

	@Test
	public void testValidScenarioHash() throws Exception
	{
		Assert.assertTrue(recoService.validateScenarioHash("3FB9A008CF3A86022A88A581846E070F"));

		Assert.assertFalse(recoService.validateScenarioHash(null));
		Assert.assertFalse(recoService.validateScenarioHash(""));
		Assert.assertFalse(recoService.validateScenarioHash("3FB9A008CF3A86022A88A581846E070"));
		Assert.assertFalse(recoService.validateScenarioHash("3FB9A008CF3A86022A88A581846E070F1"));
		Assert.assertFalse(recoService.validateScenarioHash("3fB9A008CF3A86022A88A581846E070F"));
		Assert.assertFalse(recoService.validateScenarioHash("3=B9A008CF3A86022A88A581846E070F"));
	}

	@Test
	public void testHashOnly() throws Exception
	{
		final RecommendationScenario recoScenario = new RecommendationScenario("6de4ae57e795a737", "COOKIE_ID");
		recoScenario.getScenarios().add(new Scenario(SCENARIO_ID));
		recoService.executeRecommendationScenario(recoScenario, true);
		Assert.assertNotNull(recoScenario);
		Assert.assertFalse(recoScenario.getScenarioHashes().isEmpty());
		Assert.assertTrue(recoScenario.getResultObjects().isEmpty());

		recoService.executeRecommendationScenario(recoScenario, false);
		Assert.assertNotNull(recoScenario);
		Assert.assertFalse(recoScenario.getScenarioHashes().isEmpty());
		Assert.assertFalse(recoScenario.getResultObjects().isEmpty());
	}

}
