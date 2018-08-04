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
package de.hybris.platform.sap.productconfig.services.strategies.impl;

import de.hybris.platform.commerceservices.order.CommerceCartRestoration;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;


/**
 * CPQ implementation of {@link CommerceCartRestorationStrategy}. Takes care of releasing session artifacts
 */
public class ProductConfigurationSaveCartRestorationStrategyImpl implements CommerceCartRestorationStrategy
{

	private CommerceCartRestorationStrategy commerceSaveCartRestorationStrategy;
	private SessionAccessService sessionAccessService;



	@Override
	public CommerceCartRestoration restoreCart(final CommerceCartParameter parameters) throws CommerceCartRestorationException
	{
		releaseSessionArtifacts(parameters);
		return commerceSaveCartRestorationStrategy.restoreCart(parameters);
	}

	protected void releaseSessionArtifacts(final CommerceCartParameter parameters)
	{
		final CartModel cart = parameters.getCart();
		if (cart == null)
		{
			throw new IllegalStateException("No saved cart available for restore");
		}
		cart.getEntries().stream().forEach(entry -> releaseSessionArtifactsForEntry(entry));
	}


	protected void releaseSessionArtifactsForEntry(final AbstractOrderEntryModel entry)
	{
		if (entry.getExternalConfiguration() != null)
		{
			sessionAccessService.removeSessionArtifactsForCartEntry(entry.getPk().toString(), entry.getProduct().getCode());
		}
	}

	/**
	 * Only used for unit tests
	 *
	 * @param sessionAccessService
	 *           the sessionAccessService to set
	 */
	public void setSessionAccessService(final SessionAccessService sessionAccessService)
	{
		this.sessionAccessService = sessionAccessService;
	}


	/**
	 * @return the sessionAccessService
	 */
	protected SessionAccessService getSessionAccessService()
	{
		return sessionAccessService;
	}

	/**
	 * @return the commerceCartRestorationStrategy
	 */
	public CommerceCartRestorationStrategy getCommerceSaveCartRestorationStrategy()
	{
		return commerceSaveCartRestorationStrategy;
	}

	/**
	 * @param commerceCartRestorationStrategy
	 *           the commerceCartRestorationStrategy to set
	 */
	public void setCommerceSaveCartRestorationStrategy(final CommerceCartRestorationStrategy commerceCartRestorationStrategy)
	{
		this.commerceSaveCartRestorationStrategy = commerceCartRestorationStrategy;
	}




}
