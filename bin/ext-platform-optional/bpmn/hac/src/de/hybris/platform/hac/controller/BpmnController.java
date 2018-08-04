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
package de.hybris.platform.hac.controller;

import de.hybris.platform.hac.data.form.BpmnContentFormData;
import de.hybris.platform.hac.data.form.BpmnProcessResult;
import de.hybris.platform.hac.facade.BpmnFacade;
import de.hybris.platform.processengine.transformer.bpmnhybris.data.BpmnProcessData;
import de.hybris.platform.processengine.transformer.exception.BpmnProcessConversionException;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@RequestMapping("/bpmn/**")
public class BpmnController
{
	private static final Logger LOG = LoggerFactory.getLogger(BpmnController.class);
	private static final String BPMN_PATTERN = "[<](.*[:])?definitions";
	private static final Pattern BPMN_PATTERN_COMPILED = Pattern.compile(BPMN_PATTERN);

	private static final String HYBRIS_PATTERN = "[<](.*[:])?process";
	private static final Pattern HYBRIS_PATTERN_COMPILED = Pattern.compile(HYBRIS_PATTERN);

	@Autowired
	private BpmnFacade facade;

	@RequestMapping(value = "import", method = RequestMethod.GET)
	public String bpmnImportForm(@ModelAttribute("bpmnImportContent") final BpmnContentFormData contentData)
	{

		return "bpmn/bpmnImport";
	}

	@RequestMapping(value = "editor", method = RequestMethod.GET)
	public String bpmnEditor(@ModelAttribute("bpmnImportContent") final BpmnContentFormData contentData)
	{
		return "bpmn/bpmnEditor";
	}

	@RequestMapping(value = "editor/save", method = RequestMethod.POST)
	public void bpmnEditorXML(@RequestBody final String bpmnXml, final HttpServletRequest request,
			final HttpServletResponse response, final Model model)
	{
		try
		{
			final BpmnProcessData bpmnProcessData = facade.convertBpmnToProcess(bpmnXml);
			if (bpmnProcessData != null && StringUtils.isNotEmpty(bpmnProcessData.getBpmnContent()))
			{
				facade.saveBpmnProcessData(bpmnProcessData);
				response.setStatus(HttpServletResponse.SC_OK);
				LOG.info("BPMN Editor data saved successfully");
			}
		}
		catch (final Exception invalidProcessEx)
		{
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			LOG.error("ERROR: " + invalidProcessEx.getMessage());
		}
	}

	@RequestMapping(value = "convert", method = RequestMethod.POST)
	public String bpmnImportConvertForm(@ModelAttribute("bpmnImportContent") final BpmnContentFormData contentData,
			final Model model)
	{
		LOG.debug("Post Request");

		final String srcString = contentData.getSource();
		final BpmnProcessResult.BpmnProcessResultBuilder bpmnProcessResultBuilder = new BpmnProcessResult.BpmnProcessResultBuilder();
		if (StringUtils.isNotBlank(srcString))
		{
			if (match(srcString, BPMN_PATTERN_COMPILED))
			{
				try
				{
					final BpmnProcessData bpmnProcessData = facade.convertBpmnToProcess(srcString);
					contentData.setResult(bpmnProcessData.getProcessContent());
					contentData.setCode(bpmnProcessData.getProcessCode());
					bpmnProcessResultBuilder.withMessageCode("bpmn.import.success");
				}
				catch (final Exception e)
				{
					LOG.error("Exception occured while saving BPMN process.", e);
					bpmnProcessResultBuilder.failed().withMessageCode("bpmn.import.error").withDetailMessage(e.getLocalizedMessage());
				}
			}
			else if (match(srcString, HYBRIS_PATTERN_COMPILED))
			{
				BpmnProcessData bpmnProcessData = null;
				try
				{
					bpmnProcessData = facade.convertProcessToBpmn(srcString);
					contentData.setResult(bpmnProcessData.getBpmnContent());
					contentData.setCode(bpmnProcessData.getProcessCode());
					bpmnProcessResultBuilder.withMessageCode("bpmn.import.success");
				}
				catch (final BpmnProcessConversionException e)
				{
					bpmnProcessResultBuilder.failed().withMessageCode("bpmn.convert.tobpmn.error")
							.withDetailMessage(e.getLocalizedMessage());
				}
			}
			else
			{
				bpmnProcessResultBuilder.failed().withMessageCode("bpmn.import.unknown.format");
			}
		}
		else
		{
			model.addAttribute("bpmnProcessResult", bpmnProcessResultBuilder.failed().withMessageCode("bpmn.import.empty"));
		}
		model.addAttribute("bpmnProcessResult", bpmnProcessResultBuilder.build());
		return "bpmn/bpmnImport";
	}

