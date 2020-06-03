package ch.frequenceBanane.bananaBroadcast.audio;

import ch.frequenceBanane.bananaBroadcast.database.Jingle;

public class JinglePlayer extends AudioPlayer{
	
	private Jingle jingle;
	
	public JinglePlayer() {
		super();
	}
	
	public void load(Jingle file) {
		this.jingle = file;
		super.load(file);
	}
	
	/** @return A copy of the current music used by the audioPlayer, null if no music is loaded */
	public Jingle getCurrentAudioFile() {
		return jingle;
	}
}
