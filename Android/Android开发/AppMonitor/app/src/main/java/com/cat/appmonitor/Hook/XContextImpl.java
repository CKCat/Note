package com.cat.appmonitor.Hook;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;

import com.cat.appmonitor.util.Logger;
import com.cat.appmonitor.util.Stack;
import com.cat.appmonitor.util.Util;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XContextImpl extends XHook {

    private static final String className = "android.app.ContextImpl";
    private static List<String> logList = null;
    private static XContextImpl classLoadHook;

    public static XContextImpl getInstance() {
        if (classLoadHook == null) {
            classLoadHook = new XContextImpl();
        }
        return classLoadHook;
    }

    @Override
    void hook(final XC_LoadPackage.LoadPackageParam packageParam) {
        logList = new ArrayList<String>();
        XposedHelpers.findAndHookMethod(className, packageParam.classLoader, "registerReceiver",
                BroadcastReceiver.class, IntentFilter.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        String time = Util.getSystemTime();
                        String receiverName = param.args[0].getClass().toString();
                        String callRef = Stack.getCallRef();

                        Logger.log("[=== Register Receiver ===]");
                        Logger.log("[=== Register Receiver ===] " + receiverName);
                        Logger.log("[=== Register Receiver ===] " + callRef);

                        StringBuffer logsb = new StringBuffer();
                        logsb.append("time: " + time + '\n')
                                .append("[### function ###]: registerReceiver\n")
                                .append("Receiver Name:" + receiverName + '\n')
                                .append("Call Ref : " + callRef + '\n');

                        Util.writeLog(packageParam.packageName, logsb.toString());
                    }
                });
    }

}
