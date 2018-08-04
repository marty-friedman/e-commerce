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
angular.module('permissionServiceModule', ['permissionServiceInterfaceModule', 'gatewayProxyModule'])
    .factory('permissionService', function($log, extend, PermissionServiceInterface, SE_PERMISSION_SERVICE_GATEWAY_ID, gatewayProxy) {
        var PermissionService = function() {
            gatewayProxy.initForService(this, null, SE_PERMISSION_SERVICE_GATEWAY_ID);
        };

        PermissionService = extend(PermissionServiceInterface, PermissionService);

        PermissionService.prototype._remoteCallRuleVerify = function(ruleKey, permissionNameObjs) {
            if (this.ruleVerifyFunctions && this.ruleVerifyFunctions[ruleKey]) {
                return this.ruleVerifyFunctions[ruleKey].verify(permissionNameObjs);
            } else {
                $log.warn("could not call rule verify function for rule key: " + ruleKey + ", it was not found in the iframe");
            }
        };

        return new PermissionService();
    });
