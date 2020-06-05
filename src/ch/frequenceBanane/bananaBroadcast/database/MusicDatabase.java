package ch.frequenceBanane.bananaBroadcast.database;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Create a connection with the specified database and provide useful functions to
 * retrieve data from it.
 * @author Corentin Junod
 * @author corentin.junod@epfl.ch
 */
public class MusicDatabase {
	
	public static enum Kind{MUSIC, JINGLE, TAPIS, CARTOUCHE};
	
	private Connection connection;
	
	/** Create a new connection with the database */
	public MusicDatabase(String url, String user, String password) throws SQLException{
		connection = DriverManager.getConnection(url+"?serverTimezone=UTC", user, password);
	}
		
	/** @return All musics in the database, null if an error occurs */
	public ArrayList<Music> getAllMusics() throws SQLException{
		String query = "SELECT * FROM musics WHERE 1";
		ResultSet queryResults = connection.createStatement().executeQuery(query);
		return getQueryResultMusics(queryResults);
	}
	
	/** @return All scheduled musics for the current hour of the current day, null if an error occurs */
	public ArrayList<Music> getNextScheduled() throws SQLException{
		String query = "SELECT * "
				     + "FROM musics m, scheduler s "
					 + "WHERE s.musicId = m.ID AND s.slot = HOUR(NOW()) AND s.day = CURRENT_DATE() ORDER BY playOrder";
		ResultSet queryResults = connection.createStatement().executeQuery(query);
		return getQueryResultMusics(queryResults);
	}
	
	public ArrayList<Music> getScheduledAt(LocalDate date, int hour) throws SQLException{
		ResultSet queryResults = null;
		DateTimeFormatter formater = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String query = "SELECT * "
			     + "FROM musics m, scheduler s "
				 + "WHERE s.musicId = m.ID AND s.slot = ? AND s.day = ? ORDER BY playOrder";
		
		PreparedStatement preparedStatement;
		preparedStatement = connection.prepareStatement(query);
		preparedStatement.setInt(1, hour); 
		preparedStatement.setString(2, formater.format(date)); 
		queryResults = preparedStatement.executeQuery();
		return getQueryResultMusics(queryResults);
	}
	
	public void addScheduledAt(Music music, LocalDate date, int hour) throws SQLException {
		int nextOrder = getScheduledAt(date, hour).size();
		
		DateTimeFormatter formater = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String query = "INSERT INTO scheduler(musicID, playOrder, slot, day) VALUES(?,?,?,?)";
		
		PreparedStatement preparedStatement;
		preparedStatement = connection.prepareStatement(query);
		preparedStatement.setInt(1, music.id); 
		preparedStatement.setInt(2, nextOrder); 
		preparedStatement.setInt(3, hour); 
		preparedStatement.setString(4, formater.format(date)); 
		preparedStatement.executeUpdate();
	}
	
	public ArrayList<Jingle> getCartouches(String panel) throws SQLException{
		String query = "SELECT * FROM cartouches WHERE panels LIKE ?";
		PreparedStatement preparedStatement = connection.prepareStatement(query); 
		preparedStatement.setString(1, "%"+panel+"%"); 
		ResultSet queryResults = preparedStatement.executeQuery();
		return getQueryResultCartouche(queryResults);
	}
	
	public ArrayList<String> getCategories(Kind kind) throws SQLException {
		String tableName = getTableNameFromKind(kind)+"_categories";
		String query = "SELECT DISTINCT categorie FROM "+tableName;
		ResultSet queryResults = connection.createStatement().executeQuery(query);
		ArrayList<String> result = new ArrayList<String>();
		while(queryResults.next()) {
			result.add(queryResults.getString("categorie"));
		}
		return result;
	}
	
	public ArrayList<AudioFile> getFromCategoriesAndKind(Kind kind, ArrayList<String> categories) throws Exception{
		
		String filter = "";
		if(categories == null || categories.size() == 0) {
			filter = "1";
		}else {
			for(int i=0; i<categories.size(); i++) {
				filter += "tnc.categorie = '"+categories.get(i)+"'";
				
				if(i<categories.size() -1) {
					filter += " OR ";
				}
			}
		}
		
		String query;
		String tableName = getTableNameFromKind(kind);
		String tableNameCategorie = tableName+"_categories";
		query = "SELECT * FROM "+tableName+" tn, "+tableNameCategorie+" tnc WHERE tn.ID = tnc.ID AND "+filter+" GROUP BY tn.ID";
		ResultSet queryResults = connection.createStatement().executeQuery(query);
		
		switch(kind) {
			case MUSIC :
				return new ArrayList<AudioFile>(getQueryResultMusics(queryResults));
			case CARTOUCHE :
				return new ArrayList<AudioFile>(getQueryResultCartouche(queryResults));
			case JINGLE :
				throw new Exception("Not implemented");
			case TAPIS :
				throw new Exception("Not implemented");
			default : return null;
		}
	}
	
