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
package de.hybris.platform.personalizationservices.voters.impl;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.personalizationservices.RecalculateAction;
import de.hybris.platform.personalizationservices.voters.Vote;
import de.hybris.platform.personalizationservices.voters.impl.UrlVoter.RecalculateConfig;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.MapConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class UrlVoterTest
{
	@Mock
	ConfigurationService configurationService;


	UrlVoter urlVoter;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		urlVoter = new UrlVoter();
		urlVoter.setConfigurationService(configurationService);
		final Configuration configuration = createConfiguration();
		when(configurationService.getConfiguration()).thenReturn(configuration);
	}

	private Configuration createConfiguration()
	{
		final Map<String, String> map = new HashMap<>();
		map.put("personalizationservices.calculate.checkout.url", "checkout");
		map.put("personalizationservices.calculate.checkout.actions", "RECALCULATE");
		map.put("personalizationservices.calculate.user.checkout.url", "user/checkout");
		map.put("personalizationservices.calculate.user.checkout.actions", "LOAD");
		map.put("personalizationservices.calculate.duplicatedUrl1.url", ".*/cart");
		map.put("personalizationservices.calculate.duplicatedUrl1.actions", "RECALCULATE,ASYNC_PROCESS");
		map.put("personalizationservices.calculate.duplicatedUrl2.url", ".*/cart");
		map.put("personalizationservices.calculate.duplicatedUrl2.actions", "UPDATE,RECALCULATE");
		map.put("personalizationservices.calculate.notworking", "notworking");
		map.put("personalizationservices.calculate.working.url", "url2");
		map.put("personalizationservices.calculate.working.actions", "UPDATE,RECALCULATE,LOAD,ASYNC_PROCESS");
		map.put("personalizationservices.calculate.emptyUrl.url", "");
		map.put("personalizationservices.calculate.emptyUrl.actions", "UPDATE");
		map.put("personalizationservices.calculate.emptyAction.url", "url");
		map.put("personalizationservices.calculate.emptyAction.actions", "");
		map.put("personalizationservices.calculate.wrongAction.url", "url");
		map.put("personalizationservices.calculate.wrongAction.actions", "NOT_EXISTING_ACTION");

		final MapConfiguration configuration = new MapConfiguration(map);
		configuration.setDelimiterParsingDisabled(true);
		return configuration;
	}

	@Test
	public void testConfigLoad()
	{
		//when
		final Collection<RecalculateConfig> configuration = urlVoter.getRecalculateConfiguration();

		//then
		Assert.assertEquals(6, configuration.size());
		asserContains(configuration, "checkout", "RECALCULATE");
		asserContains(configuration, "user/checkout", "LOAD");
		asserContains(configuration, "url2", "UPDATE,RECALCULATE,LOAD,ASYNC_PROCESS");
		asserContainsDuplicated(configuration, ".*/cart", "RECALCULATE,ASYNC_PROCESS", "UPDATE,RECALCULATE");
	}

	private void asserContains(final Collection<RecalculateConfig> configuration, final String url, final String actions)
	{
		final Optional<RecalculateConfig> configElement = configuration.stream()
				.filter(c -> url.equals(c.getUrlPattern().toString())).findFirst();
		Assert.assertTrue("url is not in configuration : " + url, configElement.isPresent());
		Assert.assertEquals("actions for url " + url + " are not equals", actions, configElement.get().getActions());
	}

	private void asserContainsDuplicated(final Collection<RecalculateConfig> configuration, final String url,
			final String actions1, final String actions2)
	{
		final List<RecalculateConfig> configElements = configuration.stream().filter(c -> url.equals(c.getUrlPattern().toString()))
				.collect(Collectors.toList());

		Assert.assertEquals(2, configElements.size());
		Assert.assertTrue(actions1.equals(configElements.get(0).getActions())
				|| actions2.equals(configElements.get(0).getActions()));
		Assert.assertTrue(actions1.equals(configElements.get(1).getActions())
				|| actions2.equals(configElements.get(1).getActions()));

	}

	@Test
	public void testRecalculateVote()
	{
		//given
		final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		Mockito.when(request.getRequestURI()).thenReturn("http://mywebsite/checkout");

		//when
		final Vote vote = urlVoter.getVote(request, null);

		//then
		Assert.assertTrue(vote.getRecalculateActions().contains(RecalculateAction.RECALCULATE));
	}

	@Test
	public void testMultipleActionVote()
	{
		//given
		final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		Mockito.when(request.getRequestURI()).thenReturn("http://mywebsite/url2");

		//when
		final Vote vote = urlVoter.getVote(request, null);

		//then
		Assert.assertEquals(4, vote.getRecalculateActions().size());
		Assert.assertTrue(vote.getRecalculateActions().contains(RecalculateAction.RECALCULATE));
		Assert.assertTrue(vote.getRecalculateActions().contains(RecalculateAction.ASYNC_PROCESS));
		Assert.assertTrue(vote.getRecalculateActions().contains(RecalculateAction.UPDATE));
		Assert.assertTrue(vote.getRecalculateActions().contains(RecalculateAction.LOAD));
	}

	@Test
	public void testNotConfiguredUrl()
	{
		//given
		final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		Mockito.when(request.getRequestURI()).thenReturn("http://mywebsite/product/1234");

		//when
		final Vote vote = urlVoter.getVote(request, null);

		//then
		Assert.assertTrue(vote.getRecalculateActions().isEmpty());
	}

	@Test
	public void testDuplicatedUrl()
	{
		//given
		final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		Mockito.when(request.getRequestURI()).thenReturn("http://mywebsite/user/cart");

		//when
		final Vote vote = urlVoter.getVote(request, null);

		//then
		Assert.assertEquals(3, vote.getRecalculateActions().size());
		Assert.assertTrue(vote.getRecalculateActions().contains(RecalculateAction.RECALCULATE));
		Assert.assertTrue(vote.getRecalculateActions().contains(RecalculateAction.UPDATE));
		Assert.assertTrue(vote.getRecalculateActions().contains(RecalculateAction.ASYNC_PROCESS));
	}

	@Test
	public void testNotExistingActionName()
	{
		//given
		final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		Mockito.when(request.getRequestURI()).thenReturn("http://mywebsite/url");

		//when
		final Vote vote = urlVoter.getVote(request, null);

		//then
		Assert.assertEquals(0, vote.getRecalculateActions().size());
	}

}
