package org.fd.jcaex.v2_15;

import jakarta.xml.bind.annotation.XmlElement;

import java.util.List;

/**
 * Defines base structures for a hierarchical RoleClass tree. The hierarchical structure of a role library has organizational character only.
 */
public class RoleFamily extends RoleClass {

    /**
     * Element that allows definition of child RoleClasses within the class hierarchy. The parent child relation between two RoleClasses has no semantic.
     */
    @XmlElement(name = "RoleClass")
    List<RoleFamily> roleClasses;

    @Override
    public String toString() {
        return "RoleFamily{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
