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
package de.hybris.platform.yaasyprofileconnect.yaas.client;
import de.hybris.platform.yaasconfiguration.service.YaasServiceFactory;

import java.util.List;

import de.hybris.platform.yaasyprofileconnect.yaas.ProfileReference;
import org.springframework.beans.factory.annotation.Required;

import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;


/**
 * Adapter around the Charon client to retrieve profile references in a blocking way without blocking every Charon
 * client.
 */
public class AsmProfileIdentityServiceClientAdapter implements AsmProfileIdentityServiceClient
{
    private YaasServiceFactory yaasServiceFactory;
    private final Scheduler scheduler = Schedulers.io();


    @Override
    public List<ProfileReference> getProfileReferences(final String identityKey, final String identityType,
                                                       final String identityOrigin)
    {
        return getAdaptee().getProfileReferencesAsync(identityKey, identityType, identityOrigin)
                .subscribeOn(scheduler)
                .toBlocking().first();
    }

    @Override
    public List<ProfileReference> getProfileReferences(final String identityKey, final String identityType,
                                                       final String identityOrigin, final int limit, final String sortBy, final String sortDirection)
    {
        return getAdaptee().getProfileReferencesAsync(identityKey, identityType, identityOrigin, limit, sortBy, sortDirection)
                .subscribeOn(scheduler)
                .toBlocking().first();
    }

    @Override
    public Observable<List<ProfileReference>> getProfileReferencesAsync(final String identityKey, final String identityType,
                                                                        final String identityOrigin)
    {
        return getAdaptee().getProfileReferencesAsync(identityKey, identityType, identityOrigin);
    }

    @Override
    public Observable<List<ProfileReference>> getProfileReferencesAsync(final String identityKey, final String identityType,
                                                                        final String identityOrigin, final int limit, final String sortBy, final String sortDirection)
    {
        return getAdaptee().getProfileReferencesAsync(identityKey, identityType, identityOrigin, limit, sortBy, sortDirection);
    }

    public AsmProfileIdentityServiceClient getAdaptee()
    {
        return yaasServiceFactory
                .lookupService(AsmProfileIdentityServiceClient.class);
    }

    protected YaasServiceFactory getYaasServiceFactory()
    {
        return yaasServiceFactory;
    }

    @Required
    public void setYaasServiceFactory(final YaasServiceFactory yaasServiceFactory)
    {
        this.yaasServiceFactory = yaasServiceFactory;
    }
}