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
package com.hybris.ymkt.segmentation.wcms;

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
import java.util.Optional;

import org.apache.commons.lang.ObjectUtils;
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

import com.hybris.ymkt.segmentation.services.InitiativeService;
import com.hybris.ymkt.segmentation.services.InitiativeService.InitiativeQuery;
import com.hybris.ymkt.segmentation.services.InitiativeService.InitiativeQuery.TileFilterCategory;
import com.hybris.ymkt.segmentation.dto.SAPInitiative;


/**
 * Simple text editor.
 * 
 * @deprecated since 6.6
 */
@Deprecated
public class DefaultSAPInitiativeUIEditor extends AbstractTextBasedUIEditor
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSAPInitiativeUIEditor.class);

	private static final String ENUM_EDITOR_SCLASS = "enumEditor";

	private final Combobox editorView = new Combobox();
	private InitiativeService initiativeService;
	private String searchString = "";

	@Override
	public HtmlBasedComponent createViewComponent(final Object initialValue, final Map<String, ? extends Object> parameters,
			final EditorListener listener)
	{
		this.parseInitialInputString(parameters);
		Optional<SAPInitiative> initiative = Optional.empty();

		this.editorView.setConstraint("strict");
		this.editorView.setSclass("initiative-combo");
		this.editorView.setAutodrop(true);

		final String intialValueString = (String) initialValue;
		if (intialValueString != null && !intialValueString.isEmpty())
		{
			try
			{
				initiative = getInitiativeService().getInitiative(intialValueString);
			}
			catch (final IOException e)
			{
				LOG.error("Error reading SelectedInitiative '{}'", intialValueString, e);
			}
		}

		if (isEditable())
		{
			if (initiative.isPresent())
			{
				final Comboitem item = createComboitem(initiative.get());
				this.editorView.appendChild(item);
				this.editorView.setSelectedItem(item);
			}

			final CancelButtonContainer ret = new CancelButtonContainer(listener, () -> {
				setValue(initialEditValue);
				fireValueChanged(listener);
				listener.actionPerformed(EditorListener.ESCAPE_PRESSED);
			});

			ret.setSclass(ENUM_EDITOR_SCLASS);
			ret.setContent(editorView);

			this.editorView.addEventListener(Events.ON_FOCUS, event -> {
				if (editorView.getSelectedItem() != null)
				{
					initialEditValue = this.editorView.getSelectedItem().getValue();
				}
				ret.showButton(true);
			});

			editorView.addEventListener(Events.ON_CHANGE, event -> validateAndFireEvent(listener));
			editorView.addEventListener(Events.ON_BLUR, event -> ret.showButton(false));

			editorView.addEventListener(Events.ON_OK, event -> {
				validateAndFireEvent(listener);
				listener.actionPerformed(EditorListener.ENTER_PRESSED);
			});

			editorView.addEventListener(Events.ON_CANCEL, event -> {
				ret.showButton(false);
				setValue(initialEditValue);
				fireValueChanged(listener);
				listener.actionPerformed(EditorListener.ESCAPE_PRESSED);
			});

			editorView.addEventListener(Events.ON_CHANGING, event -> {
				ret.showButton(true);
				handleChangingEvents(listener, event);
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
			if (initiative.isPresent())
			{
				ret = new Label(initiative.get().getId() + " " + initiative.get().getName());
			}
			else
			{
				ret = new Label(" ");
			}
			return ret;
		}
	}

	@Override
	public String getEditorType()
	{
		return PropertyDescriptor.TEXT;
	}

	protected InitiativeService getInitiativeService()
	{
		if (this.initiativeService == null)
		{
			this.initiativeService = Registry.getApplicationContext().getBean("initiativeService", InitiativeService.class);
		}
		return this.initiativeService;
	}

	protected synchronized void handleChangingEvents(final EditorListener listener, final Event event) throws InterruptedException
	{
		final String newSearchString = ((InputEvent) event).getValue();
		if (newSearchString.length() >= 2 && !this.searchString.equals(newSearchString))
		{
			this.searchString = newSearchString;
			clearComboBox();
			this.searchValues(newSearchString).stream() //
					.map(this::createComboitem) //
					.forEach(this.editorView::appendChild);
			listener.valueChanged(getValue());
		}
	}

	@Override
	public boolean isInline()
	{
		return true;
	}

	protected List<SAPInitiative> searchValues(final String newSearchString) throws InterruptedException
	{
		try
		{
			final InitiativeQuery query = new InitiativeQuery.Builder() //
					.searchTerms(newSearchString) //
					.tileFilterCategories(TileFilterCategory.ACTIVE, TileFilterCategory.PLANNED) //
					.build();
			return this.getInitiativeService().getInitiatives(query);
		}
		catch (final IOException e)
		{
			LOG.error("Error searching with '{}'", newSearchString, e);
			this.showErrorPopup(e);
			return Collections.emptyList();
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

	protected void clearComboBox()
	{
		final int size = this.editorView.getChildren().size();
		for (int i = 0; i < size; i++)
		{
			this.editorView.removeItemAt(0);
		}
	}

	protected Comboitem createComboitem(final SAPInitiative initiative)
	{
		final String label = initiative.getId() + " " + initiative.getName() + " (" + initiative.getMemberCount() + ")";
		final String value = initiative.getId();
		final Comboitem comboitem = new Comboitem();
		comboitem.setLabel(label);
		comboitem.setValue(value);
		comboitem.setTooltiptext(label);
		return comboitem;
	}

	protected void showErrorPopup(Exception e) throws InterruptedException
	{
		final String exceptionText = Localization.getLocalizedString("adt.connectionError.description");
		final String exceptionTitle = Localization.getLocalizedString("adt.connectionError.title");
		Messagebox.show(exceptionText + "\n" + e, exceptionTitle, Messagebox.OK, Messagebox.ERROR);
	}

	protected void validateAndFireEvent(final EditorListener listener)
	{
		if (editorView.getSelectedItem() != null)
		{
			DefaultSAPInitiativeUIEditor.this.setValue(editorView.getSelectedItem().getValue());
			editorView.setTooltiptext(ObjectUtils.toString(editorView.getSelectedItem().getValue()));
			listener.valueChanged(getValue());
		}
	}
}
