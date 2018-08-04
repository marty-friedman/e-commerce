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
package de.hybris.platform.sap.sapproductconfigsombol.integtests.order;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectException;
import de.hybris.platform.sap.core.bol.businessobject.CommunicationException;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.message.Message;
import de.hybris.platform.sap.core.common.util.LocaleUtil;
import de.hybris.platform.sap.core.test.property.PropertyAccess;
import de.hybris.platform.sap.core.test.property.PropertyAccessFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.external.CharacteristicValue;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Instance;
import de.hybris.platform.sap.productconfig.runtime.interf.external.PartOfRelation;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Basket;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Order;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemList;
import de.hybris.platform.sap.sapproductconfigsombol.integraationtests.base.JCoIntegrationTestBase;
import de.hybris.platform.sap.sapproductconfigsombol.integraationtests.base.SalesDocumentTestHelper;
import de.hybris.platform.sap.sapproductconfigsombol.transaction.item.businessobject.impl.CPQItem;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


@IntegrationTest
@SuppressWarnings("javadoc")
@ContextConfiguration(locations =
{ "classpath:sapproductconfigsombol-spring.xml", "classpath:test/MergeDirectives-test-spring.xml",
		"classpath:test/sapproductconfigsombol-test-spring.xml" })
public class OrderProductConfigurationTest extends JCoIntegrationTestBase
{

	public static PropertyAccess dataBag;

	private String prodIdCfg;
	private String prodCstic;
	private String prodCsticVal;
	private String prodCsticEnableMulti;
	private String prodCsticValEnableMulti;
	private String prodCsticMulti;
	private String prodCsticValMulti;
	private String orderIdCarryingConfigurations;
	private Order order;
	private Basket cart;
	private final SalesDocumentTestHelper salesDocumentTestHelper = SalesDocumentTestHelper.INSTANCE;
	private String soldTo;

	@BeforeClass
	public static void setUpClass() throws IOException
	{
		dataBag = new PropertyAccessFactory().createPropertyAccess();
		dataBag.setPropertyPathPrefix(
				JCoIntegrationTestBase.getCanonicalPathOfExtensionsapproductconfigsombolTest() + DATA_PATH_PREFIX);
		dataBag.addPropertyFile("erpsales.properties");
		dataBag.addPropertyFile("erpordercreate.properties");
		dataBag.addPropertyFile("erpcreatebaskettest.properties");

		dataBag.loadProperties();

		LocaleUtil.setLocale(Locale.US);

	}

	@Before
	public void init()
	{

		soldTo = dataBag.getStringProperty("soldto");
		prodIdCfg = dataBag.getStringProperty("order.prodCfg");
		prodCstic = dataBag.getStringProperty("order.cfg.cstic");
		prodCsticVal = dataBag.getStringProperty("order.cfg.val");
		prodCsticEnableMulti = dataBag.getStringProperty("order.cfg.csticEnableMulti");
		prodCsticValEnableMulti = dataBag.getStringProperty("order.cfg.valEnableMulti");
		prodCsticMulti = dataBag.getStringProperty("order.cfg.csticMulti");
		prodCsticValMulti = dataBag.getStringProperty("order.cfg.valMulti");
		orderIdCarryingConfigurations = dataBag.getStringProperty("order.cfg.orderId");

		initializeOrder();

	}

	@Test
	public void testCreateOrderMultilevel() throws BusinessObjectException
	{



		salesDocumentTestHelper.prepareOrder(soldTo, order);


		printHeader("Multilevel test");

		salesDocumentTestHelper.addItemTo(order, prodIdCfg, "1", "990");
		order.update();
		order.read();


		//now add configuration
		final ConfigModel configModel = createCFGModel(prodCsticEnableMulti, prodCsticValEnableMulti);

		System.out.println("Setting sub instance values: " + prodCsticMulti + ", " + prodCsticValMulti);
		final InstanceModel root = configModel.getRootInstance();
		final InstanceModel subInstance = new InstanceModelImpl();
		subInstance.setId("2");
		subInstance.setPosition("0010");
		final CsticModel subInstanceCstic = new CsticModelImpl();
		subInstanceCstic.setName(prodCsticMulti);
		final CsticValueModel subInstanceValue = new CsticValueModelImpl();
		subInstanceValue.setName(prodCsticValMulti);
		subInstanceCstic.setAssignedValues(Arrays.asList(subInstanceValue));
		subInstance.setCstics(Arrays.asList(subInstanceCstic));
		root.setSubInstances(Arrays.asList(subInstance));

		final ItemList itemList = order.getItemList();
		assertTrue(itemList.size() >= 1);
		final CPQItem item = (CPQItem) itemList.get(0);
		item.setProductConfiguration(configModel);

		System.out.println("Perform update in back end");
		order.update();
		order.read();

		final int size = order.getMessageList().size();
		printMessages();
		assertTrue(size >= 1);
		System.out.println();
		System.out.println("We sent sub instance characteristic successfully");



		//order.saveOrderAndCommit();
		//order.read(true);
		//System.out.println("ID in backend: " + order.getTechKey());



	}

