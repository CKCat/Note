package com.cat.appmonitor.Hook;

import com.cat.appmonitor.util.Logger;
import com.cat.appmonitor.util.Stack;
import com.cat.appmonitor.util.Util;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XString extends XHook {
	private static final String className = "java.lang.String";
	private static XString classLoadHook;


	public static XString getInstance() {
		if (classLoadHook == null) {
			classLoadHook = new XString();
		}
		return classLoadHook;
	}

	@Override
	void hook(final XC_LoadPackage.LoadPackageParam packageParam) {
		XposedHelpers.findAndHookConstructor(className, packageParam.classLoader,
				String.class, new XC_MethodHook() {

					@Override
					protected void afterHookedMethod(MethodHookParam param) {
						String str = param.args[0].toString();
						String callRef = Stack.getCallRef();

						Logger.log("[- String -] ");
						Logger.log("[- String -] " + str);
						Logger.log("[- String -] " + callRef);

                        String time = Util.getSystemTime();
                        StringBuffer logsb = new StringBuffer();
                        logsb.append("time: " + time + '\n')
                                .append("[### function ###]: String\n")
                                .append("text: " + str + '\n')
                                .append("callRef: " + callRef + '\n');

                        Util.writeLog(packageParam.packageName, logsb.toString());
					}

				});
//        XposedHelpers.findAndHookMethod(className, packageParam.classLoader, "split",
//                String.class, new XC_MethodHook() {
//
//                    @Override
//                    protected void afterHookedMethod(MethodHookParam param) {
//                        String org = (String)param.thisObject;
//                        String str = param.args[0].toString();
//                        String callRef = Stack.getCallRef();
//
//                        Logger.log("[- split regex -] " + str);
//                        Logger.log("[- split text -] " + org);
//                        Logger.log("[- split callRef -] " + callRef);
//
//                        String time = Util.getSystemTime();
//                        StringBuffer logsb = new StringBuffer();
//                        logsb.append("time: " + time + '\n')
//                                .append("[### function ###]: split\n")
//                                .append("regex: " + str + '\n')
//                                .append("text: " + org + '\n')
//                                .append("callRef: " + callRef + '\n');
//
//                        Util.writeLog(packageParam.packageName, logsb.toString());
//                    }
//
//                });
//
//        XposedHelpers.findAndHookMethod(className, packageParam.classLoader, "split",
//                String.class, int.class, new XC_MethodHook() {
//
//                    @Override
//                    protected void afterHookedMethod(MethodHookParam param) {
//                        String org = (String)param.thisObject;
//                        String str = param.args[0].toString();
//                        String callRef = Stack.getCallRef();
//                        Logger.log("[- split regex -] " + str);
//                        Logger.log("[- split text -] " + org);
//                        Logger.log("[- split callRef -] " + callRef);
//
//                        String time = Util.getSystemTime();
//                        StringBuffer logsb = new StringBuffer();
//                        logsb.append("time: " + time + '\n')
//                                .append("[### function ###]: split\n")
//                                .append("regex: " + str + '\n')
//                                .append("text: " + org + '\n')
//                                .append("callRef: " + callRef + '\n');
//
//                        Util.writeLog(packageParam.packageName, logsb.toString());
//                    }
//
//                });
//
//        XposedHelpers.findAndHookMethod(className, packageParam.classLoader, "replace",
//                char.class, char.class, new XC_MethodHook() {
//
//                    @Override
//                    protected void afterHookedMethod(MethodHookParam param) {
//                        String org = (String)param.thisObject;
//                        String str = param.args[0].toString();
//                        String str1 = param.args[1].toString();
//                        String callRef = Stack.getCallRef();
//                        Logger.log("[- replace param -] " + str  + " " + str1);
//                        Logger.log("[- replace text -] " + org);
//                        Logger.log("[- replace callRef -] " + callRef);
//
//                        String time = Util.getSystemTime();
//                        StringBuffer logsb = new StringBuffer();
//                        logsb.append("time: " + time + '\n')
//                                .append("[### function ###]: replace\n")
//                                .append("param: " +  str  + " " + str1 + '\n')
//                                .append("text: " + org + '\n')
//                                .append("callRef: " + callRef + '\n');
//
//                        Util.writeLog(packageParam.packageName, logsb.toString());
//                    }
//
//                });
//
//        XposedHelpers.findAndHookMethod(className, packageParam.classLoader, "replace",
//                CharSequence.class, CharSequence.class, new XC_MethodHook() {
//
//                    @Override
//                    protected void afterHookedMethod(MethodHookParam param) {
//                        String org = (String)param.thisObject;
//                        String str = param.args[0].toString();
//                        String str1 = param.args[1].toString();
//                        String callRef = Stack.getCallRef();
//                        Logger.log("[- replace param -] " + str  + " " + str1);
//                        Logger.log("[- replace text -] " + org);
//                        Logger.log("[- replace callRef -] " + callRef);
//
//                        String time = Util.getSystemTime();
//                        StringBuffer logsb = new StringBuffer();
//                        logsb.append("time: " + time + '\n')
//                                .append("[### function ###]: replace\n")
//                                .append("param: " +  str  + " " + str1 + '\n')
//                                .append("text: " + org + '\n')
//                                .append("callRef: " + callRef + '\n');
//
//                        Util.writeLog(packageParam.packageName, logsb.toString());
//                    }
//
//                });

//		XposedHelpers.findAndHookMethod("java.lang.StringBuilder", packageParam.classLoader, "toString", new XC_MethodHook() {
//			@Override
//			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//				String str = (String) param.getResult();
//				//String callRef = Stack.getCallRef();
//                //StringBuilder
//				Logger.log("[- StringBuilder toString -] ");
//				Logger.log(str);
//				//System.out.println( str);
////                if(str.startsWith("http:")){
////                    Logger.log("[- StringBuilder toString -] ");
////                    //new Exception().printStackTrace();
////                }
////
////				String time = Util.getSystemTime();
////				StringBuffer logsb = new StringBuffer();
////				logsb.append("time: " + time + '\n')
////						.append("[### function ###]: toString\n")
////						.append("text: " + str + '\n')
////						.append("callRef: " + callRef + '\n');
////
////				Util.writeLog(packageParam.packageName, logsb.toString());
//			}
//		});
    }



}
