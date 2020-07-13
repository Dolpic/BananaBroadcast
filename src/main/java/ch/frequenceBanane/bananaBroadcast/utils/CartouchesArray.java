package ch.frequenceBanane.bananaBroadcast.utils;

import java.sql.SQLException;
import java.util.ArrayList;

import ch.frequenceBanane.bananaBroadcast.audio.AudioPlayer;
import ch.frequenceBanane.bananaBroadcast.database.*;

public class CartouchesArray {

	private ArrayList<AudioPlayer> array = new ArrayList<>();

	public CartouchesArray(MusicDatabase database, String panel) throws SQLException {
		ArrayList<AudioFile> audioJingles = database.getCartouches(panel);
		for (int i = 0; i < audioJingles.size(); i++) {
			AudioPlayer ap = new AudioPlayer();
			ap.load(audioJingles.get(i));
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
