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
angular.module('previewErrorInterceptorModule', ['resourceLocationsModule', 'smarteditServicesModule', 'functionsModule'])
    /**
     * @ngdoc service
     * @name previewErrorInterceptorModule.service:previewErrorInterceptor
     * @description
     * Used for HTTP error code 400 from the Preview API when the pageId is not found in the context. The request will be replayed without the pageId.
     */
    .factory('previewErrorInterceptor', function($injector, $q, PREVIEW_RESOURCE_URI, sharedDataService, isBlank) {
        return {
            predicate: function(response) {
                return response.status === 400 && response.config.url.indexOf(PREVIEW_RESOURCE_URI) > -1 && !isBlank(response.config.data.pageId) && _hasUnknownIdentifierError(response.data.errors);
            },
            responseError: function(response) {
                delete response.config.data.pageId;
                sharedDataService.update("experience", function(experience) {
                    delete experience.pageId;
                    return experience;
                });
                $injector.get('iFrameManager').setCurrentLocation(null);
                return $q.when($injector.get('$http')(response.config));
            }
        };

        function _hasUnknownIdentifierError(errors) {
            var unknownIdentifierErrors = errors.filter(function(error) {
                return error.type === 'UnknownIdentifierError';
            });
            return unknownIdentifierErrors.length ? true : false;
        }
    });
