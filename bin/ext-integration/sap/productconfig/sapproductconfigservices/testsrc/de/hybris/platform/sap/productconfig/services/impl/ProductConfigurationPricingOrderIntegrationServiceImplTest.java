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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ProductInfoStatus;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceSummaryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.PriceModelImpl;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.data.CartEntryConfigurationAttributes;
import de.hybris.platform.sap.productconfig.services.intf.PricingService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.tracking.TrackingRecorder;
import de.hybris.platform.servicelayer.model.ModelService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@SuppressWarnings("javadoc")
@UnitTest
public class ProductConfigurationPricingOrderIntegrationServiceImplTest
{

	@Mock
	private TrackingRecorder recorder;
	@Mock
	private PricingService pricingService;

	private static final String DUMMY_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><SOLUTION><CONFIGURATION CFGINFO=\"\" CLIENT=\"000\" COMPLETE=\"F\" CONSISTENT=\"T\" KBBUILD=\"3\" KBNAME=\"DUMMY_KB\" KBPROFILE=\"DUMMY_KB\" KBVERSION=\"3800\" LANGUAGE=\"E\" LANGUAGE_ISO=\"EN\" NAME=\"SCE 5.0\" ROOT_NR=\"1\" SCEVERSION=\" \"><INST AUTHOR=\"5\" CLASS_TYPE=\"300\" COMPLETE=\"F\" CONSISTENT=\"T\" INSTANCE_GUID=\"\" INSTANCE_ID=\"01\" NR=\"1\" OBJ_KEY=\"DUMMY_KB\" OBJ_TXT=\"Dummy KB\" OBJ_TYPE=\"MARA\" QTY=\"1.0\" UNIT=\"ST\"><CSTICS><CSTIC AUTHOR=\"8\" CHARC=\"DUMMY_CSTIC\" CHARC_TXT=\"Dummy CStic\" VALUE=\"8\" VALUE_TXT=\"Value 8\"/></CSTICS></INST><PARTS/><NON_PARTS/></CONFIGURATION><SALES_STRUCTURE><ITEM INSTANCE_GUID=\"\" INSTANCE_ID=\"1\" INSTANCE_NR=\"1\" LINE_ITEM_GUID=\"\" PARENT_INSTANCE_NR=\"\"/></SALES_STRUCTURE></SOLUTION>";
	private static final String NEW_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><SOLUTION><CONFIGURATION CFGINFO=\"\" CLIENT=\"000\" COMPLETE=\"F\" CONSISTENT=\"T\" KBBUILD=\"3\" KBNAME=\"DUMMY_KB\" KBPROFILE=\"DUMMY_KB\" KBVERSION=\"3800\" LANGUAGE=\"E\" LANGUAGE_ISO=\"EN\" NAME=\"SCE 5.0\" ROOT_NR=\"1\" SCEVERSION=\" \"><INST AUTHOR=\"5\" CLASS_TYPE=\"300\" COMPLETE=\"F\" CONSISTENT=\"T\" INSTANCE_GUID=\"\" INSTANCE_ID=\"01\" NR=\"1\" OBJ_KEY=\"DUMMY_KB\" OBJ_TXT=\"Dummy KB\" OBJ_TYPE=\"MARA\" QTY=\"1.0\" UNIT=\"ST\"><CSTICS><CSTIC AUTHOR=\"8\" CHARC=\"DUMMY_CSTIC\" CHARC_TXT=\"Dummy CStic\" VALUE=\"9\" VALUE_TXT=\"Value 9\"/></CSTICS></INST><PARTS/><NON_PARTS/></CONFIGURATION><SALES_STRUCTURE><ITEM INSTANCE_GUID=\"\" INSTANCE_ID=\"1\" INSTANCE_NR=\"1\" LINE_ITEM_GUID=\"\" PARENT_INSTANCE_NR=\"\"/></SALES_STRUCTURE></SOLUTION>";

