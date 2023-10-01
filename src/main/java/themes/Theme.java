package themes;

public abstract class Theme {
    private static boolean noHighlighting;

    public abstract String getLightSquareStyle();

    public abstract String getDarkSquareStyle();

    public abstract String getLightPastStyle();

    public abstract String getDarkPastStyle();

    public abstract String getLastMoveLight();

    public abstract String getLastMoveDark();

    public abstract String getHoveredXStyle();

    public abstract String getHoveredStyle();

    public abstract String getKingCheckedStyle();

    public abstract String getBackGround();

    public abstract String getPiecesPath();

    public abstract String getLoginViewPaneStyle();

    public abstract String getSettingsStyle();

    public static boolean isNoHighlighting() {
        return noHighlighting;
    }

    public static void setNoHighlighting(boolean nhl) {
        noHighlighting = nhl;
    }
}
