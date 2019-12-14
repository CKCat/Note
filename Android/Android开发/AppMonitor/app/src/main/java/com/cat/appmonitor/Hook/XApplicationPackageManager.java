package com.cat.appmonitor.Hook;

import android.content.ComponentName;
import android.content.pm.PackageManager;

import com.cat.appmonitor.util.Logger;
import com.cat.appmonitor.util.Stack;
import com.cat.appmonitor.util.Util;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class XApplicationPackageManager extends XHook {

    private static final String className = "android.app.ApplicationPackageManager";
    private static List<String> logList = null;
    private static XApplicationPackageManager classLoadHook;

    public static XApplicationPackageManager getInstance() {
        if (classLoadHook == null) {
            classLoadHook = new XApplicationPackageManager();
        }
        return classLoadHook;
    }

    @Override
    void hook(final XC_LoadPackage.LoadPackageParam packageParam) {
        logList = new ArrayList<String>();

        XposedHelpers.findAndHookMethod(className, packageParam.classLoader,
                "setComponentEnabledSetting", ComponentName.class, int.class,
                int.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        StringBuffer logsb = new StringBuffer();
                        int state = (Integer) param.args[1];
                        if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
                            String time = Util.getSystemTime();
                            String callRef = Stack.getCallRef();
                            logsb.append("time:" + time)
                                    .append("[### function ###]:setComponentEnabledSetting\n")
                                    .append("action:hide Icon\n")
                                    .append("CallRef:" + callRef + '\n');

                            Logger.log("[*** Hide Icon ***]");
                            Logger.log("[*** Hide Icon ***] " + callRef);
                        }
                        Util.writeLog(packageParam.packageName, logsb.toString());
                    }
                });


        XposedHelpers.findAndHookMethod(className, packageParam.classLoader,
                "getInstalledPackages", Integer.TYPE, Integer.TYPE, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        String callRef = Stack.getCallRef();
                        Logger.log("[=== getInstalledPackages ===] " + callRef);
                        //写入log文件
                        String time = Util.getSystemTime();
                        StringBuffer logsb = new StringBuffer();
                        logsb.append("time: " + time + '\n')
                                .append("[### function ###]: setPrimaryClip\n")
                                .append("callRef: " + callRef + '\n');

                        Util.writeLog(packageParam.packageName, logsb.toString());
                        // TODO modify result
//                        Object obj = param.getResult();
//                        if (obj == null) {
//                            Logger.log("[=== getInstalledPackages ===] " + "NULL");
//                        } else if (obj instanceof List) {
//                            List<PackageInfo> list = (List<PackageInfo>) obj;
//                            List<PackageInfo> tmp = (List<PackageInfo>) obj;
//                            for (PackageInfo info : list) {
//                                Logger.log("[*** Test PackageInfo ***] " + info.packageName);
//                                if (info.packageName.contains("xposed")
//                                        || info.packageName.contains("acgmohu")) {
//                                    tmp.remove(info);
//                                }
//                            }
//                            param.setResult(tmp);
//                        } else {
//                            List<PackageInfo> list = new ArrayList<PackageInfo>();
//                            PackageInfo info = new PackageInfo();
//                            info.packageName = "com.game.kill2kill";
//                            info.packageName = "";
//                            list.add(info);
//                            param.setResult(list);
//                        }

                    }
                });


        XposedHelpers.findAndHookMethod(className, packageParam.classLoader,
                "getInstalledApplications", Integer.TYPE, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        String callRef = Stack.getCallRef();
//                        Logger.log("[=== getInstalledApplications ===]");
                        Logger.log("[=== getInstalledApplications ===] " + callRef);
                        //写入log文件
                        String time = Util.getSystemTime();
                        StringBuffer logsb = new StringBuffer();
                        logsb.append("time: " + time + '\n')
                                .append("[### function ###]: setPrimaryClip\n")
                                .append("callRef: " + callRef + '\n');

                        Util.writeLog(packageParam.packageName, logsb.toString());
//                        Object obj = param.getResult();
//                        if (obj instanceof List) {
//                            List<ApplicationInfo> list = (List<ApplicationInfo>) obj;
//                            List<ApplicationInfo> tmp = (List<ApplicationInfo>) obj;
//
//                            for (ApplicationInfo info : list) {
//                                if (info.packageName.contains("xposed")
//                                        || info.packageName.contains("acgmohu")) {
//                                    tmp.remove(info);
//                                }
//                            }
//                            param.setResult(tmp);
//                        } else {
//                            List<ApplicationInfo> list = new ArrayList<ApplicationInfo>();
//                            ApplicationInfo info = new ApplicationInfo();
//                            info.processName = "com.game.kill2kill";
//
//                            list.add(info);
//                            param.setResult(list);
//                        }
                    }
                });
    }
}
