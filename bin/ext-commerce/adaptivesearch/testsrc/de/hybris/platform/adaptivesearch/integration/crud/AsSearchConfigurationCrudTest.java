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
package de.hybris.platform.adaptivesearch.integration.crud;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.adaptivesearch.model.AsCategoryAwareSearchConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AsCategoryAwareSearchProfileModel;
import de.hybris.platform.adaptivesearch.model.AsSimpleSearchConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AsSimpleSearchProfileModel;
import de.hybris.platform.adaptivesearch.services.AsSearchConfigurationService;
import de.hybris.platform.adaptivesearch.services.AsSearchProfileService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Optional;

import javax.annotation.Resource;

import org.apache.commons.lang.CharEncoding;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@IntegrationTest
public class AsSearchConfigurationCrudTest extends ServicelayerTest
{
	private final static String CATALOG_ID = "hwcatalog";
	private final static String VERSION_STAGED = "Staged";
	private final static String VERSION_ONLINE = "Online";

	private final static String SIMPLE_SEARCH_PROFILE_CODE = "simpleProfile";
	private final static String CAT_AWARE_SEARCH_PROFILE_CODE = "categoryAwareProfile";

	private final static String UID1 = "b413b620-a4b8-4b3c-9234-7fc88c6d3eb1";
	private final static String UID2 = "016090d9-e5d7-4c1f-ad9e-6fb96e36d0c0";

	private final static String CAT_10_CODE = "cat10";
	private final static String CAT_20_CODE = "cat20";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Resource
	private ModelService modelService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private CategoryService categoryService;

	@Resource
	private AsSearchProfileService asSearchProfileService;

