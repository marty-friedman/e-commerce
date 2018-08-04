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
package de.hybris.platform.sap.sapcarintegration.services;

import org.apache.olingo.odata2.api.ep.feed.ODataFeed;

import de.hybris.platform.commerceservices.search.pagedata.PaginationData;


/**
 * Interface to provide ODataFeeds containing all pertinent multichannel CAR information
 */
public interface MultichannelDataProviderService extends CarDataProviderService
{

	/**
	 * read multi channel orders: point of sales, SD, Hybris ...
	 * 
	 * @param customerNumber
	 * @param paginationData
	 * @return {@link ODataFeed}
	 */
	ODataFeed readMultiChannelTransactionsFeed(String customerNumber, PaginationData paginationData);

	/**
	 * * read SD order header info for a given order, selection criteria (SAPClient, customerNumber, transactionNumber,
	 * storeId = null)
	 * 
	 * @param customerNumber
	 * @param transactionNumber
	 * @return {@link ODataFeed}
	 */
	ODataFeed readSalesDocumentHeaderFeed(String customerNumber, String transactionNumber);

	/**
	 * * read SD order item info for a given transaction, selection criteria (SAPClient, customerNumber,
	 * transactionNumber, businessDayDate=null,storeId=null,transactionIndex=null)
	 * 
	 * @param customerNumber
	 * @param transactionNumber
	 * @return ODataFeed
	 */
	ODataFeed readSalesDocumentItemFeed(String customerNumber, String transactionNumber);

}
