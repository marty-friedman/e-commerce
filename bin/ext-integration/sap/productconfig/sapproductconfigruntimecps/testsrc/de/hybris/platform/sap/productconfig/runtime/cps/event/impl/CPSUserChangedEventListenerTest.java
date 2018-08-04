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
package de.hybris.platform.sap.productconfig.runtime.cps.event.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.cache.CPSSessionCache;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@SuppressWarnings("javadoc")
@UnitTest
public class CPSUserChangedEventListenerTest
{
	private CPSUserChangedEventListener classUnderTest;
	private CPSUserChangedEventListenerForTest classUnderTestWithMock;
	@Mock
	private CPSSessionCache sessionCache;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new CPSUserChangedEventListener();
		classUnderTestWithMock = new CPSUserChangedEventListenerForTest();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testLookupNotDefined()
	{
		classUnderTest.onEvent(null);
	}

	@Test
	public void testCacheIsPurged()
	{
		classUnderTestWithMock.onEvent(null);
		Mockito.verify(sessionCache).purgePrices();
	}

	public class CPSUserChangedEventListenerForTest extends CPSUserChangedEventListener
	{
		@Override
		protected CPSSessionCache getCPSSessionCache()
		{
			return sessionCache;
		}
	}
}
