/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.populator.impl;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.sap.productconfig.runtime.cps.CPSContextSupplier;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalItem;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.springframework.beans.factory.annotation.Required;


/**
 * Populates CPSExternalConfiguration from external representation of configuration defined in interface extension
 */
public class ExternalConfigurationPopulator implements Populator<Configuration, CPSExternalConfiguration>
{
	private CPSContextSupplier contextSupplier;
	private Converter<Configuration, CPSExternalItem> itemTreeConverter;

	@Override
	public void populate(final Configuration source, final CPSExternalConfiguration target)
	{
		populateCoreAttributes(source, target);
		populateContext(source, target);
		populateInstanceTree(source, target);
	}

	protected void populateInstanceTree(final Configuration source, final CPSExternalConfiguration target)
	{
		target.setRootItem(getItemTreeConverter().convert(source));
	}

	protected void populateContext(final Configuration source, final CPSExternalConfiguration target)
	{
		target.setContext(getContextSupplier().retrieveContext(source.getKbKey().getProductCode()));
	}

	protected void populateCoreAttributes(final Configuration source, final CPSExternalConfiguration target)
	{
		target.setComplete(source.getRootInstance().isComplete());
		target.setConsistent(source.getRootInstance().isConsistent());
	}

	protected Converter<Configuration, CPSExternalItem> getItemTreeConverter()
	{
		return itemTreeConverter;
	}

	/**
	 * @param itemTreeConverter
	 *           the itemTreeConverter to set
	 */
	@Required
	public void setItemTreeConverter(final Converter<Configuration, CPSExternalItem> rootItemConverter)
	{
		this.itemTreeConverter = rootItemConverter;
	}

	protected CPSContextSupplier getContextSupplier()
	{
		return contextSupplier;
	}

	/**
	 * @param contextSupplier
	 *           the contextSupplier to set
	 */
	@Required
	public void setContextSupplier(final CPSContextSupplier contextSupplier)
	{
		this.contextSupplier = contextSupplier;
	}

}
