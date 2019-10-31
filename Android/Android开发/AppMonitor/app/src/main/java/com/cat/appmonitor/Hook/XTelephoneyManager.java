package com.cat.appmonitor.Hook;

import com.cat.appmonitor.util.Logger;
import com.cat.appmonitor.util.Stack;
import com.cat.appmonitor.util.Util;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XTelephoneyManager extends XHook {
    private static final String className = "android.telephony.TelephonyManager";
    private static XTelephoneyManager xTelephoneyManager;

    public static XTelephoneyManager getInstance() {
        if (xTelephoneyManager == null) {
            xTelephoneyManager = new XTelephoneyManager();
        }
        return xTelephoneyManager;
    }

    @Override
    void hook(final XC_LoadPackage.LoadPackageParam packageParam) {
        XposedHelpers.findAndHookMethod(className, packageParam.classLoader, "getDeviceId",
                new XC_MethodHook() {

                    @Override
                    protected void beforeHookedMethod(MethodHookParam param){
//                        param.setResult("356357045618430");
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        Logger.log("[=== getDeviceId ===]");
                        Logger.log("[=== getDeviceId ===] " + Stack.getCallRef());
                        String callRef = Stack.getCallRef();
                        String time = Util.getSystemTime();
                        StringBuffer logsb = new StringBuffer();
                        logsb.append("time: " + time + '\n')
                                .append("[### function ###]: getDeviceId\n")
                                .append("callRef: " + callRef + '\n');

                        Util.writeLog(packageParam.packageName, logsb.toString());
                    }
                });

        XposedHelpers.findAndHookMethod(className, packageParam.classLoader,
                "getLine1Number", new XC_MethodHook() {

                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
//                        param.setResult("13826290651");
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        Logger.log("[=== getLine1Number ===]");
                        Logger.log("[=== getLine1Number ===] " + Stack.getCallRef());
                        String callRef = Stack.getCallRef();
                        String time = Util.getSystemTime();
                        StringBuffer logsb = new StringBuffer();
                        logsb.append("time: " + time + '\n')
                                .append("[### function ###]: getLine1Number\n")
                                .append("callRef: " + callRef + '\n');

                        Util.writeLog(packageParam.packageName, logsb.toString());
                    }

                });

        XposedHelpers.findAndHookMethod(className, packageParam.classLoader,
                "getSubscriberId", new XC_MethodHook() {

                    @Override
                    protected void beforeHookedMethod(MethodHookParam param){
//                        param.setResult("460006203280876");
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        Logger.log("[=== getSubscriberId ===]");
                        Logger.log("[=== getSubscriberId ===] " + Stack.getCallRef());
                        String callRef = Stack.getCallRef();
                        String time = Util.getSystemTime();
                        StringBuffer logsb = new StringBuffer();
                        logsb.append("time: " + time + '\n')
                                .append("[### function ###]: getSubscriberId \n")
                                .append("callRef: " + callRef + '\n');

                        Util.writeLog(packageParam.packageName, logsb.toString());
                    }

                });

        XposedHelpers.findAndHookMethod(className, packageParam.classLoader,
                "getNetworkOperatorName", new XC_MethodHook() {

                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        param.getResult();
                        param.setResult("46001");
                    }

                });
    }

}
