/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.dataservicesbackoffice.audit.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.audit.internal.config.AtomicAttribute;
import de.hybris.platform.audit.internal.config.AuditReportConfig;
import de.hybris.platform.audit.internal.config.ReferenceAttribute;
import de.hybris.platform.audit.internal.config.Type;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.CollectionTypeModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.dataservicesbackoffice.dto.xmlschema.LocalElement;
import de.hybris.platform.dataservicesbackoffice.dto.xmlschema.ObjectFactory;
import de.hybris.platform.dataservicesbackoffice.dto.xmlschema.OpenAttrs;
import de.hybris.platform.dataservicesbackoffice.dto.xmlschema.Schema;
import de.hybris.platform.dataservicesbackoffice.dto.xmlschema.TopLevelComplexType;
import de.hybris.platform.dataservicesbackoffice.dto.xmlschema.TopLevelElement;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.xml.transform.StringResult;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;


@UnitTest
public class AuditReportXSDGenerationServiceUnitTest
{
	private static final Logger LOGGER = LoggerFactory.getLogger(AuditReportXSDGenerationServiceUnitTest.class);

	private DefaultAuditReportXSDGenerationService xsdGenerator;
	private ObjectFactory factory;
	private DocumentBuilder documentBuilder;
	private XPath xpath;
	private Document document;
	private static final String XS_STRING = "xs:string";

	@Before
	public void setup() throws ParserConfigurationException
	{
		xsdGenerator = new DefaultAuditReportXSDGenerationService();
		factory = new ObjectFactory();
		xpath = XPathFactory.newInstance().newXPath();
		final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		documentBuilder = dbf.newDocumentBuilder();
	}

	@Test
	public void shouldBuildSimpleHybrisField() throws JAXBException, IOException, SAXException, XPathExpressionException
	{
		parseXML(marshaller(xsdGenerator.buildSimpleHybrisField()));

		assertThat(getValue("//schema/complexType", "name")).isEqualTo("simpleHybrisField");
		assertThat(getValue("//schema/complexType/simpleContent/extension", "base")).isEqualTo(XS_STRING);
		assertThat(getValue("//schema/complexType/simpleContent/extension/attribute", "type")).isEqualTo(XS_STRING);
		assertThat(getValue("//schema/complexType/simpleContent/extension/attribute", "name")).isEqualTo("header");
	}

	@Test
	public void shouldBuildLocalizedHybrisField() throws JAXBException, IOException, SAXException, XPathExpressionException
	{
		parseXML(marshaller(xsdGenerator.buildLocalizedHybrisField()));

		final Node elementNode = getNode("//schema/complexType/sequence/element");
		final Node extensionAttributeNode = getNode(
				"//schema/complexType/sequence/element/complexType/simpleContent/extension/attribute");

		assertThat(getValue("//schema/complexType", "name")).isEqualTo("localizedHybrisField");
		assertThat(getValue(elementNode, "name")).isEqualTo("value");
		assertThat(getValue(elementNode, "minOccurs")).isEqualTo("0");
		assertThat(getValue(elementNode, "maxOccurs")).isEqualTo("unbounded");
		assertThat(getValue("//schema/complexType/sequence/element/complexType/simpleContent/extension", "base"))
				.isEqualTo(XS_STRING);
		assertThat(getValue(extensionAttributeNode, "type")).isEqualTo(XS_STRING);
		assertThat(getValue(extensionAttributeNode, "name")).isEqualTo("locale");
	}

	@Test
	public void shouldGenerateKeybase() throws JAXBException, IOException, SAXException, XPathExpressionException
	{
		final TopLevelElement element = factory.createTopLevelElement();
		element.getIdentityConstraint().add(factory.createUnique(xsdGenerator.createKeybase("user", "uid")));

		parseXML(marshaller(element));

		assertThat(getValue("//schema/element/unique", "name")).isEqualTo("unique_user_uid");
		assertThat(getValue("//schema/element/unique/selector", "xpath")).isEqualTo("user");
		assertThat(getValue("//schema/element/unique/field", "xpath")).isEqualTo("uid");
	}

