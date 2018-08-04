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
package de.hybris.platform.sap.productconfig.frontend.handler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorfacades.cart.action.CartEntryAction;
import de.hybris.platform.acceleratorfacades.cart.action.exceptions.CartEntryActionException;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.ConfigurationInfoData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.facades.ConfigurationCartIntegrationFacade;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.SessionAccessFacade;
import de.hybris.platform.sap.productconfig.services.impl.CPQConfigurableChecker;
import de.hybris.platform.sap.productconfig.testutil.DummySessionAccessFacade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ConfigCopyCartEntryActionHandlerTest
{
	private ConfigCopyCartEntryActionHandler classUnderTest;
	@Mock
	private CartFacade mockedCartFacade;
	private CartData cart;
	private List<OrderEntryData> entryList;
	private SessionAccessFacade cpqSessionFacade;
	@Mock
	private ConfigurationCartIntegrationFacade mockedConfigCartFacade;

	@Mock
	private CPQConfigurableChecker cpqConfigurableChecker;


	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new ConfigCopyCartEntryActionHandler();
		cpqSessionFacade = new DummySessionAccessFacade();
		classUnderTest.setSessionAccessFacade(cpqSessionFacade);
		classUnderTest.setCartFacade(mockedCartFacade);
		classUnderTest.setConfigCartFacade(mockedConfigCartFacade);
		classUnderTest.setCpqConfigurableChecker(cpqConfigurableChecker);

		cart = new CartData();
		entryList = new ArrayList<>();
		cart.setEntries(entryList);

		given(mockedCartFacade.getSessionCart()).willReturn(cart);
		given(mockedConfigCartFacade.copyConfiguration(Mockito.anyString(), Mockito.anyString())).willReturn("newConfigId");

	}

	@Test
	public void testGetOrderEntryByEntryNumber()
	{
		final OrderEntryData entry = new OrderEntryData();
		entry.setEntryNumber(Integer.valueOf(1));
		entryList.add(entry);
		final OrderEntryData retrievedEntry = classUnderTest.getEntryByEntryNumber(1l);
		assertNotNull(retrievedEntry);
		assertEquals(Integer.valueOf(1), retrievedEntry.getEntryNumber());
	}

	@Test
	public void testGetOrderEntryByEntryNumber_notFound()
	{
		final OrderEntryData entry = new OrderEntryData();
		entry.setEntryNumber(Integer.valueOf(5));
		entryList.add(entry);
		final OrderEntryData retrievedEntry = classUnderTest.getEntryByEntryNumber(1l);
		assertNull(retrievedEntry);
	}

	@Test
	public void testGetConfigIdByOrderEntry()
	{
		final OrderEntryData entry = new OrderEntryData();
		entry.setItemPK("567");
		cpqSessionFacade.setConfigIdForCartEntry(entry.getItemPK(), "123");
		final String configId = classUnderTest.getConfigIdByOrderEntry(entry);
		assertEquals("123", configId);
	}

	@Test
	public void testGetConfigIdByOrderEntry_noConfigIdFound()
	{
		final OrderEntryData entry = new OrderEntryData();
		entry.setItemPK("567");
		cpqSessionFacade.setConfigIdForCartEntry("678", "123");
		final String configId = classUnderTest.getConfigIdByOrderEntry(entry);
		assertNull(configId);
	}


	@Test
	public void testCopyConfiguration()
	{
		final OrderEntryData entryToCopy = prepareEntryDataForCopy();
		final ConfigurationData configData = classUnderTest.copyConfiguration(entryToCopy);
		assertNotNull(configData);
		assertNotNull(configData.getConfigId());
		assertNotNull(configData.getKbKey().getProductCode());
	}

	private OrderEntryData prepareEntryDataForCopy()
	{
		final OrderEntryData entryToCopy = new OrderEntryData();
		entryToCopy.setEntryNumber(Integer.valueOf(1));
		entryToCopy.setItemPK("567");
		entryToCopy.setProduct(new ProductData());
		entryToCopy.getProduct().setCode("pCode");
		entryToCopy.setConfigurationInfos(Collections.emptyList());
		entryList.add(entryToCopy);
		cpqSessionFacade.setConfigIdForCartEntry(entryToCopy.getItemPK(), "123");
		return entryToCopy;
	}

	@Test
	public void testHandleActionSuccess() throws CartEntryActionException
	{
		final OrderEntryData entryToCopy = prepareEntryDataForCopy();
		final Optional<String> ret = classUnderTest
				.handleAction(Collections.singletonList(Long.valueOf(entryToCopy.getEntryNumber().longValue())));
		assertFalse("No redirect expected", ret.isPresent());
	}

	@Test(expected = CartEntryActionException.class)
	public void testHandleActionFailure() throws CommerceCartModificationException, CartEntryActionException
	{
		final OrderEntryData entryToCopy = prepareEntryDataForCopy();
		given(mockedCartFacade.addToCart(Mockito.any(), Mockito.anyLong()))
				.willThrow(new CommerceCartModificationException("Test"));
		classUnderTest.handleAction(Collections.singletonList(Long.valueOf(entryToCopy.getEntryNumber().longValue())));
	}

	@Test
	public void testSupports_False()
	{
		given(cpqConfigurableChecker.isCPQConfigurableProduct(any(ProductModel.class))).willReturn(false);

		final CartEntryModel cartEntry = new CartEntryModel();
		cartEntry.setProduct(new ProductModel());
		final boolean support = classUnderTest.supports(cartEntry);
		assertFalse(support);
	}

	@Test
	public void testSupports_True()
	{
		given(cpqConfigurableChecker.isCPQConfigurableProduct(any(ProductModel.class))).willReturn(true);

		final CartEntryModel cartEntry = new CartEntryModel();
		cartEntry.setProduct(new ProductModel());
		final boolean support = classUnderTest.supports(cartEntry);
		assertTrue(support);
	}

	@Test
	public void testEnum()
	{
		assertNotNull(CartEntryAction.valueOf("CPQ_COPY"));
	}

	@Test
	public void testMsgKey()
	{
		assertEquals(classUnderTest.SUCCESS_KEY, classUnderTest.getSuccessMessageKey());
		assertEquals(classUnderTest.ERROR_KEY, classUnderTest.getErrorMessageKey());
	}

	public void testIsDefaultConfigTrue()
	{
		final boolean isDefaultConfig = classUnderTest.isDefaultConfig(Collections.emptyList());
		assertTrue(isDefaultConfig);
	}

	public void testIsDefaultConfigTrueNullItem()
	{
		final boolean isDefaultConfig = classUnderTest.isDefaultConfig(Collections.singletonList(new ConfigurationInfoData()));
		assertTrue(isDefaultConfig);
	}

	public void testIsDefaultConfigFalseNonNullItem()
	{
		final ConfigurationInfoData info = new ConfigurationInfoData();
		info.setConfigurationLabel("Label");
		info.setConfigurationValue("Value");
		final boolean isDefaultConfig = classUnderTest.isDefaultConfig(Collections.singletonList(info));
		assertFalse(isDefaultConfig);
	}

	public void testIsDefaultConfigFalse2Item()
	{
		final ConfigurationInfoData info = new ConfigurationInfoData();
		info.setConfigurationLabel("Label");
		info.setConfigurationValue("Value");
		final List<ConfigurationInfoData> infoList = new ArrayList<ConfigurationInfoData>();
		infoList.add(new ConfigurationInfoData());
		infoList.add(info);
		final boolean isDefaultConfig = classUnderTest.isDefaultConfig(infoList);
		assertFalse(isDefaultConfig);
	}

	public void testCopyAndAddToCartDirect() throws CommerceCartModificationException
	{
		final OrderEntryData entryToCopy = prepareEntryDataForCopy();
		classUnderTest.copyAndAddToCart(entryToCopy, true);
		Mockito.verify(mockedCartFacade, Mockito.times(1)).addToCart(entryToCopy.getProduct().getCode(), 1l);
		Mockito.verifyZeroInteractions(mockedConfigCartFacade);
	}

	public void testCopyAndAddToCartCPQ() throws CommerceCartModificationException
	{
		final OrderEntryData entryToCopy = prepareEntryDataForCopy();
		classUnderTest.copyAndAddToCart(entryToCopy, false);
		Mockito.verify(mockedConfigCartFacade, Mockito.times(1)).addConfigurationToCart(Mockito.any());
		Mockito.verifyZeroInteractions(mockedCartFacade);
	}
}
