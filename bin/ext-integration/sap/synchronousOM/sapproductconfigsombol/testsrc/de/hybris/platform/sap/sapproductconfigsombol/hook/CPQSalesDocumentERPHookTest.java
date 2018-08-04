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
package de.hybris.platform.sap.sapproductconfigsombol.hook;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.impl.HeaderSalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.ItemListImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.order.businessobject.impl.OrderImpl;
import de.hybris.platform.sap.sapproductconfigsombol.transaction.item.businessobject.impl.CPQItem;
import de.hybris.platform.sap.sapproductconfigsombol.transaction.item.businessobject.impl.CPQItemSalesDoc;
import de.hybris.platform.sap.sapproductconfigsombol.transaction.salesdocument.backend.impl.erp.strategy.ProductConfigurationStrategyImplTest;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 *
 */
@UnitTest
public class CPQSalesDocumentERPHookTest
{

	SalesDocument salesDocument = null;
	CPQItem item;
	CPQSalesDocumentERPHook classUnderTest;

	/**
	 *
	 */
	@Before
	public void setUp()
	{
		classUnderTest = new CPQSalesDocumentERPHook();
		salesDocument = new OrderImpl();
		salesDocument.setHeader(new HeaderSalesDocument());
		final ItemListImpl itemList = new ItemListImpl();
		item = new CPQItemSalesDoc();
		item.setProductConfiguration(ProductConfigurationStrategyImplTest.getConfigModel(null));
		item.setConfigurable(true);
		item.setQuantity(new BigDecimal(2));
		itemList.add(item);
		salesDocument.setItemList(itemList);
	}

	/**
	 * Test method for {@link de.hybris.platform.sap.sapproductconfigsombol.hook.CPQSalesDocumentERPHook#determineConfigurableItems(de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument)}.
	 */
	@Test
	public void testDetermineConfigurableItems()
	{
		final List<String> itemHandleList = classUnderTest.determineConfigurableItems(salesDocument);
		assertNotNull(itemHandleList);
		assertEquals(1, itemHandleList.size());
	}

}
