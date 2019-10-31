package com.cat.appmonitor.Hook;

import com.cat.appmonitor.util.Logger;
import com.cat.appmonitor.util.Stack;
import com.cat.appmonitor.util.Util;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XActivityThread extends XHook {

    private static final String className = "android.app.ActivityThread";
    private static XActivityThread classLoadHook;

    public static XActivityThread getInstance() {
        if (classLoadHook == null) {
            classLoadHook = new XActivityThread();
        }
        return classLoadHook;
    }

    @Override
    void hook(final XC_LoadPackage.LoadPackageParam packageParam) {
        try {
            Class<?> receiverDataClass = Class.forName("android.app.ActivityThread$ReceiverData");

            if (receiverDataClass != null) {
                XposedHelpers.findAndHookMethod(className, packageParam.classLoader,
                        "handleReceiver", receiverDataClass, new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) {
                                String time = Util.getSystemTime();
                                String callRef = Stack.getCallRef();
                                String revName = param.args[0].getClass().getName();

                                Logger.log("[=== ActivityThread$ReceiverData handleReceiver ===]");
                                Logger.log("[=== ActivityThread$ReceiverData handleReceiver ===] Receiver Name : " + revName);
                                Logger.log("[=== ActivityThread$ReceiverData handleReceiver ===] " + callRef);

                                StringBuffer logsb = new StringBuffer();
                                logsb.append("time:" + time + '\n')
                                        .append("[### function ###]:handleReceiver\n")
                                        .append("CallRef:" + callRef + '\n')
                                        .append("The Receiver Information:" + revName + '\n');
                                Util.writeLog(packageParam.packageName, logsb.toString());
                            }
                        });
            }
        } catch (ClassNotFoundException e) {
            System.out.println("class not found!!!");
        }
    }

}
