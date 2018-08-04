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

import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.ProviderFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.SolvableConflictModel;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.data.CartEntryConfigurationAttributes;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPricingOrderIntegrationService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.tracking.TrackingRecorder;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link ProductConfigurationService}.<br>
 * This implementation will synchronize access to the {@link ConfigurationProvider}, so that it is guaranteed that only
 * exactly one thread will access the configuration provider for a given configuration session. Furthermore a simple
 * session based read cache ensures that subsequent calls to read the same configuration result only into exactly one
 * read request to the configuration engine.
 *
 * @see ProductConfigurationServiceImpl#setMaxLocksPerMap(int)
 * @see SessionAccessServiceImpl#setMaxCachedConfigsInSession(int)
 */
public class ProductConfigurationServiceImpl implements ProductConfigurationService
{

	protected static final String DEBUG_CONFIG_WITH_ID = "Config with id '";
	static final Object PROVIDER_LOCK = new Object();

	private static final Logger LOG = Logger.getLogger(ProductConfigurationServiceImpl.class);

	private static int maxLocksPerMap = 1024;
	private static Map<String, Object> locks = new HashMap<>((int) (maxLocksPerMap / 0.75 + 1));
	private static Map<String, Object> oldLocks = new HashMap<>((int) (maxLocksPerMap / 0.75 + 1));

	private ProviderFactory providerFactory;
	private ProductConfigurationPricingOrderIntegrationService configurationPricingOrderIntegrationService;

	private SessionAccessService sessionAccessService;
	private TrackingRecorder recorder;

	@Override
	public ConfigModel createDefaultConfiguration(final KBKey kbKey)
	{
		// no need to synchronize create, because config session (identified by
		// the config ID)
		// is only exposed once the object has been created
		final ConfigModel config = getConfigurationProvider().createDefaultConfiguration(kbKey);
		recorder.recordCreateConfiguration(config, kbKey);

		return afterDefaultConfigCreated(config);

	}

	@Override
	public void updateConfiguration(final ConfigModel model)
	{
		final String id = model.getId();
		final Object lock = ProductConfigurationServiceImpl.getLock(id);
		synchronized (lock)
		{

			try
			{
				final boolean updateExecuted = getConfigurationProvider().updateConfiguration(model);
				if (updateExecuted)
				{
					recorder.recordUpdateConfiguration(model);
					if (LOG.isDebugEnabled())
					{
						LOG.debug(DEBUG_CONFIG_WITH_ID + model.getId() + "' updated, removing it from cache");
					}
					removeConfigAttributesFromSessionCache(id);
				}
			}
			catch (final ConfigurationEngineException ex)
			{
				cleanUpAfterEngineError(id);
				throw new IllegalStateException("Updating configuration failed", ex);
			}
		}
	}

	@Override
	public ConfigModel retrieveConfigurationModel(final String configId)
	{
		final Object lock = ProductConfigurationServiceImpl.getLock(configId);
		synchronized (lock)
		{
			ConfigModel cachedModel = sessionAccessService.getConfigurationModelEngineState(configId);
			if (cachedModel == null)
			{

				cachedModel = retrieveConfigurationModelFromConfigurationEngine(configId);
				cacheConfig(cachedModel);
				recorder.recordConfigurationStatus(cachedModel);
			}
			else
			{
				LOG.debug(DEBUG_CONFIG_WITH_ID + configId + "' retrieved from cache");
			}
			return cachedModel;
		}
	}

	protected ConfigModel retrieveConfigurationModelFromConfigurationEngine(final String configId)
	{
		try
		{
			return getConfigurationProvider().retrieveConfigurationModel(configId);
		}
		catch (final ConfigurationEngineException ex)
		{
			cleanUpAfterEngineError(configId);
			throw new IllegalStateException("Retrieving configuration failed", ex);
		}
	}

	protected void cleanUpAfterEngineError(final String configId)
	{
		getSessionAccessService().purge();
		removeConfigAttributesFromSessionCache(configId);
	}

