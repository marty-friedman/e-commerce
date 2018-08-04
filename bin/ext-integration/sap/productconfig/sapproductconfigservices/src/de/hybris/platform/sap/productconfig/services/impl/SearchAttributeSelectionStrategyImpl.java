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
package de.hybris.platform.sap.productconfig.services.impl;

import static com.google.common.base.Preconditions.checkNotNull;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.commerceservices.search.solrfacetsearch.strategies.SolrFacetSearchConfigSelectionStrategy;
import de.hybris.platform.commerceservices.search.solrfacetsearch.strategies.exceptions.NoValidSolrConfigException;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.intf.SearchAttributeSelectionStrategy;
import de.hybris.platform.solrfacetsearch.model.config.SolrFacetSearchConfigModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedTypeModel;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;


/**
 * Default implementation of {@link SearchAttributeSelectionStrategy}
 */
public class SearchAttributeSelectionStrategyImpl implements SearchAttributeSelectionStrategy
{

	private SolrFacetSearchConfigSelectionStrategy solrFacetSearchConfigSelectionStrategy;
	private SessionAccessService sessionAccessService;

	/**
	 * @param solrFacetSearchConfigSelectionStrategy
	 */
	public void setSolrFacetSearchConfigSelectionStrategy(
			final SolrFacetSearchConfigSelectionStrategy solrFacetSearchConfigSelectionStrategy)
	{
		this.solrFacetSearchConfigSelectionStrategy = solrFacetSearchConfigSelectionStrategy;

	}

	/**
	 * @return SOLR configuration selection strategy
	 */
	public SolrFacetSearchConfigSelectionStrategy getSolrFacetSearchConfigSelectionStrategy()
	{
		return this.solrFacetSearchConfigSelectionStrategy;
	}

	@Override
	public boolean isAttributeAvailableOnSearchIndex(final String propertyName) throws NoValidSolrConfigException
	{
		validateParameterNotNull(propertyName, "Property name must not be null");
		Set<String> indexedProperties = sessionAccessService.getSolrIndexedProperties();
		if (CollectionUtils.isEmpty(indexedProperties))
		{
			indexedProperties = compileIndexedProperties();
			sessionAccessService.setSolrIndexedProperties(indexedProperties);
		}
		return indexedProperties.contains(propertyName);
	}

	/**
	 * @return Set of indexed properties from SOLR
	 * @throws NoValidSolrConfigException
	 */
	protected Set<String> compileIndexedProperties() throws NoValidSolrConfigException
	{
		final SolrFacetSearchConfigModel solrFacetSearchConfig = solrFacetSearchConfigSelectionStrategy
				.getCurrentSolrFacetSearchConfig();
		checkNotNull(solrFacetSearchConfig, "No search configuration found");
		final List<SolrIndexedTypeModel> solrIndexedTypes = solrFacetSearchConfig.getSolrIndexedTypes();
		checkNotNull(solrIndexedTypes, "No indexed types");
		return solrIndexedTypes//
				.stream()//
				.filter(solrIndexedType -> solrIndexedType.getSolrIndexedProperties() != null)//
				.flatMap(solrIndexedType -> solrIndexedType.getSolrIndexedProperties().stream())//
				.map(indexedProperty -> indexedProperty.getName())//
				.collect(Collectors.toSet());
	}

	/**
	 * @param sessionAccessService
	 */
	public void setSessionAccessService(final SessionAccessService sessionAccessService)
	{
		this.sessionAccessService = sessionAccessService;
	}

	/**
	 * @return Session access service
	 */
	public SessionAccessService getSessionAccessService()
	{
		return sessionAccessService;
	}

}
