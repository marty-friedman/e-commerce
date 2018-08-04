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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigModelFactoryImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSeverity;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSource;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSourceSubType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class CsticModelImplTest
{
	private CsticModelImpl model;

	@Before
	public void setUp()
	{
		model = new CsticModelImpl();
		model.setConfigModelFactory(new ConfigModelFactoryImpl());
	}

	@Test
	public void testSetSingleValue()
	{
		assertFalse(model.isChangedByFrontend());
		assertEquals(0, model.getAssignedValues().size());

		model.setSingleValue("newValue");
		assertTrue(model.isChangedByFrontend());
		assertEquals(1, model.getAssignedValues().size());
		assertEquals("newValue", model.getSingleValue());
	}

	@Test
	public void testSetSingleValue_notChanged()
	{
		final CsticValueModel value = new CsticValueModelImpl();
		value.setName("newValue");
		model.setAssignedValuesWithoutCheckForChange(Collections.singletonList(value));
		assertFalse(model.isChangedByFrontend());
		assertEquals(1, model.getAssignedValues().size());

		model.setSingleValue("newValue");
		assertFalse(model.isChangedByFrontend());
		assertEquals(1, model.getAssignedValues().size());
		assertEquals("newValue", model.getAssignedValues().get(0).getName());
	}

	@Test
	public void testAddValue()
	{
		final CsticValueModel value = new CsticValueModelImpl();
		value.setName("anotherValue");
		model.setAssignedValuesWithoutCheckForChange(Collections.singletonList(value));
		assertFalse(model.isChangedByFrontend());
		assertEquals(1, model.getAssignedValues().size());

		model.addValue("newValue");
		assertTrue(model.isChangedByFrontend());
		assertEquals(2, model.getAssignedValues().size());
		assertEquals("newValue", model.getAssignedValues().get(1).getName());
	}

	@Test
	public void testRemoveExistingValue()
	{
		final String value = "value";
		model.addValue(value);
		model.setChangedByFrontend(false);

		model.removeValue(value);

		assertTrue("Model was changed", model.isChangedByFrontend());
		assertEquals("Wrong number of values", 0, model.getAssignedValues().size());
	}

	@Test
	public void testRemoveNonExistingValue()
	{
		model.addValue("value1");
		model.setChangedByFrontend(false);

		model.removeValue("value2");

		assertFalse("Model was not changed", model.isChangedByFrontend());
		assertEquals("Wrong number of values", 1, model.getAssignedValues().size());
	}

	@Test
	public void testAddValue_notChanged()
	{
		final List<CsticValueModel> assignedValues = new ArrayList<CsticValueModel>();
		CsticValueModel value = new CsticValueModelImpl();
		value.setName("anotherValue");
		assignedValues.add(value);
		value = new CsticValueModelImpl();
		value.setName("newValue");
		assignedValues.add(value);
		model.setAssignedValuesWithoutCheckForChange(assignedValues);
		assertFalse(model.isChangedByFrontend());
		assertEquals(2, model.getAssignedValues().size());

		model.addValue("newValue");
		assertFalse(model.isChangedByFrontend());
		assertEquals(2, model.getAssignedValues().size());
		assertEquals("newValue", model.getAssignedValues().get(1).getName());
	}

	@Test
	public void testEquals()
	{
		final CsticModel cstic1 = new CsticModelImpl();
		assertFalse(cstic1.equals(null));
		assertFalse(cstic1.equals("FALSE"));

		final CsticModel cstic2 = new CsticModelImpl();

		assertTrue(cstic1.equals(cstic2));
	}

	@Test
	public void testInstanceId()
	{
		final String instanceId = "1";
		model.setInstanceId(instanceId);
		assertEquals(instanceId, model.getInstanceId());
	}

	@Test
	public void testRetractTriggered()
	{
		model.setRetractTriggered(true);
		assertTrue(model.isRetractTriggered());
	}

	@Test
	public void testRemoveFromEmptyAssignableValue()
	{

		final List<CsticValueModel> assignableValues = Collections.EMPTY_LIST;
		model.setAssignableValues(assignableValues);
		final boolean removed = model.removeAssignableValue("value1");

		assertFalse("Assignable value is not expected to be removed", removed);
	}

	@Test
	public void testRemoveFromNullAssignableValue()
	{

		final List<CsticValueModel> assignableValues = null;
		model.setAssignableValues(assignableValues);
		final boolean removed = model.removeAssignableValue("value2");

		assertFalse("Assignable value is not expected to be removed", removed);
	}


	@Test
	public void testRemoveExistingAssignableValue()
	{
		final CsticValueModel valueModel1 = new CsticValueModelImpl();
		valueModel1.setName("value1");

		final CsticValueModel valueModel2 = new CsticValueModelImpl();
		valueModel2.setName("value2");

		final List<CsticValueModel> assignableValues = new ArrayList<CsticValueModel>();
		assignableValues.add(valueModel1);
		assignableValues.add(valueModel2);

		model.setAssignableValues(assignableValues);

		final boolean removed = model.removeAssignableValue("value1");

		assertTrue("Assignable value is expected to be removed", removed);
		assertEquals("Wrong number of assignable values", 1, model.getAssignableValues().size());
	}

	@Test
	public void testRemoveNonExistingAssignableValue()
	{
		final CsticValueModel valueModel1 = new CsticValueModelImpl();
		valueModel1.setName("value1");

		final CsticValueModel valueModel2 = new CsticValueModelImpl();
		valueModel2.setName("value2");

		final List<CsticValueModel> assignableValues = new ArrayList<CsticValueModel>();
		assignableValues.add(valueModel1);
		assignableValues.add(valueModel2);

		model.setAssignableValues(assignableValues);

		final boolean removed = model.removeAssignableValue("value3");

		assertFalse("Assignable value is not expected to be removed", removed);
		assertEquals("Wrong number of assignable values", 2, model.getAssignableValues().size());
	}


	@Test
	public void testSetGetMessageList()
	{
		final Set<ProductConfigMessage> messages = new HashSet();
		model.setMessages(messages);
		assertSame(messages, model.getMessages());
	}

	@Test
	public void testGetMessageListNotNull()
	{
		assertNotNull(model.getMessages());
	}

	@Test
	public void testAddMessagetoList()
	{
		model.getMessages().add(new ProductConfigMessageImpl("message", "key", ProductConfigMessageSeverity.INFO,
				ProductConfigMessageSource.ENGINE, ProductConfigMessageSourceSubType.DEFAULT));
	}

	@Test
	public void testInstanceName()
	{
		final String instanceName = "PRODUCT_KEY";
		model.setInstanceName(instanceName);
		assertEquals(instanceName, model.getInstanceName());
	}
}
