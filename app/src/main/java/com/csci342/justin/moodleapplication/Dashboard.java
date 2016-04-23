package com.csci342.justin.moodleapplication;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import layout.EditPersonalDetails;
import layout.SendAnnouncement;

public class Dashboard extends Activity{

    Intent previous;
    String authority;
    ObjectOutputStream output;
    ObjectInputStream input;

    FragmentManager fm = getFragmentManager();
    Fragment frag;
    FrameLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        previous = getIntent();
        authority = previous.getStringExtra("Authority");

        frag = new EditPersonalDetails();
        FragmentTransaction ft = fm.beginTransaction();

        tabs = (FrameLayout) findViewById(R.id.D_tabview_framelayout);
        tabs.removeAllViews();
        ft.replace(R.id.D_tabview_framelayout, frag).commit();

        if(authority.equals("Teacher"))
        {
            Button temp = (Button) findViewById(R.id.D_SenAnn_button);
            temp.setVisibility(View.VISIBLE);
        }
        else
        {
            Button temp = (Button) findViewById(R.id.D_SenAnn_button);
            temp.setVisibility(View.INVISIBLE);
            temp = (Button) findViewById(R.id.D_EdPerDet_button);
            LinearLayout.LayoutParams x = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0.0f);
            temp.setLayoutParams(x);
        }
    }

    public void switchToEditDetails(View v)
    {
            frag = new EditPersonalDetails();
            FragmentTransaction ft = fm.beginTransaction();
            tabs.removeAllViews();
            ft.replace(R.id.D_tabview_framelayout, frag).commit();
    }

    public void switchToSendAnnouncement(View v)
    {
            frag = new SendAnnouncement();
            FragmentTransaction ft = fm.beginTransaction();
            tabs.removeAllViews();
            ft.replace(R.id.D_tabview_framelayout, frag).commit();
    }

    public void toViewUniversityDetails(View v)
    {
            Intent i = new Intent(this, ViewUniversityDetails.class);
            startActivity(i);
    }

    public void toListOfSubjects(View v)
    {
            Intent i = new Intent(this, ListOfSubjects.class);
            startActivity(i);
    }


    public void AddNewItem(View v)
    {

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.SA_linear_layout);

        TextView txt1 = new TextView(this);
        txt1.setText("NEW ANNOUNCEMENT");
        linearLayout.addView(txt1);
    }

}