	@Override
	public String retrieveExternalConfiguration(final String configId)
	{
		final Object lock = getLock(configId);
		synchronized (lock)
		{
			try
			{
				return getConfigurationProvider().retrieveExternalConfiguration(configId);
			}
			catch (final ConfigurationEngineException e)
			{
				cleanUpAfterEngineError(configId);
				throw new IllegalStateException("Retrieving external configuration failed", e);
			}
		}
	}

	/**
	 * @param providerFactory
	 *           inject factory to access the providers
	 */
	@Required
	public void setProviderFactory(final ProviderFactory providerFactory)
	{
		this.providerFactory = providerFactory;
	}

	/**
	 * A configuration provider lock ensures, that there are no concurrent requests send to the configuration engine for
	 * the same configuration session.<br>
	 * We might not always get informed when a configuration session is released, hence we do not rely on this. Instead
	 * we just keep a maximum number of locks and release the oldest locks, when there are to many. The maximum number
	 * can be configured by this setter. <br>
	 * A look can be re-created in case it had already been deleted. The number should be high enough, so that locks do
	 * not get deleted while some concurrent threads are still using the lock, as this could cause concurrency issue.
	 * <b>The maximum number heavily depends on the number of concurrent threads expected.</b> Default is 1024.
	 *
	 * @param maxLocksPerMap
	 *           sets the maximum number of Configuration Provider Locks kept.
	 */
	public static void setMaxLocksPerMap(final int maxLocksPerMap)
	{
		ProductConfigurationServiceImpl.maxLocksPerMap = maxLocksPerMap;
	}

	protected static int getMaxLocksPerMap()
	{
		return ProductConfigurationServiceImpl.maxLocksPerMap;
	}

	protected ConfigurationProvider getConfigurationProvider()
	{
		return getProviderFactory().getConfigurationProvider();
	}

	protected static Object getLock(final String configId)
	{
		synchronized (PROVIDER_LOCK)
		{

			Object lock = locks.get(configId);
			if (lock == null)
			{
				lock = oldLocks.get(configId);
				if (lock == null)
				{
					ensureThatLockMapIsNotTooBig();
					lock = new Object();
					locks.put(configId, lock);
				}
			}
			return lock;
		}
	}

	protected static void ensureThatLockMapIsNotTooBig()
	{
		if (locks.size() >= maxLocksPerMap)
		{
			oldLocks.clear();
			oldLocks = locks;
			// avoid rehashing, create with sufficient capacity
			locks = new HashMap<>((int) (maxLocksPerMap / 0.75 + 1));
		}
	}

	protected ConfigModel afterDefaultConfigCreated(final ConfigModel config)
	{
		cacheConfig(config);
		return config;
	}

	protected ConfigModel afterConfigCreated(final ConfigModel config)
	{
		cacheConfig(config);
		return config;
	}

	@Override
	public ConfigModel createConfigurationFromExternal(final KBKey kbKey, final String externalConfiguration)
	{
		return createConfigurationFromExternal(kbKey, externalConfiguration, null);
	}

	@Override
	public ConfigModel createConfigurationFromExternal(final KBKey kbKey, final String externalConfiguration,
			final String cartEntryKey)
	{
		final ConfigModel config = getConfigurationProvider().createConfigurationFromExternalSource(kbKey, externalConfiguration);
		recorder.recordCreateConfigurationFromExternalSource(config);
		if (null != cartEntryKey)
		{
			getSessionAccessService().setConfigIdForCartEntry(cartEntryKey, config.getId());
		}


		return afterConfigCreated(config);
	}

	@Override
	public ConfigModel createConfigurationFromExternalSource(final Configuration extConfig)
	{
		final ConfigModel config = getConfigurationProvider().createConfigurationFromExternalSource(extConfig);
		recorder.recordCreateConfigurationFromExternalSource(config);

		return afterConfigCreated(config);
	}

	@Override
	public void releaseSession(final String configId)
	{
		releaseSession(configId, false);
	}

