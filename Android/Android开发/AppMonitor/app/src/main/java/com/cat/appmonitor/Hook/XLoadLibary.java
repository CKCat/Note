package com.cat.appmonitor.Hook;

import com.cat.appmonitor.util.Logger;
import com.cat.appmonitor.util.Stack;
import com.cat.appmonitor.util.Util;

import java.io.File;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XLoadLibary extends XHook {

    public String targetCls = "";
    public String targerMethod = "";

    public static XLoadLibary xLoadLibary = null;

    public static XLoadLibary getInstance() {
        if (xLoadLibary == null) {
            xLoadLibary = new XLoadLibary();
        }
        return xLoadLibary;
    }
    @Override
    void hook(final XC_LoadPackage.LoadPackageParam packageParam) {

        XposedHelpers.findAndHookMethod(Runtime.class, "doLoad", String.class, ClassLoader.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                String soName = (String) param.args[0];

                String time = Util.getSystemTime();
                String callRef = Stack.getCallRef();
                Logger.log("[### doLoad ###] soName : " + soName);
                Logger.logCallRef("[### doLoad ###]");

                StringBuffer logsb = new StringBuffer();
                logsb.append("time: " + time + '\n')
                        .append("[### function ###]: doLoad\n")
                        .append("soName: " + soName + '\n')
                        .append("callRef: " + callRef + '\n');
                Util.writeLog(packageParam.packageName, logsb.toString());

                if (new File(soName).exists()){
                    Util.writeFile(packageParam.packageName, soName);
                }else{
                }


            }
        });

//        /**
//         * Hook LoadClass 进而hook其它dex中的方法
//         */
//        XposedHelpers.findAndHookMethod(ClassLoader.class, "loadClass",
//                String.class, new XC_MethodHook() {
//
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                super.afterHookedMethod(param);
//                if (param.hasThrowable()){
//                    return;
//                }
//
//                Class<?> cls = (Class<?>) param.getResult();
//                String name = cls.getName();
//                if (targetCls.equals(name)){
//                    XposedHelpers.findAndHookMethod(cls, targerMethod, String.class, new XC_MethodHook() {
//                        @Override
//                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                            super.beforeHookedMethod(param);
//                        }
//
//                        @Override
//                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                            super.afterHookedMethod(param);
//
//                        }
//                    });
//                }
//            }
//        });
    }
}
