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
package de.hybris.platform.webservices;

import de.hybris.platform.servicelayer.event.events.AbstractWebserviceActionEvent;
import de.hybris.platform.servicelayer.event.events.AbstractWebserviceActionEvent.CRUD_METHOD;
import de.hybris.platform.servicelayer.event.events.AbstractWebserviceActionEvent.TRIGGER;


/**
 * Abstraction for a EventAction creation factory.
 */
public interface EventActionFactory
{
	AbstractWebserviceActionEvent createEventAction(AbstractResource resource, CRUD_METHOD method, TRIGGER trigger);
}
