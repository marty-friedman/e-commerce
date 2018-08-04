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
package de.hybris.platform.sap.productconfig.runtime.ssc.impl;

import de.hybris.platform.sap.productconfig.runtime.ssc.TextConverter;
import de.hybris.platform.sap.productconfig.runtime.ssc.constants.SapproductconfigruntimesscConstants;

import java.util.regex.Pattern;

import com.sap.custdev.projects.fbs.slc.cfg.client.ITextDescription;


/**
 * Default implementation of the {@link TextConverter}<br>
 * <b>Note: This class will remove any markup from the ITF-Text, as this an potential attack vector. In case it is
 * desired to display formatted ITF-Texts on the UI, this class needs to be enhanced/replaced. One should specify a
 * white list of allowed markup for formatting.</b>
 */
public class TextConverterImpl implements TextConverter
{
	private static final char DOUBLE_QUOTE = '\"';
	private static final char SINGLE_QUOTE = '\'';
	private static final String EMPTY_STRING = "";
	/**
	 * string sequence identifying a ITF-Section named Description
	 */
	private static final String SECTION_DESCRIPTION = "&DESCRIPTION&";
	/**
	 * string sequence identifying a ITF-Section named Explanaition
	 */
	private static final String SECTION_EXPLAINATION = "&EXPLANATION&";

	private final Pattern pFormat = Pattern.compile("\\<(?:\\/\\w*|\\w+)\\>");
	private final Pattern pMarkup = Pattern.compile("\\<.*?\\>");
	private final Pattern pEscapeSeq = Pattern.compile("\\<(:?\\)|\\()\\>");


	@Override
	public String convertLongText(final String formattedText)
	{
		String result = formattedText;
		if (!result.isEmpty())
		{
			result = deEscapeString(result);
			result = extractSection(result);
			result = removeFormatting(result);
			result = removeMarkup(result);
			result = replaceDoubleQuotes(result);
		}
		return result;
	}

	protected String removeFormatting(final String formattedText)
	{
		return pFormat.matcher(formattedText).replaceAll(EMPTY_STRING);
	}

	protected String removeMarkup(final String markup)
	{
		return pMarkup.matcher(markup).replaceAll(EMPTY_STRING);
	}

	protected String extractSection(final String textWithSections)
	{

		int startIdx = textWithSections.indexOf(SECTION_DESCRIPTION);
		String sectionText;
		if (startIdx != -1)
		{
			startIdx += SECTION_DESCRIPTION.length();
			int endIdx = textWithSections.indexOf(SECTION_EXPLAINATION, startIdx);
			if (endIdx == -1)
			{
				endIdx = textWithSections.length();
			}
			sectionText = textWithSections.substring(startIdx, endIdx);
		}
		else
		{
			sectionText = textWithSections;
		}

		return sectionText;
	}

	protected String deEscapeString(final String escapedString)
	{
		return pEscapeSeq.matcher(escapedString).replaceAll(EMPTY_STRING);
	}

	protected String replaceDoubleQuotes(final String singleQuotes)
	{
		return singleQuotes.replace(DOUBLE_QUOTE, SINGLE_QUOTE);
	}

	@Override
	public String convertDependencyText(final ITextDescription[] textDescriptionArray)
	{
		String text = null;
		final StringBuilder textBuffer = new StringBuilder();


		if (textDescriptionArray != null && textDescriptionArray.length > 0)
		{
			for (final ITextDescription textDescriptionLine : textDescriptionArray)
			{
				final String textLine = textDescriptionLine.getTextLine();
				textBuffer.append(textLine);
			}
		}


		if (textBuffer.length() > 0)
		{
			text = getExplanationForDependency(textBuffer.toString());
			if (!text.isEmpty())
			{
				text = deEscapeString(text);
				text = removeFormatting(text);
				text = removeMarkup(text);
				text = replaceDoubleQuotes(text);
			}
			return text;

		}
		return text;
	}

	protected String getExplanationForDependency(final String text)
	{

		String explanation = null;

		if (text == null || text.isEmpty())
		{
			explanation = "";
		}
		else
		{
			int start = 0;
			if (text.indexOf(SapproductconfigruntimesscConstants.EXPLANATION) > -1)
			{
				start = text.indexOf(SapproductconfigruntimesscConstants.EXPLANATION)
						+ SapproductconfigruntimesscConstants.EXPLANATION.length();
			}
			int end = text.length();
			if (text.indexOf(SapproductconfigruntimesscConstants.DOCUMENTATION) > -1)
			{
				end = text.indexOf(SapproductconfigruntimesscConstants.DOCUMENTATION);
			}
			explanation = text.substring(start, end);
		}

		return explanation;
	}

}
