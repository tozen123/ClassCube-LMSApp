package com.doublehammerstudios.classcube.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.doublehammerstudios.classcube.Class;
import com.doublehammerstudios.classcube.ClassRecordTabAdapter;
import com.doublehammerstudios.classcube.Configs;
import com.doublehammerstudios.classcube.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StudentClassActivityRecordsFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    ArrayList<Class> classArrayList = new ArrayList<Class>();
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String userId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    /*

    Add a functionality where the teacher can grade the activity of the students

     */
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();

        getClasses();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_student_class_activity_records, container, false);
    }

    public void getClasses(){
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
                            }
                        }

                        if(classArrayList.isEmpty()){
                            return;
                        }

                        if (getView() == null) {
                            return;
                        }

                        tabLayout = getView().findViewById(R.id.classRecsTabLayout);
                        viewPager = getView().findViewById(R.id.classRecsViewPager);

                        ClassRecordTabAdapter pagerAdapter = new ClassRecordTabAdapter(getChildFragmentManager(), classArrayList);
                        viewPager.setAdapter(pagerAdapter);
                        tabLayout.setupWithViewPager(viewPager);
                    }
                });
    }
}