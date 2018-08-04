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
package de.hybris.platform.webservices.util.objectgraphtransformer.impl;

import de.hybris.platform.webservices.util.objectgraphtransformer.GraphConfig;
import de.hybris.platform.webservices.util.objectgraphtransformer.NodeConfig;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;


public class DefaultGraphConfig implements GraphConfig
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(DefaultGraphConfig.class);

	private Map<Class, NodeConfig> nodesMap = null;

	public DefaultGraphConfig()
	{
		this.nodesMap = new HashMap<Class, NodeConfig>();
	}

	@Override
	public Map<Class, NodeConfig> getNodes()
	{
		return this.nodesMap;
	}

	public void addNode(final NodeConfig nodeConfig)
	{
		this.nodesMap.put(nodeConfig.getType(), nodeConfig);
	}
}
