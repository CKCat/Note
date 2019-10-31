package com.cat.appmonitor.Hook;

import android.app.ActivityManager;

import com.cat.appmonitor.util.Logger;
import com.cat.appmonitor.util.Stack;
import com.cat.appmonitor.util.Util;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XActivityManager extends XHook {

    private static final String className = ActivityManager.class.getName();
    private static XActivityManager classLoadHook;

    public static XActivityManager getInstance() {
        if (classLoadHook == null) {
            classLoadHook = new XActivityManager();
        }
        return classLoadHook;
    }

    @Override
    void hook(final XC_LoadPackage.LoadPackageParam packageParam) {

        XposedHelpers.findAndHookMethod(className, packageParam.classLoader,
                "killBackgroundProcesses", String.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        String time = Util.getSystemTime();
                        String callRef = Stack.getCallRef();
                        String processes = param.args[0].toString();

                        Logger.log("[### ActivityManager killBackgroundProcesses ###]");
                        Logger.log("[### ActivityManager killBackgroundProcesses ###] " + processes);
                        Logger.log("[### ActivityManager killBackgroundProcesses ###] " + callRef);

                        StringBuffer logsb = new StringBuffer();
                        logsb.append("time:" + time + '\n')
                                .append("[### function ###]:killBackgroundProcesses")
                                .append("CallRef:" + callRef)
                                .append("killed processes:" + processes);
                        Util.writeLog(packageParam.packageName, logsb.toString());
                    }
                });

        XposedHelpers.findAndHookMethod(className, packageParam.classLoader,
                "forceStopPackage", String.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        String time = Util.getSystemTime();
                        String callRef = Stack.getCallRef();
                        String pkgName = param.args[0].toString();

                        Logger.log("[### ActivityManager -> forceStopPackage ###]");
                        Logger.log("[### ActivityManager -> forceStopPackage ###] " + pkgName);
                        Logger.log("[### ActivityManager -> forceStopPackage ###] " + callRef);

                        StringBuffer logsb = new StringBuffer();
                        logsb.append("time:" + time + '\n')
                                .append("function:forceStopPackage\n")
                                .append("CallRef:" + callRef + '\n')
                                .append("stoped package:" + pkgName + '\n');
                        Util.writeLog(packageParam.packageName, logsb.toString());
                    }
                });
    }
}
