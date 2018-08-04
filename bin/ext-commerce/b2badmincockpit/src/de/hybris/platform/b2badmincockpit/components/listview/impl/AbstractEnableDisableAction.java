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

import de.hybris.platform.cockpit.components.listview.AbstractListViewAction;
import de.hybris.platform.cockpit.services.meta.TypeService;

import org.apache.commons.collections.CollectionUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Menupopup;


/**
 *
 */
public abstract class AbstractEnableDisableAction extends AbstractListViewAction
{
	protected static final String UNKNOWN_STATE_ICON = "cockpit/images/icon_status_approve_unknown.png";
	protected static final String UNAPPROVED_STATE_ICON = "cockpit/images/icon_status_approve_not_x.png";
	protected static final String APPROVED_STATE_ICON = "cockpit/images/icon_status_approve_ok.png";
	protected static final String ICON_FUNC_APPROVAL_ACTION_AVAILABLE = "cockpit/images/icon_func_approval.png";
	protected static final String ICON_FUNC_APPROVAL_ACTION_UNAVAILABLE = "cockpit/images/icon_func_approval_unavailable.png";
	protected static final String ITEM_ACTIVE = "active";

	private TypeService typeService;

	/**
	 * @return the typeService
	 */
	public TypeService getTypeService()
	{
		return typeService;
	}

	/**
	 * @param typeService
	 *           the typeService to set
	 */
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}

	public String getStatusLabel(final boolean enabled)
	{
		return enabled ? Labels.getLabel("b2bitem.enable") : Labels.getLabel("b2bitem.disable");
	}

	@Override
	public String getImageURI(final Context context)
	{
		return ICON_FUNC_APPROVAL_ACTION_AVAILABLE;
	}

	@Override
	public String getMultiSelectImageURI(final Context context)
	{
		if (CollectionUtils.isNotEmpty(context.getBrowserModel().getSelectedIndexes())
				&& context.getBrowserModel().getSelectedIndexes().size() >= 1)
		{
			return ICON_FUNC_APPROVAL_ACTION_AVAILABLE;
		}
		return ICON_FUNC_APPROVAL_ACTION_UNAVAILABLE;
	}

	@Override
	public String getTooltip(final Context context)
	{
		return Labels.getLabel("gridview.item.enabledisable.action.tooltip");
	}



	@Override
	public EventListener getEventListener(final Context context)
	{
		return null;
	}

	@Override
	public Menupopup getPopup(final Context context)
	{
		return null;
	}

	@Override
	public Menupopup getContextPopup(final Context context)
	{
		return null;
	}


	@Override
	protected void doCreateContext(final Context context)
	{
		// empty
	}

}
