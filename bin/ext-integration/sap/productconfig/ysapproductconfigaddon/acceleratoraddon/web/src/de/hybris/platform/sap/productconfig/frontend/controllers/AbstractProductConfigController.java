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
package de.hybris.platform.sap.productconfig.frontend.controllers;

import de.hybris.platform.acceleratorservices.data.RequestContextData;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.acceleratorstorefrontcommons.forms.ReviewForm;
import de.hybris.platform.acceleratorstorefrontcommons.util.MetaSanitizerUtil;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ImageDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.productconfig.facades.ConfigurationCartIntegrationFacade;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.ConfigurationFacade;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.KBKeyData;
import de.hybris.platform.sap.productconfig.facades.SessionAccessFacade;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.facades.UiType;
import de.hybris.platform.sap.productconfig.facades.tracking.UiTrackingRecorder;
import de.hybris.platform.sap.productconfig.frontend.UiStatus;
import de.hybris.platform.sap.productconfig.frontend.breadcrumb.ProductConfigureBreadcrumbBuilder;
import de.hybris.platform.sap.productconfig.frontend.constants.SapproductconfigfrontendWebConstants;
import de.hybris.platform.sap.productconfig.frontend.util.ConfigErrorHandler;
import de.hybris.platform.sap.productconfig.frontend.util.impl.UiDataStats;
import de.hybris.platform.sap.productconfig.frontend.util.impl.UiStateHandler;
import de.hybris.platform.sap.productconfig.frontend.util.impl.UiStatusSync;
import de.hybris.platform.sap.productconfig.frontend.validator.ConflictChecker;
import de.hybris.platform.servicelayer.exceptions.BusinessException;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;


/**
 * Abstract base class for all CPQ UI controllers.
 */
public abstract class AbstractProductConfigController extends AbstractPageController
{
	protected static final String CMS_PC_PAGE_TYPE = "productConfigPage";
	protected static final String CMS_PC_PAGE_ID = "productConfig";
	protected static final String CMS_PAGE_TYPE = "pageType";
	private static final Logger LOGGER = Logger.getLogger(AbstractProductConfigController.class.getName());
	private static final String LOG_CONFIG_DATA = "configuration data with [CONFIG_ID: '";
	private static final String[] ALLOWED_FIELDS_CONFIG_DATA = new String[]
	{ "kbKey.productCode", "configId", "selectedGroup", "cartItemPK", "autoExpand", "focusId", "groupIdToDisplay", "quantity",
			"groupIdToDisplay", "groupToDisplay.groupIdPath", "groupToDisplay.path", "groupIdToToggle", "groupIdToToggleInSpecTree",
			"forceExpand", "hideImageGallery", "cpqAction", "groupToDisplay.path", "groups*" };

	@Resource(name = "sapProductConfigFacade")
	private ConfigurationFacade configFacade;
	@Resource(name = "sapProductConfigCartIntegrationFacade")
	private ConfigurationCartIntegrationFacade configCartFacade;
	@Resource(name = "sapProductConfigSessionAccessFacade")
	private SessionAccessFacade sessionAccessFacade;
	@Resource(name = "sapProductConfigValidator")
	private Validator productConfigurationValidator;
	@Resource(name = "sapProductConfigConflictChecker")
	private ConflictChecker productConfigurationConflictChecker;
	@Resource(name = "productVariantFacade")
	private ProductFacade productFacade;
	@Resource(name = "productService")
	private ProductService productService;
	@Resource(name = "sapProductConfigBreadcrumbBuilder")
	private ProductConfigureBreadcrumbBuilder breadcrumbBuilder;
	@Resource(name = "sapProductConfigUiTrackingRecorder")
	private UiTrackingRecorder uiRecorder;
	@Resource(name = "sapProductConfigErrorHandler")
	private ConfigErrorHandler configurationErrorHandler;
	@Resource(name = "sapProductConfigUiStateHandler")
	private UiStateHandler uiStateHandler;
	@Resource(name = "sapProductConfigUiStatusSync")
	private UiStatusSync uiStatusSync;

