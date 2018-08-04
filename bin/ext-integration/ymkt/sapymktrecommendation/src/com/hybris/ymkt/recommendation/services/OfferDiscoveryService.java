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
package com.hybris.ymkt.recommendation.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.apache.commons.lang.StringUtils;
import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.ep.EntityProviderException;
import org.apache.olingo.odata2.api.ep.EntityProviderReadProperties;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.ymkt.common.http.HttpURLConnectionRequest;
import com.hybris.ymkt.common.http.HttpURLConnectionResponse;
import com.hybris.ymkt.common.odata.ODataService;
import com.hybris.ymkt.common.user.UserContextService;
import com.hybris.ymkt.recommendation.constants.SapymktrecommendationConstants;
import com.hybris.ymkt.recommendation.dao.OfferRecommendation;
import com.hybris.ymkt.recommendation.dao.OfferRecommendationContext;
import com.hybris.ymkt.recommendation.dao.OfferRecommendationScenario;
import com.hybris.ymkt.recommendation.dao.OfferRecommendationScenario.BasketObject;
import com.hybris.ymkt.recommendation.dao.OfferRecommendationScenario.ContextParam;
import com.hybris.ymkt.recommendation.dao.OfferRecommendationScenario.LeadingObject;
import com.hybris.ymkt.recommendation.dao.OfferRecommendationScenario.Result;
import com.hybris.ymkt.recommendation.dao.SAPOfferContentPositionType;
import com.hybris.ymkt.recommendation.dao.SAPRecommendationItemDataSourceType;
import com.hybris.ymkt.recommendation.dao.SAPRecommendationType;
import com.hybris.ymkt.recommendationbuffer.service.RecommendationBufferService;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;


/**
 * This service provides offer recommendations and helper values from the CUAN_OFFER_DISCOVERY_SRV oData service
 */
public class OfferDiscoveryService
{
	protected static final String ACCEPT = "Accept";
	protected static final String APPLICATION_JSON = "application/json";

	private static final Logger LOG = LoggerFactory.getLogger(OfferDiscoveryService.class);

	protected static final EntityProviderReadProperties NO_READ_PROPERTIES = EntityProviderReadProperties.init().build();
	protected static final String RECOMMENDATIONS = "Recommendations";

	protected CartService cartService;
	protected CommonI18NService commonI18NService;
	protected ODataService oDataService;
	protected RecentViewedItemsService recentViewedItemsService;
	protected RecommendationBufferService recommendationBufferService;
	protected UserContextService userContextService;
	
	protected Map<String, Object> convertMapBasketObject(final BasketObject bo)
	{
		final Map<String, Object> leadingObject = new HashMap<>();
		leadingObject.put("BasketObjectType", bo.getBasketObjectType());
		leadingObject.put("BasketObjectId", bo.getBasketObjectId());
		return leadingObject;
	}

	protected Map<String, Object> convertMapContextParams(final ContextParam cp)
	{
		final Map<String, Object> contextParam = new HashMap<>();
		contextParam.put("ContextId", cp.getContextId());
		contextParam.put("Name", cp.getName());
		contextParam.put("Value", cp.getValue());
		return contextParam;
	}

	protected Map<String, Object> convertMapLeadingObject(final LeadingObject lo)
	{
		final Map<String, Object> leadingObject = new HashMap<>();
		leadingObject.put("LeadingObjectType", lo.getLeadingObjectType());
		leadingObject.put("LeadingObjectId", lo.getLeadingObjectId());
		return leadingObject;
	}

	/**
	 * Represents one offer recommendation built from odata Result
	 *
	 * @param result
	 *           {@link Result}
	 * @return {@link OfferRecommendation}
	 */
	public OfferRecommendation createOfferRecommendation(final Result result)
	{
		final OfferRecommendation offerRecommendation = new OfferRecommendation();
		offerRecommendation.setOfferId(result.getOfferId());
		offerRecommendation.setTargetLink(result.getTargetLink());
		offerRecommendation.setTargetDescription(result.getTargetDescription());
		offerRecommendation.setContentId(result.getContentId());
		offerRecommendation.setContentSource(result.getContentSource());
		offerRecommendation.setContentDescription(result.getContentDescription());
		return offerRecommendation;
	}

