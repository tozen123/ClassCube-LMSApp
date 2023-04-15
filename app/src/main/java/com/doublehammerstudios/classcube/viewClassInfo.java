package com.doublehammerstudios.classcube;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.doublehammerstudios.classcube.Activity.MainActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.List;

public class viewClassInfo extends AppCompatActivity implements classListAdapter.ItemClickListener{
    public String mClassCode;
    public String[] stdList;

    public String strClassName;
    classListAdapter adapter;

    Button btnDeleteClass, btnLeaveClass;

    Configs mConfigs;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String userId;

    TextView noStudentsTextView, mClassName;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_class_info);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();

        if(bundle != null){
            stdList = (String[]) bundle.get("studentList");
            mClassCode = (String) bundle.get("CLASS_CODE");
            strClassName = (String) bundle.get("CLASS_NAME");

            Arrays.toString(stdList);
        }
        mClassName = findViewById(R.id.viewClassInfo_className);
        noStudentsTextView = findViewById(R.id.noStudentEmptyState);
        RecyclerView recyclerView = findViewById(R.id.viewClassInfo_recyclerView);



        mClassName.setText(strClassName);

         /*
        ------------------------------------------------------------------------------------------------------------------
        FIX THIS PART
        ------------------------------------------------------------------------------------------------------------------
         */

        List<String> data;
        data = Arrays.asList(stdList);
        if (data.contains("No student has joined this class")) {
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
        }


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new classListAdapter(this, Arrays.asList(stdList));
        adapter.setClickListener(viewClassInfo.this);
        recyclerView.setAdapter(adapter);



        // Get
        btnDeleteClass = findViewById(R.id.viewClassInfo_deleteClassButton);
        btnLeaveClass = findViewById(R.id.viewClassInfo_leaveClassButton);
        btnDeleteClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("class")
                        .whereEqualTo("classCode", mClassCode)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                        DocumentReference documentReference = documentSnapshot.getReference();

                                        documentReference.delete()
                                                .addOnSuccessListener(aVoid -> {
                                                    createAlertDialog("Class Deleted", "Class has been successfully deleted!");
                                                })
                                                .addOnFailureListener(e -> {
                                                    createAlertDialog("Firestore Failure Listener", "Error:  Failed to delete data from the database");
                                                });
                                    }
                                }

                            }
                        })
                        .addOnFailureListener(e -> {
                            createAlertDialog("Firestore Failure Listener", "Error:  Failed to fetch data from the database");
                        });
            }
        });

        btnLeaveClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("class")
                        .whereEqualTo("classCode", mClassCode)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    List<String> stringArray = (List<String>) documentSnapshot.get("classStudents");
                                    if (stringArray != null) {
                                        stringArray.remove(userId);
                                        documentSnapshot.getReference().update("classStudents", stringArray);
                                    }
                                }
                                Toast.makeText(viewClassInfo.this, "You have successfully leave the class: "+mClassCode, Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .addOnFailureListener(e -> {
                            createAlertDialog("Firestore Failure Listener", "Error:  Failed to fetch data from the database");
                        });
            }
        });

        if(Configs.userType.equals("Student")){
            btnDeleteClass.setVisibility(View.INVISIBLE);
            btnDeleteClass.setEnabled(false);

            btnLeaveClass.setVisibility(View.VISIBLE);
            btnLeaveClass.setEnabled(true);
        } else if(mConfigs.userType.equals("Teacher/Instructor/Professor")){
            btnLeaveClass.setVisibility(View.INVISIBLE);
            btnLeaveClass.setEnabled(false);

            btnDeleteClass.setVisibility(View.VISIBLE);
            btnDeleteClass.setEnabled(true);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "This is " + adapter.getItem(position) + " a very bright one! ", Toast.LENGTH_SHORT).show();
    }

    public void createAlertDialog(String title, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(viewClassInfo.this);
        builder.setTitle(""+title);
        builder.setMessage(""+message)
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();

                        Intent i=new Intent(viewClassInfo.this, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);

                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}


class classListAdapter extends RecyclerView.Adapter<classListAdapter.ViewHolder> {

    private List<String> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    classListAdapter(Context context, List<String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.student_list_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String student = mData.get(position);
        holder.myTextView.setText(student);
        String item = mData.get(position);

        int itemCount = getItemCount();
        holder.bind(item, itemCount);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        private TextView myTextViewCount;
        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.studentNameValue);
            myTextViewCount = itemView.findViewById(R.id.studentCountValue);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }

        public void bind(String item, int itemCount) {

            myTextViewCount.setText(""+itemCount);
        }
    }



    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}