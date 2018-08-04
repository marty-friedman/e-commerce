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
package de.hybris.platform.adaptivesearch.strategies.impl;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.adaptivesearch.context.AsSearchProfileContext;
import de.hybris.platform.adaptivesearch.daos.AsSearchConfigurationDao;
import de.hybris.platform.adaptivesearch.model.AsSimpleSearchConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AsSimpleSearchProfileModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class AsSimpleSearchConfigurationStrategyTest
{
	private AsSimpleSearchConfigurationStrategy strategy;

	@Mock
	private ModelService modelService;

	@Mock
	private AsSearchConfigurationDao asSearchConfigurationDao;

	@Mock
	private AsSearchProfileContext context;

	@Mock
	private AsSimpleSearchProfileModel searchProfile;

	@Mock
	private AsSimpleSearchConfigurationModel searchConfiguration;

	@Mock
	private CatalogVersionModel catalogVersion;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		strategy = new AsSimpleSearchConfigurationStrategy();
		strategy.setModelService(modelService);
		strategy.setAsSearchConfigurationDao(asSearchConfigurationDao);
	}

	@Test
	public void getSearchConfiguration()
	{
		// given
		final Map<String, Object> filters = new HashMap<>();
		filters.put(AsSimpleSearchConfigurationModel.SEARCHPROFILE, searchProfile);

		when(asSearchConfigurationDao.findSearchConfigurations(AsSimpleSearchConfigurationModel.class, filters))
				.thenReturn(Collections.singletonList(searchConfiguration));

		// when
		final Optional<AsSimpleSearchConfigurationModel> returnedSearchConfiguration = strategy.getForContext(context,
				searchProfile);

		// then
		assertTrue(returnedSearchConfiguration.isPresent());
		assertSame(searchConfiguration, returnedSearchConfiguration.get());
	}

	@Test
	public void getOrCreateExistingSearchConfiguration()
	{
		// given
		final Map<String, Object> filters = new HashMap<>();
		filters.put(AsSimpleSearchConfigurationModel.SEARCHPROFILE, searchProfile);

		when(asSearchConfigurationDao.findSearchConfigurations(AsSimpleSearchConfigurationModel.class, filters))
				.thenReturn(Collections.singletonList(searchConfiguration));

		// when
		final AsSimpleSearchConfigurationModel returnedSearchConfiguration = strategy.getOrCreateForContext(context, searchProfile);

		// then
		assertSame(searchConfiguration, returnedSearchConfiguration);
	}

	@Test
	public void getOrCreateNonExistingSearchConfiguration()
	{
		// given
		final Map<String, Object> filters = new HashMap<>();
		filters.put(AsSimpleSearchConfigurationModel.SEARCHPROFILE, searchProfile);

		when(asSearchConfigurationDao.findSearchConfigurations(AsSimpleSearchConfigurationModel.class, filters))
				.thenReturn(Collections.emptyList());
		when(modelService.create(AsSimpleSearchConfigurationModel.class)).thenReturn(searchConfiguration);

		when(searchProfile.getCatalogVersion()).thenReturn(catalogVersion);

		// when
		final AsSimpleSearchConfigurationModel returnedSearchConfiguration = strategy.getOrCreateForContext(context, searchProfile);

		// then
		assertSame(searchConfiguration, returnedSearchConfiguration);
		verify(searchConfiguration).setSearchProfile(searchProfile);
		verify(searchConfiguration).setCatalogVersion(catalogVersion);
	}
}
