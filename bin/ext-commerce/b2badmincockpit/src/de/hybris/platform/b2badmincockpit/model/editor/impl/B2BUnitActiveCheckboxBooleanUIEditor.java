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
package de.hybris.platform.b2badmincockpit.model.editor.impl;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.cockpit.model.editor.EditorListener;
import de.hybris.platform.cockpit.model.editor.impl.AbstractUIEditor;
import de.hybris.platform.cockpit.model.editor.impl.CheckboxBooleanUIEditor;
import de.hybris.platform.cockpit.model.meta.PropertyDescriptor;
import de.hybris.platform.cockpit.model.meta.TypedObject;
import de.hybris.platform.cockpit.util.UITools;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.user.UserModel;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Checkbox;


public class B2BUnitActiveCheckboxBooleanUIEditor extends AbstractUIEditor
{
	private static final Logger LOG = Logger.getLogger(CheckboxBooleanUIEditor.class);

	private static final String TREAT_NULL_VALUES_AS_TRUE = "treatNullValuesAsTrue";

	private final B2BUnitService<B2BUnitModel, UserModel> b2bUnitService = (B2BUnitService<B2BUnitModel, UserModel>) Registry
			.getApplicationContext().getBean("b2bUnitService");

	@Override
	public HtmlBasedComponent createViewComponent(final Object initialValue, final Map<String, ? extends Object> parameters,
			final EditorListener listener)
	{

		final TypedObject editedObject = (TypedObject) parameters.get("currentObject");
		final B2BUnitModel b2bUnit = (B2BUnitModel) editedObject.getObject();
		final B2BUnitModel parentUnit = b2bUnitService.getParent(b2bUnit);

		boolean isParentActive = true;
		if (parentUnit != null)
		{
			isParentActive = parentUnit.getActive().booleanValue();
		}

		final Checkbox editorView = new Checkbox();

		if (!isEditable())
		{
			editorView.setDisabled(true);
		}

		if (!isParentActive)
		{
			editorView.setLabel(Labels.getLabel("b2bitem.unit.unit.disabled"));
			editorView.setDisabled(true);
		}

		if (initialValue == null || initialValue instanceof Boolean)
		{
			Boolean value = ((Boolean) initialValue);
			if (value == null)
			{
				value = Boolean.valueOf((String) parameters.get(TREAT_NULL_VALUES_AS_TRUE));
			}

			editorView.setChecked(value.booleanValue());
		}
		else
		{
			LOG.error("Initial value not of type Boolean.");
		}

		editorView.addEventListener(Events.ON_BLUR, new EventListener()
		{
			@Override
			public void onEvent(final Event arg0) throws Exception
			{
				performAction(editorView, listener, b2bUnit);
			}
		});
		editorView.addEventListener(Events.ON_CHECK, new EventListener()
		{
			@Override
			public void onEvent(final Event arg0) throws Exception
			{
				performAction(editorView, listener, b2bUnit);
			}
		});
		editorView.addEventListener(Events.ON_OK, new EventListener()
		{
			@Override
			public void onEvent(final Event arg0) throws Exception
			{
				performAction(editorView, listener, b2bUnit);
			}
		});
		UITools.applyTestID(editorView, "boolean_checkbox_ed");
		return editorView;
	}

	protected void performAction(final Checkbox editorView, final EditorListener listener, final B2BUnitModel unit)
	{
		B2BUnitActiveCheckboxBooleanUIEditor.this.setValue(Boolean.valueOf(editorView.isChecked()));
		listener.valueChanged(getValue());
		listener.actionPerformed(EditorListener.ENTER_PRESSED);
	}

	@Override
	public boolean isInline()
	{
		return true;
	}

	@Override
	public String getEditorType()
	{
		return PropertyDescriptor.BOOLEAN;
	}

}
