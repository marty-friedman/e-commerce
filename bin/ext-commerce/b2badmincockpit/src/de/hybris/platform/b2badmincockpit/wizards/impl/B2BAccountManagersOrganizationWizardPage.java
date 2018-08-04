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
package de.hybris.platform.b2badmincockpit.wizards.impl;

import de.hybris.platform.cockpit.wizards.Message;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.ListModelList;


/**
 *
 */
public class B2BAccountManagersOrganizationWizardPage extends AbstractB2BOrganizationWizardPage
{

	private UserService userService;
	protected ListModelList accountManagers;

	/**
	 * @param userService
	 *           the UserService to set.
	 */
	@Autowired
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	/**
	 * @return the accountManagers
	 */
	public ListModelList getAccountManagers()
	{
		final UserGroupModel accountManagerGroup = userService.getUserGroupForUID("acctmgrgroup");
		accountManagers = new ListModelList();
		accountManagers.addAll(accountManagerGroup.getMembers());
		return accountManagers;
	}

	@Override
	public List<Message> validate()
	{

		final List<Message> validationMessages = new ArrayList<Message>();

		if (getAttribute("uid") == null || "".equals(getAttribute("uid")))
		{
			validationMessages.add(new Message(Message.ERROR, "You must select an account manager.", "uid"));
		}

		return validationMessages;
	}

	@Override
	public void renderView(final Component parent)
	{
		//custom render code
	}

}
