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
package de.hybris.platform.sap.productconfig.runtime.interf.model.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.ConfigModelFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigModelFactoryImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.BaseModel;
import de.hybris.platform.servicelayer.internal.service.ServicelayerUtils;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;


/**
 * Default implementation of the {@link BaseModel}
 */
public class BaseModelImpl implements BaseModel
{

	private Map<String, String> extensionMap = new HashMap<String, String>();
	private ConfigModelFactory configModelFactory;
	private static final String SAP_PRODUCT_CONFIG_MODEL_FACTORY = "sapProductConfigModelFactory";
	private static final Logger LOG = Logger.getLogger(BaseModelImpl.class);
	protected static final int PRIME = 31;

	/**
	 * @deprecated since 6.5
	 */
	@SuppressWarnings("squid:S1133")
	@Deprecated
	@Override
	public Map<String, String> getExtensionMap()
	{
		return extensionMap;
	}

	/**
	 * @deprecated since 6.5
	 */
	@SuppressWarnings("squid:S1133")
	@Deprecated
	@Override
	public void setExtensionMap(final Map<String, String> extensionMap)
	{
		this.extensionMap = extensionMap;
	}

	/**
	 * @deprecated since 6.5
	 */
	@SuppressWarnings("squid:S1133")
	@Deprecated
	@Override
	public void putExtensionData(final String key, final String value)
	{
		extensionMap.put(key, value);
	}

	/**
	 * @deprecated since 6.5
	 */
	@SuppressWarnings("squid:S1133")
	@Deprecated
	@Override
	public String getExtensionData(final String key)
	{
		String value = null;
		if (extensionMap != null)
		{
			value = extensionMap.get(key);
		}
		return value;
	}


	@Override
	public int hashCode()
	{
		int result = 1;
		result = PRIME * result + ((extensionMap == null) ? 0 : extensionMap.hashCode());
		return result;
	}


	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final BaseModelImpl other = (BaseModelImpl) obj;
		if (extensionMap == null)
		{
			if (other.extensionMap != null)
			{
				return false;
			}
		}
		else if (!extensionMap.equals(other.extensionMap))
		{
			return false;
		}
		return true;
	}

	protected ConfigModelFactory getConfigModelFactory()
	{
		if (null == configModelFactory)
		{
			final ApplicationContext applicationContext = getApplicationContext();
			if (applicationContext.containsBean(SAP_PRODUCT_CONFIG_MODEL_FACTORY))
			{
				configModelFactory = (ConfigModelFactory) applicationContext.getBean(SAP_PRODUCT_CONFIG_MODEL_FACTORY);
			}
			else
			{
				LOG.warn("Fallback for integration tests, if this happens in productive mode, check you spring configurations.");
				configModelFactory = new ConfigModelFactoryImpl();
			}
		}
		return configModelFactory;
	}

	protected ApplicationContext getApplicationContext()
	{
		return ServicelayerUtils.getApplicationContext();
	}

	/**
	 * @param configModelFactory
	 *           factorz for config model objects
	 */
	public void setConfigModelFactory(final ConfigModelFactory configModelFactory)
	{
		this.configModelFactory = configModelFactory;
	}
}
