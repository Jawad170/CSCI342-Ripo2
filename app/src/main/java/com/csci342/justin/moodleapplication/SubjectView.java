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
    int Resourcesize;
    String[] Resources;
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
    public static final String addr = "172.18.26.150";
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

        final String subject = previous.getStringExtra("Subject Name");



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
                    TextView textView = (TextView) findViewById(R.id.SVT_subjectname_textview) ;
                    String teachername = textView.getText().toString();
                    success_func();
                    frag = new ViewEnrolledStudents();
                    FragmentTransaction ft = fm.beginTransaction();
                    tabs.removeAllViews();
                    ft.replace(R.id.SVT_tabsview_framelayout, frag).commit();
                    get_students_server viewstudents = new get_students_server(login_token,teachername);
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
                else if(msg.what==9)
                {
                    int size=msg.arg1;
                    Toaster("Loading Subjects..");
                    String[] GetResources = (String[]) msg.obj;

                    for(int i = 0 ;i<Resourcesize;i++)
                    {
                        Log.i(" string array : ", GetResources[i]);
                    }
                    ArrayList<String> get_Resources  = new ArrayList<String>();

                    for(int i =0; i < size ; i++)//populate array.
                    {

                        get_Resources.add(GetResources[i]);
                    }
                    for(int i = 0 ;i<Resourcesize;i++)
                    {
                        Log.i(" arraylist : ", get_Resources.get(i));
                    }
                    Populate(get_Resources);


                }
                else if(msg.what==10)
                {
                    Toaster("You are now Enrolled at: Monday 3:30 -> 4:30 Tutorial");
                }
                else if(msg.what==11)
                {
                    TextView textView = (TextView) findViewById(R.id.SV_subjectname_textview);
                    String subjectname = textView.getText().toString();
                    Toaster("Successfully Dropped +"+ subjectname);
                    successfuldrop();
                }
                else if(msg.what==12)
                {
                    Log.i("StudentAnnHandler "," StudentAnnHandler");
                    int size=msg.arg1;
                    if(size == 0)
                    {

                        ArrayList<String> Announcements = new ArrayList<String>();
                        Announcements.add("There are no Current announcements");
                        PopulateAnnouncements(Announcements);
                    }
                    else {
                        Log.i("size ", " " + size);
                        String[] Getann = (String[]) msg.obj;

                        ArrayList<String> Announcements = new ArrayList<String>();

                        for (int i = 0; i < size; i++)//populate array.
                        {

                            Announcements.add(Getann[i]);
                        }
                        PopulateAnnouncements(Announcements);
                    }
                }
                else if(msg.what==0)
                {
                    TextView textView = (TextView) findViewById(R.id.SV_subjectname_textview);
                    String subjectname = textView.getText().toString();
                    Toaster("Failed to enroll at:" + subjectname+ " Tutorial");
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
        final ListView lv = (ListView) findViewById(R.id.VG_listView_Grades);
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
        String filename;
        int counter;
        int i;
        int read_so_far;
        byte[] not_so_big_Array = new byte[65536];
        public download_resources(int in_token, String in_filename)
        {
            token = in_token;
            filename = in_filename;
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
               Log.i("Got  ", "Response from server");
                if(response.tag == 1)
                {
                    Socket recieve_file = new Socket(InetAddress.getByName(addr),PORT2);
                    InputStream is = recieve_file.getInputStream();
                    output.writeObject(filename);
                    int filesize = (int) input.readObject();
                    int confirmation = (int)input.readObject();

                    Log.i("Filesize  ", ""+filesize);


                    byte[] recieve_byte = new byte[65536];


                    File to_make = new File("/sdcard/"+filename);//WHERE?!
                    FileOutputStream converter = new FileOutputStream(to_make);
                    converter.getFD().sync();
                    System.out.println("Reading File now");

                    do {
                        bytesRead = is.read(recieve_byte, 0, recieve_byte.length);
                        Log.i("bytesRead  ", ""+ bytesRead);
                        for(i=0;i<bytesRead;i++)
                        {
                            not_so_big_Array[i] = recieve_byte[i];
                            counter++;
                        }
                        for(i=0;i<bytesRead;i++) {
                            converter.write(not_so_big_Array[i]);
                        }

                        converter.getFD().sync();
                        converter.flush();
                        Log.i("bytesRead = " ,""+ bytesRead);



                    }while(bytesRead == 65536);
                    Log.i("Received byte_array: ",  bytesRead + " bytes read.");
                    is.close();


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
               with_server.close();
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
        long counter;
        public upload_resources(int login_token)
        {
            token = login_token;
            counter = 0;
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
                    OutputStream os = send_file.getOutputStream();
                    File file_to_send = new File("/sdcard/test.txt");
                    Log.i("SIZE OF", "byte "+ file_to_send.length());
                    if(file_to_send.length() < 65536) {
                        byte[] array_to_send = new byte[(int) file_to_send.length()];
                        output.writeObject(array_to_send.length);
                        output.writeObject(file_to_send.getName());
                        FileInputStream fis = new FileInputStream(file_to_send);
                        BufferedInputStream bis = new BufferedInputStream(fis);
                        bis.read(array_to_send, 0, array_to_send.length);
                        os.write(array_to_send, 0, array_to_send.length);
                        os.close();
                        output.writeObject(5);
                        Log.i("OUT OF WRITE", " "+5);
                        send_file.close();
                    }
                    else
                    {
                        counter = file_to_send.length();
                        byte[] array_to_send = new byte[65536];
                        output.writeObject(array_to_send.length);
                        output.writeObject(file_to_send.getName());
                        FileInputStream fis = new FileInputStream(file_to_send);
                        BufferedInputStream bis = new BufferedInputStream(fis);

                        do {
                            array_to_send = new byte[65536];

                            bis.read(array_to_send, 0, array_to_send.length);
                            Thread.sleep(100);
                            Log.i("OUTPU","BIS : "+ counter + " array . " + array_to_send.length);
                            os.write(array_to_send, 0, array_to_send.length);
                            Log.i("OUTPU","OS : "+ counter + " array . " + array_to_send.length);
                            counter -=65536;
                            Log.i("OUTPU","counter : "+ counter + " array . " + array_to_send.length);
                        }while(counter > 65536);
                        Log.i("OUTPU","counter : "+ counter + " array . " + array_to_send.length);
                        array_to_send = new byte[(int)counter];
                        bis.read(array_to_send, 0, array_to_send.length);
                        os.write(array_to_send, 0, array_to_send.length);
                        os.close();
                        output.writeObject(5);
                        Log.i("OUTPU","counter : "+ counter + " array . " + array_to_send.length);
                        send_file.close();
                    }
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
                with_server.close();

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
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

    private class upload_marks_server extends Thread
    {
        int mark;
        int max;
        int token;
        String subjectname;
        public upload_marks_server(int in_mark, int in_max, int in_token,String insubname)
        {
            subjectname = insubname;
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
                output.writeObject(subjectname);
                output.writeObject(User.getLogin());
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
    private class request_Metadata extends  Thread
    {

    }
    private class get_students_server extends Thread
    {
        String[] Names;
        int token;
        String subjectname;
        int size;
        public get_students_server(int in_token, String insubname)
        {
            subjectname = insubname;
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
                output.writeObject(subjectname);
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
        String subjectname;
        String username;
        public get_marks_server(int in_token, String insubejctname, String inusername)
        {
            token=in_token;
            subjectname = insubejctname;
            username=inusername;
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
                output.writeObject(subjectname);
                output.writeObject(username);
                size = (int) input.readObject();
                Marks = new String[size];
                Marks= (String[])input.readObject();

               // Max = new String [size];
               // Max = (String[])input.readObject();

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
        TextView textView = (TextView) findViewById(R.id.SV_subjectname_textview);
        String subname = textView.getText().toString();
        frag = new ViewGrades();
        FragmentTransaction ft = fm.beginTransaction();
        tabs.removeAllViews();
        ft.replace(R.id.SV_linearlayout, frag).commit();

        get_marks_server getmarks = new get_marks_server(login_token,subname,User.getLogin());
        getmarks.start();
        Log.i("VIEW","IT GOT HERE");
    }
    public void switchtoViewResources(View v)
    {
        frag = new ViewResources();
        FragmentTransaction ft = fm.beginTransaction();
        tabs.removeAllViews();
        ft.replace(R.id.SV_framelayout, frag).commit();

        getresourcesthread temp = new getresourcesthread();
        temp.start();
    }

    private class getresourcesthread extends Thread {

        public void getresourcesthread()
        {

        }

        @Override
        public void run() {
            try {
                Socket with_server = new Socket(InetAddress.getByName(addr), PORT);
                ObjectInputStream input = new ObjectInputStream(with_server.getInputStream());
                ObjectOutputStream output = new ObjectOutputStream(with_server.getOutputStream());

                Info request_Meta_data = new Info();
                request_Meta_data.setTag(8);
                request_Meta_data.setToken(login_token);
                output.writeObject(request_Meta_data);

                Resourcesize = (int) input.readObject();
                Resources = new String[Resourcesize];
                Resources = (String[]) input.readObject();
                Log.i(" RESOURCE SIZE : ",""+ Resourcesize);

                for(int i = 0 ;i<Resourcesize;i++)
                {
                    Log.i(" Files : ", Resources[i]);
                }
                Message msg = myHandler.obtainMessage();
                msg.what = 9;
                msg.arg1 = Resourcesize;
                msg.obj = (Object) Resources;
                myHandler.sendMessage(msg);

                output.writeObject(5);
                with_server.close();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }



            Log.i("VIEW", "Resources");
        }
    }

    public void Populate(ArrayList<String> Announcements)
    {
        ListView lv = (ListView) findViewById(R.id.VR_infolist_listview);
        final ArrayAdapter<String> AdapterAnn = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,Announcements);
        lv.setAdapter(AdapterAnn);
        for(int i = 0 ;i<Resourcesize;i++)
        {
            Log.i(" arraylist : ", Announcements.get(i));
        }
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
                                  {
                                      public void onItemClick(AdapterView<?> arg0,View v , int position, long arg3)
                                      {
                                          download_resources getresources = new download_resources(login_token, (String)arg0.getItemAtPosition(position));
                                          getresources.start();
                                          Log.i("Click ", ""+ (String)arg0.getItemAtPosition(position));
                                      }
                                  }
        );
    }
    public void PopulateAnnouncements(ArrayList<String> Announcements)
    {
        ListView lv = (ListView) findViewById(R.id.UA_announcement_listview);
        final ArrayAdapter<String> AdapterAnn = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,Announcements);
        lv.setAdapter(AdapterAnn);
        for(int i = 0 ;i<Resourcesize;i++)
        {
            Log.i(" arraylist : ", Announcements.get(i));
        }
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
                                  {
                                      public void onItemClick(AdapterView<?> arg0,View v , int position, long arg3)
                                      {
                                          download_resources getresources = new download_resources(login_token, (String)arg0.getItemAtPosition(position));
                                          getresources.start();
                                          Log.i("Click ", ""+ (String)arg0.getItemAtPosition(position));
                                      }
                                  }
        );
    }
    public void switchToUploadAssignment(View v)
    {
        frag = new UploadAssignment();
        FragmentTransaction ft = fm.beginTransaction();
        tabs.removeAllViews();
        ft.replace(R.id.SV_framelayout, frag).commit();
        TextView textView = (TextView) findViewById(R.id.SV_subjectname_textview);
        String name = textView.getText().toString();
        Log.i("Name is : ", name);
        studentannouncement temp = new studentannouncement(login_token,name);
        temp.start();
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
        TextView textView = (TextView) findViewById(R.id.SVT_subjectname_textview) ;
        String teachername = textView.getText().toString();
        frag = new ViewEnrolledStudents();
        FragmentTransaction ft = fm.beginTransaction();
        tabs.removeAllViews();
        ft.replace(R.id.SVT_tabsview_framelayout, frag).commit();
        get_students_server viewstudents = new get_students_server(login_token,teachername);
        viewstudents.start();

    }
    public void insertmarks(View v)
    {
       getmark = (EditText) findViewById(R.id.UM_entermarks_editext);
        getmax = (EditText) findViewById(R.id.UM_entermax_editext);
        TextView textView = (TextView) findViewById(R.id.SVT_subjectname_textview);
        String teachername = textView.getText().toString();
        int mark =Integer.parseInt(getmark.getText().toString()) ;
        int max = Integer.parseInt(getmax.getText().toString()) ;
        Log.i("NOT THREAD","mark : "+ mark + " max "+ max);
        upload_marks_server uploadmarks = new upload_marks_server(mark,max,login_token,teachername);
        uploadmarks.start();
        // get_students_server viewstudents = new get_students_server(login_token);
        //  viewstudents.start();
    }
    private class dropsubject extends Thread
    {
        int token;
        int confirmation;
        String subjectname;
        public dropsubject(int intoken,String insubjectname)
        {
            token = intoken;
            subjectname = insubjectname;
        }
        @Override
        public void run()
        {
            try {
                Socket with_server = new Socket(InetAddress.getByName(addr), PORT);
                ObjectInputStream input = new ObjectInputStream(with_server.getInputStream());
                ObjectOutputStream output = new ObjectOutputStream(with_server.getOutputStream());

                Info request_size = new Info();
                request_size.setTag(13);
                request_size.setToken(token);
                output.writeObject(request_size);

                output.writeObject(User.getLogin());
                output.writeObject(subjectname);

                confirmation = (int) input.readObject();

                if (confirmation > 0) {
                    Message msg = myHandler.obtainMessage();
                    msg.what = 11;
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
    public void successfuldrop()
    {
        Intent i = new Intent(this, ListOfSubjects.class);
        i.putExtra("User", User);
        i.putExtra("Token",login_token);
        startActivity(i);
    }
    private class enroltutorialthread extends Thread
    {
        int token;
        int confirmation;
        String subjectname;
        public enroltutorialthread(int intoken,String insubjectname)
        {
            token = intoken;
            subjectname = insubjectname;
        }
        @Override
        public void run()
        {
            try {
                Socket with_server = new Socket(InetAddress.getByName(addr), PORT);
                ObjectInputStream input = new ObjectInputStream(with_server.getInputStream());
                ObjectOutputStream output = new ObjectOutputStream(with_server.getOutputStream());

                Info request_size = new Info();
                request_size.setTag(12);
                request_size.setToken(token);
                output.writeObject(request_size);

                output.writeObject(User.getLogin());
                output.writeObject(subjectname);

                confirmation = (int) input.readObject();

                if (confirmation > 0) {
                    Message msg = myHandler.obtainMessage();
                    msg.what = 10;
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
    private class studentannouncement extends Thread
    {
        int token;
        int confirmation;
        String subjectname;
        int size;
        String[] Subjects;
        public studentannouncement(int intoken,String insubjectname)
        {
            token = intoken;
            subjectname = insubjectname;
        }
        @Override
        public void run()
        {
            try {
                Socket with_server = new Socket(InetAddress.getByName(addr), PORT);
                ObjectInputStream input = new ObjectInputStream(with_server.getInputStream());
                ObjectOutputStream output = new ObjectOutputStream(with_server.getOutputStream());

                Info request_size = new Info();
                request_size.setTag(14);
                request_size.setToken(token);
                output.writeObject(request_size);

                output.writeObject(subjectname);
                Log.i("SUBNAME : ", subjectname);
                confirmation = (int) input.readObject();
                Log.i("Confirmation : ",""+ confirmation);

                if (confirmation > 0 ) {
                    size = (int) input.readObject();

                    Subjects = new String[size];
                    Subjects = (String[]) input.readObject();
                    Message msg = myHandler.obtainMessage();
                    msg.arg1 = size;
                    msg.obj = (Object) Subjects;
                    msg.what = 12;
                    Log.i("size = ", ""+msg.arg1);

                    Log.i("WHAT = ", ""+msg.what);
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
    public void dropsub(View v)
    {
        TextView textView = (TextView) findViewById(R.id.SV_subjectname_textview);
        String subjectname = textView.getText().toString();
        dropsubject temp = new dropsubject(login_token, subjectname);
        temp.start();
    }
    public void enrolltutorial(View v)
    {
        TextView textView = (TextView) findViewById(R.id.SV_subjectname_textview);
        String subjectname = textView.getText().toString();
        enroltutorialthread temp = new enroltutorialthread(login_token,subjectname);
        temp.start();
    }
}
