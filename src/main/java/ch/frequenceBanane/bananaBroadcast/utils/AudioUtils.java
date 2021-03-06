package ch.frequenceBanane.bananaBroadcast.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import ch.frequenceBanane.bananaBroadcast.database.*;
import javazoom.jl.decoder.*;

/**
 * Provide useful static methods to manage audio files and generate waveforms
 * 
 * @author Corentin Junod
 * @author corentin.junod@epfl.ch
 */

public class AudioUtils {
	private AudioUtils() {
	};

	final static int WAVEFORM_COLOR = new Color(255, 187, 0).getRGB();
	final static Color WAVEFORM_BACKGROUND_COLOR = new Color(0, 0, 0);

	public static enum AudioFileExtension {
		MP3, WAV
	}

	/**
	 * Create a waveform of a given AudioFile, as a streamed PNG file
	 * 
	 * @param imageWidth  the width of the image to generate
	 * @param imageHeight the height of the image to generate
	 * @param audioFile   the Audiofile to generate the waveform from
	 * @return a ByteArrayOutputStream describing the output image in PNG format, or
	 *         null if an error occurs
	 * @throws IllegalArgumentException if the given given sizes are 0 or negative,
	 *                                  or the AudioFile is invalid
	 */
	public static ByteArrayOutputStream getWaveform(final int imageWidth, final int imageHeight,
			final AudioFile audioFile) {
		if (imageWidth <= 0 || imageHeight <= 0)
			throw new IllegalArgumentException("Given width or height is zero or negative");
		if (audioFile == null || audioFile.path == "")
			throw new IllegalArgumentException("Given AudioFile is null or has no path");

		try {
			PersonalAudioFormat f = PersonalAudioFormat.getDefaultAudioFormat();
			AudioInputStream inputStream = AudioUtils.getAudioFileStream(f, audioFile.path);

			byte[] dataByte = new byte[f.getFrameSize()];

			int framesToSkip = (int) Math.floor(inputStream.getFrameLength() / imageWidth / 2) * 2;
			double ratioY = imageHeight / Math.pow(2, f.getSampleSizeInBits());

			BufferedImage output = createImageWithBackground(imageWidth, imageHeight, WAVEFORM_BACKGROUND_COLOR);

			for (int curX = 0; curX < imageWidth; curX++) {

				inputStream.read(dataByte, 0, dataByte.length);
				inputStream.skip(f.getFrameSize() * framesToSkip);

				int dataRight = 0;
				int dataLeft = 0;

				if (dataByte.length != 0) {

					for (int j = 0; j < f.getSampleSize(); j++) {
						dataLeft += dataByte[j] << (8 * j);
						if (f.getChannels() == 2)
							dataRight += dataByte[j + f.getSampleSize()] << (8 * j);
					}

					drawLineFromCenterToTopAndBottom(output, curX, Math.abs(dataLeft * ratioY),
							Math.abs(dataRight * ratioY));
				}
			}
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ImageIO.write(output, "png", outputStream);
			return outputStream;

		} catch (Exception e) {
			Log.error("Exception occurs during waveform generation : " + e.getMessage());
			return null;
		}
	}

	private static void drawLineFromCenterToTopAndBottom(final BufferedImage image, final int x, final double towardTop,
			final double towardBottom) {
		for (int y = 0; y <= towardTop; y++)
			image.setRGB(x, image.getHeight() / 2 - y, WAVEFORM_COLOR);

		for (int y = 0; y <= towardBottom; y++)
			image.setRGB(x, image.getHeight() / 2 + y - 1, WAVEFORM_COLOR);
	}

