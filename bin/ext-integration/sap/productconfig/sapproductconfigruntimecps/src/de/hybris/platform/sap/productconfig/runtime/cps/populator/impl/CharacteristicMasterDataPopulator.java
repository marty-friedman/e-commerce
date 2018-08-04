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
import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.ConfigurationMasterDataService;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataCharacteristicContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCharacteristic;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;


/**
 * Responsible to populate characteristics
 */
public class CharacteristicMasterDataPopulator implements Populator<CPSCharacteristic, CsticModel>
{
	private ConfigurationMasterDataService masterDataService;

	@Override
	public void populate(final CPSCharacteristic source, final CsticModel target)
	{
		final String kbId = source.getParentItem().getParentConfiguration().getKbId();
		final CPSMasterDataCharacteristicContainer characteristicMasterData = masterDataService.getCharacteristic(kbId,
				source.getId());
		target.setLanguageDependentName(characteristicMasterData.getName());
		target.setMultivalued(characteristicMasterData.isMultiValued());
		target.setValueType(getValueType(characteristicMasterData.getType()));
		target.setEntryFieldMask(characteristicMasterData.getEntryFieldMask());
		final Integer numberDecimals = characteristicMasterData.getNumberDecimals();
		if (numberDecimals != null)
		{
			target.setNumberScale(numberDecimals.intValue());
		}

		final Integer length = characteristicMasterData.getLength();
		if (length != null)
		{
			target.setTypeLength(length.intValue());
		}
	}

	protected int getValueType(final String type)
	{
		if (type == null)
		{
			return CsticModel.TYPE_STRING;
		}

		switch (type)
		{
			case "float":
				return CsticModel.TYPE_FLOAT;
			case "int":
				return CsticModel.TYPE_INTEGER;
			case "string":
				return CsticModel.TYPE_STRING;
			case "date":
				return CsticModel.TYPE_DATE;

			default:
				return CsticModel.TYPE_STRING;
		}
	}

	/**
	 * @return the masterDataService
	 */
	public ConfigurationMasterDataService getMasterDataService()
	{
		return masterDataService;
	}

	/**
	 * @param masterDataService
	 *           the masterDataService to set
	 */
	public void setMasterDataService(final ConfigurationMasterDataService masterDataService)
	{
		this.masterDataService = masterDataService;
	}

}
