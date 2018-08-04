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

import de.hybris.platform.commercefacades.order.impl.DefaultQuoteFacade;
import de.hybris.platform.commercefacades.order.impl.DefaultSaveCartFacade;
import de.hybris.platform.commerceservices.order.impl.CommerceCartFactory;
import de.hybris.platform.sap.productconfig.facades.ConfigurationCartIntegrationFacade;
import de.hybris.platform.sap.productconfig.facades.ConfigurationFacade;
import de.hybris.platform.sap.productconfig.facades.KBKeyData;
import de.hybris.platform.sap.productconfig.facades.SessionAccessFacade;
import de.hybris.platform.sap.productconfig.facades.impl.ConfigurationOrderIntegrationFacadeImpl;
import de.hybris.platform.sap.productconfig.facades.impl.ConfigurationQuoteIntegrationFacadeImpl;
import de.hybris.platform.sap.productconfig.facades.impl.ConfigurationSavedCartIntegrationFacadeImpl;
import de.hybris.platform.sap.productconfig.service.integrationtests.CPQServiceLayerTest;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.testdata.impl.ConfigurationValueHelperImpl;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.mockito.MockitoAnnotations;


@SuppressWarnings("javadoc")
public abstract class CPQFacadeLayerTest extends CPQServiceLayerTest
{
	private static Logger LOG = Logger.getLogger(CPQFacadeLayerTest.class);

	@Resource(name = "saveCartFacade")
	protected DefaultSaveCartFacade saveCartFacade;

	@Resource(name = "commerceCartFactory")
	protected CommerceCartFactory commerceCartFactory;

	@Resource(name = "defaultQuoteFacade")
	protected DefaultQuoteFacade defaultQuoteFacade;

	@Resource(name = "sapProductConfigFacade")
	protected ConfigurationFacade cpqFacade;

	@Resource(name = "sapProductConfigCartIntegrationFacade")
	protected ConfigurationCartIntegrationFacade cpqCartFacade;

	@Resource(name = "sapProductConfigSavedCartIntegrationFacade")
	protected ConfigurationSavedCartIntegrationFacadeImpl cpqSavedCartFacade;

	@Resource(name = "sapProductConfigOrderIntegrationFacade")
	protected ConfigurationOrderIntegrationFacadeImpl configOrderIntegrationFacade;

	@Resource(name = "sapProductConfigQuoteIntegrationFacade")
	protected ConfigurationQuoteIntegrationFacadeImpl configQuoteIntegrationFacade;

	@Resource(name = "sapProductConfigSessionAccessFacade")
	protected SessionAccessFacade cpqSessionAccessFacade;

	protected ConfigurationValueHelperImpl facadeConfigValueHelper = new ConfigurationValueHelperImpl();

	private ProductConfigurationService defaultService;


	protected static final KBKeyData KB_KEY_Y_SAP_SIMPLE_POC;
	protected static final KBKeyData KB_KEY_CPQ_HOME_THEATER;
	protected static final KBKeyData KB_KEY_CPQ_LAPTOP;
	protected static final KBKeyData KB_KEY_KD990SOL;
	protected static final KBKeyData KB_KEY_NUMERIC_PRODUCT;

	static
	{
		KB_KEY_Y_SAP_SIMPLE_POC = new KBKeyData();
		KB_KEY_Y_SAP_SIMPLE_POC.setProductCode("YSAP_SIMPLE_POC");
		KB_KEY_Y_SAP_SIMPLE_POC.setKbName("YSAP_SIMPLE_POC_KB");
		KB_KEY_Y_SAP_SIMPLE_POC.setKbLogsys("WEFCLNT504");
		KB_KEY_Y_SAP_SIMPLE_POC.setKbVersion("3800");

		KB_KEY_CPQ_HOME_THEATER = new KBKeyData();
		KB_KEY_CPQ_HOME_THEATER.setProductCode(PRODUCT_CODE_CPQ_HOME_THEATER);

		KB_KEY_CPQ_LAPTOP = new KBKeyData();
		KB_KEY_CPQ_LAPTOP.setProductCode("CPQ_LAPTOP");

		KB_KEY_NUMERIC_PRODUCT = new KBKeyData();
		KB_KEY_NUMERIC_PRODUCT.setProductCode("000000000000056227");

		KB_KEY_KD990SOL = new KBKeyData();
		KB_KEY_KD990SOL.setProductCode("KD990SOL");
		KB_KEY_KD990SOL.setKbName("KD990SOL");
		KB_KEY_KD990SOL.setKbLogsys("WEFCLNT504");
		KB_KEY_KD990SOL.setKbVersion("2");
	}

	@Override
	protected void prepareCPQData() throws Exception
	{
		Assert.assertNotNull("Test setup failed, cpqFacade is null", cpqFacade);
		Assert.assertNotNull("Test setup failed, cpqCartFacade is null", cpqCartFacade);
		super.prepareCPQData();
	}


	@Before
	public void injectMockedUserService()
	{
		MockitoAnnotations.initMocks(this);
		commerceCartFactory.setUserService(mockedUserService);
		saveCartFacade.setUserService(mockedUserService);
		cpqSavedCartFacade.setUserService(mockedUserService);
		configOrderIntegrationFacade.setUserService(mockedUserService);
		defaultQuoteFacade.setUserService(mockedUserService);
		configQuoteIntegrationFacade.setUserService(mockedUserService);
	}

	@After
	public void injectStandardUserService()
	{
		commerceCartFactory.setUserService(realUserService);
		saveCartFacade.setUserService(realUserService);
		cpqSavedCartFacade.setUserService(realUserService);
		configOrderIntegrationFacade.setUserService(realUserService);
		defaultQuoteFacade.setUserService(realUserService);
		configQuoteIntegrationFacade.setUserService(realUserService);
	}

}
