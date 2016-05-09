package com.csci342.justin.moodleapplication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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

        MySQL_Handler.RESET_DATABASE();
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
                        String pass_check = MySQL_Handler.getPassHash(login_name);
                        boolean valid = pass_check.equals(info.getPass());
                        if(valid)
                        {
                            int temp = generateToken();
                            boolean success = MySQL_Handler.setToken(login_name,temp);
                            if(success) {

                                hello.setToken(temp);
                                hello.setTag(1);
                                output.writeObject(hello);
                                String x = MySQL_Handler.getAuthority(login_name);
                                output.writeObject(x);
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
                        String user_name = MySQL_Handler.checkLoggedIn(hello.token);
                        if(!(user_name == null))
                        {
                            //check tag for command
                            if(hello.tag == 0)
                            {
                                //logic for handling request 0 (LOGOUT)
                                System.out.println("Client logout request caught");
                                boolean success = MySQL_Handler.setToken(user_name,0);
                                Info temp = new Info();
                                temp.setTag(0);
                                temp.setToken(0);
                                output.writeObject(temp);
                                System.out.println("Client Log Out Request Acked");
                            }
                            else if(hello.tag == 1)
                            {
                                //logic for handling request 1 (ANNOUNCEMENT)
                                System.out.println("Upload Announcement request caught");
                                output.writeObject(hello);
                                String subject = (String) input.readObject();
                                String announcement = (String)input.readObject();
                                System.out.println("Received Announcement: " + announcement);
                                MySQL_Handler.addAnnouncement(subject, announcement);

                                int temp = 1;
                                output.writeObject(temp);
                                System.out.println("Client Upload Announcement Request Acked");
                            }
                            else if(hello.tag == 2)//TEACHER GET ANNOUNCEMENT
                            {
                                String[] announcements = MySQL_Handler.getALLAnnouncements();
                                int size = announcements.length;

                                output.writeObject(size);
                                output.writeObject(announcements);

                            }
                            else if(hello.tag == 3)// (VIEW MARKS STUDENT)
                            {   //HARD CODE REPLACE ME

                                String subject = (String) input.readObject();
                                String username = (String) input.readObject();
                                String[] marks = MySQL_Handler.getGrades(subject,username);
                                int size = marks.length;

                                output.writeObject(size);
                                output.writeObject(marks);

                            }
                            else if(hello.tag == 4)//VIEW STUDENT NAMES.
                            {
                                String subject = (String) input.readObject();
                                String[] names = MySQL_Handler.getStudentsEnrolled(subject);
                                int size = names.length;
                                output.writeObject(size);
                                output.writeObject(names);
                            }
                            else if(hello.tag == 5)//UPLOAD MARKS
                            {
                                String subject = (String) input.readObject();
                                String student = (String) input.readObject();
                                int mark = (int) input.readObject();
                                int max= (int) input.readObject();
                                Random x = new Random();
                                int lala = x.nextInt(4);
                                MySQL_Handler.addGrade(subject,student,"Assignment"+lala,mark,max);

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
                                    Socket file_client = fileserverSocket.accept();
                                    System.out.println("New File transfer connection established");
                                    InputStream is = file_client.getInputStream();
                                    int filesize = (int) input.readObject();
                                    String filename = (String) input.readObject();

                                    File files = new File("C:/Users/Justin/AndroidStudioProjects/CSCI342-Ripo2/Metadata.txt");
                                    int array_size = 0;
                                        BufferedReader br = new BufferedReader(new FileReader(files));
                                        String line;
                                        while ((line = br.readLine()) != null) {

                                            System.out.println("Line = " + line);
                                            array_size++;
                                        }

                                        br.close();

                                        String[] filenames = new String[array_size];
                                        int size;
                                        br = new BufferedReader(new FileReader(files));
                                        int counte = 0;
                                        while ((line = br.readLine()) != null) {

                                            filenames[counte] = line;
                                            System.out.println("Line = " + filenames[counte]);
                                            counte++;
                                        }

                                        FileOutputStream fos2 = new FileOutputStream(files);
                                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos2));
                                        size = counte;

                                        for(int i=0;i<size;i++)
                                        {
                                            bw.write(filenames[i]);
                                            bw.newLine();
                                        }

                                        bw.write(filename);
                                        bw.newLine();

                                    byte[] received_file = new byte[65536];
                                    int finished_writing = (int) input.readObject();
                                    System.out.println("Received Confirmation of File Transfer");
                                    FileOutputStream fos = new FileOutputStream(filename);
                                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                                    System.out.println("Reading File now");
                                    File to_make = new File(filename);
                                    FileOutputStream converter = new FileOutputStream(to_make);
                                    converter.getFD().sync();
                                    int bytesRead;
                                    byte[] bigarray = new byte[65536];

                                    do {
                                        bytesRead = is.read(received_file, 0, received_file.length);
                                        System.out.println("test bytesRead = " + bytesRead);
                                        int counter = 0;
                                        for(int i=0;i<bytesRead;i++)
                                        {
                                            bigarray[i] = received_file[counter];
                                            counter++;
                                        }
                                        for(int i=0;i<65536;i++) {
                                            converter.write(bigarray[i]);
                                        }

                                        converter.getFD().sync();
                                        converter.flush();
                                        System.out.println("bytesRead = " + bytesRead);

                                    }while(bytesRead == 65536);

                                    System.out.println("Received byte_array: " + bytesRead + " bytes read.");

                                    is.close();
                                    fos.close();
                                    bos.close();




                                    System.out.println("Received File: " + filename);
                                    converter.close();
                                    file_client.close();
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
                                long counter = 0;
                                try
                                {

                                ServerSocket fileserverSocket = new ServerSocket(33334);
                                System.out.println("File Transfer Server Setup, waiting...");
                                Info temp = new Info();
                                temp.setTag(1);
                                output.writeObject(temp);

                                    Socket file_client = fileserverSocket.accept();
                                    OutputStream os = file_client.getOutputStream();
                                    String filename = (String) input.readObject();
                                    File file_to_send = new File(filename);//SET IT
                                    //output.writeObject(file_to_send.getName());
                                    byte[] array_to_send  = new byte [65536];
                                    output.writeObject(array_to_send.length);
                                    System.out.println("New File transfer connection established");
                                    if(file_to_send.length() < 65536) {
                                        array_to_send  = new byte [65536];
                                        FileInputStream fis = new FileInputStream(file_to_send);
                                        BufferedInputStream bis = new BufferedInputStream(fis);
                                        bis.read(array_to_send, 0, array_to_send.length);
                                        try {
                                            Thread.sleep(100);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        os.write(array_to_send, 0, array_to_send.length);
                                        os.close();
                                        bis.close();
                                        fis.close();
                                        output.writeObject(5);
                                        System.out.println("Confirmed");
                                        file_client.close();
                                    }
                                    else
                                    {
                                        counter = file_to_send.length();
                                        System.out.println(file_to_send.length());
                                        array_to_send  = new byte [65536];
                                        FileInputStream fis = new FileInputStream(file_to_send);
                                        BufferedInputStream bis = new BufferedInputStream(fis);

                                        do{
                                            array_to_send = new byte[65536];
                                            bis.read(array_to_send, 0, array_to_send.length);
                                            try {
                                                Thread.sleep(100);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            os.write(array_to_send,0,array_to_send.length);
                                            System.out.println("Writing to OutputStream");
                                            counter -= 65536;
                                            System.out.println(counter);
                                        }while(counter > 65536);
                                        System.out.println("Leaving Loop");
                                        array_to_send = new byte[(int) counter];
                                        System.out.println(array_to_send.length);
                                        bis.read(array_to_send,0,array_to_send.length);
                                        try {
                                            Thread.sleep(100);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        System.out.println("BIS");
                                        os.write(array_to_send, 0, array_to_send.length);
                                        System.out.println("OS");
                                        bis.close();
                                        fis.close();
                                        os.close();
                                        output.writeObject(5);
                                        System.out.println("Confirmed");
                                        file_client.close();
                                        fileserverSocket.close();

                                    }

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

                                //logic for handling request 8 (Request file metadata)
                                File files = new File("C:/Users/Justin/AndroidStudioProjects/CSCI342-Ripo2");
                                File[] file_list = files.listFiles();
                                boolean found = false;
                                for (int i = 0; i < file_list.length; i++) {
                                    if (file_list[i].isFile())
                                    {
                                        if(file_list[i].equals("Metadata"))
                                        {
                                            found = true;
                                        }
                                    }
                                }
                                if(found == false)
                                {
                                    new File("C:/Users/Justin/AndroidStudioProjects/CSCI342-Ripo2/Metadata.txt");
                                }
                                String[] filenames;
                                int array_size = 0;
                                files = new File("C:/Users/Justin/AndroidStudioProjects/CSCI342-Ripo2/Metadata.txt");
                                try  {
                                    BufferedReader br = new BufferedReader(new FileReader(files));
                                    String line;
                                    while ((line = br.readLine()) != null) {

                                        System.out.println("Line = " + line);
                                        array_size++;
                                    }

                                    br.close();

                                    filenames = new String[array_size];
                                    br = new BufferedReader(new FileReader(files));
                                    int counter = 0;
                                    while ((line = br.readLine()) != null) {

                                        filenames[counter] = line;
                                        System.out.println("Line = " + filenames[counter]);
                                        counter++;
                                    }

                                    output.writeObject(array_size);
                                    output.writeObject(filenames);
                                    int acknowledge = (int) input.readObject();

                                }catch(Exception e)
                                {
                                    e.printStackTrace();
                                }



                            }
                            else if(hello.tag == 9) {
                                //logic for handling request 9 (retrieve ALL subjects)
                                String[] subjects = MySQL_Handler.getALLSubjects();
                                int size = subjects.length;
                                output.writeObject(size);
                                System.out.println("Wrote " + size);
                                output.writeObject(subjects);
                                System.out.println("Sent the array");
                            }
                            else if(hello.tag == 10)
                            {
                                //logic for handling request 10 (retrieve specific subjects)
                                String username = (String) input.readObject();
                                String[] subjects = MySQL_Handler.getEnrolledSubjects(username);
                                int size = subjects.length;
                                output.writeObject(size);
                                System.out.println("Wrote " + size);
                                output.writeObject(subjects);
                                System.out.println("Sent the array");
                            }
                            else if(hello.tag == 11)
                            {
                                String username = (String) input.readObject();
                                String subject = (String) input.readObject();
                                System.out.println(username);
                                System.out.println(subject);
                                boolean already_enrolled = MySQL_Handler.IsEnrolledInSubject(username,subject);
                                if(already_enrolled)
                                {
                                    System.out.println("Writing failure");
                                    output.writeObject(0);
                                }
                                else {
                                    boolean success = MySQL_Handler.enrollInSubject(username, subject);
                                    if (success) {
                                        System.out.println("Writing success");
                                        output.writeObject(5);
                                    } else {
                                        System.out.println("Writing failure");
                                        output.writeObject(0);
                                    }
                                }
                            }
                            else if(hello.tag == 12)
                            {
                                String username = (String) input.readObject();
                                String subject = (String) input.readObject();
                                System.out.println(username);
                                System.out.println(subject);
                                boolean already_enrolled = MySQL_Handler.IsEnrolledInTutorial(username, subject);
                                if(already_enrolled)
                                {
                                    System.out.println("Writing failure");
                                    output.writeObject(0);
                                }
                                else {
                                    boolean success = MySQL_Handler.enrollInTutorial(username, subject);
                                    if (success) {
                                        System.out.println("Writing success");
                                        output.writeObject(5);
                                    } else {
                                        System.out.println("Writing failure");
                                        output.writeObject(0);
                                    }
                                }
                            }
                            else if(hello.tag == 13)
                            {
                                String username = (String) input.readObject();
                                String subject = (String) input.readObject();
                                System.out.println(username);
                                System.out.println(subject);
                                boolean already_enrolled = MySQL_Handler.IsEnrolledInSubject(username, subject);
                                if(!already_enrolled)
                                {
                                    System.out.println("Writing failure");
                                    output.writeObject(0);
                                }
                                else {
                                    boolean success = MySQL_Handler.dropSubject(username, subject);
                                    if (success) {
                                        System.out.println("Writing success");
                                        output.writeObject(5);
                                    } else {
                                        System.out.println("Writing failure");
                                        output.writeObject(0);
                                    }
                                }
                            }
                            else if(hello.tag == 14)
                            {
                                System.out.println("Entering AnnouncementGet");
                                String subject = (String) input.readObject();
                                System.out.println("Received Subject");
                                output.writeObject(5);
                                System.out.println("Writing Confirmation");

                                String[] announcements = MySQL_Handler.getAnnouncements(subject);
                                int size = announcements.length;

                                output.writeObject(size);
                                System.out.println("Writing Size");
                                output.writeObject(announcements);
                                System.out.println("Writing Array");

                            }
                            else if(hello.tag == 15)
                            {
                                System.out.println("Updating Personal Information");

                                String user = (String) input.readObject();
                                String fname = (String) input.readObject();
                                String lname = (String) input.readObject();
                                String auth = (String) input.readObject();
                                String email = (String) input.readObject();
                                String phone = (String) input.readObject();
                                String address = (String) input.readObject();

                                MySQL_Handler.setPersonalInfo(user,fname,lname,auth,email,phone,address);

                                System.out.println("Updated Personal Information");
                            }
                            else if(hello.tag == 16)
                            {
                                String user = (String) input.readObject();
                                String[] info_p = MySQL_Handler.getPersonalInfo(user);
                                System.out.print("Retrieved Personal Info");
                                output.writeObject(info_p);
                                System.out.println("Sent Personal Info");
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
