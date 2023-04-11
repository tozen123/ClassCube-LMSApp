package com.doublehammerstudios.classcube;

import android.view.View;

import com.google.type.DateTime;

import java.io.Serializable;

public class ClassPost implements Serializable {
    private final String classPostTitle;
    private final String classPostSubject;
    private final String classPostDuedate;

    public ClassPost(String classPostTitle, String classPostSubject, String classPostDuedate) {
        this.classPostTitle = classPostTitle;
        this.classPostSubject = classPostSubject;
        this.classPostDuedate = classPostDuedate;


    }

    public String getClassPostTitle() {
        return classPostTitle;
    }

    public String getClassPostSubject() {
        return classPostSubject;
    }

    public String getClassPostDuedate() {
        return classPostDuedate;
    }
}

