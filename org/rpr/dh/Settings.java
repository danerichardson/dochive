package org.rpr.dh;

//---------------------------------------------------------
// This class provides common settings for DocHive.
//---------------------------------------------------------
class Settings {
	// The source file to read
	public static String sourceFile;
	// Output directory for results
	public static String destinationDirectory;
	// Auto-align pages
	public static boolean bAutoAlign = true;
	// Template location
	public static String templateDirectory = "templates";
	// Template set
	public static String optionalTemplateSet = "";
	public static boolean bOptionalTemplateSet = false;
	// Additional identifier
	public static String optionalIdentifier = "";
	public static boolean bOptionalIdentifier = false;

	// Paths to external programs that are run by DocHive
	public static enum Programs {
		// ImageMagick's convert program
		CONVERT {
			@Override
			public String path() {
				return "convert.bat";
			}
		},

		// Tesseract's OCR program
		TESSERACT {
			@Override
			public String path() {
				return "tesseract.bat";
			}
		};

		public abstract String path();
	};

	private Settings() {};

	//-------------------------------------------------
	// Handle the command line parameters and set
	// default values if required.
	//-------------------------------------------------
	// arg 0: sourceFile including path
	// arg 1: destinationDirectory including path
	// arg 2: autoalign pages
	// arg 3: template location
	// arg 4: template set
	// arg 5: additional identifier
	//-----------------------------------------------------
	public static void parseCommandLine(String []args) {

		// minimum requirements
		// source and destination parameters
		if (args.length < 2) {
			System.out.println(args.length + " arguments have been passed into DocHive+... Exiting");
			System.exit(1);
		}

		sourceFile = args[0];
		destinationDirectory = args[1];

		// (optional parameter) autoalignment
		if (args.length>2) {
			if (args[2].equals("true")) {
				bAutoAlign = true;
			}
			else {
				bAutoAlign = false;
			}
		}

		// (optional parameter) custom template directory location
		if (args.length>3) {
			// '*' to set default
			if (!args[3].equals("*")) {
				templateDirectory = args[3];
			}
		}

		// (optional parameter) custom template set
		if (args.length>4) {
			// '*' to set default
			if (!args[4].equals("*")) {
				optionalTemplateSet = args[4];
				bOptionalTemplateSet = true;
			}
		}

		// (optional parameter) custom identifier
		if (args.length>5) {
			// '*' to set default
			if (!args[5].equals("*")) {
				optionalIdentifier = args[5];
				bOptionalIdentifier = true;
			}
		}
	}
}
