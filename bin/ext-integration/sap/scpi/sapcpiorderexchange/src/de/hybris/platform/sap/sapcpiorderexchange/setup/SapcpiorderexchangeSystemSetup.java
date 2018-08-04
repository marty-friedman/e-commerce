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
package de.hybris.platform.sap.sapcpiorderexchange.setup;

import static de.hybris.platform.sap.sapcpiorderexchange.constants.SapcpiorderexchangeConstants.PLATFORM_LOGO_CODE;

import de.hybris.platform.core.initialization.SystemSetup;

import java.io.InputStream;

import de.hybris.platform.sap.sapcpiorderexchange.constants.SapcpiorderexchangeConstants;
import de.hybris.platform.sap.sapcpiorderexchange.service.SapcpiorderexchangeService;


@SystemSetup(extension = SapcpiorderexchangeConstants.EXTENSIONNAME)
public class SapcpiorderexchangeSystemSetup
{
	private final SapcpiorderexchangeService sapcpiorderexchangeService;

	public SapcpiorderexchangeSystemSetup(final SapcpiorderexchangeService sapcpiorderexchangeService)
	{
		this.sapcpiorderexchangeService = sapcpiorderexchangeService;
	}

	@SystemSetup(process = SystemSetup.Process.INIT, type = SystemSetup.Type.ESSENTIAL)
	public void createEssentialData()
	{
		sapcpiorderexchangeService.createLogo(PLATFORM_LOGO_CODE);
	}

	private InputStream getImageStream()
	{
		return SapcpiorderexchangeSystemSetup.class.getResourceAsStream("/sapcpiorderexchange/sap-hybris-platform.png");
	}
}
