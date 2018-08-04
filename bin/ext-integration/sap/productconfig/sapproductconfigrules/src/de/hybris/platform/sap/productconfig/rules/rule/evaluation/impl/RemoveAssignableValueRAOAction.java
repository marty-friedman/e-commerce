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
import de.hybris.platform.sap.productconfig.rules.rao.CsticRAO;
import de.hybris.platform.sap.productconfig.rules.rao.CsticValueRAO;
import de.hybris.platform.sap.productconfig.rules.rao.action.RemoveAssignableValueRAO;

import java.util.Map;


/**
 * Encapsulates logic of removing of a characteristic assignable value as rule action.
 */
public class RemoveAssignableValueRAOAction extends ProductConfigAbstractRAOAction
{

	@Override
	public void performAction(final RuleActionContext context)
	{
		final Map<String, Object> parameters = context.getParameters();

		validateRuleAndLog(context, parameters, CSTIC_NAME, CSTIC_VALUE);

		if (validateProcessStep(context, parameters, ProcessStep.RETRIEVE_CONFIGURATION) && validateAllowedByRuntime(context))
		{
			final CsticRAO csticRao = createCsticRAO(parameters);
			final CsticValueRAO valueRao = createCsticValueRAO(parameters);

			final RemoveAssignableValueRAO removeAssignableValueRAO = new RemoveAssignableValueRAO();
			removeAssignableValueRAO.setAppliedToObject(csticRao);
			removeAssignableValueRAO.setValueNameToRemoveFromAssignable(valueRao);

			updateContext(context, removeAssignableValueRAO);
		}
	}

	@Override
	protected String prepareActionLogText(final RuleActionContext context, final Map<String, Object> parameters)
	{
		final String csticName = (String) parameters.get(CSTIC_NAME);
		final String csticValue = (String) parameters.get(CSTIC_VALUE);
		return "Hence skipping removal of assignable value " + csticValue + " for cstic " + csticName + ".";
	}
}
