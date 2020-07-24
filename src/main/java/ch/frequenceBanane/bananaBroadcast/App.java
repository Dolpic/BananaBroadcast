package ch.frequenceBanane.bananaBroadcast;

import java.io.InputStream;
import java.net.URL;

import ch.frequenceBanane.bananaBroadcast.gui.GuiApp;

public class App {
	public static void main(String[] args) {
		new GuiApp().startApp();
	}

	public static URL getResource(String path) {
		return App.class.getClassLoader().getResource(path);
	}
	
	public static InputStream getResourceAsStream(String path) {
		return App.class.getResourceAsStream(path);
	}
}
