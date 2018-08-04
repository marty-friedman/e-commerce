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
package de.hybris.platform.sap.productconfig.runtime.cps.populator.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingItemInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSItem;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSPricingQuantity;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSQuantity;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSVariantCondition;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;


@SuppressWarnings("javadoc")
@UnitTest
public class PricingItemInputPopulatorTest
{
	private static final String ISO_UNIT = "ISO unit";
	private PricingItemInputPopulator classUnderTest;
	private PricingItemInput target;
	private CPSItem item;
	private CPSVariantCondition vc1;


	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new PricingItemInputPopulator();
		target = new PricingItemInput();
		item = new CPSItem();
		item.setVariantConditions(new ArrayList<CPSVariantCondition>());
		vc1 = new CPSVariantCondition();
		vc1.setKey("vc1");
		item.getVariantConditions().add(vc1);
		item.setQuantity(new CPSQuantity());
		item.getQuantity().setUnit(ISO_UNIT);
	}


	@Test
	public void testFillVariantConditions()
	{
		final CPSVariantCondition vc2 = new CPSVariantCondition();
		item.getVariantConditions().add(vc2);
		vc2.setKey("vc2");
		classUnderTest.fillVariantConditions(item, target);
		assertEquals(2, target.getVariantConditions().size());
		assertTrue(isVariantConditionPresent(vc1, target.getVariantConditions()));
		assertTrue(isVariantConditionPresent(vc2, target.getVariantConditions()));
	}


	@Test(expected = IllegalStateException.class)
	public void testFillVariantConditionsEmptyKey()
	{
		vc1.setKey(" ");
		classUnderTest.fillVariantConditions(item, target);
	}

	protected boolean isVariantConditionPresent(final CPSVariantCondition present, final List<CPSVariantCondition> conditions)
	{
		for (final CPSVariantCondition condition : conditions)
		{
			if (condition.equals(present))
			{
				return true;
			}
		}
		return false;
	}

	@Test
	public void testCalculateQuantityNoFixedQuantity()
	{
		final CPSItem cpsItem1 = new CPSItem();
		final CPSItem cpsItem2 = new CPSItem();
		final CPSItem cpsItem3 = new CPSItem();
		prepareItems(cpsItem1, cpsItem2, cpsItem3);

		final CPSPricingQuantity calculatedQuantity = classUnderTest.calculateQuantity(cpsItem3);
		final BigDecimal expected = BigDecimal.valueOf(1.331).setScale(3, RoundingMode.HALF_UP);
		assertEquals(0, calculatedQuantity.getValue().compareTo(expected));
		assertEquals("ST", calculatedQuantity.getUnit());
	}

	@Test
	public void testCalculateQuantityFixedQuantity()
	{
		final CPSItem cpsItem1 = new CPSItem();
		final CPSItem cpsItem2 = new CPSItem();
		final CPSItem cpsItem3 = new CPSItem();
		prepareItems(cpsItem1, cpsItem2, cpsItem3);

		cpsItem2.setFixedQuantity(true);

		final CPSPricingQuantity calculatedQuantity = classUnderTest.calculateQuantity(cpsItem3);
		final BigDecimal expected = BigDecimal.valueOf(1.21).setScale(3, RoundingMode.HALF_UP);
		assertEquals(0, calculatedQuantity.getValue().compareTo(expected));
		assertEquals("ST", calculatedQuantity.getUnit());
	}

	private void prepareItems(final CPSItem cpsItem1, final CPSItem cpsItem2, final CPSItem cpsItem3)
	{
		final CPSQuantity quantity = new CPSQuantity();
		quantity.setUnit("ST");
		quantity.setValue(1.1);

		cpsItem1.setQuantity(quantity);
		cpsItem2.setQuantity(quantity);
		cpsItem3.setQuantity(quantity);

		cpsItem3.setParentItem(cpsItem2);
		cpsItem2.setParentItem(cpsItem1);
	}
}
