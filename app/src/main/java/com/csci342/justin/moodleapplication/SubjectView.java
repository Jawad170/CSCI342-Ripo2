package com.csci342.justin.moodleapplication;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import layout.UploadAssignment;
import layout.ViewEnrolledStudents;
import layout.ViewGrades;
import layout.ViewResources;

public class SubjectView extends Activity implements ViewResources.onDataBaseAccessListener
{

    Intent previous;
    Connection connect;
    Bundle args;

    FragmentManager fm = getFragmentManager();
    Fragment frag;
    FrameLayout tabs;
    Protocol User;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        previous = getIntent();
        User = (Protocol) previous.getSerializableExtra("User");
        connect = (Connection) previous.getSerializableExtra("Connection");

        String subject = previous.getStringExtra("Subject Name");



        if(User.authority.equals("Teacher"))
        {
            setContentView(R.layout.activity_subject_view_teacher);
            TextView temp = (TextView) findViewById(R.id.SVT_subjectname_textview);
            temp.setText(subject);

            frag = new ViewResources();
            FragmentTransaction ft = fm.beginTransaction();

            tabs = (FrameLayout) findViewById(R.id.SVT_tabsview_framelayout);
            tabs.removeAllViews();
            ft.replace(R.id.SVT_tabsview_framelayout, frag).commit();
        }
        else
        {
            setContentView(R.layout.activity_subject_view);
            TextView temp = (TextView) findViewById(R.id.SV_subjectname_textview);
            temp.setText(subject);

            frag = new ViewResources();
            FragmentTransaction ft = fm.beginTransaction();

            tabs = (FrameLayout) findViewById(R.id.SV_tabview_framelayout);
            tabs.removeAllViews();
            ft.replace(R.id.SV_tabview_framelayout, frag).commit();
        }
    }

    public void switchToViewResources(View v)
    {
        frag = new ViewResources();
        FragmentTransaction ft = fm.beginTransaction();
        tabs.removeAllViews();


        args = new Bundle();
        args.putString("subject", ( (TextView) findViewById(R.id.SV_subjectname_textview)).getText().toString() );
        frag.setArguments(args);

        ft.replace(R.id.SV_tabview_framelayout, frag).commit();
    }

    public void UploadNewResource(View v)
    {
        DBHandler_Resources db = new DBHandler_Resources(this);
        String auth = User.authority;

        String name = "null";

        if ( auth.equals("Teacher") ) name = ((EditText)findViewById(R.id.UR_ResourceName_edittext)).getText().toString();
        else name = ((EditText)findViewById(R.id.UR_ResourceName_edittext)).getText().toString();

        //args = getArguments();
        //String subject = args.getString("subject");
        String subject = ((TextView) findViewById(R.id.SVT_subjectname_textview)).getText().toString();
        //String subject = ((TextView) findViewById(R.id.SV_subjectname_textview)).getText().toString();

        db.addResource(name, subject);
    }

    public void UploadNewGrade(View v)
    {
        DBHandler_Grades db = new DBHandler_Grades(this);
        String auth = User.authority;

        String name = ((TextView)findViewById(R.id.VESD_textView_studentName)).getText().toString();
        String gradable = ((EditText)findViewById(R.id.VESD_editText_Gradable)).getText().toString();
        int grade = Integer.parseInt(((EditText) findViewById(R.id.VESD_editText_Gradable)).getText().toString());
        String subject = ((TextView) findViewById(R.id.SVT_subjectname_textview)).getText().toString();

        db.addGrade(name, subject, gradable, grade);
    }

    public void switchToViewResourcesTeacher(View v)
    {
        frag = new ViewResources();
        FragmentTransaction ft = fm.beginTransaction();
        tabs.removeAllViews();

        Bundle args = new Bundle();
        args.putString("subject", ((TextView) findViewById(R.id.SVT_subjectname_textview)).getText().toString());
        frag.setArguments(args);

        ft.replace(R.id.SVT_tabsview_framelayout, frag).commit();
        //GetFromDatabase();
    }

    @Override
    public void GetFromDatabase(ListView LV)
    {
        DBHandler_Resources db = new DBHandler_Resources(this);

        String auth = User.authority;

        String subject = "null";

        if ( auth.equals("Teacher") ) subject = ((TextView) findViewById(R.id.SVT_subjectname_textview)).getText().toString();
        else subject = ((TextView) findViewById(R.id.SV_subjectname_textview)).getText().toString();

        //ListView lv = (ListView) findViewById(R.id.VR_infolist_listview);
        ListView lv = LV;
        List<String> myList = db.getAllResources(subject);

        ArrayAdapter<String> myarrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myList);
        lv.setAdapter(myarrayAdapter);
        lv.setTextFilterEnabled(true);

    }

    public void GetGradesFromDataBase(ListView LV, String student)
    {
        DBHandler_Grades db = new DBHandler_Grades(this);


        String auth = User.authority;

        String subject = "null";

        if ( auth.equals("Teacher") ) subject = ((TextView) findViewById(R.id.SVT_subjectname_textview)).getText().toString();
        else subject = ((TextView) findViewById(R.id.SV_subjectname_textview)).getText().toString();

        String name = student;

        ListView lv = LV;
        List<String>  myList  = db.getMyGradables(subject, name);
        List<Integer> myList2 = db.getMyGrades(subject, name);
        List<String> printMe = new ArrayList<String>();

        for (int i = 0; i < myList.size(); i++ )
        {
            String newStringToPrint = "[" + myList.get(i) + "] --> " + myList2.get(i) + " ";
            printMe.add(newStringToPrint);
        }

        ArrayAdapter<String> myarrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, printMe);
        lv.setAdapter(myarrayAdapter);
        lv.setTextFilterEnabled(true);
    }

    public void switchToViewGrades(View v)
    {
        frag = new ViewGrades();
        FragmentTransaction ft = fm.beginTransaction();
        tabs.removeAllViews();
        ft.replace(R.id.SV_tabview_framelayout, frag).commit();
    }

    public void switchToUploadAssignment(View v)
    {
        frag = new UploadAssignment();
        FragmentTransaction ft = fm.beginTransaction();
        tabs.removeAllViews();
        ft.replace(R.id.SV_tabview_framelayout, frag).commit();
    }

    public void switchToUploadResources(View v)
    {
        frag = new UploadResources();
        FragmentTransaction ft = fm.beginTransaction();
        tabs.removeAllViews();
        ft.replace(R.id.SVT_tabsview_framelayout, frag).commit();
    }

    public void switchToViewEnrolledStudents(View v)
    {
        frag = new ViewEnrolledStudents();
        FragmentTransaction ft = fm.beginTransaction();
        tabs.removeAllViews();
        ft.replace(R.id.SVT_tabsview_framelayout, frag).commit();

    }
}
