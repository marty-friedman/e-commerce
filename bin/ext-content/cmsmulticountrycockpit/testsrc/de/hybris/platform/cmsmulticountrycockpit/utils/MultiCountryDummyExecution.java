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
package de.hybris.platform.cmsmulticountrycockpit.utils;

import de.hybris.platform.cockpit.zk.mock.DummyExecution;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;


public class MultiCountryDummyExecution extends DummyExecution
{
	private final Map<String, Object> map = new HashMap<>();

	public MultiCountryDummyExecution(final ApplicationContext applicationContext)
	{
		super(applicationContext);
	}

	@Override
	public Object getAttribute(final String key)
	{
		return map.get(key);
	}

	@Override
	public void setAttribute(final String name, final Object value)
	{
		map.put(name, value);
	}

	@Override
	public Map<String, Object> getAttributes()
	{
		return map;
	}

	@Override
	public void removeAttribute(final String key)
	{
		map.remove(key);
	}
}
