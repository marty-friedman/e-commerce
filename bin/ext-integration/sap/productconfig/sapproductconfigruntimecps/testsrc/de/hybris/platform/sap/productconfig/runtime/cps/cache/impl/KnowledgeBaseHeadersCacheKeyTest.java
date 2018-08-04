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
public class KnowledgeBaseHeadersCacheKeyTest
{
	private static final String PRODUCT = "product";
	private KnowledgeBaseHeadersCacheKey classUnderTest;

	@Before
	public void setup()
	{
		classUnderTest = new KnowledgeBaseHeadersCacheKey(PRODUCT, AbstractCPSCacheKeyTest.TENANT_ID,
				AbstractCPSCacheKeyTest.CPS_SERVICE_URL, AbstractCPSCacheKeyTest.CPS_SERVICE_TENANT);
	}

	@Test
	public void testConstructor()
	{
		assertEquals(PRODUCT, classUnderTest.getProduct());
		assertEquals(KnowledgeBaseHeadersCacheKey.TYPECODE_KNOWLEDGEBASES, classUnderTest.getTypeCode());
	}

	@Test
	public void testHashCodeEquals()
	{
		final int hashValue = classUnderTest.hashCode();
		assertTrue(hashValue != 0);
		final KnowledgeBaseHeadersCacheKey anotherKey = new KnowledgeBaseHeadersCacheKey(PRODUCT, AbstractCPSCacheKeyTest.TENANT_ID,
				AbstractCPSCacheKeyTest.CPS_SERVICE_URL, AbstractCPSCacheKeyTest.CPS_SERVICE_TENANT);
		final int anotherHashValue = anotherKey.hashCode();
		assertTrue(hashValue == anotherHashValue);
	}

	@Test
	public void testHashCodeDifferentProduct()
	{
		final int hashValue = classUnderTest.hashCode();
		assertTrue(hashValue != 0);
		final KnowledgeBaseHeadersCacheKey anotherKey = new KnowledgeBaseHeadersCacheKey("another lang",
				AbstractCPSCacheKeyTest.TENANT_ID, AbstractCPSCacheKeyTest.CPS_SERVICE_URL,
				AbstractCPSCacheKeyTest.CPS_SERVICE_TENANT);
		final int anotherHashValue = anotherKey.hashCode();
		assertFalse(hashValue == anotherHashValue);
	}

	@Test
	public void testHashCodeAllNull()
	{
		final KnowledgeBaseHeadersCacheKey anotherKey = new KnowledgeBaseHeadersCacheKey(null, null, null, null);
		assertEquals(new AbstractCPSCacheKey(KnowledgeBaseHeadersCacheKey.TYPECODE_KNOWLEDGEBASES, null, null, null).hashCode() ^ 0,
				anotherKey.hashCode());
	}

	@Test
	public void testEquals()
	{
		final KnowledgeBaseHeadersCacheKey anotherKey = new KnowledgeBaseHeadersCacheKey(PRODUCT, AbstractCPSCacheKeyTest.TENANT_ID,
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
	public void testEqualsDifferentProduct()
	{
		final KnowledgeBaseHeadersCacheKey anotherKey = new KnowledgeBaseHeadersCacheKey("another product",
				AbstractCPSCacheKeyTest.TENANT_ID, AbstractCPSCacheKeyTest.CPS_SERVICE_URL,
				AbstractCPSCacheKeyTest.CPS_SERVICE_TENANT);
		assertFalse(classUnderTest.equals(anotherKey));
	}

	@Test
	public void testToString()
	{
		final String result = classUnderTest.toString();
		assertTrue(result.contains(PRODUCT));
		assertTrue(result.contains(AbstractCPSCacheKeyTest.TENANT_ID));
		assertTrue(result.contains(AbstractCPSCacheKeyTest.CPS_SERVICE_URL));
		assertTrue(result.contains(AbstractCPSCacheKeyTest.CPS_SERVICE_TENANT));
		assertTrue(result.contains(KnowledgeBaseHeadersCacheKey.TYPECODE_KNOWLEDGEBASES));
	}

}
