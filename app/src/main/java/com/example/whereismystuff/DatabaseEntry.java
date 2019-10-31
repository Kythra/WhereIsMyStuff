package com.example.whereismystuff;

import android.content.ContentValues;

class DatabaseEntry {
    String what;
    String towhom;
    String when;
    String closed;

    public DatabaseEntry(String what, String towhom, String when, String closed){
        this.what = what;
        this.towhom = towhom;
        this.when = when;
        this.closed = closed;
    }

    public static String[] toStringArray(DatabaseEntry item){
        String[] itemStr = {item.what, item.towhom, item.when, item.closed};
        return  itemStr;
    }

    public static ContentValues toContentValues(DatabaseEntry item){
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_WHAT, item.what);
        values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_TOWHOM, item.towhom);
        values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_WHEN, item.when);
        values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_CLOSED, item.closed);
        return values;
    }
}
