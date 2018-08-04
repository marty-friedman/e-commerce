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
package com.hybris.ymkt.clickstream.listeners;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.eventtracking.model.events.AbstractTrackingEvent;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.hybris.ymkt.clickstream.services.ClickStreamService;
import com.hybris.ymkt.common.consent.YmktConsentService;
import com.hybris.ymkt.common.consent.impl.DefaultYmktConsentService;
import com.hybris.ymkt.common.user.UserContextService;


@UnitTest
public class ClickStreamListenerTest
{
	static final String ABSTRACT_TRACKING_EVENT = AbstractTrackingEvent.class.getSimpleName();
	static final Random RANDOM = new Random(System.currentTimeMillis());

	ClickStreamListener clickStreamListener = new ClickStreamListener();

	YmktConsentService ymktConsentService = new DefaultYmktConsentService();

	ClickStreamService clickStreamService = new ClickStreamService()
	{
		@Override
		public boolean sendEvents(final List<? extends AbstractTrackingEvent> events)
		{
			Assert.assertEquals(clickStreamListener.batchSize, events.size());
			sendEventsCalled = true;
			sendEventsCount.incrementAndGet();
			return true;
		}
	};

	AbstractTrackingEvent event = Mockito.mock(AbstractTrackingEvent.class);

	boolean sendEventsCalled;

	final AtomicInteger sendEventsCount = new AtomicInteger();

	UserContextService userContextService = new UserContextService()
	{
		@Override
		public String getUserId()
		{
			return Integer.toHexString(RANDOM.nextInt());
		}

		@Override
		public String getUserOrigin()
		{
			return "COOKIE_ID";
		}
	};

	@Before
	public void setUp() throws Exception
	{
		Mockito.when(event.getEventType()).thenReturn(ABSTRACT_TRACKING_EVENT);
		clickStreamListener.setClickStreamService(clickStreamService);
		clickStreamListener.setUserContextService(userContextService);
		clickStreamListener.setYmktConsentService(ymktConsentService);
		clickStreamListener.setAllowedEvents(Collections.singletonList(ABSTRACT_TRACKING_EVENT));
	}

	@After
	public void tearDown() throws Exception
	{
		//
	}

	@Test
	public void testOnEventAbstractTrackingEvent_BatchSize0()
	{
		clickStreamListener.setBatchSize(0);
		Assert.assertEquals(0, clickStreamListener.batchQueue.size());
		Assert.assertFalse(sendEventsCalled);
		clickStreamListener.onEvent(event);
		Assert.assertEquals(0, clickStreamListener.batchQueue.size());
		Assert.assertTrue(sendEventsCalled);
	}

	@Test
	public void testOnEventAbstractTrackingEvent_BatchSize1()
	{
		clickStreamListener.setBatchSize(1);
		clickStreamListener.onEvent(event);
		Assert.assertEquals(0, clickStreamListener.batchQueue.size());
		Assert.assertTrue(sendEventsCalled);
	}

	@Test
	public void testOnEventAbstractTrackingEvent_BatchSize5()
	{
		clickStreamListener.setBatchSize(5);
		clickStreamListener.onEvent(event);
		clickStreamListener.onEvent(event);
		clickStreamListener.onEvent(event);
		clickStreamListener.onEvent(event);
		Assert.assertEquals(4, clickStreamListener.batchQueue.size());
		Assert.assertFalse(sendEventsCalled);
		clickStreamListener.onEvent(event);
		Assert.assertTrue(sendEventsCalled);
		Assert.assertEquals(0, clickStreamListener.batchQueue.size());
	}

	//	@Test
	public void testOnEventAbstractTrackingEvent_Load() throws Exception
	{
		clickStreamListener.setBatchSize(5);
		final Runnable run = () -> clickStreamListener.onEvent(event);
		final Callable<Object> c = () -> Stream.generate(() -> run).limit(5000).peek(Runnable::run).count();
		final List<Callable<Object>> list = Stream.generate(() -> c).limit(50).collect(Collectors.toList());
		for (final Future<Object> f : Executors.newCachedThreadPool().invokeAll(list))
		{
			f.get();
		}
		Assert.assertEquals(5000 * 50 / clickStreamListener.batchSize, sendEventsCount.get());
	}

}
