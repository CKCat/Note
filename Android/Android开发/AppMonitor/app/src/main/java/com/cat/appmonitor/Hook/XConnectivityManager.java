package com.cat.appmonitor.Hook;

import android.net.ConnectivityManager;

import com.cat.appmonitor.util.Logger;
import com.cat.appmonitor.util.Stack;
import com.cat.appmonitor.util.Util;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XConnectivityManager extends XHook {

    private static final String className = ConnectivityManager.class.getName();
    private static XConnectivityManager xConnectivityManager;

    public static XConnectivityManager getInstance() {
        if (xConnectivityManager == null) {
            xConnectivityManager = new XConnectivityManager();
        }
        return xConnectivityManager;
    }

    @Override
    void hook(final XC_LoadPackage.LoadPackageParam packageParam) {
        XposedHelpers.findAndHookMethod(className, packageParam.classLoader, "setMobileDataEnabled",
                Boolean.TYPE, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        String callRef = Stack.getCallRef();
                        boolean flag = (Boolean)param.args[0];
                        if (flag) {
                            Logger.log("[### Enable Mobile Data ###]");
                            Logger.log("[### Enable Mobile Data] " + callRef);
                        } else {
                            Logger.log("[### Disable Mobile Data ###]");
                            Logger.log("[### Disable Mobile Data ###] " + callRef);
                        }
                        String time = Util.getSystemTime();
                        StringBuffer logsb = new StringBuffer();
                        logsb.append("time: " + time + '\n')
                                .append("[### function ###]: setMobileDataEnabled\n")
                                .append("flag: " + flag + '\n')
                                .append("callRef: " + callRef + '\n');

                        Util.writeLog(packageParam.packageName, logsb.toString());
                    }
                });
    }

}
