package com.cat.appmonitor.util;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PermissionUtil {

    private static List<String> requestPermissions = new ArrayList<>();

    public static void addPermission(String permission){
        if (permission!=null&&!permission.equals("")&&!requestPermissions.contains(permission)){
            requestPermissions.add(permission);
        }
    }

    public static void addPermission(List<String> permissions){
        for(String s:permissions){
            addPermission(s);
        }
    }

    public static void clearPermissions(){
        requestPermissions.clear();
    }

    //权限申请
    public static void checkPermission(Activity context,int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissions = new ArrayList<>();
            for(String permission : requestPermissions){
                if(PackageManager.PERMISSION_GRANTED!=context.checkSelfPermission(permission)){
                    permissions.add(permission);
                }
            }
            if (permissions.size() != 0) {
                context.requestPermissions((String[]) permissions.toArray(new String[0]),requestCode);
            }
        }
    }

    //权限申请结果处理
    public static void handlePermissionResult(Activity context,int requestCode, String[] permissions, int[] grantResults){
        Log.i("PermissionUtil","handlePermissionResult requestCode:"+requestCode);
        if (grantResults.length>0){
            List<String> needs = new ArrayList<>();
            for (int i=0;i<grantResults.length;i++) {
                if (grantResults[i] < 0) {
                    needs.add(permissions[i]);
                }
            }
            if (needs.size()>0){
                Toast.makeText(context,"need permission:"+ Arrays.toString(needs.toArray()),Toast.LENGTH_SHORT).show();
            }
        }
    }
}

/*
* public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PermissionUtil.addPermission(Manifest.permission.INTERNET);
        PermissionUtil.addPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        PermissionUtil.checkPermission(this,100);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.handlePermissionResult(this,requestCode,permissions,grantResults);
    }
}
* */