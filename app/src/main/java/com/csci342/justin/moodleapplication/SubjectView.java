package com.csci342.justin.moodleapplication;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import layout.UploadAssignment;
import layout.ViewEnrolledStudents;
import layout.ViewGrades;
import layout.ViewResources;

public class SubjectView extends Activity {

    Intent previous;
    Connection connect;

    FragmentManager fm = getFragmentManager();
    Fragment frag;
    FrameLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        previous = getIntent();
        connect = (Connection) previous.getSerializableExtra("Connection");
        String subject = previous.getStringExtra("Subject Name");



        if(connect.user.authority.equals("Teacher"))
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


        Bundle args = new Bundle();
        args.putString("subject", ( (TextView) findViewById(R.id.SVT_subjectname_textview)).getText().toString() );
        frag.setArguments(args);

        ft.replace(R.id.SV_tabview_framelayout, frag).commit();
    }

    public void switchToViewResourcesTeacher(View v)
    {
        frag = new ViewResources();
        FragmentTransaction ft = fm.beginTransaction();
        tabs.removeAllViews();

        Bundle args = new Bundle();
        args.putString("subject", ( (TextView) findViewById(R.id.SVT_subjectname_textview)).getText().toString() );
        frag.setArguments(args);

        ft.replace(R.id.SVT_tabsview_framelayout, frag).commit();
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
