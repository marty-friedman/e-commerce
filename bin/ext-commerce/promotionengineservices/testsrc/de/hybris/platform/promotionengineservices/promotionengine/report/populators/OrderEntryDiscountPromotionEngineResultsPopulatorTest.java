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
package de.hybris.platform.promotionengineservices.promotionengine.report.populators;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.promotionengineservices.promotionengine.report.data.OrderEntryLevelPromotionEngineResults;
import de.hybris.platform.promotionengineservices.promotionengine.report.data.PromotionEngineResult;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.util.DiscountValue;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OrderEntryDiscountPromotionEngineResultsPopulatorTest
{
	@InjectMocks
	private OrderEntryDiscountPromotionEngineResultsPopulator populator;
	@Mock
	private Converter<DiscountValue, PromotionEngineResult> discountValuePromotionEngineResultConverter;
	@Mock
	private OrderEntryModel entry;
	private OrderEntryLevelPromotionEngineResults target;

	@Before
	public void setUp() throws Exception
	{
		target = new OrderEntryLevelPromotionEngineResults();

		given(entry.getQuantity()).willReturn(Long.valueOf(2l));
		given(entry.getBasePrice()).willReturn(Double.valueOf(10d));
		given(entry.getTotalPrice()).willReturn(Double.valueOf(17.05d));
	}

	@Test
	public void shouldPopulateAllGlobalDiscounts() throws Exception
	{
		//given
		final List<DiscountValue> discounts = newArrayList();
		given(entry.getDiscountValues()).willReturn(discounts);
		//when
		populator.populate(entry,target);
		//then
		verify(discountValuePromotionEngineResultConverter,times(1)).convertAll(discounts);
	}

	@Test
	public void shouldPopulateConvertedResults() throws Exception
	{
		//given
		final List<DiscountValue> discounts = newArrayList();
		given(entry.getDiscountValues()).willReturn(discounts);

		final List<PromotionEngineResult> results = newArrayList();
		given(discountValuePromotionEngineResultConverter.convertAll(discounts)).willReturn(results);
		//when
		populator.populate(entry,target);
		//then
		assertThat(target.getPromotionEngineResults()).isEqualTo(results);
	}

	@Test
	public void shouldPopulateOrderEntryReference() throws Exception
	{
		//when
		populator.populate(entry,target);
		//then
		assertThat(target.getOrderEntry()).isEqualTo(entry);
	}

	@Test
	public void shouldCalculateOrderEntryTotalPrice() throws Exception
	{
		//when
		populator.populate(entry,target);
		//then
		assertThat(target.getTotalPrice()).isEqualByComparingTo("20.00");
	}

	@Test
	public void shouldCalculateEstimatedAdjustedBasePrice() throws Exception
	{
		//when
		populator.populate(entry,target);
		//then
		assertThat(target.getEstimatedAdjustedBasePrice()).isEqualByComparingTo("8.525");
	}

	@Test
	public void shouldRaiseExceptionWhenSourceToPopulateIsNull() throws Exception
	{
		//when
		final Throwable throwable = catchThrowable(() -> populator.populate(null,target));
		//then
		assertThat(throwable).isInstanceOf(IllegalArgumentException.class).hasMessage("Source cannot be null");
	}

	@Test
	public void shouldRaiseExceptionWhenTargetIsNull() throws Exception
	{
		//when
		final Throwable throwable = catchThrowable(() -> populator.populate(entry,null));
		//then
		assertThat(throwable).isInstanceOf(IllegalArgumentException.class).hasMessage("Target cannot be null");
	}


}
