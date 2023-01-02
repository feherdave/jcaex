package org.fd.jcaex.v3_0;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

/**
 * Root-element of the CAEX schema.
 */
@XmlRootElement(name = "CAEXFile")
public class CAEXFile extends CAEXBasicObject implements org.fd.jcaex.CAEXFile {

    /**
     * Container element for the alias definition of external CAEX files.
     */
    public static class ExternalReference extends CAEXBasicObject {

        @XmlAttribute(name = "Path", required = true)
        String path;

        @XmlAttribute(name = "Alias", required = true)
        String alias;
    }

    /**
     * Root element for a system hierarchy of object instances.
     */
    public static class InstanceHierarchy extends CAEXObject {

        @XmlElement(name = "InternalElement")
        List<InternalElement> internalElements;
    }

    /**
     * Container element for a hierarchy of InterfaceClass definitions. It shall contain any interface class definitions. CAEX supports multiple interface libraries.
     */
    public static class InterfaceClassLib extends CAEXObject {

        @XmlElement(name = "InterfaceClass")
        List<InterfaceFamily> interfaceClasses;
    }

    /**
     * Container element for a hierarchy of RoleClass definitions. It shall contain any RoleClass definitions. CAEX supports multiple role libraries.
     */
    public static class RoleClassLib extends CAEXObject {
        @XmlElement(name = "RoleClass")
        List<RoleFamily> roleClasses;
    }

    /**
     * Container element for a hierarchy of SystemUnitClass definitions. It shall contain any SystemunitClass definitions. CAEX supports multiple SystemUnitClass libraries.
     */
    public static class SystemUnitClassLib extends CAEXObject {

        /**
         * Shall be used for SystemUnitClass definition, provides definition of a class of a SystemUnitClass type.
         */
        @XmlElement(name = "SystemUnitFamilyType")
        List<SystemUnitFamily> systemUnitClasses;
    }

    /**
     * Describes the version of a superior standard, e.g. AutomationML x.y. The version string is defined in the superior standard.
     */
    @XmlElement(name = "SuperiorStandardVersion")
    List<String> superiorStandardVersion;

    /**
     * Provides information about the source(s) of the CAEX document.
     */
    @XmlElement(name = "SourceDocumentInformation", required = true)
    List<SourceDocumentInformation> sourceDocumentInformations;

    @XmlElement(name = "ExternalReference")
    List<ExternalReference> externalReferences;

    @XmlElement(name = "InstanceHierarchy")
    List<InstanceHierarchy> instanceHierarchies;

    @XmlElement(name = "InterfaceClassLib")
    List<InterfaceClassLib> interfaceClassLibs;

    @XmlElement(name = "RoleClassLib")
    List<RoleClassLib> roleClassLibs;

    @XmlElement(name = "SystemUnitClassLib")
    List<SystemUnitClassLib> systemUnitClassLibs;

    /**
     * Describes the name of the CAEX file.
     */
    @XmlAttribute(name = "FileName")
    String fileName;

    /**
     * Describes the version of the schema. Each CAEX document must specify which CAEX version it requires. The version number of a CAEX document must fit to the version number specified in the CAEX schema file.
     */
    @XmlAttribute(name = "SchemaVersion", required = true)
    String schemaVersion = "3.0";

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public String getSchemaVersion() {
        return schemaVersion;
    }

}
