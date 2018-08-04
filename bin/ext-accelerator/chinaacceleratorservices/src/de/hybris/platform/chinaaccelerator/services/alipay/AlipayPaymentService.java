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
package de.hybris.platform.chinaaccelerator.services.alipay;

import de.hybris.platform.chinaaccelerator.services.alipay.AlipayEnums.AlipayTradeStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.PaymentService;

import java.util.Map;


public interface AlipayPaymentService extends PaymentService
{
	public boolean initiate(OrderModel order);

	public boolean handleResponse(OrderModel order, AlipayNotifyInfoData notifyData, Map<String, String> notifyDataMa,
			boolean isMobile);

	public boolean handleResponse(OrderModel orderModel, AlipayReturnData returnData);

	public boolean closeTrade(OrderModel orderModel);

	public AlipayTradeStatus checkTrade(OrderModel orderModel);

	public String getRequestUrl(OrderModel orderModel);

	public void saveErrorCallback(OrderModel orderModel, String errorCode);
}
