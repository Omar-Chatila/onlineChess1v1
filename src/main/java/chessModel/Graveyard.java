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

    public List<String> getCapturedPieces() {
        return capturedPieces;
    }

    public int getDifference(Graveyard other) {
        if (this.isWhite() != other.isWhite()) {
            int myScore = 0;
            int otherScore = 0;
            for (String p : this.capturedPieces) {
                switch (p) {
                    case "Q" -> myScore += 9;
                    case "P" -> myScore += 1;
                    case "R" -> myScore += 5;
                    case "N", "B" -> myScore += 3;
                }
            }
            for (String p : other.capturedPieces) {
                switch (p) {
                    case "Q" -> otherScore += 9;
                    case "P" -> otherScore += 1;
                    case "R" -> otherScore += 5;
                    case "N", "B" -> otherScore += 3;
                }
            }
            return myScore - otherScore;
        }
        throw new IllegalArgumentException("same color graveyard");
    }

    public boolean isWhite() {
        return this.white;
    }

    public static void main(String[] args) {
        Graveyard graveyard = new Graveyard(true);
        graveyard.addPiece("P");
        graveyard.addPiece("Q");
        graveyard.addPiece("N");
        graveyard.addPiece("R");
        graveyard.addPiece("Q");


        Graveyard graveyard2 = new Graveyard(true);
        graveyard2.addPiece("R");
        graveyard2.addPiece("P");
        graveyard2.addPiece("P");
        graveyard2.addPiece("N");
        graveyard2.addPiece("Q");
        System.out.println(graveyard.getCapturedPieces());
        System.out.println(graveyard2.getCapturedPieces());
        System.out.println(graveyard.getDifference(graveyard2));
    }
}
