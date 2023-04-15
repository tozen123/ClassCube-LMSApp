package com.doublehammerstudios.classcube;

import android.net.Uri;

import java.io.Serializable;

public class ClassPost implements Serializable {
    private final String classPostTitle;
    private final String classPostSubject;
    private final String classPostDuedate;

    private final String classFileUrl;

    public ClassPost(String classPostTitle, String classPostSubject, String classPostDuedate, String classFileUrl) {
        this.classPostTitle = classPostTitle;
        this.classPostSubject = classPostSubject;
        this.classPostDuedate = classPostDuedate;
        this.classFileUrl = classFileUrl;

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

    public String getClassFileUrl(){
        return classFileUrl;
    }
}

