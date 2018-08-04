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
package com.sap.hybris.sec.eventpublisher.listener;

import de.hybris.platform.core.PK;
import de.hybris.platform.tx.AfterSaveEvent;
import de.hybris.platform.tx.AfterSaveListener;

import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.sec.eventpublisher.handler.AfterSaveEventHandler;


/**
 *
 */
public class DefaultSECSaveListener implements AfterSaveListener
{

	private Map<Integer, AfterSaveEventHandler> handlers;

	@Override
	public void afterSave(final Collection<AfterSaveEvent> events)
	{
		for (final AfterSaveEvent event : events)
		{
			handleAfterSaveEvent(event);

		}
	}

	public void handleAfterSaveEvent(final AfterSaveEvent event)
	{
		final PK pk = event.getPk();
		final int typeCode = pk.getTypeCode();

		final AfterSaveEventHandler afterSaveEventHandler = handlers.get(Integer.valueOf(typeCode));
		if (afterSaveEventHandler != null)
		{
			afterSaveEventHandler.handleEvent(event);
		}

	}

	@Required
	public void setHandlers(final Map<Integer, AfterSaveEventHandler> handlers)
	{
		this.handlers = handlers;
	}

}
