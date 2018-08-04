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
import de.hybris.platform.cockpit.wizards.WizardContext;
import de.hybris.platform.core.model.order.OrderModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 *
 */
public class B2BQuoteDiscountWizardPage extends AbstractB2BOrganizationWizardPage
{

	@Override
	public List<Message> validate()
	{
		final List<Message> validationMessages = new ArrayList<Message>();

		final String discountType = (String) getAttribute("typeOfDiscount");
		BigDecimal amount = null;

		if (getAttribute("amount") == null)
		{
			validationMessages.add(new Message(Message.ERROR, "The amount you entered is not a valid", "amount"));
			return validationMessages;
		}
		else
		{
			try
			{
				amount = new BigDecimal((String) getAttribute("amount"));
			}
			catch (final NumberFormatException e)
			{
				validationMessages.add(new Message(Message.ERROR, "The amount you entered is not a valid", "amount"));
				return validationMessages;
			}
		}

		final BigDecimal oneHundred = new BigDecimal(100);

		if (discountType != null && discountType.equals("Percentage"))
		{
			if (amount.compareTo(BigDecimal.ZERO) == -1 || amount.compareTo(oneHundred) == 1)
			{
				validationMessages.add(new Message(Message.ERROR, "Amount must be between 0 and 100, inclusive.", "amount"));
			}

		}
		else if (discountType != null && discountType.equals("Absolute"))
		{
			if (amount.compareTo(BigDecimal.ZERO) == -1)
			{
				validationMessages.add(new Message(Message.ERROR, "Amount must be greater than or equal to zero.", "amount"));
			}

			//check that amount is not greater than the value of the total price.
			final WizardContext wizardContext = wizard.getWizardContext();
			final OrderModel order = (OrderModel) wizardContext.getAttribute("order");
			final BigDecimal total = BigDecimal.valueOf(order.getTotalPrice().doubleValue());
			if (total.compareTo(amount) == -1)
			{
				validationMessages.add(new Message(Message.ERROR, "The discount amount cannot be greater than the total price.",
						"amount"));
			}

		}

		return validationMessages;
	}

	public Date getExpirationInfo()
	{
		return getDateInfo("expiration");
	}
}
