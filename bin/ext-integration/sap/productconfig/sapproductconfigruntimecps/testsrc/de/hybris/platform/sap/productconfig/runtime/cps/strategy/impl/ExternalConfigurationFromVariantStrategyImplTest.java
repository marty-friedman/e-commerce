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
package de.hybris.platform.sap.productconfig.runtime.cps.strategy.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.ProductFeatureModel;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeValueModel;
import de.hybris.platform.classification.ClassificationService;
import de.hybris.platform.classification.features.Feature;
import de.hybris.platform.classification.features.FeatureList;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.productconfig.runtime.cps.cache.MasterDataCacheAccessService;
import de.hybris.platform.sap.productconfig.runtime.cps.constants.SapproductconfigruntimecpsConstants;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalCharacteristic;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalItem;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalObjectKey;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalValue;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataCharacteristicContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSQuantity;
import de.hybris.platform.sap.sapmodel.model.ERPVariantProductModel;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
@SuppressWarnings("javadoc")
public class ExternalConfigurationFromVariantStrategyImplTest
{
	private static final String variantCode = "DRAGON_CAR_V16";
	private static final String baseProductCode = "DRAGON_CAR";
	private static final String kbId = "123";
	private static final String csticKey = "COLOUR";
	private static final String classificationAttributeNonCPQ = "NON CPQ";
	private static final String csticValue = "BK";
	private static final String csticValueInClassificationSystem = csticKey + "_" + csticValue;
	private static final String classificationValueIdentifier = "8796093809248";
	private static final String productPk = "8796183429121";
	private static final String searchString = "select {pk} from {productfeature} where {stringvalue}='"
			+ classificationValueIdentifier + "'" + "and {product}='" + productPk + "'";
	private static final String AUTHOR_CONSTRAINT = "4";
	private static final String UNIT = "PCE";

	private final ExternalConfigurationFromVariantStrategyImpl classUnderTest = new ExternalConfigurationFromVariantStrategyImpl();

	@Mock
	private ClassificationService classificationService;

	@Mock
	private ProductService productService;

	@Mock
	private MasterDataCacheAccessService masterDataCacheAccessService;

	@Mock
	private I18NService i18NService;

	@Mock
	private FlexibleSearchService flexibleSearchService;

	@Mock
	private ERPVariantProductModel variantModel;

	@Mock
	private ProductModel baseProductModel;

	@Mock
	private FeatureList featureList;

	@Mock
	private Feature featureRelated;

	@Mock
	private Feature featureNotRelated;

	@Mock
	private ClassAttributeAssignmentModel classAttributeAssignment;

	@Mock
	private ClassAttributeAssignmentModel classAttributeAssignmentNotRelated;

	@Mock
	private ClassificationAttributeModel classificationAttribute;

	@Mock
	private ClassificationAttributeModel classificationAttributeNotRelated;

	@Mock
	private FeatureValue featureValue;

	@Mock
	private ClassificationAttributeValueModel classificationAttributeValue;

	@Mock
	private SearchResult<Object> searchResult;

	@Mock
	private ProductFeatureModel productFeature;

	@Mock
	private UnitModel unitModel;

	private final PK pkClassificationAttribute = PK.parse(classificationValueIdentifier);
	private final PK pkProduct = PK.parse(productPk);

	private final List<Feature> features = new ArrayList();
	private final CPSMasterDataKnowledgeBaseContainer kbContainer = new CPSMasterDataKnowledgeBaseContainer();
	private final Map<String, CPSMasterDataCharacteristicContainer> characteristics = new HashMap<>();
	private final CPSMasterDataCharacteristicContainer csticContainer = new CPSMasterDataCharacteristicContainer();
	private final List<FeatureValue> featureValueList = new ArrayList<>();
	private final List<Object> featureModelList = new ArrayList<>();



