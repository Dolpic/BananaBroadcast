package ch.frequenceBanane.bananaBroadcast.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.frequenceBanane.bananaBroadcast.audio.AudioPlayer;
import ch.frequenceBanane.bananaBroadcast.audio.JinglePlayer;
import ch.frequenceBanane.bananaBroadcast.audio.MusicPlayer;
import ch.frequenceBanane.bananaBroadcast.database.Jingle;
import ch.frequenceBanane.bananaBroadcast.database.Music;

public class TestAudioPlayer {
	
	private final String validFile = "ressources/audio/sample.wav";
	private final String invalidFile = "ressources/audio/Invalidsample.wav";
	
	boolean onLoadWorks   = false;
	boolean onFinishWorks = false;
	boolean onEndWorks    = false;
	boolean onPlayWorks   = false;

	@Test
	public void AudioPlayerOperationsDoesNothingIfNoMusicLoaded() {
		AudioPlayer player = new AudioPlayer();
		player.play();
		AudioPlayer player2 = new AudioPlayer();
		player2.pause();
		AudioPlayer player3 = new AudioPlayer();
		player3.close();
		AudioPlayer player4 = new AudioPlayer();
		player4.play();
		player4.close();
		player4.pause();
		player4.close();
		player4.play();
		player4.pause();
		player4.close();
		player4.close();
	}
	
	@Test
	public void LoadThrowsExceptionsIfInvalidParameters() {
		AudioPlayer player = new AudioPlayer();
		String[] emptyStringArray = {};
		Music invalidMusic = new Music(0,"","","","",emptyStringArray,0,0,0,"");
		assertThrows(IllegalArgumentException.class, () -> player.load(null));
		assertThrows(IllegalArgumentException.class, () -> player.load(invalidMusic));
	}
	
	@Test
	public void LoadValidFileIsOK() {
		AudioPlayer player = new AudioPlayer();
		String[] emptyStringArray = {};
		Music validMusic = new Music(0,"","","","",emptyStringArray,0,0,0,validFile);
		assertEquals(true, player.load(validMusic));
	}
	
	@Test
	public void LoadInexistingFileReturnFalse() {
		AudioPlayer player = new AudioPlayer();
		String[] emptyStringArray = {};
		Music invalidMusic = new Music(0,"","","","",emptyStringArray,0,0,0,invalidFile);
		assertEquals(false, player.load(invalidMusic));
	}

	@Test
	public void ChangingCursorPositionHasExpectedBehaviour() {
		AudioPlayer player = new AudioPlayer();
		String[] emptyStringArray = {};
		Music validMusic = new Music(0,"","","","",emptyStringArray,0,0,0,validFile);
		player.load(validMusic);

		assertEquals(false, player.isPlaying());
		player.play();
		assertEquals(true, player.isPlaying());
		player.goToStart();
		assertEquals(true, player.isPlaying());
		player.goToEnd();
		assertEquals(false, player.isPlaying());
		player.play();
		assertEquals(true, player.isPlaying());
		player.reload();
		assertEquals(false, player.isPlaying());
	}
	
	@Test
	public void SetOnRepeatModeWorksAsExpected() throws InterruptedException {
		AudioPlayer player = new AudioPlayer();
		String[] emptyStringArray = {};
		Music validMusic = new Music(0,"","","","",emptyStringArray,0,0,0,validFile);
		player.load(validMusic);
		
		assertEquals(false, player.isRepeating());
		player.setPosition( (long)player.getDuration()*1000000 - 1000);
		Thread.sleep(500);
		assertEquals(false, player.isPlaying());
		
		player.setRepeat(true);
		player.goToStart();
		player.play();
		assertEquals(true, player.isPlaying());
		
		player.setPosition( (long)player.getDuration()*1000000 - 1000);
		Thread.sleep(500);
		assertEquals(true, player.isPlaying());
		
		player.setRepeat(false);
		player.goToStart();
		player.play();
		player.setPosition( (long)player.getDuration()*1000000 - 1000);
		Thread.sleep(500);
		assertEquals(true, player.isPlaying());
	}
	
	@Test
	public void getCurrentAudioFileIsReturningTheRightAudioFile() {
		AudioPlayer player = new AudioPlayer();
		String[] emptyStringArray = {};
		Music validMusic = new Music(0,"","","","",emptyStringArray,0,0,0,validFile);
		player.load(validMusic);
		assertEquals(validMusic, player.getCurrentAudioFile());
	}
	
	@Test
	public void CantSelectNewMixerWhilePLaying() {
		AudioPlayer player = new AudioPlayer();
		String[] emptyStringArray = {};
		Music validMusic = new Music(0,"","","","",emptyStringArray,0,0,0,validFile);
		player.load(validMusic);
		player.play();
		assertThrows(IllegalStateException.class, () -> player.selectMixer(0));
	}
	
	@Test
	public void EventsAreTriggeredWhenNeeded() throws InterruptedException {
		AudioPlayer player = new AudioPlayer();
		String[] emptyStringArray = {};
		Music validMusic = new Music(0,"","","","",emptyStringArray,0,0,0,validFile);
		
		player.addOnLoadEvent(() -> setEventsBools(true, onFinishWorks, onEndWorks, onPlayWorks));
		player.addOnFinishEvent(() -> setEventsBools(onLoadWorks, true, onEndWorks, onPlayWorks));
		player.addOnEndEvent(() -> setEventsBools(onLoadWorks, onFinishWorks, true, onPlayWorks));
		player.addOnPlayEvent(() -> setEventsBools(onLoadWorks, onFinishWorks, onEndWorks, true));
		
		assertEquals(false, onLoadWorks);
		assertEquals(false, onFinishWorks);
		assertEquals(false, onEndWorks);
		assertEquals(false, onPlayWorks);
		
		player.load(validMusic);
		
		assertEquals(true, onLoadWorks);
		assertEquals(false, onFinishWorks);
		assertEquals(false, onEndWorks);
		assertEquals(false, onPlayWorks);
		
		player.play();
		Thread.sleep(100);
		
		assertEquals(true, onLoadWorks);
		assertEquals(false, onFinishWorks);
		assertEquals(false, onEndWorks);
		assertEquals(true, onPlayWorks);
		
		player.setPosition( (long)player.getDuration()*1000000 - 1000000);
		Thread.sleep(2000);
		
		assertEquals(true, onLoadWorks);
		assertEquals(true, onFinishWorks);
		assertEquals(true, onEndWorks);
		assertEquals(true, onPlayWorks);
	}
	private void setEventsBools(boolean onLoadWorks, boolean onFinishWorks, boolean onEndWorks, boolean onPlayWorks) {
		this.onLoadWorks = onLoadWorks;
		this.onFinishWorks = onFinishWorks;
		this.onEndWorks = onEndWorks;
		this.onPlayWorks = onPlayWorks;
	}
	
	@Test
	public void InstanciateMusicPlayerAndJinglePlayer() {
		MusicPlayer player = new MusicPlayer();
		String[] emptyStringArray = {};
		Music validMusic = new Music(0,"","","","",emptyStringArray,0,0,0,validFile);
		player.loadMusic(validMusic);
		assertEquals(validMusic, player.getCurrentAudioFile());

		JinglePlayer playerJingle = new JinglePlayer();
		Jingle validJingle = new Jingle(0,"",emptyStringArray,emptyStringArray,0,0,0,validFile);
		playerJingle.load(validJingle);
		assertEquals(validJingle, playerJingle.getCurrentAudioFile());
	}
	
	
}
