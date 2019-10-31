package com.test.FirstActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {
    public final static String EXTRA_MESSAGE = "com.test.FirstActivity.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MainActivity", "onCreate()");

    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        Log.d("MainActivity", "onStart()");
        super.onStart();
    }
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        Log.d("MainActivity", "onPause()");
        super.onPause();
    }
    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        Log.d("MainActivity", "onRestart()");
        super.onRestart();
    }
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        Log.d("MainActivity", "onStop()");
        super.onStop();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        Log.d("MainActivity", "onResume()");
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        Log.d("MainActivity", "onDestroy()");
        super.onDestroy();
    }



    public void SendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = findViewById(R.id.et_msg);
        String msg = editText.getText().toString();
        //设置附加数据
        intent.putExtra(EXTRA_MESSAGE, msg);
        startActivity(intent);
    }
}
