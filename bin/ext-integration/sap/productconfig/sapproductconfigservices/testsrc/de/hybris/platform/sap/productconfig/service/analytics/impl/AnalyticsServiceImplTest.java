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
package de.hybris.platform.sap.productconfig.service.analytics.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyZeroInteractions;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.AnalyticsProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.ProviderFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsDocument;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.service.testutil.DummySessionAccessService;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.analytics.impl.AnalyticsServiceImpl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@SuppressWarnings("javadoc")
@UnitTest
public class AnalyticsServiceImplTest
{
	private AnalyticsServiceImpl classUnderTest;

	@Mock
	private AnalyticsProvider mockedAnalyticsProvider;
	@Mock
	private ProviderFactory mockedProviderFactory;
	private SessionAccessService sessionAccessService;

	private ConfigModel config;
	private AnalyticsDocument analyticsDocument;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new AnalyticsServiceImpl();

		sessionAccessService = new DummySessionAccessService();
		classUnderTest.setSessionAccessService(sessionAccessService);
		classUnderTest.setProviderFactory(mockedProviderFactory);

		analyticsDocument = new AnalyticsDocument();
		config = new ConfigModelImpl();
		given(mockedProviderFactory.getAnalyticsProvider()).willReturn(mockedAnalyticsProvider);
		given(mockedAnalyticsProvider.getPopularity(config)).willReturn(analyticsDocument);

		sessionAccessService.setConfigurationModelEngineState("123", config);
	}

	@Test
	public void testGetAnalyticsData()
	{
		final AnalyticsDocument analyticData = classUnderTest.getAnalyticData("123");
		assertNotNull(analyticData);
		assertSame(analyticsDocument, analyticData);
	}

	@Test
	public void testGetAnalyticsDataIsCached()
	{
		final AnalyticsDocument analyticData = classUnderTest.getAnalyticData("123");
		assertSame(analyticData, sessionAccessService.getAnalyticData("123"));
	}


	@Test
	public void testGetAnalyticsDataCached()
	{
		sessionAccessService.setAnalyticData("123", analyticsDocument);
		final AnalyticsDocument analyticData = classUnderTest.getAnalyticData("123");
		assertNotNull(analyticData);
		assertSame(analyticsDocument, analyticData);
		verifyZeroInteractions(mockedAnalyticsProvider);
	}

	@Test
	public void testIsActiveTrue()
	{
		given(Boolean.valueOf(mockedAnalyticsProvider.isActive())).willReturn(Boolean.TRUE);
		assertTrue(classUnderTest.isActive());
	}

	@Test
	public void testIsActiveFalse()
	{
		given(Boolean.valueOf(mockedAnalyticsProvider.isActive())).willReturn(Boolean.FALSE);
		assertFalse(classUnderTest.isActive());
	}




}
