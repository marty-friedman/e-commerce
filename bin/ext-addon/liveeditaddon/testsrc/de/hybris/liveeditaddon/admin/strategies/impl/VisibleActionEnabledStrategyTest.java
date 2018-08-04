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
package de.hybris.liveeditaddon.admin.strategies.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.servicelayer.services.CMSComponentService;
import de.hybris.liveeditaddon.admin.ComponentActionMenuRequestData;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Test suite for {@link de.hybris.liveeditaddon.admin.strategies.impl.VisibleActionEnabledStrategy}
 */
@UnitTest
public class VisibleActionEnabledStrategyTest
{
	private final static String COMPONENT_UID = "componentUid";
	private VisibleActionEnabledStrategy strategy;
	@Mock
	private CMSComponentService cmsComponentService;
	@Mock
	private AbstractCMSComponentModel abstractCMSComponentModel;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		strategy = new VisibleActionEnabledStrategy();
		strategy.setCmsComponentService(cmsComponentService);
	}

	@Test
	public void testIsEnabled() throws CMSItemNotFoundException
	{
		final ComponentActionMenuRequestData componentActionMenuRequestData = new ComponentActionMenuRequestData();
		componentActionMenuRequestData.setComponentUid(COMPONENT_UID);
		given(cmsComponentService.getAbstractCMSComponent(COMPONENT_UID)).willReturn(abstractCMSComponentModel);
		given(abstractCMSComponentModel.getVisible()).willReturn(Boolean.TRUE);

		Assert.assertTrue(strategy.isEnabled(componentActionMenuRequestData));
	}

	@Test
	public void testIsNotEnabled() throws CMSItemNotFoundException
	{
		final ComponentActionMenuRequestData componentActionMenuRequestData = new ComponentActionMenuRequestData();
		componentActionMenuRequestData.setComponentUid(COMPONENT_UID);
		given(cmsComponentService.getAbstractCMSComponent(COMPONENT_UID)).willReturn(abstractCMSComponentModel);
		given(abstractCMSComponentModel.getVisible()).willReturn(Boolean.FALSE);

		Assert.assertFalse(strategy.isEnabled(componentActionMenuRequestData));
	}

	@Test
	public void testIsVisible()
	{
		final ComponentActionMenuRequestData componentActionMenuRequestData = new ComponentActionMenuRequestData();
		Assert.assertTrue(strategy.isVisible(componentActionMenuRequestData, false));
	}

}
