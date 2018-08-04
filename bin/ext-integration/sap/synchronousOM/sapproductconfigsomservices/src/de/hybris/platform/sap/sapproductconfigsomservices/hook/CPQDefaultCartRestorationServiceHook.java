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
package de.hybris.platform.sap.sapproductconfigsomservices.hook;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtservices.hook.CartRestorationServiceHook;
import de.hybris.platform.sap.sapproductconfigsombol.transaction.item.businessobject.impl.CPQItem;
import de.hybris.platform.sap.sapproductconfigsomservices.prodconf.ProductConfigurationService;

/**
 * Hook implementation for CartRestorationServiceHook for adding configuration after creating item. 
 */
public class CPQDefaultCartRestorationServiceHook implements CartRestorationServiceHook
{
	private ProductConfigurationService productConfigurationService;

	@Override
	public void afterCreateItemHook(final AbstractOrderEntryModel orderEntry, final Item item)
	{
		if (orderEntry.getProduct().getSapConfigurable().booleanValue())
		{
			final ConfigModel configModel = getProductConfigurationService().getConfigModel(orderEntry.getProduct().getCode(),
					orderEntry.getExternalConfiguration());
			if (configModel != null)
			{
				final CPQItem cpqItem = (CPQItem) item;
				cpqItem.setProductConfiguration(configModel);
				cpqItem.setConfigurable(true);
				getProductConfigurationService().setIntoSession(cpqItem.getHandle(), configModel.getId());
			}
		}
	}

	/**
	 * @return the productConfigurationService
	 */
	public ProductConfigurationService getProductConfigurationService()
	{
		return productConfigurationService;
	}

	/**
	 * @param productConfigurationService the productConfigurationService to set
	 */
	public void setProductConfigurationService(final ProductConfigurationService productConfigurationService)
	{
		this.productConfigurationService = productConfigurationService;
	}
}
