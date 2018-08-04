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

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.hook.CommerceUpdateCartEntryHook;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.tracking.TrackingRecorder;

import java.util.List;


/**
 * Deletes configuration session after removing cart entry.
 */
public class ProductConfigurationUpdateCartEntryHookImpl implements CommerceUpdateCartEntryHook
{

	private ProductConfigurationService productConfigurationService;
	private SessionAccessService sessionAccessService;
	private TrackingRecorder recorder;


	protected SessionAccessService getSessionAccessService()
	{
		return sessionAccessService;
	}

	/**
	 * @param sessionAccessService
	 *           the sessionAccessService to set
	 */
	public void setSessionAccessService(final SessionAccessService sessionAccessService)
	{
		this.sessionAccessService = sessionAccessService;
	}

	protected ProductConfigurationService getProductConfigurationService()
	{
		return productConfigurationService;
	}

	/**
	 * @param productConfigurationService
	 *           the productConfigurationService to set
	 */
	public void setProductConfigurationService(final ProductConfigurationService productConfigurationService)
	{
		this.productConfigurationService = productConfigurationService;
	}

	@Override
	public void afterUpdateCartEntry(final CommerceCartParameter parameter, final CommerceCartModification result)
	{
		// Check if update was a deletion (qty = 0).
		if (parameter.getQuantity() == 0)
		{
			// Check if a configuration has to be deleted
			final String configToBeDeleted = parameter.getConfigToBeDeleted();
			if (configToBeDeleted != null && (!configToBeDeleted.isEmpty()))
			{
				productConfigurationService.releaseSession(configToBeDeleted);
			}
		}
	}


	@Override
	public void beforeUpdateCartEntry(final CommerceCartParameter parameter)
	{
		// Check if entry should be deleted (qty = 0)
		final long qty = parameter.getQuantity();
		if (qty > 0)
		{
			return;
		}
		// Get the entry-object for the entry-number
		final List<AbstractOrderEntryModel> entries = parameter.getCart().getEntries();
		if (entries != null && !entries.isEmpty())
		{
			final Integer entryNumber = Integer.valueOf((int) parameter.getEntryNumber());
			for (final AbstractOrderEntryModel entry : entries)
			{
				handleCartEntry(parameter, entryNumber, entry);
			}
		}
	}

	protected void handleCartEntry(final CommerceCartParameter parameter, final Integer entryNumber,
			final AbstractOrderEntryModel entry)
	{
		if (entry != null && entryNumber.equals(entry.getEntryNumber()))
		{
			// Entry found: Check if it is configurable
			final String configId = sessionAccessService.getConfigIdForCartEntry(entry.getPk().toString());
			if (configId != null && (!configId.isEmpty()))
			{
				// Store configId in parameter object to be used in afterUpdateCartEntry method
				parameter.setConfigToBeDeleted(configId);

				final String productCode = entry.getProduct().getCode();
				sessionAccessService.removeSessionArtifactsForCartEntry(entry.getPk().toString(), productCode);
				recorder.recordDeleteCartEntry(entry, parameter);
			}
		}
	}

	protected TrackingRecorder getRecorder()
	{
		return recorder;
	}

	/**
	 * @param recorder
	 *           injects the cpq tracking recorder
	 */
	public void setRecorder(final TrackingRecorder recorder)
	{
		this.recorder = recorder;
	}

}
