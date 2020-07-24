package ch.frequenceBanane.bananaBroadcast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.ex.ConfigurationException;

import ch.frequenceBanane.bananaBroadcast.GPIO.*;
import ch.frequenceBanane.bananaBroadcast.audio.*;
import ch.frequenceBanane.bananaBroadcast.categories.*;
import ch.frequenceBanane.bananaBroadcast.database.*;
import ch.frequenceBanane.bananaBroadcast.scheduling.*;

/**
 * Main class of the non-GUI application. Instantiate all the different parts of
 * the application.
 * 
 * @author Corentin Junod
 * @author corentin.junod@epfl.ch
 */
public class BananaBroadcast {

	public static final int DEFAULT_PLAYLIST_SIZE = 20;

	public final AudioPlayer player1;
	public final AudioPlayer player2;
	public final AudioPlayer mainPlayer;

	public final GPIOInterface gpio;
	public final Recorder recorder;
	public final MusicDatabase database;

	public final Playlist<AudioFile> playlist;
	public final Playlist<AudioFile> playlistOld;
	public final Playlist<AudioFile> databaseList;

	public final Scheduler scheduler;
	public final CartouchesArray cartouches;

	public final CategorySelector categorySelector;

	private boolean isInManual = false;
	private static final String CONFIG_FILENAME     = "config.properties";
	private static final String DEFAULT_CONFIG_PATH = "/config/config.properties.default";

	/**
	 * Start the application and link it with the given database
	 * 
	 * @param databaseUrl      the database URL
	 * @param databaseUser     the username to connect to the database
	 * @param databasePassword the password associated with the given username
	 * @throws SQLException if the connection to the database fail or an error
	 *                      occurs during the instantiation
	 * @throws ConfigurationException 
	 * @throws IOException 
	 */
	public BananaBroadcast() throws SQLException, IOException, ConfigurationException {
		
		Configuration config    = getConfiguration();
		String databaseUrl      = config.getString("Database_URL");
		String databaseUser     = config.getString("Database_User");
		String databaseName     = config.getString("Database_Name");
		String databasePassword = config.getString("Database_Password");
		String gpioIp           = config.getString("GPIO_IP");
		int gpioPort            = config.getInt("GPIO_Port");
		
		String[] ipSplit = gpioIp.split("\\.");
		
		if(ipSplit.length != 4) {
			throw new IllegalArgumentException("Given IP address has an invalid format : "+gpioIp);
		}
		
		byte[] ip = {0,0,0,0};
		for(int i=0; i<4; i++) {
			ip[i] = (byte)Integer.parseInt(ipSplit[i]);
		}
		
		try {
			database = new MusicDatabase(databaseUrl, databaseName, databaseUser, databasePassword);
			player1 = new AudioPlayer();
			player2 = new AudioPlayer();
			mainPlayer = new AudioPlayer();
			gpio = new GPIOInterface(ip, gpioPort);
			recorder = new Recorder("records/");
			playlist = new Playlist<>();
			playlistOld = new Playlist<>();
			databaseList = new Playlist<>();
			scheduler = new Scheduler(database);
			cartouches = new CartouchesArray(database, "");
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

	private void initializePlayer(AudioPlayer player, AudioPlayer nextPlayerToPlay) {
		player.addOnFinishEvent(() -> {
			player.close();
			playlistOld.addAtEnd(player.getCurrentAudioFile());
			player.load(playlist.getNext());
			if (!isInManual)
				nextPlayerToPlay.play();
		});
	}

	/** Reload the playlist and load the two next songs into the players */
	public void loadMusics() {
		playlist.removeAll();
		playlist.addAtEnd(scheduler.getNextMusics(DEFAULT_PLAYLIST_SIZE));
		AudioFile next = playlist.getNext();
		if (next != null) {
			player1.load(next);
		}
		next = playlist.getNext();
		if (next != null) {
			player2.load(next);
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
	
	private Configuration getConfiguration() throws IOException, ConfigurationException {
		org.apache.commons.configuration2.builder.fluent.Parameters params = new org.apache.commons.configuration2.builder.fluent.Parameters();
		File propertiesFile = new File(CONFIG_FILENAME);
		
		if(!propertiesFile.exists()) {
			propertiesFile.createNewFile();
			 FileOutputStream writer = new FileOutputStream(propertiesFile);
			 System.out.println("DEBUG:"+App.getResourceAsStream(DEFAULT_CONFIG_PATH));
			 writer.write(App.getResourceAsStream(DEFAULT_CONFIG_PATH).readAllBytes());
			 writer.close();
		}

		FileBasedConfigurationBuilder<FileBasedConfiguration> builder = 
				new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
				.configure(params.fileBased().setFile(propertiesFile));
		builder.setAutoSave(true);

		propertiesFile.createNewFile();
		return builder.getConfiguration();
	}
}
