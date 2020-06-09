package ch.frequenceBanane.bananaBroadcast.utils;

/**
 * Simple class to unify the messages output through the program
 * @author Corentin Junod
 * @author corentin.junod@epfl.ch
 */
public class Log {

	private Log() {}
	
	public static void error(String msg) {
		System.err.println("ERROR : "+msg);
	}
	
	public static void warning(String msg) {
		System.out.println("WARNING : "+msg);
	}
	
	public static void log(String msg) {
		System.out.println(msg);
	}
}
