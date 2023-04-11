package com.doublehammerstudios.classcube;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class post_creator extends AppCompatActivity {

    public String CLASS_CODE;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String userId;

    Button btnPostButton;

    EditText input_postTitle, input_postSubject, input_postDueDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_creator);

        Intent iin= getIntent();
        Bundle bundle = iin.getExtras();
        CLASS_CODE = (String) bundle.get("CLASS_CODE");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();

        btnPostButton = findViewById(R.id.postCreator_PostButton);

        input_postTitle = findViewById(R.id.postCreator_PostTitle);
        input_postSubject = findViewById(R.id.postCreator_PostSubject);
        input_postDueDate = findViewById(R.id.postCreator_PostDueDate);

        btnPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = input_postTitle.getText().toString();
                String subject = input_postSubject.getText().toString();
                String duedate = input_postDueDate.getText().toString();

                if(title.isEmpty()){
                    Toast.makeText(post_creator.this, "Error: title field must not be empty!", Toast.LENGTH_SHORT).show();
                } else if(subject.isEmpty()){
                    Toast.makeText(post_creator.this, "Error: title subject must not be empty!", Toast.LENGTH_SHORT).show();
                } else if(duedate.isEmpty()){
                    Toast.makeText(post_creator.this, "Error: duedate subject must not be empty!", Toast.LENGTH_SHORT).show();
                } else {
                    ClassPost post = new ClassPost(title, subject, duedate);
                    createClassPost(post);
                }
            }
        });
    }

    private void createClassPost(ClassPost newClassPost){
        firebaseFirestore.collection("class")
                .whereEqualTo("classCode", CLASS_CODE)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Toast.makeText(post_creator.this, "Successfully created class post : "+document.getString("className"), Toast.LENGTH_SHORT).show();

                                firebaseFirestore.collection("class")
                                        .document(document.getId())
                                        .update("classActivities", FieldValue.arrayUnion(newClassPost));
                            }

                            finish();

                        } else {
                            Toast.makeText(post_creator.this, "Error creating class documents: "+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}