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
package de.hybris.platform.chinesepspwechatpayservices.order;

import de.hybris.platform.core.model.order.OrderModel;

import java.util.Optional;




/**
 * Provide method to get order model
 */
public interface WeChatPayOrderService
{
	/**
	 * Get OrderModel by OrderCode
	 *
	 * @param code
	 *           The order code of the wanted order
	 * @return OrderModel if found and empty option otherwise
	 */
	public Optional<OrderModel> getOrderByCode(final String code);

}
