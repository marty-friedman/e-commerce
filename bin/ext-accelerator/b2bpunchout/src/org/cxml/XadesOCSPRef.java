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
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.05.12 at 07:19:30 PM EDT 
//



package org.cxml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "xadesOCSPIdentifier",
    "xadesDigestAlgAndValue"
})
@XmlRootElement(name = "xades:OCSPRef")
public class XadesOCSPRef {

    @XmlElement(name = "xades:OCSPIdentifier", required = true)
    protected XadesOCSPIdentifier xadesOCSPIdentifier;
    @XmlElement(name = "xades:DigestAlgAndValue")
    protected XadesDigestAlgAndValue xadesDigestAlgAndValue;

    /**
     * Gets the value of the xadesOCSPIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link XadesOCSPIdentifier }
     *     
     */
    public XadesOCSPIdentifier getXadesOCSPIdentifier() {
        return xadesOCSPIdentifier;
    }

    /**
     * Sets the value of the xadesOCSPIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link XadesOCSPIdentifier }
     *     
     */
    public void setXadesOCSPIdentifier(XadesOCSPIdentifier value) {
        this.xadesOCSPIdentifier = value;
    }

    /**
     * Gets the value of the xadesDigestAlgAndValue property.
     * 
     * @return
     *     possible object is
     *     {@link XadesDigestAlgAndValue }
     *     
     */
    public XadesDigestAlgAndValue getXadesDigestAlgAndValue() {
        return xadesDigestAlgAndValue;
    }

    /**
     * Sets the value of the xadesDigestAlgAndValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link XadesDigestAlgAndValue }
     *     
     */
    public void setXadesDigestAlgAndValue(XadesDigestAlgAndValue value) {
        this.xadesDigestAlgAndValue = value;
    }

}
