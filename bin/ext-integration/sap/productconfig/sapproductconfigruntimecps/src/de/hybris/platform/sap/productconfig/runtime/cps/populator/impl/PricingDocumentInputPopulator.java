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
import de.hybris.platform.sap.productconfig.runtime.cps.constants.SapproductconfigruntimecpsConstants;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingDocumentInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingItemInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSItem;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.List;


/**
 * Popuplates the pricing document input data for querrying dynamic pricing information, such as total values, based on
 * the configuration runtime data.
 */
public class PricingDocumentInputPopulator extends AbstractPricingDocumentInputPopulator
		implements Populator<CPSConfiguration, PricingDocumentInput>
{


	private Converter<CPSItem, PricingItemInput> pricingItemInputConverter;

	@Override
	public void populate(final CPSConfiguration source, final PricingDocumentInput target)
	{
		fillCoreAttributes(target);
		fillPricingItemsInput(source, target);
	}

	protected void fillPricingItemsInput(final CPSConfiguration source, final PricingDocumentInput target)
	{
		target.setItems(new ArrayList<PricingItemInput>());
		fillPricingItemInput(source.getRootItem(), target.getItems());

	}

	protected void fillPricingItemInput(final CPSItem item, final List<PricingItemInput> target)
	{
		PricingItemInput pricingItemInput = null;
		if (SapproductconfigruntimecpsConstants.ITEM_TYPE_MARA.equals(item.getType()))
		{
			pricingItemInput = getPricingItemInputConverter().convert(item);
			target.add(pricingItemInput);
		}
		fillPricingSubItemInput(item, pricingItemInput);
	}

	protected void fillPricingSubItemInput(final CPSItem item, final PricingItemInput pricingItemInput)
	{
		final List<CPSItem> subItems = item.getSubItems();
		if (subItems != null && pricingItemInput != null)
		{
			pricingItemInput.setSubItems(new ArrayList<>());
			for (final CPSItem subItem : subItems)
			{
				fillPricingItemInput(subItem, pricingItemInput.getSubItems());
			}
		}
	}

	protected Converter<CPSItem, PricingItemInput> getPricingItemInputConverter()
	{
		return pricingItemInputConverter;
	}

	/**
	 * @param pricingItemInputConverter
	 *           converter to create pricing item input data from CPS item data
	 */
	public void setPricingItemInputConverter(final Converter<CPSItem, PricingItemInput> pricingItemInputConverter)
	{
		this.pricingItemInputConverter = pricingItemInputConverter;
	}


}
