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
describe('languageService - ', function() {

    var languageService, restServiceFactory;
    var $q, $rootScope, $translate;

    function loadLanguageServiceModule() {
        module('languageServiceModule', function($provide) {
            $translate = jasmine.createSpyObj('$translate', ['use']);
            $provide.value('$translate', $translate);
        });

        inject(function(_languageService_, _$q_, _$rootScope_) {
            languageService = _languageService_;
            $q = _$q_;
            $rootScope = _$rootScope_;
        });
    }

    describe('i18n languages - ', function() {

        var i18nLanguageRestService, storageService, gateway, gatewayFactory, crossFrameEventService;

        beforeEach(module('gatewayFactoryModule', function($provide) {
            var gatewayFactory = jasmine.createSpyObj('gatewayFactory', ['initListener']);
            $provide.value('gatewayFactory', gatewayFactory);
        }));

        beforeEach(module('gatewayProxyModule', function($provide) {

            var gatewayProxy = jasmine.createSpyObj('gatewayProxy', ['initForService']);
            $provide.value('gatewayProxy', gatewayProxy);
        }));

        beforeEach(module('smarteditServicesModule', function($provide) {
            i18nLanguageRestService = jasmine.createSpyObj('i18nLanguageRestService', ['get']);
            restServiceFactory = jasmine.createSpyObj('restServiceFactory', ['get']);
            restServiceFactory.get.and.returnValue(i18nLanguageRestService);
            $provide.value('restServiceFactory', restServiceFactory);
        }));

        beforeEach(module('resourceLocationsModule', function($provide) {
            $provide.value('I18N_LANGUAGES_RESOURCE_URI', '/smarteditwebservices/v1/i18n/languages');
        }));

        beforeEach(module('gatewayFactoryModule', function($provide) {
            gatewayFactory = jasmine.createSpyObj('gatewayFactory', ['createGateway']);
            gateway = jasmine.createSpyObj('gateway', ['subscribe', 'publish']);
            gatewayFactory.createGateway.and.returnValue(gateway);
            $provide.value('gatewayFactory', gatewayFactory);
        }));

        beforeEach(module('smarteditServicesModule', function($provide) {
            storageService = jasmine.createSpyObj('storageService', ['getValueFromCookie', 'putValueInCookie']);
            $provide.value('storageService', storageService);
        }));

        beforeEach(module('crossFrameEventServiceModule', function($provide) {
            crossFrameEventService = jasmine.createSpyObj('crossFrameEventService', ['publish']);
            $provide.value('crossFrameEventService', crossFrameEventService);
        }));

        beforeEach(module(function($provide) {
            /* jshint -W020 */
            navigator = {
                language: 'pt-BR'
            };
            $provide.value('navigator', navigator);
        }));

        beforeEach(function() {
            loadLanguageServiceModule();
        });

        it('GIVEN i18n REST call succeed WHEN requesting tooling languages THEN it receives a promise which contains a list of languages ', function() {
            // GIVEN
            i18nRESTCallSucceeds();

            // WHEN
            var promise = languageService.getToolingLanguages();

            // THEN
            expect(promise).toBeResolvedWithData([{
                "isoCode": "en",
                "name": "English"
            }, {
                "isoCode": "de",
                "name": "German",
            }, {
                "isoCode": "pt_BR",
                "name": "Portuguese"
            }]);
        });

        it('GIVEN i18n REST call fails WHEN requesting tooling languages THEN it receives a rejected promise', function() {

            // GIVEN
            i18nLanguageRestService.get.and.returnValue($q.reject());

            // WHEN
            var promise = languageService.getToolingLanguages();

            // THEN
            expect(promise).toBeRejected();
        });

        it('GIVEN I have previously selected a locale (de), THEN I expect to get that locale (de)', function() {

            //GIVEN
            storageService.getValueFromCookie.and.returnValue($q.when({
                'name': 'German',
                'isoCode': 'de'
            }));

            i18nRESTCallSucceeds();

            //THEN
            expect(languageService.getResolveLocale()).toBeResolvedWithData('de');
        });

        it('GIVEN I have previously selected a locale (de), THEN I expect to get the iso code for that locale', function() {
            //GIVEN
            storageService.getValueFromCookie.and.returnValue($q.when({
                'name': 'German',
                'isoCode': 'de'
            }));

            i18nRESTCallSucceeds();

            //THEN
            expect(languageService.getResolveLocaleIsoCode()).toBeResolvedWithData('de');
        });

        it('GIVEN I have not previously selected a locale, THEN I expect to get the browser locale (pt-BR)', function() {
            //GIVEN
            storageService.getValueFromCookie.and.returnValue($q.when());

            i18nRESTCallSucceeds();

            //THEN
            expect(languageService.getResolveLocale()).toBeResolvedWithData('pt_BR');
        });

        it('GIVEN I have not previously selected a locale, THEN I expect to be able to resolve the browser locale iso code', function() {
            //GIVEN
            storageService.getValueFromCookie.and.returnValue($q.when());

            i18nRESTCallSucceeds();

            //THEN
            expect(languageService.getResolveLocaleIsoCode()).toBeResolvedWithData('pt');
        });

        it('GIVEN I register for switching the language THEN it should subscribe to the gateway', function() {
            languageService.registerSwitchLanguage();
            expect(gatewayFactory.createGateway).toHaveBeenCalledWith('languageSwitch');
            expect(gateway.subscribe).toHaveBeenCalledWith('SWITCH_LANGUAGE_EVENT', jasmine.any(Function));

            expect($translate.use).not.toHaveBeenCalled();

            var data = {
                isoCode: 'kl'
            };
            var callback = gateway.subscribe.calls.argsFor(0)[1];

            callback("someeventId", data);

            expect($translate.use).toHaveBeenCalledWith('kl');

        });


        it('GIVEN I select a language THEN it should save the language in the cookie AND switch the language AND publish an event to the gateway', function() {

            var language = {
                'name': 'German',
                'isoCode': 'de'
            };

            languageService.setSelectedToolingLanguage(language);

            expect(storageService.putValueInCookie).toHaveBeenCalledWith('SELECTED_LANGUAGE', language, false);
            expect($translate.use).toHaveBeenCalledWith('de');

            expect(crossFrameEventService.publish).toHaveBeenCalledWith('SWITCH_LANGUAGE_EVENT');
            expect(gateway.publish).toHaveBeenCalledWith('SWITCH_LANGUAGE_EVENT', {
                isoCode: 'de'
            });
        });

        it('GIVEN tag in BCP47 format WHEN convertBCP47TagToJavaTag is used THEN it is converted to java tag', function() {
            // GIVEN
            var bcp47Tag = "en-US";

            // WHEN
            var javaTag = languageService.convertBCP47TagToJavaTag(bcp47Tag);

            // THEN
            expect(javaTag).toEqual('en_US');
        });

        it('GIVEN tag in java format WHEN convertJavaTagToBCP47Tag is used THEN it is converted to BCP47 tag', function() {
            // GIVEN
            var javaTag = "en_US";

            // WHEN
            var bcp47Tag = languageService.convertJavaTagToBCP47Tag(javaTag);

            // THEN
            expect(bcp47Tag).toEqual('en-US');
        });

        function i18nRESTCallSucceeds() {
            i18nLanguageRestService.get.and.returnValue($q.when({
                languages: [{
                    "isoCode": "en",
                    "name": "English"
                }, {
                    "isoCode": "de",
                    "name": "German"
                }, {
                    "isoCode": "pt_BR",
                    "name": "Portuguese"
                }]
            }));
        }
    });

    describe('site languages - ', function() {

        var SITE_UID = 'apparel-de';

        var languageRestService;

        beforeEach(module('smarteditServicesModule', function($provide) {
            languageRestService = jasmine.createSpyObj('languageRestService', ['get']);
            restServiceFactory = jasmine.createSpyObj('restServiceFactory', ['get']);
            restServiceFactory.get.and.returnValue(languageRestService);
            $provide.value('restServiceFactory', restServiceFactory);
        }));

        beforeEach(module('resourceLocationsModule', function($provide) {
            $provide.value('LANGUAGE_RESOURCE_URI', '/cmswebservices/sites/:siteUID/languages');
        }));

        beforeEach(function() {
            loadLanguageServiceModule();
        });


        it('GIVEN languages REST call fails WHEN I request all languages for a given site THEN I will receive a rejected promise', function() {
            // GIVEN
            languageRESTCallFails();

            // WHEN
            var promise = languageService.getLanguagesForSite(SITE_UID);

            // THEN
            expect(promise).toBeRejected();
        });

        it('GIVEN languages REST call succeeds WHEN I request all languages for a given site THEN I will receive a promise that resolves to the list of language objects', function() {
            // GIVEN
            languagesRESTCallSucceeds();

            // WHEN
            var promise = languageService.getLanguagesForSite(SITE_UID);

            // THEN
            expect(promise).toBeResolvedWithData([{
                "nativeName": "English",
                "isocode": "en",
                "name": "English",
                "active": true,
                "required": true
            }, {
                "nativeName": "Deutsch",
                "isocode": "de",
                "name": "German",
                "active": true,
                "required": false
            }]);
        });

        it('GIVEN languages REST call succeeds at least one WHEN I request all languages for the same site subsequently THEN I will receive a promise that resolves to a cached list of languages AND the rest service will not be called again', function() {

            // GIVEN
            languagesRESTCallSucceeds();

            // WHEN
            languageService.getLanguagesForSite(SITE_UID);
            $rootScope.$digest();
            var promise = languageService.getLanguagesForSite(SITE_UID);
            $rootScope.$digest();

            // THEN
            expect(promise).toBeResolvedWithData([{
                "nativeName": "English",
                "isocode": "en",
                "name": "English",
                "active": true,
                "required": true
            }, {
                "nativeName": "Deutsch",
                "isocode": "de",
                "name": "German",
                "active": true,
                "required": false
            }]);
            expect(languageRestService.get.calls.count()).toEqual(1);
        });

        function languagesRESTCallSucceeds() {
            languageRestService.get.and.returnValue($q.when({
                languages: [{
                    "nativeName": "English",
                    "isocode": "en",
                    "name": "English",
                    "active": true,
                    "required": true
                }, {
                    "nativeName": "Deutsch",
                    "isocode": "de",
                    "name": "German",
                    "active": true,
                    "required": false
                }]
            }));
        }

        function languageRESTCallFails() {
            languageRestService.get.and.returnValue($q.reject());
        }

    });
});
