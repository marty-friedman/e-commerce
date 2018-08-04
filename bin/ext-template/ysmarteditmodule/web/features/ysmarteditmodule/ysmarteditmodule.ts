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
 * @ngdoc overview
 * @name ysmarteditmodule
 * @description
 * Placeholder for documentation
 */
import * as angular from 'angular';
import 'ysmarteditmodule/ysmarteditmodule_bundle.js';

angular.module('ysmarteditmodule', [
	'featureServiceModule', // Feature API Module from SmartEdit Application
	'perspectiveServiceModule', // Perspective API Module from SmartEdit Application
	'decoratorServiceModule', // Decorator API Module from SmartEdit Application
	'abAnalyticsDecoratorModule' // Decorators must be added as dependencies to be wired into SmartEdit
])
	.run(function(decoratorService: any, featureService: any, perspectiveService: any) { // Parameters are injected factory methods
		'ngInject';
		////////////////////////////////////////////////////
		// Create Decorator
		////////////////////////////////////////////////////

		// Use the decoratorService.addMappings() method to associate decorators
		// The keys may be given as strings or as regex
		decoratorService.addMappings({
			SimpleResponsiveBannerComponent: ['abAnalyticsDecorator'],
			CMSParagraphComponent: ['abAnalyticsDecorator']
		});

		// Register new decorators the the featureService
		// The key MUST be the same name as the directive
		featureService.addDecorator({
			key: 'abAnalyticsDecorator',
			nameI18nKey: 'ab.analytics.feature.name'
		});

		////////////////////////////////////////////////////
		// Create  Perspective and assign features.
		////////////////////////////////////////////////////
		// Group the features created above in a perspective. This will enable the features once the user selects this new perspective.
		perspectiveService.register({
			key: 'abAnalyticsPerspective',
			nameI18nKey: 'ab.analytics.perspective.name',
			descriptionI18nKey: 'ab.analytics.perspective.description',
			features: ['abAnalyticsToolbarItem', 'abAnalyticsDecorator'],
			perspectives: []
		});
	});
