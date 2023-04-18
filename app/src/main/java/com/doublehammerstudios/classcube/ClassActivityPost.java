package com.doublehammerstudios.classcube;

import java.io.Serializable;
import java.util.ArrayList;

public class ClassActivityPost implements Serializable {

    private final String classActivityPostTitle;
    private final String classActivityPostSubject;
    private final String classActivityPostDueDate;
    private final String classActivityPostStatus;
    private final String classActivityPostSubmissionBinLink;

    private ArrayList<String> studentWhoFinishedClassActivityPost = new ArrayList<String>();
    public ClassActivityPost(String classActivityPostTitle,
                             String classActivityPostSubject,
                             String classActivityPostDueDate,
                             String classActivityPostStatus,
                             String classActivityPostSubmissionBinLink,
                             ArrayList<String> studentWhoFinishedClassActivityPost)
    {

        this.classActivityPostTitle = classActivityPostTitle;
        this.classActivityPostSubject = classActivityPostSubject;
        this.classActivityPostDueDate = classActivityPostDueDate;
        this.classActivityPostStatus = classActivityPostStatus;
        this.classActivityPostSubmissionBinLink = classActivityPostSubmissionBinLink;
        this.studentWhoFinishedClassActivityPost = studentWhoFinishedClassActivityPost;

    }


    public String getClassActivityPostTitle() {
        return classActivityPostTitle;
    }

    public String getClassActivityPostSubject() {
        return classActivityPostSubject;
    }

    public String getClassActivityPostDueDate() {
        return classActivityPostDueDate;
    }

    public String getClassActivityPostStatus() {
        return classActivityPostStatus;
    }

    public String getClassActivityPostSubmissionBinLink() {
        return classActivityPostSubmissionBinLink;
    }

    public ArrayList<String> getStudentWhoFinishedClassActivityPost() {
        return studentWhoFinishedClassActivityPost;
    }
}
