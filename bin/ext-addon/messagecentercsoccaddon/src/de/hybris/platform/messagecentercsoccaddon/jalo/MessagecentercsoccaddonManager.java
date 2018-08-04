/*
 *  
 * [y] hybris Platform
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.messagecentercsoccaddon.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.messagecentercsoccaddon.constants.MessagecentercsoccaddonConstants;
import org.apache.log4j.Logger;

@SuppressWarnings("PMD")
public class MessagecentercsoccaddonManager extends GeneratedMessagecentercsoccaddonManager
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger( MessagecentercsoccaddonManager.class.getName() );
	
	public static final MessagecentercsoccaddonManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (MessagecentercsoccaddonManager) em.getExtension(MessagecentercsoccaddonConstants.EXTENSIONNAME);
	}
	
}
