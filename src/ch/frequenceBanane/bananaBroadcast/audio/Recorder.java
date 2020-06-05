package ch.frequenceBanane.bananaBroadcast.audio;

import javazoom.jl.player.advanced.AdvancedPlayer;

/**
 * Record an input audio stream to a specified file
 * @author Corentin Junod
 * @author corentin.junod@epfl.ch
 */
public class Recorder{
	
	private final AdvancedPlayer player;

	/**
	 * Create a new Recorder save the input audio to a file
	 * @param outputPath to file to save the audio
	 */
	public Recorder(final String outputPath) {
		player = null;
	}
	
	/** Start the recording */
	public void start() {
		
	}
	
	/** Pause the recording */
	public void pause() {
		
	}
	
	/** End the recording */
	public void stop() {
		
	}
	
	/** Select the peripheral from which to listen 
	 * @param index the index of the peripheral
	 */
	public void selectMixer(final int index) {
		
	}
}
