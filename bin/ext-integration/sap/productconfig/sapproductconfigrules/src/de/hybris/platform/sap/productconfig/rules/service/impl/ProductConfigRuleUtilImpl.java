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
package de.hybris.platform.sap.productconfig.rules.service.impl;

import de.hybris.platform.sap.productconfig.rules.service.ProductConfigRuleUtil;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;


/**
 * Default implementation of the {@link ProductConfigRuleUtil}.
 */
public class ProductConfigRuleUtilImpl implements ProductConfigRuleUtil
{
	private static final int LOW = 16;

	protected void getPlainCstic(final InstanceModel instance, final List<CsticModel> cstics,
			final Map<String, CsticModel> csticMap)
	{
		if (CollectionUtils.isNotEmpty(instance.getCstics()))
		{
			for (final CsticModel cstic : instance.getCstics())
			{
				if (!csticMap.containsKey(cstic.getName()))
				{
					cstics.add(cstic);
					csticMap.put(cstic.getName(), cstic);
				}
			}
		}
	}

	protected void getPlainCsticFromSubInstance(final List<InstanceModel> subInstances, final List<CsticModel> cstics,
			final Map<String, CsticModel> csticMap)
	{
		if (CollectionUtils.isNotEmpty(subInstances))
		{
			for (final InstanceModel subInstance : subInstances)
			{
				getPlainCstics(subInstance, cstics, csticMap);
			}
		}
	}

	protected void getPlainCstics(final InstanceModel instance, final List<CsticModel> cstics,
			final Map<String, CsticModel> csticMap)
	{
		if (instance != null)
		{
			getPlainCstic(instance, cstics, csticMap);
			getPlainCsticFromSubInstance(instance.getSubInstances(), cstics, csticMap);
		}
	}

	@Override
	public List<CsticModel> getCstics(final ConfigModel source)
	{
		final int size = calculateInitialSize(source);
		final List<CsticModel> cstics = new ArrayList<>(size);
		if (source != null && source.getRootInstance() != null)
		{
			final Map<String, CsticModel> csticMap = new HashMap<>(size);
			getPlainCstics(source.getRootInstance(), cstics, csticMap);
		}
		return cstics;
	}

	@Override
	public Map<String, CsticModel> getCsticMap(final ConfigModel source)
	{
		final int size = calculateInitialSize(source);
		final Map<String, CsticModel> csticMap = new HashMap<>(size);
		if (source != null && source.getRootInstance() != null)
		{
			final List<CsticModel> cstics = new ArrayList<>(size);
			getPlainCstics(source.getRootInstance(), cstics, csticMap);
		}
		return csticMap;
	}

	protected int calculateInitialSize(final ConfigModel source)
	{
		int size = 0;
		if (source != null && source.getRootInstance() != null)
		{
			final List<CsticModel> cstics = source.getRootInstance().getCstics();
			if (cstics != null)
			{
				size = (int) (cstics.size() / 0.75f + 1);
			}
			if (!source.isSingleLevel() && size < LOW)
			{
				size = LOW;
			}
		}
		return size;
	}

}
