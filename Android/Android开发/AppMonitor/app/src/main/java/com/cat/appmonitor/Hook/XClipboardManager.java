package com.cat.appmonitor.Hook;

import android.content.ClipData;

import com.cat.appmonitor.util.Logger;
import com.cat.appmonitor.util.Stack;
import com.cat.appmonitor.util.Util;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XClipboardManager extends XHook {

    private static final String className = "android.content.ClipboardManager";
    private static XClipboardManager xClipboardManager = null;

    public static XClipboardManager getInstance() {
        if (xClipboardManager == null) {
            xClipboardManager = new XClipboardManager();
        }
        return xClipboardManager;
    }
    @Override
    void hook(final XC_LoadPackage.LoadPackageParam packageParam) {

        //setPrimaryClip(ClipData clip)
        XposedHelpers.findAndHookMethod(className, packageParam.classLoader,
            "setPrimaryClip", ClipData.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                    ClipData clipData = (ClipData)param.args[0];
                    String text = clipData.getItemAt(0).getText().toString();


                    //打印log信息
                    Logger.log("[### setPrimaryClip ###] ");
                    Logger.log("[### setPrimaryClip ###] text : " + text);
                    Logger.logCallRef("[### setPrimaryClip ###]");

                    //写入log文件
                    String callRef = Stack.getCallRef();
                    String time = Util.getSystemTime();
                    StringBuffer logsb = new StringBuffer();
                    logsb.append("time: " + time + '\n')
                            .append("[### function ###]: setPrimaryClip\n")
                            .append("text: " + text + '\n')
                            .append("callRef: " + callRef + '\n');

                    Util.writeLog(packageParam.packageName, logsb.toString());
                }
            });

        //getPrimaryClip()
        XposedHelpers.findAndHookMethod(className, packageParam.classLoader,
                "getPrimaryClip", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                        ClipData clipData = (ClipData)param.getResult();
                        String text = clipData.getItemAt(0).getText().toString();


                        //打印log信息
                        Logger.log("[### getPrimaryClip ###] ");
                        Logger.log("[### getPrimaryClip ###] text : " + text);
                        Logger.logCallRef("[### getPrimaryClip ###]");

                        //写入log文件
                        String callRef = Stack.getCallRef();
                        String time = Util.getSystemTime();
                        StringBuffer logsb = new StringBuffer();
                        logsb.append("time: " + time + '\n')
                                .append("[### function ###]: getPrimaryClip\n")
                                .append("text: " + text + '\n')
                                .append("callRef: " + callRef + '\n');

                        Util.writeLog(packageParam.packageName, logsb.toString());
                    }
                });

    }
}
