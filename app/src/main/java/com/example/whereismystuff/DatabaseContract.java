package com.example.whereismystuff;

import android.provider.BaseColumns;

final class DatabaseContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private DatabaseContract() {}

    /* Inner class that defines the table contents */
    static class DatabaseEntry implements BaseColumns {
        static final String TABLE_NAME = "stuff";
        static final String COLUMN_NAME_WHAT = "what";
        static final String COLUMN_NAME_TOWHOM = "towhom";
        static final String COLUMN_NAME_WHEN = "lendwhen";
        static final String COLUMN_NAME_CLOSED = "closed";
        static final String COLUMN_NAME_ISMONEY = "ismoney";


    }


}
