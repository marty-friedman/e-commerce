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
package de.hybris.platform.initiateyaasconfigurationsync.setup;

import static de.hybris.platform.initiateyaasconfigurationsync.constants.InitiateyaasconfigurationsyncConstants.PLATFORM_LOGO_CODE;

import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.initiateyaasconfigurationsync.constants.InitiateyaasconfigurationsyncConstants;
import de.hybris.platform.initiateyaasconfigurationsync.service.InitiateyaasconfigurationsyncService;


@SystemSetup(extension = InitiateyaasconfigurationsyncConstants.EXTENSIONNAME)
public class InitiateyaasconfigurationsyncSystemSetup
{
	private final InitiateyaasconfigurationsyncService initiateyaasconfigurationsyncService;

	public InitiateyaasconfigurationsyncSystemSetup(
			final InitiateyaasconfigurationsyncService initiateyaasconfigurationsyncService)
	{
		this.initiateyaasconfigurationsyncService = initiateyaasconfigurationsyncService;
	}

	@SystemSetup(process = SystemSetup.Process.INIT, type = SystemSetup.Type.ESSENTIAL)
	public void createEssentialData()
	{
		initiateyaasconfigurationsyncService.createLogo(PLATFORM_LOGO_CODE);
	}

}
