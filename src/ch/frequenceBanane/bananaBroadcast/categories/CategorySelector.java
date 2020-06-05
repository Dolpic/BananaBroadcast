package ch.frequenceBanane.bananaBroadcast.categories;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.function.Consumer;

import ch.frequenceBanane.bananaBroadcast.database.MusicDatabase;

/**
 * Wrapper around a ArrayList to hold to current selected categories to display in categories' list
 * @author Corentin Junod
 * @author corentin.junod@epfl.ch
 */
public class CategorySelector {
	
	public final ArrayList<String> musicsCategories;
	public final ArrayList<String> jinglesCategories;
	public final ArrayList<String> tapisCategories;
	public final ArrayList<String> cartouchesCategories;
	
	private final ArrayList<String> selected;
	private Consumer<ArrayList<String>> toRunOnChange;
	
	/**
	 * Create a new CategorySelector
	 * @param database The database to retrieve the categories
	 * @throws IOException If an error occurs during database transactions 
	 * @throws SQLException If an error occurs during database transactions 
	 */
	public CategorySelector(final MusicDatabase database) throws SQLException{
		musicsCategories     = database.getCategories(MusicDatabase.Kind.MUSIC);
		jinglesCategories    = database.getCategories(MusicDatabase.Kind.JINGLE);
		tapisCategories      = database.getCategories(MusicDatabase.Kind.TAPIS);
		cartouchesCategories = database.getCategories(MusicDatabase.Kind.CARTOUCHE);
		selected = new ArrayList<String>();
	}
	
	/**
	 * Add a category to the selected ones
	 * @param category The category to add
	 */
	public void addSelectedCategory(final String category) {
		throwExceptionIfStringNull(category);
		
		if(!selected.contains(category)) {
			selected.add(category);
			if(toRunOnChange != null) 
				toRunOnChange.accept(selected);
		}
	}
	
	/**
	 * Remove a category to the selected ones
	 * @param category The category to remove
	 */
	public void removeSelectedCategory(final String category) {
		throwExceptionIfStringNull(category);
		selected.remove(category);
		if(toRunOnChange != null) 
			toRunOnChange.accept(selected);
	}
	
	/**
	 * Indicates if a category is currently selected
	 * @param category The category to test
	 * @return true if the category is currently selected, false otherwise
	 */
	public boolean isSelectedCategory(final String category) {
		throwExceptionIfStringNull(category);
		return selected.contains(category);
	}
	
	/**
	 * Set a consumer called when the list of categories changes
	 * @param func the Consumer to call
	 */
	public void setOnSelectedCategoriesChange(final Consumer<ArrayList<String>> func) {
		if(func == null) throw new IllegalArgumentException("given consumer is null");
		toRunOnChange = func;
	}
	
	private void throwExceptionIfStringNull(final String str) {
		if(str == null) throw new IllegalArgumentException("given category is null");
	}
}
