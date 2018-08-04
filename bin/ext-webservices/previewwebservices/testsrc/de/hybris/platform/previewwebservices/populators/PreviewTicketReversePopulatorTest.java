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
package de.hybris.platform.previewwebservices.populators;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.preview.PreviewDataModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.previewwebservices.dto.PreviewTicketWsDTO;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.impex.impl.ClasspathImpExResource;
import de.hybris.platform.servicelayer.model.ModelService;

import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class PreviewTicketReversePopulatorTest extends ServicelayerTransactionalTest
{
	@Resource
	private PreviewTicketReversePopulator previewTicketReversePopulator;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private CMSSiteService cmsSiteService;

	@Resource
	private ModelService modelService;

	@Before
	public void importCatalogs() throws Exception
	{
		importData(new ClasspathImpExResource("/previewwebservices/test/previewwebservices_testcatalogs.csv", "UTF-8"));
	}

	@Test
	public void shouldHaveOnlineProductAndStagedContentCatalogs()
	{
		final PreviewTicketWsDTO source = createPreviewTicketDTO();
		final PreviewDataModel target = new PreviewDataModel();
		previewTicketReversePopulator.populate(source, target);

		Assert.assertEquals("Staged", target.getActiveCatalogVersion().getVersion());
		Assert.assertEquals("testContentCatalog", target.getActiveCatalogVersion().getCatalog().getId());
		Assert.assertEquals(target.getCatalogVersions().size(), 2);
		Assert.assertTrue(
				target.getCatalogVersions().contains(catalogVersionService.getCatalogVersion("testContentCatalog", "Staged")));
		Assert.assertTrue(
				target.getCatalogVersions().contains(catalogVersionService.getCatalogVersion("testProductCatalog", "Online")));
		Assert.assertNotNull(target.getPage());
	}



	@Test
	public void shouldNotPopulateTargetWithPageWhenPageIsNotProvided()
	{
		final PreviewTicketWsDTO source = createPreviewTicketDTO();
		source.setPageId(null);
		final PreviewDataModel target = new PreviewDataModel();
		previewTicketReversePopulator.populate(source, target);
		Assert.assertNull(target.getPage());
	}

	@Test(expected = ConversionException.class)
	public void shouldThrowConversionExceptionIfSiteNotFound()
	{
		final PreviewTicketWsDTO source = createPreviewTicketDTO();
		source.setResourcePath("/cart");
		final PreviewDataModel target = new PreviewDataModel();
		previewTicketReversePopulator.populate(source, target);
	}

	@Test
	public void shouldGetActiveBaseSiteFromTargetIfSourceDTOContainsMalformedResourcePathUrl()
	{
		final PreviewTicketWsDTO source = createPreviewTicketDTO();
		source.setResourcePath("/cart");
		final PreviewDataModel target = new PreviewDataModel();

		try
		{
			target.setActiveSite(
					cmsSiteService.getSiteForURL(new URL("https://127.0.0.1:9002/yacceleratorstorefront?site=testSite")));
		}
		catch (final CMSItemNotFoundException | MalformedURLException e)
		{
			fail("Should NOT throw MalformedURLException");
		}

		previewTicketReversePopulator.populate(source, target);

		Assert.assertEquals("Staged", target.getActiveCatalogVersion().getVersion());
		Assert.assertEquals("testContentCatalog", target.getActiveCatalogVersion().getCatalog().getId());
		Assert.assertEquals(target.getCatalogVersions().size(), 2);
		Assert.assertTrue(
				target.getCatalogVersions().contains(catalogVersionService.getCatalogVersion("testContentCatalog", "Staged")));
		Assert.assertTrue(
				target.getCatalogVersions().contains(catalogVersionService.getCatalogVersion("testProductCatalog", "Online")));
		Assert.assertNotNull(target.getPage());
	}

	@Test
	public void shouldPopulateModelWithNewBaseSiteForProperResourcePath()
	{
		final PreviewTicketWsDTO source = createPreviewTicketDTO();
		source.setResourcePath("https://127.0.0.1:9002/yacceleratorstorefront?site=testSite2");
		final PreviewDataModel target = new PreviewDataModel();

		try
		{
			target.setActiveSite(
					cmsSiteService.getSiteForURL(new URL("https://127.0.0.1:9002/yacceleratorstorefront?site=testSite")));
		}
		catch (final CMSItemNotFoundException | MalformedURLException e)
		{
			fail("Should NOT throw MalformedURLException");
		}

		previewTicketReversePopulator.populate(source, target);

		Assert.assertEquals("Staged", target.getActiveCatalogVersion().getVersion());
		Assert.assertEquals("testContentCatalog", target.getActiveCatalogVersion().getCatalog().getId());
		Assert.assertEquals(target.getCatalogVersions().size(), 2);
		Assert.assertEquals("testSite2", target.getActiveSite().getUid());
		Assert.assertTrue(
				target.getCatalogVersions().contains(catalogVersionService.getCatalogVersion("testContentCatalog", "Staged")));
		Assert.assertTrue(
				target.getCatalogVersions().contains(catalogVersionService.getCatalogVersion("testProductCatalog", "Online")));
		Assert.assertNotNull(target.getPage());
	}

	@Test
	public void shouldPopulateModelWithSimpleLanguageCode()
	{
		final PreviewTicketWsDTO source = createPreviewTicketDTO();
		source.setLanguage("en");
		final PreviewDataModel target = new PreviewDataModel();

		previewTicketReversePopulator.populate(source, target);

		assertThat(target.getLanguage(), not(nullValue()));
		assertThat(target.getLanguage().getIsocode(), equalTo("en"));
	}

	@Test
	public void shouldPopulateModelWithComplexLanguageCode()
	{
		final LanguageModel language = modelService.create(LanguageModel.class);
		language.setIsocode("en_US");
		language.setActive(Boolean.TRUE);
		modelService.save(language);

		final PreviewTicketWsDTO source = createPreviewTicketDTO();
		source.setLanguage("en-US");
		final PreviewDataModel target = new PreviewDataModel();

		previewTicketReversePopulator.populate(source, target);

		assertThat(target.getLanguage(), not(nullValue()));
		assertThat(target.getLanguage().getIsocode(), equalTo("en_US"));
	}

	private PreviewTicketWsDTO createPreviewTicketDTO()
	{
		final PreviewTicketWsDTO source = new PreviewTicketWsDTO();
		source.setCatalog("testContentCatalog");
		source.setCatalogVersion("Staged");
		source.setResourcePath("https://127.0.0.1:9002/yacceleratorstorefront?site=testSite");
		source.setPageId("homepage");
		return source;
	}
}
