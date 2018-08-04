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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.adaptivesearch.context.AsSearchProfileContext;
import de.hybris.platform.adaptivesearch.data.AsSearchProfileActivationGroup;
import de.hybris.platform.adaptivesearch.model.AbstractAsSearchProfileModel;
import de.hybris.platform.adaptivesearch.strategies.AsSearchProfileActivationMapping;
import de.hybris.platform.adaptivesearch.strategies.AsSearchProfileActivationStrategy;
import de.hybris.platform.adaptivesearch.strategies.AsSearchProfileRegistry;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultAsSearchProfileActivationServiceTest
{
	private static final String INDEX_TYPE = "indexType";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Mock
	private ModelService modelService;

	@Mock
	private SessionService sessionService;

	@Mock
	private AsSearchProfileRegistry asSearchProfileRegistry;

	@Mock
	private AsSearchProfileContext context;

	@Mock
	private List<CatalogVersionModel> catalogVersions;

	private DefaultAsSearchProfileActivationService asSearchProfileActivationService;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		when(context.getIndexType()).thenReturn(INDEX_TYPE);
		when(context.getCatalogVersions()).thenReturn(catalogVersions);

		asSearchProfileActivationService = new DefaultAsSearchProfileActivationService();
		asSearchProfileActivationService.setModelService(modelService);
		asSearchProfileActivationService.setSessionService(sessionService);
		asSearchProfileActivationService.setAsSearchProfileRegistry(asSearchProfileRegistry);
	}

	@Test
	public void setCurrentSearchProfiles()
	{
		// given
		final PK pk1 = PK.fromLong(1);
		final AbstractAsSearchProfileModel searchProfile1 = mock(AbstractAsSearchProfileModel.class);

		final PK pk2 = PK.fromLong(2);
		final AbstractAsSearchProfileModel searchProfile2 = mock(AbstractAsSearchProfileModel.class);

		when(searchProfile1.getPk()).thenReturn(pk1);
		when(modelService.get(pk1)).thenReturn(searchProfile1);

		when(searchProfile2.getPk()).thenReturn(pk2);
		when(modelService.get(pk2)).thenReturn(searchProfile2);

		when(sessionService.getAttribute(DefaultAsSearchProfileActivationService.CURRENT_SEARCH_PROFILES))
				.thenReturn(Arrays.asList(pk1, pk2));

		// when
		asSearchProfileActivationService.setCurrentSearchProfiles(Arrays.asList(searchProfile1, searchProfile2));

		// then
		verify(sessionService).setAttribute(DefaultAsSearchProfileActivationService.CURRENT_SEARCH_PROFILES,
				Arrays.asList(pk1, pk2));
	}

	@Test
	public void getEmptyCurrentSearchProfiles()
	{
		// when
		final Optional<List<AbstractAsSearchProfileModel>> searchProfilesResult = asSearchProfileActivationService
				.getCurrentSearchProfiles();

		// then
		assertFalse(searchProfilesResult.isPresent());
	}

	@Test
	public void getCurrentSearchProfiles()
	{
		// given
		final PK pk1 = PK.fromLong(1);
		final AbstractAsSearchProfileModel searchProfile1 = mock(AbstractAsSearchProfileModel.class);

		final PK pk2 = PK.fromLong(2);
		final AbstractAsSearchProfileModel searchProfile2 = mock(AbstractAsSearchProfileModel.class);

		when(searchProfile1.getPk()).thenReturn(pk1);
		when(modelService.get(pk1)).thenReturn(searchProfile1);

		when(searchProfile2.getPk()).thenReturn(pk2);
		when(modelService.get(pk2)).thenReturn(searchProfile2);

		when(sessionService.getAttribute(DefaultAsSearchProfileActivationService.CURRENT_SEARCH_PROFILES))
				.thenReturn(Arrays.asList(pk1, pk2));

		// when
		final Optional<List<AbstractAsSearchProfileModel>> searchProfilesResult = asSearchProfileActivationService
				.getCurrentSearchProfiles();

		// then
		assertTrue(searchProfilesResult.isPresent());

		final List<AbstractAsSearchProfileModel> searchProfiles = searchProfilesResult.get();
		assertEquals(2, searchProfiles.size());
		assertSame(searchProfile1, searchProfiles.get(0));
		assertSame(searchProfile2, searchProfiles.get(1));
	}

	@Test
	public void clearCurrentSearchProfiles()
	{
		// when
		asSearchProfileActivationService.clearCurrentSearchProfiles();

		// then
		verify(sessionService).removeAttribute(DefaultAsSearchProfileActivationService.CURRENT_SEARCH_PROFILES);
	}

	@Test
	public void getEmptyActiveSearchProfilesForContext()
	{
		// when
		final List<AbstractAsSearchProfileModel> searchProfiles = asSearchProfileActivationService
				.getActiveSearchProfilesForContext(context);

		// then
		assertNotNull(searchProfiles);
		assertEquals(0, searchProfiles.size());
	}

	@Test
	public void getActiveSearchProfilesForContextFromSession()
	{
		// given
		final PK pk1 = PK.fromLong(1);
		final AbstractAsSearchProfileModel searchProfile1 = mock(AbstractAsSearchProfileModel.class);

		final PK pk2 = PK.fromLong(2);
		final AbstractAsSearchProfileModel searchProfile2 = mock(AbstractAsSearchProfileModel.class);

		when(searchProfile1.getPk()).thenReturn(pk1);
		when(modelService.get(pk1)).thenReturn(searchProfile1);

		when(searchProfile2.getPk()).thenReturn(pk2);
		when(modelService.get(pk2)).thenReturn(searchProfile2);

		when(sessionService.getAttribute(DefaultAsSearchProfileActivationService.CURRENT_SEARCH_PROFILES))
				.thenReturn(Arrays.asList(pk1, pk2));

		// when
		final List<AbstractAsSearchProfileModel> searchProfiles = asSearchProfileActivationService
				.getActiveSearchProfilesForContext(context);

		// then
		assertNotNull(searchProfiles);
		assertEquals(2, searchProfiles.size());
		assertEquals(searchProfiles.get(0), searchProfile1);
		assertEquals(searchProfiles.get(1), searchProfile2);
	}

	@Test
	public void getActiveSearchProfilesForContextFromStrategiesWithSingleStrategy()
	{
		// given
		final AbstractAsSearchProfileModel searchProfile1 = mock(AbstractAsSearchProfileModel.class);
		final AbstractAsSearchProfileModel searchProfile2 = mock(AbstractAsSearchProfileModel.class);

		final AsSearchProfileActivationStrategy activationStrategy = mock(AsSearchProfileActivationStrategy.class);
		final AsSearchProfileActivationMapping activationMapping = mock(AsSearchProfileActivationMapping.class);
		final AsSearchProfileActivationGroup activationGroup = new AsSearchProfileActivationGroup();
		activationGroup.setSearchProfiles(Arrays.asList(searchProfile1, searchProfile2));

		when(activationStrategy.getSearchProfileActivationGroup(context)).thenReturn(activationGroup);
		when(activationMapping.getActivationStrategy()).thenReturn(activationStrategy);

		when(asSearchProfileRegistry.getSearchProfileActivationMappings()).thenReturn(Collections.singletonList(activationMapping));

		// when
		final List<AbstractAsSearchProfileModel> searchProfiles = asSearchProfileActivationService
				.getActiveSearchProfilesForContext(context);

		// then
		assertNotNull(searchProfiles);
		assertEquals(2, searchProfiles.size());
		assertEquals(searchProfiles.get(0), searchProfile1);
		assertEquals(searchProfiles.get(1), searchProfile2);
	}

	@Test
	public void getActiveSearchProfilesForContextFromStrategiesWithMultipleStrategies()
	{
		// given
		final AbstractAsSearchProfileModel searchProfile1 = mock(AbstractAsSearchProfileModel.class);
		final AbstractAsSearchProfileModel searchProfile2 = mock(AbstractAsSearchProfileModel.class);

		final AsSearchProfileActivationStrategy activationStrategy1 = mock(AsSearchProfileActivationStrategy.class);
		final AsSearchProfileActivationMapping activationMapping1 = mock(AsSearchProfileActivationMapping.class);
		final AsSearchProfileActivationGroup activationGroup1 = new AsSearchProfileActivationGroup();
		activationGroup1.setSearchProfiles(Arrays.asList(searchProfile1, searchProfile2));

		when(activationStrategy1.getSearchProfileActivationGroup(context)).thenReturn(activationGroup1);
		when(activationMapping1.getActivationStrategy()).thenReturn(activationStrategy1);

		final AbstractAsSearchProfileModel searchProfile3 = mock(AbstractAsSearchProfileModel.class);
		final AbstractAsSearchProfileModel searchProfile4 = mock(AbstractAsSearchProfileModel.class);

		final AsSearchProfileActivationStrategy activationStrategy2 = mock(AsSearchProfileActivationStrategy.class);
		final AsSearchProfileActivationMapping activationMapping2 = mock(AsSearchProfileActivationMapping.class);
		final AsSearchProfileActivationGroup activationGroup2 = new AsSearchProfileActivationGroup();
		activationGroup2.setSearchProfiles(Arrays.asList(searchProfile3, searchProfile4));

		when(activationStrategy2.getSearchProfileActivationGroup(context)).thenReturn(activationGroup2);
		when(activationMapping2.getActivationStrategy()).thenReturn(activationStrategy2);

		when(asSearchProfileRegistry.getSearchProfileActivationMappings())
				.thenReturn(Arrays.asList(activationMapping1, activationMapping2));

		// when
		final List<AbstractAsSearchProfileModel> searchProfiles = asSearchProfileActivationService
				.getActiveSearchProfilesForContext(context);

		// then
		assertNotNull(searchProfiles);
		assertEquals(4, searchProfiles.size());
		assertEquals(searchProfiles.get(0), searchProfile1);
		assertEquals(searchProfiles.get(1), searchProfile2);
		assertEquals(searchProfiles.get(2), searchProfile3);
		assertEquals(searchProfiles.get(3), searchProfile4);
	}
}
