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
  	} // end [DocHiveToolbox()]


	//-----------------------------------------------------
  	// Description: Report success or failure based on
  	// boolean state.
  	//-----------------------------------------------------
	void report(boolean b) {
	    System.out.println(b ? "success" : "unexpected opportunity");
    }


	//-----------------------------------------------------
  	// Description: Separate the file specified by fileName
  	// parameter into png files stored in the directoryLocation
  	// parameter. If a directoryLocation does not exist then
  	// it will be created. Return the number of pages.
  	//-----------------------------------------------------
  	int separateDocumentPages(String sourceFile, String fileName, String woext, String directoryLocation){

		// create the output directory corresponding to the file
		boolean status;
		int pageCount = 0;
		status = new File(directoryLocation + File.separator + woext).mkdir();
        report(status);

        if(status) {
			// execute separation
			Spawn.execute(Settings.Programs.CONVERT.path()+" -monochrome -density 300 " + sourceFile + " " + directoryLocation + File.separator + woext + File.separator + woext + "_%02d.png");
		}
		// return the number of files in the [input\fileName] directory
		pageCount = new File(directoryLocation + File.separator + woext).listFiles().length;
	  	return pageCount;
  	} // end [separateDocumentPages(String fileName, String directoryLocation)]
} // end [class DocHiveToolbox]
