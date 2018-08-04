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
angular.module('seNamespaceModule', [])

    .service('seNamespace', function($log) {

        this._namespaceHasFunction = function(propName) {
            var nsp = this._getNamespace();
            return (nsp && typeof nsp[propName] === 'function');
        };

        this._getNamespace = function() {
            return window.smartedit;
        };

        this.reprocessPage = function() {
            if (this._getNamespace() && typeof this._getNamespace().reprocessPage === 'function') {
                return this._getNamespace().reprocessPage();
            } else {
                $log.warn('No reprocessPage function defined on smartedit namespace');
            }
        };

        // explain slot for multiple instances of component scenario
        this.renderComponent = function(componentId, componentType, parentId) {
            if (this._getNamespace() && typeof this._getNamespace().renderComponent === 'function') {
                return this._getNamespace().renderComponent(componentId, componentType, parentId);
            } else {
                return false;
            }
        };


    });
