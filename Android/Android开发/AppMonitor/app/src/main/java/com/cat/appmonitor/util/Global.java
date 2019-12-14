package com.cat.appmonitor.util;

import android.os.Environment;

import java.io.File;

public class Global {
    public static final String TAG = "AppMonitor";
    public static final String COMM_DIR = Environment.getExternalStorageDirectory() + File.separator + "Appmonitor";
    public static final String LOG_FILE = "AppMonitor.log";
}
