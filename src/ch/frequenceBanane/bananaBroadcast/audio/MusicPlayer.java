package ch.frequenceBanane.bananaBroadcast.audio;

import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

import ch.frequenceBanane.bananaBroadcast.database.Music;

public class MusicPlayer extends AudioPlayer{
	
	private Music music;
	
	public MusicPlayer() {
		super();
	}
	
	public void loadMusic(Music file) {
		this.music = file;
		super.load(file);
	}
	
	/** @return A copy of the current music used by the audioPlayer, null if no music is loaded */
	public Music getCurrentAudioFile() {
		return music;
	}
}
