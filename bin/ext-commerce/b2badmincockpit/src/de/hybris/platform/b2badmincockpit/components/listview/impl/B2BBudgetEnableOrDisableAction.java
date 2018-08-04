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

import de.hybris.platform.b2b.model.B2BBudgetModel;
import de.hybris.platform.cockpit.events.impl.ItemChangedEvent;
import de.hybris.platform.cockpit.helpers.ModelHelper;
import de.hybris.platform.cockpit.model.meta.PropertyDescriptor;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.model.undo.impl.ItemChangeUndoableOperation;
import de.hybris.platform.cockpit.services.values.ObjectValueContainer;
import de.hybris.platform.cockpit.services.values.ValueService;
import de.hybris.platform.cockpit.session.UISessionUtils;
import de.hybris.platform.cockpit.util.UITools;
import de.hybris.platform.cockpit.util.UndoTools;

import java.util.Collections;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Messagebox;


public class B2BBudgetEnableOrDisableAction extends AbstractEnableDisableAction
{

	private static final Logger LOG = Logger.getLogger(B2BBudgetEnableOrDisableAction.class);
	private ModelHelper modelHelper;
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
	public Menupopup getMultiSelectPopup(final Context context)
	{
		LOG.debug("Inside getMulti Popoup" + context);
		return null;
	}


	@Override
	public Menupopup getPopup(final Context context)
	{
		final Menupopup popupMenu = new Menupopup();

		final PropertyDescriptor pd = getTypeService().getPropertyDescriptor("B2BBudget.active");
		Boolean originalStatus = null;
		try
		{
			originalStatus = (Boolean) getValueService().getValue(context.getItem(), pd);
		}
		catch (final Exception e)
		{
			LOG.error("Could not get active status of item (Reason: " + e.getMessage() + ").", e);
		}

		if (originalStatus != null)
		{
			final String label = getStatusLabel(!originalStatus.booleanValue());
			final Boolean finalOriginalStatus = originalStatus;
			final Menuitem menuItem = new Menuitem(label);
			menuItem.setParent(popupMenu);
			UITools.addBusyListener(menuItem, Events.ON_CLICK, new EventListener()
			{
				@Override
				public void onEvent(final Event event) throws Exception
				{
					try
					{
						doChangeStatus(context.getItem(), !finalOriginalStatus.booleanValue());
						sendUpdateItemsEvent(context);
					}
					catch (final Exception e)
					{
						LOG.error("Could not change active status of item (Reason: " + e.getMessage() + ").", e);
						Messagebox.show("Could not change active status of item (Reason: " + e.getMessage() + ").",
								Labels.getLabel("b2bitem.budget.enabledisable.exception"), Messagebox.OK, Messagebox.EXCLAMATION);
					}
					sendUpdateItemsEvent(context);
				}
			}, null, "busy.changeactivestatus");
		}
		else
		{
			LOG.error("Could not get active status of item (" + context.getItem() + ")");
		}

		return popupMenu;
	}

	protected void doChangeStatus(final TypedObject selectedItem, final boolean activeStatus) throws Exception
	{
		LOG.debug("Changing the status of B2BBudget...");
		final PropertyDescriptor pd = getTypeService().getPropertyDescriptor("B2BBudget.active");

		Object originalStatus = null;
		try
		{
			originalStatus = getValueService().getValue(selectedItem, pd);
		}
		catch (final Exception e)
		{
			LOG.error("Could not change active status of item (Reason: " + e.getMessage() + ").", e);
		}

		final B2BBudgetModel b2bBudgetModel = (B2BBudgetModel) selectedItem.getObject();
		b2bBudgetModel.setActive(Boolean.valueOf(activeStatus));

		getModelHelper().saveModel(b2bBudgetModel, true, true);

		Object newStatus = null;
		try
		{
			newStatus = getValueService().getValue(selectedItem, pd);
		}
		catch (final Exception e)
		{
			LOG.error("Could not change active status of item (Reason: " + e.getMessage() + ").", e);
		}

		//notify and add undo operation
		final ObjectValueContainer valueContainer = new ObjectValueContainer(selectedItem.getType(), selectedItem.getObject());
		valueContainer.addValue(pd, pd.isLocalized() ? UISessionUtils.getCurrentSession().getLanguageIso() : null, originalStatus);
		valueContainer.getValue(pd, pd.isLocalized() ? UISessionUtils.getCurrentSession().getLanguageIso() : null)
				.setLocalValue(newStatus);
		if (valueContainer.isModified())
		{
			final ItemChangeUndoableOperation undoOperation = new ItemChangeUndoableOperation(selectedItem, valueContainer);
			UndoTools.addUndoOperationAndEvent(UISessionUtils.getCurrentSession().getUndoManager(), undoOperation, this);

			UISessionUtils.getCurrentSession().sendGlobalEvent(new ItemChangedEvent(this, selectedItem, Collections.singleton(pd)));
		}
	}

	public ModelHelper getModelHelper()
	{
		return modelHelper;
	}


	public void setModelHelper(final ModelHelper modelHelper)
	{
		this.modelHelper = modelHelper;
	}

}
