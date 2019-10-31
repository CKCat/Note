package com.cat.appmonitor.Hook;

import com.cat.appmonitor.util.Logger;
import com.cat.appmonitor.util.Stack;
import com.cat.appmonitor.util.Util;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XBroadcastReceiver extends XHook {
	private static final String className = "android.content.BroadcastReceiver";
	private static List<String> logList = null;
	private static XBroadcastReceiver xBroadcastReceiver;

	public static XBroadcastReceiver getInstance() {
		if (xBroadcastReceiver == null) {
			xBroadcastReceiver = new XBroadcastReceiver();
		}
		return xBroadcastReceiver;
	}

	@Override
	void hook(final XC_LoadPackage.LoadPackageParam packageParam) {
		logList = new ArrayList<String>();
		XposedHelpers.findAndHookMethod(className, packageParam.classLoader,
				"abortBroadcast", new XC_MethodHook() {
					@Override
					protected void afterHookedMethod(MethodHookParam param) {
						String time = Util.getSystemTime();
						String callRef = Stack.getCallRef();
						Logger.log("[*** abort Broadcast ***]");
						Logger.log("[*** abort Broadcast ***] " + callRef);

						StringBuffer logsb = new StringBuffer();
						logsb.append("time: " + time + "\n")
								.append("[### function ###]: abortBroadcast\n")
								.append("callRef: " + callRef + "\n");
						Util.writeLog(packageParam.packageName,logsb.toString());
					}
				});
	}
}
