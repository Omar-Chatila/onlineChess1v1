package util;

import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class StageMover {

    private final Pane root;
    private final Stage stage;
    private double xOffset = 0;
    private double yOffset = 0;

    public StageMover(Pane root, Stage stage) {
        this.root = root;
        this.stage = stage;
        moveStage();
    }

    public void moveStage() {
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

    }
}
