package com.tsinghuabigdata.edu.ddmath.commons.http;

import android.content.Context;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.toolbox.HttpStack;
import com.squareup.okhttp.OkHttpClient;
import com.tsinghuabigdata.edu.commons.http.HttpRequest;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.constant.AppRequestConst;
import com.tsinghuabigdata.edu.utils.NetworkUtils;
import com.tsinghuabigdata.edu.utils.RestfulUtils;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.AppUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;


/**
 * <p>
 * Created by yanshen@tsinghuabigdata.com on 2015/11/21.
 * </p>
 *
 * @author yanshen@tsinghuabigdata.com
 * @version V1.0
 * @packageName: com.tsinghuabigdata.edu.ddmath.commons.http
 * @createTime: 2015/11/21 14:27
 */
public class AppRequestUtils{


    public synchronized static void initialization(Context context) {
        //AppRequest.initLog4j();
        HttpStack hurlStack = new RestfulUtils.ProxyOkHurlStack(new OkHttpClient()){

            @Override
            public void useProxy(OkHttpClient client, String url) {
                try {
                    URL parsedUrl = new URL(url);
                    if (AppRequestConst.RESTFUL_ADDRESS.contains(parsedUrl.getHost())) {
                        super.useProxy(client, url);
                    }
                } catch (MalformedURLException e) {
                    throw new RuntimeException("MalformedURLException", e);
                }

            }
        };

       /* HttpStack hurlStack = new RestfulUtils.ProxyHurlStack(){
            @Override
            protected HttpURLConnection createConnection(URL url) throws IOException {
                if (AppConfig.RESTFUL_ADDRESS.contains(url.getHost())) {
                    return super.createConnection(url);
                } else if (AppConfig.UPDATE_HOST.contains(url.getHost())) {
                    return super.createConnection(url);
                } else {
                    return (HttpURLConnection) url.openConnection();
                }
            }
        };*/
        RestfulUtils.initialization(context, hurlStack);
    }

    private static String getClientInfo() {
        String info;
        try{
            info = "Android," + android.os.Build.VERSION.RELEASE + "," + URLEncoder.encode( android.os.Build.MODEL, "utf-8" ) + "," + (AppUtils.isWifi() ? "Wifi" : "Mobile " + NetworkUtils.getCurrentNetworkType());
        } catch (Exception e){
            AppLog.i( "", e );
            info = "Android," + android.os.Build.VERSION.RELEASE + ","  + (AppUtils.isWifi() ? "Wifi" : "Mobile " + NetworkUtils.getCurrentNetworkType());
        }
        return info;
    }

    /**
     * GET请求
     */
    public static <RESULT> HttpRequest get(String url){
        AppRequest request = getResultAppRequest(url, Request.Method.GET);
        setToken(request);
        return request;
    }

    @NonNull
    private static <RESULT> AppRequest<RESULT> getResultAppRequest(String url, int get) {
        AppRequest request = new AppRequest<RESULT>(url, get, RestfulUtils.mRequestQueue);
        request.putHeader("ClientInfo", getClientInfo());
        return request;
    }

    /**
     * POST请求
     */
    public static <RESULT> HttpRequest post(String url){
        AppRequest request = getResultAppRequest(url, Request.Method.POST);
        setToken(request);
        return request;
    }

    private static void setToken(AppRequest request) {
        try {
            //优先学生
            LoginInfo loginInfo = AccountUtils.getLoginUser();
            //再次家长
            if( loginInfo == null ) loginInfo = AccountUtils.getLoginParent();
            if (loginInfo != null && loginInfo.getAccessToken() != null) {
                request.putHeader("access_token", loginInfo.getAccessToken());
            }else{
                if( loginInfo != null ){
                    AppLog.i("settokenfault tokenhasexpired", "access_token = null, loginInfo = " + loginInfo.getLoginName());
                }
            }
        } catch (Exception ex) {
            AppLog.i("set token fault", ex);
        }
    }

    /**
     * PUT请求
     * @param url
     * @return
     */
    public static HttpRequest put(String url){
        AppRequest request = getResultAppRequest(url, Request.Method.PUT);
        setToken(request);
        return request;
    }

    /**
     * DELETE请求
     * @param url
     * @return
     */
    public static HttpRequest delete(String url){
        AppRequest request = getResultAppRequest(url, Request.Method.DELETE);
        setToken(request);
        return request;
    }

    /**
     * 上传
     * @param url
     * @return
     */
    public static HttpRequest from(String url){
        AppRequest request = getResultAppRequest(url, 0);
        setToken(request);
        return request;
    }

}
