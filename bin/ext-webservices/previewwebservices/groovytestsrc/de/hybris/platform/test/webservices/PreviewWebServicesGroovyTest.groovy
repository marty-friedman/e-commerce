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
package de.hybris.platform.test.webservices;

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.catalog.jalo.CatalogManager
import de.hybris.platform.previewwebservices.constants.PreviewwebservicesConstants
import de.hybris.platform.servicelayer.ServicelayerTest
import de.hybris.platform.servicelayer.impex.impl.ClasspathImpExResource
import de.hybris.platform.util.JspContext
import de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert
import de.hybris.platform.webservicescommons.testsupport.client.WsSecuredRequestBuilder
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer

import javax.annotation.Resource
import java.lang.reflect.InvocationTargetException

import javax.ws.rs.client.Entity
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status
import javax.xml.bind.JAXBException

import org.junit.Assert
import org.junit.Before
import org.junit.Test

import groovy.json.JsonSlurper

@IntegrationTest
@NeedsEmbeddedServer(webExtensions =
[ PreviewwebservicesConstants.EXTENSIONNAME, de.hybris.platform.oauth2.constants.OAuth2Constants.EXTENSIONNAME ])
public class PreviewWebServicesGroovyTest extends ServicelayerTest {
	public static final String OAUTH_CLIENT_ID = "mobile_android";
	public static final String OAUTH_CLIENT_PASS = "secret";
	public static final String FIELDS_QUERYPARAM = "fields";
	public static final String USER = "previewmanager";
	public static final String PASSWORD = "1234";
	private static final String URI = "v1/preview";

	private WsSecuredRequestBuilder wsSecuredRequestBuilder;

	@Before
	void setUp() throws Exception {
		wsSecuredRequestBuilder = new WsSecuredRequestBuilder()//
				.extensionName(PreviewwebservicesConstants.EXTENSIONNAME)//
				.path(URI)//
				.client(OAUTH_CLIENT_ID, OAUTH_CLIENT_PASS);

		createCoreData();
		createDefaultCatalog();

		importData(new ClasspathImpExResource("/previewwebservices/test/democustomer-data.impex", "UTF-8"));
	}

	@Test
	public void testPostEmptyEntityForValidationErrors() throws IOException {

		// when ticket is posted to the endpoint
		Response response = authorizeAndPost([:])

		// then following errors are returned
		Map expected = [errors:[
				[message:"***", reason:"missing",subject:"resourcePath",subjectType:"parameter",type:"ValidationError"],
			]]


		Map responseJson = new JsonSlurper().parseText(response.readEntity(String.class));
		WebservicesAssert.assertJSONEquals(expected, responseJson)
	}


	@Test
	public void testPostForTicketMinimum() throws IOException {
		// given ticket with required values only
		Map requestBody = [resourcePath:"https://127.0.0.1:9002/yacceleratorstorefront?site=testCmsSite"]

		// when such a ticket is posted
		Response response = authorizeAndPost(requestBody)

		// then ticket is created and contains sent values
		WebservicesAssert.assertResponse(Status.CREATED, response);
		Map responseJson = new JsonSlurper().parseText(response.readEntity(String.class));
		Map expected = [ticketId:"***", resourcePath:"https://127.0.0.1:9002/yacceleratorstorefront?site=testCmsSite"]
		WebservicesAssert.assertJSONEquals(expected, responseJson)
	}

	@Test
	public void testPostWrongCatalog() throws IOException {
		// given ticket with wrong catalog
		Map requestBody = [catalog:"testwrongcatalog", catalogVersion:"Online", resourcePath:"https://127.0.0.1:9002/yacceleratorstorefront?site=testCmsSite"]

		// when such a ticket is posted
		Response response = authorizeAndPost(requestBody)

		// then bad request is returned with errors
		WebservicesAssert.assertResponse(Status.BAD_REQUEST, response);

	}

	@Test
	public void testPostWrongCatalogOnList() throws IOException {
		// given ticket with wrong catalog
		Map requestBody = [
			resourcePath:"https://127.0.0.1:9002/yacceleratorstorefront?site=testCmsSite",
			catalogVersions : [
				[
					catalog : "testWrongCatalog",
					catalogVersion : "Online"
				]
			]]

		// when such a ticket is posted
		Response response = authorizeAndPost(requestBody)

		// then bad request is returned with errors
		WebservicesAssert.assertResponse(Status.BAD_REQUEST, response);
		Map expected = [errors:[
				[message:"***", type:"ValidationError", reason:"invalid"]
			]]
		Map responseJson = new JsonSlurper().parseText(response.readEntity(String.class));
		WebservicesAssert.assertJSONEquals(expected, responseJson);

	}

