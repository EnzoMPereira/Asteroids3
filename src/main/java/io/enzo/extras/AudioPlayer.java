package io.enzo.extras;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioPlayer {
	private AudioInputStream audioIn;
	private Clip clip;

	public AudioPlayer(String filePath) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		audioIn = AudioSystem.getAudioInputStream(new File(filePath));
		clip = AudioSystem.getClip();
		clip.open(audioIn);
	}

	public void play() {
		clip.stop();
		clip.setMicrosecondPosition(0);

		clip.start();
	}
}