package com.doublehammerstudios.classcube;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class InsideStudentClassActivityRecordFragment extends Fragment {
    private String className;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String userId;
    TableLayout tableLayout;

    Class currentClass;

    public static InsideStudentClassActivityRecordFragment newInstance(Class objClass) {
        InsideStudentClassActivityRecordFragment fragment = new InsideStudentClassActivityRecordFragment();
        Bundle args = new Bundle();
        args.putString("className", objClass.getClassName());
        args.putSerializable("classObject", objClass);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            className = getArguments().getString("className");
            currentClass = (Class) getArguments().getSerializable("classObject");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inside_student_class_activity_record, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tableLayout = view.findViewById(R.id.tableLayout);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();

        Log.d("Statement 1", "dataClass \n" +
                "className: " + currentClass.getClassName() +
                "\nclassSubject: " + currentClass.getClassSubject() +
                "\nclassCode: " + currentClass.getClassCode() +
                "\nclassStudents: " + currentClass.getClassStudents() +
                "\nclassTeacher: " + currentClass.getClassTeacherName());


        firebaseFirestore.collection("class")
                .whereEqualTo("classCode", currentClass.getClassCode())
                .limit(1)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            if (getActivity() != null) {
                                Toast.makeText(getActivity(), "Fail to get the data.", Toast.LENGTH_SHORT).show();
                            }
                            return;
                        }

                        if (queryDocumentSnapshots.isEmpty()) {
                            // No matching documents found
                            return;
                        }
                        ArrayList<String> ActivtyTitles = new ArrayList<String>();
                        ActivtyTitles.add("Student");
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            List<Map<String, Object>> classActivitiesList = (List<Map<String, Object>>) documentSnapshot.get("classActivities");
                            if (classActivitiesList != null) {
                                for (Map<String, Object> classActivity : classActivitiesList) {
                                    if(classActivity.get("classActivityPostTitle") != null){
                                        String activityTitle = (String) classActivity.get("classActivityPostTitle");
                                        ActivtyTitles.add(activityTitle);
                                    }
                                }
                            }
                        }
                        tableAddColumn(ActivtyTitles);
                    }
                });

        firebaseFirestore.collection("class")
                .whereEqualTo("classCode", currentClass.getClassCode())
                .limit(1)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            if (getActivity() != null) {
                                Toast.makeText(getActivity(), "Fail to get the data.", Toast.LENGTH_SHORT).show();
                            }
                            return;
                        }

                        if (queryDocumentSnapshots.isEmpty()) {
                            // No matching documents found
                            return;
                        }

                        ArrayList<String> data = new ArrayList<String>();
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            List<String> classStudents = (List<String>) document.get("classStudents");
                            List<Map<String, Object>> classActivitiesList = (List<Map<String, Object>>) document.get("classActivities");

                            if(classStudents == null){
                                return;
                            }

                            if (classActivitiesList == null) {
                                return;
                            }

                            for (String student: classStudents){
                                parseStudentId(student, new OnSuccessListener<String>() {
                                    @Override
                                    public void onSuccess(String s) {
                                        data.add(s);

                                        for (Map<String, Object> classActivity : classActivitiesList) {
                                            if(classActivity.get("classActivityPostDueDate")!= null){
                                                ArrayList<String> studentFinishedActivity = (ArrayList<String>) classActivity.get("studentWhoFinishedClassActivityPost");
                                                if(studentFinishedActivity != null){
                                                    if(studentFinishedActivity.contains(student)){
                                                        data.add("Complete");
                                                    } else {
                                                        data.add("Incomplete");
                                                    }
                                                } else {
                                                    data.add("Incomplete");
                                                }
                                            }
                                        }
                                        tableAddRow(data);
                                        data.clear();
                                    }
                                });
                            }

                        }
                    }
                });
    }
    private void parseStudentId(String studentId, OnSuccessListener<String> listener){
        firebaseFirestore
                .collection("users")
                .document(studentId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            String parsed = document.get("Name").toString();
                            listener.onSuccess(parsed);
                        } else {
                            Log.d("Parse Student ID", "Error getting document: ", task.getException());
                        }
                    }
                });
    }

    private void tableAddColumn(ArrayList<String> data) {
        if(getContext() == null){
            return;
        }

        TableRow tableRow = new TableRow(getContext());

        for (String ndata : data) {
            TextView textView = new TextView(getContext());
            textView.setText(ndata);

            textView.setTextColor(Color.BLACK);
            textView.setTextSize(16);
            textView.setPadding(24, 14, 24, 14);

            tableRow.addView(textView);
        }


        tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        tableRow.setPadding(0, 0, 0, 2);
        tableRow.setBackgroundColor(Color.parseColor("#faad2e"));
        tableLayout.addView(tableRow);
    }


    private void tableAddRow(ArrayList<String> data){
        if(getContext() == null || data.isEmpty()){
            return;
        }

        TableRow tableRow = new TableRow(getContext());
        for (String ndata : data){
            TextView textView = new TextView(getContext());
            textView.setText(ndata);

            if(ndata.equals("Incomplete")){
                textView.setTextColor(Color.RED);
            } else if(ndata.equals("Complete")) {
                textView.setTextColor(Color.GREEN);
            } else {
                textView.setTextColor(Color.BLACK);
                textView.setTypeface(null, Typeface.BOLD);
            }

            textView.setTextSize(16);
            textView.setPadding(24, 14, 24, 14);
            tableRow.setBackgroundColor(Color.WHITE);
            tableRow.addView(textView);

        }

        tableLayout.addView(tableRow);
    }
}