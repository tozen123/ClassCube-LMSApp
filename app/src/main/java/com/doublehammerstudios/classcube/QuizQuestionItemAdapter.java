package com.doublehammerstudios.classcube;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class QuizQuestionItemAdapter extends RecyclerView.Adapter<QuizQuestionItemAdapter.ViewHolder>{

    private static ArrayList<QuizQuestionItem> quizQuestionItemArrayList;
    private Context context;

    public QuizQuestionItemAdapter(ArrayList<QuizQuestionItem> quizQuestionItemArrayList, Context context){
        this.quizQuestionItemArrayList = quizQuestionItemArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public QuizQuestionItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new QuizQuestionItemAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.question_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull QuizQuestionItemAdapter.ViewHolder holder, int position) {
        QuizQuestionItem quizQuestionItem = quizQuestionItemArrayList.get(position);
        holder.quizCount.setText(quizQuestionItem.getQuizQuestionCount());
        holder.quizDesc.setText(quizQuestionItem.getQuizQuestionDesc());
        holder.quizChoiceA.setText(quizQuestionItem.getQuizQuestionChoiceA());
        holder.quizChoiceB.setText(quizQuestionItem.getQuizQuestionChoiceB());
        holder.quizChoiceC.setText(quizQuestionItem.getQuizQuestionChoiceC());
        holder.quizChoiceD.setText(quizQuestionItem.getQuizQuestionChoiceD());

    }

    @Override
    public int getItemCount() {
        return quizQuestionItemArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView quizCount;
        private TextView quizDesc;
        private RadioButton quizChoiceA;
        private RadioButton quizChoiceB;
        private RadioButton quizChoiceC;
        private RadioButton quizChoiceD;
        private String quizChoiceAnswer;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            quizCount = itemView.findViewById(R.id.vTxt_questionItemNumber);
            quizDesc = itemView.findViewById(R.id.vTxt_questionItemDesc);
            quizChoiceA = itemView.findViewById(R.id.radioButtonA);
            quizChoiceB = itemView.findViewById(R.id.radioButtonB);
            quizChoiceC = itemView.findViewById(R.id.radioButtonC);
            quizChoiceD = itemView.findViewById(R.id.radioButtonD);

        }
    }
}
