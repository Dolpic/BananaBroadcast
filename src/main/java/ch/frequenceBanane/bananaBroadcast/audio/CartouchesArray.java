package ch.frequenceBanane.bananaBroadcast.audio;

import java.sql.SQLException;
import java.util.ArrayList;

import ch.frequenceBanane.bananaBroadcast.database.*;

public class CartouchesArray {

	private ArrayList<AudioPlayer> array = new ArrayList<>();

	public CartouchesArray(MusicDatabase database, String panel) throws SQLException {
		ArrayList<AudioFile> cartouches = database.getCartouches(panel);
		for (int i = 0; i < cartouches.size(); i++) {
			AudioPlayer ap = new AudioPlayer();
			ap.load(cartouches.get(i));
			array.add(ap);
		}
	}

	public int size() {
		return array.size();
	}

	public AudioPlayer get(int index) {
		return array.get(index);
	}
}
