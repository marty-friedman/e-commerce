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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsDocument;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.DummyProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceSummaryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.services.ProductConfigSessionAttributeContainer;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.session.impl.DefaultSession;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


@SuppressWarnings("javadoc")
@UnitTest
/**
 * Unit tests for {@link SessionAccessServiceImpl}
 */
public class SessionAccessServiceImplTest
{
	private static final String SESSION_ID = "123";

	private long startTime;
	private static final long unblockedMaximumExecutionTime = 100;
	private static final long blockingTime = 250;
	private final List<Long> times = new ArrayList<>();
	private long firstThreadTime;
	private long secondThreadTime;


	SessionAccessServiceImpl classUnderTest = new SessionAccessServiceImpl();

	private class SessionAccessThread extends Thread
	{
		@Override
		public void run()
		{
			classUnderTest.retrieveSessionAttributeContainer();
		}
	}

	@Mock
	private SessionService sessionService;

	@Mock
	private Session session;


	private final Session session2 = new DefaultSession();

	private final ProductConfigSessionAttributeContainer sessionContainer = new ProductConfigSessionAttributeContainer();

	private static final String configId = "1";


	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		Mockito.when(session.getSessionId()).thenReturn(SESSION_ID);
		Mockito.when(sessionService.getCurrentSession()).thenReturn(session);
		Mockito.when(sessionService.getAttribute(SessionAccessServiceImpl.PRODUCT_CONFIG_SESSION_ATTRIBUTE_CONTAINER))
				.thenReturn(sessionContainer);

