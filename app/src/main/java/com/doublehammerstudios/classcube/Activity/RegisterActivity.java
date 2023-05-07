package com.doublehammerstudios.classcube.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.doublehammerstudios.classcube.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    public static final String TAG = "TAG";
    TextView btn_alreadyHaveAnAccount;
    TextView btn_termsAndAgreement;

    EditText inputName, inputEmail, inputPassword, inputConfirmPassword;

    RadioGroup rdTypeOfUser;
    RadioButton selectedRadioButton;
    Button btnRegister;

    String emailPattern = "[a-zA-z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseFirestore fStore;
    String userID;
    String typeOfUser = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        mUser = mAuth.getCurrentUser();

        rdTypeOfUser = (RadioGroup) findViewById(R.id.rdg_typeUser);

        inputName = (EditText) findViewById(R.id.edtxt_name);
        inputEmail = (EditText) findViewById(R.id.edtxt_email);
        inputPassword = (EditText) findViewById(R.id.edtxt_password);
        inputConfirmPassword = (EditText) findViewById(R.id.edtxt_confirmpassword);

        btn_alreadyHaveAnAccount = (TextView) findViewById(R.id.btn_AlreadyHaveAnAccount);
        btn_termsAndAgreement = (TextView) findViewById(R.id.termsandagreement);

        btnRegister = (Button) findViewById(R.id.btn_createAccount);

        progressDialog = new ProgressDialog(this);

        btn_alreadyHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        btn_termsAndAgreement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, TermsAndAgreementActivity.class);
                startActivity(intent);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performAuthentication();
            }
        });

    }

    private void performAuthentication() {
        String name = inputName.getText().toString();
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        String confirmPassword = inputConfirmPassword.getText().toString();


        int selectedRadioButtonId = rdTypeOfUser.getCheckedRadioButtonId();
        if (selectedRadioButtonId != -1) {
            selectedRadioButton = findViewById(selectedRadioButtonId);
            typeOfUser = selectedRadioButton.getText().toString();
        }

        if(name.isEmpty()){
            inputName.setError("Goddamn it!, Enter your name!");
        } else if(!email.matches(emailPattern)){
            inputEmail.setError("Goddamn it!, Enter correct email!");
        } else if (password.isEmpty() || password.length()<6){
            inputPassword.setError("Goddamn it!, Enter proper password!");
        } else if(!password.equals(confirmPassword)){
            inputConfirmPassword.setError("Goddamn it!, Password does not matched!");
        } else if(typeOfUser.isEmpty()){
            Toast.makeText(this, "Goddamn it!, Choose what type user!", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.setMessage("Registering...");
            progressDialog.setTitle("Create Account");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();


            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){

                        progressDialog.dismiss();

                        userID = mAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = fStore.collection("users").document(userID);
                        Map<String,Object> user = new HashMap<>();
                        user.put("Name",name);
                        user.put("Email",email);
                        user.put("TypeOfUser", typeOfUser);


                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: user Profile is created for "+ userID);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: " + e.toString());
                            }
                        });
                        setActivity();
                        Toast.makeText(RegisterActivity.this, "Registration Completed", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration did not finished: "+ task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


    }
    private void setActivity(){
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}