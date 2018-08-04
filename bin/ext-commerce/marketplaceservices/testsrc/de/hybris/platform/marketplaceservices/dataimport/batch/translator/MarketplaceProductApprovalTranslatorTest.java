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
package de.hybris.platform.marketplaceservices.dataimport.batch.translator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.marketplaceservices.strategies.AutoApproveProductStrategy;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.Config;
import de.hybris.platform.validation.coverage.CoverageInfo;
import de.hybris.platform.validation.coverage.CoverageInfo.CoveragePropertyInfoMessage;
import de.hybris.platform.validation.coverage.strategies.impl.ValidationBasedCoverageCalculationStrategy;


@UnitTest
@RunWith(PowerMockRunner.class)
@PrepareForTest(
{ Config.class })
public class MarketplaceProductApprovalTranslatorTest
{
	private static final String PRODUCT_CODE = "Canon_123456";
	private static final String PRODUCT_SKU = "123456";
	private static final String ERROR_MSG1 = "errro 1";
	private static final String ERROR_MSG2 = "error 2";

	private MarketplaceProductApprovalTranslator translator;
	private Item item;
	private ProductModel product;

	@Mock
	private ValidationBasedCoverageCalculationStrategy strategy;

	@Mock
	private ModelService modelService;
	
	@Mock
	private AutoApproveProductStrategy autoApproveProductStrategy;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		translator = new MarketplaceProductApprovalTranslator();
		translator.setModelService(modelService);
		translator.setValidationCoverageCalculationStrategy(strategy);
		translator.setAutoApproveProductStrategy(autoApproveProductStrategy);
		product = new ProductModel();
		product.setCode(PRODUCT_CODE);
		Mockito.when(modelService.get(item)).thenReturn(product);
		PowerMockito.mockStatic(Config.class);
		PowerMockito.when(Config.getDouble("marketplaceservices.default.product.coverage.index", 1.0)).thenReturn(0.9);
	}

	@Test
	public void testValidationPassWithNullCoverageInfo()
	{
		Mockito.when(strategy.calculate(product)).thenReturn(null);
		translator.performImport(PRODUCT_SKU, item);
		assertTrue(product.getSaleable());
		assertEquals(product.getApprovalStatus(), ArticleApprovalStatus.APPROVED);
		Mockito.verify(modelService).save(product);
	}

	@Test
	public void testValidationPass()
	{
		final CoverageInfo coverage = new CoverageInfo(0.9);
		Mockito.when(strategy.calculate(product)).thenReturn(coverage);
		translator.performImport(PRODUCT_SKU, item);
		assertTrue(product.getSaleable());
		assertEquals(product.getApprovalStatus(), ArticleApprovalStatus.APPROVED);
		Mockito.verify(modelService).save(product);
	}

	@Test
	public void testValidationFail()
	{
		final CoveragePropertyInfoMessage msg1 = new CoveragePropertyInfoMessage("1", ERROR_MSG1);
		final CoveragePropertyInfoMessage msg2 = new CoveragePropertyInfoMessage("2", ERROR_MSG2);

		try
		{
			final CoverageInfo coverage = new CoverageInfo(0.8);
			coverage.setPropertyInfoMessages(Arrays.asList(msg1, msg2));
			Mockito.when(autoApproveProductStrategy.autoApproveVariantAndApparelProduct(product)).thenReturn(coverage);
			translator.performImport(PRODUCT_SKU, item);
			fail("Should have thrown exception but did not!");
		}
		catch (final IllegalArgumentException e)
		{
			final String msg = e.getMessage();
			assertTrue(msg.contains(ERROR_MSG1));
			assertTrue(msg.contains(ERROR_MSG2));
		}
	}
}
