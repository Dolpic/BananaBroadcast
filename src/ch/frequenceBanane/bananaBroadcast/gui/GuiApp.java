package ch.frequenceBanane.bananaBroadcast.gui;

import java.io.IOException;
import java.net.ConnectException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.function.Consumer;

import javax.sound.sampled.UnsupportedAudioFileException;

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
import javafx.stage.Stage;

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
    
    @Override
    public void start(Stage primaryStage) throws Exception {
    	primaryStage.setTitle("BananaBroadcast");
        try {
        	app = new BananaBroadcast(databaseUrl, databaseUser, databasePassword);
        }catch(ConnectException e){
        	Log.error("Unable to connect to the database");
        	die();
        }
        app = new BananaBroadcast(databaseUrl, databaseUser, databasePassword);
        app.initialize();
        
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
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	app.databaseList.removeAll();
        	app.databaseList.addAtEnd(newList);
        	databaseList.updateView();
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

        Scene scene = new Scene(mainPane.getRootLayout());
        primaryStage.setScene(scene);
        primaryStage.show();
        
        mainPane.afterShow();
        player1.afterShow();
        player2.afterShow();
    }
    
    public static void loadLayout(Object controller, String path) throws IOException {
    	FXMLLoader loader = new FXMLLoader(GuiApp.class.getResource(path));
    	loader.setController(controller);
    	loader.load();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
    public static void die() {
    	Platform.exit();
    	System.exit(1);
    }
}
