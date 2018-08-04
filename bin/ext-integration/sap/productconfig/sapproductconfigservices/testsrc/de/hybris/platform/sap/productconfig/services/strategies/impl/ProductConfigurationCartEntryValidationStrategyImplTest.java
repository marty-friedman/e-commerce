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
package de.hybris.platform.sap.productconfig.services.strategies.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPricingOrderIntegrationService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.model.CPQOrderEntryProductInfoModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Unit tests
 */
@UnitTest
public class ProductConfigurationCartEntryValidationStrategyImplTest
{

	private static final String EXT_CONFIG = "X";


	protected ProductConfigurationCartEntryValidationStrategyImpl classUnderTest;

	@Mock
	private ProductConfigurationService productConfigurationService;

	@Mock
	private ProductConfigurationPricingOrderIntegrationService configurationPricingOrderIntegrationService;

	@Mock
	private CartEntryModel cartEntryModel;

	@Mock
	private ProductModel productModel;

	@Mock
	private ModelService modelService;



	private ConfigModel configModel;


	private static final String configId = "1";

	/**
	 * Before each test
	 */
	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new ProductConfigurationCartEntryValidationStrategyImpl();
		classUnderTest.setProductConfigurationService(productConfigurationService);
		classUnderTest.setModelService(modelService);
		classUnderTest.setConfigurationPricingOrderIntegrationService(configurationPricingOrderIntegrationService);

		Mockito.when(cartEntryModel.getProduct()).thenReturn(productModel);
		Mockito.when(cartEntryModel.getPk()).thenReturn(PK.fromLong(1));
		configModel = new ConfigModelImpl();
		configModel.setId(configId);
		Mockito.when(configurationPricingOrderIntegrationService.ensureConfigurationInSession(Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString())).thenReturn(configModel);
		Mockito.when(productConfigurationService.retrieveConfigurationModel(configId)).thenReturn(configModel);
		Mockito.when(Boolean.valueOf(productConfigurationService.hasKbForVersion(Mockito.any(), Mockito.eq(EXT_CONFIG))))
				.thenReturn(Boolean.TRUE);
		Mockito.when(modelService.create(CPQOrderEntryProductInfoModel.class)).thenReturn(new CPQOrderEntryProductInfoModel());

	}


	/**
	 * No external configuration attached to cart entry
	 */
	@Test
	public void testValidateNoExtCFG()
	{
		assertNull(classUnderTest.validateConfiguration(cartEntryModel));
	}

	/**
	 * Configuration is not complete
	 */
	@Test
	public void testValidateNotComplete()
	{
		Mockito.when(cartEntryModel.getExternalConfiguration()).thenReturn(EXT_CONFIG);
		final CommerceCartModification modification = classUnderTest.validateConfiguration(cartEntryModel);
		assertNotNull(modification);
		assertEquals(ProductConfigurationCartEntryValidationStrategyImpl.REVIEW_CONFIGURATION, modification.getStatusCode());
	}

	/**
	 * Configuration is complete and consistent-> No validation message
	 */
	@Test
	public void testValidateCompleteAndConsistent()
	{
		Mockito.when(cartEntryModel.getExternalConfiguration()).thenReturn(EXT_CONFIG);
		configModel.setComplete(true);
		configModel.setConsistent(true);
		final CommerceCartModification modification = classUnderTest.validateConfiguration(cartEntryModel);
		assertNull(modification);
	}

	/**
	 * Configuration is not consistent but complete: validation message
	 */
	@Test
	public void testValidateCompleteNotConsistent()
	{
		Mockito.when(cartEntryModel.getExternalConfiguration()).thenReturn(EXT_CONFIG);
		configModel.setComplete(true);
		configModel.setConsistent(false);
		final CommerceCartModification modification = classUnderTest.validateConfiguration(cartEntryModel);
		assertNotNull(modification);
		assertEquals(ProductConfigurationCartEntryValidationStrategyImpl.REVIEW_CONFIGURATION, modification.getStatusCode());
	}

	/**
	 * Configuration is not complete
	 */
	@Test
	public void testValidateKbNotValid()
	{
		Mockito.when(cartEntryModel.getExternalConfiguration()).thenReturn(EXT_CONFIG);
		Mockito.when(Boolean.valueOf(productConfigurationService.hasKbForVersion(Mockito.any(), Mockito.eq(EXT_CONFIG))))
				.thenReturn(Boolean.FALSE);

		final CommerceCartModification modification = classUnderTest.validateConfiguration(cartEntryModel);
		assertNotNull(modification);
		assertEquals(ProductConfigurationCartEntryValidationStrategyImpl.KB_NOT_VALID, modification.getStatusCode());
		Mockito.verify(cartEntryModel).setExternalConfiguration(null);
	}

	@Test
	public void testValidatePricingError()
	{
		Mockito.when(cartEntryModel.getExternalConfiguration()).thenReturn(EXT_CONFIG);
		configModel.setComplete(true);
		configModel.setConsistent(true);
		configModel.setPricingError(true);
		final CommerceCartModification modification = classUnderTest.validateConfiguration(cartEntryModel);
		assertEquals(ProductConfigurationCartEntryValidationStrategyImpl.PRICING_ERROR, modification.getStatusCode());
	}

	@Test
	public void testResetConfigurationInfo()
	{
		final CartEntryModel orderEntry = new CartEntryModel();
		orderEntry.setProductInfos(Collections.unmodifiableList(Collections.emptyList()));
		classUnderTest.resetConfigurationInfo(orderEntry);
		assertEquals(1, orderEntry.getProductInfos().size());
		assertEquals(ConfiguratorType.CPQCONFIGURATOR, orderEntry.getProductInfos().get(0).getConfiguratorType());
		assertSame(orderEntry, orderEntry.getProductInfos().get(0).getOrderEntry());
	}
}
