package ch.frequenceBanane.bananaBroadcast.categories;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

import ch.frequenceBanane.bananaBroadcast.database.MusicDatabase;

public class CategorySelector {
	
	public final ArrayList<String> musicsCategories;
	public final ArrayList<String> jinglesCategories;
	public final ArrayList<String> tapisCategories;
	public final ArrayList<String> cartouchesCategories;
	
	private final ArrayList<String> selected;
	private Consumer<ArrayList<String>> toRunOnChange;
	
	public CategorySelector(MusicDatabase database) throws IOException, SQLException{
		musicsCategories     = database.getCategories(MusicDatabase.Kind.MUSIC);
		jinglesCategories    = database.getCategories(MusicDatabase.Kind.JINGLE);
		tapisCategories      = database.getCategories(MusicDatabase.Kind.TAPIS);
		cartouchesCategories = database.getCategories(MusicDatabase.Kind.CARTOUCHE);
		selected = new ArrayList<String>();
	}
	
	public void addSelectedCategory(String category) {
		if(!selected.contains(category)) {
			selected.add(category);
			toRunOnChange.accept(selected);
		}
	}
	
	public void removeSelectedCategory(String category) {
		selected.remove(category);
		toRunOnChange.accept(selected);
	}
	
	public boolean isSelectedCategory(String category) {
		return selected.contains(category);
	}
	
	public void setOnSelectedCategoriesChange(Consumer<ArrayList<String>> func) {
		toRunOnChange = func;
	}
}
