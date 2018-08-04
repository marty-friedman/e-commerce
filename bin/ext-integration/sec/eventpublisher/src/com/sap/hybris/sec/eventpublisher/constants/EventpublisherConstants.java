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
package com.sap.hybris.sec.eventpublisher.constants;

/**
 * Global class for all Eventpublisher constants. You can add global constants for your extension into this class.
 */
public final class EventpublisherConstants extends GeneratedEventpublisherConstants
{

	public static final String PLATFORM_LOGO_CODE = "EventPublisherPlatformLogo";

	public static final String MIXINS = "mixin:*";
	public static final String HYBRIS_CUSTOMER_MIXIN = "mixins.additionalCode.hybrisCustomerId:";
	public static final String QUOTES = "\"";
	public static final String HYBRIS_CUSTOMER_ADDRESS_MIXIN = "mixins.additionalCode.hybrisCustomerAddressId:";
	public static final String HYBRIS_CUSTOMERADDRESS_ID = "hybrisCustomerAddressId";
	public static final String ADDITIONAL_CODE = "additionalCode";
	public static final String SCHEMA_ATTRIBUTE = "sap.secintegration.schema.attribute";

	public static final String SEC_ENDPOINT = "sap.secintegration.endpoint";
	public static final String BASE_URL = "sap.secintegration.hci.baseurl";

	public static final String USERNAME = "sap.secintegration.hci.username";
	public static final String PASSWORD = "sap.secintegration.hci.password";

	public static final String HCI_PROJECT_PATH = "sap.secintegration.hci.project.path";
	public static final String CUSTOMER_PATH = "sap.secintegration.hci.customer.path";
	public static final String ADDRESS_PATH = "sap.secintegration.hci.customer.address.path";
	public static final String ORDER_PATH = "sap.secintegration.hci.order.path";

	public static final String SHIPPING_ADDRESS = "shippingAddress";
	public static final String BILLING_ADDRESS = "billingAddress";

	public static final String YAAS_URL = "sap.secintegration.schema.yaasurl";

	public static final String YAAS_TENANAT = "sap.secintegration.tenant";
	public static final String YAAS_CUSTOMER_SCHEMA = "sap.secintegration.customer.schema";
	public static final String YAAS_CUSTOMER_ADDRESS_SCHEMA = "sap.secintegration.customer.address.schema";
	public static final String BACKSLASH = "/";
	public static final String YAAS_ATTRIBUTE_CUSTOMER_ADDRESS_ID = "sap.secintegration.customer.addres.attribute.id";
	public static final String YAAS_ATTRIBUTE_CUSTOMER_ID = "sap.secintegration.customer.attribute.id";
	public static final String YAAS_ATTRIBUTE_CUSTOMER_UID = "sap.secintegration.customer.attribute.uid";

	public static final String HCI_PROXY_URL = "sap.secintegration.hci.proxy.url";

	public static final String BD_TYPE = "sap.secintegration.bdtype";
	public static final String DATE_FORMATTER_TYPE = "sap.secintegration.dateformat.type";

	public static final int CUSTOMER_MODEL_TYPECODE = 4;
	public static final int ADDRESS_MODEL_TYPECODE = 23;
	public static final int ORDER_MODEL_TYPECODE = 45;
	public static final String CODE_CONSTANT = "code";
	public static final String PORT_CONSTANT = "port";

	public static final String WEBSOCKET_SERVER_ENDPOINT_INTERNALCONTEXT = "internalcontext";
	public static final String WEBSOCKET_SERVER_ENDPOINT_CLIENTCONTEXT = "clientcontext";
	public static final String WEBSOCKET_SERVER_ENDPOINT_PATH = "/hybrisCustomer/hybrisAgent";
	public static final String WEBSOCKET_SERVER_ENDPOINT_BASE_URL = "ws://localhost:{" + PORT_CONSTANT
			+ "}/eventpublisher/ordersocket/";
	public static final String WEBSOCKET_SERVER_ENDPOINT_PORT = "tomcat.http.port";
	public static final String WEBSOCKET_SERVER_ENDPOINT_HOST_INTERNAL = "sap.secintegration.internal.server.host";
	public static final String ORDER_MODIFICATION_TYPE = "create";

	public static final String HCI_PUBLICATION_STATUS_OK = "200";
	public static final String HCI_PUBLICATION_STATUS_CREATED = "201";
	public static final String ORDER_ID = "id";
	public static final String ORDER_CREATED = "order-created";
	public static final String ORDER_UPDATED = "order-updated";
	public static final String WEBSOCKET_SERVER_ENDPOINT_ORDERS="orders";
	public static final String WEBSOCKET_MESSAGE_SOURCE="sap.secintegration.websocket.msgsource";
	public static final String WEBSOCKET_CURRENT_METHOD="sap.secintegration.websocket.current.method";
	public static final String WEBSOCKET_NEXT_METHOD="sap.secintegration.websocket.next.method";
	public static final String  WEBSOCKET_RESPONSE_IS_NEW_OBJECT="sap.secintegration.websocket.new.object";
	public static final int WEBSOCKET_RESPONSE_OK_VALUE=0;
	
	

	public static final String SSO_COOKIE_NAME = "sso.cookie.name";

	public static final String AFTER_ORDER_SAVE_EVENT_HANDLER = "afterOrderSaveEventHandler";

	private EventpublisherConstants()
	{
		//empty to avoid instantiating this constant class
	}


}
