package org.fd.jcaex.v3_0;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;

/**
 * Optionally describes the change state of a CAEX object.
 * If used, the ChangeMode shall have the following value range: state, create, delete and change.
 * This information should be used for further change management applications.
 */
@XmlEnum
public enum ChangeMode {
    @XmlEnumValue("state") STATE,
    @XmlEnumValue("create") CREATE,
    @XmlEnumValue("delete") DELETE,
    @XmlEnumValue("change") CHANGE
}
