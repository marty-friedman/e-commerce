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
package de.hybris.platform.sap.productconfig.facades.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.impl.DefaultCheckoutFacade;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.impl.CommerceCartFactory;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("javadoc")
@IntegrationTest
public class CPQOrderFacadeIntegrationTest extends CPQFacadeLayerTest
{
	private static Logger LOG = Logger.getLogger(CPQOrderFacadeIntegrationTest.class);

	@Resource(name = "cartFactory")
	CommerceCartFactory commerceCartFactory;

	@Resource(name = "defaultCheckoutFacade")
	private DefaultCheckoutFacade checkoutFacade;



	@Before
	public void setUp() throws Exception
	{
		prepareCPQData();

	}

	@Override
	public void importCPQTestData() throws ImpExException, Exception
	{
		super.importCPQTestData();
		importCPQUserData();
	}



	@Test
	public void testConfigOrderIntegrationFacade() throws CommerceCartModificationException, InvalidCartException
	{

		checkoutFacade.setCheckoutCustomerStrategy(mockedCheckoutCustomerStrategy);
		configOrderIntegrationFacade.setBaseStoreService(baseStoreService);


		/*
		 * Step 1: Create a cart containing one entry.
		 */
		final ConfigurationData configData = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		cpqCartFacade.addConfigurationToCart(configData);

		/*
		 * Step 2: Create an order from cart
		 */
		final OrderData order = checkoutFacade.placeOrder();

		/*
		 * Step 3: Get the ConfigurationOverviewData from ConfigurationOrderIntegrationFacade.
		 */
		final String orderCode = order.getCode();
		final int entryNumber = order.getEntries().get(0).getEntryNumber().intValue();
		final ConfigurationOverviewData configOverview = configOrderIntegrationFacade.getConfiguration(orderCode, entryNumber);
		assertNotNull(configOverview);
		assertEquals(KB_KEY_Y_SAP_SIMPLE_POC.getProductCode(), configOverview.getProductCode());

	}


}
