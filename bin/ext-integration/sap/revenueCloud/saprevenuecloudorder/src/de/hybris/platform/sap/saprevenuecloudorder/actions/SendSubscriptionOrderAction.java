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
package de.hybris.platform.sap.saprevenuecloudorder.actions;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sap.hybris.scpiconnector.data.ResponseData;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.sap.saprevenuecloudorder.outbound.SendSubscriptionOrderToSCPIHelper;
import de.hybris.platform.task.RetryLaterException;

public class SendSubscriptionOrderAction extends AbstractSimpleDecisionAction<OrderProcessModel>
{

	private static final Logger LOG = Logger.getLogger(SendSubscriptionOrderAction.class);

	private BusinessProcessService businessProcessService;
	private SendSubscriptionOrderToSCPIHelper sendSubscriptionOrderToSCPIHelper;
	
	@Override
	public Transition executeAction(final OrderProcessModel process) throws RetryLaterException {

		final String subscriptionOrderid = createSubscriptionOrder(process.getOrder());
		if (!subscriptionOrderid.isEmpty())
		{
			return Transition.OK;
		}
		else
		{
			return Transition.NOK;
		}
	}

	protected String createSubscriptionOrder(final AbstractOrderModel orderModel)
	{

		final ResponseData response = getSendOrderToSCPIHelper().sendOrder(orderModel);
		String revenueCloudOrderId = null;

		try
		{
			final String status = response.getStatus();
			if ("200".equalsIgnoreCase(status) || "201".equalsIgnoreCase(status))
			{
				final ObjectNode customerResponse = new ObjectMapper().readValue(response.getResponseContent(), ObjectNode.class);
				revenueCloudOrderId = customerResponse.get("id").asText();
				if (!revenueCloudOrderId.isEmpty())
				{
					orderModel.setRevenueCloudOrderId(revenueCloudOrderId);
					getModelService().save(orderModel);
				}
			}
		}
		catch (final IOException e)
		{
			LOG.info("exception while sending order to sci");
			LOG.error(e);
		}
		return revenueCloudOrderId;
	}

	public BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	@Required
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}

	public SendSubscriptionOrderToSCPIHelper getSendOrderToSCPIHelper()
	{
		return sendSubscriptionOrderToSCPIHelper;
	}

	public void setSendOrderToSCPIHelper(final SendSubscriptionOrderToSCPIHelper sendSubscriptionOrderToSCPIHelper) {
		this.sendSubscriptionOrderToSCPIHelper = sendSubscriptionOrderToSCPIHelper;
	}
}
