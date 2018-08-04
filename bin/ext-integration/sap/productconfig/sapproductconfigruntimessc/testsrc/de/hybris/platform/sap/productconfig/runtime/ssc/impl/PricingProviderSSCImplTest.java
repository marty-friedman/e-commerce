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
package de.hybris.platform.sap.productconfig.runtime.ssc.impl;

import static org.junit.Assert.assertFalse;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceValueUpdateModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;


/**
 * Pricing Provider test, the provider is not implemented in case of ssc. The class under test throws exceptions
 *
 */
@SuppressWarnings("javadoc")
@UnitTest
public class PricingProviderSSCImplTest
{
	private final PricingProvider provider = new PricingProviderSSCImpl();

	@Test(expected = UnsupportedOperationException.class)
	public void testGetPriceSummary() throws PricingEngineException
	{
		final String configId = "1";
		provider.getPriceSummary(configId);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testFillValuePrices_PriceValueUpdateModel() throws PricingEngineException
	{

		final List<PriceValueUpdateModel> updateModels = new ArrayList<>();
		updateModels.add(new PriceValueUpdateModel());

		final String kbId = "123";

		provider.fillValuePrices(updateModels, kbId);
	}

	@Test
	public void testProviderIsNotActive()
	{
		assertFalse(provider.isActive());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testFillValuePrices() throws PricingEngineException
	{
		provider.fillValuePrices(new ConfigModelImpl());
	}

}
