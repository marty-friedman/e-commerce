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
package de.hybris.platform.personalizationintegration.service.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.personalizationintegration.mapping.MappingData;
import de.hybris.platform.personalizationintegration.mapping.SegmentMappingData;
import de.hybris.platform.personalizationservices.data.UserToSegmentData;
import de.hybris.platform.personalizationservices.model.CxUserToSegmentModel;
import de.hybris.platform.personalizationservices.segment.CxSegmentService;
import de.hybris.platform.personalizationservices.segment.CxUserSegmentSessionService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.impex.impl.ClasspathImpExResource;
import de.hybris.platform.servicelayer.user.UserService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultCxIntegrationMappingServiceIntegrationTest extends ServicelayerTest
{
	private static final String SEGMENT1 = "segment1";
	private static final String SEGMENT2 = "segment2";
	private static final String SEGMENT3 = "segment3";
	private static final String SEGMENT4 = "segment4";
	private static final String NEW_SEGMENT = "newSegment";
	private static final String CUSTOMER_WITH_SEGMENT1 = "customer1@hybris.com";
	private static final String CUSTOMER_WITH_SEGMENT1_2_3 = "customer2@hybris.com";

	@Resource
	private DefaultCxIntegrationMappingService cxIntegrationMappingService;

	@Resource
	private UserService userService;
	@Resource
	private CxUserSegmentSessionService cxUserSegmentSessionService;
	@Resource
	private CxSegmentService cxSegmentService;


	@Before
	public void setupSampleData() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		importData(new ClasspathImpExResource("/personalizationintegration/test/testdata_personalizationintegration.impex", "UTF-8"));
	}

	@Test
	public void testAssignSegmentsToUser()
	{
		//given
		final UserModel user = userService.getUserForUID(CUSTOMER_WITH_SEGMENT1_2_3); //
		final BigDecimal affinity = BigDecimal.valueOf(0.6);
		final MappingData mappingData = createMappingData(affinity, SEGMENT2, SEGMENT3, SEGMENT4);

		//when
		cxIntegrationMappingService.assignSegmentsToUser(user, mappingData, false);

		//then
		verifySegments(mappingData.getSegments(), user.getUserToSegments());
		verifyThatThereIsNoDataInSession(user);
	}

	@Test
	public void testAssignSegmentsToUserWhenNullMapingData()
	{
		//given
		final UserModel user = userService.getUserForUID(CUSTOMER_WITH_SEGMENT1_2_3); //
		final List<SegmentMappingData> oldSegments = createUserSegmentList(BigDecimal.ONE, SEGMENT1, SEGMENT2, SEGMENT3);
		verifySegments(oldSegments, user.getUserToSegments());

		//when
		cxIntegrationMappingService.assignSegmentsToUser(user, null, false);

		//then
		verifySegments(oldSegments, user.getUserToSegments());
	}

	@Test
	public void testAssignSegmentsToUserWhenEmptySegmentList()
	{
		//given
		final UserModel user = userService.getUserForUID(CUSTOMER_WITH_SEGMENT1_2_3);
		final MappingData mappingData = new MappingData();
		mappingData.setSegments(Collections.emptyList());
		final List<SegmentMappingData> segments = createUserSegmentList(BigDecimal.ONE, SEGMENT1, SEGMENT2, SEGMENT3);
		verifySegments(segments, user.getUserToSegments());

		//when
		cxIntegrationMappingService.assignSegmentsToUser(user, mappingData, false);

		//then
		Assert.assertTrue(CollectionUtils.isEmpty(user.getUserToSegments()));
		verifyThatThereIsNoDataInSession(user);
	}

	@Test
	public void testAssignSegmentsToUserWhenNullSegmentList()
	{
		//given
		final UserModel user = userService.getUserForUID(CUSTOMER_WITH_SEGMENT1_2_3); //
		final MappingData mappingData = new MappingData();
		final List<SegmentMappingData> oldSegments = createUserSegmentList(BigDecimal.ONE, SEGMENT1, SEGMENT2, SEGMENT3);
		verifySegments(oldSegments, user.getUserToSegments());

		//when
		cxIntegrationMappingService.assignSegmentsToUser(user, mappingData, false);

		//then
		verifySegments(oldSegments, user.getUserToSegments());
	}


	@Test
	public void testAssignSegmentsToUserForNotExistingSegment()
	{
		//given
		final UserModel user = userService.getUserForUID(CUSTOMER_WITH_SEGMENT1); //
		final MappingData mappingData = createMappingData(BigDecimal.ONE, NEW_SEGMENT);
		final List<SegmentMappingData> oldSegments = createUserSegmentList(BigDecimal.ONE, SEGMENT1);
		verifySegments(oldSegments, user.getUserToSegments());
		Assert.assertFalse(cxSegmentService.getSegment(NEW_SEGMENT).isPresent());

		//when
		cxIntegrationMappingService.assignSegmentsToUser(user, mappingData, false);

		//then
		Assert.assertTrue(CollectionUtils.isEmpty(user.getUserToSegments()));
		Assert.assertFalse(cxSegmentService.getSegment(NEW_SEGMENT).isPresent());
	}

	@Test
	public void testAssignSegmentsToUserForNewSegment()
	{
		//given
		final UserModel user = userService.getUserForUID(CUSTOMER_WITH_SEGMENT1); //
		final MappingData mappingData = createMappingData(BigDecimal.ONE, NEW_SEGMENT);
		final List<SegmentMappingData> oldSegments = createUserSegmentList(BigDecimal.ONE, SEGMENT1);
		verifySegments(oldSegments, user.getUserToSegments());
		Assert.assertFalse(cxSegmentService.getSegment(NEW_SEGMENT).isPresent());

		//when
		cxIntegrationMappingService.assignSegmentsToUser(user, mappingData, true);

		//then
		verifySegments(mappingData.getSegments(), user.getUserToSegments());
		Assert.assertTrue(cxSegmentService.getSegment(NEW_SEGMENT).isPresent());
	}

	@Test
	public void testAssignSegmentsToUserWhenAffinityIsNull()
	{
		//given
		final UserModel user = userService.getUserForUID(CUSTOMER_WITH_SEGMENT1); //
		final MappingData mappingData = createMappingData(null, SEGMENT2);
		final List<SegmentMappingData> expectedSegments = createUserSegmentList(
				DefaultCxIntegrationMappingService.DEFAULT_AFFINITY, SEGMENT2);

		//when
		cxIntegrationMappingService.assignSegmentsToUser(user, mappingData, true);

		//then
		verifySegments(expectedSegments, user.getUserToSegments());
	}

	@Test
	public void testAssignSegmentsToUserWhenDuplicatedSegments()
	{
		//given
		final UserModel user = userService.getUserForUID(CUSTOMER_WITH_SEGMENT1); //
		final BigDecimal affinity = BigDecimal.valueOf(0.5);
		final BigDecimal biggerAffinity = BigDecimal.ONE;
		final MappingData mappingData = createMappingData(affinity, SEGMENT2);
		mappingData.getSegments().add(createSegmentMappingData(biggerAffinity, SEGMENT2));

		final List<SegmentMappingData> expectedSegments = createUserSegmentList(biggerAffinity, SEGMENT2);

		//when
		cxIntegrationMappingService.assignSegmentsToUser(user, mappingData, true);

		//then
		verifySegments(expectedSegments, user.getUserToSegments());
	}

	@Test
	public void testAssignSegmentsToUserWhenNewDuplicatedSegments()
	{
		//given
		final UserModel user = userService.getUserForUID(CUSTOMER_WITH_SEGMENT1); //
		final BigDecimal affinity = BigDecimal.valueOf(0.5);
		final BigDecimal biggerAffinity = BigDecimal.ONE;
		final MappingData mappingData = createMappingData(affinity, NEW_SEGMENT);
		mappingData.getSegments().add(createSegmentMappingData(biggerAffinity, NEW_SEGMENT));

		final List<SegmentMappingData> expectedSegments = createUserSegmentList(biggerAffinity, NEW_SEGMENT);

		//when
		cxIntegrationMappingService.assignSegmentsToUser(user, mappingData, true);

		//then
		verifySegments(expectedSegments, user.getUserToSegments());
	}

	@Test
	public void testAssignAndGetUserSegment()
	{
		//given
		final UserModel user = userService.getUserForUID(CUSTOMER_WITH_SEGMENT1); //
		final MappingData mappingData = createMappingData(BigDecimal.ONE, SEGMENT1, SEGMENT2, SEGMENT3);

		//when
		cxIntegrationMappingService.assignSegmentsToUser(user, mappingData, false);
		final Collection<CxUserToSegmentModel> result = cxSegmentService.getUserToSegmentForUser(user);

		//then
		verifySegments(mappingData.getSegments(), result);
	}

	@Test
	public void testAssignSegmentsToUserForAnonymous()
	{
		//given
		final UserModel user = userService.getAnonymousUser();
		final BigDecimal affinity = BigDecimal.valueOf(0.6);
		final MappingData mappingData = createMappingData(affinity, SEGMENT1, SEGMENT2, SEGMENT3);

		//when
		cxIntegrationMappingService.assignSegmentsToUser(user, mappingData, false);

		//then
		Assert.assertTrue(CollectionUtils.isEmpty(user.getUserToSegments()));
		verifySessionData(user, mappingData.getSegments());
	}

	@Test
	public void testOverrideUserToSegmentForAnonymous()
	{
		//given
		final UserModel user = userService.getAnonymousUser();
		final BigDecimal oldAffinity = BigDecimal.valueOf(0.6);
		final List<SegmentMappingData> oldSegments = createUserSegmentList(oldAffinity, SEGMENT1, SEGMENT2);
		cxUserSegmentSessionService.setUserSegmentsInSession(user, oldSegments);
		final BigDecimal newAffinity = BigDecimal.valueOf(0.8);
		final MappingData mappingData = createMappingData(newAffinity, SEGMENT1, SEGMENT3);

		//when
		cxIntegrationMappingService.assignSegmentsToUser(user, mappingData, false);

		//then
		Assert.assertTrue(CollectionUtils.isEmpty(user.getUserToSegments()));
		verifySessionData(user, mappingData.getSegments());
	}

	@Test
	public void testRemoveUserToSegmentForAnonymous()
	{
		//given
		final UserModel user = userService.getAnonymousUser();
		final MappingData mappingData = new MappingData();
		mappingData.setSegments(Collections.emptyList());
		final List<SegmentMappingData> segments = createUserSegmentList(BigDecimal.ONE, SEGMENT1, SEGMENT2);
		cxUserSegmentSessionService.setUserSegmentsInSession(user, segments);

		//when
		cxIntegrationMappingService.assignSegmentsToUser(user, mappingData, false);

		//then
		Assert.assertTrue(CollectionUtils.isEmpty(user.getUserToSegments()));
		verifyThatThereIsNoDataInSession(user);
	}

	@Test
	public void testAssignAndGetUserToSegmentForAnonymous()
	{
		//given
		final UserModel user = userService.getAnonymousUser();
		final MappingData mappingData = createMappingData(BigDecimal.ONE, SEGMENT1, SEGMENT2);

		//when
		cxIntegrationMappingService.assignSegmentsToUser(user, mappingData, false);
		final Collection<CxUserToSegmentModel> result = cxSegmentService.getUserToSegmentForUser(user);

		//then
		verifySegments(mappingData.getSegments(), result);
	}

	protected MappingData createMappingData(final BigDecimal affinity, final String... segments)
	{
		final MappingData result = new MappingData();
		final List<SegmentMappingData> userSegments = Arrays.asList(segments).stream()//
				.map(s -> createSegmentMappingData(affinity, s))//
				.collect(Collectors.toList());
		result.setSegments(createUserSegmentList(affinity, segments));
		return result;
	}

	protected List<SegmentMappingData> createUserSegmentList(final BigDecimal affinity, final String... segments)
	{
		return Arrays.asList(segments).stream().map(s -> createSegmentMappingData(affinity, s)).collect(Collectors.toList());
	}

	protected SegmentMappingData createSegmentMappingData(final BigDecimal affinity, final String code)
	{
		final SegmentMappingData data = new SegmentMappingData();
		data.setCode(code);
		data.setAffinity(affinity);
		return data;
	}

	protected void verifySegments(final Collection<? extends UserToSegmentData> expected,
			final Collection<CxUserToSegmentModel> current)
	{
		Assert.assertEquals(expected.size(), current.size());

		final Map<String, BigDecimal> expectedDataMap = expected.stream().collect(//
				Collectors.toMap(//
						UserToSegmentData::getCode,//
						UserToSegmentData::getAffinity));

		Assert.assertTrue(
				"UserToSegment are not equal",
				current.stream().allMatch(
						us -> expectedDataMap.containsKey(us.getSegment().getCode())
								&& expectedDataMap.get(us.getSegment().getCode()).compareTo(us.getAffinity()) == 0));
	}

	protected void verifyThatThereIsNoDataInSession(final UserModel user)
	{
		Assert.assertTrue(CollectionUtils.isEmpty(cxUserSegmentSessionService.getUserSegmentsFromSession(user)));
	}

	protected void verifySessionData(final UserModel user, final Collection<? extends UserToSegmentData> expected)
	{
		final Collection<UserToSegmentData> current = cxUserSegmentSessionService.getUserSegmentsFromSession(user);
		Assert.assertEquals(expected.size(), current.size());

		final Map<String, BigDecimal> expectedDataMap = expected.stream().collect(//
				Collectors.toMap(//
						UserToSegmentData::getCode,//
						UserToSegmentData::getAffinity));

		Assert.assertTrue(current.stream().allMatch(
				usd -> expectedDataMap.containsKey(usd.getCode()) && expectedDataMap.get(usd.getCode()).equals(usd.getAffinity())));
	}
}
