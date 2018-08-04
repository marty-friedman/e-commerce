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
package de.hybris.platform.sap.sapcpiorderexchangeoms.setup;

import static de.hybris.platform.sap.sapcpiorderexchangeoms.constants.SapcpiorderexchangeomsConstants.PLATFORM_LOGO_CODE;

import de.hybris.platform.core.initialization.SystemSetup;

import java.io.InputStream;

import de.hybris.platform.sap.sapcpiorderexchangeoms.constants.SapcpiorderexchangeomsConstants;
import de.hybris.platform.sap.sapcpiorderexchangeoms.service.SapcpiorderexchangeomsService;


@SystemSetup(extension = SapcpiorderexchangeomsConstants.EXTENSIONNAME)
public class SapcpiorderexchangeomsSystemSetup
{
	private final SapcpiorderexchangeomsService sapcpiorderexchangeomsService;

	public SapcpiorderexchangeomsSystemSetup(final SapcpiorderexchangeomsService sapcpiorderexchangeomsService)
	{
		this.sapcpiorderexchangeomsService = sapcpiorderexchangeomsService;
	}

	@SystemSetup(process = SystemSetup.Process.INIT, type = SystemSetup.Type.ESSENTIAL)
	public void createEssentialData()
	{
		sapcpiorderexchangeomsService.createLogo(PLATFORM_LOGO_CODE);
	}

	private InputStream getImageStream()
	{
		return SapcpiorderexchangeomsSystemSetup.class.getResourceAsStream("/sapcpiorderexchangeoms/sap-hybris-platform.png");
	}
}
