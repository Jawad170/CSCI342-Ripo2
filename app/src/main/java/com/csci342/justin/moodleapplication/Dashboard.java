package com.csci342.justin.moodleapplication;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import layout.EditPersonalDetails;
import layout.InsertAnnouncement;
import layout.SendAnnouncement;

public class Dashboard extends Activity {

    Intent previous;
    String authority;
    ObjectOutputStream output;
    ObjectInputStream input;
    FragmentManager fm = getFragmentManager();
    Fragment frag;
    FrameLayout tabs;
    Protocol User;
    Handler myHandler;
    public  int login_token = 0;
    public static final int PORT = 33333;
    public static final String addr = "192.168.1.134";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        previous = getIntent();
        login_token = previous.getIntExtra("Token",login_token);
      //TEST  Log.i("THE TOKEN IS ! : ", " "+login_token);
        authority = previous.getStringExtra("Authority");
        //----------------Here we get the user authority from the loginscreen
        User = (Protocol) previous.getSerializableExtra("User");
        //-----------
        frag = new EditPersonalDetails();
        FragmentTransaction ft = fm.beginTransaction();

        tabs = (FrameLayout) findViewById(R.id.D_tabview_framelayout);
        tabs.removeAllViews();
        ft.replace(R.id.D_tabview_framelayout, frag).commit();

        if (User.authority.equals("Teacher")) {
            Button temp = (Button) findViewById(R.id.D_SenAnn_button);
            temp.setVisibility(View.VISIBLE);
        } else {
            Button temp = (Button) findViewById(R.id.D_SenAnn_button);
            temp.setVisibility(View.INVISIBLE);
            temp = (Button) findViewById(R.id.D_EdPerDet_button);
            LinearLayout.LayoutParams x = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0.0f);
            temp.setLayoutParams(x);
        }

        //---------------------Start of Handler
        myHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.i("CAUGHT!", "Message Caught by DashBoard Handler");

                if (msg.what == 1) {
                    Log.i("GOOD", "From DashBoard");
                 //   Successful_Connection_Dashboard();
                    SentFeedback();
                } else {
                    Log.i("Bad", "From DashBoard");
                    Failure_Connection_Dashboard();
                }

            }
        };

    }

    //----------------------------------------END OF ONCREATE


    //--------------------------Start of SENT Feedback Function From server
    public void SentFeedback() {
        EditText InsertAnn = (EditText) findViewById(R.id.IA_insAnn_edittext) ;
        frag = new SendAnnouncement();
        FragmentTransaction ft = fm.beginTransaction();
        tabs.removeAllViews();
        ft.replace(R.id.D_tabview_framelayout, frag).commit();
        Toast.makeText(this, "Announcement Sent Successfully !", Toast.LENGTH_SHORT).show();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(InsertAnn.getWindowToken(), 0);
    }
    //----------------------------End of SENT Feedback Function From server

    //----------------------Start of Successful Connection From Dashboard Function
    public void Successful_Connection_Dashboard()
    {
        Toast.makeText(this,"Successfully Connected To server",Toast.LENGTH_SHORT).show();
    }
    //-----------------------END of Successful Connection From Dashboard Function

    //-----------------------Start of Failure Connection from Server to Dashboard Function
    public void Failure_Connection_Dashboard()
    {
        Toast.makeText(this,"Failed to Connect to server",Toast.LENGTH_SHORT).show();
    }
    //----------------------End of Failure Connection from server to Dashboard Function

    //--------------------Start of Send Announcement THREAD
    private class send_announcement_server extends Thread
    {
        String Announcement;
        int token_from_login;
        int ack;
        public send_announcement_server(String In_Announcement, int token)
        {
            Announcement = In_Announcement;
            token_from_login=token;
        }

        @Override
        public void run()
        {
            try
            {
                Socket with_server = new Socket(InetAddress.getByName(addr),PORT);
                ObjectInputStream input = new ObjectInputStream(with_server.getInputStream());
                ObjectOutputStream output = new ObjectOutputStream(with_server.getOutputStream());
                Log.i("Creating the Temp Info"," Proceeding to Set tag and Token");

                Info temp = new Info();
                temp.setTag(5);
                temp.setToken(token_from_login);
                output.writeObject(temp);
                Log.i("Sent the Tag and Token"," Proceeding to read reply from server");

                temp = (Info)input.readObject();
                Log.i("Finished Reading Input","");

                if(temp.tag == 1)
                {
                    Message msg = myHandler.obtainMessage();
                    msg.what=1;
                    output.writeObject(Announcement);
                    ack = (int)input.readObject();
                    myHandler.sendMessage(msg);
                    Log.i("Success ","Confirmed Connection to server");
                }
                else
                {
                    Message msg = myHandler.obtainMessage();
                    msg.what=0;
                    myHandler.sendMessage(msg);
                    Log.i("Failire ","Denied Connection to server");
                }


            }
            catch(UnknownHostException e)
            {
                e.printStackTrace();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }
    //---------------------End of Send Announcement THREAD
    public void switchToEditDetails(View v) {
        frag = new EditPersonalDetails();
        FragmentTransaction ft = fm.beginTransaction();
        tabs.removeAllViews();
        ft.replace(R.id.D_tabview_framelayout, frag).commit();
    }

    public void switchToSendAnnouncement(View v) {
        frag = new SendAnnouncement();
        FragmentTransaction ft = fm.beginTransaction();
        tabs.removeAllViews();
        ft.replace(R.id.D_tabview_framelayout, frag).commit();
    }

    public void toViewUniversityDetails(View v) {
        Intent i = new Intent(this, ViewUniversityDetails.class);
        i.putExtra("User", User);
        startActivity(i);
    }

    public void toListOfSubjects(View v) {
        Intent i = new Intent(this, ListOfSubjects.class);
        i.putExtra("User", User);
        startActivity(i);
    }

    public void toInsertAnnouncement(View v) {
        frag = new InsertAnnouncement();
        FragmentTransaction ft = fm.beginTransaction();
        tabs.removeAllViews();
        ft.replace(R.id.D_tabview_framelayout, frag).commit();
    }


    public void AddNewItem(View v) {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.SA_linear_layout);
        TextView txt1 = new TextView(this);
        txt1.setText("NEW ANNOUNCEMENT");
        linearLayout.addView(txt1);
    }

    public void confirmsendannouncement(View v) {
        EditText InsertAnn = (EditText) findViewById(R.id.IA_insAnn_edittext) ;
        String Announcement_Inserted;
        Announcement_Inserted = InsertAnn.getText().toString();
        Log.i("String is : ", Announcement_Inserted);

    }


    }
