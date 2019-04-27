package com.example.recipebuddy;

import android.provider.BaseColumns;

public class DBConstants {

    public static final class KitchenColumns implements BaseColumns {
        public static final String TABLE_NAME = "kitchen";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_FAVORITED = "favorited";
    }
}
