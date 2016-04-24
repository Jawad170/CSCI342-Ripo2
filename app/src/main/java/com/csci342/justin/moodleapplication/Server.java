package com.csci342.justin.moodleapplication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

/**
 * Created by Justin on 2016-03-30.
 */
public class Server extends Thread{

    static ServerSocket serverSocket;
    static ObjectOutputStream output;
    static ObjectInputStream input;
    static Socket client;
    static int status = 0;

    static int[] token_list;
    static int array_tracker = 0;

    public static int generateToken()
    {
        Random randInt = new Random();

        int randomNum = randInt.nextInt((9999 - 1000) + 1) + 1000;

        System.out.println("Token generated: " + randomNum);

        return randomNum;
    }

    public static boolean findToken(int to_find)
    {
        boolean found = false;
        for(int i=0;i<array_tracker;i++)
        {
            if(to_find == token_list[i])
            {
                found = true;
                break;
            }
        }

        return found;
    }

    public static void main(String[] args) {

        token_list = new int[50];

        while (true) {
            try {
                serverSocket = new MyServerSocket(33333);
                System.out.println("Server Setup, waiting...");

                while (true) {
                    client = serverSocket.accept();

                    System.out.println("Client connected to Socket");

                    output = new ObjectOutputStream(client.getOutputStream());
                    input = new ObjectInputStream(client.getInputStream());

                    System.out.println("Streams established: Waiting for Token");

                    Info hello = (Info) input.readObject();

                    if(hello.token == 0)
                    {
                        //start new login process
                        int temp = generateToken();
                        hello.setToken(temp);
                        hello.setTag(1);
                        token_list[array_tracker] = temp;
                        array_tracker++;
                        output.writeObject(hello);
                    }
                    else
                    {
                        boolean logged_in = findToken(hello.token);
                        if(logged_in)
                        {
                            //check tag for command
                            if(hello.tag == 0)
                            {
                                //logic for handling request 0
                            }
                            else if(hello.tag == 1)
                            {
                                //logic for handling request 1
                            }
                            else if(hello.tag == 2)
                            {
                                //logic for handling request 2
                            }
                            else if(hello.tag == 3)
                            {
                                //logic for handling request 3
                            }
                            else if(hello.tag == 4)
                            {
                                //logic for handling request 4
                            }
                            else if(hello.tag == 5)
                            {
                                //logic for handling request 5
                            }
                            else if(hello.tag == 6)
                            {
                                //logic for handling request 6
                            }
                            else if(hello.tag == 7)
                            {
                                //logic for handling request 7
                            }
                            else if(hello.tag == 8)
                            {
                                //logic for handling request 8
                            }
                            else if(hello.tag == 9)
                            {
                                //logic for handling request 9
                            }
                        }
                        else
                        {
                            hello.setTag(0);
                            hello.setToken(0);
                            output.writeObject(hello);
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}
