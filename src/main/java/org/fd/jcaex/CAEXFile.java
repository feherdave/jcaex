package org.fd.jcaex;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public interface CAEXFile extends GeneralizableCAEXObject {

    String getFileName();
    String getSchemaVersion();

    static CAEXFile read(File file) throws ParserConfigurationException, IOException, SAXException, CAEXFileParseException, JAXBException, URISyntaxException {

        // Try to determine file version
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);

        NodeList nl = doc.getElementsByTagName("CAEXFile");

        if (nl.getLength() == 0) {
            throw new CAEXFileParseException("File format error: <CAEXFile> root tag missing.");
        }

        if (nl.getLength() > 1) {
            throw new CAEXFileParseException("File format error: Only 1 <CAEXFile> tag is allowed.");
        }

        Node caexFileNode = nl.item(0);

        if (caexFileNode instanceof Element) {
            Element e = (Element) caexFileNode;

            if (e.hasAttribute("SchemaVersion")) {
                String fileVersion = e.getAttribute("SchemaVersion");

                switch (fileVersion) {
                    case "2.15": return parseV2_15CAEXFile(file);
                    case "3.0": return parseV3_0CAEXFile(file);
                    default: throw new CAEXFileParseException("Unsupported schema version: " + fileVersion);
                }
            } else {
                throw new CAEXFileParseException("File format error: <CAEXFile> element doesn't have the [SchemaVersion] attribute.");
            }
        } else {
            throw new CAEXFileParseException("Unknown error");
        }
    }

    /**
     * Parses CAEX file with v2.15 schema version.
     *
     * @param file
     * @return
     * @throws JAXBException
     * @throws URISyntaxException
     * @throws SAXException
     */
    private static org.fd.jcaex.v2_15.CAEXFile parseV2_15CAEXFile(File file) throws JAXBException, URISyntaxException, SAXException {
        JAXBContext jaxbContext = JAXBContext.newInstance(org.fd.jcaex.v2_15.CAEXFile.class);
        URL url = ClassLoader.getSystemResource("CAEX_ClassModel_V2.15.xsd");

        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema caexFileSchema = sf.newSchema(new File(url.toURI()));
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        jaxbUnmarshaller.setSchema(caexFileSchema);

        return (org.fd.jcaex.v2_15.CAEXFile) jaxbUnmarshaller.unmarshal(file);
    }

    /**
     * Parses CAEX file with v3.0 schema version.
     *
     * @param file
     * @return
     * @throws JAXBException
     * @throws URISyntaxException
     * @throws SAXException
     */
    private static org.fd.jcaex.v3_0.CAEXFile parseV3_0CAEXFile(File file) throws JAXBException, URISyntaxException, SAXException {
        JAXBContext jaxbContext = JAXBContext.newInstance(org.fd.jcaex.v3_0.CAEXFile.class);
        URL url = ClassLoader.getSystemResource("CAEX_ClassModel_V.3.0.xsd");

        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema caexFileSchema = sf.newSchema(new File(url.toURI()));

        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        jaxbUnmarshaller.setSchema(caexFileSchema);

        return (org.fd.jcaex.v3_0.CAEXFile) jaxbUnmarshaller.unmarshal(file);
    }

}
