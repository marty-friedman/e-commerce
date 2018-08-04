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

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.Tenant;
import de.hybris.platform.core.TenantAwareThreadFactory;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.ep.EntityProviderException;
import org.apache.olingo.odata2.api.ep.EntityProviderReadProperties;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.ymkt.common.http.HttpURLConnectionRequest;
import com.hybris.ymkt.common.http.HttpURLConnectionResponse;
import com.hybris.ymkt.common.odata.ODataService;
import com.hybris.ymkt.common.user.UserContextService;
import com.hybris.ymkt.recommendation.constants.SapymktrecommendationConstants;
import com.hybris.ymkt.recommendation.dao.ProductRecommendationData;
import com.hybris.ymkt.recommendation.dao.RecommendationContext;
import com.hybris.ymkt.recommendation.dao.RecommendationScenario;
import com.hybris.ymkt.recommendation.dao.RecommendationScenario.BasketObject;
import com.hybris.ymkt.recommendation.dao.RecommendationScenario.LeadingObject;
import com.hybris.ymkt.recommendation.dao.RecommendationScenario.Scenario;
import com.hybris.ymkt.recommendation.dao.RecommendationScenario.ScenarioHash;
import com.hybris.ymkt.recommendation.utils.RecommendationScenarioUtils;
import com.hybris.ymkt.recommendationbuffer.model.SAPRecommendationBufferModel;
import com.hybris.ymkt.recommendationbuffer.service.RecommendationBufferService;


/**
 * This service perform the 'get recommendation' actions, such as reading all scenario hashes of a user or read the
 * recommendation for a given scenario and context.
 */
public class RecommendationService
{
	protected static final String APPLICATION_JSON = "application/json";

	private static final Logger LOG = LoggerFactory.getLogger(RecommendationService.class);

	protected static final EntityProviderReadProperties NO_READ_PROPERTIES = EntityProviderReadProperties.init().build();
	protected static final String RECOMMENDATION_SCENARIOS = "RecommendationScenarios";
	protected static final String GENERIC_RECOMMENDATION = "G";
	protected static final String PERSONALIZED_RECOMMENDATION = "P";
	protected static final String RESTRICTED_RECOMMENDATION = "R";

	protected CartService cartService;
	protected ODataService oDataService;
	protected RecentViewedItemsService recentViewedItemsService;
	protected RecommendationBufferService recommendationBufferService;
	protected UserContextService userContextService;
	protected int requestTimeoutThreshold;

	protected RecommendationScenario createRecommendationScenario(final RecommendationContext context)
	{
		final RecommendationScenario recoScenario = new RecommendationScenario(this.userContextService.getUserId(),
				this.userContextService.getUserOrigin());

		// Add User's cookie scenario hash matching the scenario Id
		final Scenario scenario = new Scenario(context.getScenarioId());

		// Last seen product or categories
		for (final String leadingObjectId : context.getLeadingItemId())
		{
			scenario.getLeadingObjects().add(new LeadingObject(context.getLeadingItemDSType(), leadingObjectId));
		}

		// Basket items
		if (StringUtils.isNotBlank(context.getCartItemDSType()))
		{
			for (final String basketObjectId : this.getCartItemsFromSession())
			{
				scenario.getBasketObjects().add(new BasketObject(context.getCartItemDSType(), basketObjectId));
			}
		}

		// Basket items placed in the leading items
		if (context.isIncludeCart())
		{
			for (final String leadingObjectId : this.getCartItemsFromSession())
			{
				scenario.getLeadingObjects().add(new LeadingObject(context.getLeadingItemDSType(), leadingObjectId));
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
				scenario.getLeadingObjects().add(new LeadingObject(context.getLeadingItemDSType(), leadingObjectId));
			}
		}

		recoScenario.getScenarios().add(scenario);

		return recoScenario;
	}

	protected void executeRecommendationScenario(final RecommendationScenario recommendationScenario,
			final boolean scenarioHashesOnly) throws IOException
	{
		try
		{
			final HttpURLConnectionRequest request = new HttpURLConnectionRequest("POST",
					this.oDataService.createURL(RECOMMENDATION_SCENARIOS));
			request.getRequestProperties().put("Accept", APPLICATION_JSON);
			request.getRequestProperties().put("Content-Type", APPLICATION_JSON);

			final Map<String, Object> recommendationScenarioMap = //
					RecommendationScenarioUtils.convertRecommendationScenarioToMap(recommendationScenario, scenarioHashesOnly);

			final byte[] payload = this.oDataService.convertMapToJSONPayload(RECOMMENDATION_SCENARIOS, recommendationScenarioMap);
			request.setPayload(payload);
			request.setReadTimeout(requestTimeoutThreshold); // timeout after threshold value for better user experience

			final HttpURLConnectionResponse response = this.oDataService.executeWithRetry(request);

			final EdmEntitySet entitySet = this.oDataService.getEdm().getDefaultEntityContainer()
					.getEntitySet(RECOMMENDATION_SCENARIOS);
			try (InputStream inputStream = new ByteArrayInputStream(response.getPayload()))
			{
				final ODataEntry oData = EntityProvider.readEntry(APPLICATION_JSON, entitySet, inputStream, NO_READ_PROPERTIES);
				recommendationScenario.update(oData.getProperties());
			}
		}
		catch (final IOException | EntityProviderException | EdmException e)
		{
			throw new IOException("Error reading recommendation scenario " + recommendationScenario, e);
		}
	}

