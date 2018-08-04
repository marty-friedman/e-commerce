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
package de.hybris.platform.sap.productconfig.runtime.cps.jalo;

import de.hybris.platform.core.Registry;
import de.hybris.platform.sap.productconfig.runtime.cps.constants.SapproductconfigruntimecpsConstants;



/**
 * Jalo Manager class for <code>sapcoreconfigruntimecps</code> extension.
 */
@SuppressWarnings("PMD")
public class SapproductconfigruntimecpsManager extends GeneratedSapproductconfigruntimecpsManager
{

	/**
	 * Get the valid instance of this manager.
	 *
	 * @return the current instance of this manager
	 */
	public static SapproductconfigruntimecpsManager getInstance()
	{
		return (SapproductconfigruntimecpsManager) Registry.getCurrentTenant().getJaloConnection().getExtensionManager()
				.getExtension(SapproductconfigruntimecpsConstants.EXTENSIONNAME);
	}

}