	@Test
	public void shouldBuildAtomicAttributeElementWhenNonUniqueAndSimpleAttribute()
			throws JAXBException, IOException, SAXException, XPathExpressionException
	{
		final AttributeDescriptorModel attributeDescriptorModel = mock(AttributeDescriptorModel.class);
		final Type type = mock(Type.class);
		final TypeModel typeModel = mock(TypeModel.class);

		when(type.getCode()).thenReturn("User");

		when(attributeDescriptorModel.getQualifier()).thenReturn("name");
		when(attributeDescriptorModel.getAttributeType()).thenReturn(typeModel);
		when(typeModel.getCode()).thenReturn("pk");

		final LocalElement localElement = xsdGenerator.createAtomicAttributeElement(attributeDescriptorModel, type);

		final TopLevelComplexType complexType = factory.createTopLevelComplexType();
		complexType.setSequence(factory.createExplicitGroup());
		complexType.setName("User");
		complexType.getSequence().getParticle().add(factory.createGroupElement(localElement));

		parseXML(marshaller(complexType));

		assertThat(getValue("//schema/complexType", "name")).isEqualTo("User");
		assertThat(getValue("//schema/complexType/sequence/element", "name")).isEqualTo("name");
		assertThat(getValue("//schema/complexType/sequence/element", "type")).isEqualTo("simpleHybrisField");

	}

	@Test
	public void shouldBuildAtomicAttributeElementWhenNonUniqueAndLocalisedAttribute()
			throws JAXBException, IOException, SAXException, XPathExpressionException
	{
		final AttributeDescriptorModel attributeDescriptorModel = mock(AttributeDescriptorModel.class);
		final Type type = mock(Type.class);
		final TypeModel typeModel = mock(TypeModel.class);

		when(type.getCode()).thenReturn("User");

		when(attributeDescriptorModel.getQualifier()).thenReturn("name");
		when(attributeDescriptorModel.getLocalized()).thenReturn(true);
		when(attributeDescriptorModel.getAttributeType()).thenReturn(typeModel);
		when(typeModel.getCode()).thenReturn("pk");

		final LocalElement localElement = xsdGenerator.createAtomicAttributeElement(attributeDescriptorModel, type);

		final TopLevelComplexType complexType = factory.createTopLevelComplexType();
		complexType.setSequence(factory.createExplicitGroup());
		complexType.setName("User");
		complexType.getSequence().getParticle().add(factory.createGroupElement(localElement));

		parseXML(marshaller(complexType));

		assertThat(getValue("//schema/complexType", "name")).isEqualTo("User");
		assertThat(getValue("//schema/complexType/sequence/element", "name")).isEqualTo("name");
		assertThat(getValue("//schema/complexType/sequence/element", "type")).isEqualTo("localizedHybrisField");

	}

	@Test
	public void shouldBuildAtomicAttributeElementWhenUniqueAndSimpleAttribute()
			throws JAXBException, IOException, SAXException, XPathExpressionException
	{
		final AttributeDescriptorModel attributeDescriptorModel = mock(AttributeDescriptorModel.class);
		final Type type = mock(Type.class);

		when(type.getCode()).thenReturn("User");

		when(attributeDescriptorModel.getQualifier()).thenReturn("name");
		when(attributeDescriptorModel.getUnique()).thenReturn(true);

		final LocalElement localElement = xsdGenerator.createAtomicAttributeElement(attributeDescriptorModel, type);

		final TopLevelComplexType complexType = factory.createTopLevelComplexType();
		complexType.setSequence(factory.createExplicitGroup());
		complexType.setName("User");
		complexType.getSequence().getParticle().add(factory.createGroupElement(localElement));

		parseXML(marshaller(complexType));

		assertThat(getValue("//schema/complexType", "name")).isEqualTo("User");
		assertThat(getValue("//schema/complexType/sequence/element", "name")).isEqualTo("name");
		assertThat(getValue("//schema/complexType/sequence/element", "type")).isEqualTo("simpleHybrisField");
		assertThat(getValue("//schema/complexType/sequence/element/unique", "name")).isEqualTo("unique_user_name");
	}

