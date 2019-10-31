package com.cat.appmonitor.Hook;

import android.content.res.AssetManager;

import com.cat.appmonitor.util.Logger;
import com.cat.appmonitor.util.Stack;
import com.cat.appmonitor.util.Util;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XAssetManager extends XHook {
    private static final String className = AssetManager.class.getName();
    private static XAssetManager xAssetManager;

    public static XAssetManager getInstance() {
        if (xAssetManager == null) {
            xAssetManager = new XAssetManager();
        }
        return xAssetManager;
    }

    @Override
    void hook(final XC_LoadPackage.LoadPackageParam packageParam) {
        XposedHelpers.findAndHookMethod(className, packageParam.classLoader, "open",
                String.class, Integer.TYPE, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        String time = Util.getSystemTime();
                        String fileName = (String) param.args[0];
                        String callRef = Stack.getCallRef();

                        StringBuffer logsb = new StringBuffer();
                        logsb.append("time:" + time + '\n')
                                .append("[### function ###]:AssetManager open\n")
                                .append("file name:" + fileName + '\n')
                                .append("callRef:" + callRef + '\n');


                        Logger.log("[=== AssetManager open ===]");
                        Logger.log("[=== AssetManager open ===] " + fileName);
                        Logger.log("[=== AssetManager open ===] " + callRef);

                        Util.writeLog(packageParam.packageName, logsb.toString());
                    }

                });

    }

}
