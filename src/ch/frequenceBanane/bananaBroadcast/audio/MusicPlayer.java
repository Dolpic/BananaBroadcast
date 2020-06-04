package ch.frequenceBanane.bananaBroadcast.audio;

import ch.frequenceBanane.bananaBroadcast.database.Music;

/**
 * Player designed to play Music files
 * extends AudioPlayer
 * @author Corentin Junod
 * @author corentin.junod@epfl.ch
 */

public class MusicPlayer extends AudioPlayer{
	
	private Music music;
	
	/** Creates a new MusicPlayer **/
	public MusicPlayer() {
		super();
	}
	
	/**
	 * Load a Music
	 * @param file The Music to load
	 */
	public void loadMusic(Music file) {
		this.music = file;
		super.load(file);
	}
	
	/** @return A copy of the current music used by the audioPlayer, null if no music is loaded */
	public Music getCurrentAudioFile() {
		return music;
	}
}
