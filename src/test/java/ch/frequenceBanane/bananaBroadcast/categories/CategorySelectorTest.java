package ch.frequenceBanane.bananaBroadcast.categories;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import ch.frequenceBanane.bananaBroadcast.database.Music;
import ch.frequenceBanane.bananaBroadcast.scheduling.Playlist;

public class CategorySelectorTest {
	
	@Test
	public void NormalUtilisationHasExpectedResult(){
    Playlist<Music> playlist = new Playlist<>();
    Music music = new Music(0, "","","", null, null, 0, 0, 0, null);
    assertEquals(0, playlist.getList().size());
    playlist.addAtEnd(music);
    assertEquals(1, playlist.getList().size());
    ArrayList<Music> mlist = new ArrayList<Music>();
    mlist.add(new Music(0, "","","", null, null, 0, 0, 0, null));
    mlist.add(new Music(0, "","","", null, null, 0, 0, 0, null));
    playlist.addAtEnd(mlist);
    assertEquals(3, playlist.getList().size());
    assertEquals(music, playlist.getNext());
    assertEquals(2, playlist.getList().size());
    playlist.removeAll();
    assertEquals(0, playlist.getList().size());
	}
	
	@Test
	public void ExceptionsAreThrownWhenNeeded() throws IOException, SQLException {
    Playlist<Music> playlist = new Playlist<>();
    assertThrows(IllegalArgumentException.class, ()->playlist.setOnChange(null));
	}
}
