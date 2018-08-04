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
package de.hybris.platform.sap.productconfig.runtime.cps.pricing.impl;

import de.hybris.platform.sap.productconfig.runtime.cps.CharonPricingFacade;
import de.hybris.platform.sap.productconfig.runtime.cps.cache.CPSSessionCache;
import de.hybris.platform.sap.productconfig.runtime.cps.constants.SapproductconfigruntimecpsConstants;
import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.ConfigurationMasterDataService;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.pricing.CPSMasterDataVariantPriceKey;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.ConditionPurpose;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingDocumentInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingDocumentResult;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.pricing.CPSValuePrice;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.pricing.CPSValuePriceInfo;
import de.hybris.platform.sap.productconfig.runtime.cps.pricing.PricingConfigurationParameterCPS;
import de.hybris.platform.sap.productconfig.runtime.cps.pricing.PricingHandler;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigModelFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.CsticQualifier;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigModelFactoryImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceSummaryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceValueUpdateModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.internal.service.ServicelayerUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;


/**
 * Default implementation of {@link PricingHandler}. Responsible for calling the pricing service through charon, caching
 * price data and calculating delta prices
 */
public class PricingHandlerImpl implements PricingHandler
{
	private Converter<CPSConfiguration, PricingDocumentInput> pricingDocumentInputConverter;
	private Converter<CPSMasterDataKnowledgeBaseContainer, PricingDocumentInput> pricingDocumentInputKBConverter;
	private Converter<PricingDocumentResult, Map<CPSMasterDataVariantPriceKey, CPSValuePrice>> pricesMapConverter;
	private CharonPricingFacade charonPricingFacade;
	private PricingConfigurationParameterCPS pricingConfigurationParameter;
	private ConfigurationMasterDataService masterDataService;
	private ConfigModelFactory configModelFactory;
	private CPSSessionCache sessionCache;

	private static final String SAP_PRODUCT_CONFIG_MODEL_FACTORY = "sapProductConfigModelFactory";
	private static final Logger LOG = Logger.getLogger(PricingHandlerImpl.class);

	@Override
	public PriceSummaryModel getPriceSummary(final String configId) throws PricingEngineException
	{
		updatePricingDocument(configId);
		final PriceSummaryModel priceSummary = getConfigModelFactory().createInstanceOfPriceSummaryModel();
		priceSummary.setBasePrice(getBasePrice(configId));
		priceSummary.setCurrentTotalPrice(getCurrentTotalPrice(configId));
		priceSummary.setSelectedOptionsPrice(getSelectedOptionsPrice(configId));
		return priceSummary;
	}

	protected void updatePricingDocument(final String configId) throws PricingEngineException
	{
		final PricingDocumentInput pricingInput = getSessionCache().getPricingDocumentInput(configId);
		if (pricingInput == null)
		{
			throw new IllegalStateException("No pricing input found for config id " + configId);
		}
		final PricingDocumentResult pricingResult = getCharonPricingFacade().createPricingDocument(pricingInput);
		storePricingResult(configId, pricingResult);
	}

	@Override
	public void preparePricingDocumentInput(final CPSConfiguration configuration)
	{
		final PricingDocumentInput pricingInput = getPricingDocumentInputConverter().convert(configuration);
		storePricingInput(configuration.getId(), pricingInput);
	}

	protected void storePricingResult(final String id, final PricingDocumentResult pricingResult)
	{
		getSessionCache().setPricingDocumentResult(id, pricingResult);
	}

	protected void storePricingInput(final String id, final PricingDocumentInput pricingInput)
	{
		getSessionCache().setPricingDocumentInput(id, pricingInput);
	}

	protected PricingDocumentResult retrievePricingDocument(final String configId)
	{
		return getSessionCache().getPricingDocumentResult(configId);
	}

	protected Converter<CPSConfiguration, PricingDocumentInput> getPricingDocumentInputConverter()
	{
		return pricingDocumentInputConverter;
	}

	/**
	 * @param pricingDocumentInputConverter
	 *           Converter into input for pricing REST service
	 */
	@Required
	public void setPricingDocumentInputConverter(
			final Converter<CPSConfiguration, PricingDocumentInput> pricingDocumentInputConverter)
	{
		this.pricingDocumentInputConverter = pricingDocumentInputConverter;
	}

