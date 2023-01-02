package org.fd.jcaex.v2_15;

import jakarta.xml.bind.annotation.XmlAttribute;


/**
 * CAEX basis object derived from CAEXBasicObject, augmented by
 * Name (required) and ID (optional).
 */
public class CAEXObject extends CAEXBasicObject {

    /**
     * Optional attribute that describes a unique identifier of the CAEX object.
     */
    @XmlAttribute(name = "ID")
    protected String id;

    /**
     * Describes the name of the CAEX object.
     */
    @XmlAttribute(name = "Name", required = true)
    protected String name;

    @Override
    public String toString() {
        return "CAEXObject{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
