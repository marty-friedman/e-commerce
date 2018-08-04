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
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.ProductSearchService;
import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryTermData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.impl.DefaultSolrProductSearchService;
import de.hybris.platform.commerceservices.search.solrfacetsearch.strategies.exceptions.NoValidSolrConfigException;
import de.hybris.platform.commerceservices.threadcontext.ThreadContextService;
import de.hybris.platform.commerceservices.threadcontext.impl.DefaultThreadContextService;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ClassificationSystemCPQAttributesContainer;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.data.VariantSearchResult;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.intf.SearchAttributeSelectionStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import edu.umd.cs.findbugs.annotations.SuppressWarnings;


@UnitTest
public class ProductConfigurationVariantSearchServiceImplTest
{
	/**
	 *
	 */
	private ProductConfigurationVariantSearchServiceImpl classUnderTest;
	private final static String configId = "A";
	private final static String productCode = "productCode";

	@Mock
	SearchAttributeSelectionStrategy searchAttributeSelectionStrategy;

	@Mock
	ProductConfigurationService productConfigurationService;

	@Mock
	private SessionAccessService sessionAccessService;

	private final ConfigModel configurationModel = new ConfigModelImpl();
	private final InstanceModel rootInstance = new InstanceModelImpl();
	private final List<CsticModel> csticList = new ArrayList<>();
	private final CsticModel cstic = new CsticModelImpl();
	private final List<CsticValueModel> assignedValues = new ArrayList<>();
	private final CsticValueModel csticValue = new CsticValueModelImpl();
	private final static String csticValueName = "value";
	private final static String csticName = "cstic";
	private final static String csticNameUnknown = "csticUn";
	private static final String classificationCsticValueName = "classificationValueName";
	private ProductSearchPageData solrSearchResult;
	private List results;
	private SearchResultValueData searchResultValueData;

	@Before
	public void setUp() throws NoValidSolrConfigException
	{
		MockitoAnnotations.initMocks(this);
		Mockito.when(productConfigurationService.retrieveConfigurationModel(configId)).thenReturn(configurationModel);
		configurationModel.setRootInstance(rootInstance);
		rootInstance.setCstics(csticList);
		csticList.add(cstic);
		cstic.setName(csticName);
		assignedValues.add(csticValue);
		csticValue.setName(csticValueName);
		csticValue.setLanguageDependentName(csticValueName);
		cstic.setAssignedValues(assignedValues);
		Mockito.when(Boolean.valueOf(searchAttributeSelectionStrategy.isAttributeAvailableOnSearchIndex(csticName))).thenReturn(
				Boolean.valueOf(true));
		Mockito.when(Boolean.valueOf(searchAttributeSelectionStrategy.isAttributeAvailableOnSearchIndex(csticNameUnknown)))
				.thenReturn(Boolean.valueOf(false));
		Mockito.when(sessionAccessService.getCachedNameMap()).thenReturn(new HashMap());

		classUnderTest = new ProductConfigurationVariantSearchServiceImpl();
		classUnderTest.setProductConfigurationService(productConfigurationService);
		classUnderTest.setSearchAttributeSelectionStrategy(searchAttributeSelectionStrategy);
		classUnderTest.setSessionAccessService(sessionAccessService);
		solrSearchResult = new ProductSearchPageData<>();
		results = new ArrayList<SearchResultValueData>();
		searchResultValueData = new SearchResultValueData();
		final Map<String, Object> values = new HashMap<>();
		values.put("code", productCode);
		searchResultValueData.setValues(values);
		results.add(searchResultValueData);
		solrSearchResult.setResults(results);
	}

	@Test
	public void testConfigurationservice()
	{
		final ProductConfigurationService configService = new ProductConfigurationServiceImpl();
		classUnderTest.setProductConfigurationService(configService);
		assertEquals(configService, classUnderTest.getProductConfigurationService());
	}

	@Test
	public void testSearchService()
	{
		final ProductSearchService searchService = new DefaultSolrProductSearchService<ProductData>();
		classUnderTest.setProductSearchService(searchService);
		assertEquals(searchService, classUnderTest.getProductSearchService());
	}

