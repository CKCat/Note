package com.cat.appmonitor.Hook;

import android.net.wifi.WifiManager;

import com.cat.appmonitor.util.Logger;
import com.cat.appmonitor.util.Stack;
import com.cat.appmonitor.util.Util;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class XWifiManager extends XHook {

    private static final String className = WifiManager.class.getName();
    private static XWifiManager xWifiManager;

    public static XWifiManager getInstance() {
        if (xWifiManager == null) {
            xWifiManager = new XWifiManager();
        }
        return xWifiManager;
    }

    @Override
    void hook(final XC_LoadPackage.LoadPackageParam packageParam) {
        XposedHelpers.findAndHookMethod(className, packageParam.classLoader, "setWifiEnabled",
                Boolean.TYPE, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        String callRef = Stack.getCallRef();
                        boolean flag = (Boolean)param.args[0];
                        if (flag) {
                            Logger.log("[### setWifiEnabled Enable ###]");
                            Logger.log("[### setWifiEnabled Enable ###] " + callRef);
                        } else {
                            Logger.log("[### setWifiEnabled Disable ###]");
                            Logger.log("[### setWifiEnabled Disable ###] " + callRef);
                        }
                        String time = Util.getSystemTime();
                        StringBuffer logsb = new StringBuffer();
                        logsb.append("time: " + time + '\n')
                                .append("[### function ###]: setWifiEnabled " + flag + "\n")
                                .append("callRef: " + callRef + '\n');

                        Util.writeLog(packageParam.packageName, logsb.toString());
                    }
                });
    }

}
