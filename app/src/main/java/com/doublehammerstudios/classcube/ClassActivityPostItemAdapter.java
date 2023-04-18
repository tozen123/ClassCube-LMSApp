package com.doublehammerstudios.classcube;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ClassActivityPostItemAdapter extends RecyclerView.Adapter<ClassActivityPostItemAdapter.ViewHolder>{
    private static ArrayList<ClassActivityPost> classActivityPostIArrayList;
    private Context context;
    private ItemClickListener mClickListener;

    public ClassActivityPostItemAdapter(ArrayList<ClassActivityPost> classActivityPostIArrayList, Context context) {
        this.classActivityPostIArrayList = classActivityPostIArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ClassActivityPostItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ClassActivityPostItemAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.class_activity_post_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ClassActivityPostItemAdapter.ViewHolder holder, int position) {
        ClassActivityPost classActivityPost = classActivityPostIArrayList.get(position);
        holder.activityPostTitle.setText(classActivityPost.getClassActivityPostTitle());
        holder.activitySubjectTitle.setText(classActivityPost.getClassActivityPostSubject());
        holder.activityDueDateTitle.setText(classActivityPost.getClassActivityPostDueDate());
        holder.activityStatusTitle.setText(classActivityPost.getClassActivityPostStatus());
    }

    @Override
    public int getItemCount() {
        return classActivityPostIArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView activityPostTitle;
        private TextView activitySubjectTitle;
        private TextView activityDueDateTitle;
        private TextView activityStatusTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            activityPostTitle = itemView.findViewById(R.id.id_textview_classActivityPostTitle);
            activitySubjectTitle = itemView.findViewById(R.id.id_textview_classActivityPostSubject);
            activityDueDateTitle = itemView.findViewById(R.id.id_textview_classActivityPostDuedate);
            activityStatusTitle = itemView.findViewById(R.id.id_textview_classActivityPostStatus);

            if(Configs.userType.equals("Teacher/Instructor/Professor")){
                activityStatusTitle.setVisibility(View.INVISIBLE);
            }else if(Configs.userType.equals("Student")){
                activityStatusTitle.setVisibility(View.VISIBLE);
            }

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null) mClickListener.onItemClickActivityPost(v, getAdapterPosition());
        }

    }

    static ClassActivityPost getItem(int id) {
        return classActivityPostIArrayList.get(id);
    }


    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClickActivityPost(View view, int position);
    }

}
