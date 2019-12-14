package com.test.layout;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //注册上下文菜单
        ListView listView = findViewById(R.id.listView1);
        this.registerForContextMenu(listView);
        //显示自定义视图
        //setContenViewByCustom();
        //setContenViewByCustomView();


    }


    void setContenViewByCustom(){
        //不使用资源创建一个视图
        RelativeLayout relativeLayout = new RelativeLayout(this);
        //加入TextView控件
        TextView textView = new TextView(this);
        textView.setText(this.getString(R.string.HelloWorld));
        textView.setTextSize(40);
        textView.setWidth(800);
        textView.setHeight(1000);
        ActionBar.LayoutParams lpText = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams lpRelative= new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        relativeLayout.setLayoutParams(lpRelative);
        relativeLayout.addView(textView, lpText);
        setContentView(relativeLayout);
    }

    void setContenViewByCustomView(){
		MyView my_View = new MyView(this);
		this.setContentView(my_View);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add("item1");
        menu.add("item2");

        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId())
        {
            case 0:
                Toast.makeText(this, "menu1", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(LayoutInflater.from(this).inflate(R.layout.activity_main, null));
                AlertDialog dlg = builder.create();
                dlg.show();

                break;
            case 2:
                new AlertDialog.Builder(this)
                .setMessage("Save")
                .setPositiveButton("OK", null)
                .setNeutralButton("Abort", null)
                .setNegativeButton("Cancel", null)
                .create()
                .show();

                break;
            case 3:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setMessage("save as");
                builder1.setPositiveButton("OK", null);
                builder1.setNeutralButton("About", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("MainActivity", "onClick: " + which);

                    }
                });
                builder1.setNegativeButton("Cancel", null);
                builder1.create().show();

                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "New");
        menu.add(0, 1, 1, "Open" );
        SubMenu submenu = menu.addSubMenu(0, 2, 2,"Save >");
        submenu.add(0, 3, 3,"save as");
        return super.onCreateOptionsMenu(menu);
    }
}
