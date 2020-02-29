package com.example.whereismystuff;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    DatabaseHelper dbHelper;
    String dateFromDatePickerForWhen = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DatabaseHelper(this);
  //      dbHelper.onUpgrade(dbHelper.getWritableDatabase(), 1,1);


        setInitialParams();
        initResultTable();
    }


    private void doMySearch(String query) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor;
        Switch sw_showall = findViewById(R.id.sw_showAll);
        if (sw_showall.isChecked()) {
            cursor = db.query(
                    DatabaseContract.DatabaseEntry.TABLE_NAME,   // The table to query
                    null,          // The array of columns to return (pass null to get all)
                    " instr(" + DatabaseContract.DatabaseEntry.COLUMN_NAME_WHAT + " , ?) OR instr(" + DatabaseContract.DatabaseEntry.COLUMN_NAME_TOWHOM +
                            " ,?) OR instr(" + DatabaseContract.DatabaseEntry.COLUMN_NAME_ISMONEY + ",?)" + " OR instr( " + DatabaseContract.DatabaseEntry.COLUMN_NAME_CLOSED + ",?)"
                            + "OR instr(" + DatabaseContract.DatabaseEntry.COLUMN_NAME_WHEN + ", ?)",  // The columns for the WHERE clause
                    new String[]{query, query, query, query, query},          // The values for the WHERE clause
                    null,
                    null,                   // don't group the rows
                    null,                   // don't filter by row groups
                    null               // The sort order
            );
        }else {
            cursor = db.query(
                    DatabaseContract.DatabaseEntry.TABLE_NAME,   // The table to query
                    null,          // The array of columns to return (pass null to get all)
                    DatabaseContract.DatabaseEntry.COLUMN_NAME_CLOSED + "=? AND (" +
                            " instr(" + DatabaseContract.DatabaseEntry.COLUMN_NAME_WHAT + " , ?) OR instr(" + DatabaseContract.DatabaseEntry.COLUMN_NAME_TOWHOM +
                            " ,?) OR instr(" + DatabaseContract.DatabaseEntry.COLUMN_NAME_ISMONEY + ",?)" + " OR instr( " + DatabaseContract.DatabaseEntry.COLUMN_NAME_CLOSED + ",?)"
                            + "OR instr(" + DatabaseContract.DatabaseEntry.COLUMN_NAME_WHEN + ", ?)  )",  // The columns for the WHERE clause
                    new String[]{String.valueOf(0), query, query, query, query, query},          // The values for the WHERE clause
                    null,
                    null,                   // don't group the rows
                    null,                   // don't filter by row groups
                    null               // The sort order
            );
        }
        updateTableFromQuery(cursor);
    }

    private void calculateLendOutMoney() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseContract.DatabaseEntry.TABLE_NAME,   // The table to query
                new String[] {DatabaseContract.DatabaseEntry.COLUMN_NAME_WHAT, DatabaseContract.DatabaseEntry.COLUMN_NAME_ISMONEY, DatabaseContract.DatabaseEntry.COLUMN_NAME_CLOSED},           // The array of columns to return (pass null to get all)
                DatabaseContract.DatabaseEntry.COLUMN_NAME_ISMONEY + "=?" + " AND ( " + DatabaseContract.DatabaseEntry.COLUMN_NAME_CLOSED + "=?)",  // The columns for the WHERE clause
                new String[] {String.valueOf(0),String.valueOf(0)},          // The values for the WHERE clause
                null,
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );
        // note that the order of the money identifiers is important, substring before string will not work
        // DO: euro|eur ; DONOT: eur|euro
        Pattern MY_PATTERN = Pattern.compile("\\D*(\\d*)\\.?\\s?(\\d*,?\\d*)\\s*(?:€|EURO|EUR|euro|eur)\\s*(\\d*)\\.?\\s?(\\d*,?\\d*)\\s*.*");
        float moneyVal = 0;
        String foundValue;
        while(cursor.moveToNext()) {
            Matcher m = MY_PATTERN.matcher(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.DatabaseEntry.COLUMN_NAME_WHAT)));
            while (m.find()) {
                foundValue = m.group(1) + m.group(2);
                if (foundValue.equals("")) foundValue = m.group(3) + m.group(4);
                if (!foundValue.equals("")) moneyVal += Float.parseFloat(foundValue.replace(',', '.'));
            }
        }
        cursor.close();
        TextView tv = findViewById(R.id.tv_ausstand);
        foundValue = String.valueOf(moneyVal).replace('.', ',');
        tv.setText("Ausstand: " + foundValue + " €");
    }


    private void initResultTable() {
        calculateLendOutMoney();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
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



       updateTableFromQuery(cursor);
    }

    private void updateTableFromQuery(Cursor cursor) {
        TableLayout ll = findViewById(R.id.table_db_results);
        ll.removeAllViewsInLayout();


        while(cursor.moveToNext()) {

            final TableRow row= new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);

            Resources res = getResources();
            Display disp = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            disp.getSize(size);
            int intIdWidth = (int) res.getDimension(R.dimen.id_width);
            int intPadding = (int) res.getDimension(R.dimen.padding);
            int maxWidth = (size.x -  intIdWidth)/ 4;

            TextView tv_id = new TextView(this);
            tv_id.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.DatabaseEntry._ID)));
            tv_id.setWidth(intIdWidth);
            tv_id.setPadding(0, 0,0,0);
            tv_id.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            row.addView(tv_id);

            TextView tv_what = new TextView(this);
            tv_what.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.DatabaseEntry.COLUMN_NAME_WHAT)));
            tv_what.setMaxWidth(maxWidth);
            tv_what.setPadding(intPadding, 0,0,0);
            row.addView(tv_what);

            TextView tv_towhom = new TextView(this);
            tv_towhom.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.DatabaseEntry.COLUMN_NAME_TOWHOM)));
            tv_towhom.setMaxWidth(maxWidth);
            tv_towhom.setPadding(intPadding, 0,0,0);
            row.addView(tv_towhom);

            TextView tv_when = new TextView(this);
            tv_when.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.DatabaseEntry.COLUMN_NAME_WHEN)));
            tv_when.setMaxWidth(maxWidth);
            tv_when.setPadding(intPadding, 0,0,0);
            row.addView(tv_when);

            TextView tv_returned = new TextView(this);
            String returnedString = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.DatabaseEntry.COLUMN_NAME_CLOSED));
            if (returnedString.equals("0")){
                tv_returned.setText("---");
            }else {
                tv_returned.setText(returnedString);
            }
            tv_returned.setMaxWidth(maxWidth);
            tv_returned.setPadding(intPadding, 0,0,0);
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
                    final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

                    EditText  editWhat = popupView.findViewById(R.id.editTextWhat);
                    TextView tmpWhat = (TextView) row.getChildAt(1);
                    editWhat.setText(tmpWhat.getText());
                    EditText  editToWhom = popupView.findViewById(R.id.editTextWhom);
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




                    Button login = popupView.findViewById(R.id.btn_done);
                    login.setOnClickListener(new Button.OnClickListener(){

                        @Override
                        public void onClick(View arg0) {
                            TextView tmpID = (TextView) row.getChildAt(0);
                            int id = Integer.parseInt(String.valueOf(tmpID.getText()));

                            EditText  editWhat = popupView.findViewById(R.id.editTextWhat);
                            TextView tmpWhat = (TextView) row.getChildAt(1);
                            tmpWhat.setText(editWhat.getText());
                            EditText  editToWhom = popupView.findViewById(R.id.editTextWhom);
                            TextView tmpToWhom = (TextView) row.getVirtualChildAt(2);
                            tmpToWhom.setText(editToWhom.getText());
                            EditText  editWhen = popupView.findViewById(R.id.editText_when);
                            TextView tmpWhen = (TextView) row.getVirtualChildAt(3);
                            tmpWhen.setText(editWhen.getText());
                            EditText  editReturned = popupView.findViewById(R.id.editText_returned);
                            String returnedString = editReturned.getText().toString();
                            if (returnedString.equals("---")) returnedString = "0";
                            TextView tmpReturned = (TextView) row.getVirtualChildAt(4);
                            tmpReturned.setText(editReturned.getText());
                            DatabaseEntry item = new DatabaseEntry(editWhat.getText().toString(), editToWhom.getText().toString(), editWhen.getText().toString(), returnedString );
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

                    Button btnDel = popupView.findViewById(R.id.btn_del);
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


                    Button btnCnl = popupView.findViewById(R.id.btn_cancel);
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
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

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

        Button btnDel = popupView.findViewById(R.id.btn_del);
        btnDel.setEnabled(false);

        Button login = popupView.findViewById(R.id.btn_done);
        login.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View arg0) {

                popupWindow.setContentView(inflater.inflate(R.layout.popup_window, null, false));
                EditText  editWhom = popupView.findViewById(R.id.editTextWhom);
                EditText  editWhat = popupView.findViewById(R.id.editTextWhat);

                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String datetime = now.format(formatter);
                DatabaseEntry item = new DatabaseEntry( editWhat.getText().toString(), editWhom.getText().toString(), datetime, "0");

                SQLiteDatabase db = dbHelper.getWritableDatabase();

                // Create a new map of values, where column names are the keys
                ContentValues values = DatabaseEntry.toContentValues(item);

                // Insert the new row, returning the primary key value of the new row
                db.insert(DatabaseContract.DatabaseEntry.TABLE_NAME, null,values);

        //        addToTable(values);
                initResultTable();


                popupWindow.dismiss();
            }

        });

        Button btnCnl = popupView.findViewById(R.id.btn_cancel);
        btnCnl.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                popupWindow.dismiss();
            }
        });


    }




    public void onSwitchChanged(View view) {
        final SearchView sv = findViewById(R.id.search);
        doMySearch(sv.getQuery().toString());

    }

    private void setInitialParams(){
        Resources res = getResources();
        Display disp = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        disp.getSize(size);
        int intIdWidth = (int) res.getDimension(R.dimen.id_width);
        int maxWidth = (size.x -  intIdWidth)/ 4;

        TextView tv = findViewById(R.id.what);
        tv.setWidth(maxWidth);

        tv = findViewById(R.id.towhom);
        tv.setWidth(maxWidth);



        final SearchView sv = findViewById(R.id.search);

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                doMySearch(query);
                sv.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                doMySearch(newText);
                return true;
            }


        });
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        public String dateFromDatePicker;
        EditText editText;

        public DatePickerFragment(EditText editText){
            super();
            this.editText = editText;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            month++; //for some reason month count starts with zero
            String dateFormatted = year + "-";
            if (month<10) dateFormatted += "0";
            dateFormatted += month + "-";
            if (day<10) dateFormatted += "0";
            dateFormatted += day;
            editText.setText(dateFormatted);
        }
    }

    public void showDatePickerDialog(View view) {
        EditText editText = (EditText) view;

        DatePickerFragment newFragment = new DatePickerFragment(editText);
        newFragment.show(getSupportFragmentManager(), "datePicker");
        //editText.setText("a");
        //editText.setText(newFragment.dateFromDatePicker);
    }


}
