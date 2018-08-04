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
package com.sap.hybris.saprevenuecloudproduct.service.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import static java.lang.String.format;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;

import com.sap.hybris.saprevenuecloudproduct.dao.SapRevenueCloudProductDao;
import com.sap.hybris.saprevenuecloudproduct.service.SapRevenueCloudProductService;


/**
 * Default implementation for {@link SapRevenueCloudProductService}
 */
public class DefaultSapRevenueCloudProductService implements SapRevenueCloudProductService
{


	private SapRevenueCloudProductDao sapRevenueCloudProductDao;

	/**
	 * Get the subscription price plan for a specific priceplan ID
	 *
	 * @param pricePlanId
	 *           - price plan ID
	 *
	 * @return SubscriptionPricePlanModel
	 */
	@Override
	public SubscriptionPricePlanModel getSubscriptionPricePlanForId(final String pricePlanId,
			final CatalogVersionModel catalogVersion)
	{
		validateParameterNotNull(pricePlanId, "Price plan cannot be null");
		validateParameterNotNull(catalogVersion, "Catalog Version cannot be null");
		return getSapRevenueCloudProductDao().getSubscriptionPricePlanForId(pricePlanId, catalogVersion)
				.orElseThrow(() -> new ModelNotFoundException(format("Subscription Price plan with ID %s not found", pricePlanId)));

	}

	/**
	 * @return the sapRevenueCloudProductDao
	 */
	public SapRevenueCloudProductDao getSapRevenueCloudProductDao()
	{
		return sapRevenueCloudProductDao;
	}

	/**
	 * @param sapRevenueCloudProductDao
	 *           the sapRevenueCloudProductDao to set
	 */
	public void setSapRevenueCloudProductDao(final SapRevenueCloudProductDao sapRevenueCloudProductDao)
	{
		this.sapRevenueCloudProductDao = sapRevenueCloudProductDao;
	}



}
