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
package ycockpitpackage.components.navigationarea;

import de.hybris.platform.cockpit.components.navigationarea.DefaultNavigationAreaModel;
import de.hybris.platform.cockpit.session.impl.AbstractUINavigationArea;

import ycockpitpackage.session.impl.YcockpitNavigationArea;


/**
 * Ycockpit navigation area model.
 */
public class YcockpitNavigationAreaModel extends DefaultNavigationAreaModel
{
	public YcockpitNavigationAreaModel()
	{
		super();
	}

	public YcockpitNavigationAreaModel(final AbstractUINavigationArea area)
	{
		super(area);
	}

	@Override
	public YcockpitNavigationArea getNavigationArea()
	{
		return (YcockpitNavigationArea) super.getNavigationArea();
	}
}
