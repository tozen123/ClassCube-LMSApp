package com.doublehammerstudios.classcube.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.doublehammerstudios.classcube.Configs;
import com.doublehammerstudios.classcube.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class viewedClassActivity extends AppCompatActivity {

    String CLASS_POST_TITLE;
    String CLASS_POST_SUBJECT;
    String CLASS_POST_DUEDATE;
    String CLASS_EXTRA_DATA;
    String CLASS_CODE;
    TextView textView_title, textView_subject, textView_duedate;
    Button btn_extraData, btn_deletePost;

    Switch sw_markAsCompleted;
    private String url;


    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewed_class);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle != null){
            CLASS_POST_TITLE = (String) bundle.get("CLASS_TITLE");
            CLASS_POST_SUBJECT= (String) bundle.get("CLASS_SUBJECT");
            CLASS_POST_DUEDATE = (String) bundle.get("CLASS_DUEDATE");
            CLASS_EXTRA_DATA = (String) bundle.get("CLASS_EXTRA_DATA");
            CLASS_CODE = (String) bundle.get("CLASS_CODE");
        }
        Log.d("DELETEBOI", "PUITANSINDPASUID: "+CLASS_CODE);

        textView_title = findViewById(R.id.classPostTitle_value);
        textView_subject = findViewById(R.id.classPostSubject_value);
        textView_duedate = findViewById(R.id.classPostDueDate_value);
        btn_extraData = findViewById(R.id.extraDataButton);
        btn_deletePost = findViewById(R.id.deletePostButton);

        sw_markAsCompleted = findViewById(R.id.completedPostSwitch);

        if(CLASS_EXTRA_DATA.isEmpty()){
            btn_extraData.setVisibility(View.INVISIBLE);
        } else {
            url = CLASS_EXTRA_DATA;
            btn_extraData.setVisibility(View.VISIBLE);

            changeButtonName();
        }

        setAllMainText();

        if(Configs.userType.equals("Student"))
        {
            btn_extraData.setVisibility(View.INVISIBLE);
            btn_deletePost.setVisibility(View.INVISIBLE);

            sw_markAsCompleted.setVisibility(View.VISIBLE);

        } else
            if(Configs.userType.equals("Teacher/Instructor/Professor"))
            {
                btn_extraData.setVisibility(View.VISIBLE);
                btn_deletePost.setVisibility(View.VISIBLE);

                sw_markAsCompleted.setVisibility(View.INVISIBLE);
            }

        btn_extraData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openExtraData();
            }
        });

        btn_deletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*

                --------------------------------------------------------------------------------
                WORK ON THIS PART DELETE POST FEATURE
                --------------------------------------------------------------------------------

                 */
            }
        });

    }
    public void changeButtonName(){
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        String fileName = Uri.parse(url.toString()).getLastPathSegment();
        StorageReference fileRef = storageRef.child("files/" + Uri.parse(url.toString()).getLastPathSegment());

        btn_extraData.setText(String.format("File: %s", fileRef.getName()));
    }

    public void setAllMainText()
    {
        textView_title.setText(CLASS_POST_TITLE);
        textView_subject.setText(CLASS_POST_SUBJECT);
        textView_duedate.setText(CLASS_POST_DUEDATE);
    }

    public void openExtraData(){
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

}