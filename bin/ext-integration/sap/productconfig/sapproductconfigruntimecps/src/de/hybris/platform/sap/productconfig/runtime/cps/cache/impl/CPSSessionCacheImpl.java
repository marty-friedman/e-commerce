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
package de.hybris.platform.sap.productconfig.runtime.cps.cache.impl;

import de.hybris.platform.sap.productconfig.runtime.cps.cache.CPSSessionAttributeContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.cache.CPSSessionCache;
import de.hybris.platform.sap.productconfig.runtime.cps.constants.SapproductconfigruntimecpsConstants;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.pricing.CPSMasterDataVariantPriceKey;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingDocumentInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingDocumentResult;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.pricing.CPSValuePrice;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.List;
import java.util.Map;


/**
 * Default implementation of {@link CPSSessionCache}. Stores pricing related artifacts in the user session, using
 * {@link SessionService}.
 */
public class CPSSessionCacheImpl implements CPSSessionCache
{

	private SessionService sessionService;


	@Override
	public void setValuePricesMap(final String kbId, final Map<CPSMasterDataVariantPriceKey, CPSValuePrice> valuePricesMap)
	{
		retrieveSessionAttributeContainer().getValuePricesMap().put(kbId, valuePricesMap);

	}

	@Override
	public Map<CPSMasterDataVariantPriceKey, CPSValuePrice> getValuePricesMap(final String kbId)
	{
		return retrieveSessionAttributeContainer().getValuePricesMap().get(kbId);
	}

	protected CPSSessionAttributeContainer retrieveSessionAttributeContainer()
	{
		CPSSessionAttributeContainer attributeContainer = sessionService
				.getAttribute(SapproductconfigruntimecpsConstants.PRODUCT_CONFIG_CPS_SESSION_ATTRIBUTE_CONTAINER);
		if (attributeContainer == null)
		{
			attributeContainer = new CPSSessionAttributeContainer();
			sessionService.setAttribute(SapproductconfigruntimecpsConstants.PRODUCT_CONFIG_CPS_SESSION_ATTRIBUTE_CONTAINER,
					attributeContainer);
		}
		return attributeContainer;
	}


	/**
	 * @return Session service
	 */
	public SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 * @param sessionService
	 *           Session service
	 */
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	@Override
	public void setPricingDocumentResult(final String configId, final PricingDocumentResult pricingDocumentResult)
	{
		retrieveSessionAttributeContainer().getPricingDocumentResultMap().put(configId, pricingDocumentResult);

	}

	@Override
	public PricingDocumentResult getPricingDocumentResult(final String configId)
	{
		return retrieveSessionAttributeContainer().getPricingDocumentResultMap().get(configId);
	}

	@Override
	public void setPricingDocumentInput(final String configId, final PricingDocumentInput pricingDocumentInput)
	{
		retrieveSessionAttributeContainer().getPricingDocumentInputMap().put(configId, pricingDocumentInput);

	}

	@Override
	public PricingDocumentInput getPricingDocumentInput(final String configId)
	{
		return retrieveSessionAttributeContainer().getPricingDocumentInputMap().get(configId);
	}

	@Override
	public void purge()
	{
		sessionService.setAttribute(SapproductconfigruntimecpsConstants.PRODUCT_CONFIG_CPS_SESSION_ATTRIBUTE_CONTAINER, null);

	}

	@Override
	public void setCookies(final String configId, final List<String> cookieList)
	{
		retrieveSessionAttributeContainer().setCookies(configId, cookieList);

	}

	@Override
	public List<String> getCookies(final String configId)
	{
		return retrieveSessionAttributeContainer().getCookies(configId);
	}

	@Override
	public void removeCookies(final String configId)
	{
		retrieveSessionAttributeContainer().removeCookies(configId);

	}

	@Override
	public void removePricingDocumentInput(final String configId)
	{
		retrieveSessionAttributeContainer().getPricingDocumentInputMap().remove(configId);

	}

	@Override
	public void removeETag(final String configId)
	{
		retrieveSessionAttributeContainer().getETagMap().remove(configId);

	}

	@Override
	public void removePricingDocumentResult(final String configId)
	{
		retrieveSessionAttributeContainer().getPricingDocumentResultMap().remove(configId);

	}

	@Override
	public void purgePrices()
	{
		final CPSSessionAttributeContainer container = retrieveSessionAttributeContainer();
		container.getPricingDocumentInputMap().clear();
		container.getPricingDocumentResultMap().clear();
		container.getValuePricesMap().clear();
	}

	@Override
	public void setETag(final String configId, final String eTag)
	{
		retrieveSessionAttributeContainer().getETagMap().put(configId, eTag);

	}

	@Override
	public String getETag(final String configId)
	{
		return retrieveSessionAttributeContainer().getETagMap().get(configId);
	}

}
