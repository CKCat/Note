package com.cat.appmonitor;

import java.util.HashSet;
import java.util.Set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class NewInstallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sp = context.getSharedPreferences("pkgs", Context.MODE_WORLD_READABLE);
        Set<String> pkgSet = sp.getStringSet("pkgs", null);

        //接收广播：设备上新安装了一个应用程序包。
        if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
            String packageName = intent.getDataString().substring(8);
            if (packageName.equals(context.getPackageName())) {
                return;
            }

            HashSet<String> hashSet = new HashSet<String>();
            hashSet.add(packageName);

            if (pkgSet == null) {
                pkgSet = hashSet;
            } else {
                pkgSet.addAll(hashSet);
            }

            SharedPreferences.Editor editor = sp.edit();
            editor.clear();
            editor.putStringSet("pkgs", pkgSet);
            editor.apply();
        }

        //接收广播：设备上删除了一个应用程序包。
        if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
            if (pkgSet == null) {
                return;
            }
            String packageName = intent.getDataString().substring(8);
            pkgSet.remove(packageName);
            SharedPreferences.Editor editor = sp.edit();
            editor.clear();
            editor.putStringSet("pkgs", pkgSet);
            editor.apply();
        }
    }
}
