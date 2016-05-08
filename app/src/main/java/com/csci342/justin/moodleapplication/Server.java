package com.csci342.justin.moodleapplication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
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
    static int stringannouncementarraysize= 2;
    static String[] staticannouncements = new String[5];
    static String[] studentnames = new String [3];
    static int marksarraysize = 5;

    static String[] marks = new String[5];
    static String[] max = new String[5];
    static int array_tracker = 0;

    static String[] logins = {"Jawad", "Ahmed", "Justin"};
    static String[] hashed_passwords = {"-101-29-70358493-69-3810911689-868-3102108", "-127-36-101-3782-4877-62054-37-404962-4885", "-9641-48-33-124-218573-5865-3274-98-13-119-27"};
    static int[] token_list;
    static int index_iterator = 3;

    public static int generateToken()
    {
        Random randInt = new Random();

        int randomNum = randInt.nextInt((9999 - 1000) + 1) + 1000;

        System.out.println("Token generated: " + randomNum);

        return randomNum;
    }

    public static boolean checkHash(String name, String hash)
    {
        boolean valid = false;
        for(int i=0;i<index_iterator;i++)
        {
            if(name.equals(logins[i]))
            {
                int index = i;
                if(hashed_passwords[i].equals(hash))
                {
                    valid = true;
                    break;
                }
            }
        }

        return valid;
    }

    public static boolean findToken(int to_find)
    {
        boolean found = false;
        for(int i=0;i<index_iterator;i++)
        {
            if(to_find == token_list[i])
            {
                found = true;
                break;
            }
        }

        return found;
    }

    public static int whereToken(int to_find)
    {
        int index = -1;
        for(int i=0;i<index_iterator;i++)
        {
            if(to_find == token_list[i])
            {
                index = i;
                break;
            }
        }

        return index;
    }

    public static void removeToken(int index)
    {
        System.out.println("Removing Login Token:" + token_list[index]);
        token_list[index] = 0;
    }

    public static void main(String[] args) {

        token_list = new int[50];

        while (true) {
            try {
                serverSocket = new ServerSocket(33333);
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
                        Protocol info = (Protocol) input.readObject();
                        System.out.println("Client Pass Hash = " + info.getPass());
                        String login_name = info.getLogin();
                        boolean valid = checkHash(login_name, info.getPass());
                        if(valid)
                        {
                            int temp = generateToken();
                            boolean success = MySQL_Handler.setToken("Justin",temp);
                            if(success) {

                                hello.setToken(temp);
                                hello.setTag(1);
                                token_list[array_tracker] = temp;
                                array_tracker++;
                                output.writeObject(hello);
                            }
                            else
                            {
                                hello.setToken(0);
                                hello.setTag(0);
                                output.writeObject(hello);
                            }
                        }
                        else
                        {
                            hello.setToken(0);
                            hello.setTag(0);
                            output.writeObject(hello);
                        }

                    }
                    else
                    {
                        boolean logged_in = findToken(hello.token);
                        if(logged_in)
                        {
                            //check tag for command
                            if(hello.tag == 0)
                            {
                                //logic for handling request 0 (LOGOUT)
                                System.out.println("Client logout request caught");
                                int place = whereToken(hello.token);
                                removeToken(place);
                                Info temp = new Info();
                                temp.setTag(0);
                                temp.setToken(0);
                                output.writeObject(temp);
                                System.out.println("Client Log Out Request Acked");
                            }
                            else if(hello.tag == 1)
                            {
                                //logic for handling request 1 (ANNOUNCMENT)
                                System.out.println("Upload Announcement request caught");
                                output.writeObject(hello);
                                String announcement = (String)input.readObject();
                                System.out.println("Received Announcement: " + announcement);
                                int temp = 1;
                                output.writeObject(temp);
                                System.out.println("Client Upload Announcement Request Acked");
                            }
                            else if(hello.tag == 2)//
                            {
                                staticannouncements[0]="hello";
                                staticannouncements[1]="test";
                                output.writeObject(stringannouncementarraysize);
                                output.writeObject(staticannouncements);

                            }
                            else if(hello.tag == 3)// (VIEW MARKS STUDENT)
                            {   //HARD CODE REPLACE ME
                                staticannouncements[0]="10";
                                staticannouncements[1]="15";
                                staticannouncements[2]="20";
                                staticannouncements[3]="25";
                                staticannouncements[4]="30";
                                max[0]="50";
                                max[1]="50";
                                max[2]="40";
                                max[3]="30";
                                max[4]="100";

                                output.writeObject(marksarraysize);
                                output.writeObject(staticannouncements);
                                output.writeObject(max);


                            }
                            else if(hello.tag == 4)//VIEW STUDENT NAMES.
                            {
                                studentnames[0]="Ahmed";
                                studentnames[1]="Jawad";
                                studentnames[2]="Justin";
                                 int namesize = 3;
                                output.writeObject(namesize);
                                output.writeObject(studentnames);
                                //logic for handling request 4
                            }
                            else if(hello.tag == 5)//UPLOAD MARKS
                            {
                               int mark=0;
                                int max =0;
                                mark = (int) input.readObject();
                                max= (int) input.readObject();
                                System.out.println("mark :" + mark + " max : "+ max);
                            }
                            else if(hello.tag == 6)
                            {
                                //logic for handling request 6 (UPLOAD FILES)
                                ServerSocket fileserverSocket = new ServerSocket(33334);
                                System.out.println("File Transfer Server Setup, waiting...");

                                Info temp = new Info();
                                temp.setTag(1);
                                output.writeObject(temp);
                                try
                                {
                                    int i =0;
                                    Socket file_client = fileserverSocket.accept();
                                    System.out.println("New File transfer connection established");
                                   // int filesize = (int) input.readObject();
                                    String filename = (String) input.readObject();
                                    byte[] received_file = new byte[65536];
                                    InputStream is = file_client.getInputStream();
                                    FileOutputStream fos = new FileOutputStream(filename);
                                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                                    System.out.println("Reading File now");
                                    File to_make = new File(filename);
                                    FileOutputStream converter = new FileOutputStream(to_make);
                                    converter.getFD().sync();
                                    int bytesRead;
                                    byte[] bigarray = new byte[65536];
                                    int position = 0;

                                    do {
                                        bytesRead = is.read(received_file, 0, received_file.length);
                                        System.out.println("test bytesRead = " + bytesRead);
                                        int counter = 0;
                                        for(i=0;i<bytesRead;i++)
                                        {
                                            bigarray[i] = received_file[counter];
                                            counter++;
                                        }
                                        for(i=0;i<position;i++) {
                                            converter.write(bigarray[i]);
                                        }
                                        position += bytesRead;

                                        converter.getFD().sync();
                                        converter.flush();
                                        System.out.println("bytesRead = " + bytesRead);
                                        System.out.println("Position = " + position);

                                    }while(bytesRead == 65536);

                                    System.out.println("Received byte_array: " + bytesRead + " bytes read.");

                                    is.close();
                                    fos.close();
                                    bos.close();




                                    System.out.println("Received File: " + filename);
                                    converter.close();
                                    fileserverSocket.close();

                                }catch(EOFException e)
                                {
                                    e.printStackTrace();
                                }catch(IOException e)
                                {
                                    e.printStackTrace();
                                }

                                hello.setTag(1);
                                output.writeObject(hello);


                            }
                            else if(hello.tag == 7)//(DOWNLOAD FILES)
                            {
                                ServerSocket fileserverSocket = new ServerSocket(33334);
                                System.out.println("File Transfer Server Setup, waiting...");
                                File file_to_send = new File("/sdcard/test.txt");//SET IT
                                Info temp = new Info();
                                temp.setTag(1);
                                output.writeObject(temp);
                                try
                                {
                                    Socket file_client = fileserverSocket.accept();
                                    System.out.println("New File transfer connection established");
                                    byte[] array_to_send  = new byte [(int)file_to_send.length()];
                                    output.writeObject(array_to_send.length);
                                    output.writeObject(file_to_send.getName());
                                    FileInputStream fis = new FileInputStream(file_to_send);
                                    BufferedInputStream bis = new BufferedInputStream(fis);
                                    bis.read(array_to_send,0,array_to_send.length);
                                    OutputStream os = file_client.getOutputStream();
                                    os.write(array_to_send,0,array_to_send.length);
                                    os.close();
                                    file_client.close();

                                }catch(UnknownHostException e)
                                {
                                    e.printStackTrace();
                                }
                                catch(IOException e)
                                {
                                    e.printStackTrace();
                                }


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
                            System.out.println("Invalid Client Token submitted");
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
