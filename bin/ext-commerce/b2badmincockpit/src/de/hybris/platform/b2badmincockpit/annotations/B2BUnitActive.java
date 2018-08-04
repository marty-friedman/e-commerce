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
package de.hybris.platform.b2badmincockpit.annotations;

import de.hybris.platform.b2badmincockpit.validators.B2BUnitActiveValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;


@Target(
{ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = B2BUnitActiveValidator.class)
@Documented
public @interface B2BUnitActive
{
	String message() default "{de.hybris.platform.b2badmincockpit.B2BUnitActive.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
