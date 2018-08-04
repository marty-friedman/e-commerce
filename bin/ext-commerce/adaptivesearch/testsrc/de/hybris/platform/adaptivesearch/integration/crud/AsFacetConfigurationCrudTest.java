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
import de.hybris.platform.adaptivesearch.enums.AsFacetType;
import de.hybris.platform.adaptivesearch.model.AbstractAsConfigurableSearchConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AsExcludedFacetModel;
import de.hybris.platform.adaptivesearch.model.AsFacetModel;
import de.hybris.platform.adaptivesearch.model.AsPromotedFacetModel;
import de.hybris.platform.adaptivesearch.model.AsSimpleSearchConfigurationModel;
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
public class AsFacetConfigurationCrudTest extends ServicelayerTransactionalTest
{
	private final static String CATALOG_ID = "hwcatalog";
	private final static String VERSION_STAGED = "Staged";
	private final static String VERSION_ONLINE = "Online";

	private static final String SIMPLE_SEARCH_CONF_UID = "simpleConfiguration";

	private static final String UID1 = "e81de964-b6b8-4031-bf1a-2eeb99b606ac";
	private static final String UID2 = "e3780f3f-5e60-4174-b85d-52c84b34ee38";

	private static final String INDEX_PROPERTY1 = "property1";
	private static final String INDEX_PROPERTY2 = "property2";
	private static final String INDEX_PROPERTY3 = "property3";
	private static final String INDEX_PROPERTY4 = "property4";
	private static final String WRONG_INDEX_PROPERTY = "testPropertyError";

	private static final Integer PRIORITY1 = Integer.valueOf(1);
	private static final Integer PRIORITY2 = Integer.valueOf(2);

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
		importCsv("/adaptivesearch/test/integration/crud/asFacetConfigurationCrudTest.impex", CharEncoding.UTF_8);

		onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		stagedCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);
		final Optional<AsSimpleSearchConfigurationModel> searchConfigurationOptional = asSearchConfigurationService
				.getSearchConfigurationForUid(onlineCatalogVersion, SIMPLE_SEARCH_CONF_UID);
		searchConfiguration = searchConfigurationOptional.get();
	}

	@Test
	public void getNonExistingPromotedFacet() throws Exception
	{
		// when
		final Optional<AsPromotedFacetModel> promotedFacetOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedFacetModel.class, onlineCatalogVersion, UID1);

		// then
		assertFalse(promotedFacetOptional.isPresent());
	}

	@Test
	public void createPromotedFacetWithoutUid() throws Exception
	{
		// given
		final AsPromotedFacetModel promotedFacet = asConfigurationService.createConfiguration(AsPromotedFacetModel.class);
		promotedFacet.setCatalogVersion(onlineCatalogVersion);
		promotedFacet.setSearchConfiguration(searchConfiguration);
		promotedFacet.setIndexProperty(INDEX_PROPERTY1);
		promotedFacet.setFacetType(AsFacetType.REFINE);

		// when
		asConfigurationService.saveConfiguration(promotedFacet);

		// then
		assertNotNull(promotedFacet.getUid());
		assertFalse(promotedFacet.getUid().isEmpty());
	}

	@Test
	public void createPromotedFacet() throws Exception
	{
		// given
		final AsPromotedFacetModel promotedFacet = asConfigurationService.createConfiguration(AsPromotedFacetModel.class);
		promotedFacet.setCatalogVersion(onlineCatalogVersion);
		promotedFacet.setUid(UID1);
		promotedFacet.setSearchConfiguration(searchConfiguration);
		promotedFacet.setIndexProperty(INDEX_PROPERTY1);
		promotedFacet.setFacetType(AsFacetType.REFINE);

		// when
		asConfigurationService.saveConfiguration(promotedFacet);

		final Optional<AsPromotedFacetModel> createdPromotedFacetOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedFacetModel.class, onlineCatalogVersion, UID1);

		// then
		assertTrue(createdPromotedFacetOptional.isPresent());

		final AsPromotedFacetModel createdPromotedFacet = createdPromotedFacetOptional.get();
		assertEquals(onlineCatalogVersion, createdPromotedFacet.getCatalogVersion());
		assertEquals(UID1, createdPromotedFacet.getUid());
		assertEquals(searchConfiguration, createdPromotedFacet.getSearchConfiguration());
		assertEquals(INDEX_PROPERTY1, createdPromotedFacet.getIndexProperty());
		assertEquals(AsFacetType.REFINE, createdPromotedFacet.getFacetType());
	}

	@Test
	public void failToCreatePromotedFacetWithWrongCatalogVersion() throws Exception
	{
		// given
		final AsPromotedFacetModel promotedFacet = asConfigurationService.createConfiguration(AsPromotedFacetModel.class);
		promotedFacet.setCatalogVersion(stagedCatalogVersion);
		promotedFacet.setSearchConfiguration(searchConfiguration);
		promotedFacet.setIndexProperty(INDEX_PROPERTY1);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(promotedFacet);
	}

	@Test
	public void failToCreatePromotedFacetWithoutIndexProperty() throws Exception
	{
		// given
		final AsPromotedFacetModel promotedFacet = asConfigurationService.createConfiguration(AsPromotedFacetModel.class);
		promotedFacet.setCatalogVersion(onlineCatalogVersion);
		promotedFacet.setSearchConfiguration(searchConfiguration);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(promotedFacet);
	}

	@Test
	public void failToCreatePromotedFacetWithWrongIndexProperty() throws Exception
	{
		// given
		final AsPromotedFacetModel promotedFacet = asConfigurationService.createConfiguration(AsPromotedFacetModel.class);
		promotedFacet.setCatalogVersion(onlineCatalogVersion);
		promotedFacet.setSearchConfiguration(searchConfiguration);
		promotedFacet.setIndexProperty(WRONG_INDEX_PROPERTY);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(promotedFacet);
	}

	@Test
	public void failToCreatePromotedFacetWithWrongIndexPropertyType() throws Exception
	{
		// given
		final AsPromotedFacetModel promotedFacet = asConfigurationService.createConfiguration(AsPromotedFacetModel.class);
		promotedFacet.setCatalogVersion(onlineCatalogVersion);
		promotedFacet.setSearchConfiguration(searchConfiguration);
		promotedFacet.setIndexProperty(INDEX_PROPERTY4);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(promotedFacet);
	}

	@Test
	public void updatePromotedFacet() throws Exception
	{
		// given
		final AsPromotedFacetModel promotedFacet = asConfigurationService.createConfiguration(AsPromotedFacetModel.class);
		promotedFacet.setCatalogVersion(onlineCatalogVersion);
		promotedFacet.setUid(UID1);
		promotedFacet.setSearchConfiguration(searchConfiguration);
		promotedFacet.setIndexProperty(INDEX_PROPERTY1);
		promotedFacet.setFacetType(AsFacetType.REFINE);
		promotedFacet.setPriority(PRIORITY1);

		// when
		asConfigurationService.saveConfiguration(promotedFacet);

		final Optional<AsPromotedFacetModel> createdPromotedFacetOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedFacetModel.class, onlineCatalogVersion, UID1);

		final AsPromotedFacetModel createdPromotedFacet = createdPromotedFacetOptional.get();
		createdPromotedFacet.setFacetType(AsFacetType.MULTISELECT_AND);
		createdPromotedFacet.setPriority(PRIORITY2);
		asConfigurationService.saveConfiguration(createdPromotedFacet);

		final Optional<AsPromotedFacetModel> updatedPromotedFacetOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedFacetModel.class, onlineCatalogVersion, UID1);

		// then
		assertTrue(updatedPromotedFacetOptional.isPresent());

		final AsPromotedFacetModel updatedPromotedFacet = updatedPromotedFacetOptional.get();
		assertEquals(onlineCatalogVersion, updatedPromotedFacet.getCatalogVersion());
		assertEquals(UID1, updatedPromotedFacet.getUid());
		assertEquals(searchConfiguration, updatedPromotedFacet.getSearchConfiguration());
		assertEquals(INDEX_PROPERTY1, updatedPromotedFacet.getIndexProperty());
		assertEquals(AsFacetType.MULTISELECT_AND, updatedPromotedFacet.getFacetType());
		assertEquals(PRIORITY2, updatedPromotedFacet.getPriority());
	}

	@Test
	public void failToUpdatePromotedFacetIndexProperty() throws Exception
	{
		// given
		final AsPromotedFacetModel promotedFacet = asConfigurationService.createConfiguration(AsPromotedFacetModel.class);
		promotedFacet.setCatalogVersion(onlineCatalogVersion);
		promotedFacet.setUid(UID1);
		promotedFacet.setSearchConfiguration(searchConfiguration);
		promotedFacet.setIndexProperty(INDEX_PROPERTY1);
		promotedFacet.setFacetType(AsFacetType.REFINE);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(promotedFacet);

		final Optional<AsPromotedFacetModel> createdPromotedItemOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedFacetModel.class, onlineCatalogVersion, UID1);

		final AsPromotedFacetModel createdPromotedItem = createdPromotedItemOptional.get();
		createdPromotedItem.setIndexProperty(INDEX_PROPERTY2);
		asConfigurationService.saveConfiguration(createdPromotedItem);
	}

	@Test
	public void removePromotedFacet() throws Exception
	{
		// given
		final AsPromotedFacetModel promotedFacet = asConfigurationService.createConfiguration(AsPromotedFacetModel.class);
		promotedFacet.setCatalogVersion(onlineCatalogVersion);
		promotedFacet.setUid(UID1);
		promotedFacet.setSearchConfiguration(searchConfiguration);
		promotedFacet.setIndexProperty(INDEX_PROPERTY1);
		promotedFacet.setFacetType(AsFacetType.REFINE);

		// when
		asConfigurationService.saveConfiguration(promotedFacet);

		final Optional<AsPromotedFacetModel> createdPromotedFacetOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedFacetModel.class, onlineCatalogVersion, UID1);

		final AsPromotedFacetModel createdPromotedFacet = createdPromotedFacetOptional.get();
		asConfigurationService.removeConfiguration(createdPromotedFacet);

		final Optional<AsPromotedFacetModel> removedPromotedFacetOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedFacetModel.class, onlineCatalogVersion, UID1);

		// then
		assertFalse(removedPromotedFacetOptional.isPresent());
	}

	@Test
	public void createMultiplePromotedFacets() throws Exception
	{
		// given
		final AsPromotedFacetModel promotedFacet1 = asConfigurationService.createConfiguration(AsPromotedFacetModel.class);
		promotedFacet1.setCatalogVersion(onlineCatalogVersion);
		promotedFacet1.setUid(UID1);
		promotedFacet1.setSearchConfiguration(searchConfiguration);
		promotedFacet1.setIndexProperty(INDEX_PROPERTY1);
		promotedFacet1.setFacetType(AsFacetType.MULTISELECT_AND);

		final AsPromotedFacetModel promotedFacet2 = asConfigurationService.createConfiguration(AsPromotedFacetModel.class);
		promotedFacet2.setCatalogVersion(onlineCatalogVersion);
		promotedFacet2.setUid(UID2);
		promotedFacet2.setSearchConfiguration(searchConfiguration);
		promotedFacet2.setIndexProperty(INDEX_PROPERTY2);
		promotedFacet2.setFacetType(AsFacetType.MULTISELECT_OR);

		// when
		asConfigurationService.saveConfiguration(promotedFacet1);
		asConfigurationService.saveConfiguration(promotedFacet2);

		modelService.refresh(searchConfiguration);

		// then
		assertThat(searchConfiguration.getPromotedFacets()).containsExactly(promotedFacet1, promotedFacet2);
	}

	@Test
	public void rankAfterPromotedFacet() throws Exception
	{
		// given
		final AsPromotedFacetModel promotedFacet1 = asConfigurationService.createConfiguration(AsPromotedFacetModel.class);
		promotedFacet1.setCatalogVersion(onlineCatalogVersion);
		promotedFacet1.setUid(UID1);
		promotedFacet1.setSearchConfiguration(searchConfiguration);
		promotedFacet1.setIndexProperty(INDEX_PROPERTY1);
		promotedFacet1.setFacetType(AsFacetType.MULTISELECT_AND);

		final AsPromotedFacetModel promotedFacet2 = asConfigurationService.createConfiguration(AsPromotedFacetModel.class);
		promotedFacet2.setCatalogVersion(onlineCatalogVersion);
		promotedFacet2.setUid(UID2);
		promotedFacet2.setSearchConfiguration(searchConfiguration);
		promotedFacet2.setIndexProperty(INDEX_PROPERTY2);
		promotedFacet2.setFacetType(AsFacetType.MULTISELECT_OR);

		// when
		asConfigurationService.saveConfiguration(promotedFacet1);
		asConfigurationService.saveConfiguration(promotedFacet2);

		modelService.refresh(searchConfiguration);

		final List<AsRankChange> rankChanges = asConfigurationService.rankAfterConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.PROMOTEDFACETS, UID2, UID1);

		// then
		assertThat(rankChanges).hasSize(1);

		final AsRankChange rankChange = rankChanges.get(0);
		assertEquals(AsRankChangeType.MOVE, rankChange.getType());
		assertEquals(UID1, rankChange.getUid());
		assertEquals(Integer.valueOf(0), rankChange.getOldRank());
		assertEquals(Integer.valueOf(1), rankChange.getNewRank());

		assertThat(searchConfiguration.getPromotedFacets()).containsExactly(promotedFacet2, promotedFacet1);
	}

	@Test
	public void rankBeforePromotedFacet() throws Exception
	{
		// given
		final AsPromotedFacetModel promotedFacet1 = asConfigurationService.createConfiguration(AsPromotedFacetModel.class);
		promotedFacet1.setCatalogVersion(onlineCatalogVersion);
		promotedFacet1.setUid(UID1);
		promotedFacet1.setSearchConfiguration(searchConfiguration);
		promotedFacet1.setIndexProperty(INDEX_PROPERTY1);
		promotedFacet1.setFacetType(AsFacetType.MULTISELECT_AND);

		final AsPromotedFacetModel promotedFacet2 = asConfigurationService.createConfiguration(AsPromotedFacetModel.class);
		promotedFacet2.setCatalogVersion(onlineCatalogVersion);
		promotedFacet2.setUid(UID2);
		promotedFacet2.setSearchConfiguration(searchConfiguration);
		promotedFacet2.setIndexProperty(INDEX_PROPERTY2);
		promotedFacet2.setFacetType(AsFacetType.MULTISELECT_OR);

		// when
		asConfigurationService.saveConfiguration(promotedFacet1);
		asConfigurationService.saveConfiguration(promotedFacet2);

		modelService.refresh(searchConfiguration);

		final List<AsRankChange> rankChanges = asConfigurationService.rankBeforeConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.PROMOTEDFACETS, UID1, UID2);

		// then
		assertThat(rankChanges).hasSize(1);

		final AsRankChange rankChange = rankChanges.get(0);
		assertEquals(AsRankChangeType.MOVE, rankChange.getType());
		assertEquals(UID2, rankChange.getUid());
		assertEquals(Integer.valueOf(1), rankChange.getOldRank());
		assertEquals(Integer.valueOf(0), rankChange.getNewRank());

		assertThat(searchConfiguration.getPromotedFacets()).containsExactly(promotedFacet2, promotedFacet1);
	}

	@Test
	public void getNonExistingFacet() throws Exception
	{
		// when
		final Optional<AsFacetModel> facetOptional = asConfigurationService.getConfigurationForUid(AsFacetModel.class,
				onlineCatalogVersion, UID1);

		// then
		assertFalse(facetOptional.isPresent());
	}

	@Test
	public void createFacetWithoutUid() throws Exception
	{
		// given
		final AsFacetModel facet = asConfigurationService.createConfiguration(AsFacetModel.class);
		facet.setCatalogVersion(onlineCatalogVersion);
		facet.setSearchConfiguration(searchConfiguration);
		facet.setIndexProperty(INDEX_PROPERTY1);
		facet.setFacetType(AsFacetType.REFINE);

		// when
		asConfigurationService.saveConfiguration(facet);

		// then
		assertNotNull(facet.getUid());
		assertFalse(facet.getUid().isEmpty());
	}

	@Test
	public void createFacet() throws Exception
	{
		// given
		final AsFacetModel facet = asConfigurationService.createConfiguration(AsFacetModel.class);
		facet.setCatalogVersion(onlineCatalogVersion);
		facet.setUid(UID1);
		facet.setSearchConfiguration(searchConfiguration);
		facet.setIndexProperty(INDEX_PROPERTY1);
		facet.setFacetType(AsFacetType.REFINE);

		// when
		asConfigurationService.saveConfiguration(facet);

		final Optional<AsFacetModel> createdFacetOptional = asConfigurationService.getConfigurationForUid(AsFacetModel.class,
				onlineCatalogVersion, UID1);

		// then
		assertTrue(createdFacetOptional.isPresent());

		final AsFacetModel createdFacet = createdFacetOptional.get();
		assertEquals(onlineCatalogVersion, createdFacet.getCatalogVersion());
		assertEquals(UID1, createdFacet.getUid());
		assertEquals(searchConfiguration, createdFacet.getSearchConfiguration());
		assertEquals(INDEX_PROPERTY1, createdFacet.getIndexProperty());
		assertEquals(AsFacetType.REFINE, createdFacet.getFacetType());
	}

	@Test
	public void failToCreateFacetWithWrongCatalogVersion() throws Exception
	{
		// given
		final AsFacetModel facet = asConfigurationService.createConfiguration(AsFacetModel.class);
		facet.setCatalogVersion(stagedCatalogVersion);
		facet.setSearchConfiguration(searchConfiguration);
		facet.setIndexProperty(INDEX_PROPERTY1);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(facet);
	}

	@Test
	public void failToCreateFacetWithoutIndexProperty() throws Exception
	{
		// given
		final AsFacetModel facet = asConfigurationService.createConfiguration(AsFacetModel.class);
		facet.setCatalogVersion(onlineCatalogVersion);
		facet.setSearchConfiguration(searchConfiguration);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(facet);
	}

	@Test
	public void failToCreateFacetWithWrongIndexProperty() throws Exception
	{
		// given
		final AsFacetModel facet = asConfigurationService.createConfiguration(AsFacetModel.class);
		facet.setCatalogVersion(onlineCatalogVersion);
		facet.setSearchConfiguration(searchConfiguration);
		facet.setIndexProperty(WRONG_INDEX_PROPERTY);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(facet);
	}

	@Test
	public void failToCreateFacetWithWrongIndexPropertyType() throws Exception
	{
		// given
		final AsFacetModel facet = asConfigurationService.createConfiguration(AsFacetModel.class);
		facet.setCatalogVersion(onlineCatalogVersion);
		facet.setSearchConfiguration(searchConfiguration);
		facet.setIndexProperty(INDEX_PROPERTY4);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(facet);
	}

	@Test
	public void updateFacet() throws Exception
	{
		// given
		final AsFacetModel facet = asConfigurationService.createConfiguration(AsFacetModel.class);
		facet.setCatalogVersion(onlineCatalogVersion);
		facet.setUid(UID1);
		facet.setSearchConfiguration(searchConfiguration);
		facet.setIndexProperty(INDEX_PROPERTY1);
		facet.setFacetType(AsFacetType.REFINE);
		facet.setPriority(PRIORITY1);

		// when
		asConfigurationService.saveConfiguration(facet);

		final Optional<AsFacetModel> createdFacetOptional = asConfigurationService.getConfigurationForUid(AsFacetModel.class,
				onlineCatalogVersion, UID1);

		final AsFacetModel createdFacet = createdFacetOptional.get();
		createdFacet.setFacetType(AsFacetType.MULTISELECT_AND);
		createdFacet.setPriority(PRIORITY2);
		asConfigurationService.saveConfiguration(createdFacet);

		final Optional<AsFacetModel> updatedFacetOptional = asConfigurationService.getConfigurationForUid(AsFacetModel.class,
				onlineCatalogVersion, UID1);

		// then
		assertTrue(updatedFacetOptional.isPresent());

		final AsFacetModel updatedFacet = updatedFacetOptional.get();
		assertEquals(onlineCatalogVersion, updatedFacet.getCatalogVersion());
		assertEquals(UID1, updatedFacet.getUid());
		assertEquals(searchConfiguration, updatedFacet.getSearchConfiguration());
		assertEquals(INDEX_PROPERTY1, updatedFacet.getIndexProperty());
		assertEquals(AsFacetType.MULTISELECT_AND, updatedFacet.getFacetType());
		assertEquals(PRIORITY2, updatedFacet.getPriority());
	}

	@Test
	public void failToUpdateFacetIndexProperty() throws Exception
	{
		// given
		final AsFacetModel facet = asConfigurationService.createConfiguration(AsFacetModel.class);
		facet.setCatalogVersion(onlineCatalogVersion);
		facet.setUid(UID1);
		facet.setSearchConfiguration(searchConfiguration);
		facet.setIndexProperty(INDEX_PROPERTY1);
		facet.setFacetType(AsFacetType.REFINE);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(facet);

		final Optional<AsFacetModel> createdPromotedItemOptional = asConfigurationService.getConfigurationForUid(AsFacetModel.class,
				onlineCatalogVersion, UID1);

		final AsFacetModel createdPromotedItem = createdPromotedItemOptional.get();
		createdPromotedItem.setIndexProperty(INDEX_PROPERTY2);
		asConfigurationService.saveConfiguration(createdPromotedItem);
	}

	@Test
	public void removeFacet() throws Exception
	{
		// given
		final AsFacetModel facet = asConfigurationService.createConfiguration(AsFacetModel.class);
		facet.setCatalogVersion(onlineCatalogVersion);
		facet.setUid(UID1);
		facet.setSearchConfiguration(searchConfiguration);
		facet.setIndexProperty(INDEX_PROPERTY1);
		facet.setFacetType(AsFacetType.REFINE);

		// when
		asConfigurationService.saveConfiguration(facet);

		final Optional<AsFacetModel> createdFacetOptional = asConfigurationService.getConfigurationForUid(AsFacetModel.class,
				onlineCatalogVersion, UID1);

		final AsFacetModel createdFacet = createdFacetOptional.get();
		asConfigurationService.removeConfiguration(createdFacet);

		final Optional<AsFacetModel> removedFacetOptional = asConfigurationService.getConfigurationForUid(AsFacetModel.class,
				onlineCatalogVersion, UID1);

		// then
		assertFalse(removedFacetOptional.isPresent());
	}

	@Test
	public void createMultipleFacets() throws Exception
	{
		// given
		final AsFacetModel facet1 = asConfigurationService.createConfiguration(AsFacetModel.class);
		facet1.setCatalogVersion(onlineCatalogVersion);
		facet1.setUid(UID1);
		facet1.setSearchConfiguration(searchConfiguration);
		facet1.setIndexProperty(INDEX_PROPERTY1);
		facet1.setFacetType(AsFacetType.MULTISELECT_AND);

		final AsFacetModel facet2 = asConfigurationService.createConfiguration(AsFacetModel.class);
		facet2.setCatalogVersion(onlineCatalogVersion);
		facet2.setUid(UID2);
		facet2.setSearchConfiguration(searchConfiguration);
		facet2.setIndexProperty(INDEX_PROPERTY2);
		facet2.setFacetType(AsFacetType.MULTISELECT_OR);

		// when
		asConfigurationService.saveConfiguration(facet1);
		asConfigurationService.saveConfiguration(facet2);

		modelService.refresh(searchConfiguration);

		// then
		assertThat(searchConfiguration.getFacets()).containsExactly(facet1, facet2);
	}

	@Test
	public void rankAfterFacet() throws Exception
	{
		// given
		final AsFacetModel facet1 = asConfigurationService.createConfiguration(AsFacetModel.class);
		facet1.setCatalogVersion(onlineCatalogVersion);
		facet1.setUid(UID1);
		facet1.setSearchConfiguration(searchConfiguration);
		facet1.setIndexProperty(INDEX_PROPERTY1);
		facet1.setFacetType(AsFacetType.MULTISELECT_AND);

		final AsFacetModel facet2 = asConfigurationService.createConfiguration(AsFacetModel.class);
		facet2.setCatalogVersion(onlineCatalogVersion);
		facet2.setUid(UID2);
		facet2.setSearchConfiguration(searchConfiguration);
		facet2.setIndexProperty(INDEX_PROPERTY2);
		facet2.setFacetType(AsFacetType.MULTISELECT_OR);

		// when
		asConfigurationService.saveConfiguration(facet1);
		asConfigurationService.saveConfiguration(facet2);

		modelService.refresh(searchConfiguration);

		final List<AsRankChange> rankChanges = asConfigurationService.rankAfterConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.FACETS, UID2, UID1);

		// then
		assertThat(rankChanges).hasSize(1);

		final AsRankChange rankChange = rankChanges.get(0);
		assertEquals(AsRankChangeType.MOVE, rankChange.getType());
		assertEquals(UID1, rankChange.getUid());
		assertEquals(Integer.valueOf(0), rankChange.getOldRank());
		assertEquals(Integer.valueOf(1), rankChange.getNewRank());

		assertThat(searchConfiguration.getFacets()).containsExactly(facet2, facet1);
	}

	@Test
	public void rankBeforeFacet() throws Exception
	{
		// given
		final AsFacetModel facet1 = asConfigurationService.createConfiguration(AsFacetModel.class);
		facet1.setCatalogVersion(onlineCatalogVersion);
		facet1.setUid(UID1);
		facet1.setSearchConfiguration(searchConfiguration);
		facet1.setIndexProperty(INDEX_PROPERTY1);
		facet1.setFacetType(AsFacetType.MULTISELECT_AND);

		final AsFacetModel facet2 = asConfigurationService.createConfiguration(AsFacetModel.class);
		facet2.setCatalogVersion(onlineCatalogVersion);
		facet2.setUid(UID2);
		facet2.setSearchConfiguration(searchConfiguration);
		facet2.setIndexProperty(INDEX_PROPERTY2);
		facet2.setFacetType(AsFacetType.MULTISELECT_OR);

		// when
		asConfigurationService.saveConfiguration(facet1);
		asConfigurationService.saveConfiguration(facet2);

		modelService.refresh(searchConfiguration);

		final List<AsRankChange> rankChanges = asConfigurationService.rankBeforeConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.FACETS, UID1, UID2);

		// then
		assertThat(rankChanges).hasSize(1);

		final AsRankChange rankChange = rankChanges.get(0);
		assertEquals(AsRankChangeType.MOVE, rankChange.getType());
		assertEquals(UID2, rankChange.getUid());
		assertEquals(Integer.valueOf(1), rankChange.getOldRank());
		assertEquals(Integer.valueOf(0), rankChange.getNewRank());

		assertThat(searchConfiguration.getFacets()).containsExactly(facet2, facet1);
	}

	@Test
	public void getNonExistingExcludedFacet() throws Exception
	{
		// when
		final Optional<AsExcludedFacetModel> excludedFacetOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedFacetModel.class, onlineCatalogVersion, UID1);

		// then
		assertFalse(excludedFacetOptional.isPresent());
	}

	@Test
	public void createExcludedFacetWithoutUid() throws Exception
	{
		// given
		final AsExcludedFacetModel excludedFacet = asConfigurationService.createConfiguration(AsExcludedFacetModel.class);
		excludedFacet.setCatalogVersion(onlineCatalogVersion);
		excludedFacet.setSearchConfiguration(searchConfiguration);
		excludedFacet.setIndexProperty(INDEX_PROPERTY1);
		excludedFacet.setFacetType(AsFacetType.REFINE);

		// when
		asConfigurationService.saveConfiguration(excludedFacet);

		// then
		assertNotNull(excludedFacet.getUid());
		assertFalse(excludedFacet.getUid().isEmpty());
	}

	@Test
	public void createExcludedFacet() throws Exception
	{
		// given
		final AsExcludedFacetModel excludedFacet = asConfigurationService.createConfiguration(AsExcludedFacetModel.class);
		excludedFacet.setCatalogVersion(onlineCatalogVersion);
		excludedFacet.setUid(UID1);
		excludedFacet.setSearchConfiguration(searchConfiguration);
		excludedFacet.setIndexProperty(INDEX_PROPERTY1);
		excludedFacet.setFacetType(AsFacetType.REFINE);

		// when
		asConfigurationService.saveConfiguration(excludedFacet);

		final Optional<AsExcludedFacetModel> createdExcludedFacetOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedFacetModel.class, onlineCatalogVersion, UID1);

		// then
		assertTrue(createdExcludedFacetOptional.isPresent());

		final AsExcludedFacetModel createdExcludedFacet = createdExcludedFacetOptional.get();
		assertEquals(onlineCatalogVersion, createdExcludedFacet.getCatalogVersion());
		assertEquals(UID1, createdExcludedFacet.getUid());
		assertEquals(searchConfiguration, createdExcludedFacet.getSearchConfiguration());
		assertEquals(INDEX_PROPERTY1, createdExcludedFacet.getIndexProperty());
		assertEquals(AsFacetType.REFINE, createdExcludedFacet.getFacetType());
	}

	@Test
	public void failToCreateExcludedFacetWithWrongCatalogVersion() throws Exception
	{
		// given
		final AsExcludedFacetModel excludedFacet = asConfigurationService.createConfiguration(AsExcludedFacetModel.class);
		excludedFacet.setCatalogVersion(stagedCatalogVersion);
		excludedFacet.setSearchConfiguration(searchConfiguration);
		excludedFacet.setIndexProperty(INDEX_PROPERTY1);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(excludedFacet);
	}

	@Test
	public void failToCreateExcludedFacetWithoutIndexProperty() throws Exception
	{
		// given
		final AsExcludedFacetModel excludedFacet = asConfigurationService.createConfiguration(AsExcludedFacetModel.class);
		excludedFacet.setCatalogVersion(onlineCatalogVersion);
		excludedFacet.setSearchConfiguration(searchConfiguration);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(excludedFacet);
	}

	@Test
	public void failToCreateExcludedFacetWithWrongIndexProperty() throws Exception
	{
		// given
		final AsExcludedFacetModel excludedFacet = asConfigurationService.createConfiguration(AsExcludedFacetModel.class);
		excludedFacet.setCatalogVersion(onlineCatalogVersion);
		excludedFacet.setSearchConfiguration(searchConfiguration);
		excludedFacet.setIndexProperty(WRONG_INDEX_PROPERTY);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(excludedFacet);
	}

	@Test
	public void failToCreateExcludedFacetWithWrongIndexPropertyType() throws Exception
	{
		// given
		final AsExcludedFacetModel excludedFacet = asConfigurationService.createConfiguration(AsExcludedFacetModel.class);
		excludedFacet.setCatalogVersion(onlineCatalogVersion);
		excludedFacet.setSearchConfiguration(searchConfiguration);
		excludedFacet.setIndexProperty(INDEX_PROPERTY4);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(excludedFacet);
	}

	@Test
	public void updateExcludedFacet() throws Exception
	{
		// given
		final AsExcludedFacetModel excludedFacet = asConfigurationService.createConfiguration(AsExcludedFacetModel.class);
		excludedFacet.setCatalogVersion(onlineCatalogVersion);
		excludedFacet.setUid(UID1);
		excludedFacet.setSearchConfiguration(searchConfiguration);
		excludedFacet.setIndexProperty(INDEX_PROPERTY1);
		excludedFacet.setFacetType(AsFacetType.REFINE);
		excludedFacet.setPriority(PRIORITY1);

		// when
		asConfigurationService.saveConfiguration(excludedFacet);

		final Optional<AsExcludedFacetModel> createdExcludedFacetOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedFacetModel.class, onlineCatalogVersion, UID1);

		final AsExcludedFacetModel createdExcludedFacet = createdExcludedFacetOptional.get();
		createdExcludedFacet.setFacetType(AsFacetType.MULTISELECT_AND);
		createdExcludedFacet.setPriority(PRIORITY2);
		asConfigurationService.saveConfiguration(createdExcludedFacet);

		final Optional<AsExcludedFacetModel> updatedExcludedFacetOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedFacetModel.class, onlineCatalogVersion, UID1);

		// then
		assertTrue(updatedExcludedFacetOptional.isPresent());

		final AsExcludedFacetModel updatedExcludedFacet = updatedExcludedFacetOptional.get();
		assertEquals(onlineCatalogVersion, updatedExcludedFacet.getCatalogVersion());
		assertEquals(UID1, updatedExcludedFacet.getUid());
		assertEquals(searchConfiguration, updatedExcludedFacet.getSearchConfiguration());
		assertEquals(INDEX_PROPERTY1, updatedExcludedFacet.getIndexProperty());
		assertEquals(AsFacetType.MULTISELECT_AND, updatedExcludedFacet.getFacetType());
		assertEquals(PRIORITY2, updatedExcludedFacet.getPriority());
	}

	@Test
	public void failToUpdateExcludedFacetIndexProperty() throws Exception
	{
		// given
		final AsExcludedFacetModel excludedFacet = asConfigurationService.createConfiguration(AsExcludedFacetModel.class);
		excludedFacet.setCatalogVersion(onlineCatalogVersion);
		excludedFacet.setUid(UID1);
		excludedFacet.setSearchConfiguration(searchConfiguration);
		excludedFacet.setIndexProperty(INDEX_PROPERTY1);
		excludedFacet.setFacetType(AsFacetType.REFINE);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(excludedFacet);

		final Optional<AsExcludedFacetModel> createdPromotedItemOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedFacetModel.class, onlineCatalogVersion, UID1);

		final AsExcludedFacetModel createdPromotedItem = createdPromotedItemOptional.get();
		createdPromotedItem.setIndexProperty(INDEX_PROPERTY2);
		asConfigurationService.saveConfiguration(createdPromotedItem);
	}

	@Test
	public void removeExcludedFacet() throws Exception
	{
		// given
		final AsExcludedFacetModel excludedFacet = asConfigurationService.createConfiguration(AsExcludedFacetModel.class);
		excludedFacet.setCatalogVersion(onlineCatalogVersion);
		excludedFacet.setUid(UID1);
		excludedFacet.setSearchConfiguration(searchConfiguration);
		excludedFacet.setIndexProperty(INDEX_PROPERTY1);
		excludedFacet.setFacetType(AsFacetType.REFINE);

		// when
		asConfigurationService.saveConfiguration(excludedFacet);

		final Optional<AsExcludedFacetModel> createdExcludedFacetOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedFacetModel.class, onlineCatalogVersion, UID1);

		final AsExcludedFacetModel createdExcludedFacet = createdExcludedFacetOptional.get();
		asConfigurationService.removeConfiguration(createdExcludedFacet);

		final Optional<AsExcludedFacetModel> removedExcludedFacetOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedFacetModel.class, onlineCatalogVersion, UID1);

		// then
		assertFalse(removedExcludedFacetOptional.isPresent());
	}

	@Test
	public void createMultipleExcludedFacets() throws Exception
	{
		// given
		final AsExcludedFacetModel excludedFacet1 = asConfigurationService.createConfiguration(AsExcludedFacetModel.class);
		excludedFacet1.setCatalogVersion(onlineCatalogVersion);
		excludedFacet1.setUid(UID1);
		excludedFacet1.setSearchConfiguration(searchConfiguration);
		excludedFacet1.setIndexProperty(INDEX_PROPERTY1);
		excludedFacet1.setFacetType(AsFacetType.MULTISELECT_AND);

		final AsExcludedFacetModel excludedFacet2 = asConfigurationService.createConfiguration(AsExcludedFacetModel.class);
		excludedFacet2.setCatalogVersion(onlineCatalogVersion);
		excludedFacet2.setUid(UID2);
		excludedFacet2.setSearchConfiguration(searchConfiguration);
		excludedFacet2.setIndexProperty(INDEX_PROPERTY2);
		excludedFacet2.setFacetType(AsFacetType.MULTISELECT_OR);

		// when
		asConfigurationService.saveConfiguration(excludedFacet1);
		asConfigurationService.saveConfiguration(excludedFacet2);

		modelService.refresh(searchConfiguration);

		// then
		assertThat(searchConfiguration.getExcludedFacets()).containsExactly(excludedFacet1, excludedFacet2);
	}

	@Test
	public void rankAfterExcludedFacet() throws Exception
	{
		// given
		final AsExcludedFacetModel excludedFacet1 = asConfigurationService.createConfiguration(AsExcludedFacetModel.class);
		excludedFacet1.setCatalogVersion(onlineCatalogVersion);
		excludedFacet1.setUid(UID1);
		excludedFacet1.setSearchConfiguration(searchConfiguration);
		excludedFacet1.setIndexProperty(INDEX_PROPERTY1);
		excludedFacet1.setFacetType(AsFacetType.MULTISELECT_AND);

		final AsExcludedFacetModel excludedFacet2 = asConfigurationService.createConfiguration(AsExcludedFacetModel.class);
		excludedFacet2.setCatalogVersion(onlineCatalogVersion);
		excludedFacet2.setUid(UID2);
		excludedFacet2.setSearchConfiguration(searchConfiguration);
		excludedFacet2.setIndexProperty(INDEX_PROPERTY2);
		excludedFacet2.setFacetType(AsFacetType.MULTISELECT_OR);

		// when
		asConfigurationService.saveConfiguration(excludedFacet1);
		asConfigurationService.saveConfiguration(excludedFacet2);

		modelService.refresh(searchConfiguration);

		final List<AsRankChange> rankChanges = asConfigurationService.rankAfterConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.EXCLUDEDFACETS, UID2, UID1);

		// then
		assertThat(rankChanges).hasSize(1);

		final AsRankChange rankChange = rankChanges.get(0);
		assertEquals(AsRankChangeType.MOVE, rankChange.getType());
		assertEquals(UID1, rankChange.getUid());
		assertEquals(Integer.valueOf(0), rankChange.getOldRank());
		assertEquals(Integer.valueOf(1), rankChange.getNewRank());

		assertThat(searchConfiguration.getExcludedFacets()).containsExactly(excludedFacet2, excludedFacet1);
	}

	@Test
	public void rankBeforeExcludedFacet() throws Exception
	{
		// given
		final AsExcludedFacetModel excludedFacet1 = asConfigurationService.createConfiguration(AsExcludedFacetModel.class);
		excludedFacet1.setCatalogVersion(onlineCatalogVersion);
		excludedFacet1.setUid(UID1);
		excludedFacet1.setSearchConfiguration(searchConfiguration);
		excludedFacet1.setIndexProperty(INDEX_PROPERTY1);
		excludedFacet1.setFacetType(AsFacetType.MULTISELECT_AND);

		final AsExcludedFacetModel excludedFacet2 = asConfigurationService.createConfiguration(AsExcludedFacetModel.class);
		excludedFacet2.setCatalogVersion(onlineCatalogVersion);
		excludedFacet2.setUid(UID2);
		excludedFacet2.setSearchConfiguration(searchConfiguration);
		excludedFacet2.setIndexProperty(INDEX_PROPERTY2);
		excludedFacet2.setFacetType(AsFacetType.MULTISELECT_OR);

		// when
		asConfigurationService.saveConfiguration(excludedFacet1);
		asConfigurationService.saveConfiguration(excludedFacet2);

		modelService.refresh(searchConfiguration);

		final List<AsRankChange> rankChanges = asConfigurationService.rankBeforeConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.EXCLUDEDFACETS, UID1, UID2);

		// then
		assertThat(rankChanges).hasSize(1);

		final AsRankChange rankChange = rankChanges.get(0);
		assertEquals(AsRankChangeType.MOVE, rankChange.getType());
		assertEquals(UID2, rankChange.getUid());
		assertEquals(Integer.valueOf(1), rankChange.getOldRank());
		assertEquals(Integer.valueOf(0), rankChange.getNewRank());

		assertThat(searchConfiguration.getExcludedFacets()).containsExactly(excludedFacet2, excludedFacet1);
	}

	@Test
	public void createMultipleFacetConfigurations() throws Exception
	{
		// given
		final AsPromotedFacetModel promotedFacet = asConfigurationService.createConfiguration(AsPromotedFacetModel.class);
		promotedFacet.setCatalogVersion(onlineCatalogVersion);
		promotedFacet.setSearchConfiguration(searchConfiguration);
		promotedFacet.setIndexProperty(INDEX_PROPERTY1);

		final AsFacetModel facet = asConfigurationService.createConfiguration(AsFacetModel.class);
		facet.setCatalogVersion(onlineCatalogVersion);
		facet.setSearchConfiguration(searchConfiguration);
		facet.setIndexProperty(INDEX_PROPERTY2);

		final AsExcludedFacetModel excludedFacet = asConfigurationService.createConfiguration(AsExcludedFacetModel.class);
		excludedFacet.setCatalogVersion(onlineCatalogVersion);
		excludedFacet.setSearchConfiguration(searchConfiguration);
		excludedFacet.setIndexProperty(INDEX_PROPERTY3);

		// when
		asConfigurationService.saveConfiguration(promotedFacet);
		asConfigurationService.saveConfiguration(facet);
		asConfigurationService.saveConfiguration(excludedFacet);

		// then
		assertEquals(onlineCatalogVersion, promotedFacet.getCatalogVersion());
		assertNotNull(promotedFacet.getUid());
		assertFalse(promotedFacet.getUid().isEmpty());

		assertEquals(onlineCatalogVersion, facet.getCatalogVersion());
		assertNotNull(facet.getUid());
		assertFalse(facet.getUid().isEmpty());

		assertEquals(onlineCatalogVersion, excludedFacet.getCatalogVersion());
		assertNotNull(excludedFacet.getUid());
		assertFalse(excludedFacet.getUid().isEmpty());
	}

	@Test
	public void failToCreateMultipleFacetConfigurationsSameUid1() throws Exception
	{
		// given
		final AsPromotedFacetModel promotedFacet = asConfigurationService.createConfiguration(AsPromotedFacetModel.class);
		promotedFacet.setCatalogVersion(onlineCatalogVersion);
		promotedFacet.setUid(UID1);
		promotedFacet.setSearchConfiguration(searchConfiguration);
		promotedFacet.setIndexProperty(INDEX_PROPERTY1);
		promotedFacet.setFacetType(AsFacetType.REFINE);

		final AsFacetModel facet = asConfigurationService.createConfiguration(AsFacetModel.class);
		facet.setCatalogVersion(onlineCatalogVersion);
		facet.setUid(UID1);
		facet.setSearchConfiguration(searchConfiguration);
		facet.setIndexProperty(INDEX_PROPERTY2);
		facet.setFacetType(AsFacetType.REFINE);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(promotedFacet);
		asConfigurationService.saveConfiguration(facet);
	}

	@Test
	public void failToCreateMultipleFacetConfigurationsSameUid2() throws Exception
	{
		// given
		final AsFacetModel facet = asConfigurationService.createConfiguration(AsFacetModel.class);
		facet.setCatalogVersion(onlineCatalogVersion);
		facet.setUid(UID1);
		facet.setSearchConfiguration(searchConfiguration);
		facet.setIndexProperty(INDEX_PROPERTY1);
		facet.setFacetType(AsFacetType.REFINE);

		final AsExcludedFacetModel excludedFacet = asConfigurationService.createConfiguration(AsExcludedFacetModel.class);
		excludedFacet.setCatalogVersion(onlineCatalogVersion);
		excludedFacet.setUid(UID1);
		excludedFacet.setSearchConfiguration(searchConfiguration);
		excludedFacet.setIndexProperty(INDEX_PROPERTY2);
		excludedFacet.setFacetType(AsFacetType.REFINE);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(facet);
		asConfigurationService.saveConfiguration(excludedFacet);
	}

	@Test
	public void failToCreateMultipleFacetConfigurationsSameIndexProperty1() throws Exception
	{
		// given
		final AsPromotedFacetModel promotedFacet = asConfigurationService.createConfiguration(AsPromotedFacetModel.class);
		promotedFacet.setCatalogVersion(onlineCatalogVersion);
		promotedFacet.setSearchConfiguration(searchConfiguration);
		promotedFacet.setIndexProperty(INDEX_PROPERTY1);
		promotedFacet.setFacetType(AsFacetType.REFINE);

		final AsFacetModel facet = asConfigurationService.createConfiguration(AsFacetModel.class);
		facet.setCatalogVersion(onlineCatalogVersion);
		facet.setSearchConfiguration(searchConfiguration);
		facet.setIndexProperty(INDEX_PROPERTY1);
		facet.setFacetType(AsFacetType.REFINE);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(promotedFacet);
		asConfigurationService.saveConfiguration(facet);
	}

	@Test
	public void failToCreateMultipleFacetConfigurationsSameIndexProperty2() throws Exception
	{
		// given
		final AsFacetModel facet = asConfigurationService.createConfiguration(AsFacetModel.class);
		facet.setCatalogVersion(onlineCatalogVersion);
		facet.setSearchConfiguration(searchConfiguration);
		facet.setIndexProperty(INDEX_PROPERTY2);
		facet.setFacetType(AsFacetType.REFINE);

		final AsExcludedFacetModel excludedFacet = asConfigurationService.createConfiguration(AsExcludedFacetModel.class);
		excludedFacet.setCatalogVersion(onlineCatalogVersion);
		excludedFacet.setSearchConfiguration(searchConfiguration);
		excludedFacet.setIndexProperty(INDEX_PROPERTY2);
		excludedFacet.setFacetType(AsFacetType.REFINE);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(facet);
		asConfigurationService.saveConfiguration(excludedFacet);
	}

	@Test
	public void moveFacetConfiguration1() throws Exception
	{
		// given
		final AsPromotedFacetModel promotedFacet = asConfigurationService.createConfiguration(AsPromotedFacetModel.class);
		promotedFacet.setCatalogVersion(onlineCatalogVersion);
		promotedFacet.setUid(UID1);
		promotedFacet.setSearchConfiguration(searchConfiguration);
		promotedFacet.setIndexProperty(INDEX_PROPERTY1);
		promotedFacet.setFacetType(AsFacetType.REFINE);

		// when
		asConfigurationService.saveConfiguration(promotedFacet);

		modelService.refresh(searchConfiguration);

		final boolean result = asConfigurationService.moveConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.PROMOTEDFACETS, AbstractAsConfigurableSearchConfigurationModel.FACETS,
				UID1);

		final Optional<AsPromotedFacetModel> promotedFacetOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedFacetModel.class, onlineCatalogVersion, UID1);
		final Optional<AsFacetModel> facetOptional = asConfigurationService.getConfigurationForUid(AsFacetModel.class,
				onlineCatalogVersion, UID1);

		// then
		assertTrue(result);
		assertFalse(promotedFacetOptional.isPresent());
		assertTrue(facetOptional.isPresent());

		final AsFacetModel facet = facetOptional.get();
		assertEquals(onlineCatalogVersion, facet.getCatalogVersion());
		assertEquals(UID1, facet.getUid());
		assertEquals(searchConfiguration, facet.getSearchConfiguration());
		assertEquals(INDEX_PROPERTY1, facet.getIndexProperty());
		assertEquals(AsFacetType.REFINE, facet.getFacetType());
	}

	@Test
	public void moveFacetConfiguration2() throws Exception
	{
		// given
		final AsFacetModel facet = asConfigurationService.createConfiguration(AsFacetModel.class);
		facet.setCatalogVersion(onlineCatalogVersion);
		facet.setUid(UID1);
		facet.setSearchConfiguration(searchConfiguration);
		facet.setIndexProperty(INDEX_PROPERTY1);
		facet.setFacetType(AsFacetType.REFINE);

		// when
		asConfigurationService.saveConfiguration(facet);

		modelService.refresh(searchConfiguration);

		final boolean result = asConfigurationService.moveConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.FACETS, AbstractAsConfigurableSearchConfigurationModel.EXCLUDEDFACETS,
				UID1);

		final Optional<AsFacetModel> facetOptional = asConfigurationService.getConfigurationForUid(AsFacetModel.class,
				onlineCatalogVersion, UID1);
		final Optional<AsExcludedFacetModel> excludedFacetOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedFacetModel.class, onlineCatalogVersion, UID1);

		// then
		assertTrue(result);
		assertFalse(facetOptional.isPresent());
		assertTrue(excludedFacetOptional.isPresent());

		final AsExcludedFacetModel excludedFacet = excludedFacetOptional.get();
		assertEquals(onlineCatalogVersion, excludedFacet.getCatalogVersion());
		assertEquals(UID1, excludedFacet.getUid());
		assertEquals(searchConfiguration, excludedFacet.getSearchConfiguration());
		assertEquals(INDEX_PROPERTY1, excludedFacet.getIndexProperty());
		assertEquals(AsFacetType.REFINE, excludedFacet.getFacetType());
	}

	@Test
	public void moveFacetConfiguration3() throws Exception
	{
		// given
		final AsExcludedFacetModel excludedFacet = asConfigurationService.createConfiguration(AsExcludedFacetModel.class);
		excludedFacet.setCatalogVersion(onlineCatalogVersion);
		excludedFacet.setUid(UID1);
		excludedFacet.setSearchConfiguration(searchConfiguration);
		excludedFacet.setIndexProperty(INDEX_PROPERTY1);
		excludedFacet.setFacetType(AsFacetType.REFINE);

		// when
		asConfigurationService.saveConfiguration(excludedFacet);

		modelService.refresh(searchConfiguration);

		final boolean result = asConfigurationService.moveConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.EXCLUDEDFACETS,
				AbstractAsConfigurableSearchConfigurationModel.PROMOTEDFACETS, UID1);

		final Optional<AsPromotedFacetModel> promotedFacetOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedFacetModel.class, onlineCatalogVersion, UID1);
		final Optional<AsExcludedFacetModel> excludedFacetOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedFacetModel.class, onlineCatalogVersion, UID1);

		// then
		assertTrue(result);
		assertTrue(promotedFacetOptional.isPresent());
		assertFalse(excludedFacetOptional.isPresent());

		final AsPromotedFacetModel promotedFacet = promotedFacetOptional.get();
		assertEquals(onlineCatalogVersion, promotedFacet.getCatalogVersion());
		assertEquals(UID1, promotedFacet.getUid());
		assertEquals(searchConfiguration, promotedFacet.getSearchConfiguration());
		assertEquals(INDEX_PROPERTY1, promotedFacet.getIndexProperty());
		assertEquals(AsFacetType.REFINE, promotedFacet.getFacetType());
	}
}
