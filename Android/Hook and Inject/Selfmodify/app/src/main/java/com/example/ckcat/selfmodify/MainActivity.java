package com.example.ckcat.selfmodify;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    
    static {
        System.loadLibrary("selfmodify");
        Log.d(TAG, "static initializer: " + selfmodify());
    }
    public static native int selfmodify();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TestAdd testadd = new TestAdd();
        System.out.println("1 multiply 2 equals : " + testadd.add(1, 2));
    }
}