	@Before
	public void initialize()
	{
		MockitoAnnotations.initMocks(this);
		Mockito.when(productService.getProductForCode(variantCode)).thenReturn(variantModel);
		Mockito.when(productService.getProductForCode(baseProductCode)).thenReturn(baseProductModel);
		Mockito.when(variantModel.getBaseProduct()).thenReturn(baseProductModel);
		Mockito.when(variantModel.getPk()).thenReturn(pkProduct);
		Mockito.when(baseProductModel.getCode()).thenReturn(baseProductCode);
		Mockito.when(baseProductModel.getUnit()).thenReturn(unitModel);
		Mockito.when(unitModel.getCode()).thenReturn(UNIT);
		Mockito.when(classificationService.getFeatures(variantModel)).thenReturn(featureList);
		Mockito.when(i18NService.getCurrentLocale()).thenReturn(Locale.US);
		Mockito.when(masterDataCacheAccessService.getKbContainer(kbId, Locale.US.getLanguage())).thenReturn(kbContainer);
		Mockito.when(featureList.getFeatures()).thenReturn(features);
		Mockito.when(featureRelated.getClassAttributeAssignment()).thenReturn(classAttributeAssignment);
		Mockito.when(featureRelated.getValues()).thenReturn(featureValueList);
		Mockito.when(featureNotRelated.getClassAttributeAssignment()).thenReturn(classAttributeAssignmentNotRelated);
		Mockito.when(featureValue.getValue()).thenReturn(classificationAttributeValue);
		Mockito.when(classificationAttributeValue.getCode()).thenReturn(csticValueInClassificationSystem);
		Mockito.when(classificationAttributeValue.getPk()).thenReturn(pkClassificationAttribute);
		Mockito.when(classAttributeAssignment.getClassificationAttribute()).thenReturn(classificationAttribute);
		Mockito.when(classAttributeAssignmentNotRelated.getClassificationAttribute()).thenReturn(classificationAttributeNotRelated);
		Mockito.when(classificationAttribute.getCode()).thenReturn(csticKey);
		Mockito.when(classificationAttributeNotRelated.getCode()).thenReturn(classificationAttributeNonCPQ);
		Mockito.when(flexibleSearchService.search(searchString)).thenReturn(searchResult);
		Mockito.when(searchResult.getResult()).thenReturn(featureModelList);
		Mockito.when(productFeature.getAuthor()).thenReturn(AUTHOR_CONSTRAINT);

		features.add(featureRelated);
		features.add(featureNotRelated);
		featureValueList.add(featureValue);
		featureModelList.add(productFeature);

		kbContainer.setCharacteristics(characteristics);
		characteristics.put(csticKey, csticContainer);
		classUnderTest.setClassificationService(classificationService);
		classUnderTest.setProductService(productService);
		classUnderTest.setMasterDataCacheAccessService(masterDataCacheAccessService);
		classUnderTest.setI18NService(i18NService);
		classUnderTest.setFlexibleSearchService(flexibleSearchService);
	}

	@Test
	public void testClassificationService()
	{
		assertEquals(classificationService, classUnderTest.getClassificationService());
	}

	@Test
	public void testProductService()
	{
		assertEquals(productService, classUnderTest.getProductService());
	}

	@Test
	public void testFlexibleSearchService()
	{
		assertEquals(flexibleSearchService, classUnderTest.getFlexibleSearchService());
	}

	@Test
	public void testCreateExternalConfiguration()
	{
		final CPSExternalConfiguration configuration = classUnderTest.createExternalConfiguration(variantCode, kbId);
		assertNotNull(configuration);
		assertEquals(kbId, configuration.getKbId());
		assertTrue(configuration.isComplete());
		assertTrue(configuration.isConsistent());
		final CPSExternalItem rootItem = configuration.getRootItem();
		assertNotNull(rootItem);
		assertNull(rootItem.getSubItems());
		final List<CPSExternalCharacteristic> rootItemCstics = rootItem.getCharacteristics();
		assertNotNull(rootItemCstics);
		assertEquals(1, rootItemCstics.size());
		final CPSExternalCharacteristic cpsExternalCharacteristic = rootItemCstics.get(0);
		assertEquals(csticKey, cpsExternalCharacteristic.getId());
		final List<CPSExternalValue> values = cpsExternalCharacteristic.getValues();
		assertNotNull(values);
		assertEquals(1, values.size());
		final CPSExternalValue cpsExternalValue = values.get(0);
		assertEquals(csticValue, cpsExternalValue.getValue());
	}

