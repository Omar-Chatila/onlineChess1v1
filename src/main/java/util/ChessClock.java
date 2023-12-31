package util;

import com.example.controller.GameStates;
import javafx.application.Platform;

import java.util.Timer;
import java.util.TimerTask;

public class ChessClock {
    private Timer timer;
    private int remainingTime;
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
                ApplicationData.getInstance().getIvc().showWinner(false);
            } else if (isMyClock && !GameStates.iAmWhite() || !isMyClock && GameStates.iAmWhite()) {
                ApplicationData.getInstance().getIvc().updateInfoText("Time Up! White won!");
                ApplicationData.getInstance().getIvc().showWinner(true);
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

    public void applyIncrement() {
        this.remainingTime += GameStates.getIncrement();
        updateTimeLabels();
    }

    public void addTime() {
        this.remainingTime += 15;
        updateTimeLabels();
    }
}
