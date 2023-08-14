package util;

public class IntIntPair {
    private int row;
    private int column;

    public IntIntPair(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return row + "" + column;
    }
}
