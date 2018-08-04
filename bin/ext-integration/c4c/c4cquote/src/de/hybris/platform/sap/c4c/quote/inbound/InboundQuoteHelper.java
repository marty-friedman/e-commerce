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
package de.hybris.platform.sap.c4c.quote.inbound;

import de.hybris.platform.core.model.order.QuoteModel;


public interface InboundQuoteHelper
{

	/**
	 * Get quote for code
	 * 
	 * @param code
	 * @return quote
	 */
	QuoteModel createQuoteSnapshot(String code, String state);

	/**
	 * Set the name of new quote
	 * 
	 * @param code
	 * @return string
	 */
	String getNameForQuote(String code);

	/**
	 * Set the previous estimated total of quote
	 * 
	 * @param code
	 * @return string
	 */
	String getPreviousEstimatedTotal(String code);

	/**
	 * Apply discount on quote current version
	 * 
	 * @param quoteId
	 */
	Double applyQuoteDiscount(String quoteId, Double discountedPrice, String userUid);
	
	/**
	 * Add taxValues to current quote version
	 * 
	 * @param quoteId
	 */
	void addTaxValue(String quoteId);

	/**
	 * Get GUID from quote's current version
	 * 
	 * @param quoteId
	 */
	String getGuid(String code);

	/**
	 * Get catalog from quote and productId
	 * 
	 * @param code
	 * @param productId
	 * @return string
	 */
	String createQuoteEntryProduct(String code, String productId);

	/**
	 * Get entryNumber after converting from C4C format to Hybris format
	 * 
	 * @param code
	 * @return string
	 */
	String convertEntryNumber(String code);
}
