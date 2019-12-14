package com.cat.appmonitor.Hook;

import com.cat.appmonitor.util.Logger;
import com.cat.appmonitor.util.Stack;
import com.cat.appmonitor.util.Util;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XClass extends XHook {
    private static final String className = "java.lang.Class";
    private static XClass classLoadHook;

    public static XClass getInstance() {
        if (classLoadHook == null) {
            classLoadHook = new XClass();
        }
        return classLoadHook;
    }

    @Override
    void hook(final XC_LoadPackage.LoadPackageParam packageParam) {
        XposedHelpers.findAndHookMethod(className, packageParam.classLoader, "forName",
                String.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        String name = (String) param.args[0];
                        Logger.log("[--- Class forName ---] " + name);

                        String callRef = Stack.getCallRef();
                        Logger.log("[--- Class forName callRef ---] " + callRef);

                        //写入log文件
                        String time = Util.getSystemTime();
                        StringBuffer logsb = new StringBuffer();
                        logsb.append("time: " + time + '\n')
                                .append("[### function ###]: forName\n")
                                .append("forName: " + name + '\n')
                                .append("callRef: " + callRef + '\n');

                        Util.writeLog(packageParam.packageName, logsb.toString());

                    }
                });

        XposedHelpers.findAndHookMethod(className, packageParam.classLoader, "getMethod",
                String.class, Class[].class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        String name = (String) param.args[0];
                        Logger.log("[--- Class getMethod ---] " + name);

//                        Object obj = param.args[1];
//                        if (obj instanceof Class) {
//                            Logger.log("[--- Class getMethod ---] " + ((Class) obj).getName());
//                        } else if (obj instanceof Class[] && ((Class[])obj).length > 0) {
//                            Logger.log("[--- Class getMethod ---] " + ((Class[])obj)[0].getName());
//                        }
                        String callRef = Stack.getCallRef();
                        Logger.log("[--- Class getMethod callRef ---] " + callRef);

                        String time = Util.getSystemTime();
                        StringBuffer logsb = new StringBuffer();
                        logsb.append("time: " + time + '\n')
                                .append("[### function ###]: getMethod\n")
                                .append("getMethod: " + name + '\n')
                                .append("callRef: " + callRef + '\n');

                        Util.writeLog(packageParam.packageName, logsb.toString());

                    }
                });

        XposedHelpers.findAndHookMethod(className, packageParam.classLoader, "getDeclaredMethod",
                String.class, Class[].class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        String name = (String) param.args[0];
                        Logger.log("[--- Class getDeclaredMethod ---] " + name);

//                        Object obj = param.args[1];
//                        if (obj instanceof Class) {
//                            Logger.log("[--- Class getDeclaredMethod ---] " + ((Class) obj).getName());
//                        } else if (obj instanceof Class[] && ((Class[])obj).length > 0) {
//                            Logger.log("[--- Class getDeclaredMethod ---] " + ((Class[])obj)[0].getName());
//                        }
                        String callRef = Stack.getCallRef();


                        Logger.log("[--- Class getDeclaredMethod callRef ---] " + callRef);

                        String time = Util.getSystemTime();
                        StringBuffer logsb = new StringBuffer();
                        logsb.append("time: " + time + '\n')
                                .append("[### function ###]: getDeclaredMethod\n")
                                .append("getMethod: " + name + '\n')
                                .append("callRef: " + callRef + '\n');

                        Util.writeLog(packageParam.packageName, logsb.toString());
                    }
                });
    }

}
