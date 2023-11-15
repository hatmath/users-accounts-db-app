package com.hatmath.connect;

public class UsagerListItem {
    private final String text;
    private final int iconResId;

    public UsagerListItem(String text, int iconResId) {
        this.text = text;
        this.iconResId = iconResId;
    }

    public String getText() {
        return text;
    }

    public int getIconResId() {
        return iconResId;
    }
}
