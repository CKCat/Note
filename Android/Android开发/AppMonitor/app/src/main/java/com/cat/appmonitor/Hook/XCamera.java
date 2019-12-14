package com.cat.appmonitor.Hook;

import android.graphics.Camera;
import com.cat.appmonitor.util.Logger;
import com.cat.appmonitor.util.Stack;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by CKCat on 2019/3/20.
 */

public class XCamera extends XHook {
    private static final String className = Camera.class.getName();
    private static XCamera classLoadHook;

    /*
    public final void takePicture (Camera.ShutterCallback shutter,
                Camera.PictureCallback raw,
                Camera.PictureCallback postview,
                Camera.PictureCallback jpeg)

    public final void takePicture (Camera.ShutterCallback shutter,
                Camera.PictureCallback raw,
                Camera.PictureCallback jpeg)

    public static Camera open ()
    public static Camera open (int cameraId)
     */
    public static XCamera getInstance() {
        if (classLoadHook == null) {
            classLoadHook = new XClass();
        }
        return classLoadHook;
    }
    @Override
    void hook(XC_LoadPackage.LoadPackageParam packageParam) {
        XposedHelpers.findAndHookMethod(className, packageParam.classLoader, "open",
                int.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        String name = (String) param.args[0];
                        Logger.log("[--- Class forName ---] " + name);

                        String callRef = Stack.getCallRef();
                        Logger.log("[--- Class forName callRef ---] " + callRef);

                        //写入log文件
                        String time = Util.getSystemTime();
                        StringBuffer logsb = new StringBuffer();
                        logsb.append("time: " + time + '\n')
                                .append("[### function ###]: forName\n")
                                .append("forName: " + name + '\n')
                                .append("callRef: " + callRef + '\n');

                        Util.writeLog(packageParam.packageName, logsb.toString());

                    }
                });
    }
}
