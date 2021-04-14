package com.example.notifyhostilephonecalls.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DBSettingsHandler extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "settingsValues.db";
    private static final String TAG = DBSettingsHandler.class.getName();
    private static final int DATABASE_VERSION = 1;
    private static volatile DBSettingsHandler sInstance = null;

    private DBSettingsHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // helper won't create the database file until we first open it
        SQLiteDatabase db = getWritableDatabase();
        // onConfigure isn't calling in android 2.3
        db.execSQL("PRAGMA foreign_keys=ON");
    }

    @Nullable
    public static DBSettingsHandler getInstance(Context context)
    {
        if (sInstance == null)
        {
            synchronized (DBSettingsHandler.class)
            {
                if (sInstance == null)
                {

                    sInstance = new DBSettingsHandler(context.getApplicationContext());

                }
            }
        }
        return sInstance;
    }


    public static void invalidateCache()
    {
        if (sInstance != null)
        {
            synchronized (DBSettingsHandler.class)
            {
                if (sInstance != null)
                {
                    sInstance.close();
                    sInstance = null;
                }
            }
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(SettingsTable.Statement.CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1)
    {
        if (i != i1)
        {
            db.execSQL("DROP TABLE IF EXISTS " + SettingsTable.NAME);
            onCreate(db);
        }
    }


    @Override
    public void onConfigure(SQLiteDatabase db)
    {
        super.onConfigure(db);
        db.execSQL("PRAGMA foreign_keys=ON");
    }






    // Selects settings by name
    @Nullable
    private SettingsItemCursorWrapper getSettings(@NonNull String name)
    {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(SettingsTable.Statement.SELECT_BY_NAME, new String[]{name});

        return (validate(cursor) ? new SettingsItemCursorWrapper(cursor) : null);
    }

    // Selects value of settings by name
    @Nullable
    public String getSettingsValue(@NonNull String name)
    {
        SettingsItemCursorWrapper cursor = getSettings(name);
        if (cursor != null)
        {
            SettingsItem item = cursor.getSettings();
            cursor.close();
            return item.value;
        }
        return null;
    }

    // Sets value of settings with specified name
    public boolean setSettingsValue(@NonNull String name, @NonNull String value)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SettingsTable.Column.VALUE, value);
        // try to update value
        int n = db.update(SettingsTable.NAME, values, SettingsTable.Column.NAME + " = ? ", new String[]{name});
        if (n == 0)
        {
            // try to add name/value
            values.put(SettingsTable.Column.NAME, name);
            return db.insert(SettingsTable.NAME, null, values) >= 0;
        }

        return true;
    }






    // Settings item
    private class SettingsItem
    {
        final long id;
        final String name;
        final String value;

        SettingsItem(long id, String name, String value)
        {
            this.id = id;
            this.name = name;
            this.value = value;
        }
    }




    // SettingsItem cursor wrapper
    private class SettingsItemCursorWrapper extends CursorWrapper
    {
        private final int ID;
        private final int NAME;
        private final int VALUE;

        SettingsItemCursorWrapper(Cursor cursor)
        {
            super(cursor);
            cursor.moveToFirst();
            ID = cursor.getColumnIndex(SettingsTable.Column.ID);
            NAME = cursor.getColumnIndex(SettingsTable.Column.NAME);
            VALUE = cursor.getColumnIndex(SettingsTable.Column.VALUE);
        }

        SettingsItem getSettings()
        {
            long id = getLong(ID);
            String name = getString(NAME);
            String value = getString(VALUE);
            return new SettingsItem(id, name, value);
        }
    }



    // Table of settings
    private static class SettingsTable
    {
        static final String NAME = "settings";

        static class Column
        {
            static final String ID = "_id";
            static final String NAME = "name";
            static final String VALUE = "value";
        }

        static class Statement
        {
            static final String CREATE = "CREATE TABLE " + SettingsTable.NAME + "(" + Column.ID + " INTEGER PRIMARY KEY NOT NULL, " + Column.NAME + " TEXT NOT NULL, " + Column.VALUE + " TEXT " + ")";

            static final String SELECT_BY_NAME = "SELECT * " + " FROM " + SettingsTable.NAME + " WHERE " + Column.NAME + " = ? ";
        }
    }



    // Closes cursor if it is empty and returns false
    private boolean validate(Cursor cursor)
    {
        if (cursor == null || cursor.isClosed()) return false;
        if (cursor.getCount() == 0)
        {
            cursor.close();
            return false;
        }
        return true;
    }
}
