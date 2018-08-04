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
package com.hybris.ymkt.recommendation.cockpit.editor;


import de.hybris.platform.cockpit.model.editor.EditorListener;
import de.hybris.platform.cockpit.model.editor.impl.AbstractTextBasedUIEditor;
import de.hybris.platform.cockpit.model.meta.PropertyDescriptor;
import de.hybris.platform.core.Registry;
import de.hybris.platform.util.localization.Localization;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;

import com.hybris.ymkt.recommendation.dao.SAPOfferContentPositionType;
import com.hybris.ymkt.recommendation.dao.SAPRecommendationItemDataSourceType;
import com.hybris.ymkt.recommendation.dao.SAPRecommendationType;
import com.hybris.ymkt.recommendation.services.OfferDiscoveryService;


/**
 * @deprecated since 6.6
 */
@Deprecated
public class DefaultOfferRecoUIEditor extends AbstractTextBasedUIEditor
{
	private static final String CATEGORY_CODE = "C";
	private static final String ENUM_EDITOR_SCLASS = "enumEditor";
	private static final Logger LOG = LoggerFactory.getLogger(DefaultOfferRecoUIEditor.class);
	private static final String OFFER_DISCOVERY_SERVICE = "offerDiscoveryService";
	private static final String PRODUCT_CODE = "P";
	private final Combobox editorView = new Combobox();
	protected OfferDiscoveryService offerDiscoveryService;

	private static final Comboitem createComboItem(String value, String label)
	{
		final Comboitem item = new Comboitem();
		item.setLabel(label);
		item.setValue(value);
		return item;
	}

	private static final int getIndexByValue(List<?> list, String value)
	{
		for (int i = 0; i < list.size(); i++)
		{
			final Comboitem comboitem = (Comboitem) list.get(i);
			if (comboitem != null && comboitem.getValue().equals(value))
			{
				return i;
			}
		}
		return -1;
	}

	public CancelButtonContainer addUIEventListeners(final EditorListener listener)
	{

		final CancelButtonContainer ret = new CancelButtonContainer(listener, () -> {
			this.setComboBoxSelectedIndex(initialEditValue);
			setValue(initialEditValue);
			fireValueChanged(listener);
			listener.actionPerformed(EditorListener.ESCAPE_PRESSED);
		});

		ret.setSclass(ENUM_EDITOR_SCLASS);
		ret.setContent(editorView);

		editorView.addEventListener(Events.ON_FOCUS, event -> {
			if (editorView.getSelectedItem() != null)
			{
				initialEditValue = editorView.getValue();
			}
			ret.showButton(Boolean.TRUE.booleanValue());
		});

		editorView.addEventListener(Events.ON_CHANGE, event -> this.validateAndFireEvent(listener));
		editorView.addEventListener(Events.ON_BLUR, event -> ret.showButton(false));
		editorView.addEventListener(Events.ON_OK, event -> {
			this.validateAndFireEvent(listener);
			listener.actionPerformed(EditorListener.ENTER_PRESSED);
		});
		editorView.addEventListener(Events.ON_CANCEL, event -> {
			ret.showButton(false);
			this.setComboBoxSelectedIndex(initialEditValue);
			setValue(initialEditValue);
			fireValueChanged(listener);
			listener.actionPerformed(EditorListener.ESCAPE_PRESSED);
		});

		return ret;
	}

	protected void clearComboBox()
	{
		final int size = editorView.getChildren().size();
		for (int i = 0; i < size; i++)
		{
			editorView.removeItemAt(0);
		}
	}

	@Override
	public HtmlBasedComponent createViewComponent(final Object initialValue, final Map<String, ? extends Object> parameters,
			final EditorListener listener)
	{
		final String attribute = parameters.get("attributeQualifier").toString();
		final String targetField = attribute.substring(attribute.lastIndexOf('.') + 1).trim();

		try
		{
			return generateCommonComboBox(initialValue, parameters, listener, targetField);
		}
		catch (final IOException e)
		{
			LOG.error("Error generating ComboBox with targetField : {}", targetField, e);
		}
		return null;
	}

