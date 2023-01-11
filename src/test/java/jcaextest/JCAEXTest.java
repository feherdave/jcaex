package jcaextest;

import jakarta.xml.bind.JAXBException;
import org.fd.jcaex.CAEXFile;
import org.fd.jcaex.CAEXFileParseException;
import org.fd.jcaex.GenericCAEXObject;
import org.fd.jcaex.filter.CAEXFilter;
import org.fd.jcaex.filter.TextNodeFilter;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class JCAEXTest {

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
                    "</CAEXFile>",

            "filterTest1",
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<CAEXFile xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" FileName=\"Project4.aml\" SchemaVersion=\"2.15\" xsi:noNamespaceSchemaLocation=\"CAEX_ClassModel_V2.15.xsd\">\n" +
                    "  <InstanceHierarchy Name=\"APC Sample Instance Hierarchy\">\n" +
                    "    <InternalElement ID=\"3090f37a-3c79-4ed7-ba42-74ee496e8a41\" Name=\"Project4\">\n" +
                    "      <Attribute Name=\"ProjectManufacturer\" AttributeDataType=\"xs:string\" />\n" +
                    "      <Attribute Name=\"ProjectSign\" AttributeDataType=\"xs:string\" />\n" +
                    "      <Attribute Name=\"ProjectRevision\" AttributeDataType=\"xs:string\" />\n" +
                    "      <Attribute Name=\"ProjectInformation\" AttributeDataType=\"xs:string\" />\n" +
                    "      <InternalElement ID=\"4d182ffe-a47e-4b70-b569-cd0b99bb804a\" Name=\"S7300/ET200M station_1\">\n" +
                    "        <Attribute Name=\"TypeIdentifier\" AttributeDataType=\"xs:string\">\n" +
                    "          <Value>System:Device.S7300</Value>\n" +
                    "        </Attribute>\n" +
                    "        <InternalElement ID=\"275ec152-dc69-484e-a606-ae84d082caa7\" Name=\"Rail_0\">\n" +
                    "          <Attribute Name=\"TypeName\" AttributeDataType=\"xs:string\">\n" +
                    "            <Value>Rail</Value>\n" +
                    "          </Attribute>\n" +
                    "          <Attribute Name=\"PositionNumber\" AttributeDataType=\"xs:int\">\n" +
                    "            <Value>0</Value>\n" +
                    "          </Attribute>\n" +
                    "          <Attribute Name=\"BuiltIn\" AttributeDataType=\"xs:boolean\">\n" +
                    "            <Value>false</Value>\n" +
                    "          </Attribute>\n" +
                    "          <Attribute Name=\"TypeIdentifier\" AttributeDataType=\"xs:string\">\n" +
                    "            <Value>OrderNumber:6ES7 390-1***0-0AA0</Value>\n" +
                    "          </Attribute>\n" +
                    "          <InternalElement ID=\"cf1c1667-bdba-4e12-b607-a0232ca63ee3\" Name=\"PLC_1\">\n" +
                    "            <Attribute Name=\"TypeName\" AttributeDataType=\"xs:string\">\n" +
                    "              <Value>CPU 313C</Value>\n" +
                    "            </Attribute>\n" +
                    "            <Attribute Name=\"DeviceItemType\" AttributeDataType=\"xs:string\">\n" +
                    "              <Value>CPU</Value>\n" +
                    "            </Attribute>\n" +
                    "            <Attribute Name=\"PositionNumber\" AttributeDataType=\"xs:int\">\n" +
                    "              <Value>2</Value>\n" +
                    "            </Attribute>\n" +
                    "            <Attribute Name=\"BuiltIn\" AttributeDataType=\"xs:boolean\">\n" +
                    "              <Value>false</Value>\n" +
                    "            </Attribute>\n" +
                    "            <Attribute Name=\"TypeIdentifier\" AttributeDataType=\"xs:string\">\n" +
                    "              <Value>OrderNumber:6ES7 313-5BF03-0AB0</Value>\n" +
                    "            </Attribute>\n" +
                    "            <Attribute Name=\"FirmwareVersion\" AttributeDataType=\"xs:string\">\n" +
                    "              <Value>V2.6</Value>\n" +
                    "            </Attribute>\n" +
                    "            <InternalElement ID=\"da269ec6-2ea7-42eb-8a04-19a6dc7c1116\" Name=\"Default tag table\">\n" +
                    "              <SupportedRoleClass RefRoleClassPath=\"AutomationProjectConfigurationRoleClassLib/TagTable\" />\n" +
                    "            </InternalElement>\n" +
                    "            <InternalElement ID=\"d547b4ba-05b0-4bd3-9c54-fc34d4ecfb7b\" Name=\"MPI interface_1\">\n" +
                    "              <Attribute Name=\"Label\" AttributeDataType=\"xs:string\">\n" +
                    "                <Value>X1</Value>\n" +
                    "              </Attribute>\n" +
                    "              <Attribute Name=\"PositionNumber\" AttributeDataType=\"xs:int\">\n" +
                    "                <Value>0</Value>\n" +
                    "              </Attribute>\n" +
                    "              <Attribute Name=\"BuiltIn\" AttributeDataType=\"xs:boolean\">\n" +
                    "                <Value>true</Value>\n" +
                    "              </Attribute>\n" +
                    "              <InternalElement ID=\"010548ea-bcb1-4660-ba36-f8155113f7f2\" Name=\"MPI1\">\n" +
                    "                <Attribute Name=\"Type\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Mpi</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"NetworkAddress\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>2</Value>\n" +
                    "                </Attribute>\n" +
                    "                <SupportedRoleClass RefRoleClassPath=\"AutomationProjectConfigurationRoleClassLib/Node\" />\n" +
                    "              </InternalElement>\n" +
                    "              <SupportedRoleClass RefRoleClassPath=\"AutomationProjectConfigurationRoleClassLib/CommunicationInterface\" />\n" +
                    "            </InternalElement>\n" +
                    "            <InternalElement ID=\"86591dbb-470e-4e3b-bd90-c709ad6adb6d\" Name=\"DI 24/DO 16_1\">\n" +
                    "              <Attribute Name=\"PositionNumber\" AttributeDataType=\"xs:int\">\n" +
                    "                <Value>2</Value>\n" +
                    "              </Attribute>\n" +
                    "              <Attribute Name=\"BuiltIn\" AttributeDataType=\"xs:boolean\">\n" +
                    "                <Value>true</Value>\n" +
                    "              </Attribute>\n" +
                    "              <Attribute Name=\"Address\">\n" +
                    "                <RefSemantic CorrespondingAttributePath=\"OrderedListType\" />\n" +
                    "                <Attribute Name=\"1\">\n" +
                    "                  <Attribute Name=\"StartAddress\" AttributeDataType=\"xs:int\">\n" +
                    "                    <Value>124</Value>\n" +
                    "                  </Attribute>\n" +
                    "                  <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                    <Value>24</Value>\n" +
                    "                  </Attribute>\n" +
                    "                  <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                    <Value>Input</Value>\n" +
                    "                  </Attribute>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"2\">\n" +
                    "                  <Attribute Name=\"StartAddress\" AttributeDataType=\"xs:int\">\n" +
                    "                    <Value>124</Value>\n" +
                    "                  </Attribute>\n" +
                    "                  <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                    <Value>16</Value>\n" +
                    "                  </Attribute>\n" +
                    "                  <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                    <Value>Output</Value>\n" +
                    "                  </Attribute>\n" +
                    "                </Attribute>\n" +
                    "              </Attribute>\n" +
                    "              <ExternalInterface ID=\"bbdf5660-ba2c-4ecd-9ac0-5d5c8f502d57\" Name=\"Channel_DI_0\" RefBaseClassPath=\"AutomationProjectConfigurationInterfaceClassLib/Channel\">\n" +
                    "                <Attribute Name=\"Type\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Digital</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Input</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Number\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>0</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>1</Value>\n" +
                    "                </Attribute>\n" +
                    "              </ExternalInterface>\n" +
                    "              <ExternalInterface ID=\"6039d61f-dbbe-4de4-888b-1d646eda8abb\" Name=\"Channel_DI_1\" RefBaseClassPath=\"AutomationProjectConfigurationInterfaceClassLib/Channel\">\n" +
                    "                <Attribute Name=\"Type\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Digital</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Input</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Number\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>1</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>1</Value>\n" +
                    "                </Attribute>\n" +
                    "              </ExternalInterface>\n" +
                    "              <ExternalInterface ID=\"0c13266e-1807-41b2-87e7-97ffa7f79b71\" Name=\"Channel_DI_2\" RefBaseClassPath=\"AutomationProjectConfigurationInterfaceClassLib/Channel\">\n" +
                    "                <Attribute Name=\"Type\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Digital</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Input</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Number\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>2</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>1</Value>\n" +
                    "                </Attribute>\n" +
                    "              </ExternalInterface>\n" +
                    "              <ExternalInterface ID=\"94524773-830e-4159-9022-ac6ee7b8032c\" Name=\"Channel_DI_3\" RefBaseClassPath=\"AutomationProjectConfigurationInterfaceClassLib/Channel\">\n" +
                    "                <Attribute Name=\"Type\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Digital</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Input</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Number\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>3</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>1</Value>\n" +
                    "                </Attribute>\n" +
                    "              </ExternalInterface>\n" +
                    "              <ExternalInterface ID=\"8003fb70-fae0-4711-bcc1-d1dc294ab8d5\" Name=\"Channel_DI_4\" RefBaseClassPath=\"AutomationProjectConfigurationInterfaceClassLib/Channel\">\n" +
                    "                <Attribute Name=\"Type\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Digital</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Input</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Number\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>4</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>1</Value>\n" +
                    "                </Attribute>\n" +
                    "              </ExternalInterface>\n" +
                    "              <ExternalInterface ID=\"ba1e0911-ebf0-4443-a1c3-3a8c7c10d786\" Name=\"Channel_DI_5\" RefBaseClassPath=\"AutomationProjectConfigurationInterfaceClassLib/Channel\">\n" +
                    "                <Attribute Name=\"Type\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Digital</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Input</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Number\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>5</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>1</Value>\n" +
                    "                </Attribute>\n" +
                    "              </ExternalInterface>\n" +
                    "              <ExternalInterface ID=\"8bcafcd5-e4ce-4e45-96aa-8bb9d4cac529\" Name=\"Channel_DI_6\" RefBaseClassPath=\"AutomationProjectConfigurationInterfaceClassLib/Channel\">\n" +
                    "                <Attribute Name=\"Type\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Digital</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Input</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Number\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>6</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>1</Value>\n" +
                    "                </Attribute>\n" +
                    "              </ExternalInterface>\n" +
                    "              <ExternalInterface ID=\"11078e02-e519-417d-b5ad-5c647a6b7c48\" Name=\"Channel_DI_7\" RefBaseClassPath=\"AutomationProjectConfigurationInterfaceClassLib/Channel\">\n" +
                    "                <Attribute Name=\"Type\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Digital</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Input</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Number\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>7</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>1</Value>\n" +
                    "                </Attribute>\n" +
                    "              </ExternalInterface>\n" +
                    "              <ExternalInterface ID=\"4ab3bc54-d099-4c64-8347-b3414b45d540\" Name=\"Channel_DI_8\" RefBaseClassPath=\"AutomationProjectConfigurationInterfaceClassLib/Channel\">\n" +
                    "                <Attribute Name=\"Type\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Digital</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Input</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Number\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>8</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>1</Value>\n" +
                    "                </Attribute>\n" +
                    "              </ExternalInterface>\n" +
                    "              <ExternalInterface ID=\"12ce65ab-5901-4aac-89bc-9aa7cd910806\" Name=\"Channel_DI_9\" RefBaseClassPath=\"AutomationProjectConfigurationInterfaceClassLib/Channel\">\n" +
                    "                <Attribute Name=\"Type\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Digital</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Input</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Number\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>9</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>1</Value>\n" +
                    "                </Attribute>\n" +
                    "              </ExternalInterface>\n" +
                    "              <ExternalInterface ID=\"fb4cfe30-e38c-4a3c-a300-d6d62068650d\" Name=\"Channel_DI_10\" RefBaseClassPath=\"AutomationProjectConfigurationInterfaceClassLib/Channel\">\n" +
                    "                <Attribute Name=\"Type\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Digital</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Input</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Number\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>10</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>1</Value>\n" +
                    "                </Attribute>\n" +
                    "              </ExternalInterface>\n" +
                    "              <ExternalInterface ID=\"d91a9112-b451-44bc-9a59-3e01c490ab43\" Name=\"Channel_DI_11\" RefBaseClassPath=\"AutomationProjectConfigurationInterfaceClassLib/Channel\">\n" +
                    "                <Attribute Name=\"Type\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Digital</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Input</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Number\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>11</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>1</Value>\n" +
                    "                </Attribute>\n" +
                    "              </ExternalInterface>\n" +
                    "              <ExternalInterface ID=\"74e3d801-2a92-4eca-921b-5bf98dbdef0d\" Name=\"Channel_DI_12\" RefBaseClassPath=\"AutomationProjectConfigurationInterfaceClassLib/Channel\">\n" +
                    "                <Attribute Name=\"Type\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Digital</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Input</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Number\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>12</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>1</Value>\n" +
                    "                </Attribute>\n" +
                    "              </ExternalInterface>\n" +
                    "              <ExternalInterface ID=\"72a8afb9-c444-45bc-a8b6-5d4435056d19\" Name=\"Channel_DI_13\" RefBaseClassPath=\"AutomationProjectConfigurationInterfaceClassLib/Channel\">\n" +
                    "                <Attribute Name=\"Type\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Digital</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Input</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Number\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>13</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>1</Value>\n" +
                    "                </Attribute>\n" +
                    "              </ExternalInterface>\n" +
                    "              <ExternalInterface ID=\"98f6e4fd-ca39-4ba8-8c66-b0bfa7bd078c\" Name=\"Channel_DI_14\" RefBaseClassPath=\"AutomationProjectConfigurationInterfaceClassLib/Channel\">\n" +
                    "                <Attribute Name=\"Type\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Digital</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Input</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Number\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>14</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>1</Value>\n" +
                    "                </Attribute>\n" +
                    "              </ExternalInterface>\n" +
                    "              <ExternalInterface ID=\"f794590a-d193-466c-b85c-4b27765ae704\" Name=\"Channel_DI_15\" RefBaseClassPath=\"AutomationProjectConfigurationInterfaceClassLib/Channel\">\n" +
                    "                <Attribute Name=\"Type\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Digital</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Input</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Number\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>15</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>1</Value>\n" +
                    "                </Attribute>\n" +
                    "              </ExternalInterface>\n" +
                    "              <ExternalInterface ID=\"59958c27-b858-4922-8e2d-a9b45be4e213\" Name=\"Channel_DI_16\" RefBaseClassPath=\"AutomationProjectConfigurationInterfaceClassLib/Channel\">\n" +
                    "                <Attribute Name=\"Type\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Digital</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Input</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Number\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>16</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>1</Value>\n" +
                    "                </Attribute>\n" +
                    "              </ExternalInterface>\n" +
                    "              <ExternalInterface ID=\"616e24c3-d114-4b76-9ae5-da4f1aa503c3\" Name=\"Channel_DI_17\" RefBaseClassPath=\"AutomationProjectConfigurationInterfaceClassLib/Channel\">\n" +
                    "                <Attribute Name=\"Type\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Digital</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Input</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Number\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>17</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>1</Value>\n" +
                    "                </Attribute>\n" +
                    "              </ExternalInterface>\n" +
                    "              <ExternalInterface ID=\"1b22916b-1c21-49b1-9f81-53f905b6e3e1\" Name=\"Channel_DI_18\" RefBaseClassPath=\"AutomationProjectConfigurationInterfaceClassLib/Channel\">\n" +
                    "                <Attribute Name=\"Type\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Digital</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Input</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Number\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>18</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>1</Value>\n" +
                    "                </Attribute>\n" +
                    "              </ExternalInterface>\n" +
                    "              <ExternalInterface ID=\"63a548cb-e3f0-47f6-acb9-811fac48401d\" Name=\"Channel_DI_19\" RefBaseClassPath=\"AutomationProjectConfigurationInterfaceClassLib/Channel\">\n" +
                    "                <Attribute Name=\"Type\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Digital</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Input</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Number\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>19</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>1</Value>\n" +
                    "                </Attribute>\n" +
                    "              </ExternalInterface>\n" +
                    "              <ExternalInterface ID=\"8ba3ae19-b77d-4f7c-b7fc-e4480153714e\" Name=\"Channel_DI_20\" RefBaseClassPath=\"AutomationProjectConfigurationInterfaceClassLib/Channel\">\n" +
                    "                <Attribute Name=\"Type\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Digital</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Input</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Number\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>20</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>1</Value>\n" +
                    "                </Attribute>\n" +
                    "              </ExternalInterface>\n" +
                    "              <ExternalInterface ID=\"9d75afd4-10eb-4eb6-9612-9e4171d8a7db\" Name=\"Channel_DI_21\" RefBaseClassPath=\"AutomationProjectConfigurationInterfaceClassLib/Channel\">\n" +
                    "                <Attribute Name=\"Type\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Digital</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Input</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Number\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>21</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>1</Value>\n" +
                    "                </Attribute>\n" +
                    "              </ExternalInterface>\n" +
                    "              <ExternalInterface ID=\"c2e4b50f-dcc0-43f5-92ee-c54d3503d6a4\" Name=\"Channel_DI_22\" RefBaseClassPath=\"AutomationProjectConfigurationInterfaceClassLib/Channel\">\n" +
                    "                <Attribute Name=\"Type\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Digital</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Input</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Number\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>22</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>1</Value>\n" +
                    "                </Attribute>\n" +
                    "              </ExternalInterface>\n" +
                    "              <ExternalInterface ID=\"45e4dc22-5deb-493f-9ae9-beb6bf95126c\" Name=\"Channel_DI_23\" RefBaseClassPath=\"AutomationProjectConfigurationInterfaceClassLib/Channel\">\n" +
                    "                <Attribute Name=\"Type\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Digital</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Input</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Number\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>23</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>1</Value>\n" +
                    "                </Attribute>\n" +
                    "              </ExternalInterface>\n" +
                    "              <ExternalInterface ID=\"3d31b103-3fb6-4d43-89c9-24e15f4db531\" Name=\"Channel_DO_0\" RefBaseClassPath=\"AutomationProjectConfigurationInterfaceClassLib/Channel\">\n" +
                    "                <Attribute Name=\"Type\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Digital</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Output</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Number\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>0</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>1</Value>\n" +
                    "                </Attribute>\n" +
                    "              </ExternalInterface>\n" +
                    "              <ExternalInterface ID=\"65c2d599-5b17-4ecd-be66-9a9663a639f1\" Name=\"Channel_DO_1\" RefBaseClassPath=\"AutomationProjectConfigurationInterfaceClassLib/Channel\">\n" +
                    "                <Attribute Name=\"Type\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Digital</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Output</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Number\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>1</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>1</Value>\n" +
                    "                </Attribute>\n" +
                    "              </ExternalInterface>\n" +
                    "              <ExternalInterface ID=\"e844caaf-148a-458b-88ee-2b13a89dc00f\" Name=\"Channel_DO_2\" RefBaseClassPath=\"AutomationProjectConfigurationInterfaceClassLib/Channel\">\n" +
                    "                <Attribute Name=\"Type\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Digital</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Output</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Number\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>2</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>1</Value>\n" +
                    "                </Attribute>\n" +
                    "              </ExternalInterface>\n" +
                    "              <ExternalInterface ID=\"5778d646-5ce4-46e0-828c-bf5f4eb0f151\" Name=\"Channel_DO_3\" RefBaseClassPath=\"AutomationProjectConfigurationInterfaceClassLib/Channel\">\n" +
                    "                <Attribute Name=\"Type\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Digital</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Output</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Number\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>3</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>1</Value>\n" +
                    "                </Attribute>\n" +
                    "              </ExternalInterface>\n" +
                    "              <ExternalInterface ID=\"c113144a-f4e7-46b0-9023-20984fbdbbf0\" Name=\"Channel_DO_4\" RefBaseClassPath=\"AutomationProjectConfigurationInterfaceClassLib/Channel\">\n" +
                    "                <Attribute Name=\"Type\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Digital</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Output</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Number\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>4</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>1</Value>\n" +
                    "                </Attribute>\n" +
                    "              </ExternalInterface>\n" +
                    "              <ExternalInterface ID=\"c66e214e-6954-4f5b-9fc3-e557c196abed\" Name=\"Channel_DO_5\" RefBaseClassPath=\"AutomationProjectConfigurationInterfaceClassLib/Channel\">\n" +
                    "                <Attribute Name=\"Type\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Digital</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Output</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Number\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>5</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>1</Value>\n" +
                    "                </Attribute>\n" +
                    "              </ExternalInterface>\n" +
                    "              <ExternalInterface ID=\"fe747d57-d61c-4bf3-901b-0185d71cdc40\" Name=\"Channel_DO_6\" RefBaseClassPath=\"AutomationProjectConfigurationInterfaceClassLib/Channel\">\n" +
                    "                <Attribute Name=\"Type\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Digital</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Output</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Number\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>6</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>1</Value>\n" +
                    "                </Attribute>\n" +
                    "              </ExternalInterface>\n" +
                    "              <ExternalInterface ID=\"8f831a43-5db4-48bf-a9f9-86f54842938f\" Name=\"Channel_DO_7\" RefBaseClassPath=\"AutomationProjectConfigurationInterfaceClassLib/Channel\">\n" +
                    "                <Attribute Name=\"Type\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Digital</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Output</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Number\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>7</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>1</Value>\n" +
                    "                </Attribute>\n" +
                    "              </ExternalInterface>\n" +
                    "              <ExternalInterface ID=\"0fcbd27e-2265-4eae-9ea5-ae6446174e10\" Name=\"Channel_DO_8\" RefBaseClassPath=\"AutomationProjectConfigurationInterfaceClassLib/Channel\">\n" +
                    "                <Attribute Name=\"Type\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Digital</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Output</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Number\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>8</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>1</Value>\n" +
                    "                </Attribute>\n" +
                    "              </ExternalInterface>\n" +
                    "              <ExternalInterface ID=\"0c9174ac-83c9-4e03-8b0b-7aa5b7c80821\" Name=\"Channel_DO_9\" RefBaseClassPath=\"AutomationProjectConfigurationInterfaceClassLib/Channel\">\n" +
                    "                <Attribute Name=\"Type\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Digital</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Output</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Number\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>9</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>1</Value>\n" +
                    "                </Attribute>\n" +
                    "              </ExternalInterface>\n" +
                    "              <ExternalInterface ID=\"c2cd62c1-95d3-4423-88b4-beb4d0e84695\" Name=\"Channel_DO_10\" RefBaseClassPath=\"AutomationProjectConfigurationInterfaceClassLib/Channel\">\n" +
                    "                <Attribute Name=\"Type\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Digital</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Output</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Number\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>10</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>1</Value>\n" +
                    "                </Attribute>\n" +
                    "              </ExternalInterface>\n" +
                    "              <ExternalInterface ID=\"dcdb0b5f-ba47-4386-a563-99487778094e\" Name=\"Channel_DO_11\" RefBaseClassPath=\"AutomationProjectConfigurationInterfaceClassLib/Channel\">\n" +
                    "                <Attribute Name=\"Type\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Digital</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Output</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Number\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>11</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>1</Value>\n" +
                    "                </Attribute>\n" +
                    "              </ExternalInterface>\n" +
                    "              <ExternalInterface ID=\"7f57845f-c37c-4dff-afbd-e0a16496381d\" Name=\"Channel_DO_12\" RefBaseClassPath=\"AutomationProjectConfigurationInterfaceClassLib/Channel\">\n" +
                    "                <Attribute Name=\"Type\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Digital</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Output</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Number\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>12</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>1</Value>\n" +
                    "                </Attribute>\n" +
                    "              </ExternalInterface>\n" +
                    "              <ExternalInterface ID=\"ca6333ee-bd82-411c-a819-ff97efade35e\" Name=\"Channel_DO_13\" RefBaseClassPath=\"AutomationProjectConfigurationInterfaceClassLib/Channel\">\n" +
                    "                <Attribute Name=\"Type\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Digital</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Output</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Number\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>13</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>1</Value>\n" +
                    "                </Attribute>\n" +
                    "              </ExternalInterface>\n" +
                    "              <ExternalInterface ID=\"ef31a576-4c2a-4e67-a1ac-3044b34b3d62\" Name=\"Channel_DO_14\" RefBaseClassPath=\"AutomationProjectConfigurationInterfaceClassLib/Channel\">\n" +
                    "                <Attribute Name=\"Type\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Digital</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Output</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Number\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>14</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>1</Value>\n" +
                    "                </Attribute>\n" +
                    "              </ExternalInterface>\n" +
                    "              <ExternalInterface ID=\"57dc7d07-9c0b-4b11-b6ae-61498edc103e\" Name=\"Channel_DO_15\" RefBaseClassPath=\"AutomationProjectConfigurationInterfaceClassLib/Channel\">\n" +
                    "                <Attribute Name=\"Type\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Digital</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Output</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Number\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>15</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>1</Value>\n" +
                    "                </Attribute>\n" +
                    "              </ExternalInterface>\n" +
                    "              <SupportedRoleClass RefRoleClassPath=\"AutomationProjectConfigurationRoleClassLib/DeviceItem\" />\n" +
                    "            </InternalElement>\n" +
                    "            <InternalElement ID=\"b482756c-8a7a-4ce6-95fc-b6e4e5c3b7a2\" Name=\"AI 5/AO 2_1\">\n" +
                    "              <Attribute Name=\"PositionNumber\" AttributeDataType=\"xs:int\">\n" +
                    "                <Value>3</Value>\n" +
                    "              </Attribute>\n" +
                    "              <Attribute Name=\"BuiltIn\" AttributeDataType=\"xs:boolean\">\n" +
                    "                <Value>true</Value>\n" +
                    "              </Attribute>\n" +
                    "              <Attribute Name=\"Address\">\n" +
                    "                <RefSemantic CorrespondingAttributePath=\"OrderedListType\" />\n" +
                    "                <Attribute Name=\"1\">\n" +
                    "                  <Attribute Name=\"StartAddress\" AttributeDataType=\"xs:int\">\n" +
                    "                    <Value>752</Value>\n" +
                    "                  </Attribute>\n" +
                    "                  <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                    <Value>80</Value>\n" +
                    "                  </Attribute>\n" +
                    "                  <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                    <Value>Input</Value>\n" +
                    "                  </Attribute>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"2\">\n" +
                    "                  <Attribute Name=\"StartAddress\" AttributeDataType=\"xs:int\">\n" +
                    "                    <Value>752</Value>\n" +
                    "                  </Attribute>\n" +
                    "                  <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                    <Value>32</Value>\n" +
                    "                  </Attribute>\n" +
                    "                  <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                    <Value>Output</Value>\n" +
                    "                  </Attribute>\n" +
                    "                </Attribute>\n" +
                    "              </Attribute>\n" +
                    "              <ExternalInterface ID=\"dde4cebb-fb6a-4150-bd91-745665de2411\" Name=\"Channel_AI_0\" RefBaseClassPath=\"AutomationProjectConfigurationInterfaceClassLib/Channel\">\n" +
                    "                <Attribute Name=\"Type\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Analog</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Input</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Number\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>0</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>16</Value>\n" +
                    "                </Attribute>\n" +
                    "              </ExternalInterface>\n" +
                    "              <ExternalInterface ID=\"67f553d4-47bb-40dd-9733-61ba9948d360\" Name=\"Channel_AI_1\" RefBaseClassPath=\"AutomationProjectConfigurationInterfaceClassLib/Channel\">\n" +
                    "                <Attribute Name=\"Type\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Analog</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Input</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Number\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>1</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>16</Value>\n" +
                    "                </Attribute>\n" +
                    "              </ExternalInterface>\n" +
                    "              <ExternalInterface ID=\"b8e37a27-08b8-41d6-95f1-68697ab33e6a\" Name=\"Channel_AI_2\" RefBaseClassPath=\"AutomationProjectConfigurationInterfaceClassLib/Channel\">\n" +
                    "                <Attribute Name=\"Type\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Analog</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Input</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Number\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>2</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>16</Value>\n" +
                    "                </Attribute>\n" +
                    "              </ExternalInterface>\n" +
                    "              <ExternalInterface ID=\"1e93d8d5-b8f4-4dbe-9e8e-668ccde1d56d\" Name=\"Channel_AI_3\" RefBaseClassPath=\"AutomationProjectConfigurationInterfaceClassLib/Channel\">\n" +
                    "                <Attribute Name=\"Type\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Analog</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Input</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Number\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>3</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>16</Value>\n" +
                    "                </Attribute>\n" +
                    "              </ExternalInterface>\n" +
                    "              <ExternalInterface ID=\"7a0e4683-3f1b-4b1c-add3-bc486e3f8ad0\" Name=\"Channel_AI_4\" RefBaseClassPath=\"AutomationProjectConfigurationInterfaceClassLib/Channel\">\n" +
                    "                <Attribute Name=\"Type\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Analog</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Input</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Number\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>4</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>16</Value>\n" +
                    "                </Attribute>\n" +
                    "              </ExternalInterface>\n" +
                    "              <ExternalInterface ID=\"e9e185f2-0bee-4237-8fe1-4d0c066bb771\" Name=\"Channel_AO_0\" RefBaseClassPath=\"AutomationProjectConfigurationInterfaceClassLib/Channel\">\n" +
                    "                <Attribute Name=\"Type\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Analog</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Output</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Number\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>0</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>16</Value>\n" +
                    "                </Attribute>\n" +
                    "              </ExternalInterface>\n" +
                    "              <ExternalInterface ID=\"90cb932a-bb55-4532-a3cc-6460a1b71ab9\" Name=\"Channel_AO_1\" RefBaseClassPath=\"AutomationProjectConfigurationInterfaceClassLib/Channel\">\n" +
                    "                <Attribute Name=\"Type\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Analog</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                  <Value>Output</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Number\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>1</Value>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                  <Value>16</Value>\n" +
                    "                </Attribute>\n" +
                    "              </ExternalInterface>\n" +
                    "              <SupportedRoleClass RefRoleClassPath=\"AutomationProjectConfigurationRoleClassLib/DeviceItem\" />\n" +
                    "            </InternalElement>\n" +
                    "            <InternalElement ID=\"8f17a732-6935-4e2b-9a29-6aedd997f13b\" Name=\"Count_1\">\n" +
                    "              <Attribute Name=\"PositionNumber\" AttributeDataType=\"xs:int\">\n" +
                    "                <Value>4</Value>\n" +
                    "              </Attribute>\n" +
                    "              <Attribute Name=\"BuiltIn\" AttributeDataType=\"xs:boolean\">\n" +
                    "                <Value>true</Value>\n" +
                    "              </Attribute>\n" +
                    "              <Attribute Name=\"Address\">\n" +
                    "                <RefSemantic CorrespondingAttributePath=\"OrderedListType\" />\n" +
                    "                <Attribute Name=\"1\">\n" +
                    "                  <Attribute Name=\"StartAddress\" AttributeDataType=\"xs:int\">\n" +
                    "                    <Value>768</Value>\n" +
                    "                  </Attribute>\n" +
                    "                  <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                    <Value>128</Value>\n" +
                    "                  </Attribute>\n" +
                    "                  <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                    <Value>Input</Value>\n" +
                    "                  </Attribute>\n" +
                    "                </Attribute>\n" +
                    "                <Attribute Name=\"2\">\n" +
                    "                  <Attribute Name=\"StartAddress\" AttributeDataType=\"xs:int\">\n" +
                    "                    <Value>768</Value>\n" +
                    "                  </Attribute>\n" +
                    "                  <Attribute Name=\"Length\" AttributeDataType=\"xs:int\">\n" +
                    "                    <Value>128</Value>\n" +
                    "                  </Attribute>\n" +
                    "                  <Attribute Name=\"IoType\" AttributeDataType=\"xs:string\">\n" +
                    "                    <Value>Output</Value>\n" +
                    "                  </Attribute>\n" +
                    "                </Attribute>\n" +
                    "              </Attribute>\n" +
                    "              <SupportedRoleClass RefRoleClassPath=\"AutomationProjectConfigurationRoleClassLib/DeviceItem\" />\n" +
                    "            </InternalElement>\n" +
                    "            <SupportedRoleClass RefRoleClassPath=\"AutomationProjectConfigurationRoleClassLib/DeviceItem\" />\n" +
                    "          </InternalElement>\n" +
                    "          <SupportedRoleClass RefRoleClassPath=\"AutomationProjectConfigurationRoleClassLib/DeviceItem\" />\n" +
                    "        </InternalElement>\n" +
                    "        <SupportedRoleClass RefRoleClassPath=\"AutomationProjectConfigurationRoleClassLib/Device\" />\n" +
                    "      </InternalElement>\n" +
                    "      <SupportedRoleClass RefRoleClassPath=\"AutomationProjectConfigurationRoleClassLib/AutomationProject\" />\n" +
                    "    </InternalElement>\n" +
                    "  </InstanceHierarchy>\n" +
                    "</CAEXFile>"
    );

    @Test
    public void performTests() {

        try {
            // 1
            System.out.println("\n1 - Testing with valid schema v2.15");
            System.out.println("-----------------------------------");

            File goodVersion2_15 = generateTestFile("goodVersion_2.15");
            CAEXFile caexFile = CAEXFile.read(goodVersion2_15);

            assertEquals("2.15", caexFile.getSchemaVersion());
            assertEquals("Project4.aml", caexFile.getFileName());

            System.out.println("OK\n\n");

            // 2
            System.out.println("2 - Testing with valid schema v3.0");
            System.out.println("----------------------------------");

            File goodVersion3_0 = generateTestFile("goodVersion_3.0");
            caexFile = CAEXFile.read(goodVersion3_0);

            assertEquals("3.0", caexFile.getSchemaVersion());
            assertEquals("AutomationML2.10BaseLibraries with Part 3 Geometry attributes.aml", caexFile.getFileName());

            System.out.println("OK\n\n");

            // 3
            System.out.println("3 - Testing with unsupported schema version");
            System.out.println("-------------------------------------------");

            File unsupportedVersion = generateTestFile("unsupportedVersion");
            assertThrows(CAEXFileParseException.class, () -> CAEXFile.read(unsupportedVersion), "Unsupported schema version: 1.15");

            System.out.println("OK\n\n");

            // 4
            System.out.println("4 - Testing filtering on example .aml document");
            System.out.println("----------------------------------------------");
            System.out.println("\nDocument contents:\n" + testFileContents.get("filterTest1"));

            File filterTest1 = generateTestFile("filterTest1");
            caexFile = CAEXFile.read(filterTest1);

            // 4/1
            System.out.println("\n4/1 - Testing 'all' filter");
            System.out.println("--------------------------");
            System.out.println("\nCriteria:\n" +
                    "\tSelect all 'ExternalInterface' objects, having direct children:\n" +
                    "\t\tAttribute[Name='Type']/Value=Digital\n" +
                    "\t\t\tAND\n" +
                    "\t\tAttribute[Name='IoType']/Value=Input\n");

            CAEXFilter caexFilter = CAEXFilter.forObject(caexFile);

            caexFilter
                    .all()
                        .element("ExternalInterface")
                            .havingChild()
                                .element("Attribute", "Name='Type'")
                                    .havingChild()
                                        .textNode("Value", "Digital")
                                        .done()
                                    .done()
                                .and()
                                .element("Attribute", "Name='IoType'")
                                    .havingChild()
                                        .textNode("Value", "Input");

            List<GenericCAEXObject> result4_1 = caexFilter.execute();

            assertEquals(24, result4_1.size(), () ->
                    "Test failed - filter results:\n\n" +
                            result4_1.stream().map(GenericCAEXObject::toString).collect(Collectors.joining("\n"))
            );

            assertTrue(result4_1.stream().allMatch(p -> "ExternalInterface".equals(p.getElementName())), "Test failed - Each result element should be 'ExternalInterface'");

            System.out.println("OK - 24 results, each 'ExternalInterface'\n\n");

            // 4/2
            System.out.println("4/2 - Testing filter clearing/reusing");
            System.out.println("-------------------------------------");
            System.out.println("\nCriteria:\n" +
                    "\tSelect all 'ExternalInterface' objects, having direct children:\n" +
                    "\t\tAttribute[Name='Type']/Value=Digital\n" +
                    "\tthen\n" +
                    "\t\tAttribute[Name='Type']/Value=Analog\n");

            TextNodeFilter tnf = TextNodeFilter._for("Value", "Digital");

            caexFilter.clear();

            System.out.print("\nTesting with original filter: Attribute[Name='Type']/Value=Digital");

            caexFilter
                    .all()
                    .element("ExternalInterface")
                    .havingChild()
                        .element("Attribute", "Name='Type'")
                            .havingChild()
                                .textNode(tnf);

            List<GenericCAEXObject> result4_2_1 = caexFilter.execute();

            assertEquals(40, result4_2_1.size(), () ->
                    "Test failed - filter results:\n\n" +
                            result4_2_1.stream().map(GenericCAEXObject::toString).collect(Collectors.joining("\n")));

            System.out.println("\n\nOK - 40 results\n");

            System.out.print("\nTesting with modified parameters: Attribute[Name='Type']/Value=Analog");

            tnf.setNodeValue("Analog");
            List<GenericCAEXObject> result4_2_2 = caexFilter.execute();

            assertEquals(7, result4_2_2.size(), () ->
                    "Test failed - filter results:\n\n" +
                            result4_2_2.stream().map(GenericCAEXObject::toString).collect(Collectors.joining("\n")));

            System.out.println("\n\nOK - 7 results\n");

            // 4/3
            System.out.println("4/3 - Testing 'child' filter");
            System.out.println("----------------------------");
            System.out.println("\nCriteria:\n" +
                    "\tGet child 'Attribute' objects of the first 'InternalElement' node of 'InstanceHierarchy'");

            caexFilter.clear();

            List<GenericCAEXObject> instanceHierarchy = caexFilter.clear().children().element("InstanceHierarchy").execute();

            assertEquals(1, instanceHierarchy.size());

            List<GenericCAEXObject> result_4_3_1 = CAEXFilter.forObject(instanceHierarchy.get(0)).children().element("InternalElement").havingChild().element("Attribute", "Name='Project.+'").execute();

            assertEquals(4, result_4_3_1.size());

            System.out.println("\n\nOK - 4 results\n");

        } catch (ParserConfigurationException | IOException | SAXException | CAEXFileParseException | JAXBException |
                 URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    static void mkTestDir() {
        String testFilesDirPathStr = USER_HOME + (USER_HOME.endsWith(DIR_SEP) ? "" : DIR_SEP) + TESTFILE_DIR;

        File testFilesDir = new File(testFilesDirPathStr);

        if (!testFilesDir.mkdir()) {
            throw new RuntimeException("Unable to create directory of test files: '" + testFilesDirPathStr + "'");
        }
    }

    @AfterAll
    static void cleanUpTestFiles() {

        File testFilesDir = new File(USER_HOME + (USER_HOME.endsWith(DIR_SEP) ? "" : DIR_SEP) + TESTFILE_DIR);

        if (testFilesDir.isDirectory()) {
            if (testFilesDir.listFiles() != null) {
                for (File f : testFilesDir.listFiles()) {
                    if (!f.isDirectory()) {
                        f.delete();
                    }
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
