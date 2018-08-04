/*
 *  
 * [y] hybris Platform
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.selectivecartaddon.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.selectivecartaddon.constants.SelectivecartaddonConstants;
import org.apache.log4j.Logger;

@SuppressWarnings("PMD")
public class SelectivecartaddonManager extends GeneratedSelectivecartaddonManager
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger( SelectivecartaddonManager.class.getName() );
	
	public static final SelectivecartaddonManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (SelectivecartaddonManager) em.getExtension(SelectivecartaddonConstants.EXTENSIONNAME);
	}
	
}
