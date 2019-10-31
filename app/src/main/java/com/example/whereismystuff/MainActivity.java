package com.example.whereismystuff;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MainActivity extends AppCompatActivity {
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DatabaseHelper(this);
        initResultTable();
//        dbHelper.onUpgrade(dbHelper.getWritableDatabase(), 1,1);
    }


    private void initResultTable() {


        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sortOrder =
                DatabaseContract.DatabaseEntry.COLUMN_NAME_WHEN + " ASC";
        final Cursor cursor;
        Switch sw_showall = findViewById(R.id.sw_showAll);
        if (sw_showall.isChecked()) {
            cursor = db.query(
                    DatabaseContract.DatabaseEntry.TABLE_NAME,   // The table to query
                    null,             // The array of columns to return (pass null to get all)
                    null,              // The columns for the WHERE clause
                    null,          // The values for the WHERE clause
                    null,                   // don't group the rows
                    null,                   // don't filter by row groups
                    null               // The sort order
            );
        }else{
            cursor = db.query(
                    DatabaseContract.DatabaseEntry.TABLE_NAME,   // The table to query
                    null,             // The array of columns to return (pass null to get all)
                    DatabaseContract.DatabaseEntry.COLUMN_NAME_CLOSED + " = ?",              // The columns for the WHERE clause
                    new String[]{String.valueOf(0)},          // The values for the WHERE clause
                    null,                   // don't group the rows
                    null,                   // don't filter by row groups
                    null               // The sort order
            );
        }



        TableLayout ll = (TableLayout) findViewById(R.id.table_db_results);
        ll.removeAllViewsInLayout();

        List itemIds = new ArrayList<>();
        while(cursor.moveToNext()) {

            final TableRow row= new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);

            TextView tv_id = new TextView(this);
            tv_id.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.DatabaseEntry._ID)));
            row.addView(tv_id);

            TextView tv_what = new TextView(this);
            tv_what.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.DatabaseEntry.COLUMN_NAME_WHAT)));
            row.addView(tv_what);

            TextView tv_towhom = new TextView(this);
            tv_towhom.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.DatabaseEntry.COLUMN_NAME_TOWHOM)));
            row.addView(tv_towhom);

            TextView tv_when = new TextView(this);
            tv_when.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.DatabaseEntry.COLUMN_NAME_WHEN)));
            row.addView(tv_when);

            TextView tv_returned = new TextView(this);
            String returnedString = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.DatabaseEntry.COLUMN_NAME_CLOSED));
            if (returnedString.equals("0")){
                tv_returned.setText("---");
            }else {
                tv_returned.setText(returnedString);
            }
            row.addView(tv_returned);

            ll.addView(row);

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // inflate the layout of the popup window
                    final LayoutInflater inflater = (LayoutInflater)
                            getSystemService(LAYOUT_INFLATER_SERVICE);
                    final View popupView = inflater.inflate(R.layout.popup_window, null);

                    // create the popup window
                    int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                    int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                    boolean focusable = true; // lets taps outside the popup also dismiss it
                    final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                    EditText  editWhat = (EditText) popupView.findViewById(R.id.editTextWhat);
                    TextView tmpWhat = (TextView) row.getChildAt(1);
                    editWhat.setText(tmpWhat.getText());
                    EditText  editToWhom = (EditText) popupView.findViewById(R.id.editTextWhom);
                    TextView tmpToWhom = (TextView) row.getVirtualChildAt(2);
                    editToWhom.setText(tmpToWhom.getText());
                    EditText  editWhen = popupView.findViewById(R.id.editText_when);
                    TextView tmpWhen = (TextView) row.getVirtualChildAt(3);
                    editWhen.setText(tmpWhen.getText());
                    EditText  editReturned = popupView.findViewById(R.id.editText_returned);
                    TextView tmpReturned = (TextView) row.getVirtualChildAt(4);
                    editReturned.setText(tmpReturned.getText());



                    // show the popup window
                    // which view you pass in doesn't matter, it is only used for the window tolken
                    popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

     /*               // dismiss the popup window when touched
                    popupView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            popupWindow.dismiss();
                            return true;
                        }
                    });*/

                    Button login = (Button) popupView.findViewById(R.id.btn_done);
                    login.setOnClickListener(new Button.OnClickListener(){

                        @Override
                        public void onClick(View arg0) {
                            TextView tmpID = (TextView) row.getChildAt(0);
                            int id = Integer.parseInt(String.valueOf(tmpID.getText()));

                            EditText  editWhat = (EditText) popupView.findViewById(R.id.editTextWhat);
                            TextView tmpWhat = (TextView) row.getChildAt(1);
                            tmpWhat.setText(editWhat.getText());
                            EditText  editToWhom = (EditText) popupView.findViewById(R.id.editTextWhom);
                            TextView tmpToWhom = (TextView) row.getVirtualChildAt(2);
                            tmpToWhom.setText(editToWhom.getText());
                            EditText  editWhen = popupView.findViewById(R.id.editText_when);
                            TextView tmpWhen = (TextView) row.getVirtualChildAt(3);
                            tmpWhen.setText(editWhen.getText());
                            EditText  editReturned = popupView.findViewById(R.id.editText_returned);
                            TextView tmpReturned = (TextView) row.getVirtualChildAt(4);
                            tmpReturned.setText(editReturned.getText());
                            DatabaseEntry item = new DatabaseEntry(editWhat.getText().toString(), editToWhom.getText().toString(), editWhen.getText().toString(), editReturned.getText().toString());
                            ContentValues values = DatabaseEntry.toContentValues(item);
                            SQLiteDatabase db = dbHelper.getWritableDatabase();

      //                      Log.d("query"," " + cursorIn.getLong(cursorIn.getColumnIndexOrThrow(DatabaseContract.DatabaseEntry._ID)));
      //                      Log.d("query"," " + cursor.getString(cursorIn.getColumnIndexOrThrow(DatabaseContract.DatabaseEntry.COLUMN_NAME_WHAT)));

                            db.update(DatabaseContract.DatabaseEntry.TABLE_NAME, values, "_id="+id, null);
                            db.close();

                            initResultTable();

                            popupWindow.dismiss();
                        }

                    });

                    Button btnDel = (Button) popupView.findViewById(R.id.btn_del);
                    btnDel.setOnClickListener(new Button.OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            TextView tmpID = (TextView) row.getChildAt(0);
                            int id = Integer.parseInt(String.valueOf(tmpID.getText()));
                            SQLiteDatabase db = dbHelper.getWritableDatabase();
                            db.delete(DatabaseContract.DatabaseEntry.TABLE_NAME, "_id="+id, null);
                            initResultTable();
                            popupWindow.dismiss();
                        }
                    });


                    Button btnCnl = (Button) popupView.findViewById(R.id.btn_cancel);
                    btnCnl.setOnClickListener(new Button.OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            popupWindow.dismiss();
                        }
                    });
                }
            });


        }
    }



    public void onButtonShowPopupWindowClick(View view) {

        // inflate the layout of the popup window
        final LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater.inflate(R.layout.popup_window, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

  /*      // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        }); */

        EditText editTextwhen = popupView.findViewById(R.id.editText_when);
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String datetime = now.format(formatter);
        editTextwhen.setText(datetime);
        editTextwhen.setFocusable(false);
        EditText editTextReturned = popupView.findViewById(R.id.editText_returned);
        editTextReturned.setText("---");
        editTextReturned.setFocusable(false);

        Button btnDel = (Button) popupView.findViewById(R.id.btn_del);
        btnDel.setEnabled(false);

        Button login = (Button) popupView.findViewById(R.id.btn_done);
        login.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View arg0) {

                popupWindow.setContentView(inflater.inflate(R.layout.popup_window, null, false));
                EditText  editWhom = (EditText) popupView.findViewById(R.id.editTextWhom);
                EditText  editWhat = (EditText) popupView.findViewById(R.id.editTextWhat);

                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String datetime = now.format(formatter);
                DatabaseEntry item = new DatabaseEntry( editWhat.getText().toString(), editWhom.getText().toString(), datetime, "0");

                SQLiteDatabase db = dbHelper.getWritableDatabase();

                // Create a new map of values, where column names are the keys
                ContentValues values = DatabaseEntry.toContentValues(item);

                // Insert the new row, returning the primary key value of the new row
                long newRowId = db.insert(DatabaseContract.DatabaseEntry.TABLE_NAME, null, values);

        //        addToTable(values);
                initResultTable();

                popupWindow.dismiss();
            }

        });

        Button btnCnl = (Button) popupView.findViewById(R.id.btn_cancel);
        btnCnl.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                popupWindow.dismiss();
            }
        });
    }




    public void onSwitchChanged(View view) {
        initResultTable();
    }
}
