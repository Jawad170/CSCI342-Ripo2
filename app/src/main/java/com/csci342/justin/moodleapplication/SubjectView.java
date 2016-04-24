package com.csci342.justin.moodleapplication;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import layout.UploadAssignment;
import layout.ViewEnrolledStudents;
import layout.ViewEnrolledStudentsDetails;
import layout.ViewGrades;
import layout.ViewResources;

public class SubjectView extends Activity
{//implements ViewResources.onDataBaseAccessListener, ViewEnrolledStudents.StudentListHandler

    Intent previous;
    Connection connect;
    Bundle args;

    FragmentManager fm = getFragmentManager();
    Fragment frag;
    LinearLayout tabs;
    Protocol User;
    Handler myHandler;
    public static final int PORT = 33333;
    public static final String addr = "172.18.17.195";
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

            tabs = (LinearLayout) findViewById(R.id.SVT_tabsbar_linearlayout);
            tabs.removeAllViews();
            ft.replace(R.id.SVT_tabsbar_linearlayout, frag).commit();
        }
        else
        {
            setContentView(R.layout.activity_subject_view);
            TextView temp = (TextView) findViewById(R.id.SV_subjectname_textview);
            temp.setText(subject);

            frag = new ViewResources();
            FragmentTransaction ft = fm.beginTransaction();

            tabs = (LinearLayout) findViewById(R.id.SV_linearlayout);
            tabs.removeAllViews();
            ft.replace(R.id.SV_linearlayout, frag).commit();
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
                    Populate(Marks);
                }
                else {
                    Log.i("Bad", "From SubViewer");
                    Toaster("Failed to Connect to server");
                }
            }
        };

    }


    public void Populate(ArrayList<String> Marks)
    {
        ListView lv = (ListView) findViewById(R.id.SV_Marks_listview);
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
        DBHandler_Resources db = new DBHandler_Resources(this);
        String auth = User.authority;

        String name = "null";

        if ( auth.equals("Teacher") ) name = ((EditText)findViewById(R.id.UR_ResourceName_edittext)).getText().toString();
        else name = ((EditText)findViewById(R.id.UR_ResourceName_edittext)).getText().toString();

        //args = getArguments();
        //String subject = args.getString("subject");
        String subject = ((TextView) findViewById(R.id.SVT_subjectname_textview)).getText().toString();
        //String subject = ((TextView) findViewById(R.id.SV_subjectname_textview)).getText().toString();

        db.addResource(name, subject);
    }

    public void UploadNewGrade(View v)
    {
        DBHandler_Grades db = new DBHandler_Grades(this);
        String auth = User.authority;

        String name = ((TextView)findViewById(R.id.VESD_textView_studentName)).getText().toString();
        String gradable = ((EditText)findViewById(R.id.VESD_editText_Gradable)).getText().toString();
        int grade = Integer.parseInt(((EditText) findViewById(R.id.VESD_editText_Grade)).getText().toString());
        String subject = ((TextView) findViewById(R.id.SVT_subjectname_textview)).getText().toString();

        db.addGrade(name, subject, gradable, grade);
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
/*    @Override
    public void GetFromDatabase(ListView LV)
    {
        DBHandler_Resources db = new DBHandler_Resources(this);

        String auth = User.authority;

        String subject = "null";

        if ( auth.equals("Teacher") ) subject = ((TextView) findViewById(R.id.SVT_subjectname_textview)).getText().toString();
        else subject = ((TextView) findViewById(R.id.SV_subjectname_textview)).getText().toString();

        //ListView lv = (ListView) findViewById(R.id.VR_infolist_listview);
        ListView lv = LV;
        List<String> myList = db.getAllResources(subject);

        ArrayAdapter<String> myarrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myList);
        lv.setAdapter(myarrayAdapter);
        lv.setTextFilterEnabled(true);

    }

    public void GetGradesFromDataBase(ListView LV, String student)
    {
        DBHandler_Grades db = new DBHandler_Grades(this);


        String auth = User.authority;

        String subject = "null";

        if ( auth.equals("Teacher") ) subject = ((TextView) findViewById(R.id.SVT_subjectname_textview)).getText().toString();
        else subject = ((TextView) findViewById(R.id.SV_subjectname_textview)).getText().toString();

        String name = student;

        ListView lv = LV;
        List<String>  myList  = db.getMyGradables(subject, name);
        List<Integer> myList2 = db.getMyGrades(subject, name);
        List<String> printMe = new ArrayList<String>();

        for (int i = 0; i < myList.size(); i++ )
        {
            String newStringToPrint = "[" + myList.get(i) + "] --> " + myList2.get(i) + " ";
            printMe.add(newStringToPrint);
        }

        ArrayAdapter<String> myarrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, printMe);
        lv.setAdapter(myarrayAdapter);
        lv.setTextFilterEnabled(true);
    }



    @Override
    public void putUpStudentDetails(String name)
    {
        Log.d("SubjectView", "Entered putUpStudentDetails");
        frag = new ViewEnrolledStudentsDetails();

        Log.d("SubjectView", "Created Fragment");
        FragmentTransaction ft = fm.beginTransaction();

        Log.d("SubjectView", "Created Transaction");

        Bundle args = new Bundle();
        args.putString("name", name);
        frag.setArguments(args);
        Log.d("SubjectView", "Bundle is Fine");

        //tabs.removeAllViews();
        ft.replace(R.id.SVT_tabsview_framelayout, frag).commit();
    }
 */
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

    }
}
