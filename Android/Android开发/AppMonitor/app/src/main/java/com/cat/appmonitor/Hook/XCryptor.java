package com.cat.appmonitor.Hook;


import android.support.annotation.NonNull;
import android.util.Log;

import com.cat.appmonitor.util.Global;
import com.cat.appmonitor.util.HexDumper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;

import javax.crypto.Cipher;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;



public class XCryptor extends XHook {

    private static XCryptor xCryptor;

    public static XCryptor getInstance() {
        if (xCryptor == null) {
            xCryptor = new XCryptor();
        }
        return xCryptor;
    }
    @Override
    void hook(final XC_LoadPackage.LoadPackageParam loadPackageParam) {
        try {

            XposedBridge.hookAllConstructors(XposedHelpers.findClass("javax.crypto.spec.DESKeySpec", loadPackageParam.classLoader), new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    String keystr;
                    byte[] keybyte = new byte[8];
                    int offset = 0;

                    // 拷贝数据
                    if(param.args.length != 1) //如果有两个参数的构造函数，第二个参数是偏移
                        offset = (int)param.args[1];
                    System.arraycopy((byte[])param.args[0], offset, keybyte, 0, 8);

                    keystr = "DES KEY";
                    Util.MyLog(loadPackageParam.packageName,keystr,keybyte);
                }
            });

            XposedBridge.hookAllConstructors(XposedHelpers.findClass("javax.crypto.spec.DESedeKeySpec", loadPackageParam.classLoader), new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    String keystr;
                    byte[] keybyte = new byte[24];
                    int offset = 0;

                    // 拷贝数据
                    if(param.args.length != 1) //如果有两个参数的构造函数，第二个参数是偏移
                        offset = (int)param.args[1];
                    System.arraycopy((byte[])param.args[0], offset, keybyte, 0, 24);

                    keystr = "3DES KEY";
                    Util.MyLog(loadPackageParam.packageName,keystr,keybyte);
                }
            });

            XposedBridge.hookAllConstructors(XposedHelpers.findClass("javax.crypto.spec.SecretKeySpec", loadPackageParam.classLoader), new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);

                    int offset = 0;
                    int size = 0;
                    String Algorithm;

                    if(param.args.length != 2)
                    {
                        offset = (int)param.args[1];
                        size = (int)param.args[2];
                        Algorithm = (String)param.args[3];
                    }else {
                        Algorithm = (String) param.args[1];
                        size = ((byte[])param.args[0]).length;
                    }

                    byte[] data = new byte[size];
                    System.arraycopy((byte[])param.args[0],offset,data,0,size);

                    String str ;
                    str = Algorithm + " Key";
                    Util.MyLog(loadPackageParam.packageName,str,data);
                }
            });

            // IV 向量
            XposedBridge.hookAllConstructors(XposedHelpers.findClass("javax.crypto.spec.IvParameterSpec", loadPackageParam.classLoader), new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    String keystr;
                    byte[] IVByte;
                    byte[] tmp;
                    int offset = 0;
                    int size;
                    tmp = (byte[])param.args[0];
                    size = tmp.length;
                    if(param.args.length != 1) //如果有两个参数的构造函数，第二个参数是偏移
                    {
                        offset = (int)param.args[1];
                        size = (int)param.args[2];
                    }
                    IVByte = new byte[size];
                    System.arraycopy(tmp,offset,IVByte,0,size);
                    keystr = "Iv";
                    Util.MyLog(loadPackageParam.packageName,keystr,IVByte);
                }
            });
            // XposedBridge.hookAllMethods(XposedHelpers.findClass("javax.crypto.Cipher",loadPackageParam.classLoader),"doFinal",new HookCipher(loadPackageParam.packageName,0));
            XposedBridge.hookAllMethods(XposedHelpers.findClass("javax.crypto.Cipher", loadPackageParam.classLoader),
                    "doFinal", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);

                            Cipher cip = (Cipher)param.thisObject;
                            if(param.args.length >= 1)
                            {
                                String str = cip.getAlgorithm() + " Data:";
                                Util.MyLog(loadPackageParam.packageName,str,(byte[])param.args[0]);

                                str = cip.getAlgorithm() + "  result:";
                                Util.MyLog(loadPackageParam.packageName,str,(byte[])param.getResult());
                            }
                        }
                    });

            XposedBridge.hookAllMethods(XposedHelpers.findClass("java.security.MessageDigest",loadPackageParam.classLoader), "update", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    MessageDigest md = (MessageDigest)param.thisObject;
                    String str = md.getAlgorithm() + " update data:";
                    Util.MyLog(loadPackageParam.packageName,str,(byte[])param.args[0]);

                }
            });

            XposedBridge.hookAllMethods(XposedHelpers.findClass("java.security.MessageDigest", loadPackageParam.classLoader), "digest", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    if (param.args.length >= 1)
                    {
                        MessageDigest md = (MessageDigest)param.thisObject;

                        String str;
                        str = md.getAlgorithm() + "  data:";
                        Util.MyLog(loadPackageParam.packageName,str,(byte[])param.args[0]);

                        str = md.getAlgorithm() + "  result:";
                        Util.MyLog(loadPackageParam.packageName,str,(byte[])param.getResult());
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Util {
    @NonNull
    public static String byteArrayToString(byte[] bytes) {
        String hs = "";
        String tmp = "";
        for (int n = 0; n < bytes.length; n++) {
            //整数转成十六进制表示
            tmp = (java.lang.Integer.toHexString(bytes[n] & 0XFF));
            if (tmp.length() == 1) {
                hs = hs + "0" + tmp;
            } else {
                hs = hs + tmp;
            }
        }
        tmp = null;
        return hs.toUpperCase(); //转成大写
    }

    public static String GetStack()
    {
        String result = "";
        Throwable ex = new Throwable();
        StackTraceElement[] stackElements = ex.getStackTrace();
        if (stackElements != null) {

            int range_start = 5;
            int range_end = Math.min(stackElements.length,7);
            if(range_end < range_start)
                return  "";

            for (int i = range_start; i < range_end; i++) {

                result = result + (stackElements[i].getClassName()+"->");
                result = result + (stackElements[i].getMethodName())+"  ";
                result = result + (stackElements[i].getFileName()+"(");
                result = result + (stackElements[i].getLineNumber()+")\n");
                result = result + ("-----------------------------------\n");
            }
        }
        return result;
    }

    public  static void MyLog(String packname,String info,byte[] data)
    {

        String path = Global.COMM_DIR + File.separator + packname;
        File pather = new File(path);
        if(!pather.exists())
             pather.mkdir();

        String filename = path + File.separator + "Crypto.txt";

        if(data.length >= 256)
            return;
        try
        {
            info = info + "\n";
            info = info + GetStack() + "\n";
            info = info + HexDumper.dumpHexString(data) + "\n------------------------------------------------------------------------------------------------------------------------\n\n";
            FileWriter fw = new FileWriter(filename, true);
            fw.write(info);
            fw.close();

            Log.d("q_"+packname,info);
            XposedBridge.log("["+ packname+"]"+info);

        }catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}