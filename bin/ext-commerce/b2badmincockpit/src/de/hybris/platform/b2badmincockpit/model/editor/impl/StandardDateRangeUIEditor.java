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

import de.hybris.platform.cockpit.model.editor.EditorListener;
import de.hybris.platform.cockpit.model.editor.UIEditor;
import de.hybris.platform.cockpit.model.editor.impl.DefaultDateUIEditor;
import de.hybris.platform.util.StandardDateRange;

import java.util.Date;
import java.util.Map;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zul.Label;
import org.zkoss.zul.Space;
import org.zkoss.zul.Vbox;


/**
 * This editor is responsible for type {@link StandardDateRange} The whole management of the Start Date
 * {@link DefaultDateUIEditor} and End Date {@link DefaultDateUIEditor} to form the StandardDate Range functionality.
 *
 */

public class StandardDateRangeUIEditor implements UIEditor
{

	protected static enum DateRangeIdentifier
	{
		START_DATE, END_DATE
	}

	@Override
	public HtmlBasedComponent createViewComponent(final Object initialValue, final Map<String, ? extends Object> parameters,
			final EditorListener listener)
	{

		final Vbox box = new Vbox();
		final StandardDateRange dateRange = (StandardDateRange) initialValue;
		Date startDate = null;
		Date endDate = null;

		final DefaultDateUIEditor startDateUIEditor = new DefaultDateUIEditor();
		final DefaultDateUIEditor endDateUIEditor = new DefaultDateUIEditor();

		if (dateRange != null)
		{
			startDate = dateRange.getStart();
			endDate = dateRange.getEnd();
			startDateUIEditor.setValue(startDate);
			endDateUIEditor.setValue(endDate);
		}

		final Label startLabel = new Label();
		startLabel.setValue(Labels.getLabel("b2bitem.budget.startDateLabel"));
		final Label endLabel = new Label();
		endLabel.setValue(Labels.getLabel("b2bitem.budget.endDateLabel"));

		final StandardDateRangeListener startDateListener = new StandardDateRangeListener(DateRangeIdentifier.START_DATE, listener,
				startDateUIEditor, endDateUIEditor);
		final StandardDateRangeListener endDateRangeListener = new StandardDateRangeListener(DateRangeIdentifier.END_DATE,
				listener, startDateUIEditor, endDateUIEditor);

		final HtmlBasedComponent startEditorView = startDateUIEditor.createViewComponent(startDate, parameters, startDateListener);
		final HtmlBasedComponent endEditorView = endDateUIEditor.createViewComponent(endDate, parameters, endDateRangeListener);

		box.setSpacing("8px");
		box.appendChild(startLabel);
		box.appendChild(startEditorView);
		box.appendChild(endLabel);
		box.appendChild(endEditorView);
		box.appendChild(new Space());
		return box;

	}

	/**
	 *
	 */
	class StandardDateRangeListener implements EditorListener
	{

		private final String id;
		private final EditorListener parentListener;
		private Date startDate;
		private Date endDate;
		private final DefaultDateUIEditor startDateEditor;
		private final DefaultDateUIEditor endDateEditor;


		public StandardDateRangeListener(final DateRangeIdentifier id, final EditorListener parentListener,
				final DefaultDateUIEditor startDateEditor, final DefaultDateUIEditor endDateUIEditor)
		{
			this.id = id.name();
			this.parentListener = parentListener;
			this.startDateEditor = startDateEditor;
			this.endDateEditor = endDateUIEditor;
		}

		@Override
		public void valueChanged(final Object value)
		{
			parentListener.valueChanged(getStandardDateRange((Date) value));
		}

		protected Object getStandardDateRange(final Date date)
		{
			if (id.equals(DateRangeIdentifier.START_DATE.name()))
			{
				startDate = date;
				endDate = (Date) endDateEditor.getValue();
			}
			else if (id.equals(DateRangeIdentifier.END_DATE.name()))
			{
				endDate = date;
				startDate = (Date) startDateEditor.getValue();
			}
			if (startDate == null || endDate == null)
			{
				return null;
			}
			return new StandardDateRange(startDate, endDate);
		}

		@Override
		public void actionPerformed(final String actionCode)
		{
			//Nothing to do
		}

	}

	@Override
	public boolean isInline()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getEditorType()
	{
		return "Dummy";
	}

	@Override
	public void setValue(final Object value)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public Object getValue()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setEditable(final boolean editable)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isEditable()
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void setFocus(final HtmlBasedComponent rootEditorComponent, final boolean selectAll)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isOptional()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setOptional(final boolean optional)
	{
		// TODO Auto-generated method stub

	}

}
