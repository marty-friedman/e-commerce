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
package de.hybris.platform.b2badmincockpit.wizards.impl;

import java.util.HashMap;



/**
 *
 */
public class B2BSummaryOrganizationWizardPage extends AbstractB2BOrganizationWizardPage
{
	public Object getPageAttributes(final String pageId, final String id)
	{
		return ((HashMap<String, Object>) wizard.getWizardContext().getAttribute(pageId)).get(id);
	}

}
