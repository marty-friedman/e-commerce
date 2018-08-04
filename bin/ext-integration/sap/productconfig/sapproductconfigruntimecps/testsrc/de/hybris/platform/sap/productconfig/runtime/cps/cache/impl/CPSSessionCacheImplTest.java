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
package de.hybris.platform.sap.productconfig.runtime.cps.cache.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.cache.CPSSessionAttributeContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.constants.SapproductconfigruntimecpsConstants;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingDocumentInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingDocumentResult;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@SuppressWarnings("javadoc")
@UnitTest
public class CPSSessionCacheImplTest
{


	private CPSSessionCacheImpl classUnderTest;
	private CPSSessionAttributeContainer attributes;

	@Mock
	private SessionService sessionService;
	private final static String configId = "ID";
	private List<String> cookieList;
	private final PricingDocumentInput pricingDocumentInput = new PricingDocumentInput();
	private final PricingDocumentResult pricingDocumentResult = new PricingDocumentResult();
	private final String eTag = "123";

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new CPSSessionCacheImpl();
		classUnderTest.setSessionService(sessionService);
		attributes = new CPSSessionAttributeContainer();
		cookieList = new ArrayList<>();
	}


	@Test
	public void testRetrieveSessionAttributeContainer()
	{
		final CPSSessionAttributeContainer result = classUnderTest.retrieveSessionAttributeContainer();
		assertNotNull(result);
		Mockito.verify(sessionService)
				.getAttribute(SapproductconfigruntimecpsConstants.PRODUCT_CONFIG_CPS_SESSION_ATTRIBUTE_CONTAINER);
		Mockito.verify(sessionService).setAttribute(Mockito.anyString(), Mockito.any());
	}

	@Test
	public void testRetrieveSessionAttributeContainer_isPresent()
	{
		Mockito
				.when(sessionService.getAttribute(SapproductconfigruntimecpsConstants.PRODUCT_CONFIG_CPS_SESSION_ATTRIBUTE_CONTAINER))
				.thenReturn(attributes);
		final CPSSessionAttributeContainer result = classUnderTest.retrieveSessionAttributeContainer();
		assertEquals(attributes, result);
		Mockito.verify(sessionService)
				.getAttribute(SapproductconfigruntimecpsConstants.PRODUCT_CONFIG_CPS_SESSION_ATTRIBUTE_CONTAINER);
		Mockito.verify(sessionService, Mockito.times(0)).setAttribute(Mockito.anyString(), Mockito.any());
	}

	@Test
	public void testPurge()
	{
		classUnderTest.purge();
		Mockito.verify(sessionService)
				.setAttribute(SapproductconfigruntimecpsConstants.PRODUCT_CONFIG_CPS_SESSION_ATTRIBUTE_CONTAINER, null);
	}

	@Test
	public void testGetCookies()
	{
		Mockito
				.when(sessionService.getAttribute(SapproductconfigruntimecpsConstants.PRODUCT_CONFIG_CPS_SESSION_ATTRIBUTE_CONTAINER))
				.thenReturn(attributes);



		classUnderTest.setCookies(configId, cookieList);
		assertEquals(cookieList, classUnderTest.getCookies(configId));
	}

	@Test
	public void testGetETag()
	{
		Mockito
				.when(sessionService.getAttribute(SapproductconfigruntimecpsConstants.PRODUCT_CONFIG_CPS_SESSION_ATTRIBUTE_CONTAINER))
				.thenReturn(attributes);



		classUnderTest.setETag(configId, eTag);
		assertEquals(eTag, classUnderTest.getETag(configId));
	}

	@Test
	public void testRemoveCookies()
	{
		Mockito
				.when(sessionService.getAttribute(SapproductconfigruntimecpsConstants.PRODUCT_CONFIG_CPS_SESSION_ATTRIBUTE_CONTAINER))
				.thenReturn(attributes);


		classUnderTest.removeCookies(configId);
		assertNull(classUnderTest.getCookies(configId));
	}

	@Test
	public void testRemovePricingDocumentInput()
	{
		Mockito
				.when(sessionService.getAttribute(SapproductconfigruntimecpsConstants.PRODUCT_CONFIG_CPS_SESSION_ATTRIBUTE_CONTAINER))
				.thenReturn(attributes);

		classUnderTest.setPricingDocumentInput(configId, pricingDocumentInput);
		assertEquals(pricingDocumentInput, classUnderTest.getPricingDocumentInput(configId));
		classUnderTest.removePricingDocumentInput(configId);
		assertNull(classUnderTest.getPricingDocumentInput(configId));
	}

	@Test
	public void testRemoveETag()
	{
		Mockito
				.when(sessionService.getAttribute(SapproductconfigruntimecpsConstants.PRODUCT_CONFIG_CPS_SESSION_ATTRIBUTE_CONTAINER))
				.thenReturn(attributes);

		classUnderTest.setETag(configId, eTag);
		assertEquals(eTag, classUnderTest.getETag(configId));
		classUnderTest.removeETag(configId);
		assertNull(classUnderTest.getETag(configId));
	}

	@Test
	public void testRemovePricingDocumentResult()
	{
		Mockito
				.when(sessionService.getAttribute(SapproductconfigruntimecpsConstants.PRODUCT_CONFIG_CPS_SESSION_ATTRIBUTE_CONTAINER))
				.thenReturn(attributes);

		classUnderTest.setPricingDocumentResult(configId, pricingDocumentResult);
		assertEquals(pricingDocumentResult, classUnderTest.getPricingDocumentResult(configId));
		classUnderTest.removePricingDocumentResult(configId);
		assertNull(classUnderTest.getPricingDocumentResult(configId));
	}

	@Test
	public void testPurgePrices()
	{
		attributes.getPricingDocumentInputMap().put(configId, pricingDocumentInput);
		attributes.getPricingDocumentResultMap().put(configId, pricingDocumentResult);
		attributes.getValuePricesMap().put(configId, new HashMap<>());
		Mockito
				.when(sessionService.getAttribute(SapproductconfigruntimecpsConstants.PRODUCT_CONFIG_CPS_SESSION_ATTRIBUTE_CONTAINER))
				.thenReturn(attributes);
		classUnderTest.purgePrices();
		assertTrue(attributes.getPricingDocumentInputMap().isEmpty());
		assertTrue(attributes.getPricingDocumentResultMap().isEmpty());
		assertTrue(attributes.getValuePricesMap().isEmpty());

	}


}
