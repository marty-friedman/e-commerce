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
import de.hybris.platform.adaptivesearch.model.AsExcludedFacetValueModel;
import de.hybris.platform.adaptivesearch.model.AsPromotedFacetModel;
import de.hybris.platform.adaptivesearch.model.AsPromotedFacetValueModel;
import de.hybris.platform.adaptivesearch.services.AsConfigurationService;
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
public class AsFacetValueConfigurationCrudTest extends ServicelayerTransactionalTest
{
	private static final String CATALOG_ID = "hwcatalog";
	private static final String VERSION_STAGED = "Staged";
	private static final String VERSION_ONLINE = "Online";

	private static final String UID1 = "e81de964-b6b8-4031-bf1a-2eeb99b606ac";
	private static final String UID2 = "e3780f3f-5e60-4174-b85d-52c84b34ee38";

	private static final String FACET_VALUE1 = "FacetValue1";
	private static final String FACET_VALUE2 = "FacetValue2";

	private static final String FACET_CONFIGURATION_ID = "facetConfigurationID";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Resource
	private ModelService modelService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private AsConfigurationService asConfigurationService;

	private CatalogVersionModel onlineCatalogVersion;
	private CatalogVersionModel stagedCatalogVersion;
	private AsPromotedFacetModel promotedFacetStaged;
	private AsPromotedFacetModel promotedFacetOnline;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/adaptivesearch/test/integration/crud/asFacetValueConfigurationCrudTest.impex", CharEncoding.UTF_8);

		onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		stagedCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);

		final Optional<AsPromotedFacetModel> promotedFacetStagedOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedFacetModel.class, stagedCatalogVersion, FACET_CONFIGURATION_ID);
		promotedFacetStaged = promotedFacetStagedOptional.get();

		final Optional<AsPromotedFacetModel> promotedFacetOnlineOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedFacetModel.class, onlineCatalogVersion, FACET_CONFIGURATION_ID);
		promotedFacetOnline = promotedFacetOnlineOptional.get();
	}

	@Test
	public void getNonExistingPromotedFacetValue() throws Exception
	{
		// when
		final Optional<AsPromotedFacetValueModel> promotedValueOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedFacetValueModel.class, onlineCatalogVersion, UID1);

		// then
		assertFalse(promotedValueOptional.isPresent());
	}

	@Test
	public void createPromotedFacetValueWithoutUid() throws Exception
	{
		// given
		final AsPromotedFacetValueModel promotedValue = asConfigurationService.createConfiguration(AsPromotedFacetValueModel.class);
		promotedValue.setFacetConfiguration(promotedFacetStaged);
		promotedValue.setCatalogVersion(stagedCatalogVersion);
		promotedValue.setValue(FACET_VALUE1);

		// when
		asConfigurationService.saveConfiguration(promotedValue);

		// then
		assertNotNull(promotedValue.getUid());
		assertFalse(promotedValue.getUid().isEmpty());
	}

	@Test
	public void createPromotedFacetValue() throws Exception
	{
		// given
		final AsPromotedFacetValueModel promotedValue = asConfigurationService.createConfiguration(AsPromotedFacetValueModel.class);
		promotedValue.setCatalogVersion(onlineCatalogVersion);
		promotedValue.setUid(UID1);
		promotedValue.setFacetConfiguration(promotedFacetOnline);
		promotedValue.setValue(FACET_VALUE1);

		// when
		asConfigurationService.saveConfiguration(promotedValue);

		final Optional<AsPromotedFacetValueModel> createdPromotedFacetValueOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedFacetValueModel.class, onlineCatalogVersion, UID1);

		// then
		assertTrue(createdPromotedFacetValueOptional.isPresent());

		final AsPromotedFacetValueModel createdPromotedFacetValue = createdPromotedFacetValueOptional.get();
		assertEquals(onlineCatalogVersion, createdPromotedFacetValue.getCatalogVersion());
		assertEquals(UID1, createdPromotedFacetValue.getUid());

		assertEquals(promotedFacetOnline, createdPromotedFacetValue.getFacetConfiguration());
		assertEquals(FACET_VALUE1, createdPromotedFacetValue.getValue());
	}

	@Test
	public void failToCreatePromotedFacetValueWithWrongCatalogVersion() throws Exception
	{
		// given
		final AsPromotedFacetValueModel promotedValue = asConfigurationService.createConfiguration(AsPromotedFacetValueModel.class);
		promotedValue.setCatalogVersion(stagedCatalogVersion);
		promotedValue.setUid(UID1);
		promotedValue.setFacetConfiguration(promotedFacetOnline);
		promotedValue.setValue(FACET_VALUE1);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(promotedValue);
	}

	@Test
	public void failToCreatePromotedFacetValueWithoutValue() throws Exception
	{
		// given
		final AsPromotedFacetValueModel promotedValue = asConfigurationService.createConfiguration(AsPromotedFacetValueModel.class);
		promotedValue.setCatalogVersion(stagedCatalogVersion);
		promotedValue.setUid(UID1);
		promotedValue.setFacetConfiguration(promotedFacetOnline);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(promotedValue);
	}

	@Test
	public void updatePromotedFacetValue() throws Exception
	{
		// given
		final AsPromotedFacetValueModel promotedValue = asConfigurationService.createConfiguration(AsPromotedFacetValueModel.class);
		promotedValue.setCatalogVersion(onlineCatalogVersion);
		promotedValue.setUid(UID1);
		promotedValue.setFacetConfiguration(promotedFacetOnline);
		promotedValue.setValue(FACET_VALUE1);

		// when
		asConfigurationService.saveConfiguration(promotedValue);

		final Optional<AsPromotedFacetValueModel> createdPromotedFacetValueOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedFacetValueModel.class, onlineCatalogVersion, UID1);

		final AsPromotedFacetValueModel createdPromotedValue = createdPromotedFacetValueOptional.get();
		createdPromotedValue.setValue(FACET_VALUE2);
		asConfigurationService.saveConfiguration(createdPromotedValue);

		final Optional<AsPromotedFacetValueModel> updatedPromotedFacetValueOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedFacetValueModel.class, onlineCatalogVersion, UID1);

		// then
		assertTrue(updatedPromotedFacetValueOptional.isPresent());

		final AsPromotedFacetValueModel updatedPromotedValue = updatedPromotedFacetValueOptional.get();
		assertEquals(onlineCatalogVersion, updatedPromotedValue.getCatalogVersion());
		assertEquals(UID1, updatedPromotedValue.getUid());
		assertEquals(promotedFacetOnline, updatedPromotedValue.getFacetConfiguration());
		assertEquals(FACET_VALUE2, updatedPromotedValue.getValue());
	}

	@Test
	public void removePromotedFacetValue() throws Exception
	{
		// given
		final AsPromotedFacetValueModel promotedValue = asConfigurationService.createConfiguration(AsPromotedFacetValueModel.class);
		promotedValue.setCatalogVersion(onlineCatalogVersion);
		promotedValue.setUid(UID1);
		promotedValue.setFacetConfiguration(promotedFacetOnline);
		promotedValue.setValue(FACET_VALUE1);

		// when
		asConfigurationService.saveConfiguration(promotedValue);

		final Optional<AsPromotedFacetValueModel> createdPromotedFacetValueOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedFacetValueModel.class, onlineCatalogVersion, UID1);

		final AsPromotedFacetValueModel createdPromotedValue = createdPromotedFacetValueOptional.get();
		asConfigurationService.removeConfiguration(createdPromotedValue);

		final Optional<AsPromotedFacetValueModel> removedPromotedFacetValueOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedFacetValueModel.class, onlineCatalogVersion, UID1);

		// then
		assertFalse(removedPromotedFacetValueOptional.isPresent());
	}

	@Test
	public void createMultiplePromotedFacets() throws Exception
	{
		// given
		final AsPromotedFacetValueModel promotedValue1 = asConfigurationService
				.createConfiguration(AsPromotedFacetValueModel.class);
		promotedValue1.setCatalogVersion(onlineCatalogVersion);
		promotedValue1.setUid(UID1);
		promotedValue1.setFacetConfiguration(promotedFacetOnline);
		promotedValue1.setValue(FACET_VALUE1);

		final AsPromotedFacetValueModel promotedValue2 = asConfigurationService
				.createConfiguration(AsPromotedFacetValueModel.class);
		promotedValue2.setCatalogVersion(onlineCatalogVersion);
		promotedValue2.setUid(UID2);
		promotedValue2.setFacetConfiguration(promotedFacetOnline);
		promotedValue2.setValue(FACET_VALUE2);

		// when
		asConfigurationService.saveConfiguration(promotedValue1);
		asConfigurationService.saveConfiguration(promotedValue2);

		modelService.refresh(promotedFacetOnline);

		// then
		assertThat(promotedFacetOnline.getPromotedValues()).containsExactly(promotedValue1, promotedValue2);
	}

	@Test
	public void failToCreateTwoPromotedFacetValueConfigurationsWithSameUid()
	{
		// given
		final AsPromotedFacetValueModel promotedValue1 = asConfigurationService
				.createConfiguration(AsPromotedFacetValueModel.class);
		promotedValue1.setCatalogVersion(onlineCatalogVersion);
		promotedValue1.setUid(UID1);
		promotedValue1.setFacetConfiguration(promotedFacetOnline);
		promotedValue1.setValue(FACET_VALUE1);

		final AsPromotedFacetValueModel promotedValue2 = asConfigurationService
				.createConfiguration(AsPromotedFacetValueModel.class);
		promotedValue2.setCatalogVersion(onlineCatalogVersion);
		promotedValue2.setUid(UID1);
		promotedValue2.setFacetConfiguration(promotedFacetOnline);
		promotedValue2.setValue(FACET_VALUE2);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(promotedValue1);
		asConfigurationService.saveConfiguration(promotedValue2);
	}

	@Test
	public void failToCreateTwoPromotedFacetValueConfigurationsWithSameValue()
	{
		// given
		final AsPromotedFacetValueModel promotedValue1 = asConfigurationService
				.createConfiguration(AsPromotedFacetValueModel.class);
		promotedValue1.setCatalogVersion(onlineCatalogVersion);
		promotedValue1.setFacetConfiguration(promotedFacetOnline);
		promotedValue1.setValue(FACET_VALUE1);

		final AsPromotedFacetValueModel promotedValue2 = asConfigurationService
				.createConfiguration(AsPromotedFacetValueModel.class);
		promotedValue2.setCatalogVersion(onlineCatalogVersion);
		promotedValue2.setFacetConfiguration(promotedFacetOnline);
		promotedValue2.setValue(FACET_VALUE1);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(promotedValue1);
		asConfigurationService.saveConfiguration(promotedValue2);
	}

	@Test
	public void getNonExistingExcludedFacetValue() throws Exception
	{
		// when
		final Optional<AsExcludedFacetValueModel> excludedValueOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedFacetValueModel.class, onlineCatalogVersion, UID1);

		// then
		assertFalse(excludedValueOptional.isPresent());
	}

	@Test
	public void createExcludedFacetValueWithoutUid() throws Exception
	{
		// given
		final AsExcludedFacetValueModel excludedValue = asConfigurationService.createConfiguration(AsExcludedFacetValueModel.class);
		excludedValue.setFacetConfiguration(promotedFacetStaged);
		excludedValue.setCatalogVersion(stagedCatalogVersion);
		excludedValue.setValue(FACET_VALUE1);

		// when
		asConfigurationService.saveConfiguration(excludedValue);

		// then
		assertNotNull(excludedValue.getUid());
		assertFalse(excludedValue.getUid().isEmpty());
	}

	@Test
	public void createExcludedFacetValue() throws Exception
	{
		// given
		final AsExcludedFacetValueModel excludedValue = asConfigurationService.createConfiguration(AsExcludedFacetValueModel.class);
		excludedValue.setCatalogVersion(onlineCatalogVersion);
		excludedValue.setUid(UID1);
		excludedValue.setFacetConfiguration(promotedFacetOnline);
		excludedValue.setValue(FACET_VALUE1);

		// when
		asConfigurationService.saveConfiguration(excludedValue);

		final Optional<AsExcludedFacetValueModel> createdExcludedFacetValueOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedFacetValueModel.class, onlineCatalogVersion, UID1);

		// then
		assertTrue(createdExcludedFacetValueOptional.isPresent());

		final AsExcludedFacetValueModel createdExcludedFacetValue = createdExcludedFacetValueOptional.get();
		assertEquals(onlineCatalogVersion, createdExcludedFacetValue.getCatalogVersion());
		assertEquals(UID1, createdExcludedFacetValue.getUid());
		assertEquals(promotedFacetOnline, createdExcludedFacetValue.getFacetConfiguration());
		assertEquals(FACET_VALUE1, createdExcludedFacetValue.getValue());
	}

	@Test
	public void failToCreateExcludedFacetValueWithWrongCatalogVersion() throws Exception
	{
		// given
		final AsExcludedFacetValueModel excludedValue = asConfigurationService.createConfiguration(AsExcludedFacetValueModel.class);
		excludedValue.setCatalogVersion(stagedCatalogVersion);
		excludedValue.setUid(UID1);
		excludedValue.setFacetConfiguration(promotedFacetOnline);
		excludedValue.setValue(FACET_VALUE1);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(excludedValue);
	}

	@Test
	public void failToCreateExcludedFacetValueWithoutValue() throws Exception
	{
		// given
		final AsExcludedFacetValueModel excludedValue = asConfigurationService.createConfiguration(AsExcludedFacetValueModel.class);
		excludedValue.setCatalogVersion(stagedCatalogVersion);
		excludedValue.setUid(UID1);
		excludedValue.setFacetConfiguration(promotedFacetOnline);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(excludedValue);
	}

	@Test
	public void updateExcludedFacetValue() throws Exception
	{
		// given
		final AsExcludedFacetValueModel excludedValue = asConfigurationService.createConfiguration(AsExcludedFacetValueModel.class);
		excludedValue.setCatalogVersion(onlineCatalogVersion);
		excludedValue.setUid(UID1);
		excludedValue.setFacetConfiguration(promotedFacetOnline);
		excludedValue.setValue(FACET_VALUE1);

		// when
		asConfigurationService.saveConfiguration(excludedValue);

		final Optional<AsExcludedFacetValueModel> createdExcludedFacetValueOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedFacetValueModel.class, onlineCatalogVersion, UID1);

		final AsExcludedFacetValueModel createdExcludedValue = createdExcludedFacetValueOptional.get();
		createdExcludedValue.setValue(FACET_VALUE2);
		asConfigurationService.saveConfiguration(createdExcludedValue);

		final Optional<AsExcludedFacetValueModel> updatedExcludedFacetValueOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedFacetValueModel.class, onlineCatalogVersion, UID1);

		// then
		assertTrue(updatedExcludedFacetValueOptional.isPresent());

		final AsExcludedFacetValueModel updatedExcludedValue = updatedExcludedFacetValueOptional.get();
		assertEquals(onlineCatalogVersion, updatedExcludedValue.getCatalogVersion());
		assertEquals(UID1, updatedExcludedValue.getUid());
		assertEquals(promotedFacetOnline, updatedExcludedValue.getFacetConfiguration());
		assertEquals(FACET_VALUE2, updatedExcludedValue.getValue());
	}

	@Test
	public void removeExcludedFacetValue() throws Exception
	{
		// given
		final AsExcludedFacetValueModel excludedValue = asConfigurationService.createConfiguration(AsExcludedFacetValueModel.class);
		excludedValue.setCatalogVersion(onlineCatalogVersion);
		excludedValue.setUid(UID1);
		excludedValue.setFacetConfiguration(promotedFacetOnline);
		excludedValue.setValue(FACET_VALUE1);

		// when
		asConfigurationService.saveConfiguration(excludedValue);

		final Optional<AsExcludedFacetValueModel> createdExcludedFacetValueOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedFacetValueModel.class, onlineCatalogVersion, UID1);

		final AsExcludedFacetValueModel createdExcludedValue = createdExcludedFacetValueOptional.get();
		asConfigurationService.removeConfiguration(createdExcludedValue);

		final Optional<AsExcludedFacetValueModel> removedExcludedFacetValueOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedFacetValueModel.class, onlineCatalogVersion, UID1);

		// then
		assertFalse(removedExcludedFacetValueOptional.isPresent());
	}

	@Test
	public void createMultipleExcludedFacets() throws Exception
	{
		// given
		final AsExcludedFacetValueModel excludedValue1 = asConfigurationService
				.createConfiguration(AsExcludedFacetValueModel.class);
		excludedValue1.setCatalogVersion(onlineCatalogVersion);
		excludedValue1.setUid(UID1);
		excludedValue1.setFacetConfiguration(promotedFacetOnline);
		excludedValue1.setValue(FACET_VALUE1);

		final AsExcludedFacetValueModel excludedValue2 = asConfigurationService
				.createConfiguration(AsExcludedFacetValueModel.class);
		excludedValue2.setCatalogVersion(onlineCatalogVersion);
		excludedValue2.setUid(UID2);
		excludedValue2.setFacetConfiguration(promotedFacetOnline);
		excludedValue2.setValue(FACET_VALUE2);

		// when
		asConfigurationService.saveConfiguration(excludedValue1);
		asConfigurationService.saveConfiguration(excludedValue2);

		modelService.refresh(promotedFacetOnline);

		// then
		assertThat(promotedFacetOnline.getExcludedValues()).containsExactly(excludedValue1, excludedValue2);
	}

	@Test
	public void failToCreateTwoExcludedFacetValueConfigurationsWithSameUid()
	{
		// given
		final AsExcludedFacetValueModel excludedValue1 = asConfigurationService
				.createConfiguration(AsExcludedFacetValueModel.class);
		excludedValue1.setCatalogVersion(onlineCatalogVersion);
		excludedValue1.setUid(UID1);
		excludedValue1.setFacetConfiguration(promotedFacetOnline);
		excludedValue1.setValue(FACET_VALUE1);

		final AsExcludedFacetValueModel excludedValue2 = asConfigurationService
				.createConfiguration(AsExcludedFacetValueModel.class);
		excludedValue2.setCatalogVersion(onlineCatalogVersion);
		excludedValue2.setUid(UID1);
		excludedValue2.setFacetConfiguration(promotedFacetOnline);
		excludedValue2.setValue(FACET_VALUE2);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(excludedValue1);
		asConfigurationService.saveConfiguration(excludedValue2);
	}

	@Test
	public void failToCreateTwoExcludedFacetValueConfigurationsWithSameValue()
	{
		// given
		final AsExcludedFacetValueModel excludedValue1 = asConfigurationService
				.createConfiguration(AsExcludedFacetValueModel.class);
		excludedValue1.setCatalogVersion(onlineCatalogVersion);
		excludedValue1.setFacetConfiguration(promotedFacetOnline);
		excludedValue1.setValue(FACET_VALUE1);

		final AsExcludedFacetValueModel excludedValue2 = asConfigurationService
				.createConfiguration(AsExcludedFacetValueModel.class);
		excludedValue2.setCatalogVersion(onlineCatalogVersion);
		excludedValue2.setFacetConfiguration(promotedFacetOnline);
		excludedValue2.setValue(FACET_VALUE1);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(excludedValue1);
		asConfigurationService.saveConfiguration(excludedValue2);
	}

	@Test
	public void failToCreateTwoFacetValueConfigurationsWithSameUid()
	{
		// given
		final AsPromotedFacetValueModel promotedValue = asConfigurationService.createConfiguration(AsPromotedFacetValueModel.class);
		promotedValue.setCatalogVersion(onlineCatalogVersion);
		promotedValue.setUid(UID1);
		promotedValue.setFacetConfiguration(promotedFacetOnline);
		promotedValue.setValue(FACET_VALUE1);

		final AsExcludedFacetValueModel excludedValue = asConfigurationService.createConfiguration(AsExcludedFacetValueModel.class);
		excludedValue.setCatalogVersion(onlineCatalogVersion);
		excludedValue.setUid(UID1);
		excludedValue.setFacetConfiguration(promotedFacetOnline);
		excludedValue.setValue(FACET_VALUE2);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(promotedValue);
		asConfigurationService.saveConfiguration(excludedValue);
	}
}
