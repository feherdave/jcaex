package org.fd.jcaex.v3_0;

import jakarta.xml.bind.annotation.XmlAttribute;

/**
 * CAEX basis object that comprises a basic set of attributes and header information which exist for all CAEX elements.
 */
public class CAEXBasicObject extends Header {

    @XmlAttribute(name = "ChangeMode")
    protected ChangeMode changeMode;

}
