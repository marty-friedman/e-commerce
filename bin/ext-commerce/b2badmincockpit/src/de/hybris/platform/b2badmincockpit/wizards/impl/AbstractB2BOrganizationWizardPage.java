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
import de.hybris.platform.cockpit.wizards.impl.DefaultPage;
import de.hybris.platform.cockpit.wizards.impl.DefaultWizardContext;
import de.hybris.platform.servicelayer.session.SessionService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;


public class AbstractB2BOrganizationWizardPage extends DefaultPage
{
	protected ArrayList<String> attributesToValidate;
	protected boolean valid;
	private SessionService sessionService;
	protected final static Logger LOG = Logger.getLogger(B2BOrganizationWizardPageController.class);

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	@Autowired
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	public ArrayList<String> getAttributesToValidate()
	{
		return attributesToValidate;
	}

	public void setAttributesToValidate(final ArrayList<String> attributesToValidate)
	{
		this.attributesToValidate = attributesToValidate;
	}

	@Override
	public Map<String, Object> getAttributes()
	{
		final Map attributes = (Map) getWizard().getWizardContext().getAttribute(this.getId());
		final Map newAttributes = new HashMap<String, Object>();

		if (attributes != null)
		{
			newAttributes.putAll(attributes);
		}

		return newAttributes;
	}

	public void setAttribute(final String attributeName, final Object value)
	{
		final Map attributes = getAttributes();
		final Map newAttributes = new HashMap<String, Object>();

		if (attributes != null)
		{
			newAttributes.putAll(attributes);
		}

		newAttributes.put(attributeName, value);

		((DefaultWizardContext) getWizard().getWizardContext()).setAttribute(this.getId(), newAttributes);
	}

	public void removeAttribute(final String key)
	{

		final Map attributes = getAttributes();
		final Map newAttributes = new HashMap<String, Object>();

		if (attributes != null)
		{
			newAttributes.putAll(attributes);
		}

		newAttributes.remove(key);

		getSessionService().setAttribute(this.getId(), newAttributes);
	}

	public Object getAttribute(final String attributeName)
	{
		return getAttributes().get(attributeName);
	}

	protected List<Message> validateAttributes()
	{
		final List<Message> validationMessages = new ArrayList<Message>();
		boolean valid;

		if (attributesToValidate != null)
		{
			for (final Iterator attributeIterator = attributesToValidate.iterator(); attributeIterator.hasNext();)
			{
				final String attributeName = (String) attributeIterator.next();

				if (getAttributes().get(attributeName) instanceof String)
				{
					valid = StringUtils.isNotBlank((String) getAttributes().get(attributeName));
				}
				else
				{
					valid = (getAttributes().get(attributeName) != null);
				}

				if (!valid)
				{
					validationMessages
							.add(new Message(Message.ERROR, "Please enter a value for " + attributeName + ".", attributeName));
				}
			}
		}

		return validationMessages;
	}

	public List<Message> validate()
	{
		final List<Message> validationMessages = new ArrayList<Message>();

		return validationMessages;
	}

	protected Date getDateInfo(final String key)
	{
		final Date date = new Date();

		if (getAttribute(key) == null || getAttribute(key).equals(""))
		{
			setAttribute(key, date);
			return date;
		}
		//creates and returns data object if key returns a String.
		else if (getAttribute(key) instanceof java.lang.String)
		{
			final DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
			try
			{
				return formatter.parse(getAttribute(key).toString());
			}
			catch (final ParseException e)
			{
				LOG.error(e);
			}

		}
		else if (getAttribute(key) instanceof Date)
		{
			return (Date) getAttribute(key);
		}

		return date;
	}

}
