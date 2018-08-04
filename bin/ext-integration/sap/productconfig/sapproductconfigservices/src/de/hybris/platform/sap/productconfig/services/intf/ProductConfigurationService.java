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
package de.hybris.platform.sap.productconfig.services.intf;

import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.data.CartEntryConfigurationAttributes;

import java.util.Date;

import org.apache.log4j.Logger;


/**
 * ProductConfigurationService provides access to the configuration engine implementation.
 *
 */
public interface ProductConfigurationService
{

	/**
	 * Based on the hybris product code, provided via the <code>KBKey.productCode</code>, the configuration engine will
	 * provide a default configuration for the requested product.
	 *
	 * @param kbKey
	 *           The product code for the configurable product
	 * @return The configurable product with default configuration
	 */
	ConfigModel createDefaultConfiguration(final KBKey kbKey);

	/**
	 * Based on the hybris product code, the configuration engine will provide a configuration for the requested product
	 * variant.
	 *
	 * @param baseProductCode
	 *           The product code for the configurable base product
	 * @param variantProductCode
	 *           The product code for the specific product variant
	 * @return The configurable product with default configuration
	 */
	ConfigModel createConfigurationForVariant(final String baseProductCode, final String variantProductCode);

	/**
	 * Update the configuration model within the configuration engine.
	 *
	 * @param model
	 *           Updated model
	 */
	void updateConfiguration(final ConfigModel model);

	/**
	 * Retrieve the actual configuration model for the requested <code>configId</code> in the <code>ConfigModel</code>
	 * format.
	 *
	 * @param configId
	 *           Unique configuration ID
	 * @return The actual configuration
	 */
	ConfigModel retrieveConfigurationModel(String configId);

	/**
	 * Retrieve the actual configuration model for the requested <code>configId</code> in a <i>XML</i> format.
	 *
	 * @param configId
	 *           Unique configuration ID
	 * @return The actual configuration as XML string
	 */
	String retrieveExternalConfiguration(final String configId);

	/**
	 * Creates a configuration from the external string representation (which contains the configuration in XML format)
	 *
	 * @param externalConfiguration
	 *           Configuration as XML string
	 * @param kbKey
	 *           Key attributes needed to create a model
	 * @return Configuration model
	 */
	ConfigModel createConfigurationFromExternal(final KBKey kbKey, String externalConfiguration);

	/**
	 * Creates a configuration from the external string representation (which contains the configuration in XML format)
	 * and links it immediately with the given cart entry key
	 *
	 * @param externalConfiguration
	 *           Configuration as XML string
	 *
	 * @param kbKey
	 *           Key attributes needed to create a model
	 *
	 * @param cartEntryKey
	 *           cartEntryKey this config belongs to
	 *
	 * @return Configuration model
	 */
	default ConfigModel createConfigurationFromExternal(final KBKey kbKey, final String externalConfiguration,
			final String cartEntryKey)
	{
		return createConfigurationFromExternal(kbKey, externalConfiguration);
	}

	/**
	 * /** Create a <code>ConfigModel</code> based on a <code>Configuration</code> for the provided product code.
	 *
	 * @param extConfig
	 *           Configuration in a data structure
	 * @return Configuration model
	 */
	ConfigModel createConfigurationFromExternalSource(final Configuration extConfig);

	/**
	 * Releases the configuration sessions identified by the provided ID and all associated resources. Accessing the
	 * session afterwards is not possible anymore.
	 *
	 * @param configId
	 *           session id
	 */
	void releaseSession(String configId);

	/**
	 * Releases the configuration sessions identified by the provided ID and all associated resources. Accessing the
	 * session afterwards is not possible anymore.
	 *
	 * @param configId
	 *           session id
	 * @param keepModel
	 *           signifies whether config model should be kept despite releasing session
	 */
	default void releaseSession(final String configId, final boolean keepModel)
	{
		if (keepModel)
		{
			Logger.getLogger(ProductConfigurationService.class).warn(
					"Default implementation of releaseSession always deletes config model");
		}
		releaseSession(configId);
	}

	/**
	 * Calculates configuration relevant attributes at cart entry level
	 *
	 * @param model
	 *           Cart Entry
	 * @return attributes relevant for configuration
	 * @deprecated since 6.6 use
	 *             {@link ProductConfigurationPricingOrderIntegrationService#calculateCartEntryConfigurationAttributes(AbstractOrderEntryModel)}
	 *             instead
	 */
	@SuppressWarnings("squid:S1133")
	@Deprecated
	CartEntryConfigurationAttributes calculateCartEntryConfigurationAttributes(AbstractOrderEntryModel model);

	/**
	 * Ensures that configuration is available in session
	 *
	 * @param cartEntryKey
	 *           Key of cart entry, derived from {@link PK}
	 * @param productCode
	 *           Product ID
	 * @param externalConfiguration
	 *           External configuration as XML
	 * @return configuration model
	 * @deprecated since 6.6 use
	 *             {@link ProductConfigurationPricingOrderIntegrationService#ensureConfigurationInSession(String, String, String)}
	 *             instead
	 */
	@SuppressWarnings("squid:S1133")
	@Deprecated
	ConfigModel ensureConfigurationInSession(String cartEntryKey, String productCode, String externalConfiguration);

