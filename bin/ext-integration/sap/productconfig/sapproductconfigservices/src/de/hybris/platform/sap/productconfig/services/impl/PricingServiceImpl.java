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
package de.hybris.platform.sap.productconfig.services.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.PricingConfigurationParameter;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.ProviderFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceSummaryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceValueUpdateModel;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.intf.PricingService;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Provides price information for configuration
 *
 */
public class PricingServiceImpl implements PricingService
{
	private static final Logger LOG = Logger.getLogger(PricingServiceImpl.class);

	private ProviderFactory providerFactory;
	private SessionAccessService sessionAccessService;
	private PricingConfigurationParameter pricingConfigurationParameter;

	protected PricingConfigurationParameter getPricingConfigurationParameter()
	{
		return pricingConfigurationParameter;
	}

	@Override
	public PriceSummaryModel getPriceSummary(final String configId)
	{
		PriceSummaryModel priceSummaryModel = getSessionAccessService().getPriceSummaryState(configId);
		if (priceSummaryModel == null)
		{
			final ConfigModel configModel = getSessionAccessService().getConfigurationModelEngineState(configId);
			try
			{
				priceSummaryModel = getProviderFactory().getPricingProvider().getPriceSummary(configId);
				configModel.setPricingError(false);
				getSessionAccessService().setPriceSummaryState(configId, priceSummaryModel);
			}
			catch (final PricingEngineException e)
			{
				configModel.setPricingError(true);
				LOG.error("error when retrieving price summary from provider", e);
			}
		}
		return priceSummaryModel;

	}

	@Override
	public void fillValuePrices(final List<PriceValueUpdateModel> updateModels, final String kbId)
	{
		try
		{
			getProviderFactory().getPricingProvider().fillValuePrices(updateModels, kbId);
		}
		catch (final PricingEngineException e)
		{
			LOG.error("ignore errors when filling value prices", e);
		}
	}


	protected ProviderFactory getProviderFactory()
	{
		return providerFactory;
	}


	/**
	 * @param providerFactory
	 *           the providerFactory to set
	 */
	@Required
	public void setProviderFactory(final ProviderFactory providerFactory)
	{
		this.providerFactory = providerFactory;
	}


	@Override
	public boolean isActive()
	{
		return getProviderFactory().getPricingProvider().isActive() && getPricingConfigurationParameter().isPricingSupported();
	}

	protected SessionAccessService getSessionAccessService()
	{
		return sessionAccessService;
	}

	/**
	 * Setter for sessionAccessService
	 *
	 * @param sessionAccessService
	 *           sessionAccessService to set
	 */
	@Required
	public void setSessionAccessService(final SessionAccessService sessionAccessService)
	{
		this.sessionAccessService = sessionAccessService;
	}

	@Override
	public void fillOverviewPrices(final ConfigModel configModel)
	{
		final PriceSummaryModel summary = getPriceSummary(configModel.getId());
		if (summary != null)
		{
			fillConfigPrices(summary, configModel);
		}
		try
		{
			getProviderFactory().getPricingProvider().fillValuePrices(configModel);
		}
		catch (final PricingEngineException e)
		{
			LOG.error("ignore error when filling value prices for overview", e);
		}
	}

	protected void fillConfigPrices(final PriceSummaryModel summary, final ConfigModel configModel)
	{
		configModel.setBasePrice(summary.getBasePrice());
		configModel.setCurrentTotalPrice(summary.getCurrentTotalPrice());
		configModel.setSelectedOptionsPrice(summary.getSelectedOptionsPrice());
	}

	/**
	 * @param pricingConfigurationParameter
	 */
	public void setPricingConfigurationParameter(final PricingConfigurationParameter pricingConfigurationParameter)
	{
		this.pricingConfigurationParameter = pricingConfigurationParameter;

	}



}
