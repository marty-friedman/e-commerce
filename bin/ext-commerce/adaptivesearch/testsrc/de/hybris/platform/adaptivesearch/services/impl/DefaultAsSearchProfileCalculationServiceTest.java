/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.adaptivesearch.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.adaptivesearch.context.AsSearchProfileContext;
import de.hybris.platform.adaptivesearch.context.AsSearchProfileContextFactory;
import de.hybris.platform.adaptivesearch.data.AbstractAsBoostItemConfiguration;
import de.hybris.platform.adaptivesearch.data.AbstractAsFacetConfiguration;
import de.hybris.platform.adaptivesearch.data.AsBoostRule;
import de.hybris.platform.adaptivesearch.data.AsConfigurationHolder;
import de.hybris.platform.adaptivesearch.data.AsExcludedFacet;
import de.hybris.platform.adaptivesearch.data.AsExcludedItem;
import de.hybris.platform.adaptivesearch.data.AsFacet;
import de.hybris.platform.adaptivesearch.data.AsMergeConfiguration;
import de.hybris.platform.adaptivesearch.data.AsPromotedFacet;
import de.hybris.platform.adaptivesearch.data.AsPromotedItem;
import de.hybris.platform.adaptivesearch.data.AsSearchProfileActivationGroup;
import de.hybris.platform.adaptivesearch.data.AsSearchProfileResult;
import de.hybris.platform.adaptivesearch.enums.AsBoostItemsMergeMode;
import de.hybris.platform.adaptivesearch.enums.AsBoostOperator;
import de.hybris.platform.adaptivesearch.enums.AsBoostRulesMergeMode;
import de.hybris.platform.adaptivesearch.enums.AsBoostType;
import de.hybris.platform.adaptivesearch.enums.AsFacetType;
import de.hybris.platform.adaptivesearch.enums.AsFacetsMergeMode;
import de.hybris.platform.adaptivesearch.model.AbstractAsSearchProfileModel;
import de.hybris.platform.adaptivesearch.services.AsSearchProfileCalculationService;
import de.hybris.platform.adaptivesearch.services.AsSearchProfileService;
import de.hybris.platform.adaptivesearch.strategies.AsCacheStrategy;
import de.hybris.platform.adaptivesearch.util.MergeMap;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultAsSearchProfileCalculationServiceTest extends ServicelayerTransactionalTest
{
	private static final String INDEX_CONFIGURATION = "indexConfiguration";
	private static final String INDEX_TYPE = "index1";

	private static final String SIMPLE_SEARCH_PROFILE_1_CODE = "simpleProfile1";
	private static final String SIMPLE_SEARCH_PROFILE_2_CODE = "simpleProfile2";
	private static final String CATEGORY_AWARE_SEARCH_PROFILE_1_CODE = "categoryAwareProfile1";
	private static final String CATEGORY_AWARE_SEARCH_PROFILE_2_CODE = "categoryAwareProfile2";

	private static final String PROPERTY1 = "property1";
	private static final String PROPERTY2 = "property2";
	private static final String PROPERTY3 = "property3";

	private static final String PRODUCT1_CODE = "product1";
	private static final String PRODUCT2_CODE = "product2";
	private static final String PRODUCT3_CODE = "product3";

	private static final String BOOST_VALUE = "value";

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private CategoryService categoryService;

	@Resource
	private ProductService productService;

	@Resource
	private AsSearchProfileService asSearchProfileService;

	@Resource
	private AsSearchProfileCalculationService asSearchProfileCalculationService;

	@Resource
	private AsSearchProfileContextFactory asSearchProfileContextFactory;

	@Resource
	private AsCacheStrategy asCacheStrategy;

	private CatalogVersionModel catalogVersion;
	private CategoryModel category10;
	private CategoryModel category20;
	private ProductModel product1;
	private ProductModel product2;
	private ProductModel product3;

	@Before
	public void setUp() throws Exception
	{
		importCsv("/adaptivesearch/test/services/defaultAsSearchProfileCalculationServiceTest.impex", "utf-8");

		catalogVersion = catalogVersionService.getCatalogVersion("hwcatalog", "Staged");
		category10 = categoryService.getCategoryForCode(catalogVersion, "cat10");
		category20 = categoryService.getCategoryForCode(catalogVersion, "cat20");
		product1 = productService.getProductForCode(catalogVersion, PRODUCT1_CODE);
		product2 = productService.getProductForCode(catalogVersion, PRODUCT2_CODE);
		product3 = productService.getProductForCode(catalogVersion, PRODUCT3_CODE);
	}

	@Test
	public void calculateSearchProfile() throws Exception
	{
		// given
		final AsSearchProfileContext context = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				Collections.singletonList(catalogVersion), Collections.emptyList());
		final Optional<AbstractAsSearchProfileModel> searchProfile = asSearchProfileService.getSearchProfileForCode(catalogVersion,
				SIMPLE_SEARCH_PROFILE_1_CODE);

		// when
		final AsSearchProfileResult result = asSearchProfileCalculationService.calculate(context,
				Collections.singletonList(searchProfile.get()));

		// then
		assertNotNull(result);

		final List<AsConfigurationHolder<AsPromotedFacet, AbstractAsFacetConfiguration>> promotedFacets = ((MergeMap<String, AsConfigurationHolder<AsPromotedFacet, AbstractAsFacetConfiguration>>) result
				.getPromotedFacets()).orderedValues();
		assertEquals(1, promotedFacets.size());

		final AsPromotedFacet promotedFacet = promotedFacets.get(0).getConfiguration();
		assertEquals(PROPERTY1, promotedFacet.getIndexProperty());

		final List<AsConfigurationHolder<AsFacet, AbstractAsFacetConfiguration>> facets = ((MergeMap<String, AsConfigurationHolder<AsFacet, AbstractAsFacetConfiguration>>) result
				.getFacets()).orderedValues();
		assertEquals(0, facets.size());

		final List<AsConfigurationHolder<AsExcludedFacet, AbstractAsFacetConfiguration>> excludedFacets = ((MergeMap<String, AsConfigurationHolder<AsExcludedFacet, AbstractAsFacetConfiguration>>) result
				.getExcludedFacets()).orderedValues();
		assertEquals(1, excludedFacets.size());

		final AsExcludedFacet excludedFacet = excludedFacets.get(0).getConfiguration();
		assertEquals(PROPERTY2, excludedFacet.getIndexProperty());

		final List<AsConfigurationHolder<AsPromotedItem, AbstractAsBoostItemConfiguration>> promotedItems = ((MergeMap<PK, AsConfigurationHolder<AsPromotedItem, AbstractAsBoostItemConfiguration>>) result
				.getPromotedItems()).orderedValues();
		assertEquals(1, promotedItems.size());

		final AsPromotedItem promotedItem = promotedItems.get(0).getConfiguration();
		assertEquals(product1.getPk(), promotedItem.getItemPk());

		final List<AsConfigurationHolder<AsExcludedItem, AbstractAsBoostItemConfiguration>> excludedItems = ((MergeMap<PK, AsConfigurationHolder<AsExcludedItem, AbstractAsBoostItemConfiguration>>) result
				.getExcludedItems()).orderedValues();
		assertEquals(1, excludedItems.size());

		final AsExcludedItem excludedItem = excludedItems.get(0).getConfiguration();
		assertEquals(product2.getPk(), excludedItem.getItemPk());

		assertEquals(1, result.getBoostRules().size());
		final AsBoostRule boostRule = result.getBoostRules().get(0).getConfiguration();
		assertEquals(PROPERTY1, boostRule.getIndexProperty());
		assertEquals(AsBoostOperator.EQUAL, boostRule.getOperator());
		assertEquals(BOOST_VALUE, boostRule.getValue());
		assertEquals(Float.valueOf(1.1f), boostRule.getBoost()); // should not compare exact value
		assertEquals(AsBoostType.MULTIPLICATIVE, boostRule.getBoostType());
	}

	@Test
	public void calculateCategoryAwareSearchProfile() throws Exception
	{
		// given
		final AsSearchProfileContext context = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				Collections.singletonList(catalogVersion), Arrays.asList(category20, category10));
		final Optional<AbstractAsSearchProfileModel> searchProfile = asSearchProfileService.getSearchProfileForCode(catalogVersion,
				CATEGORY_AWARE_SEARCH_PROFILE_1_CODE);

		// when
		final AsSearchProfileResult result = asSearchProfileCalculationService.calculate(context,
				Collections.singletonList(searchProfile.get()));

		// then
		assertNotNull(result);

		final List<AsConfigurationHolder<AsPromotedFacet, AbstractAsFacetConfiguration>> promotedFacets = ((MergeMap<String, AsConfigurationHolder<AsPromotedFacet, AbstractAsFacetConfiguration>>) result
				.getPromotedFacets()).orderedValues();
		assertEquals(1, promotedFacets.size());

		final AsPromotedFacet promotedFacet = promotedFacets.get(0).getConfiguration();
		assertEquals(PROPERTY2, promotedFacet.getIndexProperty());

		final List<AsConfigurationHolder<AsFacet, AbstractAsFacetConfiguration>> facets = ((MergeMap<String, AsConfigurationHolder<AsFacet, AbstractAsFacetConfiguration>>) result
				.getFacets()).orderedValues();
		assertEquals(1, facets.size());

		final AsFacet facet = facets.get(0).getConfiguration();
		assertEquals(PROPERTY1, facet.getIndexProperty());
		assertEquals(Integer.valueOf(12), facet.getPriority());
		assertEquals(AsFacetType.REFINE, facet.getFacetType());

		final List<AsConfigurationHolder<AsExcludedFacet, AbstractAsFacetConfiguration>> excludedFacets = ((MergeMap<String, AsConfigurationHolder<AsExcludedFacet, AbstractAsFacetConfiguration>>) result
				.getExcludedFacets()).orderedValues();
		assertEquals(1, excludedFacets.size());

		final AsExcludedFacet excludedFacet = excludedFacets.get(0).getConfiguration();
		assertEquals(PROPERTY3, excludedFacet.getIndexProperty());

		final List<AsConfigurationHolder<AsPromotedItem, AbstractAsBoostItemConfiguration>> promotedItems = ((MergeMap<PK, AsConfigurationHolder<AsPromotedItem, AbstractAsBoostItemConfiguration>>) result
				.getPromotedItems()).orderedValues();
		assertEquals(1, promotedItems.size());

		final AsPromotedItem promotedItem = promotedItems.get(0).getConfiguration();
		assertEquals(product1.getPk(), promotedItem.getItemPk());

		final List<AsConfigurationHolder<AsExcludedItem, AbstractAsBoostItemConfiguration>> excludedItems = ((MergeMap<PK, AsConfigurationHolder<AsExcludedItem, AbstractAsBoostItemConfiguration>>) result
				.getExcludedItems()).orderedValues();
		assertEquals(1, excludedItems.size());

		final AsExcludedItem excludedItem = excludedItems.get(0).getConfiguration();
		assertEquals(product3.getPk(), excludedItem.getItemPk());

		assertEquals(2, result.getBoostRules().size());

		final AsBoostRule boostRule1 = result.getBoostRules().get(0).getConfiguration();
		assertEquals(PROPERTY1, boostRule1.getIndexProperty());
		assertEquals(AsBoostOperator.EQUAL, boostRule1.getOperator());
		assertEquals(BOOST_VALUE, boostRule1.getValue());
		assertEquals(Float.valueOf(1.3f), boostRule1.getBoost()); // should not compare exact value
		assertEquals(AsBoostType.MULTIPLICATIVE, boostRule1.getBoostType());

		final AsBoostRule boostRule2 = result.getBoostRules().get(1).getConfiguration();
		assertEquals(PROPERTY2, boostRule2.getIndexProperty());
		assertEquals(AsBoostOperator.EQUAL, boostRule2.getOperator());
		assertEquals(BOOST_VALUE, boostRule2.getValue());
		assertEquals(Float.valueOf(1.2f), boostRule2.getBoost()); // should not compare exact value
		assertEquals(AsBoostType.MULTIPLICATIVE, boostRule2.getBoostType());
	}

	@Test
	public void calculateMultipleSearchProfiles() throws Exception
	{
		// given
		final AsSearchProfileContext context = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				Collections.singletonList(catalogVersion), Arrays.asList(category20, category10));

		final Optional<AbstractAsSearchProfileModel> searchProfile = asSearchProfileService.getSearchProfileForCode(catalogVersion,
				SIMPLE_SEARCH_PROFILE_1_CODE);

		final Optional<AbstractAsSearchProfileModel> categoryAwareSearchProfile = asSearchProfileService
				.getSearchProfileForCode(catalogVersion, CATEGORY_AWARE_SEARCH_PROFILE_1_CODE);

		// when
		final AsSearchProfileResult result = asSearchProfileCalculationService.calculate(context,
				Arrays.asList(searchProfile.get(), categoryAwareSearchProfile.get()));

		// then
		assertNotNull(result);

		final List<AsConfigurationHolder<AsPromotedFacet, AbstractAsFacetConfiguration>> promotedFacets = ((MergeMap<String, AsConfigurationHolder<AsPromotedFacet, AbstractAsFacetConfiguration>>) result
				.getPromotedFacets()).orderedValues();
		assertEquals(1, promotedFacets.size());

		final AsPromotedFacet promotedFacet = promotedFacets.get(0).getConfiguration();
		assertEquals(PROPERTY2, promotedFacet.getIndexProperty());

		final List<AsConfigurationHolder<AsFacet, AbstractAsFacetConfiguration>> facets = ((MergeMap<String, AsConfigurationHolder<AsFacet, AbstractAsFacetConfiguration>>) result
				.getFacets()).orderedValues();
		assertEquals(1, facets.size());

		final AsFacet facet = facets.get(0).getConfiguration();
		assertEquals(PROPERTY1, facet.getIndexProperty());
		assertEquals(Integer.valueOf(12), facet.getPriority());
		assertEquals(AsFacetType.REFINE, facet.getFacetType());

		final List<AsConfigurationHolder<AsExcludedFacet, AbstractAsFacetConfiguration>> excludedFacets = ((MergeMap<String, AsConfigurationHolder<AsExcludedFacet, AbstractAsFacetConfiguration>>) result
				.getExcludedFacets()).orderedValues();
		assertEquals(1, excludedFacets.size());

		final AsExcludedFacet excludedFacet = excludedFacets.get(0).getConfiguration();
		assertEquals(PROPERTY3, excludedFacet.getIndexProperty());

		final List<AsConfigurationHolder<AsPromotedItem, AbstractAsBoostItemConfiguration>> promotedItems = ((MergeMap<PK, AsConfigurationHolder<AsPromotedItem, AbstractAsBoostItemConfiguration>>) result
				.getPromotedItems()).orderedValues();
		assertEquals(1, promotedItems.size());

		final AsPromotedItem promotedItem = promotedItems.get(0).getConfiguration();
		assertEquals(product1.getPk(), promotedItem.getItemPk());

		final List<AsConfigurationHolder<AsExcludedItem, AbstractAsBoostItemConfiguration>> excludedItems = ((MergeMap<PK, AsConfigurationHolder<AsExcludedItem, AbstractAsBoostItemConfiguration>>) result
				.getExcludedItems()).orderedValues();
		assertEquals(2, excludedItems.size());

		final AsExcludedItem excludedItem1 = excludedItems.get(0).getConfiguration();
		assertEquals(product2.getPk(), excludedItem1.getItemPk());

		final AsExcludedItem excludedItem2 = excludedItems.get(1).getConfiguration();
		assertEquals(product3.getPk(), excludedItem2.getItemPk());

		assertEquals(3, result.getBoostRules().size());

		final AsBoostRule boostRule1 = result.getBoostRules().get(0).getConfiguration();
		assertEquals(PROPERTY1, boostRule1.getIndexProperty());
		assertEquals(AsBoostOperator.EQUAL, boostRule1.getOperator());
		assertEquals(BOOST_VALUE, boostRule1.getValue());
		assertEquals(Float.valueOf(1.1f), boostRule1.getBoost()); // should not compare exact value
		assertEquals(AsBoostType.MULTIPLICATIVE, boostRule1.getBoostType());

		final AsBoostRule boostRule2 = result.getBoostRules().get(1).getConfiguration();
		assertEquals(PROPERTY1, boostRule2.getIndexProperty());
		assertEquals(AsBoostOperator.EQUAL, boostRule2.getOperator());
		assertEquals(BOOST_VALUE, boostRule2.getValue());
		assertEquals(Float.valueOf(1.3f), boostRule2.getBoost()); // should not compare exact value
		assertEquals(AsBoostType.MULTIPLICATIVE, boostRule2.getBoostType());

		final AsBoostRule boostRule3 = result.getBoostRules().get(2).getConfiguration();
		assertEquals(PROPERTY2, boostRule3.getIndexProperty());
		assertEquals(AsBoostOperator.EQUAL, boostRule3.getOperator());
		assertEquals(BOOST_VALUE, boostRule3.getValue());
		assertEquals(Float.valueOf(1.2f), boostRule3.getBoost()); // should not compare exact value
		assertEquals(AsBoostType.MULTIPLICATIVE, boostRule3.getBoostType());
	}

	@Test
	public void calculateSearchProfileCacheTest() throws Exception
	{
		// given
		final AsSearchProfileContext context = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				Collections.singletonList(catalogVersion), Collections.emptyList());
		final Optional<AbstractAsSearchProfileModel> searchProfile = asSearchProfileService.getSearchProfileForCode(catalogVersion,
				SIMPLE_SEARCH_PROFILE_1_CODE);

		// when
		asCacheStrategy.clear();

		for (int i = 0; i < 10; i++)
		{
			asSearchProfileCalculationService.calculate(context, Collections.singletonList(searchProfile.get()));
		}

		// then
		assertEquals(2, asCacheStrategy.getSize());
		assertEquals(2, asCacheStrategy.getMisses());
		// 18 = 10 * 2(caches) - 2(the first hits)
		assertEquals(18, asCacheStrategy.getHits());
	}

	@Test
	public void calculateSearchProfilesCacheTest() throws Exception
	{
		// given
		final AsSearchProfileContext context = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				Collections.singletonList(catalogVersion), Collections.emptyList());
		final Optional<AbstractAsSearchProfileModel> searchProfile1 = asSearchProfileService.getSearchProfileForCode(catalogVersion,
				SIMPLE_SEARCH_PROFILE_1_CODE);
		final Optional<AbstractAsSearchProfileModel> searchProfile2 = asSearchProfileService.getSearchProfileForCode(catalogVersion,
				SIMPLE_SEARCH_PROFILE_2_CODE);

		// when
		asCacheStrategy.clear();

		for (int i = 0; i < 10; i++)
		{
			asSearchProfileCalculationService.calculate(context, Arrays.asList(searchProfile1.get(), searchProfile2.get()));
		}

		// then
		assertEquals(5, asCacheStrategy.getSize());
		assertEquals(5, asCacheStrategy.getMisses());
		// 45 = 10 * 5(caches) - 5(the first hits)
		assertEquals(45, asCacheStrategy.getHits());
	}

	@Test
	public void calculateSearchProfileGroupCacheTest() throws Exception
	{
		// given
		final AsSearchProfileContext context = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				Collections.singletonList(catalogVersion), Collections.emptyList());
		final Optional<AbstractAsSearchProfileModel> searchProfile1 = asSearchProfileService.getSearchProfileForCode(catalogVersion,
				SIMPLE_SEARCH_PROFILE_1_CODE);
		final Optional<AbstractAsSearchProfileModel> searchProfile2 = asSearchProfileService.getSearchProfileForCode(catalogVersion,
				SIMPLE_SEARCH_PROFILE_2_CODE);
		final AsMergeConfiguration mergeConfiguration = new AsMergeConfiguration();
		mergeConfiguration.setFacetsMergeMode(AsFacetsMergeMode.ADD_AFTER);
		mergeConfiguration.setBoostRulesMergeMode(AsBoostRulesMergeMode.ADD);
		mergeConfiguration.setBoostItemsMergeMode(AsBoostItemsMergeMode.ADD_BEFORE);
		mergeConfiguration.setResultFacetsMergeMode(AsFacetsMergeMode.ADD_BEFORE);
		mergeConfiguration.setResultBoostRulesMergeMode(AsBoostRulesMergeMode.REPLACE);
		mergeConfiguration.setResultBoostItemsMergeMode(AsBoostItemsMergeMode.ADD_AFTER);

		final AsSearchProfileActivationGroup group = new AsSearchProfileActivationGroup();
		group.setSearchProfiles(Arrays.asList(searchProfile1.get(), searchProfile2.get()));
		group.setMergeConfiguration(mergeConfiguration);

		// when
		asCacheStrategy.clear();

		for (int i = 0; i < 10; i++)
		{
			asSearchProfileCalculationService.calculateGroups(context, Collections.singletonList(group));
		}

		// then
		assertEquals(5, asCacheStrategy.getSize());
		assertEquals(5, asCacheStrategy.getMisses());
		// 45 = 10 * 5(caches) - 5(the first hits)
		assertEquals(45, asCacheStrategy.getHits());
	}

	@Test
	public void calculateCategoryAwareSearchProfileCacheTest() throws Exception
	{
		// given
		final AsSearchProfileContext context = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				Collections.singletonList(catalogVersion), Arrays.asList(category20, category10));
		final Optional<AbstractAsSearchProfileModel> searchProfile = asSearchProfileService.getSearchProfileForCode(catalogVersion,
				CATEGORY_AWARE_SEARCH_PROFILE_1_CODE);

		// when
		asCacheStrategy.clear();

		for (int i = 0; i < 10; i++)
		{
			asSearchProfileCalculationService.calculate(context, Collections.singletonList(searchProfile.get()));
		}

		// then
		assertEquals(2, asCacheStrategy.getSize());
		assertEquals(2, asCacheStrategy.getMisses());
		// 18 = 10 * 2(caches) - 2(the first hits)
		assertEquals(18, asCacheStrategy.getHits());
	}

	@Test
	public void calculateCategoryAwareSearchProfileGroupCacheTest() throws Exception
	{
		// given
		final AsSearchProfileContext context = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				Collections.singletonList(catalogVersion), Arrays.asList(category20, category10));
		final Optional<AbstractAsSearchProfileModel> searchProfile1 = asSearchProfileService.getSearchProfileForCode(catalogVersion,
				CATEGORY_AWARE_SEARCH_PROFILE_1_CODE);
		final Optional<AbstractAsSearchProfileModel> searchProfile2 = asSearchProfileService.getSearchProfileForCode(catalogVersion,
				CATEGORY_AWARE_SEARCH_PROFILE_2_CODE);

		final AsSearchProfileActivationGroup group1 = new AsSearchProfileActivationGroup();
		group1.setSearchProfiles(Arrays.asList(searchProfile1.get(), searchProfile2.get()));

		final AsSearchProfileActivationGroup group2 = new AsSearchProfileActivationGroup();
		group2.setSearchProfiles(Arrays.asList(searchProfile1.get(), searchProfile2.get()));

		// when
		asCacheStrategy.clear();

		for (int i = 0; i < 10; i++)
		{
			asSearchProfileCalculationService.calculateGroups(context, Arrays.asList(group1, group2));
		}

		// then
		assertEquals(5, asCacheStrategy.getSize());
		assertEquals(5, asCacheStrategy.getMisses());
		// 95 = 20 * 5(caches) - 5(the first hits)
		assertEquals(95, asCacheStrategy.getHits());
	}

	@Test
	public void calculateCategoryAwareSearchProfileGroupCacheWithConfigurationTest() throws Exception
	{
		// given
		final AsSearchProfileContext context = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				Collections.singletonList(catalogVersion), Arrays.asList(category20, category10));
		final Optional<AbstractAsSearchProfileModel> searchProfile1 = asSearchProfileService.getSearchProfileForCode(catalogVersion,
				CATEGORY_AWARE_SEARCH_PROFILE_1_CODE);
		final Optional<AbstractAsSearchProfileModel> searchProfile2 = asSearchProfileService.getSearchProfileForCode(catalogVersion,
				CATEGORY_AWARE_SEARCH_PROFILE_2_CODE);

		final AsMergeConfiguration mergeConfiguration1 = new AsMergeConfiguration();
		mergeConfiguration1.setFacetsMergeMode(AsFacetsMergeMode.ADD_AFTER);
		mergeConfiguration1.setBoostRulesMergeMode(AsBoostRulesMergeMode.ADD);
		mergeConfiguration1.setBoostItemsMergeMode(AsBoostItemsMergeMode.ADD_BEFORE);
		mergeConfiguration1.setResultFacetsMergeMode(AsFacetsMergeMode.ADD_BEFORE);
		mergeConfiguration1.setResultBoostRulesMergeMode(AsBoostRulesMergeMode.REPLACE);
		mergeConfiguration1.setResultBoostItemsMergeMode(AsBoostItemsMergeMode.ADD_AFTER);

		final AsMergeConfiguration mergeConfiguration2 = new AsMergeConfiguration();
		mergeConfiguration2.setFacetsMergeMode(AsFacetsMergeMode.ADD_BEFORE);
		mergeConfiguration2.setBoostRulesMergeMode(AsBoostRulesMergeMode.REPLACE);
		mergeConfiguration2.setBoostItemsMergeMode(AsBoostItemsMergeMode.ADD_AFTER);
		mergeConfiguration2.setResultFacetsMergeMode(AsFacetsMergeMode.ADD_AFTER);
		mergeConfiguration2.setResultBoostRulesMergeMode(AsBoostRulesMergeMode.ADD);
		mergeConfiguration2.setResultBoostItemsMergeMode(AsBoostItemsMergeMode.ADD_BEFORE);

		final AsSearchProfileActivationGroup group1 = new AsSearchProfileActivationGroup();
		group1.setSearchProfiles(Arrays.asList(searchProfile1.get(), searchProfile2.get()));
		group1.setMergeConfiguration(mergeConfiguration1);

		final AsSearchProfileActivationGroup group2 = new AsSearchProfileActivationGroup();
		group2.setSearchProfiles(Arrays.asList(searchProfile1.get(), searchProfile2.get()));
		group2.setMergeConfiguration(mergeConfiguration2);

		// when
		asCacheStrategy.clear();

		for (int i = 0; i < 10; i++)
		{
			asSearchProfileCalculationService.calculateGroups(context, Arrays.asList(group1, group2));
		}

		// then
		assertEquals(6, asCacheStrategy.getSize());
		assertEquals(6, asCacheStrategy.getMisses());
		// 94 = (((10 iterations for each group for each profile)20 * 4(caches))80 + (10 iterations for each group)20) - 6(the first hits)
		assertEquals(94, asCacheStrategy.getHits());
	}
}
