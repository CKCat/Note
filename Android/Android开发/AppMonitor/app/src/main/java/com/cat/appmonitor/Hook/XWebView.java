package com.cat.appmonitor.Hook;

import com.cat.appmonitor.util.Logger;
import com.cat.appmonitor.util.Stack;
import com.cat.appmonitor.util.Util;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class XWebView extends XHook {

    private static final String className = "android.webkit.WebView";
    private static XWebView xWebView;

    public static XWebView getInstance() {
        if (xWebView == null) {
            xWebView = new XWebView();
        }
        return xWebView;
    }

    @Override
    void hook(final XC_LoadPackage.LoadPackageParam packageParam) {

        XposedHelpers.findAndHookMethod(className, packageParam.classLoader,
                "loadUrl", String.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        String time = Util.getSystemTime();
                        String url = param.args[0].toString();
                        String callRef = Stack.getCallRef();

                        Logger.log("[--- Webview loadUrl ---] ");
                        Logger.log("[--- Webview loadUrl ---] " + url);
                        Logger.log("[--- Webview loadUrl ---] " + callRef);

                        StringBuffer logsb = new StringBuffer();
                        logsb.append("time: " + time + '\n')
                                .append("[### function ###]: Webview.loadUrl\n")
                                .append("url : " + url + '\n')
                                .append("callRef: " + callRef + '\n');

                        Util.writeLog(packageParam.packageName, logsb.toString());
                    }
                });


    }

}
