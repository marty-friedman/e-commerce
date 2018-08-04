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
angular.module('experienceServiceModule', ['gatewayProxyModule'])

    /**
     * @ngdoc service
     * @name experienceServiceModule.service:experienceService
     *
     * @description
     * The experience Service deals with building experience objects given a context.
     */
    .service('experienceService', function(gatewayProxy) {


        var ExperienceService = function(gatewayId) {
            this.gatewayId = gatewayId;
            gatewayProxy.initForService(this);
        };

        ExperienceService.prototype.updateExperiencePageContext = function() {};

        ExperienceService.prototype.getCurrentExperience = function() {};

        return new ExperienceService('experienceService');

    });
