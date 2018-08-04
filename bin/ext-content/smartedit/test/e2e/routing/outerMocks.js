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
(function() {
    angular
        .module('e2eBackendMocks', ['ngMockE2E', 'resourceLocationsModule', 'languageServiceModule'])
        .constant('SMARTEDIT_ROOT', 'web/webroot')
        .constant('SMARTEDIT_RESOURCE_URI_REGEXP', /^(.*)\/test\/e2e/)
        .constant('STOREFRONT_URI', 'http://127.0.0.1:9000/test/e2e/routing/smarteditiframe.html')
        .run(function($httpBackend, languageService, $location, I18N_RESOURCE_URI, STOREFRONT_PATH, STOREFRONT_URI, parseQuery) {
            var map = [{
                "value": "\"thepreviewTicketURI\"",
                "key": "previewTicketURI"
            }, {
                "value": "{\"smartEditLocation\":\"/test/e2e/routing/buttonDecorator.js\"}",
                "key": "applications.CMSApp"
            }, {
                "value": "{\"smartEditContainerLocation\":\"/test/e2e/routing/outerapp.js\"}",
                "key": "applications.outerapp"
            }, {
                "value": "\"somepath\"",
                "key": "i18nAPIRoot"
            }, {
                "value": "[\"*\"]",
                "key": "whiteListedStorefronts"
            }];

            $httpBackend.whenGET(/configuration/).respond(
                function() {
                    return [200, map];
                });

            $httpBackend.whenGET(I18N_RESOURCE_URI + "/" + languageService.getBrowserLocale()).respond({});


            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/.*\/languages/).respond({
                languages: [{
                    nativeName: 'English',
                    isocode: 'en',
                    name: 'English',
                    required: true
                }]
            });

            var allSites = [{
                previewUrl: '/test/e2e/routing/smarteditiframe.html',
                name: {
                    en: "Electronics"
                },
                redirectUrl: 'redirecturlElectronics',
                uid: 'electronics',
                contentCatalogs: ['electronicsContentCatalog']
            }, {
                previewUrl: '/test/e2e/routing/smarteditiframe.html',
                name: {
                    en: "Apparels"
                },
                redirectUrl: 'redirecturlApparels',
                uid: 'apparel-uk',
                contentCatalogs: ['apparel-ukContentCatalog']
            }];

            $httpBackend.whenGET(/cmswebservices\/sites$/).respond({
                sites: allSites
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/sites\?catalogIds=.*/).respond(function(method, url) {
                var params = parseQuery(url);
                var catalogIds = params.catalogIds && params.catalogIds.split(',');

                if (catalogIds) {
                    var filteredItems = allSites.filter(function(site) {
                        return catalogIds.indexOf(site.contentCatalogs[site.contentCatalogs.length - 1]) > -1;
                    });

                    return [200, {
                        sites: filteredItems
                    }];
                }

                return [200, {
                    sites: []
                }];

            });

            $httpBackend.whenPOST(/thepreviewTicketURI/)
                .respond({
                    ticketId: 'dasdfasdfasdfa',
                    resourcePath: STOREFRONT_URI
                });

            var pathWithExperience = STOREFRONT_PATH
                .replace(":siteId", "apparel-uk")
                .replace(":catalogId", "apparel-ukContentCatalog")
                .replace(":catalogVersion", "Staged");
            $location.path(pathWithExperience);

        });

    angular.module('smarteditloader').requires.push('e2eBackendMocks');
    angular.module('smarteditcontainer').requires.push('e2eBackendMocks');
})();
