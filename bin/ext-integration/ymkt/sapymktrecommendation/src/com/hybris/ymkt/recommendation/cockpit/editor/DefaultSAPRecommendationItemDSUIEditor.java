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
import de.hybris.platform.cockpit.model.editor.impl.AbstractUIEditor;
import de.hybris.platform.cockpit.model.meta.PropertyDescriptor;
import de.hybris.platform.cockpit.session.UISessionUtils;
import de.hybris.platform.cockpit.util.UITools;
import de.hybris.platform.core.Registry;
import de.hybris.platform.util.localization.Localization;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;

import com.hybris.ymkt.recommendation.dao.SAPRecommendationItemDataSourceType;
import com.hybris.ymkt.recommendation.services.RecommendationDataSourceTypeService;


/**
 * Simple text editor.
 * 
 * @deprecated since 6.6
 */
@Deprecated
public class DefaultSAPRecommendationItemDSUIEditor extends AbstractTextBasedUIEditor
{
	private static final String ENUM_EDITOR_SCLASS = "enumEditor";
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSAPRecommendationItemDSUIEditor.class);

	private List<String> availableValues = Collections.emptyList();
	private final Combobox editorView = new Combobox();
	private List<String> originalAvailableValues = Collections.emptyList();
	private RecommendationDataSourceTypeService recommendationDataSourceTypeService;
	private String searchString = "";

	protected void clearComboBox()
	{
		while (!editorView.getChildren().isEmpty())
		{
			editorView.removeItemAt(0);
		}
	}

	protected Comboitem createComboitem(final SAPRecommendationItemDataSourceType itemDSType)
	{
		final Comboitem comboitem = new Comboitem();
		comboitem.setLabel(itemDSType.getDescription());
		comboitem.setValue(itemDSType.getId());
		return comboitem;
	}

	@Override
	public HtmlBasedComponent createViewComponent(final Object initialValue, final Map<String, ? extends Object> parameters,
			final EditorListener listener)
	{

		parseInitialInputString(parameters);

		editorView.setConstraint("strict");
		editorView.setAutodrop(true);

		final String intialValueString = (String) initialValue;
		SAPRecommendationItemDataSourceType itemDSType = null;
		if (intialValueString != null && !intialValueString.isEmpty())
		{
			itemDSType = new SAPRecommendationItemDataSourceType();
			itemDSType.setId(intialValueString);
		}

		if (isEditable())
		{
			try
			{
				this.fillComboBox(this.getRecommendationDataSourceTypeService().getItemDataSourceTypes());
			}
			catch (IOException e)
			{
				LOG.error("Error reading ItemSourceTypes.", e);
				this.showMesssagePopupWithDetail(e);
			}

			if (itemDSType != null)
			{
				setEnumValue(editorView, itemDSType.getId());
			}

			final CancelButtonContainer ret = new CancelButtonContainer(listener, () -> {
				setEnumValue(editorView, initialEditValue);
				setValue(initialEditValue);
				fireValueChanged(listener);
				listener.actionPerformed(EditorListener.ESCAPE_PRESSED);
			});

			ret.setSclass(ENUM_EDITOR_SCLASS);
			ret.setContent(editorView);


			editorView.addEventListener(Events.ON_FOCUS, event -> {
				if (editorView.getSelectedItem() != null)
				{
					initialEditValue = editorView.getSelectedItem().getValue();
				}
				ret.showButton(Boolean.TRUE.booleanValue());
			});

			editorView.addEventListener(Events.ON_CHANGE, event -> validateAndFireEvent(listener));
			editorView.addEventListener(Events.ON_BLUR, event -> ret.showButton(false));
			editorView.addEventListener(Events.ON_OK, event -> {
				validateAndFireEvent(listener);
				listener.actionPerformed(EditorListener.ENTER_PRESSED);
			});
			editorView.addEventListener(Events.ON_CANCEL, event -> {
				ret.showButton(false);
				DefaultSAPRecommendationItemDSUIEditor.this.setEnumValue(editorView, initialEditValue);
				setValue(initialEditValue);
				fireValueChanged(listener);
				listener.actionPerformed(EditorListener.ESCAPE_PRESSED);
			});

			if (UISessionUtils.getCurrentSession().isUsingTestIDs())
			{
				String id = "Enum_";
				String attributeQualifier = (String) parameters.get(AbstractUIEditor.ATTRIBUTE_QUALIFIER_PARAM);
				if (attributeQualifier != null)
				{
					attributeQualifier = attributeQualifier.replaceAll("\\W", "");
					id = id + attributeQualifier;
				}
				UITools.applyTestID(editorView, id);
			}

			return ret;
		}
		else
		{
			editorView.setDisabled(true);

			final Label ret;
			if (itemDSType != null)
			{
				ret = new Label(itemDSType.getId() + " " + itemDSType.getDescription());
			}
			else
			{
				ret = new Label(" ");
			}
			return ret;
		}
	}

	protected void fillComboBox(final List<SAPRecommendationItemDataSourceType> itemDataSourceTypes)
	{
		clearComboBox();
		itemDataSourceTypes.stream().map(this::createComboitem).forEach(editorView::appendChild);

		final List<String> values = itemDataSourceTypes.stream() //
				.map(SAPRecommendationItemDataSourceType::getId) //
				.collect(Collectors.toList());

		this.availableValues = values;
		this.originalAvailableValues = values;
	}


	@Override
	public String getEditorType()
	{
		return PropertyDescriptor.TEXT;
	}

	protected RecommendationDataSourceTypeService getRecommendationDataSourceTypeService()
	{
		if (recommendationDataSourceTypeService == null)
		{
			recommendationDataSourceTypeService = Registry.getApplicationContext().getBean("recommendationDataSourceTypeService",
					RecommendationDataSourceTypeService.class);
		}
		return recommendationDataSourceTypeService;
	}

	protected synchronized void handleChangingEvents(final EditorListener listener, final Event event)
	{
		final String newSearchString = ((InputEvent) event).getValue();
		LOG.debug("Event='{}' raised with value='{}', length={}.", event.getName(), newSearchString, newSearchString.length());
		if (newSearchString.length() >= 4 && !searchString.equals(newSearchString))
		{
			LOG.debug("String used for display: {}", newSearchString);
			searchString = newSearchString;
			fillComboBox(Collections.emptyList());
			listener.valueChanged(getValue());
		}
	}

	@Override
	public boolean isInline()
	{
		return true;
	}

	@Override
	public boolean isOptional()
	{
		return false;
	}

	protected void setEnumValue(final Combobox combo, final Object value)
	{
		final int index = this.availableValues.indexOf(value);
		if (index >= 0)
		{
			combo.setSelectedIndex(index);
		}
	}

	@Override
	public void setFocus(final HtmlBasedComponent rootEditorComponent, final boolean selectAll)
	{
		final Combobox element = (Combobox) ((CancelButtonContainer) rootEditorComponent).getContent();
		element.setFocus(true);

		if (initialInputString != null)
		{
			element.setText(initialInputString);
		}
	}

	@Override
	public void setOptional(final boolean optional)
	{
		if (!optional)
		{
			availableValues = originalAvailableValues;
		}
		super.setOptional(optional);
	}

	protected void showMesssagePopupWithDetail(IOException ioe)
	{
		try
		{
			final String exceptionText = Localization.getLocalizedString("connectionError.description", new Object[]
			{ Localization.getLocalizedString("type.CMSSAPRecommendationComponent.name") });
			final String exceptionTitle = Localization.getLocalizedString("connectionError.title");
			Messagebox.show(exceptionText + "\n" + ioe, exceptionTitle, Messagebox.OK, Messagebox.ERROR);
		}
		catch (final InterruptedException e)
		{
			LOG.error("Messagebox Exception", e);
		}
	}

	protected void validateAndFireEvent(final EditorListener listener)
	{
		if (editorView.getSelectedItem() == null)
		{
			setEnumValue(editorView, initialEditValue);
		}
		else
		{
			DefaultSAPRecommendationItemDSUIEditor.this.setValue(editorView.getSelectedItem().getValue());
			listener.valueChanged(getValue());
		}
	}
}
