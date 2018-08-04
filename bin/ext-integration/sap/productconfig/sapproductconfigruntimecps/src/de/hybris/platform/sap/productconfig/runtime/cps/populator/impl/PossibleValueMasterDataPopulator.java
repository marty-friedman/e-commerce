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
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSPossibleValue;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;


/**
 * Responsible to populate characteristics
 */
public class PossibleValueMasterDataPopulator implements Populator<CPSPossibleValue, CsticValueModel>
{
	private ConfigurationMasterDataService masterDataService;


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

	@Override
	public void populate(final CPSPossibleValue source, final CsticValueModel target)
	{
		final String valueString = source.getValueLow();
		if (valueString != null)
		{
			final String kbId = source.getParentCharacteristic().getParentItem().getParentConfiguration().getKbId();
			final String characteristicId = source.getParentCharacteristic().getId();
			target.setLanguageDependentName(masterDataService.getValueName(kbId, characteristicId, valueString));
			target.setNumeric(masterDataService.isCharacteristicNumeric(kbId, characteristicId));
		}

	}

}