	protected List<String> getCartItemsFromSession()
	{
		return this.cartService.getSessionCart().getEntries().stream() //
				.map(AbstractOrderEntryModel::getProduct) //
				.map(ProductModel::getCode) //
				.collect(Collectors.toList());
	}

	private List<ProductRecommendationData> getFallbackRecommendation(final RecommendationScenario recoScenario)
	{
		final Scenario scenario = recoScenario.getScenarios().get(0);
		final String scenarioId = scenario.getScenarioId();
		final String userId = recoScenario.getUserId();
		final List<String> leadingItems = scenario.getLeadingObjects().stream().map(LeadingObject::getLeadingObjectId)
				.collect(Collectors.toList());

		SAPRecommendationBufferModel buffer = null;

		if (!userId.isEmpty())
		{
			buffer = this.getFallbackRecommendationByType(userId, scenarioId, leadingItems, PERSONALIZED_RECOMMENDATION);

			if (buffer == null)
			{
				buffer = this.getFallbackRecommendationByType(userId, scenarioId, leadingItems, RESTRICTED_RECOMMENDATION);
			}
		}

		if (buffer == null)
		{
			buffer = this.getFallbackRecommendationByType(userId, scenarioId, leadingItems, GENERIC_RECOMMENDATION);
		}

		return (buffer == null) ? Collections.emptyList() : RecommendationScenarioUtils.convertBufferToList(buffer);

	}

	protected SAPRecommendationBufferModel getFallbackRecommendationByType( //
			final String userId, //
			final String scenarioId, //
			final List<String> leadingItems, //
			final String recommendationType)
	{
		LOG.debug("Retrieving SAP recommendation from buffer for userId='{}', scenarioId='{}', leadingItems='{}'", //
				userId, scenarioId, leadingItems);

		final List<String> subLeadingItems = new ArrayList<>(leadingItems);

		// Read buffer with subset of leading items
		while (subLeadingItems.size() > 1)
		{
			final String leadingItemsSorted = subLeadingItems.stream().sorted().collect(Collectors.joining(","));
			final SAPRecommendationBufferModel recommendation = this.getRecommendationOfAnyType(userId, scenarioId,
					leadingItemsSorted, recommendationType);
			if (recommendation != null)
			{
				return recommendation;
			}
			subLeadingItems.remove(0); // remove first element
		}

		// Read buffer for each leading item starting with most recent
		for (int i = leadingItems.size() - 1; i >= 0; i--)
		{
			final String leadingItem = leadingItems.get(i);
			final SAPRecommendationBufferModel recommendation = this.getRecommendationOfAnyType(userId, scenarioId, leadingItem,
					recommendationType);
			if (recommendation != null)
			{
				return recommendation;
			}
		}

		// Read buffer with empty leading items
		return this.getRecommendationOfAnyType(userId, scenarioId, "", recommendationType);
	}

	/**
	 * Read {@link ProductRecommendationData}s according to {@link RecommendationContext}.
	 *
	 * @param context
	 *           Parameters of the recommendations to read.
	 * @return {@link List} of {@link ProductRecommendationData}
	 */
	public List<ProductRecommendationData> getProductRecommendation(final RecommendationContext context)
	{
		final RecommendationScenario recoScenario = this.createRecommendationScenario(context);
		final String userId = recoScenario.getUserId();
		final String scenarioId = context.getScenarioId();
		final Scenario scenario = recoScenario.getScenarios().get(0);
		final String leadingItems = scenario.getLeadingObjects().stream() //
				.map(LeadingObject::getLeadingObjectId) //
				.sorted() //
				.collect(Collectors.joining(","));

		SAPRecommendationBufferModel buffer;

		// Read buffered results
		if (userId.isEmpty())
		{
			buffer = recommendationBufferService.getGenericRecommendation(scenarioId, leadingItems);
		}
		else
		{
			buffer = recommendationBufferService.getPersonalizedRecommendation(userId, scenarioId, leadingItems);
		}

		// Recommendation found in buffer
		if (buffer != null)
		{
			// If expired, asynchronously fill the buffer for next time
			if (recommendationBufferService.isRecommendationExpired(buffer))
			{
				this.createStartRunnable(recoScenario);
			}

			// Return the expired recommendation
			return RecommendationScenarioUtils.convertBufferToList(buffer);
		}

		// No recommendation found in buffer, try to get one from backend
		final List<ProductRecommendationData> recoListFromBackEnd = getRecommendationFromBackendWithinThreshold(recoScenario);
		if (!recoListFromBackEnd.isEmpty())
		{
			return recoListFromBackEnd;
		}

		// No response from backend or threshold expired, try to get a best possible match from buffer
		return this.getFallbackRecommendation(recoScenario);

	}

