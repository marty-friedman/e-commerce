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
package de.hybris.platform.sap.productconfig.runtime.cps.pricing.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.CharonPricingFacade;
import de.hybris.platform.sap.productconfig.runtime.cps.cache.CPSSessionCache;
import de.hybris.platform.sap.productconfig.runtime.cps.constants.SapproductconfigruntimecpsConstants;
import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.ConfigurationMasterDataService;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataCharacteristicContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataClassContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataProductContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.pricing.CPSMasterDataVariantPriceKey;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.ConditionPurpose;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingDocumentInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingDocumentResult;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCharacteristicGroup;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSItem;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.pricing.CPSValuePrice;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.pricing.CPSValuePriceInfo;
import de.hybris.platform.sap.productconfig.runtime.cps.pricing.PricingConfigurationParameterCPS;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigModelFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.CsticQualifier;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigModelFactoryImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceSummaryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceValueUpdateModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.PriceModelImpl;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.context.ApplicationContext;


@SuppressWarnings("javadoc")
@UnitTest
public class PricingHandlerImplTest
{
	private static final String VALUE_10 = "VALUE_10";
	private static final String VALUE_12 = "VALUE_12";
	private static final String VALUE_14 = "VALUE_14";
	private static final String CSTIC_07 = "cstic_07";
	private static final String CSTIC_06 = "cstic_06";
	private static final String CSTIC_01 = "cstic_01";
	private static final String KB_ID = "kbId";
	private static final String TARGET_FOR_SELECTED_OPTIONS = "TARGET_FOR_SELECTED_OPTIONS";
	private static final String TARGET_FOR_BASE_PRICE = "TARGET_FOR_BASE_PRICE";
	private static final String ID = "id";
	private static final String PRODUCT_ID = "productId";
	private static final String CURRENCY = "EUR";
	private static final Double NET_VALUE = Double.valueOf(123.45);
	private static final Double SELECTED_OPTIONS_PRICE = Double.valueOf(23.45);
	private static final Double BASE_PRICE = Double.valueOf(67.89);
	private static final BigDecimal valuePrice = BigDecimal.ONE;
	private static final String CLASS_ID = "Class";
	private static final String PRICE_KEY_NOT_KNOWN = "CAM_XXX";

	Map<CPSMasterDataVariantPriceKey, CPSValuePrice> pricesMap;
	private PricingHandlerImpl classUnderTest;
	private PricingDocumentResult pricingResult;
	@Mock
	private PricingConfigurationParameterCPS pricingConfigurationParameter;
	@Mock
	private Converter<CPSMasterDataKnowledgeBaseContainer, PricingDocumentInput> pricingDocumentInputKBConverter;
	@Mock
	private Converter<CPSConfiguration, PricingDocumentInput> pricingDocumentInputConverter;
	@Mock
	private CharonPricingFacade charonPricingFacade;
	@Mock
	private ConfigurationMasterDataService masterDataService;
	@Mock
	private ConfigModelFactory configModelFactory;
	@Mock
	private CPSSessionCache sessionCache;
	private PriceValueUpdateModel cstic;
	@Mock
	private CPSCharacteristicGroup group;
	@Mock
	private CPSItem item;
	@Mock
	private CPSConfiguration config;
	@Mock
	ApplicationContext mockApplicationContext;
	@Mock
	CPSMasterDataCharacteristicContainer masterDataCharacteristic;

	Answer<PriceModel> priceModelAnswer = new Answer<PriceModel>()
	{
		public PriceModel answer(final InvocationOnMock invocation) throws Throwable
		{
			return new PriceModelImpl();
		}
	};

	Answer<PriceSummaryModel> priceSummaryModelAnswer = new Answer<PriceSummaryModel>()
	{
		public PriceSummaryModel answer(final InvocationOnMock invocation) throws Throwable
		{
			return new PriceSummaryModel();
		}
	};
	final private CPSMasterDataKnowledgeBaseContainer masterDataContainer = new CPSMasterDataKnowledgeBaseContainer();
	private final Map<String, CPSMasterDataProductContainer> productMap = new HashMap<>();
	private final Map<String, CPSMasterDataClassContainer> classesMap = new HashMap<>();


