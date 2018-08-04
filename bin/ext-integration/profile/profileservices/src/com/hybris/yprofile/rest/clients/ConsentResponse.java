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
package com.hybris.yprofile.rest.clients;

import java.util.Objects;

public class ConsentResponse {

    private String id;
    private String link;
    private String consentReferenceToken;

    public ConsentResponse(final String id, final String link) {
        this.id = id;
        this.link = link;
    }

    public ConsentResponse(final String id) {
        this.id = id;
    }

    public ConsentResponse() {
        //Default constructor
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(final String link) {
        this.link = link;
    }

    public String getConsentReferenceToken() {
        return consentReferenceToken;
    }

    public void setConsentReferenceToken(String consentReferenceToken) {
        this.consentReferenceToken = consentReferenceToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConsentResponse that = (ConsentResponse) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(link, that.link);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, link, consentReferenceToken);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ConsentResponse{");
        sb.append("id='").append(id).append('\'');
        sb.append(", link='").append(link).append('\'');
        sb.append(", consentReferenceToken='").append(consentReferenceToken).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
