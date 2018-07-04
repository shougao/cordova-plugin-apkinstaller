package com.example.template;

import android.app.Application;

import com.mycompany.installer.CrashCatchHandler;

public class MyApplication extends Application {
    public static final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        CrashCatchHandler.getInstance().init(getApplicationContext());
    }
}
