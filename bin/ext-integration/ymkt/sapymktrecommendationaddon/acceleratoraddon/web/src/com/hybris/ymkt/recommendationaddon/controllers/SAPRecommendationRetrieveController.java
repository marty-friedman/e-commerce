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
package com.hybris.ymkt.recommendationaddon.controllers;

import de.hybris.platform.acceleratorcms.component.slot.CMSPageSlotComponentService;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.commercefacades.product.data.ProductReferenceData;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import com.hybris.ymkt.recommendation.dao.ImpressionContext;
import com.hybris.ymkt.recommendation.dao.InteractionContext;
import com.hybris.ymkt.recommendation.dao.RecommendationContext;
import com.hybris.ymkt.recommendation.services.InteractionService;
import com.hybris.ymkt.recommendationaddon.facades.ProductRecommendationManagerFacade;
import com.hybris.ymkt.recommendation.model.CMSSAPRecommendationComponentModel;

/**
 * Controller for RecommendationList view
 */
@Controller("SAPRecommendationRetrieveController")
public class SAPRecommendationRetrieveController
{
	private static final Logger LOG = LoggerFactory.getLogger(SAPRecommendationRetrieveController.class);

	protected static final String VIEWNAME = "addon:/sapymktrecommendationaddon/cms/productrecommendationlist";

	@Resource(name = "cmsPageSlotComponentService")
	protected CMSPageSlotComponentService cmsPageSlotComponentService;

	@Resource(name = "interactionService")
	protected InteractionService interactionService;

	@Resource(name = "sapProductRecommendationManagerFacade")
	protected ProductRecommendationManagerFacade productRecommendationManagerFacade;

	@Autowired
	protected HttpServletRequest request;

	protected Optional<CMSSAPRecommendationComponentModel> getComponent(final String componentId)
	{
		final AbstractCMSComponentModel component = cmsPageSlotComponentService.getComponentForId(componentId);

		if (!(component instanceof CMSSAPRecommendationComponentModel))
		{
			LOG.warn("ComponentId={} is not a CMSSAPRecommendationComponentModel but was '{}'", componentId, component);
			return Optional.empty();
		}
		final CMSSAPRecommendationComponentModel cmsRecoModel = (CMSSAPRecommendationComponentModel) component;
		return Optional.of(cmsRecoModel);
	}

	/**
	 * Create a click-through interaction when a recommended product is clicked
	 *
	 * @param id
	 * @param componentId
	 *
	 */
	@RequestMapping(value = "/action/prodRecoInteraction/", method = RequestMethod.POST)
	@ResponseBody
	public void registerClickthrough(@RequestParam("id") final String id, @RequestParam("componentId") final String componentId)
	{	

		final Optional<CMSSAPRecommendationComponentModel> component = this.getComponent(componentId);
		if (!component.isPresent())
		{
			return;
		}

		final Optional<ProductModel> optProduct = this.productRecommendationManagerFacade.findProduct(id);

		if (!optProduct.isPresent())
		{
			LOG.error("Product='{}' not found using componentId '{}'", id, componentId);
			return;
		}
		final InteractionContext interactionContext = new InteractionContext();
		interactionContext.setProductId(id);
		interactionContext.setProductType(component.get().getLeadingitemdstype());
		interactionContext.setSourceObjectId(request.getSession().getId());
		interactionContext.setScenarioId(component.get().getRecotype());
		interactionService.saveClickthrough(interactionContext);
	}

	/**
	 * Creates an impression row in the table with the component's item count and ScenarioId
	 *
	 * @param itemCount
	 * @param componentId
	 */
	@RequestMapping(value = "/action/prodRecoImpression/", method = RequestMethod.POST)
	@ResponseBody
	public void registerProdRecoImpression(@RequestParam("itemCount") final int itemCount,
			@RequestParam("componentId") final String componentId)
	{
		saveProdRecoImpression(itemCount, componentId);
	}

	/**
	 * Retrieve the recommended products to be rendered in the UI
	 *
	 * @param id
	 * @param productCode
	 * @param model
	 * @return viewName
	 */
	@RequestMapping(value = "/action/recommendations/")
	public String retrieveRecommendations(@RequestParam("id") final String id,
			@RequestParam("productCode") final String productCode, @RequestParam("componentId") final String componentId,
			final Model model)
	{
		final Optional<CMSSAPRecommendationComponentModel> component = this.getComponent(componentId);
		if (!component.isPresent())
		{
			return VIEWNAME;
		}

		if (StringUtils.isEmpty(component.get().getRecotype()))
		{
			LOG.debug("Recommendation Model has to be specified.");
			return VIEWNAME;
		}

		final RecommendationContext context = new RecommendationContext();
		context.setLeadingProductId(productCode);
		context.setScenarioId(component.get().getRecotype());
		context.setIncludeCart(component.get().isIncludecart());
		context.setIncludeRecent(component.get().isIncluderecent());
		context.setLeadingItemDSType(component.get().getLeadingitemdstype());
		context.setLeadingItemType(component.get().getLeadingitemtype());
		context.setCartItemDSType(component.get().getCartitemdstype());

		final List<ProductReferenceData> productRecommendations = productRecommendationManagerFacade
				.getProductRecommendation(context);
		model.addAttribute("title", component.get().getTitle());
		model.addAttribute("recoId", HtmlUtils.htmlEscape(id));
		model.addAttribute("recoType", component.get().getRecotype());
		model.addAttribute("componentId", componentId);
		model.addAttribute("leadingitemdstype", component.get().getLeadingitemdstype());
		model.addAttribute("cartitemdstype", component.get().getCartitemdstype());
		model.addAttribute("productReferences", productRecommendations);
		model.addAttribute("numberOfItems", productRecommendations.size());

		return VIEWNAME;
	}

	public void saveProdRecoImpression(final int itemCount, final String componentId)
	{

		if (itemCount <= 0 || itemCount > 100)
		{
			LOG.warn("Invalid itemCount={} for componentId={}", itemCount, componentId);
			return;
		}

		final Optional<CMSSAPRecommendationComponentModel> component = this.getComponent(componentId);
		if (!component.isPresent())
		{
			return;
		}

		final ImpressionContext impressionContext = new ImpressionContext(component.get().getRecotype(), itemCount);

		productRecommendationManagerFacade.saveImpression(impressionContext);
	}
}
