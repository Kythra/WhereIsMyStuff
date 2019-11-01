package com.example.whereismystuff;

import android.content.ContentValues;


class DatabaseEntry {
    String what;
    String towhom;
    String when;
    String closed;
    Boolean ismoney;

    public DatabaseEntry(String what, String towhom, String when, String closed){
        this.what = what;
        this.towhom = towhom;
        this.when = when;
        this.closed = closed;

   //     Resources res = Resources.getSystem();
        String[] array = new String[]{"€", "eur", "euro", "EUR", "EURO"};
        for(int i =0; i < array.length; i++)
        {
            if(what.contains(array[i]))
            {
                this.ismoney = true;
            }
        }
        this.ismoney = false;
    }

    public static String[] toStringArray(DatabaseEntry item){
        String[] itemStr = new String[]{item.what, item.towhom, item.when, item.closed, String.valueOf(item.ismoney)};
        return  itemStr;
    }

    public static ContentValues toContentValues(DatabaseEntry item){
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_WHAT, item.what);
        values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_TOWHOM, item.towhom);
        values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_WHEN, item.when);
        values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_CLOSED, item.closed);
        values.put(DatabaseContract.DatabaseEntry.COLUMN_NAME_ISMONEY, item.ismoney);
        return values;
    }
}
