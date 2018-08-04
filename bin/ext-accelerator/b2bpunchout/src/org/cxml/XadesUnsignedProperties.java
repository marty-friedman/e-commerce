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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "xadesUnsignedSignatureProperties",
    "xadesUnsignedDataObjectProperties"
})
@XmlRootElement(name = "xades:UnsignedProperties")
public class XadesUnsignedProperties {

    @XmlAttribute(name = "Id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    protected String id;
    @XmlElement(name = "xades:UnsignedSignatureProperties")
    protected XadesUnsignedSignatureProperties xadesUnsignedSignatureProperties;
    @XmlElement(name = "xades:UnsignedDataObjectProperties")
    protected XadesUnsignedDataObjectProperties xadesUnsignedDataObjectProperties;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the xadesUnsignedSignatureProperties property.
     * 
     * @return
     *     possible object is
     *     {@link XadesUnsignedSignatureProperties }
     *     
     */
    public XadesUnsignedSignatureProperties getXadesUnsignedSignatureProperties() {
        return xadesUnsignedSignatureProperties;
    }

    /**
     * Sets the value of the xadesUnsignedSignatureProperties property.
     * 
     * @param value
     *     allowed object is
     *     {@link XadesUnsignedSignatureProperties }
     *     
     */
    public void setXadesUnsignedSignatureProperties(XadesUnsignedSignatureProperties value) {
        this.xadesUnsignedSignatureProperties = value;
    }

    /**
     * Gets the value of the xadesUnsignedDataObjectProperties property.
     * 
     * @return
     *     possible object is
     *     {@link XadesUnsignedDataObjectProperties }
     *     
     */
    public XadesUnsignedDataObjectProperties getXadesUnsignedDataObjectProperties() {
        return xadesUnsignedDataObjectProperties;
    }

    /**
     * Sets the value of the xadesUnsignedDataObjectProperties property.
     * 
     * @param value
     *     allowed object is
     *     {@link XadesUnsignedDataObjectProperties }
     *     
     */
    public void setXadesUnsignedDataObjectProperties(XadesUnsignedDataObjectProperties value) {
        this.xadesUnsignedDataObjectProperties = value;
    }

}
