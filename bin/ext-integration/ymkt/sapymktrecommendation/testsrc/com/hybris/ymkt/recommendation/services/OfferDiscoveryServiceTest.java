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
package com.hybris.ymkt.recommendation.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.hybris.ymkt.common.user.UserContextService;
import com.hybris.ymkt.recommendation.dao.OfferRecommendationContext;
import com.hybris.ymkt.recommendation.dao.OfferRecommendationScenario;


@UnitTest
public class OfferDiscoveryServiceTest
{
	@Mock
	private CartService cartService;

	@Mock
	private static OfferDiscoveryService offerDiscoveryService;

	@Mock
	private static UserContextService userContextService;

	@Mock
	private static UserService userService;

	@Mock
	private CommonI18NService commonI18NService;

	@Mock
	private LanguageModel languageModel;

	@Mock
	private RecentViewedItemsService recentViewedItemsService;

	private static final String CART_ITEM_DS_TYPE = "CUAN_PRODUCT";
	private static final String LEADING_ITEM_DS_TYPE = "CUAN_PRODUCT";
	private static final String CONTENT_POSITION = "Home";
	private static final String LEADING_ITEM_TYPE = "P";
	private static final String LEADING_PRODUCT_ID = "12345678";
	private static final String RECOMMENDATION_SCENARIO_ID = "PHX_OFFER_SCENARIO_2";
	private static final String LANGUAGE = "EN";

	protected static final String APPLICATION_JSON = "application/json";
	protected static final String RECOMMENDATIONS = "Recommendations";
	protected static final String COMMUNICATION_MEDIUM = "ONLINE_SHOP";
	protected static final String CONTEXT_PARAM_COMMUNICATION_MEDIUM = "P_COMM_MEDIUM";
	protected static final String CONTEXT_PARAM_LANGUAGE = "P_LANGUAGE";
	protected static final String CONTEXT_PARAM_POSITION = "P_POSITION";

	OfferRecommendationContext offerRecommendationContext = new OfferRecommendationContext();
	OfferRecommendationContext context = new OfferRecommendationContext();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		context.setCartItemDSType(CART_ITEM_DS_TYPE);
		context.setContentPosition(CONTENT_POSITION);
		context.setLeadingCategoryIds(Collections.emptyList());
		context.setLeadingItemDSType(LEADING_ITEM_DS_TYPE);
		context.setLeadingItemType(LEADING_ITEM_TYPE);
		context.setLeadingProductId(LEADING_PRODUCT_ID);
		context.setRecommendationScenarioId(RECOMMENDATION_SCENARIO_ID);

		Mockito.doCallRealMethod().when(offerDiscoveryService).setUserContextService(Mockito.any(UserContextService.class));
		Mockito.doCallRealMethod().when(offerDiscoveryService).setCommonI18NService(Mockito.any(CommonI18NService.class));
		Mockito.doCallRealMethod().when(offerDiscoveryService)
				.setRecentViewedItemsService(Mockito.any(RecentViewedItemsService.class));

		Mockito.when(userContextService.getUserId()).thenReturn("6de4ae57e795a737");
		Mockito.when(userContextService.getUserOrigin()).thenReturn("COOKIE_ID");
		Mockito.when(commonI18NService.getCurrentLanguage()).thenReturn(languageModel);
		Mockito.when(languageModel.getIsocode()).thenReturn(LANGUAGE);

		List<String> recentViewedItems = new ArrayList<>();
		recentViewedItems.add("recentViewedItem");
		Mockito.when(this.recentViewedItemsService.getRecentViewedProducts()).thenReturn(recentViewedItems);

		List<String> cartItems = new ArrayList<>();
		cartItems.add("cartItem");
		Mockito.doReturn(cartItems).when(offerDiscoveryService).getCartItemsFromSession();
		Mockito.when(offerDiscoveryService.createOfferRecommendationScenario(context)).thenCallRealMethod();

