package com.test.FirstActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * java.lang.IllegalAccessException: access to class not allowed
 * 由于一开始没有写public导致包非法访问异常
 */
public class DisplayMessageActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.displaylayout);

        //获取MainActivity传递过来的附加数据
        Intent intent =getIntent();
        String msg = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        //显示到视图上
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(msg);

        LinearLayout layout = findViewById(R.id.content);
        layout.addView(textView);

    }
}
