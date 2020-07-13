/*package ch.frequenceBanane.bananaBroadcast.gui.player;

import java.io.IOException;

import ch.frequenceBanane.bananaBroadcast.audio.JinglePlayer;
import ch.frequenceBanane.bananaBroadcast.gui.GuiApp;
import javafx.application.Platform;
import javafx.event.*;
import javafx.fxml.FXML;

public class JinglePlayerView extends AudioPlayerView {

	public JinglePlayerView(AudioPlayer jinglePlayer) throws IOException {
		audioPlayer = jinglePlayer;
		GuiApp.loadLayout(this, "JinglePlayer.fxml");
	}

	@FXML
	public void initialize() throws IOException {
		audioPlayer.addOnPlayEvent(() -> {
			Platform.runLater(() -> {
				startTimer();
			});
		});

		getRootLayout().getStylesheets().add("css/jinglePlayer.css");

		audioPlayer.addOnFinishEvent(() -> {
			audioPlayer.reload();
			updateTimers();
			setPauseState();
		});
		setTitle();
		updateTimers();
		setEvents();
	}

	/** Update the timers shown in the player 
	public void updateTimers() {
		Platform.runLater(() -> remaining.setText("- " + formatTime(audioPlayer.getRemainingTime())));
	}

	protected void setEvents() {
		button_play.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				if (audioPlayer.isPlaying()) {
					audioPlayer.reload();
					updateTimers();
					setPauseState();
				} else {
					audioPlayer.play();
					startTimer();
					setPlayState();
				}
			}
		});
		button_repeat.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				if (audioPlayer.isRepeating()) {
					audioPlayer.setRepeat(false);
					setRepeatedDisabledState();
				} else {
					audioPlayer.setRepeat(true);
					setRepeatedActivatedState();
				}
			}
		});
	}
}*/
