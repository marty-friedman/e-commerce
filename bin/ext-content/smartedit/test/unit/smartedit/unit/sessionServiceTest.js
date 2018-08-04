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
describe('inner sessionService', function() {

    var gatewayProxy, sessionService, SessionServiceInterface;

    beforeEach(module('gatewayFactoryModule', function($provide) {
        var gatewayFactory = jasmine.createSpyObj('gatewayFactory', ['initListener']);
        $provide.value('gatewayFactory', gatewayFactory);
    }));

    beforeEach(module('gatewayProxyModule', function($provide) {
        gatewayProxy = jasmine.createSpyObj('gatewayProxy', ['initForService']);
        $provide.value('gatewayProxy', gatewayProxy);
    }));

    beforeEach(module("sessionServiceModule"));

    beforeEach(inject(function(_SessionServiceInterface_, _sessionService_) {
        SessionServiceInterface = _SessionServiceInterface_;
        sessionService = _sessionService_;
    }));

    it('extends SessionServiceInterface', function() {
        expect(sessionService instanceof SessionServiceInterface).toBe(true);
    });

    it('initializes and invokes gatewayProxy', function() {
        expect(sessionService.gatewayId).toBe("SessionServiceId");
        expect(gatewayProxy.initForService).toHaveBeenCalledWith(sessionService);
    });

    it('leaves all interface functions unimplemented', function() {
        expect(sessionService.getCurrentUsername).toBeEmptyFunction();
        expect(sessionService.getCurrentUserDisplayName).toBeEmptyFunction();
        expect(sessionService.hasUserChanged).toBeEmptyFunction();
        expect(sessionService.resetCurrentUserData).toBeEmptyFunction();
        expect(sessionService.setCurrentUsername).toBeEmptyFunction();
    });

});
