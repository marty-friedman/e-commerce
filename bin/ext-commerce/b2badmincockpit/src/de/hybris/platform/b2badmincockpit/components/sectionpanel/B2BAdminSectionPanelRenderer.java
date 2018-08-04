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
package de.hybris.platform.b2badmincockpit.components.sectionpanel;

import de.hybris.platform.catalog.CatalogTypeService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cockpit.components.listview.ActionColumnConfiguration;
import de.hybris.platform.cockpit.components.sectionpanel.SectionPanelLabelRenderer;
import de.hybris.platform.cockpit.model.gridview.impl.GridView;
import de.hybris.platform.cockpit.model.meta.ObjectTemplate;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.services.config.GridViewConfiguration;
import de.hybris.platform.cockpit.services.dragdrop.DraggedItem;
import de.hybris.platform.cockpit.session.UIEditorArea;
import de.hybris.platform.cockpit.session.UISessionUtils;
import de.hybris.platform.cockpit.session.impl.BaseUICockpitPerspective;
import de.hybris.platform.cockpit.util.ListActionHelper;
import de.hybris.platform.cockpit.util.UITools;
import de.hybris.platform.core.model.ItemModel;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Vbox;


public class B2BAdminSectionPanelRenderer implements SectionPanelLabelRenderer
{
	private static final Logger LOG = Logger.getLogger(B2BAdminSectionPanelRenderer.class);

	private static final String EDITOR_AREA_ACTIONS_DIV = "editorAreaActionsDiv";
	private static final String EDITOR_AREA_STATUS_DIV = "editorAreaStatusDiv";
	private CatalogTypeService catalogTypeService;

	private UIEditorArea editorArea;
	private TypedObject currentObject;

	@Override
	public void render(final String label, final String imageUrl, final Component parent)
	{
		final Div box = new Div();
		UITools.maximize(box);

		final Vbox vBox = new Vbox();
		vBox.setWidth("100%");

		final Div labelDiv = new Div();

		final Div descriptionContainer = new Div();
		descriptionContainer.setSclass("descriptionContainer");

		final Hbox hBox = new Hbox();
		hBox.setWidth("100%");

		if (getCurrentObject() != null)
		{
			final Div stausDiv = new Div();
			stausDiv.setClass(EDITOR_AREA_STATUS_DIV);
			renderStatus(stausDiv, getCurrentObject());
			hBox.appendChild(stausDiv);
		}

		hBox.setSpacing("100%");

		if (getCurrentObject() != null)
		{
			final Div actionsDiv = new Div();
			actionsDiv.setClass(EDITOR_AREA_ACTIONS_DIV);
			renderActions(actionsDiv, getCurrentObject());
			hBox.appendChild(actionsDiv);
		}
		vBox.appendChild(hBox);

		labelDiv.appendChild(new Label(label));

		vBox.setSpacing("8px");
		vBox.appendChild(labelDiv);

		descriptionContainer.appendChild(vBox);

		box.appendChild(descriptionContainer);

		box.setDroppable(BaseUICockpitPerspective.DRAG_DROP_ID);
		box.addEventListener(Events.ON_DROP, new EventListener()
		{
			@Override
			public void onEvent(final Event event) throws Exception
			{
				try
				{
					final DraggedItem draggedItem = getEditorArea().getPerspective().getDragAndDropWrapperService().getWrapper()
							.getDraggedItem(((DropEvent) event).getDragged());
					getEditorArea().getPerspective().activateItemInEditor(draggedItem.getSingleTypedObject());
				}
				catch (final Exception e)
				{
					LOG.error("D&D error: " + e);
				}
			}
		});

		parent.appendChild(box);

		// add catalog version mnemonic
		if (getEditorArea().getCurrentObject() != null && getEditorArea().getCurrentObject().getObject() instanceof ItemModel)
		{
			final ItemModel itemModel = (ItemModel) getEditorArea().getCurrentObject().getObject();

			if (itemModel != null && getCatalogTypeService().isCatalogVersionAwareModel(itemModel))
			{
				// get catalog version model
				final CatalogVersionModel catVersion = getCatalogTypeService()
						.getCatalogVersionForCatalogVersionAwareModel(itemModel);

				final String mnemonic = catVersion.getMnemonic();
				if (!StringUtils.isBlank(mnemonic))
				{
					final Label mnemLabel = new Label(" (" + mnemonic + ")");
					mnemLabel.setParent(labelDiv);
					mnemLabel.setSclass("catalog-mnemonic-label");
				}
			}
		}
	}


	/**
	 * Render actions related icons
	 *
	 * @param parent
	 * @param item
	 */
	protected void renderActions(final Component parent, final TypedObject item)
	{
		final ObjectTemplate template = UISessionUtils.getCurrentSession().getTypeService().getBestTemplate(item);
		final GridViewConfiguration config = UISessionUtils.getCurrentSession().getUiConfigurationService()
				.getComponentConfiguration(template, GridView.DEFAULT_GRIDVIEW_CONF, GridViewConfiguration.class);
		final ActionColumnConfiguration actionConfiguration = getActionConfiguration(config);

		ListActionHelper.renderActions(parent, item, actionConfiguration, "editorAreaActionImg");
	}

	protected void renderStatus(final Component parent, final TypedObject item)
	{
		final ObjectTemplate template = UISessionUtils.getCurrentSession().getTypeService().getBestTemplate(item);
		final GridViewConfiguration config = UISessionUtils.getCurrentSession().getUiConfigurationService()
				.getComponentConfiguration(template, GridView.DEFAULT_GRIDVIEW_CONF, GridViewConfiguration.class);
		final ActionColumnConfiguration actionConfiguration = getStatusConfiguration(config);

		ListActionHelper.renderActions(parent, item, actionConfiguration, "editorAreaActionImg");
	}



	protected ActionColumnConfiguration getActionConfiguration(final GridViewConfiguration config)
	{
		if (config != null)
		{
			final String actionSpringBeanID = config.getActionSpringBeanID();
			if (actionSpringBeanID != null)
			{
				return (ActionColumnConfiguration) SpringUtil.getBean(actionSpringBeanID);
			}
		}
		return null;
	}

	protected ActionColumnConfiguration getStatusConfiguration(final GridViewConfiguration config)
	{
		if (config != null)
		{
			final String specialActionSpringBeanID = config.getSpecialactionSpringBeanID();
			if (specialActionSpringBeanID != null)
			{
				return (ActionColumnConfiguration) SpringUtil.getBean(specialActionSpringBeanID);
			}
		}
		return null;
	}


	/**
	 * @param editorArea
	 *           the editorArea to set
	 */
	public void setEditorArea(final UIEditorArea editorArea)
	{
		this.editorArea = editorArea;
	}


	protected UIEditorArea getEditorArea()
	{
		return this.editorArea;
	}


	/**
	 * @return the currentObject
	 */
	public TypedObject getCurrentObject()
	{
		return currentObject;
	}

	/**
	 * @param currentObject
	 *           the currentObject to set
	 */
	public void setCurrentObject(final TypedObject currentObject)
	{
		this.currentObject = currentObject;
	}

	/**
	 * @return the catalogTypeService
	 */
	public CatalogTypeService getCatalogTypeService()
	{
		return catalogTypeService;
	}

	/**
	 * @param catalogTypeService
	 *           the catalogTypeService to set
	 */
	public void setCatalogTypeService(final CatalogTypeService catalogTypeService)
	{
		this.catalogTypeService = catalogTypeService;
	}

}
