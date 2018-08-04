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
package de.hybris.platform.sap.sappricing.jalo;

import de.hybris.platform.core.Registry;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.order.AbstractOrder;
import de.hybris.platform.jalo.order.AbstractOrderEntry;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
import de.hybris.platform.jalo.order.price.PriceFactory;
import de.hybris.platform.jalo.order.price.ProductPriceInformations;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.util.JspContext;
import de.hybris.platform.util.PriceValue;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.hybris.platform.sap.sappricing.constants.SappricingConstants;



/**
 * This is the extension manager of the Sappricing extension.
 */
public class SappricingManager extends GeneratedSappricingManager implements PriceFactory
{
	/** Edit the local|project.properties to change logging behavior (properties 'log4j.*'). */
	private static final Logger LOG = Logger.getLogger(SappricingManager.class.getName());

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
	public static SappricingManager getInstance()
	{
		return (SappricingManager) Registry.getCurrentTenant().getJaloConnection().getExtensionManager()
				.getExtension(SappricingConstants.EXTENSIONNAME);
	}


	/**
	 * Never call the constructor of any manager directly, call getInstance() You can place your business logic here -
	 * like registering a jalo session listener. Each manager is created once for each tenant.
	 */
	public SappricingManager() // NOPMD 
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("constructor of SappricingManager called.");
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
			LOG.debug("init() of SappricingManager called. " + getTenant().getTenantID());
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
			LOG.debug("destroy() of SappricingManager called, current tenant: " + getTenant().getTenantID());
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


	@Override
	public ProductPriceInformations getAllPriceInformations(final SessionContext ctx, final Product product, final Date date, final boolean net)
			throws JaloPriceFactoryException
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List getProductPriceInformations(final SessionContext ctx, final Product product, final Date date, final boolean net)
			throws JaloPriceFactoryException
	{
		// TODO Auto-generated method stub
		return Collections.emptyList();
	}


	@Override
	public List getProductTaxInformations(final SessionContext ctx, final Product product, final Date date) throws JaloPriceFactoryException
	{
		// TODO Auto-generated method stub
		return Collections.emptyList();
	}


	@Override
	public List getProductDiscountInformations(final SessionContext ctx, final Product product, final Date date, final boolean net)
			throws JaloPriceFactoryException
	{
		// TODO Auto-generated method stub
		return Collections.emptyList();
	}


	@Override
	public boolean isNetUser(final User user)
	{
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public Collection getTaxValues(final AbstractOrderEntry entry) throws JaloPriceFactoryException
	{
		// TODO Auto-generated method stub
		return Collections.emptyList();
	}


	@Override
	public PriceValue getBasePrice(final AbstractOrderEntry entry) throws JaloPriceFactoryException
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List getDiscountValues(final AbstractOrderEntry entry) throws JaloPriceFactoryException
	{
		// TODO Auto-generated method stub
		return Collections.emptyList();
	}


	@Override
	public List getDiscountValues(final AbstractOrder order) throws JaloPriceFactoryException
	{
		// TODO Auto-generated method stub
		return Collections.emptyList();
	}
}
