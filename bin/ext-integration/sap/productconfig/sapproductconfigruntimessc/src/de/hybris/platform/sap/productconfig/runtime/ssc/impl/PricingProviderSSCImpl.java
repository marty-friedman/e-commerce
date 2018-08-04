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

import de.hybris.platform.sap.productconfig.runtime.interf.impl.DefaultPricingProviderImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceSummaryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceValueUpdateModel;

import java.util.List;


/**
 * Default class for the pricing provider, not used in ssc
 *
 * @deprecated Since 6.6. Use
 *             {@linkde.hybris.platform.sap.productconfig.runtime.interf.impl.DefaultPricingProviderImpl)} instead.
 */
@Deprecated
public class PricingProviderSSCImpl extends DefaultPricingProviderImpl
{
	private static final String THE_PRICING_PROVIDER_IS_NOT_SUPPORTED_IN_SSC = "The pricing provider is not supported in ssc";

	@Override
	public PriceSummaryModel getPriceSummary(final String configId)
	{
		throw new UnsupportedOperationException(THE_PRICING_PROVIDER_IS_NOT_SUPPORTED_IN_SSC);
	}

	@Override
	public boolean isActive()
	{
		return false;
	}

	@Override
	public void fillValuePrices(final List<PriceValueUpdateModel> updateModels, final String kbId)
	{
		throw new UnsupportedOperationException(THE_PRICING_PROVIDER_IS_NOT_SUPPORTED_IN_SSC);
	}

	@Override
	public void fillValuePrices(final ConfigModel configModel)
	{
		throw new UnsupportedOperationException(THE_PRICING_PROVIDER_IS_NOT_SUPPORTED_IN_SSC);
	}
}