	protected List<String> getRecentItemsFromSession(final String leadingItemType)
	{
		switch (leadingItemType)
		{
			case SapymktrecommendationConstants.PRODUCT:
				return this.recentViewedItemsService.getRecentViewedProducts();
			case SapymktrecommendationConstants.CATEGORY:
				return this.recentViewedItemsService.getRecentViewedCategories();
			default:
				LOG.error("Invalid leadingItemType='{}' supplied.", leadingItemType);
				return Collections.emptyList();
		}
	}

	private List<ProductRecommendationData> getRecommendationFromBackendWithinThreshold(final RecommendationScenario recoScenario)
	{
		final RunnablePopulateRecommendationBuffer runnable = this.createStartRunnable(recoScenario);
		final long stopTime = System.currentTimeMillis() + this.requestTimeoutThreshold;

		do
		{
			try
			{
				Thread.sleep(5);
			}
			catch (InterruptedException e)
			{
				LOG.error("Exception sleeping.", e);
			}

			final List<ProductRecommendationData> recoList = runnable.getRecommendationList();
			if (recoList != null)
			{
				return recoList;
			}
		}
		while (System.currentTimeMillis() < stopTime);

		return Collections.emptyList();
	}

	private SAPRecommendationBufferModel getRecommendationOfAnyType(final String userId, final String scenarioId,
			final String leadingItemsSorted, final String recommendationType)
	{
		switch (recommendationType)
		{
			case PERSONALIZED_RECOMMENDATION:
				return recommendationBufferService.getPersonalizedRecommendation(userId, scenarioId, leadingItemsSorted);

			case RESTRICTED_RECOMMENDATION:
				return recommendationBufferService.getRestrictedRecommendation(scenarioId, leadingItemsSorted);

			case GENERIC_RECOMMENDATION:
				return recommendationBufferService.getGenericRecommendation(scenarioId, leadingItemsSorted);

			default:
				return null;
		}
	}

	/**
	 * This method read all ScenarioHash for the provided scenarioId of the current user.<br>
	 *
	 *
	 * @param scenarioIds
	 *           {@link List} of scenario ids.
	 * @return {@link List} of {@link ScenarioHash}. The resulting list may not contain all requested scenario ids.
	 *
	 */
	public List<ScenarioHash> getScenarioHashes(final List<String> scenarioIds)
	{
		final RecommendationScenario recoScenario = new RecommendationScenario(this.userContextService.getUserId(),
				this.userContextService.getUserOrigin());

		// Add all scenarioId to recoScenario
		scenarioIds.stream() //
				.map(Scenario::new) //
				.collect(Collectors.toCollection(recoScenario::getScenarios));

		try
		{
			this.executeRecommendationScenario(recoScenario, true);
			return recoScenario.getScenarioHashes();
		}
		catch (final IOException e)
		{
			LOG.error("Error reading scenario hashes for = {}", scenarioIds, e);
			return Collections.emptyList();
		}
	}

	private RunnablePopulateRecommendationBuffer createStartRunnable(final RecommendationScenario recoScenario)
	{
		final RunnablePopulateRecommendationBuffer runnableBuffer = new RunnablePopulateRecommendationBuffer();
		runnableBuffer.setoDataService(oDataService);
		runnableBuffer.setRecommendationBufferService(recommendationBufferService);
		runnableBuffer.setRecommendationScenario(new RecommendationScenario(recoScenario));

		final Tenant myTenant = Registry.getCurrentTenant();
		final TenantAwareThreadFactory threadFactory = new TenantAwareThreadFactory(myTenant);
		final Thread workerThread = myTenant.createAndRegisterBackgroundThread(runnableBuffer, threadFactory);
		workerThread.setName("RunnablePopulateRecommendationBuffer-" + myTenant.getTenantID() + "-" + workerThread.getId());
		workerThread.start();
		return runnableBuffer;
	}

	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
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
	public void setRequestTimeoutThreshold(final int requestTimeoutThreshold)
	{
		LOG.debug("requestTimeoutThreshold={}", requestTimeoutThreshold);
		this.requestTimeoutThreshold = requestTimeoutThreshold;
	}

	@Required
	public void setUserContextService(final UserContextService userContextService)
	{
		this.userContextService = userContextService;
	}

	/**
	 * Simple validation of recommendation hash ID. 32 characters hexadecimal uppercase.
	 *
	 * @param hashId
	 *           Hash ID to validate
	 * @return true if valid, false otherwise.
	 */
	public boolean validateScenarioHash(final String hashId)
	{
		if (hashId == null || hashId.length() != 32)
		{
			return false;
		}
		for (int i = 0; i < 32; i++)
		{
			final char c = hashId.charAt(i);
			if ((c < '0' || '9' < c) && (c < 'A' || 'F' < c))
			{
				return false;
			}
		}
		return true;
	}

}
