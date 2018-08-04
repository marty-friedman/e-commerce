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
package de.hybris.platform.adaptivesearchwebservices.test;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON
import static javax.ws.rs.core.MediaType.APPLICATION_XML
import static javax.ws.rs.core.Response.Status.OK

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.adaptivesearchwebservices.constants.AdaptivesearchwebservicesConstants
import de.hybris.platform.oauth2.constants.OAuth2Constants
import de.hybris.platform.webservicescommons.testsupport.client.WsSecuredRequestBuilder
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer

import org.junit.Before
import org.junit.Test

@IntegrationTest
@NeedsEmbeddedServer(webExtensions = [AdaptivesearchwebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME])
class SearchProfileWebServiceTest extends AbstractWebServiceTest {

	public static final String OAUTH_CLIENT_ID = "mobile_android";
	public static final String OAUTH_CLIENT_PASS = "secret";

	private WsSecuredRequestBuilder wsSecuredRequestBuilder;

	@Before
	void setUp() {
		super.setUp();

		importCsv("/adaptivesearchwebservices/test/searchProfiles.impex", "utf-8");
	}

	@Test
	public void testWithXMLFormat() {
		"get search profiles"(APPLICATION_XML);
	}

	@Test
	public void testWithJSONFormat() {
		"get search profiles"(APPLICATION_JSON);
	}

	def "get search profiles"(format) {
		given:
		def request = wsSecuredRequestBuilder
				.path("v1/searchprofiles")
				.queryParam("indexTypes", "testIndex")
				.queryParam("catalogVersions", "hwcatalog:online")
				.build()
				.accept(format);

		when:
		def response = request.get();

		then:
		assert response.getStatus() == OK.getStatusCode()

		def data = parseResponse(response, format);
		assert data.searchProfiles[0].code == "simpleProfile"
		assert data.searchProfiles[0].name == "Simple search profile"
		assert data.searchProfiles[0].indexType == "testIndex"
		assert data.searchProfiles[0].catalogVersion == "hwcatalog:online"
	}
}
