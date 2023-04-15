package com.doublehammerstudios.classcube;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ClassItemAdapter extends RecyclerView.Adapter<ClassItemAdapter.ViewHolder> {
    private ArrayList<Class> classArrayList;
    private Context context;
    private View.OnClickListener onClickListener;
    private ClassHandler listener;
    public ClassItemAdapter(ArrayList<Class> classArrayList, Context context, ClassHandler listener) {
        this.classArrayList = classArrayList;
        this.context = context;
        this.listener = listener;
    }
    @NonNull
    @Override
    public ClassItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // passing our layout file for displaying our card item
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.class_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ClassItemAdapter.ViewHolder holder, int position) {
        // setting data to our text views from our modal class.
        Class aClass = classArrayList.get(position);

        holder.classNameValue.setText(aClass.getClassName());
        holder.classCodeValue.setText(aClass.getClassCode());
        holder.classSubjectValue.setText(aClass.getClassSubject());
        //holder.classTeacherValue.setText(aClass.getClassTeacherID());
        holder.classTeacherNameValue.setText(aClass.getClassTeacherName());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClassClicked(classArrayList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        // returning the size of our array list.
        return classArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our text views.
        public CardView cardView;
        private final TextView classNameValue;
        private final TextView classCodeValue;
        private final TextView classSubjectValue;
        //private final TextView classTeacherValue;
        private final TextView classTeacherNameValue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.classItemId);
            // initializing our text views.
            classNameValue = itemView.findViewById(R.id.classNameValue);
            classCodeValue = itemView.findViewById(R.id.classCodeValue);
            classSubjectValue = itemView.findViewById(R.id.classSubjectValue);
            //classTeacherValue = itemView.findViewById(R.id.classTeacherValue);
            classTeacherNameValue = itemView.findViewById(R.id.classTeacherNameValue);
        }
    }
}

