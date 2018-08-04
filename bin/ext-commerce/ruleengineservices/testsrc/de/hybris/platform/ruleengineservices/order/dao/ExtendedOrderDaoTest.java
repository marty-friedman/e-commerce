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
package de.hybris.platform.ruleengineservices.order.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Test;


@IntegrationTest
public class ExtendedOrderDaoTest extends ServicelayerTransactionalTest
{
	private final static Logger LOG = Logger.getLogger(ExtendedOrderDaoTest.class);

	private static final String CODE = "testCode";

	@Resource(name = "extendedOrderDao")
	private ExtendedOrderDao extendedOrderDao;

	@Resource(name = "modelService")
	private ModelService modelService;


	@Test(expected = IllegalArgumentException.class)
	public void testFindOrderByNullCode()
	{
		extendedOrderDao.findOrderByCode(null);
	}

	@Test(expected = ModelNotFoundException.class)
	public void testFindOrderByEmptyCode()
	{
		extendedOrderDao.findOrderByCode("");
	}

	@Test(expected = ModelNotFoundException.class)
	public void testNoOrdersByCode()
	{
		extendedOrderDao.findOrderByCode("nonexistent");
	}

	@Test
	public void testFindOrderByCode()
	{
		createOrder(CODE);
		assertTrue(extendedOrderDao.findOrderByCode(CODE) instanceof OrderModel);
		assertEquals(CODE, extendedOrderDao.findOrderByCode(CODE).getCode());
	}

	@Test
	public void testFindCartByCode()
	{
		createCart(CODE);
		assertTrue(extendedOrderDao.findOrderByCode(CODE) instanceof CartModel);
		assertEquals(CODE, extendedOrderDao.findOrderByCode(CODE).getCode());
	}


	private void createOrder(final String code)
	{
		final OrderModel order = new OrderModel();
		order.setCode(code);
		order.setCurrency(createCurrency());
		order.setDate(new Date());
		final UserModel user = new UserModel();
		user.setUid("myUID");
		order.setUser(user);
		modelService.save(order);
	}

	private void createCart(final String code)
	{
		final CartModel cart = new CartModel();
		cart.setCode(code);
		cart.setCurrency(createCurrency());
		cart.setDate(new Date());
		final UserModel user = new UserModel();
		user.setUid("myUID");
		cart.setUser(user);
		modelService.save(cart);
	}

	private CurrencyModel createCurrency()
	{
		final CurrencyModel currency = modelService.create(CurrencyModel.class);
		currency.setActive(Boolean.TRUE);
		currency.setIsocode("MCURR");
		currency.setName("myCurrency");
		currency.setSymbol("mc");
		currency.setConversion(Double.valueOf(1.3));
		modelService.save(currency);
		return currency;
	}

}
