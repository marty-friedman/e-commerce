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
package de.hybris.platform.timedaccesspromotionsservices.jalo;

import de.hybris.platform.core.Registry;
import de.hybris.platform.timedaccesspromotionsservices.constants.TimedaccesspromotionsservicesConstants;
import de.hybris.platform.util.JspContext;

import java.util.Map;

import org.apache.log4j.Logger;



/**
 * This is the extension manager of the Timedaccesspromotionsservices extension.
 */
public class TimedaccesspromotionsservicesManager extends GeneratedTimedaccesspromotionsservicesManager
{
	/** Edit the local|project.properties to change logging behavior (properties 'log4j.*'). */
	private static final Logger LOG = Logger.getLogger(TimedaccesspromotionsservicesManager.class.getName());

	/*
	 * Some important tips for development:
	 * 
	 * Do NEVER use the default constructor of manager's or items. => If you want to do something whenever the manger is
	 * created use the init() or destroy() methods described below
	 * 
	 * Do NEVER use STATIC fields in your manager or items! => If you want to cache anything in a "static" way, use an
	 * instance variable in your manager, the manager is created only once in the lifetime of a "deployment" or tenant.
	 */


	/**
	 * Get the valid instance of this manager.
	 *
	 * @return the current instance of this manager
	 */
	public static TimedaccesspromotionsservicesManager getInstance()
	{
		return (TimedaccesspromotionsservicesManager) Registry.getCurrentTenant().getJaloConnection().getExtensionManager()
				.getExtension(TimedaccesspromotionsservicesConstants.EXTENSIONNAME);
	}


	/**
	 * Never call the constructor of any manager directly, call getInstance() You can place your business logic here -
	 * like registering a jalo session listener. Each manager is created once for each tenant.
	 */
	public TimedaccesspromotionsservicesManager() // NOPMD
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("constructor of TimedaccesspromotionsservicesManager called.");
		}
	}

	/**
	 * Use this method to do some basic work only ONCE in the lifetime of a tenant resp. "deployment". This method is
	 * called after manager creation (for example within startup of a tenant). Note that if you have more than one tenant
	 * you have a manager instance for each tenant.
	 */
	@Override
	public void init()
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("init() of TimedaccesspromotionsservicesManager called. " + getTenant().getTenantID());
		}
	}

	/**
	 * Use this method as a callback when the manager instance is being destroyed (this happens before system
	 * initialization, at redeployment or if you shutdown your VM). Note that if you have more than one tenant you have a
	 * manager instance for each tenant.
	 */
	@Override
	public void destroy()
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("destroy() of TimedaccesspromotionsservicesManager called, current tenant: " + getTenant().getTenantID());
		}
	}

	/**
	 * Implement this method to create initial objects. This method will be called by system creator during
	 * initialization and system update. Be sure that this method can be called repeatedly.
	 *
	 * An example usage of this method is to create required cronjobs or modifying the type system (setting e.g some
	 * default values)
	 *
	 * @param params
	 *           the parameters provided by user for creation of objects for the extension
	 * @param jspc
	 *           the jsp context; you can use it to write progress information to the jsp page during creation
	 */
	@Override
	public void createEssentialData(final Map<String, String> params, final JspContext jspc)
	{
		// implement here code creating essential data
	}

	/**
	 * Implement this method to create data that is used in your project. This method will be called during the system
	 * initialization.
	 *
	 * An example use is to import initial data like currencies or languages for your project from an csv file.
	 *
	 * @param params
	 *           the parameters provided by user for creation of objects for the extension
	 * @param jspc
	 *           the jsp context; you can use it to write progress information to the jsp page during creation
	 */
	@Override
	public void createProjectData(final Map<String, String> params, final JspContext jspc)
	{
		// implement here code creating project data
	}
}
