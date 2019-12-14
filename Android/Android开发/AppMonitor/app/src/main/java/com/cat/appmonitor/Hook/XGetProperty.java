package com.cat.appmonitor.Hook;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XGetProperty extends XHook {

    public static XGetProperty xGetProperty = null;

    public static XGetProperty getInstance() {
        if (xGetProperty == null) {
            xGetProperty = new XGetProperty();
        }
        return xGetProperty;
    }

    @Override
    void hook(final XC_LoadPackage.LoadPackageParam packageParam) {
        XposedHelpers.findAndHookMethod(System.class.getName(), packageParam.classLoader,"getProperty",
                String.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        String arg = (String) param.args[0];
                        if (arg.equals("http.proxyHost")){
                            param.setResult("");
                        }else if (arg.equals("http.proxyPort")){
                            param.setResult("-1");
                        }
                    }
                });
    }
}
