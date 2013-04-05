package org.rpr.dh;

//---------------------------------------------------------
// This class provides common settings for DocHive.
//---------------------------------------------------------
class Settings {
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
}
