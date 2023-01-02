package org.fd.jcaex.v2_15;

import jakarta.xml.bind.annotation.*;
import org.fd.jcaex.GeneralizableCAEXObject;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Defines a group of organizational information, like description, version, revision, copyright, etc.
 */
public class Header implements GeneralizableCAEXObject {

    /**
     * Textual description for CAEX objects.
     */
    public static class Description {
        @XmlAttribute(name = "ChangeMode")
        ChangeMode changeMode;

        @XmlValue
        String value;
    }

    /**
     * Organizational information about the state of the version.
     */
    public static class Version {

        @XmlAttribute(name = "ChangeMode")
        ChangeMode changeMode;

        @XmlValue
        String value;
    }

    /**
     * Organizational information about the state of the revision.
     */
    public static class Revision extends CAEXBasicObject {

        @XmlElement(name = "RevisionDate")
        LocalDateTime revisionDate;

        @XmlElement(name = "OldVersion")
        String oldVersion;

        @XmlElement(name = "NewVersion")
        String newVersion;

        @XmlElement(name = "AuthorName")
        String authorName;

        @XmlElement(name = "Comment")
        String comment;
    }

    /**
     * Organizational information about copyright.
     */
    public static class Copyright {

        @XmlAttribute(name = "ChangeMode")
        ChangeMode changeMode;

        @XmlValue
        String value;
    }

    /**
     * Optional auxiliary field that may contain any additional information about a CAEX object.
     */
    public static class AdditionalInformation {
        @XmlAnyElement
        Object value;

        @XmlAnyAttribute
        Map<?, ?> attr;
    }

    Header() {}

    @XmlElement(name = "Description")
    Description description;

    @XmlElement(name = "Version")
    Version version;

    @XmlElement(name = "Revision")
    List<Revision> revisionList;

    @XmlElement(name = "Copyright")
    Copyright copyright;

    @XmlElement(name = "AdditionalInformation")
    List<AdditionalInformation> additionalInformationList;
}