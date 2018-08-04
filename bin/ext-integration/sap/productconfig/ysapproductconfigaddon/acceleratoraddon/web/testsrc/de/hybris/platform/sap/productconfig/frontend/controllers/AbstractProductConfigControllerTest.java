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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.servicelayer.exceptions.BusinessException;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class AbstractProductConfigControllerTest extends AbstractProductConfigControllerBaseTest
{
	private static final String CPQ_ITEM_PK = "cpqItemKey";

	private AbstractProductConfigController classUnderTest;

	@Mock
	private AbstractOrderData orderData;
	private List<OrderEntryData> orderEntryDataList;
	@Mock
	private OrderEntryData cpqOrderEntry;
	@Mock
	private OrderEntryData standardOrderEntry;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		//type of classUnderTest is Abstract ->  use instance of child class in unit test
		classUnderTest = new AbstractConfigurationOverviewController();
		injectMocks(classUnderTest);

	}

	protected void prepareOrderDataTwoEntries(final String pk1, final String pk2)
	{
		orderEntryDataList = new ArrayList();
		orderEntryDataList.add(cpqOrderEntry);
		orderEntryDataList.add(standardOrderEntry);
		Mockito.when(orderData.getEntries()).thenReturn(orderEntryDataList);
		Mockito.when(cpqOrderEntry.getItemPK()).thenReturn(pk1);
		Mockito.when(standardOrderEntry.getItemPK()).thenReturn(pk2);
	}

	@Test
	public void testGetOrderEntry() throws BusinessException
	{
		prepareOrderDataTwoEntries(CPQ_ITEM_PK, null);
		final OrderEntryData result = classUnderTest.getOrderEntry(CPQ_ITEM_PK, orderData);
		assertNotNull(result);
		assertEquals(cpqOrderEntry, result);
	}

	@Test
	public void testGetOrderEntryNonConfigurableItems()
	{
		prepareOrderDataTwoEntries(null, null);

		try
		{
			classUnderTest.getOrderEntry(CPQ_ITEM_PK, orderData);
		}
		catch (final BusinessException e)
		{
			assertTrue(e.getCause() instanceof NoSuchElementException);
		}
	}
}
