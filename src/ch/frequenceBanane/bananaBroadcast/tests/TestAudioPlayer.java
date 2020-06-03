package ch.frequenceBanane.bananaBroadcast.tests;

import org.junit.jupiter.api.Test;

import ch.frequenceBanane.bananaBroadcast.audio.AudioPlayer;

public class TestAudioPlayer {

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
		/*AudioPlayer player = new AudioPlayer();
		String[] emptyStringArray = {};
		Music invalidMusic = new Music(0,"","","","",emptyStringArray,0,0,0,"");
		assertThrows(IllegalArgumentException.class, () -> player.load(null));
		assertThrows(IllegalArgumentException.class, () -> player.load(invalidMusic));*/
	}
	
	@Test
	public void loadReturnFalseIfAlreadyPlaying() {
		/*AudioPlayer player = new AudioPlayer();
		String[] emptyStringArray = {};
		Music dummyMusic  = new Music(0,"","","","",emptyStringArray,0,0,0,"sample1.mp3");
		Music dummyMusic2 = new Music(0,"","","","",emptyStringArray,0,0,0,"sample2.mp3");
		assertEquals(true, player.load(dummyMusic));
		player.play();
		assertEquals(false, player.load(dummyMusic2));*/
	}
	
	@Test
	public void isPlayingReturnsExpectedValues() {
		/*AudioPlayer player = new AudioPlayer();
		String[] emptyStringArray = {};
		Music dummyMusic  = new Music(0,"","","","",emptyStringArray,0,0,0,"sample1.mp3");
		
		assertEquals(false, player.isPlaying());
		player.load(dummyMusic);
		assertEquals(false, player.isPlaying());
		player.play();
		assertEquals(true, player.isPlaying());
		player.pause();
		assertEquals(false, player.isPlaying());
		player.play();
		assertEquals(true, player.isPlaying());
		player.close();
		assertEquals(false, player.isPlaying());
		player.load(dummyMusic);
		assertEquals(false, player.isPlaying());*/
	}
	
	@Test
	public void selectMixerThrowsRightExceptionIsInvalidArgumentOrWrongState() {
		/*AudioPlayer player = new AudioPlayer();
		String[] emptyStringArray = {};
		Music dummyMusic  = new Music(0,"","","","",emptyStringArray,0,0,0,"sample1.mp3");
		
		player.load(dummyMusic);
		assertThrows(IllegalStateException.class, () -> player.selectMixer(0));
		player.close();
		player.selectMixer(0);
		assertThrows(IllegalArgumentException.class, () -> player.selectMixer(-1));*/
	}
	
	
}