	/**
	 * Build ContextParams, BasketObjects and LeadingObjects filters
	 *
	 * @param context
	 *           Request parameters and filters
	 * @return OfferRecommendationScenario
	 */
	protected OfferRecommendationScenario createOfferRecommendationScenario(final OfferRecommendationContext context)
	{
		final OfferRecommendationScenario offerRecommendationScenario = new OfferRecommendationScenario(
				this.userContextService.getUserId(), this.userContextService.getUserOrigin(), context.getRecommendationScenarioId());

		// Last seen products or categories
		context.getLeadingItemId().stream() //
				.map(leadingObjectId -> new LeadingObject(context.getLeadingItemDSType(), leadingObjectId))//
				.forEach(offerRecommendationScenario.getLeadingObjects()::add);

		// Basket items
		if (StringUtils.isNotBlank(context.getCartItemDSType()))
		{
			for (final String basketObjectId : this.getCartItemsFromSession())
			{
				offerRecommendationScenario.getBasketObjects().add(new BasketObject(context.getCartItemDSType(), basketObjectId));
			}
		}

		// Basket items placed in the leading items
		if (context.isIncludeCart())
		{
			for (final String leadingObjectId : this.getCartItemsFromSession())
			{
				offerRecommendationScenario.getLeadingObjects()
						.add(new LeadingObject(context.getLeadingItemDSType(), leadingObjectId));
			}
		}

		// Last x viewed products or categories
		if (context.isIncludeRecent())
		{
			List<String> itemIds = Collections.emptyList();
			if (SapymktrecommendationConstants.PRODUCT.equals(context.getLeadingItemType()))
			{
				itemIds = this.recentViewedItemsService.getRecentViewedProducts();
			}
			else if (SapymktrecommendationConstants.CATEGORY.equals(context.getLeadingItemType()))
			{
				itemIds = this.recentViewedItemsService.getRecentViewedCategories();
			}
			for (final String leadingObjectId : itemIds)
			{
				offerRecommendationScenario.getLeadingObjects()
						.add(new LeadingObject(context.getLeadingItemDSType(), leadingObjectId));
			}
		}

		// Build context parameters
		final String currentLanguage = commonI18NService.getCurrentLanguage().getIsocode().toUpperCase(Locale.ENGLISH);
		offerRecommendationScenario.getContextParams().add(new ContextParam(1, "P_COMM_MEDIUM", "ONLINE_SHOP"));
		offerRecommendationScenario.getContextParams().add(new ContextParam(2, "P_LANGUAGE", currentLanguage));

		if (StringUtils.isNotBlank(context.getContentPosition()))
		{
			offerRecommendationScenario.getContextParams().add(new ContextParam(3, "P_POSITION", context.getContentPosition()));
		}

		return offerRecommendationScenario;
	}

	protected SAPOfferContentPositionType createSAPOfferContentPositionType(final ODataEntry entry)
	{
		final SAPOfferContentPositionType contentPosition = new SAPOfferContentPositionType(
				(String) entry.getProperties().get("ContentPositionId"));
		contentPosition.setCommunicationMediumId(((String) entry.getProperties().get("CommunicationMediumId")));
		contentPosition.setCommunicationMediumName(((String) entry.getProperties().get("CommunicationMediumName")));
		return contentPosition;
	}

	protected SAPRecommendationItemDataSourceType createSAPRecommendationItemDataSourceType(final ODataEntry entry)
	{
		final SAPRecommendationItemDataSourceType itemDSType = new SAPRecommendationItemDataSourceType();
		itemDSType.setId((String) entry.getProperties().get("ItemSourceObjectType"));
		itemDSType.setDescription((String) entry.getProperties().get("ItemSourceTypeDescription"));
		return itemDSType;
	}

	protected SAPRecommendationType createSAPRecommendationType(final ODataEntry entry)
	{
		final SAPRecommendationType scenario = new SAPRecommendationType(
				(String) entry.getProperties().get("OfferRecommendationScenarioId"));
		scenario.setDescription((String) entry.getProperties().get("OfferRecommendationScenarioName"));
		return scenario;
	}

