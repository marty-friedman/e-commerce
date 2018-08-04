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
package de.hybris.platform.sap.productconfig.frontend.controllers;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class AbstractConfigurationOverviewControllerTest
{

	private AbstractConfigurationOverviewController classUnderTest;

	@Before
	public void setUp()
	{
		classUnderTest = new AbstractConfigurationOverviewController();
	}

	@Test
	public void testGetErrorCountForUi_nonZero()
	{
		final Object errorCountForUi = classUnderTest.getErrorCountForUi(1);
		assertEquals("1", errorCountForUi.toString());
	}

	@Test
	public void testGetErrorCountForUi_zero()
	{
		final Object errorCountForUi = classUnderTest.getErrorCountForUi(0);
		assertEquals(" ", errorCountForUi.toString());
	}
}
