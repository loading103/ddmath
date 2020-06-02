/*
 * Copyright (c) 2016. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.tsinghuabigdata.edu.ddmath.commons.http;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.android.volley.RequestQueue;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tsinghuabigdata.edu.commons.http.HttpRequest;
import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.commons.http.VolleyHttpRequest;
import com.tsinghuabigdata.edu.commons.network.DNSHelper;
import com.tsinghuabigdata.edu.ddmath.ZxApplication;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.activity.ServerUpgradingActivity;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.constant.AppRequestConst;
import com.tsinghuabigdata.edu.ddmath.constant.BaseConfig;
import com.tsinghuabigdata.edu.ddmath.constant.ErrTag;
import com.tsinghuabigdata.edu.ddmath.module.login.model.LoginController;
import com.tsinghuabigdata.edu.ddmath.module.login.model.LoginTask;
import com.tsinghuabigdata.edu.ddmath.module.login.LoginActivity;
import com.tsinghuabigdata.edu.ddmath.parent.requestHandler.requestImpl.ParentCenterImpl;
import com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl.LoginReqImpl;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.AppUtils;
import com.tsinghuabigdata.edu.ddmath.util.PreferencesUtils;
import com.tsinghuabigdata.edu.utils.RestfulUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import au.id.villar.dns.DnsException;
import au.id.villar.dns.engine.Utils;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


/**
 * <p>
 * Created by yanshen@tsinghuabigdata.com on 2015/11/21.
 * </p>
 *
 * @author yanshen@tsinghuabigdata.com
 */
public class AppRequest<RESULT> extends VolleyHttpRequest {

    //private static Logger logger = Logger.getLogger("AppRequest");

//    public static void initLog4j() {
//        RollingFileAppender rollingFileAppender;
//        Layout fileLayout = new PatternLayout("%d %-5p [%c{2}]-[%L] %m%n");
//
//        try {
//            rollingFileAppender = new RollingFileAppender(fileLayout, Environment.getExternalStorageDirectory()
//                    + File.separator + "zxapp" + File.separator + "logs"
//                    + File.separator + "request.txt");
//        } catch (IOException e) {
//            throw new RuntimeException("Exception configuring log system", e);
//        }
//
//        rollingFileAppender.setMaxBackupIndex(5);
//        rollingFileAppender.setMaximumFileSize(1024 * 1024 * 5);
//        rollingFileAppender.setImmediateFlush(true);
//        logger.removeAllAppenders();
//        logger.addAppender(rollingFileAppender);
//    }

    /**
     * 构造函数
     *
     * @param url          请求地址
     * @param method       请求类型
     * @param requestQueue 网络请求队列
     */
    /*public*/ AppRequest(String url, int method, RequestQueue requestQueue) {
        super(url, method, requestQueue);
    }

    /**
     * 通过NDS服务器解析得到IP
     */
    private List<String> getIpFromDnsServer() {
        try {
            String domain = new URL(getUrl()).getHost();
            if (Utils.isValidDnsName(domain)) {
                AppLog.d("解析domain, URL:" + getUrl() + ", domain:" + domain);
                return new DNSHelper(AppRequestConst.DNS_SERVERS).resolver(domain);
            } else {
                AppLog.i("解析域名发现无效的域名。" + domain);
                return new ArrayList<>();
            }
        } catch (DnsException e) {
            AppLog.i("域名解析出错", e);
            return new ArrayList<>();
        } catch (Exception e) {
            AppLog.i("域名解析出错", e);
        }
        return new ArrayList<>();
    }

    /**
     * 使用代理
     */
    private boolean useProxy() {
        // Wifi环境下才能走代理
        // 已经设置了代理，就不再走代理解析了
        if (!AppUtils.isWifi() || RestfulUtils.isUseProxy()) {
            return false;
        }

        if (!BaseConfig.IS_USE_DNSPROXY) {
            return false;
        }

        // 504 time out
        List<String> ips = getIpFromDnsServer();
        if (ips.size() > 0) {
            // 使用代理
            RestfulUtils.useProxy(ips.get(0), 80);
            AppLog.d("已解析出域名.");
            return true;
        }
        AppLog.d("没有解析出域名.");
        return false;
    }