	public void testCreateOrder() throws BusinessObjectException
	{


		salesDocumentTestHelper.prepareCart(soldTo, cart);

		printHeader("Single level price change test");

		salesDocumentTestHelper.addItemTo(cart, prodIdCfg, "1", "990");
		cart.update();
		cart.read();

		final BigDecimal initialPrice = getPrice(cart);


		System.out.println("Initial price for " + prodIdCfg + " is " + initialPrice);

		//now add configuration
		final ConfigModel configModel = createCFGModel(prodCstic, prodCsticVal);
		final ItemList itemList = cart.getItemList();
		assertEquals(2, itemList.size());
		final CPQItem item = (CPQItem) itemList.get(0);
		item.setProductConfiguration(configModel);
		item.setProductConfigurationDirty(true);

		System.out.println("Perform update in back end");
		cart.update();
		cart.read();

		final BigDecimal updatedPrice = getPrice(cart);
		System.out.println("New price for " + prodIdCfg + " is " + updatedPrice);
		assertTrue(updatedPrice.compareTo(initialPrice) > 0);
		System.out.println();
		System.out.println("Price increased->Sending of configuration was successfull");


		//		order.saveOrderAndCommit();
		//		order.read(true);
		//		System.out.println("ID in backend: " + order.getTechKey());
		//		printLine();


	}

	@Test
	public void testCreateOrderWithInvalidChars() throws BusinessObjectException
	{



		salesDocumentTestHelper.prepareOrder(soldTo, order);


		final String headerText = "Test: Invalid characteristics";

		printHeader(headerText);

		salesDocumentTestHelper.addItemTo(order, prodIdCfg, "1", "990");
		order.update();
		order.read();

		//now add configuration with a non existing value

		final ConfigModel configModel = createCFGModel(prodCstic, "X");
		final ItemList itemList = order.getItemList();
		assertEquals(2, itemList.size());
		final CPQItem item = (CPQItem) itemList.get(0);
		item.setProductConfiguration(configModel);

		System.out.println("Perform update in back end with invalid cstic value");
		order.update();
		order.read();
		//we expect errors, due to errors and incompleteness

		printMessages();
		assertTrue(order.getMessageList().size() > 1);
	}

	@Test
	public void testIncompleteness() throws BusinessObjectException
	{



		salesDocumentTestHelper.prepareOrder(soldTo, order);


		final String headerText = "Test: Incompleteness";

		printHeader(headerText);

		//the default configuration is not complete
		salesDocumentTestHelper.addItemTo(order, prodIdCfg, "1", "990");
		order.update();
		order.read();

		printMessages();
		assertTrue(order.getMessageList().size() == 1);
		assertTrue(order.getMessageList().get(0).getType() == Message.ERROR);

	}

	@Test
	public void testOrderReadWithConfigurations() throws CommunicationException
	{
		printHeader("Read order with configurable items");

		System.out.println("Order number: " + orderIdCarryingConfigurations);
		System.out.println();

		order.setTechKey(new TechKey(orderIdCarryingConfigurations));
		order.read();

		//now check configurations. We expect one attached to the first item and one to the third
		//in total, order is supposed to have 5 items
		final ItemList items = order.getItemList();
		assertEquals(5, items.size());

		final CPQItem firstConfigurableItem = (CPQItem) items.get(0);
		assertTrue(firstConfigurableItem.isConfigurable());
		final Configuration firstExternalConfiguration = firstConfigurableItem.getExternalConfiguration();
		assertNotNull(firstExternalConfiguration);
		checkCstics(firstExternalConfiguration);

		final CPQItem nonConfigurableItem = (CPQItem) items.get(1);
		assertFalse(nonConfigurableItem.isConfigurable());
		assertNull(nonConfigurableItem.getExternalConfiguration());

		final CPQItem secondConfigurableItem = (CPQItem) items.get(2);
		assertTrue(secondConfigurableItem.isConfigurable());
		final Configuration secondExternalConfiguration = secondConfigurableItem.getExternalConfiguration();
		assertNotNull(secondExternalConfiguration);
		checkSubInstances(secondExternalConfiguration);
	}



