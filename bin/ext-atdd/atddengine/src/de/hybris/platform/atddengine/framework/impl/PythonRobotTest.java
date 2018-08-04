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

package de.hybris.platform.atddengine.framework.impl;

import de.hybris.platform.atddengine.framework.PythonWrapper;
import de.hybris.platform.atddengine.framework.RobotTest;
import de.hybris.platform.atddengine.framework.RobotTestResult;
import de.hybris.platform.atddengine.framework.RobotTestSuite;

import org.python.core.PyObject;


public class PythonRobotTest extends AbstractPythonAware implements RobotTest, PythonWrapper
{
	private final RobotTestSuite robotTestSuite;

	private final PyObject test;

	public PythonRobotTest(final PythonRobotTestSuite robotTestSuite, final PyObject test)
	{
		super(robotTestSuite);

		this.robotTestSuite = robotTestSuite;
		this.test = test;
	}

	@Override
	public String getName()
	{
		return test.__getattr__("name").asString();
	}

	@Override
	public PyObject getPyObject()
	{
		return test;
	}

	@Override
	public RobotTestSuite getTestSuite()
	{
		return robotTestSuite;
	}

	@Override
	public RobotTestResult run()
	{
		if (getTestSuite().isStarted())
		{
			if (getTestSuite().isClosed())
			{
				throw new IllegalStateException("Parent robotTestSuite has already been closed.");
			}
			else
			{
				return getTestSuite().run(this);
			}
		}
		else
		{
			throw new IllegalStateException("Parent robotTestSuite has not been started yet.");
		}
	}
}
