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
package com.sap.hybris.sec.eventpublisher.dto.address;

import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * The mixins metadata for this address
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "mixins"
})
public class Metadata {

    @JsonProperty("mixins")
    private MetaDataMixins mixins;

    /**
     * 
     * @return
     *     The mixins
     */
    @JsonProperty("mixins")
    public MetaDataMixins getMixins() {
        return mixins;
    }

    /**
     * 
     * @param mixins
     *     The mixins
     */
    @JsonProperty("mixins")
    public void setMixins(MetaDataMixins mixins) {
        this.mixins = mixins;
    }

    public Metadata withMixins(MetaDataMixins mixins) {
        this.mixins = mixins;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
