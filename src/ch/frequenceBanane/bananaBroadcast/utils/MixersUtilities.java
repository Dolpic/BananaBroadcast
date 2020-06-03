package ch.frequenceBanane.bananaBroadcast.utils;

import java.util.ArrayList;

import javax.sound.sampled.*;

import ch.frequenceBanane.bananaBroadcast.utils.AudioUtils.NoAvailableClipLineFound;

public class MixersUtilities {
	private MixersUtilities(){};
	
	//TODO ces fonctions sont du copier-coller, peut mieux faire
	//TODO vérifier si dans certains cas il peut y avoir des output qui ne supportent pas les classes "Clip"
	
	public static ArrayList<Mixer> getOutputMixers() {
		ArrayList<Mixer> result = new ArrayList<Mixer>();
		Mixer.Info[] mixers = AudioSystem.getMixerInfo();
		
		for(Mixer.Info mixerInfo : mixers) {
			Mixer mixer =  AudioSystem.getMixer(mixerInfo);
			Line.Info[] outputs = mixer.getSourceLineInfo();
			for(Line.Info output : outputs) {
				if(output.getLineClass() == Clip.class)
					result.add(mixer);
			}
		}
		return result;
	}
	
	public static ArrayList<String> getOutputMixersInfos() {
		ArrayList<Mixer> mixers = getOutputMixers();
		ArrayList<String> result = new ArrayList<String>();
		
		for(Mixer mixer : mixers) {
			result.add(mixer.getMixerInfo().getName());
		}
		return result;
	}
	
	
	
	public static ArrayList<Mixer> getInputMixers() {
		ArrayList<Mixer> result = new ArrayList<Mixer>();
		Mixer.Info[] mixers = AudioSystem.getMixerInfo();
		
		for(Mixer.Info mixerInfo : mixers) {
			Mixer mixer =  AudioSystem.getMixer(mixerInfo);
			Line.Info[] inputs = mixer.getTargetLineInfo();
			for(Line.Info input : inputs) {
				if(input.getLineClass() == TargetDataLine.class)
					result.add(mixer);
			}
		}
		return result;
	}
	
	public static ArrayList<String> getInputMixersInfos() {
		ArrayList<Mixer> mixers = getInputMixers();
		ArrayList<String> result = new ArrayList<String>();
		
		for(Mixer mixer : mixers) {
			result.add(mixer.getMixerInfo().getName());
		}
		return result;
	}
	
	public static Clip getClipByMixerIndex(final int index) {
		ArrayList<Mixer> mixers = MixersUtilities.getOutputMixers();
		
		if(index < 0 || index >= mixers.size())
			throw new IllegalArgumentException("Invalid index given : "+index);
		
		Mixer currentMixer = mixers.get(index);
		Line.Info[] outputs = currentMixer.getSourceLineInfo();
		for(Line.Info output : outputs) {
			if(output.getLineClass() == Clip.class) {
				try {
					return (Clip) currentMixer.getLine(output);
				} catch (LineUnavailableException e) {}
			}
		}
		throw new NoAvailableClipLineFound();
	}
	
}
