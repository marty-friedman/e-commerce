/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.b2badmincockpit.components.listview.impl;

import de.hybris.platform.cockpit.model.meta.PropertyDescriptor;
import de.hybris.platform.cockpit.services.values.ValueHandlerException;
import de.hybris.platform.cockpit.services.values.ValueService;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Menupopup;


/**
 *
 */
public class B2BItemEnableOrDisableStatus extends AbstractEnableDisableAction
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(B2BItemEnableOrDisableStatus.class);
	private ValueService valueService;


	/**
	 * @return the valueService
	 */
	public ValueService getValueService()
	{
		return valueService;
	}

	/**
	 * @param valueService
	 *           the valueService to set
	 */
	public void setValueService(final ValueService valueService)
	{
		this.valueService = valueService;
	}

	@Override
	public Menupopup getContextPopup(final Context context)
	{
		return null;
	}

	@Override
	public EventListener getEventListener(final Context context)
	{
		return null;
	}

	@Override
	public String getImageURI(final Context context)
	{
		String uri = UNKNOWN_STATE_ICON;

		final String activeStatus = getStatusCode(context);
		if (activeStatus != null)
		{
			if ("false".equals(activeStatus))
			{
				uri = UNAPPROVED_STATE_ICON;
			}
			else if ("true".equals(activeStatus))
			{
				uri = APPROVED_STATE_ICON;
			}
		}
		return uri;
	}

	@Override
	public String getStatusCode(final Context context)
	{
		final String propertyDescriptorArgument = context.getItem().getType().getCode() + ".active";
		final PropertyDescriptor pd = getTypeService().getPropertyDescriptor(propertyDescriptorArgument);

		String statusCode = null;

		try
		{
			statusCode = getValueService().getValue(context.getItem(), pd).toString();
		}
		catch (final ValueHandlerException e)
		{
			LOG.error("Could not change active status of item (Reason: " + e.getMessage() + ").", e);
		}

		return statusCode;
	}

	@Override
	public String getTooltip(final Context context)
	{
		final String activeStatus = getStatusCode(context);
		return activeStatus == null ? Labels.getLabel("gridview.item.active.tooltip") : activeStatus.toUpperCase();
	}

}
