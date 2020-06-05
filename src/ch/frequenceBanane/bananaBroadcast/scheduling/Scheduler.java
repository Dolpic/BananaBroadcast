package ch.frequenceBanane.bananaBroadcast.scheduling;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.frequenceBanane.bananaBroadcast.database.*;
import ch.frequenceBanane.bananaBroadcast.database.MusicDatabase.Kind;
import ch.frequenceBanane.bananaBroadcast.utils.Log;

public class Scheduler {
	
	private MusicDatabase database;
	private ArrayList<String> categories;
	
	private final int DEFAULT_FILL_HOURS = 2;
	private final int DEFAULT_NUMBER_OF_MUSIC_PER_HOUR = 20;
	
	public Scheduler(MusicDatabase database) {
		this.database = database;
		try {
			categories = database.getCategories(Kind.MUSIC);
		} catch (SQLException e) {
			categories = null;
			e.printStackTrace();
		}
	}
	
	public List<Music> getNextMusics(int nbMusic) {
		return getNextMusicsRecursive(nbMusic, true);
	}
	
	private List<Music> getNextMusicsRecursive(int nbMusic, boolean retry) {
		List<Music> result;
		try {
			result = database.getNextScheduled();
		} catch (SQLException e) {
			return null;
		}
		
		if(result.isEmpty() && retry) {
			fillScheduler(DEFAULT_FILL_HOURS);
			return getNextMusicsRecursive(nbMusic, false);
		}

		return result.size() > nbMusic ? result.subList(0, nbMusic) : result;
	}
	
	public ArrayList<String> getPlanningOf(LocalDate date, int hour){
		String[] planning;
		try {
			planning = database.getPlanningOfDay(date).get(hour);
		} catch (SQLException e) {
			planning = null;
			Log.error("Could not retrieve the planning : "+e.getMessage());
		}
		if(planning == null)
			return new ArrayList<String>();
		else
			return new ArrayList<String>(Arrays.asList(planning));
	}
	
	public ArrayList<String> getDefaultPlanningOf(int day, int hour){
		String[] planning;
		try {
			planning = database.getDefaultPlanningOfDay(day).get(hour);
		} catch (SQLException e) {
			planning = null;
			Log.error("Could not retrieve the default planning : "+e.getMessage());
		}
		if(planning == null)
			return new ArrayList<String>();
		else
			return new ArrayList<String>(Arrays.asList(planning));
	}
	
	public void setPlanningOf(LocalDate date, int hour, ArrayList<String> categories) {
		try {
			database.setPlanningOfDay(date, hour, categories);
		} catch (SQLException e) {
			Log.error("Database error : Unable to set the planning : "+e.getMessage());
		}
	}
	
	public void setDefaultPlanningOf(int day, int hour, ArrayList<String> categories) {
		try {
			database.setDefaultPlanningOfDay(day, hour, categories);
		} catch (SQLException e) {
			Log.error("Database error : Unable to set the default planning : "+e.getMessage());
		}
	}
	
	public void fillScheduler(int hoursToFill) {
		LocalDateTime date = LocalDateTime.now();
		
		while(hoursToFill > 0) {
			LocalDate curDate = date.toLocalDate();
			int curHours = date.getHour();
			try {
				if(database.getScheduledAt(curDate, curHours).isEmpty()) {
					ArrayList<String> planning = getPlanningOf(curDate, curHours);
					
					if(planning.isEmpty()) {
						planning = getDefaultPlanningOf(curDate.getDayOfWeek().getValue()-1, curHours);
					}
					
					database.getRandomMusicFromCategorie(planning, DEFAULT_NUMBER_OF_MUSIC_PER_HOUR).forEach(
					    music -> {
							try {
								database.addScheduledAt(music, curDate, curHours);
							} catch (SQLException e) {
								Log.error("Database error : Unable to add a music to the scheduler : "+e.getMessage());
							}
						}
					);
				}
			} catch (SQLException e) {
				Log.error("Database error : Unable to fill the scheduler : "+e.getMessage());
			}
			
			date = date.plusHours(1);
			hoursToFill--;
		}
	}
	
	public ArrayList<String> getCategories() {
		return categories;
	}

}
