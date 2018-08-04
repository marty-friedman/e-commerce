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
package de.hybris.platform.sap.sapcpicustomerexchange.service.impl;

import de.hybris.platform.sap.sapcpiadapter.clients.SapCpiCustomerClient;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiConfig;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiCustomer;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiTargetSystem;
import de.hybris.platform.sap.sapcpiadapter.service.SapCpiOAuthService;
import de.hybris.platform.sap.sapcpicustomerexchange.service.SapCpiCustomerService;

import javax.annotation.Resource;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import rx.observables.BlockingObservable;
import rx.observers.TestSubscriber;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations =
{ "classpath:sapcpiadapter-spring.xml", "classpath:test/sapcpicustomerexchange-test-spring.xml" })
public class SapCpiCustomerServiceIntegrationTest
{

	@Resource
	private SapCpiCustomerClient sapCpiCustomerClient;

	@Resource
	private SapCpiCustomerService sapCpiCustomerService;

	@Resource
	private SapCpiOAuthService authService;



	public void createCustomer() throws Exception
	{
		final SapCpiCustomer customer = new SapCpiCustomer();
		customer.setUid("testuser@sap.com");
		customer.setCustomerID("9000001280");
		customer.setFirstName("john");
		customer.setLastName("smith");
		customer.setSessionLanguage("EN");
		customer.setTitle("Mr.");
		customer.setBaseStore("electronics");
		customer.setObjType("KNA1");
		customer.setAddressUsage("US");
		customer.setCountry("US");
		customer.setStreet("10th avenue");
		customer.setPhone("1111122222");
		customer.setFax("2222211111");
		customer.setTown("Alabama");
		customer.setPostalCode("10004");
		customer.setStreetNumber("100");
		customer.setRegion(null);

		// set config
		final SapCpiTargetSystem sapCpiTargetSystem = new SapCpiTargetSystem();
		sapCpiTargetSystem.setReceiverName("HCIRED");
		sapCpiTargetSystem.setReceiverPort("HCIRED");
		sapCpiTargetSystem.setSenderName("HBRGTSM07");
		sapCpiTargetSystem.setSenderPort("HBRGTSM07");
		sapCpiTargetSystem.setUrl("http://ldai1qe6.wdf.sap.corp:44300/sap/bc/srt/idoc?sap-client=910");
		sapCpiTargetSystem.setUsername("QE6Credentials");


		final SapCpiConfig sapCpiConfig = new SapCpiConfig();

		sapCpiConfig.setSapCpiTargetSystem(sapCpiTargetSystem);

		customer.setSapCpiConfig(sapCpiConfig);


		final SapCpiCustomerService sapCpiCustomerService = new DefaultSapCpiCustomerService(sapCpiCustomerClient, authService);


		final BlockingObservable obs = sapCpiCustomerService.createCustomer(customer).toObservable().toBlocking();
		final TestSubscriber tester = new TestSubscriber();
		obs.subscribe(tester);

		tester.assertCompleted();
	}
}