		offerDiscoveryService.setCommonI18NService(commonI18NService);
		offerDiscoveryService.setCartService(cartService);
		offerDiscoveryService.setUserContextService(userContextService);
		offerDiscoveryService.setRecentViewedItemsService(recentViewedItemsService);
		userContextService.setUserService(userService);
	}

	/**
	 * Testing the includeCart() = false logic
	 */
	@Test
	public void createOfferRecommendationScenarioTestWhenIncludeCartIsFalse()
	{
		context.setIncludeCart(false);
		OfferRecommendationScenario offerRecoScenario = offerDiscoveryService.createOfferRecommendationScenario(context);
		assertEquals(1, offerRecoScenario.getLeadingObjects().size());

		//validates the entries in the leadingObjects
		assertTrue(offerRecoScenario.getLeadingObjects().stream().allMatch(i -> i.getLeadingObjectId().equals(LEADING_PRODUCT_ID)));
	}

	/**
	 * Testing the includeCart() = true logic
	 */
	@Test
	public void createOfferRecommendationScenarioTestWhenIncludeCartIsTrue()
	{
		context.setIncludeCart(true);
		OfferRecommendationScenario offerRecoScenario = offerDiscoveryService.createOfferRecommendationScenario(context);
		assertTrue(context.isIncludeCart());
		assertEquals(2, offerRecoScenario.getLeadingObjects().size());

		//validates the entries in the leadingObjects
		assertTrue(offerRecoScenario.getLeadingObjects().stream()
				.anyMatch(i -> i.getLeadingObjectId().equals("cartItem") || i.getLeadingObjectId().equals(LEADING_PRODUCT_ID)));
	}

	@Test
	public void createOfferRecommendationScenarioTestWhenCartItemDSTypeIsNotBlank()
	{
		OfferRecommendationScenario offerRecoScenario = offerDiscoveryService.createOfferRecommendationScenario(context);
		assertTrue(StringUtils.isNotBlank(context.getCartItemDSType()));
		assertEquals(1, offerRecoScenario.getBasketObjects().size());

		//validates the entries in the leadingObjects
		assertTrue(offerRecoScenario.getBasketObjects().stream()
				.anyMatch(i -> i.getBasketObjectId().equals("cartItem") || i.getBasketObjectType().equals(CART_ITEM_DS_TYPE)));
	}

	@Test
	public void createOfferRecommendationScenarioTestWhenCartItemDSTypeIsBlank()
	{
		context.setCartItemDSType("");
		OfferRecommendationScenario offerRecoScenario = offerDiscoveryService.createOfferRecommendationScenario(context);

		assertTrue(StringUtils.isBlank(context.getCartItemDSType()));
		assertEquals(0, offerRecoScenario.getBasketObjects().size());
	}

	@Test
	public void createOfferRecommendationScenarioTestWhenIncludeRecentIsTrue()
	{
		context.setIncludeRecent(true);
		assertTrue(context.isIncludeRecent());
		OfferRecommendationScenario offerRecoScenario = offerDiscoveryService.createOfferRecommendationScenario(context);

		assertEquals(2, offerRecoScenario.getLeadingObjects().size());

		//validates the entries in the leadingObjects
		assertTrue(offerRecoScenario.getLeadingObjects().stream().anyMatch(
				i -> i.getLeadingObjectId().equals("recentViewedItem") || i.getLeadingObjectId().equals(LEADING_PRODUCT_ID)));
	}

	@Test
	public void createOfferRecommendationScenarioTestWhenIncludeRecentIsFalse()
	{
		assertFalse(context.isIncludeRecent());
		OfferRecommendationScenario offerRecoScenario = offerDiscoveryService.createOfferRecommendationScenario(context);

		assertEquals(1, offerRecoScenario.getLeadingObjects().size());

		//validates the entries in the leadingObjects
		assertTrue(offerRecoScenario.getLeadingObjects().stream().anyMatch(i -> i.getLeadingObjectId().equals(LEADING_PRODUCT_ID)));
	}

	/**
	 * Validates the context params creation
	 */
	@Test
	public void createOfferRecommendationScenarioTestContextParams()
	{
		OfferRecommendationScenario offerRecoScenario = offerDiscoveryService.createOfferRecommendationScenario(context);

		assertEquals(3, offerRecoScenario.getContextParams().size());
		assertEquals(1, offerRecoScenario.getContextParams().get(0).getContextId());
		assertEquals(CONTEXT_PARAM_COMMUNICATION_MEDIUM, offerRecoScenario.getContextParams().get(0).getName());
		assertEquals(COMMUNICATION_MEDIUM, offerRecoScenario.getContextParams().get(0).getValue());

		assertEquals(2, offerRecoScenario.getContextParams().get(1).getContextId());
		assertEquals(CONTEXT_PARAM_LANGUAGE, offerRecoScenario.getContextParams().get(1).getName());
		assertEquals(LANGUAGE, offerRecoScenario.getContextParams().get(1).getValue());

		assertEquals(3, offerRecoScenario.getContextParams().get(2).getContextId());
		assertEquals(CONTEXT_PARAM_POSITION, offerRecoScenario.getContextParams().get(2).getName());
		assertEquals(CONTENT_POSITION, offerRecoScenario.getContextParams().get(2).getValue());
	}

}
