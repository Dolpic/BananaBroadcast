package ch.frequenceBanane.bananaBroadcast.scheduling;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import ch.frequenceBanane.bananaBroadcast.database.*;
import ch.frequenceBanane.bananaBroadcast.database.MusicDatabase.Kind;

public class Scheduler {
	
	private MusicDatabase database;
	private ArrayList<String> categories;
	
	private final int DEFAULT_FILL_HOURS = 2;
	
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
		String[] planning = database.getPlanningOfDay(date).get(hour);
		if(planning == null)
			return new ArrayList<String>();
		else
			return new ArrayList<String>(Arrays.asList(planning));
	}
	
	public ArrayList<String> getDefaultPlanningOf(int day, int hour){
		String[] planning = database.getDefaultPlanningOfDay(day).get(hour);
		if(planning == null)
			return new ArrayList<String>();
		else
			return new ArrayList<String>(Arrays.asList(planning));
	}
	
	public void setPlanningOf(LocalDate date, int hour, ArrayList<String> categories) {
		database.setPlanningOfDay(date, hour, categories);
	}
	
	public void setDefaultPlanningOf(int day, int hour, ArrayList<String> categories) {
		database.setDefaultPlanningOfDay(day, hour, categories);
	}
	
	public void fillScheduler(int hoursToFill) {
		LocalDateTime date = LocalDateTime.now();
		
		while(hoursToFill > 0) {
			LocalDate curDate = date.toLocalDate();
			int curHours = date.getHour();
			if(database.getScheduledAt(curDate, curHours).isEmpty()) {
				ArrayList<String> planning = getPlanningOf(curDate, curHours);
				
				if(planning.isEmpty()) {
					planning = getDefaultPlanningOf(curDate.getDayOfWeek().getValue()-1, curHours);
				}
				
				database.getRandomMusicFromCategorie(planning, 20).forEach(  //TODO changer le nombre 20 qui est magique
				    music -> database.addScheduledAt(music, curDate, curHours)
				);
			}
			
			date = date.plusHours(1);
			hoursToFill--;
		}
	}
	
	public ArrayList<String> getCategories() {
		return categories;
	}

}
