package com.csci342.justin.moodleapplication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Justin on 2016-03-30.
 */
public class Connection extends Thread implements Serializable {

    static final long serialVersionUID = 32L;

    Socket with_server;
    public static final int PORT = 33333;
    public static final String addr = "172.18.22.254";
    public Protocol user = new Protocol();
    ObjectOutputStream output;
    ObjectInputStream input;
    Info temp;
    int status = 0;

    public Connection()
    {

    }

    public void run()
    {

        try {
            with_server = new Socket(InetAddress.getByName(addr), PORT);
            input = new ObjectInputStream(with_server.getInputStream());
            output = new ObjectOutputStream(with_server.getOutputStream());



        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
            //connection made and logic waiting

            while(true)
            {
                try {
                    Info temp = (Info) input.readObject();
                    if(temp.tag == 1)
                    {
                        user.setLoggedIn(true);
                        break;
                    }

                }catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
            while(true)
            {

            }
    }

    public void send_login()
    {
        try {
            output.writeObject(user);
        }catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public boolean send_logout(MyObjectInputStream in, MyObjectOutputStream out) {
        try {
            temp = new Info();
            temp.setTag(0);
            out.writeObject(temp);
            boolean result = (boolean) in.readObject();
            if (result == true) {
                return true;
            } else {
                return false;
            }

        }catch(ClassNotFoundException e)
        {
            e.printStackTrace();
            return false;
        }catch(IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public void request_upload()
    {
        try {
            temp = new Info();
            temp.setTag(2);
            output.writeObject(temp);

        }catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public void receive_subjects()
    {
        //receive array of subjects
    }
}
