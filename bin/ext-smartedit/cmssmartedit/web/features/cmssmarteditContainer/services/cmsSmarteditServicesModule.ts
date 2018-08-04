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
import * as angular from 'angular';

import {AssetsService} from 'cmscommons/services/AssetsService';
import {PageContentSlotsComponentsRestService} from 'cmssmarteditcontainer/dao/PageContentSlotsComponentsRestServiceOuter';
import {TrashedPageService} from 'cmssmarteditcontainer/services/pages/TrashedPageService';

/**
 * @ngdoc overview
 * @name cmsSmarteditServicesModule
 *
 * @description
 * Module containing all the services shared within the CmsSmartEdit application.
 */
export const cmsSmarteditServicesModule = angular
	.module('cmsSmarteditServicesModule', [
		'smarteditServicesModule'
	])
	.service('assetsService', AssetsService)
	.service('trashedPageService', TrashedPageService)
	.service('pageContentSlotsComponentsRestService', PageContentSlotsComponentsRestService);


