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
import * as lo from 'lodash';
import {Cloneable, Payload} from 'smarteditcommons';
import {ConfigurationObject} from 'smarteditcontainer/services/bootstrap/Configuration';
import {ConfigurationModules, Module} from 'smarteditcontainer/services/bootstrap/ConfigurationModules';

enum ApplicationLayer {
	SMARTEDITCONTAINER, SMARTEDIT
}
/** @ngInject */
export class ConfigurationExtractorService {

	constructor(private lodash: lo.LoDashStatic) {
	}

	extractSEContainerModules(configurations: ConfigurationObject) {
		return this._getAppsAndLocations(configurations, ApplicationLayer.SMARTEDITCONTAINER);
	}

	extractSEModules(configurations: ConfigurationObject) {
		return this._getAppsAndLocations(configurations, ApplicationLayer.SMARTEDIT);
	}

	private _getAppsAndLocations(configurations: ConfigurationObject, applicationLayer: ApplicationLayer): ConfigurationModules {
		let locationName: string;
		switch (applicationLayer) {
			case ApplicationLayer.SMARTEDITCONTAINER:
				locationName = 'smartEditContainerLocation';
				break;
			case ApplicationLayer.SMARTEDIT:
				locationName = 'smartEditLocation';
				break;
		}

		const appsAndLocations = this.lodash.map(configurations, (value: Cloneable, prop: string) => {
			return {key: prop, value};
		}).reduce((holder: ConfigurationModules, current: {key: string, value: Cloneable}) => {

			if (current.key.indexOf('applications') === 0 && typeof (current.value as Payload)[locationName] === 'string') {

				const app = {} as Module;
				app.name = current.key.split('.')[1];
				const location = (current.value as Payload)[locationName] as string;
				if (/^https?\:\/\//.test(location)) {
					app.location = location;
				} else {
					app.location = configurations.domain + location;
				}
				const _extends = (current.value as Payload).extends as string;
				if (_extends) {
					app.extends = _extends;
				}

				holder.applications.push(app);
				// authenticationMaps from smartedit modules
				holder.authenticationMap = this.lodash.merge(holder.authenticationMap, (current.value as Payload).authenticationMap);
			} else if (current.key === 'authenticationMap') {
				// authenticationMap from smartedit
				holder.authenticationMap = this.lodash.merge(holder.authenticationMap, (current.value as Payload));
			}
			return holder;
		}, {
			applications: [],
			authenticationMap: {}
		} as ConfigurationModules);

		if (applicationLayer === ApplicationLayer.SMARTEDITCONTAINER) {
			appsAndLocations.applications.push({name: 'administration', location: [configurations.smarteditroot, '/static-resources/smarteditcontainer/modules/administrationModule.js'].join("")});
		} else if (applicationLayer === ApplicationLayer.SMARTEDIT) {
			appsAndLocations.applications.push({name: 'systemModule', location: [configurations.smarteditroot, '/static-resources/smartedit/modules/systemModule.js'].join("")});
		}
		return appsAndLocations;

	}
}
