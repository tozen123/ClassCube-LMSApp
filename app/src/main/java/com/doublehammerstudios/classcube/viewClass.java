package com.doublehammerstudios.classcube;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class viewClass extends AppCompatActivity {
    public Class CLASS_DATA;
    public String CLASS_DOC_ID;
    private TextView textView_className ,textView_classCode, textView_classSubject, textView_classTeacher;

    Configs mConfigs;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String userId;

    ProgressBar loadingPB;
    FloatingActionButton fab;

    String class_data_str;

    TextView mtxtEmpty;
    Button btnRefresh, btnClassInfo;
    private ClassPostAdapter classPostAdapterRecyclerView;
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
        mtxtEmpty = findViewById(R.id.txtEmpty);
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

        ArrayList<ClassPost> myObjects = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.classDocsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(viewClass.this));
        classPostAdapterRecyclerView = new ClassPostAdapter(myObjects, viewClass.this);

        firebaseFirestore.collection("class")
                .whereEqualTo("classCode", CLASS_DATA.classCode)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        // after getting the data we are calling on success method
                        // and inside this method we are checking if the received
                        // query snapshot is empty or not.
                        if (!queryDocumentSnapshots.isEmpty()) {

                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot documentSnapshot : list) {
                                if(documentSnapshot.contains("classActivities")){

                                    List<HashMap<String, String>> objectList = (List<HashMap<String, String>>) documentSnapshot.get("classActivities");
                                    for (HashMap<String, String> map : objectList) {
                                        String value1 = map.get("classPostDuedate");
                                        String value2 = map.get("classPostSubject");
                                        String value3 = map.get("classPostTitle");
                                        ClassPost myObject = new ClassPost(value3, value2, value1);
                                        myObjects.add(myObject);
                                    }
                                }
                            }
                            recyclerView.setAdapter(new ClassPostAdapter(myObjects, viewClass.this));

                            //classPostAdapterRecyclerView.notifyDataSetChanged();
                            classPostAdapterRecyclerView.updateList(myObjects);

                            if(classPostAdapterRecyclerView.getItemCount() == 0){
                                mtxtEmpty.setVisibility(View.VISIBLE);
                            } else {
                                mtxtEmpty.setVisibility(View.INVISIBLE);
                            }

                        } else {
                            // if the snapshot is empty we are displaying a toast message.
                            Toast.makeText(viewClass.this, "classPostRecyclerView: No data found in Database", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // if we do not get any data or any error we are displaying
                        // a toast message that we do not get any data
                        Toast.makeText(viewClass.this, "Fail to get the data.", Toast.LENGTH_SHORT).show();
                    }
                });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });

        // ***********************************************************
        // WORK ON Teacher – can view all students in his class
        // ***********************************************************

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

        String[] post_options = {"Module Post", "Activity Post", "Quiz Post"};
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
                    case 2:
                        createQuizPost();
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
        Intent intent = new Intent(viewClass.this, post_creator.class);
        intent.putExtra("CLASS_CODE", class_data_str);
        startActivity(intent);

        Toast.makeText(viewClass.this, "createPost()", Toast.LENGTH_SHORT).show();
    }

    public void createActivityPost(){
        Toast.makeText(viewClass.this, "createActivityPost()", Toast.LENGTH_SHORT).show();
    }

    public void createQuizPost(){
        Toast.makeText(viewClass.this, "createActivityPost()", Toast.LENGTH_SHORT).show();
    }
}