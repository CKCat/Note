package com.example.javahook;

import android.app.Application;
import android.content.Context;

public class MyApp extends Application {
    static {
        System.loadLibrary("javahook");
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}