	/**
	 * Calculates configuration relevant attributes at cart entry level
	 *
	 * @param cartEntryKey
	 *           Key of cart entry, derived from {@link PK}
	 * @param productCode
	 *           Product ID
	 * @param externalConfiguration
	 *           External configuration as XML
	 * @return attributes relevant for configuration
	 * @deprecated since 6.6 use
	 *             {@link ProductConfigurationPricingOrderIntegrationService#calculateCartEntryConfigurationAttributes(String, String, String)}
	 *             instead
	 */
	@SuppressWarnings("squid:S1133")
	@Deprecated
	CartEntryConfigurationAttributes calculateCartEntryConfigurationAttributes(String cartEntryKey, String productCode,
			String externalConfiguration);

	/**
	 * Get the number of errors (conflict, not filled mandatory fields), as it is set at the cart item
	 *
	 * @param configId
	 *           id of the configuration
	 * @return Total number of errors
	 */
	int calculateNumberOfIncompleteCsticsAndSolvableConflicts(final String configId);

	/**
	 * Updates cart entry's base price from configuration model if a price is available in configuration model. ConfigId
	 * has to be present in current session for given cart entry to retrieve configuration model. The caller hat to take
	 * care for triggering recalculate of cart afterwards.
	 *
	 * @param entry
	 *           cart entry
	 * @return true if cart entry has been updated
	 * @deprecated since 6.6 use
	 *             {@link ProductConfigurationPricingOrderIntegrationService#updateCartEntryBasePrice(AbstractOrderEntryModel)}
	 *             instead
	 *
	 */
	@SuppressWarnings("squid:S1133")
	@Deprecated
	boolean updateCartEntryBasePrice(final AbstractOrderEntryModel entry);

	/**
	 * Updates cart entry's external configuration from configuration model
	 *
	 * @param parameters
	 *           parameters for cart
	 * @param entry
	 *           cart entry
	 * @return true if cart entry has been updated
	 * @deprecated since 6.6 use
	 *             {@link ProductConfigurationPricingOrderIntegrationService#updateCartEntryExternalConfiguration(CommerceCartParameter, AbstractOrderEntryModel)}
	 *             instead
	 */
	@SuppressWarnings("squid:S1133")
	@Deprecated
	boolean updateCartEntryExternalConfiguration(final CommerceCartParameter parameters, final AbstractOrderEntryModel entry);

	/**
	 * Updates cart entry's external configuration and creates configuration in current session from external string
	 * representation (which contains the configuration in XML format)
	 *
	 * @param externalConfiguration
	 *           Configuration as XML string
	 * @param entry
	 *           cart entry
	 * @return true if cart entry has been updated
	 * @deprecated since 6.6 use
	 *             {@link ProductConfigurationPricingOrderIntegrationService#updateCartEntryExternalConfiguration(String, AbstractOrderEntryModel)}
	 *             instead
	 */
	@SuppressWarnings("squid:S1133")
	@Deprecated
	boolean updateCartEntryExternalConfiguration(final String externalConfiguration, final AbstractOrderEntryModel entry);

	/**
	 * Update the product of the cartItem, if the product is different to the current cart item product
	 *
	 * @param entry
	 *           Entry to change, if necessary
	 * @param product
	 *           cart item product
	 * @param configId
	 *           ID of the current configuration
	 * @return true if the entry was updated
	 * @deprecated since 6.6 use
	 *             {@link ProductConfigurationPricingOrderIntegrationService#updateCartEntryProduct(AbstractOrderEntryModel, ProductModel, String)}
	 *             instead
	 */
	@SuppressWarnings("squid:S1133")
	@Deprecated
	boolean updateCartEntryProduct(final AbstractOrderEntryModel entry, final ProductModel product, final String configId);

	/**
	 * Fill the summary map at the order entry with configuration status information
	 *
	 * @param entry
	 *           Entry to be enhanced with additional information
	 * @deprecated since 6.6 use
	 *             {@link ProductConfigurationPricingOrderIntegrationService#fillSummaryMap(AbstractOrderEntryModel)}
	 *             instead
	 */
	@SuppressWarnings("squid:S1133")
	@Deprecated
	void fillSummaryMap(final AbstractOrderEntryModel entry);

	/**
	 * Checks whether a kb version exists for a given product and date
	 *
	 * @param productCode
	 *           product code
	 * @param kbDate
	 *           date of the knowledgebase
	 * @return true if KB version for the date exists
	 */
	boolean hasKbForDate(final String productCode, final Date kbDate);

	/**
	 * Checks whether kb version specified in the external config still exists
	 *
	 * @param kbKey
	 *           knowledgebase key which is used to extract product code
	 * @param externalConfig
	 *           external configuration
	 * @return true if KB version specified in the external config exists
	 */
	boolean hasKbForVersion(KBKey kbKey, String externalConfig);

	/**
	 * Returns the total number of issues (number of solvable conflicts + number of incomplete cstics)
	 *
	 * @param configModel
	 *           configuration model
	 * @return total number of issues
	 */
	int getTotalNumberOfIssues(final ConfigModel configModel);
}
