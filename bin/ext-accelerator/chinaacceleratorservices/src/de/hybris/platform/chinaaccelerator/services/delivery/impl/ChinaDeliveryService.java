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
package de.hybris.platform.chinaaccelerator.services.delivery.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.commerceservices.delivery.impl.DefaultDeliveryService;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.jalo.order.AbstractOrder;
import de.hybris.platform.jalo.order.delivery.DeliveryMode;
import de.hybris.platform.jalo.order.delivery.JaloDeliveryModeException;
import de.hybris.platform.util.PriceValue;

import org.apache.log4j.Logger;


/**
 *
 */
public class ChinaDeliveryService extends DefaultDeliveryService
{
	private static final Logger LOG = Logger.getLogger(DefaultDeliveryService.class);

	@Override
	public PriceValue getDeliveryCostForDeliveryModeAndAbstractOrder(final DeliveryModeModel deliveryMode,
			final AbstractOrderModel abstractOrder)
	{
		validateParameterNotNull(deliveryMode, "deliveryMode model cannot be null");
		validateParameterNotNull(abstractOrder, "abstractOrder model cannot be null");

		final DeliveryMode deliveryModeSource = getModelService().getSource(deliveryMode);
		try
		{
			final AbstractOrder abstractOrderSource = getModelService().getSource(abstractOrder);
			return deliveryModeSource.getCost(abstractOrderSource);
		}
		catch (final JaloDeliveryModeException e)
		{
			LOG.warn("Could not find deliveryCost for order ... skipping!");
			return new PriceValue(abstractOrder.getCurrency().getIsocode(), 0.0, abstractOrder.getNet());
		}
	}
}
