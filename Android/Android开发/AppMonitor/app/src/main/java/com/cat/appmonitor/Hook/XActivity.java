package com.cat.appmonitor.Hook;

import android.app.ActivityManager;
import android.app.AndroidAppHelper;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.cat.appmonitor.util.Logger;
import com.cat.appmonitor.util.Stack;
import com.cat.appmonitor.util.Util;

import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XActivity extends XHook {

    private static final String className = "android.app.Activity";
    private static XActivity xActivity;

    public static XActivity getInstance() {
        if (xActivity == null) {
            xActivity = new XActivity();
        }
        return xActivity;
    }

    public static String getTopActivity(Context ctx) {
        String topActivityName = "";
        List<ActivityManager.RunningTaskInfo> taskList;

        ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) {
            return topActivityName;
        }

        int isOK = ctx.checkCallingPermission("android.permission.GET_TASKS");
        if (isOK == PackageManager.PERMISSION_DENIED) {
            return topActivityName;
        }

        taskList = activityManager.getRunningTasks(1);

        if (taskList != null) {
            Logger.log("[=== POP_AD ===] number : " + taskList.get(0).numActivities);
            ComponentName topActivity = taskList.get(0).topActivity;
            Logger.log("[=== POP_AD ===] name : " + topActivity.toString());

            if (!ctx.getApplicationInfo().packageName.equals(topActivity.getPackageName())) {
                Logger.log("[=== POP_AD ===] POP_AD from another Application.");
            }

            topActivityName = taskList.get(0).topActivity.toString();
        }

        return topActivityName;
    }

    @Override
    void hook(final XC_LoadPackage.LoadPackageParam packageParam) {

        XposedHelpers.findAndHookMethod(className, packageParam.classLoader,
                "onCreate", Bundle.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        String time = Util.getSystemTime();
                        String callRef = Stack.getCallRef();

                        StringBuffer logsb = new StringBuffer();
                        Context context = AndroidAppHelper.currentApplication();
                        if (context != null) {
                            HookApp.context = context;
                            String topActivityName = getTopActivity(context);

                            Logger.log("[=== Activity onCreate ===] ");
                            Logger.log("[=== Activity onCreate ===] " + topActivityName);
                            Logger.log("[=== Activity onCreate ===] " + callRef);

                            logsb.append("time:" + time + '\n');
                            logsb.append("[### function ###]:Activity onCreate\n");
                            logsb.append("top Activity:" + topActivityName + '\n');
                            logsb.append("callRef:" + callRef);

                            Util.writeLog(packageParam.packageName, logsb.toString());
                        } else {
                            Logger.log("[=== Activity onCreate ===] " + callRef);
                        }
                    }
                });
    }

}
