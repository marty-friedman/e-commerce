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
package de.hybris.platform.sap.productconfig.facades;

import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;


/**
 * Facade containing for integration between the shopping cart and configurable products. <br>
 * Pure configuration related behavior is handled by the {@link ConfigurationFacade}.
 */
public interface ConfigurationCartIntegrationFacade
{

	/**
	 * Adds the current configuration to shopping cart. The configuration is attached to the shopping cart item as
	 * external configuration, which is an XML-String.
	 *
	 * @param configuration
	 *           configuration to add to the shopping cart
	 * @return key/handle to re-identify the item within the session
	 * @throws CommerceCartModificationException
	 *            in case the update of the cart failed
	 */
	String addConfigurationToCart(final ConfigurationData configuration) throws CommerceCartModificationException;

	/**
	 * Checks whether item is in cart
	 *
	 * @param key
	 *           /handle to re-identify the item within the session
	 * @return <code>true</code>, only if the item is in the cart
	 */
	boolean isItemInCartByKey(String key);

	/**
	 * Copies a configuration. The implementation can decide if a deep copy is needed; if not, the input ID is simply
	 * returned.
	 *
	 * @param configId
	 *           ID of existing configuration
	 * @return ID of new configuration if a deep copy was performed; input otherwise
	 * @deprecated since 6.3 use {@link ConfigurationCartIntegrationFacade#copyConfiguration(String, String)} instead
	 */
	@SuppressWarnings("squid:S1133")
	@Deprecated
	String copyConfiguration(String configId);

	/**
	 * Copies a configuration. The implementation can decide if a deep copy is needed; if not, the input ID is simply
	 * returned.
	 *
	 * @param configId
	 *           ID of existing configuration
	 * @param productCode
	 *           product code of configurable product to be copied
	 * @return ID of new configuration if a deep copy was performed; input otherwise
	 */
	String copyConfiguration(String configId, String productCode);

	/**
	 * Resets the configuration to the initial state
	 *
	 * @param configId
	 *           ID of existing configuration
	 */
	void resetConfiguration(String configId);


	/**
	 * Restores a configuration from a cart entry specified by its key. This is needed if there is no SSC session
	 * connected to the cart entry yet.
	 *
	 * @param kbKey
	 *           knowledgebase key
	 * @param cartEntryKey
	 *           cart entry key
	 * @return Configuration runtime object. Null if configuration could not be restored
	 */
	ConfigurationData restoreConfiguration(KBKeyData kbKey, String cartEntryKey);


	/**
	 * Searches the session cart for an entry specified by a primary key. In case nothing is found, null is returned.
	 *
	 * @param cartItemPk
	 *           Entry key
	 * @return Corresponding order entry model
	 */
	default AbstractOrderEntryModel findItemInCartByPK(final PK cartItemPk)
	{
		return null;
	}

}
