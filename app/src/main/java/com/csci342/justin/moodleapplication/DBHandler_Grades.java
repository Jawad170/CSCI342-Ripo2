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

public class DBHandler_Grades extends SQLiteOpenHelper
{

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "StudentInfo";

    // Contacts table name
    private static final String TABLE_RES = "grades";

    // Shops Table Columns names
    private static final String KEY_ID          = "id"      ;
    private static final String KEY_NAME        = "name"    ;
    private static final String KEY_SUBJECT     = "subject" ;
    private static final String KEY_GRADABLE    = "gradable";
    private static final String KEY_GRADE       = "grade;"  ;

    public DBHandler_Grades(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_RES + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_SUBJECT + " TEXT" + KEY_GRADABLE + " TEXT" + KEY_GRADE + " INTEGER" + ")";
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
    public void addResource(String studentName, String subject, String gradable, int grade )
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_NAME,        studentName);
        values.put(KEY_SUBJECT,     subject)     ;
        values.put(KEY_GRADABLE,    gradable)    ;
        values.put(KEY_GRADE, grade)       ;

        // Inserting Row
        db.insert(TABLE_RES, null, values);
        db.close(); // Closing database connection
    }

    // Getting All Grades by subject for student name
    public List<Integer> getMyGrades(String subject, String name)
    {
        List<Integer> resList = new ArrayList<Integer>();

        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_RES + " WHERE " + KEY_SUBJECT + " = \"" + subject +"\" AND " + KEY_SUBJECT + " = \"" + name +"\"";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst())
        {
            do
            {
                Integer grade = cursor.getInt(4);

                resList.add(grade);

            } while (cursor.moveToNext());
        }

        // return contact list
        return resList;
    }


    // Getting All GradedItem Names by subject for student name
    public List<String> getMyGradables(String subject, String name)
    {
        List<String> resList = new ArrayList<String>();

        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_RES + " WHERE " + KEY_SUBJECT + " = \"" + subject +"\" AND " + KEY_SUBJECT + " = \"" + name +"\"";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst())
        {
            do
            {
                String gradable = cursor.getString(3);

                resList.add(gradable);

            } while (cursor.moveToNext());
        }

        // return contact list
        return resList;
    }
}