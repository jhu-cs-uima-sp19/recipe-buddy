package com.example.recipebuddy;


import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;
import android.provider.BaseColumns;
import com.example.recipebuddy.DBConstants.*;

public class KitchenDBHandler extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "kitchen.db";
    public static final int DATABASE_VERSION = 1;

    public KitchenDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CURRENT_KITCHEN_TABLE =
                "CREATE TABLE " +
                KitchenColumns.TABLE_NAME + " (" +
                KitchenColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KitchenColumns.COLUMN_NAME + " TEXT NOT NULL, " +
                KitchenColumns.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";

        db.execSQL(CURRENT_KITCHEN_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + KitchenColumns.TABLE_NAME);
        onCreate(db);
    }
}
