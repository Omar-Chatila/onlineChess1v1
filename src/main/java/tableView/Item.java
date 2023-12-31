package tableView;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Item {
    private final IntegerProperty number;
    private final StringProperty whiteMove;
    private final StringProperty blackMove;

    public Item(int number, String whiteMove, String blackMove) {
        this.number = new SimpleIntegerProperty(number);
        this.whiteMove = new SimpleStringProperty(whiteMove);
        this.blackMove = new SimpleStringProperty(blackMove);
    }

    public IntegerProperty numberProperty() {
        return number;
    }

    public StringProperty whiteMoveProperty() {
        return whiteMove;
    }

    public StringProperty blackMoveProperty() {
        return blackMove;
    }

    public void setBlackMove(String blackMove) {
        this.blackMove.set(blackMove);
    }

    public int getNumber() {
        return number.get();
    }

    public boolean isBlack() {
        return whiteMoveProperty() == null;
    }
}
