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
package de.hybris.platform.sap.productconfig.runtime.cps.populator.impl;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataPossibleValueSpecific;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataCharacteristicSpecificContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataProductContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingItemInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSVariantCondition;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;


/**
 * Populates the pricing item input data for querying static pricing information, such as value prices, based on the
 * configuration master data.<br>
 * <br>
 * We are caching the value prices per product. This of course assumes that a product is part of a KB only in
 * <b> one </b> unit, the base unit. This assumption is correct for IPC/SSC in general, even if it is possible to create
 * BOM's with different UOMs for the same product: You can achieve this by assigning different unit of issues on plant
 * level. Such situations however lead to errors during KB generation. *
 */
public class PricingItemInputKBProductPopulator extends AbstractPricingItemInputPopulator
		implements Populator<CPSMasterDataProductContainer, PricingItemInput>
{
	private static final Logger LOG = Logger.getLogger(PricingItemInputKBProductPopulator.class);

	@Override
	public void populate(final CPSMasterDataProductContainer source, final PricingItemInput target)
	{
		fillCoreAttributes(source.getId(), createQty(BigDecimal.ONE, getIsoUOM(source)), target);
		fillPricingAttributes(source.getId(), target);
		fillAccessDates(target);
		fillVariantConditions(source, target);
	}

	protected void fillVariantConditions(final CPSMasterDataProductContainer source, final PricingItemInput target)
	{
		target.setVariantConditions(new ArrayList<>());
		handleCstics(target, source.getCstics());
	}

	protected void handleCstics(final PricingItemInput target,
			final Map<String, CPSMasterDataCharacteristicSpecificContainer> cstics)
	{
		final Iterator<Entry<String, CPSMasterDataCharacteristicSpecificContainer>> csticsIterator = cstics.entrySet().iterator();
		while (csticsIterator.hasNext())
		{
			final Entry<String, CPSMasterDataCharacteristicSpecificContainer> csticSpecificContainerEntry = csticsIterator.next();
			final CPSMasterDataCharacteristicSpecificContainer csticSpecificContainer = csticSpecificContainerEntry.getValue();
			addVariantConditionsForPossibleValueSpecifics(target, csticSpecificContainer);
		}
	}

	protected void addVariantConditionsForPossibleValueSpecifics(final PricingItemInput target,
			final CPSMasterDataCharacteristicSpecificContainer csticSpecificContainer)
	{
		final Iterator<Entry<String, CPSMasterDataPossibleValueSpecific>> valuesIterator = csticSpecificContainer
				.getPossibleValueSpecifics().entrySet().iterator();
		while (valuesIterator.hasNext())
		{
			final Entry<String, CPSMasterDataPossibleValueSpecific> possibleValueSpecificEntry = valuesIterator.next();
			final CPSMasterDataPossibleValueSpecific possibleValueSpecific = possibleValueSpecificEntry.getValue();
			target.getVariantConditions().add(createVariantCondition(possibleValueSpecific.getVariantConditionKey()));
		}
	}

	protected CPSVariantCondition createVariantCondition(final String key)
	{
		final CPSVariantCondition variantCondition = new CPSVariantCondition();
		variantCondition.setFactor(String.valueOf(1));
		variantCondition.setKey(key);
		return variantCondition;
	}

	protected String getIsoUOM(final CPSMasterDataProductContainer product)
	{
		final String productId = product.getId();
		try
		{
			final ProductModel productModel = getProductService().getProductForCode(productId);
			final UnitModel unitModel = productModel.getUnit();
			return getPricingConfigurationParameter().retrieveUnitIsoCode(unitModel);
		}
		catch (final UnknownIdentifierException ex)
		{
			//In this case we fall back to the unit of measure of the root item
			final String unitOfMeasure = product.getUnitOfMeasure();
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Use root unit of measure " + unitOfMeasure + " for product " + productId);
			}
			return unitOfMeasure;
		}
	}



}
