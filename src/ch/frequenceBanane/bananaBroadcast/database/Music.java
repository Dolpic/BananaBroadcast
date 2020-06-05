package ch.frequenceBanane.bananaBroadcast.database;

/**
 * Class representing a music with its metadata and sound file path
 * @author Corentin
 * @author corentin.junod@epfl.ch
 */
public class Music extends AudioFile{
	
	public String artist;
	public String album;
	public String genre;
	public String[] categories;
	
	/**
	 * Create a new Music with the provided properties
	 * @param id
	 * @param title
	 * @param artist
	 * @param album
	 * @param genre
	 * @param categories
	 * @param duration
	 * @param startTime
	 * @param endTime
	 * @param path
	 */
	public Music(final int    id,       final String title,     final String   artist, 
				 final String album,    final String genre,     final String[] categories,
				 final int    duration, final int    startTime, final int      endTime, 
				 final String path) {
		super(id, title, duration, startTime, endTime, path);
		this.artist     = artist;
		this.album      = album;
		this.genre      = genre;
		this.categories = categories;
	}
	
	/**
	 * Create a copy of the provided music
	 * @param music The Music to copy into a new Music
	 */
	public Music(final Music music) {
		super(music.id, music.title, music.duration, music.startTime, music.endTime, music.path);
		this.artist     = music.artist;
		this.album      = music.album;
		this.genre      = music.genre;
		this.categories = music.categories;
	}
}
