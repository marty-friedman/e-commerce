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
package de.hybris.platform.b2badmincockpit.services.label.impl;

import de.hybris.platform.b2b.model.B2BCommentModel;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.services.label.ObjectLabelProvider;


public class B2BCommentLabelProvider implements ObjectLabelProvider
{
	@Override
	public String getLabel(final TypedObject typeObject)
	{
		return getLabel(typeObject, null);
	}

	@Override
	public String getLabel(final TypedObject typeObject, final String languageIso)
	{
		return ((B2BCommentModel) typeObject.getObject()).getComment();
	}

	@Override
	public String getDescription(final TypedObject object)
	{
		return "";
	}

	@Override
	public String getDescription(final TypedObject object, final String languageIso)
	{
		return "";
	}

	@Override
	public String getIconPath(final TypedObject object)
	{
		return null;
	}

	@Override
	public String getIconPath(final TypedObject object, final String languageIso)
	{
		return null;
	}
}
