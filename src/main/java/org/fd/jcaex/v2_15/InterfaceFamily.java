package org.fd.jcaex.v2_15;

import jakarta.xml.bind.annotation.XmlElement;

import java.util.List;

/**
 * Defines base structures for a hierarchical InterfaceClass tree. The hierarchical structure of an interface library has organizational character only.
 */
public class InterfaceFamily extends InterfaceClass {

    /**
     * Element that allows definition of child InterfaceClasses within the class hierarchy. The parent child relation between two InterfaceClasses has no semantic.
     */
    @XmlElement(name = "InterfaceClass")
    List<InterfaceFamily> interfaceClasses;

    @Override
    public String toString() {
        return "InterfaceFamily{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
