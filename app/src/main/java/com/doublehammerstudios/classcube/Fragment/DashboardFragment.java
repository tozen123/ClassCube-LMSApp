package com.doublehammerstudios.classcube.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.doublehammerstudios.classcube.Activity.viewedClassActivity;
import com.doublehammerstudios.classcube.Class;
import com.doublehammerstudios.classcube.ClassActivityPost;
import com.doublehammerstudios.classcube.ClassActivityPostItemAdapter;
import com.doublehammerstudios.classcube.ClassPost;
import com.doublehammerstudios.classcube.Configs;
import com.doublehammerstudios.classcube.R;
import com.doublehammerstudios.classcube.viewClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class DashboardFragment extends Fragment{

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String userId;
    TextView tv_userName, tv_classStatsHeader, tv_joinedClassValue, tv_incompleteActivitesValue, todoHeader;
    Configs mConfigs;
    RecyclerView dashboardClassItemRecyclerView, todoRecyclerView;
    ArrayList<Class> classArrayList;
    ArrayList<ClassActivityPost> todoArrayList;
    LinearLayoutManager linearLayoutManager;
    dashboardClassItemAdapter mDashboardClassItemAdapter;

    todoClassItemAdapter mTodoClassItemAdapter;
    LinearLayout statsData1Layout;
    int joinedCount;
    RelativeLayout filler;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();

        tv_userName = view.findViewById(R.id.helloUserValue);
        tv_classStatsHeader = view.findViewById(R.id.ClassStatsHeader);
        statsData1Layout = view.findViewById(R.id.statsData1);

        tv_joinedClassValue = view.findViewById(R.id.joinedClassValue);
        tv_incompleteActivitesValue = view.findViewById(R.id.incompleteActivitesValue);

        todoHeader = view.findViewById(R.id.ToDoHeader);
        filler = view.findViewById(R.id.filler);

        dashboardData1(view);

        DocumentReference documentReference = firebaseFirestore.collection("users").document(userId);
        documentReference.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                String name = value.getString("Name");;
                String typeUser = value.getString("TypeOfUser");

                if(name.isEmpty()){
                    Toast.makeText(getActivity(), "Failed to open database! "+error.toString(), Toast.LENGTH_SHORT).show();
                }
                tv_userName.setText(name);

                if (typeUser == null) {
                    return;
                }

                if (typeUser.equals("Student")) {
                    statsData1Layout.setVisibility(View.VISIBLE);
                    todoHeader.setVisibility(View.VISIBLE);
                    tv_classStatsHeader.setVisibility(View.VISIBLE);
                    filler.setVisibility(View.INVISIBLE);

                    todoData(view);
                } else if (typeUser.equals("Teacher/Instructor/Professor")){
                    statsData1Layout.setVisibility(View.INVISIBLE);
                    todoHeader.setVisibility(View.INVISIBLE);
                    tv_classStatsHeader.setVisibility(View.INVISIBLE);
                    filler.setVisibility(View.VISIBLE);
                } else {
                    return;
                }
                /*
                    Summary
                    Listens to the function in retrieving the data.
                 */
                retrieveDataForIncompleteActivities(new OnCountRetrievedListener() {
                    @Override
                    public void onCountRetrieved(int count) {
                        tv_incompleteActivitesValue.setText(String.valueOf(count));
                    }
                });

                /*
                    Summary
                    Listens to the function in retrieving the data.
                 */
                retrieveDataForJoinedClass(new OnCountRetrievedListener() {
                    @Override
                    public void onCountRetrieved(int count) {
                        tv_joinedClassValue.setText(String.valueOf(count));
                    }
                });


            }
        });
    }
    /*
        Summary
        Function to retrieve the data and send the count in the listener.
        Uses the userID to get the appearance of that id in the class via the array classStudents.
     */
    public void retrieveDataForJoinedClass(final OnCountRetrievedListener listener){
        firebaseFirestore.collection("class")
                .whereArrayContains("classStudents", userId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    // Error: Getting the required documents
                    listener.onCountRetrieved(0);
                    return;
                }

                int count = value.size();
                listener.onCountRetrieved(count);
            }
        });
    }
    /*
        Summary
        Function to Scan the documents and if the current userID is present in the
        array that the one who finished the activity will incrememnt the counting into 1

     */
    public void retrieveDataForIncompleteActivities(final OnCountRetrievedListener listener){
        firebaseFirestore.collection("class")
                .whereArrayContains("classStudents", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(!task.isSuccessful()){
                            // Error: Getting the required documents
                            listener.onCountRetrieved(0);
                        }

                        List<DocumentSnapshot> documents = task.getResult().getDocuments();

                        int count = 0;
                        for (DocumentSnapshot document : documents) {
                            List<Map<String, Object>> classActivitiesList = (List<Map<String, Object>>) document.get("classActivities");
                            if (classActivitiesList != null) {
                                for (Map<String, Object> classActivity : classActivitiesList) {
                                    List<String> ActivityPost = (List<String>) classActivity.get("studentWhoFinishedClassActivityPost");
                                    List<String> ClassPost = (List<String>) classActivity.get("studentWhoFinishedClassPost");
                                    if (ActivityPost != null) {
                                        if(!ActivityPost.contains(userId)){
                                            count++;
                                        }
                                    }

                                    if (ClassPost != null) {
                                        if(!ClassPost.contains(userId)){
                                            count++;
                                        }
                                    }
                                    listener.onCountRetrieved(count);
                                }
                            }
                        }

                    }
                });
    }

    public void todoData(@NonNull View view){
        todoRecyclerView = view.findViewById(R.id.todoRecyclerView);

        todoArrayList = new ArrayList<>();
        mTodoClassItemAdapter = new todoClassItemAdapter(todoArrayList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        todoRecyclerView.setLayoutManager(layoutManager);
        todoRecyclerView.setAdapter(mTodoClassItemAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                todoRecyclerView.getContext(),
                layoutManager.getOrientation()
        );

        dividerItemDecoration.setDrawable(
                ContextCompat.getDrawable(getContext(), R.drawable.divider_item_2)
        );

        todoRecyclerView.addItemDecoration(dividerItemDecoration);
        firebaseFirestore.collection("class")
                .whereArrayContains("classStudents", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(!task.isSuccessful()){
                            // Error: Getting the required documents
                        }

                        todoArrayList.clear();

                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        for (DocumentSnapshot document : documents) {
                            List<Map<String, Object>> classActivitiesList = (List<Map<String, Object>>) document.get("classActivities");
                            if (classActivitiesList != null) {
                                for (Map<String, Object> classActivity : classActivitiesList) {
                                    if(classActivity.containsKey("classActivityPostSubmissionBinLink")){
                                        todoArrayList.add(new ClassActivityPost(
                                                classActivity.get("classActivityPostTitle").toString(),
                                                classActivity.get("classActivityPostSubject").toString(),
                                                classActivity.get("classActivityPostDueDate").toString(),
                                                classActivity.get("classActivityPostStatus").toString(),
                                                classActivity.get("classActivityPostSubmissionBinLink").toString(),
                                                (ArrayList<String>) classActivity.get("studentWhoFinishedClassActivityPost")
                                        ));
                                    }

                                }
                            }
                        }

                        mTodoClassItemAdapter.notifyDataSetChanged();

                    }
                });
    }
    public void dashboardData1(@NonNull View view){

        dashboardClassItemRecyclerView = view.findViewById(R.id.dashboardRecyclerView);

        classArrayList = new ArrayList<>();

        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mDashboardClassItemAdapter = new dashboardClassItemAdapter(classArrayList);
        dashboardClassItemRecyclerView.setLayoutManager(linearLayoutManager);
        dashboardClassItemRecyclerView.setAdapter(mDashboardClassItemAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                dashboardClassItemRecyclerView.getContext(),
                linearLayoutManager.getOrientation()
        );

        dividerItemDecoration.setDrawable(
                ContextCompat.getDrawable(getContext(), R.drawable.divider_item)
        );

        dashboardClassItemRecyclerView.addItemDecoration(dividerItemDecoration);
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

                        if (queryDocumentSnapshots.isEmpty()) {
                            Toast.makeText(getActivity(), "Fail to get the data.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                        for (DocumentSnapshot documentSnapshot : list) {
                            if(!Configs.checkUserIfStudent()){
                                if(Objects.equals(documentSnapshot.getString("classTeacherID"), userId)){
                                    Class c = documentSnapshot.toObject(Class.class);
                                    classArrayList.add(c);
                                }
                            } else if(Configs.checkUserIfStudent()) {
                                ArrayList<String> cStudents = (ArrayList<String>) documentSnapshot.get("classStudents");

                                if (cStudents != null && cStudents.contains(userId)) {
                                    Class c = documentSnapshot.toObject(Class.class);
                                    classArrayList.add(c);
                                }
                            } else {
                                return;
                            }
                        }
                        mDashboardClassItemAdapter.notifyDataSetChanged();
                    }
                });
    }

    class todoClassItemAdapter extends RecyclerView.Adapter<todoClassItemAdapter.todoItemHolder>{

        ArrayList<ClassActivityPost> postData;

        public todoClassItemAdapter(ArrayList<ClassActivityPost> postData) {
            super();
            this.postData = postData;
        }
        @NonNull
        @Override
        public todoClassItemAdapter.todoItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.to_do_class_activity_item, parent, false);
            return new todoClassItemAdapter.todoItemHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull todoClassItemAdapter.todoItemHolder holder, int position) {
            ClassActivityPost nClassActivityPost = postData.get(position);
            holder.textViewPostTitle.setText(nClassActivityPost.getClassActivityPostTitle());
            holder.textViewSubject.setText(nClassActivityPost.getClassActivityPostSubject());
            holder.textViewPostDueDate.setText(nClassActivityPost.getClassActivityPostDueDate());
            holder.classActivityPostData = nClassActivityPost;
        }

        @Override
        public int getItemCount() {
            return postData.size();
        }

        class todoItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            TextView textViewPostTitle, textViewPostDueDate, textViewSubject;
            ClassActivityPost classActivityPostData;
            public todoItemHolder(@NonNull View itemView) {
                super(itemView);


                textViewPostTitle = itemView.findViewById(R.id.TclassPostTitle);
                textViewPostDueDate = itemView.findViewById(R.id.TclassPostDuedate);
                textViewSubject = itemView.findViewById(R.id.TclassPostSubject);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), viewedClassActivity.class);

                intent.putExtra("CLASS_TITLE",  classActivityPostData.getClassActivityPostTitle());
                intent.putExtra("CLASS_SUBJECT",  classActivityPostData.getClassActivityPostSubject());
                intent.putExtra("CLASS_DUEDATE",  classActivityPostData.getClassActivityPostDueDate());
                intent.putExtra("CLASS_ACTIVITY_BIN",  classActivityPostData.getClassActivityPostSubmissionBinLink());
                intent.putExtra("CLASS_POST_COMPLETION_STATUS",  classActivityPostData.getClassActivityPostStatus());
                intent.putExtra("CLASS_POST_STUDENTWHOFINISHED",  classActivityPostData.getStudentWhoFinishedClassActivityPost());

                intent.putExtra("CLASS_POST_TYPE",  "ACTIVITY POST");


                //startActivity(intent);
            }
        }
    }
    public interface OnCountRetrievedListener {
        void onCountRetrieved(int count);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    class dashboardClassItemAdapter extends RecyclerView.Adapter<dashboardClassItemAdapter.ClassItemHolder>{
        ArrayList<Class> data;
        public dashboardClassItemAdapter(ArrayList<Class> data) {
            super();
            this.data = data;
        }

        @NonNull
        @Override
        public ClassItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.dashboard_class_item, parent, false);
            return new ClassItemHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ClassItemHolder holder, int position) {
            Class nClass = data.get(position);
            holder.textViewClassName.setText(nClass.getClassName());
            holder.textViewClassSubject.setText(nClass.getClassSubject());
            holder.classData = nClass;
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class  ClassItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            TextView textViewClassName, textViewClassSubject;
            Class classData;
            public ClassItemHolder(@NonNull View itemView) {
                super(itemView);

                textViewClassName = itemView.findViewById(R.id.dashboardCardClassName);
                textViewClassSubject = itemView.findViewById(R.id.dashboardCardSubjectName);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), viewClass.class);

                intent.putExtra("CLASS_DATA", classData);
                startActivity(intent);
            }
        }
    }
}