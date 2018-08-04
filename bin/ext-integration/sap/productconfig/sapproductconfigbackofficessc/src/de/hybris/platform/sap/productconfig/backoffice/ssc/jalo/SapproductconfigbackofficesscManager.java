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
package de.hybris.platform.sap.productconfig.backoffice.ssc.jalo;

import de.hybris.platform.core.Registry;
import de.hybris.platform.sap.productconfig.backoffice.ssc.constants.SapproductconfigbackofficesscConstants;


/**
 * Jalo Manager class for <code>sapcoreconfigurationbackofficessc</code> extension.
 */
public class SapproductconfigbackofficesscManager extends GeneratedSapproductconfigbackofficesscManager
{

	/**
	 * Get the valid instance of this manager.
	 *
	 * @return the current instance of this manager
	 */
	public static SapproductconfigbackofficesscManager getInstance()
	{
		return (SapproductconfigbackofficesscManager) Registry.getCurrentTenant().getJaloConnection().getExtensionManager()
				.getExtension(SapproductconfigbackofficesscConstants.EXTENSIONNAME);
	}

}
