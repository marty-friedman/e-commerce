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
describe('operation context service', function() {
    var operationContextService;

    beforeEach(module('functionsModule'));
    beforeEach(module('operationContextServiceModule'));

    beforeEach(inject(function(_operationContextService_) {
        operationContextService = _operationContextService_;
    }));

    it('should be able to find an operation context for a given url', function() {
        operationContextService.register('/cmswebservices/v1/sites/:siteUID/catalogversiondetails', 'ANY_OPERATION_CONTEXT');
        var oc = operationContextService.findOperationContext('/cmswebservices/v1/sites/123/catalogversiondetails');
        expect(oc).toBe('ANY_OPERATION_CONTEXT');
    });

    it('should return null if there is no operation context registered', function() {
        var oc = operationContextService.findOperationContext('/any_url');
        expect(oc).toBeNull();
    });

    it('should be able to chain the register function', function() {
        expect(operationContextService
                .register('/any_url', 'ANY_OPERATION_CONTEXT')
                .register('/another_url', 'ANOTHER_CONTEXT'))
            .toEqual(operationContextService);
    });

    it('should throw an error if trying to register without passing a url', function() {
        var expectedErrorFunction = function() {
            operationContextService.register();
        };
        expect(expectedErrorFunction).toThrowError('operationContextService.register error: url is invalid');
    });

    it('should throw an error if trying to register with an invalid url', function() {
        var expectedErrorFunction = function() {
            operationContextService.register('');
        };
        expect(expectedErrorFunction).toThrowError('operationContextService.register error: url is invalid');
    });

    it('should throw an error if trying to register with an invalid url', function() {
        var expectedErrorFunction = function() {
            operationContextService.register(123);
        };
        expect(expectedErrorFunction).toThrowError('operationContextService.register error: url is invalid');
    });

    it('should throw an error if trying to register without passing an operationContext', function() {
        var expectedErrorFunction = function() {
            operationContextService.register('test');
        };
        expect(expectedErrorFunction).toThrowError('operationContextService.register error: operationContext is invalid');
    });

    it('should throw an error if trying to register with an invalid operationContext', function() {
        var expectedErrorFunction = function() {
            operationContextService.register('test', '');
        };
        expect(expectedErrorFunction).toThrowError('operationContextService.register error: operationContext is invalid');
    });

    it('should throw an error if trying to register with an invalid operationContext', function() {
        var expectedErrorFunction = function() {
            operationContextService.register('test', {});
        };
        expect(expectedErrorFunction).toThrowError('operationContextService.register error: operationContext is invalid');
    });
});
