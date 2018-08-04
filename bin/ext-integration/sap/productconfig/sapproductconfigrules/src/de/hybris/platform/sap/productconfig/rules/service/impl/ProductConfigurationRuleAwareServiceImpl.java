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
package de.hybris.platform.sap.productconfig.rules.service.impl;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.ruleengine.RuleEngineService;
import de.hybris.platform.ruleengine.RuleEvaluationContext;
import de.hybris.platform.ruleengine.RuleEvaluationResult;
import de.hybris.platform.ruleengine.dao.RuleEngineContextDao;
import de.hybris.platform.ruleengine.enums.RuleType;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineContextModel;
import de.hybris.platform.ruleengine.strategies.RuleEngineContextFinderStrategy;
import de.hybris.platform.ruleengineservices.enums.FactContextType;
import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.ruleengineservices.rao.ProcessStep;
import de.hybris.platform.ruleengineservices.rao.providers.FactContextFactory;
import de.hybris.platform.ruleengineservices.rao.providers.RAOProvider;
import de.hybris.platform.ruleengineservices.rao.providers.impl.FactContext;
import de.hybris.platform.sap.productconfig.rules.action.strategy.ProductConfigRuleActionStrategy;
import de.hybris.platform.sap.productconfig.rules.model.ProductConfigProcessStepModel;
import de.hybris.platform.sap.productconfig.rules.service.ProductConfigurationRuleAwareService;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSourceSubType;
import de.hybris.platform.sap.productconfig.services.impl.ProductConfigurationServiceImpl;
import de.hybris.platform.servicelayer.time.TimeService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link ProductConfigurationServiceImpl}.
 */
