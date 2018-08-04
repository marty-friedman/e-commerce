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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingConfigurationParameter;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.ProviderFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceSummaryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceValueUpdateModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.PriceModelImpl;
import de.hybris.platform.sap.productconfig.service.testutil.DummySessionAccessService;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 *
 */
@UnitTest
public class PricingServiceImplTest
{
	private final PricingServiceImpl pricingService = new PricingServiceImpl();

	@Mock
	private PricingProvider mockedPricingProvider;

	@Mock
	private ProviderFactory mockedProviderFactory;

	private SessionAccessService dummySessionAccessService;

	@Mock
	private PricingConfigurationParameter pricingConfigurationParameter;

	private static final String configId = "1";

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		pricingService.setProviderFactory(mockedProviderFactory);
		given(mockedProviderFactory.getPricingProvider()).willReturn(mockedPricingProvider);

		dummySessionAccessService = new DummySessionAccessService();
		pricingService.setSessionAccessService(dummySessionAccessService);
		Mockito.when(pricingConfigurationParameter.isPricingSupported()).thenReturn(true);
		pricingService.setPricingConfigurationParameter(pricingConfigurationParameter);

	}

	@Test
	public void testGetPriceSummaryFromService() throws PricingEngineException
	{
		final PriceSummaryModel priceSummaryModel = new PriceSummaryModel();
		Mockito.when(mockedPricingProvider.getPriceSummary(configId)).thenReturn(priceSummaryModel);
		dummySessionAccessService.setConfigurationModelEngineState(configId, new ConfigModelImpl());
		assertEquals(priceSummaryModel, pricingService.getPriceSummary(configId));
		assertEquals(priceSummaryModel, dummySessionAccessService.getPriceSummaryState(configId));
		assertFalse(dummySessionAccessService.getConfigurationModelEngineState(configId).hasPricingError());
	}

	@Test
	public void testGetPriceSummaryFromServiceException() throws PricingEngineException
	{
		// first service call fails
		Mockito.when(mockedPricingProvider.getPriceSummary(configId)).thenThrow(new PricingEngineException());
		dummySessionAccessService.setConfigurationModelEngineState(configId, new ConfigModelImpl());
		assertNull(pricingService.getPriceSummary(configId));
		assertNull(dummySessionAccessService.getPriceSummaryState(configId));
		assertTrue(dummySessionAccessService.getConfigurationModelEngineState(configId).hasPricingError());

		//second service call successful
		final PriceSummaryModel priceSummaryModel = new PriceSummaryModel();
		Mockito.doReturn(priceSummaryModel).when(mockedPricingProvider).getPriceSummary(configId);
		assertEquals(priceSummaryModel, pricingService.getPriceSummary(configId));
		assertEquals(priceSummaryModel, dummySessionAccessService.getPriceSummaryState(configId));
		assertFalse(dummySessionAccessService.getConfigurationModelEngineState(configId).hasPricingError());
	}

	@Test
	public void testGetPriceSummaryFromCache()
	{
		final PriceSummaryModel priceSummaryModel = new PriceSummaryModel();
		dummySessionAccessService.setPriceSummaryState(configId, priceSummaryModel);

		assertEquals(pricingService.getPriceSummary(configId), priceSummaryModel);
	}

	@Test
	public void testFillValuePrices() throws PricingEngineException
	{
		final String kbId = "111";
		final List<PriceValueUpdateModel> updateModels = new ArrayList<>();
		pricingService.fillValuePrices(updateModels, kbId);
		Mockito.verify(mockedPricingProvider, Mockito.times(1)).fillValuePrices(updateModels, kbId);
	}

	@Test
	public void testFillValuePricesException() throws PricingEngineException
	{
		doThrow(new PricingEngineException()).when(mockedPricingProvider).fillValuePrices(Mockito.anyList(), Mockito.anyString());
		final String kbId = "111";
		final List<PriceValueUpdateModel> updateModels = new ArrayList<>();
		pricingService.fillValuePrices(updateModels, kbId);
	}

	@Test
	public void testIsActive()
	{
		given(Boolean.valueOf(mockedPricingProvider.isActive())).willReturn(Boolean.TRUE);
		assertTrue(pricingService.isActive());
	}

	@Test
	public void testIsActiveNoPricingCustomized()
	{
		given(Boolean.valueOf(mockedPricingProvider.isActive())).willReturn(Boolean.TRUE);
		Mockito.when(pricingConfigurationParameter.isPricingSupported()).thenReturn(false);
		assertFalse(pricingService.isActive());
	}

	@Test
	public void testIsNotActive()
	{
		given(Boolean.valueOf(mockedPricingProvider.isActive())).willReturn(Boolean.FALSE);
		assertFalse(pricingService.isActive());
	}

	@Test
	public void testFillConfigPrices()
	{
		final ConfigModel configModel = new ConfigModelImpl();
		final PriceSummaryModel priceSummary = new PriceSummaryModel();
		priceSummary.setBasePrice(new PriceModelImpl());
		priceSummary.setCurrentTotalPrice(new PriceModelImpl());
		priceSummary.setSelectedOptionsPrice(new PriceModelImpl());
		pricingService.fillConfigPrices(priceSummary, configModel);
		assertEquals(priceSummary.getBasePrice(), configModel.getBasePrice());
		assertEquals(priceSummary.getCurrentTotalPrice(), configModel.getCurrentTotalPrice());
		assertEquals(priceSummary.getSelectedOptionsPrice(), configModel.getSelectedOptionsPrice());

	}

	@Test
	public void testFillOverviewPrices() throws PricingEngineException
	{
		final PriceSummaryModel priceSummaryModel = new PriceSummaryModel();
		Mockito.when(mockedPricingProvider.getPriceSummary(configId)).thenReturn(priceSummaryModel);
		final ConfigModel configModel = new ConfigModelImpl();
		configModel.setId(configId);
		dummySessionAccessService.setConfigurationModelEngineState(configId, configModel);
		pricingService.fillOverviewPrices(configModel);
		Mockito.verify(mockedPricingProvider).fillValuePrices(configModel);
	}

	@Test
	public void testFillOverviewPricesException() throws PricingEngineException
	{
		Mockito.when(mockedPricingProvider.getPriceSummary(configId)).thenThrow(new PricingEngineException());
		doThrow(new PricingEngineException()).when(mockedPricingProvider).fillValuePrices(Mockito.any());
		final ConfigModel configModel = new ConfigModelImpl();
		configModel.setId(configId);
		dummySessionAccessService.setConfigurationModelEngineState(configId, configModel);
		pricingService.fillOverviewPrices(configModel);
		Mockito.verify(mockedPricingProvider).fillValuePrices(configModel);
		assertTrue(configModel.hasPricingError());
	}

	@Test
	public void testPricingConfigurationParameter()
	{
		assertEquals(pricingConfigurationParameter, pricingService.getPricingConfigurationParameter());
	}


}