	@Test
	public void testPostForBadResourcePath() throws IOException {
		// given ticket with required values only
		Map requestBody = [resourcePath:"https://127.0.0.1:9002/yacceleratorstorefront?site=notexistingsite"]

		// when such a ticket is posted
		final Response response = authorizeAndPost(requestBody);

		// then bad request is returned with errors
		WebservicesAssert.assertResponse(Status.BAD_REQUEST, response);
		Map expected = [errors:[
				[message:"***", type:"ConversionError"]
			]]
		Map responseJson = new JsonSlurper().parseText(response.readEntity(String.class));
		WebservicesAssert.assertJSONEquals(expected, responseJson)

	}

	@Test
	public void testPostForTicketWithPage() throws IOException {
		// given ticket with page
		Map requestBody = [
			resourcePath:"https://127.0.0.1:9002/yacceleratorstorefront?site=testCmsSite",
			pageId:"homepage",
			catalogVersions : [
				[
					catalog : "testContentCatalog",
					catalogVersion : "Online"
				]
			]]

		// when such a ticket is posted
		Response response = authorizeAndPost(requestBody);

		// then ticket is created and contains sent values
		WebservicesAssert.assertResponse(Status.CREATED, response);
		Map expected =  [
			ticketId:"***",
			resourcePath:"https://127.0.0.1:9002/yacceleratorstorefront?site=testCmsSite",
			pageId:"homepage",
			catalogVersions : [
				[
					catalog : "testContentCatalog",
					catalogVersion : "Online"
				]
			]
		]

		Map responseJson = new JsonSlurper().parseText(response.readEntity(String.class));
		WebservicesAssert.assertJSONEquals(expected, responseJson)
	}

	@Test
	public void testPostForTicketWithEverything() throws IOException, JAXBException {
		// given ticket with all values
		Map requestBody = [
			resourcePath:"https://127.0.0.1:9002/yacceleratorstorefront?site=testCmsSite",
			pageId:"homepage",
			language:"en",
			user:"testoauthcustomer",
			userGroup:"regulargroup",
			time:"2013-02-14T13:15:03-0800",
			catalogVersions : [
				[
					catalog : "testContentCatalog",
					catalogVersion : "Online"
				]
			]]

		// when such a ticket is posted
		Response response = authorizeAndPost(requestBody);

		// then ticket is created and contains sent values
		WebservicesAssert.assertResponse(Status.CREATED, response);
		Map expected =  [ticketId:"***",
			resourcePath:"https://127.0.0.1:9002/yacceleratorstorefront?site=testCmsSite",
			pageId:"homepage",
			language:"en",
			user:"testoauthcustomer",
			userGroup:"regulargroup",
			time:"***",
			catalogVersions : [
				[
					catalog : "testContentCatalog",
					catalogVersion : "Online"
				]
			]]
		Map responseJson = new JsonSlurper().parseText(response.readEntity(String.class));
		WebservicesAssert.assertJSONEquals(expected, responseJson)

	}

	@Test
	public void shouldReturn201ForAdmin() throws IOException {
		Map requestBody = [
			resourcePath:"https://127.0.0.1:9002/yacceleratorstorefront?site=testCmsSite",
			catalogVersions : [
				[
					catalog : "testContentCatalog",
					catalogVersion : "Online"
				]
			]]

		//when posting with admin
		final Response result = wsSecuredRequestBuilder//
				.resourceOwner("admin", "nimda")//
				.grantResourceOwnerPasswordCredentials()//
				.build()//
				.post(Entity.entity(requestBody, MediaType.APPLICATION_JSON));

		//then we receive a 201
		WebservicesAssert.assertResponse(Status.CREATED, result);
	}

	@Test
	public void shouldReturn400ForRandomUser() throws IOException {
		Map requestBody = [resourcePath:"https://127.0.0.1:9002/yacceleratorstorefront?site=testCmsSite"]

		//when posting with a user that doesn't belong to cmsmanager group
		final Response result = wsSecuredRequestBuilder//
				.resourceOwner("randomuser", "1234")//
				.grantResourceOwnerPasswordCredentials()//
				.build()//
				.post(Entity.entity(requestBody, MediaType.APPLICATION_JSON));

		//then we receive a 403
		Assert.assertEquals(403, result.getStatus());
	}

