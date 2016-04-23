package com.csci342.justin.moodleapplication;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import layout.AddCalendarEvent;
import layout.ContactUs;
import layout.ViewCalendar;
import layout.ViewPolicies;

public class ViewUniversityDetails extends Activity {

    Intent previous;
    Connection connect;

    FragmentManager fm = getFragmentManager();
    Fragment frag;
    FrameLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_university_details);

        previous = getIntent();
        connect = (Connection) previous.getSerializableExtra("Connection");

        frag = new ViewCalendar();
        FragmentTransaction ft = fm.beginTransaction();

        tabs = (FrameLayout) findViewById(R.id.VUD_tabsview_framelayout);
        tabs.removeAllViews();
        ft.replace(R.id.VUD_tabsview_framelayout, frag).commit();

        Button temp = (Button) findViewById(R.id.VUD_AddEve_button);

        if(connect.user.authority.equals("Teacher"))
        {
            temp.setVisibility(View.VISIBLE);
        }
        else
        {
            temp.setVisibility(View.INVISIBLE);
        }

    }

    public void switchToViewCalendar(View v)
    {
        frag = new ViewCalendar();
        FragmentTransaction ft = fm.beginTransaction();
        tabs.removeAllViews();
        ft.replace(R.id.VUD_tabsview_framelayout, frag).commit();
        if(connect.user.authority.equals("Teacher")) {
            Button temp = (Button) findViewById(R.id.VUD_AddEve_button);
            temp.setVisibility(View.VISIBLE);
        }
    }

    public void switchToViewContactUs(View v)
    {
        frag = new ContactUs();
        FragmentTransaction ft = fm.beginTransaction();
        tabs.removeAllViews();
        ft.replace(R.id.VUD_tabsview_framelayout, frag).commit();
        Button temp = (Button) findViewById(R.id.VUD_AddEve_button);
        temp.setVisibility(View.INVISIBLE);
    }

    public void switchToViewPolicies(View v)
    {
        frag = new ViewPolicies();
        FragmentTransaction ft = fm.beginTransaction();
        tabs.removeAllViews();
        ft.replace(R.id.VUD_tabsview_framelayout, frag).commit();
        Button temp = (Button) findViewById(R.id.VUD_AddEve_button);
        temp.setVisibility(View.INVISIBLE);
    }

    public void AddCalendarEvent(View v)
    {
        frag = new AddCalendarEvent();
        FragmentTransaction ft = fm.beginTransaction();
        tabs.removeAllViews();
        ft.replace(R.id.VUD_tabsview_framelayout, frag).commit();
        Button temp = (Button) findViewById(R.id.VUD_AddEve_button);
        temp.setVisibility(View.INVISIBLE);
    }
}
