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
package de.hybris.platform.hac.facade;

import de.hybris.platform.processengine.transformer.bpmnhybris.data.BpmnProcessData;


public interface BpmnFacade
{
	public BpmnProcessData convertBpmnToProcess(final String bpmnXml);

	public BpmnProcessData convertProcessToBpmn(final String processXml);

	public void saveBpmnProcessData(BpmnProcessData bpmnProcessData);

	public BpmnProcessData getBpmnProcessData(String processCode);

}