	/**
	 * Return an audio data stream from a specified file in the specified format
	 * 
	 * @param targetFormat The format in which the result is formatted
	 * @param path         The path to the audio file
	 * @return The audio stream describing the file in the given format
	 * @throws Exception If an error occurs
	 */
	public static AudioInputStream getAudioFileStream(final AudioFormat targetFormat, final String path)
			throws Exception {
		if (targetFormat == null || path == null)
			throw new IllegalArgumentException("One parameter is null");

		InputStream rawInputStream;
		File audioFile = new File(path);
		long inputStreamSize = 0;

		switch (getExtension(path)) {
		case MP3:
			Bitstream bitStream = new Bitstream(new FileInputStream(audioFile));
			Decoder decoder = new Decoder();
			ByteArrayOutputStream output = new ByteArrayOutputStream();

			byte[] bytes = new byte[2];

			Header frameHeader = bitStream.readFrame();
			while (frameHeader != null) {

				short[] next = ((SampleBuffer) decoder.decodeFrame(frameHeader, bitStream)).getBuffer();

				for (int i = 0; i < next.length; i++) {
					bytes[0] = (byte) next[i];
					bytes[1] = (byte) (next[i] >> 8);
					output.write(bytes);
				}

				inputStreamSize += next.length * 2;
				bitStream.closeFrame();
				frameHeader = bitStream.readFrame();
			}

			inputStreamSize /= targetFormat.getFrameSize();
			rawInputStream = new ByteArrayInputStream(output.toByteArray());
			break;

		case WAV:
			AudioInputStream tmpStream = AudioSystem.getAudioInputStream(audioFile);
			inputStreamSize = tmpStream.getFrameLength();
			rawInputStream = tmpStream;
			break;

		default:
			throw new IllegalArgumentException("Invalid file format");
		}

		return AudioSystem.getAudioInputStream(targetFormat,
				new AudioInputStream(rawInputStream, targetFormat, inputStreamSize));
	}

	/**
	 * Return a Music object containing the metadata of the specified file in path
	 * 
	 * @param path the file from which the metadata must be read
	 * @return a Music object containing the metadata, or null if an error occurs
	 * @throws IllegalArgumentException id the given path is null
	 */
	public static AudioFile getAudioMetadata(final String path) {
		if (path == null)
			throw new IllegalArgumentException("path is null");

		try {
			File musicFile = new File(path);
			org.jaudiotagger.audio.AudioFile audioFile = AudioFileIO.read(musicFile);
			Tag tag = audioFile.getTag();

			String[] categories = { tag.getFirst(FieldKey.GENRE) };
			
			AudioFile result = new AudioFile();

			result.title      = tag.getFirst(FieldKey.TITLE);
			result.artist     = tag.getFirst(FieldKey.ARTIST);
			result.album      = tag.getFirst(FieldKey.ALBUM);
			result.genre      = tag.getFirst(FieldKey.GENRE);
			result.categories = categories;
			result.endTime    = audioFile.getAudioHeader().getTrackLength();
			result.path       = musicFile.getAbsolutePath();
			return result;
			
		} catch (Exception e) {
			Log.error("Error during file handling");
			return null;
		}
	}

	/**
	 * Deduce the file extension for a file name
	 * 
	 * @param path the file name/path from which the extension must be deduced
	 * @return The deduced extension, as a value from the enum AudioFileExtension or
	 *         null if the given name is invalid or the extension is not recognized
	 * @throws IllegalArgumentException if the given path is null
	 */
	public static AudioFileExtension getExtension(final String path) {
		if (path == null)
			throw new IllegalArgumentException("path is null");
		if (path.length() < 3) {
			Log.error("File name is too short, unable to find extension");
			return null;
		}

		switch (path.substring(path.length() - 3, path.length())) {
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

	private static BufferedImage createImageWithBackground(final int width, final int height, Color backgroundColor) {
		BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = output.createGraphics();
		graphics.setColor(backgroundColor);
		graphics.fillRect(0, 0, output.getWidth(), output.getHeight());
		return output;
	}

	public static class PersonalAudioFormat extends AudioFormat {
		private PersonalAudioFormat(final float sampleRate, final int sampleSize, final int nbChannel,
				final boolean signed, final boolean bigEndian) {
			super(sampleRate, sampleSize, nbChannel, signed, bigEndian);

		}

		public int getSampleSize() {
			return getSampleSizeInBits() / 8;
		}

		public static PersonalAudioFormat getDefaultAudioFormat() {
			final int sampleSize = 16;
			final int nbChannel = 2;
			final boolean signed = true;
			final boolean bigEndian = false;
			final float sampleRate = (float) 44.1;
			return new PersonalAudioFormat(sampleRate, sampleSize, nbChannel, signed, bigEndian);
		}
	}
}
