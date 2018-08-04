angular.module('personalizationsmarteditPreviewServiceModule', [
        'personalizationsmarteditRestServiceModule',
        'sharedDataServiceModule',
        'personalizationsmarteditCommons',
        'personalizationsmarteditContextServiceModule'
    ])
    .factory('personalizationsmarteditPreviewService', function($q, $filter, personalizationsmarteditRestService, sharedDataService, personalizationsmarteditUtils, personalizationsmarteditMessageHandler, personalizationsmarteditContextService) {

        var previewService = {};

        var updateExperience = function(variations) {
            sharedDataService.get('experience').then(function(experience) {
                experience.variations = variations;
            });
        };

        previewService.removePersonalizationDataFromPreview = function(previewTicketId) {
            var deferred = $q.defer();
            previewService.updatePreviewTicketWithVariations(previewTicketId, []).then(function successCallback(previewTicket) {
                updateExperience([]);

                deferred.resolve(previewTicket);
            }, function errorCallback(response) {
                deferred.reject(response);
            });
            return deferred.promise;
        };

        previewService.updatePreviewTicketWithVariations = function(previewTicketId, variations) {
            var deferred = $q.defer();
            personalizationsmarteditRestService.getPreviewTicket(previewTicketId).then(function successCallback(previewTicket) {
                previewTicket.variations = variations;

                updateExperience(variations);

                deferred.resolve(personalizationsmarteditRestService.updatePreviewTicket(previewTicket));
            }, function errorCallback(response) {
                if (response.status === 404) {
                    //preview ticket not found - let's try to create a new one with the same parameters
                    deferred.resolve(previewService.createPreviewTicket(variations));
                } else {
                    personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.gettingpreviewticket'));
                    deferred.reject(response);
                }
            });

            return deferred.promise;
        };

        previewService.createPreviewTicket = function(variationsForPreview) {
            var experience = personalizationsmarteditContextService.getSeData().seExperienceData;
            var configuration = personalizationsmarteditContextService.getSeData().seConfigurationData;

            var deferred = $q.defer();

            var resourcePath = configuration.domain + experience.siteDescriptor.previewUrl;

            var previewTicket = {
                catalog: experience.catalogDescriptor.catalogId,
                catalogVersion: experience.catalogDescriptor.catalogVersion,
                language: experience.languageDescriptor.isocode,
                resourcePath: resourcePath,
                variations: variationsForPreview
            };

            updateExperience(variationsForPreview);

            personalizationsmarteditRestService.createPreviewTicket(previewTicket).then(function successCallback(response) {
                previewService.storePreviewTicketData(response.resourcePath, response.ticketId);
                personalizationsmarteditContextService.refreshPreviewData();
                personalizationsmarteditMessageHandler.sendSuccess($filter('translate')('personalization.info.newpreviewticketcreated'));
                deferred.resolve(response);
            }, function errorCallback(response) {
                personalizationsmarteditMessageHandler.sendError($filter('translate')('personalization.error.creatingpreviewticket'));
                deferred.reject(response);
            });

            return deferred.promise;
        };

        previewService.storePreviewTicketData = function(resourcePathToStore, previewTicketIdToStore) {
            var previewToStore = {
                previewTicketId: previewTicketIdToStore,
                resourcePath: resourcePathToStore
            };
            sharedDataService.set('preview', previewToStore);
        };

        return previewService;
    });