	@Test
	public void shouldBuildAtomicAttributeElementWhenCollectionAttribute()
			throws JAXBException, IOException, SAXException, XPathExpressionException
	{
		final AttributeDescriptorModel attributeDescriptorModel = mock(AttributeDescriptorModel.class);
		final CollectionTypeModel collectionTypeModel = mock(CollectionTypeModel.class);
		final TypeModel typeModel = mock(TypeModel.class);
		final Type type = mock(Type.class);

		when(type.getCode()).thenReturn("User");

		when(attributeDescriptorModel.getQualifier()).thenReturn("listAddress");
		when(attributeDescriptorModel.getAttributeType()).thenReturn(collectionTypeModel);
		when(collectionTypeModel.getElementType()).thenReturn(typeModel);
		when(typeModel.getCode()).thenReturn("Address");

		final LocalElement localElement = xsdGenerator.createAtomicAttributeElement(attributeDescriptorModel, type);

		final TopLevelComplexType complexType = factory.createTopLevelComplexType();
		complexType.setSequence(factory.createExplicitGroup());
		complexType.setName("User");
		complexType.getSequence().getParticle().add(factory.createGroupElement(localElement));

		parseXML(marshaller(complexType));

		assertThat(getValue("//schema/complexType", "name")).isEqualTo("User");
		assertThat(getValue("//schema/complexType/sequence/element", "name")).isEqualTo("listAddress");
		assertThat(getValue("//schema/complexType/sequence/element", "type")).isEqualTo("Address");
		assertThat(getValue("//schema/complexType/sequence/element", "minOccurs")).isEqualTo("0");
		assertThat(getValue("//schema/complexType/sequence/element", "maxOccurs")).isEqualTo("unbounded");
	}

	@Test
	public void shouldBuildAtomicAttributeElementWhenCollectionPrimitiveAttribute()
			throws JAXBException, IOException, SAXException, XPathExpressionException
	{
		final AttributeDescriptorModel attributeDescriptorModel = mock(AttributeDescriptorModel.class);
		final CollectionTypeModel collectionTypeModel = mock(CollectionTypeModel.class);
		final TypeModel typeModel = mock(TypeModel.class);
		final Type type = mock(Type.class);

		when(type.getCode()).thenReturn("User");

		when(attributeDescriptorModel.getQualifier()).thenReturn("listString");
		when(attributeDescriptorModel.getAttributeType()).thenReturn(collectionTypeModel);
		when(collectionTypeModel.getElementType()).thenReturn(typeModel);
		when(typeModel.getCode()).thenReturn("java.lang.String");

		final LocalElement localElement = xsdGenerator.createAtomicAttributeElement(attributeDescriptorModel, type);

		final TopLevelComplexType complexType = factory.createTopLevelComplexType();
		complexType.setSequence(factory.createExplicitGroup());
		complexType.setName("User");
		complexType.getSequence().getParticle().add(factory.createGroupElement(localElement));

		parseXML(marshaller(complexType));

		assertThat(getValue("//schema/complexType", "name")).isEqualTo("User");
		assertThat(getValue("//schema/complexType/sequence/element", "name")).isEqualTo("listString");
		assertThat(getValue("//schema/complexType/sequence/element", "type")).isEqualTo("simpleHybrisField");
		assertThat(getValue("//schema/complexType/sequence/element", "minOccurs")).isEqualTo("0");
		assertThat(getValue("//schema/complexType/sequence/element", "maxOccurs")).isEqualTo("unbounded");
	}

	@Test
	public void shouldCreateElement() throws JAXBException, IOException, SAXException, XPathExpressionException
	{
		final LocalElement localElement = xsdGenerator.createElement("defaultPaymentAddress", "Address", false, false);

		final TopLevelComplexType complexType = factory.createTopLevelComplexType();
		complexType.setSequence(factory.createExplicitGroup());
		complexType.setName("User");
		complexType.getSequence().getParticle().add(factory.createGroupElement(localElement));

		parseXML(marshaller(complexType));

		assertThat(getValue("//schema/complexType/sequence/element", "name")).isEqualTo("defaultPaymentAddress");
		assertThat(getValue("//schema/complexType/sequence/element", "type")).isEqualTo("Address");
		assertThat(getValue("//schema/complexType/sequence/element", "minOccurs")).isEmpty();
		assertThat(getValue("//schema/complexType/sequence/element", "maxOccurs")).isEmpty();
	}