	public HtmlBasedComponent generateCommonComboBox(final Object initialValue, final Map<String, ? extends Object> parameters,
			final EditorListener listener, final String targetField) throws IOException
	{

		parseInitialInputString(parameters);
		editorView.setConstraint("strict");
		editorView.setAutodrop(true);

		if (isEditable())
		{
			//populate ComboBox according to target field
			this.populateComboBox(targetField);
			//select saved value (if any)
			this.setComboBoxSelectedIndex(initialValue);
			//Add UI Events listener & return HtmlBasedComponent
			return this.addUIEventListeners(listener);
		}
		else
		{
			editorView.setDisabled(true);
			return null;
		}
	}

	protected List<SAPOfferContentPositionType> getContentPositionTypes() throws IOException
	{
		return this.getOfferDiscoveryService().getContentPositionValues();
	}

	@Override
	public String getEditorType()
	{
		return PropertyDescriptor.TEXT;
	}

	protected List<SAPRecommendationItemDataSourceType> getItemDataSourceTypes() throws IOException
	{
		return this.getOfferDiscoveryService().getItemDataSourceTypes();
	}

	protected OfferDiscoveryService getOfferDiscoveryService()
	{
		if (this.offerDiscoveryService == null)
		{
			this.offerDiscoveryService = (OfferDiscoveryService) Registry.getApplicationContext().getBean(OFFER_DISCOVERY_SERVICE);
		}
		return this.offerDiscoveryService;
	}

	protected List<SAPRecommendationType> getOfferRecommendationScenarios() throws IOException
	{
		return this.getOfferDiscoveryService().getOfferRecommendationScenarios();
	}

	@Override
	public boolean isInline()
	{
		return true;
	}

	public void populateComboBox(String targetField) throws IOException
	{
		clearComboBox();

		switch (targetField)
		{
			case "recotype":
				this.getOfferRecommendationScenarios().stream() //
						.map(x -> createComboItem(x.getId(), x.getId())) //
						.forEach(editorView::appendChild);
				return;
			case "contentposition":
				this.populateContentPositionComboBox();
				return;
			case "leadingitemtype":
				this.populateLeadingItemTypeComboBox();
				return;
			case "leadingitemdstype":
			case "cartitemdstype":
				this.getItemDataSourceTypes().stream() //
						.filter(y -> StringUtils.isNotEmpty(y.getId())) //
						.map(x -> createComboItem(x.getId(), x.getDescription())) //
						.forEach(editorView::appendChild);
				return;
			default:
				break;
		}

	}

	private void populateContentPositionComboBox() throws IOException
	{
		this.getContentPositionTypes().stream() //
				.map(x -> createComboItem(x.getContentPositionId(), x.getContentPositionId())) //
				.forEach(editorView::appendChild);

		//manually add "no content position" value
		final Comboitem comboItemModelNoContentPosition = new Comboitem();
		comboItemModelNoContentPosition.setValue("");
		comboItemModelNoContentPosition
				.setLabel(Localization.getLocalizedString("type.CMSSAPOfferRecoComponent.noContentPosition"));
		editorView.appendChild(comboItemModelNoContentPosition);
	}

	private void populateLeadingItemTypeComboBox()
	{
		//hardcode "Product" and "Category" values
		final Comboitem comboItemModelCategory = new Comboitem();
		comboItemModelCategory.setValue(CATEGORY_CODE);
		comboItemModelCategory.setLabel(Localization.getLocalizedString("type.CMSSAPOfferRecoComponent.category"));
		final Comboitem comboItemModelProduct = new Comboitem();
		comboItemModelProduct.setValue(PRODUCT_CODE);
		comboItemModelProduct.setLabel(Localization.getLocalizedString("type.CMSSAPOfferRecoComponent.product"));
		editorView.appendChild(comboItemModelCategory);
		editorView.appendChild(comboItemModelProduct);
	}

	protected void setComboBoxSelectedIndex(final Object value)
	{
		if (!editorView.getChildren().isEmpty() && value != null)
		{
			final List<?> children = editorView.getChildren();
			final int index = DefaultOfferRecoUIEditor.getIndexByValue(children, value.toString());
			editorView.setSelectedIndex(index);
		}
	}

	protected void validateAndFireEvent(final EditorListener listener)
	{
		if (editorView.getSelectedItem() == null)
		{
			this.setComboBoxSelectedIndex(initialEditValue);
		}
		else
		{
			DefaultOfferRecoUIEditor.this.setValue(editorView.getSelectedItem());
			listener.valueChanged(editorView.getSelectedItem().getValue());
		}
	}

}
