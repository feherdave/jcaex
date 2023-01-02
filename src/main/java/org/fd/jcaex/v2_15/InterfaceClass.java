package org.fd.jcaex.v2_15;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import java.util.List;

/**
 * Shall be used for InterfaceClass definition, provides base structures for an interface class definition.
 */
public class InterfaceClass extends CAEXObject {

    /**
     * Characterizes properties of the InterfaceClass.
     */
    @XmlElement(name = "Attribute")
    protected List<Attribute> attributes;

    /**
     * Stores the reference of a class to its base class. References contain the full path to the referred class object.
     */
    @XmlAttribute(name = "RefBaseClassPath")
    protected String refBaseClassPath;

    @Override
    public String toString() {
        return "InterfaceClass{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
