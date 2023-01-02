package org.fd.jcaex.v3_0;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import java.util.List;

/**
 * Shall be used for RoleClass definition, provides base structures for a role class definition.
 */
public class RoleClass extends CAEXObject {

    /**
     * Characterizes properties of the RoleClass.
     */
    @XmlElement(name = "Attribute")
    List<Attribute> attributes;

    @XmlElement(name = "ExternalInterface")
    List<ExternalInterface> externalInterfaces;

    /**
     * Stores the reference of a class to its base class. References contain the full path to the refered class object.
     */
    @XmlAttribute(name = "RefBaseClassPath")
    String refBaseClassPath;

    @Override
    public String toString() {
        return "RoleClass{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
