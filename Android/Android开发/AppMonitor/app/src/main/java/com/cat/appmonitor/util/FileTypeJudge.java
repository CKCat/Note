

package com.cat.appmonitor.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileTypeJudge {

	private FileTypeJudge() {

	}



	private static String bytesToHexString(byte[] src) {

    StringBuilder stringBuilder = new StringBuilder();

    if (src == null || src.length <= 0) {
    	return null;
    }

    for (int i = 0; i < src.length; i++) {
    	int v = src[i] & 0xFF;
    	String hv = Integer.toHexString(v);
    	if (hv.length() < 2) {
        stringBuilder.append(0);
    	}
    	stringBuilder.append(hv);
    }
    return stringBuilder.toString();
	}


	private static String getFileContent(String filePath) throws IOException {

    byte[] b = new byte[15];

    InputStream inputStream = null;

    try {
    	inputStream = new FileInputStream(filePath);
    	inputStream.read(b, 0, b.length);
    } catch (IOException e) {
    	e.printStackTrace();
    	throw e;
    } finally {
    	if (inputStream != null) {
        try {
        	inputStream.close();
        } catch (IOException e) {
        	e.printStackTrace();
        	throw e;
        }
    	}
    }
    return bytesToHexString(b);
	}


	
	public static FileType getType(String filePath) throws IOException {

    String fileHead = getFileContent(filePath);

    if (fileHead == null || fileHead.length() == 0) {
    	return null;
    }

    fileHead = fileHead.toLowerCase();

    FileType[] fileTypes = FileType.values();

    for (FileType type : fileTypes) {
    	if (fileHead.startsWith(type.getValue())
        	|| type.getValue().startsWith(fileHead)) {
        return type;
    	}
    }

    return null;
	}
}
