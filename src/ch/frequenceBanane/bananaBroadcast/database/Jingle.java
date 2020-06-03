package ch.frequenceBanane.bananaBroadcast.database;

public class Jingle extends AudioFile {

	public String[] panels;
	public String[] categories;
	
	public Jingle(int id,              String title, String[] panels, 
			      String[] categories, int duration, int startTime, 
			      int endTime,         String path) {
		
		super(id, title, duration, startTime, endTime, path);
		this.panels     = panels;
		this.categories = categories;
	}
	
	public Jingle(Jingle jingle) {
		super(jingle);
		this.panels     = jingle.panels;
		this.categories = jingle.categories;
	}
}
