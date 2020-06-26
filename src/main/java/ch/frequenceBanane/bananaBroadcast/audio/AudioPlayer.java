package ch.frequenceBanane.bananaBroadcast.audio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Function;

import javax.sound.sampled.*;

import ch.frequenceBanane.bananaBroadcast.database.*;
import ch.frequenceBanane.bananaBroadcast.utils.AudioUtils;
import ch.frequenceBanane.bananaBroadcast.utils.Log;
import ch.frequenceBanane.bananaBroadcast.utils.MixersUtilities;

/**
 * Enable to load, play, pause and close an audio file over a selected audio
 * peripheral
 * 
 * @author Corentin Junod
 * @author corentin.junod@epfl.ch
 */

public class AudioPlayer {

	private Clip currentClip;
	private int loopListenerID;
	private AudioFile audioFile;

	private boolean isPlaying = false;
	private boolean isRepeating = false;

	private ArrayList<Runnable> onPlayRunnables = new ArrayList<Runnable>();
	private ArrayList<Runnable> onFinishRunnables = new ArrayList<Runnable>();
	private ArrayList<Runnable> onEndRunnables = new ArrayList<Runnable>();
	private ArrayList<Runnable> onLoadRunnables = new ArrayList<Runnable>();

	/** Create a new AudioPlayer with the first peripheral of the system selected */
	public AudioPlayer() {
		selectMixer(0);

		createListenerOnCondition(onFinishRunnables, (event) -> {
			return event.getType() == LineEvent.Type.STOP && getRemainingTime() <= 0.0 && !isRepeating;
		});
		createListenerOnCondition(onEndRunnables, (event) -> {
			return event.getType() == LineEvent.Type.STOP && getRemainingTime() <= 0.0;
		});
		createListenerOnCondition(onLoadRunnables, (event) -> {
			return event.getType() == LineEvent.Type.OPEN;
		});
		createListenerOnCondition(onPlayRunnables, (event) -> {
			return event.getType() == LineEvent.Type.START;
		});
	}

	/**
	 * Load a music in memory, then the AudioPlayer is ready to play
	 * 
	 * @param music the music to load
	 * @return true if the current music was successfully loaded, false if an error
	 *         occurred
	 * @throws IllegalArgumentException if the music is null, or its path is empty
	 */
	public boolean load(final AudioFile file) {
		if (file == null || file.path.equals(""))
			throw new IllegalArgumentException("Music parameter is null or the path is invalid");

		audioFile = file;
		AudioInputStream inputStream;
		// TODO Would it be better to throw exceptions ?
		try {
			inputStream = AudioUtils.getAudioFileStream(currentClip.getFormat(), file.path);
		} catch (Exception e1) {
			Log.error("Exception during audio file decoding : " + e1.getMessage());
			return false;
		}

		try {
			close();
			currentClip.open(inputStream);
			return true;
		} catch (LineUnavailableException e) {
			Log.error("Unavailable output peripheral");
		} catch (IllegalStateException e) {
			Log.error("The player can't be stopped");
		} catch (IOException e) {
			Log.error("Problem duraing file input / output");
		} catch (NullPointerException e) {
			Log.error("AudioPlayer missing file " + file.path);
		}
		return false;
	}

	/** Start the current loaded music. If no music is loaded, does nothing */
	public void play() {
		if (currentClip.getMicrosecondLength() == currentClip.getMicrosecondPosition())
			setPosition(0);
		currentClip.start();
		isPlaying = true;
	}

	/** Pause the current music. If no music is playing does nothing */
	public void pause() {
		currentClip.stop();
		isPlaying = false;
	}

	/** Close the current music stream. If no music is loaded does nothing */
	public void close() {
		currentClip.stop();
		currentClip.close();
		isPlaying = false;
	}

	/** Put the cursor at the end of the audio file and stop it */
	public void goToEnd() {
		currentClip.stop();
		setPosition(currentClip.getMicrosecondLength() - 1000);
		isPlaying = false;
	}

	/** Put the cursor at the beginning of the audio file */
	public void goToStart() {
		setPosition(0);
	}

