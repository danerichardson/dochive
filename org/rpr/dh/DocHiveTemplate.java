//---------------------------------------------------------
// Author: Edward Brian Duncan, Damarius Hayes
// DocHiveTemplate Description: This class contains the
// functions related to template processing.
//---------------------------------------------------------
package org.rpr.dh;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class DocHiveTemplate {

    private Boolean templateIdentified;
    private String templateLocation;
    private String templateName;
    private int pageX;
    private int pageY;

	//-----------------------------------------------------
    // Constructor- Initialize the class
    //-----------------------------------------------------
    DocHiveTemplate(String location) {
        templateLocation = location;
    }

	//-----------------------------------------------------
    // Description: Determine if a template exists for the
    // given file and return true on success.
    //-----------------------------------------------------
    boolean templateExistFor(String fileName, String destinationDirectory) {

        templateIdentified = false;

        // from template
        int intOriginX = 0;
        int intOriginY = 0;

        // from template
        int intX = 0;
        int intY = 0;
        int intWidth = 0;
        int intHeight = 0;

        // calculated
        int viaTemplateX = 0;
        int viaTemplateY = 0;
        int viaTemplatePlusWidth = 0;
        int viaTemplatePlusHeight = 0;

        String fileName_woext = fileName.substring(0, fileName.lastIndexOf('.'));
        String pathPlusFileName_woext = destinationDirectory
                + File.separator + fileName_woext
                + File.separator + fileName_woext;

        try {

            File folder = new File(templateLocation);
            File[] listOfFiles = folder.listFiles();

            long startTime = System.currentTimeMillis();

            for (File file : listOfFiles) {

                if (file.isFile() && file.getName().endsWith(".xml")) {

                    templateName = "";

                    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                    Document doc = dBuilder.parse(file);
                    doc.getDocumentElement().normalize();

                    System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
                    NodeList nList = doc.getElementsByTagName("condition");

                    for (int temp = 0; temp < nList.getLength(); temp++) {

                        Node nNode = nList.item(temp);
                        if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                            Element eElement = (Element) nNode;

                            String myX = getTagValue("x", eElement);
                            String myY = getTagValue("y", eElement);
                            String myWidth = getTagValue("width", eElement);
                            String myHeight = getTagValue("height", eElement);
                            String mySuffix = getTagValue("suffix", eElement);
                            String myRequirements = getTagValue("required", eElement);

                            intX = Integer.parseInt(myX);
                            intY = Integer.parseInt(myY);
                            intWidth = Integer.parseInt(myWidth);
                            intHeight = Integer.parseInt(myHeight);

                            viaTemplateX = intX - (intOriginX - pageX);
                            viaTemplateY = intY - (intOriginY - pageY);
                            viaTemplatePlusWidth = viaTemplateX + intWidth;
                            viaTemplatePlusHeight = viaTemplateY + intHeight;

                            Spawn.execute(Settings.Programs.CONVERT.path(),
                                    pathPlusFileName_woext + "_trim.png",
                                    "-crop", String.format("%dx%d+%d+%d", intWidth, intHeight, viaTemplateX, viaTemplateY),
                                    "+repage",
                                    pathPlusFileName_woext + "_" + mySuffix + ".png");
                            Spawn.execute(Settings.Programs.CONVERT.path(),
                                    pathPlusFileName_woext + "_trim.png",
                                    "-fill", "none",
                                    "-stroke", "red",
                                    "-strokewidth", "3",
                                    "-draw", String.format("rectangle %d,%d %d,%d",
                                            viaTemplateX, viaTemplateY, viaTemplatePlusWidth, viaTemplatePlusHeight),
                                    "+repage",
                                    pathPlusFileName_woext + "_draw.png");
                            Spawn.execute(Settings.Programs.TESSERACT.path(),
                                    pathPlusFileName_woext + "_" + mySuffix + ".png",
                                    pathPlusFileName_woext + "_" + mySuffix);

                            String line = ""; 	// String that holds current file line
                            String accumulate = "";

                            try {
                                FileReader input = new FileReader(pathPlusFileName_woext + "_" + mySuffix + ".txt");
                                BufferedReader bufRead = new BufferedReader(input);

                                line = bufRead.readLine();

                                while (line != null) {
                                    accumulate += line;
                                    line = bufRead.readLine();
                                }

                                bufRead.close();
                                input.close();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            String[] requirements = myRequirements.split("#");
                            for (String requirement : requirements) {
                                if (requirement.equals(accumulate.trim())) {
                                    templateName = file.getName();
                                    templateIdentified = true;
                                    return templateIdentified;
                                }
                            }
                        }
                    }
                }
            }

            // record end time
            long endTime = System.currentTimeMillis();
            System.out.println("Template Search [" + fileName + "] " + (endTime - startTime) / 1000 + " seconds");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return templateIdentified;
    }

	//-----------------------------------------------------
    // Description: Retrieve the template name matching
    // the file.
    //-----------------------------------------------------
    String getTemplateName() {
        return templateName;
    }

	//-----------------------------------------------------
    // Description: Determine the rotation of the png file
    // and rotate it whereby the side edge is 90 degrees.
    // Return the angle of rotation.
    //-----------------------------------------------------
    double autoAlignRotate(String fileName, String destinationDirectory) {

        String fileName_woext = (fileName.substring(0, fileName.lastIndexOf('.')));

        Spawn.execute(Settings.Programs.CONVERT.path(), "-deskew", "40%", "+repage",
                destinationDirectory + File.separator + fileName_woext + File.separator + fileName,
                destinationDirectory + File.separator + fileName_woext + File.separator + fileName);

        return 0.0;
    }

	//-----------------------------------------------------
    // Description: Determine the unique point of alignment
    // for the file specified by fileName.
    //-----------------------------------------------------
    void determinePagePointOfAlignment(String fileName, String destinationDirectory) {
        pageX = 0;
        pageY = 0;
    }

	//-----------------------------------------------------
    // Description: Normalize the file to have the origin
    // be the top left of the form non white space. We
    // are gettting rid of the whitespace borders.
    //-----------------------------------------------------
    void normalizeByTrimming(String fileName, String destinationDirectory) {

        String fileName_woext = (fileName.substring(0, fileName.lastIndexOf('.')));

        Spawn.execute(Settings.Programs.CONVERT.path(), "-trim", "-fuzz", "5%", "+repage",
                destinationDirectory + File.separator + fileName_woext + File.separator + fileName,
                destinationDirectory + File.separator + fileName_woext + File.separator + fileName_woext + "_trim.png");

        pageX = 0;
        pageY = 0;
    }

	//-----------------------------------------------------
    // Description: Get the value from the node.
    // YUUGAMEE!
    //-----------------------------------------------------
    String getTagValue(String sTag, Element eElement) {
        NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
        Node nValue = (Node) nlList.item(0);

        return nValue.getNodeValue();
    }

	//-----------------------------------------------------
    // Description: Read the text from the file.
    // AHHGAYUU!
    //-----------------------------------------------------
    String interpretText(String destinationDirectory, String fileName_woext, String suffix) {

        String line = "";
        String accumulate = "";
        Scanner reader = null;

        int dataType = 1; // For current testing

        try {
            File file = new File(destinationDirectory + File.separator + fileName_woext + File.separator + fileName_woext + "_" + suffix + ".txt");

            reader = new Scanner(new FileReader(file));

            if (dataType == 1) { 		// multiline
                while (reader.hasNextLine()) {
                    accumulate += reader.nextLine().trim() + " ";
                }
                accumulate = accumulate.trim();
            } else if (dataType == 2) { 	// address
                if (reader.hasNextLine()) {
                    accumulate = reader.nextLine().trim();
                }
                if (reader.hasNextLine()) {
                    accumulate += "," + reader.nextLine().trim();
                }
                if (reader.hasNextLine()) {
                    line = reader.nextLine().trim();
                    int index = line.indexOf(",");
                    String line31 = line.substring(0, index);

                    int i = 0;
                    int hi = line.length() - index;
                    for (i = hi; i > 0; i--) {
                        if (Character.isDigit(line.charAt(i))) {
                            break;
                        }
                    }

                    String line32 = line.substring(index + 1, i);
                    String line33 = line.substring(i + 1, line.length() - 1);

                    accumulate += "," + line31 + "," + line32 + "," + line33;
                }
            } else if (dataType == 3) { 	// single text
                accumulate = reader.nextLine();
            } else if (dataType == 4) { 	// numeric
                // coming soon
            }

            reader.close();
            return accumulate;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";

    }

	//-----------------------------------------------------
    // Description: Using the specified template, extract
    // the template sections into PNG and TXT corresponding
    // parts.
    //-----------------------------------------------------
    void extractWithTemplate(String templateName, String fileName, String destinationDirectory) {

        // from template
        int intOriginX = 0;
        int intOriginY = 0;

        // from template
        int intX = 0;
        int intY = 0;
        int intWidth = 0;
        int intHeight = 0;

        // calculated
        int viaTemplateX = 0;
        int viaTemplateY = 0;
        int viaTemplatePlusWidth = 0;
        int viaTemplatePlusHeight = 0;

        System.out.println("fileName = " + fileName);
        String fileName_woext = fileName.substring(0, fileName.lastIndexOf('.'));
        String pathPlusFileName_woext = destinationDirectory + File.separator + fileName_woext + File.separator + fileName_woext;
        System.out.println("pathPlusFileName_woext = " + pathPlusFileName_woext);

        try {

            File fXmlFile = new File(templateLocation, templateName);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();

            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

            NodeList nList = doc.getElementsByTagName("section");

            Node nNode;
            for (int temp = 0; temp < nList.getLength(); temp++) {

                nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;

                    String myX = getTagValue("x", eElement);
                    String myY = getTagValue("y", eElement);
                    String myWidth = getTagValue("width", eElement);
                    String myHeight = getTagValue("height", eElement);
                    String mySuffix = getTagValue("suffix", eElement);

                    intX = Integer.parseInt(myX);
                    intY = Integer.parseInt(myY);
                    intWidth = Integer.parseInt(myWidth);
                    intHeight = Integer.parseInt(myHeight);

                    viaTemplateX = intX - (intOriginX - pageX);
                    viaTemplateY = intY - (intOriginY - pageY);
                    viaTemplatePlusWidth = viaTemplateX + intWidth;
                    viaTemplatePlusHeight = viaTemplateY + intHeight;

                    Spawn.execute(Settings.Programs.CONVERT.path(),
                            pathPlusFileName_woext + "_trim.png",
                            "-crop", String.format("%dx%d+%d+%d", intWidth, intHeight, viaTemplateX, viaTemplateY),
                            "+repage",
                            pathPlusFileName_woext + "_" + mySuffix + ".png");
                    Spawn.execute(Settings.Programs.CONVERT.path(),
                            pathPlusFileName_woext + "_trim.png",
                            "-fill", "none",
                            "-stroke", "red",
                            "-strokewidth", "3",
                            "-draw", String.format("rectangle %d,%d %d,%d",
                                    viaTemplateX, viaTemplateY,
                                    viaTemplatePlusWidth, viaTemplatePlusHeight),
                            "+repage",
                            pathPlusFileName_woext + "_draw.png");
                    Spawn.execute(Settings.Programs.TESSERACT.path(),
                            pathPlusFileName_woext + "_" + mySuffix + ".png",
                            pathPlusFileName_woext + "_" + mySuffix);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

	//-----------------------------------------------------
    // Description: Using the specified template, create
    // csv records corresponding to the output xml criteria.
    //-----------------------------------------------------
    void transformWithTemplate(String templateName, String fileName, String destinationDirectory) {

        System.out.println("fileName = " + fileName);
        String fileName_woext = fileName.substring(0, fileName.lastIndexOf('.'));

        try {
            File fXmlFile = new File(templateLocation, templateName);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();

            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            NodeList nList = doc.getElementsByTagName("output");

            // loop through all the output nodes
            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;
                    String outputLine = getTagValue("line", eElement);

                    FileWriter output = new FileWriter(destinationDirectory + File.separator + fileName_woext.substring(0, fileName.lastIndexOf('_')) + ".csv", true);
                    BufferedWriter bufWrite = new BufferedWriter(output);

                    System.out.println(destinationDirectory + File.separator + fileName_woext.substring(0, fileName.lastIndexOf("_")) + ".csv");

                    if (Settings.optionalIdentifier != null) {
                        bufWrite.write("\"" + Settings.optionalIdentifier + "\"");
                    }

                    String[] outputItems = outputLine.split("#");
                    for (String outputItem : outputItems) {
                        if ((temp == 0) && (Settings.optionalIdentifier == null)) {
                            bufWrite.write("\"" + interpretText(destinationDirectory, fileName_woext, outputItem) + "\"");
                        } else {
                            bufWrite.write(",\"" + interpretText(destinationDirectory, fileName_woext, outputItem) + "\"");
                        }
                    }

                    bufWrite.write("\r\n");
                    bufWrite.close();
                    output.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
