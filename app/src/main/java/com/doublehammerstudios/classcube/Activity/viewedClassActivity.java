package com.doublehammerstudios.classcube.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.doublehammerstudios.classcube.Configs;
import com.doublehammerstudios.classcube.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class viewedClassActivity extends AppCompatActivity {

    String POST_TITLE;
    String POST_SUBJECT;
    String POST_DUEDATE;
    String CLASS_EXTRA_DATA;
    String CLASS_ACTIVITY_BIN;
    String POST_COMPLETION_STATUS;
    ArrayList<String> CLASS_POST_STUDENTWHOFINISHED;
    String CLASS_CODE;
    String CLASS_POST_TYPE;

    TextView textView_title, textView_subject, textView_duedate, textView_completionStats;
    Button btn_extraData, btn_deletePost, btn_uploadFile;

    Switch sw_markAsCompleted;
    private String url;

    LinearLayout submissionDataLayout;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseStorage storage;
    String userId;

    /*

        Summary
        This code handles the viewed class post, Module Post and Activity Post.

     */

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewed_class);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        storage = FirebaseStorage.getInstance();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle != null){
            POST_TITLE = (String) bundle.get("CLASS_TITLE");
            POST_SUBJECT = (String) bundle.get("CLASS_SUBJECT");
            POST_DUEDATE = (String) bundle.get("CLASS_DUEDATE");
            CLASS_EXTRA_DATA = (String) bundle.get("CLASS_EXTRA_DATA");
            CLASS_ACTIVITY_BIN = (String) bundle.get("CLASS_ACTIVITY_BIN");
            CLASS_CODE = (String) bundle.get("CLASS_CODE");
            POST_COMPLETION_STATUS = (String) bundle.get("CLASS_POST_COMPLETION_STATUS");
            CLASS_POST_TYPE = (String) bundle.get("CLASS_POST_TYPE");
            CLASS_POST_STUDENTWHOFINISHED = (ArrayList<String>) bundle.get("CLASS_POST_STUDENTWHOFINISHED");
        }
        Log.d("DELETEBOI", "PUITANSINDPASUID: "+CLASS_CODE);

        textView_title = findViewById(R.id.classPostTitle_value);
        textView_subject = findViewById(R.id.classPostSubject_value);
        textView_duedate = findViewById(R.id.classPostDueDate_value);
        textView_completionStats = findViewById(R.id.classPost_Stats);
        submissionDataLayout = findViewById(R.id.submissionDataLayout);
        btn_extraData = findViewById(R.id.extraDataButton);
        btn_deletePost = findViewById(R.id.deletePostButton);
        btn_uploadFile = findViewById(R.id.uploadFileData);

        sw_markAsCompleted = findViewById(R.id.completedPostSwitch);

        if(CLASS_ACTIVITY_BIN == null){
            submissionDataLayout.setVisibility(View.INVISIBLE);
        } else {
            submissionDataLayout.setVisibility(View.VISIBLE);
        }

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
            textView_completionStats.setVisibility(View.VISIBLE);
        } else
            if(Configs.userType.equals("Teacher/Instructor/Professor"))
            {
                btn_extraData.setVisibility(View.VISIBLE);
                btn_deletePost.setVisibility(View.VISIBLE);

                textView_completionStats.setVisibility(View.INVISIBLE);
                sw_markAsCompleted.setVisibility(View.INVISIBLE);
            }

        btn_extraData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openExtraData();
            }
        });

        btn_uploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadOutputData();
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
                                if (queryDocumentSnapshots.isEmpty()) {
                                    // Failed to fetch data
                                    return;
                                }

                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    DocumentReference documentReference = documentSnapshot.getReference();

                                    if(CLASS_POST_TYPE.equals("POST")){
                                        String classNameSaved = POST_TITLE;

                                        Map<String, Object> mapToDelete = new HashMap<>();
                                        mapToDelete.put("classFileUrl", CLASS_EXTRA_DATA);
                                        mapToDelete.put("classPostDuedate", POST_DUEDATE);
                                        mapToDelete.put("classPostSubject", POST_SUBJECT);
                                        mapToDelete.put("classPostTitle", POST_TITLE);
                                        mapToDelete.put("postStatus", POST_COMPLETION_STATUS);
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
                                    } else if(CLASS_POST_TYPE.equals("ACTIVITY POST")){

                                        final ProgressDialog progressDialog = new ProgressDialog(viewedClassActivity.this);
                                        progressDialog.setTitle("Deleting Activity Post....");
                                        progressDialog.show();

                                        String classNameSaved = POST_TITLE;

                                        Map<String, Object> mapToDelete = new HashMap<>();
                                        mapToDelete.put("classActivityPostSubmissionBinLink", CLASS_ACTIVITY_BIN);
                                        mapToDelete.put("classActivityPostDueDate", POST_DUEDATE);
                                        mapToDelete.put("classActivityPostSubject", POST_SUBJECT);
                                        mapToDelete.put("classActivityPostTitle", POST_TITLE);
                                        mapToDelete.put("classActivityPostStatus", POST_COMPLETION_STATUS);
                                        mapToDelete.put("studentWhoFinishedClassActivityPost", CLASS_POST_STUDENTWHOFINISHED);

                                        documentReference.update("classActivities", FieldValue.arrayRemove(mapToDelete))
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        FirebaseStorage storage = FirebaseStorage.getInstance();
                                                        StorageReference storageRef = storage.getReference();
                                                        StorageReference folder2Ref = storageRef.child(CLASS_ACTIVITY_BIN);

                                                        Log.d("asdasdasdasdsa", "MSG: "+CLASS_ACTIVITY_BIN);

                                                        folder2Ref.delete()
                                                                .addOnSuccessListener(taskSnapshot -> {
                                                                    progressDialog.dismiss();
                                                                    finish();
                                                                    Toast.makeText(viewedClassActivity.this, "Successfully Deleted "+classNameSaved, Toast.LENGTH_SHORT).show();
                                                                })
                                                                .addOnFailureListener(e -> {
                                                                    progressDialog.dismiss();
                                                                    finish();
                                                                    /* TO FIX DELETE FEATURERE
                                                                        PROBLEM:
                                                                                 AYAW MADELETE NAKAKA URAT TALAGA
                                                                     */
                                                                });
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

        checkTheUserAccomplishment();
        String TAG = "sw_markAsCompleted_1"; // FOR LOGGING DATA MUST BE DELETED AT ALL COST
        sw_markAsCompleted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    firebaseFirestore.collection("class")
                            .whereEqualTo("classCode", CLASS_CODE)
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    if (queryDocumentSnapshots.isEmpty()) {
                                        // Document empty, exiting operation
                                        return;
                                    }

                                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {

                                        if (!documentSnapshot.exists()) {
                                            // Document does not exist, exiting operation
                                            return;
                                        }

                                        List<Map<String, Object>> classActivities = (List<Map<String, Object>>) documentSnapshot.get("classActivities");

                                        if (classActivities == null) {
                                            // classActivity does not exist, exiting operation
                                            return;
                                        }

                                        for (Map<String, Object> map : classActivities) {
                                            /*
                                                Handles Module Posting
                                                Uses the classFileURL as the unique variable
                                             */
                                            if(map.containsKey("classFileUrl")){
                                                if(map.get("classPostTitle").toString().equals(POST_TITLE)){
                                                    List<String> students = (List<String>) map.get("studentWhoFinishedClassPost");

                                                    Log.d(TAG, "classPostTitle: "+map.get("classPostTitle"));
                                                    Log.d(TAG, "studentWhoFinishedClassPost: "+map.get("studentWhoFinishedClassPost"));

                                                    if (students == null) {
                                                        students = new ArrayList<>();
                                                    }

                                                    if(students.contains(userId)){
                                                        // This user already completed the post and will not add his name in the database
                                                        return;
                                                    }

                                                    students.add(userId);
                                                    map.put("studentWhoFinishedClassPost", students);
                                                }
                                            }
                                            /*
                                                Activity Module Posting
                                                Uses the classActivityPostSubmissionBinLink as the unique variable
                                            */
                                            if(map.containsKey("classActivityPostSubmissionBinLink")){
                                                if(map.get("classActivityPostTitle").toString().equals(POST_TITLE)){
                                                    List<String> students = (List<String>) map.get("studentWhoFinishedClassActivityPost");

                                                    Log.d(TAG, "classActivityPostTitle: "+map.get("classActivityPostTitle"));
                                                    Log.d(TAG, "studentWhoFinishedClassActivityPost: "+map.get("studentWhoFinishedClassActivityPost"));

                                                    if (students == null) {
                                                        students = new ArrayList<>();
                                                    }

                                                    if(students.contains(userId)){
                                                        // This user already completed the post and will not add his name in the database
                                                        return;
                                                    }

                                                    students.add(userId);
                                                    map.put("studentWhoFinishedClassActivityPost", students);
                                                }
                                            }

                                        }
                                        updateStorage(documentSnapshot, classActivities);
                                        checkTheUserAccomplishment();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Failed to Find the Class in the database
                                }
                            });
                    } else {
                        uncheckedCompletionStatus();
                }
            }
        });
    }

    public void checkTheUserAccomplishment(){
        firebaseFirestore.collection("class")
                .whereEqualTo("classCode", CLASS_CODE)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            // Document empty, exiting operation
                            return;
                        }

                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {

                            if (!documentSnapshot.exists()) {
                                // Document does not exist, exiting operation
                                return;
                            }

                            List<Map<String, Object>> classActivities = (List<Map<String, Object>>) documentSnapshot.get("classActivities");

                            if (classActivities == null) {
                                // classActivity does not exist, exiting operation
                                return;
                            }

                            for (Map<String, Object> map : classActivities) {
                                /*
                                                Handles Module Posting
                                                Uses the classFileURL as the unique variable
                                */
                                if(map.containsKey("classFileUrl")){
                                    if(map.get("classPostTitle").toString().equals(POST_TITLE)){
                                        List<String> students = (List<String>) map.get("studentWhoFinishedClassPost");

                                        if (students == null) {
                                            students = new ArrayList<>();
                                        }

                                        if(students.contains(userId)){
                                            textView_completionStats.setText("You already completed this activity!");

                                            sw_markAsCompleted.setChecked(true);
                                        } else {
                                            textView_completionStats.setText("You still not completed this activity!");
                                        }
                                    }
                                }
                                /*
                                                Activity Module Posting
                                                Uses the classActivityPostSubmissionBinLink as the unique variable
                                */
                                if(map.containsKey("classActivityPostSubmissionBinLink")){
                                    if(map.get("classActivityPostTitle").toString().equals(POST_TITLE)){
                                        List<String> students = (List<String>) map.get("studentWhoFinishedClassActivityPost");

                                        if (students == null) {
                                            students = new ArrayList<>();
                                        }

                                        if(students.contains(userId)){
                                            textView_completionStats.setText("You already completed this activity!");

                                            sw_markAsCompleted.setChecked(true);
                                        } else {
                                            textView_completionStats.setText("You still not completed this activity!");
                                        }
                                    }
                                }

                            }

                            updateStorage(documentSnapshot, classActivities);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to Find the Class in the database
                    }
                });
    }
    /*
            Summary
            Function to update the firebase database of the changes in the class post
     */
    public void updateStorage(@NonNull DocumentSnapshot documentSnapshot, List<Map<String, Object>> classActivity){
        // Update the modified classActivity list back to Firestore
        firebaseFirestore.collection("class").document(documentSnapshot.getId())
                .update("classActivities", classActivity)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Updated Successfully
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to update
                    }
                });
    }
    public void uncheckedCompletionStatus() {
        firebaseFirestore.collection("class")
                .whereEqualTo("classCode", CLASS_CODE)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            // Document empty, exiting operation
                            return;
                        }

                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {

                            if (!documentSnapshot.exists()) {
                                // Document does not exist, exiting operation
                                return;
                            }

                            List<Map<String, Object>> classActivity = (List<Map<String, Object>>) documentSnapshot.get("classActivities");

                            if (classActivity == null) {
                                // classActivity does not exist, exiting operation
                                return;
                            }

                            for (Map<String, Object> map : classActivity) {
                                /*
                                                Handles Module Posting
                                                Uses the classFileURL as the unique variable
                                */
                                if(map.containsKey("classFileUrl")){
                                    if(map.get("classPostTitle").toString().equals(POST_TITLE)){
                                        List<String> students = (List<String>) map.get("studentWhoFinishedClassPost");

                                        if (students == null) {
                                            return;
                                        }

                                        if(students.contains(userId)){
                                            students.remove(userId);
                                        }

                                        map.put("studentWhoFinishedClassPost", students);
                                    }
                                }
                                 /*
                                                Activity Module Posting
                                                Uses the classActivityPostSubmissionBinLink as the unique variable
                                */
                                if(map.containsKey("classActivityPostSubmissionBinLink")){
                                    if(map.get("classActivityPostTitle").toString().equals(POST_TITLE)){
                                        List<String> students = (List<String>) map.get("studentWhoFinishedClassActivityPost");

                                        if (students == null) {
                                            return;
                                        }

                                        if(students.contains(userId)){
                                            students.remove(userId);
                                        }

                                        map.put("studentWhoFinishedClassActivityPost", students);
                                    }
                                }
                            }

                            updateStorage(documentSnapshot, classActivity);
                            checkTheUserAccomplishment();
                        }
                    }
                });
    }

    /*
            Summary
            Function to change the button based on the retrieved data
    */

    public void changeButtonName(){
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        String fileName = Uri.parse(url.toString()).getLastPathSegment();
        StorageReference fileRef = storageRef.child("files/" + Uri.parse(url.toString()).getLastPathSegment());

        btn_extraData.setText(String.format("File: %s", fileRef.getName()));
    }

    /*
        Summary
        Function to set the text of major textView elements
    */

    public void setAllMainText()
    {
        textView_title.setText(POST_TITLE);
        textView_subject.setText(POST_SUBJECT);
        textView_duedate.setText(POST_DUEDATE);
    }

    /*
        Summary
        Function to start implicit intent to open the URI data
    */
    public void openExtraData(){
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
    public final int PICK_FILE_REQUEST = 1;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading File....");
        progressDialog.show();

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri fileUri = data.getData();

            StorageReference storageRef = storage.getReference().child(CLASS_ACTIVITY_BIN+"/" + "OUTPUT:"+POST_TITLE+"_USER:"+userId);
            UploadTask uploadTask = storageRef.putFile(fileUri);

            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    progressDialog.dismiss();
                    Toast.makeText(viewedClassActivity.this, "You have successfully uploaded your file!", Toast.LENGTH_SHORT).show();
                }
            });
            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                    progressDialog.setMessage("Uploaded: "+(int)progress+"%");
                }
            });
        }
    }

    /*
        Summary
        Function to upload the file that the user student uploaded
     */
    public void uploadOutputData(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // You can set the file type here. For example, "image/*" for images or "application/pdf" for PDFs.
        startActivityForResult(Intent.createChooser(intent, "Select a File"), PICK_FILE_REQUEST);
    }

}