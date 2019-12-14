package com.cat.appmonitor.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Util {
    
    public static String getSystemTime(){
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy/MM/dd----hh:mm:ss", Locale.getDefault());
        Date date = new Date(System.currentTimeMillis());
        String dateTime = sDateFormat.format(date);
        return dateTime;
    }

    public static void writeLog(String pkgName, String log){
        if(SDUtils.isSdCardAvailable()){
            String FilePath = Global.COMM_DIR + File.separator + pkgName + File.separator + Global.LOG_FILE;
            FileIOUtils.writeFileFromString(FilePath, log, true);
        }
    }
    public static void writeFile(String pkgName, String filePath){

        InputStream in = null;
        OutputStream out = null;
        String outDir = Global.COMM_DIR + File.separator+  pkgName;
        String outPath = outDir  + File.separator+ filePath;
        int i = 0;
        while (new File(outPath).exists()) {
            outPath += i;
        }

        try {
            in = new FileInputStream(filePath);
            FileIOUtils.writeFileFromIS(outPath, in);
//            out = new FileOutputStream(outPath);
//            byte[] buf = new byte[1024];
//            int len;
//            while ((len = in.read(buf)) > 0) {
//                out.write(buf, 0, len);
//            }
//            in.close();
//            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void writeNetLog(String pkgName, List<String> logList){
        if(SDUtils.isSdCardAvailable()){
            File logFile = SDUtils.createFile("Appmonitor/NetLog", pkgName);
            FileWriter fw = null;
            try{
                fw = new FileWriter(logFile, true);
                for(String log : logList){
                    fw.write(log+"\n");
                }
                fw.write("\n");
                fw.flush();
                fw.close();
            }catch (FileNotFoundException e) {
                System.out.println("file not found!");
            } catch (IOException e) {
                System.out.println("Output error!");
            }
        }
    }

}
