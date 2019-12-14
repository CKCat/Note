package com.cat.appmonitor.Hook;

import android.app.Notification;
import android.app.NotificationManager;

import com.cat.appmonitor.util.Logger;
import com.cat.appmonitor.util.Stack;
import com.cat.appmonitor.util.Util;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
public class XNotificationManager extends XHook {

    private static final String className = NotificationManager.class.getName();
    private static XNotificationManager classLoadHook;

    public static XNotificationManager getInstance() {
        if (classLoadHook == null) {
            classLoadHook = new XNotificationManager();
        }
        return classLoadHook;
    }

    @Override
    void hook(final XC_LoadPackage.LoadPackageParam packageParam) {
        XposedHelpers.findAndHookMethod(className, packageParam.classLoader, "notify",
                int.class, Notification.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        String time = Util.getSystemTime();
                        String notificationName = param.args[1].toString();
                        String callRef = Stack.getCallRef();

                        Logger.log("[### Notification ###]");
                        Logger.log("[### Notification ###] " + notificationName);
                        Logger.log("[### Notification ###] " + callRef);

                        StringBuffer logsb = new StringBuffer();
                        logsb.append("time:" + time + '\n')
                                .append("[### function ###]: notify AD\n")
                                .append("Notification:" + notificationName + '\n')
                                .append("callRef: " + callRef + '\n');

                        Util.writeLog(packageParam.packageName, logsb.toString());
                    }
                });
    }

}
