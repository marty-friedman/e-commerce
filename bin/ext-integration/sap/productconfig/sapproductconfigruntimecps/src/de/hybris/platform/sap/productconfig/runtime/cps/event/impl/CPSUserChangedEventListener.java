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
package de.hybris.platform.sap.productconfig.runtime.cps.event.impl;

import de.hybris.platform.sap.productconfig.runtime.cps.cache.CPSSessionCache;
import de.hybris.platform.servicelayer.event.events.AfterSessionUserChangeEvent;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;


/**
 * This bean listens to the {@link AfterSessionUserChangeEvent}. It will clear the pricing cache of the cps runtime
 * configuration engine for any configuration stored in the user session. This will ensure that pricing data is re-read
 * from the pricing client.
 */
public class CPSUserChangedEventListener extends AbstractEventListener<AfterSessionUserChangeEvent>
{

	@Override
	protected void onEvent(final AfterSessionUserChangeEvent evt)
	{
		getCPSSessionCache().purgePrices();
	}

	protected CPSSessionCache getCPSSessionCache()
	{
		throw new UnsupportedOperationException(
				"Please define in the spring configuration a <lookup-method> for getCPSSessionCache().");
	}

}
