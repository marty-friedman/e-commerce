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
package de.hybris.platform.sap.productconfig.frontend.controllers;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.AbstractController;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.frontend.UiStatus;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;




@UnitTest
public class CartConfigureProductControllerTest extends AbstractProductConfigControllerBaseTest
{
	private CartConfigureProductController classUnderTest;

	@Mock
	private CartFacade cartFacade;

	@Before
	public void setUp()
	{
		classUnderTest = new CartConfigureProductController();
		MockitoAnnotations.initMocks(this);
		injectMocks(classUnderTest);
		classUnderTest.setCartFacade(cartFacade);

		kbKey = createKbKey();
		csticList = createCsticsList();
		configData = createConfigurationDataWithGeneralGroupOnly();
	}

	@Test
	public void testGetConfigForRestoredProduct() throws Exception
	{
		initializeFirstCall();
		final ConfigurationData configData = classUnderTest.getConfigDataForRestoredProduct(kbKey, productData, "id", null);
		Assert.assertNotNull(configData);
	}

	@Test
	public void testGetConfigForRestoredProductWithNoConfiguration() throws Exception
	{
		initializeFirstCall();
		final ConfigurationData configData = classUnderTest.getConfigDataForRestoredProduct(kbKey, productData, null, null);
		Assert.assertNotNull(configData);
	}

	@Test
	public void testUiStatusFromSessionInCaseOfRestore() throws Exception
	{
		initializeFirstCall();
		given(sessionAccessFacade.getConfigIdForCartEntry("TR")).willReturn("configId");
		final UiStatus stat = classUnderTest.getUiStatusFromSession("TR", kbKey, productData);
		Assert.assertNotNull(stat);
		//assertEquals("confId", stat.getConfigId());
	}

	@Test
	public void testUiStatusFromSession() throws Exception
	{
		final UiStatus uiStatus = createUiStatus("1");
		given(sessionAccessFacade.getUiStatusForCartEntry("IT")).willReturn(uiStatus);

		final UiStatus stat = classUnderTest.getUiStatusFromSession("IT", null, productData);
		assertEquals(uiStatus, stat);
	}


	@Test
	public void testConfigureCartEntryAfterLogout() throws CommerceCartModificationException
	{
		// this happens if a user presses logout on config screen while configuring a cart entry
		// after logout cart is empty
		given(cartFacade.getSessionCart()).willReturn(new CartData());
		final String view = classUnderTest.configureCartEntry(0, model, request);
		assertEquals(AbstractController.REDIRECT_PREFIX + AbstractController.ROOT, view);
	}
}