	private static final String CONFIG_ID_2 = "asdasdwer4543556zgfhvchtr";
	private static final String CONFIG_ID_1 = "asdsafsdgftert6er6erzz";

	private ProductConfigurationPricingOrderIntegrationServiceImpl cut;

	@Mock
	private ProductConfigurationService configurationService;

	@Mock
	private ConfigModel modelMock;

	private static final String CONFIG_ID = "abc123";

	@Mock
	private CartEntryModel cartEntry;

	@Mock
	private ProductModel productModel;

	@Mock
	private SessionAccessService sessionAccessService;

	@Mock
	private ModelService modelService;

	@Mock
	private CommerceCartService commerceCartService;

	private static final long keyAsLong = 12;

	private final PK primaryKey = PK.fromLong(keyAsLong);

	private CommerceCartParameter parameters;

	private static final String configId = "1";

	private final ConfigModel configModel = new ConfigModelImpl();

	private final InstanceModel instanceModel = new InstanceModelImpl();

	@Mock
	private CartModel cart;

	protected void prepareModelsForUpdateCartEntryPrices()
	{
		final PriceModel price = new PriceModelImpl();
		price.setPriceValue(new BigDecimal(2));
		price.setCurrency("EUR");
		configModel.setCurrentTotalPrice(price);
		configModel.setId("123");
	}


	@Before
	public void setup()
	{
		cut = new ProductConfigurationPricingOrderIntegrationServiceImpl();
		MockitoAnnotations.initMocks(this);
		cut.setPricingService(pricingService);
		cut.setConfigurationService(configurationService);
		Mockito.when(configurationService.retrieveConfigurationModel(configId)).thenReturn(configModel);
		Mockito.when(Boolean.valueOf(pricingService.isActive())).thenReturn(Boolean.FALSE);
		cut.setSessionAccessService(sessionAccessService);
		cut.setRecorder(recorder);
		cut.setCommerceCartService(commerceCartService);
		cut.setModelService(modelService);

		Mockito.when(modelMock.getId()).thenReturn(CONFIG_ID);
		Mockito.when(cartEntry.getPk()).thenReturn(primaryKey);
		Mockito.when(cartEntry.getProduct()).thenReturn(productModel);
		Mockito.when(cartEntry.getOrder()).thenReturn(cart);
		Mockito.when(sessionAccessService.getConfigIdForCartEntry(primaryKey.toString())).thenReturn(configId);

		Mockito.when(sessionAccessService.getConfigurationModelEngineState(configId)).thenReturn(configModel);

		configModel.setRootInstance(instanceModel);
		configModel.setId(configId);
		instanceModel.setSubInstances(Collections.EMPTY_LIST);

		parameters = new CommerceCartParameter();
		parameters.setConfigId(CONFIG_ID);
	}

	@Test
	public void testUpdateCartEntryExternalConfiguration() throws Exception
	{
		Mockito.when(configurationService.retrieveExternalConfiguration(CONFIG_ID)).thenReturn(DUMMY_XML);
		cut.updateCartEntryExternalConfiguration(parameters, cartEntry);
		Mockito.verify(cartEntry).setExternalConfiguration(DUMMY_XML);
		Mockito.verify(modelService).save(cartEntry);

	}

	@Test
	public void testUpdateCartEntryExternalConfiguration_withXML() throws Exception
	{
		final CartEntryModel cartEntry = Mockito.spy(new CartEntryModel());
		Mockito.when(cartEntry.getPk()).thenReturn(primaryKey);
		Mockito.when(cartEntry.getProduct()).thenReturn(productModel);
		cartEntry.setExternalConfiguration(DUMMY_XML);
		final ConfigModel cfgModel = createConfigModel();
		final String cartEntryKey = cartEntry.getPk().toString();
		Mockito.when(
				configurationService.createConfigurationFromExternal(Mockito.any(), Mockito.eq(NEW_XML), Mockito.eq(cartEntryKey)))
				.thenReturn(cfgModel);

		Mockito.when(configurationService.retrieveExternalConfiguration(CONFIG_ID)).thenReturn(NEW_XML);

		cut.updateCartEntryExternalConfiguration(NEW_XML, cartEntry);

		Mockito.verify(cartEntry).setExternalConfiguration(NEW_XML);
		assertEquals("New XML should be set on cartEntry", NEW_XML, cartEntry.getExternalConfiguration());
		Mockito.verify(modelService, Mockito.times(0)).save(cartEntry);
	}

