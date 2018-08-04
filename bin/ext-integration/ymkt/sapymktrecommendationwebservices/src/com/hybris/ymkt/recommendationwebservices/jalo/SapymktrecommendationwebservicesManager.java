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
package com.hybris.ymkt.recommendationwebservices.jalo;

import de.hybris.platform.jalo.JaloSession;
import com.hybris.ymkt.recommendationwebservices.constants.SapymktrecommendationwebservicesConstants;

/**
 * This is the extension manager of the Sapprodrecowebservices extension.
 */
public class SapymktrecommendationwebservicesManager extends GeneratedSapymktrecommendationwebservicesManager {
	public static final SapymktrecommendationwebservicesManager getInstance() {
		return (SapymktrecommendationwebservicesManager) JaloSession.getCurrentSession().getExtensionManager()
				.getExtension(SapymktrecommendationwebservicesConstants.EXTENSIONNAME);
	}
}
