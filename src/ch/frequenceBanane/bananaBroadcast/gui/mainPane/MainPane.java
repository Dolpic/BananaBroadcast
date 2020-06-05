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
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainPane{

	@FXML private Pane topLeftPane;
	@FXML private Pane topRightPane;
	@FXML private Pane bottomTopPane;
	@FXML private Pane bottomBottomPane;
	@FXML private VBox mainPaneRoot;
	
	@FXML private MenuItem buttonQuit;
	@FXML private MenuItem buttonHelp;
	
	@FXML private GridPane tableJingles;
	
	@FXML private Pane mainPlayerPane;
	@FXML private Pane databaseListPane;
	
	@FXML private Pane categorySelectorPane;
	
	@FXML private Button schedulerButton;
	@FXML private Button addMusicButton;
	@FXML private ToggleButton manualButton;
	
	private CartouchesArray jingles;
	private Stage primaryStage;
	private Scheduler scheduler;
	private MusicDatabase database;
	private BananaBroadcast app;
	private ArrayList<JinglePlayerView> jinglesPlayer = new ArrayList<JinglePlayerView>();
	
	public MainPane(BananaBroadcast app, Stage primaryStage) throws IOException{
		this.jingles = app.jingles;
		this.scheduler = app.scheduler;
		this.primaryStage = primaryStage;
		this.database = app.database;
		this.app = app;
        GuiApp.loadLayout(this, "MainPane.fxml");
	}
	
	@FXML
    public void initialize() throws IOException {
		for(int i=0; i<jingles.size(); i++) {		
			JinglePlayerView cur = new JinglePlayerView(jingles.get(i));
			jinglesPlayer.add(cur);
			tableJingles.add(cur.getRootLayout(), 
							i%tableJingles.getColumnCount(), 
							i/tableJingles.getColumnCount());
		}
		loadEvents();
	}
	
	public void afterShow() {
		for(int i=0; i<jinglesPlayer.size(); i++) {
			jinglesPlayer.get(i).loadWaveform();
		}
	}
	
	public void addTopLeftNode(Pane pane) {
		topLeftPane.getChildren().add(pane);
		makeResponsive(topLeftPane, pane);
	}
	
	public void addTopRightNode(Pane pane) {
		topRightPane.getChildren().add(pane);
		makeResponsive(topRightPane, pane);
	}
	
	public void addBottomTopNode(Pane pane) {
		bottomTopPane.getChildren().add(pane);
		makeResponsive(bottomTopPane, pane);
	}
	
	public void addBottomBottomNode(Pane pane) {
		bottomBottomPane.getChildren().add(pane);
		makeResponsive(bottomBottomPane, pane);
	}
	
	public void addMainPlayerPane(Pane pane) {
		mainPlayerPane.getChildren().add(pane);
		makeResponsive(mainPlayerPane, pane);
	}
	
	public void addDatabaseListPane(Pane pane) {
		databaseListPane.getChildren().add(pane);
		makeResponsive(databaseListPane, pane);
	}
	
	public void addCategorySelector(Pane pane) {
		categorySelectorPane.getChildren().add(pane);
		makeResponsive(categorySelectorPane, pane);
	}
	
	public VBox getRootLayout() {
		return mainPaneRoot;
	}
	
	private void makeResponsive(Pane innerPane, Pane targetPane) {
		innerPane.widthProperty().addListener((obs, oldValue, newValue) -> {
			targetPane.setPrefWidth(innerPane.getWidth());
		});
		innerPane.heightProperty().addListener((obs, oldValue, newValue) -> {
			targetPane.setPrefHeight(innerPane.getHeight());
		});
	}
	
	private void loadEvents() {
		buttonQuit.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		        Platform.exit();
		        System.exit(0);
		    }
		});
		
		schedulerButton.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
	            try {
					new SchedulerView(scheduler);
				} catch (IOException e1) {
					Log.error("Unable to create the Scheduler : "+e1.getMessage());
				}
		    }
		});
		
		addMusicButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
            	FileChooser fileChooser = new FileChooser();
                List<File> list = fileChooser.showOpenMultipleDialog(primaryStage);
                if (list != null) {
                    for (File file : list) {
                        Music music = AudioUtils.getAudioMetadata(file.getAbsolutePath());
                        try {
							database.addNewMusic(music);
						} catch (SQLException e1) {
							Log.error("Can't add the music : "+music.title+" - Reason : "+e1.getMessage());
						}
                    }
                }
            }
        });
		
		manualButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
            	app.setManual(manualButton.isSelected());
            }
        });
	}
}
