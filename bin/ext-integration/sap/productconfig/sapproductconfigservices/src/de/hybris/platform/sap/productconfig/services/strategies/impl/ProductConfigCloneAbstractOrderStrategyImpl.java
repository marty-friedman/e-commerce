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

import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.order.strategies.ordercloning.CloneAbstractOrderStrategy;
import de.hybris.platform.order.strategies.ordercloning.impl.DefaultCloneAbstractOrderStrategy;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;


/**
 * CPQ specific default implementation of {@link CloneAbstractOrderStrategy}. Adds configuration session management on
 * top of the default implementation {@link DefaultCloneAbstractOrderStrategy}
 */
public class ProductConfigCloneAbstractOrderStrategyImpl implements CloneAbstractOrderStrategy
{

	private DefaultCloneAbstractOrderStrategy defaultCloneAbstractOrderStrategy;
	private SessionAccessService sessionAccessService;
	private ProductConfigurationService productConfigurationService;

	/**
	 * @return the productConfigurationService
	 */
	protected ProductConfigurationService getProductConfigurationService()
	{
		return productConfigurationService;
	}


	/**
	 * @return the sessionAccessService
	 */
	protected SessionAccessService getSessionAccessService()
	{
		return sessionAccessService;
	}


	@Override
	public <T extends AbstractOrderModel> T clone(final ComposedTypeModel orderType, final ComposedTypeModel entryType,
			final AbstractOrderModel original, final String code, final Class abstractOrderClassResult,
			final Class abstractOrderEntryClassResult)
	{
		//Whenever a cart is cloned into a quote: release all configuration sessions attached to the cart
		if (isCleanUpNeeded(original, abstractOrderClassResult))
		{
			cleanUp(original);
		}
		return defaultCloneAbstractOrderStrategy.clone(orderType, entryType, original, code, abstractOrderClassResult,
				abstractOrderEntryClassResult);
	}


	protected boolean isCleanUpNeeded(final AbstractOrderModel original, final Class abstractOrderClassResult)
	{
		return QuoteModel.class.isAssignableFrom(abstractOrderClassResult) && original instanceof CartModel;
	}

	protected boolean isCleanUpNeeded(final AbstractOrderModel original)
	{
		return original instanceof CartModel;
	}

	protected void cleanUp(final AbstractOrderModel original)
	{
		if (original == null)
		{
			throw new IllegalArgumentException("Abstract Order to clean up must not be null");
		}
		original.getEntries().stream().forEach(entry -> cleanUpEntry(entry));
	}

	protected void cleanUpEntry(final AbstractOrderEntryModel entry)
	{
		final PK pk = entry.getPk();
		if (pk != null)
		{
			final String cartEntryKey = pk.toString();
			final String configId = getSessionAccessService().getConfigIdForCartEntry(cartEntryKey);
			if (!StringUtils.isEmpty(configId))
			{
				getProductConfigurationService().releaseSession(configId);
				getSessionAccessService().removeSessionArtifactsForCartEntry(cartEntryKey, entry.getProduct().getCode());
			}
		}
	}


	@Override
	public <T extends AbstractOrderEntryModel> Collection<T> cloneEntries(final ComposedTypeModel entriesType,
			final AbstractOrderModel original)
	{
		//Whenever a cart is cloned into a quote: release all configuration sessions attached to the cart
		if (isCleanUpNeeded(original))
		{
			cleanUp(original);
		}
		return defaultCloneAbstractOrderStrategy.cloneEntries(entriesType, original);
	}

	/**
	 * @param defaultCloneAbstractOrderStrategy
	 */
	public void setDefaultCloneAbstractOrderStrategy(final DefaultCloneAbstractOrderStrategy defaultCloneAbstractOrderStrategy)
	{
		this.defaultCloneAbstractOrderStrategy = defaultCloneAbstractOrderStrategy;

	}

	/**
	 * @return the defaultCloneAbstractOrderStrategy
	 */
	protected DefaultCloneAbstractOrderStrategy getDefaultCloneAbstractOrderStrategy()
	{
		return defaultCloneAbstractOrderStrategy;
	}


	/**
	 * @param sessionAccessService
	 */
	public void setSessionAccessService(final SessionAccessService sessionAccessService)
	{
		this.sessionAccessService = sessionAccessService;

	}


	/**
	 * @param productConfigurationService
	 */
	public void setProductConfigurationService(final ProductConfigurationService productConfigurationService)
	{
		this.productConfigurationService = productConfigurationService;

	}

}
