//---------------------------------------------------------
// Author: Edward Brian Duncan
// DocHive Description: This is DocHive's point of entry.
// The main method controls the DocHive template-based
// conversion process.
//---------------------------------------------------------
package org.rpr.dh;

import java.io.File;

public class DocHive{

	//-----------------------------------------------------
	// Description: This application will convert a multipage
	// document into its csv content. This is the entry point.
	//-----------------------------------------------------
    public static void main(String args[]){

		String absolutePath;
		String filePath;

		// Parse command-line arguments
		Settings.parseCommandLine(args);

		// source file
		File temp = new File(Settings.sourceFile);
		if (temp.exists()) {
			absolutePath = temp.getAbsolutePath();
			filePath = absolutePath.substring(0,absolutePath.lastIndexOf(File.separator));
			System.out.println("File path: " + filePath);
		}
		else {
			// exit
			System.out.println("sourceFile argument does not exist");
			System.out.println("value: "+Settings.sourceFile+"... Exiting");
			return;
		}

		// destination location (directory)
		File dest = new File(Settings.destinationDirectory);
		if(!dest.exists()) {
			// exit
			System.out.println("destinationDirectory argument does not exist");
			System.out.println("value: "+Settings.destinationDirectory+"... Exiting");
			return;
		}

		//-------------------------------------------------
		// now we have the parameters, lets separate the
		// source file into separate PNG pages
		//-------------------------------------------------

		// initialize toolbox (rotation, alignment)
	  	DocHiveToolbox tools = new DocHiveToolbox();

		// pages in current document
	  	int index = temp.getName().lastIndexOf('.');
		String fileName = temp.getName();
		String woext = temp.getName().substring(0, index);

		long sepStartTime = System.currentTimeMillis();
		int pageCount = tools.separateDocumentPages(Settings.sourceFile, fileName, woext, Settings.destinationDirectory);

		long sepEndTime = System.currentTimeMillis();
		System.out.println("Converted in " + (sepEndTime - sepStartTime)/1000 + " seconds");

		//-------------------------------------------------
		// we have separated all the pages into PNG files.
		// now we can align them (if asked for), find the
		// point of reference and template for each file.
		// finally, convert the PNG file into one or more
		// CSV lines
		//-------------------------------------------------

	  	double rotate;
	  	String files;
		File folder = new File(Settings.destinationDirectory + File.separator + woext);
	  	File[] listOfFiles = folder.listFiles();
	  	boolean templateAvailable;

		System.out.println("Page Count: " + pageCount);

		// only continue if pages exist
	  	if(pageCount>0) {

			long startTime = System.currentTimeMillis();

			// loop through all the files in destinationDirectory
			for (int i = 0; i < listOfFiles.length; i++) {

				// ensure it is a file
		  		if (listOfFiles[i].isFile()) {

		     		String afile = listOfFiles[i].getName();
		          	if (afile.toUpperCase().endsWith(".PNG"))	{

						// create the output directory corresponding to the file
						boolean status;
						status = new File(Settings.destinationDirectory + File.separator + woext).mkdir();
						status = new File(Settings.destinationDirectory + File.separator + woext + File.separator + afile.substring(0, afile.lastIndexOf("."))).mkdir();

						String curLocation = Settings.destinationDirectory + File.separator + woext + File.separator + afile;
						String newLocation = Settings.destinationDirectory + File.separator + woext + File.separator + afile.substring(0, afile.lastIndexOf(".")) + File.separator + afile;

						File currentFile = new File(curLocation);
						currentFile.renameTo(new File(newLocation));

						// instanciate template class
						DocHiveTemplate dhTemplate = new DocHiveTemplate(Settings.templateDirectory);

						// auto rotate file
					    if(Settings.bAutoAlign) rotate = dhTemplate.autoAlignRotate(afile, Settings.destinationDirectory + File.separator + fileName.substring(0, fileName.lastIndexOf(".")));

						// trim/normalize form
					    dhTemplate.normalizeByTrimming(afile, Settings.destinationDirectory + File.separator + fileName.substring(0, fileName.lastIndexOf(".")));
						// dhTemplate.determinePagePointOfAlignment(afile, Settings.destinationDirectory);

						// determine if a template exists for the file
					    templateAvailable = false;
					    templateAvailable = dhTemplate.templateExistFor(afile, Settings.destinationDirectory + File.separator + fileName.substring(0, fileName.lastIndexOf(".")));


						System.out.println("-------------------------------------------------------------------");
						System.out.println(templateAvailable ? "success" : "unexpected opportunity");

						// use template to extract information
					    if(templateAvailable) {

							// yo, mark page as having a template ************************************************************

							// get current template name
 						    String templateName = dhTemplate.getTemplateName();
							System.out.println("Template ["+templateName+"] selected for ["+afile+"]");

							// use the template to extract pieces
							dhTemplate.extractWithTemplate(templateName, afile, Settings.destinationDirectory + File.separator + fileName.substring(0, fileName.lastIndexOf(".")));

							// convert the pieces into a csv file
							dhTemplate.transformWithTemplate(templateName, afile, Settings.bOptionalIdentifier, Settings.optionalIdentifier, Settings.destinationDirectory + File.separator + fileName.substring(0, fileName.lastIndexOf(".")));

					    }
					    else {

							// mark page as not having a template + new method needed ****************************************
						}

					}

				}

			}

			long endTime = System.currentTimeMillis();
			System.out.println("Converted in " + (endTime - startTime)/1000 + " seconds");
		}
	}
}

