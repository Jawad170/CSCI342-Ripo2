package com.csci342.justin.moodleapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class ListOfSubjects extends Activity {

    Intent previous;
    Connection connect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_subjects);

        previous = getIntent();
        connect = (Connection) previous.getSerializableExtra("Connection");

        Spinner spinner = (Spinner) findViewById(R.id.LOS_yearselection_spinner);

        String[] years = new String[] {"2016", "2015", "2014"};

        final ArrayAdapter spinner_adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, years);

        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(spinner_adapter);

        //test comment line for commit's sake

        //final ListView listview = (ListView) findViewById(R.id.LOS_subjectlist_listview);

        //receive info from online database to fill the listview
            //and set the year (default)
        String[] values = new String[] {"CSCI015", "CSCI103", "CSCI131", "CSCI114", "CSCI124", "CSCI203", "CSCI235"};

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.LOS_linearlayout);

        for (int i = 0; i < values.length; ++i)
        {
            Button butt = new Button(this);
            butt.setText(values[i]);
            butt.setOnClickListener(handleOnClick(butt));
            linearLayout.addView(butt);
        }
        /*
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                String x = (String) adapter.getItem(position);

                Intent i = new Intent(ListOfSubjects.this, SubjectView.class);
                i.putExtra("Subject Name", x);
                i.putExtra("Authority", authority);
                startActivity(i);
            }

        });*/
    }

    void SubjectButtonOnClick(View v)
    {
        Button butt = (Button) v;
        String x = (String) butt.getText();

        Intent i = new Intent(ListOfSubjects.this, SubjectView.class);
        i.putExtra("Subject Name", x);
        i.putExtra("Connection",connect);
        startActivity(i);
    }

    View.OnClickListener handleOnClick(final Button button) {
        return new View.OnClickListener() {
            public void onClick(View v) {

                Button butt = (Button) v;
                String x = (String) butt.getText();

                Intent i = new Intent(ListOfSubjects.this, SubjectView.class);
                i.putExtra("Subject Name", x);
                i.putExtra("Connection",connect);
                startActivity(i);

            }
        };
    }

}
