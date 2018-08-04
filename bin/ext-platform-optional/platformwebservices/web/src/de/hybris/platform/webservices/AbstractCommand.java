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
package de.hybris.platform.webservices;

import de.hybris.platform.core.model.ItemModel;




public abstract class AbstractCommand<RESOURCE> extends AbstractYResponseBuilder<RESOURCE, Object, Object> implements
		Command<RESOURCE, Object, Object>
{
	private String name = null;

	public AbstractCommand()
	{
		super(null);
		this.name = this.getClass().getSimpleName().toLowerCase();
	}

	@Override
	public String getName()
	{
		return this.name;
	}

	@Override
	public final Object createResponseEntity(final RESOURCE resourceEntity, final Object requestEntity) throws Exception
	{
		Object result = this.execute(resourceEntity, requestEntity);

		if (result instanceof ItemModel)
		{
			result = super.modelToDto(result);
		}

		return result;
	}
}
