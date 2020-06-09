package ch.frequenceBanane.bananaBroadcast.database;

/**
 * Container to hold the data of a Jingle
 * @author Corentin Junod
 * @author corentin.junod@epfl.ch
 */
public class Jingle extends AudioFile {

	public String[] panels;
	
	/**
	 * Create a new holder for Jingle data
	 * @param id 
	 * @param title
	 * @param panels
	 * @param categories
	 * @param duration
	 * @param startTime
	 * @param endTime
	 * @param path
	 */
	public Jingle(final int id,              final String title, final String[] panels, 
			      final String[] categories, final int duration, final int startTime, 
			      final int endTime,         final String path) {
		
		super(id, title, duration, startTime, endTime, path, categories);
		this.panels = panels;
	}
	
	/**
	 * Create a copy of a given Jingle 
	 * @param jingle the Jingle to copy
	 */
	public Jingle(final Jingle jingle) {
		super(jingle);
		this.panels = jingle.panels;
	}
}
