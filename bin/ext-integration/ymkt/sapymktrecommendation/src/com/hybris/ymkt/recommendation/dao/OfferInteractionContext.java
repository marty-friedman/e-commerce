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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * This class is used to represent the payload that will be sent to ymkt
 */
public class OfferInteractionContext
{

	public static class Offer
	{
		private String id;
		private String contentItemId;
		private String recommendationScenarioId;

		/**
		 * @return the id
		 */
		public String getId()
		{
			return id;
		}

		/**
		 * @param id
		 *           the id to set
		 */
		public void setId(String id)
		{
			this.id = id;
		}

		/**
		 * @return the contentItemId
		 */
		public String getContentItemId()
		{
			return contentItemId;
		}

		/**
		 * @param contentItemId
		 *           the contentItemId to set
		 */
		public void setContentItemId(String contentItemId)
		{
			this.contentItemId = contentItemId;
		}

		/**
		 * @return the recommendationScenarioId
		 */
		public String getRecommendationScenarioId()
		{
			return recommendationScenarioId;
		}

		/**
		 * @param recommendationScenarioId
		 *           the recommendationScenarioId to set
		 */
		public void setRecommendationScenarioId(String recommendationScenarioId)
		{
			this.recommendationScenarioId = recommendationScenarioId;
		}

	}

	public static class Interaction
	{

		private String key;
		private String communicationMedium;
		private String interactionType;
		private boolean isAnonymous;
		private String contactId;
		private String contactIdOrigin;
		private Date timeStamp;
		protected final List<Offer> offers = new ArrayList<>();

		/**
		 * @return the key
		 */
		public String getKey()
		{
			return key;
		}

		/**
		 * @param key
		 *           the key to set
		 */
		public void setKey(String key)
		{
			this.key = key;
		}

		/**
		 * @return the offers
		 */
		public List<Offer> getOffers()
		{
			return offers;
		}

		/**
		 * @return the communicationMedium
		 */
		public String getCommunicationMedium()
		{
			return communicationMedium;
		}

		/**
		 * @param communicationMedium
		 *           the communicationMedium to set
		 */
		public void setCommunicationMedium(String communicationMedium)
		{
			this.communicationMedium = communicationMedium;
		}

		/**
		 * @return the interactionType
		 */
		public String getInteractionType()
		{
			return interactionType;
		}

		/**
		 * @param interactionType
		 *           the interactionType to set
		 */
		public void setInteractionType(String interactionType)
		{
			this.interactionType = interactionType;
		}

		/**
		 * @return the isAnonymous
		 */
		public boolean isAnonymous()
		{
			return isAnonymous;
		}

		/**
		 * @param isAnonymous
		 *           the isAnonymous to set
		 */
		public void setAnonymous(boolean isAnonymous)
		{
			this.isAnonymous = isAnonymous;
		}

		/**
		 * @return the contactId
		 */
		public String getContactId()
		{
			return contactId;
		}

		/**
		 * @param contactId
		 *           the contactId to set
		 */
		public void setContactId(String contactId)
		{
			this.contactId = contactId;
		}

		/**
		 * @return the contactIdOrigin
		 */
		public String getContactIdOrigin()
		{
			return contactIdOrigin;
		}

		/**
		 * @param contactIdOrigin
		 *           the contactIdOrigin to set
		 */
		public void setContactIdOrigin(String contactIdOrigin)
		{
			this.contactIdOrigin = contactIdOrigin;
		}

		public Date getTimeStamp()
		{
			return timeStamp;
		}

		public void setTimeStamp(Date timeStamp)
		{
			this.timeStamp = timeStamp;
		}

	}

	private Date timestamp;
	protected final List<Interaction> interactions = new ArrayList<>();
	protected final List<Offer> offers = new ArrayList<>();

	/**
	 * @return the timestamp
	 */
	public Date getTimestamp()
	{
		return timestamp;
	}

	/**
	 * @param timestamp
	 *           the timestamp to set
	 */
	public void setTimestamp(Date timestamp)
	{
		this.timestamp = timestamp;
	}

	public List<Interaction> getInteractions()
	{
		return interactions;
	}
}