	/** Put the cursor at the beginning of the audio file and stop it */
	public void reload() {
		pause();
		setPosition(0);
	}

	public void setPosition(final long position) {
		currentClip.setMicrosecondPosition(position);
	}

	/** @return true if the audio file is currently playing, false otherwise */
	public boolean isPlaying() {
		return isPlaying;
	}

	/**
	 * Set if the current player must repeat the audio when the end is reached
	 * 
	 * @param isRepeating if true, the audio will loop when the end is reached
	 */
	public void setRepeat(final boolean isRepeating) {
		this.isRepeating = isRepeating;
		if (isRepeating) {
			loopListenerID = addOnEndEvent(() -> goToStart());
		} else {
			removeOnEndEvent(loopListenerID);
		}
	}

	/**
	 * @return true if the audio is set to be repeated when the end is reached,
	 *         false otherwise
	 */
	public boolean isRepeating() {
		return isRepeating;
	}

	/** @return the current AudioFile used by the player */
	public AudioFile getCurrentAudioFile() {
		return audioFile;
	}

	/**
	 * Select the output audio peripheral with the given index
	 * 
	 * @param index the index off the output peripheral to select
	 * @throws IllegalArgumentException if index is negative or greater than the
	 *                                  number of available output peripherals
	 * @throws IllegalStateException    is the player has a music opened
	 */
	public void selectMixer(final int index) {
		if (currentClip != null) {
			if (currentClip.isOpen()) {
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
		return getDuration() - getElapsedTime();
	}

	/**
	 * Call the given runnable when the player reach the end of the current music
	 * and the music is not repeating
	 * 
	 * @param runnable The runnable to run when the event is triggered
	 * @throws IllegalArgumentException if the given runnable is null
	 */
	public int addOnFinishEvent(final Runnable runnable) {
		if (runnable == null)
			throw new IllegalArgumentException("Given paramter is null");
		onFinishRunnables.add(runnable);
		return onFinishRunnables.indexOf(runnable);
	}

	/**
	 * Call the given runnable when the player reach the end of the current music,
	 * even if it is set to be repeated
	 * 
	 * @param runnable The runnable to run when the event is triggered
	 * @throws IllegalArgumentException if the given runnable is null
	 */
	public int addOnEndEvent(final Runnable runnable) {
		if (runnable == null)
			throw new IllegalArgumentException("Given paramter is null");
		onEndRunnables.add(runnable);
		return onEndRunnables.indexOf(runnable);
	}

	/**
	 * Disable a listener returned by the function addOnEndEvent
	 * 
	 * @param The listener to disable
	 */
	public void removeOnEndEvent(final int eventID) {
		onEndRunnables.remove(eventID);
	}

	/**
	 * Call the given runnable when the player loads a new music
	 * 
	 * @param runnable The runnable to run when the event is triggered
	 * @throws IllegalArgumentException if the given runnable is null
	 */
	public int addOnLoadEvent(final Runnable runnable) {
		if (runnable == null)
			throw new IllegalArgumentException("Given paramter is null");
		onLoadRunnables.add(runnable);
		return onLoadRunnables.indexOf(runnable);
	}

	/**
	 * Call the given runnable when the player starts to play the loaded music
	 * 
	 * @param runnable The runnable to run when the event is triggered
	 * @throws IllegalArgumentException if the given runnable is null
	 */
	public int addOnPlayEvent(final Runnable runnable) {
		if (runnable == null)
			throw new IllegalArgumentException("Given paramter is null");
		onPlayRunnables.add(runnable);
		return onPlayRunnables.indexOf(runnable);
	}

	private LineListener createListenerOnCondition(final ArrayList<Runnable> runnables,
			final Function<LineEvent, Boolean> condition) {
		LineListener listener = new LineListener() {
			public void update(LineEvent event) {
				if (condition.apply(event)) {
					for (Runnable runnable : runnables) {
						runnable.run();
					}
				}
			}
		};
		currentClip.addLineListener(listener);
		return listener;
	}
}
