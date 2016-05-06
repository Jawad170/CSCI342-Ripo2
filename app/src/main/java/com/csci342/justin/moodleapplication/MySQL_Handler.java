package com.csci342.justin.moodleapplication;

import android.os.StrictMode;

import java.sql.*;
import java.sql.Connection;
import java.util.ArrayList;

/**
 * Created by Jawad on 5/6/2016.
 */
public class MySQL_Handler
{

    private static final String url = "jdbc:mysql://192.168.1.7:3306/csci342";
    private static final String user = "csci342username";
    private static final String pass = "1234";

    //MAIN IS USED ONLY TO TEST THE GET/SET FUNCTIONS, REMOVE FOR FINAL VERSION.
    public static void main(String[] args)
    {
        ///TEST GETTING ANNOUNCEMENTS
        String[] testAnnouncements;
        testAnnouncements = getAnnouncements("CSCI222");
        for ( int i = 0; i < testAnnouncements.length; i++ )
        { System.out.println(testAnnouncements[i]); }

        ///TEST ADDING GRADE
        //addGrade("CSCI222", "Justin", "Deliverable 1", 4, 5);

        ///TEST GETTING GRADES
        String[] testGrades;
        testGrades = getGrades("CSCI222", "Justin");
        for ( int i = 0; i < testGrades.length; i++ )
        { System.out.println(testGrades[i]); }


    }

    //Connects to database and returns a Statement object ready to execute sql.
    private static Statement startConnection()
    {
        //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        //StrictMode.setThreadPolicy(policy);
        Statement st = null;

        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, user, pass);
            st = con.createStatement();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        finally
        {
            return st;
        }
    }



    //Returns in this format: [DATE] ANNOUNCEMENT
    public static String[] getAnnouncements(String Subject)
    {
        ArrayList<String> AList = new ArrayList<String>();
        String[] announcements = null;

        try
        {
            String sqlQuery = "SELECT * FROM `tbl_announcements` WHERE Subject = \"" + Subject + "\"";
            ResultSet rs = startConnection().executeQuery(sqlQuery);

            while (rs.next())
            {
                AList.add("[" + rs.getDate(3).toString() + "] " + rs.getString(2));
            }

            announcements = new String[AList.size()];
            for ( int i = 0; i < announcements.length; i++)
            {
                announcements[i] = AList.get(i);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            return announcements;
        }

    }

    //Returns only announcement text
    public static String[] getAnnouncements_NoDate(String Subject)
    {
        ArrayList<String> AList = new ArrayList<String>();
        String[] announcements = null;

        try
        {
            String sqlQuery = "SELECT * FROM `tbl_announcements` WHERE Subject = \"" + Subject + "\"";
            ResultSet rs = startConnection().executeQuery(sqlQuery);

            while (rs.next())
            {
                AList.add(rs.getString(2));
            }

            announcements = new String[AList.size()];
            for ( int i = 0; i < announcements.length; i++)
            {
                announcements[i] = AList.get(i);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            return announcements;
        }

    }

    public static String[] getGrades(String Subject, String Student)
    {

        ArrayList<String> AList = new ArrayList<String>();
        String[] grades = null;

        try
        {
            String sqlQuery = "SELECT * FROM `tbl_grades` WHERE Subject = \"" + Subject + "\" AND Student = \"" + Student + "\"";
            ResultSet rs = startConnection().executeQuery(sqlQuery);

            while (rs.next())
            {
                AList.add("[" + rs.getString(3) + "]  " + Integer.toString(rs.getInt(4)) + " / " + Integer.toString(rs.getInt(5)));
            }

            grades = new String[AList.size()];
            for ( int i = 0; i < grades.length; i++)
            {
                grades[i] = AList.get(i);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            return grades;
        }

    }

    public static void addGrade(String Subject, String Student, String GradedItem, int grade, int max)
    {

        try
        {
            String sqlQuery = "INSERT INTO `tbl_grades` (`Subject`, `Student`, `Graded_Item`, `Grade_Achieved`, `Grade_Max`) VALUES ('"
            + Subject + "', '" + Student + "', '" + GradedItem + "', '" + grade + "', '" + max + "');";
            startConnection().execute(sqlQuery);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
