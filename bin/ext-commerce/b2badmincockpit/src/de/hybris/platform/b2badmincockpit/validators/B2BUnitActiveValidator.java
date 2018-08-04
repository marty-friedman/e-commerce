/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.b2badmincockpit.validators;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2badmincockpit.annotations.B2BUnitActive;
import de.hybris.platform.servicelayer.internal.model.impl.ModelValueHistory;
import de.hybris.platform.servicelayer.model.ItemModelContextImpl;
import de.hybris.platform.servicelayer.model.ModelContextUtils;

import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.log4j.Logger;


public class B2BUnitActiveValidator implements ConstraintValidator<B2BUnitActive, Object>
{

	private final static Logger LOG = Logger.getLogger(B2BUnitActiveValidator.class);

	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context)
	{
		final B2BUnitModel unit = (B2BUnitModel) value;

		final ItemModelContextImpl ctx = (ItemModelContextImpl) ModelContextUtils.getItemModelContext(unit);
		final ModelValueHistory valueHistory = ctx.getValueHistory();
		final Set<String> dirtyAttributes = valueHistory.getDirtyAttributes();

		return !dirtyAttributes.contains("active");
	}

	@Override
	public void initialize(final B2BUnitActive arg0)
	{
		LOG.debug("B2BUnitActivValidator.initialize() called!");
	}
}
