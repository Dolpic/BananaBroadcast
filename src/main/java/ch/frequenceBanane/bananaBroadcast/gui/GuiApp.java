package ch.frequenceBanane.bananaBroadcast.gui;

import java.io.IOException;
import java.sql.SQLException;
import java.util.function.Consumer;

import org.apache.commons.configuration2.ex.ConfigurationException;

import ch.frequenceBanane.bananaBroadcast.App;
import ch.frequenceBanane.bananaBroadcast.BananaBroadcast;
import ch.frequenceBanane.bananaBroadcast.gui.mainPane.MainPane;
import ch.frequenceBanane.bananaBroadcast.utils.Log;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Main class of the software, JavaFX Application
 * 
 * @author Corentin
 * @author corentin.junod@epfl.ch
 */
public class GuiApp extends Application {

	private BananaBroadcast app;
	private Stage primaryStage;
	private MainPane mainPane;
	
	public void startApp() {
		launch();
	}

	/** Main functions called by JavaFX. Instantiate the whole program */
	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		primaryStage.setTitle("BananaBroadcast");
		
		initialize();
		Scene scene = new Scene(mainPane.getRootLayout());
		primaryStage.setScene(scene);
		primaryStage.show();
		afterShow();
	}

	private void initialize() throws IOException, ConfigurationException {
		
		try {
			app = new BananaBroadcast();
		} catch (SQLException e) {
			die("Unable to open the database, please check that the database is reachable and the creditentials are corrects.\n\nThey are set in config.properties\n\n Error : "+e.getMessage());
		}
		
		try {
			mainPane = new MainPane(app, primaryStage);
		} catch (IOException e) {
			die("Missing required file :"+e.getMessage());
		}
	}
	
	private void afterShow() {
		mainPane.afterShow();
	}

	/**
	 * Set a Consumer to be triggered then a button is clicked
	 * 
	 * @param button   The button on which the action must be bind
	 * @param consumer The consumer to run when the button is clicked
	 */
	public static void setOnActionButton(ButtonBase button, Consumer<ActionEvent> consumer) {
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				consumer.accept(e);
			}
		});
	}

	/**
	 * Set a Consumer to be triggered then a MenuItme is clicked
	 * 
	 * @param button   The MenuItem on which the action must be bind
	 * @param consumer The consumer to run when the MenuItem is clicked
	 */
	public static void setOnActionButton(MenuItem button, Consumer<ActionEvent> consumer) {
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				consumer.accept(e);
			}
		});
	}

	/**
	 * Utility function to load a JavaFX layout file (.fxml)
	 * 
	 * @param controller The Object on which the layout is loaded, generally the
	 *                   caller itself (this)
	 * @param path       The path to the .fxml layout file
	 * @return The loaded object hierarchy
	 * @throws IOException If an error occurs during the file operations
	 */
	public static Parent loadLayout(final Object controller, final String file) throws IOException {
		FXMLLoader loader = new FXMLLoader(App.getResource("fxml/" + file));
		loader.setController(controller);
		return loader.load();
	}

	/**
	 * Automatically resize the given innerPane to the size of the targetPane
	 * 
	 * @param innerPane  the innerPane to resize automatically
	 * @param targetPane the parent pane (container of the child)
	 */
	public static void makeResponsive(final Pane innerPane, final Pane targetPane) {
		innerPane.widthProperty().addListener((obs, oldValue, newValue) -> {
			targetPane.setPrefWidth(innerPane.getWidth());
		});
		innerPane.heightProperty().addListener((obs, oldValue, newValue) -> {
			targetPane.setPrefHeight(innerPane.getHeight());
		});
	}

	/** Stop the execution right away with an error message */
	public static void die(final String msg) {
		Log.errorDialog(msg);
		Platform.exit();
		System.exit(1);
	}
}
