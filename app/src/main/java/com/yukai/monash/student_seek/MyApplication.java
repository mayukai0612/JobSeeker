package com.yukai.monash.student_seek;

import android.app.Application;

public class MyApplication extends Application {

    @Override
    public void onCreate() {

        try {
            Class.forName("android.os.AsyncTask");
        } catch (ClassNotFoundException e) {

        }

        super.onCreate();
    }
}
