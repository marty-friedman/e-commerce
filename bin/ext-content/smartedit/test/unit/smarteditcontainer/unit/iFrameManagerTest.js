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
describe('iFrameManagerModule', function() {

    var yjQuery, $httpBackend, $rootScope, $q, $location, iFrameManager, deviceSupports, deviceOrientations, iframeMock,
        previewRESTService, htmlFetchService, sharedDataService, modalService, heartBeatService, restServiceFactory,
        loadConfigManagerService, previewService;
    var previewTicket = "previewTicket1";


    beforeEach(module('gatewayFactoryModule', function($provide) {
        var gatewayFactory = jasmine.createSpyObj('gatewayFactory', ['initListener']);
        $provide.value('gatewayFactory', gatewayFactory);
    }));

    beforeEach(module('gatewayProxyModule', function($provide) {
        var gatewayProxy = jasmine.createSpyObj('gatewayProxy', ['initForService']);
        $provide.value('gatewayProxy', gatewayProxy);
    }));

    beforeEach(module('smarteditServicesModule', function($provide) {

        previewService = jasmine.createSpyObj('previewService', ['createPreview', 'getResourcePathFromPreviewUrl']);
        $provide.value('previewService', previewService);

    }));

    beforeEach(module('iFrameManagerModule', function($provide) {

        sharedDataService = jasmine.createSpyObj('sharedDataService', ["set", "get"]);
        $provide.value("sharedDataService", sharedDataService);

        var i18nMap = {
            'se.deviceorientation.vertical.label': 'Vertical',
            'se.deviceorientation.horizontal.label': 'Horizontal'
        };

        var translateMock = function(key) {
            return {
                then: function(callback) {
                    return callback(i18nMap[key]);
                },

            };
        };

        translateMock.storageKey = function() {};
        translateMock.storage = function() {};
        translateMock.preferredLanguage = function() {};

        $provide.value("$translate", translateMock);

        loadConfigManagerService = jasmine.createSpyObj('loadConfigManagerService', ["loadAsObject"]);
        $provide.value("loadConfigManagerService", loadConfigManagerService);

        restServiceFactory = jasmine.createSpyObj('restServiceFactory', ["get"]);

        previewRESTService = jasmine.createSpyObj('previewRESTService', ["save"]);
        htmlFetchService = jasmine.createSpyObj('htmlFetchService', ["get"]);

        $provide.value("LANDING_PAGE_PATH", "/");

        restServiceFactory.get.and.callFake(function(uri) {
            if (uri === 'thepreviewTicketUri') {
                return previewRESTService;
            } else {
                return htmlFetchService;
            }
        }); //configurations.previewTicketURI || PREVIEW_RESOURCE_URI
        $provide.value("restServiceFactory", restServiceFactory);

        modalService = jasmine.createSpyObj('modalService', ["open"]);
        $provide.value("modalService", modalService);

        heartBeatService = jasmine.createSpyObj('heartBeatService', ["resetTimer"]);
        $provide.value("heartBeatService", heartBeatService);

        yjQuery = jasmine.createSpyObj('yjQuery', ['contains']);
        yjQuery.fn = {
            extend: function() {}
        };
        $provide.value("yjQuery", yjQuery);
    }));

    beforeEach(inject(function(_$httpBackend_, _$rootScope_, _$q_, _$location_, _iFrameManager_, _deviceSupports_, _deviceOrientations_) {
        $httpBackend = _$httpBackend_;
        $rootScope = _$rootScope_;
        $q = _$q_;
        htmlFetchService.get.and.returnValue($q.when());
        $location = _$location_;
        iFrameManager = _iFrameManager_;
        deviceSupports = _deviceSupports_;
        deviceOrientations = _deviceOrientations_;
        iframeMock = jasmine.createSpyObj('iframeMock', ["removeClass", "addClass", "css", "attr", "showWaitModal"]);
        iframeMock.removeClass.and.returnValue(iframeMock);
        iframeMock.addClass.and.returnValue(iframeMock);
        spyOn(iFrameManager, "getIframe").and.returnValue(iframeMock);
        spyOn($location, "url").and.returnValue("");

        $httpBackend.whenGET("/failinghtml").respond(function() {
            return [404, null, {
                'Content-type': 'text/html'
            }];
        });

        $httpBackend.whenGET("/notfailinghtml").respond(function() {
            return [200];
        });



        var preview = {
            previewTicketId: 'fgwerwertwertwer',
            resourcePath: 'returnedResourcePath/?someSite=site'
        };
        previewService.createPreview.and.callFake(function() {
            return $q.when(preview);
        });
        // Setup $q spies once we have _$q_
        previewService.getResourcePathFromPreviewUrl.and.callFake(function() {
            return $q.when("bla");
        });

    }));


    it('iFrameManager WILL show a wait modal dialog', function() {

        iFrameManager.showWaitModal();
        expect(modalService.open).toHaveBeenCalledWith({
            templateUrl: 'waitDialog.html',
            cssClasses: 'ySEWaitDialog',
            controller: jasmine.any(Array)
        });

    });

    describe('_mustLoadAsSuch', function() {

        it('will return true if not currentLocation is set', function() {
            iFrameManager.setCurrentLocation(undefined);
            expect(iFrameManager._mustLoadAsSuch('/myurl')).toBe(true);
        });

        it('will return true if the currentLocation is the homePageOrPageFromPageList', function() {
            iFrameManager.setCurrentLocation('/profilepage');
            expect(iFrameManager._mustLoadAsSuch('/profilepage')).toBe(true);
        });

        it('will return true if the currentLocation has a cmsTicketId', function() {
            iFrameManager.setCurrentLocation('/profilepage?cmsTicketId=myticketID');
            expect(iFrameManager._mustLoadAsSuch('/otherpage')).toBe(true);
        });

        it('will return false if we have a currentLocation that is not the home page or a page from the page list, and doesn\'t have a cmsTicketID', function() {
            iFrameManager.setCurrentLocation('/randomURL');
            expect(iFrameManager._mustLoadAsSuch('/homePageOrPageFromPageList')).toBe(false);
        });

    });


    it('GIVEN that _mustLoadHasSuch has returned true WHEN I request to load a preview THEN the page will be loaded in preview mode', function() {
        // Arrange
        iFrameManager._mustLoadAsSuch = jasmine.createSpy().and.returnValue(true);
        spyOn(iFrameManager, 'load').and.returnValue();

        // Act
        iFrameManager.loadPreview("myurl", previewTicket);
        $rootScope.$digest();

        // Assert
        expect(iFrameManager.load).toHaveBeenCalledWith('myurl/previewServlet?cmsTicketId=previewTicket1');
    });


    it('GIVEN that _mustLoadHasSuch has returned false WHEN I request to load a preview THEN the page will be first loaded in preview mode, then we will load the currentLocation', function() {
        // Arrange
        iFrameManager._mustLoadAsSuch = jasmine.createSpy().and.returnValue(false);
        spyOn(iFrameManager, 'load').and.returnValue();

        // Act
        iFrameManager.setCurrentLocation('aLocation');
        iFrameManager.loadPreview("myurl", previewTicket);
        $rootScope.$digest();

        // Assert
        expect(restServiceFactory.get).toHaveBeenCalledWith('myurl/previewServlet?cmsTicketId=previewTicket1');
        expect(htmlFetchService.get).toHaveBeenCalled();
        expect(iFrameManager.load).toHaveBeenCalledWith('aLocation', true, 'myurl/previewServlet?cmsTicketId=previewTicket1');
    });


    it('GIVEN that loads is called with checkIfFailingHTML set to true WHEN the HTML is not failing THEN iframe will display the requested URL', function() {
        // Arrange/Act
        iFrameManager.load('/notfailinghtml', true, '/myhomepage');
        $httpBackend.flush();

        // Assert
        expect(iframeMock.attr).toHaveBeenCalledWith('src', '/notfailinghtml');
        expect(heartBeatService.resetTimer).toHaveBeenCalledWith(true);
    });

    it('GIVEN that loads is called with checkIfFailingHTML set to true WHEN the HTML is failing THEN iframe will display the homepage', function() {
        // Arrange/Act
        iFrameManager.load('/failinghtml', true, '/myhomepage');
        $httpBackend.flush();

        // Assert
        expect(iframeMock.attr).toHaveBeenCalledWith('src', '/myhomepage');
        expect(heartBeatService.resetTimer).toHaveBeenCalledWith(true);
    });


    it('iFrameManager load the expected url into the iframe and does not open wait dialog (open by storefront loading)', function() {
        // Arrange/Act
        iFrameManager.load("myurl");

        // Assert
        expect(iframeMock.attr).toHaveBeenCalledWith('src', 'myurl');
        expect(heartBeatService.resetTimer).toHaveBeenCalledWith(true);
        expect(modalService.open).not.toHaveBeenCalled();
        expect(htmlFetchService.get).not.toHaveBeenCalled();
    });

    it('iFrameManager loadPreview appends previewServlet suffix to the url and the preview ticket to the query string case 1', function() {
        // Arrange
        spyOn(iFrameManager, 'load').and.returnValue();

        // Act
        iFrameManager.loadPreview("myurl", previewTicket);

        // Assert
        expect(iFrameManager.load).toHaveBeenCalledWith('myurl/previewServlet?cmsTicketId=previewTicket1');
    });
    it('iFrameManager loadPreview appends previewServlet suffix to the url and the preview ticket to the query string case 2', function() {
        // Arrange
        spyOn(iFrameManager, 'load').and.returnValue();

        // Act
        iFrameManager.loadPreview("myurl/", previewTicket);

        // Assert
        expect(iFrameManager.load).toHaveBeenCalledWith('myurl/previewServlet?cmsTicketId=previewTicket1');
    });
    it('iFrameManager loadPreview appends previewServlet suffix to the url and the preview ticket to the query string case 3', function() {
        // Arrange
        spyOn(iFrameManager, 'load').and.returnValue();

        // Act
        iFrameManager.loadPreview("myurl?param1=value1", previewTicket);

        // Assert
        expect(iFrameManager.load).toHaveBeenCalledWith('myurl/previewServlet?param1=value1&cmsTicketId=previewTicket1');
    });
    it('iFrameManager loadPreview appends previewServlet suffix to the url and the preview ticket to the query string case 4', function() {
        // Arrange
        spyOn(iFrameManager, 'load').and.returnValue();

        // Act
        iFrameManager.loadPreview("myurl/?param1=value1", previewTicket);

        // Assert
        expect(iFrameManager.load).toHaveBeenCalledWith('myurl/previewServlet?param1=value1&cmsTicketId=previewTicket1');
    });

    it('iFrameManager getDeviceSupports returns the expected deviceSupports from factory', function() {

        expect(iFrameManager.getDeviceSupports()).toBe(deviceSupports);
    });

    it('iFrameManager getDeviceOrientations returns the expected deviceOrientations with label being translation of keys', function() {
        $rootScope.$digest();
        var deviceOrientations = iFrameManager.getDeviceOrientations();
        expect(deviceOrientations).toBe(deviceOrientations);

        expect(deviceOrientations).toEqualData([{
            orientation: 'vertical',
            key: 'se.deviceorientation.vertical.label',
            label: 'Vertical'
        }, {
            orientation: 'horizontal',
            key: 'se.deviceorientation.horizontal.label',
            label: 'Horizontal'
        }]);
    });

    it('apply on no arguments gives a full frame', function() {

        iFrameManager.apply(undefined, undefined);
        expect(iframeMock.removeClass).toHaveBeenCalled();
        expect(iframeMock.addClass).not.toHaveBeenCalled();
        expect(iframeMock.css).toHaveBeenCalledWith({
            width: '100%',
            height: '100%',
            display: 'block',
            margin: 'auto'
        });
    });

    it('apply device support with no orientation sets it to vertical', function() {

        iFrameManager.apply({
            width: 600,
            height: '100%'

        }, undefined);
        expect(iframeMock.removeClass).toHaveBeenCalled();
        expect(iframeMock.addClass).toHaveBeenCalledWith("device-vertical device-default");
        expect(iframeMock.css).toHaveBeenCalledWith({
            width: 600,
            height: '100%',
            display: 'block',
            margin: 'auto'
        });
    });

    it('apply device support with orientation applies this orientation', function() {

        iFrameManager.apply({
            height: 600,
            width: '100%'
        }, {
            orientation: 'horizontal',
            key: 'se.deviceorientation.horizontal.label',
        });
        expect(iframeMock.removeClass).toHaveBeenCalled();
        expect(iframeMock.addClass).toHaveBeenCalledWith("device-horizontal device-default");
        expect(iframeMock.css).toHaveBeenCalledWith({
            width: 600,
            height: '100%',
            display: 'block',
            margin: 'auto'
        });
    });


    it('GIVEN that an experience has been set WHEN I request to load a storefront THEN initializeCatalogPreview will call loadPreview with the right parameters', function() {
        // Arrange
        spyOn(iFrameManager, 'loadPreview');

        var experience = {
            siteDescriptor: {
                name: "some name",
                previewUrl: "/someURI/?someSite=site",
                uid: "some uid"
            },
            catalogDescriptor: {
                name: "some cat name",
                catalogId: "some cat uid",
                catalogVersion: "some cat version"
            },
            languageDescriptor: {
                isocode: "some language isocode",
            },
            time: null
        };

        var preview = {
            previewTicketId: 'fgwerwertwertwer',
            resourcePath: 'returnedResourcePath/?someSite=site'
        };

        previewService.createPreview.and.returnValue($q.when(preview));
        sharedDataService.get.and.returnValue($q.when(experience));

        // Act
        iFrameManager.initializeCatalogPreview();
        $rootScope.$digest();

        // Assert
        expect(iFrameManager.loadPreview).toHaveBeenCalledWith('returnedResourcePath/?someSite=site', preview.previewTicketId);
        expect(window.smartEditBootstrapped).toEqualData({});

        expect(sharedDataService.set).toHaveBeenCalledWith('preview', preview);

    });

    it('GIVEN that no experience has been set WHEN I request to load a storefront THEN initializeCatalogPreview will redirect to landing page', function() {
        // Arrange
        spyOn(iFrameManager, 'loadPreview');
        sharedDataService.get.and.returnValue($q.when(null));

        // Act
        iFrameManager.initializeCatalogPreview();
        $rootScope.$digest();

        // Assert
        expect($location.url).toHaveBeenCalledWith('/');
        expect(iFrameManager.loadPreview).not.toHaveBeenCalled();
    });

});
