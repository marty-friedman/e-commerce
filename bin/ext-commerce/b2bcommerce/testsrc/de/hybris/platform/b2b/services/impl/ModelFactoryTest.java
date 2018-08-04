/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.b2b.services.impl;

import de.hybris.platform.b2b.testframework.ModelFactory;
import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.variants.model.VariantProductModel;

import javax.annotation.Resource;

import org.junit.Test;

import junit.framework.Assert;


public class ModelFactoryTest extends ServicelayerTransactionalTest
{
	@Resource
	private ModelFactory modelFactory;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private CatalogService catalogService;

	@Test
	public void shouldCreateCatalogVersion()
	{
		String catalogId = "myCatalog";
		String catalogVersion = "Online";

		CatalogVersionModel cv = modelFactory.createCatalogVersion(catalogId, catalogVersion);

		Assert.assertEquals(catalogVersion, cv.getVersion());
		Assert.assertNotNull(cv.getCatalog());
		Assert.assertEquals(catalogId, cv.getCatalog().getId());

		CatalogVersionModel foundCV = catalogVersionService.getCatalogVersion(catalogId, catalogVersion);
		Assert.assertNotNull(foundCV);
	}

	@Test
	public void shouldCreateACatalog()
	{
		String catalogId = "myCatalog";

		CatalogModel catalog = modelFactory.createCatalog(catalogId);

		Assert.assertEquals(catalogId, catalog.getId());

		CatalogModel foundCatalog = catalogService.getCatalogForId(catalogId);
		Assert.assertNotNull(foundCatalog);
		Assert.assertEquals(catalogId, foundCatalog.getId());
	}

	@Test
	public void shouldCreateAndUpdateAProduct()
	{
		String code = "myCode";

		CatalogVersionModel cv = modelFactory.createCatalogVersion("myCatalog", "Online");
		ProductModel p = modelFactory.createProduct(code, cv, VariantProductModel._TYPECODE, modelFactory.createUnit("STK"));

		Assert.assertEquals(code, p.getCode());
	}

}