	@Test
	public void testUpdateCartEntryBasePrice_NoPrice() throws Exception
	{
		Mockito.when(configurationService.retrieveConfigurationModel(CONFIG_ID)).thenReturn(modelMock);
		Mockito.when(modelMock.getCurrentTotalPrice()).thenReturn(PriceModel.NO_PRICE);

		final boolean entryUpdated = cut.updateCartEntryBasePrice(cartEntry);

		assertFalse("Entry should not be updated", entryUpdated);

	}

	@Test
	public void testUpdateCartEntryBasePrice() throws Exception
	{
		final ConfigModel cfgModel = createConfigModel();
		Mockito.when(configurationService.retrieveConfigurationModel(CONFIG_ID)).thenReturn(cfgModel);
		Mockito.when(sessionAccessService.getConfigIdForCartEntry(Mockito.any())).thenReturn(CONFIG_ID);

		final boolean entryUpdated = cut.updateCartEntryBasePrice(cartEntry);

		Mockito.verify(cartEntry, Mockito.times(1)).setBasePrice(
				Mockito.eq(Double.valueOf(cfgModel.getCurrentTotalPrice().getPriceValue().doubleValue())));

		assertTrue("Entry should be updated", entryUpdated);

	}

	private ConfigModel createConfigModel()
	{
		final PriceModel currentTotalPrice = new PriceModelImpl();
		final ConfigModel configModel = new ConfigModelImpl();
		configModel.setId(CONFIG_ID);
		currentTotalPrice.setCurrency("EUR");
		currentTotalPrice.setPriceValue(BigDecimal.valueOf(132.85));
		configModel.setCurrentTotalPrice(currentTotalPrice);
		return configModel;
	}


	@Test
	public void testGetLockDifferrentForDifferntConfigIds()
	{
		final Object lock1 = ProductConfigurationServiceImpl.getLock(CONFIG_ID_1);
		final Object lock2 = ProductConfigurationServiceImpl.getLock(CONFIG_ID_2);
		Assert.assertNotSame("Lock objects should not be same!", lock1, lock2);
	}

	@Test
	public void testGetLockSameforSameConfigIds()
	{
		final Object lock1 = ProductConfigurationServiceImpl.getLock(CONFIG_ID_1);
		final Object lock2 = ProductConfigurationServiceImpl.getLock(CONFIG_ID_1);
		Assert.assertSame("Lock objects should be same!", lock1, lock2);
	}

	@Test
	public void testGetLockMapShouldNotGrowEndless()
	{

		final Object lock1 = ProductConfigurationServiceImpl.getLock(CONFIG_ID_1);
		final int maxLocks = ProductConfigurationServiceImpl.getMaxLocksPerMap() * 2;
		for (int ii = 0; ii <= maxLocks; ii++)
		{
			ProductConfigurationServiceImpl.getLock(String.valueOf(ii));
		}
		final Object lock2 = ProductConfigurationServiceImpl.getLock(CONFIG_ID_1);
		Assert.assertNotSame("Lock objects should not be same!", lock1, lock2);
	}

	@Test
	public void testSessionAccessService()
	{
		final SessionAccessService sessionAccessService = new SessionAccessServiceImpl();
		cut.setSessionAccessService(sessionAccessService);
		assertEquals("Service should be available", sessionAccessService, cut.getSessionAccessService());
	}

