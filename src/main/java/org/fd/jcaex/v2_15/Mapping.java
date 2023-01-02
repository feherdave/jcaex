package org.fd.jcaex.v2_15;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import org.fd.jcaex.v2_15.CAEXBasicObject;

import java.util.List;

/**
 * Base element for AttributeNameMapping and InterfaceNameMapping.
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
     * Mapping of interface names of corresponding RoleClasses and SystemUnitClasses.
     */
    public static class InterfaceNameMapping extends CAEXBasicObject {
        @XmlAttribute(name = "SystemUnitInterfaceName", required = true)
        String systemUnitInterfaceName = "";

        @XmlAttribute(name = "RoleInterfaceName", required = true)
        String roleInterfaceName = "";
    }

    @XmlElement(name = "AttributeNameMapping")
    List<AttributeNameMapping> attributeNameMappings;

    @XmlElement(name = "InterfaceNameMapping")
    List<InterfaceNameMapping> interfaceNameMappings;
}
