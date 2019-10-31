package com.cat.appmonitor.Hook;

import com.cat.appmonitor.util.Logger;
import com.cat.appmonitor.util.Stack;
import com.cat.appmonitor.util.Util;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class XAbstractHttpClient extends XHook {
    private static final String className = "org.apache.http.impl.client.AbstractHttpClient";
    private static XAbstractHttpClient xAbstractHttpClient;

    public static XAbstractHttpClient getInstance() {
        if (xAbstractHttpClient == null) {
            xAbstractHttpClient = new XAbstractHttpClient();
        }
        return xAbstractHttpClient;
    }


    @Override
    void hook(final XC_LoadPackage.LoadPackageParam packageParam) {

        XposedHelpers.findAndHookMethod(className, packageParam.classLoader, "execute",
                HttpHost.class, HttpRequest.class, HttpContext.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        HttpHost host = (HttpHost) param.args[0];
                        HttpRequestBase request = (HttpRequestBase) param.args[1];
                        //BasicHttpResponse respone = (BasicHttpResponse) param.getResult();
                        String callRef = Stack.getCallRef();

                        StringBuffer logsb = new StringBuffer();
                        logsb.append("time:" + Util.getSystemTime() + '\n');
                        logsb.append("[### function ###]: execute\n");

                        if (request instanceof HttpGet) {
                            HttpGet httpGet = (HttpGet) request;
                            String log = handleHttpGet(host, httpGet);
                            logsb.append(log + '\n');
                        } else if (request instanceof HttpPost) {
                            HttpPost httpPost = (HttpPost) request;
                            String log = handleHttpPost(host, httpPost);
                            logsb.append(log + '\n');
                        }

                        //logsb.append(handleResult(respone) + '\n');
                        logsb.append("callRef:" + callRef + '\n');
                        Util.writeLog(packageParam.packageName, logsb.toString());

                        Logger.log("[=== AbstractHttpClient execute ===] host : " + host);
                        Logger.logCallRef("[=== AbstractHttpClient execute ===] ");
                    }
                });
    }


    public String handleHttpGet(HttpHost httpHost, HttpGet httpGet) {
        String host = httpHost.toURI().toString();
        String url = httpGet.getURI().toString();
        StringBuffer logsb = new StringBuffer();
        logsb.append("HTTP METHOD:" + httpGet.getMethod() + '\n')
                .append("HOST:" + host + '\n')
                .append("URL:" + url + '\n');


        Logger.log("[=== HttpGet ===] host : " + host);
        Logger.log("[=== HttpGet ===] url  : " + url);

        Header[] header = httpGet.getAllHeaders();
        if (header != null) {
            for (int i = 0; i < header.length; i++) {
                logsb.append(header[i].getName() + ":" + header[i].getValue() + '\n');
                Logger.log("[=== HttpGet ===] (Header) " + header[i].getName() + " : " + header[i].getValue());
            }
        }
        return logsb.toString();
    }

    public String handleHttpPost(HttpHost httpHost, HttpPost httpPost) {
        String host = httpHost.toURI().toString();
        String url = httpPost.getURI().toString();

        StringBuffer logsb = new StringBuffer();

        logsb.append("HTTP METHOD:" + httpPost.getMethod() + '\n')
                .append("HOST:" + host + '\n')
                .append("URL:" + url + '\n');

        Logger.log("[=== HttpPost ===] host : " + host);
        Logger.log("[=== HttpPost ===] url  : " + url);

        Header[] header = httpPost.getAllHeaders();
        if (header != null) {
            for (int i = 0; i < header.length; i++) {
                logsb.append(header[i].getName() + ":" + header[i].getValue() + '\n');
                Logger.log("[=== HttpPost ===] (Header) " + header[i].getName() + " : " + header[i].getValue());
            }
        }

        HttpEntity entity = httpPost.getEntity();
        String contentType = null;
        if (entity.getContentType() != null) {
            contentType = entity.getContentType().getValue();
            if (URLEncodedUtils.CONTENT_TYPE.equals(contentType)) {
                try {
                    byte[] data = new byte[(int) entity.getContentLength()];
                    entity.getContent().read(data);
                    String content = new String(data, HTTP.DEFAULT_CONTENT_CHARSET);

                    logsb.append("HTTP POST CONTENT:" + content + '\n');
                    Logger.log("[=== HttpPost ===] Content  : " + content);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (contentType.startsWith(HTTP.DEFAULT_CONTENT_TYPE)) {
                try {
                    byte[] data = new byte[(int) entity.getContentLength()];
                    entity.getContent().read(data);
                    String content = new String(data, contentType.substring(contentType.lastIndexOf("=") + 1));

                    logsb.append("HTTP POST CONTENT:" + content + '\n');
                    Logger.log("[=== HttpPost ===] Content : " + content);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                byte[] data = new byte[(int) entity.getContentLength()];
                entity.getContent().read(data);
                String content = new String(data, HTTP.DEFAULT_CONTENT_CHARSET);

                logsb.append("HTTP POST CONTENT:" + content + '\n');
                Logger.log("[=== HttpPost ===] Content : " + content);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return logsb.toString();
    }

    public String handleResult(BasicHttpResponse respone) {
        StringBuffer logsb = new StringBuffer();
        if (respone != null) {
            logsb.append("result code:" + respone.getStatusLine().getStatusCode() + '\n');
            Header[] header = respone.getAllHeaders();
            if (header != null) {
                for (int i = 0; i < header.length; i++) {
                    logsb.append(header[i].getName() + ":" + header[i].getValue() + '\n');
                    Logger.log("[=== AbstractHttpClient execute ===] (Header) " + header[i].getName() + " : " + header[i].getValue());
                }
            }
            String result = "";
            try {
                InputStream is = respone.getEntity().getContent();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader reader = new BufferedReader(isr);
                StringBuffer sb = new StringBuffer();
                String line = reader.readLine();
                while (line != null){
                    sb.append(line + '\n');
                }
                result = sb.toString();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            logsb.append("result:\n" + result + '\n');

            Logger.log("[=== AbstractHttpClient execute ===] result : " + result);
        } else {
            logsb.append("result:null\n");
        }
        return logsb.toString();
    }
}
