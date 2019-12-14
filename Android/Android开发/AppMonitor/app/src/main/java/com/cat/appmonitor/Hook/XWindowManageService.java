package com.cat.appmonitor.Hook;

import com.cat.appmonitor.util.Logger;
import com.cat.appmonitor.util.Stack;
import com.cat.appmonitor.util.Util;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XWindowManageService extends XHook {
    // TODO ClassNotFound
    private static final String className = "com.android.server.wm.WindowManageService";
    private static XWindowManageService xWindowManageService;

    public static XWindowManageService getInstance() {
        if (xWindowManageService == null) {
            xWindowManageService = new XWindowManageService();
        }
        return xWindowManageService;
    }

    @Override
    void hook(final XC_LoadPackage.LoadPackageParam packageParam) {
        XposedHelpers.findAndHookMethod(className, packageParam.classLoader, "startViewServer", Integer.TYPE,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        String time = Util.getSystemTime();
                        String port = (String) param.args[0];
                        String callRef = Stack.getCallRef();


                        Logger.log("[--- WindowManageService startViewServer ---]");
                        Logger.log("[--- WindowManageService startViewServer ---] " + port);
                        Logger.log("[--- WindowManageService startViewServer ---] " + callRef);

                        StringBuffer logsb = new StringBuffer();
                        logsb.append("time: " + time + '\n')
                                .append("[### function ###]: startViewServer\n")
                                .append("port before " + port + '\n')
                                .append("callRef:" + callRef);
                        param.setResult(false);
                        Util.writeLog(packageParam.packageName, logsb.toString());
                    }

                });

    }

}