	@Test
	public void shouldReturn201WithSearchRestrictionsEnabled() throws NoSuchMethodException, SecurityException,
	IllegalAccessException, IllegalArgumentException, InvocationTargetException, JAXBException {
		//yeah, we have to do that because it is a private method...
		//		final Method method = CatalogManager.class.getDeclaredMethod("createSearchRestrictions", JspContext.class);
		//		method.setAccessible(true);
		//		method.invoke(CatalogManager.getInstance(), (JspContext) null);

		CatalogManager.getInstance().metaClass.methods.each { method ->
			if (method.name == 'createSearchRestrictions') {
				method.public = true
				method.invoke(CatalogManager.getInstance(), (JspContext) null)
			}
		}

		Map requestBody = [
			resourcePath:"https://127.0.0.1:9002/yacceleratorstorefront?site=testCmsSite",
			pageId:"homepage",
			language:"en",
			user:"testoauthcustomer",
			userGroup:"regulargroup",
			time:"2013-02-14T13:15:03-0800",
			catalogVersions : [
				[
					catalog : "testContentCatalog",
					catalogVersion : "Online"
				]
			]]

		// when such a ticket is posted
		final Response response = authorizeAndPost(requestBody)

		// then ticket is created and contains sent values
		WebservicesAssert.assertResponse(Status.CREATED, response);
		Map expectedResponse = [ticketId:"***",
			resourcePath:"https://127.0.0.1:9002/yacceleratorstorefront?site=testCmsSite",
			pageId:"homepage",
			language:"en",
			user:"testoauthcustomer",
			userGroup:"regulargroup",
			time:"***",
			catalogVersions : [
				[
					catalog : "testContentCatalog",
					catalogVersion : "Online"
				]
			]]
		Map responseJson = new JsonSlurper().parseText(response.readEntity(String.class));
		WebservicesAssert.assertJSONEquals(expectedResponse, responseJson)
	}

	@Test
	public void testGetForForPostTicketMinimum() throws IOException {
		Map requestBody = [resourcePath:"https://127.0.0.1:9002/yacceleratorstorefront?site=testCmsSite"]

		Response response = authorizeAndPost(requestBody)

		WebservicesAssert.assertResponse(Status.CREATED, response)
		String id = new JsonSlurper().parseText(response.readEntity(String.class)).ticketId;

		//when
		response = wsSecuredRequestBuilder
				.resourceOwner(USER, PASSWORD)//
				.grantResourceOwnerPasswordCredentials().path(id)//
				.build()//
				.get();

		//then
		WebservicesAssert.assertResponse(Status.OK, response);
		Map expectedResponse = [ticketId:id, resourcePath:"https://127.0.0.1:9002/yacceleratorstorefront?site=testCmsSite"]
		Map responseJson = new JsonSlurper().parseText(response.readEntity(String.class));
		WebservicesAssert.assertJSONEquals(expectedResponse, responseJson)
	}

	@Test
	public void testGetForCatalogOnly() throws IOException {
		Map requestBody = [
			resourcePath:"https://127.0.0.1:9002/yacceleratorstorefront?site=testCmsSite",
			catalogVersions : [
				[
					catalog : "testContentCatalog",
					catalogVersion : "Online"
				]
			]]

		Response response = authorizeAndPost(requestBody)
		WebservicesAssert.assertResponse(Status.CREATED, response);
		String id = new JsonSlurper().parseText(response.readEntity(String.class)).ticketId;

		//when
		response = wsSecuredRequestBuilder
				.resourceOwner(USER, PASSWORD)//
				.grantResourceOwnerPasswordCredentials()//
				.path(id)//
				.queryParam(FIELDS_QUERYPARAM, "catalogVersions")//
				.build()//
				.get();

		//then
		WebservicesAssert.assertResponse(Status.OK, response);
		Map expectedResponse = [
			catalogVersions : [
				[
					catalog : "testContentCatalog",
					catalogVersion : "Online"
				]
			]]
		Map responseJson = new JsonSlurper().parseText(response.readEntity(String.class));
		WebservicesAssert.assertJSONEquals(expectedResponse, responseJson,false)

	}

	@Test
	public void testGetForNotExistingTicket() throws IOException {
		//when
		final Response response = wsSecuredRequestBuilder.path("1234")//
				.resourceOwner(USER, PASSWORD)//
				.grantResourceOwnerPasswordCredentials()//
				.build()//
				.get();

		//then
		WebservicesAssert.assertResponse(Status.NOT_FOUND, response);
		Map expected = [errors:[
				[message:"***", type:"NotFoundError"]
			]]

		Map responseJson = new JsonSlurper().parseText(response.readEntity(String.class));
		WebservicesAssert.assertJSONEquals(expected, responseJson)
	}

	@Test
	public void testPutForNotExistingTicket() throws IOException {
		Map requestBody = [ticketId:"1234", resourcePath:"https://127.0.0.1:9002/yacceleratorstorefront?site=testCmsSite"]

		//when
		final Response response = wsSecuredRequestBuilder.path("1234")//
				.resourceOwner(USER, PASSWORD)//
				.grantResourceOwnerPasswordCredentials()//
				.build()//
				.put(Entity.entity(requestBody, MediaType.APPLICATION_JSON));

		//then
		WebservicesAssert.assertResponse(Status.NOT_FOUND, response);
		Map expected = [errors:[
				[message:"***", type:"NotFoundError"]
			]]

		Map responseJson = new JsonSlurper().parseText(response.readEntity(String.class));
		WebservicesAssert.assertJSONEquals(expected, responseJson);
	}

