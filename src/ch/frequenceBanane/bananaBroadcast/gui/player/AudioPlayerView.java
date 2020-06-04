package ch.frequenceBanane.bananaBroadcast.gui.player;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import ch.frequenceBanane.bananaBroadcast.audio.*;
import ch.frequenceBanane.bananaBroadcast.gui.GuiApp;
import ch.frequenceBanane.bananaBroadcast.utils.AudioUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;

public class AudioPlayerView {
	
	@FXML private GridPane playerRoot;
	@FXML private Label title;
	@FXML private Pane backgroundPane;
	
	@FXML protected Button button_play;
	@FXML protected Button button_repeat;
	@FXML protected Region button_play_region;
	@FXML protected Region button_repeat_region;
	
	@FXML private Region button_previous_region;
	@FXML private Region button_next_region;
	
	@FXML private Button button_previous;
	@FXML private Button button_next;
	
	@FXML protected Label remaining;
	
	@FXML private Line middleLine;
	
	@FXML protected Label elapsed;
	@FXML protected Label artist;
	
	protected AudioPlayer audioPlayer;
	
	protected AudioPlayerView(){}
	
	public AudioPlayerView(AudioPlayer audioPlayer) throws IOException {
		this.audioPlayer = audioPlayer;
		setOnLoadEvent();
		GuiApp.loadLayout(this, "MusicPlayer.fxml");
	}
	
	@FXML
    public void initialize() throws IOException { 		
		audioPlayer.addOnPlayEvent( () -> { 
			Platform.runLater( () -> startTimer());
		});
		
		audioPlayer.addOnFinishEvent(() ->{
			Platform.runLater( () -> setPauseState());
		});
		
		getRootLayout().getStylesheets().add("ressources/css/musicPlayer.css");
		
		//Responsive
		getRootLayout().widthProperty().addListener((obs, oldValue, newValue) -> {
			middleLine.setEndX(getRootLayout().getWidth());
		});
		
		setTitle();
		updateTimers();
		setEvents();
    } 
    
    public void afterShow() {
		loadWaveform();
    }
	
	// TODO copie défensive ??
	public GridPane getRootLayout() {
		return playerRoot;
	}
	
	public void loadWaveform(){
		if(audioPlayer.getCurrentAudioFile() == null)
			return;

		int width  = (int) backgroundPane.getWidth();
		int height = (int) backgroundPane.getHeight();
		
		if(width == 0 || height == 0)
			return;
		
		ByteArrayOutputStream outputStream = AudioUtils.getWaveform(width*6, height*6, audioPlayer.getCurrentAudioFile());
		if(outputStream != null) {
			ByteArrayInputStream stream   = new ByteArrayInputStream(outputStream.toByteArray());
			Image background = new Image(stream, width, height, false, true);
			BackgroundImage backgroundImg = new BackgroundImage(background, null, null, null, BackgroundSize.DEFAULT);
			backgroundPane.setBackground(new Background(backgroundImg));
		}
	}
	
	public void startTimer() {		
		Thread thread = new Thread() {
			public void run() {
				while(audioPlayer.isPlaying()) {
					Platform.runLater( () -> {updateTimers();} );
					try {
						Thread.sleep(10);
					} catch(Exception e) {}
				}
			}
		};
		thread.start();
	}
	
	public void setTitle() {
		Platform.runLater( () ->{
			if(audioPlayer.getCurrentAudioFile() != null)
				title.setText(audioPlayer.getCurrentAudioFile().title);
		});
	}
	
	public void reload() {
		audioPlayer.reload();
	}
	
	public void setRepeatedActivatedState() {
		button_repeat_region.getStyleClass().add("button_repeat_activated");
		button_repeat_region.getStyleClass().removeAll("button_play_region_play");
	}
	
	public void setRepeatedDisabledState() {
		button_repeat_region.getStyleClass().removeAll("button_repeat_activated");
		button_repeat_region.getStyleClass().add("button_play_region_stop");
	}
	
	public void setPlayState() {
		button_play_region.getStyleClass().removeAll("button_play_region_play");
		button_play_region.getStyleClass().add("button_play_region_pause");
	}
	
	public void setPauseState() {
		button_play_region.getStyleClass().removeAll("button_play_region_pause");
		button_play_region.getStyleClass().add("button_play_region_play");
	}
	
	public void updateTimers() {
		Platform.runLater( () ->
			remaining.setText("- "+formatTime(audioPlayer.getRemainingTime()))
		);
	}
	
	public static String formatTime(double duration) {
		int min = (int) Math.floor(duration/60);
		duration -= min*60;
		int sec = (int) Math.floor(duration);
		duration -= sec;
		int subSec = (int) Math.floor(duration*100);
		return String.format("%02d",min)+" : "+String.format("%02d",sec)+" : "+String.format("%02d",subSec);
	}
	
	protected void setEvents() {
		button_previous.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent e) {
		    	audioPlayer.goToStart();
		}});
		button_play.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent e) {
		    	if(audioPlayer.isPlaying()) {
		    		audioPlayer.pause();
		    		setPauseState();
		    	}else {
		    		audioPlayer.play();
		    		startTimer();
		    		setPlayState();
		    	}
		}});
		button_repeat.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent e) { 
		    	if(audioPlayer.isRepeating()) {
		    		audioPlayer.setRepeat(false);
		    		setRepeatedDisabledState();
		    	}else {
		    		audioPlayer.setRepeat(true);
		    		setRepeatedActivatedState();
		    	}
		}});
		button_next.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent e) {
		    	audioPlayer.goToEnd();
		}});
	}
	
	private void setOnLoadEvent() {
		audioPlayer.addOnLoadEvent( () -> { 
			Platform.runLater( () -> {
				setTitle();
				artist.setText(" - ");
				loadWaveform();
				updateTimers();
			});
		});
	}
}
