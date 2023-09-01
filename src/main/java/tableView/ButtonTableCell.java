package tableView;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;

public class ButtonTableCell extends TableCell<Item, String> {
    private final Button button;
    private final String defaultStyle = "-fx-background-color: transparent;" + "-fx-background-radius: 0;" + "-fx-text-fill: black;";
    private final String hoverStyle = "-fx-background-color: #3489eb;" + "-fx-background-radius: 0;" + "-fx-text-fill: black;";
<<<<<<< HEAD
=======
    // private static boolean isWhite = true;   schwarze züge grau markiert werden sollen
>>>>>>> 72fc51c78d5d44dd0c292551a6cd101c95050b90

    public ButtonTableCell() {
        button = new Button();
        button.setStyle(defaultStyle);
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(defaultStyle));
        button.setPrefWidth(50);
<<<<<<< HEAD
=======
        button.setAlignment(Pos.BASELINE_LEFT);
        // if (!isWhite) button.setStyle(button.getStyle() + "-fx-background-color: #736b6b;");
        /*isWhite = !isWhite;*/
>>>>>>> 72fc51c78d5d44dd0c292551a6cd101c95050b90
        button.setOnAction(event -> {
            String move = getItem();
            if (move != null) {
                System.out.println(move);
                // Handle button click event here
                // You can access the data associated with this cell using getTableRow().getItem()
                // For example: Item item = (Item) getTableRow().getItem();
            }
        });
    }

    @Override
    protected void updateItem(String move, boolean empty) {
        super.updateItem(move, empty);
        if (empty || move == null) {
            setGraphic(null);
        } else {
            button.setText(move);
            setGraphic(button);
        }
    }
}