	@Test
	public void testCreateRootInstanceFromVariant()
	{
		final CPSExternalItem rootItem = classUnderTest.createExternalRootItem(variantCode);
		assertNotNull(rootItem);
		final CPSQuantity quantity = rootItem.getQuantity();
		assertNotNull(quantity);
		assertEquals(Double.valueOf(1), quantity.getValue());
		assertEquals(UNIT, quantity.getUnit());
		assertEquals(ExternalConfigurationFromVariantStrategyImpl.INSTANCE_ID_ROOT, rootItem.getId());
		final CPSExternalObjectKey objectKey = rootItem.getObjectKey();
		assertNotNull(objectKey);
		assertEquals(SapproductconfigruntimecpsConstants.ITEM_TYPE_MARA, objectKey.getType());
		assertEquals(ExternalConfigurationFromVariantStrategyImpl.DEFAULT_CLASS_TYPE, objectKey.getClassType());
		assertEquals(baseProductCode, objectKey.getId());
		assertEquals(ExternalConfigurationFromVariantStrategyImpl.AUTHOR_USER, rootItem.getObjectKeyAuthor());
		assertTrue(rootItem.isComplete());
		assertTrue(rootItem.isConsistent());
	}

	@Test
	public void testDetermineBaseProduct()
	{
		final ProductModel base = classUnderTest.determineBaseProduct(variantCode);
		assertEquals(baseProductModel, base);
	}

	@Test(expected = IllegalStateException.class)
	public void testDetermineBaseProductNoVariant()
	{
		classUnderTest.determineBaseProduct(baseProductCode);
	}

	@Test
	public void testGetMasterDataCacheAccessService()
	{
		assertEquals(masterDataCacheAccessService, classUnderTest.getMasterDataCacheAccessService());
	}

	@Test
	public void testIsFeatureRelated()
	{
		assertTrue(classUnderTest.isFeatureRelatedToCurrentProduct(featureRelated, kbId));
	}

	@Test
	public void testIsFeatureRelatedNotRelated()
	{
		assertFalse(classUnderTest.isFeatureRelatedToCurrentProduct(featureNotRelated, kbId));
	}

	@Test
	public void testGetI18NService()
	{
		assertEquals(i18NService, classUnderTest.getI18NService());
	}

	@Test
	public void testReadCharacteristicName()
	{
		assertEquals(csticKey, classUnderTest.readCharacteristicName(featureRelated));
	}

	@Test(expected = NullPointerException.class)
	public void testReadCharacteristicNameNoClassAttributeAssignment()
	{
		Mockito.when(featureRelated.getClassAttributeAssignment()).thenReturn(null);
		classUnderTest.readCharacteristicName(featureRelated);
	}

	@Test(expected = NullPointerException.class)
	public void testReadCharacteristicNameNoClassificationAttribute()
	{
		Mockito.when(classAttributeAssignment.getClassificationAttribute()).thenReturn(null);
		classUnderTest.readCharacteristicName(featureRelated);
	}

	@Test
	public void testMapToCPSCharacteristics()
	{
		final CPSExternalCharacteristic characteristic = classUnderTest.mapToCPSCharacteristics(featureRelated, variantModel);
		assertNotNull(characteristic);
		assertEquals(csticKey, characteristic.getId());
		final List<CPSExternalValue> values = characteristic.getValues();
		assertNotNull(values);
		assertTrue(values.size() > 0);
	}

	@Test
	public void testAddCPSCharacteristicValue()
	{
		final List<CPSExternalValue> values = new ArrayList<>();
		classUnderTest.addCPSCharacteristicValue(classificationAttributeValue, values, csticKey, variantModel);
		assertEquals(1, values.size());
		final CPSExternalValue externalValue = values.get(0);
		assertEquals(csticValue, externalValue.getValue());
	}