	protected CharonPricingFacade getCharonPricingFacade()
	{
		return charonPricingFacade;
	}

	/**
	 * @param charonPricingFacade
	 *           Charon facade, wraps direct REST calls
	 */
	@Required
	public void setCharonPricingFacade(final CharonPricingFacade charonPricingFacade)
	{
		this.charonPricingFacade = charonPricingFacade;
	}

	protected PriceModel getCurrentTotalPrice(final String configId)
	{
		final PricingDocumentResult pricingDocument = retrievePricingDocument(configId);
		if (pricingDocument == null)
		{
			return PriceModel.NO_PRICE;
		}
		return createPriceModel(pricingDocument.getDocumentCurrencyUnit(), pricingDocument.getNetValue());
	}

	protected PriceModel getSelectedOptionsPrice(final String configId)
	{
		return getPriceFromConditionsWithPurpose(getPricingConfigurationParameter().getTargetForSelectedOptions(), configId);
	}

	protected PriceModel getBasePrice(final String configId)
	{
		return getPriceFromConditionsWithPurpose(getPricingConfigurationParameter().getTargetForBasePrice(), configId);
	}

	protected PriceModel getPriceFromConditionsWithPurpose(final String pricingKey, final String configId)
	{

		final PricingDocumentResult pricingDocument = retrievePricingDocument(configId);
		ConditionPurpose purposeFound = null;
		if (pricingDocument == null || pricingDocument.getConditionsWithPurpose() == null || pricingKey == null)
		{
			return PriceModel.NO_PRICE;
		}
		else
		{
			for (final ConditionPurpose purpose : pricingDocument.getConditionsWithPurpose())
			{
				if (pricingKey.equals(purpose.getPurpose()))
				{
					purposeFound = purpose;
					break;
				}
			}
		}
		if (purposeFound == null)
		{
			return PriceModel.NO_PRICE;
		}

		return createPriceModel(pricingDocument.getDocumentCurrencyUnit(), purposeFound.getValue());
	}

	protected PriceModel createPriceModel(final String currency, final Double valuePrice)
	{
		final PriceModel price = getConfigModelFactory().createInstanceOfPriceModel();
		price.setCurrency(currency);
		price.setPriceValue(BigDecimal.valueOf(valuePrice.doubleValue()));
		return price;
	}

	protected PricingDocumentResult retrieveVariantConditions(final String kbId) throws PricingEngineException
	{
		//We prepare here a pricing document input not for the "real" pricing call of a configuration runtime state,
		//but for a "simulated" KB based call.
		//The purpose of this call is to retrieve values of surcharges that are assigned to characteristic values of the related KB products.
		//We use these values for calculation of the delta prices.
		//In this case productId is used as "itemId"
		//(this is OK since even if a product is used several time inside a KB, the surchage values for the product are always the same)
		//and quantity is always set to 1 independent from the BOM quantity.
		final PricingDocumentInput pricingDocumentInput = getPricingDocumentInputKBConverter()
				.convert(getMasterDataService().getMasterData(kbId));
		return getCharonPricingFacade().createPricingDocument(pricingDocumentInput);
	}

	protected Map<CPSMasterDataVariantPriceKey, CPSValuePrice> getPricesMap(final String kbId) throws PricingEngineException
	{
		if (getSessionCache().getValuePricesMap(kbId) == null)
		{
			final PricingDocumentResult pricingResult = retrieveVariantConditions(kbId);
			final Map<CPSMasterDataVariantPriceKey, CPSValuePrice> pricesMap = getPricesMapConverter().convert(pricingResult);
			getSessionCache().setValuePricesMap(kbId, pricesMap);
			return pricesMap;
		}
		else
		{
			return getSessionCache().getValuePricesMap(kbId);
		}
	}

	@Override
	public void fillValuePrices(final String kbId, final PriceValueUpdateModel updateModel) throws PricingEngineException
	{
		if (updateModel != null)
		{
			final Map<String, CPSValuePriceInfo> valuePrices = getValuePrices(kbId, updateModel);
			final boolean useDeltaPrices = getPricingConfigurationParameter().showDeltaPrices();
			fillPriceInfos(valuePrices, updateModel, useDeltaPrices);
		}
	}

