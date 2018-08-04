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
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.PriceModelImpl;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPricingOrderIntegrationService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.tracking.TrackingRecorder;
import de.hybris.platform.servicelayer.model.ModelService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ProductConfigAddToCartHookImplTest
{
	private static final String CONFIG_ID = "1234";

	private ProductConfigAddToCartHookImpl classUnderTest;
	private CommerceCartParameter parameters;
	@Mock
	private ProductConfigurationService productConfigurationService;
	@Mock
	private SessionAccessService sessionAccessService;
	@Mock
	private ModelService modelService;
	@Mock
	private CommerceCartService commerceCartService;
	@Mock
	private TrackingRecorder recorder;
	@Mock
	private ProductConfigurationPricingOrderIntegrationService configurationPricingOrderIntegrationService;


	@Mock
	private CPQConfigurableChecker cpqConfigurableChecker;

	@Mock
	private ProductModel mockedProduct;
	private ConfigModel configModel;

	private final CommerceCartModification result = new CommerceCartModification();
	@Mock
	private AbstractOrderEntryModel cartEntry;
	private final PriceModel currentTotalPrice = new PriceModelImpl();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		classUnderTest = new ProductConfigAddToCartHookImpl();

		parameters = new CommerceCartParameter();
		parameters.setProduct(mockedProduct);
		final CartModel toCart = new CartModel();
		toCart.setGuid("123");
		parameters.setCart(toCart);
		parameters.setConfigId(CONFIG_ID);

		classUnderTest.setProductConfigurationService(productConfigurationService);
		classUnderTest.setSessionAccessService(sessionAccessService);
		classUnderTest.setModelService(modelService);
		classUnderTest.setCommerceCartService(commerceCartService);
		classUnderTest.setRecorder(recorder);
		classUnderTest.setConfigurationPricingOrderIntegrationService(configurationPricingOrderIntegrationService);
		classUnderTest.setCpqConfigurableChecker(cpqConfigurableChecker);
		configModel = new ConfigModelImpl();
		configModel.setId(CONFIG_ID);
		result.setEntry(cartEntry);
	}


	@Test
	public void test_beforeAddToCart_noConfig_noNewEntry() throws Exception
	{
		parameters.setCreateNewEntry(false);
		Mockito.when(cpqConfigurableChecker.isCPQConfigurableProduct(mockedProduct)).thenReturn(Boolean.FALSE);
		classUnderTest.beforeAddToCart(parameters);
		assertFalse("createNewEntry changed for non-configurable product", parameters.isCreateNewEntry());
	}

	@Test
	public void test_beforeAddToCart_noConfig_newEntry() throws Exception
	{
		parameters.setCreateNewEntry(true);
		Mockito.when(cpqConfigurableChecker.isCPQConfigurableProduct(mockedProduct)).thenReturn(Boolean.FALSE);
		classUnderTest.beforeAddToCart(parameters);
		assertTrue("createNewEntry changed for non-configurable product", parameters.isCreateNewEntry());
	}

	@Test
	public void test_beforeAddToCart_config_newEntry() throws Exception
	{
		parameters.setCreateNewEntry(true);
		Mockito.when(cpqConfigurableChecker.isCPQConfigurableProduct(mockedProduct)).thenReturn(Boolean.TRUE);
		classUnderTest.beforeAddToCart(parameters);
		assertTrue("createNewEntry should be always true for configurable product", parameters.isCreateNewEntry());
	}

	@Test
	public void test_beforeAddToCart_config_noNewEntry() throws Exception
	{
		parameters.setCreateNewEntry(false);
		Mockito.when(cpqConfigurableChecker.isCPQConfigurableProduct(mockedProduct)).thenReturn(Boolean.TRUE);
		classUnderTest.beforeAddToCart(parameters);
		assertTrue("createNewEntry should be always true for configurable product", parameters.isCreateNewEntry());
	}

	@Test
	public void test_afterAddToCart_executable() throws Exception
	{
		classUnderTest.afterAddToCart(null, null);
	}


	@Test
	public void test_beforeAddToCart_config_noConfigID() throws Exception
	{
		Mockito.when(cpqConfigurableChecker.isCPQConfigurableProduct(mockedProduct)).thenReturn(Boolean.TRUE);
		Mockito.when(productConfigurationService.createDefaultConfiguration((Mockito.any(KBKey.class)))).thenReturn(configModel);

		parameters.setConfigId(null);
		classUnderTest.beforeAddToCart(parameters);
		assertEquals("Config Id is not set in parameters ", configModel.getId(), parameters.getConfigId());
	}

	@Test
	public void test_beforeAddToCart_config_configID() throws Exception
	{
		Mockito.when(cpqConfigurableChecker.isCPQConfigurableProduct(mockedProduct)).thenReturn(Boolean.TRUE);

		final String configId = "222";
		parameters.setConfigId(configId);
		classUnderTest.beforeAddToCart(parameters);
		Mockito.verify(productConfigurationService, Mockito.never()).createDefaultConfiguration((Mockito.any(KBKey.class)));

	}

	@Test
	public void test_afterAddToCartModelNotSaved() throws CommerceCartModificationException
	{
		Mockito.when(cpqConfigurableChecker.isCPQConfigurableProduct(mockedProduct)).thenReturn(Boolean.TRUE);
		Mockito.when(cartEntry.getPk()).thenReturn(null);

		classUnderTest.afterAddToCart(parameters, result);

		Mockito.verify(configurationPricingOrderIntegrationService, Mockito.times(0)).updateCartEntryBasePrice(cartEntry);
		Mockito.verify(configurationPricingOrderIntegrationService, Mockito.times(0))
				.updateCartEntryExternalConfiguration(parameters, cartEntry);
		Mockito.verify(configurationPricingOrderIntegrationService, Mockito.times(0)).fillSummaryMap(cartEntry);
	}

	@Test
	public void test_afterAddToCart() throws CommerceCartModificationException
	{
		Mockito.when(cpqConfigurableChecker.isCPQConfigurableProduct(mockedProduct)).thenReturn(Boolean.TRUE);
		Mockito.when(cartEntry.getPk()).thenReturn(PK.fromLong(1));

		classUnderTest.afterAddToCart(parameters, result);

		Mockito.verify(configurationPricingOrderIntegrationService, Mockito.times(1)).updateCartEntryPrices(cartEntry, true,
				parameters);
		Mockito.verify(configurationPricingOrderIntegrationService, Mockito.times(1))
				.updateCartEntryExternalConfiguration(parameters, cartEntry);
		Mockito.verify(configurationPricingOrderIntegrationService, Mockito.times(1)).fillSummaryMap(cartEntry);
	}


}
