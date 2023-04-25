package com.doublehammerstudios.classcube;

import java.io.Serializable;
import java.util.ArrayList;

/*
    The whole quiz itself
 */

public class QuizQuestion implements Serializable {

    private String classQuizPostTitle;
    private String classQuizPostSubject;
    private String classQuizPostDueDate;
    private String classQuizPostStatus;
    public ArrayList<QuizQuestionItem> QuizItem;
    private ArrayList<String> studentWhoFinishedClassQuizPost = new ArrayList<String>();

    public QuizQuestion(String classQuizPostTitle, String classQuizPostSubject, String classQuizPostDueDate, String classQuizPostStatus, ArrayList<QuizQuestionItem> QuizItem, ArrayList<String> studentWhoFinishedClassQuizPost){
        this.classQuizPostTitle = classQuizPostTitle;
        this.classQuizPostSubject = classQuizPostSubject;
        this.classQuizPostDueDate = classQuizPostDueDate;
        this.classQuizPostStatus = classQuizPostStatus;
        this.QuizItem = QuizItem;
        this.studentWhoFinishedClassQuizPost = studentWhoFinishedClassQuizPost;
    }

    public ArrayList<QuizQuestionItem> getQuizItem(){
        return QuizItem;
    }

    public String getClassQuizPostStatus() {
        return classQuizPostStatus;
    }

    public String getClassQuizPostDueDate() {
        return classQuizPostDueDate;
    }

    public String getClassQuizPostSubject() {
        return classQuizPostSubject;
    }

    public String getClassQuizPostTitle() {
        return classQuizPostTitle;
    }

    public ArrayList<String> getStudentWhoFinishedClassQuizPost() {
        return studentWhoFinishedClassQuizPost;
    }

    public void setStudentWhoFinishedClassQuizPost(ArrayList<String> studentWhoFinishedClassQuizPost) {
        this.studentWhoFinishedClassQuizPost = studentWhoFinishedClassQuizPost;
    }
}

