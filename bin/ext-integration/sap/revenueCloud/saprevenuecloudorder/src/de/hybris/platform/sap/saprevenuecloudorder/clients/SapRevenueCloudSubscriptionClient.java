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
package de.hybris.platform.sap.saprevenuecloudorder.clients;

import de.hybris.platform.sap.saprevenuecloudorder.pojo.CancelSubscription;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.ExtendSubscription;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.Subscription;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.hybris.charon.annotations.Control;
import com.hybris.charon.annotations.Http;
import com.hybris.charon.annotations.OAuth;


@OAuth
@Http
@Control(retries = "${retries:3}", retriesInterval = "${retriesInterval:2000}", timeout = "${timeout:40000}")
public interface SapRevenueCloudSubscriptionClient
{

	@GET
	@Path("/subscriptions?customer.id={clientId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	List<Subscription> getSubscriptionsByClientId(@PathParam("clientId") String clientId);

	@GET
	@Path("/subscriptions/{subscriptionsId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	Subscription getSubscriptionById(@PathParam("subscriptionsId") String subscriptionsId);

	@POST
	@Path("/subscriptions")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	Subscription createSubscription(Subscription subscription);

	@POST
	@Path("/subscriptions/{id}/cancellation")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	Response cancelSubscription(@PathParam("id") String id, CancelSubscription subscription);

	@POST
	@Path("/subscriptions/{id}/extension")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	Response extendSubscription(@PathParam("id") String id, ExtendSubscription subscription);
}