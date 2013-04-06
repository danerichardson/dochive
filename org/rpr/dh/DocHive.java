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

		// Parse command-line arguments
		Settings.parseCommandLine(args);

		// source file
		File temp = new File(Settings.sourceFile);
		if (temp.exists()) {
			String filePath = temp.getParentFile().getAbsolutePath();
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
		String fileName = temp.getName();
		int index = fileName.lastIndexOf('.');
		String woext = fileName.substring(0, index);

		long sepStartTime = System.currentTimeMillis();
		File[] listOfFiles = tools.separateDocumentPages(Settings.sourceFile, fileName, woext);

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
		File folder = new File(Settings.destinationDirectory, woext);
		String mainOutputDirectory = folder.getPath();
	  	boolean templateAvailable;

		System.out.println("Page Count: " + listOfFiles.length);

		// only continue if pages exist
		if (listOfFiles.length > 0) {

			long startTime = System.currentTimeMillis();

			// loop through all the files in destinationDirectory
			for (File file : listOfFiles) {

				// ensure it is a file
				if (file.isFile()) {

					String afile = file.getName();
		          	if (afile.toUpperCase().endsWith(".PNG"))	{

						// create the output directory corresponding to the file
						boolean status;
						File pageOutputDirectory = new File(folder, afile.substring(0, afile.lastIndexOf('.')));
						status = pageOutputDirectory.mkdir();

						File currentFile = new File(folder, afile);
						File newFile = new File(pageOutputDirectory, afile);
						currentFile.renameTo(newFile);

						// instanciate template class
						DocHiveTemplate dhTemplate = new DocHiveTemplate(Settings.templateDirectory);

						// auto rotate file
						if(Settings.bAutoAlign) rotate = dhTemplate.autoAlignRotate(afile, mainOutputDirectory);

						// trim/normalize form
						dhTemplate.normalizeByTrimming(afile, mainOutputDirectory);
						// dhTemplate.determinePagePointOfAlignment(afile, Settings.destinationDirectory);

						// determine if a template exists for the file
					    templateAvailable = false;
						templateAvailable = dhTemplate.templateExistFor(afile, mainOutputDirectory);


						System.out.println("-------------------------------------------------------------------");
						System.out.println(templateAvailable ? "success" : "failure");

						// use template to extract information
					    if(templateAvailable) {

							// yo, mark page as having a template ************************************************************

							// get current template name
 						    String templateName = dhTemplate.getTemplateName();
							System.out.println("Template ["+templateName+"] selected for ["+afile+"]");

							// use the template to extract pieces
							dhTemplate.extractWithTemplate(templateName, afile, mainOutputDirectory);

							// convert the pieces into a csv file
							dhTemplate.transformWithTemplate(templateName, afile, Settings.bOptionalIdentifier, Settings.optionalIdentifier, mainOutputDirectory);

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