	private void checkSubInstances(final Configuration secondExternalConfiguration)
	{
		System.out.println();
		final Instance rootInstance = secondExternalConfiguration.getRootInstance();
		assertNotNull(rootInstance);
		System.out.println("Root instance of second model: " + rootInstance.getObjectKey());
		System.out.println("Root instance description: " + rootInstance.getObjectText());

		final List<Instance> instances = secondExternalConfiguration.getInstances();
		assertEquals(3, instances.size());
		final Instance subInstance = instances.get(1);
		final String classType = subInstance.getClassType();
		assertNotNull(classType);
		final String id = subInstance.getId();
		assertNotNull(id);
		final String objectType = subInstance.getObjectType();
		assertNotNull(objectType);
		final String quantity = subInstance.getQuantity();
		assertNotNull(quantity);
		final String quantityUnit = subInstance.getQuantityUnit();
		assertNotNull(quantityUnit);
		System.out.println("Attributes of sub instance:");
		System.out.println("ID: \t\t\t\t" + id);
		System.out.println("Object type: \t\t\t" + objectType);
		System.out.println("Quantity in BOM: \t\t" + quantity.trim());
		System.out.println("Quantity unit in BOM: \t\t" + quantityUnit);

		final List<PartOfRelation> partOfRelations = secondExternalConfiguration.getPartOfRelations();
		assertEquals(2, partOfRelations.size());
		final PartOfRelation partOfRelation = partOfRelations.get(0);
		final String parentInstId = partOfRelation.getParentInstId();
		assertNotNull(parentInstId);
		final String posNr = partOfRelation.getPosNr();
		assertNotNull(posNr);
		System.out.println("Attributes of partOf:");
		System.out.println("Parent ID: \t\t\t" + parentInstId);
		System.out.println("Position Number in BOM: \t" + posNr);


	}

	private void checkCstics(final Configuration firstExternalConfiguration)
	{
		final Instance rootInstance = firstExternalConfiguration.getRootInstance();
		assertNotNull(rootInstance);
		System.out.println("Root instance of first model: " + rootInstance.getObjectKey());
		System.out.println("Root instance description: " + rootInstance.getObjectText());
		final List<CharacteristicValue> characteristicValues = firstExternalConfiguration.getCharacteristicValues();
		assertEquals(5, characteristicValues.size());
		final CharacteristicValue cstic = characteristicValues.get(0);
		final String characteristic = cstic.getCharacteristic();
		assertNotNull(characteristic);
		System.out.println("Attributes of first characteristic value: ");
		final String characteristicText = cstic.getCharacteristicText();
		assertNotNull(characteristicText);
		assertFalse(characteristic.equals(characteristicText));
		System.out.println("Cstic name: \t\t\t" + characteristic);
		System.out.println("Cstic name (lang.dep): \t\t" + characteristicText);
		System.out.println("Value: \t\t\t\t" + cstic.getValue());
		System.out.println("Value (lang.dep}: \t\t" + cstic.getValueText());
		System.out.println("Author: \t\t\t" + cstic.getAuthor());

	}

	/**
	 *
	 */
	private void printMessages()
	{
		for (final Message message : order.getMessageList())
		{
			System.out.println(message);
		}
	}

	/**
	 *
	 */
	private void printHeader(final String headerText)
	{
		System.out.println();
		System.out.println();
		System.out.println(headerText);
		System.out.println();
	}


	@After
	public void printLine()
	{
		System.out.println("----------------------------------------------------------------------------");
	}


	private ConfigModel createCFGModel(final String csticRoot, final String csticValRoot)
	{
		final ConfigModel configModel = new ConfigModelImpl();
		final InstanceModel rootInstance = new InstanceModelImpl();
		final CsticModel cstic = new CsticModelImpl();
		cstic.setName(csticRoot);
		final CsticValueModel value = new CsticValueModelImpl();
		value.setName(csticValRoot);
		cstic.setAssignedValues(Arrays.asList(value));
		rootInstance.setCstics(Arrays.asList(cstic));
		rootInstance.setId("1");
		configModel.setRootInstance(rootInstance);
		System.out.println("Creating model with one instance and values: " + csticRoot + ", " + csticValRoot);
		return configModel;
	}


	private BigDecimal getPrice(final SalesDocument order2)
	{

		return order2.getItemList().get(0).getNetValueWOFreight();
	}

	/**
		 *
		 */
	private void initializeOrder()
	{
		order = genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BO_ORDER);
		order.clearMessages();
		cart = genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BO_CART);
		cart.clearMessages();
	}

}
