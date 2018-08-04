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
package de.hybris.platform.sap.productconfig.facades.populator;

import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.converters.ConfigurablePopulator;
import de.hybris.platform.sap.productconfig.facades.ClassificationSystemCPQAttributesProvider;
import de.hybris.platform.sap.productconfig.facades.impl.ConfigPricingImpl;
import de.hybris.platform.sap.productconfig.facades.impl.NoConfigPrice;
import de.hybris.platform.sap.productconfig.facades.overview.CharacteristicValue;
import de.hybris.platform.sap.productconfig.facades.overview.ValuePositionTypeEnum;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ClassificationSystemCPQAttributesContainer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * Transforms an {@link CsticValueModel} into into a list of {@link CharacteristicValue} data as required by the
 * configuration overview page.<br>
 * It is typically called by the {@link ConfigurationOverviewInstancePopulator}.
 */
public class ConfigurationOverviewValuePopulator implements ConfigurablePopulator<CsticValueModel, CharacteristicValue, Map>
{
	static final String HYBRIS_NAMES = "HYBRIS_NAMES";
	static final String CSTIC_MODEL = "CSTIC_MODEL";
	static final String VALUE_POSITION_TYPE = "VALUE_POSITION_TYPE";

	private ClassificationSystemCPQAttributesProvider nameProvider;
	private ConfigPricingImpl configPricing;

	@Override
	public void populate(final CsticValueModel source, final CharacteristicValue target, final Collection<Map> options)
	{
		final HashMap optionsMap = (HashMap) options.iterator().next();
		final CsticModel cstic = (CsticModel) optionsMap.get(CSTIC_MODEL);
		final ClassificationSystemCPQAttributesContainer hybrisNames = (ClassificationSystemCPQAttributesContainer) optionsMap
				.get(HYBRIS_NAMES);
		final ValuePositionTypeEnum valuePositionType = (ValuePositionTypeEnum) optionsMap.get(VALUE_POSITION_TYPE);

		final ClassificationSystemCPQAttributesProvider cpqNameProvider = getNameProvider();
		final boolean isNameProviderDebugEnabled = cpqNameProvider.isDebugEnabled();

		target.setCharacteristic(cpqNameProvider.getDisplayName(cstic, hybrisNames, isNameProviderDebugEnabled));

		target.setValue(cpqNameProvider.getOverviewValueName(source, cstic, hybrisNames, isNameProviderDebugEnabled));

		target.setPriceDescription(setCsticPriceDescription(source.getValuePrice()));

		target.setValuePositionType(valuePositionType);
	}

	protected String setCsticPriceDescription(final PriceModel valuePrice)
	{
		final PriceData price = getConfigPricing().getPriceData(valuePrice);
		if (price instanceof NoConfigPrice)
		{
			return null;
		}

		return price.getFormattedValue();
	}

	/**
	 * @return the hybris characteristic and value name provider
	 */
	protected ClassificationSystemCPQAttributesProvider getNameProvider()
	{
		return nameProvider;
	}

	/**
	 * @param nameProvider
	 *           hybris characteristic and value name provider
	 */
	public void setNameProvider(final ClassificationSystemCPQAttributesProvider nameProvider)
	{
		this.nameProvider = nameProvider;
	}

	/**
	 * @return the configPricing
	 */
	public ConfigPricingImpl getConfigPricing()
	{
		return configPricing;
	}

	/**
	 * @param configPricing
	 *           the configPricing to set
	 */
	public void setConfigPricing(final ConfigPricingImpl configPricing)
	{
		this.configPricing = configPricing;
	}
}
