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
angular.module('gatewayProxyModule', ['gatewayFactoryModule'])
    /**
     * @ngdoc service
     * @name gatewayProxyModule.gatewayProxy
     *
     * @description
     * To seamlessly integrate the gateway factory between two services on different frames, you can use a gateway
     * proxy. The gateway proxy service simplifies using the gateway module by providing an API that registers an
     * instance of a service that requires a gateway for communication.
     *
     * This registration process automatically attaches listeners to each of the service's functions (turned into promises), allowing stub
     * instances to forward calls to these functions using an instance of a gateway from {@link
     * gatewayFactoryModule.gatewayFactory gatewayFactory}. Any function that has an empty body declared on the service is used
     * as a proxy function. It delegates a publish call to the gateway under the same function name, and wraps the result
     * of the call in a Promise.
     */
    .factory('gatewayProxy', function($log, $q, toPromise, isFunctionEmpty, isBlank, gatewayFactory) {

        var turnToProxy = function(fnName, service, gateway) {
            delete service[fnName];
            service[fnName] = function() {
                var args = Array.prototype.slice.call(arguments);
                return gateway.publish(fnName, {
                    arguments: args
                }).then(function(resolvedData) {
                    if (!isBlank(resolvedData)) {
                        delete resolvedData.$resolved;
                        delete resolvedData.$promise;
                    }
                    return resolvedData;
                }, function(error) {
                    if (error) {
                        $log.error("gatewayProxy - publish failed for gateway " + gateway.gatewayId + ", method " + fnName + " and arguments", args);
                    }
                    return $q.reject(error);
                });
            };
        };

        var onGatewayEvent = function(fnName, _service, eventId, data) {
            return _service[fnName].apply(_service, data.arguments);
        };

        /**
         * @ngdoc method
         * @name gatewayProxyModule.gatewayProxy#initForService
         * @methodOf gatewayProxyModule.gatewayProxy
         *
         * @description Mutates the given service into a proxied service.
         * You must provide a unique string gatewayId, in one of 2 ways.<br />
         * 1) Having a gatewayId property on the service provided<br />
         * OR<br />
         * 2) providing a gatewayId as 3rd param of this function<br />
         *
         * @param {Service} service Service to mutate into a proxied service.
         * @param {Array=} methodsSubset An explicit set of methods on which the gatewayProxy will trigger. Otherwise, by default all functions will be proxied. This is particularly useful to avoid inner methods being unnecessarily turned into promises.
         * @param {String=} gatewayId The gateway ID to use internaly for the proxy. If not provided, the service <strong>must<strong> have a gatewayId property.
         */
        var initForService = function(service, methodsSubset, gatewayId) {

            var gwId = gatewayId || service.gatewayId;

            if (!gwId) {
                $log.error('initForService() - service expected to have an associated gatewayId - methodsSubset:', methodsSubset && methodsSubset.length ? methodsSubset.join(',') : []);
                return null;
            }

            var gateway = gatewayFactory.createGateway(gwId);

            var loopedOver = methodsSubset;
            if (!loopedOver) {
                loopedOver = [];
                for (var key in service) {
                    if (typeof service[key] === "function" && !isNonProxiableMethod(key)) {
                        loopedOver.push(key);
                    }
                }
            }

            loopedOver.forEach(function(fnName) {
                if (typeof service[fnName] === 'function') {
                    if (isFunctionEmpty(service[fnName])) {
                        turnToProxy(fnName, service, gateway);
                    } else {
                        service[fnName] = toPromise(service[fnName], service);
                        gateway.subscribe(fnName, onGatewayEvent.bind(null, fnName, service));
                    }
                }
            });
        };

        var nonProxiableMethods = ['getMethodForVoid', 'getMethodForSingleInstance', 'getMethodForArray'];

        function isNonProxiableMethod(key) {
            return nonProxiableMethods.indexOf(key) > -1;
        }

        return {
            initForService: initForService
        };
    });
