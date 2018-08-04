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
package com.hybris.ymkt.recommendationwebservices.facades;

import de.hybris.platform.cmsfacades.data.OptionData;
import de.hybris.platform.util.localization.Localization;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.ymkt.recommendation.dao.SAPRecommendationItemDataSourceType;
import com.hybris.ymkt.recommendation.dao.SAPRecommendationType;
import com.hybris.ymkt.recommendation.services.RecommendationDataSourceTypeService;
import com.hybris.ymkt.recommendation.services.RecommendationScenarioService;


/**
 * Populator to generate dropdown values
 */
public class RecommendationPopulatorFacade
{
	private static final String CATEGORY_CODE = "C";

	private static final Logger LOGGER = LoggerFactory.getLogger(RecommendationPopulatorFacade.class);

	private static final String PRODUCT_CODE = "P";

	protected RecommendationDataSourceTypeService recommendationDataSourceTypeService;
	protected RecommendationScenarioService recommendationScenarioService;

	protected OptionData createOptionData(final SAPRecommendationItemDataSourceType source)
	{
		return this.createOptionData(source.getId(), source.getDescription());
	}

	protected OptionData createOptionData(final SAPRecommendationType type)
	{
		return this.createOptionData(type.getId());
	}

	protected OptionData createOptionData(final String idAndLabel)
	{
		return this.createOptionData(idAndLabel, idAndLabel);
	}

	protected OptionData createOptionData(final String id, final String label)
	{
		final OptionData opData = new OptionData();
		opData.setId(id);
		opData.setLabel(label);
		return opData;
	}

	protected List<OptionData> getItemDataSourceTypes()
	{
		try
		{
			return this.recommendationDataSourceTypeService.getItemDataSourceTypes().stream() //
					.map(this::createOptionData) //
					.collect(Collectors.toList());
		}
		catch (IOException e)
		{
			LOGGER.error("Error retrieving data source types", e);
			return Collections.emptyList();
		}
	}

	protected List<OptionData> getLeadingItemTypes()
	{
		final OptionData opData1 = createOptionData(PRODUCT_CODE,
				Localization.getLocalizedString("type.CMSSAPRecommendationComponent.product"));
		final OptionData opData2 = createOptionData(CATEGORY_CODE,
				Localization.getLocalizedString("type.CMSSAPRecommendationComponent.category"));
		return Arrays.asList(opData1, opData2);
	}

	protected List<OptionData> getRecommendationTypes()
	{
		try
		{
			return this.recommendationScenarioService.getRecommendationScenarios().stream() //
					.map(this::createOptionData) //
					.collect(Collectors.toList());
		}
		catch (IOException e)
		{
			LOGGER.error("Error retrieving scenario IDs", e);
			return Collections.emptyList();
		}
	}

	/**
	 * Call method to fill the appropriate dropdown.
	 * 
	 * @param sourceField
	 *           dropdown that needs to be filled
	 * @return {@link List} of {@link OptionData} for dropdown.
	 */
	public List<OptionData> populateDropDown(final String sourceField)
	{
		switch (sourceField)
		{
			case "recotype":
				return getRecommendationTypes();
			case "leadingitemtype":
				return getLeadingItemTypes();
			case "leadingitemdstype":
			case "cartitemdstype":
				return getItemDataSourceTypes();
			default:
				return Collections.emptyList();
		}
	}

	@Required
	public void setRecommendationDataSourceTypeService(RecommendationDataSourceTypeService recommendationDataSourceTypeService)
	{
		this.recommendationDataSourceTypeService = recommendationDataSourceTypeService;
	}

	@Required
	public void setRecommendationScenarioService(RecommendationScenarioService recommendationScenarioService)
	{
		this.recommendationScenarioService = recommendationScenarioService;
	}

}

