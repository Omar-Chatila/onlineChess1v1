package themes;

import java.util.Objects;

public class SwagTheme extends Theme {
    public String getLightSquareStyle() {
        return "-fx-background-radius: 0;" + "-fx-background-color: #F4F4F4;";
    }

    @Override
    public String getDarkSquareStyle() {
        return "-fx-background-radius: 0;" + "-fx-background-color: #333333A0;";
    }

    public String getLightPastStyle() {
        return "-fx-background-radius: 0;" + "-fx-background-color: #D6D6D6;"; // Light gray for past light squares
    }

    public String getDarkPastStyle() {
        return "-fx-background-radius: 0;" + "-fx-background-color: #888888A0;"; // Dark gray for past dark squares
    }

    @Override
    public String getLastMoveLight() {
        /* Green color */
        if (isNoHighlighting()) {
            return "-fx-background-color: transparent;";
        }
        return "-fx-background-color: #d1f0d5;";
    }

    @Override
    public String getLastMoveDark() {
        if (isNoHighlighting()) {
            return "-fx-background-color: transparent;";
        }
        return "-fx-background-color: derive(#d1f0d5, -20%);";
    }

    @Override
    public String getHoveredXStyle() {
        if (isNoHighlighting()) {
            return "-fx-background-color: transparent;";
        }
        return "-fx-background-color: rgba(236,88,88,0.44);" + "-fx-background-radius: 0;";
    }

    @Override
    public String getHoveredStyle() {
        if (isNoHighlighting()) {
            return "-fx-background-color: transparent;";
        }
        return "-fx-background-color: #87CEEB80;" + "-fx-background-radius: 0;";
    }

    @Override
    public String getKingCheckedStyle() {
        return "-fx-background-color: rgba(255, 0, 0, 0.5);" + "-fx-background-radius: 0;";
    }

    @Override
    public String getBackGround() {
        String imageUrl = Objects.requireNonNull(getClass().getResource("/images/swag/wallpaper.jpg")).toExternalForm();
        return "-fx-background-image: url('" + imageUrl + "'); " +
                "-fx-background-size: cover;";
    }

    @Override
    public String getPiecesPath() {
        return "swag/";
    }

    @Override
    public String getLoginViewPaneStyle() {
        return getBackGround();
    }

    @Override
    public String getSettingsStyle() {
        return getBackGround();
    }
}
