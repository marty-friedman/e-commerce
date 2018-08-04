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
package de.hybris.platform.acceleratorservices.dataexport.generic.output.csv;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(
{ ElementType.METHOD })
public @interface DelimitedFileMethod
{
	/**
	 * The column position of the attribute
	 */
	int position();

	/**
	 * Specify a value if the getter returns null. default is an empty string.
	 */
	String nullValue() default "";

	/**
	 * name of the property if not the name itself
	 */
	String name() default "";
}
