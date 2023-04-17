package com.doublehammerstudios.classcube.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.doublehammerstudios.classcube.Configs;
import com.doublehammerstudios.classcube.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class viewedClassActivity extends AppCompatActivity {

    String CLASS_POST_TITLE;
    String CLASS_POST_SUBJECT;
    String CLASS_POST_DUEDATE;
    String CLASS_EXTRA_DATA;
    String CLASS_POST_COMPLETION_STATUS;
    ArrayList<String> CLASS_POST_STUDENTWHOFINISHED;
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
            CLASS_POST_COMPLETION_STATUS = (String) bundle.get("CLASS_POST_COMPLETION_STATUS");
            CLASS_POST_STUDENTWHOFINISHED = (ArrayList<String>) bundle.get("CLASS_POST_STUDENTWHOFINISHED");
        }
        Log.d("DELETEBOI", "PUITANSINDPASUID: "+CLASS_CODE);

        textView_title = findViewById(R.id.classPostTitle_value);
        textView_subject = findViewById(R.id.classPostSubject_value);
        textView_duedate = findViewById(R.id.classPostDueDate_value);
        btn_extraData = findViewById(R.id.extraDataButton);
        btn_deletePost = findViewById(R.id.deletePostButton);

        sw_markAsCompleted = findViewById(R.id.completedPostSwitch);

        if(CLASS_EXTRA_DATA == null){
            btn_extraData.setEnabled(false);
        } else {
            url = CLASS_EXTRA_DATA;
            btn_extraData.setEnabled(true);

            changeButtonName();
        }
        setAllMainText();

        if(Configs.userType.equals("Student"))
        {
            btn_extraData.setVisibility(View.VISIBLE);
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
                firebaseFirestore.collection("class")
                        .whereEqualTo("classCode", CLASS_CODE)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                        DocumentReference documentReference = documentSnapshot.getReference();
                                        String classNameSaved = CLASS_POST_TITLE;

                                        Map<String, Object> mapToDelete = new HashMap<>();
                                        mapToDelete.put("classFileUrl", CLASS_EXTRA_DATA);
                                        mapToDelete.put("classPostDuedate", CLASS_POST_DUEDATE);
                                        mapToDelete.put("classPostSubject", CLASS_POST_SUBJECT);
                                        mapToDelete.put("classPostTitle", CLASS_POST_TITLE);
                                        mapToDelete.put("postStatus", CLASS_POST_COMPLETION_STATUS);
                                        mapToDelete.put("studentWhoFinishedClassPost", CLASS_POST_STUDENTWHOFINISHED);

                                        documentReference.update("classActivities", FieldValue.arrayRemove(mapToDelete))
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        finish();
                                                        Toast.makeText(viewedClassActivity.this, "Successfully Deleted "+classNameSaved, Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(viewedClassActivity.this, "Failed to Delete "+classNameSaved, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                }

                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(viewedClassActivity.this, "Failed to listen to the database", Toast.LENGTH_SHORT).show();
                        });
            }
        });

        sw_markAsCompleted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    // WORK ON THE MARK COMPLETE
                    // Switch is checked
                } else {
                    // Switch is unchecked
                }
            }
        });
    }

    /*

    CREATE THE SYSTEM WHERE THE USER CAN MARK A POST INTO COMPLETE


     */
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