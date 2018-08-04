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
package de.hybris.platform.sap.productconfig.runtime.cps.cache.impl;

import de.hybris.platform.regioncache.key.AbstractCacheKey;


/**
 * Abstract Cache Key for CPS that encapsulates technical attributes
 */
public class AbstractCPSCacheKey extends AbstractCacheKey
{
	protected static final int PRIME = 31;
	private final String cpsServiceUrl;
	private final String cpsServiceTenant;

	/**
	 * Default constructor
	 *
	 * @param typecode
	 *           typecode of the cache key
	 * @param tenantId
	 *           tenant id
	 * @param cpsServiceUrl
	 *           CPS service url
	 * @param cpsServiceTenant
	 *           CPS service tenant
	 *
	 */
	public AbstractCPSCacheKey(final String typecode, final String tenantId, final String cpsServiceUrl,
			final String cpsServiceTenant)
	{
		super(typecode, tenantId);
		this.cpsServiceUrl = cpsServiceUrl;
		this.cpsServiceTenant = cpsServiceTenant;
	}

	@Override
	public int hashCode()
	{
		if (this.cachedHash == 0)
		{
			int result = 1;
			result = PRIME * result + ((this.typeCode == null) ? 0 : this.typeCode.hashCode());
			result = ((this.tenantId == null) ? 0 : this.tenantId.hashCode()) ^ result;
			result = ((this.cpsServiceUrl == null) ? 0 : this.cpsServiceUrl.hashCode()) ^ result;
			result = ((this.cpsServiceTenant == null) ? 0 : this.cpsServiceTenant.hashCode()) ^ result;
			this.cachedHash = result;
		}
		return this.cachedHash;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (!super.equals(obj))
		{
			return false;
		}
		final AbstractCPSCacheKey cacheKey = (AbstractCPSCacheKey) obj;
		if (!this.cpsServiceUrl.equals(cacheKey.getCpsServiceUrl()))
		{
			return false;
		}
		return this.cpsServiceTenant.equals(cacheKey.getCpsServiceTenant());
	}

	/**
	 * @return the cpsServiceUrl
	 */
	public String getCpsServiceUrl()
	{
		return cpsServiceUrl;
	}

	/**
	 * @return the cpsServiceTenant
	 */
	public String getCpsServiceTenant()
	{
		return cpsServiceTenant;
	}

}
