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
package de.hybris.platform.sap.productconfig.b2b.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2bacceleratorfacades.api.cart.CheckoutFacade;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.order.impl.DefaultCheckoutFacade;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.order.impl.CommerceCartFactory;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.impl.ConfigurationOrderIntegrationFacadeImpl;
import de.hybris.platform.sap.productconfig.facades.integrationtests.CPQFacadeLayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.text.ParseException;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class CPQReorderIntegrationTest extends CPQFacadeLayerTest
{

	@Resource(name = "cartFacade")
	private CartFacade cartFacade;
	@Resource(name = "b2bCheckoutFacade")
	private CheckoutFacade b2bCheckoutFacade;
	@Resource(name = "sapProductConfigOrderIntegrationFacade")
	private ConfigurationOrderIntegrationFacadeImpl cpqOrderIntegrationFacade;
	@Resource(name = "defaultCheckoutFacade")
	private DefaultCheckoutFacade checkoutFacade;

	@Resource(name = "cartService")
	private CartService cartService;
	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "cartFactory")
	CommerceCartFactory commerceCartFactory;

	@Resource(name = "userService")
	private UserService userService;



	@After
	public void cleanUp()
	{
		commerceCartFactory.setUserService(userService);
		checkoutFacade.setUserService(userService);
		((DefaultCheckoutFacade) b2bCheckoutFacade).setUserService(userService);
		cpqOrderIntegrationFacade.setUserService(userService);
	}

	@Before
	public void setUp() throws Exception
	{
		super.prepareCPQData();
		cpqOrderIntegrationFacade.setUserService(mockedUserService);
		checkoutFacade.setCheckoutCustomerStrategy(mockedCheckoutCustomerStrategy);
		checkoutFacade.setUserService(mockedUserService);
		commerceCartFactory.setUserService(mockedUserService);
		((DefaultCheckoutFacade) b2bCheckoutFacade).setUserService(mockedUserService);
	}

	@Override
	public void importCPQTestData() throws ImpExException, Exception
	{
		super.importCPQTestData();
		importCPQUserData();
	}


	@Test
	public void testReorder_OK()
			throws CommerceCartModificationException, InvalidCartException, CMSItemNotFoundException, ParseException
	{
		final String oldConfigId = createConfigInCart();
		final OrderData order = placeOrder();

		// reorder
		assertTrue("order should be re-orderable", cpqOrderIntegrationFacade.isReorderable(order.getCode()));
		b2bCheckoutFacade.createCartFromOrder(order.getCode());
		final List<CartModificationData> modifications = cartFacade.validateCartData();

		// check messages
		for (final CartModificationData modification : modifications)
		{
			assertEquals(CommerceCartModificationStatus.SUCCESS, modification.getStatusCode());
		}

		// check cart
		final String newConfigId = getAndCheckNewConfigId(oldConfigId);

		// check config
		ConfigurationData newConfigData = createConfigDataForGet(newConfigId);
		newConfigData = cpqFacade.getConfiguration(newConfigData);
		assertFalse(facadeConfigValueHelper.getCstic(newConfigData, "YSAP_POC_SIMPLE_FLAG").getDomainvalues().get(0).isSelected());
		assertEquals("125.0", facadeConfigValueHelper.getCstic(newConfigData, "WCEM_NUMBER_SIMPLE").getValue());
	}

	@Test
	public void testReorder_InvalidKB()
			throws CommerceCartModificationException, InvalidCartException, CMSItemNotFoundException, ParseException
	{
		final String oldConfigId = createConfigInCart();
		makeFirstCartEntryKBInvalid();
		final OrderData order = placeOrder();



		// reorder
		assertFalse("order should not be re-orderable", cpqOrderIntegrationFacade.isReorderable(order.getCode()));
		b2bCheckoutFacade.createCartFromOrder(order.getCode());
		final List<CartModificationData> modifications = cartFacade.validateCartData();

		// check messages
		for (final CartModificationData modification : modifications)
		{
			assertEquals(ConfigurationOrderIntegrationFacadeImpl.KB_NOT_VALID, modification.getStatusCode());
		}

		final String newConfigId = getAndCheckNewConfigId(oldConfigId);


		ConfigurationData newConfigData = createConfigDataForGet(newConfigId);
		newConfigData = cpqFacade.getConfiguration(newConfigData);
		assertTrue(facadeConfigValueHelper.getCstic(newConfigData, "YSAP_POC_SIMPLE_FLAG").getDomainvalues().get(0).isSelected());
		assertNull(facadeConfigValueHelper.getCstic(newConfigData, "WCEM_NUMBER_SIMPLE"));

	}

	protected ConfigurationData createConfigDataForGet(final String newConfigId)
	{
		final ConfigurationData newConfigData = new ConfigurationData();
		newConfigData.setKbKey(KB_KEY_Y_SAP_SIMPLE_POC);
		newConfigData.setConfigId(newConfigId);
		return newConfigData;
	}

	protected String getAndCheckNewConfigId(final String oldConfigId)
	{
		final List<OrderEntryData> entries = cartFacade.getSessionCart().getEntries();
		assertEquals(1, entries.size());
		final String newConfigId = cpqSessionAccessFacade.getConfigIdForCartEntry(entries.get(0).getItemPK());
		assertFalse(oldConfigId.equals(newConfigId));
		return newConfigId;
	}


	protected void makeFirstCartEntryKBInvalid()
	{
		final AbstractOrderEntryModel entryModel = cartService.getSessionCart().getEntries().get(0);
		String extConfig = entryModel.getExternalConfiguration();
		extConfig = extConfig.replaceAll("KBVERSION=\"3800\"", "KBVERSION=\"3700\"");
		entryModel.setExternalConfiguration(extConfig);
		modelService.save(entryModel);

	}

	protected OrderData placeOrder() throws InvalidCartException
	{
		final OrderData order = checkoutFacade.placeOrder();
		assertTrue(CollectionUtils.isEmpty(cartFacade.getSessionCart().getEntries()));
		return order;
	}



	protected String createConfigInCart() throws CommerceCartModificationException
	{
		// create config and modify it
		ConfigurationData configData = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		facadeConfigValueHelper.setCsticValue(configData, "YSAP_POC_SIMPLE_FLAG", "X", false);
		cpqFacade.updateConfiguration(configData);
		configData = cpqFacade.getConfiguration(configData);
		facadeConfigValueHelper.setCstic(configData, "WCEM_NUMBER_SIMPLE", "125");
		cpqFacade.updateConfiguration(configData);
		facadeConfigValueHelper.setCstic(configData, "EXP_NO_USERS", "300");
		cpqFacade.updateConfiguration(configData);
		final String oldConfigId = configData.getConfigId();

		// add To cart and order it
		cpqCartFacade.addConfigurationToCart(configData);
		return oldConfigId;
	}


}
