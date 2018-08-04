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
package de.hybris.platform.cmssmarteditwebservices.structures.facade.impl;

import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.types.ComponentTypeNotFoundException;
import de.hybris.platform.cmssmarteditwebservices.structures.service.TypeStructureService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultStructureFacadeTest
{
	private static final String ITEM_TYPECODE = "itemType";
	private static final String MODE = "MODE";

	@InjectMocks
	@Spy
	private DefaultStructureFacade facade;
	@Mock
	private TypeStructureService typeStructureService;

	@Test
	public void testGetComponentTypesByCode() throws ComponentTypeNotFoundException
	{
		facade.getComponentTypesByCode(ITEM_TYPECODE);
		verify(typeStructureService).getComponentTypesByCode(ITEM_TYPECODE);
	}

	@Test
	public void testGetComponentTypeByCodeAndMode() throws ComponentTypeNotFoundException
	{
		facade.getComponentTypeByCodeAndMode(ITEM_TYPECODE, MODE);
		verify(typeStructureService).getComponentTypeByCodeAndMode(ITEM_TYPECODE, MODE);
	}

}
