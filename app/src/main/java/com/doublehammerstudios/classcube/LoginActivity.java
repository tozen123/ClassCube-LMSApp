package com.doublehammerstudios.classcube;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    TextView btn_forgotPassword;
    TextView btn_dontHaveAnAccount;

    EditText inputmEmail, inputmPassword;
    Button btn_Login;
    FirebaseAuth mAuth;

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputmEmail = (EditText) findViewById(R.id.edTxt_email_login);
        inputmPassword = (EditText) findViewById(R.id.edTxt_password_login);

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        btn_Login = findViewById(R.id.btn_logintxt);
        progressDialog = new ProgressDialog(this);

        btn_forgotPassword = (TextView) findViewById(R.id.btn_forgotPasswordtxt);
        btn_dontHaveAnAccount = (TextView) findViewById(R.id.btn_doesntHaveAnAccountTxt);
        btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputmEmail.getText().toString();
                String password = inputmPassword.getText().toString();

                if(TextUtils.isEmpty(email)){
                    inputmEmail.setError("Email is Required.");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    inputmPassword.setError("Password is Required.");
                    return;
                }

                if(password.length() < 6){
                    inputmPassword.setError("Password Must be >= 6 Characters");
                    return;
                }
                Log.d("TAG", "EMAIL: "+email);
                Log.d("TAG", "PASSWORD: "+password);

                progressDialog.setMessage("Logging in...");
                progressDialog.setTitle("Log-In");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }else {
                            Toast.makeText(LoginActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }

                    }
                });

            }
        });

        btn_forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordFactor.class);
                startActivity(intent);
            }
        });

        btn_dontHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });


    }
}