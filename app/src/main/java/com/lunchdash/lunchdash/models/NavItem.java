package com.lunchdash.lunchdash.models;

public class NavItem {

    private int imageResource;
    private String title;
    private boolean selected = false;

    public NavItem(String title, int imageResource) {
        this.title = title;
        this.imageResource = imageResource;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }


}
