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

export * from './bootstrap';
export * from './notifications';
export * from './DelegateRestService';
export * from './DragAndDropCrossOriginOuter';
export * from './perspectives/FeatureServiceOuter';
export * from './PageInfoServiceOuter';
export * from './PreviewServiceOuter';
export * from './ProductService';
export * from './perspectives/PerspectiveServiceOuter';
export * from './RestServiceFactory';
export * from './SharedDataService';
export * from './SiteService';
export * from './StorageService';
export {PermissionsRegistrationService} from './PermissionsRegistrationService';
export * from './UrlService';
export * from './WaitDialogService';
// smarteditServicesModule must be the last one to be imported, error only seen in runtime
export * from './smarteditServicesModule';
