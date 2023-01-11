package org.fd.jcaex.v2_15;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import java.util.List;

/**
 * Defines base structures for attribute definitions.
 */
public class Attribute extends CAEXObject {

    /**
     * A reference to a definition of a defined attribute, e. g. to an attribute in a standardized library, this allows the semantic definition of the attribute.
     */
    public static class RefSemantic extends CAEXBasicObject {
        @XmlAttribute(name = "CorrespondingAttributePath", required = true)
        public String correspondingAttributePath = "";
    }

    /**
     * A predefined default value for an attribute.
     */
    @XmlElement(name = "DefaultValue")
    String defaultValue;

    /**
     * Element describing the value of an attribute.
     */
    @XmlElement(name = "Value")
    String value;

    @XmlElement(name = "RefSemantic")
    List<RefSemantic> refSemantics;

    /**
     * Element to restrict the range of validity of a defined attribute.
     */
    @XmlElement(name = "Constraint")
    List<AttributeValueRequirement> constraints;

    /**
     * Element that allows the description of nested attributes.
     */
    @XmlElement(name = "Attribute")
    List<Attribute> attributes;

    /**
     * Describes the unit of the attribute.
     */
    @XmlAttribute(name = "Unit")
    String unit;

    /**
     * Describes the data type of the attribute using XML notation.
     */
    @XmlAttribute(name = "AttributeDataType")
    String attributeDataType;

    @Override
    public String toString() {
        return "Attribute{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public Object getValue() {
        return value;
    }
}
