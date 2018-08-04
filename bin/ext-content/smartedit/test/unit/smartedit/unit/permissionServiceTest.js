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
describe('smartedit permissionService', function() {

    var gatewayProxy, permissionService, SE_PERMISSION_SERVICE_GATEWAY_ID;

    beforeEach(module('permissionServiceModule', function($provide) {
        gatewayProxy = jasmine.createSpyObj('gatewayProxy', ['initForService']);
        $provide.value('gatewayProxy', gatewayProxy);
    }));

    beforeEach(inject(function(_permissionService_, _SE_PERMISSION_SERVICE_GATEWAY_ID_) {
        permissionService = _permissionService_;
        SE_PERMISSION_SERVICE_GATEWAY_ID = _SE_PERMISSION_SERVICE_GATEWAY_ID_;
    }));

    it('PermissionService initializes and invokes gatewayProxy', function() {
        expect(gatewayProxy.initForService).toHaveBeenCalledWith(permissionService, null, SE_PERMISSION_SERVICE_GATEWAY_ID);
    });

});
