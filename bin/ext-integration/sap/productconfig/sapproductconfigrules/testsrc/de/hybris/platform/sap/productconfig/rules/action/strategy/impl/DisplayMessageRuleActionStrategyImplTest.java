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
package de.hybris.platform.sap.productconfig.rules.action.strategy.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigModelFactoryImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSeverity;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSourceSubType;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class DisplayMessageRuleActionStrategyImplTest
{
	private DisplayMessageRuleActionStrategyImpl classUnderTest;

	@Before
	public void setUp()
	{
		classUnderTest = new DisplayMessageRuleActionStrategyImpl();
	}

	@Test
	public void testEsxecuteAction()
	{
		final boolean configChanged = classUnderTest.executeAction(null, null, null);
		assertFalse(configChanged);
	}

	@Test
	public void testIsActionPossible()
	{
		final boolean actionPossible = classUnderTest.isActionPossible(null, null, null);
		assertTrue(actionPossible);
	}

	@Test
	public void testCreateMessage()
	{
		classUnderTest.setConfigModelFactory(new ConfigModelFactoryImpl());
		final ProductConfigMessage message = classUnderTest.createMessage("123", "text", ProductConfigMessageSeverity.WARNING);
		assertSame(ProductConfigMessageSourceSubType.DISPLAY_MESSAGE, message.getSourceSubType());
	}
}
