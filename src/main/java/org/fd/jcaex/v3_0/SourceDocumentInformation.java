package org.fd.jcaex.v3_0;

import jakarta.xml.bind.annotation.XmlAttribute;

import java.util.Date;

/**
 * Defines a structure to model information about the data source of the present CAEX document.
 */
public class SourceDocumentInformation {

    /**
     * Name of the origin of the CAEX document, e.g. the source engineering tool or an exporter software
     */
    @XmlAttribute(name = "OriginName", required = true)
    String originName;

    /**
     * Unique identifier of the origin of the CAEX document, e.g. a unique identifier of a source engineering tool or an exporter software. The ID shall not change even if the origin gets renamed.
     */
    @XmlAttribute(name = "OriginID", required = true)
    String originID;

    /**
     * Optional: the vendor of the data source of the CAEX document
     */
    @XmlAttribute(name = "OriginVendor")
    String originVendor;

    /**
     * Optional: the vendors URL of the data source of the CAEX document
     */
    @XmlAttribute(name = "OriginVendorURL")
    String originVendorURL;

    /**
     * Version of the origin of the CAEX document, e.g. the version of the source engineering tool or the exporter software.
     */
    @XmlAttribute(name = "OriginVersion", required = true)
    String originVersion;

    /**
     * Optional: release information of the origin of the CAEX document, e.g. the version of the source engineering tool or the exporter software.
     */
    @XmlAttribute(name = "OriginRelease")
    String originRelease;

    /**
     * Date and time of the creation of the CAEX document.
     */
    @XmlAttribute(name = "LastWritingDateTime", required = true)
    Date lastWritingDateTime;

    /**
     * Optional: the title of the corresponding source project
     */
    @XmlAttribute(name = "OriginProjectTitle")
    String originProjectTitle;

    /**
     * Optional: a unique identifier of the corresponding source project
     */
    @XmlAttribute(name = "OriginProjectID")
    String originProjectID;
}
