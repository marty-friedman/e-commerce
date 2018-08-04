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
package de.hybris.platform.customercouponservices.strategies.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.customercouponservices.model.CustomerCouponModel;
import de.hybris.platform.util.Config;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * Unit test for {@link DefaultCouponCampaignURLGenerationStrategy}
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(
{ Config.class })
@UnitTest
public class DefaultGenerateCouponClaimingURLStrategyTest
{

	private static final String COUPON_ID = "test";
	private static final String URL_PREFIX_KEY = "coupon.claiming.url.prefix";
	private static final String URL_PREFIX = "/url/";
	private static final String DEFAULT_VAL = StringUtils.EMPTY;

	private DefaultCouponCampaignURLGenerationStrategy strategy;

	@Mock
	private CustomerCouponModel coupon;

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);
		PowerMockito.mockStatic(Config.class);

		strategy = new DefaultCouponCampaignURLGenerationStrategy();

		Mockito.when(coupon.getCouponId()).thenReturn(COUPON_ID);
		PowerMockito.when(Config.getString(URL_PREFIX_KEY, DEFAULT_VAL)).thenReturn(URL_PREFIX);
	}

	@Test
	public void testGenerate()
	{
		final String url = strategy.generate(coupon);
		Assert.assertEquals(URL_PREFIX + COUPON_ID, url);
	}

	@Test
	public void testGenerate_null_param()
	{
		final String url = strategy.generate(null);
		Assert.assertTrue(StringUtils.isBlank(url));
	}

	@Test
	public void testGenerate_null_prefix()
	{
		PowerMockito.when(Config.getString(URL_PREFIX_KEY, DEFAULT_VAL)).thenReturn(null);
		final String url = strategy.generate(coupon);
		Assert.assertTrue(StringUtils.isBlank(url));
	}

	@Test
	public void testGenerate_null_couponId()
	{
		Mockito.when(coupon.getCouponId()).thenReturn(null);
		final String url = strategy.generate(coupon);
		Assert.assertTrue(StringUtils.isBlank(url));
	}
}