	@Resource
	private AsSearchConfigurationService asSearchConfigurationService;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/adaptivesearch/test/integration/crud/asSearchConfigurationCrudTest.impex", CharEncoding.UTF_8);
	}

	@Test
	public void createSimpleSearchConfigurationWithoutUid() throws Exception
	{
		// given
		final CatalogVersionModel onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		final Optional<AsSimpleSearchProfileModel> searchProfileResult = asSearchProfileService
				.getSearchProfileForCode(onlineCatalogVersion, SIMPLE_SEARCH_PROFILE_CODE);

		final AsSimpleSearchConfigurationModel newSearchConfiguration = modelService.create(AsSimpleSearchConfigurationModel.class);
		newSearchConfiguration.setCatalogVersion(onlineCatalogVersion);
		newSearchConfiguration.setSearchProfile(searchProfileResult.get());

		// when
		modelService.save(newSearchConfiguration);

		// then
		assertNotNull(newSearchConfiguration.getUid());
		assertFalse(newSearchConfiguration.getUid().isEmpty());
	}

	@Test
	public void createSimpleSearchConfiguration() throws Exception
	{
		// given
		final CatalogVersionModel onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		final Optional<AsSimpleSearchProfileModel> searchProfileResult = asSearchProfileService
				.getSearchProfileForCode(onlineCatalogVersion, SIMPLE_SEARCH_PROFILE_CODE);

		final AsSimpleSearchConfigurationModel newSearchConfiguration = modelService.create(AsSimpleSearchConfigurationModel.class);
		newSearchConfiguration.setCatalogVersion(onlineCatalogVersion);
		newSearchConfiguration.setSearchProfile(searchProfileResult.get());
		newSearchConfiguration.setUid(UID1);

		// when
		modelService.save(newSearchConfiguration);
		final Optional<AsSimpleSearchConfigurationModel> searchConfigurationResult = asSearchConfigurationService
				.getSearchConfigurationForUid(onlineCatalogVersion, UID1);

		// then
		assertTrue(searchConfigurationResult.isPresent());

		final AsSimpleSearchConfigurationModel searchConfiguration = searchConfigurationResult.get();
		assertEquals(onlineCatalogVersion, searchConfiguration.getCatalogVersion());
		assertEquals(searchProfileResult.get(), searchConfiguration.getSearchProfile());
		assertEquals(UID1, searchConfiguration.getUid());
	}

	@Test
	public void failToCreateSimpleSearchConfigurationWithoutSearchProfile() throws Exception
	{
		// given
		final CatalogVersionModel onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		final AsSimpleSearchConfigurationModel newSearchConfiguration = modelService.create(AsSimpleSearchConfigurationModel.class);
		newSearchConfiguration.setCatalogVersion(onlineCatalogVersion);
		newSearchConfiguration.setUid(UID1);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		modelService.save(newSearchConfiguration);
	}

	@Test
	public void failToCreateSimpleSearchConfigurationWithWrongCatalogVersion() throws Exception
	{
		// given
		final CatalogVersionModel stagedCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);
		final CatalogVersionModel onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		final Optional<AsSimpleSearchProfileModel> searchProfileResult = asSearchProfileService
				.getSearchProfileForCode(onlineCatalogVersion, SIMPLE_SEARCH_PROFILE_CODE);

		final AsSimpleSearchConfigurationModel newSearchConfiguration = modelService.create(AsSimpleSearchConfigurationModel.class);
		newSearchConfiguration.setCatalogVersion(stagedCatalogVersion);
		newSearchConfiguration.setUid(UID1);
		newSearchConfiguration.setSearchProfile(searchProfileResult.get());

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		modelService.save(newSearchConfiguration);
	}


	@Test
	public void failToCreateMultipleSimpleSearchConfigurations() throws Exception
	{
		// given
		final CatalogVersionModel onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		final Optional<AsSimpleSearchProfileModel> searchProfileResult = asSearchProfileService
				.getSearchProfileForCode(onlineCatalogVersion, SIMPLE_SEARCH_PROFILE_CODE);

		final AsSimpleSearchConfigurationModel newSearchConfiguration1 = modelService
				.create(AsSimpleSearchConfigurationModel.class);
		newSearchConfiguration1.setCatalogVersion(onlineCatalogVersion);
		newSearchConfiguration1.setSearchProfile(searchProfileResult.get());
		newSearchConfiguration1.setUid(UID1);

		final AsSimpleSearchConfigurationModel newSearchConfiguration2 = modelService
				.create(AsSimpleSearchConfigurationModel.class);
		newSearchConfiguration2.setCatalogVersion(onlineCatalogVersion);
		newSearchConfiguration2.setSearchProfile(searchProfileResult.get());
		newSearchConfiguration2.setUid(UID2);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		modelService.save(newSearchConfiguration1);
		modelService.save(newSearchConfiguration2);
	}

	@Test
	public void createCategoryAwareSearchConfigurationWithoutUid() throws Exception
	{
		// given
		final CatalogVersionModel onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		final Optional<AsCategoryAwareSearchProfileModel> searchProfileResult = asSearchProfileService
				.getSearchProfileForCode(onlineCatalogVersion, CAT_AWARE_SEARCH_PROFILE_CODE);

		final AsCategoryAwareSearchConfigurationModel newSearchConfiguration = modelService
				.create(AsCategoryAwareSearchConfigurationModel.class);
		newSearchConfiguration.setCatalogVersion(onlineCatalogVersion);
		newSearchConfiguration.setSearchProfile(searchProfileResult.get());
		newSearchConfiguration.setUid(UID1);

		// when
		modelService.save(newSearchConfiguration);

		// then
		assertNotNull(newSearchConfiguration.getUid());
		assertFalse(newSearchConfiguration.getUid().isEmpty());
	}

	@Test
	public void createCategoryAwareSearchConfiguration() throws Exception
	{
		// given
		final CatalogVersionModel onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		final Optional<AsCategoryAwareSearchProfileModel> searchProfileResult = asSearchProfileService
				.getSearchProfileForCode(onlineCatalogVersion, CAT_AWARE_SEARCH_PROFILE_CODE);

		final AsCategoryAwareSearchConfigurationModel newSearchConfiguration = modelService
				.create(AsCategoryAwareSearchConfigurationModel.class);
		newSearchConfiguration.setCatalogVersion(onlineCatalogVersion);
		newSearchConfiguration.setSearchProfile(searchProfileResult.get());
		newSearchConfiguration.setUid(UID1);

		// when
		modelService.save(newSearchConfiguration);
		final Optional<AsCategoryAwareSearchConfigurationModel> searchConfigurationResult = asSearchConfigurationService
				.getSearchConfigurationForUid(onlineCatalogVersion, UID1);

		// then
		assertTrue(searchConfigurationResult.isPresent());

		final AsCategoryAwareSearchConfigurationModel searchConfiguration = searchConfigurationResult.get();
		assertEquals(onlineCatalogVersion, searchConfiguration.getCatalogVersion());
		assertEquals(searchProfileResult.get(), searchConfiguration.getSearchProfile());
		assertEquals(UID1, searchConfiguration.getUid());
	}

	@Test
	public void failToCreateCategoryAwareSearchConfigurationWithoutSearchProfile() throws Exception
	{
		// given
		final CatalogVersionModel onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		final AsCategoryAwareSearchConfigurationModel newSearchConfiguration = modelService
				.create(AsCategoryAwareSearchConfigurationModel.class);
		newSearchConfiguration.setCatalogVersion(onlineCatalogVersion);
		newSearchConfiguration.setUid(UID1);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		modelService.save(newSearchConfiguration);
	}

	@Test
	public void failToCreateCategoryAwareSearchConfigurationWithWrongCatalogVersion() throws Exception
	{
		// given
		final CatalogVersionModel stagedCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);
		final CatalogVersionModel onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		final Optional<AsCategoryAwareSearchProfileModel> searchProfileResult = asSearchProfileService
				.getSearchProfileForCode(onlineCatalogVersion, CAT_AWARE_SEARCH_PROFILE_CODE);

		final AsCategoryAwareSearchConfigurationModel newSearchConfiguration = modelService
				.create(AsCategoryAwareSearchConfigurationModel.class);
		newSearchConfiguration.setCatalogVersion(stagedCatalogVersion);
		newSearchConfiguration.setSearchProfile(searchProfileResult.get());
		newSearchConfiguration.setUid(UID1);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		modelService.save(newSearchConfiguration);
	}

	@Test
	public void createMultipleCategoryAwareSearchConfigurations() throws Exception
	{
		// given
		final CatalogVersionModel onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		final Optional<AsCategoryAwareSearchProfileModel> searchProfileResult = asSearchProfileService
				.getSearchProfileForCode(onlineCatalogVersion, CAT_AWARE_SEARCH_PROFILE_CODE);
		final CategoryModel category1 = categoryService.getCategoryForCode(onlineCatalogVersion, CAT_10_CODE);
		final CategoryModel category2 = categoryService.getCategoryForCode(onlineCatalogVersion, CAT_20_CODE);

		final AsCategoryAwareSearchConfigurationModel newSearchConfiguration1 = modelService
				.create(AsCategoryAwareSearchConfigurationModel.class);
		newSearchConfiguration1.setCatalogVersion(onlineCatalogVersion);
		newSearchConfiguration1.setSearchProfile(searchProfileResult.get());
		newSearchConfiguration1.setUid(UID1);
		newSearchConfiguration1.setCategory(category1);

		final AsCategoryAwareSearchConfigurationModel newSearchConfiguration2 = modelService
				.create(AsCategoryAwareSearchConfigurationModel.class);
		newSearchConfiguration2.setCatalogVersion(onlineCatalogVersion);
		newSearchConfiguration2.setSearchProfile(searchProfileResult.get());
		newSearchConfiguration2.setUid(UID2);
		newSearchConfiguration2.setCategory(category2);

		// when
		modelService.save(newSearchConfiguration1);
		modelService.save(newSearchConfiguration2);

		final Optional<AsCategoryAwareSearchConfigurationModel> searchConfigurationResult1 = asSearchConfigurationService
				.getSearchConfigurationForUid(onlineCatalogVersion, UID1);
		final Optional<AsCategoryAwareSearchConfigurationModel> searchConfigurationResult2 = asSearchConfigurationService
				.getSearchConfigurationForUid(onlineCatalogVersion, UID2);

		// then
		assertTrue(searchConfigurationResult1.isPresent());

		final AsCategoryAwareSearchConfigurationModel searchConfiguration1 = searchConfigurationResult1.get();
		assertEquals(onlineCatalogVersion, searchConfiguration1.getCatalogVersion());
		assertEquals(UID1, searchConfiguration1.getUid());

		assertTrue(searchConfigurationResult2.isPresent());

		final AsCategoryAwareSearchConfigurationModel searchConfiguration2 = searchConfigurationResult2.get();
		assertEquals(onlineCatalogVersion, searchConfiguration2.getCatalogVersion());
		assertEquals(UID2, searchConfiguration2.getUid());
	}

	@Test
	public void failToCreateCategoryAwareSearchConfigurationsWithSameCategory() throws Exception
	{
		// given
		final CatalogVersionModel onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		final Optional<AsCategoryAwareSearchProfileModel> searchProfileResult = asSearchProfileService
				.getSearchProfileForCode(onlineCatalogVersion, CAT_AWARE_SEARCH_PROFILE_CODE);

		final AsCategoryAwareSearchConfigurationModel searchConfiguration1 = modelService
				.create(AsCategoryAwareSearchConfigurationModel.class);
		searchConfiguration1.setCatalogVersion(onlineCatalogVersion);
		searchConfiguration1.setSearchProfile(searchProfileResult.get());
		searchConfiguration1.setUid(UID1);
		searchConfiguration1.setCategory(null);

		final AsCategoryAwareSearchConfigurationModel searchConfiguration2 = modelService
				.create(AsCategoryAwareSearchConfigurationModel.class);
		searchConfiguration2.setCatalogVersion(onlineCatalogVersion);
		searchConfiguration2.setSearchProfile(searchProfileResult.get());
		searchConfiguration2.setUid(UID2);
		searchConfiguration2.setCategory(null);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		modelService.save(searchConfiguration1);
		modelService.save(searchConfiguration2);
	}

	@Test
	public void createSearchConfigurationsWithSameUidButDifferentCatalogVersions() throws Exception
	{
		// given
		final CatalogVersionModel stagedCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);
		final CatalogVersionModel onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		final Optional<AsSimpleSearchProfileModel> searchProfile1Result = asSearchProfileService
				.getSearchProfileForCode(stagedCatalogVersion, SIMPLE_SEARCH_PROFILE_CODE);
		final Optional<AsCategoryAwareSearchProfileModel> searchProfile2Result = asSearchProfileService
				.getSearchProfileForCode(onlineCatalogVersion, CAT_AWARE_SEARCH_PROFILE_CODE);

		final AsSimpleSearchConfigurationModel newSearchConfiguration1 = modelService
				.create(AsSimpleSearchConfigurationModel.class);
		newSearchConfiguration1.setCatalogVersion(stagedCatalogVersion);
		newSearchConfiguration1.setSearchProfile(searchProfile1Result.get());
		newSearchConfiguration1.setUid(UID1);

		final AsCategoryAwareSearchConfigurationModel newSearchConfiguration2 = modelService
				.create(AsCategoryAwareSearchConfigurationModel.class);
		newSearchConfiguration2.setCatalogVersion(onlineCatalogVersion);
		newSearchConfiguration2.setSearchProfile(searchProfile2Result.get());
		newSearchConfiguration2.setUid(UID1);

		// when
		modelService.save(newSearchConfiguration1);
		modelService.save(newSearchConfiguration2);

		final Optional<AsSimpleSearchConfigurationModel> searchConfigurationResult1 = asSearchConfigurationService
				.getSearchConfigurationForUid(stagedCatalogVersion, UID1);
		final Optional<AsCategoryAwareSearchConfigurationModel> searchConfigurationResult2 = asSearchConfigurationService
				.getSearchConfigurationForUid(onlineCatalogVersion, UID1);

		// then
		assertTrue(searchConfigurationResult1.isPresent());

		final AsSimpleSearchConfigurationModel searchConfiguration1 = searchConfigurationResult1.get();
		assertEquals(stagedCatalogVersion, searchConfiguration1.getCatalogVersion());
		assertEquals(searchProfile1Result.get(), searchConfiguration1.getSearchProfile());
		assertEquals(UID1, searchConfiguration1.getUid());

		assertTrue(searchConfigurationResult2.isPresent());

		final AsCategoryAwareSearchConfigurationModel searchConfiguration2 = searchConfigurationResult2.get();
		assertEquals(onlineCatalogVersion, searchConfiguration2.getCatalogVersion());
		assertEquals(searchProfile2Result.get(), searchConfiguration2.getSearchProfile());
		assertEquals(UID1, searchConfiguration2.getUid());
	}

	@Test
	public void failToCreateSearchConfigurationsWithSameUidSameCatalogVersion() throws Exception
	{
		// given
		final CatalogVersionModel onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		final Optional<AsSimpleSearchProfileModel> searchProfile1Result = asSearchProfileService
				.getSearchProfileForCode(onlineCatalogVersion, SIMPLE_SEARCH_PROFILE_CODE);
		final Optional<AsCategoryAwareSearchProfileModel> searchProfile2Result = asSearchProfileService
				.getSearchProfileForCode(onlineCatalogVersion, CAT_AWARE_SEARCH_PROFILE_CODE);

		final AsSimpleSearchConfigurationModel searchConfiguration1 = modelService.create(AsSimpleSearchConfigurationModel.class);
		searchConfiguration1.setCatalogVersion(onlineCatalogVersion);
		searchConfiguration1.setSearchProfile(searchProfile1Result.get());
		searchConfiguration1.setUid(UID1);

		final AsCategoryAwareSearchConfigurationModel searchConfiguration2 = modelService
				.create(AsCategoryAwareSearchConfigurationModel.class);
		searchConfiguration2.setCatalogVersion(onlineCatalogVersion);
		searchConfiguration2.setSearchProfile(searchProfile2Result.get());
		searchConfiguration2.setUid(UID1);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		modelService.save(searchConfiguration1);
		modelService.save(searchConfiguration2);
	}
}
