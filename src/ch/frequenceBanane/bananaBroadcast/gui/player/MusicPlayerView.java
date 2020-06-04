package ch.frequenceBanane.bananaBroadcast.gui.player;

import java.io.IOException;

import ch.frequenceBanane.bananaBroadcast.audio.*;
import ch.frequenceBanane.bananaBroadcast.database.*;
import ch.frequenceBanane.bananaBroadcast.gui.GuiApp;
import ch.frequenceBanane.bananaBroadcast.utils.Log;
import javafx.application.Platform;
import javafx.fxml.FXML;

public class MusicPlayerView extends AudioPlayerView{
	
	private MusicPlayer musicPlayer;
	
	public MusicPlayerView(MusicPlayer musicPlayer) throws IOException {
		this.musicPlayer = musicPlayer;
		this.audioPlayer = musicPlayer;
		setOnLoadEvent();
		GuiApp.loadLayout(this, "MusicPlayer.fxml");
	}
	
	@FXML
	public void initialize() throws IOException { 
		super.initialize();
		setOnLoadEvent();
		updateView();
	}
	
	public void updateTimers() {
		remaining.setText("- "+formatTime(audioPlayer.getRemainingTime()));
		elapsed.setText(formatTime(audioPlayer.getElapsedTime()));
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
