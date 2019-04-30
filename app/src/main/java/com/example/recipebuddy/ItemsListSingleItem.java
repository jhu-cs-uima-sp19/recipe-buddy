package com.example.recipebuddy;

import android.graphics.drawable.Drawable;

public class ItemsListSingleItem {
    private String title;
    private Drawable draw;
    /**
     * Just for the sake of internal reference so that we can identify the item.
     */
    long id;

    /**
     *
     * @param id
     * @param title
     * @param draw
     */
    public ItemsListSingleItem(long id, String title, Drawable draw) {
        this.id = id;
        this.title = title;
        this.draw = draw;
    }

    public String getTitle() {
        return title;
    }

    public long getID() {
        return id;
    }

    public Drawable getThumbnail() {
        return this.draw;
    }

    public void setBackground() {

    }
}
