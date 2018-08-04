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
describe('personalizationsmarteditDateUtils', function() {
    var mockModules = {};
    setupMockModules(mockModules); // jshint ignore:line

    var personalizationsmarteditDateUtils;

    beforeEach(module('personalizationsmarteditCommons', function($provide) {
        var mockTranslateFilter = function(value) {
            return value;
        };
        $provide.value('translateFilter', mockTranslateFilter);

        $provide.value('isBlank', function(elem) {
            return elem === undefined;
        });
    }));


    beforeEach(inject(function(_personalizationsmarteditDateUtils_) {
        personalizationsmarteditDateUtils = _personalizationsmarteditDateUtils_;
    }));


    describe('formatDate', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditDateUtils.formatDate).toBeDefined();
        });

        it('should return empty string if date parameter not passed', function() {
            expect(personalizationsmarteditDateUtils.formatDate()).toBe("");
            expect(personalizationsmarteditDateUtils.formatDate(null, "YYYY-MM-DD")).toBe("");
        });

        it('should return date in format that was passed as argument', function() {
            var mockFormat = "YYYY-MM-DD";
            var mockDate = "2010-11-20T12:12:12";
            var formattedDate = personalizationsmarteditDateUtils.formatDate(mockDate, mockFormat);
            expect(formattedDate).toMatch(/^(\d{4})\-(\d{2})\-(\d{2})$/);
        });

    });

    describe('isDateInThePast', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditDateUtils.isDateInThePast).toBeDefined();
        });

        it('should return false if date parameter not passed', function() {
            expect(personalizationsmarteditDateUtils.isDateInThePast()).toBe(false);
        });

        it('should return true if date is in the past', function() {
            var dateInThePastStr = "11/20/10 12:12 AM";
            expect(personalizationsmarteditDateUtils.isDateInThePast(dateInThePastStr)).toBe(true);
        });

        it('should return false if date is in the future', function() {
            var dateInTheFutureStr = "11/20/40 12:12 AM";
            expect(personalizationsmarteditDateUtils.isDateInThePast(dateInTheFutureStr)).toBe(false);
        });

    });

    describe('isDateValidOrEmpty', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditDateUtils.isDateValidOrEmpty).toBeDefined();
        });

        it('should return true if date parameter not passed', function() {
            expect(personalizationsmarteditDateUtils.isDateValidOrEmpty()).toBe(true);
        });

        it('should return true if date string is in right format', function() {
            var validDateStr = "11/20/10 12:12 AM";
            expect(personalizationsmarteditDateUtils.isDateInThePast(validDateStr)).toBe(true);
        });

        it('should return false if date string is not in date format', function() {
            var inValidDateStr = "312adwdafawdaw";
            expect(personalizationsmarteditDateUtils.isDateInThePast(inValidDateStr)).toBe(false);
        });

    });

    describe('isDateRangeValid', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditDateUtils.isDateRangeValid).toBeDefined();
        });

        it('should return true if no dates were passed', function() {
            expect(personalizationsmarteditDateUtils.isDateRangeValid()).toBe(true);
        });

        it('should return true if only startdate are passed', function() {
            var startDateStr = "2010-11-20T12:12:12";
            expect(personalizationsmarteditDateUtils.isDateRangeValid(startDateStr)).toBe(true);
        });

        it('should return true if only enddate are passed', function() {
            var endDateStr = "2010-11-20T12:12:12";
            expect(personalizationsmarteditDateUtils.isDateRangeValid(undefined, endDateStr)).toBe(true);
        });

        it('should return true if only startdate is before enddate are passed', function() {
            var startDateStr = "2010-11-20T12:12:12";
            var endDateStr = "2011-11-20T12:12:12";
            expect(personalizationsmarteditDateUtils.isDateRangeValid(startDateStr, endDateStr)).toBe(true);
        });

        it('should return false if only startdate is after enddate are passed', function() {
            var startDateStr = "2011-11-20T12:12:12";
            var endDateStr = "2010-11-20T12:12:12";
            expect(personalizationsmarteditDateUtils.isDateRangeValid(startDateStr, endDateStr)).toBe(false);
        });

    });

    describe('isDateStrFormatValid', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditDateUtils.isDateStrFormatValid).toBeDefined();
        });

        it('should return true if no paremeters were passed', function() {
            expect(personalizationsmarteditDateUtils.isDateStrFormatValid()).toBe(false);
        });

        it('should return true for valid format', function() {
            var dateStr = "2/3/17 1:12 AM";
            var format = "M/D/YY h:mm A";

            expect(personalizationsmarteditDateUtils.isDateStrFormatValid(dateStr, format)).toBe(true);
        });

        it('should return false for invalid format', function() {
            var dateStr = "2/3";
            var format = "M/D/YY h:mm A";

            expect(personalizationsmarteditDateUtils.isDateStrFormatValid(dateStr, format)).toBe(false);
        });

    });

});
