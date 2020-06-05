package ch.frequenceBanane.bananaBroadcast;

import java.sql.SQLException;
import java.util.ArrayList;

import ch.frequenceBanane.bananaBroadcast.GPIO.*;
import ch.frequenceBanane.bananaBroadcast.audio.*;
import ch.frequenceBanane.bananaBroadcast.categories.*;
import ch.frequenceBanane.bananaBroadcast.database.*;
import ch.frequenceBanane.bananaBroadcast.scheduling.*;
import ch.frequenceBanane.bananaBroadcast.utils.CartouchesArray;

/**
 * Main class of the non-GUI application.
 * Instantiate all the different parts of the application.
 * @author Corentin Junod
 * @author corentin.junod@epfl.ch
 */
public class BananaBroadcast {
	
	public final MusicPlayer player1;
	public final MusicPlayer player2;
	public final AudioPlayer mainPlayer;
	
	public final GPIOInterface gpio;
	public final Recorder recorder;
	public final MusicDatabase database;
	
	public final Playlist<Music>     playlist;
	public final Playlist<Music>     playlistOld;
	public final Playlist<AudioFile> databaseList;
	
	public final Scheduler scheduler;
	public final CartouchesArray jingles;
	
	public final CategorySelector categorySelector;
	
	private final int playlisteSize = 20;
	private final byte[] gpioIp     = {10, 10, 2, (byte) 235};
	private final int gpioPort      = 93;
	
	private boolean isInManual = false;
	
	/**
	 * 
	 * @param databaseUrl
	 * @param databaseUser
	 * @param databasePassword
	 * @throws Exception 
	 */
	public BananaBroadcast(final String databaseUrl, final String databaseUser, final String databasePassword) throws Exception {
		try {
			database         = new MusicDatabase(databaseUrl, databaseUser, databasePassword);
			player1          = new MusicPlayer();
			player2          = new MusicPlayer();
			mainPlayer       = new AudioPlayer();
			gpio             = new GPIOInterface(gpioIp, gpioPort);
			recorder         = new Recorder("records/");
			playlist         = new Playlist<>();
			playlistOld      = new Playlist<>();
			databaseList     = new Playlist<>();
			scheduler        = new Scheduler(database);
			jingles          = new CartouchesArray(database, "");
			categorySelector = new CategorySelector(database);
		}catch(SQLException e) {
			throw new SQLException("Database error occurs during main class creation : "+e.getMessage());
		}
	}
	
	public void initialize() throws SQLException {
		gpio.bindGPIOFunction(1, 4, () -> player1.pause() );
		gpio.bindGPIOFunction(1, 3, () -> player1.play() );
		gpio.bindGPIOFunction(2, 4, () -> player2.pause() );
		gpio.bindGPIOFunction(2, 3, () -> player2.play() );
		
		player1.addOnFinishEvent( () -> {
			AudioFile next = playlist.getNext();
			player1.close();
			playlistOld.addAtEnd(player1.getCurrentAudioFile());
			player1.load(next);
			if(!isInManual)
				player2.play();
		});
		
		player2.addOnFinishEvent( () -> {
			AudioFile next = playlist.getNext();
			player2.close();
			playlistOld.addAtEnd(player2.getCurrentAudioFile());
			player2.load(next);
			if(!isInManual)
				player1.play();
		});
		
		playlist.addAtEnd(scheduler.getNextMusics(playlisteSize));
		databaseList.addAtEnd(new ArrayList<AudioFile>(database.getAllMusics()));
		
		Music next = playlist.getNext();
		if(next != null) {
			player1.loadMusic(next);
		}
		next = playlist.getNext();
		if(next != null) {
			player2.loadMusic(next);
		}
	}
	
	public void setManual(final boolean isManual) {
		isInManual = isManual;
	}
	
	public void startGPIO() {
		gpio.start();
	}
	
	public void stopGPIO() {
		gpio.close();
	}
}
