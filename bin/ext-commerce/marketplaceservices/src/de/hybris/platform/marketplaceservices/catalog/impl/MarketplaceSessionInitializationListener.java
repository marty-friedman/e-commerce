/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.marketplaceservices.catalog.impl;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.marketplaceservices.vendor.VendorService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchListener;
import de.hybris.platform.solrfacetsearch.indexer.IndexerContext;
import de.hybris.platform.solrfacetsearch.indexer.IndexerListener;
import de.hybris.platform.solrfacetsearch.indexer.IndexerQueryContext;
import de.hybris.platform.solrfacetsearch.indexer.IndexerQueryListener;
import de.hybris.platform.solrfacetsearch.indexer.exceptions.IndexerException;

import java.util.Collection;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Required;


/**
 *
 */
public class MarketplaceSessionInitializationListener implements IndexerQueryListener, IndexerListener, IndexerBatchListener
{
	private VendorService vendorService;

	@Override
	public void afterBatch(IndexerBatchContext context) throws IndexerException
	{
		// NOOP
	}

	@Override
	public void afterBatchError(IndexerBatchContext context) throws IndexerException
	{
		// NOOP
	}

	@Override
	public void beforeBatch(IndexerBatchContext context) throws IndexerException
	{
		initializeSession(context.getFacetSearchConfig(), context.getIndexedType());
	}

	@Override
	public void afterIndex(IndexerContext context) throws IndexerException
	{
		// NOOP
	}

	@Override
	public void afterIndexError(IndexerContext context) throws IndexerException
	{
		// NOOP
	}

	@Override
	public void beforeIndex(IndexerContext context) throws IndexerException
	{
		initializeSession(context.getFacetSearchConfig(), context.getIndexedType());
	}

	@Override
	public void afterQuery(IndexerQueryContext context) throws IndexerException
	{
		// NOOP
	}

	@Override
	public void afterQueryError(IndexerQueryContext context) throws IndexerException
	{
		// NOOP
	}

	@Override
	public void beforeQuery(IndexerQueryContext context) throws IndexerException
	{
		initializeSession(context.getFacetSearchConfig(), context.getIndexedType());
	}

	protected void initializeSession(final FacetSearchConfig facetSearchConfig, final IndexedType indexedType)
	{
		final IndexConfig indexConfig = facetSearchConfig.getIndexConfig();
		Collection<CatalogVersionModel> catalogVersions = new HashSet<>();
		catalogVersions.addAll(indexConfig.getCatalogVersions());
		catalogVersions.addAll(getVendorService().getActiveProductCatalogVersions());
		indexConfig.setCatalogVersions(catalogVersions);
		facetSearchConfig.setIndexConfig(indexConfig);
	}

	protected VendorService getVendorService()
	{
		return vendorService;
	}

	@Required
	public void setVendorService(VendorService vendorService)
	{
		this.vendorService = vendorService;
	}
}
