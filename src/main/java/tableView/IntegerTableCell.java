package tableView;

import javafx.scene.control.TableCell;

public class IntegerTableCell extends TableCell<Item, Integer> {
    @Override
    protected void updateItem(Integer item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
        } else {
            setText(item.toString());
        }
    }
}
