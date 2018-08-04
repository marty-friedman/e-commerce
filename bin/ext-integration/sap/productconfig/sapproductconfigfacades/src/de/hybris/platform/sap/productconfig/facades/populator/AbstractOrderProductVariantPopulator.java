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
package de.hybris.platform.sap.productconfig.facades.populator;

import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.commercefacades.order.data.ConfigurationInfoData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.FeatureData;
import de.hybris.platform.commercefacades.product.data.FeatureValueData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.sapmodel.model.ERPVariantProductModel;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;


/**
 *
 */
public class AbstractOrderProductVariantPopulator
{
	private static final Logger LOG = Logger.getLogger(AbstractOrderProductVariantPopulator.class);
	private int maxNumberOfDisplayedCsticsInCart = 4;
	private Populator<ProductModel, ProductData> classificationPopulator;
	private FeatureProvider featureProvider;

	/**
	 * @return the featureProvider
	 */
	public FeatureProvider getFeatureProvider()
	{
		return featureProvider;
	}

	/**
	 * @param featureProvider
	 *           the featureProvider to set
	 */
	public void setFeatureProvider(final FeatureProvider featureProvider)
	{
		this.featureProvider = featureProvider;
	}

	/**
	 * @return the classificationPopulator
	 */
	public Populator<ProductModel, ProductData> getClassificationPopulator()
	{
		return classificationPopulator;
	}

	/**
	 * @param classificationPopulator
	 *           the classificationPopulator to set
	 */
	public void setClassificationPopulator(final Populator<ProductModel, ProductData> classificationPopulator)
	{
		this.classificationPopulator = classificationPopulator;
	}

	/**
	 * @param maxNumberOfDisplayedCsticsInCart
	 *           the maxNumberOfDisplayedCsticsInCart to set
	 */
	public void setMaxNumberOfDisplayedCsticsInCart(final int maxNumberOfDisplayedCsticsInCart)
	{
		this.maxNumberOfDisplayedCsticsInCart = maxNumberOfDisplayedCsticsInCart;
	}

	/**
	 * Transfers product variant related attributes from order entry into its DTO representation
	 *
	 * @param targetList
	 *           Order DTO entries, used to get the cart entry DTO via searching for key
	 * @param entry
	 *           Order entry model
	 */
	protected void populateAbstractOrderData(final AbstractOrderEntryModel entry, final List<OrderEntryData> targetList)
	{
		final ProductModel product = entry.getProduct();
		if (product instanceof ERPVariantProductModel)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Item " + entry.getItemtype() + " with PK " + entry.getPk() + " is a product variant ==> populating DTO.");
			}
			final ProductData productData = new ProductData();
			getClassificationPopulator().populate(product, productData);
			final List<FeatureData> features = getFeatureProvider().getListOfFeatures(productData);
			writeToTargetEntry(targetList, entry, features);
		}
		else
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("CartItem with PK " + entry.getPk() + " is NOT a product variant ==> skipping population of DTO.");
			}
		}
	}

	/**
	 * Writes result to target entry DTO
	 *
	 * @param targetList
	 *           Order DTO entries, used to get the cart entry DTO via searching for key
	 * @param sourceEntry
	 *           Order entry model
	 * @param features
	 *           List of features
	 */
	protected void writeToTargetEntry(final List<OrderEntryData> targetList, final AbstractOrderEntryModel sourceEntry,
			final List<FeatureData> features)
	{
		final OrderEntryData targetEntry = targetList.stream() //
				.filter(entry -> entry.getEntryNumber().equals(sourceEntry.getEntryNumber())) //
				.findFirst() //
				.orElse(null);
		if (targetEntry == null)
		{
			throw new IllegalArgumentException("Target items do not match source items");
		}
		adjustTargetEntryForVariant(sourceEntry, features, targetEntry);
	}

	protected void adjustTargetEntryForVariant(final AbstractOrderEntryModel sourceEntry, final List<FeatureData> features,
			final OrderEntryData targetEntry)
	{
		targetEntry.setItemPK(sourceEntry.getPk().toString());
		targetEntry.setConfigurationInfos(processFeatureList(features));
		targetEntry.getProduct().getBaseOptions().clear();
	}

	protected List<ConfigurationInfoData> processFeatureList(final List<FeatureData> features)
	{
		final List<ConfigurationInfoData> configurationInfos = new ArrayList<>();
		for (int i = 0; i < features.size() && i < maxNumberOfDisplayedCsticsInCart; i++)
		{
			final ConfigurationInfoData configInfoInline = new ConfigurationInfoData();
			populateConfigInfoData(features.get(i), configInfoInline);
			configurationInfos.add(configInfoInline);
		}
		return configurationInfos;
	}

	protected void populateConfigInfoData(final FeatureData source, final ConfigurationInfoData target)
	{
		target.setConfigurationLabel(source.getName());
		target.setConfiguratorType(ConfiguratorType.CPQCONFIGURATOR);
		target.setConfigurationValue(generateConfigurationValueLine(source));
	}

	/**
	 * Generates a string of feature values separated with the value separator for configuration display in order
	 *
	 * @param source
	 *           FeatureData
	 */
	protected String generateConfigurationValueLine(final FeatureData source)
	{
		final List<FeatureValueData> featureValues = (List) source.getFeatureValues();
		final StringBuilder builder = new StringBuilder();
		if (!CollectionUtils.isEmpty(featureValues))
		{
			for (int i = 0; i < featureValues.size(); i++)
			{
				if (i > 0)
				{
					builder.append(ConfigurationOrderEntryProductInfoModelPopulator.VALUE_SEPARATOR);
				}
				builder.append(featureValues.get(i).getValue());
			}
		}
		return builder.toString();
	}
}
