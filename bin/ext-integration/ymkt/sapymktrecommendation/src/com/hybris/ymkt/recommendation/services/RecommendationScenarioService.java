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
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.ep.EntityProvider;
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
import com.hybris.ymkt.recommendation.dao.SAPRecommendationType;


/**
 * This service reads the ProductRecoScenarios (ScenarioId) entity from the ODataService.
 */
public class RecommendationScenarioService
{
	private static final Logger LOG = LoggerFactory.getLogger(RecommendationScenarioService.class);

	protected static final EntityProviderReadProperties NO_READ_PROPERTIES = EntityProviderReadProperties.init().build();

	protected ODataService oDataService;

	protected SAPRecommendationType createSAPRecommendationType(final ODataEntry entry)
	{
		final SAPRecommendationType scenario = new SAPRecommendationType((String) entry.getProperties().get("ScenarioId"));
		scenario.setDescription((String) entry.getProperties().get("ScenarioDescription"));
		return scenario;
	}

	/**
	 * @return {@link List} of {@link SAPRecommendationType} from the yMKT system.<br>
	 *         The list is sorted by {@link SAPRecommendationType#getId()}.
	 * @throws IOException
	 */
	@Nonnull
	public List<SAPRecommendationType> getRecommendationScenarios() throws IOException
	{
		try
		{
			final URL url = this.oDataService.createURL("ProductRecoScenarios", //
					"$orderby", "ScenarioId", //
					"$select", "ScenarioId,ScenarioDescription");
			final HttpURLConnectionRequest request = new HttpURLConnectionRequest("GET", url);

			request.getRequestProperties().put("Accept", "application/json");

			final HttpURLConnectionResponse response = this.oDataService.executeWithRetry(request);

			final EdmEntitySet entitySet = this.oDataService.getEdm().getDefaultEntityContainer()
					.getEntitySet("ProductRecoScenarios");
			final InputStream content = new ByteArrayInputStream(response.getPayload());
			final ODataFeed feed = EntityProvider.readFeed("application/json", entitySet, content, NO_READ_PROPERTIES);
			final List<ODataEntry> entries = feed.getEntries();

			final List<SAPRecommendationType> scenarios = entries == null ? Collections.emptyList() : //
					entries.stream() //
							.map(this::createSAPRecommendationType) //
							.collect(Collectors.toList());

			if (scenarios.isEmpty())
			{
				LOG.warn("yMKT has returned no scenarios!");
			}

			return scenarios;
		}
		catch (ODataException e)
		{
			throw new IOException("Error using/parsing entitySet ProductRecoScenarios.", e);
		}
	}

	@Required
	public void setODataService(final ODataService oDataService)
	{
		this.oDataService = oDataService;
	}

}
