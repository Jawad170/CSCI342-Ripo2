package com.csci342.justin.moodleapplication;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

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
    public static final String addr = "172.18.29.57";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        previous = getIntent();
        login_token = previous.getIntExtra("Token",login_token);
        Log.i("THE TOKEN IS Dash! : ", " "+login_token);
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
                }
                else if(msg.what == 2)//logout
                {
                    login_token = 0;
                    Toaster("Logged out.");
                    finish();
                }
                else if(msg.what==3)
                {
                    int size=msg.arg1;
                    Toaster("Loading Announcements..");
                    String[] Getann = (String[]) msg.obj;

                    ArrayList<String> Announcements  = new ArrayList<String>();

                    for(int i =0; i < size ; i++)//populate array.
                    {

                        Announcements.add(Getann[i]);
                    }
                Populate(Announcements);
                }
                else {
                    Log.i("Bad", "From DashBoard");
                    Toaster("Failed to Connect to server");
                }

            }
        };

    }
    public void Populate(ArrayList<String> Announcements)
    {
        ListView lv = (ListView) findViewById(R.id.SA_SendAnn_listview);
        ArrayAdapter<String> AdapterAnn = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,Announcements);
        lv.setAdapter(AdapterAnn);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> arg0,View v , int position, long arg3)
            {

            }
        }
        );
    }
    //----------------------------------------END OF ONCREATE

    //-------------------------------------Start of Toaster
    public void Toaster(String input)
    {
        Toast.makeText(this,input,Toast.LENGTH_SHORT).show();
    }
    //--------------------------------------End of Toaster

    //--------------------------Start of SENT Feedback Function From server
    public void SentFeedback() {
        EditText InsertAnn = (EditText) findViewById(R.id.IA_insAnn_edittext) ;
        frag = new SendAnnouncement();
        FragmentTransaction ft = fm.beginTransaction();
        tabs.removeAllViews();
        ft.replace(R.id.D_tabview_framelayout, frag).commit();
        Toaster("Announcement Sent Successfully !");
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(InsertAnn.getWindowToken(), 0);
    }
    //----------------------------End of SENT Feedback Function From server

    //-----------------------------Start of GetAnnouncement Thread
    private class Get_Announcement_server extends Thread
    {
        String[] Announcements;
        int token;
        int size;

        public Get_Announcement_server(int intoken)
        {
            token = intoken;
            size=0;
        }
        @Override
        public void run()
        {
            try
            {
                Socket with_server = new Socket(InetAddress.getByName(addr),PORT);
                ObjectInputStream input = new ObjectInputStream(with_server.getInputStream());
                ObjectOutputStream output = new ObjectOutputStream(with_server.getOutputStream());

                Info request_size = new Info();
                request_size.setTag(2);
                request_size.setToken(token);
                output.writeObject(request_size);
                /*
                Info confirm_server = new Info();
                confirm_server = (Info) input.readObject();
              */
                    size = (int) input.readObject();
                    Announcements = new String[size];
                    Announcements = (String[]) input.readObject();

                Message msg = myHandler.obtainMessage();
                msg.what = 3;
                msg.arg1 = size;
                msg.obj = (Object) Announcements;
                myHandler.sendMessage(msg);


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
    //----------------------------End of GetAnnouncement Thread
    //-----------------Start Logout Thread
    private class Request_Logout_server extends Thread
    {
        int token;

        public Request_Logout_server(int in_token)
        {
            token=in_token;
        }
        @Override//after recieving confirmaiton info has 0 0
        public void run()
        {
            try {
                Socket with_server = new Socket(InetAddress.getByName(addr), PORT);
                ObjectInputStream input = new ObjectInputStream(with_server.getInputStream());
                ObjectOutputStream output = new ObjectOutputStream(with_server.getOutputStream());
                Log.i("Creating info logout", " Proceeding to Set and Token");
                Info send_to_server = new Info();
                send_to_server.tag = 0;
                send_to_server.token = token;
                output.writeObject(send_to_server);
                Log.i("Sent to server", " Token 0 Proceeding to Recieve");

                Info get_from_server = new Info();
                get_from_server = (Info) input.readObject();
                if (get_from_server.token == 0) {
                    Message msg = myHandler.obtainMessage();
                    msg.what = 2;
                    myHandler.sendMessage(msg);
                } else {
                   Toaster("Logout failed");
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
    //-----------------End Logout Thread
    //--------------------Start of Send Announcement THREAD
    private class send_announcement_server extends Thread
    {


        String Announcement;
        int token_from_login;
        int ack=0;
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

                Info send_to_server = new Info();
                send_to_server.setTag(1);
                send_to_server.setToken(token_from_login);
                output.writeObject(send_to_server);
                Log.i("Sent the Tag and Token"," Proceeding to read reply from server");

                Info get_from_server = new Info();
                get_from_server = (Info)input.readObject();
                Log.i("Finished Reading Input","");

                if(get_from_server.tag == 1)
                {
                    Message msg = myHandler.obtainMessage();
                    msg.what=1;
                    output.writeObject(Announcement);
                    ack = (int)input.readObject();
                    if(ack != 0)
                        {
                        myHandler.sendMessage(msg);
                        Log.i("Success ", "Confirmed Connection to server");
                         }
                    else
                    {
                        Log.i("Failed", "Failed to recieve confirmation from server");
                    }

                    }
                else
                {
                    Message msg = myHandler.obtainMessage();
                    msg.what=0;
                    myHandler.sendMessage(msg);
                    Log.i("Failure ","Denied Connection to server");
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

        Get_Announcement_server getann = new Get_Announcement_server(login_token);
        getann.start();
    }

    public void toViewUniversityDetails(View v) {
        Intent i = new Intent(this, ViewUniversityDetails.class);
        i.putExtra("User", User);
        startActivity(i);
    }

    public void toListOfSubjects(View v) {
        Intent i = new Intent(this, ListOfSubjects.class);
        i.putExtra("User", User);
        i.putExtra("Token",login_token);
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
        send_announcement_server confirmsend = new send_announcement_server(Announcement_Inserted, login_token);
        confirmsend.start();
        Log.i("String is : ", Announcement_Inserted);

    }
    public void LogOut(View v)
    {
        Request_Logout_server startlogout = new Request_Logout_server(login_token);
        startlogout.start();
    }

    }
