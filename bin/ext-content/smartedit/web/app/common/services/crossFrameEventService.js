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
angular.module('crossFrameEventServiceModule', ['gatewayFactoryModule', 'eventServiceModule'])
    .constant('CROSS_FRAME_EVENT', 'CROSS_FRAME_EVENT')
    .factory('crossFrameEventServiceGateway', function(CROSS_FRAME_EVENT, gatewayFactory) {
        return gatewayFactory.createGateway(CROSS_FRAME_EVENT);
    })
    /**
     * @ngdoc service
     * @name crossFrameEventServiceModule.service:CrossFrameEventService
     *
     * @description
     * The Cross Frame Event Service is responsible for publishing and subscribing events within and between frames.
     * It uses {@link gatewayFactoryModule.gatewayFactory gatewayFactory} and {@link eventServiceModule.EventService EventService} to transmit events.
     * 
     */
    .factory('crossFrameEventService', function($q, systemEventService, crossFrameEventServiceGateway) {

        var CrossFrameEventService = function() {

            /**
             * @ngdoc method
             * @name crossFrameEventServiceModule.service:CrossFrameEventService#publish
             * @methodOf crossFrameEventServiceModule.service:CrossFrameEventService
             *
             * @description
             * Publishes an event within and across the gateway.
             *
             * The publish method is used to send events using {@link eventServiceModule.EventService#sendEvent sendEvent} of
             * {@link eventServiceModule.EventService EventService} and as well send the message across the gateway by using 
             * {@link gatewayFactoryModule.MessageGateway#publish publish} of the {@link gatewayFactoryModule.gatewayFactory gatewayFactory}.
             *
             * @param {String} eventId Event identifier
             * @param {String} data The event payload
             * @returns {Promise} Promise to resolve
             */
            this.publish = function(eventId, data) {
                return $q.all([systemEventService.sendAsynchEvent(eventId, data), crossFrameEventServiceGateway.publish(eventId, data)]);
            };

            /**
             * @ngdoc method
             * @name crossFrameEventServiceModule.service:CrossFrameEventService#subscribe
             * @methodOf crossFrameEventServiceModule.service:CrossFrameEventService
             *
             * @description
             * Subscribe to an event across both frames.
             *
             * The subscribe method is used to register for listening to events using registerEventHandler method of
             * {@link eventServiceModule.EventService EventService} and as well send the registration message across the gateway by using 
             * {@link gatewayFactoryModule.MessageGateway#subscribe subscribe} of the {@link gatewayFactoryModule.gatewayFactory gatewayFactory}.
             *
             * @param {String} eventId Event identifier
             * @param {Function} handler Callback function to be invoked
             * @returns {Function} The function to call in order to unsubscribe the event listening; this will unsubscribe both from the systemEventService and the crossFrameEventServiceGatway
             */
            this.subscribe = function(eventId, handler) {
                var systemEventServiceUnsubscribeFn = systemEventService.registerEventHandler(eventId, handler);
                var crossFrameEventServiceGatewayUnsubscribeFn = crossFrameEventServiceGateway.subscribe(eventId, handler);

                var unSubscribeFn = function() {
                    systemEventServiceUnsubscribeFn();
                    crossFrameEventServiceGatewayUnsubscribeFn();
                };

                return unSubscribeFn;
            };
        };
        return new CrossFrameEventService();
    });
