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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import layout.UploadAssignment;
import layout.UploadMarks;
import layout.ViewEnrolledStudents;
import layout.ViewGrades;
import layout.ViewResources;

public class SubjectView extends Activity
{//implements ViewResources.onDataBaseAccessListener, ViewEnrolledStudents.StudentListHandler

    Intent previous;
    Connection connect;
    Bundle args;
    EditText getmark;
    EditText getmax;
    FragmentManager fm = getFragmentManager();
    Fragment frag;
    FrameLayout tabs;
    Protocol User;
    Handler myHandler;
    public static final int PORT = 33333;
    public static final int PORT2 = 33334;
    public static final String addr = "172.18.17.120";
   public int login_token=0;

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        previous = getIntent();
        login_token = previous.getIntExtra("Token",login_token);
        Log.i("Token SV"," "+login_token);
        User = (Protocol) previous.getSerializableExtra("User");
        connect = (Connection) previous.getSerializableExtra("Connection");

        String subject = previous.getStringExtra("Subject Name");



        if(User.authority.equals("Teacher"))
        {
            setContentView(R.layout.activity_subject_view_teacher);
            TextView temp = (TextView) findViewById(R.id.SVT_subjectname_textview);
            temp.setText(subject);

            frag = new ViewResources();
            FragmentTransaction ft = fm.beginTransaction();

            tabs = (FrameLayout) findViewById(R.id.SVT_tabsview_framelayout);
            tabs.removeAllViews();

            ft.replace(R.id.SVT_tabsview_framelayout, frag).commit();
        }
        else
        {
            setContentView(R.layout.activity_subject_view);
            TextView temp = (TextView) findViewById(R.id.SV_subjectname_textview);
            temp.setText(subject);

            frag = new ViewResources();
            FragmentTransaction ft = fm.beginTransaction();

            tabs = (FrameLayout) findViewById(R.id.SV_framelayout);
            tabs.removeAllViews();
            ft.replace(R.id.SV_framelayout, frag).commit();
        }

        myHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                Log.i("CAUGHT!", "Message Caught by SubViewer Handler");

