package ch.frequenceBanane.bananaBroadcast.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.sound.sampled.AudioFormat;

import org.junit.jupiter.api.Test;
import java.io.File;

import ch.frequenceBanane.bananaBroadcast.utils.AudioUtils;

public class AudioUtilsTest {

	/*private final String validFile   = new File(AudioUtilsTest.class.getClassLoader().getResource("audio/sample.mp3").getFile()).getAbsolutePath();
	private final String invalidFile = new File(AudioUtilsTest.class.getClassLoader().getResource("audio").getFile()).getAbsolutePath()+"/wrongSample.wav";
	
	private String[] emptyStringArray = {};
	private Music validAudioFile = new Music(0,"","","","",emptyStringArray,0,0,0,validFile);
	private Music invalidAudioFile = new Music(0,"","","","",emptyStringArray,0,0,0,invalidFile);
	private String wrongFormatFile = "audio/sample.txt";
	
	@Test
	public void GetWaveFormThrowsExpectedExceptions() {
    assertThrows(IllegalArgumentException.class, ()->AudioUtils.getWaveform(0, 100, validAudioFile));
    assertThrows(IllegalArgumentException.class, ()->AudioUtils.getWaveform(-3, 100, validAudioFile));
    assertThrows(IllegalArgumentException.class, ()->AudioUtils.getWaveform(100, 0, validAudioFile));
    assertThrows(IllegalArgumentException.class, ()->AudioUtils.getWaveform(100, -5, validAudioFile));
    assertThrows(IllegalArgumentException.class, ()->AudioUtils.getWaveform(300, 100, null));
	}
	
	@Test
	public void getWaveformReturnResultAsExpected() {
    assertEquals(null, AudioUtils.getWaveform(300, 100, invalidAudioFile));
    assertNotEquals(null, AudioUtils.getWaveform(300, 100, validAudioFile));
	}
	
	@Test
	public void getAudioFileStreamReturnNullWhenInvalidParams() {
    assertThrows(IllegalArgumentException.class, ()->AudioUtils.getAudioFileStream(null, validAudioFile.path));
    assertThrows(IllegalArgumentException.class, ()->AudioUtils.getAudioFileStream(getDefaultAudioFormat(), null));
    
    assertThrows(Exception.class, ()->AudioUtils.getAudioFileStream(getDefaultAudioFormat(), invalidAudioFile.path));
    assertThrows(Exception.class, ()->AudioUtils.getAudioFileStream(getDefaultAudioFormat(), wrongFormatFile));
	}
	
	@Test
	public void getExtensionThrowsExceptionOrReturnNullIfInvalidParameters() {
    assertThrows(IllegalArgumentException.class, ()->AudioUtils.getExtension(null));
    assertEquals(null, AudioUtils.getExtension("a"));
	}
	
	@Test
	public void getAudioMetadataHandlesCorrectlyErrors() {
    assertThrows(IllegalArgumentException.class, ()->AudioUtils.getAudioMetadata(null));
    assertEquals(null, AudioUtils.getAudioMetadata("a"));
	}
	
	@Test
	public void getAudioMetadataReturnsCorrectValues() {
    String[] cat = {"(13)Pop"};
    Music reference = new Music(0, "Never Gonna Give You Up", "Rick Astley", "Whenever You Need Somebody", "(13)Pop", cat, 212, 0, 0 , "");
    Music toCheck = AudioUtils.getAudioMetadata(validAudioFile.path);
    System.out.println(toCheck.album);
    assertEquals(toCheck.album, reference.album);
    assertEquals(toCheck.artist, reference.artist);
    assertEquals(toCheck.duration, reference.duration);
    assertEquals(toCheck.title, reference.title);
    assertEquals(toCheck.genre, reference.genre);
	}
	
	private static AudioFormat getDefaultAudioFormat() {
    final int sampleSize      = 16;
    final int nbChannel       = 2;
    final boolean signed      = true;
    final boolean bigEndian   = false;
    final float sampleRate    = (float) 44.1;
    return new AudioFormat(sampleRate, sampleSize, nbChannel, signed, bigEndian);
	}*/
}
