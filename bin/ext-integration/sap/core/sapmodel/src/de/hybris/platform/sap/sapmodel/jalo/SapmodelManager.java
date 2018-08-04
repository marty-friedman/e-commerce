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
package de.hybris.platform.sap.sapmodel.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.sap.sapmodel.constants.SapmodelConstants;
import org.apache.log4j.Logger;

@SuppressWarnings("PMD")
public class SapmodelManager extends GeneratedSapmodelManager
{
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger( SapmodelManager.class.getName() );
	
	public static final SapmodelManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (SapmodelManager) em.getExtension(SapmodelConstants.EXTENSIONNAME);
	}
	
}
