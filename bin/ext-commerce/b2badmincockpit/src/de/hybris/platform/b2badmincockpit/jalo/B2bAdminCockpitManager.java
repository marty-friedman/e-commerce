/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.b2badmincockpit.jalo;

import de.hybris.platform.b2badmincockpit.constants.B2bAdminCockpitConstants;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;

import org.apache.log4j.Logger;


public class B2bAdminCockpitManager extends GeneratedB2bAdminCockpitManager
{
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(B2bAdminCockpitManager.class.getName());

	public static final B2bAdminCockpitManager getInstance()
	{
		final ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (B2bAdminCockpitManager) em.getExtension(B2bAdminCockpitConstants.EXTENSIONNAME);
	}

}
