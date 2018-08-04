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

import de.hybris.platform.commerceservices.search.ProductSearchService;
import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryTermData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.strategies.exceptions.NoValidSolrConfigException;
import de.hybris.platform.commerceservices.threadcontext.ThreadContextService;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ClassificationSystemCPQAttributesContainer;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.data.VariantSearchResult;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationVariantSearchService;
import de.hybris.platform.sap.productconfig.services.intf.SearchAttributeSelectionStrategy;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * Default implementaion of the {@link ProductConfigurationVariantSearchService}.
 */
public class ProductConfigurationVariantSearchServiceImpl implements ProductConfigurationVariantSearchService
{

	private static final String MESSAGE_CONFIG_ID_MUST_NOT_BE_NULL = "ConfigId must not be null";
	private static final String PRODUCT_CODE_ON_SOLR = "code";
	protected static final String BASE_PRODUCT_ON_SOLR = "baseProduct";
	private static final Logger LOG = Logger.getLogger(ProductConfigurationVariantSearchServiceImpl.class);
	private ProductConfigurationService productConfigurationService;
	private ProductSearchService<SolrSearchQueryData, SearchResultValueData, ProductSearchPageData<SolrSearchQueryData, SearchResultValueData>> productSearchService;
	private SearchAttributeSelectionStrategy searchAttributeSelectionStrategy;
	private ThreadContextService threadContextService;
	private SessionAccessService sessionAccessService;


	@Override
	public List<VariantSearchResult> getVariantsForConfiguration(final String configId, final String productCode)
	{
		final SolrSearchQueryData searchQueryData = createSearchQueryData(configId);
		addBaseProductToQuery(productCode, searchQueryData);
		if (LOG.isDebugEnabled())
		{
			traceQuery(searchQueryData);
		}
		return getVariantsForCustomQuery(searchQueryData);
	}

	protected void traceQuery(final SolrSearchQueryData searchQueryData)
	{
		final StringBuilder debugOutput = new StringBuilder("Query terms for variant search: \n");
		searchQueryData.getFilterTerms()//
				.stream()//
				.forEach(term -> appendTerm(term, debugOutput));
		LOG.debug(debugOutput.toString());
	}

	protected void appendTerm(final SolrSearchQueryTermData term, final StringBuilder debugOutput)
	{
		debugOutput.append("Key: ").append(term.getKey()).append(", value: ").append(term.getValue()).append("\n");
	}

	@Override
	public List<VariantSearchResult> getVariantsForCustomQuery(final SolrSearchQueryData searchQueryData)
	{
		final PageableData pageableData = new PageableData();


		final ProductSearchPageData<SolrSearchQueryData, SearchResultValueData> solrSearchResult = getThreadContextService()
				.executeInContext(//
						() -> getProductSearchService().searchAgain(searchQueryData, pageableData));


		return compileSearchResult(solrSearchResult);
	}


	protected ThreadContextService getThreadContextService()
	{
		return this.threadContextService;
	}

	protected ProductConfigurationService getProductConfigurationService()
	{
		return productConfigurationService;
	}

	/**
	 * @param productConfigurationService
	 */
	public void setProductConfigurationService(final ProductConfigurationService productConfigurationService)
	{
		this.productConfigurationService = productConfigurationService;
	}

	/**
	 * @param productSearchService
	 *           ProductSearchService
	 */
	public void setProductSearchService(
			final ProductSearchService<SolrSearchQueryData, SearchResultValueData, ProductSearchPageData<SolrSearchQueryData, SearchResultValueData>> productSearchService)
	{
		this.productSearchService = productSearchService;

	}

	protected ProductSearchService<SolrSearchQueryData, SearchResultValueData, ProductSearchPageData<SolrSearchQueryData, SearchResultValueData>> getProductSearchService()
	{
		return this.productSearchService;
	}

	protected SolrSearchQueryData createSearchQueryData(final String configId)
	{
		final SolrSearchQueryData searchQueryData = new SolrSearchQueryData();
		searchQueryData.setFilterTerms(getFilterTerms(configId));
		return searchQueryData;
	}

	/**
	 * Fetches the characteristics on root instance level
	 *
	 * @param configId
	 *           Must not be null
	 * @return The characteristics on root level
	 */
	protected List<CsticModel> getRootCharacteristics(final String configId)
	{
		validateParameterNotNull(configId, MESSAGE_CONFIG_ID_MUST_NOT_BE_NULL);
		final ConfigModel configurationModel = productConfigurationService.retrieveConfigurationModel(configId);
		final InstanceModel rootInstance = configurationModel.getRootInstance();
		checkNotNull(rootInstance, "No root instance found");
		return rootInstance.getCstics();
	}

	/**
	 * Check on cstic value level whether we use it for search. Default implementation: Use for search if not empty
	 *
	 * @param csticValueModel
	 * @return Use value for search?
	 */
	protected boolean isUsedForSearch(final CsticValueModel csticValueModel)
	{
		return !csticValueModel.getName().isEmpty();
	}

