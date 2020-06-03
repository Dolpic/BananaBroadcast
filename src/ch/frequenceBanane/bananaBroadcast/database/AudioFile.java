package ch.frequenceBanane.bananaBroadcast.database;

public abstract class AudioFile {
	
	public int id;
	public String title;
	public int duration;
	public int startTime;
	public int endTime;
	public String path;
	
	public AudioFile(int id,        String title, int    duration, 
			         int startTime, int endTime,  String path) {
		this.id        = id;
		this.title     = title;
		this.duration  = duration;
		this.startTime = startTime;
		this.endTime   = endTime;
		this.path      = path;
	}
	
	public AudioFile(AudioFile audio) {
		this.id        = audio.id;
		this.title     = audio.title;
		this.duration  = audio.duration;
		this.startTime = audio.startTime;
		this.endTime   = audio.endTime;
		this.path      = audio.path;
	}
}
