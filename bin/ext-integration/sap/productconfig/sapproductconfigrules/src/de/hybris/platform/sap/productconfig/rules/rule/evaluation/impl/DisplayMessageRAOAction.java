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
package de.hybris.platform.sap.productconfig.rules.rule.evaluation.impl;

import de.hybris.platform.ruleengineservices.rao.ProcessStep;
import de.hybris.platform.ruleengineservices.rule.evaluation.RuleActionContext;
import de.hybris.platform.sap.productconfig.rules.rao.action.ProductConfigDisplayMessageRAO;

import java.util.Map;


/**
 * Encapsulates logic of removing of a characteristic assignable value as rule action.
 */
public class DisplayMessageRAOAction extends ProductConfigAbstractRAOAction
{

	@Override
	public void performAction(final RuleActionContext context)
	{
		final Map<String, Object> parameters = context.getParameters();

		validateRuleAndLog(context, parameters);

		if (validateProcessStep(context, parameters, ProcessStep.CREATE_DEFAULT_CONFIGURATION, ProcessStep.RETRIEVE_CONFIGURATION)
				&& validateAllowedByRuntime(context))
		{
			final ProductConfigDisplayMessageRAO displayMessageRao = new ProductConfigDisplayMessageRAO();
			updateContext(context, displayMessageRao);
		}
	}

	@Override
	protected String prepareActionLogText(final RuleActionContext context, final Map<String, Object> parameters)
	{
		return "Hence skipping display message.";
	}

}
