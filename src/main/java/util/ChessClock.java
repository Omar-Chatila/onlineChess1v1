package util;

import com.example.controller.GameStates;
import javafx.application.Platform;

import java.util.Timer;
import java.util.TimerTask;

public class ChessClock {
    private Timer timer;
    private int remainingTime;
    private boolean timeUp;
    private final boolean isMyClock;

    public ChessClock(int totalTime, boolean myClock) {
        this.remainingTime = totalTime;
        this.isMyClock = myClock;
        timer = new Timer();
    }

    public void startTimer() {
        timer = new Timer();
        this.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                remainingTime--;
                if (remainingTime == 10) {
                    new SoundPlayer().playLowTimeSound();
                }
                updateTimeLabels();
                if (remainingTime <= 0) {
                    timer.cancel();
                    playerFlagged();
                }
                if (GameStates.isGameOver()) {
                    timer.cancel();
                }
            }
        }, 50, 1000);
    }

    private void updateTimeLabels() {
        if (isMyClock) {
            Platform.runLater(() -> ApplicationData.getInstance().getIvc().updateMyClock(getClockText()));
        } else {
            Platform.runLater(() -> ApplicationData.getInstance().getIvc().updateOppClock(getClockText()));
        }
    }

    private void playerFlagged() {
        GameStates.setGameOver(true);
        new SoundPlayer().playGameEndSound();
        Platform.runLater(() -> {
            if (isMyClock && GameStates.iAmWhite() || !isMyClock && !GameStates.iAmWhite()) {
                ApplicationData.getInstance().getIvc().updateInfoText("Time Up! Black won!");
            } else if (isMyClock && !GameStates.iAmWhite() || !isMyClock && GameStates.iAmWhite()) {
                ApplicationData.getInstance().getIvc().updateInfoText("Time Up! White won!");
            }
        });
    }

    public String getClockText() {
        int minutes = (remainingTime / 60) % 60;
        int seconds = remainingTime % 60;
        String min = String.format("%02d", minutes);
        String sec = String.format("%02d", seconds);
        return min + ":" + sec;
    }

    public void pauseClock() {
        this.timer.cancel();
    }
}
