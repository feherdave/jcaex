package jcaextest;

import jakarta.xml.bind.JAXBException;
import org.fd.jcaex.CAEXFile;
import org.fd.jcaex.CAEXFileParseException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class jcaextest {

    static String DIR_SEP = System.getProperty("file.separator");
    static String USER_HOME = System.getProperty("user.home");
    static String TESTFILE_DIR = ".jcaex";
    static String TESTFILE_PREFIX = "jcaextest_";

    Map<String, String> testFileContents = Map.of(
            "goodVersion_2.15",
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<CAEXFile xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FileName=\"Project4.aml\" SchemaVersion=\"2.15\" xsi:noNamespaceSchemaLocation=\"CAEX_ClassModel_V2.15.xsd\">\n" +
                    "</CAEXFile>",

            "goodVersion_3.0",
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<CAEXFile xmlns=\"http://www.dke.de/CAEX\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" SchemaVersion=\"3.0\" FileName=\"AutomationML2.10BaseLibraries with Part 3 Geometry attributes.aml\" xsi:schemaLocation=\"http://www.dke.de/CAEX CAEX_ClassModel_V.3.0.xsd\">" +
                    "<SuperiorStandardVersion>AutomationML 2.10</SuperiorStandardVersion>" +
                    "<SourceDocumentInformation OriginID=\"IEC SC65E WG 9\" OriginName=\"IEC SC65E WG 9\" OriginVersion=\"2.10.0\" LastWritingDateTime=\"2016-08-25T09:58:00.0Z\" OriginProjectID=\"Automation Markup Language Standard Library\" OriginRelease=\"2.10.0\" OriginVendor=\"IEC\" OriginVendorURL=\"www.iec.ch\" OriginProjectTitle=\"Automation Markup Language Standard Libraries\" />" +
                    "</CAEXFile>",

            "unsupportedVersion",
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<CAEXFile xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FileName=\"Project4.aml\" SchemaVersion=\"1.15\" xsi:noNamespaceSchemaLocation=\"CAEX_ClassModel_V2.15.xsd\">\n" +
                    "</CAEXFile>"
    );

    @Test
    public void performTests() {

        try {
            File goodVersion2_15 = generateTestFile("goodVersion_2.15");
            CAEXFile caexFile = CAEXFile.read(goodVersion2_15);

            assertEquals("2.15", caexFile.getSchemaVersion());
            assertEquals("Project4.aml", caexFile.getFileName());

            File goodVersion3_0 = generateTestFile("goodVersion_3.0");
            caexFile = CAEXFile.read(goodVersion3_0);

            assertEquals("3.0", caexFile.getSchemaVersion());
            assertEquals("AutomationML2.10BaseLibraries with Part 3 Geometry attributes.aml", caexFile.getFileName());

            File unsupportedVersion = generateTestFile("unsupportedVersion");
            assertThrows(CAEXFileParseException.class, () -> CAEXFile.read(unsupportedVersion), "Unsupported schema version: 1.15");

        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (CAEXFileParseException e) {
            throw new RuntimeException(e);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }

    @BeforeAll
    static void mkTestDir() {
        File testFilesDir = new File(USER_HOME + (USER_HOME.endsWith(DIR_SEP) ? "" : DIR_SEP) + TESTFILE_DIR);

        testFilesDir.mkdir();
    }

    @AfterAll
    static void cleanUpTestFiles() {

        File testFilesDir = new File(USER_HOME + (USER_HOME.endsWith(DIR_SEP) ? "" : DIR_SEP) + TESTFILE_DIR);

        if (testFilesDir.isDirectory()) {
            for (File f : testFilesDir.listFiles()) {
                if (!f.isDirectory()) {
                    f.delete();
                }
            }

            testFilesDir.delete();
        }
    }

    File generateTestFile(String testCaseKey) {

        try {
            Path res = Files.writeString(
                    Path.of(USER_HOME + (USER_HOME.endsWith(DIR_SEP) ? "" : DIR_SEP) + TESTFILE_DIR + DIR_SEP + TESTFILE_PREFIX + testCaseKey + ".aml"),
                    testFileContents.get(testCaseKey),
                    StandardCharsets.UTF_8
                    );

            return res.toFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
