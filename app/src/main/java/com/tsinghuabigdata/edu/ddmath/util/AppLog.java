package com.tsinghuabigdata.edu.ddmath.util;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.tsinghuabigdata.edu.ddmath.ZxApplication;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.constant.ErrTag;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.LocalPageInfo;

import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.spi.LocationInfo;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * <p>
 * Created by yanshen@tsinghuabigdata.com on 2015/11/5.
 * </p>
 *
 * @author yanshen@tsinghuabigdata.com
 * @version V1.0
 * @packageName: com.tsinghuabigdata.edu.ddmath.util
 * @createTime: 2015/11/5 11:46
 */
public class AppLog {

    

/*
    public static void e(String msg){
        msg = wrapMessage(msg);
        Log.e(ZxApplication.LOG_TAG, msg);
        logger.error(msg);

                try {
            MobclickAgent.reportError(ZxApplication.getApplication(), msg);
        } catch (Exception e) {
            w("上传友盟失败", e);
        }
    }

    public static void e(String msg, Exception e){
        msg = wrapMessage(msg);
        Log.e(ZxApplication.LOG_TAG, msg, e);
        logger.error(msg, e);
        try {
            MobclickAgent.reportError(ZxApplication.getApplication(), e);
        } catch (Exception ex) {
            w("上传友盟失败", ex);
        }
    }*/

    /**
     * 将用户信息包装到日志消息中
     * @param msg
     * @return
     */
/*    public static String wrapMessage(String msg) {
        LoginInfo loginInfo = AccountUtils.getLoginUser();
        if (loginInfo != null) {
            StringBuffer wrap = new StringBuffer();
            wrap.append("[").append(loginInfo.getStudentId()).append(":").append(loginInfo.getLoginName()).append("]\n");
            wrap.append(msg);
            return wrap.toString();
        }
        return msg;
    }*/


    // log4j日志，将日志信息输出到文件保存
    private static Logger logger = Logger.getLogger("AppRequestUtils");
    private static String sessionid;
    private static String logname = Environment.getExternalStorageDirectory()
            + "/ddmath" + File.separator + "logs"
            + File.separator + "app.txt";

    private static String agent = "android_"+ Build.VERSION.RELEASE + "_" + android.os.Build.VERSION.SDK_INT + "-machine_" + Build.MODEL + "_" + Build.PRODUCT;

    private static LogUploader mLogUploader = LogUploader.instance();
    /**
     * 初始化LOG4J
     */
    public static void configLog4j( Context context, String sid, String appname, String restserver, String appver ){
        sessionid = sid;
        if( agent!=null&&!agent.contains("-appver_") )
            agent += "-appver_"+appver;
        mLogUploader.config(context, appname, logname, restserver);

        File file = new File( logname );
        if( file.exists() && file.length() > 0 ){
            mLogUploader.addInfo();
        }
        initLog4j();
    }

    private static void initLog4j(){

        /*LogConfigurator logConfigurator = new LogConfigurator();
        logConfigurator.setFileName(Environment.getExternalStorageDirectory()
                + File.separator + "zxapp" + File.separator + "logs"
                + File.separator + "log4j.txt");
        logConfigurator.setRootLevel(Level.DEBUG);
        logConfigurator.setLevel("org.apache", Level.ERROR);
        logConfigurator.setFilePattern("%d %-5p [%c{2}]-[%L] %m%n");
        logConfigurator.setMaxFileSize(1024 * 1024 * 5);
        logConfigurator.setImmediateFlush(true);
        logConfigurator.configure();*/

        RollingFileAppender rollingFileAppender;
        //Layout fileLayout = new PatternLayout("%d %-5p [%c{2}]-[%L] %m%n");
        //Layout fileLayout = new PatternLayout("[Tag][SessionId][%p][%d{yyyy-MM-dd HH:mm:ss:SSS}][%t][%C{1}][%M][%L][Agent][]%m%n%n");
        Layout fileLayout = new PatternLayout("%m");
        try {
            rollingFileAppender = new RollingFileAppender(fileLayout, logname );

            rollingFileAppender.setMaxBackupIndex(5);
            rollingFileAppender.setMaximumFileSize(1024 * 1024 * 5);
            rollingFileAppender.setImmediateFlush(true);
            logger.removeAllAppenders();
            logger.addAppender(rollingFileAppender);

            upload_time = System.currentTimeMillis();

        } catch (IOException e) {
            //throw new RuntimeException("Exception configuring log system", e);
            AppLog.d("",e);
        }

    }


    //-----------------------------------------------------------
    public static void caermaLog(long time, String type, LocalPageInfo localPageInfo){
        //只记录10秒以上的数据
        if( System.currentTimeMillis() - time < 10000 ){
            return;
        }
        LoginInfo loginInfo = AccountUtils.getLoginUser();
        if( loginInfo!=null && localPageInfo != null ){
            AppLog.i( String.format(Locale.getDefault(),"camera monitor type = %s, usetime = %ds,loginname=%s, bookname = %s, chapterName = %s, pageNume = %d ", type, (System.currentTimeMillis() - time)/1000, loginInfo.getLoginName(), localPageInfo.getLearnMaterialName(), localPageInfo.getChapterName(), localPageInfo.getPageNum() ) );
        }
    }
    public static void i(String msg){
        debugLogi(null,msg, null);
    }

