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

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.cockpit.wizards.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zk.ui.Component;


/**
 *
 */
public class B2BAdminOrganizationWizardPage extends AbstractB2BOrganizationWizardPage
{

	private B2BCustomerService b2bCustomerService;

	/**
	 * @return the b2bCustomerService
	 */
	protected B2BCustomerService getB2bCustomerService()
	{
		return b2bCustomerService;
	}

	/**
	 * @param b2bCustomerService
	 *           the b2bCustomerService to set
	 */
	@Autowired
	public void setB2bCustomerService(final B2BCustomerService b2bCustomerService)
	{
		this.b2bCustomerService = b2bCustomerService;
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
		final String customerUid = (String) getAttribute("uid");
		final String email = (String) getAttribute("email");

		// validate email
		if (StringUtils.isNotBlank(email))
		{
			final String emailPatternRegex = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
			final Pattern pattern = Pattern.compile(emailPatternRegex, Pattern.CASE_INSENSITIVE);
			final Matcher matcher = pattern.matcher(email);

			final boolean emailIsValid = matcher.matches();

			if (!emailIsValid)
			{
				validationMessages.add(new Message(Message.ERROR, "The email address you entered is not valid.", "email"));
			}
		}

		// validate customerId
		if (StringUtils.isNotBlank(customerUid))
		{
			final B2BCustomerModel admin = (B2BCustomerModel) getB2bCustomerService().getUserForUID(customerUid);

			if (admin != null)
			{
				validationMessages.add(new Message(Message.ERROR, "The Id you selected already exists.", "uid"));
			}
		}

		return validationMessages;
	}
}
