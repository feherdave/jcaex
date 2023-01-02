package org.fd.jcaex.v3_0;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import java.util.List;

/**
 * Base element for AttributeNameMapping and InterfaceIDMapping.
 */
public class Mapping extends CAEXBasicObject {

    /**
     * Allows the definition of the mapping between attribute names of corresponding RoleClasses and SystemUnitClasses.
     */
    public static class AttributeNameMapping extends CAEXBasicObject {
        @XmlAttribute(name = "SystemUnitAttributeName", required = true)
        String systemUnitAttributeName = "";

        @XmlAttribute(name = "RoleAttributeName", required = true)
        String roleAttributeName = "";
    }

    /**
     * Allows the definition of the mapping between interfaces of a related role class and interfaces of the hosting system unit.
     */
    public static class InterfaceIDMapping extends CAEXBasicObject {
        @XmlAttribute(name = "SystemUnitInterfaceID", required = true)
        String systemUnitInterfaceID = "";

        @XmlAttribute(name = "RoleInterfaceID", required = true)
        String roleInterfaceID = "";
    }

    @XmlElement(name = "AttributeNameMapping")
    List<AttributeNameMapping> attributeNameMappings;

    @XmlElement(name = "InterfaceIDMapping")
    List<InterfaceIDMapping> interfaceIDMappings;
}
