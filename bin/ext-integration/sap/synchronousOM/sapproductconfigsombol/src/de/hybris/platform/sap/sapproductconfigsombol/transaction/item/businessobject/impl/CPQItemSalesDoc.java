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
package de.hybris.platform.sap.sapproductconfigsombol.transaction.item.businessobject.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.ItemSalesDoc;

import java.util.Date;


/**
 * Represents the backend's view of the configurable items of a shopping basket.
 */
public class CPQItemSalesDoc extends ItemSalesDoc implements CPQItem
{
	ConfigModel productConfiguration;
	private boolean productConfigurationDirty = true;
	private Configuration externalConfiguration;
	private Date kbDate;

	/**
	 * @return the productConfiguration
	 */
	public ConfigModel getProductConfiguration()
	{
		return productConfiguration;
	}

	/**
	 * @param productConfiguration
	 *           the productConfiguration to set
	 */
	public void setProductConfiguration(final ConfigModel productConfiguration)
	{
		this.productConfiguration = productConfiguration;
	}

	/**
	 * @return the productConfigurationDirty
	 */
	public boolean isProductConfigurationDirty()
	{
		return productConfigurationDirty;
	}

	/**
	 * @param productConfigurationDirty
	 *           the productConfigurationDirty to set
	 */
	public void setProductConfigurationDirty(final boolean productConfigurationDirty)
	{
		this.productConfigurationDirty = productConfigurationDirty;
	}

	/**
	 * @return the externalConfiguration
	 */
	@Override
	public Configuration getExternalConfiguration()
	{
		return externalConfiguration;
	}

	/**
	 * @param externalConfiguration
	 *           the externalConfiguration to set
	 */
	@Override
	public void setExternalConfiguration(final Configuration externalConfiguration)
	{
		this.externalConfiguration = externalConfiguration;
	}

	/**
	 * @return the kbDate
	 */
	public Date getKbDate()
	{
		return kbDate;
	}

	/**
	 * @param kbDate
	 *           the kbDate to set
	 */
	public void setKbDate(final Date kbDate)
	{
		this.kbDate = kbDate;
	}

}
