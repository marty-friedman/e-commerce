/*
 *  
 * [y] hybris Platform
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.assistedservicepromotionaddon.jalo;

import de.hybris.platform.assistedservicepromotionaddon.constants.AssistedservicepromotionaddonConstants;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import org.apache.log4j.Logger;

@SuppressWarnings("PMD")
public class AssistedservicepromotionaddonManager extends GeneratedAssistedservicepromotionaddonManager
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger( AssistedservicepromotionaddonManager.class.getName() );
	
	public static final AssistedservicepromotionaddonManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (AssistedservicepromotionaddonManager) em.getExtension(AssistedservicepromotionaddonConstants.EXTENSIONNAME);
	}
	
}
