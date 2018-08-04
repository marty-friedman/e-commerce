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
package de.hybris.platform.sap.productconfig.runtime.cps.cache;

import de.hybris.platform.sap.productconfig.runtime.cps.cache.impl.KnowledgeBaseHeadersCacheKey;
import de.hybris.platform.sap.productconfig.runtime.cps.cache.impl.MasterDataCacheKey;


/**
 * Generates cache keys to be used for hybris cache regions in CPS context
 */
public interface CPSCacheKeyGenerator
{
	/**
	 * Creates a cache key for the master data cache region
	 * 
	 * @param kbId
	 *           knowledgebase id
	 * @param lang
	 *           language
	 * @return the created cache key
	 */
	MasterDataCacheKey createMasterDataCacheKey(final String kbId, final String lang);

	/**
	 * Creates a cache key for the knowledgebase headers cache region
	 * 
	 * @param product
	 *           product for which knowledgebases are looked up
	 * @return the created cache key
	 */
	KnowledgeBaseHeadersCacheKey createKnowledgeBaseHeadersCacheKey(final String product);
}
