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
import de.hybris.platform.sap.productconfig.runtime.cps.constants.SapproductconfigruntimecpsConstants;
import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.ConfigurationMasterDataService;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCharacteristicGroup;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSItem;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;


/**
 * Responsible to populate cstic groups from master data
 */
public class CharacteristicGroupMasterDataPopulator implements Populator<CPSCharacteristicGroup, CsticGroupModel>
{
	private ConfigurationMasterDataService masterDataService;

	@Override
	public void populate(final CPSCharacteristicGroup source, final CsticGroupModel target)
	{
		final String groupId = source.getId();
		final CPSItem parentItem = source.getParentItem();
		final String kbId = parentItem.getParentConfiguration().getKbId();
		final String productId = parentItem.getKey();
		if (groupId.equalsIgnoreCase(SapproductconfigruntimecpsConstants.CPS_GENERAL_GROUP_ID))
		{
			target.setName(InstanceModel.GENERAL_GROUP_NAME);
		}
		else
		{
			target.setName(groupId);
		}
		target.setDescription(masterDataService.getGroupName(kbId, productId, parentItem.getType(), groupId));
		target.setCsticNames(masterDataService.getGroupCharacteristicIDs(kbId, productId, parentItem.getType(), groupId));
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
