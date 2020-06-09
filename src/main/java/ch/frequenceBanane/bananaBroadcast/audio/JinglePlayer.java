package ch.frequenceBanane.bananaBroadcast.audio;

import ch.frequenceBanane.bananaBroadcast.database.Jingle;

/**
 * Player designed to play Jingle files
 * @author Corentin Junod
 * @author corentin.junod@epfl.ch
 */
public class JinglePlayer extends AudioPlayer{
	
	private Jingle jingle;
	
	/** Creates a new JinglePlayer **/
	public JinglePlayer() {
		super();
	}
	
	/**
	 * Load a Jingle
	 * @param file The Jingle to load
	 */
	public void load(Jingle file) {
		this.jingle = file;
		super.load(file);
	}
	
	/** @return A copy of the current Jingle used by the audioPlayer, null if no jingle is loaded */
	public Jingle getCurrentAudioFile() {
		return jingle;
	}
}
