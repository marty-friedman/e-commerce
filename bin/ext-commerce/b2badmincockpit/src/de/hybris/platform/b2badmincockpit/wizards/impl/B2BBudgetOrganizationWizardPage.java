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

import de.hybris.platform.b2b.model.B2BBudgetModel;
import de.hybris.platform.b2b.services.B2BCostCenterService;
import de.hybris.platform.b2b.services.B2BItemService;
import de.hybris.platform.cockpit.wizards.Message;
import de.hybris.platform.core.model.c2l.CurrencyModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.ListModelList;


/**
 *
 */
public class B2BBudgetOrganizationWizardPage extends AbstractB2BOrganizationWizardPage
{
	@SuppressWarnings("deprecation")
	private B2BItemService b2bItemService;
	protected ListModelList currencies;
	private B2BCostCenterService b2bCostCenter;


	/**
	 * @return the b2bCostCenter
	 */
	protected B2BCostCenterService getB2bCostCenter()
	{
		return b2bCostCenter;
	}

	/**
	 * @param b2bCostCenter
	 *           the b2bCostCenter to set
	 */
	@Autowired
	public void setB2bCostCenter(final B2BCostCenterService b2bCostCenter)
	{
		this.b2bCostCenter = b2bCostCenter;
	}

	/**
	 * @return the currencies
	 */
	public ListModelList getCurrencies()
	{
		@SuppressWarnings("deprecation")
		final List<CurrencyModel> currencyModels = b2bItemService.findAllItems(CurrencyModel.class);

		currencies = new ListModelList();
		currencies.addAll(currencyModels);

		return currencies;
	}

	/**
	 * @return the b2bItemService
	 */
	protected B2BItemService getB2bItemService()
	{
		return b2bItemService;
	}

	/**
	 * @param b2bItemService
	 *           the itemService to set
	 */
	@Autowired
	public void setb2bItemService(final B2BItemService b2bItemService)
	{
		this.b2bItemService = b2bItemService;
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

		// validate uid
		if (getAttribute("uid") != null)
		{
			final B2BBudgetModel budget = getB2bCostCenter().getB2BBudgetForCode((String) getAttribute("uid"));

			if (budget != null)
			{
				validationMessages.add(new Message(Message.ERROR, "The ID you selected already exits.", "uid"));
			}
		}

		//budget is a number
		validateBudgetIsNumber(validationMessages);

		//starting date < ending date
		validateDates(validationMessages);


		return validationMessages;
	}

	protected void validateBudgetIsNumber(final List<Message> validationMessages)
	{
		if (getAttribute("budget") != null)
		{
			try
			{
				final BigDecimal budget = new BigDecimal((String) getAttribute("budget"));
				if (budget.compareTo(BigDecimal.ZERO) < 1)
				{
					validationMessages.add(new Message(Message.ERROR, "The budget you entered is not valid", "budget"));
				}
			}
			catch (final NumberFormatException e)
			{
				LOG.error(e);
				validationMessages.add(new Message(Message.ERROR, "The budget you entered is not valid", "budget"));
			}
		}
	}

	public Date getStartDateInfo()
	{
		return getDateInfo("startDate");
	}

	public Date getEndDateInfo()
	{
		return getDateInfo("endDate");
	}

	protected void validateDates(final List<Message> validationMessages)
	{
		//checks that dates were entered
		if ((getAttribute("startDate") == null) || (getAttribute("endDate") == null))
		{
			validationMessages.add(new Message(Message.ERROR, "You must enter a starting date and an ending date.", "startDate"));
			return;
		}

		final Date start = getDateInfo("startDate");
		final Date end = getDateInfo("endDate");

		//sets start and end Date objects in attributes.
		if (start.before(end) || start.equals(end))
		{
			setAttribute("startDate", start);
			setAttribute("endDate", end);
		}
		else
		{
			validationMessages.add(new Message(Message.ERROR, "The starting date must be earlier than or equal to the ending date.",
					"startDate"));
		}
	}

}
