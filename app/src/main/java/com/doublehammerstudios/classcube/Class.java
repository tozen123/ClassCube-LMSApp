package com.doublehammerstudios.classcube;

import java.io.Serializable;
import java.util.ArrayList;

public class Class implements Serializable {
    String className;
    String classCode;
    String classSubject;
    String classTeacherID;
    String classTeacherName;
    ArrayList<String> classStudents = new ArrayList<String>();

    public Class() {
        // empty constructor
        // required for Firebase.
    }
    public Class(String className, String classCode, String classSubject, String classTeacherID, String classTeacherName, ArrayList<String> classStudents) {
        this.className = className;
        this.classCode = classCode;
        this.classSubject = classSubject;
        this.classTeacherID = classTeacherID;
        this.classStudents = classStudents;
        this.classTeacherName = classTeacherName;

    }

    public String getClassName(){
        return className;
    }

    public String getClassCode(){
        return classCode;
    }

    public String getClassSubject(){
        return classSubject;
    }

    public String getClassTeacherID(){
        return classTeacherID;
    }
    public String getClassTeacherName(){
        return classTeacherName;
    }
    public ArrayList<String> getClassStudents(){
        return classStudents;
    }

}
