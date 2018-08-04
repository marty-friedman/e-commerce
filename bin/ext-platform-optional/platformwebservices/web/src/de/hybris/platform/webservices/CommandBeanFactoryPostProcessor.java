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
package de.hybris.platform.webservices;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;

import com.google.common.collect.Iterables;

public class CommandBeanFactoryPostProcessor implements BeanFactoryPostProcessor
{
	private static final Logger LOG = Logger.getLogger(CommandBeanFactoryPostProcessor.class);

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException
	{
		final List<String> commandBeanNames = Arrays.asList(beanFactory.getBeanNamesForType(Command.class));
		final List<String> commandHandlerBeanNames = Arrays.asList(beanFactory.getBeanNamesForType(CommandHandler.class));
		
		final Iterable<String> beanNames = Iterables.concat(commandBeanNames, commandHandlerBeanNames);
		
		for (final String beanName : beanNames)
		{
			final BeanDefinition definition = beanFactory.getBeanDefinition(beanName);
			changeToPrototypeScope(beanName, definition);
		}
	}

	private void changeToPrototypeScope(final String beanName, final BeanDefinition definition)
	{
		if (definition.isPrototype())
		{
			return;
		}
		
		final String scope = definition.getScope();
		final boolean isDefaultScope = AbstractBeanDefinition.SCOPE_DEFAULT.equals(scope);
		final String message = "Changing scope of bean '" + beanName + "' from " +
				(isDefaultScope ? "default" : "'" + scope +"'") + " to '" +
				BeanDefinition.SCOPE_PROTOTYPE + "'."; 
		
		if (isDefaultScope)
		{
			LOG.info(message);
		}
		else
		{
			LOG.warn(message);
		}
		
		definition.setScope(BeanDefinition.SCOPE_PROTOTYPE);
	}

}
