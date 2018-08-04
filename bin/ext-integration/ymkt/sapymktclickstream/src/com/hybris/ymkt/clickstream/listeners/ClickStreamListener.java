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

import de.hybris.eventtracking.model.events.AbstractProductAwareTrackingEvent;
import de.hybris.eventtracking.model.events.AbstractTrackingEvent;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.ymkt.clickstream.services.ClickStreamService;
import com.hybris.ymkt.common.consent.YmktConsentService;
import com.hybris.ymkt.common.constants.SapymktcommonConstants;
import com.hybris.ymkt.common.user.UserContextService;


public class ClickStreamListener extends AbstractEventListener<AbstractTrackingEvent>
{
	private static final Logger LOG = LoggerFactory.getLogger(ClickStreamListener.class);

	protected final Set<String> allowedEvents = new HashSet<>();
	protected final ConcurrentLinkedQueue<AbstractTrackingEvent> batchQueue = new ConcurrentLinkedQueue<>();
	protected int batchSize = 1;
	protected ClickStreamService clickStreamService;
	protected FlexibleSearchService flexibleSearchService;
	protected UserContextService userContextService;
	protected YmktConsentService ymktConsentService;

	protected AbstractTrackingEvent enrich(final AbstractTrackingEvent event)
	{
		event.setYmktContactId(event.getPiwikId());
		event.setYmktContactIdOrigin(this.userContextService.getAnonymousUserOrigin());

		final String customerId = event.getUserId();
		if (customerId != null && !customerId.isEmpty())
		{
			try
			{
				final CustomerModel customer = new CustomerModel();
				customer.setCustomerID(customerId);
				final List<CustomerModel> customers = this.flexibleSearchService.getModelsByExample(customer);
				if (!customers.isEmpty())
				{
					event.setYmktContactId(customers.get(0).getCustomerID());
					event.setYmktContactIdOrigin(UserContextService.getOriginIdSapHybrisConsumer());
				}
			}
			catch (final SystemException e)
			{
				LOG.warn("Error reading customer ID {}", customerId, e);
			}
		}
		return event;
	}

	protected boolean filterByAllowedEvents(final AbstractTrackingEvent event)
	{
		final String eventType = event.getEventType();
		return this.allowedEvents.contains(eventType);
	}

	protected boolean filterByConsent(final AbstractTrackingEvent event)
	{
		final String customerId = event.getUserId();
		return this.ymktConsentService.getUserConsent(customerId, SapymktcommonConstants.PERSONALIZATION_CONSENT_ID);
	}

	protected boolean filterByProductEvents(final AbstractTrackingEvent event)
	{
		if (event instanceof AbstractProductAwareTrackingEvent)
		{
			// Product events must have a product ID
			final AbstractProductAwareTrackingEvent pEvent = (AbstractProductAwareTrackingEvent) event;
			final String productId = pEvent.getProductId();
			final boolean isValidProductEvent = productId != null && !productId.isEmpty();
			if (!isValidProductEvent)
			{
				LOG.warn("Invalid event='{}' because of an incorrect productId='{}'", event, productId);
			}
			return isValidProductEvent;
		}
		return true;
	}

	@Override
	protected void onEvent(final AbstractTrackingEvent event)
	{
		Optional.of(event) //
				.filter(this::filterByAllowedEvents) //
				.filter(this::filterByProductEvents) //
				.filter(this::filterByConsent) //
				.map(this::enrich) //
				.ifPresent(this.batchQueue::offer);
		this.prepareBatchEvents().ifPresent(this.clickStreamService::sendEvents);
	}

	protected Optional<List<AbstractTrackingEvent>> prepareBatchEvents()
	{
		if (this.batchQueue.size() < this.batchSize)
		{
			return Optional.empty();
		}

		synchronized (this.batchQueue)
		{
			if (this.batchQueue.size() < this.batchSize)
			{
				return Optional.empty();
			}
			return Optional.of(Stream.generate(this.batchQueue::poll).limit(this.batchSize).collect(Collectors.toList()));
		}
	}

	@Required
	public void setAllowedEvents(final List<String> allowedEvents)
	{
		LOG.debug("allowedEvents={}", allowedEvents);
		this.allowedEvents.clear();
		allowedEvents.stream().map(String::intern).forEach(this.allowedEvents::add);
	}

	@Required
	public void setBatchSize(final int batchSize)
	{
		LOG.debug("batchSize={}", batchSize);
		this.batchSize = batchSize < 1 ? 1 : batchSize;
	}

	@Required
	public void setClickStreamService(final ClickStreamService clickStreamService)
	{
		this.clickStreamService = Objects.requireNonNull(clickStreamService);
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = Objects.requireNonNull(flexibleSearchService);
	}

	@Required
	public void setUserContextService(final UserContextService userContextService)
	{
		this.userContextService = Objects.requireNonNull(userContextService);
	}

	@Required
	public void setYmktConsentService(final YmktConsentService ymktConsentService)
	{
		this.ymktConsentService = Objects.requireNonNull(ymktConsentService);
	}
}
