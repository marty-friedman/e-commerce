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

import de.hybris.platform.regioncache.CacheValueLoader;
import de.hybris.platform.regioncache.region.impl.EHCacheRegion;
import de.hybris.platform.sap.productconfig.runtime.cps.cache.CPSCacheKeyGenerator;
import de.hybris.platform.sap.productconfig.runtime.cps.cache.MasterDataCacheAccessService;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link MasterDataCacheAccessService}. Uses {@link EHCacheRegion} for caching. Caches KB
 * data per id and language, which means that master data attributes like characteristic types e.g. are cached
 * redundantly.
 */
public class MasterDataCacheAccessServiceImpl implements MasterDataCacheAccessService
{
	private EHCacheRegion cache;
	private CacheValueLoader<CPSMasterDataKnowledgeBaseContainer> loader;
	private CPSCacheKeyGenerator keyGenerator;

	@Override
	public CPSMasterDataKnowledgeBaseContainer getKbContainer(final String kbId, final String language)
	{
		final MasterDataCacheKey cacheKey = getKeyGenerator().createMasterDataCacheKey(kbId, language);

		return (CPSMasterDataKnowledgeBaseContainer) getCache().getWithLoader(cacheKey, getLoader());
	}

	@Override
	public void clearCache()
	{
		getCache().clearCache();
	}

	@Override
	public EHCacheRegion getCache()
	{
		return cache;
	}

	/**
	 * @param cache
	 *           Cache region
	 */
	@Required
	public void setCache(final EHCacheRegion cache)
	{
		this.cache = cache;
	}

	protected CacheValueLoader<CPSMasterDataKnowledgeBaseContainer> getLoader()
	{
		return loader;
	}

	/**
	 * @param loader
	 *           Loader for reading KB data via REST
	 */
	@Required
	public void setLoader(final CacheValueLoader<CPSMasterDataKnowledgeBaseContainer> loader)
	{
		this.loader = loader;
	}

	protected CPSCacheKeyGenerator getKeyGenerator()
	{
		return keyGenerator;
	}

	@Required
	public void setKeyGenerator(final CPSCacheKeyGenerator keyGenerator)
	{
		this.keyGenerator = keyGenerator;
	}
}
