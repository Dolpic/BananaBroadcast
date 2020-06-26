package ch.frequenceBanane.bananaBroadcast.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Simple class to unify the messages output through the program
 * 
 * @author Corentin Junod
 * @author corentin.junod@epfl.ch
 */
public class Log {

	private Log() {
	}

	public static void error(String msg) {
		System.err.println("ERROR : " + msg);
	}

	public static void warning(String msg) {
		System.out.println("WARNING : " + msg);
	}

	public static void log(String msg) {
		System.out.println(msg);
	}

	public static void errorDialog(String msg) {
		showDialog("Error", msg, AlertType.ERROR);
	}

	public static void warningDialog(String msg) {
		showDialog("Warning", msg, AlertType.WARNING);
	}

	public static void informationDialog(String msg) {
		showDialog("Information", msg, AlertType.INFORMATION);
	}

	private static void showDialog(String title, String msg, AlertType type) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(title);
		alert.setContentText(msg);
		alert.showAndWait();
	}

}
