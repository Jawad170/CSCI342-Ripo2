package com.csci342.justin.moodleapplication;

/**
 * Created by Jawad on 4/20/2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHandler_Resources  extends SQLiteOpenHelper
{

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "shopsInfo";

    // Contacts table name
    private static final String TABLE_RES = "resources";

    // Shops Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_SUBJECT = "subject";

    public DBHandler_Resources(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_RES + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_SUBJECT + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RES);

        // Creating tables again
        onCreate(db);
    }


    // Adding new shop
    public void addResource(String resourceName, String subject)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_NAME, resourceName); // Shop Name
        values.put(KEY_SUBJECT, subject); // Shop Phone Number

        // Inserting Row
        db.insert(TABLE_RES, null, values);
        db.close(); // Closing database connection
    }

    // Getting All Resources by subject
    public List<String> getAllResources(String subject)
    {
        List<String> resList = new ArrayList<String>();

        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_RES + " WHERE " + KEY_SUBJECT + " = " + subject;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst())
        {
            do
            {
                String res = cursor.getString(0);

                resList.add(res);

            } while (cursor.moveToNext());
        }

        // return contact list
        return resList;
    }
}