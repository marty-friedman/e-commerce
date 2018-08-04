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
import {yMoreTextComponent} from "smarteditcommons/components/yMoreText/yMoreTextComponent";
import {TextTruncateService} from "smarteditcommons/services/text/textTruncateService";

/**
 * @ngdoc overview
 * @name yMoreTextModule
 *
 * @description
 * Module containing parts of the yMoreTextComponent component.
 */
export const yMoreTextModule = angular.module('yMoreTextModule', [])
	.component('yMoreText', yMoreTextComponent)
	.service('textTruncateService', TextTruncateService);

