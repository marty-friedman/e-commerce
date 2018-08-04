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
package de.hybris.platform.adaptivesearch.strategies.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.adaptivesearch.context.AsSearchProfileContext;
import de.hybris.platform.adaptivesearch.data.AsSearchProfileResult;
import de.hybris.platform.adaptivesearch.data.AsSort;
import de.hybris.platform.adaptivesearch.enums.AsBoostItemsMergeMode;
import de.hybris.platform.adaptivesearch.enums.AsBoostRulesMergeMode;
import de.hybris.platform.adaptivesearch.enums.AsFacetsMergeMode;
import de.hybris.platform.adaptivesearch.enums.AsSortsMergeMode;
import de.hybris.platform.adaptivesearch.strategies.AsBoostItemsMergeStrategy;
import de.hybris.platform.adaptivesearch.strategies.AsBoostRulesMergeStrategy;
import de.hybris.platform.adaptivesearch.strategies.AsFacetsMergeStrategy;
import de.hybris.platform.adaptivesearch.strategies.AsMergeStrategyFactory;
import de.hybris.platform.adaptivesearch.strategies.AsSearchProfileResultFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;

import de.hybris.platform.adaptivesearch.strategies.AsSortsMergeStrategy;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultAsSearchProfileMergeStrategyTest
{
	@Mock
	private AsSearchProfileResultFactory asSearchProfileResultFactory;

	@Mock
	private AsMergeStrategyFactory asMergeStrategyFactory;

	@Mock
	private AsFacetsMergeStrategy asFacetsMergeStrategy1;

	@Mock
	private AsFacetsMergeStrategy asFacetsMergeStrategy2;

	@Mock
	private AsBoostItemsMergeStrategy asBoostItemsMergeStrategy1;

	@Mock
	private AsBoostItemsMergeStrategy asBoostItemsMergeStrategy2;

	@Mock
	private AsBoostRulesMergeStrategy asBoostRulesMergeStrategy1;

	@Mock
	private AsBoostRulesMergeStrategy asBoostRulesMergeStrategy2;

	@Mock
	private AsSortsMergeStrategy asSortsMergeStrategy1;

	@Mock
	private AsSortsMergeStrategy asSortsMergeStrategy2;

	@Mock
	private AsSearchProfileContext context;

	private DefaultAsMergeStrategy strategy;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		strategy = new DefaultAsMergeStrategy();
		strategy.setAsSearchProfileResultFactory(asSearchProfileResultFactory);
		strategy.setAsMergeStrategyFactory(asMergeStrategyFactory);

	}

	protected AsSearchProfileResult createResult()
	{
		final AsSearchProfileResult result = new AsSearchProfileResult();
		result.setFacetsMergeMode(AsFacetsMergeMode.ADD_AFTER);
		result.setPromotedFacets(new LinkedHashMap<>());
		result.setFacets(new LinkedHashMap<>());
		result.setExcludedFacets(new LinkedHashMap<>());
		result.setBoostItemsMergeMode(AsBoostItemsMergeMode.ADD_AFTER);
		result.setPromotedItems(new LinkedHashMap<>());
		result.setExcludedItems(new LinkedHashMap<>());
		result.setBoostRulesMergeMode(AsBoostRulesMergeMode.ADD);
		result.setBoostRules(new ArrayList<>());
		result.setSortsMergeMode(AsSortsMergeMode.ADD_AFTER);
		result.setSorts(new LinkedHashMap<>());
		return result;
	}

	@Test
	public void mergeEmpty()
	{
		//given
		final AsSearchProfileResult expectedResult = createResult();

		when(asSearchProfileResultFactory.createResult()).thenReturn(expectedResult);

		//when
		final AsSearchProfileResult result = strategy.merge(context, Collections.emptyList(), null);

		//then
		assertSame(expectedResult, result);
	}

	@Test
	public void mergeSingleResult()
	{
		//given
		final AsSearchProfileResult expectedResult = new AsSearchProfileResult();

		//when
		final AsSearchProfileResult result = strategy.merge(context, Collections.singletonList(expectedResult), null);

		//then
		assertSame(expectedResult, result);
		verify(asSearchProfileResultFactory, never()).createResult();
		verify(asFacetsMergeStrategy1, never()).mergeFacets(Mockito.any(), Mockito.any());
		verify(asBoostItemsMergeStrategy1, never()).mergeBoostItems(Mockito.any(), Mockito.any());
		verify(asBoostRulesMergeStrategy1, never()).mergeBoostRules(Mockito.any(), Mockito.any());
		verify(asSortsMergeStrategy1, never()).mergeSorts(Mockito.any(), Mockito.any());
	}

	@Test
	public void mergeMultipleResults()
	{
		//given
		final AsSearchProfileResult result1 = createResult();
		final AsSearchProfileResult result2 = createResult();
		final AsSearchProfileResult expectedResult = createResult();

		when(asSearchProfileResultFactory.createResult()).thenReturn(expectedResult);
		when(asMergeStrategyFactory.getFacetsMergeStrategy(AsFacetsMergeMode.ADD_AFTER)).thenReturn(asFacetsMergeStrategy1);
		when(asMergeStrategyFactory.getBoostItemsMergeStrategy(AsBoostItemsMergeMode.ADD_AFTER))
				.thenReturn(asBoostItemsMergeStrategy1);
		when(asMergeStrategyFactory.getBoostRulesMergeStrategy(AsBoostRulesMergeMode.ADD)).thenReturn(asBoostRulesMergeStrategy1);
		when(asMergeStrategyFactory.getSortsMergeStrategy(AsSortsMergeMode.ADD_AFTER)).thenReturn(asSortsMergeStrategy1);

		//when
		final AsSearchProfileResult result = strategy.merge(context, Arrays.asList(result1, result2), null);

		//then
		assertEquals(expectedResult, result);
		verify(asFacetsMergeStrategy1).mergeFacets(result1, expectedResult);
		verify(asFacetsMergeStrategy1).mergeFacets(result2, expectedResult);
		verify(asBoostItemsMergeStrategy1).mergeBoostItems(result1, expectedResult);
		verify(asBoostItemsMergeStrategy1).mergeBoostItems(result2, expectedResult);
		verify(asBoostRulesMergeStrategy1).mergeBoostRules(result1, expectedResult);
		verify(asBoostRulesMergeStrategy1).mergeBoostRules(result2, expectedResult);
		verify(asSortsMergeStrategy1).mergeSorts(result1, expectedResult);
		verify(asSortsMergeStrategy1).mergeSorts(result2, expectedResult);
	}

	@Test
	public void mergeMultipleResultsDifferentStrategies()
	{
		//given
		final AsSearchProfileResult result1 = createResult();
		result1.setFacetsMergeMode(AsFacetsMergeMode.REPLACE);
		result1.setBoostItemsMergeMode(AsBoostItemsMergeMode.REPLACE);
		result1.setBoostRulesMergeMode(AsBoostRulesMergeMode.REPLACE);
		result1.setSortsMergeMode(AsSortsMergeMode.REPLACE);

		final AsSearchProfileResult result2 = createResult();
		result2.setFacetsMergeMode(AsFacetsMergeMode.ADD_BEFORE);
		result2.setBoostItemsMergeMode(AsBoostItemsMergeMode.ADD_BEFORE);
		result2.setBoostRulesMergeMode(AsBoostRulesMergeMode.ADD);
		result2.setSortsMergeMode(AsSortsMergeMode.ADD_BEFORE);

		final AsSearchProfileResult expectedResult = createResult();

		when(asSearchProfileResultFactory.createResult()).thenReturn(expectedResult);
		when(asMergeStrategyFactory.getFacetsMergeStrategy(AsFacetsMergeMode.REPLACE)).thenReturn(asFacetsMergeStrategy1);
		when(asMergeStrategyFactory.getBoostItemsMergeStrategy(AsBoostItemsMergeMode.REPLACE))
				.thenReturn(asBoostItemsMergeStrategy1);
		when(asMergeStrategyFactory.getBoostRulesMergeStrategy(AsBoostRulesMergeMode.REPLACE))
				.thenReturn(asBoostRulesMergeStrategy1);
		when(asMergeStrategyFactory.getSortsMergeStrategy(AsSortsMergeMode.REPLACE))
				.thenReturn(asSortsMergeStrategy1);
		when(asMergeStrategyFactory.getFacetsMergeStrategy(AsFacetsMergeMode.ADD_BEFORE)).thenReturn(asFacetsMergeStrategy2);
		when(asMergeStrategyFactory.getBoostItemsMergeStrategy(AsBoostItemsMergeMode.ADD_BEFORE))
				.thenReturn(asBoostItemsMergeStrategy2);
		when(asMergeStrategyFactory.getBoostRulesMergeStrategy(AsBoostRulesMergeMode.ADD)).thenReturn(asBoostRulesMergeStrategy2);
		when(asMergeStrategyFactory.getSortsMergeStrategy(AsSortsMergeMode.ADD_BEFORE))
				.thenReturn(asSortsMergeStrategy2);

		//when
		final AsSearchProfileResult result = strategy.merge(context, Arrays.asList(result1, result2), null);

		//then
		assertEquals(expectedResult, result);
		assertEquals(expectedResult.getFacetsMergeMode(), AsFacetsMergeMode.REPLACE);
		assertEquals(expectedResult.getBoostItemsMergeMode(), AsBoostItemsMergeMode.REPLACE);
		assertEquals(expectedResult.getSortsMergeMode(), AsSortsMergeMode.REPLACE);
		assertEquals(expectedResult, result);
		verify(asFacetsMergeStrategy1).mergeFacets(result1, expectedResult);
		verify(asBoostRulesMergeStrategy1).mergeBoostRules(result1, expectedResult);
		verify(asSortsMergeStrategy1).mergeSorts(result1, expectedResult);
		verify(asFacetsMergeStrategy2).mergeFacets(result2, expectedResult);
		verify(asBoostRulesMergeStrategy2).mergeBoostRules(result2, expectedResult);
		verify(asSortsMergeStrategy2).mergeSorts(result2, expectedResult);
	}
}