	protected Map<String, CPSValuePriceInfo> getValuePrices(final String kbId, final PriceValueUpdateModel updateModel)
			throws PricingEngineException
	{

		final Pair<BigDecimal, Map<String, CPSValuePriceInfo>> pair = getSelectedValuePriceAndValuePricesMap(kbId, updateModel);
		final Map<String, CPSValuePriceInfo> mapValuePriceInfo = pair.getRight();
		calculateDeltaPrices(pair.getLeft(), mapValuePriceInfo);
		return mapValuePriceInfo;
	}

	protected Pair<BigDecimal, Map<String, CPSValuePriceInfo>> getSelectedValuePriceAndValuePricesMap(final String kbId,
			final PriceValueUpdateModel updateModel) throws PricingEngineException
	{
		final Map<String, CPSValuePriceInfo> mapValuePriceInfo = new HashMap<>();
		BigDecimal selectedValuePrice = null;

		final CsticQualifier qualifier = updateModel.getCsticQualifier();
		final String itemKey = qualifier.getInstanceName();
		final String csticId = qualifier.getCsticName();
		final Set<String> possibleValues = getMasterDataService().getPossibleValueIds(kbId, csticId);
		final Set<String> specificPossibleValues = getMasterDataService().getSpecificPossibleValueIds(kbId, itemKey,
				determineInstanceType(kbId, itemKey), csticId);
		final boolean multiValued = getMasterDataService().getCharacteristic(kbId, csticId).isMultiValued();
		if (!specificPossibleValues.isEmpty())
		{
			final CPSValuePrice valuePrice = getValuePrice(kbId, itemKey, csticId, specificPossibleValues.iterator().next());
			if (valuePrice != null)
			{
				final String currency = valuePrice.getCurrency();
				for (final String possibleValue : possibleValues)
				{
					final BigDecimal value = addValueToValuePriceInfoMap(kbId, mapValuePriceInfo, itemKey, csticId, possibleValue,
							currency);
					selectedValuePrice = updateSelectedValuePrice(multiValued, possibleValue, value, updateModel.getSelectedValues(),
							selectedValuePrice);
				}
			}
		}
		return Pair.of(selectedValuePrice, mapValuePriceInfo);
	}

	protected String determineInstanceType(final String kbId, final String itemKey)
	{
		final CPSMasterDataKnowledgeBaseContainer masterData = getMasterDataService().getMasterData(kbId);
		if (masterData == null)
		{
			throw new IllegalArgumentException("KB not found: " + kbId);
		}
		final boolean isProduct = masterData.getProducts().containsKey(itemKey);
		final boolean isClassNode = masterData.getClasses().containsKey(itemKey);
		if (isProduct)
		{
			//in case the itemKey is both part of the product list _and_ part of the class list,
			//we return type 'MARA' -> in this case the class probably backs a configurable sub item
			//and does not represent a class node
			if (isClassNode && LOG.isDebugEnabled())
			{
				LOG.debug(itemKey + " is part of the KB as product and as class. Value prices are compiled for product");
			}
			return SapproductconfigruntimecpsConstants.ITEM_TYPE_MARA;
		}
		else if (isClassNode)
		{
			return SapproductconfigruntimecpsConstants.ITEM_TYPE_KLAH;
		}
		throw new IllegalStateException("Key not found: " + itemKey);
	}

	protected BigDecimal addValueToValuePriceInfoMap(final String kbId, final Map<String, CPSValuePriceInfo> mapValuePriceInfo,
			final String productId, final String csticId, final String possibleValue, final String currency)
					throws PricingEngineException
	{
		final CPSValuePriceInfo valuePriceInfo = new CPSValuePriceInfo();

		CPSValuePrice valuePrice = getValuePrice(kbId, productId, csticId, possibleValue);
		BigDecimal value = null;
		if (valuePrice != null)
		{
			value = valuePrice.getValuePrice();
		}
		else
		{
			value = BigDecimal.ZERO;
			valuePrice = new CPSValuePrice();
			valuePrice.setValuePrice(value);
			valuePrice.setCurrency(currency);
		}
		valuePriceInfo.setValuePrice(valuePrice);

		mapValuePriceInfo.put(possibleValue, valuePriceInfo);
		return value;
	}

	protected BigDecimal updateSelectedValuePrice(final boolean isMultiValued, final String possibleValue,
			final BigDecimal valuePrice, final List<String> selectedValues, final BigDecimal oldSelectedValuePrice)
	{
		if (oldSelectedValuePrice != null)
		{
			return oldSelectedValuePrice;
		}
		if (!selectedValues.contains(possibleValue) || isMultiValued)
		{
			return null;
		}
		return valuePrice;
	}