    public static void i(String msg, Exception e){
        debugLogi(null, msg, e);
    }

    public static void i(String tag, String msg){
        debugLogi(tag,msg, null);
    }

    public static void i(String tag, String msg, Exception e){
        debugLogi(tag, msg, e);
    }

    //----------------------------------------------------------
    public static void d(String msg){
        debugLogd(null, msg, null);
    }

    public static void d(String msg, Exception e){
        debugLogd(null, msg, e);
    }

    public static void d(String tag, String msg){
        debugLogd(tag, msg, null);
    }

    public static void d(String tag, String msg, Exception e){
        debugLogd(tag,msg,e);
    }

    //---------------------------------------------------------------
//    public static void w(String msg) {
//        debugLogw(null, msg, null);
//    }
//
//    public static void w(String msg, Exception e){
//        debugLogw(null, msg, e);
//    }

    public static void w(String tag, String msg) {
        debugLogw(tag,msg, null);
    }

    public static void w(String tag, String msg, Exception e){
        debugLogw(tag,msg,e);
    }
    //-----------------------------------------------------------------
//    public static void e(String msg){
//        debugLoge(null, msg, null);
//    }
//
//    public static void e(String msg, Throwable e) {
//        debugLoge(null, msg, e);
//    }

    public static void e(String tag, String msg){
        debugLoge(tag,msg, null);
    }

    public static void e(String tag, String msg, Throwable e) {
        debugLoge( tag,msg,e);
    }

    //-----------------------------------------------------------------
    //程序崩溃后打印
    public static void c(String msg){
        debugLogc( ErrTag.TAG_CRASH, msg );
    }

    //-------------------------------------------------------------------
    private static final String getLogData( String level, String tag,/* String threadName,*/ String className, String methodName, String fileName, String lineNumber, String data ){
        return String.format("[%s][%s][%s][%s][][%s][%s][%s][%s][]%s", tag==null?"":tag, sessionid==null?"":sessionid, level, getTime(), /*threadName,*/className, methodName, lineNumber, agent, data);
    }

    //日志上传，保证90s内上传一次
    private static final long UPLOAD_MAXTIME = 90*1000;
    private static long upload_time = 0;

    private static final void debugLogi(String tag, String data, Exception e) {

        LocationInfo info = new LocationInfo( new Throwable(), AppLog.class.getName() );
        String fullClassName    = info.getClassName(); //Thread.currentThread().getStackTrace()[ClassIndex].getClassName();
        String className        = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
        String methodName       = info.getMethodName(); //Thread.currentThread().getStackTrace()[ClassIndex].getMethodName();
        String fileName         = info.getFileName();   //Thread.currentThread().getStackTrace()[ClassIndex].getFileName();
        String lineNumber          = info.getLineNumber(); //Thread.currentThread().getStackTrace()[ClassIndex].getLineNumber();

        //String msg = String.format("at %s.%s(%s:%s):%s", className, methodName, fileName, lineNumber, data);
        String msg = getLogData( "Info", tag, className, methodName, fileName, lineNumber, data );
        if( e == null ){
            Log.i(ZxApplication.LOG_TAG, msg );
            logger.info(msg);
            logger.info(";;\n");
        }else{
            Log.i(ZxApplication.LOG_TAG, msg, e );
            logger.info(msg, e);
            logger.info(";;\n");
        }

        if( System.currentTimeMillis() - upload_time > UPLOAD_MAXTIME ){
            mLogUploader.addInfo();
            initLog4j();
        }
    }

    public static final void debugLogd(String tag, String str, Exception e) {
        LocationInfo info = new LocationInfo( new Throwable(), AppLog.class.getName() );
        String fullClassName = info.getClassName(); //Thread.currentThread().getStackTrace()[ClassIndex].getClassName();
        String className        = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
        String methodName       = info.getMethodName(); //Thread.currentThread().getStackTrace()[ClassIndex].getMethodName();
        String fileName         = info.getFileName();   //Thread.currentThread().getStackTrace()[ClassIndex].getFileName();
        String lineNumber       = info.getLineNumber(); //Thread.currentThread().getStackTrace()[ClassIndex].getLineNumber();

        String msg = String.format("at %s.%s(%s:%s):%s", className, methodName, fileName, lineNumber, str);
        //String msg = getLogData("Debug", tag, className, methodName, fileName, lineNumber, str);
//        Log.d(ZxApplication.LOG_TAG, msg);
        tag = tag!=null?tag:ZxApplication.LOG_TAG;
        if( e == null ){
            Log.i( tag, msg);
        }else{
            Log.i( tag, msg, e);
        }
    }


