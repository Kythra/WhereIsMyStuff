package com.example.whereismystuff;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Database.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_TABLE);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " +
                    DatabaseContract.DatabaseEntry.TABLE_NAME
                    + " (" +
                    DatabaseContract.DatabaseEntry._ID + " INTEGER PRIMARY KEY," +
                    DatabaseContract.DatabaseEntry.COLUMN_NAME_WHAT + " TEXT," +
                    DatabaseContract.DatabaseEntry.COLUMN_NAME_TOWHOM + " TEXT," +
                    DatabaseContract.DatabaseEntry.COLUMN_NAME_WHEN + " DATETIME," +
                    DatabaseContract.DatabaseEntry.COLUMN_NAME_CLOSED + " TEXT" +
                    ")";

    private static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + DatabaseContract.DatabaseEntry.TABLE_NAME;



}
