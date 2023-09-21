package util;

public record IntIntPair(int row, int column) {

    @Override
    public String toString() {
        return row + "" + column;
    }

}
