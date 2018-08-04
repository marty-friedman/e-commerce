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
package de.hybris.platform.personalizationservices.service;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.personalizationservices.action.CxActionResultService;
import de.hybris.platform.personalizationservices.action.CxActionService;
import de.hybris.platform.personalizationservices.customization.CxCustomizationService;
import de.hybris.platform.personalizationservices.data.CxAbstractActionResult;
import de.hybris.platform.personalizationservices.model.CxAbstractActionModel;
import de.hybris.platform.personalizationservices.model.CxCustomizationModel;
import de.hybris.platform.personalizationservices.model.CxSegmentModel;
import de.hybris.platform.personalizationservices.model.CxUserToSegmentModel;
import de.hybris.platform.personalizationservices.model.CxVariationModel;
import de.hybris.platform.personalizationservices.process.CxProcessService;
import de.hybris.platform.personalizationservices.service.impl.DefaultCxService;
import de.hybris.platform.personalizationservices.variation.CxVariationService;
import de.hybris.platform.servicelayer.action.ActionService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Lists;


@UnitTest
public class DefaultCxServiceTest
{
	private final DefaultCxService service = new DefaultCxService();

	@Mock
	private ActionService actionService;

	@Mock
	private CxCustomizationService cxCustomizationService;

	@Mock
	private CxVariationService cxVariationService;

	@Mock
	private CxActionResultService cxActionResultService;

	@Mock
	private CxActionService cxActionService;

	@Mock
	private CatalogVersionModel catalogVersion;

	@Mock
	private CxProcessService cxProcessService;

	@Before
	public void initMocks()
	{
		MockitoAnnotations.initMocks(this);
		service.setActionService(actionService);
		service.setCxVariationService(cxVariationService);
		service.setCxActionResultService(cxActionResultService);
		service.setCxActionService(cxActionService);
		service.setCxProcessService(cxProcessService);

		BDDMockito.given(cxCustomizationService.getCustomizations(catalogVersion))
				.willReturn(Lists.newArrayList((CxCustomizationModel) null));
	}

	@Test
	public void shouldCalculateAndStorePersonalization()
	{
		final UserModel user = new UserModel();
		final ArrayList<CxSegmentModel> segments = new ArrayList<>();
		setSegments(user, segments);

		final List<CxVariationModel> variations = new ArrayList<>();
		final List<CxAbstractActionModel> actions = new ArrayList<>();
		final CxAbstractActionModel action = new CxAbstractActionModel();
		actions.add(action);

		BDDMockito.given(cxVariationService.getActiveVariations(user, catalogVersion)).willReturn(variations);
		BDDMockito.given(cxActionService.getActionsForVariations(variations)).willReturn(actions);

		service.calculateAndStorePersonalization(user, catalogVersion);

		BDDMockito.verify(actionService, BDDMockito.times(1)).prepareAndTriggerAction(BDDMockito.eq(action),
				BDDMockito.any(Map.class));
		BDDMockito.verify(cxActionResultService, BDDMockito.times(1)).storeActionResults(BDDMockito.eq(user),
				BDDMockito.eq(catalogVersion), BDDMockito.any());

	}

	@Test
	public void shouldClearPersonalizationInSession()
	{
		final UserModel user = new UserModel();

		service.clearPersonalizationInSession(user, catalogVersion);

		BDDMockito.verify(cxActionResultService, BDDMockito.times(1)).clearActionResultsInSession(user, catalogVersion);
	}

	@Test
	public void shouldLoadPersonalizationInSession()
	{
		final UserModel user = new UserModel();
		final List<CatalogVersionModel> cvs = Arrays.asList(catalogVersion);

		service.loadPersonalizationInSession(user, cvs);

		BDDMockito.verify(cxActionResultService, BDDMockito.times(1)).loadActionResultsInSession(user, cvs);
	}

	@Test
	public void shouldPreviewPersonalizationInSession()
	{
		final UserModel user = new UserModel();
		final List<CxVariationModel> variations = new ArrayList<>();
		final List<CxAbstractActionModel> actions = new ArrayList<>();
		final CxAbstractActionModel action = new CxAbstractActionModel();
		actions.add(action);

		BDDMockito.given(cxActionService.getActionsForVariations(variations)).willReturn(actions);

		service.calculateAndLoadPersonalizationInSession(user, catalogVersion, variations);

		BDDMockito.verify(cxActionService).getActionsForVariations(variations);
		BDDMockito.verify(actionService).prepareAndTriggerAction(BDDMockito.eq(action), BDDMockito.any());
		BDDMockito.verify(cxActionResultService).setActionResultsInSession(BDDMockito.eq(user), BDDMockito.eq(catalogVersion),
				BDDMockito.anyList());
	}

	@Test
	public void shouldGetActionResults()
	{
		final UserModel user = new UserModel();
		final List<CxAbstractActionResult> results = new ArrayList<>();

		BDDMockito.given(cxActionResultService.getActionResults(user, catalogVersion)).willReturn(results);

		final List<CxAbstractActionResult> actionResultsFromSession = service.getActionResultsFromSession(user, catalogVersion);

		BDDMockito.verify(cxActionResultService, BDDMockito.times(1)).getActionResults(user, catalogVersion);
		Assert.assertSame(results, actionResultsFromSession);
	}


	@Test
	public void shouldCalculateAndLoadPersonalizationInSession()
	{
		final UserModel user = new UserModel();
		final List<CxVariationModel> variations = new ArrayList<>();
		final List<CxAbstractActionModel> actions = new ArrayList<>();
		final CxAbstractActionModel action = new CxAbstractActionModel();
		actions.add(action);

		BDDMockito.given(cxActionService.getActionsForVariations(variations)).willReturn(actions);
		BDDMockito.given(cxVariationService.getActiveVariations(user, catalogVersion)).willReturn(variations);

		service.calculateAndLoadPersonalizationInSession(user, catalogVersion);

		BDDMockito.verify(cxActionService).getActionsForVariations(variations);
		BDDMockito.verify(actionService).prepareAndTriggerAction(BDDMockito.eq(action), BDDMockito.any());
		BDDMockito.verify(cxActionResultService).setActionResultsInSession(BDDMockito.eq(user), BDDMockito.eq(catalogVersion),
				BDDMockito.anyList());

	}


	private void setSegments(final UserModel user, final List<CxSegmentModel> segments)
	{
		user.setUserToSegments(new ArrayList<CxUserToSegmentModel>());

		for (final CxSegmentModel segment : segments)
		{
			final CxUserToSegmentModel uts = new CxUserToSegmentModel();
			uts.setSegment(segment);
			uts.setUser(user);
			uts.setAffinity(BigDecimal.ONE);
			segment.setUserToSegments(new ArrayList<CxUserToSegmentModel>());
			segment.getUserToSegments().add(uts);
			user.getUserToSegments().add(uts);
		}
	}
}
