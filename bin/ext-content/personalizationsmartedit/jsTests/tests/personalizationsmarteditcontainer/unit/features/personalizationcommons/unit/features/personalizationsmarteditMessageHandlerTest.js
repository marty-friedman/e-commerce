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
describe('personalizationsmarteditMessageHandler', function() {
    var mockModules = {};
    setupMockModules(mockModules); // jshint ignore:line

    var personalizationsmarteditMessageHandler;

    beforeEach(module('personalizationsmarteditCommons'));
    beforeEach(inject(function(_personalizationsmarteditMessageHandler_) {
        personalizationsmarteditMessageHandler = _personalizationsmarteditMessageHandler_;
    }));


    describe('sendInformation', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditMessageHandler.sendInformation).toBeDefined();
        });

        it('properly forward message to alertService', function() {
            // when
            personalizationsmarteditMessageHandler.sendInformation("test message");
            //then
            expect(mockModules.alertService.showInfo).toHaveBeenCalledWith("test message");
        });

    });

    describe('sendError', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditMessageHandler.sendError).toBeDefined();
        });

        it('properly forward message to alertService', function() {
            // when
            personalizationsmarteditMessageHandler.sendError("test error");
            // then
            expect(mockModules.alertService.showDanger).toHaveBeenCalledWith("test error");
        });

    });

    describe('sendWarning', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditMessageHandler.sendWarning).toBeDefined();
        });

        it('properly forward message to alertService', function() {
            // when
            personalizationsmarteditMessageHandler.sendWarning("test warning");
            // then
            expect(mockModules.alertService.showWarning).toHaveBeenCalledWith("test warning");
        });

    });

    describe('sendSuccess', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditMessageHandler.sendSuccess).toBeDefined();
        });

        it('properly forward message to alertService', function() {
            // when
            personalizationsmarteditMessageHandler.sendSuccess("test success");
            // then
            expect(mockModules.alertService.showSuccess).toHaveBeenCalledWith("test success");
        });

    });

});
