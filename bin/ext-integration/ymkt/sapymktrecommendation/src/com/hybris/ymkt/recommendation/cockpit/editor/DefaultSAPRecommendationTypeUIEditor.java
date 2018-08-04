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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;

import com.hybris.ymkt.recommendation.dao.SAPRecommendationType;
import com.hybris.ymkt.recommendation.services.RecommendationScenarioService;


/**
 * Simple text editor.
 * 
 * @deprecated since 6.6
 */
@Deprecated
public class DefaultSAPRecommendationTypeUIEditor extends AbstractTextBasedUIEditor
{
	protected static final String ENUM_EDITOR_SCLASS = "enumEditor";
	private static final Logger LOG = Logger.getLogger(DefaultSAPRecommendationTypeUIEditor.class);

	protected List<String> availableValues = Collections.emptyList();
	protected final Combobox editorView = new Combobox();
	protected List<String> originalAvailableValues = Collections.emptyList();
	protected RecommendationScenarioService recommendationScenarioService;
	protected String searchString = "";

	protected void clearComboBox()
	{
		while (!editorView.getChildren().isEmpty())
		{
			editorView.removeItemAt(0);
		}
	}

	protected Comboitem createComboitem(final SAPRecommendationType recommendationType)
	{
		final Comboitem comboitem = new Comboitem();
		comboitem.setLabel(recommendationType.getId());
		comboitem.setValue(recommendationType.getId());
		return comboitem;
	}

	@Override
	public HtmlBasedComponent createViewComponent(final Object initialValue, final Map<String, ? extends Object> parameters,
			final EditorListener listener)
	{
		parseInitialInputString(parameters);
		SAPRecommendationType scenario = null;

		editorView.setConstraint("strict");
		editorView.setAutodrop(true);

		final String intialValueString = (String) initialValue;
		if (intialValueString != null && !intialValueString.isEmpty())
		{
			scenario = new SAPRecommendationType(intialValueString);
		}

		if (isEditable())
		{
			try
			{
				fillComboBox(getRecommendationScenarioService().getRecommendationScenarios());
			}
			catch (final IOException e)
			{
				LOG.error("Error with getRecommendationScenarios()", e);
				this.showMesssagePopupWithDetail();
			}

			if (scenario != null)
			{
				setEnumValue(editorView, scenario.getId());
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
				DefaultSAPRecommendationTypeUIEditor.this.setEnumValue(editorView, initialEditValue);
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
			return scenario == null ? new Label(" ") : new Label(scenario.getId() + " " + scenario.getDescription());
		}
	}

	protected void fillComboBox(final List<SAPRecommendationType> types)
	{
		this.clearComboBox();
		final List<String> values = new ArrayList<>();
		for (final SAPRecommendationType type : types)
		{
			editorView.appendChild(this.createComboitem(type));
			values.add(type.getId());
		}
		this.setAvailableValues(values);
	}


	@Override
	public String getEditorType()
	{
		return PropertyDescriptor.TEXT;
	}

	protected Combobox getEditorView()
	{
		return editorView;
	}

	/**
	 * @return the recommendationScenarioService
	 */
	protected RecommendationScenarioService getRecommendationScenarioService()
	{
		if (recommendationScenarioService == null)
		{
			recommendationScenarioService = Registry.getApplicationContext().getBean("recommendationScenarioService",
					RecommendationScenarioService.class);
		}
		return recommendationScenarioService;
	}

	protected synchronized void handleChangingEvents(final EditorListener listener, final Event event)
	{
		final String newSearchString = ((InputEvent) event).getValue();
		LOG.debug("Event raise for: " + newSearchString + newSearchString.length());
		if (newSearchString.length() >= 4 && !searchString.equals(newSearchString))
		{
			LOG.debug("String used for display: " + newSearchString);
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

	/**
	 * @param availableValues
	 */
	public void setAvailableValues(final List<String> availableValues)
	{
		if (availableValues.isEmpty())
		{
			editorView.setValue(Labels.getLabel("general.nothingtodisplay"));
			editorView.setDisabled(true);
			this.availableValues = Collections.emptyList();
			this.originalAvailableValues = Collections.emptyList();
		}
		else
		{
			this.availableValues = new ArrayList<>(availableValues);
			this.originalAvailableValues = new ArrayList<>(availableValues);
		}
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

	protected void showMesssagePopupWithDetail()
	{
		try
		{
			final String exceptionText = Localization.getLocalizedString("connectionError.description", new Object[]
			{ Localization.getLocalizedString("type.CMSSAPRecommendationComponent.name") });
			final String exceptionTitle = Localization.getLocalizedString("connectionError.title");
			Messagebox.show(exceptionText, exceptionTitle, Messagebox.OK, Messagebox.ERROR);
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
			DefaultSAPRecommendationTypeUIEditor.this.setValue(editorView.getSelectedItem().getValue());
			listener.valueChanged(getValue());
		}
	}

}
