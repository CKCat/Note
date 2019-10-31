package com.cat.appmonitor.Hook;

import android.app.Dialog;
import android.view.View;

import com.cat.appmonitor.util.Logger;
import com.cat.appmonitor.util.Stack;
import com.cat.appmonitor.util.Util;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XDialog extends XHook {
    private static final String className = Dialog.class.getName();
    private static XDialog xDialog;

    public static XDialog getInstance() {
        if (xDialog == null) {
            xDialog = new XDialog();
        }
        return xDialog;
    }

    @Override
    void hook(final XC_LoadPackage.LoadPackageParam packageParam) {
        XposedHelpers.findAndHookMethod(className, packageParam.classLoader,
                "setContentView", View.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        String time = Util.getSystemTime();
                        String callRef = Stack.getCallRef();

                        Logger.log("[--- DIALOG ---]");
                        Logger.log("[--- DIALOG ---]" + callRef);

                        StringBuffer logsb = new StringBuffer();
                        logsb.append("time: " + time + '\n')
                                .append("[### function ###]: POP DIALOG\n")
                                .append("StackTrace: " + callRef + '\n');

                        Util.writeLog(packageParam.packageName, logsb.toString());
                    }
                });
    }

}
