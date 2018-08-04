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
package com.hybris.ymkt.clickstream.jalo;

import de.hybris.platform.core.Registry;

import com.hybris.ymkt.clickstream.constants.SapymktclickstreamConstants;


/**
 * This is the extension manager of the Sapymktclickstream extension.
 */
public class SapymktclickstreamManager extends GeneratedSapymktclickstreamManager
{
	/**
	 * Get the valid instance of this manager.
	 *
	 * @return the current instance of this manager
	 */
	public static SapymktclickstreamManager getInstance()
	{
		return (SapymktclickstreamManager) Registry.getCurrentTenant().getJaloConnection().getExtensionManager()
				.getExtension(SapymktclickstreamConstants.EXTENSIONNAME);
	}
}