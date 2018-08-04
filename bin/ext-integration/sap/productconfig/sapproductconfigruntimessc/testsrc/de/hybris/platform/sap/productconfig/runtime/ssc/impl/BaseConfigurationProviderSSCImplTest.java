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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;


/**
 * Unit Tests
 */
@UnitTest
public class BaseConfigurationProviderSSCImplTest
{
	BaseConfigurationProviderSSCImpl classUnderTest = new BaseConfigurationProviderSSCImpl()
	{

		@Override
		protected ConfigModel fillConfigModel(final String qualifiedId)
		{
			return null;
		}

		@Override
		public ConfigModel retrieveConfigurationFromVariant(final String baseProductCode, final String variantProductCode)
		{
			return null;
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
	};
	private static final String sessionId = "session1";
	private static final String configId = "12938";

	@Test
	public void testQualifiedId()
	{
		final String qualifiedId = classUnderTest.retrieveQualifiedId(sessionId, configId);
		assertTrue(qualifiedId.contains(sessionId));
		assertTrue(qualifiedId.contains(configId));

	}

	@Test
	public void testGetFormattedDateNotNull()
	{
		assertNotNull(classUnderTest.getFormattedDate(new KBKeyImpl("pCode")));
	}

	@Test
	public void testGetFormattedDate()
	{
		final Calendar calendar = Calendar.getInstance();
		calendar.set(2016, Calendar.JANUARY, 5);
		final KBKeyImpl kbKey = new KBKeyImpl("pCode", "kbName", "logSys", "kbVersion", calendar.getTime());

		final String formattedDate = classUnderTest.getFormattedDate(kbKey);

		assertEquals("20160105", formattedDate);

	}

}
