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
package de.hybris.platform.sap.productconfig.services.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.AnalyticsProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.DefaultAnalyticsProviderImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.DummyPricingProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.DummyProvider;
import de.hybris.platform.sap.productconfig.service.testutil.DummySessionAccessService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;


@UnitTest
public class ProviderFactoryImplTest
{


	private ProviderFactoryImpl classUnderTest;
	private SessionAccessServiceImpl sessionAccessService;

	@Mock
	ApplicationContext mockApplicationContext;

	final ConfigurationProvider dummyConfigurationProvider = new DummyProvider();
	final ConfigurationProvider dummyConfigurationLocalProvider = new DummyProvider();
	final PricingProvider dummyPricingProvider = new DummyPricingProvider();
	final PricingProvider dummyPricingLocalProvider = new DummyPricingProvider();
	final AnalyticsProvider dummyAnalyticsProvider = new DefaultAnalyticsProviderImpl();

	@Before
	public void setUp()
	{

		classUnderTest = new ProviderFactoryImpl();
		classUnderTest = Mockito.spy(classUnderTest);
		MockitoAnnotations.initMocks(this);


		sessionAccessService = new DummySessionAccessService();

		classUnderTest.setSessionAccessService(sessionAccessService);
		classUnderTest.setApplicationContext(mockApplicationContext);

		Mockito.when(mockApplicationContext.getBean("sapProductConfigConfigurationProvider"))
				.thenReturn(dummyConfigurationProvider);
		Mockito.when(mockApplicationContext.getBean("sapProductConfigLocalConfigurationProvider"))
				.thenReturn(dummyConfigurationLocalProvider);

		Mockito.when(mockApplicationContext.getBean("sapProductConfigPricingProvider")).thenReturn(dummyPricingProvider);
		Mockito.when(mockApplicationContext.getBean("sapProductConfigLocalConfigurationProvider"))
				.thenReturn(dummyPricingLocalProvider);

		Mockito.when(mockApplicationContext.getBean("sapProductConfigAnalyticsProvider")).thenReturn(dummyAnalyticsProvider);
	}

	@Test
	public void testGetConfigurationProvider_newSession()
	{
		sessionAccessService.setConfigurationProvider(null);
		final ConfigurationProvider provider = classUnderTest.getConfigurationProvider();
		assertNotNull(provider);
		assertSame(dummyConfigurationProvider, provider);
	}

	@Test
	public void testGetConfigurationProvider_existingSession()
	{
		sessionAccessService.setConfigurationProvider(dummyConfigurationProvider);
		final ConfigurationProvider provider = classUnderTest.getConfigurationProvider();
		assertNotNull(provider);
		assertSame(dummyConfigurationProvider, provider);
	}

	@Test
	public void testGetPricingProvider_newSession()
	{
		sessionAccessService.setPricingProvider(null);
		final PricingProvider provider = classUnderTest.getPricingProvider();
		assertNotNull(provider);
		assertSame(dummyPricingProvider, provider);
	}

	@Test
	public void testGetPricingProvider_existingSession()
	{
		sessionAccessService.setPricingProvider(dummyPricingProvider);
		final PricingProvider provider = classUnderTest.getPricingProvider();
		assertNotNull(provider);
		assertSame(dummyPricingProvider, provider);
	}

	@Test
	public void testGetSameAnalyticsProvider()
	{
		final AnalyticsProvider analyticsProvider = classUnderTest.getAnalyticsProvider();
		assertNotNull(analyticsProvider);
		final AnalyticsProvider sameAnalyticsProvider = classUnderTest.getAnalyticsProvider();
		assertSame(analyticsProvider, sameAnalyticsProvider);
	}

	@Test
	public void testGetSameAnalyticsProviderFromCache()
	{
		final AnalyticsProvider sessionAnylyticsProvider = new DefaultAnalyticsProviderImpl();
		sessionAccessService.setAnalyticsProvider(sessionAnylyticsProvider);
		final AnalyticsProvider analyticsProvider = classUnderTest.getAnalyticsProvider();
		assertNotNull(analyticsProvider);
		assertSame(sessionAnylyticsProvider, analyticsProvider);
	}

	@Test
	public void testGetAnalyticsproviderByApplicationContext()
	{
		final AnalyticsProvider analyticsProvider = classUnderTest.getAnalyticsProvider();
		assertNotNull(analyticsProvider);
		assertSame(dummyAnalyticsProvider, analyticsProvider);

	}
}
