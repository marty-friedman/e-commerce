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
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.adaptivesearch.daos.AsSearchProfileActivationSetDao;
import de.hybris.platform.adaptivesearch.model.AsCategoryAwareSearchProfileModel;
import de.hybris.platform.adaptivesearch.model.AsSearchProfileActivationSetModel;
import de.hybris.platform.adaptivesearch.model.AsSimpleSearchProfileModel;
import de.hybris.platform.adaptivesearch.services.AsSearchProfileService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
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
public class AsSearchProfileCrudTest extends ServicelayerTransactionalTest
{
	private final static String CATALOG_ID = "hwcatalog";
	private final static String VERSION_STAGED = "Staged";
	private final static String VERSION_ONLINE = "Online";

	private final static String CODE1 = "searchProfile1";
	private final static String CODE2 = "searchProfile2";

	private final static String INDEX_TYPE_1 = "testIndex1";
	private final static String INDEX_TYPE_2 = "testIndex2";
	private final static String WRONG_INDEX_TYPE = "testIndexError";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Resource
	private ModelService modelService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private AsSearchProfileService asSearchProfileService;

	@Resource
	private AsSearchProfileActivationSetDao asSearchProfileActivationSetDao;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/adaptivesearch/test/integration/crud/asSearchProfileCrudTest.impex", CharEncoding.UTF_8);
	}

	@Test
	public void createSimpleSearchProfile() throws Exception
	{
		// given
		final CatalogVersionModel onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);

		final AsSimpleSearchProfileModel newSearchProfile = modelService.create(AsSimpleSearchProfileModel.class);
		newSearchProfile.setCatalogVersion(onlineCatalogVersion);
		newSearchProfile.setCode(CODE1);
		newSearchProfile.setIndexType(INDEX_TYPE_1);

		// when
		modelService.save(newSearchProfile);
		final Optional<AsSimpleSearchProfileModel> searchProfileResult = asSearchProfileService
				.getSearchProfileForCode(onlineCatalogVersion, CODE1);

		// then
		assertTrue(searchProfileResult.isPresent());
		final AsSimpleSearchProfileModel searchProfile = searchProfileResult.get();
		assertEquals(onlineCatalogVersion, searchProfile.getCatalogVersion());
		assertEquals(CODE1, searchProfile.getCode());
	}

	@Test
	public void failToCreateSimpleSearchProfileWithoutIndexType() throws Exception
	{
		// given
		final CatalogVersionModel onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);

		final AsSimpleSearchProfileModel newSearchProfile = modelService.create(AsSimpleSearchProfileModel.class);
		newSearchProfile.setCatalogVersion(onlineCatalogVersion);
		newSearchProfile.setCode(CODE1);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		modelService.save(newSearchProfile);
	}

	@Test
	public void failToCreateSimpleSearchProfileWithWrongIndexType() throws Exception
	{
		// given
		final CatalogVersionModel onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);

		final AsSimpleSearchProfileModel newSearchProfile = modelService.create(AsSimpleSearchProfileModel.class);
		newSearchProfile.setCatalogVersion(onlineCatalogVersion);
		newSearchProfile.setCode(CODE1);
		newSearchProfile.setIndexType(WRONG_INDEX_TYPE);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		modelService.save(newSearchProfile);
	}

	@Test
	public void createSimpleSearchProfileWithActivationSet() throws Exception
	{
		// given
		final CatalogVersionModel onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		final Optional<AsSearchProfileActivationSetModel> activationSet = asSearchProfileActivationSetDao
				.findSearchProfileActivationSetByIndexType(onlineCatalogVersion, INDEX_TYPE_1);

		final AsSimpleSearchProfileModel newSearchProfile = modelService.create(AsSimpleSearchProfileModel.class);
		newSearchProfile.setCatalogVersion(onlineCatalogVersion);
		newSearchProfile.setCode(CODE1);
		newSearchProfile.setIndexType(INDEX_TYPE_1);
		newSearchProfile.setActivationSet(activationSet.get());

		// when
		modelService.save(newSearchProfile);
		final Optional<AsSimpleSearchProfileModel> searchProfileResult = asSearchProfileService
				.getSearchProfileForCode(onlineCatalogVersion, CODE1);

		// then
		assertTrue(searchProfileResult.isPresent());
		final AsSimpleSearchProfileModel searchProfile = searchProfileResult.get();
		assertEquals(onlineCatalogVersion, searchProfile.getCatalogVersion());
		assertEquals(CODE1, searchProfile.getCode());
	}

	@Test
	public void failtToCreateSimpleSearchProfileWithActivationSetWrongIndexType() throws Exception
	{
		// given
		final CatalogVersionModel onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		final Optional<AsSearchProfileActivationSetModel> activationSet = asSearchProfileActivationSetDao
				.findSearchProfileActivationSetByIndexType(onlineCatalogVersion, INDEX_TYPE_2);

		final AsSimpleSearchProfileModel newSearchProfile = modelService.create(AsSimpleSearchProfileModel.class);
		newSearchProfile.setCatalogVersion(onlineCatalogVersion);
		newSearchProfile.setCode(CODE1);
		newSearchProfile.setIndexType(INDEX_TYPE_1);
		newSearchProfile.setActivationSet(activationSet.get());

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		modelService.save(newSearchProfile);
	}

	@Test
	public void failtToCreateSimpleSearchProfileWithActivationSetWrongCatalogVersion() throws Exception
	{
		// given
		final CatalogVersionModel stagedCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);
		final CatalogVersionModel onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		final Optional<AsSearchProfileActivationSetModel> activationSet = asSearchProfileActivationSetDao
				.findSearchProfileActivationSetByIndexType(stagedCatalogVersion, INDEX_TYPE_1);

		final AsSimpleSearchProfileModel newSearchProfile = modelService.create(AsSimpleSearchProfileModel.class);
		newSearchProfile.setCatalogVersion(onlineCatalogVersion);
		newSearchProfile.setCode(CODE1);
		newSearchProfile.setIndexType(INDEX_TYPE_1);
		newSearchProfile.setActivationSet(activationSet.get());

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		modelService.save(newSearchProfile);
	}

	@Test
	public void createMultipleSimpleSearchProfiles() throws Exception
	{
		// given
		final CatalogVersionModel onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);

		final AsSimpleSearchProfileModel newSearchProfile1 = modelService.create(AsSimpleSearchProfileModel.class);
		newSearchProfile1.setCatalogVersion(onlineCatalogVersion);
		newSearchProfile1.setCode(CODE1);
		newSearchProfile1.setIndexType(INDEX_TYPE_1);

		final AsSimpleSearchProfileModel newSearchProfile2 = modelService.create(AsSimpleSearchProfileModel.class);
		newSearchProfile2.setCatalogVersion(onlineCatalogVersion);
		newSearchProfile2.setCode(CODE2);
		newSearchProfile2.setIndexType(INDEX_TYPE_1);

		// when
		modelService.save(newSearchProfile1);
		modelService.save(newSearchProfile2);

		final Optional<AsSimpleSearchProfileModel> searchProfile1Result = asSearchProfileService
				.getSearchProfileForCode(onlineCatalogVersion, CODE1);
		final Optional<AsSimpleSearchProfileModel> searchProfile2Result = asSearchProfileService
				.getSearchProfileForCode(onlineCatalogVersion, CODE1);

		// then
		assertTrue(searchProfile1Result.isPresent());
		final AsSimpleSearchProfileModel searchProfile1 = searchProfile1Result.get();
		assertEquals(onlineCatalogVersion, searchProfile1.getCatalogVersion());
		assertEquals(CODE1, searchProfile1.getCode());

		assertTrue(searchProfile2Result.isPresent());
		final AsSimpleSearchProfileModel searchProfile2 = searchProfile2Result.get();
		assertEquals(onlineCatalogVersion, searchProfile2.getCatalogVersion());
		assertEquals(CODE1, searchProfile2.getCode());
	}

	@Test
	public void createCategoryAwareSearchProfile() throws Exception
	{
		// given
		final CatalogVersionModel onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);

		final AsCategoryAwareSearchProfileModel newSearchProfile = modelService.create(AsCategoryAwareSearchProfileModel.class);
		newSearchProfile.setCatalogVersion(onlineCatalogVersion);
		newSearchProfile.setCode(CODE1);
		newSearchProfile.setIndexType(INDEX_TYPE_1);

		// when
		modelService.save(newSearchProfile);

		final Optional<AsCategoryAwareSearchProfileModel> searchProfileResult = asSearchProfileService
				.getSearchProfileForCode(onlineCatalogVersion, CODE1);

		// then
		assertTrue(searchProfileResult.isPresent());
		final AsCategoryAwareSearchProfileModel searchProfile = searchProfileResult.get();
		assertEquals(onlineCatalogVersion, searchProfile.getCatalogVersion());
		assertEquals(CODE1, searchProfile.getCode());
	}

	@Test
	public void failToCreateCategoryAwareSearchProfileWithoutIndexType() throws Exception
	{
		// given
		final CatalogVersionModel onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);

		final AsCategoryAwareSearchProfileModel newSearchProfile = modelService.create(AsCategoryAwareSearchProfileModel.class);
		newSearchProfile.setCatalogVersion(onlineCatalogVersion);
		newSearchProfile.setCode(CODE1);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		modelService.save(newSearchProfile);
	}

	@Test
	public void failToCreateCategoryAwareSearchProfileWithWrongIndexType() throws Exception
	{
		// given
		final CatalogVersionModel onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);

		final AsCategoryAwareSearchProfileModel newSearchProfile = modelService.create(AsCategoryAwareSearchProfileModel.class);
		newSearchProfile.setCatalogVersion(onlineCatalogVersion);
		newSearchProfile.setCode(CODE1);
		newSearchProfile.setIndexType(WRONG_INDEX_TYPE);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		modelService.save(newSearchProfile);
	}

	@Test
	public void createCategoryAwareSearchProfileWithActivationSet() throws Exception
	{
		// given
		final CatalogVersionModel onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		final Optional<AsSearchProfileActivationSetModel> activationSet = asSearchProfileActivationSetDao
				.findSearchProfileActivationSetByIndexType(onlineCatalogVersion, INDEX_TYPE_1);

		final AsCategoryAwareSearchProfileModel newSearchProfile = modelService.create(AsCategoryAwareSearchProfileModel.class);
		newSearchProfile.setCatalogVersion(onlineCatalogVersion);
		newSearchProfile.setCode(CODE1);
		newSearchProfile.setIndexType(INDEX_TYPE_1);
		newSearchProfile.setActivationSet(activationSet.get());

		// when
		modelService.save(newSearchProfile);
		final Optional<AsCategoryAwareSearchProfileModel> searchProfileResult = asSearchProfileService
				.getSearchProfileForCode(onlineCatalogVersion, CODE1);

		// then
		assertTrue(searchProfileResult.isPresent());
		final AsCategoryAwareSearchProfileModel searchProfile = searchProfileResult.get();
		assertEquals(onlineCatalogVersion, searchProfile.getCatalogVersion());
		assertEquals(CODE1, searchProfile.getCode());
	}

	@Test
	public void failtToCreateCategoryAwareSearchProfileWithActivationSetWrongIndexType() throws Exception
	{
		// given
		final CatalogVersionModel onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		final Optional<AsSearchProfileActivationSetModel> activationSet = asSearchProfileActivationSetDao
				.findSearchProfileActivationSetByIndexType(onlineCatalogVersion, INDEX_TYPE_2);

		final AsCategoryAwareSearchProfileModel newSearchProfile = modelService.create(AsCategoryAwareSearchProfileModel.class);
		newSearchProfile.setCatalogVersion(onlineCatalogVersion);
		newSearchProfile.setCode(CODE1);
		newSearchProfile.setIndexType(INDEX_TYPE_1);
		newSearchProfile.setActivationSet(activationSet.get());

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		modelService.save(newSearchProfile);
	}

	@Test
	public void failtToCreateCategoryAwareSearchProfileWithActivationSetWrongCatalogVersion() throws Exception
	{
		// given
		final CatalogVersionModel stagedCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);
		final CatalogVersionModel onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		final Optional<AsSearchProfileActivationSetModel> activationSet = asSearchProfileActivationSetDao
				.findSearchProfileActivationSetByIndexType(stagedCatalogVersion, INDEX_TYPE_1);

		final AsCategoryAwareSearchProfileModel newSearchProfile = modelService.create(AsCategoryAwareSearchProfileModel.class);
		newSearchProfile.setCatalogVersion(onlineCatalogVersion);
		newSearchProfile.setCode(CODE1);
		newSearchProfile.setIndexType(INDEX_TYPE_1);
		newSearchProfile.setActivationSet(activationSet.get());

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		modelService.save(newSearchProfile);
	}

	@Test
	public void createMultipleCategoryAwareSearchProfiles() throws Exception
	{
		// given
		final CatalogVersionModel onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);

		final AsCategoryAwareSearchProfileModel newSearchProfile1 = modelService.create(AsCategoryAwareSearchProfileModel.class);
		newSearchProfile1.setCatalogVersion(onlineCatalogVersion);
		newSearchProfile1.setCode(CODE1);
		newSearchProfile1.setIndexType(INDEX_TYPE_1);

		final AsCategoryAwareSearchProfileModel newSearchProfile2 = modelService.create(AsCategoryAwareSearchProfileModel.class);
		newSearchProfile2.setCatalogVersion(onlineCatalogVersion);
		newSearchProfile2.setCode(CODE2);
		newSearchProfile2.setIndexType(INDEX_TYPE_1);

		// when
		modelService.save(newSearchProfile1);
		modelService.save(newSearchProfile2);

		final Optional<AsCategoryAwareSearchProfileModel> searchProfile1Result = asSearchProfileService
				.getSearchProfileForCode(onlineCatalogVersion, CODE1);
		final Optional<AsCategoryAwareSearchProfileModel> searchProfile2Result = asSearchProfileService
				.getSearchProfileForCode(onlineCatalogVersion, CODE1);

		// then
		assertTrue(searchProfile1Result.isPresent());
		final AsCategoryAwareSearchProfileModel searchProfile1 = searchProfile1Result.get();
		assertEquals(onlineCatalogVersion, searchProfile1.getCatalogVersion());
		assertEquals(CODE1, searchProfile1.getCode());

		assertTrue(searchProfile2Result.isPresent());
		final AsCategoryAwareSearchProfileModel searchProfile2 = searchProfile2Result.get();
		assertEquals(onlineCatalogVersion, searchProfile2.getCatalogVersion());
		assertEquals(CODE1, searchProfile2.getCode());
	}

	@Test
	public void createSearchProfilesWithSameCodeButDifferentCatalogVersions() throws Exception
	{
		// given
		final CatalogVersionModel stagedCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);
		final CatalogVersionModel onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);

		final AsSimpleSearchProfileModel newSearchProfile1 = modelService.create(AsSimpleSearchProfileModel.class);
		newSearchProfile1.setCatalogVersion(stagedCatalogVersion);
		newSearchProfile1.setCode(CODE1);
		newSearchProfile1.setIndexType(INDEX_TYPE_1);

		final AsCategoryAwareSearchProfileModel newSearchProfile2 = modelService.create(AsCategoryAwareSearchProfileModel.class);
		newSearchProfile2.setCatalogVersion(onlineCatalogVersion);
		newSearchProfile2.setCode(CODE1);
		newSearchProfile2.setIndexType(INDEX_TYPE_1);

		// when
		modelService.save(newSearchProfile1);
		modelService.save(newSearchProfile2);

		final Optional<AsSimpleSearchProfileModel> searchProfile1Result = asSearchProfileService
				.getSearchProfileForCode(stagedCatalogVersion, CODE1);
		final Optional<AsCategoryAwareSearchProfileModel> searchProfile2Result = asSearchProfileService
				.getSearchProfileForCode(onlineCatalogVersion, CODE1);

		// then
		assertTrue(searchProfile1Result.isPresent());
		final AsSimpleSearchProfileModel searchProfile1 = searchProfile1Result.get();
		assertEquals(stagedCatalogVersion, searchProfile1.getCatalogVersion());
		assertEquals(CODE1, searchProfile1.getCode());

		assertTrue(searchProfile2Result.isPresent());
		final AsCategoryAwareSearchProfileModel searchProfile2 = searchProfile2Result.get();
		assertEquals(onlineCatalogVersion, searchProfile2.getCatalogVersion());
		assertEquals(CODE1, searchProfile2.getCode());
	}

	@Test
	public void failToCreateSearchProfilesWithSameCodeSameCatalogVersion() throws Exception
	{
		// given
		final CatalogVersionModel stagedCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);

		final AsCategoryAwareSearchProfileModel newSearchProfile1 = modelService.create(AsCategoryAwareSearchProfileModel.class);
		newSearchProfile1.setCatalogVersion(stagedCatalogVersion);
		newSearchProfile1.setCode(CODE1);
		newSearchProfile1.setIndexType(INDEX_TYPE_1);

		final AsCategoryAwareSearchProfileModel newSearchProfile2 = modelService.create(AsCategoryAwareSearchProfileModel.class);
		newSearchProfile2.setCatalogVersion(stagedCatalogVersion);
		newSearchProfile2.setCode(CODE1);
		newSearchProfile2.setIndexType(INDEX_TYPE_1);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		modelService.save(newSearchProfile1);
		modelService.save(newSearchProfile2);
	}
}
