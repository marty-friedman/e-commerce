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
package de.hybris.platform.sap.productconfig.runtime.cps.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.pricing.PricingHandler;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceSummaryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceValueUpdateModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@SuppressWarnings("javadoc")
@UnitTest
public class CPSPricingProviderTest
{
	private static final String KB_ID = "kbId";
	private static final String CONFIG_ID = "configId";
	private CPSPricingProvider classUnderTest;
	@Mock
	private PricingHandler pricingHandler;

	@Before
	public void setup() throws PricingEngineException
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new CPSPricingProvider();
		classUnderTest.setPricingHandler(pricingHandler);
		Mockito.when(pricingHandler.getPriceSummary(CONFIG_ID)).thenReturn(new PriceSummaryModel());
	}

	@Test
	public void testGetPriceSummaryNotNull() throws PricingEngineException
	{
		final PriceSummaryModel result = classUnderTest.getPriceSummary(CONFIG_ID);
		assertNotNull(result);
	}

	@Test
	public void testFillValuePrices_updateModels_kbid() throws PricingEngineException
	{
		final List<PriceValueUpdateModel> updateModels = new ArrayList<>();
		for (int i = 0; i < 10; i++)
		{
			updateModels.add(new PriceValueUpdateModel());
		}

		classUnderTest.fillValuePrices(updateModels, KB_ID);
		Mockito.verify(pricingHandler, Mockito.times(updateModels.size())).fillValuePrices(Mockito.anyString(),
				Mockito.any(PriceValueUpdateModel.class));
	}

	@Test
	public void testProviderIsActive()
	{
		assertTrue(classUnderTest.isActive());
	}

	@Test
	public void testFillValuePrices() throws PricingEngineException
	{
		final ConfigModel configModel = createAndFillConfigModelSingleLevel();
		classUnderTest.fillValuePrices(configModel);
		for (final CsticModel csticModel : configModel.getRootInstance().getCstics())
		{
			checkValuePrices(csticModel);
		}
	}

	protected void checkValuePrices(final CsticModel csticModel)
	{
		assertNotNull(csticModel);
		for (final CsticValueModel possibleValue : csticModel.getAssignableValues())
		{
			assertNotNull(possibleValue.getValuePrice());
		}
	}

	protected ConfigModel createAndFillConfigModelSingleLevel()
	{
		final ConfigModel configModel = new ConfigModelImpl();
		configModel.setKbId(KB_ID);
		configModel.setRootInstance(new InstanceModelImpl());
		final CsticModel cstic1 = createCsticModel("cstic1");
		final CsticModel cstic2 = createCsticModel("cstic2");
		final CsticModel cstic3 = createCsticModel("cstic3");
		configModel.getRootInstance().addCstic(cstic1);
		configModel.getRootInstance().addCstic(cstic2);
		configModel.getRootInstance().addCstic(cstic3);
		return configModel;
	}

	protected CsticModel createCsticModel(final String cstic)
	{
		final CsticModel cstic1 = new CsticModelImpl();
		cstic1.setName(cstic);
		return cstic1;
	}

	@Test
	public void testFillValuePrices_Instance_SingleLevel() throws PricingEngineException
	{
		final ConfigModel singleLevel = createAndFillConfigModelSingleLevel();
		classUnderTest.fillValuePricesForInstance(singleLevel.getRootInstance(), singleLevel.getKbId());
		for (final CsticModel cstic : singleLevel.getRootInstance().getCstics())
		{
			Mockito.verify(pricingHandler).fillValuePrices(KB_ID, cstic);
		}
	}

	@Test
	public void testFillValuePrices_SingleLevel() throws PricingEngineException
	{
		final ConfigModel singleLevel = createAndFillConfigModelSingleLevel();
		classUnderTest.fillValuePrices(singleLevel);
		for (final CsticModel cstic : singleLevel.getRootInstance().getCstics())
		{
			Mockito.verify(pricingHandler).fillValuePrices(KB_ID, cstic);
		}
	}

	protected ConfigModel createAndFillConfigModelMultiLevel()
	{
		final ConfigModel configModel = new ConfigModelImpl();
		configModel.setKbId(KB_ID);
		configModel.setRootInstance(new InstanceModelImpl());
		final CsticModel cstic1 = createCsticModel("cstic1");
		final CsticModel cstic2 = createCsticModel("cstic2");
		final CsticModel cstic3 = createCsticModel("cstic3");
		configModel.getRootInstance().addCstic(cstic1);
		configModel.getRootInstance().addCstic(cstic2);
		configModel.getRootInstance().addCstic(cstic3);

		final List<InstanceModel> subInstances = new ArrayList<>();
		configModel.getRootInstance().setSubInstances(subInstances);
		subInstances.add(new InstanceModelImpl());
		subInstances.get(0).setSubInstances(new ArrayList<>());
		subInstances.get(0).getSubInstances().add(new InstanceModelImpl());
		subInstances.get(0).getSubInstances().get(0).addCstic(createCsticModel("cstic4"));
		subInstances.get(0).getSubInstances().get(0).addCstic(createCsticModel("cstic5"));
		subInstances.get(0).getSubInstances().add(new InstanceModelImpl());

		return configModel;
	}

	@Test
	public void testFillValuePrices_Instance_MultiLevel() throws PricingEngineException
	{
		final ConfigModel multiLevel = createAndFillConfigModelMultiLevel();
		classUnderTest.fillValuePricesForInstance(multiLevel.getRootInstance(), multiLevel.getKbId());
		for (final CsticModel cstic : multiLevel.getRootInstance().getCstics())
		{
			Mockito.verify(pricingHandler).fillValuePrices(KB_ID, cstic);
		}
		for (final CsticModel cstic : multiLevel.getRootInstance().getSubInstances().get(0).getCstics())
		{
			Mockito.verify(pricingHandler).fillValuePrices(KB_ID, cstic);
		}
		Mockito.verify(pricingHandler, Mockito.times(5)).fillValuePrices(Mockito.eq(KB_ID), Mockito.any(CsticModel.class));
	}

}