	@RequestMapping(value = "/bpmn/save", method = RequestMethod.POST)
	public String bpmnImportSaveForm(@ModelAttribute("bpmnImportContent") final BpmnContentFormData contentData,
			final HttpServletRequest request, final Model model)
	{
		LOG.debug("Save Request");
		final BpmnProcessResult.BpmnProcessResultBuilder bpmnProcessResultBuilder = new BpmnProcessResult.BpmnProcessResultBuilder();
		final BpmnProcessData bpmnProcessData = new BpmnProcessData();
		populateBpmnProcessData(bpmnProcessData, contentData);
		try
		{
			facade.saveBpmnProcessData(bpmnProcessData);
			bpmnProcessResultBuilder.withMessageCode("bpmn.editor.hybris.success");
		}
		catch (final ModelSavingException e)
		{
			final StringBuilder localizedMessage = new StringBuilder(e.getLocalizedMessage());
			if (e.getCause() != null && e.getCause().getCause() != null && e.getCause() instanceof InterceptorException)
			{
				localizedMessage.append(" (").append(e.getCause().getCause() == null ? e.getCause().getLocalizedMessage()
						: e.getCause().getCause().getLocalizedMessage()).append(")");
			}
			bpmnProcessResultBuilder.failed().withMessageCode("bpmn.editor.hybris.error")
					.withDetailMessage(localizedMessage.toString());
		}
		model.addAttribute("bpmnProcessResult", bpmnProcessResultBuilder.build());
		return "bpmn/bpmnImport";
	}

	protected void populateBpmnProcessData(final BpmnProcessData bpmnProcessData, final BpmnContentFormData contentData)
	{
		final String scriptContent = contentData.getSource();
		final String resultContent = contentData.getResult();
		bpmnProcessData.setProcessCode(contentData.getCode());

		if (match(scriptContent, BPMN_PATTERN_COMPILED))
		{
			bpmnProcessData.setBpmnContent(scriptContent);
			bpmnProcessData.setProcessContent(resultContent);
		}
		else
		{
			bpmnProcessData.setBpmnContent(resultContent);
			bpmnProcessData.setProcessContent(scriptContent);
		}
	}

	protected boolean match(String line, final Pattern bpmnPattern)
	{
		if (line != null && line.startsWith("<?xml "))
		{
			final int indexOf = line.indexOf(">");
			line = line.substring(indexOf + 1);
		}
		LOG.debug(line);

		// Now create matcher object.
		final Matcher m = bpmnPattern.matcher(line);
		return m.find();
	}

	@RequestMapping(value = "export", method = RequestMethod.GET)
	public String bpmnExport(final HttpServletRequest request)
	{
		return "bpmn/bpmnExport";
	}

	@RequestMapping(value = "export", method = RequestMethod.POST)
	public void bpmnExport(@RequestBody final String processCode, final HttpServletRequest request,
			final HttpServletResponse response) throws IOException
	{
		LOG.debug("Export Request");
		final String errorMessage = "BPMN Process Content for " + processCode + " is not present";
		if (StringUtils.isNotEmpty(processCode))
		{
			// get the latest active version of bpmncontent
			final BpmnProcessData bpmnProcessData = facade.getBpmnProcessData(processCode.trim());
			if (bpmnProcessData != null)
			{
				final String bpmnContent = bpmnProcessData.getBpmnContent();
				final byte[] bpmnContentByte;
				if (StringUtils.isNotEmpty(bpmnContent))
				{
					bpmnContentByte = bpmnContent.getBytes(StandardCharsets.UTF_8);
					final String headerKey = "Content-Disposition";
					final String headerValue = String.format("attachment; filename=\"%s\"", processCode + ".bpmn");
					response.setHeader(headerKey, headerValue);
					response.setDateHeader("Expires", -1);
					response.setContentType("text/plain");
					response.getOutputStream().write(bpmnContentByte);
					response.getOutputStream().close();
					response.getOutputStream().flush();
				}
				else
				{
					LOG.debug(errorMessage);
					request.setAttribute("processCode", processCode);
					response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				}
			}
			else
			{
				LOG.debug(errorMessage);
				request.setAttribute("processCode", processCode);
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			}
		}
		else
		{
			LOG.debug("Process Content for {} is not present", processCode);
			request.setAttribute("processCode", processCode);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

}
