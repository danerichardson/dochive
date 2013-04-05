package org.rpr.dh;

import java.io.File;

//---------------------------------------------------------
// This class provides common settings for DocHive.
//---------------------------------------------------------
class Settings {
	// The path to ths JAR file
	private static String jarFile;
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
				if (isWindows()) {
					return jarFile+File.separator+"convert.bat";
				} else {
					return "convert";
				}
			}
		},

		// Tesseract's OCR program
		TESSERACT {
			@Override
			public String path() {
				if (isWindows()) {
					return jarFile+File.separator+"tesseract.bat";
				} else {
					return "tesseract";
				}
			}
		};

		public abstract String path();
	};

	private Settings() {};

	//-------------------------------------------------
	// Check for OS type
	//-------------------------------------------------
	private static String OS = System.getProperty("os.name").toLowerCase();
	private static boolean isWindows() {
		return (OS.indexOf("win") >= 0) || (OS.indexOf("nt") >= 0);
	}
	private static boolean isUnix() {
		return (OS.indexOf("nix") >= 0) || (OS.indexOf("nux") >= 0) || (OS.indexOf("aix") >= 0);
	}
	private static boolean isMac() {
		return (OS.indexOf("mac") >= 0);
	}

	//-------------------------------------------------
	// Handle the command line parameters and set
	// default values if required.
	//-------------------------------------------------
	// arg 0: Path to the JAR file
	// arg 1: sourceFile including path
	// arg 2: destinationDirectory including path
	// arg 3: autoalign pages
	// arg 4: template location
	// arg 5: template set
	// arg 6: additional identifier
	//-----------------------------------------------------
	public static void parseCommandLine(String []args) {

		// minimum requirements
		// source and destination parameters
		if (args.length < 3) {
			System.out.println(args.length + " arguments have been passed into DocHive+... Exiting");
			System.exit(1);
		}

		jarFile = args[0];
		sourceFile = args[1];
		destinationDirectory = args[2];

		// (optional parameter) autoalignment
		if (args.length>3) {
			if (args[3].equals("true")) {
				bAutoAlign = true;
			}
			else {
				bAutoAlign = false;
			}
		}

		// (optional parameter) custom template directory location
		if (args.length>4) {
			// '*' to set default
			if (!args[4].equals("*")) {
				templateDirectory = args[4];
			}
		}

		// (optional parameter) custom template set
		if (args.length>5) {
			// '*' to set default
			if (!args[5].equals("*")) {
				optionalTemplateSet = args[5];
				bOptionalTemplateSet = true;
			}
		}

		// (optional parameter) custom identifier
		if (args.length>6) {
			// '*' to set default
			if (!args[6].equals("*")) {
				optionalIdentifier = args[6];
				bOptionalIdentifier = true;
			}
		}
	}
}
