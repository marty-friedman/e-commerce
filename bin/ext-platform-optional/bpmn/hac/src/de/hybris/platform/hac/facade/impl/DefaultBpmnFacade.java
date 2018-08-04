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
package de.hybris.platform.hac.facade.impl;

import de.hybris.platform.hac.facade.BpmnFacade;
import de.hybris.platform.processengine.transformer.bpmnhybris.data.BpmnProcessData;
import de.hybris.platform.processengine.transformer.bpmnhybris.service.BpmnService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class DefaultBpmnFacade implements BpmnFacade
{

	@Autowired
	private BpmnService bpmnService;

	@Override
	public BpmnProcessData convertBpmnToProcess(final String bpmnXml)
	{
		return bpmnService.convertBpmnToProcess(bpmnXml);
	}

	@Override
	public BpmnProcessData convertProcessToBpmn(final String processXml)
	{
		return bpmnService.convertProcessToBpmn(processXml);
	}

	@Override
	public void saveBpmnProcessData(final BpmnProcessData bpmnProcessData)
	{
		bpmnService.saveBpmnProcessData(bpmnProcessData);
	}

	@Override
	public BpmnProcessData getBpmnProcessData(final String processCode)
	{
		try
		{
			return bpmnService.getBpmnProcessDataBy(processCode);
		}
		catch (final UnknownIdentifierException | AmbiguousIdentifierException ex)
		{
			return null;
		}
	}
}
