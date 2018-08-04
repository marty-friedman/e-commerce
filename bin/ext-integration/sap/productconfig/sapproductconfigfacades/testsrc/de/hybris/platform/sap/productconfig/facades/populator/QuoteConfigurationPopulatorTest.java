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
package de.hybris.platform.sap.productconfig.facades.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.quote.data.QuoteData;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class QuoteConfigurationPopulatorTest extends AbstractOrderConfigurationPopulatorTest
{
	@Override
	@Before
	public void setup()
	{
		super.setup();
		classUnderTest = new QuoteConfigurationPopulator();

		source = new QuoteModel();
		source.setEntries(entryList);
		target = new QuoteData();
		target.setEntries(targetEntryList);
	}

	@Override
	@Test(expected = IllegalArgumentException.class)
	public void testWriteToTargetEntryIllegalArgument()
	{
		super.testWriteToTargetEntryIllegalArgument();
	}

	@Override
	@Test
	public void testWriteToTargetEntry()
	{
		super.testWriteToTargetEntry();
	}

	@Override
	@Test
	public void testWriteToTargetEntryInconsistent()
	{
		super.testWriteToTargetEntryInconsistent();
	}

	@Override
	@Test
	public void testCreateConfigurationInfos()
	{
		super.testCreateConfigurationInfos();
	}

	@Override
	@Test(expected = ConversionException.class)
	public void testCreateConfigurationInfosException()
	{
		super.testCreateConfigurationInfosException();
	}

	@Override
	@Test(expected = IllegalStateException.class)
	public void testWriteToTargetEntrySummaryMapNull()
	{
		super.testWriteToTargetEntrySummaryMapNull();
	}

	@Override
	@Test(expected = IllegalStateException.class)
	public void testValidateAndSetConfigAttachedNullExternal()
	{
		super.testValidateAndSetConfigAttachedNullExternal();
	}

	@Override
	@Test(expected = IllegalStateException.class)
	public void testValidateAndSetConfigAttachedEmptyExternal()
	{
		super.testValidateAndSetConfigAttachedEmptyExternal();
	}
}