		classUnderTest.setSessionService(sessionService);

	}

	@Test
	public void testSessionService()
	{
		assertNotNull(classUnderTest.getSessionId());
	}

	@Test
	public void testCartEntryConfigId()
	{
		final String configId = "1";
		final String cartEntryKey = "X";
		classUnderTest.setConfigIdForCartEntry(cartEntryKey, configId);
		assertEquals(configId, classUnderTest.getConfigIdForCartEntry(cartEntryKey));
	}

	@Test
	public void testUIStatus()
	{
		final String cartEntryKey = "X";
		final Object status = "S";
		classUnderTest.setUiStatusForCartEntry(cartEntryKey, status);
		assertEquals(status, classUnderTest.getUiStatusForCartEntry(cartEntryKey));
		classUnderTest.removeUiStatusForCartEntry(cartEntryKey);
		assertNull(classUnderTest.getUiStatusForCartEntry(cartEntryKey));
	}


	@Test
	public void testUIStatusProduct()
	{
		final String productKey = "X";
		final Object status = "S";
		classUnderTest.setUiStatusForProduct(productKey, status);
		assertEquals(status, classUnderTest.getUiStatusForProduct(productKey));
		classUnderTest.removeUiStatusForProduct(productKey);
		assertNull(classUnderTest.getUiStatusForProduct(productKey));
	}

	@Test
	public void testConfigIdForCartEntry()
	{
		final String cartEntryKey = "X";
		classUnderTest.setConfigIdForCartEntry(cartEntryKey, configId);
		assertEquals(cartEntryKey, classUnderTest.getCartEntryForConfigId(configId));
	}

	@Test
	public void testConfigIdForCartEntryEnsureLastAssignmentWins()
	{

		final String cartEntryKey = "X";
		final String cartEntryKeySecond = "Y";
		classUnderTest.setConfigIdForCartEntry(cartEntryKey, configId);
		classUnderTest.setConfigIdForCartEntry(cartEntryKeySecond, configId);
		assertEquals(cartEntryKeySecond, classUnderTest.getCartEntryForConfigId(configId));
	}

	@Test
	public void testConfigIdForCartEntryEnsureFirstAssignmentGone()
	{

		final String cartEntryKey = "X";
		final String cartEntryKeySecond = "Y";
		classUnderTest.setConfigIdForCartEntry(cartEntryKey, configId);
		classUnderTest.setConfigIdForCartEntry(cartEntryKeySecond, configId);
		assertNull(classUnderTest.getConfigIdForCartEntry(cartEntryKey));
	}

	@Test
	public void testRemoveConfigIdForCartEntry()
	{
		final String cartEntryKey = "X";
		classUnderTest.setConfigIdForCartEntry(cartEntryKey, configId);
		classUnderTest.removeConfigIdForCartEntry(cartEntryKey);
		assertNull(classUnderTest.getCartEntryForConfigId(configId));
	}

	@Test
	public void testCartEntryForProduct()
	{
		final String cartEntryId = "1";
		final String productKey = "X";
		classUnderTest.setCartEntryForProduct(productKey, cartEntryId);
		assertEquals(cartEntryId, classUnderTest.getCartEntryForProduct(productKey));
		classUnderTest.removeCartEntryForProduct(productKey);
		assertNull(classUnderTest.getCartEntryForProduct(productKey));
	}

	@Test
	public void testRemoveSessionArtifactsForCartEntryCartEntryMap()
	{
		final String cartEntryId = "1";
		final String productKey = "X";
		classUnderTest.setCartEntryForProduct(productKey, cartEntryId);
		assertEquals(cartEntryId, classUnderTest.getCartEntryForProduct(productKey));
		classUnderTest.removeSessionArtifactsForCartEntry(cartEntryId, productKey);
		//We expect that the corresponding product/cartEntry entry is gone!
		assertNull(classUnderTest.getCartEntryForProduct(productKey));
	}

	@Test
	public void testRemoveSessionArtifactsForCartEntryConfigMap()
	{
		final String cartEntryKey = "X";
		classUnderTest.setConfigIdForCartEntry(cartEntryKey, configId);
		classUnderTest.removeSessionArtifactsForCartEntry(cartEntryKey, "");
		assertNull(classUnderTest.getConfigIdForCartEntry(cartEntryKey));
	}

	@Test
	public void testGetSolrProperties()
	{
		final Set<String> solrProperties = new HashSet<>();
		classUnderTest.setSolrIndexedProperties(solrProperties);
		assertEquals(solrProperties, classUnderTest.getSolrIndexedProperties());
	}

	@Test
	public void testConfigurationProvider()
	{
		final ConfigurationProvider provider = new DummyProvider();
		classUnderTest.setConfigurationProvider(provider);
		assertEquals(provider, classUnderTest.getConfigurationProvider());
	}

	@Test
	public void testConfigurationModelEngineState()
	{
		final ConfigModel configModel = new ConfigModelImpl();
		classUnderTest.setConfigurationModelEngineState(configId, configModel);
		assertEquals(configModel, classUnderTest.getConfigurationModelEngineState(configId));
		classUnderTest.removeConfigAttributeState(configId);
		assertNull(classUnderTest.getConfigurationModelEngineState(configId));
	}

	@Test
	public void testPriceSummaryState()
	{
		final PriceSummaryModel priceSummary = new PriceSummaryModel();
		classUnderTest.setPriceSummaryState(configId, priceSummary);
		assertEquals(priceSummary, classUnderTest.getPriceSummaryState(configId));
		classUnderTest.removeConfigAttributeState(configId);
		assertNull(classUnderTest.getPriceSummaryState(configId));
	}

	@Test
	public void testRemoveConfigAttributeState()
	{
		final String configId = "ID";
		final PriceSummaryModel priceSummary = new PriceSummaryModel();
		classUnderTest.setPriceSummaryState(configId, priceSummary);
		final ConfigModel configModel = new ConfigModelImpl();
		classUnderTest.setConfigurationModelEngineState(configId, configModel);
		classUnderTest.setAnalyticData(configId, new AnalyticsDocument());

		classUnderTest.removeConfigAttributeState(configId);
		assertNull(classUnderTest.getPriceSummaryState(configId));
		assertNull(classUnderTest.getConfigurationModelEngineState(configId));
		assertNull(classUnderTest.getAnalyticData(configId));

	}

	@Test
	public void testRetrieveSessionAttributeContainerThreadsWait() throws InterruptedException
	{
		delaySynchronizedCall();

		final SessionAccessThread firstThread = new SessionAccessThread();
		firstThread.start();
		final SessionAccessThread secondThread = new SessionAccessThread();
		secondThread.start();
		Thread.sleep(blockingTime + unblockedMaximumExecutionTime);
		checkBothThreadsFinished();
		assertTrue("First thread took too long: " + firstThreadTime, firstThreadTime < unblockedMaximumExecutionTime);
		assertTrue("First thread took not long enough: " + secondThreadTime, secondThreadTime >= blockingTime);
	}


	@Test
	public void testRetrieveSessionAttributeContainerThreadsDontWait() throws InterruptedException
	{
		delaySynchronizedCall();

		final SessionAccessThread firstThread = new SessionAccessThread();
		firstThread.start();
		Thread.sleep(unblockedMaximumExecutionTime);

		//Now simulate a different user session so that threads don't need to wait for each other
		Mockito.when(sessionService.getCurrentSession()).thenReturn(session2);

		final SessionAccessThread secondThread = new SessionAccessThread();
		secondThread.start();
		Thread.sleep(unblockedMaximumExecutionTime);
		checkBothThreadsFinished();
		assertTrue("First thread took too long: " + firstThreadTime, firstThreadTime < unblockedMaximumExecutionTime);
		assertTrue("Second thread took too long: " + secondThreadTime, secondThreadTime < blockingTime);

	}

	protected void checkBothThreadsFinished()
	{
		assertEquals("We expect both threads to be finished", 2, times.size());
		firstThreadTime = times.get(0).longValue();
		secondThreadTime = times.get(1).longValue();

	}

	protected void delaySynchronizedCall()
	{
		startTime = System.currentTimeMillis();
		times.clear();
		Mockito.doAnswer(new Answer()
		{
			@Override
			public Void answer(final InvocationOnMock invocation) throws Throwable
			{
				times.add(Long.valueOf(System.currentTimeMillis() - startTime));
				Thread.sleep(blockingTime);
				return null;
			}
		}).when(sessionService).getAttribute(SessionAccessServiceImpl.PRODUCT_CONFIG_SESSION_ATTRIBUTE_CONTAINER);
	}


	@Test
	public void testPurge()
	{
		classUnderTest.purge();
		Mockito.verify(sessionService).setAttribute(SessionAccessServiceImpl.PRODUCT_CONFIG_SESSION_ATTRIBUTE_CONTAINER, null);
	}

	@Test
	public void testConfigCacheGrowsNotEndless()
	{
		classUnderTest.setConfigurationModelEngineState("configID", new ConfigModelImpl());
		final int maxCachedConfigs = classUnderTest.getMaxCachedConfigsInSession();
		for (int ii = 0; ii <= maxCachedConfigs; ii++)
		{
			final String configId = String.valueOf(ii);
			classUnderTest.setConfigurationModelEngineState(configId, new ConfigModelImpl());
		}

		assertNull(classUnderTest.getConfigurationModelEngineState("configID"));
		assertEquals(classUnderTest.getMaxCachedConfigsInSession() / 2 + 2,
				classUnderTest.retrieveSessionAttributeContainer().getConfigurationModelEngineStates().size());
	}

	@Test
	public void testOldConfigsAreStillAvailable()
	{
		assertEquals(0, classUnderTest.retrieveSessionAttributeContainer().getConfigurationModelEngineStates().size());

		classUnderTest.setConfigurationModelEngineState("configID", new ConfigModelImpl());
		final int maxCachedConfigs = classUnderTest.getMaxCachedConfigsInSession() / 2 - 1;
		for (int ii = 0; ii <= maxCachedConfigs; ii++)
		{
			final String configId = String.valueOf(ii);
			classUnderTest.setConfigurationModelEngineState(configId, new ConfigModelImpl());
			assertEquals(ii + 2, classUnderTest.retrieveSessionAttributeContainer().getConfigurationModelEngineStates().size());
		}

		assertNotNull(classUnderTest.getConfigurationModelEngineState("configID"));
		assertEquals(classUnderTest.getMaxCachedConfigsInSession() / 2 + 1,
				classUnderTest.retrieveSessionAttributeContainer().getConfigurationModelEngineStates().size());
	}
}
