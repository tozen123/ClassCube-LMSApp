package com.doublehammerstudios.classcube.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.doublehammerstudios.classcube.ClassActivityPost;
import com.doublehammerstudios.classcube.QuizQuestion;
import com.doublehammerstudios.classcube.QuizQuestionItem;
import com.doublehammerstudios.classcube.QuizQuestionItemAdapter;
import com.doublehammerstudios.classcube.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class postQuizCreatorActivity extends AppCompatActivity {
    public String CLASS_CODE;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String userId, dateOutput;
    private String _postStatus = "Incomplete";
    EditText quizPostTitle, quizPostSubject;
    TextView textView_dateText;
    DatePickerDialog datePickerDialog;
    RecyclerView quizItemsRecyclerView;
    Button btnAddQuestion, btnPost, btnDatePick;
    QuizQuestionItemAdapter quizQuestionItemAdapter;
    ArrayList<QuizQuestionItem> quizQuestionItemArrayList = new ArrayList<QuizQuestionItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_quiz_creator);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();

        Intent iin= getIntent();
        Bundle bundle = iin.getExtras();

        if(bundle != null){
            CLASS_CODE = (String) bundle.get("CLASS_CODE");
        }

        btnAddQuestion = findViewById(R.id.vbtn_addQuestion);
        btnPost = findViewById(R.id.vBtn_PostQuiz);
        btnDatePick = findViewById(R.id.postQuizCreator_PostDueDatePicker);
        quizPostTitle = findViewById(R.id.postQuizCreator_PostTitle);
        quizPostSubject = findViewById(R.id.postQuizCreator_PostSubject);
        textView_dateText = findViewById(R.id.postQuizCreator_postDueDateText);

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        textView_dateText.setText(formattedDate);

        btnAddQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDialogAddQuestion();
            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = quizPostTitle.getText().toString();
                String subject = quizPostSubject.getText().toString();

                if(title.isEmpty()){
                    Toast.makeText(postQuizCreatorActivity.this, "Error: title field must not be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(subject.isEmpty()){
                    Toast.makeText(postQuizCreatorActivity.this, "Error: title subject must not be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                /*
                QuizQuestion quizQuestion = new QuizQuestion(title, subject, dateOutput, _postStatus, quizQuestionItemArrayList, null);
                CreateQuizPost(quizQuestion);
                 */

            }
        });

        btnDatePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCalendarDialog();
            }
        });

        quizItemsRecyclerView = findViewById(R.id.questionsRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        quizItemsRecyclerView.setLayoutManager(linearLayoutManager);
        quizQuestionItemAdapter = new QuizQuestionItemAdapter(quizQuestionItemArrayList, getApplicationContext());
        quizItemsRecyclerView.setAdapter(quizQuestionItemAdapter);
    }

    public void setDialogAddQuestion(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Question");

        final View customLayout = getLayoutInflater().inflate(R.layout.create_quiz_question_dialog, null);
        builder.setView(customLayout);

        builder.setPositiveButton("Create", null);
        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                EditText input_questionDesc = customLayout.findViewById(R.id.edt_question_desc);
                EditText input_questionNumber= customLayout.findViewById(R.id.edt_question_number);
                EditText input_questionChoice1 = customLayout.findViewById(R.id.edt_question_chosen1);
                EditText input_questionChoice2 = customLayout.findViewById(R.id.edt_question_chosen2);
                EditText input_questionChoice3 = customLayout.findViewById(R.id.edt_question_chosen3);
                EditText input_questionChoice4 = customLayout.findViewById(R.id.edt_question_chosen4);
                RadioGroup input_radioGroupAnswer = customLayout.findViewById(R.id.radioGroupAnswer);

                EditText[] editTexts = {input_questionDesc, input_questionNumber, input_questionChoice1, input_questionChoice2, input_questionChoice3, input_questionChoice4};

                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        for (EditText editText : editTexts) {
                            if (editText.getText().toString().isEmpty()) {
                                editText.setError("Error: Please input value in the field");
                            }
                        }
                        int selectedId = input_radioGroupAnswer.getCheckedRadioButtonId();

                        if (selectedId == -1) {
                            Toast.makeText(getApplicationContext(), "Error: Please choose a answer", Toast.LENGTH_LONG).show();
                        } else {
                            RadioButton radioButton = customLayout.findViewById(selectedId);
                            String selectedValue = radioButton.getText().toString();


                            /*
                            Create Class for saving quiz
                             */
                            QuizQuestionItem quizQuestionItem =
                                    new QuizQuestionItem(input_questionNumber.getText().toString(),
                                            input_questionDesc.getText().toString(),
                                            input_questionChoice1.getText().toString(),
                                            input_questionChoice2.getText().toString(),
                                            input_questionChoice3.getText().toString(),
                                            input_questionChoice4.getText().toString(),
                                            selectedValue);

                            quizQuestionItemArrayList.add(quizQuestionItem);
                            quizQuestionItemArrayList.forEach( quizQuestionItem1 -> {
                                Log.d("q123", "desc: "+quizQuestionItem1.getQuizQuestionDesc().toString());
                                Log.d("q123", "getQuizQuestionChoiceA: "+quizQuestionItem1.getQuizQuestionChoiceA().toString());
                                Log.d("q123", "getQuizQuestionChoiceB: "+quizQuestionItem1.getQuizQuestionChoiceB().toString());
                                Log.d("q123", "getQuizQuestionChoiceC: "+quizQuestionItem1.getQuizQuestionChoiceC().toString());
                                Log.d("q123", "getQuizQuestionChoiceD: "+quizQuestionItem1.getQuizQuestionChoiceD().toString());
                                Log.d("q123", "getQuizQuestionChoiceAnswer: "+quizQuestionItem1.getQuizQuestionChoiceAnswer().toString());
                            });

                            dialog.dismiss();
                        }

                    }
                });
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    public void CreateQuizPost(QuizQuestion quizQuestion){
        firebaseFirestore.collection("class")
                .whereEqualTo("classCode", CLASS_CODE)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Toast.makeText(postQuizCreatorActivity.this, "Successfully created class post : "+document.getString("className"), Toast.LENGTH_SHORT).show();

                                firebaseFirestore.collection("class")
                                        .document(document.getId())
                                        .update("classActivities", FieldValue.arrayUnion(quizQuestion));
                            }

                            finish();

                        } else {
                            Toast.makeText(postQuizCreatorActivity.this, "Error creating class documents: "+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void openCalendarDialog(){
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day

        datePickerDialog = new DatePickerDialog(postQuizCreatorActivity.this,
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
}