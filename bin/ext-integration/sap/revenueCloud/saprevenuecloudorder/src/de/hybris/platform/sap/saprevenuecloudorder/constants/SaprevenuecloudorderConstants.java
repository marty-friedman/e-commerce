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
package de.hybris.platform.sap.saprevenuecloudorder.constants;

import java.time.format.DateTimeFormatter;


@SuppressWarnings(
{ "deprecation" })
public class SaprevenuecloudorderConstants extends GeneratedSaprevenuecloudorderConstants
{
	public static final String EXTENSIONNAME = "saprevenuecloudorder";
	public static final String SAP_SUBSCRIPTION_CONFIRMATION_EVENT = "SapSubscriptionConfirmationEvent-";
	public static final String SAP_SUBSCRIPTION_SUBPROCESS = "sap-subscription-process";
	public static final String SUBSCRIPTION_ORDER_PATH_URL = "subscription.order.path.url";
	public static final String SUBSCRIPTIONITEM = "subscriptionItem";
	public static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	public static final DateTimeFormatter ISO8601_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
	public static final String UTC = "UTC";
	public static final String DEFAULT_TIMEZONE = "saprevenuecloud.subscription.default.timezone";
	public static final String DEFAULT_PAYMENT_TYPE = "saprevenuecloud.subscription.default.paymenttype";
	public static final String YYYY_MM_DD = "yyyy-MM-dd";
	public static final String CANCELLATION_REASON = "saprevenuecloud.subscription.cancellation.reason";

	private SaprevenuecloudorderConstants()
	{
		//empty
	}


}
