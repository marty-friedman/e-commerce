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
package org.merchandise.cms.randompicturecmscomponent.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import org.merchandise.cms.randompicturecmscomponent.constants.RandompicturecmscomponentConstants;
import org.apache.log4j.Logger;

@SuppressWarnings("PMD")
public class RandompicturecmscomponentManager extends GeneratedRandompicturecmscomponentManager
{
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger( RandompicturecmscomponentManager.class.getName() );
	
	public static final RandompicturecmscomponentManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (RandompicturecmscomponentManager) em.getExtension(RandompicturecmscomponentConstants.EXTENSIONNAME);
	}
	
}
