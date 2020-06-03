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

//TODO Est-ce que la gestion des exceptions est bien faite ici ?
public class AudioUtils {
	private AudioUtils(){};
	
	public static ByteArrayOutputStream getWaveform(int imageWidth, int imageHeight, AudioFile audioFile) throws IOException{		
		if(imageWidth <= 0 || imageHeight <= 0) {
			throw new IllegalArgumentException("given width or height is zero or negative");
		}
		if(audioFile == null) {
			throw new IllegalArgumentException("given audioFile is null");
		}
		
		int color = new Color(255, 187, 0).getRGB();
		
		AudioFormat format = AudioUtils.getDefaultAudioFormat();
		int byteArrayLength = format.getChannels()*format.getSampleSizeInBits()/8;
		
		AudioInputStream inputStream = AudioUtils.getAudioFileStream(format, audioFile.path);
		BufferedImage output   = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D    graphics = output.createGraphics();
		graphics.setPaint(new Color(0, 0, 0));
		graphics.fillRect(0, 0, output.getWidth(), output.getHeight());
		
		long streamLength = inputStream.getFrameLength();
		int framesToSkip  = (int)Math.floor(streamLength/imageWidth/2)*2;
		
		double ratioY = imageHeight/Math.pow(2, format.getSampleSizeInBits());
		
		byte[] dataByte = new byte[byteArrayLength];
		
		int i=0;
		do{
			dataByte = inputStream.readNBytes(byteArrayLength);
			inputStream.skip(format.getChannels()*framesToSkip*format.getSampleSizeInBits()/8);
			
			int dataRight = 0;
			int dataLeft  = 0;

			if(dataByte.length != 0) {
				for(int j=0; j<format.getSampleSizeInBits()/8; j++) {
					dataLeft +=  dataByte[j]<<(8*j);
					if(format.getChannels() == 2) {
						dataRight += dataByte[j+ format.getSampleSizeInBits()/8 ]<<(8*j);
					}
				}

				for(int j=0; j<=Math.abs(dataLeft*ratioY); j++) {
					output.setRGB(i, imageHeight/2-j, color);
				}
				
				for(int j=0; j<=Math.abs(dataRight*ratioY); j++) {
					output.setRGB(i, imageHeight/2+j -1, color);
				}
			}
			i++;
		}while(i<imageWidth && dataByte.length != 0);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ImageIO.write(output, "png", outputStream);
		
		return outputStream;
	}
	
	public static AudioInputStream getAudioFileStream(AudioFormat targetFormat, String name) {
		InputStream rawInputStream;
		long inputStreamSize = 0;
		
		if(getExtension(name).equals("mp3")) {
			
			Bitstream bitStream;
			
			try {
				bitStream = new Bitstream(new FileInputStream(new File(name)));
			} catch (FileNotFoundException e1) {
				System.err.println("File not found");
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
			} catch (BitstreamException e) {
				System.err.println("Error in file reading");
				return null;
			} catch (DecoderException e) {
				System.err.println("Error in file decoding");
				return null;
			} catch (IOException e) {
				System.err.println("Error in file IO");
				return null;
			}
        	
        	inputStreamSize /= targetFormat.getFrameSize();
        	rawInputStream = new ByteArrayInputStream(output.toByteArray());
			
		}else if(getExtension(name).equals("wav")) {
			AudioInputStream tmpStream;
			try {
				tmpStream = AudioSystem.getAudioInputStream(new File(name));
			} catch (UnsupportedAudioFileException e) {
				System.err.println("Unsupported file format");
				return null;
			} catch (IOException e) {
				System.err.println("Error in file IO");
				return null;
			}
			inputStreamSize = tmpStream.getFrameLength();
			rawInputStream = tmpStream;
		}else {
			System.err.println("Unsupported file format");
			return null;
		}
		
		return AudioSystem.getAudioInputStream(targetFormat, new AudioInputStream(rawInputStream, targetFormat, inputStreamSize)); 
	}
	
	public static Music getAudioMetadata(String file) {
		try {
			org.jaudiotagger.audio.AudioFile audioFile = AudioFileIO.read(new File(file));
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
					new File(file).getAbsolutePath()
				);			
		} catch (CannotReadException | IOException | TagException | ReadOnlyFileException
				| InvalidAudioFrameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	private static String getTextTagValue(FieldKey key, Tag tag) {
		String value = tag.getFirstField(key).toString();
		if(value.startsWith("Text=")) {
			return value.substring(6, value.length()-3);
		}else {
			return value;
		}
	}
	
	private static String getExtension(String name) {
		if(name.length() < 3) {
			System.err.println("File name is too short, unable to find extension");
		}
		return name.substring(name.length()-3, name.length());
	}
	
	private static AudioFormat getDefaultAudioFormat() {
		int sampleSize      = 16;
		int nbChannel       = 2;
		boolean signed      = true;
		boolean bigEndian   = false;
		float sampleRate    = (float) 44.1;
		return new AudioFormat(sampleRate, sampleSize, nbChannel, signed, bigEndian);
	}
	
	public static class NoAvailableClipLineFound extends NoSuchElementException{
		private static final long serialVersionUID = -4498309451179199554L;
	}
}
