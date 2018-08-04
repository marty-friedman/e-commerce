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
package com.hybris.ymkt.recommendationaddon.jalo;

import de.hybris.platform.core.Registry;

import com.hybris.ymkt.recommendationaddon.constants.SapymktrecommendationaddonConstants;



/**
 *
 * This is the extension manager of the SapymktrecommendationaddonManager extension.
 */
public class SapymktrecommendationaddonManager extends GeneratedSapymktrecommendationaddonManager
{

	/**
	 * Get the valid instance of this manager.
	 *
	 * @return the current instance of this manager
	 */
	public static SapymktrecommendationaddonManager getInstance()
	{
		return (SapymktrecommendationaddonManager) Registry.getCurrentTenant().getJaloConnection().getExtensionManager()
				.getExtension(SapymktrecommendationaddonConstants.EXTENSIONNAME);
	}
}
