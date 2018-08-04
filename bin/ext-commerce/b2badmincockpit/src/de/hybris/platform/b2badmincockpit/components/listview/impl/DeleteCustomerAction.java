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

import de.hybris.platform.b2b.services.B2BOrderService;
import de.hybris.platform.cockpit.components.listview.AbstractListViewAction;
import de.hybris.platform.cockpit.events.impl.ItemChangedEvent;
import de.hybris.platform.cockpit.helpers.ModelHelper;
import de.hybris.platform.cockpit.session.UISessionUtils;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;

import java.util.Collections;
import java.util.List;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Messagebox;

public class DeleteCustomerAction extends AbstractListViewAction
{
	private ModelHelper modelHelper;
	private B2BOrderService b2bOrderService;

	/**
	 * @param b2bOrderService
	 *           the b2bOrderService to set
	 */
	public void setB2bOrderService(final B2BOrderService b2bOrderService)
	{
		this.b2bOrderService = b2bOrderService;
	}

	/**
	 * @param modelHelper
	 *           the modelHelper to set
	 */
	public void setModelHelper(final ModelHelper modelHelper)
	{
		this.modelHelper = modelHelper;
	}

	@Override
	protected void doCreateContext(final Context context)
	{
		// YTODO Auto-generated method stub
	}

	@Override
	public Menupopup getContextPopup(final Context context)
	{
		return null;
	}

	@Override
	public EventListener getEventListener(final Context context)
	{
		return new EventListener()
		{
			@Override
			public void onEvent(final Event event) throws Exception
			{

				final ItemModel b2BCustomerModel = (ItemModel) context.getItem().getObject();
				if (Messagebox.show(Labels.getLabel("ba.messagebox.confirm_delete_b2bcustomer"),
						Labels.getLabel("ba.messagebox.confirm_delete_title"), Messagebox.OK + Messagebox.CANCEL,
						Messagebox.EXCLAMATION) == Messagebox.OK)
				{
					final List<OrderModel> orders = (List<OrderModel>) ((UserModel) b2BCustomerModel).getOrders();
					if (!orders.isEmpty())
					{
						for (final OrderModel order : orders)
						{
							b2bOrderService.deleteOrder(order.getCode());
						}
					}
					modelHelper.removeModel(b2BCustomerModel, true);
					UISessionUtils.getCurrentSession().getCurrentPerspective().getBrowserArea().getFocusedBrowser().updateItems();
					UISessionUtils.getCurrentSession().sendGlobalEvent(
							new ItemChangedEvent(this, context.getItem(), Collections.emptyList(), ItemChangedEvent.ChangeType.REMOVED));
				}
			}
		};
	}

	@Override
	public String getImageURI(final Context context)
	{
		return "cockpit/images//trash.png";
	}

	@Override
	public Menupopup getPopup(final Context context)
	{
		return null;
	}

	@Override
	public String getTooltip(final Context context)
	{
		return Labels.getLabel("ba.delete_tooltip");
	}
}
