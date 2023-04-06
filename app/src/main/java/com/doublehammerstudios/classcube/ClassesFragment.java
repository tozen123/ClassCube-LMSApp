package com.doublehammerstudios.classcube;

import android.app.AlertDialog;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;


public class ClassesFragment extends Fragment {
    Configs mConfigs;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String userId;

    private RecyclerView classRecyclerView;
    private ArrayList<Class> classArrayList;
    private AdapterItem classRecyclerViewAdapter;
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
            public void onClick(View viewz) {
                if(mConfigs.userType.equals("Student")){
                    showJoinClassDialogButtonClicked(viewz);
                } else if(mConfigs.userType.equals("Teacher/Instructor/Professor")){
                    showCreateClassDialogButtonClicked(viewz);
                }

            }
        });

        // creating our new array list
        classArrayList = new ArrayList<>();
        classRecyclerView.setHasFixedSize(true);
        classRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // adding our array list to our recycler view adapter class.
        classRecyclerViewAdapter = new AdapterItem(classArrayList, getActivity());

        // setting adapter to our recycler view.
        classRecyclerView.setAdapter(classRecyclerViewAdapter);
        if(mConfigs.userType.equals("Student")){
            firebaseFirestore.collection("class").whereArrayContains("classStudents", userId).get()

                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                loadingPB.setVisibility(View.GONE);
                                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                                for (DocumentSnapshot documentSnapshot : list) {
                                    Class c = documentSnapshot.toObject(Class.class);
                                    classArrayList.add(c);
                                }
                                classRecyclerViewAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getActivity(), "No data found in Database", Toast.LENGTH_SHORT).show();
                                loadingPB.setVisibility(View.INVISIBLE);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Fail to get the data.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }else{
            firebaseFirestore.collection("class").get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (!queryDocumentSnapshots.isEmpty()) {

                                loadingPB.setVisibility(View.GONE);
                                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                                for (DocumentSnapshot documentSnapshot : list) {
                                    if(mConfigs.userType.equals("Teacher/Instructor/Professor")){
                                        if(Objects.equals(documentSnapshot.getString("classTeacherID"), userId)){
                                            Class c = documentSnapshot.toObject(Class.class);
                                            classArrayList.add(c);
                                        }
                                    }
                                }
                                classRecyclerViewAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getActivity(), "No data found in Database", Toast.LENGTH_SHORT).show();
                                loadingPB.setVisibility(View.INVISIBLE);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Fail to get the data.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_classes, container, false);
    }

    public void showCreateClassDialogButtonClicked(View view) {
        // Create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Create Class");

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
            } else if(input_classCode.getText().toString().isEmpty()){
                Toast.makeText(getActivity(), "Error: failed to create class due to field, Class Code being empty", Toast.LENGTH_LONG).show();
            } else if(input_classSubject.getText().toString().isEmpty()){
                Toast.makeText(getActivity(), "Error: failed to create class due to field, Class Subject being empty", Toast.LENGTH_LONG).show();
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

    // Do something with the data coming from the AlertDialog
    private void CreateClass(String cc_className, String cc_classCode, String cc_classSubject){
        Class newClass = new Class(cc_className, cc_classCode, cc_classSubject, userId, Configs.userName, null);
        firebaseFirestore.collection("class")
                .add(newClass);
    }

    public void showJoinClassDialogButtonClicked(View view) {
        // Create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Join Class");

        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.join_class_dialog, null);
        builder.setView(customLayout);

        // add a button
        builder.setPositiveButton("Join", (dialog, which) -> {
            // send data from the AlertDialog to the Activity
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

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    // Do something with the data coming from the AlertDialog
    private void JoinClass(String classCode){

        firebaseFirestore.collection("class")
                .whereEqualTo("classCode", classCode)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("SUPERTAGGER3", document.getId() + " => " + document.getData());

                                firebaseFirestore.collection("class")
                                        .document(document.getId())
                                        .update("classStudents", FieldValue.arrayUnion(userId));
                            }
                        } else {
                            Log.d("SUPERTAGGER3", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}