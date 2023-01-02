package org.fd.jcaex.v3_0;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import java.util.List;

/**
 * Defines base structures for a SystemUnit class definition.
 */
public class SystemUnitClass extends CAEXObject {

    public static class SupportedRoleClass extends CAEXBasicObject {
        @XmlElement(name = "MappingObject")
        Mapping mapping;

        @XmlAttribute(name = "RefRoleClassPath", required = true)
        String refRoleClassPath;
    }

    public static class InternalLink extends CAEXObject {
        @XmlAttribute(name = "RefPartnerSideA", required = true)
        String refPartnerSideA;

        @XmlAttribute(name = "RefPartnerSideB", required = true)
        String refPartnerSideB;
    }

    /**
     * Characterizes properties of the SystemUnitClass.
     */
    @XmlElement(name = "Attribute")
    List<Attribute> attributes;

    /**
     * Description of an external interface.
     */
    @XmlElement(name = "ExternalInterface")
    List<InterfaceClass> externalInterfaces;

    /**
     * Shall be used in order to define nested objects inside of a SystemUnitClass or another InternalElement. Allows description of the internal structure of a CAEX object.
     */
    @XmlElement(name = "InternalElement")
    List<InternalElement> internalElements;

    @XmlElement(name = "SupportedRoleClass")
    List<SupportedRoleClass> supportedRoleClasses;

    @XmlElement(name = "InternalLink")
    List<InternalLink> internalLinks;
}
