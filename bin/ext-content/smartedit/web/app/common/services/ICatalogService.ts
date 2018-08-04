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
export interface ICatalog {
	name: string;
	uuid?: string;
}

export interface ICatalogService {
	/**
	 * list catalogs for a given site
	 *
	 * @param {=string} siteId the site identifier
	 *
	 * @return {ng.IPromise<ICatalog[]>} list of catalogs for the given site
	 */
	getCatalogs(siteId?: string): ng.IPromise<ICatalog[]>;
}
