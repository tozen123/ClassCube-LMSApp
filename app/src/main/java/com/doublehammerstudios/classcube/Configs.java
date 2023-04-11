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

}
