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
package de.hybris.platform.marketplaceservices.setup;

import de.hybris.platform.commerceservices.setup.AbstractSystemSetup;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.core.initialization.SystemSetupParameterMethod;
import de.hybris.platform.marketplaceservices.constants.MarketplaceservicesConstants;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;


@SystemSetup(extension = MarketplaceservicesConstants.EXTENSIONNAME)
public class MarketplaceservicesSystemSetup extends AbstractSystemSetup
{
	private static final String ESSENTIAL_DATA_PARAM_KEY = "importEssentialData";
	private static final String ESSENTIAL_DATA_PARAM_VALUE = "Import Essential Data";
	private static final String ESSENTIAL_DATA_PREFIX = "essentialdata_";

	@Override
	@SystemSetupParameterMethod
	public List<SystemSetupParameter> getInitializationOptions()
	{
		final List<SystemSetupParameter> params = new ArrayList<>();
		params.add(createBooleanSystemSetupParameter(ESSENTIAL_DATA_PARAM_KEY, ESSENTIAL_DATA_PARAM_VALUE, true));
		return params;
	}

	@SystemSetup(process = SystemSetup.Process.ALL, type = SystemSetup.Type.ESSENTIAL)
	public void importInitializationData()
	{
		importEssentialData();
	}

	/**
	 * import essential data here
	 */
	protected void importEssentialData()
	{
		final String[] fileNames =
		{ "usergroups", "usergroups_en", "personalization", "applicableRestrictionTypes" };
		for (final String fileName : fileNames)
		{
			final String fileFullName = ESSENTIAL_DATA_PREFIX + fileName + ".impex";
			getSetupImpexService().importImpexFile(
					MessageFormat.format("/{0}/import/{1}", MarketplaceservicesConstants.EXTENSIONNAME, fileFullName), false);
		}
	}
}
