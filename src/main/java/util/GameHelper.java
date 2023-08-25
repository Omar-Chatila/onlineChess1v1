package util;

public class GameHelper {
    public static String[][] copyBoard(String[][] board) {
        String[][] copy = new String[8][8];
        for (int i = 0; i < board.length; i++) {
            System.arraycopy(board[i], 0, copy[i], 0, board[i].length);
        }
        return copy;
    }

    public static void print(String[][] board) {
        System.out.println("-----------------------------");
        for (String[] a : board) {
            for (String s : a) {
                System.out.print(s + " ");
            }
            System.out.println();
        }
        System.out.println("-----------------------------");
        System.out.println();
    }

    public static void initialize(String[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (i != 1 && i != 6) {
                    board[i][j] = ".";
                } else if (i == 1) {
                    board[i][j] = "p";
                } else {
                    board[i][j] = "P";
                }
            }
        }
        board[0][0] = "r";
        board[0][1] = "n";
        board[0][2] = "b";
        board[0][3] = "q";
        board[0][4] = "k";
        board[0][5] = "b";
        board[0][6] = "n";
        board[0][7] = "r";

        board[7][0] = "R";
        board[7][1] = "N";
        board[7][2] = "B";
        board[7][3] = "Q";
        board[7][4] = "K";
        board[7][5] = "B";
        board[7][6] = "N";
        board[7][7] = "R";
    }
}
