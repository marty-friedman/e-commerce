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
/**
 * @ngdoc service
 * @name iFrameManagerModule
 *
 * @description
 * Module that provides a service called {@link iFrameManagerModule.iFrameManager iFrameManager} which has a set of methods
 * to load the storefront within an iframe.
 *
 */
angular.module('iFrameManagerModule', [
        'alertServiceModule',
        'functionsModule',
        'translationServiceModule',
        'ngResource',
        'loadConfigModule',
        'smarteditServicesModule',
        'resourceLocationsModule',
        'heartBeatServiceModule',
        'eventServiceModule',
        'yLoDashModule',
        'yjqueryModule'
    ])

    .factory("deviceSupports", function() {
        return [{
            icon: "static-resources/images/icon_res_phone.png",
            selectedIcon: "static-resources/images/icon_res_phone_s.png",
            type: "phone",
            width: 480
        }, {
            icon: "static-resources/images/icon_res_wphone.png",
            selectedIcon: "static-resources/images/icon_res_wphone_s.png",
            type: "wide-phone",
            width: 600
        }, {
            icon: "static-resources/images/icon_res_tablet.png",
            selectedIcon: "static-resources/images/icon_res_tablet_s.png",
            type: "tablet",
            width: 700
        }, {
            icon: "static-resources/images/icon_res_wtablet.png",
            selectedIcon: "static-resources/images/icon_res_wtablet_s.png",
            type: "wide-tablet",
            width: 1024
        }, {
            icon: "static-resources/images/icon_res_desktop.png",
            selectedIcon: "static-resources/images/icon_res_desktop_s.png",
            type: "desktop",
            width: 1200
        }, {
            type: "wide-desktop",
            icon: "static-resources/images/icon_res_wdesktop.png",
            selectedIcon: "static-resources/images/icon_res_wdesktop_s.png",
            width: "100%"
        }];
    })

    .factory("deviceOrientations", function($translate) {

        var deviceOrientations = [{
            orientation: 'vertical',
            key: 'se.deviceorientation.vertical.label'
        }, {
            orientation: 'horizontal',
            key: 'se.deviceorientation.horizontal.label'
        }];

        deviceOrientations.forEach(function(instance) {
            $translate(instance.key).then(function(translation) {
                this.label = translation;
            }.bind(instance));

        });

        return deviceOrientations;
    })

    /**
     * @ngdoc service
     * @name iFrameManagerModule.iFrameManager
     *
     * @description
     * The iFrame Manager service provides methods to load the storefront into an iframe. The preview of the storefront can be loaded for a specified input homepage and a specified preview ticket. The iframe src attribute is updated with that information in order to display the storefront in SmartEdit.
     */
    .factory("iFrameManager", function(
        $q,
        $log,
        $http,
        $location,
        alertService,
        deviceSupports,
        deviceOrientations,
        getOrigin,
        getURI,
        heartBeatService,
        isBlank,
        getAbsoluteURL,
        LANDING_PAGE_PATH,
        loadConfigManagerService,
        lodash,
        parseQuery,
        previewService,
        PREVIEW_RESOURCE_URI,
        restServiceFactory,
        sharedDataService,
        SMARTEDIT_IFRAME_ID,
        systemEventService,
        waitDialogService,
        yjQuery) {

        var DEFAULT_WIDTH = "100%";
        var DEFAULT_HEIGHT = "100%";

        var currentLocation;

        function getPageAsync(url) {
            return $http({
                method: 'GET',
                url: url
            });
        }

        function getIFrameElement() {
            return yjQuery('iframe#' + SMARTEDIT_IFRAME_ID);
        }

        return {

            /**
             * @ngdoc method
             * @name iFrameManagerModule.iFrameManager#setCurrentLocation
             * @methodOf iFrameManagerModule.iFrameManager
             *
             * @description
             * This method sets the current page location and stores it in the service. The storefront will be loaded with this location.
             *
             * @param {String} URL Location to be stored
             */
            setCurrentLocation: function(_currentLocation) {
                currentLocation = _currentLocation;
            },
            getIframe: function() {
                return getIFrameElement();
            },
            getIFrameSrc: function() {
                return getIFrameElement().attr("src");
            },
            hideWaitModal: function() {
                waitDialogService.hideWaitModal();
            },
            showWaitModal: function() {
                waitDialogService.showWaitModal();
            },
            getDeviceSupports: function() {
                return deviceSupports;
            },
            getDeviceOrientations: function() {
                return deviceOrientations;
            },
            isCrossOrigin: function() {
                return getOrigin() !== getOrigin(currentLocation);
            },

            /*
             * if currentLocation is not set yet, it means that this is a first loading and we are trying to load the homepage,
             * or if the page has a ticket ID but is not the homepage, it means that we try to load a page from the page list.
             * For those scenarios, we want to load the page as such in preview mode.
             */
            _mustLoadAsSuch: function(homePageOrPageFromPageList) {
                return !currentLocation || getURI(homePageOrPageFromPageList) === getURI(currentLocation) || 'cmsTicketId' in parseQuery(currentLocation);
            },

            /**
             * @ngdoc method
             * @name iFrameManagerModule.iFrameManager#load
             * @methodOf iFrameManagerModule.iFrameManager
             *
             * @description
             * This method loads the storefront within an iframe by setting the src attribute to the specified input URL.
             * If this method is called within the context of a new or updated experience, prior to the loading, it will check if the page exists.
             * If the pages does not exist (the server returns a 404 and a content-type:text/html), the user will be redirected to the homepage of the storefront. Otherwise,
             * the user will be redirected to the requested page for the experience.
             *
             * @param {String} URL The URL of the storefront.
             * @param {Boolean} [checkIfFailingHTML] Boolean indicating if we need to check if the page call returns a 404
             * @param {String} [homepageInPreviewMode] URL of the storefront homepage in preview mode if it's a new experience
             *
             */
            load: function(url, checkIfFailingHTML, pageInPreviewMode) {
                if (checkIfFailingHTML) {
                    getPageAsync(url).then(function() {
                        this.getIframe().attr('src', url);
                        heartBeatService.resetTimer(true);
                    }.bind(this), function(error) {
                        if (error.status === 404) {
                            this.getIframe().attr('src', pageInPreviewMode);
                            heartBeatService.resetTimer(true);
                        }
                    }.bind(this));
                } else {
                    $log.debug("loading storefront ", url);
                    this.getIframe().attr('src', url);
                    heartBeatService.resetTimer(true);
                }

            },

            _appendURISuffix: function(url) {
                var pair = url.split('?');
                return pair[0]
                    .replace(/(.+)([^\/])$/g, "$1$2/previewServlet")
                    .replace(/(.+)\/$/g, "$1/previewServlet") + (pair.length === 2 ? "?" + pair[1] : "");
            },

            /**
             * @ngdoc method
             * @name iFrameManagerModule.iFrameManager#loadPreview
             * @methodOf iFrameManagerModule.iFrameManager
             *
             * @description
             * This method loads the preview of the storefront for a specified input homepage URL or a page from the page list, and for a specified preview ticket.
             * This method will add '/previewServlet' to the URI and append the preview ticket in the query string.
             * <br/>If it is an initial load,  {@link iFrameManagerModule.iFrameManager#load load} will be called with this modified homepage or page from page list.
             * <br/>If it is a subsequent call, the modified homepage will be called through Ajax to initialize the preview (storefront constraint) and then
             * {@link iFrameManagerModule.iFrameManager#load load} will be called with the current location.
             *
             * @param {String} homePageOrPageFromPageList The URL of the storefront homepage or a page from the page list for a given experience context.
             * @param {String} previewTicket The preview ticket.
             */
            loadPreview: function(homePageOrPageFromPageList, previewTicket) {
                var previewURL = homePageOrPageFromPageList;
                if (!/.+\.html/.test(previewURL)) { //for testing purposes
                    previewURL = this._appendURISuffix(previewURL);
                }
                $log.debug("loading storefront iframe with preview ticket:", previewTicket);
                var pageInPreviewMode = previewURL + (previewURL.indexOf("?") === -1 ? "?" : "&") + "cmsTicketId=" + previewTicket;

                // If we don't have a current location, or the current location is the homePage or a page from page list, or the current location has a cmsTicketID
                if (this._mustLoadAsSuch(homePageOrPageFromPageList)) {
                    this.load(pageInPreviewMode);
                } else {
                    var isCrossOrigin = this.isCrossOrigin();
                    /*
                     * check failing HTML only if same origin to prevent CORS errors.
                     * if location to reload in new experience context is different from homepage, one will have to
                     * first load the home page in preview mode and then access the location without preview mode 
                     */
                    (isCrossOrigin ? $q.when() : restServiceFactory.get(pageInPreviewMode).get()).then(function() {
                        // FIXME: use gatewayProxy to load url from the inner
                        this.load(currentLocation, !isCrossOrigin, pageInPreviewMode);
                    }.bind(this));
                }
            },

            /**
             * @ngdoc method
             * @name iFrameManagerModule.iFrameManager#initializeCatalogPreview
             * @methodOf iFrameManagerModule.iFrameManager
             *
             * @description
             * If an experience is set in the shared data service, this method will load the preview for this experience (such as Catalog, language, date and time).
             * Otherwise, the user will be redirected to the landing page to select an experience.
             * To load a preview, we need to get a preview ticket from an API
             */
            initializeCatalogPreview: function() {
                this.setCurrentLocation(null);
                return sharedDataService.get('experience').then(function(experience) {
                    if (!experience) {
                        $location.url(LANDING_PAGE_PATH);
                        return;
                    }

                    return previewService.getResourcePathFromPreviewUrl(experience.siteDescriptor.previewUrl).then(function(resourcePath) {
                        /**
                         *  Shuffling around some fields to represent a IPreviewData
                         *  In the future this should be the experience service managing this stuff
                         *  CMSX-6844
                         */
                        experience = lodash.cloneDeep(experience);
                        experience.catalog = experience.catalogDescriptor.catalogId;
                        experience.catalogVersion = experience.catalogDescriptor.catalogVersion;
                        experience.language = experience.languageDescriptor.isocode;
                        experience.resourcePath = resourcePath;
                        experience.pageId = experience.pageId;

                        return previewService.createPreview(experience).then(function(response) {
                            window.smartEditBootstrapped = {};
                            this.loadPreview(response.resourcePath, response.previewTicketId);
                            sharedDataService.set('preview', response);
                            return $q.when();
                        }.bind(this), function(err) {
                            $log.error('iFrameManager.initializeCatalogPreview() - failed to create preview');
                            return $q.reject(err);
                        });
                    }.bind(this), function(err) {
                        $log.error('iFrameManager.initializeCatalogPreview() - failed to retrieve resourcepath');
                        return $q.reject(err);
                    });
                }.bind(this), function(err) {
                    $log.error('iFrameManager.initializeCatalogPreview() - failed to retrieve experience');
                    return $q.reject(err);
                });

            },

            apply: function(deviceSupport, deviceOrientation) {

                var width;
                var height;
                var isVertical = true;

                if (!isBlank(deviceOrientation)) {
                    isVertical = deviceOrientation.orientation === 'vertical';
                }

                if (!isBlank(deviceSupport)) {
                    width = (isVertical ? deviceSupport.width : deviceSupport.height);
                    height = (isVertical ? deviceSupport.height : deviceSupport.width);
                }
                if (!width) {
                    width = DEFAULT_WIDTH;
                }
                if (!height) {
                    height = DEFAULT_HEIGHT;
                }
                if (deviceSupport) {
                    //hardcoded the name to default to remove the device skin
                    this.getIframe().removeClass().addClass("device-" + (isVertical ? "vertical" : "horizontal") + " device-" + "default");
                } else {
                    this.getIframe().removeClass();
                }
                this.getIframe().css({
                    "width": width,
                    "height": height,
                    "display": "block",
                    "margin": "auto"
                });
            },


            applyDefault: function() {
                this.apply(this.getDeviceSupports[0], this.getDeviceOrientations()[0]);
            }
        };

    });