	@InitBinder(SapproductconfigfrontendWebConstants.CONFIG_ATTRIBUTE)
	protected void initBinderConfigData(final WebDataBinder binder)
	{
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug("INIT Binder at: " + System.currentTimeMillis());
		}
		binder.setValidator(getProductConfigurationValidator());
		binder.setAllowedFields(ALLOWED_FIELDS_CONFIG_DATA);
	}

	protected BindingResult getBindingResultForConfigAndSaveUiStatus(final ConfigurationData configData, final UiStatus uiStatus)
	{
		final BindingResult errors = getBindingResultForConfiguration(configData, uiStatus);

		if (uiStatus.getUserInputToRemember() != null)
		{
			getSessionAccessFacade().setUiStatusForProduct(configData.getKbKey().getProductCode(), uiStatus);
		}

		return errors;
	}

	protected BindingResult getBindingResultForConfiguration(final ConfigurationData configData, final UiStatus uiStatus)
	{
		getUiStateHandler().resetGroupStatus(configData);
		BindingResult errors = new BeanPropertyBindingResult(configData, SapproductconfigfrontendWebConstants.CONFIG_ATTRIBUTE);
		// UI-Errors
		Map<String, FieldError> userInputToRestore = null;
		if (uiStatus != null)
		{
			userInputToRestore = uiStatus.getUserInputToRestore();
			final Map<String, FieldError> userInputToRemember = uiStatus.getUserInputToRemember();
			userInputToRestore = getUiStateHandler().mergeUiErrors(userInputToRestore, userInputToRemember);
			errors = getUiStateHandler().restoreValidationErrorsOnGetConfig(userInputToRestore, configData, errors);
		}

		productConfigurationConflictChecker.checkConflicts(configData, errors);
		if (configData.getCartItemPK() != null && !configData.getCartItemPK().isEmpty())
		{
			productConfigurationConflictChecker.checkMandatoryFields(configData, errors);
			logConfigurationCheckDeviation(errors, configData);
		}
		getProductConfigurationConflictChecker().checkCompletness(configData);
		getUiStateHandler().countNumberOfUiErrorsPerGroup(configData.getGroups());

		if (userInputToRestore != null)
		{
			final Map<String, FieldError> userInputToRemeber = getUiStateHandler().findCollapsedErrorCstics(userInputToRestore,
					configData);
			uiStatus.setUserInputToRemember(userInputToRemeber);
		}

		return errors;
	}

	/**
	 * The ConflictChecker checks only visible characteristics for consistency and completeness as only those
	 * characteristics might be changed by the user.<br>
	 * <br>
	 * If the model is modeled in a way that a conflict appears for an invisible characteristic or an invisible
	 * characteristic is mandatory but not filled this would not be identified by those checks but the overall
	 * configuration status is not consistent/complete. This leads to a situation where the configuration cannot be sent
	 * to the backend system.<br>
	 * <br>
	 * In this case the modeler needs to improve the model to avoid such situations. The user cannot do anything about
	 * this so this is only logged as an error as a hint for the modeler.
	 */
	protected void logConfigurationCheckDeviation(final BindingResult errors, final ConfigurationData configData)
	{
		if (!(configData.isComplete() && configData.isConsistent()) && !errors.hasErrors())
		{
			// Configuration is incomplete/inconsistent: check whether this is reflected in the BindingResult
			// BindingResult does not contain errors -> log deviation
			LOGGER.warn("HINT: Configuration model of product "
					+ configData.getKbKey().getProductCode()
					+ " needs to be improved! Configuration status is [complete="
					+ configData.isComplete()
					+ "; consistent="
					+ configData.isConsistent()
					+ "] but the ConflictChecker signals no errors, i.e. the inconsistency/incompleteness exists at characteristics invisible for the user. Thus the user has no information what went wrong.");
		}
	}

	protected void setCartItemPk(final ConfigurationData configData)
	{
		final String cartItemKey = getSessionAccessFacade().getCartEntryForProduct(configData.getKbKey().getProductCode());
		if (cartItemKey != null)
		{
			final boolean isItemInCart = configCartFacade.isItemInCartByKey(cartItemKey);
			if (!isItemInCart)
			{
				getSessionAccessFacade().removeCartEntryForProduct(configData.getKbKey().getProductCode());
			}
			else
			{
				configData.setCartItemPK(cartItemKey);
			}
		}
	}

	protected Integer getCartEntryNumber(final KBKeyData kbKey)
	{
		return getCartEntryNumber(kbKey.getProductCode());
	}

	protected Integer getCartEntryNumber(final String productCode)
	{
		final String cartItemKey = getSessionAccessFacade().getCartEntryForProduct(productCode);
		if (cartItemKey != null)
		{
			final PK cartItemPK = PK.parse(cartItemKey);
			final AbstractOrderEntryModel item = configCartFacade.findItemInCartByPK(cartItemPK);
			return item != null ? item.getEntryNumber() : null;
		}
		return null;
	}

	protected Integer getCartEntryNumber(final AbstractOrderData orderData, final String configId)
			throws CommerceCartModificationException
	{
		final String cartItemKey = getSessionAccessFacade().getCartEntryForConfigId(configId);
		if (cartItemKey != null)
		{
			OrderEntryData entryData;
			try
			{
				entryData = getOrderEntry(cartItemKey, orderData);
			}
			catch (final BusinessException bex)
			{
				throw new CommerceCartModificationException("Could not find cart entry!", bex);
			}
			return entryData != null ? entryData.getEntryNumber() : null;
		}
		return null;
	}

	/**
	 * Creates a new configuration session. Either returns a default configuration or creates a configuration from the
	 * external configuration attached to a cart entry. <br>
	 * Stores a new UIStatus based on the new configuration in the session (per product).
	 *
	 * @return Null if no configuration could be created
	 */
	protected ConfigurationData loadNewConfiguration(final KBKeyData kbKey, final ProductData productData,
			final String cartItemHandle)
	{
		final ConfigurationData configData;
		if (cartItemHandle != null && StringUtils.isEmpty(productData.getBaseProduct()))
		{
			configData = configCartFacade.restoreConfiguration(kbKey, cartItemHandle);
			if (configData == null)
			{
				return null;
			}
		}
		else
		{
			configData = configFacade.getConfiguration(productData);
			kbKey.setProductCode(configData.getKbKey().getProductCode());
		}

		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug("Load new " + LOG_CONFIG_DATA + configData.getConfigId() + "']");
		}

		getUiStatusSync().setInitialStatus(configData);
		final UiStatus uiStatus = getUiStatusSync().extractUiStatusFromConfiguration(configData);
		getSessionAccessFacade().setUiStatusForProduct(configData.getKbKey().getProductCode(), uiStatus);
		return configData;
	}

	protected ConfigurationData reloadConfiguration(final KBKeyData kbKey, final UiStatus uiStatus)
	{
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug("Reload " + LOG_CONFIG_DATA + uiStatus.getConfigId() + "']");
		}
		final ConfigurationData configData = this.getConfigData(kbKey, uiStatus.getConfigId());
		getUiStatusSync().applyUiStatusToConfiguration(configData, uiStatus);
		getUiStateHandler().compileGroupForDisplay(configData, uiStatus);
		return configData;
	}

	protected ConfigurationData getConfigData(final KBKeyData kbKey, final String configId)
	{
		final ConfigurationData configContent = new ConfigurationData();
		configContent.setConfigId(configId);
		configContent.setKbKey(kbKey);
		final ConfigurationData configData = configFacade.getConfiguration(configContent);
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug("Retrieve " + LOG_CONFIG_DATA + configData.getConfigId() + "']");
		}
		return configData;
	}

	protected ProductData populateProductData(final String productCode, final Model model, final HttpServletRequest request)
	{
		handleRequestContext(request, productCode);
		updatePageTitle(productCode, model);
		final ProductData productData = populateProductDetailForDisplay(productCode, model);

		model.addAttribute(new ReviewForm());
		final String metaKeywords = MetaSanitizerUtil.sanitizeKeywords(productData.getKeywords());
		final String metaDescription = MetaSanitizerUtil.sanitizeDescription(productData.getDescription());
		setUpMetaData(model, metaKeywords, metaDescription);

		return productData;
	}

	protected void populateCMSAttributes(final Model model) throws CMSItemNotFoundException
	{
		model.addAttribute(CMS_PAGE_TYPE, getPageType());
		final AbstractPageModel configPage = getCmsPageService().getPageForId(getPageId());
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug("Using CMS page: '" + configPage.getName() + "' [ '" + configPage.getUid() + "'] with PageType '"
					+ getPageType() + "'");
		}
		storeCmsPageInModel(model, configPage);
	}

	protected String getPageId()
	{
		return CMS_PC_PAGE_ID;
	}

	protected String getPageType()
	{
		return CMS_PC_PAGE_TYPE;
	}

	protected ProductData populateProductDetailForDisplay(final String productCode, final Model model)
	{
		final ProductData productData = getProductDataForProductCode(productCode);
		populateProductData(productData, model);
		return productData;
	}

	protected ProductData getProductDataForProductCode(final String productCode)
	{
		return getProductFacade().getProductForCodeAndOptions(
				productCode,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.SUMMARY, ProductOption.DESCRIPTION,
						ProductOption.GALLERY, ProductOption.STOCK));
	}

	protected void populateProductData(final ProductData productData, final Model model)
	{
		model.addAttribute("galleryImages", getGalleryImages(productData));
		model.addAttribute(SapproductconfigfrontendWebConstants.PRODUCT_ATTRIBUTE, productData);
	}

	protected List<Map<String, ImageData>> getGalleryImages(final ProductData productData)
	{
		if (CollectionUtils.isEmpty(productData.getImages()))
		{
			return Collections.emptyList();
		}

		final List<Map<String, ImageData>> galleryImages = new ArrayList<>();
		final List<ImageData> images = new ArrayList<>();
		for (final ImageData image : productData.getImages())
		{
			if (ImageDataType.GALLERY.equals(image.getImageType()))
			{
				images.add(image);
			}
		}

		if (CollectionUtils.isNotEmpty(images))
		{
			Collections.sort(images, (image1, image2) -> image1.getGalleryIndex().compareTo(image2.getGalleryIndex()));
			int currentIndex = images.get(0).getGalleryIndex().intValue();
			Map<String, ImageData> formats = new HashMap<>();
			for (final ImageData image : images)
			{
				if (currentIndex != image.getGalleryIndex().intValue())
				{
					galleryImages.add(formats);
					formats = new HashMap<>();
					currentIndex = image.getGalleryIndex().intValue();
				}
				formats.put(image.getFormat(), image);
			}
			if (!formats.isEmpty())
			{
				galleryImages.add(formats);
			}
		}
		return galleryImages;
	}

	protected boolean isProductVariant(final ProductData productData)
	{
		return StringUtils.isNotEmpty(productData.getBaseProduct());
	}

	protected void ifProductVariant(final HttpServletRequest request, final Model model, final ProductData productData,
			final KBKeyData kbKey)
	{
		if (isProductVariant(productData))
		{
			cleanUpSessionAttribute(productData.getBaseProduct());
			populateProductData(productData.getBaseProduct(), model, request);
			kbKey.setProductCode(productData.getBaseProduct());
		}
	}

	protected void cleanUpSessionAttribute(final String baseProduct)
	{
		if (getSessionAccessFacade().getCartEntryForProduct(baseProduct) != null)
		{
			getSessionAccessFacade().removeCartEntryForProduct(baseProduct);
		}
	}

	protected KBKeyData createKBKeyForProduct(final ProductData productData)
	{
		final KBKeyData kbKey = new KBKeyData();
		kbKey.setProductCode(productData.getCode());
		return kbKey;
	}

	protected void removeNullCsticsFromGroup(final List<CsticData> dirtyList)
	{
		if (dirtyList == null)
		{
			return;
		}

		final List<CsticData> cleanList = new ArrayList<>(dirtyList.size());

		for (final CsticData data : dirtyList)
		{
			if (data.getName() != null && (data.getType() != UiType.READ_ONLY || data.isRetractTriggered()))
			{
				cleanList.add(data);
			}
		}

		dirtyList.clear();
		dirtyList.addAll(cleanList);
	}

	protected void removeNullCstics(final List<UiGroupData> groups)
	{
		if (groups == null)
		{
			return;
		}

		for (final UiGroupData group : groups)
		{
			removeNullCsticsFromGroup(group.getCstics());

			final List<UiGroupData> subGroups = group.getSubGroups();
			removeNullCstics(subGroups);
		}
	}

	protected void handleRequestContext(final HttpServletRequest request, final String productCode)
	{
		final RequestContextData requestContext = getRequestContextData(request);
		if (requestContext != null)
		{
			requestContext.setProduct(getProductService().getProductForCode(productCode));
		}
	}

	protected void logModelmetaData(final ConfigurationData configData)
	{
		if (LOGGER.isDebugEnabled())
		{
			final UiDataStats numbers = new UiDataStats();
			numbers.countCstics(configData.getGroups());

			LOGGER.debug("Modelstats of product '" + configData.getKbKey().getProductCode() + "' after Update: '" + numbers + "'");
		}
	}

	protected void logRequestMetaData(final ConfigurationData configData, final HttpServletRequest request)
	{
		if (LOGGER.isDebugEnabled())
		{
			final NumberFormat decFormat = DecimalFormat.getInstance(Locale.ENGLISH);
			LOGGER.debug("Update Configuration of product '" + configData.getKbKey().getProductCode() + "'");
			final StringBuilder sb = new StringBuilder().append("ContentLength=")
					.append(decFormat.format(request.getContentLength())).append("Bytes");
			if (request.getParameterMap() != null)
			{
				sb.append("; numberParams=");
				sb.append(decFormat.format(request.getParameterMap().size()));
			}
			else
			{
				sb.append("; parameterMap=null");
			}
			LOGGER.debug(sb.toString());
			final UiDataStats numbers = new UiDataStats();
			numbers.countCstics(configData.getGroups());

			LOGGER.debug(numbers);
		}
	}

	protected void updatePageTitle(final String productCode, final Model model)
	{
		storeContentPageTitleInModel(model, getPageTitleResolver().resolveProductPageTitle(productCode));
	}

	protected ConfigurationFacade getConfigFacade()
	{
		return configFacade;
	}

	protected ConfigurationCartIntegrationFacade getConfigCartFacade()
	{
		return configCartFacade;
	}

	protected SessionAccessFacade getSessionAccessFacade()
	{
		return sessionAccessFacade;
	}

	protected Validator getProductConfigurationValidator()
	{
		return productConfigurationValidator;
	}

	protected ConflictChecker getProductConfigurationConflictChecker()
	{
		return productConfigurationConflictChecker;
	}

	protected ProductFacade getProductFacade()
	{
		return productFacade;
	}

	protected ProductService getProductService()
	{
		return productService;
	}

	protected OrderEntryData getOrderEntry(final int entryNumber, final AbstractOrderData abstractOrder) throws BusinessException
	{
		final List<OrderEntryData> entries = abstractOrder.getEntries();
		if (entries == null)
		{
			throw new BusinessException("AbstractOrder is empty");
		}
		try
		{
			return entries.stream().filter(e -> e != null).filter(e -> e.getEntryNumber().intValue() == entryNumber).findAny().get();
		}
		catch (final NoSuchElementException e)
		{
			throw new BusinessException("AbstractOrder entry #" + entryNumber + " does not exist", e);
		}
	}

	protected OrderEntryData getOrderEntry(final String cartItemKey, final AbstractOrderData abstractOrder)
			throws BusinessException
	{
		final List<OrderEntryData> entries = abstractOrder.getEntries();
		if (entries == null)
		{
			throw new BusinessException("AbstractOrder is empty");
		}
		try
		{
			return entries.stream().filter(e -> e != null && e.getItemPK() != null).filter(e -> e.getItemPK().equals(cartItemKey))
					.findAny().get();
		}
		catch (final NoSuchElementException e)
		{
			throw new BusinessException("AbstractOrder entry with item pk " + cartItemKey + " does not exist", e);
		}
	}

	protected ProductConfigureBreadcrumbBuilder getBreadcrumbBuilder()
	{
		return breadcrumbBuilder;
	}

	/**
	 * @param configFacade
	 *           CPQ facade
	 */
	public void setConfigFacade(final ConfigurationFacade configFacade)
	{
		this.configFacade = configFacade;
	}

	/**
	 * @param configCartFacade
	 *           CPQ cart integration facade
	 */
	public void setConfigCartFacade(final ConfigurationCartIntegrationFacade configCartFacade)
	{
		this.configCartFacade = configCartFacade;
	}

	/**
	 * @param sessionAccessFacade
	 *           CPQ session cache access
	 */
	public void setSessionAccessFacade(final SessionAccessFacade sessionAccessFacade)
	{
		this.sessionAccessFacade = sessionAccessFacade;
	}

	/**
	 * @param productConfigurationValidator
	 *           CPQ validator
	 */
	public void setProductConfigurationValidator(final Validator productConfigurationValidator)
	{
		this.productConfigurationValidator = productConfigurationValidator;
	}

	/**
	 * @param productConfigurationConflictChecker
	 *           status and UI error handling&checking
	 */
	public void setProductConfigurationConflictChecker(final ConflictChecker productConfigurationConflictChecker)
	{
		this.productConfigurationConflictChecker = productConfigurationConflictChecker;
	}

	/**
	 * @param productFacade
	 *           for accessing product master data
	 */
	public void setProductFacade(final ProductFacade productFacade)
	{
		this.productFacade = productFacade;
	}

	/**
	 * @param productService
	 *           for accessing product related service
	 */
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	/**
	 * @param productConfigurationBreadcrumbBuilder
	 *           for building UI breadcrumbs
	 */
	public void setBreadcrumbBuilder(final ProductConfigureBreadcrumbBuilder productConfigurationBreadcrumbBuilder)
	{
		this.breadcrumbBuilder = productConfigurationBreadcrumbBuilder;
	}


	protected UiTrackingRecorder getUiRecorder()
	{
		return uiRecorder;
	}

	/**
	 * @param uiRecorder
	 *           triggering CPQ tracking
	 */
	public void setUiRecorder(final UiTrackingRecorder uiRecorder)
	{
		this.uiRecorder = uiRecorder;
	}

	protected boolean isConfigRemoved(final String productCode)
	{
		final boolean isConfigRemoved = getSessionAccessFacade().getUiStatusForProduct(productCode) == null;

		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug("Is configuration removed: '" + isConfigRemoved + "'");
		}

		return isConfigRemoved;
	}

	/**
	 * @return configuration error handler
	 */
	public ConfigErrorHandler getConfigurationErrorHandler()
	{
		return configurationErrorHandler;
	}

	/**
	 * @param configurationErrorHandler
	 *           for handling configuration errors
	 *
	 */
	public void setConfigurationErrorHandler(final ConfigErrorHandler configurationErrorHandler)
	{
		this.configurationErrorHandler = configurationErrorHandler;
	}

	protected UiStateHandler getUiStateHandler()
	{
		return uiStateHandler;
	}

	/**
	 * @param uiStateHandler
	 *           UI state handler
	 */
	public void setUiStateHandler(final UiStateHandler uiStateHandler)
	{
		this.uiStateHandler = uiStateHandler;
	}

	protected UiStatusSync getUiStatusSync()
	{
		return uiStatusSync;
	}

	/**
	 * @param uiStatusSync
	 *           UI status sync
	 */
	public void setUiStatusSync(final UiStatusSync uiStatusSync)
	{
		this.uiStatusSync = uiStatusSync;
	}

}