    private void printRequestLog() {
        AppLog.d(ErrTag.TAG_HTTP, "resturl = " + getRestfulUrl(getUrl()));
        AppLog.d(ErrTag.TAG_HTTP, "request method:" + getMethod() + " @see Request.Method");
        AppLog.d(ErrTag.TAG_HTTP, "request encoding:" + getParamEncoding());
        AppLog.d(ErrTag.TAG_HTTP, "request params:" + getRequestParams());
        AppLog.d(ErrTag.TAG_HTTP, "request rest:" + getRestfulParams());
        AppLog.d(ErrTag.TAG_HTTP, "request head:" + getHeader());
    }

    private boolean isExpired(HttpRequestException ex) {

        //是登录接口，直接返回不超期
        if( getUrl().contains( AppRequestConst.POST_LOGIN_STUDENT ) ){
            AppLog.d("fduuurl kkkkkkkk is Login" );
            return false;
        }

        String errMessage = ex.getMessage();
        // 新版过期判断
        boolean errCodeRes = "token has expired".equalsIgnoreCase(errMessage);
        if (errCodeRes) {
            return true;
        } else {
            // 老版过期判断
            String body = ex.getRequest().getBody();
            if (body != null) {
                body = body.toLowerCase();
                return ex.getRequest().getStatusCode() == 402 && body.contains("token has expired");
            }
            return ex.getRequest().getStatusCode() == 402;
        }
//        boolean oldRes = errCodeRes ? errCodeRes : ex.getRequest().getStatusCode() == 402 && ex.getRequest().getBody().contains("token has expired");
//        return errCodeRes || oldRes;
    }

    /**
     * 请求数据，如果出现token过期，需要重新登录
     */
    private HttpRequest getHttpRequest() throws HttpRequestException {
        try {
            printRequestLog();
            HttpRequest request = super.request();
            assertTokenExpired();
            return request;
        } catch (HttpRequestException ex) {
            if (isExpired(ex)) {
                // 重新登录 老版接口
                if (reLogin()) {
                    LoginInfo loginInfo = AccountUtils.getLoginUser();
                    if( loginInfo!=null ) putHeader("access_token", loginInfo.getAccessToken());
                    return super.request();
                }
            }
            throw ex;
        }
    }

    private HttpRequest getHttpRequestJson() throws HttpRequestException {
        try {
            printRequestLog();
            HttpRequest request = super.requestJson();
            assertTokenExpired();
            return request;
        } catch (HttpRequestException ex) {
            // token过期
            if (isExpired(ex)) {
                // 重新登录 老版接口
                if (reLogin()) {
                    LoginInfo loginInfo = AccountUtils.getLoginUser();
                    if( loginInfo!=null ) putHeader("access_token", loginInfo.getAccessToken());
                    return super.requestJson();
                }
            }
            throw ex;
        }
    }

    private void assertTokenExpired() throws HttpRequestException {

        // 判断body中是否包含20132错误码和Invalid token, token has expired信息
        String body = super.getBody();
        if (TextUtils.isEmpty(body)) {
            return;
        }
        AppLog.d("body = "+body);
        try {
            JSONObject object = new JSONObject(body);
            if (object.has("code") && object.getInt("code") == ResponseCode.CODE_20132) {
                AppLog.i(ErrTag.TAG_HTTP, "tokenhasexpired, body = " + body + ",, url = " + getUrl() );
                throw new HttpRequestException("token has expired", new Exception("token has expired"), this);
            }
        } catch (JSONException e) {
            AppLog.w(ErrTag.TAG_HTTP, "warn", e);
        }
    }

