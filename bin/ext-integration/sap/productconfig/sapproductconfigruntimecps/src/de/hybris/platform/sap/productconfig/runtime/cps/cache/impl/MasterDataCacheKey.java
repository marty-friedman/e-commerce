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

import de.hybris.platform.regioncache.key.legacy.LegacyCacheKeyType;


/**
 * Immutable Cache Key for master data in CPS context.
 */
public class MasterDataCacheKey extends AbstractCPSCacheKey
{
	static final String TYPECODE_MASTER_DATA = "__MASTER_DATA__";

	private final String kbId;
	private final String lang;

	/**
	 * Default Constructor
	 *
	 * @param kbId
	 *           id of the knowledge base
	 * @param lang
	 *           language key
	 * @param tenantId
	 *           tennantId
	 * @param cpsServiceUrl
	 *           cps service url
	 * @param cpsServiceTenant
	 *           cps service tenant
	 */
	public MasterDataCacheKey(final String kbId, final String lang, final String tenantId, final String cpsServiceUrl,
			final String cpsServiceTenant)
	{
		super(TYPECODE_MASTER_DATA, tenantId, cpsServiceUrl, cpsServiceTenant);
		this.kbId = kbId;
		this.lang = lang;
	}

	@Override
	public int hashCode()
	{
		if (this.cachedHash == 0)
		{
			int result = super.hashCode();
			result = ((this.kbId == null) ? 0 : this.kbId.hashCode()) ^ result;
			result = ((this.lang == null) ? 0 : this.lang.hashCode()) ^ result;
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
		if (!(obj instanceof MasterDataCacheKey))
		{
			return false;
		}
		final MasterDataCacheKey cacheKey = (MasterDataCacheKey) obj;
		if (!this.kbId.equals(cacheKey.getKbId()))
		{
			return false;
		}
		return this.lang.equals(cacheKey.getLang());
	}

	@Override
	public String toString()
	{
		return new StringBuilder().append("CacheKey(").append(this.tenantId).append(")[valueType=").append(")[valueType=")
				.append(this.valueType).append(", tenantId=").append(this.tenantId).append(", typeCode=")
				.append(LegacyCacheKeyType.getFullTypeName(this.typeCode)).append(", cpsServiceUrl=").append(this.getCpsServiceUrl())
				.append(", cpsServiceTenant=").append(this.getCpsServiceTenant()).append(", kbId=").append(this.kbId)
				.append(", language:").append(this.lang).append("]").toString();
	}

	/**
	 * @return id of the knowledge base
	 */
	public String getKbId()
	{
		return kbId;
	}

	/**
	 * @return language key
	 */
	public String getLang()
	{
		return lang;
	}
}
