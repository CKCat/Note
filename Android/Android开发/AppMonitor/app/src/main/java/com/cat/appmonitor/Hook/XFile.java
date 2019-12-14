package com.cat.appmonitor.Hook;

import com.cat.appmonitor.util.FileType;
import com.cat.appmonitor.util.FileTypeJudge;
import com.cat.appmonitor.util.Logger;
import com.cat.appmonitor.util.Stack;
import com.cat.appmonitor.util.Util;

import java.io.File;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XFile extends XHook{
    private static final String className = File.class.getName();
    private static XFile xFile;

    public static XFile getInstance() {
        if (xFile == null) {
            xFile = new XFile();
        }
        return xFile;
    }

    @Override
    void hook(final XC_LoadPackage.LoadPackageParam packageParam) {
        // file.delete()
        XposedHelpers.findAndHookMethod(className, packageParam.classLoader, "delete", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                File file = (File) param.thisObject;
                String callRef = Stack.getCallRef();


                Logger.log("[### File delete ###] ");
                Logger.log("[### File delete ###] fileName : " + file.getAbsolutePath() );

                Logger.logCallRef("[### File delete ###]");

                String time = Util.getSystemTime();
                StringBuffer logsb = new StringBuffer();
                logsb.append("time: " + time + '\n')
                        .append("[### function ###]: File delete\n")
                        .append("fileName: " + file.getAbsolutePath() + '\n')
                        .append("callRef: " + callRef + '\n');

                Util.writeLog(packageParam.packageName, logsb.toString());
                FileType fileType = FileTypeJudge.getType(file.getAbsolutePath());
                if (fileType == FileType.dex || fileType == FileType.zip ||fileType == FileType.png || fileType == FileType.jpg){
                    Util.writeFile(packageParam.packageName, file.getAbsolutePath());
                }


            }
        });

//    //private void open(String name)
//    XposedHelpers.findAndHookMethod(FileInputStream.class.getName(), packageParam.classLoader,
//        "open", String.class, new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                String name = (String) param.args[0];
//
//                Log.d(Global.TAG, "afterHookedMethod: open " + name);
//                File file = new File(name);
//                FileType fileType = FileTypeJudge.getType(file.getAbsolutePath());
//                if (fileType == FileType.dex || fileType==FileType.zip){
//                    Util.writeFile(packageParam.packageName, name);
//                }
//                //super.afterHookedMethod(param);
//            }
//        });
    }
}
