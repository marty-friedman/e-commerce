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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.catalog.enums.ProductInfoStatus;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.ConfigurationInfoData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.quote.data.QuoteData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.impl.CommerceCartFactory;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("javadoc")
@IntegrationTest
public class CPQQuoteFacadeIntegrationTest extends CPQFacadeLayerTest
{
	private static Logger LOG = Logger.getLogger(CPQQuoteFacadeIntegrationTest.class);




	@Resource(name = "cartFactory")
	CommerceCartFactory commerceCartFactory;

	@Resource(name = "cartFacade")
	private CartFacade cartFacade;

	@Resource(name = "cartService")
	private CartService cartService;

	@Resource(name = "quoteService")
	private QuoteService quoteService;


	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "sapProductConfigConfigurationService")
	private ProductConfigurationService cpqConfigurationService;



	@Override
	protected void importCPQTestData() throws Exception
	{
		super.importCPQTestData();
		importCPQUserData();
		importCsv("/sapproductconfigfacades/test/sapProductConfig_quote_testData.impex", "utf-8");
	}



	@Before
	public void setUp() throws Exception
	{
		prepareCPQData();


		/*
		 * Ensure we have the same user across the entire process
		 */
		defaultQuoteFacade.setQuoteUserIdentificationStrategy(mockedQuoteUserIdentificationStrategy);

		configQuoteIntegrationFacade.setBaseStoreService(baseStoreService);
		configQuoteIntegrationFacade.setQuoteUserIdentificationStrategy(mockedQuoteUserIdentificationStrategy);
		configQuoteIntegrationFacade.setQuoteService(quoteService);
	}


	@Test
	public void testQuoteDataHasConfigurationData() throws CommerceCartModificationException
	{
		final ConfigurationData configData = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);

		final String cartItemKey = cpqCartFacade.addConfigurationToCart(configData);
		assertNotNull(cartItemKey);
		if (LOG.isDebugEnabled())
		{
			final StringBuilder sb = new StringBuilder().append("Configuration with ID ").append(configData.getConfigId())
					.append(" has been added to cart with item key ").append(cartItemKey);
			LOG.debug(sb.toString());
		}

		final QuoteData result = defaultQuoteFacade.initiateQuote();
		LOG.debug("Quote has been created from cart");
		final OrderEntryData entry = validateQuoteBasic(result);

		final List<ConfigurationInfoData> configQuoteData = entry.getConfigurationInfos();
		assertNotNull(configQuoteData);

		assertEquals(1, configQuoteData.size());
		assertEquals(ConfiguratorType.CPQCONFIGURATOR, configQuoteData.get(0).getConfiguratorType());
		assertEquals(ProductInfoStatus.SUCCESS, configQuoteData.get(0).getStatus());
		assertEquals("Simple Flag: Hide options", configQuoteData.get(0).getConfigurationLabel());
		assertEquals("Hide", configQuoteData.get(0).getConfigurationValue());
	}

	protected OrderEntryData validateQuoteBasic(final QuoteData result)
	{
		assertNotNull(result);
		assertNotNull(result.getEntries());
		assertEquals(1, result.getEntries().size());
		final OrderEntryData entry = result.getEntries().get(0);

		assertNotNull(entry.getItemPK());
		assertTrue(entry.isConfigurationAttached());

		assertFalse(entry.isConfigurationConsistent());
		assertEquals(2, entry.getConfigurationErrorCount());

		assertNotNull(entry.getStatusSummaryMap());
		assertEquals(1, entry.getStatusSummaryMap().size());
		assertEquals(Integer.valueOf(2), entry.getStatusSummaryMap().get(ProductInfoStatus.ERROR));
		return entry;
	}

	@Test
	public void testQuoteSessionArtifacts() throws CommerceCartModificationException
	{

		/*
		 * Step 1: Create a cart containing one entry. We expect to have session artifacts belonging to our cart entry
		 */
		final ConfigurationData configData = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		final String cartEntryKey = cpqCartFacade.addConfigurationToCart(configData);
		checkSessionArtifactsPresent(cartEntryKey);

		/*
		 * Step 2: Create a quote from cart. Now the session artifacts for the previous cart entry are gone as we released
		 * them, also there are no artifacts for the new quote entry yet
		 */
		final QuoteData quote = defaultQuoteFacade.initiateQuote();
		assertNotNull(quote);
		final List<OrderEntryData> quoteEntries = quote.getEntries();
		assertEquals(1, quoteEntries.size());
		final String quoteEntryKey = quoteEntries.get(0).getItemPK();

		checkSessionArtifactsNone(cartEntryKey);
		checkSessionArtifactsNone(quoteEntryKey);

		/*
		 * Step 3: Now we start quotation edit process, quotation is not submitted yet. We get a new session cart entry,
		 * based on its corresponding quote entry, for which we expect to have session artifacts
		 */
		defaultQuoteFacade.enableQuoteEdit(quote.getCode());
		final List<OrderEntryData> cartEntriesQuoteEdit = cartFacade.getSessionCart().getEntries();
		assertEquals(1, cartEntriesQuoteEdit.size());
		final String cartEntryQuoteEdit = cartEntriesQuoteEdit.get(0).getItemPK();
		assertFalse(cartEntryQuoteEdit.equals(quoteEntryKey));
		assertFalse(cartEntryQuoteEdit.equals(cartEntryKey));
		checkSessionArtifactsPresent(cartEntryQuoteEdit);


		/*
		 * Step 4: Quotation is submitted. All session artifacts must be gone
		 */
		// disable treshhold check, only executed if quote user equls cart user
		cartService.getSessionCart().setUser(realUserService.getCurrentUser());
		defaultQuoteFacade.submitQuote(quote.getCode());
		checkSessionArtifactsNone(cartEntryKey);
		checkSessionArtifactsNone(quoteEntryKey);
		checkSessionArtifactsNone(cartEntryQuoteEdit);

	}

	@Test
	public void testConfigQuoteIntegrationFacade() throws CommerceCartModificationException
	{


		/*
		 * Step 1: Create a cart containing one entry.
		 */
		final ConfigurationData configData = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		cpqCartFacade.addConfigurationToCart(configData);

		/*
		 * Step 2: Create a quote from cart.
		 */
		final QuoteData quote = defaultQuoteFacade.initiateQuote();

		/*
		 * Step 3: Get the ConfigurationOverviewData from ConfigurationQuoteIntegrationFacade.
		 */
		final String quoteCode = quote.getCode();
		final int entryNumber = quote.getEntries().get(0).getEntryNumber().intValue();
		final ConfigurationOverviewData configOverview = configQuoteIntegrationFacade.getConfiguration(quoteCode, entryNumber);
		assertNotNull(configOverview);
		assertEquals(KB_KEY_Y_SAP_SIMPLE_POC.getProductCode(), configOverview.getProductCode());

	}


	protected void checkSessionArtifactsNone(final String cartEntryKey)
	{
		final String configId = cpqSessionAccessFacade.getConfigIdForCartEntry(cartEntryKey);
		assertTrue(StringUtils.isEmpty(configId));

	}

	protected void checkSessionArtifactsPresent(final String cartEntryKey)
	{
		final String configId = cpqSessionAccessFacade.getConfigIdForCartEntry(cartEntryKey);
		assertFalse("We expect a config ID for cart entry", StringUtils.isEmpty(configId));
		final String entryKeyFromSession = cpqSessionAccessFacade.getCartEntryForConfigId(configId);
		assertEquals("Cart entry keys must match", entryKeyFromSession, cartEntryKey);
		final ConfigModel configModel = cpqConfigurationService.retrieveConfigurationModel(configId);
		assertNotNull("We expect to get config model from product config service", configModel);
	}

	protected String getCartEntryKey(final CartData sessionCart)
	{
		assertEquals(1, sessionCart.getEntries().size());
		final OrderEntryData cartEntry = sessionCart.getEntries().get(0);
		final String cartKey = cartEntry.getItemPK();
		return cartKey;
	}

	@Test
	public void testQuoteDataHasNoConfigurationData() throws CommerceCartModificationException
	{
		// trigger add to cart without going through configuration facade "dark add to cart"
		final CartModificationData cartModificationData = cartFacade.addToCart(KB_KEY_Y_SAP_SIMPLE_POC.getProductCode(), 1);
		assertNotNull(cartModificationData);
		final QuoteData result = defaultQuoteFacade.initiateQuote();
		final OrderEntryData entry = validateQuoteBasic(result);

		final List<ConfigurationInfoData> configQuoteData = entry.getConfigurationInfos();
		assertNotNull(configQuoteData);
		assertEquals(1, configQuoteData.size());

		final ConfigurationInfoData info = configQuoteData.get(0);
		assertNull(info.getConfigurationLabel());
		assertNull(info.getConfigurationValue());
		assertNotNull(info.getConfiguratorType());
	}

}
