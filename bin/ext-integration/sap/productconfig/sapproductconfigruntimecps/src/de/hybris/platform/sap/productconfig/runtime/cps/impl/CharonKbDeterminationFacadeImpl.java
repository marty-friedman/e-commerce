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
package de.hybris.platform.sap.productconfig.runtime.cps.impl;

import de.hybris.platform.sap.productconfig.runtime.cps.CharonKbDeterminationFacade;
import de.hybris.platform.sap.productconfig.runtime.cps.RequestErrorHandler;
import de.hybris.platform.sap.productconfig.runtime.cps.cache.KnowledgeBaseHeadersCacheAccessService;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSCommerceExternalConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.common.CPSMasterDataKBHeaderInfo;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.common.CPSMasterDataKnowledgebaseKey;
import de.hybris.platform.sap.productconfig.runtime.cps.strategy.CommerceExternalConfigurationStrategy;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hybris.charon.exp.HttpException;


/**
 * Default implementation of {@link CharonKbDeterminationFacade}
 */
public class CharonKbDeterminationFacadeImpl implements CharonKbDeterminationFacade
{

	private ObjectMapper objectMapper;
	private RequestErrorHandler requestErrorHandler;
	private KnowledgeBaseHeadersCacheAccessService knowledgeBasesCacheAccessService;
	private CommerceExternalConfigurationStrategy commerceExternalConfigurationStrategy;

	@Override
	public boolean hasKbForDate(final String productcode, final Date kbDate)
	{
		return readKbIdForDate(productcode, kbDate) != null;
	}

	@Override
	public Integer getCurrentKbIdForProduct(final String productcode)
	{
		final Date kbDate = new Date();
		return readKbIdForDate(productcode, kbDate);
	}

	@Override
	public Integer readKbIdForDate(final String productcode, final Date kbDate)
	{
		try
		{
			final List<CPSMasterDataKBHeaderInfo> knowledgebases = getKnowledgeBasesCacheAccessService().getKnowledgeBases(
					productcode);
			return resultIdAvailable(productcode, kbDate, knowledgebases);
		}
		catch (final HttpException ex)
		{
			getRequestErrorHandler().processHasKbError(ex);
			return null;
		}
	}

	@Override
	public boolean hasKbForExtConfig(final String product, final String externalcfg)
	{
		final CPSExternalConfiguration externalConfigStructured = parseFromJSON(externalcfg);
		final List<CPSMasterDataKBHeaderInfo> knowledgebases = getKnowledgeBasesCacheAccessService().getKnowledgeBases(product);
		return resultAvailable(knowledgebases, externalConfigStructured);
	}

	protected Integer resultIdAvailable(final String productcode, final Date kbDate,
			final List<CPSMasterDataKBHeaderInfo> knowledgebases)
	{
		if (knowledgebases.isEmpty())
		{
			throw new IllegalStateException("No KB found for product and date: " + productcode + " / " + kbDate);
		}
		return knowledgebases.get(0).getId();
	}

	protected boolean resultAvailable(final List<CPSMasterDataKBHeaderInfo> knowledgebases,
			final CPSExternalConfiguration externalConfigStructured)
	{
		return knowledgebases.stream().anyMatch(kb -> isMatch(kb, externalConfigStructured));
	}

	protected boolean isMatch(final CPSMasterDataKBHeaderInfo kb, final CPSExternalConfiguration externalConfigStructured)
	{
		final CPSMasterDataKnowledgebaseKey cpsKbKey = kb.getKey();
		final CPSMasterDataKnowledgebaseKey kbKey = externalConfigStructured.getKbKey();
		if (cpsKbKey == null || kbKey == null)
		{
			return false;
		}
		return kbKey.getLogsys().equals(cpsKbKey.getLogsys()) && kbKey.getName().equals(cpsKbKey.getName())
				&& kbKey.getVersion().equals(cpsKbKey.getVersion());
	}

	protected String convertToString(final Date kbDate)
	{
		return Instant.ofEpochMilli(kbDate.getTime()).toString().substring(0, 10);
	}

	protected CPSExternalConfiguration parseFromJSON(final String externalcfg)
	{
		CPSCommerceExternalConfiguration externalConfigStructured;
		try
		{
			externalConfigStructured = getObjectMapper().readValue(externalcfg, CPSCommerceExternalConfiguration.class);
		}
		catch (final IOException e)
		{
			throw new IllegalStateException("Parsing from JSON failed", e);
		}
		return getCommerceExternalConfigurationStrategy().extractCPSFormatFromCommerceRepresentation(externalConfigStructured);
	}

	/**
	 * @return the commerceExternalConfigurationStrategy
	 */
	protected CommerceExternalConfigurationStrategy getCommerceExternalConfigurationStrategy()
	{
		return commerceExternalConfigurationStrategy;
	}

	/**
	 * @return the objectMapper
	 */
	protected ObjectMapper getObjectMapper()
	{
		if (objectMapper == null)
		{
			objectMapper = new ObjectMapper();
		}
		return objectMapper;
	}

	protected RequestErrorHandler getRequestErrorHandler()
	{
		return requestErrorHandler;
	}

	/**
	 * Set the error handler for REST service calls
	 *
	 * @param requestErrorHandler
	 *           For wrapping the http errors we receive from the REST service call
	 */
	public void setRequestErrorHandler(final RequestErrorHandler requestErrorHandler)
	{
		this.requestErrorHandler = requestErrorHandler;
	}

	protected KnowledgeBaseHeadersCacheAccessService getKnowledgeBasesCacheAccessService()
	{
		return knowledgeBasesCacheAccessService;
	}

	/**
	 * Set the service to access the kb cache
	 *
	 * @param knowledgeBasesCacheAccessService
	 *           the knowledgeBasesCacheAccessService to set
	 */
	@Required
	public void setKnowledgeBasesCacheAccessService(final KnowledgeBaseHeadersCacheAccessService knowledgeBasesCacheAccessService)
	{
		this.knowledgeBasesCacheAccessService = knowledgeBasesCacheAccessService;
	}

	/**
	 * Set the configuration strategy for external configurations
	 *
	 * @param commerceExternalConfigurationStrategy
	 *           The commerceExternalConfigurationStrategy to set
	 */
	public void setCommerceExternalConfigurationStrategy(
			final CommerceExternalConfigurationStrategy commerceExternalConfigurationStrategy)
	{
		this.commerceExternalConfigurationStrategy = commerceExternalConfigurationStrategy;

	}
}