	protected void executeOfferRecommendation(final OfferRecommendationScenario offerRecommendationScenario) throws IOException
	{
		try
		{
			final HttpURLConnectionRequest request = new HttpURLConnectionRequest("POST",
					this.oDataService.createURL(RECOMMENDATIONS));
			request.getRequestProperties().put(ACCEPT, APPLICATION_JSON);
			request.getRequestProperties().put("Content-Type", APPLICATION_JSON);

			//Build object for JSON payload
			final Map<String, Object> offerRecoPayloadMap = new LinkedHashMap<>();

			offerRecoPayloadMap.put("UserId", offerRecommendationScenario.getUserId());
			offerRecoPayloadMap.put("UserOriginId", offerRecommendationScenario.getUserOriginId());
			offerRecoPayloadMap.put("RecommendationScenarioId", offerRecommendationScenario.getRecommendationScenarioId());

			offerRecoPayloadMap.put("LeadingObjects",
					offerRecommendationScenario.getLeadingObjects().stream() //
							.map(this::convertMapLeadingObject) //
							.collect(Collectors.toList()));

			offerRecoPayloadMap.put("BasketObjects",
					offerRecommendationScenario.getBasketObjects().stream() //
							.map(this::convertMapBasketObject) //
							.collect(Collectors.toList()));

			offerRecoPayloadMap.put("ContextParams",
					offerRecommendationScenario.getContextParams().stream() //
							.map(this::convertMapContextParams) //
							.collect(Collectors.toList()));

			offerRecoPayloadMap.put("Results", Collections.emptyList());

			final byte[] payload = this.oDataService.convertMapToJSONPayload(RECOMMENDATIONS, offerRecoPayloadMap);
			request.setPayload(payload);

			final HttpURLConnectionResponse response = this.oDataService.executeWithRetry(request);

			final EdmEntitySet entitySet = this.oDataService.getEdm().getDefaultEntityContainer().getEntitySet(RECOMMENDATIONS);
			try (InputStream inputStream = new ByteArrayInputStream(response.getPayload()))
			{
				final ODataEntry oData = EntityProvider.readEntry(APPLICATION_JSON, entitySet, inputStream, NO_READ_PROPERTIES);
				offerRecommendationScenario.update(oData.getProperties());
			}
		}
		catch (final IOException | EntityProviderException | EdmException e)
		{
			throw new IOException("Error reading offer recommendation scenario " + offerRecommendationScenario, e);

		}
	}

	protected List<String> getCartItemsFromSession()
	{
		return this.cartService.getSessionCart().getEntries().stream() //
				.map(AbstractOrderEntryModel::getProduct) //
				.map(ProductModel::getCode) //
				.collect(Collectors.toList());
	}

	public List<SAPOfferContentPositionType> getContentPositionValues() throws IOException
	{
		try
		{
			final URL url = this.oDataService.createURL("ContentPositions", //
					"$filter", "CommunicationMediumId eq 'ONLINE_SHOP'");

			final HttpURLConnectionRequest request = new HttpURLConnectionRequest("GET", url);

			request.getRequestProperties().put(ACCEPT, APPLICATION_JSON);

			final HttpURLConnectionResponse response = this.oDataService.executeWithRetry(request);
			final EdmEntitySet entitySet = this.oDataService.getEdm().getDefaultEntityContainer().getEntitySet("ContentPositions");
			final InputStream content = new ByteArrayInputStream(response.getPayload());
			final ODataFeed feed = EntityProvider.readFeed(APPLICATION_JSON, entitySet, content, NO_READ_PROPERTIES);
			final List<ODataEntry> entries = feed.getEntries();

			return (entries == null) ? Collections.emptyList() : //
					entries.stream() //
							.map(this::createSAPOfferContentPositionType) //
							.collect(Collectors.toList());
		}
		catch (ODataException e)
		{
			throw new IOException("Error using/parsing entitySet OfferRecommendationScenarios.", e);
		}
	}

