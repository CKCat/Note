package com.cat.appmonitor.Hook;

import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.cat.appmonitor.util.Logger;
import com.cat.appmonitor.util.Stack;
import com.cat.appmonitor.util.Util;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XViewGroup extends XHook {
    private static final String className = ViewGroup.class.getName();
    private static XViewGroup xViewGroup;
    //android.view.WindowManagerImpl
    public static XViewGroup getInstance() {
        if (xViewGroup == null) {
            xViewGroup = new XViewGroup();
        }
        return xViewGroup;
    }

    @Override
    void hook(final XC_LoadPackage.LoadPackageParam packageParam) {
        XposedHelpers.findAndHookMethod(className, packageParam.classLoader, "addView",
                View.class, Integer.TYPE, ViewGroup.LayoutParams.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        View view = (View) param.args[0];
                        String viewName = view.getClass().getName();

                        // TODO maybe read the view api to a list could be better ...
                        if (viewName.startsWith("android.widget.") || viewName.startsWith("android.view.")
                                || viewName.startsWith("android.support.v7.widget.") || viewName.startsWith("android.support.v7.internal.widget.")
                                || viewName.startsWith("com.android.internal.widget.") || viewName.startsWith("com.android.internal.view")) {
                            return;
                        }

                        Logger.log("[=== ViewGroup addView ===] ");
                        Logger.log("[=== ViewGroup addView ===] " + viewName);
                        Logger.logCallRef("[=== ViewGroup addView ===]");
                        //??????log??????
                        String callRef = Stack.getCallRef();
                        String time = Util.getSystemTime();
                        StringBuffer logsb = new StringBuffer();
                        logsb.append("time: " + time + '\n')
                                .append("[### function ###]: ViewGroup addView\n")
                                .append("callRef: " + callRef + '\n');

                        Util.writeLog(packageParam.packageName, logsb.toString());

                    }

                });

                XposedHelpers.findAndHookMethod("android.view.WindowManagerImpl",packageParam.classLoader,
                        "addView", View.class, ViewGroup.LayoutParams.class,  new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

//                            WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
//                            wmParams.type = Build.VERSION.SDK_INT > 19 ? 2005 : 2002;  // ????????????????????????????????????????????????
//                        LayoutParams(int w, int h, int _type, int _flags, int _format)
//                        ???????????????
//                                Width = m_int/10
//                        Height = 50
//                        Type = 2003 FIRST_SYSTEM_WINDOW | TYPE_APPLICATION_STARTING
//                        Flags = 262168
//                        ???????????????????????????view?????????????????????????????????
//                                Format = -3
//                        ????????????????????????3?????????
//                        TRANSLUCENT(?????????) = -3
//                        TRANSPARENT(??????) = -2
//                        OPAQUE(?????????) = -1
//                        Alpha(0~1)(??????~?????????)


                        WindowManager.LayoutParams wmParams = (WindowManager.LayoutParams)param.args[1];
                        String tmp = "wmParams.flags = " + wmParams.flags +  ", wmParams.type = " + wmParams.type + ", wmParams.alpha = " + wmParams.alpha + ", wmParams.format = " + wmParams.format
                                + ", wmParams.width = " + wmParams.width + ", wmParams.height = " + wmParams.height;

                        if (wmParams.alpha == 0f)
                            wmParams.alpha = 0.1f;
                        if (wmParams.format == -2)
                            wmParams.format = -3;
                        param.args[1] = wmParams;

                        //??????log??????
                        Logger.log("[### WindowManager addView ###] " + tmp + "\n");
                        Logger.logCallRef("[### WindowManager addView ###]");

                        //??????log??????
                        String callRef = Stack.getCallRef();
                        String time = Util.getSystemTime();
                        StringBuffer logsb = new StringBuffer();
                        logsb.append("time: " + time + '\n')
                                .append("[### function ###]: WindowManager addView " + tmp+ "\n")
                                .append("callRef: " + callRef + '\n');

                        Util.writeLog(packageParam.packageName, logsb.toString());


                    }
                });

    }

}
