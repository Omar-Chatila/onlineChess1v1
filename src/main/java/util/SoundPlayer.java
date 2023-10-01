package util;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.Objects;

public class SoundPlayer {

    public static boolean muted;

    public void playNotificationSound() {
        if (!muted) {
            String soundFile = Objects.requireNonNull(getClass().getResource("/sounds/notify.mp3")).toString();
            Media sound = new Media(soundFile);
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play();
        }
    }

    public void playMoveSelfSound() {
        if (!muted) {
            String soundFile = Objects.requireNonNull(getClass().getResource("/sounds/move-self.mp3")).toString();
            Media sound = new Media(soundFile);
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play();
        }
    }

    public void playCheckSound() {
        if (!muted) {
            String soundFile = Objects.requireNonNull(getClass().getResource("/sounds/move-check.mp3")).toString();
            Media sound = new Media(soundFile);
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play();
        }
    }

    public void playCastleSound() {
        if (!muted) {
            String soundFile = Objects.requireNonNull(getClass().getResource("/sounds/castle.mp3")).toString();
            Media sound = new Media(soundFile);
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play();
        }
    }

    public void playCaptureSound() {
        if (!muted) {
            String soundFile = Objects.requireNonNull(getClass().getResource("/sounds/capture.mp3")).toString();
            Media sound = new Media(soundFile);
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play();
        }
    }

    public void playPromotionSound() {
        if (!muted) {
            String soundFile = Objects.requireNonNull(getClass().getResource("/sounds/promote.mp3")).toString();
            Media sound = new Media(soundFile);
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play();
        }
    }

    public void playOpponentMoveSound() {
        if (!muted) {
            String soundFile = Objects.requireNonNull(getClass().getResource("/sounds/move-opponent.mp3")).toString();
            Media sound = new Media(soundFile);
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play();
        }
    }

    public void playIllegalMoveSound() {
        if (!muted) {
            String soundFile = Objects.requireNonNull(getClass().getResource("/sounds/illegal.mp3")).toString();
            Media sound = new Media(soundFile);
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play();
        }
    }

    public void playGameEndSound() {
        if (!muted) {
            String soundFile = Objects.requireNonNull(getClass().getResource("/sounds/game-end.mp3")).toString();
            Media sound = new Media(soundFile);
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play();
        }
    }

    public void playGameStartSound() {
        if (!muted) {
            String soundFile = Objects.requireNonNull(getClass().getResource("/sounds/game-start.mp3")).toString();
            Media sound = new Media(soundFile);
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play();
        }
    }

    public void playLowTimeSound() {
        if (!muted) {
            String soundFile = Objects.requireNonNull(getClass().getResource("/sounds/tenseconds.mp3")).toString();
            Media sound = new Media(soundFile);
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play();
        }
    }
}
