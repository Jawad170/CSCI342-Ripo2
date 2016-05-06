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

    public static void main(String[] args)
    {
        String[] testAnnouncements;
        testAnnouncements = getAnnouncements("CSCI222");

        for ( int i = 0; i < testAnnouncements.length; i++ )
        {
            System.out.println(testAnnouncements[i]);
        }
    }

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

}
