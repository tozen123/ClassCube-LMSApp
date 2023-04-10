package com.doublehammerstudios.classcube;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Arrays;

public class viewedClassActivity extends AppCompatActivity {

    String CLASS_POST_TITLE;
    String CLASS_POST_SUBJECT;
    String CLASS_POST_DUEDATE;

    TextView textView_title, textView_subject, textView_duedate;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewed_class);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle != null){
            CLASS_POST_TITLE = (String) bundle.get("CLASS_TITLE");
            CLASS_POST_SUBJECT= (String) bundle.get("CLASS_SUBJECT");
            CLASS_POST_DUEDATE = (String) bundle.get("CLASS_DUEDATE");
        }

        textView_title = findViewById(R.id.classPostTitle_value);
        textView_subject = findViewById(R.id.classPostSubject_value);
        textView_duedate = findViewById(R.id.classPostDueDate_value);
        setAllMainText();


    }

    public void setAllMainText()
    {
        textView_title.setText(CLASS_POST_TITLE);
        textView_subject.setText(CLASS_POST_SUBJECT);
        textView_duedate.setText(CLASS_POST_DUEDATE);
    }

}