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
package de.hybris.platform.sap.productconfig.runtime.cps.impl;

import de.hybris.platform.sap.productconfig.runtime.cps.pricing.PricingHandler;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceSummaryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceValueUpdateModel;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Facilitates pricing calls for CPS engine
 */
public class CPSPricingProvider implements PricingProvider
{
	private PricingHandler pricingHandler;

	@Override
	public PriceSummaryModel getPriceSummary(final String configId) throws PricingEngineException
	{
		return getPricingHandler().getPriceSummary(configId);
	}

	@Override
	public boolean isActive()
	{
		return true;
	}

	protected PricingHandler getPricingHandler()
	{
		return pricingHandler;
	}

	/**
	 * @param pricingHandler
	 *           Bean that handles REST call, delta price calculation and caching
	 */
	@Required
	public void setPricingHandler(final PricingHandler pricingHandler)
	{
		this.pricingHandler = pricingHandler;
	}

	@Override
	public void fillValuePrices(final List<PriceValueUpdateModel> updateModels, final String kbId) throws PricingEngineException
	{
		for (final PriceValueUpdateModel updateModel : updateModels)
		{
			getPricingHandler().fillValuePrices(kbId, updateModel);
		}
	}

	@Override
	public void fillValuePrices(final ConfigModel configModel) throws PricingEngineException
	{
		final InstanceModel rootInstance = configModel.getRootInstance();
		fillValuePricesForInstance(rootInstance, configModel.getKbId());
	}

	protected void fillValuePricesForInstance(final InstanceModel instance, final String kbId) throws PricingEngineException
	{

		for (final CsticModel cstic : instance.getCstics())
		{
			getPricingHandler().fillValuePrices(kbId, cstic);
		}
		for (final InstanceModel subInstance : instance.getSubInstances())
		{
			fillValuePricesForInstance(subInstance, kbId);
		}
	}
}