	@Test
	public void testGetCartEntryConfigurationAttributesEmptyConfig()
	{
		final CartEntryConfigurationAttributes entryAttribs = cut.calculateCartEntryConfigurationAttributes(cartEntry);
		assertNotNull(entryAttribs);
		assertEquals("Empty configuration not consistent", Boolean.FALSE, entryAttribs.getConfigurationConsistent());
		assertEquals("No errors expected", 0, entryAttribs.getNumberOfErrors().intValue());
	}

	@Test
	public void testGetCartEntryConfigurationAttributesNoExternalCFG()
	{
		Mockito.when(sessionAccessService.getConfigurationModelEngineState(configId)).thenReturn(null);

		// no configuration: in this case we create a default configuration
		// which should not contain issues
		final CartEntryConfigurationAttributes cartEntryConfigurationAttributes = cut
				.calculateCartEntryConfigurationAttributes(cartEntry);
		assertEquals("No errors expected", 0, cartEntryConfigurationAttributes.getNumberOfErrors().intValue());

	}

	@Test
	public void testGetCartEntryConfigurationAttributesNumberOfIssues()
	{
		Mockito.when(Integer.valueOf(configurationService.getTotalNumberOfIssues(configModel))).thenReturn(Integer.valueOf(1));
		final CartEntryConfigurationAttributes entryAttribs = cut.calculateCartEntryConfigurationAttributes(cartEntry);
		assertNotNull(entryAttribs);
		assertEquals("One error expected", 1, entryAttribs.getNumberOfErrors().intValue());
	}

	@Test
	public void testGetCartEntryConfigurationAttributes()
	{
		configModel.setComplete(true);
		configModel.setConsistent(true);
		checkCartEntryConsistent();
	}

	@Test
	public void testGetCartEntryConfigurationAttributesNotComplete()
	{
		configModel.setComplete(false);
		configModel.setConsistent(true);
		checkCartEntryNotConsistent();
	}

	@Test
	public void testGetCartEntryConfigurationAttributesNotConsistent()
	{
		configModel.setComplete(true);
		configModel.setConsistent(false);
		checkCartEntryNotConsistent();
	}

	private void checkCartEntryConsistent()
	{
		final CartEntryConfigurationAttributes entryAttribs = cut.calculateCartEntryConfigurationAttributes(cartEntry);
		assertNotNull(entryAttribs);
		assertEquals("Configuration should be consistent ", Boolean.TRUE, entryAttribs.getConfigurationConsistent());
	}

	private void checkCartEntryNotConsistent()
	{
		final CartEntryConfigurationAttributes entryAttribs = cut.calculateCartEntryConfigurationAttributes(cartEntry);
		assertNotNull(entryAttribs);
		assertEquals("Configuration shouldn't be consistent ", Boolean.FALSE, entryAttribs.getConfigurationConsistent());
	}

	@Test
	public void testNoConfigID()
	{
		final String cartEntryKey = cartEntry.getPk().toString();
		final String externalConfig = "testExternalConfig";
		Mockito.when(cartEntry.getExternalConfiguration()).thenReturn(externalConfig);
		Mockito.when(sessionAccessService.getConfigIdForCartEntry(primaryKey.toString())).thenReturn(null);
		Mockito.when(
				configurationService.createConfigurationFromExternal(Mockito.any(), Mockito.eq(externalConfig),
						Mockito.eq(cartEntryKey))).thenReturn(configModel);

		final CartEntryConfigurationAttributes entryAttribs = cut.calculateCartEntryConfigurationAttributes(cartEntry);
		assertNotNull(entryAttribs);
	}

	@Test
	public void testEnsureConfigurationInSessionWithIdAndModel()
	{
		final String cartEntryKey = cartEntry.getPk().toString();
		cut.ensureConfigurationInSession(cartEntryKey, cartEntry.getProduct().getCode(), cartEntry.getExternalConfiguration());
		verify(configurationService, times(1)).retrieveConfigurationModel(configId);
		verify(configurationService, times(0)).createDefaultConfiguration(Mockito.any());
		verify(configurationService, times(0)).createConfigurationFromExternal(Mockito.any(), Mockito.any());
	}

