package org.rpr.dh;

class Spawn {
	//-----------------------------------------------------
	// Description: via StreamGobbler, execute an external
	// command and wait for it to finish executing.
	//-----------------------------------------------------
	public static void execute(String... cmd) {
  		try {
			Runtime rt = Runtime.getRuntime();
			System.out.print("Executing: " + cmd[0]);
			for (int i = 1; i < cmd.length; i++) {
				System.out.format(" \"%s\"", cmd[i]);
			}
			System.out.println();
			Process proc = rt.exec(cmd);

			// any error message?
			StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(),
			                                               "ERROR");

			// any output?
			StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(),
			                                                "OUTPUT");

			// kick them off
			errorGobbler.start();
			outputGobbler.start();

			// any error???
			int exitVal = proc.waitFor();
			System.out.println("ExitValue: " + exitVal);

		} catch (Throwable t){
			t.printStackTrace();
		}
	}
}

