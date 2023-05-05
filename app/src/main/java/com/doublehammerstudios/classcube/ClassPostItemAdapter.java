package com.doublehammerstudios.classcube;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ClassPostItemAdapter extends RecyclerView.Adapter<ClassPostItemAdapter.ViewHolder>{
    private static ArrayList<ClassPost> classPostArrayList;
    private Context context;
    private ItemClickListener mClickListener;
    public ClassPostItemAdapter(ArrayList<ClassPost> classPostArrayList, Context context) {
        this.classPostArrayList = classPostArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ClassPostItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.class_post_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ClassPostItemAdapter.ViewHolder holder, int position) {
        ClassPost classpost = classPostArrayList.get(position);
        holder.postTitle.setText(classpost.getClassPostTitle());
        holder.postSubject.setText(classpost.getClassPostSubject());
        holder.postDueDate.setText(classpost.getClassPostDuedate());


        holder.postCompletionStatus.setText(classpost.getPostStatus());

    }

    public void updateList (ArrayList<ClassPost> items) {
        if (items != null && items.size() > 0) {
            classPostArrayList.clear();
            classPostArrayList.addAll(items);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return classPostArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // creating variables for our text views.
        private final TextView postTitle;
        private final TextView postSubject;
        private final TextView postDueDate;
        private final TextView postCompletionStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            postTitle = itemView.findViewById(R.id.id_textview_classPostTitle);
            postSubject = itemView.findViewById(R.id.id_textview_classPostSubject);
            postDueDate = itemView.findViewById(R.id.id_textview_classPostDuedate);
            postCompletionStatus = itemView.findViewById(R.id.id_textview_classPostStatus);

            if(Configs.userType.equals("Teacher/Instructor/Professor")){
                postCompletionStatus.setVisibility(View.INVISIBLE);
            }else if(Configs.userType.equals("Student")){
                postCompletionStatus.setVisibility(View.VISIBLE);
            }

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClickClassPost(view, getAdapterPosition());
        }

    }
    // convenience method for getting data at click position
    static ClassPost getItem(int id) {
        return classPostArrayList.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClickClassPost(View view, int position);
    }

}
