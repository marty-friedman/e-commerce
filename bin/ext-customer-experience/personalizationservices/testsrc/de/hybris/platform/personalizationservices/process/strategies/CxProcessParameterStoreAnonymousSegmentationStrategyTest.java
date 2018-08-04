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
package de.hybris.platform.personalizationservices.process.strategies;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.personalizationservices.constants.PersonalizationservicesConstants;
import de.hybris.platform.personalizationservices.model.CxSegmentModel;
import de.hybris.platform.personalizationservices.model.CxUserToSegmentModel;
import de.hybris.platform.personalizationservices.model.process.CxPersonalizationProcessModel;
import de.hybris.platform.personalizationservices.process.data.CxAnonymousToSegment;
import de.hybris.platform.personalizationservices.process.strategies.impl.CxProcessParameterAnonymousSegmentationStrategy;
import de.hybris.platform.personalizationservices.segment.CxSegmentService;
import de.hybris.platform.processengine.model.BusinessProcessParameterModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class CxProcessParameterStoreAnonymousSegmentationStrategyTest extends BaseCxProcessParameterStrategyTest
{
	private final CxProcessParameterAnonymousSegmentationStrategy strategy = new CxProcessParameterAnonymousSegmentationStrategy();

	@Mock
	protected UserService userService;
	@Mock
	protected CxSegmentService cxSegmentService;
	@Mock
	protected Converter<CxUserToSegmentModel, CxAnonymousToSegment> anonymousSegmentForSessionConverter;
	@Mock
	protected Converter<CxAnonymousToSegment, CxUserToSegmentModel> anonymousSegmentForSessionReverseConverter;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		strategy.setUserService(userService);
		strategy.setCxSegmentService(cxSegmentService);
		strategy.setAnonymousSegmentForSessionConverter(anonymousSegmentForSessionConverter);
		strategy.setAnonymousSegmentForSessionReverseConverter(anonymousSegmentForSessionReverseConverter);
		strategy.setProcessParameterHelper(processParameterHelper);

	}

	@Test
	public void shouldStoreAnonymousSegmentationInProcess()
	{
		//given
		final UserModel user = new UserModel();
		final List<CxSegmentModel> segments = createSegments();
		setUserToSegments(user, segments);
		final CxPersonalizationProcessModel process = new CxPersonalizationProcessModel();
		process.setUser(user);
		final List<CxAnonymousToSegment> anonymousToSegments = createAnonymousToSegments(segments);

		given(Boolean.valueOf(userService.isAnonymousUser(user))).willReturn(Boolean.TRUE);
		given(cxSegmentService.getUserToSegmentForCalculation(user)).willReturn(user.getUserToSegments());
		given(anonymousSegmentForSessionConverter.convertAll(user.getUserToSegments())).willReturn(anonymousToSegments);

		//when
		strategy.store(process);

		//then
		verify(processParameterHelper).setProcessParameter(process,
				PersonalizationservicesConstants.USER_TO_SEGMENTS_PROCESS_PARAMETER, anonymousToSegments);
	}

	@Test
	public void shouldNotStoreNonAnonymousSegmentationInProcess()
	{
		//given
		final UserModel user = new UserModel();

		final CxPersonalizationProcessModel process = new CxPersonalizationProcessModel();
		process.setUser(user);

		given(Boolean.valueOf(userService.isAnonymousUser(user))).willReturn(Boolean.FALSE);

		//when
		strategy.store(process);

		//then
		verifyZeroInteractions(processParameterHelper);
	}



	@Test
	public void shouldLoadAnonymousSegmentFromProcess()
	{
		//given
		final UserModel user = new UserModel();
		final List<CxSegmentModel> segments = createSegments();
		setUserToSegments(user, segments);
		final CxPersonalizationProcessModel process = new CxPersonalizationProcessModel();
		process.setUser(user);
		final List<CxAnonymousToSegment> anonymousToSegments = createAnonymousToSegments(segments);
		final Collection<CxUserToSegmentModel> userToSegments = user.getUserToSegments();

		final BusinessProcessParameterModel processParameter = createBusinessProcessParameterModel(
				PersonalizationservicesConstants.USER_TO_SEGMENTS_PROCESS_PARAMETER, anonymousToSegments);

		given(
				Boolean.valueOf(processParameterHelper.containsParameter(process,
						PersonalizationservicesConstants.USER_TO_SEGMENTS_PROCESS_PARAMETER))).willReturn(Boolean.TRUE);
		given(
				processParameterHelper.getProcessParameterByName(process,
						PersonalizationservicesConstants.USER_TO_SEGMENTS_PROCESS_PARAMETER)).willReturn(processParameter);
		given((Collection) anonymousSegmentForSessionReverseConverter.convertAll(anonymousToSegments)).willReturn(userToSegments);
		//when
		strategy.load(process);

		//then
		verify(cxSegmentService).saveUserToSegments(userToSegments);

	}
}
