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
package de.hybris.platform.sap.productconfig.runtime.cps.cache.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.media.storage.impl.DefaultLocalMediaFileCacheService.MediaCacheKey;
import de.hybris.platform.regioncache.key.CacheKey;
import de.hybris.platform.regioncache.key.CacheUnitValueType;

import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("javadoc")
@UnitTest
public class AbstractCPSCacheKeyTest
{
	private static final String TYPE = "type";
	protected static final String TENANT_ID = "tenantId";
	protected static final String CPS_SERVICE_URL = "cps service url";
	protected static final String CPS_SERVICE_TENANT = "cps service tenant";
	private AbstractCPSCacheKey classUnderTest;

	@Before
	public void setup()
	{
		classUnderTest = new AbstractCPSCacheKey(TYPE, TENANT_ID, CPS_SERVICE_URL, CPS_SERVICE_TENANT);
	}

	@Test
	public void testConstructor()
	{
		assertEquals(CPS_SERVICE_URL, classUnderTest.getCpsServiceUrl());
		assertEquals(CPS_SERVICE_TENANT, classUnderTest.getCpsServiceTenant());
		assertEquals(CacheUnitValueType.NON_SERIALIZABLE, classUnderTest.getCacheValueType());
		assertEquals(TENANT_ID, classUnderTest.getTenantId());
		assertEquals(TYPE, classUnderTest.getTypeCode());
	}

	@Test
	public void testHashCodeEquals()
	{
		final int hashValue = classUnderTest.hashCode();
		assertTrue(hashValue != 0);
		final AbstractCPSCacheKey anotherKey = new AbstractCPSCacheKey(TYPE, TENANT_ID, CPS_SERVICE_URL, CPS_SERVICE_TENANT);
		final int anotherHashValue = anotherKey.hashCode();
		assertTrue(hashValue == anotherHashValue);
	}

	@Test
	public void testHashCodeDifferentServiceUrl()
	{
		final int hashValue = classUnderTest.hashCode();
		assertTrue(hashValue != 0);
		final AbstractCPSCacheKey anotherKey = new AbstractCPSCacheKey(TYPE, TENANT_ID, "another service url", CPS_SERVICE_TENANT);
		final int anotherHashValue = anotherKey.hashCode();
		assertFalse(hashValue == anotherHashValue);
	}

	@Test
	public void testHashCodeDifferentServiceTenant()
	{
		final int hashValue = classUnderTest.hashCode();
		assertTrue(hashValue != 0);
		final AbstractCPSCacheKey anotherKey = new AbstractCPSCacheKey(TYPE, TENANT_ID, CPS_SERVICE_URL, "another service tenant");
		final int anotherHashValue = anotherKey.hashCode();
		assertFalse(hashValue == anotherHashValue);
	}

	@Test
	public void testHashCodeDifferentTenant()
	{
		final int hashValue = classUnderTest.hashCode();
		assertTrue(hashValue != 0);
		final AbstractCPSCacheKey anotherKey = new AbstractCPSCacheKey(TYPE, "another hybris tenant", CPS_SERVICE_URL,
				CPS_SERVICE_TENANT);
		final int anotherHashValue = anotherKey.hashCode();
		assertFalse(hashValue == anotherHashValue);
	}

	@Test
	public void testHashCodeAllNull()
	{
		final AbstractCPSCacheKey anotherKey = new AbstractCPSCacheKey(TYPE, null, null, null);
		assertEquals((AbstractCPSCacheKey.PRIME + TYPE.hashCode()) ^ 0 ^ 0 ^ 0, anotherKey.hashCode());
	}

	@Test
	public void testEquals()
	{
		final AbstractCPSCacheKey anotherKey = new AbstractCPSCacheKey(TYPE, TENANT_ID, CPS_SERVICE_URL, CPS_SERVICE_TENANT);
		assertTrue(classUnderTest.equals(anotherKey));
	}

	@Test
	public void testEqualsDifferentClass()
	{
		final CacheKey anotherKey = new MediaCacheKey(TENANT_ID, "folder", "location");
		assertFalse(classUnderTest.equals(anotherKey));
	}

	@Test
	public void testEqualsDifferentServiceUrl()
	{
		final AbstractCPSCacheKey anotherKey = new AbstractCPSCacheKey(TYPE, TENANT_ID, "another service url", CPS_SERVICE_TENANT);
		assertFalse(classUnderTest.equals(anotherKey));
	}

	@Test
	public void testEqualsDifferentServiceTenant()
	{
		final AbstractCPSCacheKey anotherKey = new AbstractCPSCacheKey(TYPE, TENANT_ID, CPS_SERVICE_URL, "another service tenant");
		assertFalse(classUnderTest.equals(anotherKey));
	}

	@Test(expected = NullPointerException.class)
	public void testEqualsNulls()
	{
		final AbstractCPSCacheKey anotherKey = new AbstractCPSCacheKey(TYPE, null, CPS_SERVICE_URL, CPS_SERVICE_TENANT);
		assertFalse(anotherKey.equals(classUnderTest));
	}

}
