package ch.frequenceBanane.bananaBroadcast.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Container class to hold AudioFile data such as the title, the duration or the
 * file path
 * 
 * @author Corentin Junod
 * @author corentin.junod@epfl.ch
 */
public class AudioFile {

	// Those fields are public, as this class is only a container
	public int id;
	public int duration;
	public int startTime;
	public int endTime;
	
	public String title;
	public String artist;
	public String album;
	public String genre;
	public String path;

	public String[] categories;
	public String[] panels;

	/**
	 * Create a new Audio File
	 */
	public AudioFile() {
		id        = 0;
		duration  = 0;
		startTime = 0;
		endTime   = 0;
		
		title  = "";
		artist = "";
		album  = "";
		genre  = "";
		path   = "";
		
		categories = null;
		panels     = null;
	}
	
	public static ArrayList<AudioFile> getFromQueryResult(ResultSet queryResults) throws SQLException {
		ArrayList<AudioFile> result = new ArrayList<AudioFile>();
		while (queryResults.next()) {
			AudioFile curData = new AudioFile();
			
			int ID = queryResults.getInt("ID");
			
			curData.id        = ID;
			curData.duration  = queryResults.getInt("duration");
			curData.startTime = queryResults.getInt("startTime");
			curData.endTime   = queryResults.getInt("endTime");
			
			curData.title     = getStringOrDefault(queryResults, "title",  "-");
			curData.artist    = getStringOrDefault(queryResults, "artist", "-");
			curData.album     = getStringOrDefault(queryResults, "album",  "-");
			curData.genre     = getStringOrDefault(queryResults, "genre",  "-");
			curData.path      = getStringOrDefault(queryResults, "path",   "-");
			
			curData.categories = MusicDatabase.getCategoriesFromID(ID, "musics_categories");
			
			result.add(curData);
		}
		return result;
	}
	
	private static String getStringOrDefault(ResultSet queryResults, String name, String defaultValue) {
		try {
			return queryResults.getString(name);
		}catch(SQLException e) {
			return defaultValue;
		}
	}
}
