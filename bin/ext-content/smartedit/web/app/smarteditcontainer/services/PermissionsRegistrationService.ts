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
import {ISharedDataService} from 'smarteditcommons';

/** @internal */
/** @ngInject */
export class PermissionsRegistrationService {

	constructor(private permissionService: any, private sharedDataService: ISharedDataService) {}

	/**
	 * Method containing registrations of rules and permissions to be used in smartedit workspace
	 */
	registerRulesAndPermissions(): void {

		// Rules
		this.permissionService.registerRule({
			names: ['se.slot.belongs.to.page'],
			verify: (permissionObjects: any) => {
				return this.sharedDataService.get('experience').then(function(experience: any) {
					return experience.pageContext && experience.pageContext.catalogVersionUuid === permissionObjects[0].context.slotCatalogVersionUuid;
				});
			}
		});

		// Permissions
		this.permissionService.registerPermission({
			aliases: ['se.slot.not.external'],
			rules: ['se.slot.belongs.to.page']
		});

	}

}