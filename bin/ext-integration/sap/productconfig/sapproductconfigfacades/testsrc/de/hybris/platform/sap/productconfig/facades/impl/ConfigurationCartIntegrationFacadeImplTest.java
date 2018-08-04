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
package de.hybris.platform.sap.productconfig.facades.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.productconfig.facades.ConfigConsistenceChecker;
import de.hybris.platform.sap.productconfig.facades.ConfigPricing;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.ConfigurationTestData;
import de.hybris.platform.sap.productconfig.facades.KBKeyData;
import de.hybris.platform.sap.productconfig.facades.PricingData;
import de.hybris.platform.sap.productconfig.facades.populator.ConfigurationOrderEntryProductInfoModelPopulator;
import de.hybris.platform.sap.productconfig.facades.populator.SolvableConflictPopulator;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingConfigurationParameter;
import de.hybris.platform.sap.productconfig.runtime.interf.ProviderFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ClassificationSystemCPQAttributesContainer;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.analytics.intf.AnalyticsService;
import de.hybris.platform.sap.productconfig.services.impl.SessionAccessServiceImpl;
import de.hybris.platform.sap.productconfig.services.intf.PricingService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPricingOrderIntegrationService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.model.CPQOrderEntryProductInfoModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


@UnitTest
public class ConfigurationCartIntegrationFacadeImplTest
{
	private static final String PRODUCT_CODE = "SAP_SIMPLE_POC";

	private static final String DUMMY_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><SOLUTION><CONFIGURATION CFGINFO=\"\" CLIENT=\"000\" COMPLETE=\"F\" CONSISTENT=\"T\" KBBUILD=\"3\" KBNAME=\"DUMMY_KB\" KBPROFILE=\"DUMMY_KB\" KBVERSION=\"3800\" LANGUAGE=\"E\" LANGUAGE_ISO=\"EN\" NAME=\"SCE 5.0\" ROOT_NR=\"1\" SCEVERSION=\" \"><INST AUTHOR=\"5\" CLASS_TYPE=\"300\" COMPLETE=\"F\" CONSISTENT=\"T\" INSTANCE_GUID=\"\" INSTANCE_ID=\"01\" NR=\"1\" OBJ_KEY=\"DUMMY_KB\" OBJ_TXT=\"Dummy KB\" OBJ_TYPE=\"MARA\" QTY=\"1.0\" UNIT=\"ST\"><CSTICS><CSTIC AUTHOR=\"8\" CHARC=\"DUMMY_CSTIC\" CHARC_TXT=\"Dummy CStic\" VALUE=\"8\" VALUE_TXT=\"Value 8\"/></CSTICS></INST><PARTS/><NON_PARTS/></CONFIGURATION><SALES_STRUCTURE><ITEM INSTANCE_GUID=\"\" INSTANCE_ID=\"1\" INSTANCE_NR=\"1\" LINE_ITEM_GUID=\"\" PARENT_INSTANCE_NR=\"\"/></SALES_STRUCTURE></SOLUTION>";


	private ConfigurationCartIntegrationFacadeImpl configCartIntegrationFacade;

	@Mock
	private CommerceCartService commerceCartService;

	@Mock
	private ProductConfigurationService configService;

	@Mock
	private ProductConfigurationPricingOrderIntegrationService configurationPricingOrderIntegrationService;

	@Mock
	private CartService cartService;

	@Mock
	private SessionAccessService sessionAccessService;

	@Mock
	private ModelService modelService;

	@Mock
	private ProductService productService;

	@Mock
	private ConfigurationOrderEntryProductInfoModelPopulator configInfoPopulator;

	@Mock
	private CartEntryModel otherCartItem;

	@Mock
	private ConfigPricing configPricing;

	@Mock
	private AbstractOrderEntryModel cartItem;

	@Mock
	private ProviderFactory providerFactory;

	@Mock
	private PricingConfigurationParameter pricingConfigurationParameters;

	private KBKeyData kbKey;

	private CartModel shoppingCart;

	private ProductModel product;

	private UnitModel unit;

	private ConfigurationData configData;

	private final List<AbstractOrderEntryModel> itemsInCart = new ArrayList<>();

	private CommerceCartModification modification;

	private ConfigurationFacadeImpl configFacade;

	@Mock
	private BaseStoreService baseStoreService;

	@Mock
	public SessionService sessionService;

	@Mock
	public PricingService pricingService;

	@Mock
	public ConfigurationVariantUtilImpl configurationVariantUtil;

	private ConfigModel createdConfigModel;

	@Mock
	private Session session;

