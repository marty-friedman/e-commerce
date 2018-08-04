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
package com.hybris.ymkt.common.jalo;

import de.hybris.platform.core.Registry;

import com.hybris.ymkt.common.constants.SapymktcommonConstants;



/**
 * This is the extension manager of the Sapymktcommon extension.
 */
public class SapymktcommonManager extends GeneratedSapymktcommonManager
{
	/**
	 * Get the valid instance of this manager.
	 *
	 * @return the current instance of this manager
	 */
	public static SapymktcommonManager getInstance()
	{
		return (SapymktcommonManager) Registry.getCurrentTenant().getJaloConnection().getExtensionManager()
				.getExtension(SapymktcommonConstants.EXTENSIONNAME);
	}
}
