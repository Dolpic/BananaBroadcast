/**
 * @question Est-ce bien d'avoir des subclasses ?
 * TODO permettre de supprimer des fonctions trigerrables
*/

package ch.frequenceBanane.bananaBroadcast.audio;

import java.io.IOException;
import javax.sound.sampled.*;

import ch.frequenceBanane.bananaBroadcast.database.*;
import ch.frequenceBanane.bananaBroadcast.utils.AudioUtils;
import ch.frequenceBanane.bananaBroadcast.utils.MixersUtilities;

/**
 * Enable to load, play, pause and close an audio file over a selected audio peripheral
 * @author Corentin
 * @author corentin.junod@epfl.ch
 */
public class AudioPlayer{
	
	private Clip currentClip;
	private boolean isPlaying   = false;
	private boolean isRepeating = false;
	private LineListener loopListener;
	
	private AudioFile audioFile;
	
	/** Create a new AudioPlayer with the first peripheral of the system selected*/
	public AudioPlayer(){
		selectMixer(0);
	}
	
	/**
	 * Load a music in memory, then the AudioPlayer is ready to play
	 * @param music the music to load
	 * @return true if the current music was successfully loaded, false if an error occurred
	 * @throws IllegalArgumentException if the music is null, or his path is empty
	 */
	public boolean load(AudioFile file){
		audioFile = file;
		
		if(file == null)
			throw new IllegalArgumentException("Music parameter is null");
		if(file.path.equals(""))
			throw new IllegalArgumentException("Music path is null");

		AudioInputStream inputStream = AudioUtils.getAudioFileStream(currentClip.getFormat(), file.path);
		try {
			close();
			currentClip.open(inputStream);
			return true;
		} catch (LineUnavailableException e) {
			System.err.println("Unavailable output peripheral");
		} catch (IllegalStateException e) {
			System.err.println("Error : the player can't be stopped");
		} catch (IOException e) {
			System.err.println("Error in file input / output");
		}
		return false;
	}
	
	/** Start the current loaded music. If no music is loaded, does nothing*/
	public void play() {
		System.out.println("PLAY!");
		if(currentClip.getMicrosecondLength() == currentClip.getMicrosecondPosition())
			setPosition(0);
		currentClip.start();
		isPlaying = true;
	}
	
	/** Pause the current music. If no music is playing does nothing*/
	public void pause() {
		System.out.println("PAUSE!");
		currentClip.stop();
		isPlaying = false;
	}
	
	/** Close the current music stream. If no music is loaded does nothing*/
	public void close() {
		currentClip.stop();
		currentClip.close();
		isPlaying = false;
	}
	
	public void goToEnd() {
		currentClip.stop();
		setPosition(currentClip.getMicrosecondLength());
		isPlaying = false;
	}
	
	public void goToStart() {
		setPosition(0);
	}
	
	public void reload() {
		pause();
		setPosition(0);
	}
	
	public void setPosition(long position) {
		currentClip.setMicrosecondPosition(position);
	}
	
	/** @return true if the audio file is currently playing, false otherwise */
	public boolean isPlaying() {
		return isPlaying;
	}
	
	public void setRepeat(boolean isRepeating) {
		this.isRepeating = isRepeating;
		if(isRepeating) {
			loopListener = addOnEndEvent( () -> {reload();play();} );
		}else {
			removeOnEndEvent(loopListener);
		}
	}
	
	public boolean isRepeating() {
		return isRepeating;
	}
	
	public AudioFile getCurrentAudioFile() {
		return audioFile;
	}
	
	/**
	 * Select the output audio peripheral with the given index
	 * @param index the index off the output peripheral to select
	 * @throws IllegalArgumentException if index is negative or greater than the number of available output peripherals
	 * @throws IllegalStateException is the player has a music opened
	 */
	public void selectMixer(final int index) {
		if(currentClip != null) {
			if(currentClip.isOpen()) {
				throw new IllegalStateException("The player has a music loaded : cannot change the output");
			}
			close();
		}
		currentClip = MixersUtilities.getClipByMixerIndex(index);
	}
	
	/** @return The duration of the current music in seconds */
	public double getDuration() {
		return currentClip.getFrameLength() / currentClip.getFormat().getFrameRate();
	}
	
	/** @return The elapsed time of the current music in seconds */
	public double getElapsedTime() {
		return currentClip.getFramePosition() / currentClip.getFormat().getFrameRate();
	}
	
	/** @return The remaining the of the current music in seconds */
	public double getRemainingTime() {
		return getDuration()-getElapsedTime();
	}
	
	/**
	 * Call the given runnable when the player reach the end of the current music and the music is not repeating
	 * @param runnable The runnable to run when the event is triggered
	 */
	public LineListener addOnFinishEvent(Runnable runnable) {
		LineListener listener = new LineListener() {
	        public void update(LineEvent event) {
	            if (event.getType() == LineEvent.Type.STOP && getRemainingTime() == 0.0) {
	            	if(!isRepeating) 
	            		runnable.run();
	            }   
	    }};
		currentClip.addLineListener(listener);
		return listener;
	}
	
	public LineListener addOnEndEvent(Runnable runnable) {
		LineListener listener = new LineListener() {
	        public void update(LineEvent event) {
	            if (event.getType() == LineEvent.Type.STOP && getRemainingTime() == 0.0) { 
	            	runnable.run();
	            }   
	    }};
		currentClip.addLineListener(listener);
		return listener;
	}
	
	public void removeOnEndEvent(LineListener listener) {
		currentClip.removeLineListener(listener);
	}
	
	/**
	 * Call the given runnable when the player loads a new music
	 * @param runnable The runnable to run when the event is triggered
	 */
	public void addOnLoadEvent(Runnable runnable) {
		LineListener listener = new LineListener() {
	        public void update(LineEvent event) {
	            if (event.getType() == LineEvent.Type.OPEN)
	                runnable.run();
	    }};
		currentClip.addLineListener(listener);
	}
	
	/**
	 * Call the given runnable when the player starts to play the loaded music
	 * @param runnable The runnable to run when the event is triggered
	 */
	public void addOnPlayEvent(Runnable runnable) {
		LineListener listener = new LineListener() {
	        public void update(LineEvent event) {
	            if (event.getType() == LineEvent.Type.START)
	                runnable.run();
	    }};
		currentClip.addLineListener(listener);
	}
}
