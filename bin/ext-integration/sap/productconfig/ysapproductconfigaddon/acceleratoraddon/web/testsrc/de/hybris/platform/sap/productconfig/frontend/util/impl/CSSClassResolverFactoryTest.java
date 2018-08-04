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
package de.hybris.platform.sap.productconfig.frontend.util.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.frontend.util.CSSClassResolver;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class CSSClassResolverFactoryTest
{

	private CsticData cstic;
	private UiGroupData group;
	@Mock
	private CSSClassResolver resolver;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		CSSClassResolverFactory.setResolver(resolver);
		cstic = new CsticData();
		group = new UiGroupData();


		Mockito.when(resolver.getLabelStyleClass(cstic)).thenReturn("labelStyle");
		Mockito.when(resolver.getValueStyleClass(cstic)).thenReturn("valueStyle");
		Mockito.when(resolver.getGroupStyleClass(group, false)).thenReturn("groupStyle");
	}

	@Test
	public void testGetLabelStyleClassForCstic()
	{
		final String labelStyleClassForCstic = CSSClassResolverFactory.getLabelStyleClassForCstic(cstic);
		assertEquals("labelStyle", labelStyleClassForCstic);
	}

	@Test
	public void testGetValueStyleClassForCstic()
	{
		final String valueStyleClassForCstic = CSSClassResolverFactory.getValueStyleClassForCstic(cstic);
		assertEquals("valueStyle", valueStyleClassForCstic);
	}

}