	/*public void addSchedulerCategorie(Kind kind, String categorie, LocalDate date, int hour) {
		DateTimeFormatter formater = DateTimeFormatter.ofPattern("yyyy-mm-dd");
		String query = "INSERT IGNORE INTO scheduler_planning (date, categories) VALUES('?','?')";
		PreparedStatement preparedStatement;
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, formater.format(date)+" "+hour+":00:00"); 
			preparedStatement.setString(2, categorie); 
			preparedStatement.executeQuery();
		} catch (SQLException e) {
			// Auto-generated catch block
			e.printStackTrace();
		} 
	}*/
	
	public void removeSchedulerCategorie(Kind kind, String categorie, LocalDate date, int hour) throws SQLException {
		DateTimeFormatter formater = DateTimeFormatter.ofPattern("yyyy-mm-dd");
		String query = "DELETE FROM scheduler_planning WHERE date = ? AND categorie = ?";
		PreparedStatement preparedStatement;
		preparedStatement = connection.prepareStatement(query);
		preparedStatement.setString(1, formater.format(date)+" "+hour+":00:00"); 
		preparedStatement.setString(2, categorie); 
		preparedStatement.executeQuery();
	}
	
	public TreeMap<Integer, String[]> getPlanningOfDay(LocalDate date) throws SQLException{
		TreeMap<Integer, String[]> result = new TreeMap<Integer, String[]>();
		DateTimeFormatter formater = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		
		String query = "SELECT HOUR(date) as hour, categories FROM scheduler_planning WHERE CONVERT(date, date) = '"+formater.format(date)+"' ORDER BY hour";
		ResultSet queryResults = connection.createStatement().executeQuery(query);
		while(queryResults.next()) {
			result.put(queryResults.getInt("hour"), queryResults.getString("categories").split(";"));
		}
		return result;
	}
	
	public TreeMap<Integer, String[]> getDefaultPlanningOfDay(int day) throws SQLException{
		TreeMap<Integer, String[]> result = new TreeMap<Integer, String[]>();
		
		String query = "SELECT hour, categories FROM scheduler_default WHERE day = ? ORDER BY hour";
		PreparedStatement preparedStatement;
		preparedStatement = connection.prepareStatement(query);
		preparedStatement.setInt(1, day); 
		ResultSet queryResults = preparedStatement.executeQuery();
		while(queryResults.next()) {
			result.put(queryResults.getInt("hour"), queryResults.getString("categories").split(";"));
		}
		return result;
	}
	
	public void setPlanningOfDay(LocalDate date, int hour, ArrayList<String> categories) throws SQLException{
		String joined = "";
		DateTimeFormatter formater = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		
		for(int i=0; i<categories.size(); i++) {
			joined += categories.get(i);
			if(i < categories.size()-1) {
				joined += ";";
			}
		}
		String query = "INSERT INTO scheduler_planning (date, categories) VALUES(?, ?) ON DUPLICATE KEY UPDATE categories=?";

		PreparedStatement preparedStatement;
		preparedStatement = connection.prepareStatement(query);
		preparedStatement.setString(1, formater.format(date)+" "+hour+":00:00"); 
		preparedStatement.setString(2, joined); 
		preparedStatement.setString(3, joined);
		preparedStatement.executeUpdate();
	}
	
	public void setDefaultPlanningOfDay(int day, int hour, ArrayList<String> categories) throws SQLException{
		String joined = "";
		
		for(int i=0; i<categories.size(); i++) {
			joined += categories.get(i);
			if(i < categories.size()-1) {
				joined += ";";
			}
		}
		String query = "INSERT INTO scheduler_default (day, hour, categories) VALUES(?,?,?) ON DUPLICATE KEY UPDATE categories=?";

		PreparedStatement preparedStatement;
		preparedStatement = connection.prepareStatement(query);
		preparedStatement.setInt(1, day); 
		preparedStatement.setInt(2, hour);
		preparedStatement.setString(3, joined); 
		preparedStatement.setString(4, joined);
		preparedStatement.executeUpdate();
	}
	
