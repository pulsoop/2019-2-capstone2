package com.example.expcheck_camera;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class DataModel {
    public String title;
    public String date;

    public DataModel(){
        //Defrault Constructor
    }
    public DataModel(String title, String date){
        this.title = title;
        this.date = date;
    }
}
