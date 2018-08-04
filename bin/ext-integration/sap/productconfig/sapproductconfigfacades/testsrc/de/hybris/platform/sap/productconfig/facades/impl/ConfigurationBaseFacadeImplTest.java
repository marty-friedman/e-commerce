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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.spy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.productconfig.facades.ConfigPricing;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.CsticTypeMapper;
import de.hybris.platform.sap.productconfig.facades.KBKeyData;
import de.hybris.platform.sap.productconfig.facades.ProductConfigMessageUISeverity;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.facades.populator.SolvableConflictPopulator;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSeverity;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSource;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSourceSubType;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ProductConfigMessageImpl;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.analytics.intf.AnalyticsService;
import de.hybris.platform.sap.productconfig.services.intf.PricingService;
import de.hybris.platform.variants.model.VariantProductModel;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@SuppressWarnings("javadoc")
@UnitTest
public class ConfigurationBaseFacadeImplTest
{
	private static final String NAME = "A";
	private static final String DESCRIPTION = "B";
	private static final String PRODUCT_CODE = "product_123";

	private ConfigurationBaseFacadeImpl classUnderTest = new ConfigurationBaseFacadeImpl();
	private UiGroupData uiGroup;
	private List<UiGroupData> subGroups;
	private boolean oneSubGroupConfigurable;
	private final UiGroupData subGroup = new UiGroupData();

	@Mock
	private ProductService productServiceMock;
	@Mock
	private SessionAccessService sessionAccessService;
	@Mock
	private PricingService pricingServiceMock;
	@Mock
	private SolvableConflictPopulator conflictsPopulator;
	@Mock
	private ProductModel productModelMock;
	@Mock
	private ConfigPricing configPricing;
	@Mock
	private ConfigurationVariantUtilImpl configurationVariantUtil;
	@Mock
	private AnalyticsService analyticsServiceMock;


	private ConfigModel configModel;
	private ConfigurationData configData;
	private InstanceModel rootInstance;
	private final KBKeyData kbKey = new KBKeyData();

	@Mock
	private CsticTypeMapper csticTypeMapper;

	protected void callPopulateAndCheckPricingMode()
	{
		given(Boolean.valueOf(analyticsServiceMock.isActive())).willReturn(Boolean.TRUE);
		given(Boolean.valueOf(pricingServiceMock.isActive())).willReturn(Boolean.TRUE);
		classUnderTest.populateConfigDataFromModel(configData, configModel);
		assertTrue(configData.isAsyncPricingMode());
		assertFalse(configData.isPricingError());
	}


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest.setProductService(productServiceMock);
		classUnderTest.setConfigurationVariantUtil(configurationVariantUtil);
		classUnderTest.setUiKeyGenerator(new UniqueUIKeyGeneratorImpl());
		classUnderTest.setSessionAccessService(sessionAccessService);
		classUnderTest.setPricingService(pricingServiceMock);
		classUnderTest.setConflictPopulator(conflictsPopulator);
		classUnderTest.setConfigPricing(configPricing);
		classUnderTest.setOfferVariantSearch(false);
		classUnderTest.setAnalyticsService(analyticsServiceMock);

		given(productServiceMock.getProductForCode(PRODUCT_CODE)).willReturn(productModelMock);