	@Test
	public void testEnsureConfigurationInSessionWithoutIdAndWithoutExternal()
	{
		Mockito.when(sessionAccessService.getConfigIdForCartEntry(primaryKey.toString())).thenReturn(null);
		Mockito.when(configurationService.createDefaultConfiguration(Mockito.any())).thenReturn(configModel);
		cut.ensureConfigurationInSession(cartEntry.getPk().toString(), cartEntry.getProduct().getCode(),
				cartEntry.getExternalConfiguration());
		verify(configurationService, times(0)).retrieveConfigurationModel(configId);
		verify(configurationService, times(1)).createDefaultConfiguration(Mockito.any());
		verify(configurationService, times(0)).createConfigurationFromExternal(Mockito.any(), Mockito.any());
	}

	@Test
	public void testEnsureConfigurationInSessionWithoutIdAndWithExternal()
	{
		Mockito.when(cartEntry.getExternalConfiguration()).thenReturn(DUMMY_XML);
		final String cartEntryKey = cartEntry.getPk().toString();
		Mockito.when(
				configurationService.createConfigurationFromExternal(Mockito.any(), Mockito.eq(DUMMY_XML), Mockito.eq(cartEntryKey)))
				.thenReturn(configModel);
		Mockito.when(sessionAccessService.getConfigIdForCartEntry(primaryKey.toString())).thenReturn(null);
		cut.ensureConfigurationInSession(cartEntryKey, cartEntry.getProduct().getCode(), cartEntry.getExternalConfiguration());
		verify(configurationService, times(0)).retrieveConfigurationModel(configId);
		verify(configurationService, times(0)).createDefaultConfiguration(Mockito.any());
		verify(configurationService, times(1)).createConfigurationFromExternal(Mockito.any(), Mockito.eq(DUMMY_XML),
				Mockito.eq(cartEntryKey));
	}

	@Test
	public void testFillSummaryMap()
	{
		Mockito.when(Integer.valueOf(configurationService.getTotalNumberOfIssues(configModel))).thenReturn(Integer.valueOf(1));
		final ArgumentCaptor<Map> arg = ArgumentCaptor.forClass(Map.class);
		cut.fillSummaryMap(cartEntry);
		verify(cartEntry, times(1)).setCpqStatusSummaryMap(arg.capture());
		assertNotNull(arg.getValue());
		assertEquals(1, arg.getValue().size());
		assertEquals(Integer.valueOf(1), arg.getValue().get(ProductInfoStatus.ERROR));
	}

	@Test
	public void testFillSummaryMapClear()
	{
		configModel.setComplete(true);
		configModel.setConsistent(true);
		final ArgumentCaptor<Map> arg = ArgumentCaptor.forClass(Map.class);
		cut.fillSummaryMap(cartEntry);
		verify(cartEntry, times(1)).setCpqStatusSummaryMap(arg.capture());
		assertNotNull(arg.getValue());
		assertTrue(arg.getValue().isEmpty());
	}


	@Test
	public void testRetrieveCurrentTotalPriceSSC() throws ConfigurationEngineException
	{
		final ConfigModel cfgModel = createConfigModel();
		Mockito.when(configurationService.retrieveConfigurationModel(CONFIG_ID)).thenReturn(cfgModel);
		Mockito.when(Boolean.valueOf(pricingService.isActive())).thenReturn(Boolean.FALSE);
		final PriceModel result = cut.retrieveCurrentTotalPrice(CONFIG_ID);
		assertNotNull(result);
		Mockito.verify(pricingService, Mockito.times(0)).getPriceSummary(CONFIG_ID);
		Mockito.verify(configurationService).retrieveConfigurationModel(CONFIG_ID);
	}

