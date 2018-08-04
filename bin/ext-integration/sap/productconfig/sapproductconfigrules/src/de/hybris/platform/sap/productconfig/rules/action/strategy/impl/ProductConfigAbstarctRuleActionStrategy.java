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
package de.hybris.platform.sap.productconfig.rules.action.strategy.impl;

import de.hybris.platform.ruleengine.RuleEngineService;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;
import de.hybris.platform.ruleengineservices.rule.services.RuleParametersService;
import de.hybris.platform.ruleengineservices.rule.strategies.RuleConverterException;
import de.hybris.platform.sap.productconfig.rules.action.strategy.ProductConfigRuleActionStrategy;
import de.hybris.platform.sap.productconfig.rules.action.strategy.ProductConfigRuleActionStrategyChecker;
import de.hybris.platform.sap.productconfig.rules.enums.ProductConfigRuleMessageSeverity;
import de.hybris.platform.sap.productconfig.rules.service.ProductConfigRuleFormatTranslator;
import de.hybris.platform.sap.productconfig.rules.service.ProductConfigRuleUtil;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigModelFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSeverity;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSource;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.internal.service.ServicelayerUtils;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;



/**
 * Abstract base class for all CPQ Rule Engine Strategy implementations.<br>
 * Contains some boiler plate code required for every action strategy implementation, logging utilities and
 * setter/getter for common bean dependencies.
 */
public abstract class ProductConfigAbstarctRuleActionStrategy implements ProductConfigRuleActionStrategy
{

	private static final String EMPTY_STRING = "";
	private static final String RULE_UUID_END = "\\}";
	private static final String RULE_UUID_START = "\\{";

	private static final Logger LOG = Logger.getLogger(ProductConfigAbstarctRuleActionStrategy.class);

	private ProductConfigRuleActionStrategyChecker ruleActionChecker;
	private ProductConfigRuleFormatTranslator rulesFormator;
	private RuleEngineService ruleEngineService;
	private RuleParametersService ruleParametersService;
	private ConfigModelFactory configModelFactory;
	private I18NService i18NService;

	private ProductConfigRuleUtil ruleUtil;

	private static final Pattern paramPattern = Pattern.compile(".*\\{[\\-a-f0-9]+\\}.*");


