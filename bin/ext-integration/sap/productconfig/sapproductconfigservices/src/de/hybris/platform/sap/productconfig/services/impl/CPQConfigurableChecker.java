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
package de.hybris.platform.sap.productconfig.services.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.commerceservices.product.impl.DefaultProductConfigurableChecker;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.model.AbstractConfiguratorSettingModel;
import de.hybris.platform.variants.model.VariantProductModel;

import javax.annotation.Nonnull;


/**
 * Support class to check if a specific product is configurable with the CPQ configurator.
 */
public class CPQConfigurableChecker extends DefaultProductConfigurableChecker
{
	/**
	 * Check if the given product can be configured by the CPQ configurator.
	 *
	 * @param product
	 *           The product to check
	 * @return TRUE if it is a CPQ product, otherwise FALSE
	 */
	public boolean isCPQConfigurableProduct(@Nonnull
	final ProductModel product)
	{
		validateParameterNotNullStandardMessage("product", product);

		if (product instanceof VariantProductModel)
		{
			return false;
		}

		return getConfiguratorSettingsService().getConfiguratorSettingsForProduct(product).stream()
				.anyMatch(this::isCPQConfigurator);
	}

	protected boolean isCPQConfigurator(final AbstractConfiguratorSettingModel configService)
	{
		return configService.getConfiguratorType() == ConfiguratorType.CPQCONFIGURATOR;
	}
}
