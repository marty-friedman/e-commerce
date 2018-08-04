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
package com.sap.hybris.sec.eventpublisher.handler.impl;

import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.tx.AfterSaveEvent;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.UUID;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.sec.eventpublisher.constants.EventpublisherConstants;
import com.sap.hybris.sec.eventpublisher.data.ResponseData;
import com.sap.hybris.sec.eventpublisher.dto.order.OrderIndex;
import com.sap.hybris.sec.eventpublisher.dto.order.OrderWS;
import com.sap.hybris.sec.eventpublisher.handler.AfterSaveEventHandler;
import com.sap.hybris.sec.eventpublisher.publisher.Publisher;


/**
 * Publish a websocket event and replicate the updated/created order to target
 */
@ClientEndpoint
public class AfterOrderSaveEventHandler implements AfterSaveEventHandler
{
	private static final Logger LOGGER = LogManager.getLogger(AfterOrderSaveEventHandler.class);

	private String randomCode;
	private Session session;
	private ModelService modelService;
	private ConfigurationService configurationService;
	private Publisher publisher;
	private Populator<OrderModel, OrderIndex> orderPopulator;
	private Populator<OrderModel, OrderWS> orderWSPopulator;

	@Override
	public void handleEvent(final AfterSaveEvent event)
	{
		final PK pk = event.getPk();
		if ((event.getType() == AfterSaveEvent.CREATE || event.getType() == AfterSaveEvent.UPDATE)
				&& (modelService.get(pk) instanceof OrderModel))
		{
			final OrderModel orderModel = (OrderModel) modelService.get(pk);
			try
			{
				if (isWebSocketReplyEligible(event))
				{
					sendOrderCreateStatusToWebSocket(orderModel);
				}
				final CustomerModel customerModel = getCustomerModel(orderModel.getUser());
				if (customerModel != null && !CustomerType.GUEST.equals(customerModel.getType()))
				{
					createOrUpdateOrderIndex(orderModel, event.getType());
				}

			}
			catch (DeploymentException | IOException | URISyntaxException e)
			{
				LOGGER.error("Failed to publish Order event", e);
			}
		}
	}

	/**
	 * @param resData
	 * @return boolean
	 */
	protected boolean isWebSocketReplyEligible(final AfterSaveEvent event)
	{
		return (event.getType() == AfterSaveEvent.CREATE) || (event.getType() == AfterSaveEvent.UPDATE);

	}

	public String getCode()
	{
		final String copy = randomCode;
		randomCode = null;
		return copy;
	}

	protected void sendOrderCreateStatusToWebSocket(final OrderModel orderModel) throws DeploymentException, IOException
	{
		if (session == null || !session.isOpen())
		{
			final WebSocketContainer container = ContainerProvider.getWebSocketContainer();
			final String webSocketServerEndpoint = getServerEndpoint();
			session = container.connectToServer(AfterOrderSaveEventHandler.class, URI.create(webSocketServerEndpoint));
		}

		final OrderWS orderWSJson = new OrderWS();
		getOrderWSPopulator().populate(orderModel, orderWSJson);
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug("Sending from local to  ", session.getBasicRemote());
		}

		session.getBasicRemote().sendText(orderWSJson.toString());

	}

	private String getRandomCode()
	{
		if (randomCode == null)
		{
			randomCode = UUID.randomUUID() + Integer.toString(new Random().hashCode());
		}
		return randomCode;
	}

	/**
	 * @param orderModel
	 */
	private ResponseData createOrUpdateOrderIndex(final OrderModel orderModel, final int eventType)
			throws URISyntaxException, IOException
	{
		final OrderIndex orderJson = new OrderIndex();
		if (eventType == AfterSaveEvent.CREATE)
		{
			orderJson.setEventStatus(EventpublisherConstants.ORDER_CREATED);
		}
		else
		{
			orderJson.setEventStatus(EventpublisherConstants.ORDER_UPDATED);
		}
		getOrderPopulator().populate(orderModel, orderJson);
		return getPublisher().createOrUpdateOrder(orderJson.toString());


	}

	private String getServerEndpoint()
	{
		String webSocketServerEndpointbaseURL = null;
		final String baseWebSocketURL = EventpublisherConstants.WEBSOCKET_SERVER_ENDPOINT_BASE_URL;
		if (!StringUtils.isEmpty(baseWebSocketURL))
		{
			webSocketServerEndpointbaseURL = baseWebSocketURL
					.replace("{" + EventpublisherConstants.PORT_CONSTANT + "}",
							configurationService.getConfiguration().getString(EventpublisherConstants.WEBSOCKET_SERVER_ENDPOINT_PORT))
					.concat("/" + EventpublisherConstants.WEBSOCKET_SERVER_ENDPOINT_INTERNALCONTEXT
							+ EventpublisherConstants.WEBSOCKET_SERVER_ENDPOINT_PATH)
					.concat("?" + EventpublisherConstants.CODE_CONSTANT + "=" + getRandomCode());
		}
		return webSocketServerEndpointbaseURL;
	}

	private CustomerModel getCustomerModel(final UserModel user)
	{
		CustomerModel customer = null;
		if (user instanceof CustomerModel)
		{
			customer = (CustomerModel) user;
		}
		return customer;
	}

	/**
	 * @return the orderPopulator
	 */
	public Populator<OrderModel, OrderIndex> getOrderPopulator()
	{
		return orderPopulator;
	}

	/**
	 * @param configurationService
	 *           the configurationService to set
	 */
	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @param orderPopulator
	 *           the orderPopulator to set
	 */
	public void setOrderPopulator(final Populator<OrderModel, OrderIndex> orderPopulator)
	{
		this.orderPopulator = orderPopulator;
	}

	/**
	 * @return the hciPublisher
	 */
	public Publisher getPublisher()
	{
		return publisher;
	}

	@Required
	public void setPublisher(final Publisher publisher)
	{
		this.publisher = publisher;
	}

	public Populator<OrderModel, OrderWS> getOrderWSPopulator()
	{
		return orderWSPopulator;
	}

	public void setOrderWSPopulator(final Populator<OrderModel, OrderWS> orderWSPopulator)
	{
		this.orderWSPopulator = orderWSPopulator;
	}

}
