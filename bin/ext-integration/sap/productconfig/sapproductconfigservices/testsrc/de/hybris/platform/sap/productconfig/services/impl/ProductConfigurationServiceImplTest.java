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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.ProviderFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.SolvableConflictModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.PriceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.SolvableConflictModelImpl;
import de.hybris.platform.sap.productconfig.service.testutil.DummySessionAccessService;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.tracking.TrackingRecorder;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;


@SuppressWarnings("javadoc")
@UnitTest
public class ProductConfigurationServiceImplTest
{

	private static final String PRODUCT_KEY = "product key";

	private static Logger LOG = Logger.getLogger(ProductConfigurationServiceImplTest.class);

	@Mock
	private TrackingRecorder recorder;

	static class ThreadBlocking extends Thread
	{

		private static final long WAIT_TIME = 500;

		@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "SWL_SLEEP_WITH_LOCK_HELD", justification = "Intended behaviour to test that locking is working")
		@Override
		public void run()
		{
			synchronized (ProductConfigurationServiceImpl.PROVIDER_LOCK)
			{
				try
				{
					Thread.sleep(WAIT_TIME);
				}
				catch (final InterruptedException e)
				{
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	static class ThreadAccessing extends Thread
	{
		long duration = 0;

		@Override
		public void run()
		{
			final long startTime = System.currentTimeMillis();
			ProductConfigurationServiceImpl.getLock(CONFIG_ID_1);
			duration = System.currentTimeMillis() - startTime;
		}
	}

	private static final String DUMMY_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><SOLUTION><CONFIGURATION CFGINFO=\"\" CLIENT=\"000\" COMPLETE=\"F\" CONSISTENT=\"T\" KBBUILD=\"3\" KBNAME=\"DUMMY_KB\" KBPROFILE=\"DUMMY_KB\" KBVERSION=\"3800\" LANGUAGE=\"E\" LANGUAGE_ISO=\"EN\" NAME=\"SCE 5.0\" ROOT_NR=\"1\" SCEVERSION=\" \"><INST AUTHOR=\"5\" CLASS_TYPE=\"300\" COMPLETE=\"F\" CONSISTENT=\"T\" INSTANCE_GUID=\"\" INSTANCE_ID=\"01\" NR=\"1\" OBJ_KEY=\"DUMMY_KB\" OBJ_TXT=\"Dummy KB\" OBJ_TYPE=\"MARA\" QTY=\"1.0\" UNIT=\"ST\"><CSTICS><CSTIC AUTHOR=\"8\" CHARC=\"DUMMY_CSTIC\" CHARC_TXT=\"Dummy CStic\" VALUE=\"8\" VALUE_TXT=\"Value 8\"/></CSTICS></INST><PARTS/><NON_PARTS/></CONFIGURATION><SALES_STRUCTURE><ITEM INSTANCE_GUID=\"\" INSTANCE_ID=\"1\" INSTANCE_NR=\"1\" LINE_ITEM_GUID=\"\" PARENT_INSTANCE_NR=\"\"/></SALES_STRUCTURE></SOLUTION>";
	private static final String NEW_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><SOLUTION><CONFIGURATION CFGINFO=\"\" CLIENT=\"000\" COMPLETE=\"F\" CONSISTENT=\"T\" KBBUILD=\"3\" KBNAME=\"DUMMY_KB\" KBPROFILE=\"DUMMY_KB\" KBVERSION=\"3800\" LANGUAGE=\"E\" LANGUAGE_ISO=\"EN\" NAME=\"SCE 5.0\" ROOT_NR=\"1\" SCEVERSION=\" \"><INST AUTHOR=\"5\" CLASS_TYPE=\"300\" COMPLETE=\"F\" CONSISTENT=\"T\" INSTANCE_GUID=\"\" INSTANCE_ID=\"01\" NR=\"1\" OBJ_KEY=\"DUMMY_KB\" OBJ_TXT=\"Dummy KB\" OBJ_TYPE=\"MARA\" QTY=\"1.0\" UNIT=\"ST\"><CSTICS><CSTIC AUTHOR=\"8\" CHARC=\"DUMMY_CSTIC\" CHARC_TXT=\"Dummy CStic\" VALUE=\"9\" VALUE_TXT=\"Value 9\"/></CSTICS></INST><PARTS/><NON_PARTS/></CONFIGURATION><SALES_STRUCTURE><ITEM INSTANCE_GUID=\"\" INSTANCE_ID=\"1\" INSTANCE_NR=\"1\" LINE_ITEM_GUID=\"\" PARENT_INSTANCE_NR=\"\"/></SALES_STRUCTURE></SOLUTION>";

	private static final String CONFIG_ID_2 = "asdasdwer4543556zgfhvchtr";
	private static final String CONFIG_ID_1 = "asdsafsdgftert6er6erzz";

	private ProductConfigurationServiceImpl cut;

	@Mock
	private ConfigurationProvider configurationProviderMock;

	@Mock
	private ConfigModel modelMock;

	@Mock
	private ProviderFactory providerFactoryMock;

	private static final String CONFIG_ID = "abc123";

	@Mock
	private CartEntryModel cartEntry;

	@Mock
	private ProductModel productModel;

	@Mock
	private SessionAccessService sessionAccessService;

	private static final long keyAsLong = 12;

	private final PK primaryKey = PK.fromLong(keyAsLong);

	private CommerceCartParameter parameters;

	private static final String configId = "1";

	private final ConfigModel configModel = new ConfigModelImpl();

	private final InstanceModel instanceModel = new InstanceModelImpl();

	@Before
	public void setup()
	{
		cut = Mockito.spy(new ProductConfigurationServiceImpl());
		MockitoAnnotations.initMocks(this);
		cut.setProviderFactory(providerFactoryMock);
		Mockito.when(providerFactoryMock.getConfigurationProvider()).thenReturn(configurationProviderMock);
		Mockito
				.when(configurationProviderMock.createConfigurationFromExternalSource(Mockito.any(KBKey.class), Mockito.anyString()))
				.thenReturn(configModel);
		Mockito.when(configurationProviderMock.createDefaultConfiguration((Mockito.any(KBKey.class)))).thenReturn(configModel);
		cut.setSessionAccessService(sessionAccessService);
		cut.setRecorder(recorder);

		Mockito.when(modelMock.getId()).thenReturn(CONFIG_ID);
		Mockito.when(cartEntry.getPk()).thenReturn(primaryKey);
		Mockito.when(cartEntry.getProduct()).thenReturn(productModel);
		Mockito.when(sessionAccessService.getConfigIdForCartEntry(primaryKey.toString())).thenReturn(configId);

		Mockito.when(sessionAccessService.getConfigurationModelEngineState(configId)).thenReturn(configModel);

		configModel.setRootInstance(instanceModel);
		configModel.setId(configId);
		instanceModel.setSubInstances(Collections.EMPTY_LIST);

		parameters = new CommerceCartParameter();
		parameters.setConfigId(CONFIG_ID);
	}

	@Test
	public void testRetrieveConfiguration() throws Exception
	{
		Mockito.when(configurationProviderMock.retrieveConfigurationModel(CONFIG_ID)).thenReturn(modelMock);

		final ConfigModel retrievedModel = cut.retrieveConfigurationModel(CONFIG_ID);

		assertTrue("Not delegated", retrievedModel == modelMock);
	}

	@Test
	public void testRetrieveExternalConfiguration() throws Exception
	{
		Mockito.when(configurationProviderMock.retrieveExternalConfiguration(CONFIG_ID)).thenReturn(DUMMY_XML);

		final String xmlString = cut.retrieveExternalConfiguration(CONFIG_ID);

		assertTrue("Not delegated", xmlString == DUMMY_XML);
	}

	@Test
	public void testRetrieveExternalConfigurationFailure() throws ConfigurationEngineException
	{
		Mockito.when(configurationProviderMock.retrieveExternalConfiguration(CONFIG_ID)).thenThrow(
				ConfigurationEngineException.class);

		try
		{
			cut.retrieveExternalConfiguration(CONFIG_ID);
			Assert.fail();
		}
		catch (final IllegalStateException ex)
		{
			Mockito.verify(sessionAccessService).purge();
		}

	}


	@Test
	public void testCreateConfigurationFromExternalSource() throws Exception
	{
		final KBKey kbKey = new KBKeyImpl("pCode");
		Mockito.when(configurationProviderMock.createConfigurationFromExternalSource(kbKey, "extConfig")).thenReturn(configModel);

		final ConfigModel craetedConfigModel = cut.createConfigurationFromExternal(kbKey, "extConfig");

		assertSame(configModel, craetedConfigModel);
		verify(sessionAccessService).setConfigurationModelEngineState(configId, craetedConfigModel);
		verifyNoMoreInteractions(sessionAccessService);

	}

	@Test
	public void testCreateConfigurationFromExternalSourceWithEntry() throws Exception
	{
		final KBKey kbKey = new KBKeyImpl("pCode");
		Mockito.when(configurationProviderMock.createConfigurationFromExternalSource(kbKey, "extConfig")).thenReturn(configModel);

		final ConfigModel craetedConfigModel = cut.createConfigurationFromExternal(kbKey, "extConfig", "123");

		assertSame(configModel, craetedConfigModel);
		verify(sessionAccessService).setConfigurationModelEngineState(configId, craetedConfigModel);
		verify(sessionAccessService).setConfigIdForCartEntry("123", configId);
	}


	private ConfigModel createConfigModel()
	{
		final PriceModel currentTotalPrice = new PriceModelImpl();
		final ConfigModel configModel = new ConfigModelImpl();
		configModel.setId(CONFIG_ID);
		currentTotalPrice.setCurrency("EUR");
		currentTotalPrice.setPriceValue(BigDecimal.valueOf(132.85));
		configModel.setCurrentTotalPrice(currentTotalPrice);
		return configModel;
	}


	@Test
	public void testGetLockNotNull()
	{
		Assert.assertNotNull("Lock objects may not be null", ProductConfigurationServiceImpl.getLock(CONFIG_ID_1));
	}

	@Test
	public void testGetLockDifferrentForDifferntConfigIds()
	{
		final Object lock1 = ProductConfigurationServiceImpl.getLock(CONFIG_ID_1);
		final Object lock2 = ProductConfigurationServiceImpl.getLock(CONFIG_ID_2);
		Assert.assertNotSame("Lock objects should not be same!", lock1, lock2);
	}

	@Test
	public void testGetLockSameforSameConfigIds()
	{
		final Object lock1 = ProductConfigurationServiceImpl.getLock(CONFIG_ID_1);
		final Object lock2 = ProductConfigurationServiceImpl.getLock(CONFIG_ID_1);
		Assert.assertSame("Lock objects should be same!", lock1, lock2);
	}

	@Test
	public void testGetLockMapShouldNotGrowEndless()
	{

		final Object lock1 = ProductConfigurationServiceImpl.getLock(CONFIG_ID_1);
		final int maxLocks = ProductConfigurationServiceImpl.getMaxLocksPerMap() * 2;
		for (int ii = 0; ii <= maxLocks; ii++)
		{
			ProductConfigurationServiceImpl.getLock(String.valueOf(ii));
		}
		final Object lock2 = ProductConfigurationServiceImpl.getLock(CONFIG_ID_1);
		Assert.assertNotSame("Lock objects should not be same!", lock1, lock2);
	}

	@Test
	public void testRetrieveConfigurationCached() throws ConfigurationEngineException
	{

		Mockito.when(configurationProviderMock.retrieveConfigurationModel(CONFIG_ID)).thenReturn(modelMock);

		ConfigModel retrievedModel = cut.retrieveConfigurationModel(CONFIG_ID);

		Mockito.verify(sessionAccessService, Mockito.times(1)).setConfigurationModelEngineState(Mockito.contains(CONFIG_ID),
				Mockito.same(retrievedModel));

		Mockito.when(sessionAccessService.getConfigurationModelEngineState(Mockito.contains(CONFIG_ID))).thenReturn(modelMock);

		retrievedModel = cut.retrieveConfigurationModel(CONFIG_ID);

		Mockito.verify(configurationProviderMock, Mockito.times(1)).retrieveConfigurationModel(CONFIG_ID);
		assertTrue("Not delegated", retrievedModel == modelMock);
	}

	@Test(expected = IllegalStateException.class)
	public void testRetrieveConfigurationEngineException() throws ConfigurationEngineException
	{

		Mockito.when(configurationProviderMock.retrieveConfigurationModel(CONFIG_ID)).thenThrow(new ConfigurationEngineException());

		try
		{
			cut.retrieveConfigurationModelFromConfigurationEngine(CONFIG_ID);
		}
		catch (final IllegalStateException ex)
		{
			Mockito.verify(sessionAccessService).purge();
			Mockito.verify(sessionAccessService).removeConfigAttributeState(CONFIG_ID);
			assertTrue(ex.getCause() instanceof ConfigurationEngineException);
			throw ex;
		}
	}

	@Test(expected = IllegalStateException.class)
	public void testUpdateConfigurationEngineException() throws ConfigurationEngineException
	{

		Mockito.when(Boolean.valueOf(configurationProviderMock.updateConfiguration(modelMock))).thenThrow(
				new ConfigurationEngineException());
		try
		{
			cut.updateConfiguration(modelMock);
		}
		catch (final IllegalStateException ex)
		{
			Mockito.verify(sessionAccessService).removeConfigAttributeState(CONFIG_ID);
			Mockito.verify(sessionAccessService).purge();
			assertTrue(ex.getCause() instanceof ConfigurationEngineException);
			throw ex;
		}
	}

	@Test
	public void testUpdateConfigurationInvalidateCache() throws ConfigurationEngineException
	{
		Mockito.when(configurationProviderMock.retrieveConfigurationModel(CONFIG_ID)).thenReturn(modelMock);
		Mockito.when(Boolean.valueOf(configurationProviderMock.updateConfiguration(modelMock))).thenReturn(Boolean.TRUE);

		cut.updateConfiguration(modelMock);

		Mockito.verify(sessionAccessService, Mockito.times(1)).removeConfigAttributeState(CONFIG_ID);
	}

	@Test
	public void testSessionAccessService()
	{
		final SessionAccessService sessionAccessService = new SessionAccessServiceImpl();
		cut.setSessionAccessService(sessionAccessService);
		assertEquals("Service should be available", sessionAccessService, cut.getSessionAccessService());
	}

	@Test
	public void testGetNumberOfConflictsEmptyConfig()
	{
		final int numberOfConflicts = cut.countNumberOfSolvableConflicts(configModel);
		assertEquals("No conflicts", 0, numberOfConflicts);
	}

	@Test
	public void testGetNumberOfConflictsWithSolvableConflicts()
	{
		final SolvableConflictModel conflict = new SolvableConflictModelImpl();
		configModel.setSolvableConflicts(Arrays.asList(conflict));
		final int numberOfConflicts = cut.countNumberOfSolvableConflicts(configModel);
		assertEquals("We expect one conflict", 1, numberOfConflicts);
	}

	@Test
	public void testGetNumberOfConflictsWithNotConsistenCstics()
	{
		instanceModel.setCstics(createListOfCsticsOnlyConsistenFlag());
		final InstanceModel subInstance = new InstanceModelImpl();
		subInstance.setCstics(createListOfCsticsOnlyConsistenFlag());
		instanceModel.getSubInstances().add(subInstance);

		configModel.getRootInstance().getCstics().get(1).setConsistent(false);
		final int numberOfConflicts = cut.countNumberOfSolvableConflicts(configModel);
		assertEquals("We expect four conflict", 4, numberOfConflicts);
	}

	private List<CsticModel> createListOfCsticsOnlyConsistenFlag()
	{
		final List<CsticModel> cstics = new ArrayList<>();
		CsticModel cstic = new CsticModelImpl();
		cstic.setConsistent(false);
		cstics.add(cstic);

		cstic = new CsticModelImpl();
		cstic.setConsistent(false);
		cstics.add(cstic);

		cstic = new CsticModelImpl();
		cstic.setConsistent(true);
		cstics.add(cstic);
		return cstics;
	}

	@Test
	@SuppressFBWarnings(value = "SWL_SLEEP_WITH_LOCK_HELD", justification = "required by test scenario")
	public void testSynchronizationBlockingIsFirst() throws InterruptedException
	{
		synchronized (ProductConfigurationServiceImplTest.class)
		{
			final ThreadBlocking threadBlocking = new ThreadBlocking();
			final ThreadAccessing threadAccessing = new ThreadAccessing();
			threadBlocking.start();
			Thread.sleep(100);
			threadAccessing.start();
			threadAccessing.join();
			LOG.info("BF - Accessing took: " + threadAccessing.duration);
			assertTrue(
					"We expect accessing thread needs to wait (wait time is 500), so it should consume more than 250 ms, but duration was only "
							+ threadAccessing.duration, threadAccessing.duration > 250);
		}
	}

	@Test
	@SuppressFBWarnings(value = "SWL_SLEEP_WITH_LOCK_HELD", justification = "required by test scenario")
	public void testSynchronizationAccessingIsFirst() throws InterruptedException
	{
		synchronized (ProductConfigurationServiceImplTest.class)
		{
			final ThreadBlocking threadBlocking = new ThreadBlocking();
			final ThreadAccessing threadAccessing = new ThreadAccessing();
			threadAccessing.start();
			Thread.sleep(100);
			threadBlocking.start();
			threadAccessing.join();
			LOG.info("AF - Accessing took: " + threadAccessing.duration);
			assertTrue(
					"We expect accessing thread does not needs to wait (wait time is 500ms), so it should be faster as 50ms, but durtaion was "
							+ threadAccessing.duration, threadAccessing.duration < 50);
		}

	}

	@Test
	public void releaseSession()
	{
		final DummySessionAccessService dummySession = new DummySessionAccessService();
		cut.setSessionAccessService(dummySession);
		final ConfigModel config = new ConfigModelImpl();
		config.setId("123");
		cut.cacheConfig(config);
		cut.releaseSession("123");
		assertNull(dummySession.getConfigurationModelEngineState("123"));
		Mockito.verify(configurationProviderMock).releaseSession("123");
	}

	@Test
	public void releaseSession_true()
	{
		final DummySessionAccessService dummySession = new DummySessionAccessService();
		cut.setSessionAccessService(dummySession);
		final ConfigModel config = new ConfigModelImpl();
		config.setId("123");
		cut.cacheConfig(config);
		cut.releaseSession("123", true);
		assertNotNull(dummySession.getConfigurationModelEngineState("123"));
		Mockito.verify(configurationProviderMock).releaseSession("123");
	}

	@Test
	public void releaseSession_false()
	{
		final DummySessionAccessService dummySession = new DummySessionAccessService();
		cut.setSessionAccessService(dummySession);
		final ConfigModel config = new ConfigModelImpl();
		config.setId("123");
		cut.cacheConfig(config);
		cut.releaseSession("123", false);
		assertNull(dummySession.getConfigurationModelEngineState("123"));
		Mockito.verify(configurationProviderMock).releaseSession("123");
	}

	@Test
	public void hasKbForDate_noInput()
	{
		final String productCode = null;
		final Date kbDate = null;
		assertFalse("No KB version exist.", cut.hasKbForDate(productCode, kbDate));
	}

	@Test
	public void hasKbForDate_false() throws ParseException
	{
		final String productCode = "Product1";
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		final Date kbDate = sdf.parse("20120201");
		Mockito.when(Boolean.valueOf(configurationProviderMock.isKbForDateExists(productCode, kbDate))).thenReturn(Boolean.FALSE);

		assertFalse("No KB version exists.", cut.hasKbForDate(productCode, kbDate));
	}

	@Test
	public void isKbForDateExists_true() throws ParseException
	{
		final String productCode = "Product2";
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		final Date kbDate = sdf.parse("20150201");
		Mockito.when(Boolean.valueOf(configurationProviderMock.isKbForDateExists(productCode, kbDate))).thenReturn(Boolean.TRUE);

		assertTrue("The KB version have to exist.", cut.hasKbForDate(productCode, kbDate));
	}

	@Test
	public void testRemoveConfigFromSessionCache()
	{
		cut.removeConfigAttributesFromSessionCache(configId);
		Mockito.verify(sessionAccessService).removeConfigAttributeState(configId);
	}

	@Test
	public void testCleanUpAfterEngineErrorNoConfigModel()
	{
		cut.cleanUpAfterEngineError(CONFIG_ID);
		Mockito.verify(sessionAccessService).purge();
	}
}
