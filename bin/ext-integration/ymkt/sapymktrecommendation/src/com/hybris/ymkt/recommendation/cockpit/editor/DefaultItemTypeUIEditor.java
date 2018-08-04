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
import de.hybris.platform.util.localization.Localization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Label;

import com.hybris.ymkt.recommendation.constants.SapymktrecommendationConstants;


/**
 * Simple text editor.
 * 
 * @deprecated since 6.6
 */
@Deprecated
public class DefaultItemTypeUIEditor extends AbstractTextBasedUIEditor
{
	private static final String ENUM_EDITOR_SCLASS = "enumEditor";
	private List<? extends Object> availableValues = Collections.emptyList();
	private List<? extends Object> originalAvailableValues;
	private final Combobox editorView = new Combobox();

	protected void setEnumValue(final Combobox combo, final Object value)
	{
		final int index = this.availableValues.indexOf(value) - 1;
		if (index >= 0)
		{
			combo.setSelectedIndex(index);
		}
	}

	@Override
	public HtmlBasedComponent createViewComponent(final Object initialValue, final Map<String, ? extends Object> parameters,
			final EditorListener listener)
	{
		parseInitialInputString(parameters);
		editorView.setConstraint("strict");
		editorView.setAutodrop(true);

		if (isEditable())
		{

			fillComboBox();
			setEnumValue(editorView, initialValue);

			final CancelButtonContainer ret = new CancelButtonContainer(listener, new CancelListener()
			{
				@Override
				public void cancelPressed()
				{
					setEnumValue(editorView, initialEditValue);
					setValue(initialEditValue);
					fireValueChanged(listener);
					listener.actionPerformed(EditorListener.ESCAPE_PRESSED);
				}
			});

			ret.setSclass(ENUM_EDITOR_SCLASS);
			ret.setContent(editorView);


			editorView.addEventListener(Events.ON_FOCUS, new EventListener()
			{

				@Override
				public void onEvent(final Event event)
				{
					if (editorView.getSelectedItem() != null)
					{
						initialEditValue = editorView.getSelectedItem().getValue();
					}
					ret.showButton(true);
				}
			});

			editorView.addEventListener(Events.ON_CHANGE, new EventListener()
			{
				@Override
				public void onEvent(final Event arg0)
				{
					validateAndFireEvent(listener);
				}
			});

			editorView.addEventListener(Events.ON_BLUR, new EventListener()
			{
				@Override
				public void onEvent(final Event arg0)
				{
					ret.showButton(false);
				}
			});
			editorView.addEventListener(Events.ON_OK, new EventListener()
			{
				@Override
				public void onEvent(final Event arg0)
				{
					validateAndFireEvent(listener);
					listener.actionPerformed(EditorListener.ENTER_PRESSED);
				}
			});
			editorView.addEventListener(Events.ON_CANCEL, new EventListener()
			{
				@Override
				public void onEvent(final Event arg0)
				{
					ret.showButton(false);
					DefaultItemTypeUIEditor.this.setEnumValue(editorView, initialEditValue);
					setValue(initialEditValue);
					fireValueChanged(listener);
					listener.actionPerformed(EditorListener.ESCAPE_PRESSED);
				}
			});

			if (UISessionUtils.getCurrentSession().isUsingTestIDs())
			{
				final String attributeQualifier = (String) parameters.get(AbstractUIEditor.ATTRIBUTE_QUALIFIER_PARAM);
				String id = "Enum_";
				if (attributeQualifier != null)
				{
					id = id + attributeQualifier.replaceAll("\\W", "");
				}
				UITools.applyTestID(editorView, id);
			}

			return ret;
		}
		else
		{
			editorView.setDisabled(true);

			String initialLabel = "";
			if (SapymktrecommendationConstants.CATEGORY.equals(initialValue))
			{
				initialLabel = Localization.getLocalizedString("type.CMSSAPRecommendationComponent.category");
			}
			else if (SapymktrecommendationConstants.PRODUCT.equals(initialValue))
			{
				initialLabel = Localization.getLocalizedString("type.CMSSAPRecommendationComponent.product");
			}
			return new Label(initialLabel);
		}
	}

	/**
	 * @param availableValues
	 */
	protected void setAvailableValues(final List<? extends Object> availableValues)
	{
		this.availableValues = new ArrayList<>(availableValues);
		if (isOptional())
		{
			this.availableValues.add(0, null);
		}
		this.originalAvailableValues = new ArrayList<>(availableValues);

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

	protected void validateAndFireEvent(final EditorListener listener)
	{
		if (editorView.getSelectedItem() == null)
		{
			setEnumValue(editorView, initialEditValue);
		}
		else
		{
			DefaultItemTypeUIEditor.this.setValue(editorView.getSelectedItem().getValue());
			listener.valueChanged(getValue());
		}
	}

	/**
	 *
	 */
	protected void fillComboBox()
	{
		clearComboBox();

		this.setAvailableValues(Arrays.asList(SapymktrecommendationConstants.PRODUCT, SapymktrecommendationConstants.CATEGORY));

		final Comboitem comboitemProduct = new Comboitem();
		comboitemProduct.setLabel(Localization.getLocalizedString("type.CMSSAPRecommendationComponent.product"));
		comboitemProduct.setValue(SapymktrecommendationConstants.PRODUCT);
		editorView.appendChild(comboitemProduct);
		final Comboitem comboitemCategory = new Comboitem();
		comboitemCategory.setLabel(Localization.getLocalizedString("type.CMSSAPRecommendationComponent.category"));
		comboitemCategory.setValue(SapymktrecommendationConstants.CATEGORY);
		editorView.appendChild(comboitemCategory);
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
	public boolean isInline()
	{
		return true;
	}

	@Override
	public String getEditorType()
	{
		return PropertyDescriptor.TEXT;
	}

}
