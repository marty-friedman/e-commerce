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


import de.hybris.platform.b2b.model.B2BCommentModel;
import de.hybris.platform.b2b.services.B2BOrderService;
import de.hybris.platform.cockpit.wizards.Message;
import de.hybris.platform.cockpit.wizards.Wizard;
import de.hybris.platform.cockpit.wizards.WizardPage;
import de.hybris.platform.cockpit.wizards.exception.WizardConfirmationException;
import de.hybris.platform.cockpit.wizards.impl.DefaultPageController;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.tx.Transaction;
import de.hybris.platform.util.DiscountValue;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;


/**
 *
 */
public class B2BQuoteDiscountWizardPageController extends DefaultPageController
{

	protected ModelService modelService;
	protected CalculationService calculationService;
	protected Set<String> attributeSets;
	protected B2BOrderService b2bOrderService;
	protected final static Logger LOG = Logger.getLogger(B2BQuoteDiscountWizardPageController.class);


	/**
	 * @return the b2bOrderService
	 */
	public B2BOrderService getB2bOrderService()
	{
		return b2bOrderService;
	}

	/**
	 * @param b2bOrderService
	 *           the b2bOrderService to set
	 */
	public void setB2bOrderService(final B2BOrderService b2bOrderService)
	{
		this.b2bOrderService = b2bOrderService;
	}

	public CalculationService getCalculationService()
	{
		return calculationService;
	}

	public void setCalculationService(final CalculationService calculationService)
	{
		this.calculationService = calculationService;
	}



	protected ModelService getModelService()
	{
		return modelService;
	}

	@Autowired
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}


	/**
	 * @param attributeSets
	 *           the attributeSets to set
	 */
	public void setAttributeSets(final Set<String> attributeSets)
	{
		this.attributeSets = attributeSets;
	}

	@Override
	public void done(final Wizard wizard, final WizardPage page) throws WizardConfirmationException
	{
		final Transaction tx = Transaction.current();
		boolean success = false;

		tx.begin();

		try
		{
			applyDiscount(wizard);
			success = true;
		}
		catch (final CalculationException e)
		{
			success = false;
			LOG.error("Error while applying discount.", e);
		}
		finally
		{
			if (success)
			{
				tx.commit();
			}
			else
			{
				tx.rollback();
			}
		}
	}

	protected void applyDiscount(final Wizard wizard) throws CalculationException
	{
		final HashMap<String, Object> discountAttributes = (HashMap<String, Object>) getAttributesForPage("quoteDiscount", wizard);
		final OrderModel order = (OrderModel) wizard.getWizardContext().getAttribute("order");

		//comments
		if (discountAttributes.get("comment") instanceof String)
		{
			final B2BCommentModel comment = getModelService().create(B2BCommentModel.class);
			comment.setComment((String) discountAttributes.get("comment"));

			final ArrayList<B2BCommentModel> commentList = new ArrayList<B2BCommentModel>(order.getB2bcomments());
			commentList.add(comment);
			order.setB2bcomments(commentList);
			getB2bOrderService().saveOrder(order);
		}

		//discount
		final String code = (String) discountAttributes.get("code");
		final Double amount = Double.valueOf((String) discountAttributes.get("amount"));
		boolean isAbsolute = false;
		if (discountAttributes.get("typeOfDiscount").equals("Absolute"))
		{
			isAbsolute = true;
		}
		final String isoCode = order.getCurrency().getIsocode();
		final DiscountValue discountValue = new DiscountValue(code, amount.doubleValue(), isAbsolute, isoCode);

		final List<DiscountValue> discountList = new ArrayList<DiscountValue>(order.getGlobalDiscountValues());
		discountList.add(discountValue);
		order.setGlobalDiscountValues(discountList);

		//expiration date
		if (discountAttributes.get("expiration") != null)
		{
			Date expiration;

			DateFormat formatter;
			formatter = new SimpleDateFormat("MM/dd/yyyy");
			try
			{
				expiration = formatter.parse(discountAttributes.get("expiration").toString());
				order.setQuoteExpirationDate(expiration);
			}
			catch (final ParseException e)
			{
				LOG.error(e);
			}
		}

		//save
		getCalculationService().calculateTotals(order, true);
		getB2bOrderService().saveOrder(order);

	}

	@Override
	public boolean validate(final Wizard wizard, final WizardPage page)
	{
		final AbstractB2BOrganizationWizardPage currentPage = (AbstractB2BOrganizationWizardPage) page;
		final List<Message> validationMessages = new ArrayList<Message>();

		// validate current page
		validationMessages.addAll(currentPage.validate());
		validationMessages.addAll(currentPage.validateAttributes());

		final boolean isValid = validationMessages.isEmpty();

		if (!isValid)
		{
			for (final Iterator messageIterator = validationMessages.iterator(); messageIterator.hasNext();)
			{
				final Message message = (Message) messageIterator.next();
				wizard.addMessage(message);
			}
		}

		return isValid;
	}

	/**
	 * @return the attributeSets
	 */
	protected Set<String> getAttributeSets()
	{
		return attributeSets;
	}

	protected Map<String, Object> getAttributesForPage(final String pageId, final Wizard wizard)
	{
		return (Map<String, Object>) wizard.getWizardContext().getAttribute(pageId);
	}

}
