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
package com.hybris.ymkt.segmentation.jalo;

import de.hybris.platform.core.Registry;

import com.hybris.ymkt.segmentation.constants.Sapymktsegmentationb2bConstants;



/**
 * This is the extension manager of the Sapymktsegmentationb2b extension.
 */
public class Sapymktsegmentationb2bManager extends GeneratedSapymktsegmentationb2bManager
{
	/**
	 * Get the valid instance of this manager.
	 * 
	 * @return the current instance of this manager
	 */
	public static Sapymktsegmentationb2bManager getInstance()
	{
		return (Sapymktsegmentationb2bManager) Registry.getCurrentTenant().getJaloConnection().getExtensionManager()
				.getExtension(Sapymktsegmentationb2bConstants.EXTENSIONNAME);
	}

}
