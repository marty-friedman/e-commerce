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
import '../services/functions.js';

import * as angular from 'angular';
import {CloneableUtils} from '../dtos/Cloneable';
import {CryptographicUtils} from './CryptographicUtils';
import {UrlUtils} from './UrlUtils';

/*
 * module for transition of functions.js to typescript
 */
angular.module('functionsModule')
	.service('cloneableUtils', CloneableUtils)
	.service('urlUtils', UrlUtils)
	.service('cryptographicUtils', CryptographicUtils);

