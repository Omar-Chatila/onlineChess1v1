package util;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.Objects;

public class SoundPlayer {

    public void playNotificationSound() {
        String soundFile = Objects.requireNonNull(getClass().getResource("/sounds/notify.mp3")).toString();
        Media sound = new Media(soundFile);
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }

    public void playMoveSelfSound() {
        String soundFile = Objects.requireNonNull(getClass().getResource("/sounds/move-self.mp3")).toString();
        Media sound = new Media(soundFile);
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }

    public void playCheckSound() {
        String soundFile = Objects.requireNonNull(getClass().getResource("/sounds/move-check.mp3")).toString();
        Media sound = new Media(soundFile);
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }

    public void playCastleSound() {
        String soundFile = Objects.requireNonNull(getClass().getResource("/sounds/castle.mp3")).toString();
        Media sound = new Media(soundFile);
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }

    public void playCaptureSound() {
        String soundFile = Objects.requireNonNull(getClass().getResource("/sounds/capture.mp3")).toString();
        Media sound = new Media(soundFile);
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }


}