public class ProductConfigurationRuleAwareServiceImpl extends ProductConfigurationServiceImpl
		implements ProductConfigurationRuleAwareService
{
	private FactContextFactory factContextFactory;
	private CartService cartService;
	private String defaultRuleEngineContextName;
	private RuleEngineContextDao ruleEngineContextDao;
	private RuleEngineService commerceRuleEngineService;
	private TimeService timeService;

	private Map<String, ProductConfigRuleActionStrategy> actionStrategiesMapping;

	private RuleEngineContextFinderStrategy ruleEngineContextFinderStrategy;
	private ProductService productService;

	private static final Logger LOG = Logger.getLogger(ProductConfigurationRuleAwareServiceImpl.class);

	@Override
	protected ConfigModel afterDefaultConfigCreated(final ConfigModel config)
	{
		cacheConfig(config);
		ConfigModel checkedConfig;
		final boolean adjusted = adjustConfigurationRuleBased(config, ProcessStep.CREATE_DEFAULT_CONFIGURATION);
		if (adjusted)
		{
			final Set<ProductConfigMessage> oldMessages = config.getMessages();
			removeMessagesRecomputedOnNextStep(oldMessages, ProcessStep.RETRIEVE_CONFIGURATION);
			updateConfiguration(config);
			checkedConfig = retrieveConfigurationModel(config.getId());
			checkedConfig.getMessages().addAll(oldMessages);
		}
		else
		{
			adjustConfigurationRuleBased(config, ProcessStep.RETRIEVE_CONFIGURATION);
			getRecorder().recordConfigurationStatus(config);
			checkedConfig = config;
			cacheConfig(checkedConfig);
		}

		return checkedConfig;
	}

	@Override
	protected ConfigModel afterConfigCreated(final ConfigModel config)
	{
		cacheConfig(config);
		if (adjustConfigurationRuleBased(config, ProcessStep.RETRIEVE_CONFIGURATION))
		{
			getRecorder().recordConfigurationStatus(config);
			cacheConfig(config);
		}
		return config;
	}


	protected void removeMessagesRecomputedOnNextStep(final Set<ProductConfigMessage> oldMessages, final ProcessStep nextStep)
	{
		final Iterator<ProductConfigMessage> msgItr = oldMessages.iterator();
		while (msgItr.hasNext())
		{
			final ProductConfigMessage message = msgItr.next();
			if (removeMessageBeforeNextStep(nextStep, message))
			{
				msgItr.remove();
			}
		}

	}

	protected boolean removeMessageBeforeNextStep(final ProcessStep nextStep, final ProductConfigMessage message)
	{
		return ProductConfigMessageSourceSubType.DISPLAY_MESSAGE.equals(message.getSourceSubType())
				&& ProcessStep.RETRIEVE_CONFIGURATION.equals(nextStep);
	}

	@Override
	public ConfigModel retrieveConfigurationModel(final String configId)
	{
		final Object lock = ProductConfigurationServiceImpl.getLock(configId);
		synchronized (lock)
		{
			ConfigModel cachedModel = getSessionAccessService().getConfigurationModelEngineState(configId);
			if (cachedModel == null)
			{
				cachedModel = provideConfigurationModel(configId, true);
				cacheConfig(cachedModel);
				this.getRecorder().recordConfigurationStatus(cachedModel);
			}
			else
			{
				LOG.debug(DEBUG_CONFIG_WITH_ID + configId + "' retrieved from cache");
			}
			return cachedModel;
		}
	}

	protected ConfigModel provideConfigurationModel(final String configId, final boolean useRuleEngine)
	{
		final ConfigModel config = super.retrieveConfigurationModelFromConfigurationEngine(configId);

		if (useRuleEngine)
		{
			adjustConfigurationRuleBased(config, ProcessStep.RETRIEVE_CONFIGURATION);
		}
		return config;
	}


	@Override
	public ConfigModel retrieveConfigurationModelBypassRules(final String configId)
	{

		final Object lock = ProductConfigurationServiceImpl.getLock(configId);
		synchronized (lock)
		{
			ConfigModel cachedModel = getSessionAccessService().getConfigurationModelEngineState(configId);
			if (cachedModel == null)
			{
				cachedModel = provideConfigurationModel(configId, false);
				cacheConfig(cachedModel);
			}
			else
			{
				LOG.debug(DEBUG_CONFIG_WITH_ID + configId + "' retrieved from cache");
			}
			return cachedModel;
		}
	}

	@Override
	public ConfigModel createConfigurationFromExternalBypassRules(final KBKey kbKey, final String externalConfiguration)
	{
		final ConfigModel config = getConfigurationProvider().createConfigurationFromExternalSource(kbKey, externalConfiguration);
		getRecorder().recordCreateConfigurationFromExternalSource(config);
		cacheConfig(config);
		return config;
	}


	protected boolean adjustConfigurationRuleBased(final ConfigModel currentConfigModel, final ProcessStep processStep)
	{
		boolean adjusted = false;

		final ProductConfigProcessStepModel processStepModel = new ProductConfigProcessStepModel();
		processStepModel.setProcessStep(processStep);

		final CartModel cartModel = getCartService().getSessionCart();

		final FactContext factContext = createFactContext(cartModel, currentConfigModel, processStepModel);
		final AbstractRuleEngineContextModel engineContext = determineRuleEngineContext(currentConfigModel);
		final RuleEvaluationContext ruleEvaluationContext = prepareRuleEvaluationContext(factContext, engineContext);

		LOG.debug("Triggering rule engine for process Step " + processStep + " and config id " + currentConfigModel.getId());

		final RuleEvaluationResult rulesResult = getCommerceRuleEngineService().evaluate(ruleEvaluationContext);
		adjusted = applyRulesResult(rulesResult, currentConfigModel);

		LOG.debug("Rule engine processing done for process Step " + processStep + " and config id " + currentConfigModel.getId());


		return adjusted;
	}

	protected FactContext createFactContext(final CartModel cartModel, final ConfigModel currentConfigModel,
			final ProductConfigProcessStepModel processStepModel)
	{
		final List<Object> facts = new ArrayList<>();

		facts.add(cartModel);
		facts.add(currentConfigModel);
		facts.add(processStepModel);

		final Date date = getTimeService().getCurrentTime();
		facts.add(date);

		return getFactContextFactory().createFactContext(FactContextType.PRODUCTCONFIG_DEFAULT_CONFIGURATION, facts);
	}

	protected AbstractRuleEngineContextModel determineRuleEngineContext(final ConfigModel currentConfigModel)
	{
		AbstractRuleEngineContextModel engineContext = null;
		if (isNotEmpty(getDefaultRuleEngineContextName()))
		{
			engineContext = getRuleEngineContextDao().findRuleEngineContextByName(getDefaultRuleEngineContextName());
		}
		else
		{
			final ProductModel product = productService.getProductForCode(currentConfigModel.getRootInstance().getName());
			LOG.debug("Product = " + product.getCode());
			LOG.debug("Catalog Version = " + product.getCatalogVersion());
			engineContext = getRuleEngineContextFinderStrategy().findRuleEngineContext(product, RuleType.PRODUCTCONFIG)
					.orElseThrow(IllegalStateException::new);
			LOG.debug("Context Name = " + engineContext.getName());
		}

		return engineContext;
	}

	/**
	 * @deprecated since 6.6
	 */
	@Deprecated
	@SuppressWarnings(
	{ "squid:S1133" })
	protected RuleEvaluationContext prepareRuleEvaluationContext(final FactContext factContext)
	{

		final AbstractRuleEngineContextModel engineContext = getRuleEngineContextDao()
				.findRuleEngineContextByName(getDefaultRuleEngineContextName());

		return prepareRuleEvaluationContext(factContext, engineContext);
	}

	protected RuleEvaluationContext prepareRuleEvaluationContext(final FactContext factContext,
			final AbstractRuleEngineContextModel engineContext)
	{
		final Set<Object> convertedFacts = provideRAOs(factContext);
		final RuleEvaluationContext evaluationContext = new RuleEvaluationContext();
		evaluationContext.setRuleEngineContext(engineContext);
		evaluationContext.setFacts(convertedFacts);
		return evaluationContext;
	}

	protected Set<Object> provideRAOs(final FactContext factContext)
	{
		final Set<Object> result = new HashSet<>();
		if (factContext != null)
		{
			for (final Object fact : factContext.getFacts())
			{
				for (final RAOProvider raoProvider : factContext.getProviders(fact))
				{
					result.addAll(raoProvider.expandFactModel(fact));
				}
			}
		}
		return result;
	}


	protected FactContextFactory getFactContextFactory()
	{
		return factContextFactory;
	}

	/**
	 * @param factContextFactory
	 *           injects the rule engine fact context factory, which is required to trigger the rule engine
	 */
	@Required
	public void setFactContextFactory(final FactContextFactory factContextFactory)
	{
		this.factContextFactory = factContextFactory;
	}

	protected CartService getCartService()
	{
		return cartService;
	}

	/**
	 * @param cartService
	 *           the cart service to access the session cart contents, which may influence the rule engine result
	 */
	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	protected String getDefaultRuleEngineContextName()
	{
		return defaultRuleEngineContextName;
	}

	/**
	 * @param defaultRuleEngineContextName
	 *           injects the rule engine context, which is required to trigger the rule engine
	 */
	public void setDefaultRuleEngineContextName(final String defaultRuleEngineContextName)
	{
		this.defaultRuleEngineContextName = defaultRuleEngineContextName;
	}

	protected RuleEngineContextDao getRuleEngineContextDao()
	{
		return ruleEngineContextDao;
	}

	/**
	 * @param ruleEngineContextDao
	 *           injects the rule engine context DAO, which is required to trigger the rule engine
	 */
	@Required
	public void setRuleEngineContextDao(final RuleEngineContextDao ruleEngineContextDao)
	{
		this.ruleEngineContextDao = ruleEngineContextDao;
	}

	protected RuleEngineService getCommerceRuleEngineService()
	{
		return commerceRuleEngineService;
	}

	/**
	 * @param ruleEngineService
	 *           injects the rule engine service, which is required to trigger the rule engine
	 */
	@Required
	public void setCommerceRuleEngineService(final RuleEngineService ruleEngineService)
	{
		this.commerceRuleEngineService = ruleEngineService;
	}


	protected boolean applyRulesResult(final RuleEvaluationResult rulesResult, final ConfigModel configModel)
	{
		boolean configChanged = false;

		if (rulesResult == null || rulesResult.getResult() == null)
		{
			return false;
		}

		final Set<AbstractRuleActionRAO> actions = rulesResult.getResult().getActions();
		if (CollectionUtils.isEmpty(actions))
		{
			return false;
		}

		for (final AbstractRuleActionRAO action : actions)
		{
			final ProductConfigRuleActionStrategy stategy = getRuleActionStrategy(action.getActionStrategyKey());
			configChanged |= stategy.apply(configModel, action);
		}

		return configChanged;
	}

	protected Map<String, ProductConfigRuleActionStrategy> getActionStrategiesMapping()
	{
		return actionStrategiesMapping;
	}

	/**
	 * @param actionStrategiesMapping
	 *           injects the mapping between actionStrategyKey, which is defined by the rule action and a concreate
	 *           actionStrategy class, which will apply the defined action
	 */
	@Required
	public void setActionStrategiesMapping(final Map<String, ProductConfigRuleActionStrategy> actionStrategiesMapping)
	{
		this.actionStrategiesMapping = actionStrategiesMapping;
	}

	/**
	 * returns the {@code ProductConfigRuleActionStrategy} defined in the {@code actionStrategiesMapping} attribute of
	 * this service by looking up it's hey.
	 *
	 * @param strategyKey
	 *           the key of the RuleActionStrategy to look up
	 * @return the found bean id
	 * @throws IllegalArgumentException
	 *            if the requested strategy cannot be found
	 * @throws IllegalStateException
	 *            if this method is called but no strategies are configured
	 */
	protected ProductConfigRuleActionStrategy getRuleActionStrategy(final String strategyKey)
	{
		if (MapUtils.isNotEmpty(getActionStrategiesMapping()))
		{
			final ProductConfigRuleActionStrategy strategy = getActionStrategiesMapping().get(strategyKey);
			if (strategy != null)
			{
				return strategy;
			}
			throw new IllegalArgumentException("cannot find ProductConfigRuleActionStrategy for given action: " + strategyKey);
		}
		throw new IllegalStateException("no strategy mapping defined");
	}

	protected TimeService getTimeService()
	{
		return timeService;
	}

	/**
	 * @param timeService
	 *           time service
	 */
	@Required
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}

	/**
	 * @return the ruleEngineContextFinderStrategy
	 */
	protected RuleEngineContextFinderStrategy getRuleEngineContextFinderStrategy()
	{
		return ruleEngineContextFinderStrategy;
	}

	/**
	 * @param ruleEngineContextFinderStrategy
	 *           the ruleEngineContextFinderStrategy to set
	 */
	@Required
	public void setRuleEngineContextFinderStrategy(final RuleEngineContextFinderStrategy ruleEngineContextFinderStrategy)
	{
		this.ruleEngineContextFinderStrategy = ruleEngineContextFinderStrategy;
	}

	/**
	 * @return the productService
	 */
	protected ProductService getProductService()
	{
		return productService;
	}

	/**
	 * @param productService
	 *           the productService to set
	 */
	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}
}