	@Before
	public void setup() throws PricingEngineException
	{
		MockitoAnnotations.initMocks(this);
		pricesMap = new HashMap<>();
		Mockito.when(pricingConfigurationParameter.getTargetForSelectedOptions()).thenReturn(TARGET_FOR_SELECTED_OPTIONS);
		Mockito.when(pricingConfigurationParameter.getTargetForBasePrice()).thenReturn(TARGET_FOR_BASE_PRICE);
		final PricingDocumentInput pricingDocumentInput = new PricingDocumentInput();
		Mockito.when(pricingDocumentInputKBConverter.convert(Mockito.any())).thenReturn(pricingDocumentInput);
		pricingResult = new PricingDocumentResult();
		Mockito.when(charonPricingFacade.createPricingDocument(pricingDocumentInput)).thenReturn(pricingResult);
		Mockito.when(masterDataService.getMasterData(KB_ID)).thenReturn(masterDataContainer);
		masterDataContainer.setProducts(productMap);
		productMap.put(PRODUCT_ID, new CPSMasterDataProductContainer());
		masterDataContainer.setClasses(classesMap);
		classesMap.put(CLASS_ID, new CPSMasterDataClassContainer());
		Mockito.when(masterDataService.getCharacteristic(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(masterDataCharacteristic);
		final Set<String> possibleValues = fillPossibleValues();
		Mockito.when(masterDataService.getSpecificPossibleValueIds(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString())).thenReturn(possibleValues);
		Mockito.when(masterDataService.getPossibleValueIds(Mockito.anyString(), Mockito.anyString())).thenReturn(possibleValues);
		classUnderTest = new PricingHandlerImpl();
		classUnderTest.setPricingConfigurationParameter(pricingConfigurationParameter);
		classUnderTest.setPricingDocumentInputKBConverter(pricingDocumentInputKBConverter);
		classUnderTest.setPricingDocumentInputConverter(pricingDocumentInputConverter);
		classUnderTest.setCharonPricingFacade(charonPricingFacade);
		classUnderTest.setMasterDataService(masterDataService);
		classUnderTest.setConfigModelFactory(configModelFactory);
		Mockito.when(configModelFactory.createInstanceOfPriceModel()).thenAnswer(priceModelAnswer);
		Mockito.when(configModelFactory.getZeroPriceModel()).thenReturn(PriceModel.NO_PRICE);
		Mockito.when(configModelFactory.createInstanceOfPriceSummaryModel()).thenAnswer(priceSummaryModelAnswer);
		classUnderTest.setSessionCache(sessionCache);
		cstic = new PriceValueUpdateModel();
		cstic.setCsticQualifier(new CsticQualifier());
		cstic.getCsticQualifier().setCsticName("cstic id");
		cstic.setSelectedValues(new ArrayList<>());
	}

	@Test
	public void testStorePricingResult()
	{
		final PricingDocumentResult pricingResult = new PricingDocumentResult();
		classUnderTest.storePricingResult(ID, pricingResult);
		Mockito.verify(sessionCache).setPricingDocumentResult(ID, pricingResult);
	}

	@Test
	public void testStorePricingInput()
	{
		final PricingDocumentInput pricingInput = new PricingDocumentInput();
		classUnderTest.storePricingInput(ID, pricingInput);
		Mockito.verify(sessionCache).setPricingDocumentInput(ID, pricingInput);
	}

	@Test
	public void testRetrievePricingResult()
	{
		fillCache();
		final PricingDocumentResult result = classUnderTest.retrievePricingDocument(ID);
		assertNotNull(result);
		assertEquals(pricingResult, result);
	}

	@Test
	public void testGetCurrentTotalPrice()
	{
		fillCache();
		final PriceModel total = classUnderTest.getCurrentTotalPrice(ID);
		assertEquals(CURRENCY, total.getCurrency());
		assertEquals(BigDecimal.valueOf(NET_VALUE.doubleValue()), total.getPriceValue());

	}

	@Test
	public void testGetCurrentTotalPrice_NoPriceDocument()
	{
		final PriceModel total = classUnderTest.getCurrentTotalPrice(ID);
		assertEquals(PriceModel.NO_PRICE, total);
	}

	private void fillCache()
	{
		pricingResult.setNetValue(NET_VALUE);
		pricingResult.setDocumentCurrencyUnit(CURRENCY);
		pricingResult.setConditionsWithPurpose(new ArrayList<>());
		final ConditionPurpose condPurposeBase = new ConditionPurpose();
		condPurposeBase.setPurpose(pricingConfigurationParameter.getTargetForBasePrice());
		condPurposeBase.setValue(BASE_PRICE);
		pricingResult.getConditionsWithPurpose().add(condPurposeBase);
		final ConditionPurpose condPurposeOptions = new ConditionPurpose();
		condPurposeOptions.setPurpose(pricingConfigurationParameter.getTargetForSelectedOptions());
		condPurposeOptions.setValue(SELECTED_OPTIONS_PRICE);
		pricingResult.getConditionsWithPurpose().add(condPurposeOptions);
		Mockito.when(sessionCache.getPricingDocumentResult(ID)).thenReturn(pricingResult);
	}

	private void fillPricesCache()
	{
		pricesMap.put(createPriceKey(PRODUCT_ID, "CAM500"), createPriceModel(100));
		pricesMap.put(createPriceKey(PRODUCT_ID, "CAM200"), createPriceModel(50));
		pricesMap.put(createPriceKey(PRODUCT_ID, "CAM700"), createPriceModel(700));
		Mockito.when(sessionCache.getValuePricesMap(KB_ID)).thenReturn(pricesMap);
	}

	protected CPSValuePrice createPriceModel(final double valuePrice)
	{
		final CPSValuePrice valuePriceObject = new CPSValuePrice();
		valuePriceObject.setValuePrice(BigDecimal.valueOf(valuePrice));
		valuePriceObject.setCurrency(CURRENCY);
		return valuePriceObject;
	}

	protected CPSMasterDataVariantPriceKey createPriceKey(final String productId, final String pricingKey)
	{
		final CPSMasterDataVariantPriceKey priceKey = new CPSMasterDataVariantPriceKey();
		priceKey.setProductId(productId);
		priceKey.setVariantConditionKey(pricingKey);
		return priceKey;
	}


	@Test
	public void testGetSelectedOptionsPrice()
	{
		fillCache();
		final PriceModel selected = classUnderTest.getSelectedOptionsPrice(ID);
		assertEquals(BigDecimal.valueOf(SELECTED_OPTIONS_PRICE.doubleValue()), selected.getPriceValue());
	}

	@Test
	public void testGetSelectedOptionsPrice_NoResult()
	{
		final PriceModel selected = classUnderTest.getSelectedOptionsPrice(ID);
		assertEquals(PriceModel.NO_PRICE, selected);
	}

	@Test
	public void testGetSelectedOptionsPrice_NoTargetForSelectedOptions()
	{
		fillCache();
		Mockito.when(pricingConfigurationParameter.getTargetForSelectedOptions()).thenReturn(null);
		final PriceModel selected = classUnderTest.getSelectedOptionsPrice(ID);
		assertEquals(PriceModel.NO_PRICE, selected);
	}

	@Test
	public void testGetSelectedOptionsPrice_NoSelectedOptionsPresent()
	{
		fillCache();
		final PricingDocumentResult pricingDocumentResult = classUnderTest.retrievePricingDocument(ID);
		final List<ConditionPurpose> conditionsWithPurpose = pricingDocumentResult.getConditionsWithPurpose();
		conditionsWithPurpose.remove(1);
		Mockito.when(sessionCache.getPricingDocumentResult(ID)).thenReturn(pricingResult);
		final PriceModel selected = classUnderTest.getSelectedOptionsPrice(ID);
		assertEquals(PriceModel.NO_PRICE, selected);
	}

	@Test
	public void testGetBasePrice()
	{
		fillCache();
		final PriceModel base = classUnderTest.getBasePrice(ID);
		assertEquals(BigDecimal.valueOf(BASE_PRICE.doubleValue()), base.getPriceValue());
	}

	@Test
	public void testRetrieveVariantConditions() throws PricingEngineException
	{
		final PricingDocumentResult pricingDocumentResult = classUnderTest.retrieveVariantConditions(KB_ID);
		assertNotNull(pricingDocumentResult);
	}

	@Test
	public void testGetPricesMap() throws PricingEngineException
	{
		fillPricesCache();
		final Map<CPSMasterDataVariantPriceKey, CPSValuePrice> pricesMap = classUnderTest.getPricesMap(KB_ID);
		assertNotNull(pricesMap);
	}

	@Test
	public void testGetValuePrice() throws PricingEngineException
	{
		Mockito.when(masterDataService.getValuePricingKey(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString())).thenReturn("CAM700");
		fillPricesCache();
		final CPSValuePrice price = classUnderTest.getValuePrice(KB_ID, PRODUCT_ID, "characteristicId", "valueId");
		final double valuePrice = 700;
		assertEquals(BigDecimal.valueOf(valuePrice), price.getValuePrice());
	}

	@Test
	public void testGetValuePrice_pricingKeyNull() throws PricingEngineException
	{
		Mockito.when(masterDataService.getValuePricingKey(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString())).thenReturn(null);
		final CPSValuePrice price = classUnderTest.getValuePrice(KB_ID, PRODUCT_ID, "characteristicId", "valueId");
		assertNull(price);
	}


	@Test
	public void testIsValueSelectedWithNullListOfValuesReturnsFalse()
	{
		final List<CsticValueModel> cpsValues = null;
		final String value = CSTIC_01;
		assertFalse(classUnderTest.isValueSelected(value, cpsValues));
	}

	@Test
	public void testIsValueSelectedWithEmptyListOfValuesReturnsFalse()
	{
		final List<CsticValueModel> cpsValues = new ArrayList<>();
		final String value = CSTIC_01;
		assertFalse(classUnderTest.isValueSelected(value, cpsValues));
	}

	@Test
	public void testIsValueSelectedWithListOfValuesReturnsFalse()
	{
		final List<CsticValueModel> cpsValues = new ArrayList<>();
		cpsValues.add(createCsticValueModel("cstic_04"));

		final String value = CSTIC_01;
		assertFalse(classUnderTest.isValueSelected(value, cpsValues));
	}

	@Test
	public void testIsValueSelectedWithListOfNullValueReturnsFalse()
	{
		final List<CsticValueModel> cpsValues = new ArrayList<>();
		cpsValues.add(createCsticValueModel(null));

		final String value = CSTIC_01;
		assertFalse(classUnderTest.isValueSelected(value, cpsValues));
	}

	private CsticValueModel createCsticValueModel(final String csticValueId)
	{
		final CsticValueModel csticValue = new CsticValueModelImpl();
		csticValue.setName(csticValueId);
		return csticValue;
	}

	@Test
	public void testIsValueSelectedWithListOfWrongValuesReturnsFalse()
	{
		final List<CsticValueModel> cpsValues = new ArrayList<>();
		cpsValues.add(createCsticValueModel("cstic_02"));
		cpsValues.add(createCsticValueModel("cstic_04"));
		cpsValues.add(createCsticValueModel(CSTIC_06));

		final String value = CSTIC_01;
		assertFalse(classUnderTest.isValueSelected(value, cpsValues));
	}

	@Test
	public void testIsValueSelectedWithListOfValuesReturnsTrue()
	{
		final List<CsticValueModel> cpsValues = new ArrayList<>();
		cpsValues.add(createCsticValueModel(CSTIC_01));
		cpsValues.add(createCsticValueModel("cstic_03"));
		cpsValues.add(createCsticValueModel("cstic_04"));
		cpsValues.add(createCsticValueModel(CSTIC_06));

		final String value = "cstic_04";
		assertTrue(classUnderTest.isValueSelected(value, cpsValues));
	}

	@Test
	public void testHandleSelectedValuePriceForSingleValueReturnNull()
	{
		final boolean isMultiValued = false;
		final String possibleValue = CSTIC_01;
		final BigDecimal valuePrice = BigDecimal.valueOf(300);
		final List<String> cpsValues = new ArrayList<>();
		final BigDecimal oldSelectedValuePrice = null;

		final BigDecimal price = classUnderTest.updateSelectedValuePrice(isMultiValued, possibleValue, valuePrice, cpsValues,
				oldSelectedValuePrice);
		assertNull(price);
	}

	@Test
	public void testHandleSelectedValuePriceForSingleValueReturnPrice()
	{
		final boolean isMultiValued = false;
		final String possibleValue = CSTIC_01;
		final BigDecimal valuePrice = BigDecimal.valueOf(300);
		final List<String> cpsValues = new ArrayList<>();
		final BigDecimal oldSelectedValuePrice = null;
		cpsValues.add(CSTIC_01);

		final BigDecimal price = classUnderTest.updateSelectedValuePrice(isMultiValued, possibleValue, valuePrice, cpsValues,
				oldSelectedValuePrice);
		assertNotNull(price);
		assertEquals(valuePrice, price);
	}

	@Test
	public void testHandleSelectedValuePriceForMultiValueReturnNull()
	{
		final boolean isMultiValued = true;
		final String possibleValue = CSTIC_01;
		final BigDecimal valuePrice = BigDecimal.valueOf(300);
		final List<String> cpsValues = new ArrayList<>();
		final BigDecimal oldSelectedValuePrice = null;
		cpsValues.add(CSTIC_01);

		final BigDecimal price = classUnderTest.updateSelectedValuePrice(isMultiValued, possibleValue, valuePrice, cpsValues,
				oldSelectedValuePrice);
		assertNull(price);
	}


	@Test
	public void testHandleSelectedValuePriceFor_SingleValue_selectedValuePriceAlreadySet()
	{
		final boolean isMultiValued = false;
		final String possibleValue = CSTIC_01;
		final BigDecimal valuePrice = BigDecimal.valueOf(300);
		final List<String> cpsValues = new ArrayList<>();
		final BigDecimal oldSelectedValuePrice = BigDecimal.valueOf(400);
		cpsValues.add(CSTIC_01);

		final BigDecimal price = classUnderTest.updateSelectedValuePrice(isMultiValued, possibleValue, valuePrice, cpsValues,
				oldSelectedValuePrice);
		assertNotNull(price);
		assertEquals(oldSelectedValuePrice, price);

	}


	@Test
	public void testCalculateDeltaPriceWithSelectedValuePriceSet_300()
	{
		final BigDecimal selectedValuePrice = BigDecimal.valueOf(300);

		final Map<String, CPSValuePriceInfo> mapValuePriceInfo = new HashMap<>();
		mapValuePriceInfo.put(CSTIC_01, createValuePriceInfo(BigDecimal.valueOf(300)));
		mapValuePriceInfo.put(CSTIC_06, createValuePriceInfo(null));
		mapValuePriceInfo.put(CSTIC_07, createValuePriceInfo(null));

		classUnderTest.calculateDeltaPrices(selectedValuePrice, mapValuePriceInfo);
		assertEquals(null, mapValuePriceInfo.get(CSTIC_07).getDeltaPrice());
	}

	@Test
	public void testCalculateDeltaPriceWithSelectedValuePriceSet_100()
	{
		final BigDecimal selectedValuePrice = BigDecimal.valueOf(100);

		final Map<String, CPSValuePriceInfo> mapValuePriceInfo = new HashMap<>();
		mapValuePriceInfo.put(CSTIC_01, createValuePriceInfo(null));
		mapValuePriceInfo.put(CSTIC_06, createValuePriceInfo(null));
		mapValuePriceInfo.put(CSTIC_07, createValuePriceInfo(BigDecimal.valueOf(400)));

		classUnderTest.calculateDeltaPrices(selectedValuePrice, mapValuePriceInfo);
		assertEquals(BigDecimal.valueOf(300), mapValuePriceInfo.get(CSTIC_07).getDeltaPrice().getValuePrice());
	}

	protected Set<String> fillPossibleValues()
	{
		final Set<String> possibleValues = new HashSet<>();
		possibleValues.add(CSTIC_01);
		possibleValues.add(CSTIC_06);
		possibleValues.add(CSTIC_07);
		return possibleValues;
	}

	@Test
	public void testCalculateDeltaPriceWithSelectedValuePriceNull()
	{
		final BigDecimal selectedValuePrice = null;

		final Map<String, CPSValuePriceInfo> mapValuePriceInfo = new HashMap<>();
		mapValuePriceInfo.put(CSTIC_01, createValuePriceInfo(null));
		mapValuePriceInfo.put(CSTIC_06, createValuePriceInfo(null));
		mapValuePriceInfo.put(CSTIC_07, createValuePriceInfo(BigDecimal.valueOf(400)));

		classUnderTest.calculateDeltaPrices(selectedValuePrice, mapValuePriceInfo);
		assertEquals(BigDecimal.valueOf(400), mapValuePriceInfo.get(CSTIC_07).getDeltaPrice().getValuePrice());
	}

	@Test
	public void testCalculateDeltaPriceWith_valuePriceNull()
	{
		final BigDecimal selectedValuePrice = null;

		final Map<String, CPSValuePriceInfo> mapValuePriceInfo = new HashMap<>();
		final CPSValuePriceInfo priceInfo = createValuePriceInfo(null);
		priceInfo.setValuePrice(null);
		mapValuePriceInfo.put(CSTIC_01, priceInfo);
		mapValuePriceInfo.put(CSTIC_06, createValuePriceInfo(null));
		mapValuePriceInfo.put(CSTIC_07, createValuePriceInfo(BigDecimal.valueOf(400)));

		classUnderTest.calculateDeltaPrices(selectedValuePrice, mapValuePriceInfo);
		assertEquals(BigDecimal.valueOf(400), mapValuePriceInfo.get(CSTIC_07).getDeltaPrice().getValuePrice());
	}


	protected CPSValuePriceInfo createValuePriceInfo(final BigDecimal price)
	{
		final CPSValuePriceInfo valuePriceInfo = new CPSValuePriceInfo();
		final CPSValuePrice valuePrice = new CPSValuePrice();
		valuePrice.setValuePrice(price);
		valuePrice.setCurrency(CURRENCY);
		valuePriceInfo.setValuePrice(valuePrice);
		return valuePriceInfo;
	}

	protected CPSValuePriceInfo createValuePriceInfoWithDelta(final BigDecimal price, final BigDecimal delta)
	{
		final CPSValuePriceInfo valuePriceInfo = new CPSValuePriceInfo();
		final CPSValuePrice valuePrice = new CPSValuePrice();
		valuePrice.setValuePrice(price);
		valuePrice.setCurrency(CURRENCY);
		valuePriceInfo.setValuePrice(valuePrice);

		final CPSValuePrice deltaPrice = new CPSValuePrice();
		deltaPrice.setValuePrice(delta);
		deltaPrice.setCurrency(CURRENCY);
		valuePriceInfo.setDeltaPrice(deltaPrice);

		return valuePriceInfo;
	}

	protected CsticValueModel createPossibleValue(final String possibleValueId)
	{
		final CsticValueModel possibleValue = new CsticValueModelImpl();
		possibleValue.setName(possibleValueId);
		return possibleValue;
	}

	@Test
	public void testCreatePriceModelFromCPSValue()
	{
		final CPSValuePrice input = new CPSValuePrice();
		input.setValuePrice(valuePrice);
		input.setCurrency(CURRENCY);
		final PriceModel priceModel = classUnderTest.createPriceModelFromCPSValue(input);
		assertNotNull(priceModel);
		assertEquals(valuePrice, priceModel.getPriceValue());
	}

	@Test
	public void testCreatePriceModelNull()
	{
		final PriceModel priceModel = classUnderTest.createPriceModelFromCPSValue(null);
		assertNotNull(priceModel);
		assertEquals(PriceModel.NO_PRICE, priceModel);
	}

	@Test
	public void testGetSelectedValuePriceAndValuePricesMap() throws PricingEngineException
	{
		Mockito.when(masterDataService.getValuePricingKey(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString())).thenReturn("CAM700");
		fillPricesCache();
		Mockito.when(Boolean.valueOf(masterDataCharacteristic.isMultiValued())).thenReturn(Boolean.FALSE);
		cstic.getCsticQualifier().setInstanceName(PRODUCT_ID);
		final Pair<BigDecimal, Map<String, CPSValuePriceInfo>> pair = classUnderTest.getSelectedValuePriceAndValuePricesMap(KB_ID,
				cstic);
		final Map<String, CPSValuePriceInfo> mapValuePriceInfo = pair.getRight();
		assertNull(pair.getLeft());
		assertEquals(3, mapValuePriceInfo.size());
		assertEquals(BigDecimal.valueOf(700.00), mapValuePriceInfo.get(CSTIC_01).getValuePrice().getValuePrice());
		assertEquals(CURRENCY, mapValuePriceInfo.get(CSTIC_01).getValuePrice().getCurrency());
	}

	@Test
	public void testGetSelectedValuePriceAndValuePricesMapKeyNotKnown() throws PricingEngineException
	{
		Mockito.when(masterDataService.getValuePricingKey(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString())).thenReturn(PRICE_KEY_NOT_KNOWN);
		fillPricesCache();
		Mockito.when(Boolean.valueOf(masterDataCharacteristic.isMultiValued())).thenReturn(Boolean.FALSE);
		cstic.getCsticQualifier().setInstanceName(PRODUCT_ID);
		final Pair<BigDecimal, Map<String, CPSValuePriceInfo>> pair = classUnderTest.getSelectedValuePriceAndValuePricesMap(KB_ID,
				cstic);
		final Map<String, CPSValuePriceInfo> mapValuePriceInfo = pair.getRight();
		assertNull(pair.getLeft());
		assertEquals(0, mapValuePriceInfo.size());
	}

	@Test
	public void testGetSelectedValuePriceAndValuePricesMap_onlySomeValuesHavePrices() throws PricingEngineException
	{

		Mockito.when(Boolean.valueOf(masterDataCharacteristic.isMultiValued())).thenReturn(Boolean.FALSE);
		Mockito.when(masterDataService.getSpecificPossibleValueIds(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString())).thenReturn(new HashSet<String>(Arrays.asList(CSTIC_01)));
		Mockito.when(masterDataService.getValuePricingKey(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.eq(CSTIC_01))).thenReturn("CAM700");
		fillPricesCache();
		cstic.getCsticQualifier().setInstanceName(PRODUCT_ID);
		final Pair<BigDecimal, Map<String, CPSValuePriceInfo>> pair = classUnderTest.getSelectedValuePriceAndValuePricesMap(KB_ID,
				cstic);
		final Map<String, CPSValuePriceInfo> mapValuePriceInfo = pair.getRight();
		assertNull(pair.getLeft());
		assertEquals(3, mapValuePriceInfo.size());
		assertEquals(BigDecimal.valueOf(700.00), mapValuePriceInfo.get(CSTIC_01).getValuePrice().getValuePrice());
		assertEquals(CURRENCY, mapValuePriceInfo.get(CSTIC_01).getValuePrice().getCurrency());
		assertEquals(BigDecimal.ZERO, mapValuePriceInfo.get(CSTIC_06).getValuePrice().getValuePrice());
		assertEquals(CURRENCY, mapValuePriceInfo.get(CSTIC_06).getValuePrice().getCurrency());
		assertEquals(BigDecimal.ZERO, mapValuePriceInfo.get(CSTIC_07).getValuePrice().getValuePrice());
		assertEquals(CURRENCY, mapValuePriceInfo.get(CSTIC_07).getValuePrice().getCurrency());
	}

	@Test
	public void testGetSelectedValuePriceAndValuePricesMap_NoValuePrices() throws PricingEngineException
	{

		Mockito.when(masterDataService.getSpecificPossibleValueIds(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString())).thenReturn(Collections.emptySet());
		Mockito.when(Boolean.valueOf(masterDataCharacteristic.isMultiValued())).thenReturn(Boolean.FALSE);
		cstic.getCsticQualifier().setInstanceName(PRODUCT_ID);
		final Pair<BigDecimal, Map<String, CPSValuePriceInfo>> pair = classUnderTest.getSelectedValuePriceAndValuePricesMap(KB_ID,
				cstic);
		final Map<String, CPSValuePriceInfo> mapValuePriceInfo = pair.getRight();
		assertNull(pair.getLeft());
		assertEquals(0, mapValuePriceInfo.size());
	}

	public void testGetSelectedValuePriceAndValuePricesMap_checkMap() throws PricingEngineException
	{
		Mockito.when(masterDataService.getValuePricingKey(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString())).thenReturn("CAM700");
		fillPricesCache();
		Mockito.when(Boolean.valueOf(masterDataCharacteristic.isMultiValued())).thenReturn(Boolean.FALSE);
		final Pair<BigDecimal, Map<String, CPSValuePriceInfo>> pair = classUnderTest.getSelectedValuePriceAndValuePricesMap(KB_ID,
				cstic);
		final Map<String, CPSValuePriceInfo> mapValuePriceInfo = pair.getRight();
		assertEquals(3, mapValuePriceInfo.size());
		assertEquals(BigDecimal.valueOf(700.00), mapValuePriceInfo.get(CSTIC_07).getValuePrice().getValuePrice());
		assertEquals(BigDecimal.valueOf(700.00), mapValuePriceInfo.get(CSTIC_07).getDeltaPrice().getValuePrice());
	}

	@Test
	public void testGetConfigModelFactoryContainsBeanFalse()
	{
		final PricingHandlerImpl pricingHandler = Mockito.spy(new PricingHandlerImpl());
		Mockito.doReturn(mockApplicationContext).when(pricingHandler).getApplicationContext();
		Mockito.doReturn(Boolean.FALSE).when(mockApplicationContext).containsBean("sapProductConfigModelFactory");

		final ConfigModelFactory configModelFactory = pricingHandler.getConfigModelFactory();
		assertNotNull(configModelFactory);
		assertTrue(configModelFactory instanceof ConfigModelFactoryImpl);
	}

	@Test
	public void testGetConfigModelFactoryContainsBeanTrue()
	{
		final ConfigModelFactoryImpl factory = new ConfigModelFactoryImpl();
		final PricingHandlerImpl pricingHandler = Mockito.spy(new PricingHandlerImpl());
		Mockito.doReturn(mockApplicationContext).when(pricingHandler).getApplicationContext();
		Mockito.when(mockApplicationContext.getBean("sapProductConfigModelFactory")).thenReturn(factory);
		Mockito.doReturn(Boolean.TRUE).when(mockApplicationContext).containsBean("sapProductConfigModelFactory");

		final ConfigModelFactory configModelFactory = pricingHandler.getConfigModelFactory();
		assertNotNull(configModelFactory);
		assertTrue(configModelFactory instanceof ConfigModelFactoryImpl);
	}

	@Test
	public void testGetConfigModelFactoryNotNull()
	{
		final PricingHandlerImpl pricingHandler = Mockito.spy(new PricingHandlerImpl());
		pricingHandler.setConfigModelFactory(new ConfigModelFactoryImpl());
		final ConfigModelFactory configModelFactory = pricingHandler.getConfigModelFactory();
		assertNotNull(configModelFactory);
		assertTrue(configModelFactory instanceof ConfigModelFactoryImpl);
	}

	@Test
	public void testCreatePricingDocument() throws PricingEngineException
	{
		classUnderTest.preparePricingDocumentInput(config);
		Mockito.verify(pricingDocumentInputConverter).convert(config);
		Mockito.verify(sessionCache).setPricingDocumentInput(Mockito.anyString(), Mockito.any());
		Mockito.verify(charonPricingFacade, Mockito.times(0)).createPricingDocument(Mockito.any());
	}

	@Test(expected = IllegalStateException.class)
	public void testUpdatePricingDocumentException() throws PricingEngineException
	{
		Mockito.when(sessionCache.getPricingDocumentInput(ID)).thenReturn(null);
		classUnderTest.updatePricingDocument(ID);
	}

	@Test
	public void testUpdatePricingDocument() throws PricingEngineException
	{
		Mockito.when(sessionCache.getPricingDocumentInput(ID)).thenReturn(new PricingDocumentInput());
		classUnderTest.updatePricingDocument(ID);
		Mockito.verify(charonPricingFacade).createPricingDocument(Mockito.any());
		Mockito.verify(sessionCache).setPricingDocumentResult(Mockito.anyString(), Mockito.any());
	}

	@Test
	public void testGetPriceSummary() throws PricingEngineException
	{
		Mockito.when(sessionCache.getPricingDocumentInput(ID)).thenReturn(new PricingDocumentInput());
		final PriceSummaryModel result = classUnderTest.getPriceSummary(ID);
		assertNotNull(result);
		Mockito.verify(sessionCache).setPricingDocumentResult(Mockito.anyString(), Mockito.any());
		Mockito.verify(charonPricingFacade).createPricingDocument(Mockito.any());

	}

	@Test
	public void testCreatePriceModel()
	{
		final PriceModel result = classUnderTest.createPriceModel(CURRENCY, NET_VALUE);
		assertNotNull(result);
		assertEquals(CURRENCY, result.getCurrency());
		assertEquals(BigDecimal.valueOf(NET_VALUE.doubleValue()), result.getPriceValue());

	}

	@Test
	public void testFillValuePrices() throws PricingEngineException
	{
		cstic.getCsticQualifier().setInstanceName(PRODUCT_ID);
		Mockito.when(masterDataService.getValuePricingKey(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString())).thenReturn("CAM700");
		fillPricesCache();
		cstic.setSelectedValues(new ArrayList<>());
		cstic.getSelectedValues().add(CSTIC_01);
		classUnderTest.fillValuePrices(KB_ID, cstic);
		assertNotNull(cstic.getValuePrices());
		assertEquals(3, cstic.getValuePrices().size());
		for (final PriceModel deltaPrice : cstic.getValuePrices().values())
		{
			assertNotNull(deltaPrice);
		}
	}


	@Test
	public void testFillValueAndDeltaPricesCsticNull() throws PricingEngineException
	{
		classUnderTest.fillValuePrices(KB_ID, (PriceValueUpdateModel) null);
		//Test: No exception happens
	}

	@Test
	public void testGetValuePrices() throws PricingEngineException
	{
		cstic.getCsticQualifier().setInstanceName(PRODUCT_ID);
		Mockito.when(masterDataService.getValuePricingKey(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString())).thenReturn("CAM700");
		fillPricesCache();
		final Map<String, CPSValuePriceInfo> result = classUnderTest.getValuePrices(KB_ID, cstic);
		assertNotNull(result);
	}

	@Test
	public void testFillPriceInfos_ShowDelta()
	{
		final Map<String, CPSValuePriceInfo> mapValuePriceInfo = new HashMap<>();
		mapValuePriceInfo.put(VALUE_10, createValuePriceInfoWithDelta(BigDecimal.valueOf(400), BigDecimal.valueOf(300)));
		mapValuePriceInfo.put(VALUE_12, createValuePriceInfoWithDelta(BigDecimal.valueOf(600), BigDecimal.valueOf(500)));

		final PriceValueUpdateModel updateModel = new PriceValueUpdateModel();
		classUnderTest.fillPriceInfos(mapValuePriceInfo, updateModel, true);
		assertNotNull(updateModel.getValuePrices());
		assertEquals(BigDecimal.valueOf(300), updateModel.getValuePrices().get(VALUE_10).getPriceValue());
		assertEquals(BigDecimal.valueOf(500), updateModel.getValuePrices().get(VALUE_12).getPriceValue());
	}

	@Test
	public void testFillPriceInfos_ShowAbsolute()
	{
		final Map<String, CPSValuePriceInfo> mapValuePriceInfo = new HashMap<>();
		mapValuePriceInfo.put(VALUE_10, createValuePriceInfoWithDelta(BigDecimal.valueOf(400), BigDecimal.valueOf(300)));
		mapValuePriceInfo.put(VALUE_12, createValuePriceInfoWithDelta(BigDecimal.valueOf(600), BigDecimal.valueOf(500)));

		final PriceValueUpdateModel updateModel = new PriceValueUpdateModel();
		classUnderTest.fillPriceInfos(mapValuePriceInfo, updateModel, false);
		assertNotNull(updateModel.getValuePrices());
		assertEquals(BigDecimal.valueOf(400), updateModel.getValuePrices().get(VALUE_10).getPriceValue());
		assertEquals(BigDecimal.valueOf(600), updateModel.getValuePrices().get(VALUE_12).getPriceValue());
		assertFalse(updateModel.isShowDeltaPrices());
	}

	@Test
	public void testFillPriceInfosPriceNull()
	{
		final Map<String, CPSValuePriceInfo> mapValuePriceInfo = new HashMap<>();
		mapValuePriceInfo.put(VALUE_10, null);

		final PriceValueUpdateModel updateModel = new PriceValueUpdateModel();
		classUnderTest.fillPriceInfos(mapValuePriceInfo, updateModel, true);
		assertNotNull(updateModel.getValuePrices());
		assertEquals(PriceModel.NO_PRICE, updateModel.getValuePrices().get(VALUE_10));
		assertTrue(updateModel.isShowDeltaPrices());
	}

	@Test
	public void testFillPriceInfosNotFound()
	{
		final Map<String, CPSValuePriceInfo> mapValuePriceInfo = new HashMap<>();

		final PriceValueUpdateModel updateModel = new PriceValueUpdateModel();
		classUnderTest.fillPriceInfos(mapValuePriceInfo, updateModel, true);
		assertNotNull(updateModel.getValuePrices());
		assertEquals(null, updateModel.getValuePrices().get(VALUE_10));
		assertTrue(updateModel.isShowDeltaPrices());
	}

	@Test
	public void testCreateUpdateModel()
	{
		final CsticModel csticModel = new CsticModelImpl();
		csticModel.setInstanceName("instance name");
		csticModel.setName("cstic name");
		final PriceValueUpdateModel result = classUnderTest.createUpdateModel(csticModel);
		assertNotNull(result);
		assertNotNull(result.getCsticQualifier());
		assertNotNull(result.getSelectedValues());
		assertNull(result.getValuePrices());
		assertEquals("instance name", result.getCsticQualifier().getInstanceName());
		assertEquals("cstic name", result.getCsticQualifier().getCsticName());
		assertTrue(result.getSelectedValues().isEmpty());
	}

	protected CsticModel createCsticModel(final String cstic, final String value1, final String value2, final String value3)
	{
		final CsticModel cstic1 = new CsticModelImpl();
		final List<CsticValueModel> assignedValues = new ArrayList<>();
		cstic1.setAssignedValuesWithoutCheckForChange(assignedValues);
		assignedValues.add(createCsticValueModel(value1));
		assignedValues.add(createCsticValueModel(value2));
		assignedValues.add(createCsticValueModel(value3));
		cstic1.setName(cstic);
		return cstic1;
	}

	@Test
	public void testFillValuePriceInfos()
	{
		final Map<String, CPSValuePriceInfo> valuePrices = new HashMap<>();
		final BigDecimal fourHundred = BigDecimal.valueOf(400);
		final BigDecimal sixHundred = BigDecimal.valueOf(600);
		valuePrices.put(VALUE_10, createValuePriceInfo(fourHundred));
		valuePrices.put(VALUE_14, createValuePriceInfo(sixHundred));
		final CsticModel csticModel = createCsticModel(CSTIC_01, VALUE_10, VALUE_12, VALUE_14);
		assertNotNull(csticModel.getAssignedValues().get(0));
		classUnderTest.fillValuePriceInfos(valuePrices, csticModel);
		assertEquals(fourHundred, csticModel.getAssignedValues().get(0).getValuePrice().getPriceValue());
		assertEquals(PriceModel.NO_PRICE, csticModel.getAssignedValues().get(1).getValuePrice());
		assertEquals(sixHundred, csticModel.getAssignedValues().get(2).getValuePrice().getPriceValue());
	}

	@Test
	public void testFillValuePriceInfo_ignoreZeroPrices()
	{
		final Map<String, CPSValuePriceInfo> valuePrices = new HashMap<>();
		valuePrices.put(VALUE_10, createValuePriceInfo(BigDecimal.ZERO));
		final CsticModel csticModel = createCsticModel(CSTIC_01, VALUE_10, VALUE_12, VALUE_14);
		assertNotNull(csticModel.getAssignedValues().get(0));
		classUnderTest.fillValuePriceInfos(valuePrices, csticModel);
		assertEquals(PriceModel.NO_PRICE, csticModel.getAssignedValues().get(0).getValuePrice());
	}

	@Test
	public void testAddValueToValuePriceInfoMap() throws PricingEngineException
	{
		Mockito.when(masterDataService.getValuePricingKey(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString())).thenReturn("CAM700");
		fillPricesCache();
		final Map<String, CPSValuePriceInfo> valuePrices = new HashMap<>();
		classUnderTest.addValueToValuePriceInfoMap(KB_ID, valuePrices, PRODUCT_ID, CSTIC_01, "CAM700", null);
		assertEquals(BigDecimal.valueOf(700.0), valuePrices.get("CAM700").getValuePrice().getValuePrice());
		assertEquals(CURRENCY, valuePrices.get("CAM700").getValuePrice().getCurrency());
	}

	@Test
	public void testAddValueToValuePriceInfoMap_zeroPrice() throws PricingEngineException
	{
		final Map<String, CPSValuePriceInfo> valuePrices = new HashMap<>();
		classUnderTest.addValueToValuePriceInfoMap(KB_ID, valuePrices, PRODUCT_ID, CSTIC_01, "CAM700", CURRENCY);
		assertEquals(BigDecimal.ZERO, valuePrices.get("CAM700").getValuePrice().getValuePrice());
		assertEquals(CURRENCY, valuePrices.get("CAM700").getValuePrice().getCurrency());
	}

	@Test
	public void testIsValuePriceZero()
	{
		final CPSValuePriceInfo zeroPrice = createValuePriceInfo(BigDecimal.ZERO);
		assertTrue(classUnderTest.isValuePriceZero(zeroPrice));
		final CPSValuePriceInfo nonZeroPrice = createValuePriceInfo(BigDecimal.valueOf(100.0));
		assertFalse(classUnderTest.isValuePriceZero(nonZeroPrice));
	}

	@Test
	public void testDetermineInstanceType()
	{
		assertEquals(SapproductconfigruntimecpsConstants.ITEM_TYPE_MARA, classUnderTest.determineInstanceType(KB_ID, PRODUCT_ID));
	}

	@Test
	public void testDetermineInstanceTypeClass()
	{
		assertEquals(SapproductconfigruntimecpsConstants.ITEM_TYPE_KLAH, classUnderTest.determineInstanceType(KB_ID, CLASS_ID));
	}

	@Test(expected = IllegalStateException.class)
	public void testDetermineInstanceTypeNotFound()
	{
		classUnderTest.determineInstanceType(KB_ID, "unknown");
	}

	@Test
	public void testDetermineInstanceTypeUnambigous()
	{
		//If we find key in products and classes: We consider it as product!
		productMap.put(CLASS_ID, new CPSMasterDataProductContainer());
		assertEquals(SapproductconfigruntimecpsConstants.ITEM_TYPE_MARA, classUnderTest.determineInstanceType(KB_ID, CLASS_ID));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDetermineInstanceTypeWrongKb()
	{
		classUnderTest.determineInstanceType("unknown", CLASS_ID);
	}
}
