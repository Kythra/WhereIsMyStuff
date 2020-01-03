package com.example.whereismystuff;

import android.content.ContentValues;


class DatabaseEntry {
    private String what;
    private String towhom;
    private String when;
    private String closed;
    private Boolean ismoney;

    DatabaseEntry(String what, String towhom, String when, String closed){
        this.what = what;
        this.towhom = towhom;
        this.when = when;
        this.closed = closed;

   //     Resources res = Resources.getSystem();
        String[] array = new String[]{"€", "eur", "euro", "EUR", "EURO"};
        for (String s : array) {
            if (what.contains(s)) {
                this.ismoney = true;
            }
        }
        this.ismoney = false;
    }

    public static String[] toStringArray(DatabaseEntry item){
        return new String[]{item.what, item.towhom, item.when, item.closed, String.valueOf(item.ismoney)};
    }

    static ContentValues toContentValues(DatabaseEntry item){
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_WHAT, item.what);
        values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_TOWHOM, item.towhom);
        values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_WHEN, item.when);
        values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_CLOSED, item.closed);
        values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_ISMONEY, item.ismoney);
        return values;
    }
}