	@Test
	public void testPutForCodeConflictExistingTicket() throws IOException {
		Map requestBody = [ticketId:"1234", resourcePath:"https://127.0.0.1:9002/yacceleratorstorefront?site=testCmsSite"]

		//when
		final Response response = wsSecuredRequestBuilder.path("12345")//
				.resourceOwner(USER, PASSWORD)//
				.grantResourceOwnerPasswordCredentials()//
				.build()//
				.put(Entity.entity(requestBody, MediaType.APPLICATION_JSON));

		//then
		WebservicesAssert.assertResponse(Status.CONFLICT, response);
		Map expected = [errors:[
				[message:"***", type:"CodeConflictError"]
			]]
		Map responseJson = new JsonSlurper().parseText(response.readEntity(String.class));
		WebservicesAssert.assertJSONEquals(expected, responseJson);
	}

	@Test
	public void testPutPreviewWithNullMandatoryFields() throws IOException {
		Map requestBody = [resourcePath:"https://127.0.0.1:9002/yacceleratorstorefront?site=testCmsSite"]

		Response response = authorizeAndPost(requestBody)
		WebservicesAssert.assertResponse(Status.CREATED, response);
		String id = new JsonSlurper().parseText(response.readEntity(String.class)).ticketId;


		//when
		response = wsSecuredRequestBuilder.path(id)//
				.resourceOwner(USER, PASSWORD)//
				.grantResourceOwnerPasswordCredentials()//
				.build()//
				.put(Entity.entity([ticketId:id], MediaType.APPLICATION_JSON));

		//then
		WebservicesAssert.assertResponse(Status.BAD_REQUEST, response);
		Map expected = [errors:[
				[message:"***", type:"ValidationError", reason:"missing"]
			]]
		Map responseJson = new JsonSlurper().parseText(response.readEntity(String.class));
		WebservicesAssert.assertJSONEquals(expected, responseJson);
	}

	@Test
	public void testPutPreviewWithNullNotMandatoryFields() throws IOException, JAXBException {
		// given ticket with all values
		Map requestBody = [
			resourcePath:"https://127.0.0.1:9002/yacceleratorstorefront?site=testCmsSite",
			pageId:"homepage",
			language:"en",
			user:"testoauthcustomer",
			userGroup:"regulargroup",
			time:"2013-02-14T13:15:03-0800",
			catalogVersions : [
				[
					catalog : "testContentCatalog",
					catalogVersion : "Online"
				]
			]]

		Response response = authorizeAndPost(requestBody)
		WebservicesAssert.assertResponse(Status.CREATED, response);
		String id = new JsonSlurper().parseText(response.readEntity(String.class)).ticketId;

		requestBody = [ticketId:id,
			resourcePath:"https://127.0.0.1:9002/yacceleratorstorefront?site=testCmsSite",
			catalogVersions : [
				[
					catalog : "testContentCatalog",
					catalogVersion : "Online"
				]
			]]


		//when
		response = wsSecuredRequestBuilder.path(id)//
				.resourceOwner(USER, PASSWORD)//
				.grantResourceOwnerPasswordCredentials()//
				.build()//
				.put(Entity.entity(requestBody, MediaType.APPLICATION_JSON));

		//then
		WebservicesAssert.assertResponse(Status.OK, response);
		Map expectedResponse = [ticketId:id,
			resourcePath:"https://127.0.0.1:9002/yacceleratorstorefront?site=testCmsSite",
			catalogVersions : [
				[
					catalog : "testContentCatalog",
					catalogVersion : "Online"
				]
			]]

		Map responseJson = new JsonSlurper().parseText(response.readEntity(String.class));
		WebservicesAssert.assertJSONEquals(expectedResponse, responseJson, true);
		Assert.assertFalse("Response should not contain pageId",responseJson.containsKey("pageId"));
		Assert.assertFalse("Response should not contain language",responseJson.containsKey("language"));
		Assert.assertFalse("Response should not contain user",responseJson.containsKey("user"));
		Assert.assertFalse("Response should not contain userGroup",responseJson.containsKey("userGroup"));
		Assert.assertFalse("Response should not contain time",responseJson.containsKey("time"));
	}



	private Response authorizeAndPost(Map requestBody) {
		final Response result = wsSecuredRequestBuilder//
				.resourceOwner(USER, PASSWORD)//
				.grantResourceOwnerPasswordCredentials()//
				.build()//
				.post(Entity.entity(requestBody, MediaType.APPLICATION_JSON))
		return result
	}
}