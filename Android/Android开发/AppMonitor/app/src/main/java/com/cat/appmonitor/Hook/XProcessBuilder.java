package com.cat.appmonitor.Hook;

import com.cat.appmonitor.util.Logger;
import com.cat.appmonitor.util.Stack;
import com.cat.appmonitor.util.Util;

import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XProcessBuilder extends XHook {

    private static final String className = "java.lang.ProcessBuilder";
    private static XProcessBuilder classLoadHook;

    public static XProcessBuilder getInstance() {
        if (classLoadHook == null) {
            classLoadHook = new XProcessBuilder();
        }
        return classLoadHook;
    }

    @Override
    void hook(final XC_LoadPackage.LoadPackageParam packageParam) {
        XposedHelpers.findAndHookMethod(className, packageParam.classLoader, "start",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        String time = Util.getSystemTime();

                        ProcessBuilder pb = (ProcessBuilder) param.thisObject;
                        List<String> cmds = pb.command();
                        StringBuilder cmdSb = new StringBuilder();
                        cmdSb.append(cmdSb.append("CMD:"));
                        for (int i = 0; i < cmds.size(); i++) {
                            cmdSb.append(cmds.get(i) + " ");
                        }

                        String callRef = Stack.getCallRef();
                        Logger.log("[=== ProcessBuilder start ===] ");
                        Logger.log("[=== ProcessBuilder start ===] " + cmdSb);
                        Logger.log("[=== ProcessBuilder start ===] " + callRef);

                        StringBuffer logsb = new StringBuffer();
                        logsb.append("time:" + time + '\n')
                                .append("[### function ###]:ProcessBuilder.start\n")
                                .append("cmd: " + cmdSb.toString() + '\n');

                        Util.writeLog(packageParam.packageName, logsb.toString());
                    }
                });
    }

}
