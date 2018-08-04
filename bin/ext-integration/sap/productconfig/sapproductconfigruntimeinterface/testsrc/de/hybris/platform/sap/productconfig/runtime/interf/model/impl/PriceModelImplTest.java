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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.testframework.Assert;

import java.math.BigDecimal;

import org.junit.Test;


@UnitTest
public class PriceModelImplTest
{
	private final PriceModel model = new PriceModelImpl();

	@Test
	public void testPriceTest()
	{
		final String currency = "USD";
		final BigDecimal priceValue = BigDecimal.ONE;

		model.setCurrency(currency);
		model.setPriceValue(priceValue);

		assertEquals(currency, model.getCurrency());
		assertEquals(priceValue, model.getPriceValue());
	}

}
