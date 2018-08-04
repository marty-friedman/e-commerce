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
package de.hybris.platform.acceleratorservices.process.strategies.impl;

import java.util.Optional;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;

/**
 * Strategy to impersonate site and initialize session context from an instance of OrderProcessModel.
 */
public class OrderProcessContextStrategy extends AbstractOrderProcessContextStrategy
{
	@Override
	protected Optional<AbstractOrderModel> getOrderModel(final BusinessProcessModel businessProcessModel)
	{
		return Optional.of(businessProcessModel)
				.filter(businessProcess -> businessProcess instanceof OrderProcessModel)
				.map(businessProcess -> ((OrderProcessModel) businessProcess).getOrder());
	}
}
