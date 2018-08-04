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
package com.hybris.yprofile.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hybris.yprofile.dto.*;
import com.hybris.yprofile.rest.clients.ProfileClient;
import com.hybris.yprofile.services.ProfileConfigurationService;
import com.hybris.yprofile.services.ProfileTransactionService;
import com.hybris.yprofile.services.RetrieveRestClientStrategy;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Implementation for {@link ProfileTransactionService}. Communication service to send transactions to Profile
 */
public class DefaultProfileTransactionService implements ProfileTransactionService {
    private static final Logger LOG = Logger.getLogger(DefaultProfileTransactionService.class);
    private static final String NULL = "null";
    private static final String ACCOUNT_REGISTRATION_EVENT_TYPE = "account-registration";
    private static final String LOGIN_EVENT_TYPE = "login";
    private static final String SUBMIT_ORDER_EVENT_TYPE = "profile-commerce-order";
    private static final String SHIPMENT_ORDER_EVENT_TYPE = "profile-commerce-shipment";
    private static final String RETURN_ORDER_EVENT_TYPE = "profile-commerce-return";

    private RetrieveRestClientStrategy retrieveRestClientStrategy;

    private ProfileConfigurationService profileConfigurationService;

    private Converter<OrderModel, Order> profileOrderEventConverter;

    private Converter<ConsignmentModel, Order> profileConsignmentEventConverter;

    private Converter<ReturnRequestModel, Order> profileReturnEventConverter;

    private Converter<UserModel, User> profileUserEventConverter;

    /**
     * Send order to yprofile.
     * @param orderModel the order model
     */
    @Override
    public void sendSubmitOrderEvent(final OrderModel orderModel) {
        final Order order = getProfileOrderEventConverter().convert(orderModel);

        this.sendOrder(orderModel.getConsentReference(), order, SUBMIT_ORDER_EVENT_TYPE);
    }

    /**
     * Send consignment to yprofile.
     * @param consignmentModel the consignment model
     */
    @Override
    public void sendConsignmentEvent(final ConsignmentModel consignmentModel) {

        final OrderModel orderModel = (OrderModel) consignmentModel.getOrder();
        final Order order = getProfileConsignmentEventConverter().convert(consignmentModel);

        this.sendOrder(orderModel.getConsentReference(), order, SHIPMENT_ORDER_EVENT_TYPE);
    }

    /**
     * Send return to yprofile.
     * @param returnRequestModel the return request
     */
    @Override
    public void sendReturnOrderEvent(final ReturnRequestModel returnRequestModel) {

        OrderModel orderModel = returnRequestModel.getOrder();

        final Order order = getProfileReturnEventConverter().convert(returnRequestModel);

        this.sendOrder(orderModel.getConsentReference(), order, RETURN_ORDER_EVENT_TYPE);
    }

    protected void sendOrder(final String consentReference, final Order order, final String eventType) {
        if (shouldSendEvent(consentReference, order)) {
            getClient().sendTransaction(eventType, consentReference, order)
                    .subscribe(response -> this.logSuccess(order),
                            error -> this.logError(error, order),
                            () -> this.logSuccess(order));
        }
    }

    protected boolean shouldSendEvent(final String consentReference, final Order order) {
        return !getProfileConfigurationService().isProfileTrackingPaused()
                && getProfileConfigurationService().isYaaSConfigurationPresentForBaseSiteId(order.getChannelRef())
                && isValidConsentReference(consentReference);
    }

    /**
     * Send user registration event to yprofile.
     * @param userModel the user model
     * @param consentReferenceId consent refrence
     * @param sessionId ec session id
     * @param storeName storefront name like 'electronics'
     */
    @Override
    public void sendUserRegistrationEvent(final UserModel userModel, final String consentReferenceId, final String sessionId, final String storeName) {
        this.sendUserEvent(userModel, consentReferenceId, sessionId, storeName, ACCOUNT_REGISTRATION_EVENT_TYPE);
    }

