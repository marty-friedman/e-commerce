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
package com.sap.hybris.sapomsreturnprocess.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import org.apache.log4j.Logger;

import com.sap.hybris.sapomsreturnprocess.constants.YsapomsreturnprocessConstants;

@SuppressWarnings("PMD")
public class YsapomsreturnprocessManager extends GeneratedYsapomsreturnprocessManager
{
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger( YsapomsreturnprocessManager.class.getName() );
	
	public static final YsapomsreturnprocessManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (YsapomsreturnprocessManager) em.getExtension(YsapomsreturnprocessConstants.EXTENSIONNAME);
	}
	
}
