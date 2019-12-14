package com.cat.appmonitor.Hook;

import android.content.ContextWrapper;
import android.content.Intent;

import com.cat.appmonitor.util.Logger;
import com.cat.appmonitor.util.Stack;
import com.cat.appmonitor.util.Util;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XContext extends XHook {

    private static final String className = ContextWrapper.class.getName();
    private static XContext xContext;

    public static XContext getInstance() {
        if (xContext == null) {
            xContext = new XContext();
        }
        return xContext;
    }

    @Override
    void hook(final XC_LoadPackage.LoadPackageParam packageParam) {
        XposedHelpers.findAndHookMethod(className, packageParam.classLoader, "sendBroadcast",
                Intent.class, String.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        String time = Util.getSystemTime();
                        Intent intent = (Intent) param.args[0];
                        String receiverPermission = (String) param.args[1];
                        String callRef = Stack.getCallRef();

                        Logger.log("[=== sendBroadcast ===] " + intent.getAction());
                        Logger.log("[=== sendBroadcast ===] " + receiverPermission);
                        Logger.log("[=== sendBroadcast ===] " + callRef);

                        StringBuffer logsb = new StringBuffer();

                        logsb.append("time: " + time + '\n')
                                .append("[### function ###]: sendBroadcast\n")
                                .append("acton: " + intent.getAction() + '\n')
                                .append("receiver Permission: " + receiverPermission + '\n')
                                .append("callRef: " + callRef);

                        Util.writeLog(packageParam.packageName, logsb.toString());
                    }
                });
    }

}