	@Override
	public void releaseSession(final String configId, final boolean keepModel)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Releasing config session with id " + configId);
		}

		final Object lock = ProductConfigurationServiceImpl.getLock(configId);
		synchronized (lock)
		{
			getConfigurationProvider().releaseSession(configId);
			if (!keepModel)
			{
				removeConfigAttributesFromSessionCache(configId);
			}

			synchronized (PROVIDER_LOCK)
			{
				locks.remove(configId);
				oldLocks.remove(configId);
			}
		}
	}

	protected void removeConfigAttributesFromSessionCache(final String configId)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Removing config with id '" + configId + "' from cache");
		}

		getSessionAccessService().removeConfigAttributeState(configId);
	}

	protected void cacheConfig(final ConfigModel config)
	{
		getSessionAccessService().setConfigurationModelEngineState(config.getId(), config);

		if (LOG.isDebugEnabled())
		{
			LOG.debug(DEBUG_CONFIG_WITH_ID + config.getId() + "' read frist time, caching it for further access");
		}
	}

	protected ProviderFactory getProviderFactory()
	{
		return providerFactory;
	}

	/**
	 * Sets session access service (Accessing mappings which we store in the hybris session)
	 *
	 * @param sessionAccessService
	 */
	@Required
	public void setSessionAccessService(final SessionAccessService sessionAccessService)
	{
		this.sessionAccessService = sessionAccessService;

	}

	protected SessionAccessService getSessionAccessService()
	{
		return sessionAccessService;
	}

	/**
	 * @deprecated since 6.6
	 */
	@SuppressWarnings("squid:S1133")
	@Override
	@Deprecated
	public CartEntryConfigurationAttributes calculateCartEntryConfigurationAttributes(final AbstractOrderEntryModel entryModel)
	{
		return getConfigurationPricingOrderIntegrationService().calculateCartEntryConfigurationAttributes(entryModel);
	}

	/**
	 * @deprecated since 6.6
	 */
	@SuppressWarnings("squid:S1133")
	@Override
	@Deprecated
	public CartEntryConfigurationAttributes calculateCartEntryConfigurationAttributes(final String cartEntryKey,
			final String productCode, final String externalConfiguration)
	{
		return getConfigurationPricingOrderIntegrationService().calculateCartEntryConfigurationAttributes(cartEntryKey,
				productCode, externalConfiguration);
	}

	/**
	 * @deprecated since 6.6
	 */
	@SuppressWarnings("squid:S1133")
	@Override
	@Deprecated
	public ConfigModel ensureConfigurationInSession(final String cartEntryKey, final String productCode,
			final String externalConfiguration)
	{
		return getConfigurationPricingOrderIntegrationService().ensureConfigurationInSession(cartEntryKey, productCode,
				externalConfiguration);
	}

	@Override
	public int calculateNumberOfIncompleteCsticsAndSolvableConflicts(final String configId)
	{
		final ConfigModel configurationModel = retrieveConfigurationModel(configId);

		return countNumberOfIncompleteCstics(configurationModel.getRootInstance())
				+ countNumberOfSolvableConflicts(configurationModel);

	}


	protected int countNumberOfIncompleteCstics(final InstanceModel rootInstance)
	{

		int numberOfErrors = 0;
		for (final InstanceModel subInstace : rootInstance.getSubInstances())
		{
			numberOfErrors += countNumberOfIncompleteCstics(subInstace);
		}
		for (final CsticModel cstic : rootInstance.getCstics())
		{
			if (cstic.isRequired() && !cstic.isComplete())
			{
				numberOfErrors++;
				if (LOG.isDebugEnabled())
				{
					LOG.debug("Mandatory Cstic missing: " + cstic.getName());
				}
			}
		}
		return numberOfErrors;

	}

	protected int countNumberOfNotConsistentCstics(final InstanceModel instance)
	{
		int result = (int) instance.getCstics().stream().filter(cstic -> !cstic.isConsistent()).count();

		for (final InstanceModel subInstance : instance.getSubInstances())
		{
			result += countNumberOfNotConsistentCstics(subInstance);
		}

		return result;
	}

	protected int countNumberOfSolvableConflicts(final ConfigModel configModel)
	{
		int result = 0;
		final List<SolvableConflictModel> solvableConflicts = configModel.getSolvableConflicts();
		if (solvableConflicts != null)
		{
			return solvableConflicts.size();
		}

		if (!configModel.isConsistent())
		{
			result = countNumberOfNotConsistentCstics(configModel.getRootInstance());
		}

		return result;
	}

	/**
	 * @deprecated since 6.6
	 */
	@SuppressWarnings("squid:S1133")
	@Override
	@Deprecated
	public boolean updateCartEntryBasePrice(final AbstractOrderEntryModel entry)
	{
		return getConfigurationPricingOrderIntegrationService().updateCartEntryBasePrice(entry);
	}

	/**
	 * @deprecated since 6.6
	 */
	@SuppressWarnings("squid:S1133")
	@Override
	@Deprecated
	public boolean updateCartEntryExternalConfiguration(final CommerceCartParameter parameters, final AbstractOrderEntryModel entry)
	{
		return getConfigurationPricingOrderIntegrationService().updateCartEntryExternalConfiguration(parameters, entry);
	}

	/**
	 * @deprecated since 6.6
	 */
	@SuppressWarnings("squid:S1133")
	@Override
	@Deprecated
	public boolean updateCartEntryExternalConfiguration(final String externalConfiguration, final AbstractOrderEntryModel entry)
	{
		return getConfigurationPricingOrderIntegrationService().updateCartEntryExternalConfiguration(externalConfiguration, entry);
	}

	@Override
	public ConfigModel createConfigurationForVariant(final String baseProductCode, final String variantProductCode)
	{
		if (getProviderFactory().getConfigurationProvider().isConfigureVariantSupported())
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("create variant configuration for base product " + baseProductCode + " of product variant "
						+ variantProductCode);
			}
			final ConfigModel configModel = getConfigurationProvider().retrieveConfigurationFromVariant(baseProductCode,
					variantProductCode);
			recorder.recordCreateConfigurationForVariant(configModel, baseProductCode, variantProductCode);

			return afterConfigCreated(configModel);
		}
		else
		{
			throw new IllegalStateException(
					"The active configuration provider does not support the configuration of a product variant");
		}
	}

	/**
	 * @deprecated since 6.6
	 */
	@SuppressWarnings("squid:S1133")
	@Override
	@Deprecated
	public boolean updateCartEntryProduct(final AbstractOrderEntryModel entry, final ProductModel product, final String configId)
	{
		return getConfigurationPricingOrderIntegrationService().updateCartEntryProduct(entry, product, configId);
	}

	/**
	 * @deprecated since 6.6
	 */
	@SuppressWarnings("squid:S1133")
	@Override
	@Deprecated
	public void fillSummaryMap(final AbstractOrderEntryModel entry)
	{
		getConfigurationPricingOrderIntegrationService().fillSummaryMap(entry);
	}

	protected TrackingRecorder getRecorder()
	{
		return recorder;
	}

	/**
	 * @param recorder
	 *           inject the CPQ tracking recorder for tracking CPQ events
	 */
	@Required
	public void setRecorder(final TrackingRecorder recorder)
	{
		this.recorder = recorder;
	}

	@Override
	public boolean hasKbForDate(final String productCode, final Date kbDate)
	{
		return getConfigurationProvider().isKbForDateExists(productCode, kbDate);
	}


	@Override
	public boolean hasKbForVersion(final KBKey kbKey, final String externalConfig)
	{
		return getConfigurationProvider().isKbVersionExists(kbKey, externalConfig);
	}

	protected boolean isConfigureVariantSupported()
	{
		return getConfigurationProvider().isConfigureVariantSupported();
	}

	protected ProductConfigurationPricingOrderIntegrationService getConfigurationPricingOrderIntegrationService()
	{
		return configurationPricingOrderIntegrationService;
	}

	/**
	 * @param configurationPricingOrderIntegrationService
	 *           the configurationPricingOrderIntegrationService to set
	 */
	@Required
	public void setConfigurationPricingOrderIntegrationService(
			final ProductConfigurationPricingOrderIntegrationService configurationPricingOrderIntegrationService)
	{
		this.configurationPricingOrderIntegrationService = configurationPricingOrderIntegrationService;
	}

	@Override
	public int getTotalNumberOfIssues(final ConfigModel configModel)
	{
		return countNumberOfIncompleteCstics(configModel.getRootInstance()) + countNumberOfSolvableConflicts(configModel);
	}

}
