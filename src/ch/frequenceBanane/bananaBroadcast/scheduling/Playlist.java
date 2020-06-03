package ch.frequenceBanane.bananaBroadcast.scheduling;

import java.util.ArrayList;
import java.util.List;

public class Playlist<AudioType> {
	
	private ArrayList<AudioType> audios;
	private Runnable onChangeRunnable = null;
	
	public Playlist() {
		audios = new ArrayList<AudioType>();
	}

	public AudioType getNext() {
		if(!audios.isEmpty()) {
			triggerOnChange();
			return audios.remove(0);
		}else {
			return null;
		}
	}
	
	public void addAtEnd(AudioType audio) {
		audios.add(audio);
		triggerOnChange();
	}
	
	public void addAtEnd(List<AudioType> audio) {
		if(audio != null)
			audio.forEach( (elem) -> addAtEnd(elem));
	}
	
	public void removeAll() {
		while(!audios.isEmpty())
			audios.remove(0);
	}
	
	public void setOnChange(Runnable runnable) {
		onChangeRunnable = runnable;
	}
	
	public List<AudioType> getList() {
		return new ArrayList<AudioType>(audios);
	}
	
	private void triggerOnChange() {
		if(onChangeRunnable != null)
			onChangeRunnable.run();
	}

}
