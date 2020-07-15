package ch.frequenceBanane.bananaBroadcast.gui;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.function.Consumer;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.ex.ConfigurationException;

import ch.frequenceBanane.bananaBroadcast.BananaBroadcast;
import ch.frequenceBanane.bananaBroadcast.database.AudioFile;
import ch.frequenceBanane.bananaBroadcast.database.MusicDatabase.Kind;
import ch.frequenceBanane.bananaBroadcast.gui.categorySelector.CategorySelectorView;
import ch.frequenceBanane.bananaBroadcast.gui.mainPane.MainPane;
import ch.frequenceBanane.bananaBroadcast.gui.player.AudioPlayerView;
import ch.frequenceBanane.bananaBroadcast.gui.player.AudioPlayerView.AudioPlayerKind;
import ch.frequenceBanane.bananaBroadcast.gui.playlist.AudioFileListView;
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

	private AudioPlayerView player1;
	private AudioPlayerView player2;
	private AudioPlayerView mainPlayer;

	private MainPane mainPane;

	private AudioFileListView<AudioFile> playlist;
	private AudioFileListView<AudioFile> playlistOld;
	private AudioFileListView<AudioFile> databaseList;

	private CategorySelectorView categorySelector;
	
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
	}

	private void initialize() throws IOException, ConfigurationException {
		
		try {
			Configuration config = getConfiguration();
			app = new BananaBroadcast(
					config.getString("Database_URL"), 
					config.getString("Database_User"), 
					config.getString("Database_Password"), 
					config.getString("GPIO_IP"),
					config.getInt("GPIO_Port"));
		} catch (SQLException e) {
			die("Unable to open the database, please check that the database is reachable and the creditentials are corrects.\n\nThey are set in config.properties\n\n Error : "+e.getMessage());
		}
		
		//try {
			playlist         = new AudioFileListView<>(app.playlist,     AudioFileListView.getAudioFileData());
			playlistOld      = new AudioFileListView<>(app.playlistOld,  AudioFileListView.getAudioFileData());
			databaseList     = new AudioFileListView<>(app.databaseList, AudioFileListView.getAudioFileData());
			player1          = new AudioPlayerView(app.player1,    AudioPlayerKind.Music);
			player2          = new AudioPlayerView(app.player2,    AudioPlayerKind.Music);
			mainPlayer       = new AudioPlayerView(app.mainPlayer, AudioPlayerKind.Music);
			mainPane         = new MainPane(app, primaryStage);
			categorySelector = new CategorySelectorView(app.categorySelector);
		/*} catch (IOException e) {
			die("Missing required file :"+e.getMessage());
		}*/
		
		app.categorySelector.setOnSelectedCategoriesChange((selected) -> {
			try {
				ArrayList<AudioFile> newList = app.database.getFromCategoriesAndKind(Kind.MUSIC, selected);
				app.databaseList.removeAll();
				app.databaseList.addAtEnd(newList);
				databaseList.updateView();
			} catch (Exception e) {
				Log.error("Unable to update the categorie lists : " + e.getMessage());
			}
		});

		mainPane.addTopLeftNode(player1.getRootLayout());
		mainPane.addTopRightNode(player2.getRootLayout());
		mainPane.addMainPlayerPane(mainPlayer.getRootLayout());
		mainPane.addBottomTopNode(playlistOld.getRootLayout());
		mainPane.addBottomBottomNode(playlist.getRootLayout());
		mainPane.addDatabaseListPane(databaseList.getRootLayout());
		mainPane.addCategorySelector(categorySelector.getRootLayout());

		Consumer<AudioFile> loadAudioFileInPlayer = (AudioFile audioFile) -> {
			app.mainPlayer.load(audioFile);
		};

		playlist.setOnElementDoubleClick(loadAudioFileInPlayer);
		playlistOld.setOnElementDoubleClick(loadAudioFileInPlayer);
		databaseList.setOnElementDoubleClick(loadAudioFileInPlayer);

		playlist.setSortable(false);

		app.player1.addOnLoadEvent(() -> {
			player1.setTitle();
		});

		app.player1.addOnPlayEvent(() -> {
			player1.updateTimers();
			player1.setPlayState();
		});

		app.player1.addOnFinishEvent(() -> {
			player1.setPauseState();
		});

		app.player2.addOnLoadEvent(() -> {
			player2.setTitle();
		});

		app.player2.addOnPlayEvent(() -> {
			player2.updateTimers();
			player2.setPlayState();
		});

		app.player2.addOnFinishEvent(() -> {
			player2.setPauseState();
		});

		mainPane.afterShow();
		player1.afterShow();
		player2.afterShow();
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
		FXMLLoader loader = new FXMLLoader(GuiApp.class.getClassLoader().getResource("fxml/" + file));
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
	
	private Configuration getConfiguration() throws IOException, ConfigurationException {
		org.apache.commons.configuration2.builder.fluent.Parameters params = new org.apache.commons.configuration2.builder.fluent.Parameters();
		File propertiesFile = new File("config.properties");

		FileBasedConfigurationBuilder<FileBasedConfiguration> builder = 
				new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
				.configure(params.fileBased().setFile(propertiesFile));
		builder.setAutoSave(true);

		propertiesFile.createNewFile();
		return builder.getConfiguration();
	}
}
