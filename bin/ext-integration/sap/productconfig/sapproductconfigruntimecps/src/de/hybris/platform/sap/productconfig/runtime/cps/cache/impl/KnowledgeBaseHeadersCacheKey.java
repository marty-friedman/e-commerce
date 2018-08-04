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
 * Immutable Cache Key for KB Header Lists in CPS context.
 */
public class KnowledgeBaseHeadersCacheKey extends AbstractCPSCacheKey
{
	static final String TYPECODE_KNOWLEDGEBASES = "__KNOWLEDGEBASES__";

	private final String product;

	/**
	 * Default Constructor
	 *
	 * @param product
	 *           product code
	 * @param tenantId
	 *           tennantId
	 * @param cpsServiceUrl
	 *           cps service url
	 * @param cpsServiceTenant
	 *           cps service tenant
	 */
	public KnowledgeBaseHeadersCacheKey(final String product, final String tenantId, final String cpsServiceUrl,
			final String cpsServiceTenant)
	{
		super(TYPECODE_KNOWLEDGEBASES, tenantId, cpsServiceUrl, cpsServiceTenant);
		this.product = product;
	}

	@Override
	public int hashCode()
	{
		if (this.cachedHash == 0)
		{
			int result = super.hashCode();
			result = ((this.product == null) ? 0 : this.product.hashCode()) ^ result;
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
		if (!(obj instanceof KnowledgeBaseHeadersCacheKey))
		{
			return false;
		}
		final KnowledgeBaseHeadersCacheKey cacheKey = (KnowledgeBaseHeadersCacheKey) obj;
		return this.product.equals(cacheKey.getProduct());
	}

	@Override
	public String toString()
	{
		return new StringBuilder().append("CacheKey(").append(this.tenantId).append(")[valueType=").append(")[valueType=")
				.append(this.valueType).append(", tenantId=").append(this.tenantId).append(", typeCode=")
				.append(LegacyCacheKeyType.getFullTypeName(this.typeCode)).append(", cpsServiceUrl=").append(this.getCpsServiceUrl())
				.append(", cpsServiceTenant=").append(this.getCpsServiceTenant()).append(", product=").append(this.product)
				.append("]").toString();
	}

	/**
	 * @return product code
	 */
	public String getProduct()
	{
		return product;
	}
}