	@Mock
	private AnalyticsService analyticsServiceMock;


	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);

		final ConfigConsistenceChecker configConsistenceChecker = new ConfigConsistenceCheckerImpl();
		final CsticTypeMapperImpl typeMapper = new CsticTypeMapperImpl();
		typeMapper.setUiTypeFinder(new UiTypeFinderImpl());
		typeMapper.setValueFormatTranslator(new ValueFormatTranslatorImpl());
		final UniqueUIKeyGeneratorImpl uiKeyGenerator = new UniqueUIKeyGeneratorImpl();
		typeMapper.setUiKeyGenerator(uiKeyGenerator);
		final IntervalInDomainHelperImpl intervalHandler = new IntervalInDomainHelperImpl();
		typeMapper.setIntervalHandler(intervalHandler);
		typeMapper.setProviderFactory(providerFactory);
		given(providerFactory.getPricingParameter()).willReturn(pricingConfigurationParameters);
		given(pricingConfigurationParameters.showDeltaPrices()).willReturn(true);

		configCartIntegrationFacade = new ConfigurationCartIntegrationFacadeImpl();
		configCartIntegrationFacade.setConfigurationService(configService);
		configCartIntegrationFacade.setCartService(cartService);
		configCartIntegrationFacade.setModelService(modelService);
		configCartIntegrationFacade.setProductService(productService);
		configCartIntegrationFacade.setCommerceCartService(commerceCartService);
		configCartIntegrationFacade.setConfigPricing(configPricing);
		configCartIntegrationFacade.setSessionAccessService(sessionAccessService);
		configCartIntegrationFacade.setConflictPopulator(new SolvableConflictPopulator());
		configCartIntegrationFacade.setConfigInfoPopulator(configInfoPopulator);
		configCartIntegrationFacade.setConfigurationVariantUtil(configurationVariantUtil);
		configCartIntegrationFacade.setUiKeyGenerator(uiKeyGenerator);
		configCartIntegrationFacade.setPricingService(pricingService);
		configCartIntegrationFacade.setAnalyticsService(analyticsServiceMock);
		configCartIntegrationFacade.setConfigurationPricingOrderIntegrationService(configurationPricingOrderIntegrationService);

		configFacade = new ConfigurationFacadeImpl();
		configFacade.setConfigurationService(configService);
		configFacade.setCsticTypeMapper(typeMapper);
		configFacade.setConfigConsistenceChecker(configConsistenceChecker);
		configFacade.setConfigPricing(configPricing);
		configFacade.setConflictPopulator(new SolvableConflictPopulator());
		configFacade.setProductService(productService);
		configFacade.setConfigurationVariantUtil(configurationVariantUtil);
		configFacade.setUiKeyGenerator(uiKeyGenerator);
		configFacade.setAnalyticsService(analyticsServiceMock);

		final SessionAccessServiceImpl sessionAccessServiceNotMock = new SessionAccessServiceImpl();
		sessionAccessServiceNotMock.setSessionService(sessionService);
		given(sessionService.getAttribute(ClassificationSystemCPQAttributesContainer.class.getName())).willReturn(null);
		Mockito.when(sessionService.getCurrentSession()).thenReturn(session);
		configFacade.setSessionAccessService(sessionAccessServiceNotMock);

		configFacade.setPricingService(pricingService);
		given(Boolean.valueOf(pricingService.isActive())).willReturn(Boolean.FALSE);

		kbKey = new KBKeyData();
		kbKey.setProductCode(PRODUCT_CODE);
		kbKey.setKbName("YSAP_SIMPLE_POC");
		kbKey.setKbLogsys("ABC");
		kbKey.setKbVersion("123");

		shoppingCart = new CartModel();
		shoppingCart.setEntries(itemsInCart);
		product = new ProductModel();
		unit = new UnitModel();

		product.setCode(PRODUCT_CODE);
		product.setUnit(unit);

		configData = new ConfigurationData();
		configData.setKbKey(kbKey);

		final PricingData pricingData = new PricingData();
		pricingData.setBasePrice(ConfigPricing.NO_PRICE);
		pricingData.setSelectedOptions(ConfigPricing.NO_PRICE);
		pricingData.setCurrentTotal(ConfigPricing.NO_PRICE);

		given(cartService.getSessionCart()).willReturn(shoppingCart);
		given(otherCartItem.getPk()).willReturn(PK.parse("1234567890"));
		given(configPricing.getPricingData(any(ConfigModel.class))).willReturn(pricingData);


		cartItem.setProduct(product);
		modification = new CommerceCartModification();
		modification.setEntry(cartItem);


		given(cartItem.getPk()).willReturn(PK.parse("123"));
		given(cartItem.getProduct()).willReturn(product);

		/*
		 * we use mock instead of real class, because calling baseStore.getName() would lead to illegalArgument Exception,
		 * as no LocalProvider is injected into the item Context. baseStore.getName() is used in some debug statetments,
		 * so when running tests with log level debug this would lead to an exception
		 */
		final BaseStoreModel baseStore = Mockito.mock(BaseStoreModel.class);
		given(baseStore.getCatalogs()).willReturn(Collections.EMPTY_LIST);
		given(baseStoreService.getCurrentBaseStore()).willReturn(baseStore);

		final ClassificationSystemCPQAttributesProviderImpl nameProvider = new ClassificationSystemCPQAttributesProviderImpl();
		nameProvider.setBaseStoreService(baseStoreService);
		typeMapper.setNameProvider(nameProvider);

		given(productService.getProductForCode(PRODUCT_CODE)).willReturn(product);
	}


	private ConfigModel initializeFirstCall()
	{
		createdConfigModel = ConfigurationTestData.createConfigModelWithCstic();
		given(configService.createDefaultConfiguration(any(KBKey.class))).willReturn(createdConfigModel);
		given(configService.retrieveConfigurationModel(createdConfigModel.getId())).willReturn(createdConfigModel);
		return createdConfigModel;
	}

	@Test
	public void testAddConfigurationToCart() throws CommerceCartModificationException
	{
		initializeFirstCall();
		final ConfigurationData configContent = configFacade.getConfiguration(kbKey);

		given(configService.retrieveExternalConfiguration(configContent.getConfigId())).willReturn(DUMMY_XML);
		given(productService.getProductForCode(kbKey.getProductCode())).willReturn(product);


		given(commerceCartService.addToCart(Mockito.any(CommerceCartParameter.class))).willReturn(modification);

		configCartIntegrationFacade.addConfigurationToCart(configContent);

		Mockito.verify(cartItem).setProduct(product);

	}

	@Test
	public void testAddConfigurationToCartWithQty() throws CommerceCartModificationException
	{
		initializeFirstCall();
		final ConfigurationData configContent = configFacade.getConfiguration(kbKey);

		given(configService.retrieveExternalConfiguration(configContent.getConfigId())).willReturn(DUMMY_XML);
		given(productService.getProductForCode(kbKey.getProductCode())).willReturn(product);

		when(commerceCartService.addToCart(Mockito.any(CommerceCartParameter.class))).then(createAddToCartAnswer());

		final long myQty = 7L;
		configContent.setQuantity(myQty);
		configCartIntegrationFacade.addConfigurationToCart(configContent);
		assertEquals(Long.valueOf(myQty), cartItem.getQuantity());
	}


	@Test
	public void testCopyConfiguration()
	{

		initializeFirstCall();
		given(configService.createDefaultConfiguration(any(KBKey.class))).willReturn(createdConfigModel);
		final ConfigModel clonedConfigModel = new ConfigModelImpl();
		final String newId = "B";
		clonedConfigModel.setId(newId);

		given(configService.createConfigurationFromExternal(any(KBKey.class), any(String.class))).willReturn(clonedConfigModel);
		final String newConfigId = configCartIntegrationFacade.copyConfiguration(createdConfigModel.getId(),
				ConfigurationTestData.ROOT_INSTANCE_NAME);
		assertEquals(newId, newConfigId);
	}


	@Test
	public void testConvertNullToNullPK()
	{
		final PK pk = configCartIntegrationFacade.convertStringToPK(null);
		assertEquals("null value should be mapped to NULL PK", PK.NULL_PK, pk);
	}

	@Test
	public void testConvertEmptyStringToNullPK()
	{
		final PK pk = configCartIntegrationFacade.convertStringToPK("");
		assertEquals("empty value should be mapped to NULL PK", PK.NULL_PK, pk);
	}

	@Test
	public void testConvertStringToPK()
	{
		final PK pk = configCartIntegrationFacade.convertStringToPK("123");
		assertEquals("string pk conversion failed", PK.parse("123"), pk);
	}

	@Test
	public void testGetOrCreateCartItem_newItemWithQty() throws CommerceCartModificationException
	{
		itemsInCart.add(otherCartItem);

		when(commerceCartService.addToCart(Mockito.any(CommerceCartParameter.class))).then(createAddToCartAnswer());
		final long myQty = 5L;
		configData.setQuantity(myQty);
		final AbstractOrderEntryModel cartItem = configCartIntegrationFacade.getOrCreateCartItem(product, configData);
		assertNotNull(cartItem);
		assertEquals(Long.valueOf(myQty), cartItem.getQuantity());
	}

	protected Answer createAddToCartAnswer()
	{
		final Answer answer = new Answer<CommerceCartModification>()
		{
			@Override
			public CommerceCartModification answer(final InvocationOnMock invocation) throws Throwable
			{
				final Object[] args = invocation.getArguments();
				final CommerceCartParameter ccp = (CommerceCartParameter) args[0];
				modification.setQuantity(ccp.getQuantity());
				given(cartItem.getQuantity()).willReturn(Long.valueOf(ccp.getQuantity()));
				return modification;
			}
		};
		return answer;
	}

	@Test
	public void testGetOrCreateCartItem_newItem() throws CommerceCartModificationException
	{
		itemsInCart.add(otherCartItem);
		given(commerceCartService.addToCart(Mockito.any(CommerceCartParameter.class))).willReturn(modification);
		final AbstractOrderEntryModel cartItem = configCartIntegrationFacade.getOrCreateCartItem(product, configData);
		assertNotNull(cartItem);
	}

	@Test
	public void testGetOrCreateCartItem_updateItem() throws CommerceCartModificationException
	{
		itemsInCart.add(otherCartItem);
		configData.setCartItemPK(otherCartItem.getPk().toString());

		given(Boolean.valueOf(modelService.isRemoved(otherCartItem))).willReturn(Boolean.FALSE);
		final AbstractOrderEntryModel cartItem = configCartIntegrationFacade.getOrCreateCartItem(product, configData);

		assertNotNull(cartItem);
	}

	@Test
	public void testItemInCart_false() throws CommerceCartModificationException
	{
		itemsInCart.add(otherCartItem);

		final boolean itemInCart = configCartIntegrationFacade.isItemInCartByKey(cartItem.getPk().toString());

		assertFalse("Item should not be in cart", itemInCart);
	}

	@Test
	public void testItemInCart_true() throws CommerceCartModificationException
	{
		itemsInCart.add(otherCartItem);
		itemsInCart.add(cartItem);

		final boolean itemInCart = configCartIntegrationFacade.isItemInCartByKey(cartItem.getPk().toString());

		assertTrue("Item should be in cart", itemInCart);
	}




	@Test
	public void testGetOrCreateCartItem_updateRemovedItem() throws CommerceCartModificationException
	{
		itemsInCart.add(otherCartItem);
		configData.setCartItemPK(otherCartItem.getPk().toString());

		given(Boolean.valueOf(modelService.isRemoved(otherCartItem))).willReturn(Boolean.TRUE);

		given(commerceCartService.addToCart(Mockito.any(CommerceCartParameter.class))).willReturn(modification);
		final AbstractOrderEntryModel cartItem = configCartIntegrationFacade.getOrCreateCartItem(product, configData);

		assertNotNull(cartItem);
		assertNotSame("New iteme expeted", otherCartItem, cartItem);
	}

	@Test
	public void testRest()
	{
		configCartIntegrationFacade.resetConfiguration("123");
		Mockito.verify(configService).releaseSession("123");
	}

	@Test(expected = RuntimeException.class)
	public void testRestoreConfigurationWrongKey()
	{
		final KBKeyData kbKey = new KBKeyData();
		final String cartEntryKey = "X";
		configCartIntegrationFacade.restoreConfiguration(kbKey, cartEntryKey);
	}

	@Test
	public void testRestoreConfiguration()
	{
		final KBKeyData kbKey = new KBKeyData();
		final String cartEntryKey = cartItem.getPk().toString();
		given(cartItem.getExternalConfiguration()).willReturn("X");
		itemsInCart.add(cartItem);

		final ConfigModel newConfigModel = new ConfigModelImpl();
		final String newId = "B";
		newConfigModel.setId(newId);
		final InstanceModel rootInstance = new InstanceModelImpl();
		final List<CsticGroupModel> csticGroups = new ArrayList<>();
		rootInstance.setCsticGroups(csticGroups);
		newConfigModel.setRootInstance(rootInstance);

		given(configService.createConfigurationFromExternal(any(KBKey.class), any(String.class))).willReturn(newConfigModel);
		given(productService.getProductForCode(any(String.class))).willReturn(product);

		assertNotNull(configCartIntegrationFacade.restoreConfiguration(kbKey, cartEntryKey));
	}

	@Test
	public void testLinkEntryWithConfigInfos()
	{
		final List<AbstractOrderEntryProductInfoModel> configInlineModels = new ArrayList<>();
		final CPQOrderEntryProductInfoModel inlineInfo = new CPQOrderEntryProductInfoModel();
		final CPQOrderEntryProductInfoModel anotherInlineInfo = new CPQOrderEntryProductInfoModel();
		configInlineModels.add(inlineInfo);
		configInlineModels.add(anotherInlineInfo);
		final ArgumentCaptor<List> arg = ArgumentCaptor.forClass(List.class);
		configCartIntegrationFacade.linkEntryWithConfigInfos(cartItem, configInlineModels);
		verify(cartItem, times(1)).setProductInfos(arg.capture());
		assertNotNull(arg.getValue());
		assertEquals(configInlineModels, arg.getValue());
		assertEquals(cartItem, inlineInfo.getOrderEntry());
		assertEquals(cartItem, anotherInlineInfo.getOrderEntry());
	}
}
