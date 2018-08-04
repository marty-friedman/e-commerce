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
package de.hybris.platform.sap.productconfig.runtime.cps.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("javadoc")
@UnitTest
public class CPSSessionAttributeContainerTest
{
	private static final String configid = "Id";
	private static final String cookie = "Cookie";
	CPSSessionAttributeContainer classUnderTest = new CPSSessionAttributeContainer();
	private final List<String> cookieList = new ArrayList<String>();
	private byte[] serializeds;
	private Object deSerializedObject;
	private CPSSessionAttributeContainer newContainer;

	@Before
	public void initialize()
	{
		cookieList.add(cookie);
		classUnderTest.setCookies(configid, cookieList);
	}

	@Test
	public void testGetCookies()
	{
		assertEquals(cookieList, classUnderTest.getCookies(configid));
	}

	@Test
	public void testRemoveCookies()
	{
		classUnderTest.removeCookies(configid);
		assertNull(classUnderTest.getCookies(configid));
	}

	@Test
	public void testDeserializePricingDocumentInputMap()
	{
		serializeAndDeserialize();
		assertNotNull(newContainer.getPricingDocumentInputMap());
	}

	@Test
	public void testDeserializeETagMap()
	{
		serializeAndDeserialize();
		assertNotNull(newContainer.getETagMap());
	}


	@Test
	public void testDeserializePricingDocumentResultMap()
	{
		serializeAndDeserialize();
		assertNotNull(newContainer.getPricingDocumentResultMap());
	}

	@Test
	public void testDeserializeValuePricesMap()
	{
		serializeAndDeserialize();
		assertNotNull(newContainer.getValuePricesMap());
	}

	@Test
	public void testDeserializeCookieList()
	{
		serializeAndDeserialize();
		//Deserialize must create a new list instance (which is empty at this point)
		assertNull(newContainer.getCookies(configid));
	}

	protected void serializeAndDeserialize()
	{
		serializeds = SerializationUtils.serialize(classUnderTest);
		deSerializedObject = SerializationUtils.deserialize(serializeds);
		assertTrue(deSerializedObject instanceof CPSSessionAttributeContainer);
		newContainer = (CPSSessionAttributeContainer) deSerializedObject;
	}
}
