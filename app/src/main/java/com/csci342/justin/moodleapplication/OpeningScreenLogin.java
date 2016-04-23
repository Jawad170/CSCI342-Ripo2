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

    Protocol user;

    public static final int PORT = 33333;
    public static final String addr = "192.168.1.134";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_opening_screen_login);

        spinner = (Spinner) findViewById(R.id.OSL_tempspin_spinner);

        String[] logins = new String[] {"Student", "Teacher"};

        final ArrayAdapter spinner_adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, logins);

        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(spinner_adapter);

        user = new Protocol();

        myHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                Log.i("CAUGHT", "Message caught by handler");

                if(msg.what == 1)
                {
                    Log.i("GOOD", "SETTING LOGIN TOKEN");
                    login_token = msg.arg1;
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

        public logMeIn(Protocol user)
        {
            the_user = user;
        }

        @Override
        public void run()
        {
            try {

                Socket with_server = new Socket(InetAddress.getByName(addr), PORT);
                ObjectInputStream input = new ObjectInputStream(with_server.getInputStream());
                ObjectOutputStream output = new ObjectOutputStream(with_server.getOutputStream());

                output.writeObject(the_user);

                Log.i("WAITING", "Waiting for Server Reply");

                Info temp = (Info) input.readObject();

                Log.i("RECEIVED", "Server Reply Received");

                if(temp.tag == 1) {
                    Message msg = myHandler.obtainMessage();
                    msg.what = 1;
                    msg.arg1 = 1;
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
            user.generateHash(passw);
            user.setLogin(ema);
            logMeIn temp = new logMeIn(user);
            temp.start();
    }

    public void login(View v) {
        Toast.makeText(this, "Attempting to connect", Toast.LENGTH_LONG).show();

        startLoginProcess();


    }

    public void openDashboard()
    {
        Intent i = new Intent(this, Dashboard.class);
        String x = ((Spinner)findViewById(R.id.OSL_tempspin_spinner)).getSelectedItem().toString();
        i.putExtra("Authority",x);
        startActivity(i);
    }

}
