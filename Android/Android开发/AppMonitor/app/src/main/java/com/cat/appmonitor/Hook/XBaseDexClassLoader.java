package com.cat.appmonitor.Hook;

import com.cat.appmonitor.util.Logger;
import com.cat.appmonitor.util.Stack;
import com.cat.appmonitor.util.Util;

import java.io.File;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XBaseDexClassLoader extends XHook {
    private static final String className = "dalvik.system.BaseDexClassLoader";
    private static XBaseDexClassLoader classLoadHook;

    public static XBaseDexClassLoader getInstance() {
        if (classLoadHook == null) {
            classLoadHook = new XBaseDexClassLoader();
        }
        return classLoadHook;
    }

    // public BaseDexClassLoader(String	dexPath,File optimizedDirectory, String	libraryPath, ClassLoader parent)
    // libcore/dalvik/src/main/java/dalvik/system/BaseDexClassLoader.java
    // http://developer.android.com/reference/dalvik/system/BaseDexClassLoader.html

    @Override
    void hook(final XC_LoadPackage.LoadPackageParam packageParam) {
        XposedHelpers.findAndHookConstructor(className, packageParam.classLoader,
                String.class, File.class, String.class,
                ClassLoader.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        String time = Util.getSystemTime();
                        String dexPath = (String) param.args[0];
                        File optimizedDir = (File) param.args[1];
                        String libPath = (String) param.args[2];
                        ClassLoader parent = (ClassLoader) param.args[3];
                        String callRef = Stack.getCallRef();


                        Logger.log("[### DexClassLoader ###] ");
                        Logger.log("[### DexClassLoader ###] dexPath : " + dexPath);
                        Logger.log("[### DexClassLoader ###] optimizedDir : " + optimizedDir);
                        Logger.log("[### DexClassLoader ###] libPath : " + libPath);
                        Logger.log("[### DexClassLoader ###] parent : " + parent);

                        Logger.logCallRef("[### DexClassLoader ###]");

                        StringBuffer logsb = new StringBuffer();
                        logsb.append("time: " + time + '\n')
                                .append("[### function ###]: DexClassLoader\n")
                                .append("dexPath: " + dexPath + '\n')
                                .append("optimizedDir: " + optimizedDir + '\n')
                                .append("libPath: " + libPath + '\n')
                                .append("parent: " + parent + '\n')
                                .append("callRef: " + callRef + '\n');

                        Util.writeLog(packageParam.packageName, logsb.toString());
                        if (new File(dexPath).exists()){
                            Util.writeFile(packageParam.packageName, dexPath);
                        }

                    }
                });

        /**
         *     public DexPathList(ClassLoader definingContext, String dexPath, String libraryPath, File optimizedDirectory)
         */
        XposedHelpers.findAndHookConstructor("dalvik.system.DexPathList", packageParam.classLoader,
                ClassLoader.class, String.class, String.class,
                File.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        String time = Util.getSystemTime();
                        ClassLoader parent = (ClassLoader) param.args[0];
                        String dexPath = (String) param.args[1];
                        String libPath = (String) param.args[2];
                        File optimizedDir = (File) param.args[3];
                        String callRef = Stack.getCallRef();
                        Logger.log("[### DexPathList ###] ");
                        Logger.log("[### DexPathList ###] dexPath : " + dexPath);
                        Logger.log("[### DexPathList ###] optimizedDir : " + optimizedDir);
                        Logger.log("[### DexPathList ###] libPath : " + libPath);
                        Logger.log("[### DexPathList ###] parent : " + parent);

                        Logger.logCallRef("[### DexClassLoader ###]");

                        StringBuffer logsb = new StringBuffer();
                        logsb.append("time: " + time + '\n')
                                .append("[### function ###]: DexPathList\n")
                                .append("dexPath: " + dexPath + '\n')
                                .append("optimizedDir: " + optimizedDir + '\n')
                                .append("libPath: " + libPath + '\n')
                                .append("parent: " + parent + '\n')
                                .append("callRef: " + callRef + '\n');

                        Util.writeLog(packageParam.packageName, logsb.toString());
                        if (new File(dexPath).exists()){
                            Util.writeFile(packageParam.packageName, dexPath);
                        }
                    }
                });

        /**
         * private static int openDexFile(String sourceName, String outputName, int flags)
         */
        XposedHelpers.findAndHookMethod("dalvik.system.DexFile", packageParam.classLoader, "openDexFile",
                String.class, String.class, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                String sourceName = (String) param.args[0];
                String outputName = (String) param.args[1];

                Logger.log("[### openDexFile ###] ");
                Logger.log("[### openDexFile ###] dexPath : " + sourceName);
                Logger.log("[### openDexFile ###] optimizedDir : " + outputName);
                Logger.logCallRef("[### openDexFile ###]");

                String time = Util.getSystemTime();
                String callRef = Stack.getCallRef();
                StringBuffer logsb = new StringBuffer();
                logsb.append("time: " + time + '\n')
                        .append("[### function ###]: DexPathList\n")
                        .append("dexPath: " + sourceName + '\n')
                        .append("optimizedDir: " + outputName + '\n')
                        .append("callRef: " + callRef + '\n');

                Util.writeLog(packageParam.packageName, logsb.toString());
                if (new File(sourceName).exists()){
                    Util.writeFile(packageParam.packageName, sourceName);
                }


//                String outDir = baseDir;
//                String dexPath = (String) param.args[0];

                //Ignore loading of files from /system, comment this out if you wish
//                if (dexPath.startsWith("/system/"))
//                    return;
//
//                XposedBridge.log("Hooking dalvik.system.DexFile for " + packageName);
//                String uniq = UUID.randomUUID().toString();
//                outDir = outDir + "/" + packageName  + dexPath.replace("/", "_") + "-" + uniq;
//
//                XposedBridge.log("Capturing " + dexPath);
//                XposedBridge.log("Writing to " + outDir);
//
//                InputStream in = new FileInputStream(dexPath);
//                OutputStream out = new FileOutputStream(outDir);
//                byte[] buf = new byte[1024];
//                int len;
//                while ((len = in.read(buf)) > 0) {
//                    out.write(buf, 0, len);
//                }
//                in.close();
//                out.close();
            }

        });

    }

}
