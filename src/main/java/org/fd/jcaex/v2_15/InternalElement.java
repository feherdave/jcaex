package org.fd.jcaex.v2_15;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import java.util.List;

/**
 * Type for definition of nested objects inside of a SystemUnitClass.
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

        @XmlAttribute(name = "RefBaseRoleClassPath")
        String refBaseRoleClassPath;

        public List<Attribute> getAttributes() {
            return attributes;
        }

        public List<InterfaceClass> getExternalInterFaces() {
            return externalInterFaces;
        }

        public String getRefBaseRoleClassPath() {
            return refBaseRoleClassPath;
        }
    }

    @XmlElement(name = "RoleRequirements")
    RoleRequirement roleRequirements;

    /**
     * Host element for AttributeNameMapping and InterfaceNameMapping.
     */
    @XmlElement(name = "MappingObject")
    Mapping mappingObjects;

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

    public RoleRequirement getRoleRequirements() {
        return roleRequirements;
    }

    public Mapping getMappingObjects() {
        return mappingObjects;
    }

    public String getRefBaseSystemUnitPath() {
        return refBaseSystemUnitPath;
    }

}
