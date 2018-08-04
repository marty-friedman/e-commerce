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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.ep.EntityProviderException;
import org.apache.olingo.odata2.api.ep.EntityProviderReadProperties;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hybris.ymkt.common.http.HttpURLConnectionRequest;
import com.hybris.ymkt.common.http.HttpURLConnectionResponse;
import com.hybris.ymkt.common.odata.ODataService;
import com.hybris.ymkt.recommendation.dao.ProductRecommendationData;
import com.hybris.ymkt.recommendation.dao.RecommendationScenario;
import com.hybris.ymkt.recommendation.dao.RecommendationScenario.LeadingObject;
import com.hybris.ymkt.recommendation.dao.RecommendationScenario.ResultObject;
import com.hybris.ymkt.recommendation.dao.RecommendationScenario.ScenarioHash;
import com.hybris.ymkt.recommendation.utils.RecommendationScenarioUtils;
import com.hybris.ymkt.recommendationbuffer.model.SAPRecommendationBufferModel;
import com.hybris.ymkt.recommendationbuffer.service.RecommendationBufferService;


/**
 * Runnable class used to do an eager fill of the product recommendation buffer
 */
public class RunnablePopulateRecommendationBuffer implements Runnable
{
	private static final Logger LOG = LoggerFactory.getLogger(RunnablePopulateRecommendationBuffer.class);

	protected static final String APPLICATION_JSON = "application/json";
	protected static final String RECOMMENDATION_SCENARIOS = "RecommendationScenarios";
	protected static final EntityProviderReadProperties NO_READ_PROPERTIES = EntityProviderReadProperties.init().build();
	private volatile List<ProductRecommendationData> successRecoList;

	private ODataService oDataService;
	private RecommendationBufferService recommendationBufferService;
	private RecommendationScenario recommendationScenario;

	/**
	 * Makes the call to SAP Hybris Marketing to retrieve a product recommendation
	 */
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

	/**
	 * Call SAP Hybris Marketing to get a recommendation. Then add or update the recommendation in the buffer.
	 */
	private void getRecommendationFromBackend(final RecommendationScenario recommendationScenario)
	{
		try
		{
			this.executeRecommendationScenario(recommendationScenario, false);
			this.updateRecommendationBuffer(recommendationScenario);
		}
		catch (final IOException e)
		{
			LOG.error("Error reading recommendation from backend using = {}", recommendationScenario, e);
		}
	}

	public List<ProductRecommendationData> getRecommendationList()
	{
		return successRecoList;
	}

	@Override
	public void run()
	{
		final String threadName = Thread.currentThread().getName();
		LOG.info("Thread '{}' - Start populate recommendation buffer with {}", threadName, recommendationScenario);

		final List<LeadingObject> copyLeadingObjects = new ArrayList<>(
				recommendationScenario.getScenarios().get(0).getLeadingObjects());

		//Get a recommendation using the complete list of leading items
		this.getRecommendationFromBackend(recommendationScenario);
		this.updateSuccessRecoList(recommendationScenario);

		//Get a recommendation for each leading item in list
		if (copyLeadingObjects.size() >= 2)
		{
			for (final LeadingObject lo : copyLeadingObjects)
			{
				final String scenarioId = recommendationScenario.getScenarios().get(0).getScenarioId();
				final String userId = recommendationScenario.getUserId();
				final SAPRecommendationBufferModel buffer;
				if (userId.isEmpty())
				{
					buffer = recommendationBufferService.getGenericRecommendation(scenarioId, lo.getLeadingObjectId());
				}
				else
				{
					buffer = recommendationBufferService.getPersonalizedRecommendation(userId, scenarioId, lo.getLeadingObjectId());
				}

				//Get a recommendation from backend if buffer entry is not found or is found but is expired
				if (buffer == null || recommendationBufferService.isRecommendationExpired(buffer))
				{
					//Remove leading items and add the single leading item from loop
					recommendationScenario.getScenarios().get(0).getLeadingObjects().clear();
					recommendationScenario.getScenarios().get(0).getLeadingObjects().add(lo);
					this.getRecommendationFromBackend(recommendationScenario);
					this.updateSuccessRecoList(recommendationScenario);
				}
			}
		}

		//Get a recommendation with empty leading items
		if (!copyLeadingObjects.isEmpty())
		{
			recommendationScenario.getScenarios().get(0).getLeadingObjects().clear();
			this.getRecommendationFromBackend(recommendationScenario);
			this.updateSuccessRecoList(recommendationScenario);
		}

		LOG.info("Thread '{}' - Finish populate recommendation buffer", threadName);
	}

	public void setoDataService(final ODataService oDataService)
	{
		this.oDataService = oDataService;
	}

	public void setRecommendationBufferService(final RecommendationBufferService recommendationBufferService)
	{
		this.recommendationBufferService = recommendationBufferService;
	}

	public void setRecommendationScenario(final RecommendationScenario recommendationScenario)
	{
		this.recommendationScenario = recommendationScenario;
	}

	protected void updateRecommendationBuffer(final RecommendationScenario recommendationScenario)
	{
		final List<ScenarioHash> scenarioHashes = recommendationScenario.getScenarioHashes();
		if (scenarioHashes.isEmpty())
		{
			return;
		}
		assert scenarioHashes.size() == 1 : scenarioHashes.size();
		final ScenarioHash scenarioHash = scenarioHashes.get(0);

		final List<ResultObject> resultObjects = recommendationScenario.getResultObjects();
		if (resultObjects.isEmpty())
		{
			return;
		}

		final String userId = recommendationScenario.getUserId();
		final String scenarioId = scenarioHash.getScenarioId();
		final String hashId = scenarioHash.getHashId();

		final String leadingItems = recommendationScenario.getScenarios().get(0) //
				.getLeadingObjects().stream() //
				.map(LeadingObject::getLeadingObjectId) //
				.sorted() //
				.collect(Collectors.joining(","));

		final String recoList = resultObjects.stream().map(ResultObject::getResultObjectId).collect(Collectors.joining(","));
		final String recoType = scenarioHash.getResultScope();
		final Date expiresOn = scenarioHash.getExpiresOn();

		this.recommendationBufferService.saveRecommendation(userId, scenarioId, hashId, leadingItems, recoList, recoType,
				expiresOn);
	}

	private void updateSuccessRecoList(final RecommendationScenario recommendationScenario)
	{
		if (this.successRecoList == null)
		{
			//Update volatile list with backend result
			this.successRecoList = recommendationScenario.getResultObjects().stream() //
					.map(ResultObject::getResultObjectId) //
					.map(ProductRecommendationData::new) //
					.collect(Collectors.toList());

			LOG.debug("Found recommendation from backend using = {}", recommendationScenario);
		}
	}
}