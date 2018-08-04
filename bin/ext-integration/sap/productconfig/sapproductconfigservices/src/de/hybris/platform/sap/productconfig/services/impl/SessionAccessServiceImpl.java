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
package de.hybris.platform.sap.productconfig.services.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.AnalyticsProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingConfigurationParameter;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsDocument;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceSummaryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.services.ProductConfigSessionAttributeContainer;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ClassificationSystemCPQAttributesContainer;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;


/**
 * Default implementation of {@link SessionAccessService}
 */
public class SessionAccessServiceImpl implements SessionAccessService
{
	private int maxCachedConfigMapSize = 20;
	private Set<String> cachedConfigIds = Collections.synchronizedSet(new HashSet<>((int) (maxCachedConfigMapSize / 0.75 + 1)));
	private Set<String> oldCachedConfigIds = Collections.synchronizedSet(new HashSet<>((int) (maxCachedConfigMapSize / 0.75 + 1)));

	private static final String TRACE_MESSAGE_FOR_CART_ENTRY = "for cart entry: ";
	private static final String TRACE_MESSAGE_FOR_PRODUCT = "for product: ";
	private static final Logger LOG = Logger.getLogger(SessionAccessServiceImpl.class);
	private SessionService sessionService;

	/**
	 * @param sessionService
	 *           the sessionService to set
	 */
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}


	@Override
	public String getSessionId()
	{
		return getSessionService().getCurrentSession().getSessionId();
	}

	@Override
	public void setConfigIdForCartEntry(final String cartEntryKey, final String configId)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Put config ID " + configId + " into session for cart entry: " + cartEntryKey);
		}

		final String other = getCartEntryForConfigId(configId);
		if (other != null)
		{
			removeConfigIdForCartEntry(other);
		}
		getCartEntryConfigCache().put(cartEntryKey, configId);
	}

	@Override
	public String getConfigIdForCartEntry(final String cartEntryKey)
	{
		String configId = null;

		final Map<String, String> sessionConfigCartEntryCache = retrieveSessionAttributeContainer().getCartEntryConfigurations();

		if (sessionConfigCartEntryCache != null)
		{
			configId = sessionConfigCartEntryCache.get(cartEntryKey);
		}

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Get config ID " + configId + " from session for cart entry: " + cartEntryKey);
		}

		return configId;

	}


	@Override
	public <T> T getUiStatusForCartEntry(final String cartEntryKey)
	{
		return getUiStatusFromSession(cartEntryKey, true, TRACE_MESSAGE_FOR_CART_ENTRY);
	}


	/**
	 * Retrieves UiStatus from session
	 *
	 * @param key
	 *           Key of object in map
	 * @param forCart
	 *           true for UI Statuses for cart entries, false for catalog products
	 * @param traceMessage
	 *           Post fix of the trace message which identifies the type of key
	 * @return UiStatus
	 */
	protected <T> T getUiStatusFromSession(final String key, final boolean forCart, final String traceMessage)
	{
		Object uiStatus = null;

		Map<String, Object> sessionUiStatusCache;
		if (forCart)
		{
			sessionUiStatusCache = retrieveSessionAttributeContainer().getCartEntryUiStatuses();
		}
		else
		{
			sessionUiStatusCache = retrieveSessionAttributeContainer().getProductUiStatuses();
		}
		if (sessionUiStatusCache != null)
		{
			uiStatus = sessionUiStatusCache.get(key);
		}

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Get UiStatus " + uiStatus + " from session " + traceMessage + key);
		}

		return (T) uiStatus;
	}


	@Override
	public void setUiStatusForCartEntry(final String cartEntryKey, final Object uiStatus)
	{
		setUiStatusIntoSession(cartEntryKey, uiStatus, true, TRACE_MESSAGE_FOR_CART_ENTRY);
	}

	@Override
	public Object getUiStatusForProduct(final String productKey)
	{
		return getUiStatusFromSession(productKey, false, TRACE_MESSAGE_FOR_PRODUCT);
	}


	@Override
	public void setUiStatusForProduct(final String productKey, final Object uiStatus)
	{
		setUiStatusIntoSession(productKey, uiStatus, false, TRACE_MESSAGE_FOR_PRODUCT);
	}

	/**
	 * Puts UiStatus object into session
	 *
	 * @param key
	 *           Key for object
	 * @param uiStatus
	 *           The object we want to store in session
	 * @param forCart
	 *           true for UI Statuses for cart entries, false for catalog products
	 * @param traceMessage
	 *           Post fix of the trace message which identifies the type of key
	 */
	protected void setUiStatusIntoSession(final String key, final Object uiStatus, final boolean forCart,
			final String traceMessage)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Put UiStatus " + uiStatus + " into session " + traceMessage + key);
		}

		Map<String, Object> sessionUiStatusEntryCache;
		if (forCart)
		{
			sessionUiStatusEntryCache = retrieveSessionAttributeContainer().getCartEntryUiStatuses();
		}
		else
		{
			sessionUiStatusEntryCache = retrieveSessionAttributeContainer().getProductUiStatuses();
		}

		sessionUiStatusEntryCache.put(key, uiStatus);
	}


	@Override
	public void removeUiStatusForCartEntry(final String cartEntryKey)
	{
		removeUiStatusFromSession(cartEntryKey, true, TRACE_MESSAGE_FOR_CART_ENTRY);
	}

	/**
	 * Removes UiStatus object from session
	 *
	 * @param key
	 *           Key for object
	 * @param forCart
	 *           true for UI Statuses for cart entries, false for catalog products
	 * @param traceMessage
	 *           Post fix of the trace message which identifies the type of key
	 */
	protected void removeUiStatusFromSession(final String key, final boolean forCart, final String traceMessage)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Remove UiStatus from session " + traceMessage + key);
		}

		Map<String, Object> uiStatusMap;
		if (forCart)
		{
			uiStatusMap = retrieveSessionAttributeContainer().getCartEntryUiStatuses();
		}
		else
		{
			uiStatusMap = retrieveSessionAttributeContainer().getProductUiStatuses();
		}

		if (!MapUtils.isEmpty(uiStatusMap))
		{
			uiStatusMap.remove(key);
		}
		else
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Map does not exist in session");
			}
		}
	}

	@Override
	public void removeUiStatusForProduct(final String productKey)
	{
		removeUiStatusFromSession(productKey, false, TRACE_MESSAGE_FOR_PRODUCT);
	}

	@Override
	public String getCartEntryForConfigId(final String configId)
	{

		final Map<String, String> sessionCartEntryConfigurations = retrieveSessionAttributeContainer().getCartEntryConfigurations();

		if (sessionCartEntryConfigurations != null)
		{
			final List<String> matches = sessionCartEntryConfigurations.entrySet().stream()
					.filter(entry -> entry.getValue().equals(configId))//
					.map(entry -> entry.getKey())//
					.collect(Collectors.toList());

			if (matches.size() > 1)
			{
				throw new IllegalStateException("Multiple matches for configuration: " + configId);
			}
			if (!matches.isEmpty())
			{
				final String cartEntryKey = matches.get(0);
				if (LOG.isDebugEnabled())
				{
					LOG.debug("Get cart entry key " + cartEntryKey + " from session for config ID" + configId);
				}
				return cartEntryKey;
			}
		}
		return null;
	}




	@Override
	public void setCartEntryForProduct(final String productKey, final String cartEntryId)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Put cartEntryId " + cartEntryId + " into session for product: " + productKey);
		}
		getProductCartEntryCache().put(productKey, cartEntryId);
	}


	@Override
	public String getCartEntryForProduct(final String productKey)
	{
		String cartEntryKey = null;

		final Map<String, String> sessionProductCartEntryCache = retrieveSessionAttributeContainer().getProductCartEntries();

		if (sessionProductCartEntryCache != null)
		{
			cartEntryKey = sessionProductCartEntryCache.get(productKey);
		}

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Get cart entry key " + cartEntryKey + " from session for product: " + productKey);
		}

		return cartEntryKey;
	}


	@Override
	public void removeCartEntryForProduct(final String productKey)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Remove cartEntryId for product: " + productKey);
		}
		getProductCartEntryCache().remove(productKey);
	}

	/**
	 * @return Cache for cart entries per product
	 */
	protected Map<String, String> getProductCartEntryCache()
	{
		return retrieveSessionAttributeContainer().getProductCartEntries();
	}


	@Override
	public void removeSessionArtifactsForCartEntry(final String cartEntryId, final String productKey)
	{

		//remove configuration ID if needed
		removeConfigIdForCartEntry(cartEntryId);

		//remove UI status attached to cart entry
		removeUiStatusForCartEntry(cartEntryId);

		//check if this configuration is maintained at product level also
		final String currentCartEntryForProduct = getCartEntryForProduct(productKey);
		if (currentCartEntryForProduct != null && (currentCartEntryForProduct.equals(cartEntryId)))
		{
			//We need to clean up more storages
			removeCartEntryForProduct(productKey);
			removeUiStatusForProduct(productKey);
		}

	}


	@Override
	public void removeConfigIdForCartEntry(final String cartEntryKey)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Remove config ID for cart entry: " + cartEntryKey);
		}

		getCartEntryConfigCache().remove(cartEntryKey);

	}

	/**
	 * @return Map: Configuration ID's for cart entry
	 */
	protected Map<String, String> getCartEntryConfigCache()
	{
		return retrieveSessionAttributeContainer().getCartEntryConfigurations();
	}

	@Override
	public Map<String, ClassificationSystemCPQAttributesContainer> getCachedNameMap()
	{
		return retrieveSessionAttributeContainer().getClassificationSystemCPQAttributes();
	}

	@Override
	public Set<String> getSolrIndexedProperties()
	{
		return retrieveSessionAttributeContainer().getIndexedProperties();
	}

	@Override
	public void setSolrIndexedProperties(final Set<String> solrTypes)
	{
		retrieveSessionAttributeContainer().setIndexedProperties(solrTypes);
	}


	@Override
	public ConfigurationProvider getConfigurationProvider()
	{
		return retrieveSessionAttributeContainer().getConfigurationProvider();
	}

	@Override
	public void setConfigurationProvider(final ConfigurationProvider provider)
	{
		retrieveSessionAttributeContainer().setConfigurationProvider(provider);
	}

	@Override
	public ConfigModel getConfigurationModelEngineState(final String configId)
	{
		if (LOG.isDebugEnabled())
		{
			final StringBuilder debugOutput = new StringBuilder();
			String sessionId = null;
			if (getSessionService() != null && getSessionService().getCurrentSession() != null)
			{
				sessionId = getSessionService().getCurrentSession().getSessionId();
			}
			debugOutput.append("getConfigurationModelEngineState, configuration ID ").append(configId)
					.append(" is bound to session ").append(sessionId);
			LOG.debug(debugOutput);
		}
		return retrieveSessionAttributeContainer().getConfigurationModelEngineStates().get(configId);
	}

	@Override
	public void setConfigurationModelEngineState(final String configId, final ConfigModel configModel)
	{
		if (LOG.isDebugEnabled())
		{
			final StringBuilder debugOutput = new StringBuilder();
			String sessionId = null;
			if (getSessionService() != null && getSessionService().getCurrentSession() != null)
			{
				sessionId = getSessionService().getCurrentSession().getSessionId();
			}
			String rootProductId = null;
			if (configModel != null && configModel.getRootInstance() != null)
			{
				rootProductId = configModel.getRootInstance().getName();
			}
			debugOutput.append("setConfigurationModelEngineState, configuration ID ").append(configId)
					.append(" is bound to session ").append(sessionId).append(" and belongs to root product ").append(rootProductId);
			LOG.debug(debugOutput);
		}
		ensureThatNotToManyConfigsAreCachedInSession();
		cachedConfigIds.add(configId);
		retrieveSessionAttributeContainer().getConfigurationModelEngineStates().put(configId, configModel);
	}

	@Override
	public void removeConfigAttributeStates()
	{
		final ProductConfigSessionAttributeContainer container = retrieveSessionAttributeContainer(false);
		if (container != null)
		{
			LOG.debug("Cleaning product config engine state read cache");
			container.getConfigurationModelEngineStates().clear();
			container.getPriceSummaryStates().clear();
		}
	}

	protected ProductConfigSessionAttributeContainer retrieveSessionAttributeContainer()
	{
		return retrieveSessionAttributeContainer(true);
	}

	protected ProductConfigSessionAttributeContainer retrieveSessionAttributeContainer(final boolean createLazy)
	{

		synchronized (getSessionService().getCurrentSession())
		{
			ProductConfigSessionAttributeContainer attributeContainer = getSessionService()
					.getAttribute(PRODUCT_CONFIG_SESSION_ATTRIBUTE_CONTAINER);
			if (attributeContainer == null && createLazy)
			{
				attributeContainer = new ProductConfigSessionAttributeContainer();
				getSessionService().setAttribute(PRODUCT_CONFIG_SESSION_ATTRIBUTE_CONTAINER, attributeContainer);
			}
			return attributeContainer;
		}
	}

	@Override
	public void setPricingProvider(final PricingProvider provider)
	{
		retrieveSessionAttributeContainer().setPricingProvider(provider);

	}

	@Override
	public PricingConfigurationParameter getPricingConfigurationParameter()
	{
		return retrieveSessionAttributeContainer().getPricingConfigurationParameter();
	}

	@Override
	public void setPricingConfigurationParameter(final PricingConfigurationParameter pricinParameter)
	{
		retrieveSessionAttributeContainer().setPricingConfigurationParameter(pricinParameter);
	}

	@Override
	public PricingProvider getPricingProvider()
	{
		return retrieveSessionAttributeContainer().getPricingProvider();
	}

	@Override
	public PriceSummaryModel getPriceSummaryState(final String configId)
	{
		return retrieveSessionAttributeContainer().getPriceSummaryStates().get(configId);
	}

	@Override
	public void setPriceSummaryState(final String configId, final PriceSummaryModel priceSummaryModel)
	{
		retrieveSessionAttributeContainer().getPriceSummaryStates().put(configId, priceSummaryModel);
	}

	@Override
	public void removeConfigAttributeState(final String configId)
	{
		final ProductConfigSessionAttributeContainer container = retrieveSessionAttributeContainer();
		container.getConfigurationModelEngineStates().remove(configId);
		container.getPriceSummaryStates().remove(configId);
		container.getAnalyticDataStates().remove(configId);
		cachedConfigIds.remove(configId);
	}



	@Override
	public void setAnalyticsProvider(final AnalyticsProvider analyticsProvider)
	{
		retrieveSessionAttributeContainer().setAnalyticsProvider(analyticsProvider);
	}

	@Override
	public AnalyticsProvider getAnalyticsProvider()
	{
		return retrieveSessionAttributeContainer().getAnalyticsProvider();
	}

	@Override
	public void setAnalyticData(final String configId, final AnalyticsDocument analyticsDocument)
	{
		retrieveSessionAttributeContainer().setAnalyticData(configId, analyticsDocument);

	}

	@Override
	public AnalyticsDocument getAnalyticData(final String configId)
	{
		return retrieveSessionAttributeContainer().getAnalyticData(configId);
	}



	@Override
	public void purge()
	{
		getSessionService().setAttribute(SessionAccessServiceImpl.PRODUCT_CONFIG_SESSION_ATTRIBUTE_CONTAINER, null);
	}

	protected int getMaxCachedConfigsInSession()
	{
		return maxCachedConfigMapSize * 2;
	}

	/**
	 * Re-reading a configuration from the configuration engine can be expensive, especially for large configurations.
	 * This is only necessary when the configuration was updated since the last time being read. To make life for callers
	 * easier, this implementation features a simple read-cache for configurations based on the user session. So any
	 * calls to read configuration will always result in a cache hit until the configuration is updated.
	 *
	 * @param maxCachedConfigsInSession
	 *           set the maximum number of configs to be cached in the session. Default is 10.
	 */
	public void setMaxCachedConfigsInSession(final int maxCachedConfigsInSession)
	{
		this.maxCachedConfigMapSize = maxCachedConfigsInSession / 2;
	}

	protected void ensureThatNotToManyConfigsAreCachedInSession()
	{
		if (cachedConfigIds.size() >= maxCachedConfigMapSize)
		{
			for (final String configId : oldCachedConfigIds)
			{
				// clear old configs from session cache
				removeConfigAttributesFromSessionCache(configId);
			}
			oldCachedConfigIds = cachedConfigIds;
			// avoid rehashing, create with sufficient capacity
			cachedConfigIds = Collections.synchronizedSet(new HashSet<>((int) (maxCachedConfigMapSize / 0.75 + 1)));
		}
	}

	protected void removeConfigAttributesFromSessionCache(final String configId)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Removing config with id '" + configId + "' from cache");
		}

		removeConfigAttributeState(configId);
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

}
