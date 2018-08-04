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
package de.hybris.platform.sap.productconfig.runtime.cps.client;

import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCharacteristicInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCreateConfigInput;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.hybris.charon.RawResponse;
import com.hybris.charon.annotations.PATCH;

import rx.Observable;


/**
 * Specifies REST APIs for CPS calls to create, update and release a configuration runtime object
 */
public interface ConfigurationClientBase
{
	/**
	 * Updates a configuration runtime object
	 *
	 * @param characteristicInput
	 *           Changes to a characteristic
	 * @param cfgId
	 *           ID of runtime object
	 * @param itemId
	 *           ID of item that carries the characteristics to be changed
	 * @param csticId
	 *           ID of characteristic (language independent name)
	 * @param sessionCookieAsString
	 *           Cookie identifying the session
	 * @param cfCookieAsString
	 *           Cookie identifying the node
	 * @param eTag
	 *           eTag id for optimistic locking
	 * 
	 * @return Response. The observable cannot be specified, otherwise the charon method invocation fails
	 */
	@SuppressWarnings("rawtypes")
	@PATCH
	@Produces("application/json")
	@Path("/configurations/{cfgId}/items/{itemId}/characteristics/{csticId}")
	Observable updateConfiguration(CPSCharacteristicInput characteristicInput, @PathParam("cfgId") String cfgId,
			@PathParam("itemId") String itemId, @PathParam("csticId") String csticId,
			@HeaderParam("Cookie") String sessionCookieAsString, @HeaderParam("Cookie") String cfCookieAsString,
			@HeaderParam("If-Match") String eTag);

	/**
	 * Get a configuration runtime object
	 *
	 * @param cfgId
	 *           ID of runtime object
	 * @param lang
	 *           language in which language-dependent fields are returned (e.g. conflict texts)
	 * @param sessionCookieAsString
	 *           Cookie identifying the session
	 * @param cfCookieAsString
	 *           Cookie identifying the node
	 * @return Observable wrapping the configuration object
	 */
	@GET
	@Produces("application/json")
	@Path("/configurations/{cfgId}")
	Observable<CPSConfiguration> getConfiguration(@PathParam("cfgId") String cfgId, @HeaderParam("Accept-Language") String lang,
			@HeaderParam("Cookie") String sessionCookieAsString, @HeaderParam("Cookie") String cfCookieAsString);


	/**
	 * Create a configuration runtime object
	 *
	 * @param createConfigInput
	 *           Data we need to create a default configuration. We typically only provide the product code
	 * @param lang
	 *           language in which language-dependent fields are returned (e.g. conflict texts)
	 * @return Response wrapping the configuration object
	 */
	@POST
	@Produces("application/json")
	@Path("/configurations")
	Observable<RawResponse<CPSConfiguration>> createDefaultConfiguration(CPSCreateConfigInput createConfigInput,
			@HeaderParam("Accept-Language") String lang);

	/**
	 * Get the external representation of a configuration runtime object
	 *
	 * @param cfgId
	 *           ID of runtime object
	 * @param sessionCookieAsString
	 *           Cookie identifying the session
	 * @param cfCookieAsString
	 *           Cookie identifying the server node
	 * @return Observable wrapping the external representation of a configuration object
	 */
	@GET
	@Produces("application/json")
	@Path("/externalConfigurations/{cfgId}")
	Observable<CPSExternalConfiguration> getExternalConfiguration(@PathParam("cfgId") String cfgId,
			@HeaderParam("Cookie") String sessionCookieAsString, @HeaderParam("Cookie") String cfCookieAsString);

	/**
	 * Create a configuration runtime object based on a external representation
	 *
	 * @param externalConfiguration
	 *           External representation of a configuration
	 * @return Response wrapping the configuration object
	 */
	@POST
	@Produces("application/json")
	@Path("/externalConfigurations")
	Observable<RawResponse<CPSConfiguration>> createRuntimeConfigurationFromExternal(
			CPSExternalConfiguration externalConfiguration);

	/**
	 * Release a configuration runtime object
	 *
	 * @param cfgId
	 *           ID of runtime object
	 * @param sessionCookieAsString
	 *           Cookie identifying the session
	 * @param cfCookieAsString
	 *           Cookie identifying the server node
	 * @param eTag
	 *           eTag id for optimistic locking
	 * @return Response. The observable cannot be specified, otherwise the charon method invocation fails
	 */

	@SuppressWarnings("rawtypes")
	@DELETE
	@Produces("application/json")
	@Path("/configurations/{cfgId}")
	Observable deleteConfiguration(@PathParam("cfgId") String cfgId, @HeaderParam("Cookie") String sessionCookieAsString,
			@HeaderParam("Cookie") String cfCookieAsString, @HeaderParam("If-Match") String eTag);
}
