package com.doublehammerstudios.classcube;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterItem extends RecyclerView.Adapter<AdapterItem.ViewHolder> {
    private ArrayList<Class> classArrayList;
    private Context context;

    // creating constructor for our adapter class
    public AdapterItem(ArrayList<Class> classArrayList, Context context) {
        this.classArrayList = classArrayList;
        this.context = context;
    }
    @NonNull
    @Override
    public AdapterItem.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // passing our layout file for displaying our card item
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.class_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterItem.ViewHolder holder, int position) {
        // setting data to our text views from our modal class.
        Class aClass = classArrayList.get(position);
        holder.classNameValue.setText(aClass.getClassName());
        holder.classCodeValue.setText(aClass.getClassCode());
        holder.classSubjectValue.setText(aClass.getClassSubject());
        holder.classTeacherValue.setText(aClass.getClassTeacherID());
        holder.classTeacherNameValue.setText(aClass.getClassTeacherName());
    }

    @Override
    public int getItemCount() {
        // returning the size of our array list.
        return classArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our text views.
        private final TextView classNameValue;
        private final TextView classCodeValue;
        private final TextView classSubjectValue;
        private final TextView classTeacherValue;
        private final TextView classTeacherNameValue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views.
            classNameValue = itemView.findViewById(R.id.classNameValue);
            classCodeValue = itemView.findViewById(R.id.classCodeValue);
            classSubjectValue = itemView.findViewById(R.id.classSubjectValue);
            classTeacherValue = itemView.findViewById(R.id.classTeacherValue);
            classTeacherNameValue = itemView.findViewById(R.id.classTeacherNameValue);
        }
    }
}