	protected void calculateDeltaPrices(final BigDecimal selectedValuePrice,
			final Map<String, CPSValuePriceInfo> mapValuePriceInfo)
	{
		for (final CPSValuePriceInfo valuePriceInfo : mapValuePriceInfo.values())
		{
			final CPSValuePrice valuePrice = valuePriceInfo.getValuePrice();
			if (valuePrice != null && valuePrice.getValuePrice() != null)
			{
				final CPSValuePrice deltaPrice = new CPSValuePrice();
				deltaPrice.setCurrency(valuePrice.getCurrency());
				if (selectedValuePrice != null)
				{
					deltaPrice.setValuePrice(valuePrice.getValuePrice().subtract(selectedValuePrice));
					valuePriceInfo.setDeltaPrice(deltaPrice);
				}
				else
				{
					deltaPrice.setValuePrice(valuePrice.getValuePrice());
					valuePriceInfo.setDeltaPrice(deltaPrice);
				}
			}
		}
	}

	protected CPSValuePrice getValuePrice(final String kbId, final String productId, final String characteristicId,
			final String valueId) throws PricingEngineException
	{
		final String pricingKey = getMasterDataService().getValuePricingKey(kbId, productId, characteristicId, valueId);

		if (pricingKey == null)
		{
			return null;
		}

		final Map<CPSMasterDataVariantPriceKey, CPSValuePrice> pricesMap = getPricesMap(kbId);
		final CPSMasterDataVariantPriceKey priceKey = new CPSMasterDataVariantPriceKey();
		priceKey.setVariantConditionKey(pricingKey);
		priceKey.setProductId(productId);

		return pricesMap.get(priceKey);
	}

	protected boolean isValueSelected(final String value, final List<CsticValueModel> cpsValues)
	{

		if (CollectionUtils.isNotEmpty(cpsValues))
		{

			return cpsValues.//
					stream().//
					anyMatch(v -> value.equals(v.getName()));
		}
		return false;
	}

	protected PriceModel createPriceModelFromCPSValue(final CPSValuePrice valuePrice)
	{
		final PriceModel priceModel = getConfigModelFactory().createInstanceOfPriceModel();
		if (valuePrice != null)
		{
			priceModel.setPriceValue(valuePrice.getValuePrice());
			priceModel.setCurrency(valuePrice.getCurrency());
		}
		else
		{
			return PriceModel.NO_PRICE;
		}
		return priceModel;
	}


	protected void fillPriceInfos(final Map<String, CPSValuePriceInfo> valuePrices, final PriceValueUpdateModel updateModel,
			final boolean showDeltaPrices)
	{
		final Map<String, PriceModel> valuePricesMap = new HashMap<>();
		for (final Map.Entry<String, CPSValuePriceInfo> entry : valuePrices.entrySet())
		{
			final String valueName = entry.getKey();
			final CPSValuePriceInfo cpsValuePriceInfo = entry.getValue();
			if (cpsValuePriceInfo != null)
			{
				CPSValuePrice price = null;
				if (showDeltaPrices)
				{
					price = cpsValuePriceInfo.getDeltaPrice();
				}
				else
				{
					price = cpsValuePriceInfo.getValuePrice();
				}
				valuePricesMap.put(valueName, createPriceModelFromCPSValue(price));
			}
			else
			{
				valuePricesMap.put(valueName, createPriceModelFromCPSValue(null));
			}
		}
		updateModel.setValuePrices(valuePricesMap);
		updateModel.setShowDeltaPrices(showDeltaPrices);
	}

	protected PricingConfigurationParameterCPS getPricingConfigurationParameter()
	{
		return pricingConfigurationParameter;
	}

	/**
	 * @param pricingConfigurationParameter
	 *           Pricing settings from customizing
	 */
	@Required
	public void setPricingConfigurationParameter(final PricingConfigurationParameterCPS pricingConfigurationParameter)
	{
		this.pricingConfigurationParameter = pricingConfigurationParameter;
	}

	protected Converter<CPSMasterDataKnowledgeBaseContainer, PricingDocumentInput> getPricingDocumentInputKBConverter()
	{
		return pricingDocumentInputKBConverter;
	}

