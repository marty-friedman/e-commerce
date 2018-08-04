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
package de.hybris.platform.btg.servicelayer.services.evaluator.impl;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;

import de.hybris.platform.btg.consent.BtgConsentService;
import de.hybris.platform.btg.model.BTGSegmentModel;
import de.hybris.platform.btg.model.BTGSegmentResultModel;
import de.hybris.platform.btg.model.CmsRestrictionActionDefinitionModel;
import de.hybris.platform.btg.model.restrictions.BtgSegmentRestrictionModel;
import de.hybris.platform.btg.services.BTGResultService;
import de.hybris.platform.btg.services.impl.BTGEvaluationContext;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class BtgSegmentRestrictionEvaluatorTest
{
	private BTGResultService mockBtgResultService;
	private UserService mockUserService;
	private BTGSegmentResultModel mockBtgSegmentResult;
	private BtgSegmentRestrictionModel mockRestriction;
	private CmsRestrictionActionDefinitionModel mockOutputDefinition;
	private BtgSegmentRestrictionEvaluator evaluator;
	private ModelService modelServiceMock;
	private BTGSegmentModel segmentModelMock;
	private BtgConsentService btgConsentService;

	@Test
	public void shouldEvaluateToTrueIfBtgServiceReturnsTrueAndActionDefinitionIsNotInverted()
	{
		//given
		expect(mockBtgResultService.checkSegmentForUser(null, segmentModelMock, null)).andReturn(mockBtgSegmentResult);
		expect(Boolean.valueOf(mockBtgSegmentResult.isFulfilled())).andReturn(Boolean.TRUE);
		expect(mockOutputDefinition.getInverted()).andReturn(Boolean.FALSE);
		expect(segmentModelMock.getActive()).andReturn(Boolean.TRUE);
		replay(mockBtgSegmentResult, mockOutputDefinition, segmentModelMock, mockBtgResultService);

		//when
		final boolean evaluationResult = evaluator.evaluate(mockRestriction, null);

		//then
		verify(mockBtgSegmentResult, mockOutputDefinition, segmentModelMock, mockBtgResultService);
		assertTrue(evaluationResult);
	}


	@Test
	public void shouldEvaluateToFalseIfBtgServiceReturnsTrueAndActionDefinitionIsInverted()
	{
		//given
		expect(mockBtgResultService.checkSegmentForUser(null, segmentModelMock, null)).andReturn(mockBtgSegmentResult);
		expect(Boolean.valueOf(mockBtgSegmentResult.isFulfilled())).andReturn(Boolean.TRUE);
		expect(mockOutputDefinition.getInverted()).andReturn(Boolean.TRUE);
		expect(segmentModelMock.getActive()).andReturn(Boolean.TRUE);
		replay(mockBtgSegmentResult, mockOutputDefinition, segmentModelMock, mockBtgResultService);

		//when
		final boolean evaluationResult = evaluator.evaluate(mockRestriction, null);

		//then
		verify(mockBtgSegmentResult, mockOutputDefinition, segmentModelMock, mockBtgResultService);
		assertFalse(evaluationResult);
	}

	@Test
	public void shouldEvaluateToFalseIfBtgServiceReturnsFalseAndActionDefinitionIsNotInverted()
	{
		//given
		expect(mockBtgResultService.checkSegmentForUser(null, segmentModelMock, null)).andReturn(mockBtgSegmentResult);
		expect(Boolean.valueOf(mockBtgSegmentResult.isFulfilled())).andReturn(Boolean.FALSE);
		expect(mockOutputDefinition.getInverted()).andReturn(Boolean.FALSE);
		expect(segmentModelMock.getActive()).andReturn(Boolean.TRUE);
		replay(mockBtgSegmentResult, mockOutputDefinition, segmentModelMock, mockBtgResultService);

		//when
		final boolean evaluationResult = evaluator.evaluate(mockRestriction, null);

		//then
		verify(mockBtgSegmentResult, mockOutputDefinition, segmentModelMock, mockBtgResultService);
		assertFalse(evaluationResult);
	}

	@Test
	public void shouldEvaluateToTrueIfBtgServiceReturnsFalseAndActionDefinitionIsInverted()
	{
		//given
		expect(mockBtgResultService.checkSegmentForUser(null, segmentModelMock, null)).andReturn(mockBtgSegmentResult);
		expect(Boolean.valueOf(mockBtgSegmentResult.isFulfilled())).andReturn(Boolean.FALSE);
		expect(mockOutputDefinition.getInverted()).andReturn(Boolean.TRUE);
		expect(segmentModelMock.getActive()).andReturn(Boolean.TRUE);
		replay(mockBtgSegmentResult, mockOutputDefinition, segmentModelMock, mockBtgResultService);

		//when
		final boolean evaluationResult = evaluator.evaluate(mockRestriction, null);

		//then
		verify(mockBtgSegmentResult, mockOutputDefinition, segmentModelMock, mockBtgResultService);
		assertTrue(evaluationResult);
	}

	@Test
	public void shouldEvaluateToTrueIfRuleIsInactive()
	{
		//given
		expect(segmentModelMock.getActive()).andReturn(Boolean.FALSE);
		replay(mockBtgSegmentResult, mockOutputDefinition, segmentModelMock);

		//when
		final boolean evaluationResult = evaluator.evaluate(mockRestriction, null);

		//then
		verify(segmentModelMock);
		assertTrue(evaluationResult);
	}

	@After
	public void verifyMocks()
	{
		verify(mockRestriction, mockUserService, modelServiceMock);
	}

	@Before
	public void prepareInstance()
	{
		evaluator = new BtgSegmentRestrictionEvaluator()
		{
			@Override
			protected BTGEvaluationContext getEvaluationContext()
			{
				return null;
			}
		};

		segmentModelMock = createMock(BTGSegmentModel.class);
		modelServiceMock = createMock(ModelService.class);
		mockBtgSegmentResult = createMock(BTGSegmentResultModel.class);
		mockUserService = createMock(UserService.class);
		mockBtgResultService = createMock(BTGResultService.class);
		mockRestriction = createMock(BtgSegmentRestrictionModel.class);
		mockOutputDefinition = createMock(CmsRestrictionActionDefinitionModel.class);
		btgConsentService = createMock(BtgConsentService.class);

		expect(mockUserService.getCurrentUser()).andReturn(null);
		expect(mockRestriction.getOutputActionDefinition()).andReturn(mockOutputDefinition);
		expect(mockOutputDefinition.getSegment()).andReturn(segmentModelMock);
		expect(Boolean.valueOf(modelServiceMock.isNew(segmentModelMock))).andReturn(Boolean.FALSE);
		expect(btgConsentService.userHasActiveConsent(any())).andReturn(Boolean.TRUE);



		modelServiceMock.refresh(segmentModelMock);
		evaluator.setModelService(modelServiceMock);
		evaluator.setBtgResultService(mockBtgResultService);
		evaluator.setUserService(mockUserService);
		evaluator.setBtgConsentService(btgConsentService);

		replay(modelServiceMock, mockRestriction, mockUserService, btgConsentService);
	}

}
