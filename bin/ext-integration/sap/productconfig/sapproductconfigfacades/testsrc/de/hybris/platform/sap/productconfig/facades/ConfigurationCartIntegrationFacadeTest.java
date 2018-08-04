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
package de.hybris.platform.sap.productconfig.facades;

import static org.junit.Assert.assertNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;

import org.junit.Test;


@UnitTest
public class ConfigurationCartIntegrationFacadeTest
{

	@Test
	public void testFindItemInCartByPKDefault()
	{
		final ConfigurationCartIntegrationFacade defaultFacade = new ConfigurationCartIntegrationFacadeStable();
		assertNull(defaultFacade.findItemInCartByPK(null));
	}

	private static final class ConfigurationCartIntegrationFacadeStable implements ConfigurationCartIntegrationFacade
	{
		@Override
		public ConfigurationData restoreConfiguration(final KBKeyData kbKey, final String cartEntryKey)
		{
			return null;
		}

		@Override
		public void resetConfiguration(final String configId)
		{
			//empty
		}

		@Override
		public boolean isItemInCartByKey(final String key)
		{
			return false;
		}

		@Override
		public String copyConfiguration(final String configId)
		{
			return null;
		}

		@Override
		public String copyConfiguration(final String configId, final String productCode)
		{
			return null;
		}

		@Override
		public String addConfigurationToCart(final ConfigurationData configuration) throws CommerceCartModificationException
		{
			return null;
		}
	}
}