    public static final void debugLogw(String tag, String str,Exception e) {
        LocationInfo info = new LocationInfo( new Throwable(), AppLog.class.getName() );
        String fullClassName = info.getClassName(); //Thread.currentThread().getStackTrace()[ClassIndex].getClassName();
        String className        = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
        String methodName       = info.getMethodName(); //Thread.currentThread().getStackTrace()[ClassIndex].getMethodName();
        String fileName         = info.getFileName();   //Thread.currentThread().getStackTrace()[ClassIndex].getFileName();
        String lineNumber       = info.getLineNumber(); //Thread.currentThread().getStackTrace()[ClassIndex].getLineNumber();


        //String msg = String.format("at %s.%s(%s:%s):%s", className, methodName, fileName, lineNumber, str);
        String msg = getLogData("Warn", tag, className, methodName, fileName, lineNumber, str);
        if( e == null ){
            Log.w(ZxApplication.LOG_TAG, msg);
            logger.warn(msg);
            logger.info(";;\n");
        }else{
            Log.w(ZxApplication.LOG_TAG, msg, e);
            logger.warn(msg, e);
            logger.info(";;\n");
        }

        mLogUploader.addWarn();
        initLog4j();
    }


    public static final void debugLoge(String tag, String str, Exception e) {

//        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
//        Log.d(ZxApplication.LOG_TAG, "===================== " );
//        for( StackTraceElement element : sts ){
//            Log.e(ZxApplication.LOG_TAG, "element = " + element.getClassName() );
//        }

        LocationInfo info = new LocationInfo( new Throwable(), AppLog.class.getName() );
        String fullClassName = info.getClassName(); //Thread.currentThread().getStackTrace()[ClassIndex].getClassName();
        String className        = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
        String methodName       = info.getMethodName(); //Thread.currentThread().getStackTrace()[ClassIndex].getMethodName();
        String fileName         = info.getFileName();   //Thread.currentThread().getStackTrace()[ClassIndex].getFileName();
        String lineNumber       = info.getLineNumber(); //Thread.currentThread().getStackTrace()[ClassIndex].getLineNumber();


        //String msg = String.format("at %s.%s(%s:%s):%s", className, methodName, fileName, lineNumber, str);
        String msg = getLogData("Error", tag, className, methodName, fileName, lineNumber, str);
        if( e == null ){
            Log.e(ZxApplication.LOG_TAG, msg);
            logger.error(msg);
            logger.info(";;\n");
        }else{
            Log.e(ZxApplication.LOG_TAG, msg, e);
            logger.error(msg, e);
            logger.info(";;\n");
        }

        mLogUploader.addError();
        initLog4j();
    }

    public static final void debugLoge(String tag, String str, Throwable e) {

        LocationInfo info = new LocationInfo( new Throwable(), AppLog.class.getName() );
        String fullClassName = info.getClassName(); //Thread.currentThread().getStackTrace()[ClassIndex].getClassName();
        String className        = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
        String methodName       = info.getMethodName(); //Thread.currentThread().getStackTrace()[ClassIndex].getMethodName();
        String fileName         = info.getFileName();   //Thread.currentThread().getStackTrace()[ClassIndex].getFileName();
        String lineNumber       = info.getLineNumber(); //Thread.currentThread().getStackTrace()[ClassIndex].getLineNumber();

        //String msg = String.format("at %s.%s(%s:%s):%s", className, methodName, fileName, lineNumber, str);
        String msg = getLogData("Error", tag, className, methodName, fileName, lineNumber, str);
        if( e == null ){
            Log.e(ZxApplication.LOG_TAG, msg);
            logger.error(msg);
            logger.info(";;\n");
        }else{
            Log.e(ZxApplication.LOG_TAG, msg, e);
            logger.error(msg, e);
            logger.info(";;\n");
        }

        mLogUploader.addError();
        initLog4j();
    }

    public static final void debugLogc(String tag, String str) {

        LocationInfo info = new LocationInfo( new Throwable(), AppLog.class.getName() );
        String fullClassName = info.getClassName(); //Thread.currentThread().getStackTrace()[ClassIndex].getClassName();
        String className        = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
        String methodName       = info.getMethodName(); //Thread.currentThread().getStackTrace()[ClassIndex].getMethodName();
        String fileName         = info.getFileName();   //Thread.currentThread().getStackTrace()[ClassIndex].getFileName();
        String lineNumber       = info.getLineNumber(); //Thread.currentThread().getStackTrace()[ClassIndex].getLineNumber();

        //String msg = String.format("at %s.%s(%s:%s):%s", className, methodName, fileName, lineNumber, str);
        String msg = getLogData("Error", tag, className, methodName, fileName, lineNumber, str);
        Log.e(ZxApplication.LOG_TAG, msg);
        logger.error(msg);
        logger.info(";;\n");

        mLogUploader.addCrash();
        initLog4j();
    }

    private static String getTime(){
        long time=System.currentTimeMillis();//long now = android.os.SystemClock.uptimeMillis();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        Date d1=new Date(time);
        return format.format(d1);
    }

}
