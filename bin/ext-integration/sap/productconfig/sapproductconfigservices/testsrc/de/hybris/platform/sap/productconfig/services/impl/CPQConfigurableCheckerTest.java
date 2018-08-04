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
package de.hybris.platform.sap.productconfig.services.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ConfiguratorSettingsService;
import de.hybris.platform.product.model.AbstractConfiguratorSettingModel;
import de.hybris.platform.sap.productconfig.services.model.CPQConfiguratorSettingsModel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@SuppressWarnings("javadoc")
@UnitTest
public class CPQConfigurableCheckerTest
{

	private final CPQConfigurableChecker classUnderTest = new CPQConfigurableChecker();

	@Mock
	private ConfiguratorSettingsService configuratorSettingsService;

	@Mock
	private ProductModel product;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		classUnderTest.setConfiguratorSettingsService(configuratorSettingsService);
	}

	@Test
	public void testIsProductConfigurableNullProduct()
	{
		try
		{
			classUnderTest.isProductConfigurable(null);
			Assert.fail("Should throw IllegalArgumentException");
		}
		catch (final IllegalArgumentException e)
		{
			verifyNoMoreInteractions(configuratorSettingsService);
		}
	}

	@Test
	public void testIsCpqConfigurator()
	{
		final AbstractConfiguratorSettingModel configuratorSetting = new CPQConfiguratorSettingsModel();
		configuratorSetting.setConfiguratorType(ConfiguratorType.CPQCONFIGURATOR);

		assertTrue(classUnderTest.isCPQConfigurator(configuratorSetting));
	}

	@Test
	public void testIsNotCpqConfigurator()
	{
		final AbstractConfiguratorSettingModel configuratorSetting = mock(AbstractConfiguratorSettingModel.class);

		assertFalse(classUnderTest.isCPQConfigurator(configuratorSetting));
	}

	@Test
	public void testProductCPQConfigurable()
	{
		final List<AbstractConfiguratorSettingModel> list = new ArrayList<>();
		final AbstractConfiguratorSettingModel configuratorSetting = new CPQConfiguratorSettingsModel();
		configuratorSetting.setConfiguratorType(ConfiguratorType.CPQCONFIGURATOR);
		list.add(configuratorSetting);

		when(configuratorSettingsService.getConfiguratorSettingsForProduct(product)).thenReturn(list);

		assertTrue(classUnderTest.isCPQConfigurableProduct(product));
	}

	@Test
	public void testProductNotCPQConfigurable()
	{
		final List<AbstractConfiguratorSettingModel> list = new ArrayList<>();
		final AbstractConfiguratorSettingModel configuratorSetting = mock(AbstractConfiguratorSettingModel.class);
		list.add(configuratorSetting);

		when(configuratorSettingsService.getConfiguratorSettingsForProduct(product)).thenReturn(list);

		assertFalse(classUnderTest.isCPQConfigurableProduct(product));
	}

}
