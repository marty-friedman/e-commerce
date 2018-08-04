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

import de.hybris.platform.core.Registry;
import de.hybris.platform.sap.productconfig.runtime.cps.cache.CPSCacheKeyGenerator;
import de.hybris.platform.sap.productconfig.runtime.cps.client.KbDeterminationClient;
import de.hybris.platform.sap.productconfig.runtime.cps.client.MasterDataClient;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.yaasconfiguration.model.BaseSiteServiceMappingModel;
import de.hybris.platform.yaasconfiguration.model.YaasClientCredentialModel;
import de.hybris.platform.yaasconfiguration.model.YaasServiceModel;
import de.hybris.platform.yaasconfiguration.service.YaasConfigurationService;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link CPSCacheKeyGenerator}
 */
public class CPSCacheKeyGeneratorImpl implements CPSCacheKeyGenerator
{
	private YaasConfigurationService yaasConfigurationService;
	private BaseSiteService baseSiteService;

	@Override
	public MasterDataCacheKey createMasterDataCacheKey(final String kbId, final String lang)
	{
		final Pair<String, String> parameterPair = getCPSServiceParameter(MasterDataClient.class.getSimpleName());
		return new MasterDataCacheKey(kbId, lang, getTenantId(), parameterPair.getLeft(), parameterPair.getRight());
	}

	@Override
	public KnowledgeBaseHeadersCacheKey createKnowledgeBaseHeadersCacheKey(final String product)
	{
		final Pair<String, String> parameterPair = getCPSServiceParameter(KbDeterminationClient.class.getSimpleName());
		return new KnowledgeBaseHeadersCacheKey(product, getTenantId(), parameterPair.getLeft(), parameterPair.getRight());
	}


	protected String getTenantId()
	{
		return Registry.getCurrentTenant().getTenantID();
	}

	protected String getCurrentBaseSite()
	{
		return getBaseSiteService().getCurrentBaseSite().getUid();
	}

	protected Pair<String, String> getCPSServiceParameter(final String serviceId)
	{
		final YaasServiceModel serviceModel = getYaasConfigurationService().getYaasServiceForId(serviceId);

		final BaseSiteServiceMappingModel siteMapping = (BaseSiteServiceMappingModel) yaasConfigurationService
				.getBaseSiteServiceMappingForId(getCurrentBaseSite(), serviceModel);
		final YaasClientCredentialModel credentialModel = siteMapping.getYaasClientCredential();
		return Pair.of(serviceModel.getServiceURL(), credentialModel.getYaasProject().getIdentifier());
	}

	protected YaasConfigurationService getYaasConfigurationService()
	{
		return yaasConfigurationService;
	}

	/**
	 * @param yaasConfigurationService
	 *           the yaasConfigurationService to set
	 */
	@Required
	public void setYaasConfigurationService(final YaasConfigurationService yaasConfigurationService)
	{
		this.yaasConfigurationService = yaasConfigurationService;
	}

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	/**
	 * @param baseSiteService
	 *           the baseSiteService to set
	 */
	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}
}
