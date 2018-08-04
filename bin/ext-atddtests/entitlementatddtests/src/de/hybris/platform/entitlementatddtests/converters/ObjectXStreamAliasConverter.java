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
package de.hybris.platform.entitlementatddtests.converters;

import de.hybris.platform.commercefacades.xstream.alias.AttributeAliasMapping;
import de.hybris.platform.commercefacades.xstream.alias.FieldAliasMapping;
import de.hybris.platform.commercefacades.xstream.alias.TypeAliasMapping;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.thoughtworks.xstream.XStream;


public class ObjectXStreamAliasConverter implements ApplicationContextAware
{
	private static final Logger LOG = Logger.getLogger(ObjectXStreamAliasConverter.class);

	private ApplicationContext ctx;
	private XStream xstream = null;

	public String getXStreamXmlFromObject(final Object object)
	{
		final String xml = getXstream().toXML(object);

		if (LOG.isDebugEnabled())
		{
			LOG.debug(xml);
		}

		return xml;
	}

	public XStream getXstream()
	{
		if (xstream != null)
		{
			return xstream;
		}

		xstream = new XStream();

		final Map<String, TypeAliasMapping> allTypeAliases = BeanFactoryUtils.beansOfTypeIncludingAncestors(ctx,
				TypeAliasMapping.class);

		for (final TypeAliasMapping alias : allTypeAliases.values())
		{

			if (!(alias instanceof AttributeAliasMapping) && !(alias instanceof FieldAliasMapping))
			{
				if (LOG.isDebugEnabled())
				{
					LOG.debug("registering type alias " + alias.getAlias() + " , " + alias.getAliasedClass());
				}
				xstream.alias(alias.getAlias(), alias.getAliasedClass());
			}
		}

		final Map<String, AttributeAliasMapping> allAttributes = BeanFactoryUtils.beansOfTypeIncludingAncestors(ctx,
				AttributeAliasMapping.class);

		for (final Map.Entry<String, AttributeAliasMapping> entry : allAttributes.entrySet())
		{
			xstream.useAttributeFor(entry.getValue().getAlias(), entry.getValue().getAliasedClass());
		}

		return xstream;
	}

	@Override
	public void setApplicationContext(final ApplicationContext ctx) throws BeansException
	{
		this.ctx = ctx;
	}
}
