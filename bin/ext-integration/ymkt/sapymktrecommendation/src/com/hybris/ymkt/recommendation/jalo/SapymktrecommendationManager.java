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
package com.hybris.ymkt.recommendation.jalo;

import de.hybris.platform.jalo.JaloSession;

import com.hybris.ymkt.recommendation.constants.SapymktrecommendationConstants;


public class SapymktrecommendationManager extends GeneratedSapymktrecommendationManager
{

	public static final SapymktrecommendationManager getInstance()
	{
		return (SapymktrecommendationManager) JaloSession.getCurrentSession().getExtensionManager()
				.getExtension(SapymktrecommendationConstants.EXTENSIONNAME);
	}

}
