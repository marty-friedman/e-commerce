/*
 *  
 * [y] hybris Platform
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.ysapdpordermanagement.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.ysapdpordermanagement.constants.YsapdpordermanagementConstants;
import org.apache.log4j.Logger;

@SuppressWarnings("PMD")
public class YsapdpordermanagementManager extends GeneratedYsapdpordermanagementManager
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger( YsapdpordermanagementManager.class.getName() );
	
	public static final YsapdpordermanagementManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (YsapdpordermanagementManager) em.getExtension(YsapdpordermanagementConstants.EXTENSIONNAME);
	}
	
}
