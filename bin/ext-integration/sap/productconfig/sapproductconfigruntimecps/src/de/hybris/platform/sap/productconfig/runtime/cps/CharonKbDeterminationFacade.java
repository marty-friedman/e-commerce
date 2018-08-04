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
package de.hybris.platform.sap.productconfig.runtime.cps;

import java.util.Date;


/**
 * Wraps the service calls for KB determination. Implementation details (usage of charon) should be hidden from the
 * configuration provider
 */
public interface CharonKbDeterminationFacade
{

	/**
	 * Check for knowledge bases on a given date
	 *
	 * @param productcode
	 *           Product code
	 * @param kbdate
	 *           Validity date
	 * @return Does at least one knowledge base exist?
	 */
	boolean hasKbForDate(String productcode, Date kbdate);

	/**
	 * Checks for knowledge base for a given external configuration
	 *
	 * @param product
	 *           product code
	 * @param externalcfg
	 *           external representation of the configuration
	 * @return Does a knowledge base exist for the name, version and logical system specified in the external
	 *         configuration?
	 */
	boolean hasKbForExtConfig(String product, String externalcfg);

	/**
	 * Reads KB ID for a given product and date. It's not required that the KB for the product/date combination is
	 * unique, if multiple knowledge bases exist, the ID for the first found KB is returned
	 *
	 * @param productcode
	 *           product code
	 * @param kbdate
	 *           Validity date of KB
	 * @return KB ID for first matching KB
	 */
	Integer readKbIdForDate(String productcode, Date kbdate);

	/**
	 * Reads the current KB ID for a given product.
	 *
	 * @param productcode
	 *           product code
	 * @return KB ID for matching KB
	 */
	Integer getCurrentKbIdForProduct(String productcode);

}
