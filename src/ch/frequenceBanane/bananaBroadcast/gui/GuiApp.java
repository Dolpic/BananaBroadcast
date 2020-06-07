package ch.frequenceBanane.bananaBroadcast.gui;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.function.Consumer;

import ch.frequenceBanane.bananaBroadcast.BananaBroadcast;
import ch.frequenceBanane.bananaBroadcast.database.AudioFile;
import ch.frequenceBanane.bananaBroadcast.database.Music;
import ch.frequenceBanane.bananaBroadcast.database.MusicDatabase.Kind;
import ch.frequenceBanane.bananaBroadcast.gui.categorySelector.CategorySelectorView;
import ch.frequenceBanane.bananaBroadcast.gui.mainPane.MainPane;
import ch.frequenceBanane.bananaBroadcast.gui.player.AudioPlayerView;
import ch.frequenceBanane.bananaBroadcast.gui.player.MusicPlayerView;
import ch.frequenceBanane.bananaBroadcast.gui.playlist.AudioFileListView;
import ch.frequenceBanane.bananaBroadcast.utils.Log;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Main class of the software, JavaFX Application
 * @author Corentin
 * @author corentin.junod@epfl.ch
 */
public class GuiApp extends Application{
	
    private BananaBroadcast app;
    
    private MusicPlayerView player1;
    private MusicPlayerView player2;
    private AudioPlayerView mainPlayer;
    
    private MainPane mainPane;
    
    private AudioFileListView<Music> playlist;
    private AudioFileListView<Music> playlistOld;
    private AudioFileListView<AudioFile> databaseList;
    
    private CategorySelectorView categorySelector;
    
	private final String databaseUrl      = "jdbc:mysql://localhost:3307/bananabroadcast";
	private final String databaseUser     = "root";
	private final String databasePassword = "usbw";
    
	/** Main functions called by JavaFX. Instantiate the whole program */
    @Override
    public void start(Stage primaryStage) throws Exception {
    	primaryStage.setTitle("BananaBroadcast");
        try {
        	app = new BananaBroadcast(databaseUrl, databaseUser, databasePassword);
        }catch(SQLException e){
        	die("Unable to connect to the database : "+e.getMessage());
        }
        
        playlist     = new AudioFileListView<>(app.playlist, AudioFileListView.getMusicData());
        playlistOld  = new AudioFileListView<>(app.playlistOld, AudioFileListView.getMusicData());
        databaseList = new AudioFileListView<>(app.databaseList, AudioFileListView.getAudioData());
        
        player1      = new MusicPlayerView(app.player1);
        player2      = new MusicPlayerView(app.player2);
        mainPlayer   = new AudioPlayerView(app.mainPlayer);
        mainPane     = new MainPane(app, primaryStage);
        
        categorySelector = new CategorySelectorView(app.categorySelector);
        
        app.categorySelector.setOnSelectedCategoriesChange((selected) -> {
        	ArrayList<AudioFile> newList = null;
			try {
				newList = app.database.getFromCategoriesAndKind(Kind.MUSIC, selected);
	        	app.databaseList.removeAll();
	        	app.databaseList.addAtEnd(newList);
	        	databaseList.updateView();
			} catch (Exception e) {
				Log.error("Unable to update the categorie lists : "+e.getMessage());
			}
        });
        mainPane.addTopLeftNode(player1.getRootLayout());
        mainPane.addTopRightNode(player2.getRootLayout());
        mainPane.addMainPlayerPane(mainPlayer.getRootLayout());
        mainPane.addBottomTopNode(playlistOld.getRootLayout());
        mainPane.addBottomBottomNode(playlist.getRootLayout());
        mainPane.addDatabaseListPane(databaseList.getRootLayout());
        mainPane.addCategorySelector(categorySelector.getRootLayout());
        
        Consumer<AudioFile> loadAudioFileInPlayer = (AudioFile audioFile) -> {app.mainPlayer.load(audioFile);};

        playlist.setOnElementDoubleClick(loadAudioFileInPlayer);
        playlistOld.setOnElementDoubleClick(loadAudioFileInPlayer);
        databaseList.setOnElementDoubleClick(loadAudioFileInPlayer);
        
        playlist.setSortable(false);

        Scene scene = new Scene(mainPane.getRootLayout());
        primaryStage.setScene(scene);
        primaryStage.show();

        mainPane.afterShow();
        player1.afterShow();
        player2.afterShow();
        
        Log.log("10");
    }
    
    /**
     * Utility function to load a JavaFX layout file (.fxml)
     * @param controller The Object on which the layout is loaded, generally the caller itself (this)
     * @param path The path to the .fxml layout file
     * @throws IOException If an error occurs during the file operations
     */
    public static void loadLayout(final Object controller, final String path) throws IOException {
    	FXMLLoader loader = new FXMLLoader(GuiApp.class.getResource(path));
    	loader.setController(controller);
    	loader.load();
    }
    
    /**
     * Automatically resize the given innerPane to the size of the targetPane
     * @param innerPane the innerPane to resize automatically
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
    
    /** Stop the execution right away with an error message*/
    public static void die(final String msg) {
    	Log.error(msg);
    	Platform.exit();
    	System.exit(1);
    }
    
    /** Program entry point */
    public static void main(String[] args) {
        launch(args);
    }
}
