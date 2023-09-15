package chessModel;

import javafx.application.Platform;
import util.ApplicationData;

import java.util.ArrayList;
import java.util.List;

public class Graveyard {
    private final List<String> capturedPieces;
    private final boolean white;

    public Graveyard(boolean white) {
        capturedPieces = new ArrayList<>();
        this.white = white;
    }

    public void addPiece(String piece) {
        capturedPieces.add(piece);
        sortList();
        if (white) {
            Platform.runLater(() -> ApplicationData.getInstance().getWgc().addPiece(piece));
        } else {
            Platform.runLater(() -> ApplicationData.getInstance().getBgc().addPiece(piece));
        }
    }

    private void sortList() {
        for (int i = 0; i < capturedPieces.size(); i++) {
            for (int j = i + 1; j < capturedPieces.size(); j++) {
                String current = capturedPieces.get(i);
                String next = capturedPieces.get(j);
                switch (current) {
                    case "P" -> swap(i, j);
                    case "N" -> {
                        if (!next.equals("P")) swap(i, j);
                    }
                    case "B" -> {
                        if (!next.matches("[PN]")) swap(i, j);
                    }
                    case "R" -> {
                        if (!next.matches("[PNB]")) swap(i, j);
                    }
                }
            }
        }
    }

    private void swap(int i, int j) {
        String t = capturedPieces.get(i);
        String s = capturedPieces.get(j);
        capturedPieces.set(i, s);
        capturedPieces.set(j, t);
    }

    public boolean isWhite() {
        return this.white;
    }

    public int count(String p) {
        int result = 0;
        for (String s : this.capturedPieces) {
            if (s.equals(p)) result++;
        }
        return result;
    }
}