	@Test
	public void shouldBuildElementWhenIsUnique() throws JAXBException, IOException, SAXException, XPathExpressionException
	{
		final LocalElement localElement = xsdGenerator.createElement("defaultPaymentAddress", "Address", false, true);

		final TopLevelComplexType complexType = factory.createTopLevelComplexType();
		complexType.setSequence(factory.createExplicitGroup());
		complexType.setName("User");
		complexType.getSequence().getParticle().add(factory.createGroupElement(localElement));

		parseXML(marshaller(complexType));

		assertThat(getValue("//schema/complexType/sequence/element", "name")).isEqualTo("defaultPaymentAddress");
		assertThat(getValue("//schema/complexType/sequence/element", "type")).isEqualTo("Address");
		assertThat(getValue("//schema/complexType/sequence/element", "minOccurs")).isEmpty();
		assertThat(getValue("//schema/complexType/sequence/element", "maxOccurs")).isEmpty();
		assertThat(getValue("//schema/complexType/sequence/element/unique", "name"))
				.isEqualTo("unique_address_defaultpaymentaddress");
	}

	@Test
	public void shouldBuildElementWhenIsMany() throws JAXBException, IOException, SAXException, XPathExpressionException
	{
		final LocalElement localElement = xsdGenerator.createElement("defaultPaymentAddress", "Address", true, false);

		final TopLevelComplexType complexType = factory.createTopLevelComplexType();
		complexType.setSequence(factory.createExplicitGroup());
		complexType.setName("User");
		complexType.getSequence().getParticle().add(factory.createGroupElement(localElement));

		parseXML(marshaller(complexType));

		assertThat(getValue("//schema/complexType/sequence/element", "name")).isEqualTo("defaultPaymentAddress");
		assertThat(getValue("//schema/complexType/sequence/element", "type")).isEqualTo("Address");
		assertThat(getValue("//schema/complexType/sequence/element", "minOccurs")).isEqualTo("0");
		assertThat(getValue("//schema/complexType/sequence/element", "maxOccurs")).isEqualTo("unbounded");
	}

	@Test
	public void shouldCleanXSD()
	{
		final String xml = "<xs:element type=\"simpleHybrisField\" minOccurs=\"0\" maxOccurs=\"unbounded\" name=\"listString\" xmlns=\"\"/>";

		assertThat(xsdGenerator.cleanXSD(xml)).isEqualTo(
				"<xs:element type=\"simpleHybrisField\" minOccurs=\"0\" maxOccurs=\"unbounded\" name=\"listString\"/>");
	}

	@Test
	public void shouldMarshaller() throws JAXBException
	{
		final TopLevelComplexType complexType = factory.createTopLevelComplexType();
		complexType.setSequence(factory.createExplicitGroup());
		complexType.setName("User");

		final Schema schema = factory.createSchema();
		schema.getSimpleTypeOrComplexTypeOrGroup().add(complexType);

		final String xml = xsdGenerator.marshaller(schema);

		assertThat(xml).isXmlEqualTo(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"><xs:complexType name=\"User\"><xs:sequence/></xs:complexType></xs:schema>");
	}

	@Test
	public void shouldHasUniqueConstraint()
	{
		final LocalElement element = factory.createLocalElement();
		element.getIdentityConstraint().add(factory.createUnique(xsdGenerator.createKeybase("user", "uid")));

		final TopLevelComplexType topLevelComplexType = factory.createTopLevelComplexType();
		topLevelComplexType.setSequence(factory.createExplicitGroup());
		topLevelComplexType.getSequence().getParticle().add(factory.createGroupElement(element));


		assertThat(xsdGenerator.hasUniqueAttribute(topLevelComplexType)).isTrue();

	}

	@Test
	public void shouldHasNotUniqueConstraint()
	{
		final TopLevelComplexType topLevelComplexType = factory.createTopLevelComplexType();
		topLevelComplexType.setSequence(factory.createExplicitGroup());

		assertThat(xsdGenerator.hasUniqueAttribute(topLevelComplexType)).isFalse();
	}

	@Test
	public void shouldBeRequiredMandatoryAttribute()
	{
		final TypeModel typeModel = mock(TypeModel.class);
		when(typeModel.getCode()).thenReturn("nonPK");

		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);
		when(attributeDescriptor.getUnique()).thenReturn(false);
		when(attributeDescriptor.getOptional()).thenReturn(false);
		when(attributeDescriptor.getDatabaseColumn()).thenReturn("p_column");
		when(attributeDescriptor.getAttributeType()).thenReturn(typeModel);

