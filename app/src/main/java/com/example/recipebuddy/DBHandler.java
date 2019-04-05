package com.example.recipebuddy;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;

public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "db";
    public static final String TABLE = "stuff";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PRODUCTNAME = "name";

    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        //String query = "CREATE TABLE " + TABLE + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT " + COLUMN_PRODUCTNAME + " TEXT " + ");";
        String query = null;
        db.execSQL(query);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
}
