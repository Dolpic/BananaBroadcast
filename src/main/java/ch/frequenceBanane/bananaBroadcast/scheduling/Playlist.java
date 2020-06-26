package ch.frequenceBanane.bananaBroadcast.scheduling;

import java.util.ArrayList;
import java.util.List;

import ch.frequenceBanane.bananaBroadcast.database.AudioFile;

/**
 * Wrapper around a List to handle the music files queued to be played
 * 
 * @author Corentin Junod
 * @author corentin.junod@epfl.ch
 */
public class Playlist<AudioType extends AudioFile> {

	final private ArrayList<AudioType> audios;
	private Runnable onChangeRunnable = null;

	/** Create a new Playlist **/
	public Playlist() {
		audios = new ArrayList<AudioType>();
	}

	/**
	 * Return the next AudioFile in the list
	 * 
	 * @return The next AudioFile in the list, or null if the list is empty
	 */
	public AudioType getNext() {
		if (!audios.isEmpty()) {
			triggerOnChange();
			return audios.remove(0);
		} else {
			return null;
		}
	}

	/**
	 * Add a new AudioFile at the end of the list
	 * 
	 * @param audio The AudioFile to add
	 * @throws IllegalArgumentException if the parameter is null
	 */
	public void addAtEnd(final AudioType audio) {
		if (audio == null)
			throw new IllegalArgumentException();
		audios.add(audio);
		triggerOnChange();
	}

	/**
	 * Add every AudioFile in a list to the end of the playlist
	 * 
	 * @param audio The list of AudioFile to add
	 * @throws IllegalArgumentException if audio is null
	 */
	public void addAtEnd(final List<? extends AudioType> audio) {
		if (audio == null)
			throw new IllegalArgumentException("Given parameter is empty");
		else
			audio.forEach((elem) -> addAtEnd(elem));
	}

	/** Remove all AudioFile in the list */
	public void removeAll() {
		while (!audios.isEmpty())
			audios.remove(0);
	}

	/**
	 * Set a runnable to be called every time the list changes
	 * 
	 * @param runnable to runnable to run when the list changes
	 * @throws IllegalArgumentException if the given runnable is null
	 */
	public void setOnChange(final Runnable runnable) {
		if (runnable == null)
			throw new IllegalArgumentException("Given parameter is null");
		onChangeRunnable = runnable;
	}

	/**
	 * Return a copy of the current playlist
	 * 
	 * @return a copy of the current playlist
	 */
	public List<AudioType> getList() {
		return new ArrayList<AudioType>(audios);
	}

	private void triggerOnChange() {
		if (onChangeRunnable != null)
			onChangeRunnable.run();
	}

}