		assertThat(xsdGenerator.isRequiredMandatoryAttribute(attributeDescriptor)).isTrue();
	}

	@Test
	public void shouldNotBeRequiredMandatoryAttributeWhenIsPK()
	{
		final TypeModel typeModel = mock(TypeModel.class);
		when(typeModel.getCode()).thenReturn(PK.class.getName());

		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);
		when(attributeDescriptor.getUnique()).thenReturn(false);
		when(attributeDescriptor.getOptional()).thenReturn(false);
		when(attributeDescriptor.getDatabaseColumn()).thenReturn("p_column");
		when(attributeDescriptor.getAttributeType()).thenReturn(typeModel);

		assertThat(xsdGenerator.isRequiredMandatoryAttribute(attributeDescriptor)).isFalse();
	}

	@Test
	public void shouldNotBeRequiredMandatoryAttributeWhenIsUnique()
	{
		final TypeModel typeModel = mock(TypeModel.class);
		when(typeModel.getCode()).thenReturn("nonPK");

		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);
		when(attributeDescriptor.getUnique()).thenReturn(true);
		when(attributeDescriptor.getOptional()).thenReturn(false);
		when(attributeDescriptor.getDatabaseColumn()).thenReturn("p_column");
		when(attributeDescriptor.getAttributeType()).thenReturn(typeModel);

		assertThat(xsdGenerator.isRequiredMandatoryAttribute(attributeDescriptor)).isFalse();
	}

	@Test
	public void shouldNotBeRequiredMandatoryAttributeWhenIsNotPersistProperty()
	{
		final TypeModel typeModel = mock(TypeModel.class);
		when(typeModel.getCode()).thenReturn("nonPK");

		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);
		when(attributeDescriptor.getUnique()).thenReturn(false);
		when(attributeDescriptor.getOptional()).thenReturn(false);
		when(attributeDescriptor.getDatabaseColumn()).thenReturn(null);
		when(attributeDescriptor.getAttributeType()).thenReturn(typeModel);

		assertThat(xsdGenerator.isRequiredMandatoryAttribute(attributeDescriptor)).isFalse();
	}

	@Test
	public void shouldNotBeRequiredMandatoryAttributeWhenIsOptional()
	{
		final TypeModel typeModel = mock(TypeModel.class);
		when(typeModel.getCode()).thenReturn("nonPK");

		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);
		when(attributeDescriptor.getUnique()).thenReturn(false);
		when(attributeDescriptor.getOptional()).thenReturn(true);
		when(attributeDescriptor.getDatabaseColumn()).thenReturn("p_column");
		when(attributeDescriptor.getAttributeType()).thenReturn(typeModel);

		assertThat(xsdGenerator.isRequiredMandatoryAttribute(attributeDescriptor)).isFalse();
	}

	@Test
	public void shouldNotBeRequiredMandatoryAttributeWhenHasDefaultValue()
	{
		final TypeModel typeModel = mock(TypeModel.class);
		when(typeModel.getCode()).thenReturn("nonPK");

		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);
		when(attributeDescriptor.getDefaultValue()).thenReturn("some default value");
		when(attributeDescriptor.getUnique()).thenReturn(false);
		when(attributeDescriptor.getOptional()).thenReturn(false);
		when(attributeDescriptor.getDatabaseColumn()).thenReturn("p_column");
		when(attributeDescriptor.getAttributeType()).thenReturn(typeModel);

		assertThat(xsdGenerator.isRequiredMandatoryAttribute(attributeDescriptor)).isFalse();
	}



	@Test
	public void shouldAtomicAttributeInAuditConfig()
	{
		final Type type = Type.builder()
				.withAtomicAttributes(AtomicAttribute.builder().withQualifier("testAttr").build())
				.build();
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);
		when(attributeDescriptor.getQualifier()).thenReturn("testAttr");

		assertThat(xsdGenerator.isAttributeInAuditConfig(type, attributeDescriptor)).isTrue();
	}

	@Test
	public void shouldAtomicAttributeNotInAuditConfig()
	{
		final Type type = Type.builder()
				.withAtomicAttributes(AtomicAttribute.builder().withQualifier("someAttr").build())
				.build();
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);
		when(attributeDescriptor.getQualifier()).thenReturn("otherAttr");

		assertThat(xsdGenerator.isAttributeInAuditConfig(type, attributeDescriptor)).isFalse();
	}

	@Test
	public void shouldReferenceAttributeInAuditConfig()
	{
		final String attr = "test";
		final Type type = Type.builder()
				.withReferenceAttributes(ReferenceAttribute.builder().withQualifier(attr).build())
				.build();
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);
		when(attributeDescriptor.getQualifier()).thenReturn(attr);

		assertThat(xsdGenerator.isAttributeInAuditConfig(type, attributeDescriptor)).isTrue();
	}

	@Test
	public void shouldReferenceAttributeNotInAuditConfig()
	{
		final Type type = Type.builder()
				.withReferenceAttributes(ReferenceAttribute.builder().withQualifier("someAttr").build())
				.build();
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);
		when(attributeDescriptor.getQualifier()).thenReturn("otherAttr");

		assertThat(xsdGenerator.isAttributeInAuditConfig(type, attributeDescriptor)).isFalse();
	}

	@Test
	public void shouldTypeCodeExistsInSchema()
	{
		final TopLevelComplexType complexType = factory.createTopLevelComplexType();
		complexType.setSequence(factory.createExplicitGroup());
		complexType.setName("User");

		final Schema schema = factory.createSchema();
		schema.getSimpleTypeOrComplexTypeOrGroup().add(complexType);

		assertThat(xsdGenerator.isTypeCodeInSchema(schema, "User")).isTrue();
	}

	@Test
	public void shouldTypeCodeDoseNotExistInSchema()
	{
		assertThat(xsdGenerator.isTypeCodeInSchema(factory.createSchema(), "typeCode")).isFalse();
	}

	@Test
	public void shouldTypeCodeExistsInAuditConfig()
	{

		final AuditReportConfig auditReportConfig = AuditReportConfig.builder()
				.withTypes(
						Type.builder().withCode("User").build()
				)
				.build();
		auditReportConfig.getAllTypes();

		assertThat(xsdGenerator.isTypeCodeInAuditConfig(auditReportConfig.getAllTypes(), "User")).isTrue();
	}

	@Test
	public void shouldTypeCodeDoseNotExistInAuditConfig()
	{
		final AuditReportConfig auditReportConfig = AuditReportConfig.builder()
				.withTypes(
						Type.builder().withCode("Order").build()
				)
				.build();
		auditReportConfig.getAllTypes();

		assertThat(xsdGenerator.isTypeCodeInAuditConfig(auditReportConfig.getAllTypes(), "User")).isFalse();
	}

	@Test
	public void shouldAtomicAttribute()
	{
		assertThat(xsdGenerator.isAtomicAttribute(String.class.getName())).isTrue();
		assertThat(xsdGenerator.isAtomicAttribute(Date.class.getName())).isTrue();
	}

	@Test
	public void shouldNotAtomicAttribute()
	{
		assertThat(xsdGenerator.isAtomicAttribute(UserModel.class.getName())).isFalse();
	}

	@Test
	public void shouldBuildRootElement()
	{
		final AuditReportConfig auditReportConfig = AuditReportConfig.builder()
				.withGivenRootType(
						Type.builder().withCode("User").build()
				)
				.build();
		final TopLevelElement topLevelElement = xsdGenerator.buildRootElement(auditReportConfig);

		assertThat(topLevelElement.getName()).isEqualTo("User");
		assertThat(topLevelElement.getType().getLocalPart()).isEqualTo("User");
	}

	private void parseXML(final String xml) throws IOException, SAXException
	{
		LOGGER.debug(xml);
		document = documentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
	}

	private String marshaller(final OpenAttrs openAttrs) throws JAXBException
	{
		final JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class.getPackage().getName());
		final Marshaller marshaller = jaxbContext.createMarshaller();

		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		final Schema schema = factory.createSchema();
		schema.getSimpleTypeOrComplexTypeOrGroup().add(openAttrs);

		final StringResult result = new StringResult();

		marshaller.marshal(schema, result);

		return result.toString();

	}

	private Node getNode(final String xpath) throws XPathExpressionException
	{
		return (Node) this.xpath.evaluate(xpath, document, XPathConstants.NODE);
	}

	private String getValue(final String xpath, final String attribute) throws XPathExpressionException
	{
		final Node node = getNode(xpath);
		if (node == null || node.getAttributes().getNamedItem(attribute) == null)
		{
			return StringUtils.EMPTY;
		}

		return node.getAttributes().getNamedItem(attribute).getNodeValue();
	}

	private String getValue(final Node node, final String attribute)
	{
		return node.getAttributes().getNamedItem(attribute).getNodeValue();
	}
}