    /**
     * Send user login event to yprofile.
     * @param userModel the user model
     * @param consentReferenceId consent refrence
     * @param sessionId ec session id
     * @param storeName storefront name like 'electronics'
     */
    @Override
    public void sendLoginEvent(final UserModel userModel, final String consentReferenceId, final String sessionId, final String storeName) {
        this.sendUserEvent(userModel, consentReferenceId, sessionId, storeName, LOGIN_EVENT_TYPE);
    }

    protected void sendUserEvent(final UserModel userModel, final String consentReferenceId, final String sessionId, final String storeName, final String eventType) {
        User user = getProfileUserEventConverter().convert(userModel);
        user.setType(eventType);
        user.setSessionId(sessionId);
        user.setChannelRef(storeName);

        if (getProfileConfigurationService().isYaaSConfigurationPresentForBaseSiteId(storeName) && this.isValidConsentReference(consentReferenceId)) {
            getClient().sendTransaction(eventType, consentReferenceId, user)
                    .subscribe(response -> this.logSuccess(user),
                            error -> this.logError(error, user),
                            () -> this.logSuccess(user));
        }
    }

    protected static boolean isValidConsentReference(String consentReferenceId) {
        return StringUtils.isNotBlank(consentReferenceId) && !NULL.equals(consentReferenceId);
    }

    protected static void logSuccess(final Object obj) {
        if (LOG.isDebugEnabled()) {
            String event = parseEventToJson(obj);
            LOG.debug(event + " sent to yprofile ");
        }
    }

    protected static void logError(final Throwable error, final Object obj) {
        if (LOG.isDebugEnabled()) {
            String event = parseEventToJson(obj);
            LOG.debug(event + " sending to yprofile failed", error);
        }

        LOG.error("Error sending transaction to yprofile", error);
    }

    protected static String parseEventToJson(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        String event = obj.toString();
        try {
            event = mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            LOG.error("Encountered problem with json processing", e);
        }
        return event;
    }

    protected ProfileClient getClient(){
        return getRetrieveRestClientStrategy().getProfileRestClient();
    }

    public RetrieveRestClientStrategy getRetrieveRestClientStrategy() {
        return retrieveRestClientStrategy;
    }

    @Required
    public void setRetrieveRestClientStrategy(RetrieveRestClientStrategy retrieveRestClientStrategy) {
        this.retrieveRestClientStrategy = retrieveRestClientStrategy;
    }

    public ProfileConfigurationService getProfileConfigurationService() {
        return profileConfigurationService;
    }

    @Required
    public void setProfileConfigurationService(ProfileConfigurationService profileConfigurationService) {
        this.profileConfigurationService = profileConfigurationService;
    }

    public Converter<OrderModel, Order> getProfileOrderEventConverter() {
        return profileOrderEventConverter;
    }

    @Required
    public void setProfileOrderEventConverter(Converter<OrderModel, Order> profileOrderEventConverter) {
        this.profileOrderEventConverter = profileOrderEventConverter;
    }

    public Converter<ConsignmentModel, Order> getProfileConsignmentEventConverter() {
        return profileConsignmentEventConverter;
    }

    @Required
    public void setProfileConsignmentEventConverter(Converter<ConsignmentModel, Order> profileConsignmentEventConverter) {
        this.profileConsignmentEventConverter = profileConsignmentEventConverter;
    }

    public Converter<ReturnRequestModel, Order> getProfileReturnEventConverter() {
        return profileReturnEventConverter;
    }

    @Required
    public void setProfileReturnEventConverter(Converter<ReturnRequestModel, Order> profileReturnEventConverter) {
        this.profileReturnEventConverter = profileReturnEventConverter;
    }

    public Converter<UserModel, User> getProfileUserEventConverter() {
        return profileUserEventConverter;
    }

    @Required
    public void setProfileUserEventConverter(Converter<UserModel, User> profileUserEventConverter) {
        this.profileUserEventConverter = profileUserEventConverter;
    }
}