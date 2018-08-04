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
package de.hybris.platform.personalizationservices.segment.impl;


import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.personalizationservices.AbstractCxServiceTest;
import de.hybris.platform.personalizationservices.model.CxSegmentModel;
import de.hybris.platform.personalizationservices.model.CxUserToSegmentModel;
import de.hybris.platform.personalizationservices.segment.CxSegmentService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultCxUserSegmentServiceIntegrationTest extends AbstractCxServiceTest
{
	private static final String SEGMENT1 = "segment1";
	private static final String SEGMENT2 = "segment2";
	private static final String SEGMENT3 = "segment3";
	private static final String SEGMENT4 = "segment4";
	private static final String CUSTOMER_WITH_SEGMENT1 = "customer2@hybris.com";
	private static final String CUSTOMER_WITH_SEGMENT2_3 = "customer6@hybris.com";
	private static final String CUSTOMER_WITH_SEGMENT1_2_3_FOR_TWO_BASESITES = "customer7@hybris.com";
	private static final String BASE_SITE = "testSite";
	private static final String BASE_SITE_1 = "testSite1";


	@Resource
	private DefaultCxUserSegmentService cxUserSegmentService;
	@Resource
	private UserService userService;
	@Resource
	private CxSegmentService cxSegmentService;
	@Resource
	private BaseSiteService baseSiteService;

	private BaseSiteModel baseSite;

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		baseSite = baseSiteService.getBaseSiteForUID(BASE_SITE);
		baseSiteService.setCurrentBaseSite(baseSite, false);
	}

	@Test
	public void testGetUserSegments()
	{
		//given
		final UserModel user = userService.getUserForUID(CUSTOMER_WITH_SEGMENT2_3); //
		final Collection<CxUserToSegmentModel> expected = createUserSegments(user, BigDecimal.ONE, baseSite, SEGMENT2, SEGMENT3);

		//when
		final Collection<CxUserToSegmentModel> result = cxUserSegmentService.getUserSegments(user);

		//then
		verifySegments(expected, result);
	}

	@Test
	public void testGetUserSegmentsForBaseSite()
	{
		//given
		final UserModel user = userService.getUserForUID(CUSTOMER_WITH_SEGMENT2_3); //
		final Collection<CxUserToSegmentModel> expected = createUserSegments(user, BigDecimal.ONE, baseSite, SEGMENT2, SEGMENT3);

		//when
		final Collection<CxUserToSegmentModel> result = cxUserSegmentService.getUserSegments(user, baseSite);

		//then
		verifySegments(expected, result);
	}

	@Test
	public void testGetUserSegmentsWhenDataForTwoBaseSite()
	{
		//given
		final UserModel user = userService.getUserForUID(CUSTOMER_WITH_SEGMENT1_2_3_FOR_TWO_BASESITES); //
		final Collection<CxUserToSegmentModel> expected = createUserSegments(user, BigDecimal.ONE, baseSite, SEGMENT1, SEGMENT2,
				SEGMENT3);

		//when
		final Collection<CxUserToSegmentModel> result = cxUserSegmentService.getUserSegments(user, baseSite);

		//then
		verifySegments(expected, result);
	}

	@Test
	public void testSetUserSegments()
	{
		//given
		final UserModel user = userService.getUserForUID(CUSTOMER_WITH_SEGMENT2_3); //
		final Collection<CxUserToSegmentModel> userSegments = createUserSegments(user, BigDecimal.valueOf(0.7), baseSite, SEGMENT2,
				SEGMENT4);

		//when
		cxUserSegmentService.setUserSegments(user, userSegments);

		//then
		verifySegments(userSegments, user.getUserToSegments());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetUserSegmentsWithOtherUserSegments()
	{
		//given
		final UserModel user1 = userService.getUserForUID(CUSTOMER_WITH_SEGMENT1); //
		final UserModel user2 = userService.getUserForUID(CUSTOMER_WITH_SEGMENT2_3); //

		final Collection<CxUserToSegmentModel> userSegments = createUserSegments(user1, BigDecimal.ONE, baseSite, SEGMENT2);

		//when
		cxUserSegmentService.setUserSegments(user2, userSegments);
	}

	@Test
	public void testSetUserSegmentsForBaseSite()
	{
		//given
		final UserModel user = userService.getUserForUID(CUSTOMER_WITH_SEGMENT2_3); //
		final Collection<CxUserToSegmentModel> userSegments = createUserSegments(user, BigDecimal.valueOf(0.7), baseSite, SEGMENT2,
				SEGMENT4);

		//when
		cxUserSegmentService.setUserSegments(user, baseSite, userSegments);

		//then
		verifySegments(userSegments, user.getUserToSegments());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetUserSegmentsWhenBaseSiteDoesntMatch()
	{
		//given
		final BaseSiteModel baseSite1 = baseSiteService.getBaseSiteForUID(BASE_SITE_1);
		final UserModel user = userService.getUserForUID(CUSTOMER_WITH_SEGMENT2_3); //
		final Collection<CxUserToSegmentModel> userSegments = createUserSegments(user, BigDecimal.valueOf(0.7), baseSite, SEGMENT2,
				SEGMENT4);

		//when
		cxUserSegmentService.setUserSegments(user, baseSite1, userSegments);
	}

	@Test
	public void testSetUserSegmentsForTwoBaseSites()
	{
		//given
		final BaseSiteModel baseSite1 = baseSiteService.getBaseSiteForUID(BASE_SITE_1);
		final UserModel user = userService.getUserForUID(CUSTOMER_WITH_SEGMENT2_3); //
		final Collection<CxUserToSegmentModel> userSegmentsForBaseSite = createUserSegments(user, BigDecimal.valueOf(0.7),
				baseSite, SEGMENT2, SEGMENT4);
		final Collection<CxUserToSegmentModel> userSegmentsForBaseSite1 = createUserSegments(user, BigDecimal.valueOf(0.7),
				baseSite1, SEGMENT1, SEGMENT2);

		//when
		cxUserSegmentService.setUserSegments(user, baseSite, userSegmentsForBaseSite);
		cxUserSegmentService.setUserSegments(user, baseSite1, userSegmentsForBaseSite1);

		//then
		final Collection<CxUserToSegmentModel> resultForBaseSite = user.getUserToSegments().stream()//
				.filter(us -> BASE_SITE.equals(us.getBaseSite().getUid()))//
				.collect(Collectors.toSet());
		verifySegments(userSegmentsForBaseSite, resultForBaseSite);

		final Collection<CxUserToSegmentModel> resultForBaseSite1 = user.getUserToSegments().stream()//
				.filter(us -> BASE_SITE_1.equals(us.getBaseSite().getUid()))//
				.collect(Collectors.toSet());
		verifySegments(userSegmentsForBaseSite1, resultForBaseSite1);
	}

	@Test
	public void testAddUserSegments()
	{
		//given
		final UserModel user = userService.getUserForUID(CUSTOMER_WITH_SEGMENT2_3); //
		final Collection<CxUserToSegmentModel> userSegmentsToAdd = createUserSegments(user, BigDecimal.valueOf(0.7), baseSite,
				SEGMENT2, SEGMENT4);
		final Collection<CxUserToSegmentModel> expectedUserSegments = createUserSegments(user, BigDecimal.ONE, baseSite, SEGMENT3);
		expectedUserSegments.addAll(userSegmentsToAdd);

		//when
		cxUserSegmentService.addUserSegments(user, userSegmentsToAdd);

		//then
		verifySegments(expectedUserSegments, user.getUserToSegments());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddUserSegmentsWithOtherUserSegments()
	{
		//given
		final UserModel user1 = userService.getUserForUID(CUSTOMER_WITH_SEGMENT1); //
		final UserModel user2 = userService.getUserForUID(CUSTOMER_WITH_SEGMENT2_3); //

		final Collection<CxUserToSegmentModel> userSegments = createUserSegments(user1, BigDecimal.ONE, baseSite, SEGMENT4);

		//when
		cxUserSegmentService.addUserSegments(user2, userSegments);
	}

	@Test
	public void testAddDuplicatedUserSegments()
	{
		//given
		final UserModel user = userService.getUserForUID(CUSTOMER_WITH_SEGMENT2_3); //
		final Collection<CxUserToSegmentModel> userSegmentsToAdd = createUserSegments(user, BigDecimal.valueOf(0.7), baseSite,
				SEGMENT4);
		userSegmentsToAdd.addAll(createUserSegments(user, BigDecimal.ONE, baseSite, SEGMENT4));
		final Collection<CxUserToSegmentModel> expectedUserSegments = createUserSegments(user, BigDecimal.ONE, baseSite, SEGMENT2,
				SEGMENT3, SEGMENT4);

		//when
		cxUserSegmentService.addUserSegments(user, userSegmentsToAdd);

		//then
		verifySegments(expectedUserSegments, user.getUserToSegments());
	}

	@Test
	public void testAddUserSegmentsForMultipleBaseSites()
	{
		//given
		final BaseSiteModel baseSite1 = baseSiteService.getBaseSiteForUID(BASE_SITE_1);
		final UserModel user = userService.getUserForUID(CUSTOMER_WITH_SEGMENT1_2_3_FOR_TWO_BASESITES); //
		//baseSite
		final Collection<CxUserToSegmentModel> userSegmentsToAddForBaseSite = createUserSegments(user, BigDecimal.valueOf(0.7),
				baseSite, SEGMENT2, SEGMENT4);
		final Collection<CxUserToSegmentModel> expectedUserSegmentsForBaseSite = createUserSegments(user, BigDecimal.ONE, baseSite,
				SEGMENT1, SEGMENT3);
		expectedUserSegmentsForBaseSite.addAll(userSegmentsToAddForBaseSite);
		//baseSite1
		final Collection<CxUserToSegmentModel> userSegmentsToAddForBaseSite1 = createUserSegments(user, BigDecimal.valueOf(0.7),
				baseSite1, SEGMENT2, SEGMENT4);
		final Collection<CxUserToSegmentModel> expectedUserSegmentsForBaseSite1 = createUserSegments(user, BigDecimal.ONE,
				baseSite1, SEGMENT1, SEGMENT3);
		expectedUserSegmentsForBaseSite1.addAll(userSegmentsToAddForBaseSite1);

		//when
		cxUserSegmentService.addUserSegments(user,
				CollectionUtils.union(userSegmentsToAddForBaseSite, userSegmentsToAddForBaseSite1));

		//then
		final Collection<CxUserToSegmentModel> resultForBaseSite = user.getUserToSegments().stream()//
				.filter(us -> BASE_SITE.equals(us.getBaseSite().getUid()))//
				.collect(Collectors.toSet());
		verifySegments(expectedUserSegmentsForBaseSite, resultForBaseSite);

		final Collection<CxUserToSegmentModel> resultForBaseSite1 = user.getUserToSegments().stream()//
				.filter(us -> BASE_SITE_1.equals(us.getBaseSite().getUid()))//
				.collect(Collectors.toSet());
		verifySegments(expectedUserSegmentsForBaseSite1, resultForBaseSite1);
	}

	@Test
	public void testRemoveUserSegments()
	{
		//given
		final UserModel user = userService.getUserForUID(CUSTOMER_WITH_SEGMENT2_3); //
		final Collection<CxUserToSegmentModel> userSegmentsToRemove = createUserSegments(user, BigDecimal.ONE, baseSite, SEGMENT2,
				SEGMENT4);
		final Collection<CxUserToSegmentModel> expectedUserSegments = createUserSegments(user, BigDecimal.ONE, baseSite, SEGMENT3);

		//when
		cxUserSegmentService.removeUserSegments(user, userSegmentsToRemove);

		//then
		verifySegments(expectedUserSegments, user.getUserToSegments());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRemoveUserSegmentsWithOtherUserSegments()
	{
		//given
		final UserModel user1 = userService.getUserForUID(CUSTOMER_WITH_SEGMENT1); //
		final UserModel user2 = userService.getUserForUID(CUSTOMER_WITH_SEGMENT2_3); //

		final Collection<CxUserToSegmentModel> userSegments = createUserSegments(user1, BigDecimal.ONE, baseSite, SEGMENT3);

		//when
		cxUserSegmentService.removeUserSegments(user2, userSegments);
	}


	@Test
	public void testRemoveUserSegmentsForMultipleBaseSites()
	{
		//given
		final BaseSiteModel baseSite1 = baseSiteService.getBaseSiteForUID(BASE_SITE_1);
		final UserModel user = userService.getUserForUID(CUSTOMER_WITH_SEGMENT1_2_3_FOR_TWO_BASESITES); //
		//baseSite
		final Collection<CxUserToSegmentModel> userSegmentsToRemoveForBaseSite = createUserSegments(user, BigDecimal.ONE, baseSite,
				SEGMENT2, SEGMENT4);
		final Collection<CxUserToSegmentModel> expectedUserSegmentsForBaseSite = createUserSegments(user, BigDecimal.ONE, baseSite,
				SEGMENT1, SEGMENT3);
		//baseSite1
		final Collection<CxUserToSegmentModel> userSegmentsToRemoveForBaseSite1 = createUserSegments(user, BigDecimal.ONE,
				baseSite1, SEGMENT2, SEGMENT4);
		final Collection<CxUserToSegmentModel> expectedUserSegmentsForBaseSite1 = createUserSegments(user, BigDecimal.ONE,
				baseSite1, SEGMENT1, SEGMENT3);


		//when
		cxUserSegmentService.removeUserSegments(user,
				CollectionUtils.union(userSegmentsToRemoveForBaseSite, userSegmentsToRemoveForBaseSite1));

		//then
		final Collection<CxUserToSegmentModel> resultForBaseSite = user.getUserToSegments().stream()//
				.filter(us -> BASE_SITE.equals(us.getBaseSite().getUid()))//
				.collect(Collectors.toSet());
		verifySegments(expectedUserSegmentsForBaseSite, resultForBaseSite);

		final Collection<CxUserToSegmentModel> resultForBaseSite1 = user.getUserToSegments().stream()//
				.filter(us -> BASE_SITE_1.equals(us.getBaseSite().getUid()))//
				.collect(Collectors.toSet());
		verifySegments(expectedUserSegmentsForBaseSite1, resultForBaseSite1);
	}

	protected Collection<CxUserToSegmentModel> createUserSegments(final UserModel user, final BigDecimal affinity,
			final BaseSiteModel baseSite, final String... segments)
	{
		return Arrays.asList(segments).stream().map(s -> createUserSegment(user, affinity, s, baseSite))
				.collect(Collectors.toList());
	}

	protected CxUserToSegmentModel createUserSegment(final UserModel user, final BigDecimal affinity, final String segmentCode,
			final BaseSiteModel baseSite)
	{
		final CxUserToSegmentModel data = new CxUserToSegmentModel();
		data.setUser(user);
		data.setAffinity(affinity);
		final Optional<CxSegmentModel> segmentModel = cxSegmentService.getSegment(segmentCode);
		data.setSegment(segmentModel.orElse(null));
		data.setBaseSite(baseSite);
		return data;
	}

	protected void verifySegments(final Collection<CxUserToSegmentModel> expected, final Collection<CxUserToSegmentModel> current)
	{
		Assert.assertEquals(expected.size(), current.size());

		final Map<String, BigDecimal> expectedAffinityMap = expected.stream().collect(//
				Collectors.toMap(//
						this::getUserSegmentKey, //
						CxUserToSegmentModel::getAffinity));

		Assert.assertTrue(current.stream().allMatch(
				us -> expectedAffinityMap.containsKey(getUserSegmentKey(us))
						&& expectedAffinityMap.get(getUserSegmentKey(us)).compareTo(us.getAffinity()) == 0));
	}

	protected String getUserSegmentKey(final CxUserToSegmentModel us)
	{
		return us.getSegment().getCode() + (us.getBaseSite() == null ? "" : us.getBaseSite().getUid());
	}
}
