package ch.frequenceBanane.bananaBroadcast;

import java.sql.SQLException;

import ch.frequenceBanane.bananaBroadcast.GPIO.*;
import ch.frequenceBanane.bananaBroadcast.audio.*;
import ch.frequenceBanane.bananaBroadcast.categories.*;
import ch.frequenceBanane.bananaBroadcast.database.*;
import ch.frequenceBanane.bananaBroadcast.scheduling.*;
import ch.frequenceBanane.bananaBroadcast.utils.CartouchesArray;
import ch.frequenceBanane.bananaBroadcast.utils.Log;

/**
 * Main class of the non-GUI application. Instantiate all the different parts of
 * the application.
 * 
 * @author Corentin Junod
 * @author corentin.junod@epfl.ch
 */
public class BananaBroadcast {

	public static final int DEFAULT_PLAYLIST_SIZE = 20;

	public final MusicPlayer player1;
	public final MusicPlayer player2;
	public final AudioPlayer mainPlayer;

	public final GPIOInterface gpio;
	public final Recorder recorder;
	public final MusicDatabase database;

	public final Playlist<Music> playlist;
	public final Playlist<Music> playlistOld;
	public final Playlist<AudioFile> databaseList;

	public final Scheduler scheduler;
	public final CartouchesArray jingles;

	public final CategorySelector categorySelector;

	private boolean isInManual = false;

	/**
	 * Start the application and link it with the given database
	 * 
	 * @param databaseUrl      the database URL
	 * @param databaseUser     the username to connect to the database
	 * @param databasePassword the password associated with the given username
	 * @throws SQLException if the connection to the database fail or an error
	 *                      occurs during the instantiation
	 */
	public BananaBroadcast(final String databaseUrl, final String databaseUser, final String databasePassword, final String gpioIp, final int gpioPort)
			throws SQLException {
		
		String[] ipSplit = gpioIp.split("\\.");
		
		if(ipSplit.length != 4) {
			throw new IllegalArgumentException("Given IP address has an invalid format : "+gpioIp);
		}
		
		byte[] ip = {0,0,0,0};
		for(int i=0; i<4; i++) {
			ip[i] = (byte)Integer.parseInt(ipSplit[i]);
		}
		
		try {
			database = new MusicDatabase(databaseUrl, databaseUser, databasePassword);
			player1 = new MusicPlayer();
			player2 = new MusicPlayer();
			mainPlayer = new AudioPlayer();
			gpio = new GPIOInterface(ip, gpioPort);
			recorder = new Recorder("records/");
			playlist = new Playlist<>();
			playlistOld = new Playlist<>();
			databaseList = new Playlist<>();
			scheduler = new Scheduler(database);
			jingles = new CartouchesArray(database, "");
			categorySelector = new CategorySelector(database);
		} catch (SQLException e) {
			throw new SQLException("Database error occurs during main class creation : " + e.getMessage());
		}

		initialize();
	}

	private void initialize() throws SQLException {
		gpio.bindGPIOFunction(1, 4, () -> player1.pause());
		gpio.bindGPIOFunction(1, 3, () -> player1.play());
		gpio.bindGPIOFunction(2, 4, () -> player2.pause());
		gpio.bindGPIOFunction(2, 3, () -> player2.play());

		initializePlayer(player1, player2);
		initializePlayer(player2, player1);

		databaseList.addAtEnd(database.getAllMusics());

		loadMusics();
	}

	private void initializePlayer(MusicPlayer player, MusicPlayer nextPlayerToPlay) {
		player.addOnFinishEvent(() -> {
			player.close();
			playlistOld.addAtEnd(player.getCurrentAudioFile());
			player.loadMusic(playlist.getNext());
			if (!isInManual)
				nextPlayerToPlay.play();
		});
	}

	/** Reload the playlist and load the two next songs into the players */
	public void loadMusics() {
		playlist.removeAll();
		playlist.addAtEnd(scheduler.getNextMusics(DEFAULT_PLAYLIST_SIZE));
		Music next = playlist.getNext();
		if (next != null) {
			player1.loadMusic(next);
		}
		next = playlist.getNext();
		if (next != null) {
			player2.loadMusic(next);
		}
	}

	/**
	 * Switch the application to manual mode
	 * 
	 * @param isManual if true set the application to manual mode, set the
	 *                 application to automatic mode otherwise
	 */
	public void setManual(final boolean isManual) {
		isInManual = isManual;
	}

	/** Start the communication with the GPIOs */
	public void startGPIO() {
		gpio.start();
	}

	/** Stop the communication with the GPIOs */
	public void stopGPIO() {
		gpio.close();
	}
}
