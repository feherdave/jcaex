package org.fd.jcaex.v3_0;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import java.util.List;

/**
 * Defines base structures for a hierarchical object instance. The instance maybe part of the InstanceHierarchy or a SystemUnitClass.
 */
public class InternalElement extends SystemUnitClass {

    /**
     * Describes role requirements of an InternalElement. It allows the definition of a reference to a RoleClass and the specification of role requirements like required attributes and required interfaces.
     */
    public static class RoleRequirement extends CAEXBasicObject {

        /**
         * Characterizes properties of the RoleRequirements.
         */
        @XmlElement(name = "Attribute")
        List<Attribute> attributes;

        @XmlElement(name = "ExternalInterface")
        List<InterfaceClass> externalInterFaces;

        /**
         * Host element for AttributeNameMapping and InterfaceIDMapping.
         */
        @XmlElement(name = "MappingObject")
        Mapping mappingObject;

        @XmlAttribute(name = "RefBaseRoleClassPath")
        String refBaseRoleClassPath;
    }

    @XmlElement(name = "RoleRequirements")
    RoleRequirement roleRequirements;

    /**
     * Stores the reference of an InternalElement to a class or instance definition. References contain the full path information.
     */
    @XmlAttribute(name = "RefBaseSystemUnitPath")
    String refBaseSystemUnitPath;

    @Override
    public String toString() {
        return "InternalElement{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
