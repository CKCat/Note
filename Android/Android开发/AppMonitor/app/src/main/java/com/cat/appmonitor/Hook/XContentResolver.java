package com.cat.appmonitor.Hook;

import android.content.ContentValues;
import android.net.Uri;

import com.cat.appmonitor.util.Logger;
import com.cat.appmonitor.util.Stack;
import com.cat.appmonitor.util.Util;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XContentResolver extends XHook {

    private static final String className = "android.content.ContentResolver";
    private static final String[] privacyUris = {
            "content://com.android.contacts",
            "content://sms",
            "content://mms-sms",
            "content://contacts/",
            "content://com.android.contacts",
            "content://call_log",
            "content://telephony",
            "content://browser/bookmarks"};
    private static List<String> logList = null;

    private static XContentResolver xContentResolver;

    public static XContentResolver getInstance() {
        if (xContentResolver == null) {
            xContentResolver = new XContentResolver();
        }
        return xContentResolver;
    }

    private boolean isUriAvailable(String uri) {
        String url = uri.toLowerCase();
        for (int i = 0; i < privacyUris.length; i++) {
            if (url.contains(privacyUris[i])) {
                return true;
            }
        }
        return false;
    }

    @Override
    void hook(final XC_LoadPackage.LoadPackageParam packageParam) {
        logList = new ArrayList<String>();
        XposedHelpers.findAndHookMethod(className, packageParam.classLoader, "query",
                Uri.class, String[].class, String.class, String[].class,
                String.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        String uri = param.args[0].toString();
                        String selection = "";
                        if (param.args[2] != null) {
                            selection = (String) param.args[2];
                        }
                        String callRef = Stack.getCallRef();
                        String time = Util.getSystemTime();
                        StringBuffer logsb = new StringBuffer();

                        if (isUriAvailable(uri)) {

                            Logger.log("[### ContentResolver query ###]");
                            Logger.log("[### ContentResolver query ###] " + uri);

                            logsb.append("time: " + time + '\n')
                                    .append("[### function ###]: query\n")
                                    .append("Uri: " + uri + '\n');

                            if (!selection.equals("")){
                                Logger.log("[### ContentResolver query ###] " + selection);
                                logsb.append("selection:" + selection + '\n');
                            }

                            Logger.log("[### ContentResolver query ###] " + callRef);
                            logsb.append("callRef: " + callRef + '\n');

                            Util.writeLog(packageParam.packageName, logsb.toString());

                        } else {
                            Logger.log("[=== ContentResolver query ===]");
                            Logger.log("[=== ContentResolver query ===] " + uri);

                            logsb.append("time: " + time + '\n')
                                    .append("[### function ###]: query\n")
                                    .append("Uri: " + uri + '\n');

                            if (!selection.equals("")){
                                Logger.log("[=== ContentResolver query ===] " + selection);
                                logsb.append("selection:" + selection + '\n');
                            }

                            Logger.log("[=== ContentResolver query ===] " + callRef);
                            logsb.append("callRef: " + callRef + '\n');

                            Util.writeLog(packageParam.packageName, logsb.toString());
                        }


                    }
                });

        XposedHelpers.findAndHookMethod(className, packageParam.classLoader, "insert",
                Uri.class, ContentValues.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        String uri = (String) param.args[0];
                        String value = (String) param.args[1];
                        String callRef = Stack.getCallRef();
                        String time = Util.getSystemTime();

                        StringBuffer logsb = new StringBuffer();

                        if (isUriAvailable(uri)) {
                            Logger.log("[### ContentResolver query ###]");
                            Logger.log("[### ContentResolver query ###] " + uri);
                            Logger.log("[### ContentResolver query ###] " + value);
                            Logger.log("[### ContentResolver query ###] " + callRef);

                            logsb.append("time: " + time + '\n')
                                    .append("[### function ###]: insert\n")
                                    .append("Uri:" + uri + '\n')
                                    .append("value:" + value + '\n')
                                    .append("callRef: " + callRef + '\n');

                            Util.writeLog(packageParam.packageName, logsb.toString());

                        } else {
                            Logger.log("[=== ContentResolver query ===]");
                            Logger.log("[=== ContentResolver query ===] " + uri);
                            Logger.log("[=== ContentResolver query ===] " + value);
                            Logger.log("[=== ContentResolver query ===] " + callRef);

                            logsb.append("time: " + time + '\n')
                                    .append("[### function ###]: insert\n")
                                    .append("Uri:" + uri + '\n')
                                    .append("value:" + value + '\n')
                                    .append("callRef: " + callRef + '\n');

                            Util.writeLog(packageParam.packageName, logsb.toString());
                        }
                    }
                });

        XposedHelpers.findAndHookMethod(className, packageParam.classLoader, "delete",
                Uri.class, String.class, String[].class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        String uri = (String) param.args[0];
                        String where = (String) param.args[1];
                        String selection = (String) param.args[2];
                        String callRef = Stack.getCallRef();
                        String time = Util.getSystemTime();
                        StringBuffer logsb = new StringBuffer();

                        if (isUriAvailable(uri)) {
                            Logger.log("[### ContentResolver query ###]");
                            Logger.log("[### ContentResolver query ###] " + uri);
                            Logger.log("[### ContentResolver query ###] " + where);
                            Logger.log("[### ContentResolver query ###] " + selection);
                            Logger.log("[### ContentResolver query ###] " + callRef);

                            logsb.append("time: " + time + '\n')
                                    .append("[### function ###]: delete\n")
                                    .append("Uri: " + uri + '\n')
                                    .append("where: " + where + '\n')
                                    .append("selectionArgs: " + selection + '\n')
                                    .append("callRef: " + callRef + '\n');

                            Util.writeLog(packageParam.packageName, logsb.toString());
                        } else {
                            Logger.log("[=== ContentResolver query ===]");
                            Logger.log("[=== ContentResolver query ===] " + uri);
                            Logger.log("[=== ContentResolver query ===] " + where);
                            Logger.log("[=== ContentResolver query ===] " + selection);
                            Logger.log("[=== ContentResolver query ===] " + callRef);

                            logsb.append("time: " + time + '\n')
                                    .append("[### function ###]: delete\n")
                                    .append("Uri: " + uri + '\n')
                                    .append("where: " + where + '\n')
                                    .append("selectionArgs: " + selection + '\n')
                                    .append("callRef: " + callRef + '\n');

                            Util.writeLog(packageParam.packageName, logsb.toString());
                        }

                    }
                });
    }

}
