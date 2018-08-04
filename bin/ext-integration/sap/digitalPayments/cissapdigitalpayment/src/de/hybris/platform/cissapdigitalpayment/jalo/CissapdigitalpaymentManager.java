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
package de.hybris.platform.cissapdigitalpayment.jalo;

import de.hybris.platform.cissapdigitalpayment.constants.CissapdigitalpaymentConstants;
import de.hybris.platform.cissapdigitalpayment.jalo.GeneratedCissapdigitalpaymentManager;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;

import org.apache.log4j.Logger;


@SuppressWarnings("PMD")
public class CissapdigitalpaymentManager extends GeneratedCissapdigitalpaymentManager
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(CissapdigitalpaymentManager.class.getName());

	public static final CissapdigitalpaymentManager getInstance()
	{
		final ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (CissapdigitalpaymentManager) em.getExtension(CissapdigitalpaymentConstants.EXTENSIONNAME);
	}

}
