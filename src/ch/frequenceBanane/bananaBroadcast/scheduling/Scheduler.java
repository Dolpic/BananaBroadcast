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

/**
 * The Scheduler is able to create a list of musics from a database and
 * retrieve the musics from it
 * @author Corentin
 * @author corentin.junod@epfl.ch
 */
public class Scheduler {
	
	private MusicDatabase database;
	private ArrayList<String> categories;
	
	private final int DEFAULT_FILL_HOURS = 2;
	private final int DEFAULT_NUMBER_OF_MUSIC_PER_HOUR = 20;
	
	/**
	 * Create a new Scheduler linked with a database
	 * @param database The database
	 */
	public Scheduler(final MusicDatabase database) {
		this.database = database;
		try {
			categories = database.getCategories(Kind.MUSIC);
		} catch (SQLException e) {
			categories = null;
			Log.error("Unable to retrieve the catgegories from the database : "+e.getMessage());
		}
	}
	
	/**
	 * Return a specified number of next musics in the scheduler
	 * @param nbMusic the number of musics to retrieve
	 * @return A list of nbMusic next musics in the scheduler, or null if an error occurs
	 */
	public List<Music> getNextMusics(final int nbMusic) {
		return getNextMusicsRecursive(nbMusic, true);
	}
	
	private List<Music> getNextMusicsRecursive(final int nbMusic, final boolean retry) {
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
	
	/**
	 * Retrieve the planning of an hour of a particular day
	 * @param date The day from which the planning must be retrieved
	 * @param hour The hour from which the planning must be retrieved
	 * @return A list of categories corresponding to the planning of the given date + hour
	 */
	public ArrayList<String> getPlanningOf(final LocalDate date, final int hour){
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
	
	/**
	 * Retrieve the default planning of an hour of a particular day
	 * @param date The day from which the planning must be retrieved
	 * @param hour The hour from which the planning must be retrieved
	 * @return A list of categories corresponding to the planning of the given date + hour
	 */
	public ArrayList<String> getDefaultPlanningOf(final int day, final int hour){
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
	
	/**
	 * Set the categories associated to a specific date and hour in the planning
	 * @param date The day to which the planning must be set
	 * @param hour The hour to which the planning must be set
	 * @param categories The list of categories 
	 */
	public void setPlanningOf(LocalDate date, int hour, ArrayList<String> categories) {
		try {
			database.setPlanningOfDay(date, hour, categories);
		} catch (SQLException e) {
			Log.error("Database error : Unable to set the planning : "+e.getMessage());
		}
	}
	
	/**
	 * Set the categories associated to a specific date and hour in the default planning
	 * @param date The day to which the planning must be set
	 * @param hour The hour to which the planning must be set
	 * @param categories The list of categories 
	 */
	public void setDefaultPlanningOf(int day, int hour, ArrayList<String> categories) {
		try {
			database.setDefaultPlanningOfDay(day, hour, categories);
		} catch (SQLException e) {
			Log.error("Database error : Unable to set the default planning : "+e.getMessage());
		}
	}
	
	/**
	 * Fill the scheduler until the next "hoursToFill" hours are filled
	 * @param hoursToFill The number of next hours to fill in the Scheduler
	 */
	public void fillScheduler(int hoursToFill) {
		LocalDateTime date = LocalDateTime.now();
	
		try {
			for(;hoursToFill > 0; hoursToFill--) {
				final LocalDate curDate = date.toLocalDate();
				final int curHours      = date.getHour();
				ArrayList<String> planning = getPlanningOf(curDate, curHours);
				
					if(database.getScheduledAt(curDate, curHours).isEmpty()) {
						
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
				date = date.plusHours(1);
			}
		} catch (SQLException e) {
			Log.error("Database error : Unable to fill the scheduler : "+e.getMessage());
		}
	}
	
	/**@return All the music categories in the database*/
	public ArrayList<String> getCategories() {
		return new ArrayList<String>(categories);
	}

}
