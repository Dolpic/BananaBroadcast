package ch.frequenceBanane.bananaBroadcast.database;

/**
 * Container class to hold AudioFile data such as the title, the duration or the file path
 * @author Corentin Junod
 * @author corentin.junod@epfl.ch
 */
public abstract class AudioFile {
	
	//Those fields are public, as this class is only a container
	public int id;
	public String title;
	public int duration;
	public int startTime;
	public int endTime;
	public String path;
	
	public String[] categories;
	
	/**
	 * Create a new Audio File with the given parameters
	 * @param id the unique ID of the file
	 * @param title the title
	 * @param duration the duration in seconds
	 * @param startTime the time at which the audio section to play starts
	 * @param endTime the time at which the audio section to play stops
	 * @param path the path to the audio file
	 */
	public AudioFile(final int id,        final String title, final int    duration, 
			         final int startTime, final int endTime,  final String path, final String[] categories) {
		this.id         = id;
		this.title      = title;
		this.duration   = duration;
		this.startTime  = startTime;
		this.endTime    = endTime;
		this.path       = path;
		this.categories = categories;
	}
	
	/**
	 * Creates a copy of an AudioFile
	 * @param audio the AudioFile to copy
	 */
	public AudioFile(final AudioFile audio) {
		this.id        = audio.id;
		this.title     = audio.title;
		this.duration  = audio.duration;
		this.startTime = audio.startTime;
		this.endTime   = audio.endTime;
		this.path      = audio.path;
	}
}
