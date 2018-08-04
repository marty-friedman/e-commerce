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
import {annotationService} from 'smarteditcommons/services/annotationService';

export const cache = annotationService.getMethodAnnotationFactory('cache');

// to be added to an angular recipe
/** @internal */
export function cacheAnnotationFactory($log: angular.ILogService) {
	'ngInject';
	return annotationService.setMethodAnnotationFactory("cache", (factoryArgument: any[]) => {

		return function(target: any, propertyName: string, originalMethod: (...x: any[]) => any, ...invocationArguments: any[]) {
			$log.info(`will cache with region '${factoryArgument[0]}' and priority ${factoryArgument[1]}`);
			return originalMethod.apply(this, invocationArguments);
		};
	});
}