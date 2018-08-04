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
package de.hybris.platform.sap.productconfig.services.strategies.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartRestoration;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationStrategy;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceCartRestorationStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ProductConfigurationSaveCartRestorationStrategyImplTest
{

	private static final String productKey = "DRAGON_CAR";

	private static final String xml = "XML";

	private static final long longValue = 1234;

	ProductConfigurationSaveCartRestorationStrategyImpl classUnderTest = new ProductConfigurationSaveCartRestorationStrategyImpl();


	@Mock
	CartModel cartModel;

	@Mock
	CartEntryModel cartEntry;

	@Mock
	SessionAccessService sessionAccessService;

	@Mock
	private ProductModel productModel;

	private PK pk;

	private final CommerceCartParameter parameters = new CommerceCartParameter();

	private final CommerceCartRestoration commerceCartRestoration = new CommerceCartRestoration();

	private final List<AbstractOrderEntryModel> entryList = new ArrayList<AbstractOrderEntryModel>();

	private final CommerceCartRestorationStrategy commerceSaveCartRestorationStrategy = new DefaultCommerceCartRestorationStrategy();



	@Before
	public void initialize() throws CommerceCartRestorationException
	{

		MockitoAnnotations.initMocks(this);
		entryList.add(cartEntry);
		parameters.setCart(cartModel);
		Mockito.when(cartModel.getEntries()).thenReturn(entryList);
		pk = PK.fromLong(longValue);
		Mockito.when(cartEntry.getPk()).thenReturn(pk);
		Mockito.when(cartEntry.getProduct()).thenReturn(productModel);
		Mockito.when(productModel.getCode()).thenReturn(productKey);
		classUnderTest.setSessionAccessService(sessionAccessService);
		classUnderTest.setCommerceSaveCartRestorationStrategy(commerceSaveCartRestorationStrategy);
	}

	@Test
	public void testCommerceSaveCartRestorationStrategy()
	{
		assertEquals(commerceSaveCartRestorationStrategy, classUnderTest.getCommerceSaveCartRestorationStrategy());
	}


	@Test
	public void testReleaseSessionArtifacts()
	{
		classUnderTest.releaseSessionArtifacts(parameters);
		Mockito.verify(cartModel, Mockito.times(1)).getEntries();
	}

	@Test(expected = IllegalStateException.class)
	public void testReleaseSessionArtifactsNoCart()
	{
		parameters.setCart(null);
		classUnderTest.releaseSessionArtifacts(parameters);
	}

	@Test
	public void testSessionAccessService()
	{
		assertEquals(sessionAccessService, classUnderTest.getSessionAccessService());
	}

	@Test
	public void testReleaseSessionArtifactsForEntryNotConfigurable()
	{
		classUnderTest.releaseSessionArtifactsForEntry(cartEntry);
		Mockito.verify(sessionAccessService, Mockito.times(0)).removeSessionArtifactsForCartEntry(String.valueOf(longValue),
				productKey);
	}

	@Test
	public void testReleaseSessionArtifactsForEntryConfigurable()
	{
		Mockito.when(cartEntry.getExternalConfiguration()).thenReturn(xml);

		classUnderTest.releaseSessionArtifactsForEntry(cartEntry);
		Mockito.verify(sessionAccessService, Mockito.times(1)).removeSessionArtifactsForCartEntry(String.valueOf(longValue),
				productKey);
	}



}