	/**
	 * @return {@link List} of {@link SAPRecommendationItemDataSourceType} from the yMKT system.<br>
	 *         The list is sorted by {@link SAPRecommendationItemDataSourceType#getId()}.
	 * @throws IOException
	 */
	@Nonnull
	public List<SAPRecommendationItemDataSourceType> getItemDataSourceTypes() throws IOException
	{
		try
		{
			final URL url = this.oDataService.createURL("ItemSourceTypes", //
					"$select", "ItemSourceObjectType,ItemSourceTypeDescription");
			final HttpURLConnectionRequest request = new HttpURLConnectionRequest("GET", url);

			request.getRequestProperties().put(ACCEPT, APPLICATION_JSON);

			final HttpURLConnectionResponse response = this.oDataService.executeWithRetry(request);

			final EdmEntitySet entitySet = this.oDataService.getEdm().getDefaultEntityContainer().getEntitySet("ItemSourceTypes");
			final InputStream content = new ByteArrayInputStream(response.getPayload());
			final ODataFeed feed = EntityProvider.readFeed(APPLICATION_JSON, entitySet, content, NO_READ_PROPERTIES);
			final List<ODataEntry> entries = feed.getEntries();

			final List<SAPRecommendationItemDataSourceType> dsTypes = entries == null ? Collections.emptyList() : //
					entries.stream() //
							.map(this::createSAPRecommendationItemDataSourceType) //
							.sorted(Comparator.comparing(SAPRecommendationItemDataSourceType::getId)) //
							.collect(Collectors.toList());

			if (dsTypes.isEmpty())
			{
				LOG.warn("Zero DataSource Types found in yMKT via OData.");
			}

			return dsTypes;
		}
		catch (ODataException e)
		{
			throw new IOException("Error using/parsing entitySet ItemSourceTypes.", e);
		}
	}

	/**
	 * Main method to trigger offer recommendation retrieval
	 *
	 * @param context
	 *           Request parameters and filters
	 * @return {@link List} of OfferRecommendation
	 */
	public List<OfferRecommendation> getOfferRecommendations(final OfferRecommendationContext context)
	{
		try
		{
			//Collect data needed to make offer request
			final OfferRecommendationScenario offerRecommendationScenario = this.createOfferRecommendationScenario(context);

			//trigger backend request
			this.executeOfferRecommendation(offerRecommendationScenario);

			//convert odata results to list for UI display
			return offerRecommendationScenario.getResults().stream() //
					.map(this::createOfferRecommendation) //
					.collect(Collectors.toList());
		}
		catch (final IOException e)
		{
			LOG.error("Error reading offer recommendations from backend using scenarioId {} and context {}",
					context.getRecommendationScenarioId(), context, e);
			return Collections.emptyList();
		}
	}

	/**
	 * @return {@link List} of {@link SAPRecommendationType} from the yMKT system.<br>
	 *         The list is sorted by {@link SAPRecommendationType#getId()}.
	 * @throws IOException
	 */
	@Nonnull
	public List<SAPRecommendationType> getOfferRecommendationScenarios() throws IOException
	{
		try
		{
			final URL url = this.oDataService.createURL("OfferRecommendationScenarios", //
					"$orderby", "OfferRecommendationScenarioId", //
					"$select", "OfferRecommendationScenarioId,OfferRecommendationScenarioName");

			final HttpURLConnectionRequest request = new HttpURLConnectionRequest("GET", url);

			request.getRequestProperties().put(ACCEPT, APPLICATION_JSON);

			final HttpURLConnectionResponse response = this.oDataService.executeWithRetry(request);

			final EdmEntitySet entitySet = this.oDataService.getEdm().getDefaultEntityContainer()
					.getEntitySet("OfferRecommendationScenarios");
			final InputStream content = new ByteArrayInputStream(response.getPayload());
			final ODataFeed feed = EntityProvider.readFeed(APPLICATION_JSON, entitySet, content, NO_READ_PROPERTIES);
			final List<ODataEntry> entries = feed.getEntries();

			final List<SAPRecommendationType> scenarios = entries == null ? Collections.emptyList() : //
					entries.stream() //
							.map(this::createSAPRecommendationType) //
							.collect(Collectors.toList());

			if (scenarios.isEmpty())
			{
				LOG.warn("yMKT has returned no offer scenarios!");
			}

			return scenarios;
		}
		catch (ODataException e)
		{
			throw new IOException("Error using/parsing entitySet OfferRecommendationScenarios.", e);
		}
	}
	
	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	@Required
	public void setCommonI18NService(CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	@Required
	public void setODataService(final ODataService oDataService)
	{
		this.oDataService = oDataService;
	}

	@Required
	public void setRecentViewedItemsService(final RecentViewedItemsService recentViewedItemsService)
	{
		this.recentViewedItemsService = recentViewedItemsService;
	}

	@Required
	public void setRecommendationBufferService(final RecommendationBufferService recommendationBufferService)
	{
		this.recommendationBufferService = recommendationBufferService;
	}

	@Required
	public void setUserContextService(UserContextService userContextService)
	{
		this.userContextService = userContextService;
	}
	
}
