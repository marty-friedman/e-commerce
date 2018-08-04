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

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;


/**
 * Container for CPS session attibutes
 */
public class CPSSessionAttributeContainer implements Serializable
{
	private static final long serialVersionUID = 1L;

	@SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
	private transient Map<String, Map<CPSMasterDataVariantPriceKey, CPSValuePrice>> valuePricesMap = Collections
			.synchronizedMap(new HashMap<String, Map<CPSMasterDataVariantPriceKey, CPSValuePrice>>());

	@SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
	private transient Map<String, PricingDocumentResult> pricingDocumentResultMap = Collections
			.synchronizedMap(new HashMap<String, PricingDocumentResult>());

	@SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
	private transient Map<String, PricingDocumentInput> pricingDocumentInputMap = Collections
			.synchronizedMap(new HashMap<String, PricingDocumentInput>());

	@SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
	private transient Map<String, List<String>> cookieList = Collections.synchronizedMap(new HashMap<String, List<String>>());

	@SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
	private transient Map<String, String> eTagMap = Collections.synchronizedMap(new HashMap<String, String>());


	/**
	 * @return Map of value prices. Key is the KB identifier
	 */
	public Map<String, Map<CPSMasterDataVariantPriceKey, CPSValuePrice>> getValuePricesMap()
	{
		return valuePricesMap;
	}

	/**
	 * @return Map of pricing call results. Key is the configuration runtime ID
	 */
	public Map<String, PricingDocumentResult> getPricingDocumentResultMap()
	{
		return pricingDocumentResultMap;
	}

	/**
	 * @return Map of pricing call inputs. Key is the configuration runtime ID
	 */
	public Map<String, PricingDocumentInput> getPricingDocumentInputMap()
	{
		return pricingDocumentInputMap;
	}

	/**
	 * @return Map of eTags. Key is the configuration runtime ID
	 */
	public Map<String, String> getETagMap()
	{
		return eTagMap;
	}

	private void readObject(final java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		valuePricesMap = Collections.synchronizedMap(new HashMap<String, Map<CPSMasterDataVariantPriceKey, CPSValuePrice>>());
		pricingDocumentResultMap = Collections.synchronizedMap(new HashMap<String, PricingDocumentResult>());
		pricingDocumentInputMap = Collections.synchronizedMap(new HashMap<String, PricingDocumentInput>());
		eTagMap = Collections.synchronizedMap(new HashMap<String, String>());
		cookieList = Collections.synchronizedMap(new HashMap<String, List<String>>());
	}

	/**
	 * Set cookies
	 *
	 * @param configid
	 * @param cookieList
	 */
	public void setCookies(final String configid, final List<String> cookieList)
	{
		this.cookieList.put(configid, cookieList);

	}

	/**
	 * @param configid
	 * @return List of cookies
	 */
	public List<String> getCookies(final String configid)
	{
		return cookieList.get(configid);
	}

	/**
	 * Removes cookies per configId
	 *
	 * @param configId
	 */
	public void removeCookies(final String configId)
	{
		cookieList.remove(configId);

	}


}
