
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

// forced import to make sure d.ts are generated for the interfaces below
import './forcedImport';

// Barrel
export {annotationService} from './annotationService';
export {cache, cacheAnnotationFactory} from './cacheAnnotation';
export {instrument} from './instrumentation';
export {ICatalogService} from './ICatalogService';
export {IContextualMenuButton} from './IContextualMenuButton';
export {ComponentAttributes, IContextualMenuConfiguration} from './IContextualMenuConfiguration';
export {IDecorator} from './IDecorator';
export {InternalFeature, IFeature} from './IFeature';
export {IFeatureService, IFeaturesToAlias} from './IFeatureService';
export {IBound, INotificationMouseLeaveDetectionService} from './INotificationMouseLeaveDetectionService';
export {IConfiguration, INotificationService} from './INotificationService';
export {IPageInfoService} from './IPageInfoService';
export {IPerspective} from './perspectives/IPerspective';
export {IPerspectiveService} from './perspectives/IPerspectiveService';
export {IPreviewService} from './IPreviewService';
export {IPreviewData, IPreviewResponse} from './IPreview';
export {IPrioritized} from './IPrioritized';
export {IProduct} from './IProduct';
export {default as IProxiedService} from './IProxiedService';
export {IReflectable} from './IReflectable';
export {IRestService} from './IRestService';
export {IRestServiceFactory} from './IRestServiceFactory';
export {ISharedDataService} from './ISharedDataService';
export {ISite} from './ISite';
export {IAuthToken, IStorageService} from './IStorageService';
export {IToolbarItem} from './IToolbarItem';
export {IUriContext} from './IUriContext';
export {IUrlService} from './IUrlService';
export {IWaitDialogService} from './IWaitDialogService';

export {PolyfillService} from './PolyfillService';
export {PriorityService} from './PriorityService';
export {SmarteditBoostrapGateway} from './SmarteditBoostrapGateway';
export {TestModeService} from './testModeService';

export * from './dragAndDrop';