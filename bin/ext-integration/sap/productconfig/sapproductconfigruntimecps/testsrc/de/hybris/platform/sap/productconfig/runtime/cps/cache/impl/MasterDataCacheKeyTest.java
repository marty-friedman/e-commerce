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

import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("javadoc")
@UnitTest
public class MasterDataCacheKeyTest
{
	private static final String LANG = "en";
	private static final String KB_ID = "kbId";
	private MasterDataCacheKey classUnderTest;

	@Before
	public void setup()
	{
		classUnderTest = new MasterDataCacheKey(KB_ID, LANG, AbstractCPSCacheKeyTest.TENANT_ID,
				AbstractCPSCacheKeyTest.CPS_SERVICE_URL, AbstractCPSCacheKeyTest.CPS_SERVICE_TENANT);
	}

	@Test
	public void testConstructor()
	{
		assertEquals(KB_ID, classUnderTest.getKbId());
		assertEquals(LANG, classUnderTest.getLang());
		assertEquals(MasterDataCacheKey.TYPECODE_MASTER_DATA, classUnderTest.getTypeCode());
	}

	@Test
	public void testHashCodeEquals()
	{
		final int hashValue = classUnderTest.hashCode();
		assertTrue(hashValue != 0);
		final MasterDataCacheKey anotherKey = new MasterDataCacheKey(KB_ID, LANG, AbstractCPSCacheKeyTest.TENANT_ID,
				AbstractCPSCacheKeyTest.CPS_SERVICE_URL, AbstractCPSCacheKeyTest.CPS_SERVICE_TENANT);
		final int anotherHashValue = anotherKey.hashCode();
		assertTrue(hashValue == anotherHashValue);
	}

	@Test
	public void testHashCodeDifferentKb()
	{
		final int hashValue = classUnderTest.hashCode();
		assertTrue(hashValue != 0);
		final MasterDataCacheKey anotherKey = new MasterDataCacheKey("another kb id", LANG, AbstractCPSCacheKeyTest.TENANT_ID,
				AbstractCPSCacheKeyTest.CPS_SERVICE_URL, AbstractCPSCacheKeyTest.CPS_SERVICE_TENANT);
		final int anotherHashValue = anotherKey.hashCode();
		assertFalse(hashValue == anotherHashValue);
	}

	@Test
	public void testHashCodeDifferentLanguage()
	{
		final int hashValue = classUnderTest.hashCode();
		assertTrue(hashValue != 0);
		final MasterDataCacheKey anotherKey = new MasterDataCacheKey(KB_ID, "another lang", AbstractCPSCacheKeyTest.TENANT_ID,
				AbstractCPSCacheKeyTest.CPS_SERVICE_URL, AbstractCPSCacheKeyTest.CPS_SERVICE_TENANT);
		final int anotherHashValue = anotherKey.hashCode();
		assertFalse(hashValue == anotherHashValue);
	}


	@Test
	public void testHashCodeAllNull()
	{
		final MasterDataCacheKey anotherKey = new MasterDataCacheKey(null, null, null, null, null);
		assertEquals(new AbstractCPSCacheKey(MasterDataCacheKey.TYPECODE_MASTER_DATA, null, null, null).hashCode() ^ 0 ^ 0,
				anotherKey.hashCode());
	}

	@Test
	public void testEquals()
	{
		final MasterDataCacheKey anotherKey = new MasterDataCacheKey(KB_ID, LANG, AbstractCPSCacheKeyTest.TENANT_ID,
				AbstractCPSCacheKeyTest.CPS_SERVICE_URL, AbstractCPSCacheKeyTest.CPS_SERVICE_TENANT);
		assertTrue(classUnderTest.equals(anotherKey));
	}

	@Test
	public void testEqualsDifferentClass()
	{
		final CacheKey anotherKey = new MediaCacheKey(AbstractCPSCacheKeyTest.TENANT_ID, "folder", "location");
		assertFalse(classUnderTest.equals(anotherKey));
	}

	@Test
	public void testEqualsDifferentKb()
	{
		final MasterDataCacheKey anotherKey = new MasterDataCacheKey("another kb", LANG, AbstractCPSCacheKeyTest.TENANT_ID,
				AbstractCPSCacheKeyTest.CPS_SERVICE_URL, AbstractCPSCacheKeyTest.CPS_SERVICE_TENANT);
		assertFalse(classUnderTest.equals(anotherKey));
	}

	@Test
	public void testEqualsDifferentLang()
	{
		final MasterDataCacheKey anotherKey = new MasterDataCacheKey(KB_ID, "another lang", AbstractCPSCacheKeyTest.TENANT_ID,
				AbstractCPSCacheKeyTest.CPS_SERVICE_URL, AbstractCPSCacheKeyTest.CPS_SERVICE_TENANT);
		assertFalse(classUnderTest.equals(anotherKey));
	}

	@Test
	public void testToString()
	{
		final String result = classUnderTest.toString();
		assertTrue(result.contains(KB_ID));
		assertTrue(result.contains(LANG));
		assertTrue(result.contains(AbstractCPSCacheKeyTest.TENANT_ID));
		assertTrue(result.contains(AbstractCPSCacheKeyTest.CPS_SERVICE_URL));
		assertTrue(result.contains(AbstractCPSCacheKeyTest.CPS_SERVICE_TENANT));
		assertTrue(result.contains(MasterDataCacheKey.TYPECODE_MASTER_DATA));
	}

}
