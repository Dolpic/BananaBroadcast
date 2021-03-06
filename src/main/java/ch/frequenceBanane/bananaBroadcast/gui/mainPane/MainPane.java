package ch.frequenceBanane.bananaBroadcast.gui.mainPane;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import ch.frequenceBanane.bananaBroadcast.BananaBroadcast;
import ch.frequenceBanane.bananaBroadcast.audio.CartouchesArray;
import ch.frequenceBanane.bananaBroadcast.database.AudioFile;
import ch.frequenceBanane.bananaBroadcast.database.MusicDatabase;
import ch.frequenceBanane.bananaBroadcast.database.MusicDatabase.Kind;
import ch.frequenceBanane.bananaBroadcast.gui.GuiApp;
import ch.frequenceBanane.bananaBroadcast.gui.categorySelector.CategorySelectorView;
import ch.frequenceBanane.bananaBroadcast.gui.player.AudioPlayerView;
import ch.frequenceBanane.bananaBroadcast.gui.player.AudioPlayerView.AudioPlayerKind;
import ch.frequenceBanane.bananaBroadcast.gui.playlist.AudioFileListView;
import ch.frequenceBanane.bananaBroadcast.gui.scheduler.SchedulerView;
import ch.frequenceBanane.bananaBroadcast.scheduling.Scheduler;
import ch.frequenceBanane.bananaBroadcast.utils.AudioUtils;
import ch.frequenceBanane.bananaBroadcast.utils.Log;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * The main layout of the app. Contains all others GUI part of the program
 * 
 * @author Corentin
 * @author corentin.junod@epfl.ch
 */
public class MainPane {

	@FXML
	private Pane topLeftPane;
	@FXML
	private Pane topRightPane;
	@FXML
	private Pane bottomTopPane;
	@FXML
	private Pane bottomBottomPane;
	@FXML
	private VBox mainPaneRoot;

	@FXML
	private MenuItem buttonQuit;
	@FXML
	private MenuItem buttonAbout;
	@FXML
	private MenuItem buttonOptions;
	
	@FXML
	private GridPane tableJingles;

	@FXML
	private Pane mainPlayerPane;
	@FXML
	private Pane databaseListPane;

	@FXML
	private Pane categorySelectorPane;

	@FXML
	private Button schedulerButton;
	@FXML
	private Button addMusicButton;
	@FXML
	private Button refreshPlaylistButton;
	@FXML
	private ToggleButton manualButton;

	private final CartouchesArray cartouches;
	private final Stage primaryStage;
	private final Scheduler scheduler;
	private final MusicDatabase database;
	private final BananaBroadcast app;
	private final ArrayList<AudioPlayerView> cartouchesPlayer = new ArrayList<AudioPlayerView>();
	
	private AudioPlayerView player1;
	private AudioPlayerView player2;
	private AudioPlayerView mainPlayer;
	
	private AudioFileListView playlist;
	private AudioFileListView playlistOld;
	private AudioFileListView databaseList;
	
	private CategorySelectorView categorySelector;

	/**
	 * Create a new main layout for the app
	 * 
	 * @param app          The app to which the layout must be created
	 * @param primaryStage The primaryStage given by JavaFX
	 * @throws IOException If an error occurs during layout file reading
	 */
	public MainPane(BananaBroadcast app, Stage primaryStage) throws IOException {
		this.cartouches = app.cartouches;
		this.scheduler = app.scheduler;
		this.primaryStage = primaryStage;
		this.database = app.database;
		this.app = app;
		GuiApp.loadLayout(this, "MainPane.fxml");
	}

