package com.cat.appmonitor.Hook;

import android.content.Context;
import android.util.Log;

import com.cat.appmonitor.util.FileUtils;
import com.cat.appmonitor.util.Global;

import java.io.File;
import java.util.Set;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class HookApp implements IXposedHookLoadPackage {
    public Set appList;
    public static Context context;

    private Set<String> getHookPkg(){
        XSharedPreferences pkgsPref = new XSharedPreferences("com.cat.appmonitor", "pkgs");
        return pkgsPref.getStringSet("pkgs", null);
    }

    @Override
    public void handleLoadPackage(LoadPackageParam loadPackageParam) {

        if (loadPackageParam.appInfo == null){
            Log.d(Global.TAG, "handleLoadPackage: loadPackageParam.appInfo == null ");
        }
        appList = new XSharedPreferences("com.cat.appmonitor", "pkgs").getStringSet("pkgs", null);
        if (appList == null){
            Log.d(Global.TAG, "handleLoadPackage: appList == null");
        }
        if (!appList.contains(loadPackageParam.packageName)) {
            //Log.d(Global.TAG, "handleLoadPackage: appList " + appList.toString() + " 不包含 " + loadPackageParam.packageName);
            return;
        }
//        if (!loadPackageParam.packageName.equals("me.ele"))
//            return;

        Log.d(Global.TAG, "handleLoadPackage : " + loadPackageParam.packageName);
        XposedBridge.log(loadPackageParam.packageName);
        //根据包名创建对应的目录
        String packageDir = Global.COMM_DIR + File.separator + loadPackageParam.packageName;
        FileUtils.createOrExistsDir(packageDir);

        //创建log文件
        String logfile = packageDir + File.separator + Global.LOG_FILE;
        FileUtils.createOrExistsFile(logfile);

        hook(XLoadLibary.getInstance(), loadPackageParam);
        hook(XBaseDexClassLoader.getInstance(), loadPackageParam);
        hook(XFile.getInstance(), loadPackageParam);
        hook(XGetProperty.getInstance(), loadPackageParam);
        hook(XClipboardManager.getInstance(), loadPackageParam);
        hook(XViewGroup.getInstance(), loadPackageParam);
        hook(XAbstractHttpClient.getInstance(), loadPackageParam);
        hook(XProcessBuilder.getInstance(), loadPackageParam);
        hook(XRuntime.getInstance(), loadPackageParam);

        hook(XActivity.getInstance(), loadPackageParam);
        hook(XActivityManager.getInstance(), loadPackageParam);
        hook(XActivityThread.getInstance(), loadPackageParam);
        hook(XApplicationPackageManager.getInstance(), loadPackageParam);
        hook(XAssetManager.getInstance(), loadPackageParam);
        hook(XAudioRecord.getInstance(), loadPackageParam);
        hook(XBroadcastReceiver.getInstance(), loadPackageParam);
        hook(XClass.getInstance(), loadPackageParam);
        hook(XConnectivityManager.getInstance(), loadPackageParam);
        hook(XContext.getInstance(), loadPackageParam);
        hook(XContextImpl.getInstance(), loadPackageParam);
        hook(XContentResolver.getInstance(), loadPackageParam);
        hook(XDialog.getInstance(), loadPackageParam);
        hook(XMediaRecorder.getInstance(), loadPackageParam);
        hook(XNotificationManager.getInstance(), loadPackageParam);
        hook(XSmsManger.getInstance(), loadPackageParam);
        hook(XSmsMessage.getInstance(), loadPackageParam);
        hook(XString.getInstance(), loadPackageParam);
        hook(XTelephoneyManager.getInstance(), loadPackageParam);
        hook(XURL.getInstance(), loadPackageParam);
        hook(XWebView.getInstance(), loadPackageParam);
        hook(XWindowManageService.getInstance(), loadPackageParam);
        hook(XWifiManager.getInstance(), loadPackageParam);
        hook(XCryptor.getInstance(), loadPackageParam);
    }

    public void hook(XHook xhook, LoadPackageParam packageParam) {
        xhook.hook(packageParam);
    }
}