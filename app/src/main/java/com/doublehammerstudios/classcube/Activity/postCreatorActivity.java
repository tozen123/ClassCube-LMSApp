package com.doublehammerstudios.classcube.Activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.doublehammerstudios.classcube.ClassActivityPost;
import com.doublehammerstudios.classcube.ClassPost;
import com.doublehammerstudios.classcube.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class postCreatorActivity extends AppCompatActivity {

    /*
    Handles post creating in two major features: Post and Activity Post

     */
    public String CLASS_CODE;
    public String CLASS_POSTING_TYPE;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String userId;

    Button btnPostButton;
    String dateOutput;
    EditText input_postTitle, input_postSubject;
    Button button_Post;
    TextView textView_dateText, textView_filePath, textView_postHeader;
    DatePickerDialog datePickerDialog;
    LinearLayout extraDataLayout;

    ImageButton imageButton_AddFile;
    private static final int PICK_FILE_REQUEST_CODE = 1234;
    private Uri file_uri;
    private String fileUrl;
    private String _postStatus = "Incomplete";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_creator);

        Intent iin= getIntent();
        Bundle bundle = iin.getExtras();

        if(bundle != null){
            CLASS_CODE = (String) bundle.get("CLASS_CODE");
            CLASS_POSTING_TYPE = (String) bundle.get("CLASS_POSTING_TYPE");
        }

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();


        btnPostButton = findViewById(R.id.postCreator_PostButton);

        input_postTitle = findViewById(R.id.postCreator_PostTitle);
        input_postSubject = findViewById(R.id.postCreator_PostSubject);
        button_Post = findViewById(R.id.postCreator_PostDueDatePicker);
        textView_dateText = findViewById(R.id.postCreator_postDueDateText);
        imageButton_AddFile = findViewById(R.id.postCreator_postAddFile);
        textView_filePath = findViewById(R.id.postCreator_postAddFilePath);
        textView_postHeader = findViewById(R.id.postCreator_postTypeTitle);
        extraDataLayout = findViewById(R.id.extraDataLayout);

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        textView_dateText.setText(formattedDate);

        /*
        Disable and Hide some element depending on the POSTING_TYPE
         */

        if(CLASS_POSTING_TYPE.equals("Post")){
            imageButton_AddFile.setEnabled(true);
            extraDataLayout.setVisibility(View.VISIBLE);
            textView_postHeader.setText("Module Post");

        } else if (CLASS_POSTING_TYPE.equals("Activity Post")){
            imageButton_AddFile.setEnabled(false);
            extraDataLayout.setVisibility(View.INVISIBLE);
            textView_postHeader.setText("Activity Post");
        }


        btnPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = input_postTitle.getText().toString();
                String subject = input_postSubject.getText().toString();

                if(title.isEmpty()){
                    Toast.makeText(postCreatorActivity.this, "Error: title field must not be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(subject.isEmpty()){
                    Toast.makeText(postCreatorActivity.this, "Error: title subject must not be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(CLASS_POSTING_TYPE.equals("Post")) {
                    Toast.makeText(postCreatorActivity.this, "CLASS_POSTING_TYPE: Post", Toast.LENGTH_SHORT).show();

                    if (file_uri == null) {
                        ClassPost post = new ClassPost(title, subject, dateOutput, fileUrl, _postStatus, null);
                        createClassPost(post);
                    } else {
                        uploadFileToFirestore(file_uri, new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String fileUrl = uri.toString();
                                ClassPost post = new ClassPost(title, subject, dateOutput, fileUrl, _postStatus, null);
                                createClassPost(post);
                            }
                        });
                    }
                } else if (CLASS_POSTING_TYPE.equals("Activity Post")){

                    final ProgressDialog progressDialog = new ProgressDialog(postCreatorActivity.this);
                    progressDialog.setTitle("Creating Activity Post....");
                    progressDialog.show();

                    Toast.makeText(postCreatorActivity.this, "CLASS_POSTING_TYPE: Activity Post", Toast.LENGTH_SHORT).show();

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();
                    StorageReference folderRef = storageRef.child("activity_bins/"+CLASS_CODE+"/"+title);
                    folderRef.child(".metadata").putBytes(new byte[0])
                            .addOnSuccessListener(taskSnapshot -> {
                                String submissionLink = folderRef.getPath().toString();

                                Log.d("submissionLINKED", "MSG: "+submissionLink);
                                ClassActivityPost activityPost = new ClassActivityPost(title, subject, dateOutput, _postStatus, submissionLink,null);
                                createClassActivityPost(activityPost);
                            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                    double progress = (100.0 * snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                                    progressDialog.setMessage("Making Directory Bin: "+(int)progress+"%");
                                }
                            })

                            .addOnFailureListener(e -> {
                                // SYMPRE ERROR NANAMAN
                            });
                }
            }
        });

        button_Post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCalendarDialog();
            }
        });

        imageButton_AddFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("*/*");
                startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            file_uri = data.getData();
            textView_filePath.setText(file_uri.toString());
        }
    }

    private void uploadFileToFirestore(Uri uri, OnSuccessListener<Uri> successListener) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading File....");
        progressDialog.show();

        StorageReference reference = FirebaseStorage.getInstance().getReference().child("files").child(CLASS_CODE+"_"+input_postTitle.getText().toString());
        reference.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        uriTask.addOnSuccessListener(successListener);
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100.0 * snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                        progressDialog.setMessage("Uploaded: "+(int)progress+"%");
                    }
                });
    }

    public void openCalendarDialog(){
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day

        datePickerDialog = new DatePickerDialog(postCreatorActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // set day of month , month and year value in the edit text
                        dateOutput = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        textView_dateText.setText(dateOutput);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();

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
                                Toast.makeText(postCreatorActivity.this, "Successfully created class post : "+document.getString("className"), Toast.LENGTH_SHORT).show();

                                firebaseFirestore.collection("class")
                                        .document(document.getId())
                                        .update("classActivities", FieldValue.arrayUnion(newClassPost));
                            }

                            finish();

                        } else {
                            Toast.makeText(postCreatorActivity.this, "Error creating class documents: "+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void createClassActivityPost(ClassActivityPost newClassActivityPost){
        firebaseFirestore.collection("class")
                .whereEqualTo("classCode", CLASS_CODE)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Toast.makeText(postCreatorActivity.this, "Successfully created class post : "+document.getString("className"), Toast.LENGTH_SHORT).show();

                                firebaseFirestore.collection("class")
                                        .document(document.getId())
                                        .update("classActivities", FieldValue.arrayUnion(newClassActivityPost));
                            }

                            finish();

                        } else {
                            Toast.makeText(postCreatorActivity.this, "Error creating class documents: "+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}