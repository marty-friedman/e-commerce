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
package de.hybris.platform.promotionengineservices.promotionengine.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.promotionengineservices.model.RuleBasedPromotionModel;
import de.hybris.platform.promotions.model.AbstractPromotionModel;
import de.hybris.platform.ruleengine.RuleEngineService;
import de.hybris.platform.ruleengine.RuleEvaluationResult;
import de.hybris.platform.ruleengine.dao.RuleEngineContextDao;
import de.hybris.platform.ruleengine.enums.RuleType;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineContextModel;
import de.hybris.platform.ruleengine.model.DroolsRuleEngineContextModel;
import de.hybris.platform.ruleengine.strategies.RuleEngineContextFinderStrategy;
import de.hybris.platform.ruleengine.strategies.RuleEngineContextForCatalogVersionsFinderStrategy;
import de.hybris.platform.ruleengine.strategies.impl.DefaultRuleEngineContextFinderStrategy;
import de.hybris.platform.ruleengineservices.converters.populator.CartModelBuilder;
import de.hybris.platform.ruleengineservices.enums.FactContextType;
import de.hybris.platform.ruleengineservices.rao.providers.FactContextFactory;
import de.hybris.platform.ruleengineservices.rao.providers.impl.FactContext;
import de.hybris.platform.servicelayer.time.TimeService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultPromotionEngineUnitTest
{
	private static final String RULE_BASED_PROMOTION_DESC = "RuleBasedPromotion description";
	private static final String NOT_RULE_BASED_PROMOTION_DESC = "Not RuleBasedPromotion description";

	@Mock
	private RuleBasedPromotionModel ruleBasedPromotion;

	@Mock
	private AbstractPromotionModel abstractPromotion;

	@Mock
	private RuleEngineContextDao ruleEngineContextDao;

	@Mock
	private DroolsRuleEngineContextModel legacyTestContext;

	@Mock
	private FactContextFactory factContextFactory;

	@Mock
	private FactContext factContext;

	@Mock
	private RuleEngineContextForCatalogVersionsFinderStrategy ruleEngineContextForCatalogVersionsFinderStrategy;

	@Mock
	private DroolsRuleEngineContextModel context1;

	@Mock
	private DroolsRuleEngineContextModel context2;

	@Mock
	private RuleEngineContextFinderStrategy fallbackRuleEngineContextFinderStrategy;

	@Mock
	private RuleEngineService commerceRuleEngineService;

	@Mock
	private TimeService timeService;

	@Mock
	private DefaultRuleEngineContextFinderStrategy ruleEngineContextFinderStrategy;

	@InjectMocks
	private DefaultPromotionEngineService defaultPromotionEngineService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		when(ruleBasedPromotion.getPromotionDescription()).thenReturn(RULE_BASED_PROMOTION_DESC);
		when(abstractPromotion.getDescription()).thenReturn(NOT_RULE_BASED_PROMOTION_DESC);
		when(timeService.getCurrentTime()).thenReturn(new Date());
	}

	@Test
	public void testGetPromotionDescriptionRuleBasedPromotion()
	{
		final String result = defaultPromotionEngineService.getPromotionDescription(ruleBasedPromotion);
		assertEquals(RULE_BASED_PROMOTION_DESC, result);
	}

	@Test
	public void testGetPromotionDescriptionNotRuleBasedPromotion()
	{
		final String result = defaultPromotionEngineService.getPromotionDescription(abstractPromotion);
		assertEquals(NOT_RULE_BASED_PROMOTION_DESC, result);
	}

	@Test
	public void testEvaluationFailsWhenLessThanOneContextMappedIsFoundAndNoContextByRuleModuleFound()
	{

		final List<AbstractRuleEngineContextModel> contexts1 = new ArrayList<>();

		when(factContextFactory.createFactContext(Mockito.any(FactContextType.class), Mockito.anyCollection())).thenReturn(
				factContext);
		when(factContext.getFacts()).thenReturn(Collections.emptyList());
		when(ruleEngineContextForCatalogVersionsFinderStrategy.findRuleEngineContexts(Mockito.anyCollection(), Mockito.any()))
				.thenReturn(contexts1);
		when(fallbackRuleEngineContextFinderStrategy.findRuleEngineContext(Mockito.any())).thenReturn(Optional.empty());

		final AbstractOrderModel cart = CartModelBuilder.newCart("cart").addProduct("product", 1, 10d, 0).getModel();

		when(ruleEngineContextFinderStrategy.findRuleEngineContext(cart, RuleType.PROMOTION)).thenReturn(Optional.empty());

		final RuleEvaluationResult result = defaultPromotionEngineService.evaluate(cart, Collections.emptyList());
		assertTrue(result.isEvaluationFailed());
		assertNotNull(result.getErrorMessage());
		assertNull(result.getResult());
	}


	@Test
	public void testEvaluationPassWhenLessThanOneContextMappedIsFoundAndOneContextByRuleModuleFound()
	{

		final List<AbstractRuleEngineContextModel> contexts1 = new ArrayList<>();
		when(context1.getName()).thenReturn("context1");

		when(ruleEngineContextForCatalogVersionsFinderStrategy.findRuleEngineContexts(Mockito.anyCollection(), Mockito.any()))
				.thenReturn(contexts1);
		when(fallbackRuleEngineContextFinderStrategy.findRuleEngineContext(Mockito.any())).thenReturn(Optional.of(context1));

		final AbstractOrderModel cart = CartModelBuilder.newCart("cart").addProduct("product", 1, 10d, 0).getModel();
		when(ruleEngineContextFinderStrategy.findRuleEngineContext(cart, RuleType.PROMOTION)).thenReturn(Optional.of(context1));

		final AbstractRuleEngineContextModel context = defaultPromotionEngineService.determineRuleEngineContext(cart);
		assertEquals(context1, context);
	}

}
