package fun.rich.ui.notification;

import java.awt.Color;

public enum NotificationMode {

    SUCCESS("Success", new Color(0, 255, 0), "a"),
    WARNING("WARNING", new Color(255, 128, 0), "b"),
    INFO("Information",new Color(255, 255, 255), "c");

    private final String iconString;
    private final String titleString;
    private final Color color;

    NotificationMode(String titleString, Color color, String iconString) {
        this.titleString = titleString;
        this.color = color;
        this.iconString = iconString;
    }

    public final String getIconString() {
        return iconString;
    }
    public final Color getColor() {
        return color;
    }
    public final String getTitleString() {
        return titleString;
    }
}
