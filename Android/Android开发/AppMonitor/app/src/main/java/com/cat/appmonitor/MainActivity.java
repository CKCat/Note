package com.cat.appmonitor;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cat.appmonitor.UI.AppInfo;
import com.cat.appmonitor.UI.PackageInfoAdapter;
import com.cat.appmonitor.util.FileUtils;
import com.cat.appmonitor.util.Global;
import com.cat.appmonitor.util.PermissionUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * https://github.com/FunnyParty/AppMonitor
 */
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {


        private SharedPreferences pkgsPref;
        private List<String> selectedApp;
        private List<AppInfo> appInfoList;
        private boolean[] isSeleted;
        private TextView tv_selectapps;
        private String selectapps = "";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.process_list);

            PermissionUtil.addPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            PermissionUtil.addPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            PermissionUtil.checkPermission(this,100);

            Button save = (Button) findViewById(R.id.saveButton);
            Button exit = (Button) findViewById(R.id.exitButton);
            tv_selectapps = findViewById(R.id.select_pkg);
            ListView packageList = (ListView) findViewById(R.id.packageList);
            appInfoList = new ArrayList<AppInfo>();
            selectedApp = new ArrayList<String>();
            pkgsPref = this.getSharedPreferences("pkgs", Context.MODE_WORLD_READABLE);
            //setWorldReadable();

            getPkgList();
            loadInit();


            FileUtils.createOrExistsDir(Global.COMM_DIR);

            PackageInfoAdapter packgaeAdapter = new PackageInfoAdapter(this, appInfoList, isSeleted);
            packageList.setAdapter(packgaeAdapter);
            packageList.setOnItemClickListener(this);
            packageList.setAlwaysDrawnWithCacheEnabled(true);

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    whichSelect(appInfoList, isSeleted);
                    saveData(selectedApp);
                    tv_selectapps.setText(selectapps);
                    Toast.makeText(MainActivity.this.getApplicationContext(), "monitor begin", Toast.LENGTH_SHORT).show();
                }
            });

            exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

        }



    private void saveData(List<String> selectedApp)
        {
            try
            {
                SharedPreferences.Editor edit = pkgsPref.edit();
                edit.clear();
                edit.putStringSet("pkgs", new HashSet<String>(selectedApp));
                edit.apply();
                selectapps = "";
                for (String app:selectedApp){
                    selectapps += app;
                    selectapps += "\n";
                }
            } catch (Throwable e)
            {
                System.out.println(e.getMessage());
            }
        }

        //初始化
        public void loadInit() {
            isSeleted = new boolean[appInfoList.size()];
            Set<String> pkgs = pkgsPref.getStringSet("pkgs", null);

            selectapps = "";
            if (pkgs != null){
                for (AppInfo appinfo : appInfoList) {
                    if (pkgs.contains(appinfo.getPkgName())) {
                        int i = appInfoList.indexOf(appinfo);
                        isSeleted[i] = true;
                        selectapps += appinfo.getPkgName();
                        selectapps += "\n";
                    }
                }
            }
        }

        //获取所有的APP包名信息
        public void getPkgList() {
            PackageManager packManager = this.getPackageManager();
            List<PackageInfo> packageInfoList = packManager.getInstalledPackages(0);

            for (int i = 0; i < packageInfoList.size(); i++) {
                AppInfo appInfo = new AppInfo();
                PackageInfo packageInfo = packageInfoList.get(i);
                appInfo.setAppIcon(packManager
                        .getApplicationIcon(packageInfo.applicationInfo));
                appInfo.setAppLabel(packManager.getApplicationLabel(
                        packageInfo.applicationInfo).toString());
                appInfo.setPkgName(packageInfo.applicationInfo.packageName);

                appInfoList.add(appInfo);
            }
        }

        //将选中的APP保存恰里
        public void whichSelect(List<AppInfo> appInfo, boolean[] selected) {
            selectedApp.clear();
            for (int i = 0; i < selected.length; i++) {
                if (selected[i]) {
                    selectedApp.add(appInfo.get(i).getPkgName());
                }
            }
        }

        //将选中的app设置为以选中
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            String pkgName = appInfoList.get(arg2).getPkgName();
            Log.d(Global.TAG, "onItemClick: " + pkgName);
            RelativeLayout lr = (RelativeLayout) arg1;
            CheckBox tmp = (CheckBox) lr.getChildAt(3);
            tmp.toggle();
            isSeleted[arg2] = tmp.isChecked();
        }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.handlePermissionResult(this,requestCode,permissions,grantResults);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        selectedApp.clear();
        saveData(selectedApp);
        for (int i = 0; i< isSeleted.length; i++){
            isSeleted[i] = false;
        }
        return true;
        //return super.onOptionsItemSelected(item);
    }
//    private void setWorldReadable() {
//        File dataDir = new File(getApplicationInfo().dataDir);
//        File prefsDir = new File(dataDir, "shared_prefs");
//        File prefsFile = new File(prefsDir + File.separator + "pkgs.xml");
//        if (!prefsFile.exists())
//            return ;
//        CommandResult result = Shell.SU.run("id");
//        if (result.isSuccessful()) {
//            String cmd = "chmod 777 " + prefsFile;
//            Shell.SU.run(cmd);
//        }
//    }
}
