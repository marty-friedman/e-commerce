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
package de.hybris.platform.personalizationyprofile.mapper.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.personalizationyprofile.mapper.affinity.impl.CxConsumptionLayerSumAffinityStrategy;
import de.hybris.platform.personalizationyprofile.yaas.Affinity;
import de.hybris.platform.personalizationyprofile.yaas.Profile;

import java.math.BigDecimal;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@UnitTest
public class CxCategoryConsumptionLayerMapperTest extends AbstractCxConsumptionLayerMapperTest
{
	private static final String CATEGORY_1 = "c1";
	private static final String CATEGORY_2 = "c2";

	public CxCategoryConsumptionLayerMapper categoryMapper = new CxCategoryConsumptionLayerMapper();


	@Override
	@Before
	public void init()
	{
		super.init();
		categoryMapper.setConfigurationService(configurationService);
		categoryMapper.setAffinityStrategy(new CxConsumptionLayerSumAffinityStrategy());
	}

	@Test
	public void testNullSource()
	{
		//given
		final Profile source = null;

		//when
		categoryMapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(0, target.getSegments().size());
	}


	@Test
	public void testMissingInsights()
	{
		//given
		final Profile source = new Profile();

		//when
		categoryMapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(0, target.getSegments().size());
	}

	@Test
	public void testMissingAffinities()
	{
		//given
		final Profile source = createProfile(null);
		source.getInsights().setAffinities(null);

		//when
		categoryMapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(0, target.getSegments().size());
	}

	@Test
	public void testMissingCategories()
	{
		//given
		final Profile source = createProfile(null);

		//when
		categoryMapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(0, target.getSegments().size());
	}

	@Test
	public void testEmptyCategories()
	{
		//given
		final Profile source = createProfile(new HashMap<>());

		//when
		categoryMapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(0, target.getSegments().size());
	}

	@Test
	public void testSingleCategoryAffinity()
	{
		//given
		final HashMap<String, Affinity> categoryAffinity = new HashMap<>();
		categoryAffinity.put(CATEGORY_1, createAffinity(BigDecimal.valueOf(10), BigDecimal.valueOf(10)));
		final Profile source = createProfile(categoryAffinity);

		//when
		categoryMapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(1, target.getSegments().size());
		assertAffinityForSegment(categoryMapper.getSegmentCode(CATEGORY_1), "20", target);
	}

	@Test
	public void testMultipleCategoryInput()
	{
		//given
		final HashMap<String, Affinity> categoryAffinity = new HashMap<>();
		categoryAffinity.put(CATEGORY_1, createAffinity(BigDecimal.valueOf(10), BigDecimal.valueOf(10)));
		categoryAffinity.put(CATEGORY_2, createAffinity(BigDecimal.valueOf(0), BigDecimal.valueOf(10)));
		final Profile source = createProfile(categoryAffinity);

		//when
		categoryMapper.populate(source, target);

		//then
		Assert.assertNotNull(target.getSegments());
		Assert.assertEquals(2, target.getSegments().size());
		assertAffinityForSegment(categoryMapper.getSegmentCode(CATEGORY_1), "20", target);
		assertAffinityForSegment(categoryMapper.getSegmentCode(CATEGORY_2), "10", target);
	}

	protected Profile createProfile(final HashMap<String, Affinity> categoryAffinities)
	{
		final Profile profile = createProfile();
		profile.getInsights().getAffinities().setCategories(categoryAffinities);
		return profile;
	}
}
