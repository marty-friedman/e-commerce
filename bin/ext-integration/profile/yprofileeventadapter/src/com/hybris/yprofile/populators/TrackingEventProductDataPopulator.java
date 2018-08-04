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
package com.hybris.yprofile.populators;

import com.hybris.yprofile.dto.TrackingEvent;
import de.hybris.eventtracking.publisher.csv.model.TrackingEventCsvData;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.site.BaseSiteService;
import com.hybris.yprofile.common.Utils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.util.*;
import java.util.stream.Collectors;

/**
 * TrackingEventProductDataPopulator provides additional data regarding the product and its category structure
 * for ProductDetailPageViewEvent
 */
public class TrackingEventProductDataPopulator implements Populator<TrackingEventCsvData , TrackingEvent> {

    public static final String PRODUCT_DETAIL_PAGE_VIEW_EVENT = "ProductDetailPageViewEvent";
    private static final Logger LOG = Logger.getLogger(TrackingEventProductDataPopulator.class);
    private BaseSiteService baseSiteService;
    private CatalogVersionService catalogVersionService;
    private ProductService productService;

    @Override
    public void populate(TrackingEventCsvData source, TrackingEvent target) {

        if (shouldPopulateProductData(source)){

            try {
                setupSiteAndCatalogVersion(source);

                ProductModel productModel = getProductService().getProductForCode(source.getProductId());

                HashMap<String, Object> profileCustom = new HashMap<>();

                profileCustom.put("productName", StringUtils.trimToEmpty(source.getProductName()));
                profileCustom.put("productDescription", StringUtils.trimToEmpty(productModel.getSummary()));
                profileCustom.put("productPrice", StringUtils.trimToEmpty(source.getProductPrice()));
                profileCustom.put("productBrand", StringUtils.trimToEmpty(getBrand(productModel)));
                profileCustom.put("productCategories",  StringUtils.trimToEmpty(getCategories(productModel)));

                target.set_profile_custom(profileCustom);

            } catch (Exception ex){
                LOG.debug("Could not retrieve product information for site : '" +  getSiteId(source) + "' and product: '" + StringUtils.trimToEmpty(source.getProductName()) + "'", ex);
            }
        }

    }

    protected boolean shouldPopulateProductData(TrackingEventCsvData source) {
        return StringUtils.isNotBlank(source.getProductId()) &&
                PRODUCT_DETAIL_PAGE_VIEW_EVENT.equals(source.getEventType()) &&
                StringUtils.isNotBlank(getSiteId(source));
    }

    protected String getCategories(ProductModel productModel) {

        Collection<CategoryModel> categories = productModel.getSupercategories();
        return categories.stream()
                .map(categoryModel -> categoryModel.getName())
                        .collect(Collectors.joining(","));
    }

    protected String getBrand(ProductModel productModel) {
        return productModel.getManufacturerName();
    }

    /**
     * Sets up the base site id in session to get the correct catalog information
     */
    protected void setupSiteAndCatalogVersion(TrackingEventCsvData source) {

        if (getBaseSiteService().getCurrentBaseSite() == null) {
            getBaseSiteService().setCurrentBaseSite(getSiteId(source), true);
        }

        BaseSiteModel baseSiteModel = getBaseSiteService().getCurrentBaseSite();

        List<CatalogModel> productCatalogs = getBaseSiteService().getProductCatalogs(baseSiteModel);

        if (!productCatalogs.isEmpty()){

            CatalogModel catalogModel = productCatalogs.iterator().next();
            CatalogVersionModel activeCatalogVersion = catalogModel.getActiveCatalogVersion();
            getCatalogVersionService().setSessionCatalogVersion(catalogModel.getId(), activeCatalogVersion.getVersion());
        }
    }

    protected String getSiteId(TrackingEventCsvData source){
        return Utils.remapSiteId(source.getIdsite());
    }

    public BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    @Required
    public void setBaseSiteService(BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }

    public CatalogVersionService getCatalogVersionService() {
        return catalogVersionService;
    }

    @Required
    public void setCatalogVersionService(CatalogVersionService catalogVersionService) {
        this.catalogVersionService = catalogVersionService;
    }

    public ProductService getProductService() {
        return productService;
    }

    @Required
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }
}
