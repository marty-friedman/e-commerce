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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.adaptivesearch.data.AsRankChange;
import de.hybris.platform.adaptivesearch.data.AsRankChangeType;
import de.hybris.platform.adaptivesearch.model.AbstractAsConfigurableSearchConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AsSimpleSearchConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AsSortModel;
import de.hybris.platform.adaptivesearch.services.AsConfigurationService;
import de.hybris.platform.adaptivesearch.services.AsSearchConfigurationService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.apache.commons.lang.CharEncoding;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@IntegrationTest
public class AsSortConfigurationCrudTest extends ServicelayerTransactionalTest
{
	private final static String CATALOG_ID = "hwcatalog";
	private final static String VERSION_STAGED = "Staged";
	private final static String VERSION_ONLINE = "Online";

	private static final String SIMPLE_SEARCH_CONF_UID = "simpleConfiguration";

	private static final String UID1 = "d3299865-5a12-4985-bcde-0726f302b6f1";
	private static final String UID2 = "381c1991-65d5-4c60-bff5-c0761842d60d";

	private static final String SORT1_CODE = "sort1code";
	private static final String SORT1_NAME = "sort1name";

	private static final String SORT2_CODE = "sort2code";
	private static final String SORT2_NAME = "sort2name";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Resource
	private ModelService modelService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private AsSearchConfigurationService asSearchConfigurationService;

	@Resource
	private AsConfigurationService asConfigurationService;

	private CatalogVersionModel onlineCatalogVersion;
	private CatalogVersionModel stagedCatalogVersion;
	private AsSimpleSearchConfigurationModel searchConfiguration;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/adaptivesearch/test/integration/crud/asSortConfigurationCrudTest.impex", CharEncoding.UTF_8);

		onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		stagedCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);
		final Optional<AsSimpleSearchConfigurationModel> searchConfigurationOptional = asSearchConfigurationService
				.getSearchConfigurationForUid(onlineCatalogVersion, SIMPLE_SEARCH_CONF_UID);
		searchConfiguration = searchConfigurationOptional.get();
	}

	@Test
	public void getNonExistingSort() throws Exception
	{
		// when
		final Optional<AsSortModel> sortOptional = asConfigurationService.getConfigurationForUid(AsSortModel.class,
				onlineCatalogVersion, UID1);

		// then
		assertFalse(sortOptional.isPresent());
	}

	@Test
	public void createSortWithoutUid() throws Exception
	{
		// given
		final AsSortModel sort = asConfigurationService.createConfiguration(AsSortModel.class);
		sort.setCatalogVersion(onlineCatalogVersion);
		sort.setSearchConfiguration(searchConfiguration);
		sort.setCode(SORT1_CODE);
		sort.setName(SORT1_NAME);

		// when
		asConfigurationService.saveConfiguration(sort);

		// then
		assertNotNull(sort.getUid());
		assertFalse(sort.getUid().isEmpty());
	}

	@Test
	public void createSort() throws Exception
	{
		// given
		final AsSortModel sort = asConfigurationService.createConfiguration(AsSortModel.class);
		sort.setCatalogVersion(onlineCatalogVersion);
		sort.setUid(UID1);
		sort.setSearchConfiguration(searchConfiguration);
		sort.setCode(SORT1_CODE);
		sort.setName(SORT1_NAME);

		// when
		asConfigurationService.saveConfiguration(sort);

		final Optional<AsSortModel> createdSortOptional = asConfigurationService.getConfigurationForUid(AsSortModel.class,
				onlineCatalogVersion, UID1);

		// then
		assertTrue(createdSortOptional.isPresent());

		final AsSortModel createdSort = createdSortOptional.get();
		assertEquals(onlineCatalogVersion, createdSort.getCatalogVersion());
		assertEquals(UID1, createdSort.getUid());
		assertEquals(searchConfiguration, createdSort.getSearchConfiguration());
		assertEquals(SORT1_CODE, createdSort.getCode());
		assertEquals(SORT1_NAME, createdSort.getName());
	}

	@Test
	public void failToCreateSortWithWrongCatalogVersion() throws Exception
	{
		// given
		final AsSortModel sort = asConfigurationService.createConfiguration(AsSortModel.class);
		sort.setCatalogVersion(stagedCatalogVersion);
		sort.setSearchConfiguration(searchConfiguration);
		sort.setCode(SORT1_CODE);
		sort.setName(SORT1_NAME);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(sort);
	}

	@Test
	public void failToCreateSortWithoutSearchConfiguration() throws Exception
	{
		// given
		final AsSortModel sort = asConfigurationService.createConfiguration(AsSortModel.class);
		sort.setCatalogVersion(onlineCatalogVersion);
		sort.setCode(SORT1_CODE);
		sort.setName(SORT1_NAME);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(sort);
	}

	@Test
	public void updateSort() throws Exception
	{
		// given
		final AsSortModel sort = asConfigurationService.createConfiguration(AsSortModel.class);
		sort.setCatalogVersion(onlineCatalogVersion);
		sort.setUid(UID1);
		sort.setSearchConfiguration(searchConfiguration);
		sort.setCode(SORT1_CODE);
		sort.setName(SORT1_NAME);

		// when
		asConfigurationService.saveConfiguration(sort);

		final Optional<AsSortModel> createdSortOptional = asConfigurationService.getConfigurationForUid(AsSortModel.class,
				onlineCatalogVersion, UID1);

		final AsSortModel createdSort = createdSortOptional.get();
		createdSort.setCode(SORT2_CODE);
		createdSort.setName(SORT2_NAME);
		asConfigurationService.saveConfiguration(createdSort);

		final Optional<AsSortModel> updatedSortOptional = asConfigurationService.getConfigurationForUid(AsSortModel.class,
				onlineCatalogVersion, UID1);

		// then
		assertTrue(updatedSortOptional.isPresent());

		final AsSortModel updatedSort = updatedSortOptional.get();
		assertEquals(onlineCatalogVersion, updatedSort.getCatalogVersion());
		assertEquals(UID1, updatedSort.getUid());
		assertEquals(searchConfiguration, updatedSort.getSearchConfiguration());
		assertEquals(SORT2_CODE, updatedSort.getCode());
		assertEquals(SORT2_NAME, updatedSort.getName());
	}

	@Test
	public void failToUpdateSortWithExistingCode() throws Exception
	{
		// given
		final AsSortModel sort1 = asConfigurationService.createConfiguration(AsSortModel.class);
		sort1.setCatalogVersion(onlineCatalogVersion);
		sort1.setUid(UID1);
		sort1.setSearchConfiguration(searchConfiguration);
		sort1.setCode(SORT1_CODE);
		sort1.setName(SORT1_NAME);

		final AsSortModel sort2 = asConfigurationService.createConfiguration(AsSortModel.class);
		sort2.setCatalogVersion(onlineCatalogVersion);
		sort2.setUid(UID2);
		sort2.setSearchConfiguration(searchConfiguration);
		sort2.setCode(SORT2_CODE);
		sort2.setName(SORT2_NAME);

		// when
		asConfigurationService.saveConfiguration(sort1);
		asConfigurationService.saveConfiguration(sort2);

		final Optional<AsSortModel> createdSort2Optional = asConfigurationService.getConfigurationForUid(AsSortModel.class,
				onlineCatalogVersion, UID2);

		final AsSortModel createdSort = createdSort2Optional.get();
		createdSort.setCode(SORT1_CODE);


		// expect
		expectedException.expect(ModelSavingException.class);

		//when
		asConfigurationService.saveConfiguration(createdSort);
	}

	@Test
	public void removeSort() throws Exception
	{
		// given
		final AsSortModel sort = asConfigurationService.createConfiguration(AsSortModel.class);
		sort.setCatalogVersion(onlineCatalogVersion);
		sort.setUid(UID1);
		sort.setSearchConfiguration(searchConfiguration);
		sort.setCode(SORT1_CODE);
		sort.setName(SORT1_NAME);

		// when
		asConfigurationService.saveConfiguration(sort);

		final Optional<AsSortModel> createdSortOptional = asConfigurationService.getConfigurationForUid(AsSortModel.class,
				onlineCatalogVersion, UID1);

		final AsSortModel createdSort = createdSortOptional.get();
		asConfigurationService.removeConfiguration(createdSort);

		final Optional<AsSortModel> removedSortOptional = asConfigurationService.getConfigurationForUid(AsSortModel.class,
				onlineCatalogVersion, UID1);

		// then
		assertFalse(removedSortOptional.isPresent());
	}

	@Test
	public void createMultipleSorts() throws Exception
	{
		// given
		final AsSortModel sort1 = asConfigurationService.createConfiguration(AsSortModel.class);
		sort1.setCatalogVersion(onlineCatalogVersion);
		sort1.setUid(UID1);
		sort1.setSearchConfiguration(searchConfiguration);
		sort1.setCode(SORT1_CODE);
		sort1.setName(SORT1_NAME);

		final AsSortModel sort2 = asConfigurationService.createConfiguration(AsSortModel.class);
		sort2.setCatalogVersion(onlineCatalogVersion);
		sort2.setUid(UID2);
		sort2.setSearchConfiguration(searchConfiguration);
		sort2.setCode(SORT2_CODE);
		sort2.setName(SORT2_NAME);

		// when
		asConfigurationService.saveConfiguration(sort1);
		asConfigurationService.saveConfiguration(sort2);

		modelService.refresh(searchConfiguration);

		// then
		assertThat(searchConfiguration.getSorts()).containsExactly(sort1, sort2);
	}

	@Test
	public void rankAfterSort() throws Exception
	{
		// given
		final AsSortModel sort1 = asConfigurationService.createConfiguration(AsSortModel.class);
		sort1.setCatalogVersion(onlineCatalogVersion);
		sort1.setUid(UID1);
		sort1.setSearchConfiguration(searchConfiguration);
		sort1.setCode(SORT1_CODE);
		sort1.setName(SORT1_NAME);

		final AsSortModel sort2 = asConfigurationService.createConfiguration(AsSortModel.class);
		sort2.setCatalogVersion(onlineCatalogVersion);
		sort2.setUid(UID2);
		sort2.setSearchConfiguration(searchConfiguration);
		sort2.setCode(SORT2_CODE);
		sort2.setName(SORT2_NAME);

		// when
		asConfigurationService.saveConfiguration(sort1);
		asConfigurationService.saveConfiguration(sort2);

		modelService.refresh(searchConfiguration);

		final List<AsRankChange> rankChanges = asConfigurationService.rankAfterConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.SORTS, UID2, UID1);

		// then
		assertThat(rankChanges).hasSize(1);

		final AsRankChange rankChange = rankChanges.get(0);
		assertEquals(AsRankChangeType.MOVE, rankChange.getType());
		assertEquals(UID1, rankChange.getUid());
		assertEquals(Integer.valueOf(0), rankChange.getOldRank());
		assertEquals(Integer.valueOf(1), rankChange.getNewRank());

		assertThat(searchConfiguration.getSorts()).containsExactly(sort2, sort1);
	}

	@Test
	public void rankBeforeSort() throws Exception
	{
		// given
		final AsSortModel sort1 = asConfigurationService.createConfiguration(AsSortModel.class);
		sort1.setCatalogVersion(onlineCatalogVersion);
		sort1.setUid(UID1);
		sort1.setSearchConfiguration(searchConfiguration);
		sort1.setCode(SORT1_CODE);
		sort1.setName(SORT1_NAME);

		final AsSortModel sort2 = asConfigurationService.createConfiguration(AsSortModel.class);
		sort2.setCatalogVersion(onlineCatalogVersion);
		sort2.setUid(UID2);
		sort2.setSearchConfiguration(searchConfiguration);
		sort2.setCode(SORT2_CODE);
		sort2.setName(SORT2_NAME);

		// when
		asConfigurationService.saveConfiguration(sort1);
		asConfigurationService.saveConfiguration(sort2);

		modelService.refresh(searchConfiguration);

		final List<AsRankChange> rankChanges = asConfigurationService.rankBeforeConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.SORTS, UID1, UID2);

		// then
		assertThat(rankChanges).hasSize(1);

		final AsRankChange rankChange = rankChanges.get(0);
		assertEquals(AsRankChangeType.MOVE, rankChange.getType());
		assertEquals(UID2, rankChange.getUid());
		assertEquals(Integer.valueOf(1), rankChange.getOldRank());
		assertEquals(Integer.valueOf(0), rankChange.getNewRank());

		assertThat(searchConfiguration.getSorts()).containsExactly(sort2, sort1);
	}

	@Test
	public void failToCreateMultipleSortConfigurationsWithSameUid() throws Exception
	{
		// given
		final AsSortModel sort1 = asConfigurationService.createConfiguration(AsSortModel.class);
		sort1.setCatalogVersion(onlineCatalogVersion);
		sort1.setUid(UID1);
		sort1.setSearchConfiguration(searchConfiguration);
		sort1.setCode(SORT1_CODE);
		sort1.setName(SORT1_NAME);

		final AsSortModel sort2 = asConfigurationService.createConfiguration(AsSortModel.class);
		sort2.setCatalogVersion(onlineCatalogVersion);
		sort2.setUid(UID1);
		sort2.setSearchConfiguration(searchConfiguration);
		sort2.setCode(SORT2_CODE);
		sort2.setName(SORT2_NAME);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(sort1);
		asConfigurationService.saveConfiguration(sort2);
	}

	@Test
	public void failToCreateMultipleSortConfigurationsWithSameCode() throws Exception
	{
		// given
		final AsSortModel sort1 = asConfigurationService.createConfiguration(AsSortModel.class);
		sort1.setCatalogVersion(onlineCatalogVersion);
		sort1.setUid(UID1);
		sort1.setSearchConfiguration(searchConfiguration);
		sort1.setCode(SORT1_CODE);
		sort1.setName(SORT1_NAME);

		final AsSortModel sort2 = asConfigurationService.createConfiguration(AsSortModel.class);
		sort2.setCatalogVersion(onlineCatalogVersion);
		sort2.setUid(UID2);
		sort2.setSearchConfiguration(searchConfiguration);
		sort2.setCode(SORT1_CODE);
		sort2.setName(SORT2_NAME);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(sort1);
		asConfigurationService.saveConfiguration(sort2);
	}
}
