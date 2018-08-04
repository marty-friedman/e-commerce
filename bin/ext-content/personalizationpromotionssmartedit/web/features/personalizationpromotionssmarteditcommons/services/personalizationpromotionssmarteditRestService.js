angular.module('personalizationpromotionssmarteditRestServiceModule', [
        'smarteditServicesModule',
        'personalizationsmarteditCommons'
    ])
    .factory('personalizationpromotionssmarteditRestService', function(restServiceFactory, personalizationsmarteditUtils) {

        var AVAILABLE_PROMOTIONS = "/personalizationwebservices/v1/query/cxpromotionsforcatalog";

        var restService = {};

        restService.getPromotions = function(catalogVersions) {
            var restService = restServiceFactory.get(AVAILABLE_PROMOTIONS);
            var entries = [];

            catalogVersions = catalogVersions || [];

            catalogVersions.forEach(
                function(element, i) {
                    personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "catalog" + i, element.catalog);
                    personalizationsmarteditUtils.pushToArrayIfValueExists(entries, "version" + i, element.catalogVersion);
                }
            );

            var requestParams = {
                "params": {
                    "entry": entries
                }
            };

            return restService.save(requestParams);
        };

        return restService;
    });
