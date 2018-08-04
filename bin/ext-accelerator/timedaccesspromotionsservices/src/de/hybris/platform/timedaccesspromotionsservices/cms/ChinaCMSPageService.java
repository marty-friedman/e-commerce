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
package de.hybris.platform.timedaccesspromotionsservices.cms;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ProductPageModel;
import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cms2.servicelayer.services.impl.DefaultCMSPageService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;

import java.util.Collection;


/**
 * An implementation to override method getPageForProduct.
 */
public class ChinaCMSPageService extends DefaultCMSPageService
{

	@Override
	public ProductPageModel getPageForProduct(final ProductModel product) throws CMSItemNotFoundException
	{
		final ProductPageModel page = (ProductPageModel) this.getSinglePage("ProductPage");
		if (page != null)
		{
			LOG.debug("Only one ProductPage for product [" + product.getCode() + "] found. Considering this as default.");
			return page;
		}
		else
		{
			final ComposedTypeModel type = getTypeService().getComposedTypeForCode("ProductPage");
			final Collection versions = getCatalogVersionService().getSessionCatalogVersions();
			final RestrictionData data = getCmsDataFactory().createRestrictionData(product);
			final Collection pages = getCmsPageDao().findAllPagesByTypeAndCatalogVersions(type, versions);
			final Collection result = getCmsRestrictionService().evaluatePages(pages, data);
			if (result.isEmpty())
			{
				throw new CMSItemNotFoundException("No page for product [" + product.getCode() + "] found.");
			}
			else
			{
				if (result.size() > 1)
				{
					LOG.warn("More than one page found for product [" + product.getCode() + "]. Returning default.");
				}

				return (ProductPageModel) result.iterator().next();
			}
		}
	}
}
