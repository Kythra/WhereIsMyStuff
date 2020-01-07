package com.example.whereismystuff;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("com.example.whereismystuff", appContext.getPackageName());
    }

    @Test
    public void database_containsEntries() {
        Context context = activityRule.getActivity().getApplicationContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseContract.DatabaseEntry.TABLE_NAME,   // The table to query
                null,          // The array of columns to return (pass null to get all)
                null,     // The values for the WHERE clause
                null,
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );
        boolean containsEntries = (null == cursor);
        assertEquals(false, containsEntries);
    }
}
