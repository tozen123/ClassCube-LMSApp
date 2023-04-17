package com.doublehammerstudios.classcube;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;

public class ClassPost implements Serializable {
    private final String classPostTitle;
    private final String classPostSubject;
    private final String classPostDuedate;
    private final String postStatus;
    private final String classFileUrl;

    private ArrayList<String> studentWhoFinishedClassPost = new ArrayList<String>();

    public ClassPost(String classPostTitle, String classPostSubject, String classPostDuedate, String classFileUrl, String postStatus, ArrayList<String> studentWhoFinishedClassPost) {
        this.classPostTitle = classPostTitle;
        this.classPostSubject = classPostSubject;
        this.classPostDuedate = classPostDuedate;
        this.classFileUrl = classFileUrl;
        this.postStatus = postStatus;
        this.studentWhoFinishedClassPost = studentWhoFinishedClassPost;

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
        if(classFileUrl != null){
            return classFileUrl;
        }
        return null;
    }

    public String getPostStatus(){
        return postStatus;
    }
    public ArrayList<String> getstudentWhoFinishedClassPost(){
        return studentWhoFinishedClassPost;
    }
}

