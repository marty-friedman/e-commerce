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
package com.sap.hybris.jalo;

import com.sap.hybris.constants.SapreturnsbackofficeConstants;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import org.apache.log4j.Logger;

@SuppressWarnings("PMD")
public class SapreturnsbackofficeManager extends GeneratedSapreturnsbackofficeManager
{
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger( SapreturnsbackofficeManager.class.getName() );
	
	public static final SapreturnsbackofficeManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (SapreturnsbackofficeManager) em.getExtension(SapreturnsbackofficeConstants.EXTENSIONNAME);
	}
	
}
