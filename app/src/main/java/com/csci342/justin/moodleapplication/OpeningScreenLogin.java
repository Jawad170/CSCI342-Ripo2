package com.csci342.justin.moodleapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class OpeningScreenLogin extends AppCompatActivity{

    Spinner spinner;
    Connection connect;
    EditText email_test;
    EditText password_test;

    Handler myHandler;
    public static int login_token = 0;



    Protocol User;

    public static final int PORT = 33333;
    public static final String addr = "172.18.26.150";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_opening_screen_login);

        //----------------Here is the place where we initialize the user protocol and pass it to the rest.
        User = new Protocol();
        //User.setAuthority("Teacher");
        User.setPass("12345");
        User.setLogin("abosami");
        //----------------------------------HARDCODED FOR NOW
        spinner = (Spinner) findViewById(R.id.OSL_tempspin_spinner);

        String[] logins = new String[] {"Student", "Teacher"};

        final ArrayAdapter spinner_adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, logins);

        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(spinner_adapter);



        myHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                Log.i("CAUGHT", "Message caught by handler");

                if(msg.what == 1)
                {
                    Log.i("GOOD", "SETTING LOGIN TOKEN");
                    login_token = msg.arg2;
                    User.setAuthority((String)msg.obj);
                    Toaster("Your Authority is : "+ User.authority);
                    successFunction();
                }
                else
                {
                    Log.i("BAD", "DENYING LOGIN TOKEN");
                    login_token = 0;
                    failureFunction();
                }

            }
        };

    }
    public void Toaster(String input)
    {
        Toast.makeText(this,input,Toast.LENGTH_SHORT).show();
    }

    public void failureFunction()
    {
        Toast.makeText(this, "Login Failed, please try again", Toast.LENGTH_SHORT).show();
    }

    public void successFunction()
    {
        Log.i("CONTINUE", "Opening Dashboard activity");
        openDashboard();
    }

    private class logMeIn extends Thread
    {
        Protocol the_user;
        String auth;
        public logMeIn(Protocol User)
        {
            the_user = User;
        }

        @Override
        public void run()
        {
            try {

                Socket with_server = new Socket(InetAddress.getByName(addr), PORT);
                ObjectInputStream input = new ObjectInputStream(with_server.getInputStream());
                ObjectOutputStream output = new ObjectOutputStream(with_server.getOutputStream());
                Info Mylogin = new Info();
                Mylogin.setTag(0);
                Mylogin.setToken(login_token);
                output.writeObject(Mylogin);
                output.writeObject(the_user);
                Log.i("WAITING", "Waiting for Server Reply");

                Info temp = (Info) input.readObject();

                auth = ((String)input.readObject());
                Log.i("RECEIVED", "Server Reply Received");

                if(temp.tag == 1) {
                    Message msg = myHandler.obtainMessage();
                    msg.what = 1;
                    msg.arg1 = 1;
                    msg.arg2 = temp.token;
                    msg.obj = (Object) auth;
                    myHandler.sendMessage(msg);
                    Log.i("SUCCESS", "Message sent confirming login");
                }
                else
                {
                    Message msg = myHandler.obtainMessage();
                    msg.what = 0;
                    myHandler.sendMessage(msg);
                    Log.i("FAILURE", "Message sent denying login");
                }

            }catch(UnknownHostException e)
            {
                e.printStackTrace();
            }catch(IOException e)
            {
                e.printStackTrace();
            }catch(ClassNotFoundException e)
            {
                e.printStackTrace();
            }

        }
    }

    public void startLoginProcess()
    {
            EditText email_test = (EditText) findViewById(R.id.OSL_email_edittext);
            EditText password_test = (EditText) findViewById(R.id.OSL_password_edittext);
            String ema = email_test.getText().toString();
            String passw = password_test.getText().toString();
            User.generateHash(passw);
            User.setLogin(ema);
            logMeIn temp = new logMeIn(User);
            temp.start();
    }

    public void login(View v) {
        Toast.makeText(this, "Attempting to connect", Toast.LENGTH_LONG).show();

        startLoginProcess();
       // openDashboard();

    }

    public void openDashboard()
    {
        Intent i = new Intent(this, Dashboard.class);
        i.putExtra("User",User);
        i.putExtra("Token",login_token);
        startActivity(i);
    }

}