	/**
	 * @param pricingDocumentInputKBConverter
	 *           Converter from KB master data into pricing service REST input
	 */
	@Required
	public void setPricingDocumentInputKBConverter(
			final Converter<CPSMasterDataKnowledgeBaseContainer, PricingDocumentInput> pricingDocumentInputKBConverter)
	{
		this.pricingDocumentInputKBConverter = pricingDocumentInputKBConverter;
	}

	protected Converter<PricingDocumentResult, Map<CPSMasterDataVariantPriceKey, CPSValuePrice>> getPricesMapConverter()
	{
		return pricesMapConverter;
	}

	/**
	 * @param pricesMapConverter
	 *           Converter from pricing service REST ouput to prices map
	 */
	@Required
	public void setPricesMapConverter(
			final Converter<PricingDocumentResult, Map<CPSMasterDataVariantPriceKey, CPSValuePrice>> pricesMapConverter)
	{
		this.pricesMapConverter = pricesMapConverter;
	}

	protected ConfigurationMasterDataService getMasterDataService()
	{
		return masterDataService;
	}

	/**
	 * @param masterDataService
	 *           master data service
	 */
	@Required
	public void setMasterDataService(final ConfigurationMasterDataService masterDataService)
	{
		this.masterDataService = masterDataService;
	}

	protected ConfigModelFactory getConfigModelFactory()
	{
		if (this.configModelFactory == null)
		{
			final ApplicationContext applicationContext = getApplicationContext();
			if (applicationContext.containsBean(SAP_PRODUCT_CONFIG_MODEL_FACTORY))
			{
				this.configModelFactory = (ConfigModelFactory) applicationContext.getBean(SAP_PRODUCT_CONFIG_MODEL_FACTORY);
				return this.configModelFactory;
			}
			LOG.warn("We assume that we are running in the integration test context.");
			this.configModelFactory = new ConfigModelFactoryImpl();
			return this.configModelFactory;
		}
		return this.configModelFactory;
	}

	protected ApplicationContext getApplicationContext()
	{
		return ServicelayerUtils.getApplicationContext();
	}

	/**
	 * @param configModelFactory
	 *           Config model factory, responsible for instantiating service layer models not related to hybris
	 *           persistence
	 */
	@Required
	public void setConfigModelFactory(final ConfigModelFactory configModelFactory)
	{
		this.configModelFactory = configModelFactory;
	}

	protected CPSSessionCache getSessionCache()
	{
		return sessionCache;
	}

	/**
	 * @param sessionCache
	 *           Session cache
	 */
	@Required
	public void setSessionCache(final CPSSessionCache sessionCache)
	{
		this.sessionCache = sessionCache;
	}

	@Override
	public void fillValuePrices(final String kbId, final CsticModel cstic) throws PricingEngineException
	{
		if (cstic != null)
		{
			final PriceValueUpdateModel updateModel = createUpdateModel(cstic);
			final Map<String, CPSValuePriceInfo> valuePrices = getValuePrices(kbId, updateModel);
			fillValuePriceInfos(valuePrices, cstic);
		}

	}

	protected boolean isValuePriceZero(final CPSValuePriceInfo priceInfo)
	{
		return BigDecimal.ZERO.equals(priceInfo.getValuePrice().getValuePrice());
	}


	protected void fillValuePriceInfos(final Map<String, CPSValuePriceInfo> valuePrices, final CsticModel cstic)
	{
		for (final CsticValueModel value : cstic.getAssignedValues())
		{
			final CPSValuePriceInfo priceInfo = valuePrices.get(value.getName());
			if (priceInfo != null && !isValuePriceZero(priceInfo))
			{

				value.setValuePrice(createPriceModelFromCPSValue(priceInfo.getValuePrice()));
			}
			else
			{
				value.setValuePrice(createPriceModelFromCPSValue(null));
			}
		}

	}

	protected PriceValueUpdateModel createUpdateModel(final CsticModel cstic)
	{
		final PriceValueUpdateModel updateModel = new PriceValueUpdateModel();
		final CsticQualifier cq = new CsticQualifier();
		cq.setInstanceName(cstic.getInstanceName());
		cq.setCsticName(cstic.getName());
		updateModel.setCsticQualifier(cq);
		updateModel.setSelectedValues(new ArrayList<>());
		return updateModel;
	}


}
