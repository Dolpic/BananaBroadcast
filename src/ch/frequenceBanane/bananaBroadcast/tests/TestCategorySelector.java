package ch.frequenceBanane.bananaBroadcast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.sql.SQLException;


import org.junit.jupiter.api.Test;

import ch.frequenceBanane.bananaBroadcast.categories.CategorySelector;
import ch.frequenceBanane.bananaBroadcast.database.MusicDatabase;

public class TestCategorySelector {
	
	private final String databaseUrl      = "jdbc:mysql://localhost:3307/bananabroadcast";
	private final String databaseUser     = "root";
	private final String databasePassword = "usbw";
	
	private MusicDatabase database = new MusicDatabase(databaseUrl, databaseUser, databasePassword);
	
	@Test
	public void GetWaveFormThrowsExpectedExceptions() throws IOException, SQLException {
		CategorySelector catSel = new CategorySelector(database);
		assertEquals(false, catSel.isSelectedCategory("test category"));
		catSel.addSelectedCategory("test category");
		assertEquals(true, catSel.isSelectedCategory("test category"));
		catSel.removeSelectedCategory("test category");
		assertEquals(false, catSel.isSelectedCategory("test category"));
	}
	
	@Test
	public void ExceptionsAreThrowsWhenNeeded() throws IOException, SQLException {
		CategorySelector catSel = new CategorySelector(database);
		assertThrows(IllegalArgumentException.class, ()->catSel.addSelectedCategory(null));
		assertThrows(IllegalArgumentException.class, ()->catSel.removeSelectedCategory(null));
		assertThrows(IllegalArgumentException.class, ()->catSel.isSelectedCategory(null));
		assertThrows(IllegalArgumentException.class, ()->catSel.setOnSelectedCategoriesChange(null));
	}
}
