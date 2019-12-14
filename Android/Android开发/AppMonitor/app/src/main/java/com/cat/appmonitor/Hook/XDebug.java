package com.cat.appmonitor.Hook;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.util.Log;
import com.cat.appmonitor.util.Global;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * https://github.com/riusksk/BDOpener
 */
public class XDebug extends XHook{
    private static XDebug xDebug;

    public static XDebug getInstance() {
        if (xDebug == null) {
            xDebug = new XDebug();
        }
        return xDebug;
    }

    @Override
    void hook(final XC_LoadPackage.LoadPackageParam packageParam){
        XposedBridge.hookAllMethods(XposedHelpers.findClass("com.android.server.pm.PackageManagerService",
                packageParam.classLoader), "getPackageInfo", new XC_MethodHook() {
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                int v6 = 32768;
                Object packageInfo = param.getResult();
                if(packageInfo != null) {
                    ApplicationInfo applicationInfo = ((PackageInfo)packageInfo).applicationInfo;
                    int flags = applicationInfo.flags;
                    Log.i(Global.TAG, "Load App : " + applicationInfo.packageName);
                    Log.i(Global.TAG, "==== After Hook ====");
                    if((flags & v6) == 0) {
                        flags |= v6;
                    }

                    if((flags & 2) == 0) {
                        flags |= 2;
                    }

                    applicationInfo.flags = flags;
                    param.setResult(packageInfo);
                    Log.i(Global.TAG, "flags = " + flags);
                    isDebugable(applicationInfo);
                    isBackup(applicationInfo);
                }
            }
        });
    }

    public static boolean isBackup(ApplicationInfo info) {
        try {
            if((info.flags & 32768) == 0) {
                Log.i(Global.TAG, "Close Backup");
                return false;
            }

            Log.i(Global.TAG, "Open Backup");
            return true;
        }
        catch(Exception v0) {
        }

        return false;
    }

    public static boolean isDebugable(ApplicationInfo info) {
        try {
            if((info.flags & 2) == 0) {
                Log.i("BDOpener", "Close Debugable");
                return false;
            }

            Log.i("BDOpener", "Open Debugable");
            return true;
        }
        catch(Exception v0) {
        }
        return false;
    }
}
