package org.fd.jcaex.v2_15;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.fd.jcaex.v2_15.CAEXBasicObject;

import java.util.List;

/**
 * Defines base structures for definition of value requirements of an attribute.
 */
public class AttributeValueRequirement extends CAEXBasicObject {

    /**
     * Element of to define constraints of ordinal scaled attribute values.
     */
    @XmlRootElement(name = "OrdinalScaledType")
    public static class OrdinalScaledType {

        /**
         * Element to define a maximum value of an attribute.
         */
        @XmlElement(name = "RequiredMaxValue")
        Object requiredMaxValue;

        /**
         * Element to define a required value of an attribute.
         */
        @XmlElement(name = "RequiredValue")
        Object requiredValue;

        /**
         * Element to define a minimum value of an attribute.
         */
        @XmlElement(name = "RequiredMinValue")
        Object requiredMinValue;
    }

    /**
     * Element of to define constraints of nominal scaled attribute values.
     */
    @XmlRootElement(name = "NominalScaledType")
    public static class NominalScaledType {

        /**
         * Element to define a required value of an attribute. It may be defined multiple times in order to define a discrete value range of the attribute.
         */
        @XmlElement(name = "RequiredValue")
        List<Object> requiredValues;
    }

    /**
     * Element to define constraints for attribute values of an unknown scale type.
     */
    @XmlRootElement(name = "UnknownType")
    public static class UnknownType {

        /**
         * Defines informative requirements as a constraint for an attribute value.
         */
        @XmlElement(name = "Requirements")
        String requirements;
    }

    @XmlElements({
            @XmlElement(name = "OrdinalScaledType", type = OrdinalScaledType.class),
            @XmlElement(name = "NominalScaledType", type = NominalScaledType.class),
            @XmlElement(name = "UnknownType", type = UnknownType.class)
    })
    Object type;

    /**
     * Describes the name of the contraint.
     */
    @XmlAttribute(name = "Name", required = true)
    String name;

    @Override
    public String toString() {
        return "AttributeValueRequirement{" +
                ", name='" + name + '\'' +
                '}';
    }
}
