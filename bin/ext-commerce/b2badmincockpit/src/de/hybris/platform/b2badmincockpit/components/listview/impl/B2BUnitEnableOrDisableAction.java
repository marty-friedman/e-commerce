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

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.catalog.model.CompanyModel;
import de.hybris.platform.cockpit.events.impl.ItemChangedEvent;
import de.hybris.platform.cockpit.model.meta.PropertyDescriptor;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.model.undo.impl.ItemChangeUndoableOperation;
import de.hybris.platform.cockpit.services.values.ObjectValueContainer;
import de.hybris.platform.cockpit.services.values.ValueService;
import de.hybris.platform.cockpit.session.UISessionUtils;
import de.hybris.platform.cockpit.util.UndoTools;
import de.hybris.platform.core.model.user.UserModel;

import java.util.Collections;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Messagebox;


public class B2BUnitEnableOrDisableAction extends AbstractEnableDisableAction
{
	private static final Logger LOG = Logger.getLogger(B2BUnitEnableOrDisableAction.class);
	private B2BUnitService<CompanyModel, UserModel> b2bUnitService;
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
	public String getImageURI(final Context context)
	{
		return ICON_FUNC_APPROVAL_ACTION_AVAILABLE;
	}


	@Override
	public Menupopup getPopup(final Context context)
	{

		final Menupopup popupMenu = new Menupopup();

		final B2BUnitModel b2bUnitModel = (B2BUnitModel) context.getItem().getObject();
		final Boolean activeStatus = b2bUnitModel.getActive();

		final String label = getStatusLabel(!activeStatus.booleanValue());

		final Menuitem menuItem = new Menuitem(label);
		menuItem.setParent(popupMenu);
		menuItem.addEventListener(Events.ON_CLICK, new EventListener()
		{
			@Override
			public void onEvent(final Event event) throws Exception
			{
				try
				{
					doChangeStatus(context.getItem(), !activeStatus.booleanValue());
					sendUpdateItemsEvent(context);
				}
				catch (final Exception e)
				{
					LOG.error("Could not change active status of item (Reason: " + e.getMessage() + ").", e);
					Messagebox.show("Could not change active status of item (Reason: " + e.getMessage() + ").",
							Labels.getLabel("b2bitem.unit.enabledisable.exception"), Messagebox.OK, Messagebox.EXCLAMATION);
				}

				sendUpdateItemsEvent(context);
			}
		});

		return popupMenu;
	}

	@SuppressWarnings("deprecation")
	protected void doChangeStatus(final TypedObject selectedItem, final boolean activeStatus) throws Exception
	{
		LOG.debug("Changing the status of B2BUnit...");

		final PropertyDescriptor pd = getTypeService().getPropertyDescriptor("B2BUnit.active");

		Object originalStatus = null;
		try
		{
			originalStatus = getValueService().getValue(selectedItem, pd);
		}
		catch (final Exception e)
		{
			LOG.error("Could not change active status of item (Reason: " + e.getMessage() + ").", e);
		}

		this.toggleUnit(selectedItem, activeStatus);

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

	protected void toggleUnit(final TypedObject selectedItem, final boolean activeStatus)
	{

		final B2BUnitModel b2bUnitModel = (B2BUnitModel) selectedItem.getObject();
		b2bUnitModel.setActive(Boolean.valueOf(activeStatus));
		if (activeStatus)
		{
			b2bUnitService.enableUnit(b2bUnitModel);
		}
		else
		{
			b2bUnitService.disableBranch(b2bUnitModel);
		}

	}


	public B2BUnitService<CompanyModel, UserModel> getB2bUnitService()
	{
		return b2bUnitService;
	}

	public void setB2bUnitService(final B2BUnitService<CompanyModel, UserModel> b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}

}
