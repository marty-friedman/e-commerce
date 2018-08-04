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
package de.hybris.platform.assistedserviceatddtests.keywords;

import de.hybris.platform.assistedservicefacades.AssistedServiceFacade;
import de.hybris.platform.assistedserviceservices.exception.AssistedServiceException;
import de.hybris.platform.atddengine.keywords.AbstractKeywordLibrary;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;


public class AssistedServiceKeywordLibrary extends AbstractKeywordLibrary
{
	@Autowired
	private AssistedServiceFacade assistedServiceFacade;

	@Autowired
	private CustomerFacade customerFacade;

	@Autowired
	private CartService cartService;

	@Autowired
	private FlexibleSearchService flexibleSearchService;

	@Autowired
	private UserService userService;

	@Autowired
	private BaseSiteService baseSiteService;

	public void emulateCustomer(final String customerId, final String cartId) throws AssistedServiceException
	{
		assistedServiceFacade.emulateCustomer(customerId, cartId);
	}

	public void emulateAfterLogin() throws AssistedServiceException
	{
		assistedServiceFacade.emulateAfterLogin();
	}

	public void verifySessionCustomer(final String customerId)
	{
		final CustomerData customer = customerFacade.getCurrentCustomer();
		Assert.assertNotNull("Session customer is null", customer);
		Assert.assertEquals("Unexpected session customer", customerId, customer.getUid());
	}

	public void verifySessionCartUser(final String customerId)
	{
		final CartModel sessionCart = cartService.getSessionCart();
		Assert.assertNotNull("Session cart is null", sessionCart);
		Assert.assertEquals("Unexpected cart user", customerId, sessionCart.getUser().getUid());
	}

	public void launchAssistedServiceMode()
	{
		assistedServiceFacade.launchAssistedServiceMode();
	}

	public void quitAssistedServiceMode() throws AssistedServiceException
	{
		assistedServiceFacade.quitAssistedServiceMode();
	}

	public void loginAgent(final String username, final String password) throws AssistedServiceException
	{
		assistedServiceFacade.loginAssistedServiceAgent(username, password);
	}

	public void logoutAgent() throws AssistedServiceException
	{
		assistedServiceFacade.logoutAssistedServiceAgent();
	}

	public List<CustomerData> suggestCustomers(final String username) throws AssistedServiceException
	{
		return assistedServiceFacade.getSuggestedCustomerList(username);
	}

	public void bindCustomerToCart(final String customerId) throws AssistedServiceException
	{
		assistedServiceFacade.bindCustomerToCart(customerId, null);
	}

	public void verifySessionCartContainsProduct(final String productCode)
	{
		final CartModel sessionCart = cartService.getSessionCart();
		for (final AbstractOrderEntryModel entry : sessionCart.getEntries())
		{
			if (productCode.equals(entry.getProduct().getCode()))
			{
				return;
			}
		}
		Assert.fail("The session cart does not contain the expected product " + productCode);
	}

	public void stopEmulation()
	{
		assistedServiceFacade.stopEmulateCustomer();
	}

	public CartModel getCart()
	{
		return cartService.getSessionCart();
	}

	public CartModel getCart(final String code)
	{
		final String queryString = "SELECT {p:" + CartModel.PK + "}" + "FROM {" + CartModel._TYPECODE + " AS p} " + "WHERE {"
				+ CartModel.CODE + "} = ?code";
		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString);
		query.addQueryParameter("code", code);
		return flexibleSearchService.<CartModel> search(query).getResult().get(0);
	}

	public Collection<CartData> getCustomerCarts(final String customerId)
	{
		Assert.assertNotSame("No any base site found to set it as current one" + baseSiteService.getAllBaseSites().size(),
				Integer.valueOf(0), Integer.valueOf(baseSiteService.getAllBaseSites().size()));
		final CustomerModel customer = (CustomerModel) userService.getUserForUID(customerId);
		return assistedServiceFacade.getCartListForCustomer(customer);
	}

	public CartData getFirstCustomerCart(final String customerId)
	{
		final CustomerModel customer = (CustomerModel) userService.getUserForUID(customerId);
		return assistedServiceFacade.getCartListForCustomer(customer).stream().findFirst().get();
	}

	public void setTestBaseSite()
	{
		baseSiteService.setCurrentBaseSite(baseSiteService.getAllBaseSites().toArray(new BaseSiteModel[1])[0], false);
	}

}
