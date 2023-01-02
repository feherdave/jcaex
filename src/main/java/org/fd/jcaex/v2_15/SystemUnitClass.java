package org.fd.jcaex.v2_15;

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

        public Mapping getMapping() {
            return mapping;
        }

        public String getRefRoleClassPath() {
            return refRoleClassPath;
        }
    }

    public static class InternalLink extends CAEXObject {
        @XmlAttribute(name = "RefPartnerSideA")
        String refPartnerSideA;

        @XmlAttribute(name = "RefPartnerSideB")
        String refPartnerSideB;
    }

    /**
     * Characterizes properties of the SystemUnitClass.
     */
    @XmlElement(name = "Attribute")
    protected List<Attribute> attributes;

    /**
     * Description of an external interface.
     */
    @XmlElement(name = "ExternalInterface")
    protected List<InterfaceClass> externalInterfaces;

    /**
     * Shall be used in order to define nested objects inside of a SystemUnitClass or another InternalElement. Allows description of the internal structure of a CAEX object.
     */
    @XmlElement(name = "InternalElement")
    protected List<InternalElement> internalElements;

    @XmlElement(name = "SupportedRoleClass")
    protected List<SupportedRoleClass> supportedRoleClasses;

    @XmlElement(name = "InternalLink")
    protected List<InternalLink> internalLinks;

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public List<InternalElement> getInternalElements() {
        return internalElements;
    }

    public List<InterfaceClass> getExternalInterfaces() {
        return externalInterfaces;
    }

    public List<InternalLink> getInternalLinks() {
        return internalLinks;
    }

    public List<SupportedRoleClass> getSupportedRoleClasses() {
        return supportedRoleClasses;
    }


}
