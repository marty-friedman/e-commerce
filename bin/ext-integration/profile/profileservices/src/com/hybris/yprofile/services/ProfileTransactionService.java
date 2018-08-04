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
package com.hybris.yprofile.services;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.returns.model.ReturnRequestModel;

/**
 * ProfileTransactionService Interface. Communication service to send transactions to Profile
 */
public interface ProfileTransactionService {

    /**
     * Sends order to Profile.
     * @param orderModel the order model
     */
    void sendSubmitOrderEvent(final OrderModel orderModel);

    /**
     * Sends consignment to Profile.
     * @param consignmentModel the consignment model
     */
    void sendConsignmentEvent(final ConsignmentModel consignmentModel);

    /**
     * Sends return order to Profile.
     * @param returnRequestModel the return model
     */
    void sendReturnOrderEvent(final ReturnRequestModel returnRequestModel);

    /**
     * Sends user registration event to Profile.
     * @param userModel the user model
     * @param consentReferenceId consent refrence
     * @param sessionId ec session id
     * @param storeName storefront name like 'electronics'
     */
    void sendUserRegistrationEvent(final UserModel userModel, final String consentReferenceId, final String sessionId, final String storeName);

    /**
     * Sends user login event to Profile.
     * @param userModel the user model
     * @param consentReferenceId consent refrence
     * @param sessionId ec session id
     * @param storeName storefront name like 'electronics'
     */
    void sendLoginEvent(final UserModel userModel, final String consentReferenceId, final String sessionId, final String storeName);
}
