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
package de.hybris.platform.webservices.objectgraphtransformer;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyContext;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyInterceptor;


public class ItemModelToUserModelConverter implements PropertyInterceptor<ItemModel, UserModel>
{
	@Override
	public UserModel intercept(final PropertyContext ctx, final ItemModel source)
	{
		return (UserModel) source;
	}

}
