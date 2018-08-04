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
package de.hybris.platform.sap.productconfig.runtime.interf.model.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSeverity;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSource;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSourceSubType;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class ProductConfigMessageImplTest
{

	private ProductConfigMessageImpl classUnderTest;

	@Before
	public void setUp()
	{
		classUnderTest = new ProductConfigMessageImpl("test message", "123", ProductConfigMessageSeverity.INFO,
				ProductConfigMessageSource.ENGINE, ProductConfigMessageSourceSubType.DEFAULT);
	}


	@Test
	public void testDataNotModified()
	{
		assertNotNull(classUnderTest);
		assertEquals("test message", classUnderTest.getMessage());
		assertEquals("123", classUnderTest.getKey());
		assertSame(ProductConfigMessageSeverity.INFO, classUnderTest.getSeverity());
		assertSame(ProductConfigMessageSource.ENGINE, classUnderTest.getSource());
	}

	@Test
	public void testEqualsSameData()
	{
		final ProductConfigMessageImpl messageWithSameData = new ProductConfigMessageImpl("test message", "123",
				ProductConfigMessageSeverity.INFO, ProductConfigMessageSource.ENGINE, ProductConfigMessageSourceSubType.DEFAULT);
		assertTrue(classUnderTest.equals(messageWithSameData));
		assertEquals(classUnderTest.hashCode(), messageWithSameData.hashCode());
	}

	@Test
	public void testEqualsSameKey()
	{
		final ProductConfigMessageImpl messageWithSameKey = new ProductConfigMessageImpl("test message aaa", "123",
				ProductConfigMessageSeverity.WARNING, ProductConfigMessageSource.ENGINE, null);
		assertTrue(classUnderTest.equals(messageWithSameKey));
		assertEquals(classUnderTest.hashCode(), messageWithSameKey.hashCode());
	}

	@Test
	public void testEqualsOtherKey()
	{
		final ProductConfigMessageImpl messageWithDifferentKey = new ProductConfigMessageImpl("test message", "456",
				ProductConfigMessageSeverity.WARNING, ProductConfigMessageSource.ENGINE, ProductConfigMessageSourceSubType.DEFAULT);
		assertFalse(classUnderTest.equals(messageWithDifferentKey));

	}
}
