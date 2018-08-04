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
package de.hybris.platform.sap.productconfig.runtime.ssc.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;

import java.util.Date;

import org.apache.commons.lang.NotImplementedException;
import org.junit.Test;


@UnitTest
public class ConfigurationProviderTest
{

	@Test(expected = NotImplementedException.class)
	public void testDefault_RetrieveConfigurationFromVariant()
	{
		final ConfigurationProvider provider = new DefaultConfigurationProvider();
		provider.retrieveConfigurationFromVariant(null, null);
	}

	// if you modify the ConfigurationProvider interface, do NOT change the
	// class below.
	// instead fix any syntax error by defining default implementations, or
	// proper use of deprecation
	// add a test for default behavior of method above
	private static class DefaultConfigurationProvider implements ConfigurationProvider
	{
		@Override
		public ConfigModel createDefaultConfiguration(final KBKey kbKey)
		{
			return null;
		}

		@Override
		public boolean updateConfiguration(final ConfigModel model)
		{
			return false;
		}

		@Override
		public ConfigModel retrieveConfigurationModel(final String configId)
		{
			return null;
		}

		@Override
		public String retrieveExternalConfiguration(final String configId)
		{
			return null;
		}

		@Override
		public ConfigModel createConfigurationFromExternalSource(final Configuration extConfig)
		{
			return null;
		}

		@Override
		public ConfigModel createConfigurationFromExternalSource(final KBKey kbKey, final String extConfig)
		{
			return null;
		}

		@Override
		public void releaseSession(final String configId)
		{
			// empty
		}

		@Override
		public boolean isKbForDateExists(final String productCode, final Date kbDate)
		{
			return false;
		}


		@Override
		public boolean isKbVersionExists(final KBKey kbKey, final String externalConfig)
		{
			return false;
		}

	}

}
