package com.doublehammerstudios.classcube.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.doublehammerstudios.classcube.Class;
import com.doublehammerstudios.classcube.Configs;
import com.doublehammerstudios.classcube.R;
import com.doublehammerstudios.classcube.viewClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class DashboardFragment extends Fragment{

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String userId;
    TextView userName;
    Configs mConfigs;
    RecyclerView dashboardClassItemRecyclerView;
    ArrayList<Class> classArrayList;
    LinearLayoutManager linearLayoutManager;
    dashboardClassItemAdapter mDashboardClassItemAdapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();

        userName = view.findViewById(R.id.helloUserValue);
        dashboardData1(view);

        DocumentReference documentReference = firebaseFirestore.collection("users").document(userId);
        documentReference.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                String name = value.getString("Name");
                if(name.isEmpty()){
                    Toast.makeText(getActivity(), "Failed to open database! "+error.toString(), Toast.LENGTH_SHORT).show();
                }
                userName.setText(name);
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

                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot documentSnapshot : list) {
                                if(Configs.userType.equals("Teacher/Instructor/Professor")){
                                    if(Objects.equals(documentSnapshot.getString("classTeacherID"), userId)){
                                        Log.d("SUPERTAGGER5 ", "6CHECK - "+documentSnapshot.toString());
                                        Class c = documentSnapshot.toObject(Class.class);
                                        classArrayList.add(c);
                                    }
                                } else if(Configs.userType.equals("Student")) {
                                    ArrayList<String> cStudents = (ArrayList<String>) documentSnapshot.get("classStudents");

                                    if (cStudents != null && cStudents.contains(userId)) {
                                        Class c = documentSnapshot.toObject(Class.class);
                                        classArrayList.add(c);
                                    }


                                } else {
                                    return;
                                }
                            }
                        } else {
                           // Toast.makeText(getActivity(), "No data found in Database", Toast.LENGTH_SHORT).show();
                        }

                        mDashboardClassItemAdapter.notifyDataSetChanged();
                    }
                });

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
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.dashboard_class_item, null, false);
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
                Toast.makeText(getActivity(), "Hi "+textViewClassName.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}