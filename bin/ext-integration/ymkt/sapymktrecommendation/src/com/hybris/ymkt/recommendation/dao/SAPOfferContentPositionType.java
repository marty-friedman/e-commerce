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
package com.hybris.ymkt.recommendation.dao;

/**
 *
 */
public class SAPOfferContentPositionType
{
	protected final String contentPositionId;
	protected String communicationMediumId;
	protected String communicationMediumName;

	public SAPOfferContentPositionType(final String contentPositionId)
	{
		this.contentPositionId = contentPositionId;
	}

	/**
	 * @return the contentPositionId
	 */
	public String getContentPositionId()
	{
		return contentPositionId;
	}

	/**
	 * @return the communicationMediumId
	 */
	public String getCommunicationMediumId()
	{
		return communicationMediumId;
	}

	/**
	 * @return the communicationMediumName
	 */
	public String getCommunicationMediumName()
	{
		return communicationMediumName;
	}

	/**
	 * @param communicationMediumId
	 *           the communicationMediumId to set
	 */
	public void setCommunicationMediumId(final String communicationMediumId)
	{
		this.communicationMediumId = communicationMediumId;
	}

	/**
	 * @param communicationMediumName
	 *           the communicationMediumName to set
	 */
	public void setCommunicationMediumName(final String communicationMediumName)
	{
		this.communicationMediumName = communicationMediumName;
	}
}
