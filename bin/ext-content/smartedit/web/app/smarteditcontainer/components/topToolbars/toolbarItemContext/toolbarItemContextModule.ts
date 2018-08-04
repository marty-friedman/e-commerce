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
import {toolbarItemContext} from 'smarteditcontainer/components/topToolbars/toolbarItemContext/toolbarItemContextComponent';

/**
 * @internal
 * 
 * @ngdoc overview
 * @name toolbarItemContextModule
 *
 * @description
 * Module containing parts of the toolbarItemContext component.
 */
export const toolbarItemContextModule = angular.module('toolbarItemContextModule', ['crossFrameEventServiceModule', 'seConstantsModule'])
	.component('toolbarItemContext', toolbarItemContext);