	@FXML
	public void initialize() throws IOException {
		
		playlist         = new AudioFileListView(app.playlist);
		playlistOld      = new AudioFileListView(app.playlistOld);
		databaseList     = new AudioFileListView(app.databaseList);
		player1          = new AudioPlayerView(app.player1,    AudioPlayerKind.Music);
		player2          = new AudioPlayerView(app.player2,    AudioPlayerKind.Music);
		mainPlayer       = new AudioPlayerView(app.mainPlayer, AudioPlayerKind.Music);
		categorySelector = new CategorySelectorView(app.categorySelector);
		
		for (int i = 0; i < cartouches.size(); i++) {
			AudioPlayerView cur = new AudioPlayerView(cartouches.get(i), AudioPlayerKind.Cartouche);
			cartouchesPlayer.add(cur);
			tableJingles.add(cur.getRootLayout(), i % tableJingles.getColumnCount(), i / tableJingles.getColumnCount());
		}
		
		Consumer<AudioFile> loadAudioFileInPlayer = (AudioFile audioFile) -> {
			app.mainPlayer.load(audioFile);
		};
		
		playlist.setOnElementDoubleClick(loadAudioFileInPlayer);
		playlistOld.setOnElementDoubleClick(loadAudioFileInPlayer);
		databaseList.setOnElementDoubleClick(loadAudioFileInPlayer);

		playlist.setSortable(false);
		

		topLeftPane.getChildren().add(player1.getRootLayout());
		GuiApp.makeResponsive(topLeftPane, player1.getRootLayout());
		
		topRightPane.getChildren().add(player2.getRootLayout());
		GuiApp.makeResponsive(topRightPane, player2.getRootLayout());
		
		mainPlayerPane.getChildren().add(mainPlayer.getRootLayout());
		GuiApp.makeResponsive(mainPlayerPane, mainPlayer.getRootLayout());
		
		bottomTopPane.getChildren().add(playlistOld.getRootLayout());
		GuiApp.makeResponsive(bottomTopPane, playlistOld.getRootLayout());
		
		bottomBottomPane.getChildren().add(playlist.getRootLayout());
		GuiApp.makeResponsive(bottomBottomPane, playlist.getRootLayout());
		
		databaseListPane.getChildren().add(databaseList.getRootLayout());
		GuiApp.makeResponsive(databaseListPane, databaseList.getRootLayout());
		
		categorySelectorPane.getChildren().add(categorySelector.getRootLayout());
		GuiApp.makeResponsive(categorySelectorPane, categorySelector.getRootLayout());
		
		app.categorySelector.setOnSelectedCategoriesChange((selected) -> updateDatabaseList(selected));
		
		loadEvents();
	}

	/** Method to call after the primaryStage is showed */
	public void afterShow() {
		player1.afterShow();
		player2.afterShow();
		
		for (int i = 0; i < cartouchesPlayer.size(); i++) {
			cartouchesPlayer.get(i).loadWaveform();
		}
	}

	/** @return The base Layout of the object */
	public VBox getRootLayout() {
		return mainPaneRoot;
	}

	private void loadEvents() {

		GuiApp.setOnActionButton(buttonQuit, (event) -> {
			Platform.exit();
			System.exit(0);
		});

		GuiApp.setOnActionButton(buttonAbout, (event) -> showAboutDialog());
		
		GuiApp.setOnActionButton(buttonOptions, (event) -> Log.informationDialog("Preferences are not implemented yet"));

		GuiApp.setOnActionButton(schedulerButton, (event) -> {
			try {
				new SchedulerView(scheduler);
			} catch (IOException e) {
				Log.error("Unable to create the Scheduler : " + e.getMessage());
			}
		});

		GuiApp.setOnActionButton(addMusicButton, (event) -> {
			FileChooser fileChooser = new FileChooser();
			List<File> list = fileChooser.showOpenMultipleDialog(primaryStage);
			if (list != null) {
				for (File file : list) {
					AudioFile music = AudioUtils.getAudioMetadata(file.getAbsolutePath());
					try {
						database.addNewMusic(music);
					} catch (SQLException e) {
						Log.error("Can't add the music : " + music.title + " - Reason : " + e.getMessage());
					}
				}
				updateDatabaseList(app.categorySelector.getSelection());
			}
		});

		GuiApp.setOnActionButton(manualButton, (event) -> app.setManual(manualButton.isSelected()));

		GuiApp.setOnActionButton(refreshPlaylistButton, (event) -> app.loadMusics());
	}

	private void showAboutDialog() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("A propos");
		alert.setHeaderText("BananaBroadcast");
		alert.setContentText("Logiciel de broadcasting audio \n\n"
							+ "Développeurs : \n"
							+ " Corentin Junod - corentin.junod@epfl.ch \n\n"
							+ "Développé avec l'aide du Dependable Systems Laboratory, EPFL \n\n"
							+ "2020");
		alert.showAndWait();
	}
	
	private void updateDatabaseList(ArrayList<String> selected) {
		try {
			ArrayList<AudioFile> newList = app.database.getFromCategoriesAndKind(Kind.MUSIC, selected);
			app.databaseList.removeAll();
			app.databaseList.addAtEnd(newList);
			databaseList.updateView();
		} catch (Exception e) {
			Log.error("Unable to update the categorie lists : " + e.getMessage());
		}
	}
}