	@Test(expected = NullPointerException.class)
	public void testGetRootCharacteristicsNoRootInstance()
	{
		configurationModel.setRootInstance(null);
		classUnderTest.getRootCharacteristics(configId);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetRootCharacteristicsNoConfigId()
	{
		classUnderTest.getRootCharacteristics(null);
	}

	@Test
	public void testGetRootCharacteristics()
	{
		final List<CsticModel> characteristics = classUnderTest.getRootCharacteristics(configId);
		assertEquals(csticList, characteristics);
	}

	@Test(expected = IllegalArgumentException.class)
	@SuppressWarnings("NP_NULL_PARAM_DEREF_ALL_TARGETS_DANGEROUS")
	public void testCreateQueryDataTermNull()
	{
		classUnderTest.createQueryDataTerm(null, null, null);
	}

	@Test
	public void testCreateQueryDataTerm()
	{
		final SolrSearchQueryTermData queryTermData = classUnderTest.createQueryDataTerm(cstic, csticValue, null);
		assertNotNull(queryTermData);
		assertEquals(csticName, queryTermData.getKey());
		assertEquals(csticValueName, queryTermData.getValue());
	}

	@Test
	public void testCreateQueryDataTermOverwriteValueNameViaClassification()
	{
		final Map<String, String> valueNames = new HashMap<>();
		valueNames.put(csticName + "_" + csticValueName, classificationCsticValueName);
		final ClassificationSystemCPQAttributesContainer cpqAttribute = new ClassificationSystemCPQAttributesContainer(csticName,
				csticName, null, valueNames, new ArrayList(), new HashMap());

		final SolrSearchQueryTermData queryTermData = classUnderTest.createQueryDataTerm(cstic, csticValue, cpqAttribute);
		assertNotNull(queryTermData);
		assertEquals(csticName, queryTermData.getKey());
		assertEquals(classificationCsticValueName, queryTermData.getValue());
	}

	@Test(expected = IllegalArgumentException.class)
	@SuppressWarnings("NP_NULL_PARAM_DEREF_ALL_TARGETS_DANGEROUS")
	public void testConvertToQueryDataListNull()
	{
		classUnderTest.convertToQueryDataList(null, null);
	}

	@Test
	public void testConvertToQueryDataList()
	{
		final List<SolrSearchQueryTermData> queryDataList = classUnderTest.convertToQueryDataList(cstic, new HashMap());
		assertNotNull(queryDataList);
		assertEquals(1, queryDataList.size());
		assertEquals(csticName, queryDataList.get(0).getKey());
		assertEquals(csticValueName, queryDataList.get(0).getValue());
	}

	@Test
	public void testConvertToQueryDataListOverwriteValueNameViaClassification()
	{
		final Map<String, ClassificationSystemCPQAttributesContainer> hybrisNamesMap = new HashMap<>();
		final Map<String, String> valueNames = new HashMap<>();
		valueNames.put(csticName + "_" + csticValueName, classificationCsticValueName);
		final ClassificationSystemCPQAttributesContainer cpqAttribute = new ClassificationSystemCPQAttributesContainer(csticName,
				csticName, null, valueNames, new ArrayList(), new HashMap());
		hybrisNamesMap.put(csticName, cpqAttribute);

		final List<SolrSearchQueryTermData> queryDataList = classUnderTest.convertToQueryDataList(cstic, hybrisNamesMap);
		assertNotNull(queryDataList);
		assertEquals(1, queryDataList.size());
		assertEquals(csticName, queryDataList.get(0).getKey());
		assertEquals(classificationCsticValueName, queryDataList.get(0).getValue());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetFilterTermsNull()
	{
		classUnderTest.getFilterTerms(null);
	}

	@Test
	public void testGetFilterTerms()
	{
		final List<SolrSearchQueryTermData> filterTerms = classUnderTest.getFilterTerms(configId);
		assertNotNull(filterTerms);
		assertEquals(1, filterTerms.size());
		assertEquals(csticName, filterTerms.get(0).getKey());
		assertEquals(csticValueName, filterTerms.get(0).getValue());
	}

	@Test(expected = NullPointerException.class)
	public void testCompileSearchResultNull()
	{
		solrSearchResult = null;
		classUnderTest.compileSearchResult(solrSearchResult);
	}

	@Test
	public void testCompileSearchResult()
	{
		final List<VariantSearchResult> result = classUnderTest.compileSearchResult(solrSearchResult);
		assertNotNull(result);
		assertEquals(1, result.size());
		assertNotNull(result.get(0));
		assertEquals(productCode, result.get(0).getProductCode());
	}

	@Test
	public void testSearchAttributeSelectionStrategy()
	{
		assertEquals(searchAttributeSelectionStrategy, classUnderTest.getSearchAttributeSelectionStrategy());
	}

	@Test
	public void testIsUsedForSearch()
	{
		assertTrue(classUnderTest.isUsedForSearch(cstic));
	}


	@Test
	public void testIsUsedForSearchUnknown()
	{
		cstic.setName(csticNameUnknown);
		assertFalse(classUnderTest.isUsedForSearch(cstic));
	}

	@Test(expected = NullPointerException.class)
	public void testConvertToVariantSearchResultNull()
	{
		classUnderTest.convertToVariantSearchResult(null);
	}

	@Test(expected = NullPointerException.class)
	public void testConvertToVariantSearchResultAttributeNotAvailable()
	{
		searchResultValueData.getValues().remove("code");
		classUnderTest.convertToVariantSearchResult(searchResultValueData);
	}

	@Test
	public void testThreadContextService()
	{
		final ThreadContextService threadContextService = new DefaultThreadContextService();
		classUnderTest.setThreadContextService(threadContextService);
		assertEquals(threadContextService, classUnderTest.getThreadContextService());
	}

	@Test
	public void testAddBaseProductToQuery()
	{
		final SolrSearchQueryData searchQuery = new SolrSearchQueryData();
		searchQuery.setFilterTerms(new ArrayList<>());
		classUnderTest.addBaseProductToQuery(productCode, searchQuery);
		final List<SolrSearchQueryTermData> filterTerms = searchQuery.getFilterTerms();
		assertEquals(1, filterTerms.size());
		final SolrSearchQueryTermData solrSearchQueryTermData = filterTerms.get(0);
		assertEquals(productCode, solrSearchQueryTermData.getValue());
		assertEquals(ProductConfigurationVariantSearchServiceImpl.BASE_PRODUCT_ON_SOLR, solrSearchQueryTermData.getKey());
	}
}
