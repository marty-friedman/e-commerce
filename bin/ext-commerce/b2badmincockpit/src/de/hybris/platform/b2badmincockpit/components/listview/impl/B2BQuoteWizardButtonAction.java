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
import de.hybris.platform.cockpit.model.general.ListComponentModel;
import de.hybris.platform.cockpit.model.general.ListModel;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.wizards.Wizard;
import de.hybris.platform.cockpit.wizards.impl.DefaultWizardContext;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Menupopup;


/**
 *
 */
public class B2BQuoteWizardButtonAction extends AbstractListViewAction
{
	protected boolean alwaysEnabled = true;

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(B2BQuoteWizardButtonAction.class);

	private static final String ICON_FUNC_APPROVAL_ACTION_AVAILABLE = "cockpit/images/icon_func_approval.png";

	private static final String TOOLTIP = "cockpit.wizard.quoteWizard.action.tooltip";
	private SessionService sessionService;
	private UserService userService;

	/**
	 * @return the sessionService
	 */
	protected SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 * @param sessionService
	 *           the sessionService to set
	 */
	@Autowired
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	@Autowired
	public void setUserService(final UserService defaultUserService)
	{
		this.userService = defaultUserService;
	}

	@Override
	public boolean isAlwaysEnabled()
	{
		return this.alwaysEnabled;
	}

	@Override
	public void setAlwaysEnabled(final boolean alwaysEnabled)
	{
		this.alwaysEnabled = true;
	}

	@Override
	public String getImageURI(final Context context)
	{
		return ICON_FUNC_APPROVAL_ACTION_AVAILABLE;
	}

	@Override
	public EventListener getMultiSelectEventListener(final Context context)
	{
		return new EventListener()
		{
			@Override
			public void onEvent(final Event event) throws Exception
			{
				if (Events.ON_CLICK.equals(event.getName()))
				{
					final ListComponentModel listComponentModel = context.getListModel();
					final ListModel listModel = listComponentModel.getListModel();
					final TypedObject orderObject = (TypedObject) listModel.elementAt(0);

					final DefaultWizardContext wizardContext = new DefaultWizardContext();
					wizardContext.setAttribute("order", orderObject.getObject());

					Wizard.show("quoteWizard", wizardContext);
				}
			}
		};
	}

	@Override
	public EventListener getEventListener(final Context context)
	{
		Assert.notNull(context);
		return new EventListener()
		{
			@Override
			public void onEvent(final Event event) throws Exception
			{

				final ListComponentModel listComponentModel = context.getListModel();
				final ListModel listModel = listComponentModel.getListModel();
				final TypedObject orderObject = (TypedObject) listModel.elementAt(0);

				final DefaultWizardContext wizardContext = new DefaultWizardContext();
				wizardContext.setAttribute("order", orderObject.getObject());

				Wizard.show("quoteWizard", wizardContext);
			}
		};
	}

	@Override
	public String getMultiSelectImageURI(final Context context)
	{
		final UserModel currentuser = userService.getCurrentUser();
		if (userService.isAdmin(currentuser))
		{
			return ICON_FUNC_APPROVAL_ACTION_AVAILABLE;
		}
		else
		{
			return null;
		}

	}

	@Override
	public String getTooltip(final Context context)
	{
		return Labels.getLabel(TOOLTIP);
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
		//
	}

}