    private boolean reLogin() {

//        if( AppUtils.isDebug() )
//            return false;

        final Context context = ZxApplication.getApplication();
        if (context == null) {
            AppLog.i("刷新Token失败, content=null");
            return false;
        }

        boolean isStudent = AccountUtils.getLoginUser() != null;
        final String loginName = PreferencesUtils.getString(context, isStudent?AppConst.LOGIN_NAME:AppConst.LOGIN_PARENTNAME, null);
        final String loginPass = PreferencesUtils.getString(context, isStudent?AppConst.LOGIN_PASS:AppConst.LOGIN_PARENTPASS, null);
        if (loginName == null || loginPass == null) {
            AppLog.i("刷新Token失败, 用户名密码找不到");
            return false;
        }
        try {

            if( isStudent ){
                LoginInfo loginInfo = new LoginTask( new LoginReqImpl(),context, null ).getLoginInfo( loginName, loginPass );
                if (loginInfo == null) {
                    throw new RuntimeException("获取登录信息为null");
                }
                AppLog.i("刷新Token成功, token=" + loginInfo.getAccessToken() + ",  loginName = " + loginName);
                LoginController.getInstance().Login(true);
                return true;
            }else{
                //家长端
                LoginInfo loginInfo = new ParentCenterImpl().login( loginName, loginPass, AppUtils.getDeviceId( context ) );
                if (loginInfo == null) {
                    throw new RuntimeException("获取登录信息为null");
                }
                AppLog.d("刷新Token成功, token=" + loginInfo.getAccessToken());
                return true;
            }

        } catch (Exception e) {
            AppLog.i("刷新Token失败", e);
            forceReLogin();
        }
        return false;
    }

    @Override
    public HttpRequest request() throws HttpRequestException {
        try {
            return getHttpRequest();
        } catch (HttpRequestException ex) {
            // 出现异常，通过解析NDS，设置代理去访问一次
            if (useProxy()) {
                // 再请求一次
                return getHttpRequest();
            }
            throw ex;
        }
    }

    @Override
    public HttpRequest requestJson() throws HttpRequestException {
        try {
            return getHttpRequestJson();
        } catch (HttpRequestException ex) {
            // 出现异常，通过解析NDS，设置代理去访问一次
            if (useProxy()) {
                // 再请求一次
                return getHttpRequestJson();
            }
            throw ex;
        }
    }

    //存在漏洞，获取全部请使用 getFullBody()    仅获取返回结果的data下面的内容，请调用 getDataBody()
    @Deprecated
    @Override
    public String getBody() {
        String body = super.getBody();
        if (TextUtils.isEmpty(body)) {
            return "body is null";
        }
        if( "time out".equals(body) ){
            throw new RuntimeException("request time out");
        }
        try {
            JSONObject object = new JSONObject(body);
            if (object.has("code")) {
                if (object.getInt("code") != ResponseCode.CODE_10000) {
                    // 非10000抛出异常
                    AppLog.w(ErrTag.TAG_HTTP, "request failure " + getUrl() + " res " + body);
                    if (object.getInt("code") == ResponseCode.CODE_10120){
                        startServerUpgradingActivity();
                    }
                    dealEmptyTokenException( object );

                    throw new AppRequestException(getInternalResult());
                }
                dealScoreShow( object.getString("data") );
                return object.getString("data");
            } else {
                throw new AppRequestException(getInternalResult());
            }
        } catch (Exception e) {
            AppLog.w(ErrTag.TAG_HTTP, "body="+body+",   url = "+ getUrl()+", exception = " + e.getMessage() );
            throw new AppRequestException(getInternalResult());
        }
    }

