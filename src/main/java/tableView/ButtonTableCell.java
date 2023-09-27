package tableView;

import com.example.controller.ClientController;
import com.example.controller.GameStates;
import com.example.controller.ServerController;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import util.ApplicationData;

public class ButtonTableCell extends TableCell<Item, String> {
    private final Button button;
    private final String defaultStyle = "-fx-background-color: transparent;" + "-fx-background-radius: 0;" + "-fx-text-fill: black;";
    private final String hoverStyle = "-fx-background-color: #3489eb;" + "-fx-background-radius: 0;" + "-fx-text-fill: black;";
    private final String clickedStyle = "-fx-background-color: rgba(236,16,16,0.5);" + "-fx-background-radius: 0;" + "-fx-text-fill: black;";

    public ButtonTableCell() {
        button = new Button();
        button.setStyle(defaultStyle);
        button.setOnMouseEntered(e -> {
            if (!button.getStyle().equals(clickedStyle)) {
                button.setStyle(hoverStyle);
            }
        });
        button.setOnMouseExited(e -> {
            if (!button.getStyle().equals(clickedStyle)) {
                button.setStyle(defaultStyle);
            }
        });
        button.setPrefWidth(80);
        button.setAlignment(Pos.BASELINE_LEFT);
        button.setOnAction(event -> {
            String move = getItem();
            if (move != null) {
                int rowIndex = getIndex() * 2;
                int colIndex = getTableView().getColumns().indexOf(getTableColumn()) - 1;
                int number = rowIndex + colIndex + 1;
                if (GameStates.isServer()) {
                    ServerController.currentPositionNr = number;
                    ApplicationData.getInstance().getServerController().showBoardAt(number);
                } else {
                    ClientController.currentPositionNr = number;
                    ApplicationData.getInstance().getClientController().showBoardAt(number);
                }
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