	@Test
	public void testAddCPSCharacteristicValueDouble()
	{
		final List<CPSExternalValue> values = new ArrayList<>();
		final Double numericValue = Double.valueOf(3);
		classUnderTest.addCPSCharacteristicValue(numericValue, values, csticKey, variantModel);
		assertEquals(1, values.size());
		final CPSExternalValue externalValue = values.get(0);
		assertEquals(numericValue.toString(), externalValue.getValue());
	}

	@Test
	public void testAddCPSCharacteristicStringDouble()
	{
		final List<CPSExternalValue> values = new ArrayList<>();
		final String freeTextValue = "Huhu";
		classUnderTest.addCPSCharacteristicValue(freeTextValue, values, csticKey, variantModel);
		assertEquals(1, values.size());
		final CPSExternalValue externalValue = values.get(0);
		assertEquals(freeTextValue, externalValue.getValue());
	}

	@Test(expected = IllegalStateException.class)
	public void testAddCPSCharacteristicValueCsticValueDoesNotContainCstic()
	{
		final List<CPSExternalValue> values = new ArrayList<>();
		Mockito.when(classificationAttributeValue.getCode()).thenReturn(csticValue);
		classUnderTest.addCPSCharacteristicValue(classificationAttributeValue, values, csticKey, variantModel);
	}

	@Test
	public void testFindAuthor()
	{
		final String author = classUnderTest.findAuthor(classificationValueIdentifier, productPk);
		assertEquals(AUTHOR_CONSTRAINT, author);
	}

	@Test
	public void testFindAuthorDontReturnNull()
	{
		Mockito.when(productFeature.getAuthor()).thenReturn(null);
		final String author = classUnderTest.findAuthor(classificationValueIdentifier, productPk);
		assertEquals(ExternalConfigurationFromVariantStrategyImpl.AUTHOR_USER, author);
	}


	@Test(expected = IllegalStateException.class)
	public void testFindAuthorNoProductFeatures()
	{
		featureModelList.clear();
		classUnderTest.findAuthor(classificationValueIdentifier, productPk);
	}

	@Test
	public void testDetermineCharacteristics()
	{
		final List<CPSExternalCharacteristic> cpsCharacteristics = classUnderTest.determineCharacteristics(kbId, variantModel);
		assertNotNull(cpsCharacteristics);
		assertEquals(1, cpsCharacteristics.size());
	}

	@Test
	public void testDetermineCharacteristicsNoValues()
	{
		featureValueList.clear();
		final List<CPSExternalCharacteristic> cpsCharacteristics = classUnderTest.determineCharacteristics(kbId, variantModel);
		assertNotNull(cpsCharacteristics);
		assertTrue(cpsCharacteristics.isEmpty());
	}

	@Test
	public void testMapToValueModel()
	{
		final Object valueModel = classUnderTest.mapToValueModel(featureValue);
		assertTrue(valueModel instanceof ClassificationAttributeValueModel);
		assertEquals(csticValueInClassificationSystem, ((ClassificationAttributeValueModel) valueModel).getCode());
	}

	@Test
	public void testMapToValueModelDouble()
	{
		final Double numericValue = Double.valueOf(3);
		Mockito.when(featureValue.getValue()).thenReturn(numericValue);
		final Object valueModel = classUnderTest.mapToValueModel(featureValue);
		assertTrue(valueModel instanceof Double);
	}

	@Test
	public void testMapToValueModelString()
	{
		final String freeTextValue = "Huhu";
		Mockito.when(featureValue.getValue()).thenReturn(freeTextValue);
		final Object valueModel = classUnderTest.mapToValueModel(featureValue);
		assertTrue(valueModel instanceof String);
	}


	@Test(expected = IllegalStateException.class)
	public void testMapToValueModelWrongFeatureType()
	{
		Mockito.when(featureValue.getValue()).thenReturn(productFeature);
		classUnderTest.mapToValueModel(featureValue);
	}

}
