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
package de.hybris.platform.sap.productconfig.runtime.interf.model.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;
import de.hybris.platform.sap.productconfig.runtime.interf.model.SolvableConflictModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;


@UnitTest
public class ConfigModelImplTest
{
	private final ConfigModelImpl classUnderTest = new ConfigModelImpl();

	@Test
	public void testToStringSolvableConflicts()
	{
		final List<SolvableConflictModel> solvableConflicts = new ArrayList<SolvableConflictModel>();
		final SolvableConflictModel solvableConflict = new SolvableConflictModelImpl();
		final String description = "This is a description";
		solvableConflict.setDescription(description);
		solvableConflicts.add(solvableConflict);
		classUnderTest.setSolvableConflicts(solvableConflicts);
		assertTrue("We expect the description of the conflict to appear in toString", classUnderTest.toString()
				.indexOf(description) > -1);
	}


	@Test
	public void testSetGetMessageList()
	{
		final Set<ProductConfigMessage> messages = new HashSet();
		classUnderTest.setMessages(messages);
		assertSame(messages, classUnderTest.getMessages());
	}

	@Test
	public void testGetMessageListNotNull()
	{
		assertNotNull(classUnderTest.getMessages());
	}

}
