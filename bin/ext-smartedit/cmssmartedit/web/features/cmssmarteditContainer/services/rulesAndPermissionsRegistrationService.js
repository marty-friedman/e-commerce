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
angular.module('rulesAndPermissionsRegistrationModule', [
        'catalogServiceModule',
        'catalogVersionPermissionModule',
        'catalogVersionRestServiceModule',
        'pageServiceModule',
        'permissionServiceModule',
        'smarteditServicesModule'
    ])

    .run(function(
        catalogService,
        catalogVersionPermissionService,
        catalogVersionRestService,
        pageService,
        permissionService,
        sharedDataService,
        siteService,
        $q
    ) {

        // Rules
        permissionService.registerRule({
            names: ['se.write.page', 'se.write.slot', 'se.write.component'],
            verify: function(permissionNameObjs) {
                var promises = permissionNameObjs.map(function(permissionNameObject) {
                    if (permissionNameObject.context) {
                        return catalogVersionPermissionService.hasWritePermission(
                            permissionNameObject.context.catalogId,
                            permissionNameObject.context.catalogVersion
                        );
                    } else {
                        return catalogVersionPermissionService.hasWritePermissionOnCurrent();
                    }
                });

                var onSuccess = function(result) {
                    return result.reduce(function(acc, val) {
                        return acc && val;
                    }, true) === true;
                };

                var onError = function() {
                    return false;
                };

                return $q.all(promises).then(onSuccess, onError);
            }
        });

        permissionService.registerRule({
            names: ['se.read.page', 'se.read.slot', 'se.read.component'],
            verify: function() {
                return catalogVersionPermissionService.hasReadPermissionOnCurrent();
            }
        });

        permissionService.registerRule({
            names: ['se.page.belongs.to.experience'],
            verify: function() {
                return sharedDataService.get('experience').then(function(experience) {
                    return experience.pageContext && experience.pageContext.catalogVersionUuid === experience.catalogDescriptor.catalogVersionUuid;
                });
            }
        });

        /**
         * Show the clone icon:
         * - If a page belonging to an active catalog version is a primary page, whose copyToCatalogsDisabled flag is set to false and has at-least one clonable target.
         * - If a page belonging to a non active catalog version has at-least one clonable target.
         */
        permissionService.registerRule({
            names: ['se.cloneable.page'],
            verify: function() {

                return sharedDataService.get('experience').then(function(experience) {

                    var pageUriContext = {
                        CURRENT_CONTEXT_SITE_ID: experience.pageContext.siteId,
                        CURRENT_CONTEXT_CATALOG: experience.pageContext.catalogId,
                        CURRENT_CONTEXT_CATALOG_VERSION: experience.pageContext.catalogVersion
                    };

                    return pageService.getCurrentPageInfo().then(function(pageInfo) {
                        return catalogVersionRestService.getCloneableTargets(pageUriContext).then(function(targets) {

                            if (experience.pageContext.active) {
                                return targets.versions.length > 0 && pageInfo.defaultPage && !pageInfo.copyToCatalogsDisabled;
                            }

                            return targets.versions.length > 0;

                        });
                    });
                });

            }
        });

        permissionService.registerRule({
            names: ['se.content.catalog.non.active'],
            verify: function() {
                return catalogService.isContentCatalogVersionNonActive();
            }
        });

        // Permissions
        permissionService.registerPermission({
            aliases: ['se.add.component'],
            rules: ['se.write.slot', 'se.write.component', 'se.page.belongs.to.experience', 'se.content.catalog.non.active']
        });

        permissionService.registerPermission({
            aliases: ['se.read.restriction', 'se.read.page'],
            rules: ['se.read.page']
        });

        permissionService.registerPermission({
            aliases: ['se.edit.page'],
            rules: ['se.write.page']
        });

        permissionService.registerPermission({
            aliases: ['se.sync.catalog'],
            rules: ['se.write.page', 'se.write.slot', 'se.write.component']
        });

        permissionService.registerPermission({
            aliases: ['se.sync.slot.context.menu', 'se.sync.page', 'se.sync.slot.indicator'],
            rules: ['se.write.page', 'se.write.slot', 'se.write.component', 'se.page.belongs.to.experience', 'se.content.catalog.non.active']
        });

        permissionService.registerPermission({
            aliases: ['se.edit.navigation'],
            rules: ['se.write.component']
        });

        permissionService.registerPermission({
            aliases: ['se.context.menu.remove.component'],
            rules: ['se.write.slot', 'se.page.belongs.to.experience']
        });

        permissionService.registerPermission({
            aliases: ['se.slot.context.menu.shared.icon', 'se.slot.context.menu.unshared.icon'],
            rules: ['se.read.slot']
        });

        permissionService.registerPermission({
            aliases: ['se.slot.context.menu.visibility'],
            rules: ['se.page.belongs.to.experience']
        });

        permissionService.registerPermission({
            aliases: ['se.clone.page'],
            rules: ['se.cloneable.page']
        });

        permissionService.registerPermission({
            aliases: ['se.context.menu.edit.component'],
            rules: ['se.write.component', 'se.page.belongs.to.experience']
        });

        permissionService.registerPermission({
            aliases: ['se.context.menu.drag.and.drop.component'],
            rules: ['se.write.slot', 'se.write.component', 'se.page.belongs.to.experience']
        });

        permissionService.registerPermission({
            aliases: ['se.edit.page.link', 'se.delete.page.menu', 'se.shared.slot.override.options', 'se.revert.to.shared.slot.link'],
            rules: ['se.write.page', 'se.page.belongs.to.experience', 'se.content.catalog.non.active']
        });

        permissionService.registerPermission({
            aliases: ['se.clone.component'],
            rules: ['se.write.component', 'se.page.belongs.to.experience']
        });
    });
