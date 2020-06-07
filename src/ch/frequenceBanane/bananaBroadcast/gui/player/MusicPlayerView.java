package ch.frequenceBanane.bananaBroadcast.gui.player;

import java.io.IOException;

import ch.frequenceBanane.bananaBroadcast.audio.*;
import ch.frequenceBanane.bananaBroadcast.database.*;
import ch.frequenceBanane.bananaBroadcast.gui.GuiApp;
import ch.frequenceBanane.bananaBroadcast.utils.Log;
import javafx.application.Platform;
import javafx.fxml.FXML;

/**
 * Graphical view of a MusicPlayer
 * @author Corentin
 * @author corentin.junod@epfl.ch
 */
public class MusicPlayerView extends AudioPlayerView{
	
	private MusicPlayer musicPlayer;
	
	/**
	 * Instantiate a new MusicPlayerView
	 * @param musicPlayer the music player controlled by the view
	 * @throws IOException If an error occurs during the layout file reading
	 */
	public MusicPlayerView(MusicPlayer musicPlayer) throws IOException {
		this.musicPlayer = musicPlayer;
		this.audioPlayer = musicPlayer;
		setOnLoadEvent();
		GuiApp.loadLayout(this, "player/MusicPlayer.fxml");
	}
	
	@FXML
	public void initialize() throws IOException { 
		super.initialize();
		setOnLoadEvent();
		updateView();
	}
	
	private void setOnLoadEvent() {
		audioPlayer.addOnLoadEvent( () -> { 
			Platform.runLater( () -> updateView());
		});
	}
	
	private void updateView() {
		Platform.runLater( () -> {
			Music curMusic = musicPlayer.getCurrentAudioFile();
			if(curMusic != null) {
				artist.setText(curMusic.artist);
				loadWaveform();
				updateTimers();
			}else {
				Log.warning("Trying to update a view with no music loaded");
			}
		});
	}
}