	@Test
	public void testRetrieveCurrentTotalPriceCPS()
	{
		final PriceSummaryModel priceSummary = new PriceSummaryModel();
		priceSummary.setCurrentTotalPrice(new PriceModelImpl());
		Mockito.when(pricingService.getPriceSummary(CONFIG_ID)).thenReturn(priceSummary);
		Mockito.when(Boolean.valueOf(pricingService.isActive())).thenReturn(Boolean.TRUE);
		final PriceModel result = cut.retrieveCurrentTotalPrice(CONFIG_ID);
		assertNotNull(result);
		Mockito.verify(pricingService).getPriceSummary(CONFIG_ID);
		Mockito.verify(configurationService, Mockito.times(0)).retrieveConfigurationModel(CONFIG_ID);
	}

	@Test
	public void testRetrieveCurrentTotalPriceCPSNull()
	{
		Mockito.when(pricingService.getPriceSummary(CONFIG_ID)).thenReturn(null);
		Mockito.when(Boolean.valueOf(pricingService.isActive())).thenReturn(Boolean.TRUE);
		final PriceModel result = cut.retrieveCurrentTotalPrice(CONFIG_ID);
		assertNull(result);
		Mockito.verify(pricingService).getPriceSummary(CONFIG_ID);
	}

	@Test
	public void testGetParametersForCartUpdate()
	{
		final CommerceCartParameter result = cut.getParametersForCartUpdate(cartEntry);
		assertEquals(cartEntry.getOrder(), result.getCart());
		assertEquals(configId, result.getConfigId());
		assertTrue(result.isEnableHooks());
	}

	@Test
	public void testhasBasePriceChanged()
	{
		assertTrue(cut.hasBasePriceChanged(cartEntry, Double.valueOf(4)));
	}

	@Test
	public void testhasBasePriceChanged_Not()
	{
		assertFalse(cut.hasBasePriceChanged(cartEntry, cartEntry.getBasePrice()));
	}

	@Test
	public void testUpdateCartEntryPricesEntrySaved()
	{
		prepareModelsForUpdateCartEntryPrices();
		assertTrue(cut.updateCartEntryPrices(cartEntry, true, null));
		Mockito.verify(commerceCartService).calculateCart(Mockito.any(CommerceCartParameter.class));
		Mockito.verify(modelService).save(cartEntry);
	}

	@Test
	public void testUpdateCartEntryPricesNoCalculate()
	{
		prepareModelsForUpdateCartEntryPrices();
		assertTrue(cut.updateCartEntryPrices(cartEntry, false, null));
		Mockito.verify(commerceCartService, Mockito.times(0)).calculateCart(Mockito.any(CommerceCartParameter.class));
		Mockito.verify(modelService).save(cartEntry);
		Mockito.verify(modelService).save(cartEntry.getOrder());
	}

	@Test
	public void testUpdateCartEntryPricesCartSaved()
	{
		prepareModelsForUpdateCartEntryPrices();
		assertTrue(cut.updateCartEntryPrices(cartEntry, false, null));
		Mockito.verify(modelService).save(cartEntry.getOrder());
	}


	@Test
	public void testUpdateCartEntryPrices_passedParameter()
	{
		prepareModelsForUpdateCartEntryPrices();
		final CommerceCartParameter passedParameter = new CommerceCartParameter();
		assertTrue(cut.updateCartEntryPrices(cartEntry, true, passedParameter));
		Mockito.verify(modelService).save(cartEntry);
		Mockito.verify(commerceCartService).calculateCart(passedParameter);
	}

	@Test
	public void testUpdateCartEntryPricesNoUpdate()
	{
		configModel.setCurrentTotalPrice(null);
		assertFalse(cut.updateCartEntryPrices(cartEntry, true, null));
		Mockito.verify(modelService, Mockito.times(0)).save(cartEntry);
		Mockito.verify(commerceCartService, Mockito.times(0)).calculateCart(Mockito.any(CommerceCartParameter.class));
	}

}
