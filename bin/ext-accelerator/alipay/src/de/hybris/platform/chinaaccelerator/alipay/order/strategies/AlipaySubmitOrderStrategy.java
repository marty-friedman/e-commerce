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
package de.hybris.platform.chinaaccelerator.alipay.order.strategies;

import de.hybris.platform.chinaaccelerator.services.alipay.AlipayPaymentService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.strategies.SubmitOrderStrategy;



public class AlipaySubmitOrderStrategy implements SubmitOrderStrategy
{
	private AlipayPaymentService alipayPaymentService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.order.strategies.SubmitOrderStrategy#submitOrder(de.hybris.platform.core.model.order.OrderModel
	 * )
	 */
	@Override
	public void submitOrder(final OrderModel order)
	{
		getAlipayPaymentService().initiate(order);
	}

	/**
	 * @return the alipayPaymentService
	 */
	public AlipayPaymentService getAlipayPaymentService()
	{
		return alipayPaymentService;
	}

	/**
	 * @param alipayPaymentService
	 *           the alipayPaymentService to set
	 */
	public void setAlipayPaymentService(final AlipayPaymentService alipayPaymentService)
	{
		this.alipayPaymentService = alipayPaymentService;
	}

}
