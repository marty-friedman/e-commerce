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
package de.hybris.platform.sap.productconfig.services.tracking.impl;

import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.tracking.EventType;
import de.hybris.platform.sap.productconfig.services.tracking.RecorderParameters;
import de.hybris.platform.sap.productconfig.services.tracking.TrackingItem;
import de.hybris.platform.sap.productconfig.services.tracking.TrackingItemKey;
import de.hybris.platform.sap.productconfig.services.tracking.TrackingWriter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Base class for CPQ Tracking recorders.<br>
 * Every tracking recorder should call the same list of writers for processing of tracking items.
 */
public class AbstractTrackingRecorderImpl
{

	private SessionAccessService sessionAccessService;
	private List<TrackingWriter> writers;
	private boolean trackingEnabled = true;


	protected TrackingItem createTrackingItem(final String configId, final EventType event, final RecorderParameters parameter,
			final String parameterValue)
	{
		final TrackingItem item = new TrackingItem();
		final TrackingItemKey itemKey = fillItemKey(configId, event);
		item.setTrackingItemKey(itemKey);
		item.setParameters(new HashMap<>());
		item.getParameters().put(parameter.toString(), parameterValue);
		return item;
	}

	protected TrackingItemKey fillItemKey(final String configId, final EventType event)
	{
		final TrackingItemKey itemKey = new TrackingItemKey();
		itemKey.setEventType(event);
		itemKey.setTimestamp(LocalDateTime.now());
		final String sessionId = sessionAccessService.getSessionId();
		itemKey.setSessionId(DigestUtils.sha256Hex(sessionId));
		itemKey.setConfigId(DigestUtils.sha256Hex(configId));
		return itemKey;
	}

	protected void notifyWriter(final TrackingItem item)
	{
		for (final TrackingWriter writer : writers)
		{
			writer.trackingItemCreated(item);
		}
	}

	protected SessionAccessService getSessionAccessService()
	{
		return sessionAccessService;
	}

	/**
	 * @param sessionAccessService
	 *           injects the session access service
	 */
	@Required
	public void setSessionAccessService(final SessionAccessService sessionAccessService)
	{
		this.sessionAccessService = sessionAccessService;
	}

	protected List<TrackingWriter> getWriters()
	{
		return writers;
	}

	/**
	 * @param writers
	 *           injects a list of {@link TrackingWriter}, that are called back when a CPQ tracking item is created
	 */
	@Required
	public void setWriters(final List<TrackingWriter> writers)
	{
		this.writers = writers;
	}

	protected boolean isTrackingEnabled()
	{
		return trackingEnabled;
	}

	/**
	 * @param trackingEnabled
	 *           only if <code>true</code> CPQ tracking is enabled, default is <code>true</code>
	 */
	public void setTrackingEnabled(final boolean trackingEnabled)
	{
		this.trackingEnabled = trackingEnabled;
	}

}
