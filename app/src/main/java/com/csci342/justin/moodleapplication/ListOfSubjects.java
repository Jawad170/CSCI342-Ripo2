package com.csci342.justin.moodleapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ListOfSubjects extends Activity {
    Handler myHandler;
    ObjectOutputStream output;
    ObjectInputStream input;
    Intent previous;
    Connection connect;
    Protocol User;
    public  int login_token = 0;
    public static final int PORT = 33333;
    public static final String addr = "172.18.26.150";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_subjects);
       //------------------------------

        //-------------------
        previous = getIntent();
        User = (Protocol) previous.getSerializableExtra("User");
        connect = (Connection) previous.getSerializableExtra("Connection");
        login_token = previous.getIntExtra("Token",login_token);
        Log.i("Token LOS",""+login_token);

        myHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.i("CAUGHT!", "Message Caught by Subhectchoice Handler");

                if (msg.what == 1) {
                    int size=msg.arg1;
                    Toaster("Loading Subjects..");
                    String[] Getann = (String[]) msg.obj;

                    ArrayList<String> Announcements  = new ArrayList<String>();

                    for(int i =0; i < size ; i++)//populate array.
                    {

                        Announcements.add(Getann[i]);
                    }
                    Populate_Full(Announcements);
                }
                else if(msg.what==2)
                {
                    int size=msg.arg1;
                    Toaster("Loading Subjects..");
                    String[] Getann = (String[]) msg.obj;

                    ArrayList<String> Announcements  = new ArrayList<String>();

                    for(int i =0; i < size ; i++)//populate array.
                    {

                        Announcements.add(Getann[i]);
                    }
                    Populate(Announcements);
                }
                else if(msg.what==3)
                {
                    Toaster("Enrolled Successfully");
                    getspecific temp = new getspecific(login_token);
                    Log.i("Starting ", " Thread ");
                    temp.start();

                }
                else if(msg.what==4)
                {
                    int size=msg.arg1;
                    Toaster("Loading Subjects..");
                    String[] Getann = (String[]) msg.obj;

                    ArrayList<String> Announcements  = new ArrayList<String>();

                    for(int i =0; i < size ; i++)//populate array.
                    {

                        Announcements.add(Getann[i]);
                    }
                    Populate(Announcements);
                }
                else if (msg.what==0)
                {
                    Toaster("Already enrolled in Subject");
                }

            }

        };

        getSubjectSpecificlist temp = new getSubjectSpecificlist(login_token);
        Log.i("Starting ", " Thread ");
        temp.start();
    }
    public void enrollsubject(View v)
    {
        getSubjectList temp = new getSubjectList(login_token);
        temp.start();
    }
    public void Populate(ArrayList<String> Announcements) {

        ListView lv = (ListView) findViewById(R.id.LOS_list_listview);
        ArrayAdapter<String> AdapterAnn = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Announcements);
        lv.setAdapter(AdapterAnn);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            String name;
                                      public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                                         name = (String)arg0.getItemAtPosition(position);
                                          SubjectButtonOnClick(name);
                                      }
                                  }
        );
    }

    public void Populate_Full(ArrayList<String> Announcements) {

        ListView lv = (ListView) findViewById(R.id.LOS_list_listview);
        ArrayAdapter<String> AdapterAnn = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Announcements);
        lv.setAdapter(AdapterAnn);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                      String name;

                                      public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                                          name = (String)arg0.getItemAtPosition(position);
                                          sendsubjectname temp = new sendsubjectname(login_token,name);
                                            temp.start();
                                      }
                                  }
        );
    }
    public void Toaster(String input)
    {
        Toast.makeText(this,input,Toast.LENGTH_SHORT).show();
    }

    public void switchtosubjectchoice(View v)
    {
      //  Intent i = new Intent(this, subjectchoice.class);
     //   startActivity(i);
    }
    void SubjectButtonOnClick(String name)
    {

        Intent i = new Intent(ListOfSubjects.this, SubjectView.class);
        i.putExtra("Subject Name", name);
        i.putExtra("User",User);
        i.putExtra("Token",login_token);
        startActivity(i);
    }

    View.OnClickListener handleOnClick(final Button button) {
        return new View.OnClickListener() {
            public void onClick(View v) {

                Button butt = (Button) v;
                String x = (String) butt.getText();

                Intent i = new Intent(ListOfSubjects.this, SubjectView.class);
                i.putExtra("Subject Name", x);
                i.putExtra("User",User);
                i.putExtra("Token",login_token);
                startActivity(i);

            }
        };
    }

    private class sendsubjectname extends Thread
    {
        String subjectname;
        int token;
        int confirmation;
        public sendsubjectname(int intoken,String insubjectname)
        {
            subjectname=insubjectname;
            token=intoken;
        }
        @Override
        public void run()
        {
            try {
                Socket with_server = new Socket(InetAddress.getByName(addr), PORT);
                ObjectInputStream input = new ObjectInputStream(with_server.getInputStream());
                ObjectOutputStream output = new ObjectOutputStream(with_server.getOutputStream());

                Info request_size = new Info();
                request_size.setTag(11);
                request_size.setToken(token);
                output.writeObject(request_size);

                output.writeObject(User.getLogin());
                output.writeObject(subjectname);

                confirmation = (int) input.readObject();

                if (confirmation > 0) {
                    Message msg = myHandler.obtainMessage();
                    msg.what = 3;
                    myHandler.sendMessage(msg);
                }
                else
                {
                    Message msg = myHandler.obtainMessage();
                    msg.what = 0;
                    myHandler.sendMessage(msg);
                }
                with_server.close();
            }catch(UnknownHostException e)
            {
                e.printStackTrace();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

    }
    private class getSubjectList extends Thread
    {
        int token;
        int size;
        String[] Subjects;

        public  getSubjectList(int intoken)
        {
            token=intoken;
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
                request_size.setTag(9);
                request_size.setToken(token);
                output.writeObject(request_size);

                size = (int) input.readObject();

                Subjects = new String[size];
                Subjects = (String[]) input.readObject();

                Message msg = myHandler.obtainMessage();
                msg.what = 1;
                msg.arg1 = size;
                msg.obj = (Object) Subjects;
                myHandler.sendMessage(msg);
                with_server.close();
            }catch(UnknownHostException e)
            {
                e.printStackTrace();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    //private class
    private class getSubjectSpecificlist extends Thread
    {
        int token;
        int size;
        String[] Subjects;

        public  getSubjectSpecificlist(int intoken)
        {
            token=intoken;
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
                request_size.setTag(10);
                request_size.setToken(token);
                output.writeObject(request_size);
                output.writeObject(User.getLogin());
                size = (int) input.readObject();

                Subjects = new String[size];
                Subjects = (String[]) input.readObject();

                Message msg = myHandler.obtainMessage();
                msg.what = 2;
                msg.arg1 = size;
                msg.obj = (Object) Subjects;
                myHandler.sendMessage(msg);
                with_server.close();
            }catch(UnknownHostException e)
            {
                e.printStackTrace();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private class getspecific extends Thread
    {
        int token;
        int size;
        String[] Subjects;

        public  getspecific(int intoken)
        {
            token=intoken;
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
                request_size.setTag(10);
                request_size.setToken(token);
                output.writeObject(request_size);
                output.writeObject(User.getLogin());
                size = (int) input.readObject();

                Subjects = new String[size];
                Subjects = (String[]) input.readObject();

                Message msg = myHandler.obtainMessage();
                msg.what = 4;
                msg.arg1 = size;
                msg.obj = (Object) Subjects;
                myHandler.sendMessage(msg);
                with_server.close();
            }catch(UnknownHostException e)
            {
                e.printStackTrace();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    //private class
}



