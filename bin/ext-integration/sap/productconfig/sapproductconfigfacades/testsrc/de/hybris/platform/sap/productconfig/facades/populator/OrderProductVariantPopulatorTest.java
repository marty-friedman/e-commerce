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
package de.hybris.platform.sap.productconfig.facades.populator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.FeatureData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;


@UnitTest
public class OrderProductVariantPopulatorTest
{
	/**
	 *
	 */
	private static final String ITEM_PK = "1";
	@Spy
	private OrderProductVariantPopulator classUnderTest;
	private List<FeatureData> features;
	@Mock
	private AbstractOrderEntryModel sourceEntry;
	private List<OrderEntryData> targetList;
	@Mock
	private OrderEntryData entry0;
	@Mock
	private OrderEntryData entry1;
	private ProductData productData;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new OrderProductVariantPopulator();
		targetList = new ArrayList<>();
		features = new ArrayList<>();
		productData = new ProductData();
		productData.setBaseOptions(new ArrayList<>());
		Mockito.when(sourceEntry.getEntryNumber()).thenReturn(Integer.valueOf(0));
		Mockito.when(sourceEntry.getPk()).thenReturn(PK.parse(ITEM_PK));
		Mockito.when(entry0.getEntryNumber()).thenReturn(Integer.valueOf(0));
		Mockito.when(entry1.getEntryNumber()).thenReturn(Integer.valueOf(1));
		Mockito.when(entry0.getProduct()).thenReturn(productData);
	}

	@Test
	public void testWriteToTargetEntryEmptyList()
	{
		classUnderTest.writeToTargetEntry(targetList, sourceEntry, features);
		assertTrue(targetList.isEmpty());
	}

	@Test
	public void testWriteToTargetEntryNoMatchingEntry()
	{
		targetList.add(entry1);
		classUnderTest.writeToTargetEntry(targetList, sourceEntry, features);
		assertEquals(1, targetList.size());
		verify(entry1, times(0)).setItemPK(ITEM_PK);
	}

	@Test
	public void testWriteToTargetEntryMatchingEntry()
	{
		targetList.add(entry0);
		classUnderTest.writeToTargetEntry(targetList, sourceEntry, features);
		assertEquals(1, targetList.size());
		verify(entry0).setItemPK(ITEM_PK);
	}
}
