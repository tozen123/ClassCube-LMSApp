package com.doublehammerstudios.classcube;


public class Configs {
    private static Configs mInstance= null;

    public static String userName;
    public static String userType;

    protected Configs(){}

    public static synchronized Configs getInstance() {
        if(null == mInstance){
            mInstance = new Configs();
        }
        return mInstance;
    }

    /*
            Summary
            Global function to handle the current user type and returns true if the user type is student and false for teacher
     */
    public static boolean checkUserIfStudent(){
        if(userType.equals("Student")){
            return true;
        } else if(userType.equals("Teacher/Instructor/Professor")){
            return false;
        }
        return false;
    }
}