		configModel = new ConfigModelImpl();
		rootInstance = new InstanceModelImpl();
		configModel.setRootInstance(rootInstance);
		configData = new ConfigurationData();
		kbKey.setProductCode(PRODUCT_CODE);
		configData.setKbKey(kbKey);
	}

	@Test
	public void testOneGroupConfigurableFalse()
	{
		oneSubGroupConfigurable = false;
		classUnderTest.checkAdoptSubGroup(uiGroup, subGroups, oneSubGroupConfigurable);
		assertNull(subGroup.getName());
		assertNull(subGroup.getDescription());
	}

	@Test
	public void testOneGroupConfigurableTrue()
	{
		oneSubGroupConfigurable = true;
		classUnderTest.checkAdoptSubGroup(uiGroup, subGroups, oneSubGroupConfigurable);
		assertNotNull(subGroup.getName());
		assertNotNull(subGroup.getDescription());
	}


	@Before
	public void createTestData()
	{
		uiGroup = new UiGroupData();
		subGroups = new ArrayList<UiGroupData>();
		uiGroup.setName(NAME);
		uiGroup.setDescription(DESCRIPTION);
		uiGroup.setSubGroups(subGroups);
		subGroups.add(subGroup);
	}

	@Test
	public void testShowVariants_disabled()
	{
		classUnderTest.setOfferVariantSearch(false);
		assertFalse("Do not show the variants, as varaint search is disbaled", classUnderTest.showVariants(PRODUCT_CODE));
	}

	@Test
	public void testShowVariants_enabledNoVariantsExistingNull()
	{
		classUnderTest.setOfferVariantSearch(true);
		assertFalse("Do not show the variants search, as no variants exist for thze given product",
				classUnderTest.showVariants(PRODUCT_CODE));
	}

	@Test
	public void testShowVariants_enabledNoVariantsEmptyList()
	{
		classUnderTest.setOfferVariantSearch(true);
		given(productModelMock.getVariants()).willReturn(Collections.emptyList());
		assertFalse("Do not show the variants search, as no variants exist for thze given product",
				classUnderTest.showVariants(PRODUCT_CODE));
	}

	@Test
	public void testShowVariants_enabledVariantsFound()
	{
		classUnderTest.setOfferVariantSearch(true);
		given(productModelMock.getVariants()).willReturn(Collections.singletonList(new VariantProductModel()));
		given(Boolean.valueOf(configurationVariantUtil.isCPQBaseProduct(productModelMock))).willReturn(Boolean.TRUE);
		assertTrue("At least one varaint exists, so show the variant search", classUnderTest.showVariants(PRODUCT_CODE));
	}

	@Test
	public void testMapMessagesFromModelToDataEmpty()
	{
		classUnderTest.mapMessagesFromModelToData(configData, configModel);
		assertTrue(configData.getMessages().isEmpty());
	}

	@Test
	public void testMapMessagesFromModelToData_Info()
	{
		final ProductConfigMessage message = new ProductConfigMessageImpl("a_test_message", "messagekey123",
				ProductConfigMessageSeverity.INFO, ProductConfigMessageSource.ENGINE, ProductConfigMessageSourceSubType.DEFAULT);
		configModel.getMessages().add(message);

		classUnderTest.mapMessagesFromModelToData(configData, configModel);

		assertEquals(1, configData.getMessages().size());
		assertEquals("a_test_message", configData.getMessages().get(0).getMessage());
		assertEquals(ProductConfigMessageUISeverity.CONFIG, configData.getMessages().get(0).getSeverity());
	}

	@Test
	public void testMapMessagesFromModelToDataForCstic()
	{
		final CsticModel csticModel = new CsticModelImpl();
		final ProductConfigMessage message = new ProductConfigMessageImpl("a_test_message", "messagekey123",
				ProductConfigMessageSeverity.INFO, ProductConfigMessageSource.ENGINE, ProductConfigMessageSourceSubType.DEFAULT);
		csticModel.getMessages().add(message);

		final CsticData csticData = new CsticData();
		classUnderTest.mapMessagesFromModelToData(csticData, csticModel);

		assertEquals(1, csticData.getMessages().size());
		assertEquals("a_test_message", csticData.getMessages().get(0).getMessage());
		assertEquals(ProductConfigMessageUISeverity.CONFIG, csticData.getMessages().get(0).getSeverity());
	}

	@Test
	public void testMapMessagesFromModelToData_Warning()
	{
		final ProductConfigMessage message = new ProductConfigMessageImpl("a_test_message", "messagekey123",
				ProductConfigMessageSeverity.WARNING, ProductConfigMessageSource.ENGINE, ProductConfigMessageSourceSubType.DEFAULT);
		configModel.getMessages().add(message);

		classUnderTest.mapMessagesFromModelToData(configData, configModel);

		assertEquals(1, configData.getMessages().size());
		assertEquals("a_test_message", configData.getMessages().get(0).getMessage());
		assertEquals(ProductConfigMessageUISeverity.INFO, configData.getMessages().get(0).getSeverity());
	}

	@Test
	public void testMapMessagesFromModelToData_Error()
	{
		final ProductConfigMessage message = new ProductConfigMessageImpl("a_test_message", "messagekey123",
				ProductConfigMessageSeverity.ERROR, ProductConfigMessageSource.ENGINE, ProductConfigMessageSourceSubType.DEFAULT);
		configModel.getMessages().add(message);

		classUnderTest.mapMessagesFromModelToData(configData, configModel);

		assertEquals(1, configData.getMessages().size());
		assertEquals("a_test_message", configData.getMessages().get(0).getMessage());
		assertEquals(ProductConfigMessageUISeverity.ERROR, configData.getMessages().get(0).getSeverity());
	}


	@Test
	public void testMapMessagesFromModelToData_Endcode()
	{
		final ProductConfigMessage message = new ProductConfigMessageImpl("><img src=x onerror=alert(1)>", "messagekey123",
				ProductConfigMessageSeverity.ERROR, ProductConfigMessageSource.ENGINE, ProductConfigMessageSourceSubType.DEFAULT);
		configModel.getMessages().add(message);

		classUnderTest.mapMessagesFromModelToData(configData, configModel);

		assertEquals(1, configData.getMessages().size());
		assertEquals("&gt;&lt;img&#x20;src&#x3d;x&#x20;onerror&#x3d;alert&#x28;1&#x29;&gt;",
				configData.getMessages().get(0).getMessage());
		assertEquals(ProductConfigMessageUISeverity.ERROR, configData.getMessages().get(0).getSeverity());
	}


	@Test
	public void testMapMessagesFromModelToData_EndcodeErr() throws UnsupportedEncodingException
	{
		final ProductConfigMessage message = new ProductConfigMessageImpl("\uffff", "messagekey123",
				ProductConfigMessageSeverity.ERROR, ProductConfigMessageSource.ENGINE, ProductConfigMessageSourceSubType.DEFAULT);
		configModel.getMessages().add(message);

		classUnderTest = spy(classUnderTest);
		willThrow(UnsupportedEncodingException.class).given(classUnderTest).encodeHTML("\uffff");

		classUnderTest.mapMessagesFromModelToData(configData, configModel);

		assertEquals(1, configData.getMessages().size());
		assertNull(configData.getMessages().get(0).getMessage());
		assertEquals(ProductConfigMessageUISeverity.ERROR, configData.getMessages().get(0).getSeverity());
	}

	@Test
	public void testPopulateConfigDataFromModelCompleteConsistent()
	{
		configModel.setComplete(true);
		configModel.setConsistent(true);
		classUnderTest.populateConfigDataFromModel(configData, configModel);
		assertTrue(configData.isComplete());
		assertTrue(configData.isConsistent());
	}

	@Test
	public void testPopulateConfigDataFromModelPricingAndAnalyticsMode()
	{
		configModel.setSingleLevel(true);
		callPopulateAndCheckPricingMode();
		assertTrue(configData.isAnalyticsEnabled());
	}

	@Test
	public void testPopulateConfigDataFromModelPricingAndAnalyticsModeMultiLevel()
	{
		configModel.setSingleLevel(false);
		callPopulateAndCheckPricingMode();
		assertFalse(configData.isAnalyticsEnabled());
	}


	@Test
	public void testPopulateConfigDataFromModelNotCompleteConsistent()
	{
		classUnderTest.populateConfigDataFromModel(configData, configModel);
		assertFalse(configData.isComplete());
		assertFalse(configData.isConsistent());
	}

	@Test
	public void testPopulateConfigDataFromModelEmptyGroupLists()
	{
		classUnderTest.populateConfigDataFromModel(configData, configModel);
		final List<UiGroupData> groups = configData.getGroups();
		assertNotNull(groups);
		assertEquals(0, groups.size());
		final List<UiGroupData> groupsFlat = configData.getCsticGroupsFlat();
		assertNotNull(groupsFlat);
		assertEquals(0, groupsFlat.size());

	}

	@Test
	public void testGetListOfCsticDataInconsistent()
	{
		final CsticModel invisibleCstic = createInvisibleCsticAndPrepareMapper();
		invisibleCstic.setConsistent(false);
		final List<CsticModel> csticModelList = new ArrayList<>();
		csticModelList.add(invisibleCstic);
		final List<CsticData> listOfCsticData = classUnderTest.getListOfCsticData(csticModelList, null, null);
		assertNotNull(listOfCsticData);
		assertEquals(1, listOfCsticData.size());
	}

	@Test
	public void testGetListOfCsticDataConsistent()
	{
		final CsticModel invisibleCstic = createInvisibleCsticAndPrepareMapper();
		invisibleCstic.setConsistent(true);
		final List<CsticModel> csticModelList = new ArrayList<>();
		csticModelList.add(invisibleCstic);
		final List<CsticData> listOfCsticData = classUnderTest.getListOfCsticData(csticModelList, null, null);
		assertNotNull(listOfCsticData);
		assertEquals(0, listOfCsticData.size());
	}


	protected CsticModel createInvisibleCsticAndPrepareMapper()
	{
		classUnderTest.setCsticTypeMapper(csticTypeMapper);
		final CsticData value = new CsticData();
		Mockito.when(csticTypeMapper.mapCsticModelToData(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(value);
		final CsticModel invisibleCstic = new CsticModelImpl();
		invisibleCstic.setVisible(false);
		return invisibleCstic;
	}
}
