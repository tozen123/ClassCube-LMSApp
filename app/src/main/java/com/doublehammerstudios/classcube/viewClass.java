package com.doublehammerstudios.classcube;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.doublehammerstudios.classcube.Activity.postCreatorActivity;
import com.doublehammerstudios.classcube.Activity.postQuizCreatorActivity;
import com.doublehammerstudios.classcube.Activity.viewedClassActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class viewClass extends AppCompatActivity implements ClassPostItemAdapter.ItemClickListener, ClassActivityPostItemAdapter.ItemClickListener{
    public Class CLASS_DATA;

    private TextView textView_className ,textView_classCode, textView_classSubject, textView_classTeacher;

    Configs mConfigs;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String userId;

    ProgressBar loadingPB;

    String class_data_str;

    ImageView imgEmpty;
    Button btnRefresh, btnClassInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_class);
        // Difference
        FloatingActionButton fab = findViewById(R.id.viewClassFAB);
        if(mConfigs.userType.equals("Student")){
            fab.setVisibility(View.INVISIBLE);
        } else if(mConfigs.userType.equals("Teacher/Instructor/Professor")){
            fab.setVisibility(View.VISIBLE);
        }


        Intent iin= getIntent();
        Bundle bundle = iin.getExtras();
        CLASS_DATA = (Class) bundle.get("CLASS_DATA");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();

        btnRefresh = findViewById(R.id.refreshButtonViewClass);
        btnClassInfo = findViewById(R.id.classInfoButton);

        mConfigs = Configs.getInstance();
        loadingPB = findViewById(R.id.progressBar2);

        textView_className = findViewById(R.id.txt_className_value);
        textView_classCode = findViewById(R.id.txt_classCode_value);
        textView_classSubject = findViewById(R.id.txt_classSubject_value);
        textView_classTeacher = findViewById(R.id.txt_classTeacher_value);
        imgEmpty = findViewById(R.id.imageEmpty);

        // Load Pre-Data
        firebaseFirestore.collection("class")
                .whereEqualTo("classCode", CLASS_DATA.classCode)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            loadingPB.setVisibility(View.GONE);
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Toast.makeText(viewClass.this, "Successfully loaded class: "+document.getString("className"), Toast.LENGTH_SHORT).show();
                                String a = document.getString("className");
                                String b = document.getString("classCode");
                                String c = document.getString("classSubject");
                                String d = document.getString("classTeacherName");
                                class_data_str = b;

                                textView_className.setText(a);
                                textView_classCode.setText(b);
                                textView_classSubject.setText(c);
                                textView_classTeacher.setText(d);
                            }
                        } else {
                            Toast.makeText(viewClass.this, "Error getting class data documents: "+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        loadPost();

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });

        btnClassInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("class")
                        .whereEqualTo("classCode", CLASS_DATA.classCode)
                        .limit(1)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                    for (DocumentSnapshot documentSnapshot : list) {
                                        if(documentSnapshot.contains("classStudents")) {
                                            List<String> stringList = (List<String>) documentSnapshot.get("classStudents");

                                            if (stringList != null && !stringList.isEmpty()) {
                                                List<String> mStdList = new ArrayList<>();

                                                for (String stdListz : stringList) {
                                                    String userId = stdListz;

                                                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                                                    DocumentReference userRef = db.collection("users").document(userId);

                                                    userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                DocumentSnapshot document = task.getResult();
                                                                if (document.exists()) {
                                                                    String value = document.getData().get("Name").toString();
                                                                    Log.d("2TAG5", "" + value);
                                                                    mStdList.add(value.toString());
                                                                    showStudentList(mStdList);

                                                                }
                                                            }
                                                        }
                                                    });

                                                }
                                                Log.d("1TAG5", "" + mStdList);


                                            } else {
                                                List<String> emptyList = Collections.singletonList("No student has joined this class");
                                                showStudentList(emptyList);
                                            }
                                        }
                                    }

                                }
                            }
                        });
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mConfigs.userType.equals("Teacher/Instructor/Professor")){
                    openClassPostDialog();
                }
            }
        });


    }

    private void loadPost() {

        List<ClassPost> classPosts = new ArrayList<>();
        List<ClassActivityPost> classActivityPosts = new ArrayList<>();

        ClassPostItemAdapter classPostItemAdapter = new ClassPostItemAdapter((ArrayList<ClassPost>) classPosts, viewClass.this);
        classPostItemAdapter.setClickListener(viewClass.this);

        ClassActivityPostItemAdapter classActivityPostItemAdapter = new ClassActivityPostItemAdapter((ArrayList<ClassActivityPost>) classActivityPosts, viewClass.this);
        classActivityPostItemAdapter.setClickListener(viewClass.this);

        RecyclerView recyclerView = findViewById(R.id.classDocsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(viewClass.this));

        /*
            setting up the loader in the element
         */

        ConcatAdapter concatAdapter = new ConcatAdapter(classPostItemAdapter, classActivityPostItemAdapter);
        recyclerView.setAdapter(concatAdapter);
        String TAG = "viewClass.javaData";

        firebaseFirestore.collection("class")
                .whereEqualTo("classCode", CLASS_DATA.classCode)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {
                            Log.w(TAG, "Listen failed.", error);
                            return;
                        }

                        classPosts.clear();
                        classActivityPosts.clear();

                        if (value.isEmpty()) {
                            Log.d(TAG, "No data found in Database");
                            return;
                        }

                        List<DocumentSnapshot> list = value.getDocuments();
                        for (DocumentSnapshot documentSnapshot : list) {
                            if (!documentSnapshot.contains("classActivities")) {
                                Log.d(TAG, "No called classActivities data found in Database");
                                return;
                            }

                            List<HashMap<String, String>> objectList = (List<HashMap<String, String>>) documentSnapshot.get("classActivities");

                            for (HashMap<String, String> map : objectList) {
                                Object students = null;
                                boolean completed = false;
                                String value1 = null;
                                String value2 = null;
                                String value3 = null;
                                String value4 = null;
                                String value5 = null;
                                String value6 = null;

                                if(map.containsKey("classFileUrl")){
                                    value1 = map.get("classPostDuedate");
                                    value2 = map.get("classFileUrl");
                                    value3 = map.get("classPostSubject");
                                    value4 = map.get("classPostTitle");

                                    if(value1 == null){
                                        value1 = "No Schedule";
                                    }

                                    students = map.get("studentWhoFinishedClassPost");

                                }
                                if(map.containsKey("classActivityPostSubmissionBinLink")){
                                        Log.d("asdasdasdasd", "IM HERE");
                                        value1 = map.get("classActivityPostDueDate");
                                        value2 = map.get("classActivityPostStatus");
                                        value3 = map.get("classActivityPostSubject");
                                        value4 = map.get("classActivityPostSubmissionBinLink");
                                        value5 = map.get("classActivityPostTitle");

                                        if(value1 == null){
                                            value1 = "No Schedule";
                                        }

                                        students = map.get("studentWhoFinishedClassActivityPost");
                                }

                                if (students instanceof String) {
                                    String[] studentsArr = { (String) students };
                                    for (String studentId : studentsArr) {
                                        if (studentId.equals(userId)) {
                                            completed = true;
                                        }
                                    }
                                } else if (students instanceof List) {
                                    List<String> studentsList = (List<String>) students;
                                    if (studentsList.contains(userId)) {
                                        completed = true;
                                    }
                                }

                                value6 = completed ? "Completed" : "Incomplete";

                                if(map.containsKey("classFileUrl")){
                                    ClassPost newClassPost = new ClassPost(value4, value3, value1, value2, value6, null);
                                    classPosts.add(newClassPost);
                                }

                                if(map.containsKey("classActivityPostSubmissionBinLink")){
                                    Log.d("asdasdasdasd", "IM HERE I UPDATED THE STATUS");
                                    ClassActivityPost newClassActivityPost = new ClassActivityPost(value5, value3, value1, value6, value4, null);
                                    classActivityPosts.add(newClassActivityPost);
                                }

                            }
                        }

                        classPostItemAdapter.notifyDataSetChanged();
                        classActivityPostItemAdapter.notifyDataSetChanged();

                        if(classPosts.size() == 0 && classActivityPosts.size() == 0 ){
                            imgEmpty.setVisibility(View.VISIBLE);
                        } else {
                            imgEmpty.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }
    public void onItemClickActivityPost(View view, int position){
        Intent intent = new Intent(viewClass.this, viewedClassActivity.class);

        intent.putExtra("CLASS_TITLE",  ClassActivityPostItemAdapter.getItem(position).getClassActivityPostTitle());
        intent.putExtra("CLASS_SUBJECT",  ClassActivityPostItemAdapter.getItem(position).getClassActivityPostSubject());
        intent.putExtra("CLASS_DUEDATE",  ClassActivityPostItemAdapter.getItem(position).getClassActivityPostDueDate());
        intent.putExtra("CLASS_ACTIVITY_BIN",  ClassActivityPostItemAdapter.getItem(position).getClassActivityPostSubmissionBinLink());
        intent.putExtra("CLASS_POST_COMPLETION_STATUS",  ClassActivityPostItemAdapter.getItem(position).getClassActivityPostStatus());
        intent.putExtra("CLASS_POST_STUDENTWHOFINISHED",  ClassActivityPostItemAdapter.getItem(position).getStudentWhoFinishedClassActivityPost());

        intent.putExtra("CLASS_POST_TYPE",  "ACTIVITY POST");
        intent.putExtra("CLASS_CODE",  CLASS_DATA.getClassCode());


        startActivity(intent);
        Toast.makeText(this, "" + ClassActivityPostItemAdapter.getItem(position).getClassActivityPostTitle(), Toast.LENGTH_SHORT).show();
    }
    public void onItemClickClassPost(View view, int position) {
        Intent intent = new Intent(viewClass.this, viewedClassActivity.class);


        intent.putExtra("CLASS_TITLE",  ClassPostItemAdapter.getItem(position).getClassPostTitle());
        intent.putExtra("CLASS_SUBJECT",  ClassPostItemAdapter.getItem(position).getClassPostSubject());
        intent.putExtra("CLASS_DUEDATE",  ClassPostItemAdapter.getItem(position).getClassPostDuedate());
        intent.putExtra("CLASS_EXTRA_DATA",  ClassPostItemAdapter.getItem(position).getClassFileUrl());
        intent.putExtra("CLASS_POST_COMPLETION_STATUS",  ClassPostItemAdapter.getItem(position).getPostStatus());
        intent.putExtra("CLASS_POST_STUDENTWHOFINISHED",  ClassPostItemAdapter.getItem(position).getstudentWhoFinishedClassPost());

        intent.putExtra("CLASS_POST_TYPE",  "POST");
        intent.putExtra("CLASS_CODE",  CLASS_DATA.getClassCode());


        startActivity(intent);
        Toast.makeText(this, "" + ClassPostItemAdapter.getItem(position).getClassPostTitle(), Toast.LENGTH_SHORT).show();
    }

    public void showStudentList(List<String> stdList) {
        String[] items = new String[stdList.size()];
        items = stdList.toArray(items);

        Intent intent = new Intent(viewClass.this, viewClassInfo.class);
        intent.putExtra("studentList", items);
        intent.putExtra("CLASS_CODE", class_data_str);
        intent.putExtra("CLASS_NAME", CLASS_DATA.className);
        startActivity(intent);

    }

    public void openClassPostDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose type of post");

        String[] post_options = {"Module Post", "Activity Post"};
        builder.setItems(post_options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        createPost();
                        break;
                    case 1:
                        createActivityPost();
                        break;
                }
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    public void createPost(){
        Intent intent = new Intent(viewClass.this, postCreatorActivity.class);
        intent.putExtra("CLASS_POSTING_TYPE", "Post");
        intent.putExtra("CLASS_CODE", class_data_str);

        startActivity(intent);

        Toast.makeText(viewClass.this, "createPost()", Toast.LENGTH_SHORT).show();
    }

    public void createActivityPost(){
        Intent intent = new Intent(viewClass.this, postCreatorActivity.class);
        intent.putExtra("CLASS_POSTING_TYPE", "Activity Post");
        intent.putExtra("CLASS_CODE", class_data_str);

        startActivity(intent);


        Toast.makeText(viewClass.this, "createActivityPost()", Toast.LENGTH_SHORT).show();
    }

    public void createQuizPost(){
        Intent intent = new Intent(viewClass.this, postQuizCreatorActivity.class);
        intent.putExtra("CLASS_CODE", class_data_str);

        startActivity(intent);
        Toast.makeText(viewClass.this, "createActivityPost()", Toast.LENGTH_SHORT).show();
    }
}