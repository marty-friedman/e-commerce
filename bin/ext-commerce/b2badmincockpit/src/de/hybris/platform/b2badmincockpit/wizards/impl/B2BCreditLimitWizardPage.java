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

import de.hybris.platform.b2b.enums.B2BPeriodRange;
import de.hybris.platform.b2b.model.B2BCreditLimitModel;
import de.hybris.platform.b2b.services.B2BItemService;
import de.hybris.platform.cockpit.wizards.Message;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zk.ui.Component;


public class B2BCreditLimitWizardPage extends AbstractB2BOrganizationWizardPage
{

	@SuppressWarnings("deprecation")
	private B2BItemService b2bItemService;
	private final static Logger LOG = Logger.getLogger(B2BOrganizationWizardPageController.class);

	/**
	 * @return the credit limits
	 */
	public ArrayList<String> getCreditLimits()
	{

		@SuppressWarnings("deprecation")
		final List<B2BCreditLimitModel> limits = b2bItemService.findAllItems(B2BCreditLimitModel.class);
		final Set<BigDecimal> creditLimitValues = new TreeSet<BigDecimal>();

		for (final B2BCreditLimitModel b2bCreditLimit : limits)
		{

			BigDecimal cl = b2bCreditLimit.getAmount();
			cl = cl.setScale(2, RoundingMode.DOWN);
			creditLimitValues.add(cl);
		}

		final BigDecimal[] numericalResults = creditLimitValues.toArray(new BigDecimal[creditLimitValues.size()]);
		Arrays.sort(numericalResults);

		final ArrayList<String> results = new ArrayList<String>();

		for (final BigDecimal value : numericalResults)
		{
			results.add(value.toString());
		}

		return results;
	}

	/**
	 * validates the credit limit's code as unique.
	 */
	protected boolean isCreditLimitCodeUnique(final String code)
	{

		final List<B2BCreditLimitModel> limits = b2bItemService.findAllItems(B2BCreditLimitModel.class);

		for (final B2BCreditLimitModel b2bCreditLimit : limits)
		{
			if (b2bCreditLimit.getCode().equals(code))
			{
				return false;
			}
		}

		return true;
	}

	/**
	 * @return the name of the session's currency (set on the wizard's budget page).
	 */
	public String getCurrencyName()
	{
		return (String) ((HashMap<String, Object>) getWizard().getWizardContext().getAttribute("costCenter")).get("currency");
	}

	public ArrayList<String> getPeriods()
	{
		final B2BPeriodRange[] b2bPeriodRange = B2BPeriodRange.values();
		final ArrayList<String> periodValues = new ArrayList<String>();

		for (final B2BPeriodRange period : b2bPeriodRange)
		{
			periodValues.add(period.toString());
		}

		return periodValues;
	}


	@Override
	public List<Message> validate()
	{

		final List<Message> validationMessages = new ArrayList<Message>();

		if (getAttribute("creditLimit") != null)
		{

			if (getAttribute("creditLimit").getClass().equals(BigDecimal.class))
			{
				return validationMessages;
			}
			else if (!isCreditLimitCodeUnique((String) getAttribute("uid")))
			{
				validationMessages.add(new Message(Message.ERROR, "The ID you selected already exits.", "uid"));
			}

			try
			{
				final BigDecimal value = new BigDecimal((String) getAttribute("creditLimit"));
				if (value.intValue() < 0)
				{
					validationMessages.add(new Message(Message.ERROR, "You must enter a number greater than 0", "creditLimit"));
				}

			}
			catch (final NumberFormatException e)
			{
				LOG.error(e);
				validationMessages.add(new Message(Message.ERROR, "The number you entered is not a valid", "creditLimit"));
			}
		}

		return validationMessages;
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

}