                if(msg.what == 4)//ViewGrades
                {
                int size = msg.arg1;
                    Toaster("Loading Marks");
                    String[] Getmark = (String[]) msg.obj;
                    Log.i("VIEW","IT GOT HERE");
                    ArrayList<String> Marks = new ArrayList<String>();
                    for(int i =0; i < size ; i++)
                    {
                        Marks.add(Getmark[i]);
                    }
                    Populate_Marks(Marks);
                }
                else if(msg.what == 5)//View Students TEACHER VIEW
                {   int size = msg.arg1;
                    Log.i("size is 5what:",""+size);
                    Toaster("Loading Students");
                    String[] GetNames = (String[]) msg.obj;
                    ArrayList<String> Names = new ArrayList<String>();
                    for(int i =0; i < size ; i++)
                    {
                        Names.add(GetNames[i]);
                    }
                    Populate_Names(Names);
                }
                else if(msg.what==6)
                {
                    Toaster("Marks Uploaded Successfully!");
                    success_func();
                    frag = new ViewEnrolledStudents();
                    FragmentTransaction ft = fm.beginTransaction();
                    tabs.removeAllViews();
                    ft.replace(R.id.SVT_tabsview_framelayout, frag).commit();
                    get_students_server viewstudents = new get_students_server(login_token);
                    viewstudents.start();
                }
                else if(msg.what==7)
                {
                    Toaster("File Uploaded Successfully!");
                }
                else if(msg.what==8)
                {
                    Toaster("Downloaded File Successfully!");
                }
                else {
                    Log.i("Bad", "From SubViewer");
                    Toaster("Failed to Connect to server");
                }
            }
        };

    }

    public void success_func()
    {
        EditText insertmark = (EditText) findViewById(R.id.UM_entermarks_editext) ;
        EditText insertmax= (EditText) findViewById(R.id.UM_entermax_editext);
        InputMethodManager cleanmark = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        cleanmark.hideSoftInputFromWindow(insertmark.getWindowToken(), 0);
        InputMethodManager cleanmax = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        cleanmax.hideSoftInputFromWindow(insertmax.getWindowToken(), 0);

    }
    public void Populate_Names(ArrayList<String> Names)
    {
       final ListView lv = (ListView) findViewById(R.id.VES_list_listview);
        ArrayAdapter<String> AdapterName = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,Names);
        lv.setAdapter(AdapterName);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
                                  {
                                      public void onItemClick(AdapterView<?> arg0,View v , int position, long arg3)
                                      {
                                          frag = new UploadMarks();
                                          FragmentTransaction ft = fm.beginTransaction();
                                          tabs.removeAllViews();

                                          ft.replace(R.id.SVT_tabsview_framelayout, frag).commit();
                                      }
                                  }
        );
    }
    public void Populate_Marks(ArrayList<String> Marks)
    {
        final ListView lv = (ListView) findViewById(R.id.SV_Marks_listview);
        ArrayAdapter<String> AdapterMARK = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,Marks);
        lv.setAdapter(AdapterMARK);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
                                  {
                                      public void onItemClick(AdapterView<?> arg0,View v , int position, long arg3)
                                      {

                                      }
                                  }
        );
    }

    private class download_resources extends Thread
    {
        int token;
        int bytesRead;
        int position;
        int counter;
        int i;
        int read_so_far;
        byte[] not_so_big_Array = new byte[65536];
        public download_resources(int in_token)
        {
            token = in_token;
            position = 0;
            counter = 0;
            i=0;
            read_so_far=0;
        }
        //@Override
         public void run()
        {
           try {
                Socket with_server = new Socket(InetAddress.getByName(addr), PORT);
                ObjectInputStream input = new ObjectInputStream(with_server.getInputStream());
                ObjectOutputStream output = new ObjectOutputStream(with_server.getOutputStream());

                Log.i("File Download","Requesting download file");
                Info request = new Info();
                request.setTag(7);
                request.setToken(token);
                output.writeObject(request);

                Info response = (Info)input.readObject();
                if(response.tag == 1)
                {
                    Socket recieve_file = new Socket(InetAddress.getByName(addr),PORT2);
                   // int filesize = (int) input.readObject();
                    String filename = (String) input.readObject();
                    byte[] recieve_byte = new byte[65536];
                    InputStream is = recieve_file.getInputStream();
                    FileOutputStream fos = new FileOutputStream(filename);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    File to_make = new File(filename);//WHERE?!
                    FileOutputStream converter = new FileOutputStream(to_make);
                    converter.getFD().sync();
                    System.out.println("Reading File now");

                    do {
                        bytesRead = is.read(recieve_byte, 0, recieve_byte.length);
                        for(i=0;i<bytesRead;i++)
                        {
                            not_so_big_Array[i] = recieve_byte[counter];
                            counter++;
                        }
                        for(i=0;i<position;i++) {
                            converter.write(not_so_big_Array[i]);
                        }
                        position += bytesRead;
                        converter.getFD().sync();
                        converter.flush();
                        Log.i("bytesRead = " ,""+ bytesRead);
                        Log.i("Position = " , ""+position);


                    }while(bytesRead == 65536);
                    Log.i("Received byte_array: ",  bytesRead + " bytes read.");
                    is.close();
                    fos.close();
                    bos.close();
                    System.out.println("Received File: " + filename);
                    converter.close();
                    with_server.close();

                }
               else
                {
                    Log.i("SERVER", "Rejected Download request");
                    return;
                }
               Message msg= myHandler.obtainMessage();
               msg.what=8;
               myHandler.sendMessage(msg);

            }catch(EOFException e)
           {
               e.printStackTrace();
           }catch(IOException e)
           {
               e.printStackTrace();
           }
           catch(ClassNotFoundException e)
           {
               e.printStackTrace();
           }
        }
    }
    private class upload_resources extends Thread
    {
        int token;

        public upload_resources(int login_token)
        {
            token = login_token;
        }

        @Override
        public void run()
        {
            try
            {
                Socket with_server = new Socket(InetAddress.getByName(addr),PORT);
                ObjectInputStream input = new ObjectInputStream(with_server.getInputStream());
                ObjectOutputStream output = new ObjectOutputStream(with_server.getOutputStream());

                Log.i("FILE UPLOAD", "Requesting File Upload");
                Info send_request = new Info();
                send_request.setTag(6);
                send_request.setToken(token);
                output.writeObject(send_request);

                Info response = (Info)input.readObject();
                if(response.tag == 1)
                {
                    Socket send_file = new Socket(InetAddress.getByName(addr),PORT2);
                    File file_to_send = new File("/sdcard/test.txt");
                    byte[] array_to_send  = new byte [(int)file_to_send.length()];
                    output.writeObject(array_to_send.length);
                    output.writeObject(file_to_send.getName());
                    FileInputStream fis = new FileInputStream(file_to_send);
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    bis.read(array_to_send,0,array_to_send.length);
                    OutputStream os = send_file.getOutputStream();
                    os.write(array_to_send,0,array_to_send.length);
                    os.close();
                    send_file.close();
                }
                else
                {
                    Log.i("SERVER", "Rejected upload request");
                    return;
                }

                Log.i("MESSAGE", "Sending message to main thread");
                Message msg= myHandler.obtainMessage();
                msg.what=7;
                myHandler.sendMessage(msg);

            }
            catch(UnknownHostException e)
            {
                e.printStackTrace();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
            catch(ClassNotFoundException e)
            {
                e.printStackTrace();
            }
        }
    }

    private class upload_marks_server extends Thread
    {
        int mark;
        int max;
        int token;
        public upload_marks_server(int in_mark, int in_max, int in_token)
        {
            mark = in_mark;
            max = in_max;
            token =in_token;
        }
        @Override
        public void run()
        {
            try
            {
                Socket with_server = new Socket(InetAddress.getByName(addr),PORT);
                ObjectInputStream input = new ObjectInputStream(with_server.getInputStream());
                ObjectOutputStream output = new ObjectOutputStream(with_server.getOutputStream());

                Info send_request = new Info();
                send_request.setTag(5);
                send_request.setToken(token);
                output.writeObject(send_request);
                Log.i("IN THREAD","mark : "+ mark + " max "+ max);
                output.writeObject(mark);
                output.writeObject(max);

                Message msg= myHandler.obtainMessage();
                msg.what=6;
                myHandler.sendMessage(msg);
            }
            catch(UnknownHostException e)
            {
                e.printStackTrace();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    private class get_students_server extends Thread
    {
        String[] Names;
        int token;
        int size;
        public get_students_server(int in_token)
        {
            token = in_token;
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
                request_size.setTag(4);
                request_size.setToken(token);
                output.writeObject(request_size);

                size = (int) input.readObject();
                Names = new String[size];
                Names= (String[])input.readObject();

                Message msg= myHandler.obtainMessage();
                msg.what=5;
                msg.arg1=size;
                msg.obj = (Object) Names;
                myHandler.sendMessage(msg);
            }
            catch(UnknownHostException e)
            {
                e.printStackTrace();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            } catch (ClassNotFoundException e)
            {
                e.printStackTrace();
            }
        }
    }


    private class get_marks_server extends Thread
    {
        String[] Marks;
        String[] Max;
        int token;
        int size;
        public get_marks_server(int in_token)
        {
            token=in_token;

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
                request_size.setTag(3);
                request_size.setToken(token);
                output.writeObject(request_size);

                size = (int) input.readObject();
                Marks = new String[size];
                Marks= (String[])input.readObject();

                Max = new String [size];
                Max = (String[])input.readObject();

                String TotalMark[] = new String[size];

                for(int i =0 ; i<size;i++)
                {
                    TotalMark[i] = Marks[i] + "/" + Max[i];
                }
                Message msg= myHandler.obtainMessage();
                msg.what=4;
                msg.arg1=size;
                msg.obj = (Object) TotalMark;
                myHandler.sendMessage(msg);
            }
            catch(UnknownHostException e)
            {
                e.printStackTrace();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            } catch (ClassNotFoundException e)
            {
                e.printStackTrace();
            }

        }
    }

    //-----------------------------------Start of Toaster
    public void Toaster(String input)
    {
        Toast.makeText(this,input,Toast.LENGTH_SHORT).show();
    }
    //--------------------------------------End of Toaster
    public void switchToViewResources(View v)
    {
        frag = new ViewResources();
        FragmentTransaction ft = fm.beginTransaction();
        tabs.removeAllViews();


        args = new Bundle();
        args.putString("subject", ( (TextView) findViewById(R.id.SV_subjectname_textview)).getText().toString() );
        frag.setArguments(args);

        ft.replace(R.id.SV_linearlayout, frag).commit();
    }

    public void UploadNewResource(View v)
    {

        String auth = User.authority;

        String name = "null";

        if ( auth.equals("Teacher") ) name = ((EditText)findViewById(R.id.UR_ResourceName_edittext)).getText().toString();
        else name = ((EditText)findViewById(R.id.UR_ResourceName_edittext)).getText().toString();


        String subject = ((TextView) findViewById(R.id.SVT_subjectname_textview)).getText().toString();



    }

    public void UploadTest(View v)
    {
        Log.i("CLIENT", "Starting Upload Test");
        upload_resources x = new upload_resources(login_token);
        x.start();
    }

    public void UploadNewGrade(View v)
    {

        String auth = User.authority;

        String name = ((TextView)findViewById(R.id.VESD_textView_studentName)).getText().toString();
        String gradable = ((EditText)findViewById(R.id.VESD_editText_Gradable)).getText().toString();
        int grade = Integer.parseInt(((EditText) findViewById(R.id.VESD_editText_Grade)).getText().toString());
        String subject = ((TextView) findViewById(R.id.SVT_subjectname_textview)).getText().toString();


    }

    public void switchToViewResourcesTeacher(View v)
    {
        frag = new ViewResources();
        FragmentTransaction ft = fm.beginTransaction();
        tabs.removeAllViews();

        Bundle args = new Bundle();
        args.putString("subject", ((TextView) findViewById(R.id.SVT_subjectname_textview)).getText().toString());
        frag.setArguments(args);

        ft.replace(R.id.SVT_tabsview_framelayout, frag).commit();
        //GetFromDatabase();
    }
    public void switchToViewGrades(View v)
    {
        frag = new ViewGrades();
        FragmentTransaction ft = fm.beginTransaction();
        tabs.removeAllViews();
        ft.replace(R.id.SV_linearlayout, frag).commit();

        get_marks_server getmarks = new get_marks_server(login_token);
        getmarks.start();
        Log.i("VIEW","IT GOT HERE");
    }
    public void switchtoViewResources(View v)
    {
       /* frag = new ViewResources();
        FragmentTransaction ft = fm.beginTransaction();
        tabs.removeAllViews();
        ft.replace(R.id.VR_MainLayout_relativelayout, frag).commit();
*/
        download_resources getresources = new download_resources(login_token);
        getresources.start();
        Log.i("VIEW","Resources");
    }
    public void switchToUploadAssignment(View v)
    {
        frag = new UploadAssignment();
        FragmentTransaction ft = fm.beginTransaction();
        tabs.removeAllViews();
        ft.replace(R.id.SV_linearlayout, frag).commit();
    }

    public void switchToUploadResources(View v)
    {
        frag = new UploadResources();
        FragmentTransaction ft = fm.beginTransaction();
        tabs.removeAllViews();
        ft.replace(R.id.SVT_tabsview_framelayout, frag).commit();
    }

    public void switchToViewEnrolledStudents(View v)
    {
        frag = new ViewEnrolledStudents();
        FragmentTransaction ft = fm.beginTransaction();
        tabs.removeAllViews();
        ft.replace(R.id.SVT_tabsview_framelayout, frag).commit();
        get_students_server viewstudents = new get_students_server(login_token);
        viewstudents.start();

    }
    public void insertmarks(View v)
    {
       getmark = (EditText) findViewById(R.id.UM_entermarks_editext);
        getmax = (EditText) findViewById(R.id.UM_entermax_editext);

        int mark =Integer.parseInt(getmark.getText().toString()) ;
        int max = Integer.parseInt(getmax.getText().toString()) ;
        Log.i("NOT THREAD","mark : "+ mark + " max "+ max);
        upload_marks_server uploadmarks = new upload_marks_server(mark,max,login_token);
        uploadmarks.start();
        // get_students_server viewstudents = new get_students_server(login_token);
        //  viewstudents.start();
    }
}
