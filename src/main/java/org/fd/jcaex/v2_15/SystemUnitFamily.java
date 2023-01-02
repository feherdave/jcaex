package org.fd.jcaex.v2_15;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import java.util.List;

/**
 * Defines base structures for a hierarchical SystemUnitClass tree. The hierarchical structure of a SystemUnit library has organizational character only.
 */
public class SystemUnitFamily extends SystemUnitClass {

    /**
     * Element that allows definition of child SystemUnitClasses within the class hierarchy. The parent child relation between two SystemUnitClasses has no semantic.
     */
    @XmlElement(name = "SystemUnitClass")
    List<SystemUnitFamily> systemUnitClasses;

    /**
     * Stores the reference of a class to its base class. References contain the full path to the refered class object.
     */
    @XmlAttribute(name = "RefBaseClassPath")
    String refBaseClassPath;
}
