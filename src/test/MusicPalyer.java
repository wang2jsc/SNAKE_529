package test;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

public class MusicPalyer {

    boolean isMusicPlaying = false;
    private Clip clip;

    public void playMusic(String musicLocation) {
        try {
            File musicPath = new File(musicLocation);
            if (musicPath.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                clip = AudioSystem.getClip();
                clip.open(audioInput);
                clip.start();
                clip.loop(Clip.LOOP_CONTINUOUSLY);
                isMusicPlaying = true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void stopMusic() {
        if (isMusicPlaying) {
            clip.stop();
            clip.close();
            isMusicPlaying = false;
        }
    }
}

