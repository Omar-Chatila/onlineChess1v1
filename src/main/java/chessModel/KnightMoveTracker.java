package chessModel;

public class KnightMoveTracker {
	public static boolean validateKnight(String[][] board, String move, boolean white) { // z�ge wie NEd5 oder N5e4 //																					// ber�cksichtigen fehlt
		int[] offsetY = { -2, -1, 1, 2, -2, -1, 1, 2 };
		int[] offsetX = { -1, -2, -2, -1, 1, 2, 2, 1 };
		if (!move.contains("x")) {
			int file = move.charAt(1) - 'a';
			int rank = 8 - Character.getNumericValue(move.charAt(2));
			for (int i = 0; i < 8; i++) {
				if (rank + offsetY[i] >= 0 && rank + offsetY[i] < 8 && file + offsetX[i] >= 0
						&& file + offsetX[i] < 8) {
					int rankY = rank + offsetY[i];
					int fileX = file + offsetX[i];
					if (board[rankY][fileX].equals("N") && white) {
						board[rankY][fileX] = ".";
						board[rank][file] = "N";
						return true;
					} else if (board[rankY][fileX].equals("n") && !white) {
						board[rankY][fileX] = ".";
						board[rank][file] = "n";
						return true;
					}
				}
			}
		} else {
			int file = move.charAt(2) - 'a';
			int rank = 8 - Character.getNumericValue(move.charAt(3));
			for (int i = 0; i < 8; i++) {
				if (rank + offsetY[i] >= 0 && rank + offsetY[i] < 8 && file + offsetX[i] >= 0
						&& file + offsetX[i] < 8) {
					int rankY = rank + offsetY[i];
					int fileX = file + offsetX[i];
					if (board[rankY][fileX].equals("N") && white) {
						if (board[rank][file].matches("[npkqrb]")) {
							board[rankY][fileX] = ".";
							board[rank][file] = "N";
							return true;
						}
					} else if (board[rankY][fileX].equals("n") && !white) {
						if (board[rank][file].matches("[NPKBRQ]")) {
							board[rankY][fileX] = ".";
							board[rank][file] = "n";
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}