	@Override
	public boolean apply(final ConfigModel model, final AbstractRuleActionRAO action)
	{

		final Map<String, CsticModel> csticMap = getRuleUtil().getCsticMap(model);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Checking if Action '" + action + "' execution is possible.");
		}
		boolean configChanged;
		if (!isActionPossible(model, action, csticMap))
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Discarding Action '" + action + "', because action execution is not possible.");
			}
			configChanged = false;
		}
		else
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Executing Action: " + action);
			}
			configChanged = executeAction(model, action, csticMap);
			handleMessage(model, action, csticMap);
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Action Execution resulted in configChange='" + configChanged + "' for action: " + action);
			}
		}
		return configChanged;
	}

	protected void handleMessage(final ConfigModel model, final AbstractRuleActionRAO action,
			final Map<String, CsticModel> csticMap)
	{
		final String code = action.getFiredRuleCode();
		final String moduleName = action.getModuleName();

		final AbstractRuleEngineRuleModel rule = getRuleEngineService().getRuleForCodeAndModule(code, moduleName);

		String messageFired = rule.getMessageFired(i18NService.getCurrentLocale());
		final String params = rule.getRuleParameters();

		final ProductConfigRuleMessageSeverity ruleMessageSeverity = rule.getMessageSeverity();
		final ProductConfigMessageSeverity messageSeverity = mapSeverity(ruleMessageSeverity);

		String ruleMessageForCstic = rule.getMessageForCstic();
		CsticModel cstic = null;
		boolean csticNameProvided = false;
		if (StringUtils.isNotEmpty(ruleMessageForCstic))
		{
			ruleMessageForCstic = ruleMessageForCstic.toUpperCase(Locale.ENGLISH);
			csticNameProvided = true;
			cstic = csticMap.get(ruleMessageForCstic);

			if (cstic == null)
			{
				LOG.error("Rule with code " + code + " tries to assign a message to the characteristic " + ruleMessageForCstic
						+ " of the model " + model.getName()
						+ " . However the root instance of this model does not contain the required characteristic.");
			}
		}

		logMessageData(code, messageFired, params, ruleMessageSeverity, cstic);

		if (StringUtils.isNotEmpty(messageFired))
		{
			if (messageContainsParameters(messageFired))
			{
				try
				{
					final List<RuleParameterData> paramList = getRuleParametersService().convertParametersFromString(params);
					messageFired = replaceMessageParameters(messageFired, paramList);
				}
				catch (final RuleConverterException ex)
				{
					LOG.error("Failed to parse rule message parmetrs. ", ex);
				}
				if (messageContainsParameters(messageFired))
				{
					LOG.error("Could not resolve all parameters of message. Please check in backoffice for rule '" + code
							+ "' whether the given UUID's are correct for message '" + messageFired + "'.");
				}
			}
			final ProductConfigMessage message = createMessage(code, messageFired, messageSeverity);

			if (cstic != null)
			{
				cstic.getMessages().add(message);

			}
			else if (!csticNameProvided)
			{
				model.getMessages().add(message);
			}
		}
	}

	protected void logMessageData(final String code, final String messageFired, final String params,
			final ProductConfigRuleMessageSeverity ruleMessageSeverity, final CsticModel cstic)
	{
		if (LOG.isDebugEnabled())
		{
			String logMessage = "Rule with code='" + code;
			if (StringUtils.isNotEmpty(messageFired))
			{
				logMessage += "' has messageFired='" + messageFired + "'.";
				if (StringUtils.isNotEmpty(params))
				{
					logMessage += " With params='" + params + "'.";
				}
			}
			else
			{
				logMessage += "' has no message assigned.";
			}
			LOG.debug(logMessage);
			LOG.debug(ruleMessageSeverity);

			if (cstic != null)
			{
				LOG.debug("Message is supposed to be applied to the cstic " + cstic.getLanguageDependentName() + " ("
						+ cstic.getLanguageDependentName() + ").");
			}
		}
	}

	protected ProductConfigMessageSeverity mapSeverity(final ProductConfigRuleMessageSeverity ruleMessageSeverity)
	{
		ProductConfigMessageSeverity severity;
		if (ruleMessageSeverity != null)
		{
			switch (ruleMessageSeverity)
			{
				case WARNING:
					severity = ProductConfigMessageSeverity.WARNING;
					break;
				case INFO:
					severity = ProductConfigMessageSeverity.INFO;
					break;
				default:
					severity = ProductConfigMessageSeverity.INFO;
					break;
			}
		}
		else
		{
			severity = ProductConfigMessageSeverity.INFO;
		}

		return severity;
	}

	protected boolean messageContainsParameters(final String messageFired)
	{
		return paramPattern.matcher(messageFired).matches();
	}

	protected String replaceMessageParameters(final String messageFired, final List<RuleParameterData> paramList)
	{
		String replacedMassage = messageFired;
		for (final RuleParameterData ruleParam : paramList)
		{
			final Pattern pattern = Pattern.compile(RULE_UUID_START + ruleParam.getUuid() + RULE_UUID_END);
			final String valueString = ruleParam.getValue() == null ? EMPTY_STRING : ruleParam.getValue().toString();
			replacedMassage = pattern.matcher(replacedMassage).replaceAll(valueString);
		}
		return replacedMassage;
	}

	protected ProductConfigMessage createMessage(final String code, final String messageFired,
			final ProductConfigMessageSeverity severity)
	{
		return getConfigModelFactory().createInstanceOfProductConfigMessage(messageFired, code, severity,
				ProductConfigMessageSource.RULE);
	}

	protected CsticModel getCstic(final ConfigModel model, final AbstractRuleActionRAO action,
			final Map<String, CsticModel> csticMap)
	{
		return getRuleActionChecker().getCstic(model, action, csticMap);
	}

	protected ProductConfigRuleActionStrategyChecker getRuleActionChecker()
	{
		return ruleActionChecker;
	}

	/**
	 * @param ruleActionChecker
	 */
	@Required
	public void setRuleActionChecker(final ProductConfigRuleActionStrategyChecker ruleActionChecker)
	{
		this.ruleActionChecker = ruleActionChecker;
	}

	protected ProductConfigRuleFormatTranslator getRulesFormator()
	{
		return rulesFormator;
	}

	/**
	 * @param rulesFormator
	 */
	@Required
	public void setRulesFormator(final ProductConfigRuleFormatTranslator rulesFormator)
	{
		this.rulesFormator = rulesFormator;
	}

	protected RuleEngineService getRuleEngineService()
	{
		return ruleEngineService;
	}

	/**
	 * @param ruleEngineService
	 */
	@Required
	public void setRuleEngineService(final RuleEngineService ruleEngineService)
	{
		this.ruleEngineService = ruleEngineService;
	}

	protected ConfigModelFactory getConfigModelFactory()
	{
		if (this.configModelFactory == null)
		{
			this.configModelFactory = (ConfigModelFactory) ServicelayerUtils.getApplicationContext()
					.getBean("sapProductConfigModelFactory");
			return this.configModelFactory;
		}
		return this.configModelFactory;
	}

	/**
	 * @param configModelFactory
	 */
	@Required
	public void setConfigModelFactory(final ConfigModelFactory configModelFactory)
	{
		this.configModelFactory = configModelFactory;
	}

	protected I18NService getI18NService()
	{
		return i18NService;
	}

	/**
	 * @param i18NService
	 */
	public void setI18NService(final I18NService i18NService)
	{
		this.i18NService = i18NService;
	}

	protected RuleParametersService getRuleParametersService()
	{
		return ruleParametersService;
	}

	/**
	 * @param ruleParametersService
	 */
	@Required
	public void setRuleParametersService(final RuleParametersService ruleParametersService)
	{
		this.ruleParametersService = ruleParametersService;
	}

	protected ProductConfigRuleUtil getRuleUtil()
	{
		return ruleUtil;
	}

	/**
	 * @param ruleUtil
	 */
	@Required
	public void setRuleUtil(final ProductConfigRuleUtil ruleUtil)
	{
		this.ruleUtil = ruleUtil;
	}

	protected abstract boolean executeAction(ConfigModel model, AbstractRuleActionRAO action, Map<String, CsticModel> csticMap);

	protected abstract boolean isActionPossible(final ConfigModel model, final AbstractRuleActionRAO action,
			Map<String, CsticModel> csticMap);
}
