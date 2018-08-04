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

import de.hybris.platform.regioncache.region.impl.EHCacheRegion;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;


/**
 * Facilitates cache access for master data
 */
public interface MasterDataCacheAccessService
{
	/**
	 * Retrieves the knowledgebase container for a given id and language
	 * 
	 * @param kbId
	 *           knowledgebase id
	 * @param language
	 *           language code
	 * @return knowledgebase for id and language code
	 */
	CPSMasterDataKnowledgeBaseContainer getKbContainer(final String kbId, final String language);

	/**
	 * Access the Cache Region object
	 *
	 * @return CacheRegion object
	 */
	EHCacheRegion getCache();

	/**
	 * Clears the cache
	 */
	void clearCache();

}
