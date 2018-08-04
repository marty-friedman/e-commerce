/*
 *  
 * [y] hybris Platform
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.marketplacestore.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.marketplacestore.constants.MarketplacestoreConstants;
import org.apache.log4j.Logger;

@SuppressWarnings("PMD")
public class MarketplacestoreManager extends GeneratedMarketplacestoreManager
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger( MarketplacestoreManager.class.getName() );
	
	public static final MarketplacestoreManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (MarketplacestoreManager) em.getExtension(MarketplacestoreConstants.EXTENSIONNAME);
	}
	
}
