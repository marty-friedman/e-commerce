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
package de.hybris.platform.sap.productconfig.runtime.cps.cache;

import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.pricing.CPSMasterDataVariantPriceKey;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingDocumentInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingDocumentResult;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.pricing.CPSValuePrice;

import java.util.List;
import java.util.Map;


/**
 * Cache for CPS specific attributes in user session.
 */


public interface CPSSessionCache
{

	/**
	 * Sets ValuePrice into session cache
	 *
	 * @param kbId
	 *           knowledgebase id
	 * @param valuePricesMap
	 *           map of value prices
	 *
	 */
	void setValuePricesMap(String kbId, Map<CPSMasterDataVariantPriceKey, CPSValuePrice> valuePricesMap);


	/**
	 * Get ValuePrice from session cache
	 *
	 * @param kbId
	 *           knowledgebase id
	 * @return map of value prices
	 */
	Map<CPSMasterDataVariantPriceKey, CPSValuePrice> getValuePricesMap(String kbId);

	/**
	 * Sets the pricing document result into session cache
	 *
	 * @param configId
	 *           id of the runtime configuration
	 * @param pricingDocumentResult
	 *           pricing document result
	 */
	void setPricingDocumentResult(String configId, PricingDocumentResult pricingDocumentResult);

	/**
	 * Gets the pricing document result from session cache
	 *
	 * @param configId
	 *           id of the runtime configuration
	 * @return pricing document result
	 **/
	PricingDocumentResult getPricingDocumentResult(String configId);

	/**
	 * Sets the pricing document input into session cache
	 *
	 * @param configId
	 *           id of the runtime configuration
	 * @param pricingDocumentInput
	 *           pricing document input
	 */
	void setPricingDocumentInput(String configId, PricingDocumentInput pricingDocumentInput);

	/**
	 * Gets the pricing document input from session cache
	 *
	 * @param configId
	 *           id of the runtime configuration
	 * @return pricing document input
	 **/
	PricingDocumentInput getPricingDocumentInput(String configId);

	/**
	 * Purges the cache for the current session
	 */
	void purge();

	/**
	 * Purges all pricing related data from the cache for the current session
	 */
	void purgePrices();


	/**
	 * Sets cookies per configId
	 *
	 * @param configId
	 *           ID of runtime configuration
	 * @param cookieList
	 */
	void setCookies(String configId, List<String> cookieList);



	/**
	 * @param configId
	 * @return List of cookies per configId
	 */
	List<String> getCookies(String configId);



	/**
	 * Removes cookies per configId
	 *
	 * @param configId
	 */
	void removeCookies(String configId);


	/**
	 * Removes pricing document input
	 *
	 * @param configId
	 */
	void removePricingDocumentInput(String configId);


	/**
	 * Removes pricing document result
	 *
	 * @param configId
	 */
	void removePricingDocumentResult(String configId);

	/**
	 * Sets the eTag into session cache
	 *
	 * @param configId
	 *           id of the runtime configuration
	 * @param eTag
	 *           eTag
	 */
	void setETag(String configId, String eTag);

	/**
	 * Gets the eTag from session cache
	 *
	 * @param configId
	 *           id of the runtime configuration
	 * @return eTag
	 **/
	String getETag(String configId);


	/**
	 * Removes eTag per configId
	 *
	 * @param configId
	 */
	void removeETag(String configId);

}