	public ArrayList<Music> getRandomMusicFromCategorie(ArrayList<String> categories, int nbMusics) throws SQLException{
		if(categories.isEmpty())
			return new ArrayList<Music>();
		
		String toInsert = "(";
		for(int i=0; i<categories.size(); i++) {
			toInsert += "?";
			if(i < categories.size()-1)
				toInsert += ",";
		}
		toInsert += ")";
		
		String query = "SELECT * FROM musics m, musics_categories mc WHERE m.ID = mc.ID AND mc.categorie IN "+toInsert+" ORDER BY RAND() LIMIT ?";
		ResultSet queryResults = null;
		PreparedStatement preparedStatement;
		preparedStatement = connection.prepareStatement(query);
		for(int i=0; i<categories.size(); i++) {
			preparedStatement.setString(i+1, categories.get(i));
		}
		preparedStatement.setInt(categories.size()+1, nbMusics); 
		queryResults = preparedStatement.executeQuery();
		return getQueryResultMusics(queryResults);
	}
	
	//TODO : gérer les startTime / endTime et albumIndex qui est toujours à 0
	public void addNewMusic(Music music) throws SQLException {
		int nextID = getNextAutoIncrement("musics");
		
		String query = "INSERT INTO musics(title, artist, album, albumIndex, genre, duration, addTime, startTime, endTime, path)"
				       + "VALUES(?, ?, ?, ?, ?, ?, NOW(), 0, 0, ?)";
		
		String queryCategories = "INSERT INTO musics_categories(ID, categorie) VALUES(?, ?)";
				
		PreparedStatement preparedStatement;
		preparedStatement = connection.prepareStatement(query);
		preparedStatement.setString(1, music.title); 
		preparedStatement.setString(2, music.artist); 
		preparedStatement.setString(3, music.album); 
		preparedStatement.setInt(4, 0); 
		preparedStatement.setString(5, music.genre); 
		preparedStatement.setInt(6, music.duration); 
		preparedStatement.setString(7, music.path); 
		preparedStatement.executeUpdate();
		
		preparedStatement = connection.prepareStatement(queryCategories);
		preparedStatement.setInt(1, nextID);
		preparedStatement.setString(2, music.genre);
		preparedStatement.executeUpdate();
	}
	
	private ArrayList<Music> getQueryResultMusics(ResultSet queryResults) throws SQLException{
		ArrayList<Music> result = new ArrayList<Music>();
		while(queryResults.next()) {
			int ID = queryResults.getInt("ID");
			Music curData = new Music(ID,
									  queryResults.getString("title"),
									  queryResults.getString("artist"),
									  queryResults.getString("album"),
									  queryResults.getString("genre"),
									  getCategoriesFromID(ID, "musics_categories"),
									  queryResults.getInt("duration"),
									  queryResults.getInt("startTime"),
									  queryResults.getInt("endTime"),
									  queryResults.getString("path"));
			result.add(curData);
		}
		return result;
	}
	
	private ArrayList<Jingle> getQueryResultCartouche(ResultSet queryResults) throws SQLException{
		ArrayList<Jingle> result = new ArrayList<Jingle>();
		while(queryResults.next()) {
			int ID = queryResults.getInt("ID");
			Jingle curData = new Jingle(ID,
									  queryResults.getString("title"),
									  queryResults.getString("panels").split(";"),
									  getCategoriesFromID(ID, "cartouches_categories"),
									  queryResults.getInt("duration"),
									  queryResults.getInt("startTime"),
									  queryResults.getInt("endTime"),
									  queryResults.getString("path"));
			result.add(curData);
		}
		return result;
	}
	
	private String[] getCategoriesFromID(int ID, String tableName) throws SQLException {
		String query = "SELECT categorie FROM "+tableName+" WHERE ID = "+ID;
		ResultSet queryResults = connection.createStatement().executeQuery(query);
		ArrayList<String> tmp = new ArrayList<>();
		while(queryResults.next()) {
			tmp.add(queryResults.getString("categorie"));
		}
		
		String[] result = new String[tmp.size()];
		for(int i=0; i<tmp.size(); i++) {
			result[i] = tmp.get(i);
		}
		return result;
	}
	
	private String getTableNameFromKind(Kind kind) {
		switch (kind) {
			case MUSIC:     return "musics";
			case JINGLE:    return "jingles";
			case TAPIS:     return "tapis";
			case CARTOUCHE: return "cartouches";
			default: throw new IllegalArgumentException();
		}
	}
	
	private int getNextAutoIncrement(String table) throws SQLException{
		String query = "SELECT AUTO_INCREMENT FROM information_schema.tables WHERE table_name = ?";
		PreparedStatement preparedStatement;
		ResultSet queryResults;
		preparedStatement = connection.prepareStatement(query);
		preparedStatement.setString(1, table); 
		queryResults = preparedStatement.executeQuery();
		queryResults.next();
		return queryResults.getInt("AUTO_INCREMENT");
	}
}
