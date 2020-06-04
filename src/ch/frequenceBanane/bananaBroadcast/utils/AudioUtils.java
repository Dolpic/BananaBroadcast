package ch.frequenceBanane.bananaBroadcast.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.TagField;

import ch.frequenceBanane.bananaBroadcast.database.*;
import javazoom.jl.decoder.*;

/**
 * Provide useful static methods to manage audio files and generate waveforms
 * @author Corentin Junod
 * @author corentin.junod@epfl.ch
 */

public class AudioUtils {
	private AudioUtils(){};
	
	final static int   WAVEFORM_COLOR            = new Color(255, 187, 0).getRGB();
	final static Color WAVEFORM_BACKGROUND_COLOR = new Color(0, 0, 0);
	
	public static enum AudioFileExtension{
		MP3,
		WAV
	}
	
	/**
	 * Create a waveform of a given AudioFile, as a streamed PNG file
	 * @param imageWidth the width of the image to generate
	 * @param imageHeight the height of the image to generate
	 * @param audioFile the Audiofile to generate the waveform from
	 * @return a ByteArrayOutputStream describing the output image in PNG format, or null if an error occurs
	 * @throws IllegalArgumentException if the given given sizes are 0 or negative, or the AudioFile is invalid
	 */
	public static ByteArrayOutputStream getWaveform(final int imageWidth, final int imageHeight, final AudioFile audioFile){		
		if(imageWidth <= 0 || imageHeight <= 0) 
			throw new IllegalArgumentException("Given width or height is zero or negative");
		if(audioFile == null || audioFile.path == "")
			throw new IllegalArgumentException("Given AudioFile is null or has no path");
		
		AudioFormat      format      = AudioUtils.getDefaultAudioFormat();
		AudioInputStream inputStream = AudioUtils.getAudioFileStream(format, audioFile.path);
		if(inputStream == null) {
			Log.error("Unable to open file : "+audioFile.path);
			return null;
		}
		
		//TODO embed those values in a dedicated class, to simplify
		int sampleSizeInBytes = format.getSampleSizeInBits()/8;
		int byteArrayLength   = format.getChannels()*sampleSizeInBytes;
		byte[] dataByte       = new byte[byteArrayLength];
		
		int framesToSkip  = (int)Math.floor(inputStream.getFrameLength()/imageWidth/2)*2;
		double ratioY = imageHeight/Math.pow(2, format.getSampleSizeInBits());
		
		//Set image background
		BufferedImage output = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = output.createGraphics();
		graphics.setColor(WAVEFORM_BACKGROUND_COLOR);
		graphics.fillRect(0, 0, output.getWidth(), output.getHeight());
		for(int i=0; i<imageWidth && dataByte.length != 0; i++) {
			try {
				dataByte = inputStream.readNBytes(byteArrayLength);
				inputStream.skip(format.getChannels()*framesToSkip*sampleSizeInBytes);
			} catch (IOException e) {
				Log.error("Unable to read "+byteArrayLength+" bytes from the file "+audioFile.path);
				return null;
			}
			int dataRight = 0;
			int dataLeft  = 0;

			if(dataByte.length != 0) {
				for(int j=0; j<sampleSizeInBytes; j++) {
					dataLeft += dataByte[j]<<(8*j);
					if(format.getChannels() == 2) {
						dataRight += dataByte[j+ sampleSizeInBytes ]<<(8*j);
					}
				}

				for(int j=0; j<=Math.abs(dataLeft*ratioY); j++)
					output.setRGB(i, imageHeight/2-j, WAVEFORM_COLOR);
				
				for(int j=0; j<=Math.abs(dataRight*ratioY); j++)
					output.setRGB(i, imageHeight/2+j -1, WAVEFORM_COLOR);
			}
		}
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			ImageIO.write(output, "png", outputStream);
			return outputStream;
		} catch (IOException e) {
			Log.error("Unable to write to write the waveform to the output stream");
			return null;
		}
	}
	
	/**
	 * Return an audio data stream from a specified file in the specified format
	 * @param targetFormat The format in which the result is formatted
	 * @param path The path to the audio file
	 * @return The audio stream describing the file in the given format, or null if an error occurs
	 */
	public static AudioInputStream getAudioFileStream(final AudioFormat targetFormat, final String path) {
		if(targetFormat == null || path == null)
			throw new IllegalArgumentException("One parameter is null");
		
		InputStream rawInputStream;
		File audioFile = new File(path);
		long inputStreamSize = 0;
		
		if(getExtension(path) == AudioFileExtension.MP3) {
			
			Bitstream bitStream;
			
			try {
				bitStream = new Bitstream(new FileInputStream(audioFile));
			} catch (FileNotFoundException e1) {
				Log.error("File '"+path+"' not found");
				return null;
			}
			
			Decoder decoder = new Decoder();
			Header frameHeader;
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			
        	byte[] bytes = new byte[2];
        	
        	try {
				frameHeader = bitStream.readFrame();
				while (frameHeader != null) {

					short[] next = ((SampleBuffer)decoder.decodeFrame(frameHeader, bitStream)).getBuffer();
		            for(int i=0; i<next.length; i++) {
		            	bytes[0] = (byte) next[i];
		            	bytes[1] = (byte) (next[i] >> 8);
		            	output.write(bytes);
		            }
		            inputStreamSize += next.length*2;
		            bitStream.closeFrame();
					frameHeader = bitStream.readFrame();
				}
			} catch (Exception e) {
				Log.error("Error during file decoding");
				return null;
			}
        	
        	inputStreamSize /= targetFormat.getFrameSize();
        	rawInputStream = new ByteArrayInputStream(output.toByteArray());
			
		}else if(getExtension(path) == AudioFileExtension.WAV) {
			AudioInputStream tmpStream;
			try {
				tmpStream = AudioSystem.getAudioInputStream(audioFile);
			} catch (Exception e) {
				Log.error("Error during file decoding");
				return null;
			}
			inputStreamSize = tmpStream.getFrameLength();
			rawInputStream = tmpStream;
		}else {
			Log.error("Unsupported file format");
			return null;
		}
		
		return AudioSystem.getAudioInputStream(targetFormat, new AudioInputStream(rawInputStream, targetFormat, inputStreamSize)); 
	}
	
	/**
	 * Return a Music object containing the metadata of the specified file in path
	 * @param path the file from which the metadata must be read
	 * @return a Music object containing the metadata, or null if an error occurs
	 * @throws IllegalArgumentException id the given path is null
	 */
	public static Music getAudioMetadata(final String path) {
		if(path == null)
			throw new IllegalArgumentException("path is null");
		
		try {
			File musicFile = new File(path);
			org.jaudiotagger.audio.AudioFile audioFile = AudioFileIO.read(musicFile);
			Tag tag = audioFile.getTag();
			
			String[] categories = {getTextTagValue(FieldKey.GENRE, tag)};

			return new Music(0,
					getTextTagValue(FieldKey.TITLE, tag),
					getTextTagValue(FieldKey.ARTIST, tag),
					getTextTagValue(FieldKey.ALBUM, tag),
					getTextTagValue(FieldKey.GENRE, tag),
					categories,
					audioFile.getAudioHeader().getTrackLength(),
					0,0,
					musicFile.getAbsolutePath()
				);			
		} catch (Exception e) {
			Log.error("Error during file handling");
			return null;
		}
	}
	
	/**
	 * Deduce the file extension for a file name
	 * @param path the file name/path from which the extension must be deduced
	 * @return The deduced extension, as a value from the enum AudioFileExtension or null 
	 * if the given name is invalid or the extension is not recognized
	 * @throws IllegalArgumentException if the given path is null
	 */
	public static AudioFileExtension getExtension(final String path) {
		if(path == null)
			throw new IllegalArgumentException("path is null");
		if(path.length() < 3) {
			Log.error("File name is too short, unable to find extension");
			return null;
		}
		
		switch(path.substring(path.length()-3, path.length())){
			case "mp3":
			case "MP3":
				return AudioFileExtension.MP3;
			case "wav":
			case "WAV:":
				return AudioFileExtension.WAV;
			default:
				return null;
		}
	}

	private static String getTextTagValue(final FieldKey key, final Tag tag) {
		String value = tag.getFirstField(key).toString();
		if(value.startsWith("Text=")) {
			return value.substring(6, value.length()-3);
		}else {
			return value;
		}
	}

	private static AudioFormat getDefaultAudioFormat() {
		final int sampleSize      = 16;
		final int nbChannel       = 2;
		final boolean signed      = true;
		final boolean bigEndian   = false;
		final float sampleRate    = (float) 44.1;
		return new AudioFormat(sampleRate, sampleSize, nbChannel, signed, bigEndian);
	}
}
