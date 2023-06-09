package com.doublehammerstudios.classcube.Fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.doublehammerstudios.classcube.Class;
import com.doublehammerstudios.classcube.ClassHandler;
import com.doublehammerstudios.classcube.ClassItemAdapter;
import com.doublehammerstudios.classcube.Configs;
import com.doublehammerstudios.classcube.R;
import com.doublehammerstudios.classcube.viewClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;


public class ClassesFragment extends Fragment implements ClassHandler {
    Configs mConfigs;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String userId;

    private RecyclerView classRecyclerView;
    private ArrayList<Class> classArrayList;
    private ClassItemAdapter classRecyclerViewAdapter;
    ProgressBar loadingPB;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initializing our variables.
        classRecyclerView = view.findViewById(R.id.idRecyclerViewClass);
        loadingPB = view.findViewById(R.id.progressBar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();

        mConfigs = Configs.getInstance();


        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Configs.userType.equals("Student")){
                    showJoinClassDialogButtonClicked(view);
                } else if(Configs.userType.equals("Teacher/Instructor/Professor")){
                    showCreateClassDialogButtonClicked(view);
                }

            }
        });

        classArrayList = new ArrayList<>();
        classRecyclerView.setHasFixedSize(true);
        classRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        classRecyclerViewAdapter = new ClassItemAdapter(classArrayList, getActivity(), this::onClassClicked);
        classRecyclerView.setAdapter(classRecyclerViewAdapter);

        if(Configs.userType.equals("Student")) {
            studentViewClass();
        } else {
            teacherViewClass();
        }
    }
    public void teacherViewClass()
    {
        firebaseFirestore.collection("class")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Toast.makeText(getActivity(), "Fail to get the data.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        classArrayList.clear();

                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot documentSnapshot : list) {
                                if(Objects.equals(documentSnapshot.getString("classTeacherID"), userId)){
                                    Class c = documentSnapshot.toObject(Class.class);
                                    classArrayList.add(c);
                                }
                            }
                        } else {
                            Toast.makeText(getActivity(), "No data found in Database", Toast.LENGTH_SHORT).show();
                        }

                        classRecyclerViewAdapter.notifyDataSetChanged();
                        loadingPB.setVisibility(View.INVISIBLE);
                    }
                });
    }
    public void studentViewClass()
    {
        firebaseFirestore.collection("class")
                .whereArrayContains("classStudents", userId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Toast.makeText(getActivity(), "Fail to get the data.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        classArrayList.clear();

                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot documentSnapshot : list) {
                                Class c = documentSnapshot.toObject(Class.class);
                                classArrayList.add(c);
                            }
                        }

                        classRecyclerViewAdapter.notifyDataSetChanged();
                        loadingPB.setVisibility(View.INVISIBLE);
                    }
                });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_classes, container, false);
    }

    public void showCreateClassDialogButtonClicked(View view) {
        // Create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.create_class_dialog, null);
        builder.setView(customLayout);

        // add a button
        builder.setPositiveButton("Create", (dialog, which) -> {
            // send data from the AlertDialog to the Activity
            EditText input_className = customLayout.findViewById(R.id.edt_className_create);
            EditText input_classCode = customLayout.findViewById(R.id.edt_classCode_create);
            EditText input_classSubject = customLayout.findViewById(R.id.edt_classSubject_create);

            if(input_className.getText().toString().isEmpty()){
                Toast.makeText(getActivity(), "Error: failed to create class due to field, Class Name being empty", Toast.LENGTH_LONG).show();
            } else if(input_classSubject.getText().toString().isEmpty()){
                Toast.makeText(getActivity(), "Error: failed to create class due to field, Class Subject being empty", Toast.LENGTH_LONG).show();
            } else if(input_classCode.getText().toString().isEmpty()){
                String generatedCode = randomCodeGenerator(6);
                CreateClass(input_className.getText().toString(), generatedCode, input_classSubject.getText().toString());
            } else {
                CreateClass(input_className.getText().toString(), input_classCode.getText().toString(), input_classSubject.getText().toString());
            }

        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }
    String randomCodeGenerator(int stringLength){
        String letters = "abcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(stringLength);

        for (int i = 0; i < stringLength; i++) {
            int index = random.nextInt(letters.length());
            char randomChar = letters.charAt(index);
            sb.append(randomChar);
        }
        return sb.toString();
    }
    private void CreateClass(String cc_className, String cc_classCode, String cc_classSubject){
        Class newClass = new Class(cc_className, cc_classCode, cc_classSubject, userId, Configs.userName, null);
        firebaseFirestore.collection("class")
                .add(newClass)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getActivity(), "Class Name: "+cc_className+" was successfully created!", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Class Name: "+cc_className+" was not created! due to: " + e, Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void showJoinClassDialogButtonClicked(View view) {
        // Create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.join_class_dialog, null);
        builder.setView(customLayout);

        // add a button
        builder.setPositiveButton("Join", (dialog, which) -> {
            EditText input_classCode = customLayout.findViewById(R.id.edt_classCode_join);
            if(input_classCode.getText().toString().isEmpty()){
                Toast.makeText(getActivity(), "Error: failed to join class due to field, Class Code being empty", Toast.LENGTH_LONG).show();
            } else {
                JoinClass(input_classCode.getText().toString());
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


    private void JoinClass(String classCode){
        firebaseFirestore.collection("class")
                .whereEqualTo("classCode", classCode)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Toast.makeText(getActivity(), "Successfully joined class: "+document.getString("className"), Toast.LENGTH_SHORT).show();
                                firebaseFirestore.collection("class")
                                        .document(document.getId())
                                        .update("classStudents", FieldValue.arrayUnion(userId));
                            }
                        } else {
                            Toast.makeText(getActivity(), "Error getting documents: "+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClassClicked(Class selectedClass) {

        Toast.makeText(getActivity(), "Opening Class Name: "+selectedClass.getClassName(), Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getActivity(), viewClass.class);
        intent.putExtra("CLASS_DATA", selectedClass);

        startActivity(intent);

    }
}