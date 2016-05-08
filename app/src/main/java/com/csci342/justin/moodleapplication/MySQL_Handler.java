package com.csci342.justin.moodleapplication;

import android.os.StrictMode;

import java.sql.*;
import java.sql.Date;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Jawad on 5/6/2016.
 */
public class MySQL_Handler
{

    private static final String url =  "jdbc:mysql://172.18.16.94:3306/csci342";
    private static final String user = "csci342username";
    private static final String pass = "1234";

    private static Connection con;

    //MAIN IS USED ONLY TO TEST THE GET/SET FUNCTIONS, REMOVE FOR FINAL VERSION.
    public static void main(String[] args)
    {
        //..................................................................
        ///TEST ADD ANNOUNCEMENT
        //addAnnouncement("CSCI222", "Ignore This Announcement");

        ///TEST GETTING ANNOUNCEMENTS
            //String[] testAnnouncements;
            //System.out.println(" ");
            //testAnnouncements = getAnnouncements("CSCI222");
            //for ( int i = 0; i < testAnnouncements.length; i++ )
            //{ System.out.println(testAnnouncements[i]); }
        //......ANNOUNCEMENTS.................................................


        //......GRADES.........................................................
        ///TEST ADDING GRADE
        //addGrade("CSCI222", "Justin", "Deliverable 1", 4, 5);

        ///TEST GETTING GRADES
            //String[] testGrades;
            //System.out.println(" ");
            //testGrades = getGrades("CSCI222", "Justin");
            //for ( int i = 0; i < testGrades.length; i++ )
            //{ System.out.println(testGrades[i]); }
        //.....................................................................


        //......USER INFO......................................................
        ///TEST UPDATING PERSONAL INFO
        //

        ///TEST GETS
        //
            //System.out.println(" ");
            //System.out.println("Jawad's Hash            =  " + getPassHash("Jawad") + "\n");
            //System.out.println("Jawad's Authority       =  " + getAuthority("Jawad") + "\n");
            //System.out.println("Jawad's Token           =  " + getToken("Jawad") + "\n");
            //setToken("Jawad", 5566);
            //System.out.println("Jawad's Updated Token   =  " + getToken("Jawad") + "\n");
            //setToken("Jawad", 0);
            //System.out.println("Jawad's Reset Token     =  " + getToken("Jawad") + "\n");

            //System.out.println("Jawad's PersonalInfo:  ");
            //String[] PI = getPersonalInfo("Jawad");
            //System.out.println(PI[0]);
            //System.out.println(PI[1]);
            //System.out.println(PI[2]);
            //System.out.println(PI[3] + "\n");

            //setPersonalInfo("Jawad", "Bobo", "Bobberson", "Teacher", "Bob@Bobmail.com", "+1-247-5551337", "Lala Land");
            //System.out.println("Jawad's Updated PersonalInfo:  ");
            //PI = getPersonalInfo("Jawad");
            //System.out.println(PI[0]);
            //System.out.println(PI[1]);
            //System.out.println(PI[2]);
            //System.out.println(PI[3] + "\n");


            //setPersonalInfo("Jawad", "Jawad", "Jandali Refai", "Student", "jjr318@uowmail.edu.au", "+971-50-1620709", "UOWD, Knowledge Village, Dubai");
            //System.out.println("Jawad's Reset PersonalInfo:  ");
            //PI = getPersonalInfo("Jawad");
            //System.out.println(PI[0]);
            //System.out.println(PI[1]);
            //System.out.println(PI[2]);
            //System.out.println(PI[3] + "\n");

        //.....................................................................

        ///TESTING GET ALL SUBJECTS
            //String Subjects[] = getALLSujects();
            //for (int i = 0; i < Subjects.length; i++ )
            //{ System.out.println(Subjects[i]); }
        //.....................................................................


        //System.out.println("\n Is Justin Enrolled in CSCI323? " + IsEnrolledInSubject("Justin", "CSCI323"));
        //System.out.println("\n Is Justin Enrolled in it's Tutorial? " + IsEnrolledInTutorial("Justin", "CSCI323"));


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
            con = DriverManager.getConnection(url, user, pass);
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
            try {if ( con != null ) con.close();}
            catch (Exception e) { }
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
            try {if ( con != null ) con.close();}
            catch (Exception e) { }
            return announcements;
        }

    }

    //Adds announcement to database to the specified subject with todays date
    public static boolean addAnnouncement(String Subject, String AnnouncementText)
    {
        String today = getToday();

        try
        {
            String sqlQuery = "INSERT INTO `tbl_announcements` (`Subject`, `Announcement`, `Date`) VALUES ('"
                    + Subject + "', '" + AnnouncementText + "', '" + today + "');";
            startConnection().execute(sqlQuery);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        finally
        {
            try {if ( con != null ) con.close();}
            catch (Exception e) { }
        }
    }

    //Returns Grade String Array in this format: [GRADED ITEM] ## / ##
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
            try {if ( con != null ) con.close();}
            catch (Exception e) { }
            return grades;
        }
    }

    //Adds grade to student for specified subject/graded item
    public static boolean addGrade(String Subject, String Student, String GradedItem, int grade, int max)
    {
        try
        {
            String sqlQuery = "INSERT INTO `tbl_grades` (`Subject`, `Student`, `Graded_Item`, `Grade_Achieved`, `Grade_Max`) VALUES ('"
            + Subject + "', '" + Student + "', '" + GradedItem + "', '" + grade + "', '" + max + "');";
            startConnection().execute(sqlQuery);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        finally
        {
            try {if ( con != null ) con.close();}
            catch (Exception e) { }
        }
    }

    //gets password hash from database for specific username
    public static String getPassHash(String Username)
    {
        String hash = null;

        try
        {
            String sqlQuery = "SELECT PassH FROM `tbl_users` WHERE Username = \"" + Username + "\"";
            ResultSet rs = startConnection().executeQuery(sqlQuery);

            while (rs.next())
            {
                hash = rs.getString(1);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try {if ( con != null ) con.close();}
            catch (Exception e) { }
            return hash;
        }
    }

    //gets authority of specific username
    public static String getAuthority(String Username)
    {
        String auth = null;

        try
        {
            String sqlQuery = "SELECT Authority FROM `tbl_users` WHERE Username = \"" + Username + "\"";
            ResultSet rs = startConnection().executeQuery(sqlQuery);

            while (rs.next())
            {
                auth = rs.getString(1);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try {if ( con != null ) con.close();}
            catch (Exception e) { }
            return auth;
        }
    }

    //gets current token from database for specific username
    public static int getToken(String Username)
    {
        int token = -1;

        try
        {
            String sqlQuery = "SELECT Token FROM `tbl_users` WHERE Username = \"" + Username + "\"";
            ResultSet rs = startConnection().executeQuery(sqlQuery);

            while (rs.next())
            {
                token = rs.getInt(1);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try {if ( con != null ) con.close();}
            catch (Exception e) { }
            return token;
        }
    }

    //updates token for specific username
    public static boolean setToken(String Username, int newToken)
    {
        try
        {
            String sqlQuery = "UPDATE tbl_users SET Token = " + newToken + " WHERE Username = \"" + Username + "\"";
            startConnection().execute(sqlQuery);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        finally
        {
            try {if ( con != null ) con.close();}
            catch (Exception e) { }
        }
    }

    public static String checkLoggedIn(int token)
    {
        String username = null;

        try
        {
            String sqlQuery = "SELECT Username FROM `tbl_users` WHERE Token = " + token + "";
            ResultSet rs = startConnection().executeQuery(sqlQuery);

            while (rs.next())
            {
                username = rs.getString(1);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try {if ( con != null ) con.close();}
            catch (Exception e) { }
            return username;
        }
    }

    //Returns array of strings with single users personal info, display one per line.
    public static String[] getPersonalInfo(String Username)
    {
        String[] PI = new String[4];

        try
        {
            String sqlQuery = "SELECT * FROM `tbl_users` WHERE Username = \"" + Username + "\"";
            ResultSet rs = startConnection().executeQuery(sqlQuery);

            while (rs.next())
            {
                PI[0] = "NAME : " + rs.getString(7) + ", " + rs.getString(8) + " [" + rs.getString(3) + "]";
                PI[1] = "EMAIL: " + rs.getString(9);
                PI[2] = "PHONE: " + rs.getString(6);
                PI[3] = "ADDRS: " + rs.getString(5);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try {if ( con != null ) con.close();}
            catch (Exception e) { }
            return PI;
        }
    }

    //Updates entry in database for specified username with provided details (all strings)
    public static boolean setPersonalInfo(String Username , String FirstName, String LastName,
                                          String Authority, String Email    , String Phone   , String Address  )
    {
        try
        {
            String sqlQuery = "UPDATE tbl_users SET First_Name = \"" + FirstName +
                                               "\", Last_Name  = \"" + LastName  +
                                               "\", Authority  = \"" + Authority +
                                               "\", Email      = \"" + Email     +
                                               "\", Phone      = \"" + Phone     +
                                               "\", Address    = \"" + Address   +
                                               "\" WHERE Username = \"" + Username + "\"";

            startConnection().execute(sqlQuery);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        finally
        {
            try {if ( con != null ) con.close();}
            catch (Exception e) { }
        }
    }

    //Returns array of ALL subjects possible (CSCI222, ARTS015, etc...)
    public static String[] getALLSubjects()
    {
        ArrayList<String> AList = new ArrayList<String>();
        String[] subjects = null;

        try
        {
            String sqlQuery = "SELECT * FROM `tbl_listofsubjects` ORDER BY `Subject` ASC";
            ResultSet rs = startConnection().executeQuery(sqlQuery);

            while (rs.next())
            {
                AList.add(rs.getString(1));
            }

            subjects = new String[AList.size()];
            for ( int i = 0; i < subjects.length; i++)
            {
                subjects[i] = AList.get(i);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try {if ( con != null ) con.close();}
            catch (Exception e) { }
            return subjects;
        }
    }

    //Returns TRUE if provided student name is enrolled in specified subject
    public static boolean IsEnrolledInSubject(String Student, String Subject)
    {
        int type = -1;

        try
        {
            String sqlQuery = "SELECT Type FROM `tbl_enrollments` WHERE Student = \"" + Student + "\" AND Enrolled = \"" + Subject + "\"";
            ResultSet rs = startConnection().executeQuery(sqlQuery);

            while (rs.next())
            {
                int newType = rs.getInt(1);
                if ( newType == 1 ) type = 1;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try {if ( con != null ) con.close();}
            catch (Exception e) { }
            if ( type == 1) return true;
            else return false;
        }
    }

    //Returns TRUE if provided student name is enrolled in specified subject tutorial
    public static boolean IsEnrolledInTutorial(String Student, String Subject)
    {
        int type = -1;

        try
        {
            String sqlQuery = "SELECT Type FROM `tbl_enrollments` WHERE Student = \"" + Student + "\" AND Enrolled = \"" + Subject + "\"";
            ResultSet rs = startConnection().executeQuery(sqlQuery);

            while (rs.next())
            {
                int newType = rs.getInt(1);
                if ( newType == 2 ) type = 2;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try {if ( con != null ) con.close();}
            catch (Exception e) { }
            if ( type == 2) return true;
            else return false;
        }
    }

    public static boolean enrollInSubject(String Student, String Subject)
    {
        try
        {
            String sqlQuery = "INSERT INTO `tbl_enrollments` (`Student`, `Enrolled`, `Type`) VALUES ('"
                    + Student + "', '" + Subject + "', '1');";
            startConnection().execute(sqlQuery);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        finally
        {
            try {if ( con != null ) con.close();}
            catch (Exception e) { }
        }
    }

    public static String[] getEnrolledSubjects(String Student)
    {
        ArrayList<String> AList = new ArrayList<String>();
        String[] subjects = null;

        try
        {
            String sqlQuery = "SELECT Enrolled FROM `tbl_enrollments` WHERE Student = \"" + Student + "\" AND Type = 1";
            ResultSet rs = startConnection().executeQuery(sqlQuery);

            while (rs.next())
            {
                AList.add(rs.getString(1));
            }

            subjects = new String[AList.size()];
            for ( int i = 0; i < subjects.length; i++)
            {
                subjects[i] = AList.get(i);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try {if ( con != null ) con.close();}
            catch (Exception e) { }
            return subjects;
        }
    }

    public static boolean dropSubject(String Student, String Subject)
    {
        try
        {
            String sqlQuery = "DELETE FROM `tbl_enrollments` WHERE Enrolled = \""
                    + Subject  + "\" AND Student = \"" + Student + "\"";
            startConnection().execute(sqlQuery);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        finally
        {
            try {if ( con != null ) con.close();}
            catch (Exception e) { }
        }
    }

    public static boolean enrollInTutorial(String Student, String Subject)
    {
        try
        {
            String sqlQuery = "INSERT INTO `tbl_enrollments` (`Student`, `Enrolled`, `Type`) VALUES ('"
                    + Student + "', '" + Subject + "', '2');";
            startConnection().execute(sqlQuery);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        finally
        {
            try {if ( con != null ) con.close();}
            catch (Exception e) { }
        }
    }




    //RESETS ALL TABLES TO DEFAULT VALUES
    public static boolean RESET_DATABASE()
    {
        try
        {
            String NextQuery = null;
            Statement resetStatement = startConnection();

            //RESETTING ANNOUNCEMENTS TABLE
            NextQuery = "DELETE FROM tbl_announcements;";
            resetStatement.execute(NextQuery);
            NextQuery = "INSERT INTO `tbl_announcements` (`Subject`, `Announcement`, `Date`) VALUES ('CSCI203', 'Assignment due in n^2 hours.', '2016-05-05');";
            resetStatement.execute(NextQuery);
            NextQuery = "INSERT INTO `tbl_announcements` (`Subject`, `Announcement`, `Date`) VALUES ('CSCI222', 'Sample Announcement For CSCSI222.', '2016-05-03');";
            resetStatement.execute(NextQuery);
            NextQuery = "INSERT INTO `tbl_announcements` (`Subject`, `Announcement`, `Date`) VALUES ('CSCI222', 'Tutorial Room Changed to Food Court', '2016-04-17');";
            resetStatement.execute(NextQuery);
            NextQuery = "INSERT INTO `tbl_announcements` (`Subject`, `Announcement`, `Date`) VALUES ('CSCI124', 'This subject is not difficult. Stop Complainings.', '2016-04-28');";
            resetStatement.execute(NextQuery);
            NextQuery = "INSERT INTO `tbl_announcements` (`Subject`, `Announcement`, `Date`) VALUES ('CSCI015', 'Exam will be easy, calm down.', '2016-05-01');";
            resetStatement.execute(NextQuery);

            //RESETTING ENROLLMENTS TABLE
            NextQuery = "DELETE FROM tbl_enrollments;";
            resetStatement.execute(NextQuery);
            NextQuery = "INSERT INTO `tbl_enrollments` (`Student`, `Enrolled`, `Type`) VALUES ('Jawad', 'CSCI342', '1');";
            resetStatement.execute(NextQuery);
            NextQuery = "INSERT INTO `tbl_enrollments` (`Student`, `Enrolled`, `Type`) VALUES ('Jawad', 'CSCI342', '2');";
            resetStatement.execute(NextQuery);
            NextQuery = "INSERT INTO `tbl_enrollments` (`Student`, `Enrolled`, `Type`) VALUES ('Justin', 'CSCI342', '1');";
            resetStatement.execute(NextQuery);
            NextQuery = "INSERT INTO `tbl_enrollments` (`Student`, `Enrolled`, `Type`) VALUES ('Justin', 'CSCI342', '2');";
            resetStatement.execute(NextQuery);
            NextQuery = "INSERT INTO `tbl_enrollments` (`Student`, `Enrolled`, `Type`) VALUES ('Ahmed', 'CSCI342', '1');";
            resetStatement.execute(NextQuery);
            NextQuery = "INSERT INTO `tbl_enrollments` (`Student`, `Enrolled`, `Type`) VALUES ('Ahmed', 'CSCI342', '2');";
            resetStatement.execute(NextQuery);

            //RESETTING GRADES TABLE
            NextQuery = "DELETE FROM tbl_grades;";
            resetStatement.execute(NextQuery);
            NextQuery = "INSERT INTO `tbl_grades` (`Subject`, `Student`, `Graded_Item`, `Grade_Achieved`, `Grade_Max`) VALUES ('CSCI342', 'Jawad', 'Virtual Assignment 1', '8', '10');";
            resetStatement.execute(NextQuery);
            NextQuery = "INSERT INTO `tbl_grades` (`Subject`, `Student`, `Graded_Item`, `Grade_Achieved`, `Grade_Max`) VALUES ('CSCI342', 'Jawad', 'Virtual Exam', '26', '30');";
            resetStatement.execute(NextQuery);
            NextQuery = "INSERT INTO `tbl_grades` (`Subject`, `Student`, `Graded_Item`, `Grade_Achieved`, `Grade_Max`) VALUES ('CSCI342', 'Justin', 'Virtual Assignment 1', '9', '10');";
            resetStatement.execute(NextQuery);
            NextQuery = "INSERT INTO `tbl_grades` (`Subject`, `Student`, `Graded_Item`, `Grade_Achieved`, `Grade_Max`) VALUES ('CSCI342', 'Justin', 'Virtual Exam', '28', '30');";
            resetStatement.execute(NextQuery);
            NextQuery = "INSERT INTO `tbl_grades` (`Subject`, `Student`, `Graded_Item`, `Grade_Achieved`, `Grade_Max`) VALUES ('CSCI342', 'Ahmed', 'Virtual Assignment 1', '9', '10');";
            resetStatement.execute(NextQuery);
            NextQuery = "INSERT INTO `tbl_grades` (`Subject`, `Student`, `Graded_Item`, `Grade_Achieved`, `Grade_Max`) VALUES ('CSCI342', 'Ahmed', 'Virtual Exam', '27', '30');";
            resetStatement.execute(NextQuery);

            //TEMPORARY - Hardcoded reset of tokens for only 3 users.
            setToken("Justin", 0);
            setToken("Jawad", 0);
            setToken("Ahmed", 0);


            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        finally
        {
            try {if ( con != null ) con.close();}
            catch (Exception e) { }
        }
    }

    public static String getToday()
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-mm-dd");

        return format1.format(cal.getTime());
    }
}
