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
package de.hybris.platform.sap.productconfig.model.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.sap.productconfig.model.constants.SapproductconfigmodelConstants;


/**
 * Jalo Manager class for <code>sapproductconfigmodel</code> extension.
 */
@SuppressWarnings("PMD")
public class SapproductconfigmodelManager extends GeneratedSapproductconfigmodelManager
{

	/**
	 * factory-method for this class
	 *
	 * @return manager instance
	 */
	public static final SapproductconfigmodelManager getInstance()
	{
		final ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (SapproductconfigmodelManager) em.getExtension(SapproductconfigmodelConstants.EXTENSIONNAME);
	}

}
