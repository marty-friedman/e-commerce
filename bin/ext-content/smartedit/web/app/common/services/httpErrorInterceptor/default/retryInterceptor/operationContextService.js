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
/**
 * @ngdoc overview
 * @name operationContextServiceModule
 * 
 * @description
 * This module provides the functionality to register a set of url with their associated operation contexts.
 */
angular.module('operationContextServiceModule', ['functionsModule'])
    /**
     * @ngdoc object
     * @name operationContextServiceModule.object:OPERATION_CONTEXT
     *
     * @description
     * Injectable angular constant<br/>
     * This object provides an enumeration of operation context for the application.
     */
    .constant('OPERATION_CONTEXT', {
        BACKGROUND_TASKS: 'Background Tasks',
        INTERACTIVE: 'Interactive',
        NON_INTERACTIVE: 'Non-Interactive',
        BATCH_OPERATIONS: 'Batch Operations',
        TOOLING: 'Tooling',
        CMS: 'CMS'
    })
    /**
     * @ngdoc service
     * @name operationContextServiceModule.service:operationContextService
     */
    .service('operationContextService', function(isBlank) {
        this._store = [];

        /**
         * @ngdoc method
         * @name operationContextServiceModule.service:operationContextService#register
         * @methodOf operationContextServiceModule.service:operationContextService
         * 
         * @description
         * Register a new url with it's associated operationContext.
         * 
         * @param {String} url The url that is associated to the operation context.
         * @param {String} operationContext The operation context name that is associated to the given url.
         * 
         * @return {Object} operationContextService The operationContextService service
         */
        this.register = function(url, operationContext) {
            if (typeof url !== 'string' || isBlank(url)) {
                throw new Error('operationContextService.register error: url is invalid');
            }
            if (typeof operationContext !== 'string' || isBlank(operationContext)) {
                throw new Error('operationContextService.register error: operationContext is invalid');
            }
            var regexIndex = this._store.findIndex(function(store) {
                return store.urlRegex.test(url) === true && store.operationContext === operationContext;
            });
            if (regexIndex !== -1) {
                return;
            }
            var urlRegex = new RegExp(url.replace(/\/:[^\/]*/g, '/.*'));
            this._store.push({
                urlRegex: urlRegex,
                operationContext: operationContext
            });
            return this;
        };

        /**
         * @ngdoc method
         * @name operationContextServiceModule.service:operationContextService#findOperationContext
         * @methodOf operationContextServiceModule.service:operationContextService
         * 
         * @description
         * Find the first matching operation context for the given url.
         * 
         * @param {String} url The request url.
         * 
         * @return {String} operationContext
         */
        this.findOperationContext = function(url) {
            var regexIndex = this._store.findIndex(function(store) {
                return store.urlRegex.test(url) === true;
            });
            return ~regexIndex ? this._store[regexIndex].operationContext : null;
        };
    });
