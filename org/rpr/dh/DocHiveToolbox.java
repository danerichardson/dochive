//---------------------------------------------------------
// Author: Edward Brian Duncan
// DocHiveToolbox Description: General uility functions for
// DocHive.
//---------------------------------------------------------
package org.rpr.dh;

import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class DocHiveToolbox {

	//-----------------------------------------------------
	// Constructor- Initialize the class.
	//-----------------------------------------------------
  	DocHiveToolbox() {
	}


	//-----------------------------------------------------
	// Description: Report success or failure based on
	// boolean state.
	//-----------------------------------------------------
	void report(boolean b) {
	    System.out.println(b ? "success" : "failure");
    }


	//-----------------------------------------------------
	// Description: Separate the file specified by fileName
	// parameter into png files stored in the output directory
	// in the settings. Returns the pages produced.
	//-----------------------------------------------------
	File[] separateDocumentPages(String sourceFile, String fileName, String woext) {

		// create the output directory corresponding to the file
		File outputDirectory = new File(Settings.destinationDirectory, woext);
		boolean status;
		status = outputDirectory.mkdir();
        report(status);

        if(status) {
			// execute separation
			Spawn.execute(Settings.Programs.CONVERT.path(),
			              "-monochrome",
			              "-density", "300",
			              sourceFile,
			              new File(outputDirectory, woext + "_%02d.png").getPath());
		}
		// return the files in the [output/fileName] directory
		return outputDirectory.listFiles();
	}
}

