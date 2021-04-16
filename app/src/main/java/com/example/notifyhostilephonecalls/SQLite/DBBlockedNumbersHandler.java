package com.example.notifyhostilephonecalls.SQLite;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.notifyhostilephonecalls.models.PhoneNumber;

import java.util.ArrayList;

public class DBBlockedNumbersHandler extends SQLiteOpenHelper
{

    // creating a constant variables for our database.
    // below variable is for our database name.
    private static final String DB_NAME_FOR_BLOCKED_NUMBERS = "hostilePhoneCalls_db_hostile_numbers";

    // below int is our database version
    private static final int DB_VERSION = 1;

    // below variable is for our table name.
    private static final String TABLE_BLOCKED_NUMBERS_NAME = "hostilePhoneNumbersBlockedNumbers";

    // below variable is for our id column.
    private static final String ID_COL = "id";

    // below variable is for phones number column
    private static final String NUMBER_COL = "number";

    // below variable id for phones rating.
    private static final String RATING_COL = "rating";


    // creating a constructor for our database handler.
    public DBBlockedNumbersHandler(Context context)
    {
        super(context, DB_NAME_FOR_BLOCKED_NUMBERS, null, DB_VERSION);
    }

    // below method is for creating a database by running a sqlite query
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // on below line we are creating
        // an sqlite query and we are
        // setting our column names
        // along with their data types.
        String query = "CREATE TABLE " + TABLE_BLOCKED_NUMBERS_NAME + " (" + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NUMBER_COL + " TEXT," + RATING_COL + " TEXT)";

        // at last we are calling a exec sql
        // method to execute above sql query
        db.execSQL(query);
    }

    // this method is use to add new course to our sqlite database.
    public void addPhoneNumber(String phoneNumber, String rating)
    {

        // on below line we are creating a variable for
        // our sqlite database and calling writable method
        // as we are writing data in our database.
        SQLiteDatabase db = this.getWritableDatabase();

        // on below line we are creating a
        // variable for content values.
        ContentValues values = new ContentValues();

        // on below line we are passing all values
        // along with its key and value pair.
        values.put(NUMBER_COL, phoneNumber);
        values.put(RATING_COL, rating);

        // after adding all values we are passing
        // content values to our table.
        db.insert(TABLE_BLOCKED_NUMBERS_NAME, null, values);

        // at last we are closing our
        // database after adding database.
        db.close();
    }

    // we have created a new method for reading all the courses.
    public ArrayList<PhoneNumber> getAllNumbers()
    {
        // on below line we are creating a
        // database for reading our database.
        SQLiteDatabase db = this.getReadableDatabase();

        // on below line we are creating a cursor with query to read data from database.
        Cursor cursorCourses = db.rawQuery("SELECT * FROM " + TABLE_BLOCKED_NUMBERS_NAME, null);

        // on below line we are creating a new array list.
        ArrayList<PhoneNumber> courseModalArrayList = new ArrayList<>();

        // moving our cursor to first position.
        if (cursorCourses.moveToFirst())
        {
            do
            {
                // on below line we are adding the data from cursor to our array list.
                courseModalArrayList.add(new PhoneNumber(cursorCourses.getString(1), cursorCourses.getString(2)));
            } while (cursorCourses.moveToNext());
            // moving our cursor to next.
        }
        // at last closing our cursor
        // and returning our array list.
        cursorCourses.close();
        db.close();
        return courseModalArrayList;
    }

    // below is the method for updating our courses
    public void updatePhoneNumber(String originalCourseName, String phoneNumber, String rating)
    {

        // calling a method to get writable database.
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // on below line we are passing all values
        // along with its key and value pair.
        values.put(NUMBER_COL, phoneNumber);
        values.put(RATING_COL, rating);

        // on below line we are calling a update method to update our database and passing our values.
        // and we are comparing it with name of our course which is stored in original name variable.
        db.update(TABLE_BLOCKED_NUMBERS_NAME, values, "number=?", new String[]{originalCourseName});
        db.close();
    }


    // below is the method for deleting our course.
    public void deletePhoneNumber(String phoneNumber)
    {

        // on below line we are creating
        // a variable to write our database.
        SQLiteDatabase db = this.getWritableDatabase();

        // on below line we are calling a method to delete our
        // course and we are comparing it with our course name.
        db.delete(TABLE_BLOCKED_NUMBERS_NAME, "number=?", new String[]{phoneNumber});
        db.close();
    }


    public void clearDatabase()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String clearDBQuery = "DELETE FROM " + TABLE_BLOCKED_NUMBERS_NAME;
        db.execSQL(clearDBQuery);
        db.close();
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BLOCKED_NUMBERS_NAME);
        onCreate(db);
    }
}