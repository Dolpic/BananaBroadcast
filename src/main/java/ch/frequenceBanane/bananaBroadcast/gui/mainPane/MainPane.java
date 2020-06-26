package ch.frequenceBanane.bananaBroadcast.gui.mainPane;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ch.frequenceBanane.bananaBroadcast.BananaBroadcast;
import ch.frequenceBanane.bananaBroadcast.database.Music;
import ch.frequenceBanane.bananaBroadcast.database.MusicDatabase;
import ch.frequenceBanane.bananaBroadcast.gui.GuiApp;
import ch.frequenceBanane.bananaBroadcast.gui.player.JinglePlayerView;
import ch.frequenceBanane.bananaBroadcast.gui.scheduler.SchedulerView;
import ch.frequenceBanane.bananaBroadcast.scheduling.Scheduler;
import ch.frequenceBanane.bananaBroadcast.utils.AudioUtils;
import ch.frequenceBanane.bananaBroadcast.utils.CartouchesArray;
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

	private final CartouchesArray jingles;
	private final Stage primaryStage;
	private final Scheduler scheduler;
	private final MusicDatabase database;
	private final BananaBroadcast app;
	private final ArrayList<JinglePlayerView> jinglesPlayer = new ArrayList<JinglePlayerView>();

	/**
	 * Create a new main layout for the app
	 * 
	 * @param app          The app to which the layout must be created
	 * @param primaryStage The primaryStage given by JavaFX
	 * @throws IOException If an error occurs during layout file reading
	 */
	public MainPane(BananaBroadcast app, Stage primaryStage) throws IOException {
		this.jingles = app.jingles;
		this.scheduler = app.scheduler;
		this.primaryStage = primaryStage;
		this.database = app.database;
		this.app = app;
		GuiApp.loadLayout(this, "MainPane.fxml");
	}

	@FXML
	public void initialize() throws IOException {
		for (int i = 0; i < jingles.size(); i++) {
			JinglePlayerView cur = new JinglePlayerView(jingles.get(i));
			jinglesPlayer.add(cur);
			tableJingles.add(cur.getRootLayout(), i % tableJingles.getColumnCount(), i / tableJingles.getColumnCount());
		}
		loadEvents();
	}

	/** Method to call after the primaryStage is showed */
	public void afterShow() {
		for (int i = 0; i < jinglesPlayer.size(); i++) {
			jinglesPlayer.get(i).loadWaveform();
		}
	}

	public void addTopLeftNode(final Pane pane) {
		topLeftPane.getChildren().add(pane);
		GuiApp.makeResponsive(topLeftPane, pane);
	}

	public void addTopRightNode(final Pane pane) {
		topRightPane.getChildren().add(pane);
		GuiApp.makeResponsive(topRightPane, pane);
	}

	public void addBottomTopNode(final Pane pane) {
		bottomTopPane.getChildren().add(pane);
		GuiApp.makeResponsive(bottomTopPane, pane);
	}

	public void addBottomBottomNode(final Pane pane) {
		bottomBottomPane.getChildren().add(pane);
		GuiApp.makeResponsive(bottomBottomPane, pane);
	}

	public void addMainPlayerPane(final Pane pane) {
		mainPlayerPane.getChildren().add(pane);
		GuiApp.makeResponsive(mainPlayerPane, pane);
	}

	public void addDatabaseListPane(final Pane pane) {
		databaseListPane.getChildren().add(pane);
		GuiApp.makeResponsive(databaseListPane, pane);
	}

	public void addCategorySelector(final Pane pane) {
		categorySelectorPane.getChildren().add(pane);
		GuiApp.makeResponsive(categorySelectorPane, pane);
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
					Music music = AudioUtils.getAudioMetadata(file.getAbsolutePath());
					try {
						database.addNewMusic(music);
					} catch (SQLException e) {
						Log.error("Can't add the music : " + music.title + " - Reason : " + e.getMessage());
					}
				}
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
}
