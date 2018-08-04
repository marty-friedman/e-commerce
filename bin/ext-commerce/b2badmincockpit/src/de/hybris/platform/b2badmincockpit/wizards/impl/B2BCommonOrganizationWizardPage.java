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

import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.catalog.model.CompanyModel;
import de.hybris.platform.cockpit.wizards.Message;
import de.hybris.platform.core.model.user.UserModel;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.ListModelList;


public class B2BCommonOrganizationWizardPage extends AbstractB2BOrganizationWizardPage
{

	protected boolean isNameValid;
	protected boolean isB2bUserGroupSet = false;

	protected ListModelList userGroupList = new ListModelList();
	protected B2BUserGroupModel b2bUserGroup;

	private B2BUnitService<CompanyModel, UserModel> b2bUnitService;

	protected B2BUnitService getB2bUnitService()
	{
		return b2bUnitService;
	}

	@Autowired
	public void setB2bUnitService(final B2BUnitService b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}


	@Override
	public void renderView(final Component parent)
	{
		//custom render code
	}

	@Override
	public List<Message> validate()
	{
		final List<Message> validationMessages = new ArrayList<Message>();
		final String unitUid = (String) getAttribute("uid");

		// validate unit uid
		if (StringUtils.isNotBlank(unitUid))
		{
			final CompanyModel unit = getB2bUnitService().getUnitForUid((String) getAttribute("uid"));
			if (unit != null)
			{
				validationMessages.add(new Message(Message.ERROR, "The Uid you selected already exists.", "uid"));
			}
		}

		return validationMessages;
	}
}
