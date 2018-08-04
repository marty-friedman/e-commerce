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
package com.hybris.ymkt.consent.jalo;

import de.hybris.platform.core.Registry;

import com.hybris.ymkt.consent.constants.SapymktconsentConstants;


/**
 * This is the extension manager of the Sapymktconsent extension.
 */
public class SapymktconsentManager extends GeneratedSapymktconsentManager
{

	/**
	 * Get the valid instance of this manager.
	 * 
	 * @return the current instance of this manager
	 */
	public static SapymktconsentManager getInstance()
	{
		return (SapymktconsentManager) Registry.getCurrentTenant().getJaloConnection().getExtensionManager()
				.getExtension(SapymktconsentConstants.EXTENSIONNAME);
	}

}