    public String getDataBody() throws JSONException, AppRequestException {
        String body = super.getBody();
        if (TextUtils.isEmpty(body)) {
            throw new JSONException("body is null");
        }
        if( "time out".equals(body) ){
            throw new RuntimeException("request time out");
        }
        JSONObject object = new JSONObject(body);
        if (object.has("code")) {
            if (object.getInt("code") != ResponseCode.CODE_10000) {
                // 非10000抛出异常
                AppLog.w(ErrTag.TAG_HTTP, "request failure " + getUrl() + " res " + body);
                if (object.getInt("code") == ResponseCode.CODE_10120){
                    startServerUpgradingActivity();
                }
                dealEmptyTokenException( object );
                throw new AppRequestException(getInternalResult());
            }
            dealScoreShow( object.getString("data") );
            return object.getString("data");
        } else {
            throw new JSONException("code is null");
        }
    }

    //服务器正在升级维护界面
    private void startServerUpgradingActivity() {
        Context context = ZxApplication.getApplication();
        Intent intent = new Intent(context, ServerUpgradingActivity.class);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    //异常时使用
    private HttpResponse getInternalResult() {
        TypeToken<HttpResponse<Boolean>> token = new TypeToken<HttpResponse<Boolean>>(){};
        Type type = token.getType();
        String body = super.getBody();
        AppLog.w("getInternalResult","body = "+ body + ",,,type = " + type.toString() );
        return new Gson().fromJson( body, type);
    }

    /**
     * 获取全部body内容
     */
    public String getFullBody() {
        String data = super.getBody();
        if( !TextUtils.isEmpty(data) && data.startsWith("{") && data.endsWith("}") ){
            try{
                com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject( data );
                if( jsonObject.containsKey("data") ){
                    dealScoreShow( jsonObject.getString("data") );
                }
            }catch (Exception e){ AppLog.d( "", e);}
        }
        return data;
    }

    //在返回结果{ code: ,data: }结构里面分析 data的内容
    private void dealScoreShow( String data ){
        if(TextUtils.isEmpty(data)) return;

        if( data.indexOf("pointAmt") > 0 && data.startsWith("{") && data.endsWith("}") ){
            try{
                com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject( data );
                if( jsonObject.containsKey("pointAmt") && (!jsonObject.containsKey("remainCount") || !jsonObject.containsKey("userExchange") )){
                    int point = jsonObject.getIntValue("pointAmt");
                    if( point > 0 && point < 60 ) ZxApplication.getApplication().showUserScore( point );
                }
            }catch (Exception e){ AppLog.d( "", e);}
        }
    }

    //在返回的异常信息里面处理缺少token的异常
    private void dealEmptyTokenException( JSONObject object ){
        String key = "accesstoken and uri can't be empty";
        try {
            if( (object.has("inform") && object.getString("inform").equals(key)) || (object.has("message") && object.getString("message").equals(key)) ){
                forceReLogin();
            }
        }catch (Exception e){ AppLog.d("",e); }
    }

    //强制用户重新登录，token过期， 或者 token 丢失
    private void forceReLogin(){

        //2018/11/10 如果当前界面是登录界面，怎么处理？？？
        ActivityManager.RunningTaskInfo topTask = AppUtils.getTopTask( ZxApplication.getApplication());
        PackageInfo packageInfo = AppUtils.getPackageInfo(ZxApplication.getApplication());
        if (packageInfo!=null && AppUtils.isTopActivity(topTask, packageInfo.packageName, LoginActivity.class.getName())) {
            return;
        }

        // 清空
        AccountUtils.clear();

        boolean isStudent = AccountUtils.getLoginUser() != null;
        final Context context = ZxApplication.getApplication();
        if (context == null) return;

        // 通知所有页面Finish
        context.sendBroadcast(new Intent(RoboActivity.ACTION));
        LoginController.getInstance().Login(false);

        // 跳转到登录页面
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra( LoginActivity.ROLE_TYPE, isStudent?LoginActivity.ROLE_STUDENT:LoginActivity.ROLE_PARENT );
        intent.putExtra(LoginActivity.MESSAGE, "登录已过期,请重新登录");
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
