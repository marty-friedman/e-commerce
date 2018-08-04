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
package de.hybris.platform.sap.productconfig.frontend.controllers;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.data.RequestContextData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.sap.productconfig.facades.ConfigurationOverviewFacade;
import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;
import de.hybris.platform.sap.productconfig.frontend.OverviewMode;
import de.hybris.platform.sap.productconfig.frontend.OverviewUiData;
import de.hybris.platform.sap.productconfig.frontend.UiStatus;
import de.hybris.platform.sap.productconfig.frontend.model.ProductConfigOverviewPageModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class VariantOverviewControllerTest extends AbstractProductConfigControllerBaseTest
{

	@Mock
	private ConfigurationOverviewFacade configurationOverviewFacade;
	@Mock
	ProductData productData;

	// will inject cmsPageService and pageTitleResolver, as well. For both no setter exists
	@InjectMocks
	private VariantOverviewController classUnderTest;

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new VariantOverviewController();
		MockitoAnnotations.initMocks(this);
		injectMocks(classUnderTest);
		classUnderTest.setConfigurationOverviewFacade(configurationOverviewFacade);

		initializeFirstCall();
		request.setAttribute("de.hybris.platform.acceleratorcms.utils.SpringHelper.bean.requestContextData",
				new RequestContextData());
	}

	@Test
	public void testProductDataIsSet() throws Exception
	{
		classUnderTest.getVariantOverview(PRODUCT_CODE, model, request);
		Mockito.verify(model).addAttribute(Mockito.eq("product"), Mockito.any(ProductData.class));
	}

	@Test
	public void testPageTypeSet() throws Exception
	{
		classUnderTest.populateCMSAttributes(model);
		Mockito.verify(model).addAttribute(Mockito.eq("pageType"), Mockito.eq("productConfigOverviewPage"));
	}

	@Test
	public void testOverviewDataInitializedCorrect() throws Exception
	{
		final ConfigurationOverviewData configOverviewData = new ConfigurationOverviewData();
		given(productData.getCode()).willReturn(PRODUCT_CODE);

		final OverviewUiData overviewUiData = classUnderTest.prepareOverviewUiData(configOverviewData, productData);
		assertEquals(OverviewMode.VARIANT_OVERVIEW, overviewUiData.getOverviewMode());
	}

	@Test
	public void testResetUiCartConfigurationForProduct()
	{
		final UiStatus uiStatus = new UiStatus();
		uiStatus.setConfigId(CONFIG_ID);
		given(sessionAccessFacade.getUiStatusForProduct(PRODUCT_CODE)).willReturn(uiStatus);
		classUnderTest.resetUiCartConfigurationForProduct(PRODUCT_CODE);

		verify(sessionAccessFacade, times(1)).removeUiStatusForProduct(PRODUCT_CODE);
		verify(configCartIntegrationFacade, times(1)).resetConfiguration(CONFIG_ID);
		verify(sessionAccessFacade, times(1)).removeCartEntryForProduct(PRODUCT_CODE);
	}

	@Test
	public void testResetUiCartConfigurationForProductWitCartEntry()
	{
		final UiStatus uiStatus = new UiStatus();
		uiStatus.setConfigId(CONFIG_ID);
		given(sessionAccessFacade.getUiStatusForProduct(PRODUCT_CODE)).willReturn(uiStatus);
		given(sessionAccessFacade.getCartEntryForConfigId(CONFIG_ID)).willReturn("cartentry123");
		classUnderTest.resetUiCartConfigurationForProduct(PRODUCT_CODE);

		verify(sessionAccessFacade, times(1)).removeUiStatusForProduct(PRODUCT_CODE);
		verify(configCartIntegrationFacade, times(0)).resetConfiguration(CONFIG_ID);
		verify(sessionAccessFacade, times(1)).removeCartEntryForProduct(PRODUCT_CODE);
	}


	@Test
	public void testResetUiCartConfigurationForProductUiStatusNull()
	{
		given(sessionAccessFacade.getUiStatusForProduct(PRODUCT_CODE)).willReturn(null);
		classUnderTest.resetUiCartConfigurationForProduct(PRODUCT_CODE);

		verify(sessionAccessFacade, times(0)).removeUiStatusForProduct(PRODUCT_CODE);
		verify(configCartIntegrationFacade, times(0)).resetConfiguration(CONFIG_ID);
		verify(sessionAccessFacade, times(1)).removeCartEntryForProduct(PRODUCT_CODE);
	}

	@Override
	protected void initializeFirstCall() throws Exception
	{
		super.initializeFirstCall();
		final ConfigurationOverviewData configOverviewData = new ConfigurationOverviewData();

		given(configurationOverviewFacade.getOverviewForProductVariant(Mockito.eq(PRODUCT_CODE), Mockito.any()))
				.willReturn(configOverviewData);
		given(cmsPageService.getPageForId("productConfigOverview")).willReturn(new ProductConfigOverviewPageModel());
	}

}
