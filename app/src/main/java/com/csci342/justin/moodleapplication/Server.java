package com.csci342.justin.moodleapplication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Justin on 2016-03-30.
 */
public class Server extends Thread{

    static ServerSocket serverSocket;
    static ObjectOutputStream output;
    static ObjectInputStream input;
    static Socket client;
    static int status = 0;

    public static void main(String[] args) {

        try {
            serverSocket = new MyServerSocket(33333);
            System.out.println("Server Setup, waiting...");
            client = serverSocket.accept();

            System.out.println("Client connected to Socket");

            output = new ObjectOutputStream(client.getOutputStream());
            input = new ObjectInputStream(client.getInputStream());

            System.out.println("Client Connected: Waiting for Login");

            Protocol login_attempt = (Protocol) input.readObject();

            System.out.println("Client: " + login_attempt.getLogin());
            System.out.println("   using password hash: " + login_attempt.getPass());

            Info temp = new Info();
            temp.tag = 1;
            output.writeObject(temp);

            System.out.println("Login Confirmation Sent");

            while(true)
            {

            }

        }catch(IOException e)
        {
            e.printStackTrace();
        }catch(ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }

}
