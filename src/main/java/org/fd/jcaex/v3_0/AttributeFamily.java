package org.fd.jcaex.v3_0;

import jakarta.xml.bind.annotation.XmlElement;

import java.util.List;

/**
 * Defines base structures for attribute type definitions.
 */
public class AttributeFamily extends Attribute {

    @XmlElement(name = "AttributeType")
    List<AttributeFamily> attributeTypes;
}