	/**
	 * Check on cstic level whether we consider the cstic for searching. We consult
	 * {@link SearchAttributeSelectionStrategy} to do this.
	 *
	 * @param csticModel
	 *           the cstic model, to check
	 * @return Use cstic for search?
	 */
	protected boolean isUsedForSearch(final CsticModel csticModel)
	{
		try
		{
			return searchAttributeSelectionStrategy.isAttributeAvailableOnSearchIndex(csticModel.getName());
		}
		catch (final NoValidSolrConfigException e)
		{
			throw new IllegalStateException("No SOLR configuration found", e);
		}
	}


	protected SolrSearchQueryTermData createQueryDataTerm(final CsticModel csticModel, final CsticValueModel value,
			final ClassificationSystemCPQAttributesContainer cpqAttributes)
	{
		validateParameterNotNull(csticModel, "CsticModel must not be null");
		validateParameterNotNull(value, "CsticValueModel must not be null");

		final SolrSearchQueryTermData queryTermData = new SolrSearchQueryTermData();
		queryTermData.setKey(csticModel.getName());
		queryTermData.setValue(getValueName(value, csticModel, cpqAttributes));
		return queryTermData;
	}

	protected String getValueName(final CsticValueModel valueModel, final CsticModel csticModel,
			final ClassificationSystemCPQAttributesContainer cpqAttributes)
	{
		String hybrisValueName = null;
		if (cpqAttributes != null && MapUtils.isNotEmpty(cpqAttributes.getValueNames()))
		{
			hybrisValueName = cpqAttributes.getValueNames().get(csticModel.getName() + "_" + valueModel.getName());
		}
		final String langDepName = valueModel.getLanguageDependentName();

		String displayName = null;
		if (!StringUtils.isEmpty(hybrisValueName))
		{
			displayName = hybrisValueName;
		}
		else if (!StringUtils.isEmpty(langDepName))
		{
			displayName = langDepName;
		}
		return displayName;
	}


	protected List<SolrSearchQueryTermData> convertToQueryDataList(final CsticModel csticModel,
			final Map<String, ClassificationSystemCPQAttributesContainer> hybrisNamesMap)
	{
		validateParameterNotNull(csticModel, "Cstic model must not be null");
		final ClassificationSystemCPQAttributesContainer cpqAttributes = hybrisNamesMap.get(csticModel.getName());

		return csticModel.getAssignedValues() //
				.stream() //
				.filter(value -> isUsedForSearch(csticModel)) //
				.filter(this::isUsedForSearch) //
				.map(value -> createQueryDataTerm(csticModel, value, cpqAttributes)) //
				.collect(Collectors.toList());
	}


	protected List<SolrSearchQueryTermData> getFilterTerms(final String configId)
	{
		validateParameterNotNull(configId, MESSAGE_CONFIG_ID_MUST_NOT_BE_NULL);
		final List<CsticModel> rootCharacteristics = getRootCharacteristics(configId);
		final Map<String, ClassificationSystemCPQAttributesContainer> hybrisNamesMap = getSessionAccessService().getCachedNameMap();

		return rootCharacteristics.stream() //
				.map(model -> convertToQueryDataList(model, hybrisNamesMap)) //
				.flatMap(element -> element.stream()) //
				.collect(Collectors.toList());
	}



	protected List<VariantSearchResult> compileSearchResult(
			final ProductSearchPageData<SolrSearchQueryData, SearchResultValueData> solrSearchResult)
	{
		checkNotNull(solrSearchResult, "ProductSearchPageData must not be null");

		return solrSearchResult.getResults()//
				.stream() //
				.map(this::convertToVariantSearchResult) //
				.collect(Collectors.toList());
	}


	/**
	 * @param value
	 * @return Variant Search Result
	 */
	protected VariantSearchResult convertToVariantSearchResult(final SearchResultValueData value)
	{
		final VariantSearchResult variantSearchResult = new VariantSearchResult();
		checkNotNull(value, "Search result must not be null");
		final Object productCodeFromSolr = value.getValues().get(PRODUCT_CODE_ON_SOLR);
		checkNotNull(productCodeFromSolr, "Result must contain 'code'");
		variantSearchResult.setProductCode((String) productCodeFromSolr);
		return variantSearchResult;
	}

	/**
	 * @param searchAttributeSelectionStrategy
	 */
	public void setSearchAttributeSelectionStrategy(final SearchAttributeSelectionStrategy searchAttributeSelectionStrategy)
	{
		this.searchAttributeSelectionStrategy = searchAttributeSelectionStrategy;

	}


	protected SearchAttributeSelectionStrategy getSearchAttributeSelectionStrategy()
	{
		return this.searchAttributeSelectionStrategy;
	}

	/**
	 * @param threadContextService
	 */
	public void setThreadContextService(final ThreadContextService threadContextService)
	{
		this.threadContextService = threadContextService;
	}

	protected void addBaseProductToQuery(final String productcode, final SolrSearchQueryData searchQuery)
	{
		final SolrSearchQueryTermData searchTerm = new SolrSearchQueryTermData();
		searchTerm.setKey(BASE_PRODUCT_ON_SOLR);
		searchTerm.setValue(productcode);
		searchQuery.getFilterTerms().add(searchTerm);
	}


	protected SessionAccessService getSessionAccessService()
	{
		return sessionAccessService;
	}

	/**
	 * @param sessionAccessService
	 *           for accessing session data
	 */
	public void setSessionAccessService(final SessionAccessService sessionAccessService)
	{
		this.sessionAccessService = sessionAccessService;
	}
}
