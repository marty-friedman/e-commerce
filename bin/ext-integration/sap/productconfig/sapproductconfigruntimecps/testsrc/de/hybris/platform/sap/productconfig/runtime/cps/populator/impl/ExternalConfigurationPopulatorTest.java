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
package de.hybris.platform.sap.productconfig.runtime.cps.populator.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.CPSContextSupplier;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalItem;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Instance;
import de.hybris.platform.sap.productconfig.runtime.interf.external.impl.ConfigurationImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.external.impl.InstanceImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@SuppressWarnings("javadoc")
@UnitTest
public class ExternalConfigurationPopulatorTest
{
	private ExternalConfigurationPopulator classUnderTest;
	private final Configuration source = new ConfigurationImpl();
	private final CPSExternalConfiguration target = new CPSExternalConfiguration();

	@Mock
	private CPSContextSupplier contextSupplier;
	@Mock
	private Converter<Configuration, CPSExternalItem> rootItemConverter;

	private static final boolean CONSISTENT = true;
	private static final boolean COMPLETE = true;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new ExternalConfigurationPopulator();
		classUnderTest.setContextSupplier(contextSupplier);
		classUnderTest.setItemTreeConverter(rootItemConverter);
		final Instance rootInstance = new InstanceImpl();
		rootInstance.setComplete(COMPLETE);
		rootInstance.setConsistent(CONSISTENT);
		source.setRootInstance(rootInstance);
	}

	@Test
	public void testPopulateCoreAttributes()
	{
		classUnderTest.populateCoreAttributes(source, target);
		assertEquals(Boolean.valueOf(COMPLETE), Boolean.valueOf(target.isComplete()));
		assertEquals(Boolean.valueOf(CONSISTENT), Boolean.valueOf(target.isConsistent()));
	}

	@Test
	public void testPopulateContext()
	{
		final KBKey kbKey = new KBKeyImpl("PRODUCTCODE");
		source.setKbKey(kbKey);
		classUnderTest.populateContext(source, target);
		Mockito.verify(contextSupplier).retrieveContext("PRODUCTCODE");
	}

	@Test
	public void testPopulateRootItem()
	{
		classUnderTest.populateInstanceTree(source, target);
		Mockito.verify(rootItemConverter).convert(source);
	}

}
