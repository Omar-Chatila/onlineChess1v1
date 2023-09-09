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

    public void playPromotionSound() {
        String soundFile = Objects.requireNonNull(getClass().getResource("/sounds/promote.mp3")).toString();
        Media sound = new Media(soundFile);
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }

    public void playOpponentMoveSound() {
        String soundFile = Objects.requireNonNull(getClass().getResource("/sounds/move-opponent.mp3")).toString();
        Media sound = new Media(soundFile);
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }

    public void playIllegalMoveSound() {
        String soundFile = Objects.requireNonNull(getClass().getResource("/sounds/illegal.mp3")).toString();
        Media sound = new Media(soundFile);
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }

    public void playGameEndSound() {
        String soundFile = Objects.requireNonNull(getClass().getResource("/sounds/game-end.mp3")).toString();
        Media sound = new Media(soundFile);
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }

    public void playGameStartSound() {
        String soundFile = Objects.requireNonNull(getClass().getResource("/sounds/game-start.mp3")).toString();
        Media sound = new Media(soundFile);
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }
}
