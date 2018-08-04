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

/**
 * Backwards compatibility for partners and downstream teams
 * The deprecated modules below were moved to smarteditServicesModule
 * 
 * IMPORANT: THE DEPRECATED MODULES WILL NOT BE AVAILABLE IN FUTURE RELEASES
 * @deprecated since 6.7
 */
angular.module('urlServiceModule', ['smarteditServicesModule']);
angular.module('sharedDataServiceModule', ['smarteditServicesModule']);
angular.module('waitDialogServiceModule', ['smarteditServicesModule']);
angular.module('productServiceModule', ['smarteditServicesModule']);
angular.module('perspectiveServiceModule', ['smarteditServicesModule']);
angular.module('siteServiceModule', ['smarteditServicesModule']);
angular.module('notificationServiceModule', ['smarteditServicesModule']);
angular.module('notificationMouseLeaveDetectionServiceModule', ['smarteditServicesModule']);
angular.module('storageServiceModule', ['smarteditServicesModule']);
angular.module('featureServiceModule', ['smarteditServicesModule']);
angular.module('bootstrapServiceModule', ['cmsSmarteditServicesModule']);