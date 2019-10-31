package com.example.whereismystuff;

import android.provider.BaseColumns;

public final class DatabaseContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private DatabaseContract() {}

    /* Inner class that defines the table contents */
    public static class DatabaseEntry implements BaseColumns {
        public static final String TABLE_NAME = "stuff";
        public static final String COLUMN_NAME_WHAT = "what";
        public static final String COLUMN_NAME_TOWHOM = "towhom";
        public static final String COLUMN_NAME_WHEN = "lendwhen";
        public static final String COLUMN_NAME_CLOSED = "closed";


    }


}
