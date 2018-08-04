/*
 *  
 * [y] hybris Platform
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sapdigitalpaymentaddon.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.sapdigitalpaymentaddon.constants.SapdigitalpaymentaddonConstants;
import org.apache.log4j.Logger;

@SuppressWarnings("PMD")
public class SapdigitalpaymentaddonManager extends GeneratedSapdigitalpaymentaddonManager
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger( SapdigitalpaymentaddonManager.class.getName() );
	
	public static final SapdigitalpaymentaddonManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (SapdigitalpaymentaddonManager) em.getExtension(SapdigitalpaymentaddonConstants.EXTENSIONNAME);
	}
	
}
