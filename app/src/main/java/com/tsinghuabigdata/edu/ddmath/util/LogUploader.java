package com.tsinghuabigdata.edu.ddmath.util;

import android.content.Context;

import com.tsinghuabigdata.edu.ddmath.constant.AppRequestConst;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * 日志上传系统
 * 有需要上传的日志后，把 日志保存为新的文件，按格式命名log_warn_XX,log_err_xx,log_crash_XX,完成这个任务后，删除对应的文件
 * 进入时 检查有没有日志文件，有就继续处理
 */
 class LogUploader {

    //上传接口
    private static final String VERSION = "1";
    private static final String POST_LOG = AppRequestConst.LOG_UPLOAD + VERSION;

    private static final String LOGPATH = "uplog/";

    //内部使用
    private static final String TYPE_INFO   = "info";
    private static final String TYPE_WARN   = "warn";
    private static final String TYPE_ERROR  = "error";
    //private static final String TYPE_CRASH  = "crash";

    private static final int LOCAL_MAX = 500;      //本地保存最大时，直接删除

    private Context mContext;
    private String mLogName;                 //正常log放置的地方
    private String mUploadUrl;               //正常log放置的地方

    private String mLogPath;

    private static LogUploader instance;
    private final Object lock = new Object();
    //-------------------------------------------------------------------
    //对外接口


    public static LogUploader instance(){
        if( instance == null ){
            instance = new LogUploader();
        }
        return instance;
    }
    public void config(Context context, String appname, String logname, String domain ){
        this.mContext   = context;
        this.mLogName   = logname;

        this.mUploadUrl = domain + POST_LOG;
        this.mUploadUrl = mUploadUrl.replace(":plat", appname );

        initDir();
        loadLocalLogTask();

    }

    //添加 定时上传机制
    void addInfo(){
        if(AppUtils.isDebug()) return;
        LogTask task = new LogTask();
        task.type = TYPE_INFO;
        task.time = System.currentTimeMillis();
        if( task.ready() ){
            start(task);
        }
    }

    //添加警告
    void addWarn(){
        if(AppUtils.isDebug()) return;
        LogTask task = new LogTask();
        task.type = TYPE_WARN;
        task.time = System.currentTimeMillis();
        if( task.ready() ){
            start(task);
        }
    }

    //添加错误
    void addError(){
        if(AppUtils.isDebug()) return;
        LogTask task = new LogTask();
        task.type = TYPE_ERROR;
        task.time = System.currentTimeMillis();
        if( task.ready() ){
            start( task);
        }
    }

    //添加崩溃   只保存 不上传
    void addCrash(){
        if(AppUtils.isDebug()) return;
        LogTask task = new LogTask();
        task.type = TYPE_ERROR;
        task.time = System.currentTimeMillis();
        task.ready();

    }

    //----------------------------------------------------------
    //隐藏构造函数
    private LogUploader(){}

    //任务池
    private ArrayList<LogTask> mLogTaskList = new ArrayList<>();

    private static final int ST_IDLE   = 0;
    private static final int ST_RUN    = 1;
    private static final int ST_STOP   = 2;

    private int run_status = ST_IDLE;

    //启动任务
    private void start( LogTask task ){

        if(AppUtils.isDebug()){
            if(task!=null)task.finish();
            return;
        }

        synchronized(lock){
            mLogTaskList.add( task );
            if( mLogTaskList.size() > LOCAL_MAX ){
                //不上传，删除第二个
                task = mLogTaskList.get(1);
                if(task!=null)task.finish();
                mLogTaskList.remove( task );
                AppLog.w("LocalLog","Local Log files is bigger than " + LOCAL_MAX );
            }

            if( run_status == ST_RUN ) return;

            run_status = ST_RUN;
        }

        startMainThread();
    }

    //上传主线程
    private void startMainThread(){

        new Thread(new Runnable() {
            @Override
            public void run() {

                while( true ){

                    LogTask task;
                    synchronized(lock) {
                        if (mLogTaskList.size() == 0)
                            break;

                        task = mLogTaskList.get(0);
                    }
                    boolean success = uploadfile(task);
                    if(!success){
                        success = uploadfile(task);     //如果失败，重试一次
                    }

                    if( success && task!=null ){     //成功 执行清理动作
                        task.finish();
                    }
                    synchronized(lock) {
                        mLogTaskList.remove( task );
                    }
                }
                run_status = ST_STOP;
            }
        }).start();
    }

    /*
     * 以表单形式完成一个任务数据上传提交
     */
    private boolean uploadfile( LogTask task ){

        if( task == null ) return true;

        File file = new File( task.logname );
        if( !file.exists() || file.length() == 0 ) {
            return true;
        }

        HttpURLConnection conn = null;

        String multipart_form_data = "multipart/form-data";
        String twoHyphens = "--";
        String boundary = "****************";// 数据分隔符
        String lineEnd = System.getProperty("line.separator"); // The value is "\r\n" in Windows.（换行符）

        try {
            URL urls = new URL( mUploadUrl );
            conn = (HttpURLConnection) urls.openConnection();

            conn.setConnectTimeout(90 * 1000);//8秒超时
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "keep-alive");
            //表单形式上传图片（很重要）
            conn.setRequestProperty( "Content-Type", multipart_form_data+";boundary="+boundary);
            conn.connect();//打开链接

            //很重要（数据封装到这里上传）
            DataOutputStream output = new DataOutputStream(conn.getOutputStream());

            StringBuilder spilt = new StringBuilder();
            spilt.append( twoHyphens ).append( boundary).append( lineEnd );
            spilt.append("Content-Disposition: form-data; name=\"log_file\";filename=\" ").append( file.getName()).append(" \" ").append(lineEnd);
            spilt.append("Content-Type: text/plain").append(lineEnd);
            spilt.append(lineEnd);
            // 发送图片数据
            FileInputStream fis = null;
            try {
                output.writeBytes(spilt.toString());
                //output.write(fileToBytes(file), 0, (int) file.length());

                byte buffer[] = new byte[512];
                fis = new FileInputStream( file );
                int len = fis.read( buffer );
                while ( len != -1 ){
                    output.write( buffer,0,len);
                    len = fis.read( buffer );
                }

                output.writeBytes(lineEnd);

            } catch (IOException e) {
                AppLog.d("LogUploader", e);
            }finally {
             if( fis!=null ){
                 try {
                     fis.close();
                 }catch (Exception e){AppLog.d("",e);}
             }
            }
            output.writeBytes( twoHyphens+boundary+twoHyphens+lineEnd );//数据结束标志
            output.flush();
            output.close();

            //获取返回信息
            if( conn.getResponseCode()==200 ){//成功
                AppLog.d("LogUploader", "LogUploader success");

                conn.disconnect();//关闭链接
                conn = null;
                return true;
            }
            AppLog.d("LogUploader", "LogUploader code = " + conn.getResponseCode() );

        }catch (Exception ie) {
            AppLog.d("LogUploader", ie);
        }finally {
            AppLog.d("LogUploader", " finally");
            if(conn!=null){ conn.disconnect(); }
        }
        return false;
    }

    //目录初始化
    private void initDir(){

        if( mContext == null ) return;

        File exfile = mContext.getExternalCacheDir();
        if( exfile==null ) return;

        String path = exfile.toString();

        if( !path.endsWith("/") ){
            path += "/";
        }
        path += LOGPATH;

        File file = new File( path );
        boolean b = file.mkdirs();

        mLogPath = path;

    }

    /*
     * 扫描本地上传日志文件，依次添加入上传任务
     */
    private void loadLocalLogTask(){

        if( mLogPath == null )
            return;

        File file = new File( mLogPath );

        if( !file.exists() || !file.isDirectory() )
            return;

        String list[] = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.startsWith("log_");
            }
        });

        if( list == null )
            return;

        for( String name : list ){

            String params[] = name.split("_");
            if( params.length < 3 )
                continue;
            LogTask task = new LogTask();
            task.type = params[1];
            task.time = Long.parseLong(params[2].replace(".log", ""));
            task.logname = mLogPath + name;

            start( task );
        }

    }


    //--------------------------------------------------------------------------
    private class LogTask {

        String type;
        long   time;
        String logname;       // 日志文件内容保存的文件名

        //把当前系统日志文件保存为此次日志文件，清除系统日志文件
        boolean ready(){

            //先把log文件拷贝到指定目录
            if( mLogPath == null || mLogName == null )
                return  false;

            String fname = mLogPath + "log_"+type+"_"+time+".log";
            File file = new File( fname );
            File logfile = new File( mLogName );

            byte buffer[] = new byte[512];

            //copy 文件
            FileInputStream fis = null;
            FileOutputStream fos = null;
            try {
                fis = new FileInputStream( logfile );
                fos = new FileOutputStream( file );

                int len  = fis.read( buffer );
                while( len != -1 ){
                    fos.write( buffer, 0, len );
                    len  = fis.read( buffer );
                }

                fos.flush();

                //删除原日志文件
                logfile.delete();
                logfile.createNewFile();

                logname = fname;

                return true;

            }catch (Exception e){
                AppLog.d("LogUploader", e);
            }finally {
                if( fis!=null ){ try {
                    fis.close();
                }catch (Exception e){AppLog.d("",e);}
                }
                if( fos!=null ){ try {
                    fos.close();
                }catch (Exception e){AppLog.d("",e);}
                }
            }
            return false;
        }

        //清除本次的日志文件
        public void finish(){

            if( logname == null )
                return;

            File file = new File( logname );
            if( file.exists() ) {
                boolean b = file.delete();
                int index = 0;
                while( !b ){
                    b = file.delete();
                    index++;
                    if( index > 10 ){
                        break;
                    }
                }
            }
        }
    }

}
