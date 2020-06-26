package ch.frequenceBanane.bananaBroadcast.utils;

import java.sql.SQLException;
import java.util.ArrayList;

import ch.frequenceBanane.bananaBroadcast.audio.JinglePlayer;
import ch.frequenceBanane.bananaBroadcast.database.*;

public class CartouchesArray {

	private ArrayList<JinglePlayer> array = new ArrayList<>();

	public CartouchesArray(MusicDatabase database, String panel) throws SQLException {
		ArrayList<Jingle> audioJingles = database.getCartouches(panel);
		for (int i = 0; i < audioJingles.size(); i++) {
			JinglePlayer ap = new JinglePlayer();
			ap.load(audioJingles.get(i));
			array.add(ap);
		}
	}

	public int size() {
		return array.size();
	}

	public JinglePlayer get(int index) {
		return array.get(index);
	}
}
