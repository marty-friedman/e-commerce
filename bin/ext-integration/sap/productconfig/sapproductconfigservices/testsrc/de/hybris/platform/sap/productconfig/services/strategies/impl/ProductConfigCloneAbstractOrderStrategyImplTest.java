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
package de.hybris.platform.sap.productconfig.services.strategies.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.QuoteEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.order.strategies.ordercloning.impl.DefaultCloneAbstractOrderStrategy;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;


@UnitTest
public class ProductConfigCloneAbstractOrderStrategyImplTest
{
	public static class CartModelSubClass extends CartModel
	{
		//
	}

	ProductConfigCloneAbstractOrderStrategyImpl classUnderTest = new ProductConfigCloneAbstractOrderStrategyImpl();
	@Mock
	DefaultCloneAbstractOrderStrategy defaultCloneAbstractOrderStrategy;
	@Mock
	private ComposedTypeModel orderType;
	@Mock
	private ComposedTypeModel entryType;
	@Mock
	private CartModel original;
	@Mock
	private QuoteModel originalQuote;
	@Mock
	private AbstractOrderModel clonedAbstractOrder;
	@Mock
	private SessionAccessService sessionAccessService;
	@Mock
	private ProductConfigurationService productConfigurationService;
	@Mock
	private AbstractOrderEntryModel entry;
	@Mock
	private ProductModel product;
	@Mock
	private CartModelSubClass originalSubClass;

	private String code;
	private final Class abstractOrderClassResult = QuoteModel.class;
	private final Class abstractOrderEntryClassResult = QuoteEntryModel.class;
	private static final long cartKey = 123;
	private final PK cartEntryPk = PK.fromLong(cartKey);
	private static final String configId = "S1";
	private static final String productId = "PRODUCT_ID";

	@Before
	@SuppressFBWarnings("UWF_UNWRITTEN_FIELD")
	public void initialize()
	{
		MockitoAnnotations.initMocks(this);
		//Findbugs complains about this line although it is needed in our context
		Mockito.when(defaultCloneAbstractOrderStrategy.clone(orderType, entryType, original, code, abstractOrderClassResult,
				abstractOrderEntryClassResult)).thenReturn(clonedAbstractOrder);

		Mockito.when(original.getEntries()).thenReturn(Arrays.asList(entry));
		Mockito.when(originalQuote.getEntries()).thenReturn(Arrays.asList(entry));
		Mockito.when(entry.getPk()).thenReturn(cartEntryPk);
		Mockito.when(sessionAccessService.getConfigIdForCartEntry(String.valueOf(cartKey))).thenReturn(configId);
		Mockito.when(entry.getProduct()).thenReturn(product);
		Mockito.when(product.getCode()).thenReturn(productId);
		classUnderTest.setDefaultCloneAbstractOrderStrategy(defaultCloneAbstractOrderStrategy);
		classUnderTest.setSessionAccessService(sessionAccessService);
		classUnderTest.setProductConfigurationService(productConfigurationService);

	}

	@Test
	public void testDefaultCloneAbstractOrderStrategy()
	{

		classUnderTest.setDefaultCloneAbstractOrderStrategy(defaultCloneAbstractOrderStrategy);
		assertEquals(defaultCloneAbstractOrderStrategy, classUnderTest.getDefaultCloneAbstractOrderStrategy());
	}

	@Test
	public void testClone()
	{
		final AbstractOrderModel clone = classUnderTest.clone(orderType, entryType, original, code, abstractOrderClassResult,
				abstractOrderEntryClassResult);
		assertEquals(clonedAbstractOrder, clone);
	}

	@Test
	public void testIsCleanupNeeded()
	{
		assertTrue(classUnderTest.isCleanUpNeeded(original, abstractOrderClassResult));
	}

	@Test
	public void testIsCleanupNeededQuoteToQuote()
	{
		assertFalse(classUnderTest.isCleanUpNeeded(originalQuote, abstractOrderClassResult));
	}

	@Test
	public void testIsCleanupNeededCartToOrder()
	{
		assertFalse(classUnderTest.isCleanUpNeeded(original, OrderModel.class));
	}

	@Test
	public void testIsCleanupNeededSubClasses()
	{

		class QuoteModelSubClass extends QuoteModel
		{
			//
		}

		assertTrue(classUnderTest.isCleanUpNeeded(originalSubClass, QuoteModelSubClass.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCleanupArgumentNull()
	{
		classUnderTest.cleanUp(null);
	}

	@Test
	public void testCleanUp()
	{
		classUnderTest.cleanUp(original);
		Mockito.verify(original).getEntries();
	}

	@Test
	public void testSessionAccessService()
	{
		assertEquals(sessionAccessService, classUnderTest.getSessionAccessService());
	}

	@Test
	public void testProductConfigurationService()
	{
		assertEquals(productConfigurationService, classUnderTest.getProductConfigurationService());
	}

	@Test
	public void testCleanUpEntryReleaseSession()
	{
		classUnderTest.cleanUpEntry(entry);
		Mockito.verify(productConfigurationService).releaseSession(configId);
	}

	@Test
	public void testCleanUpEntryRemoveSessionArtifacts()
	{
		classUnderTest.cleanUpEntry(entry);
		Mockito.verify(sessionAccessService).removeSessionArtifactsForCartEntry(String.valueOf(cartKey), productId);
	}

	@Test
	public void testCleanUpEntryRemoveSessionArtifactsNullPk()
	{
		Mockito.when(entry.getPk()).thenReturn(null);
		classUnderTest.cleanUpEntry(entry);
		Mockito.verify(sessionAccessService, Mockito.never()).removeSessionArtifactsForCartEntry(String.valueOf(cartKey),
				productId);
	}

	@Test
	public void testCleanUpEntryNonConfigurable()
	{
		Mockito.when(sessionAccessService.getConfigIdForCartEntry(String.valueOf(cartKey))).thenReturn(null);
		classUnderTest.cleanUpEntry(entry);
		Mockito.verify(productConfigurationService, Mockito.never()).releaseSession(configId);
	}

	@Test
	public void testCleanUpEntryNonConfigurableNoRemovalSessionArtifacts()
	{
		Mockito.when(sessionAccessService.getConfigIdForCartEntry(String.valueOf(cartKey))).thenReturn(null);
		classUnderTest.cleanUpEntry(entry);
		Mockito.verify(sessionAccessService, Mockito.never()).removeSessionArtifactsForCartEntry(String.valueOf(cartKey),
				productId);
	}

	@Test
	public void testCloneEntries()
	{
		assertNotNull(classUnderTest.cloneEntries(entryType, original));
		Mockito.verify(productConfigurationService).releaseSession(configId);
	}

	@Test
	public void testCloneEntriesWrongSourceType()
	{
		assertNotNull(classUnderTest.cloneEntries(entryType, originalQuote));
		Mockito.verify(productConfigurationService, Mockito.never()).releaseSession(configId);
	}

	@Test
	public void testCleanUpNeededOnlySource()
	{
		assertTrue(classUnderTest.isCleanUpNeeded(original));
	}

	@Test
	public void testCleanUpNeededOnlySourceSubclass()
	{
		assertTrue(classUnderTest.isCleanUpNeeded(originalSubClass));
	}

	@Test
	public void testCleanUpNeededOnlyWrongSource()
	{
		assertFalse(classUnderTest.isCleanUpNeeded(originalQuote));
	}



}
