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
/**
 *
 */
package de.hybris.platform.test.webservices

import de.hybris.platform.servicelayer.impex.impl.ClasspathImpExResource

import javax.annotation.Resource;

import static javax.ws.rs.core.MediaType.*
import static javax.ws.rs.core.Response.Status.*

import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.previewwebservices.constants.PreviewwebservicesConstants
import de.hybris.platform.servicelayer.ServicelayerTest
import de.hybris.platform.webservicescommons.testsupport.client.WsSecuredRequestBuilder
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer

import javax.ws.rs.client.Entity
import javax.ws.rs.core.MediaType

import org.junit.Before
import org.junit.Test

import groovy.json.JsonSlurper


@IntegrationTest
@NeedsEmbeddedServer(webExtensions =
[ PreviewwebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME ])
public class PreviewWebServicesCompatibilityTest extends ServicelayerTest {
	public static final String OAUTH_CLIENT_ID = "mobile_android";
	public static final String OAUTH_CLIENT_PASS = "secret";
	public static final String FIELDS_QUERYPARAM = "fields";

	private static final String URI = "v1/preview";

	private WsSecuredRequestBuilder wsSecuredRequestBuilder;

	@Before
	void setUp() {
		wsSecuredRequestBuilder = new WsSecuredRequestBuilder()//
				.extensionName(PreviewwebservicesConstants.EXTENSIONNAME)//
				.path(URI)//
				.client(OAUTH_CLIENT_ID, OAUTH_CLIENT_PASS);

		createCoreData();
		createDefaultCatalog();

		importData(new ClasspathImpExResource("/previewwebservices/test/democustomer-data.impex", "UTF-8"));
	}

	/**
	 * Compatibility test for 6.1 format. If this test breaks, it means that you might have broken the backward
	 * compatility of this webservice /json format with 6.1 version.
	 */
	@Test
	public void testXmlCompatibility_6_1() {
		"create preview ticket"("xml", APPLICATION_XML)
	}

	/**
	 * Compatibility test for 6.1 format. If this test breaks, it means that you might have broken the backward
	 * compatility of this webservice /json format with 6.1 version.
	 */
	@Test
	public void testJSONCompatibility_6_1() {
		"create preview ticket"("json", APPLICATION_JSON)
	}

	def "create preview ticket"(ext, format) {
		given: "predefined request and response"
		def request = loadText("/previewwebservices/test/wstests/preview-request."+ext)
		def expected = loadObject("/previewwebservices/test/wstests/preview-response."+ext, format )

		when: "such a ticket is posted"
		def response = wsSecuredRequestBuilder//
				.resourceOwner("previewmanager", "1234")//
				.grantResourceOwnerPasswordCredentials()//
				.build()//
				.accept(format)//
				.post(Entity.entity(request, format));
		def actual = parse(response, format)


		then: "ticket is created and contains sent values"
		assert response.status == CREATED.statusCode
		assert expected.ticketId != null

		when: "random field is normalized"
		actual.ticketId = expected.ticketId

		then: "actual response details are the same as expected"
		assert actual.catalog == expected.catalog
		assert actual.catelogVersion == expected.catelogVersion
		assert actual.catelogVersions == expected.catelogVersions
		assert actual.language == expected.language
		assert actual.pageId == expected.pageId
		assert actual.resourcePath == expected.resourcePath
		assert actual.ticketId == expected.ticketId
		assert actual.time == expected.time
		assert actual.user == expected.user
		assert actual.userGroup == expected.userGroup

	}

	def loadText(name) {
		this.getClass().getResource(name).text
	}

	def loadObject(name, format) {
		stringParse( loadText(name), format )
	}

	def parse(response, format) {
		def text = response.readEntity(String.class)
		stringParse(text, format)
	}

	def stringParse(text, format) {
		switch(format) {
			case APPLICATION_JSON:
				return new JsonSlurper().parseText(text);
			case APPLICATION_XML:
				return new XmlSlurper().parseText(text);
			default:
				return null;
		}
	}
}
