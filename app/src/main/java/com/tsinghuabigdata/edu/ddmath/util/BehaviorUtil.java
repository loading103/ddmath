package com.tsinghuabigdata.edu.ddmath.util;

import android.content.Context;

import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.constant.AppRequestConst;
import com.tsinghuabigdata.edu.ddmath.constant.ErrTag;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * 用户行为上传
 * 依赖日志上传服务
 */
public class BehaviorUtil {

    private static final String VERSION = "1";
    private static final String URL_UPLOAD = AppRequestConst.BRHAVIOR_UPLOAD + VERSION;

    private static final String LOGPATH = "behavior/";

    /**
     * LOGIN 登入事件
     * LOGOUT 登出事件
     * ENTER_APP app到前台
     * QUIT_APP  app到后台
     */
    private static final String EVENT_LOGIN = "EVENT_LOGIN";
    //private static final String EVENT_LOGOUT = "EVENT_LOGOUT";
    //private static final String EVENT_ENTER = "EVENT_ENTER_APP";
    private static final String EVENT_QUIT = "EVENT_QUIT_APP";


    private Context mContext;
    //private String mSessionId;           //
    private String mDeviceId;
    //private String mAppName;

    //private String mLogName;                 //正常log放置的地方
    private String mUploadUrl;               //正常log放置的地方

    private String mLogPath;

    private static BehaviorUtil instance;

    //------------------------------------------------------------------------------

    public void config(Context context, String appname, String session, String domain) {

        this.mContext = context;
        //this.mSessionId = session;
        mDeviceId = AppUtils.getDeviceId(context);

        //this.mAppName = appname;

        this.mUploadUrl = domain + URL_UPLOAD;
        this.mUploadUrl = mUploadUrl.replace(":plat", appname);

        initDir();
        loadLocalTask();
    }

    //进入APP
//    public static void enterApp() {
//        if (instance != null && AccountUtils.getLoginUser()!= null ) {
//            instance.enter();
//        }
//    }

    //退出APP
//    public static void quitApp() {
//        if (instance != null && AccountUtils.getLoginUser()!= null) {
//            instance.quit();
//        }
//    }

    //进入APP
//    public static void loginApp() {
//        if (instance != null && AccountUtils.getLoginUser()!= null) {
//            instance.login();
//        }
//    }

    //退出APP
//    public static void logoutApp() {
//        if (instance != null && AccountUtils.getLoginUser()!= null) {
//            instance.logout();
//        }
//    }

    //事件上传
    public static void updateEventLog(String event, String extendJson) {
        if (instance != null && AccountUtils.getLoginUser()!= null) {
            instance.updateEvent(event, extendJson);
        }
    }

    public static BehaviorUtil instance() {
        if (instance == null) {
            instance = new BehaviorUtil();
        }
        return instance;
    }


    //----------------------------------------------------------------------------------------------
    //
    public void login() {
        BehaviorTask task = new BehaviorTask(EVENT_LOGIN);
        String json = getDeviceIdJson(mDeviceId).toString();
        task.setExtend(json);
        start(task);
    }

//    public void logout() {
//        BehaviorTask task = new BehaviorTask(EVENT_LOGOUT);
//        String json = getDeviceIdJson(mDeviceId).toString();
//        task.setExtend(json);
//        task.save();
//    }

//    public void enter() {
//        BehaviorTask task = new BehaviorTask(EVENT_ENTER);
//        String json = getDeviceIdJson(mDeviceId).toString();
//        task.setExtend(json);
//        start(task);
//    }

    public void quit() {
        BehaviorTask task = new BehaviorTask(EVENT_QUIT);
        String json = getDeviceIdJson(mDeviceId).toString();
        task.setExtend(json);
        start(task);
    }

    //点击流事件统计
    private void updateEvent(String event, String extentJson) {
        BehaviorTask task = new BehaviorTask(event);
        task.setExtend(extentJson);
        start(task);
    }

    private JSONObject getDeviceIdJson(String deviceid) {
        JSONObject json = new JSONObject();
        try {
            json.put("deviceId", deviceid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    //----------------------------------------------------------
    //隐藏构造函数
    private BehaviorUtil() {
    }

    //任务池
    private ArrayList<BehaviorTask> mBehaviorTaskList = new ArrayList<>();

    private static final int ST_IDLE = 0;
    private static final int ST_RUN = 1;
    private static final int ST_STOP = 2;

    private int run_status = ST_IDLE;

    //启动任务
    private void start(BehaviorTask task) {

        if(AppUtils.isDebug()) return;

        mBehaviorTaskList.add(task);

        if (run_status == ST_RUN) return;

        run_status = ST_RUN;
        startMainThread();
    }

    //上传主线程
    private void startMainThread() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {

                    if (mBehaviorTaskList.size() == 0)
                        break;

                    BehaviorTask task = mBehaviorTaskList.get(0);
                    if( task == null ){
                        mBehaviorTaskList.remove(0);
                        continue;
                    }

                    long timebak = task.interval;
                    if (task.interval != 0) {        //此值非空, 则表明上次失败了  ，要更 现在到事件发生经过的时间 单位：s
                        task.interval = (System.currentTimeMillis() - task.interval) / 1000;
                    }

                    boolean success = uploadfile(task);
                    if (!success) {
                        success = uploadfile(task);     //如果失败，重试一次
                    }

                    if (!success) {
                        //AppLog.d("Behavior task.interval = " +task.interval + ",,timebak = " + timebak );
                        if (task.interval != 0) {//非第一次，恢复为第一次提交的时间
                            task.interval = timebak;
                        } else {//第一次提交，保存出错，持久化
                            task.interval = System.currentTimeMillis();
                        }
                        //AppLog.d("Behavior task.interval = " +task.interval  );
                        task.save();
                    } else {
                        //检查本地未上传的操作，继续上传
                        loadLocalTask();
                    }
                    mBehaviorTaskList.remove(task);
                }
                run_status = ST_STOP;
            }
        }).start();
    }

    /*
     * 以表单形式完成一个任务数据上传提交
     */
    private boolean uploadfile(BehaviorTask task) {

        AppLog.i("Behavior", "Behavior upload data = " + task.getJson().toString());
        HttpURLConnection conn = null;
        try {
            URL realurl = new URL(mUploadUrl);
            conn = (HttpURLConnection) realurl.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setConnectTimeout(30 * 1000);//30秒超时
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Connection", "keep-alive");
            conn.connect();//打开链接

            PrintWriter pw = new PrintWriter(conn.getOutputStream());
            pw.print(task.getJson().toString());
            pw.flush();
            pw.close();

            if (conn.getResponseCode() == 200) {//成功
                AppLog.d("Behavior", "Behavior success");

                conn.disconnect();//关闭链接
                conn = null;
                return true;
            }
//        } catch (MalformedURLException eio) {
//            AppLog.d("Behavior", eio);
        }catch (Exception e) {
            AppLog.d("Behavior", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return false;
    }

    //目录初始化
    private void initDir() {

        if (mContext == null) return;

        File exfile = mContext.getExternalCacheDir();
        if (exfile == null) return;

        String path = exfile.toString();

        if (!path.endsWith("/")) {
            path += "/";
        }
        path += LOGPATH;

        File file = new File(path);
        file.mkdirs();

        mLogPath = path;

    }

    /*
     * 扫描本地上传日志文件，依次添加入上传任务
     */
    private void loadLocalTask() {

        if (mLogPath == null)
            return;

        File file = new File(mLogPath);

        if (!file.exists() || !file.isDirectory())
            return;

        String list[] = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.startsWith("behav_");
            }
        });

        if (list == null)
            return;

        for (String name : list) {
            BehaviorTask task = new BehaviorTask();
            task.load(mLogPath + name);
            start(task);
        }

    }


    //--------------------------------------------------------------------------
    private class BehaviorTask implements Serializable {

        private static final long serialVersionUID = 751798067368192675L;

        String sessionId = "";
        String eventName = "";
        String eventContent = "";
        String agent = "";
        String extend = "";
        long interval = 0L;

        BehaviorTask() {
        }

        BehaviorTask(String eventName) {
            String tokenid = "";
            LoginInfo loginInfo = AccountUtils.getLoginUser();
            if (loginInfo != null) {
                tokenid = loginInfo.getAccessToken();
            }
            this.sessionId = tokenid;
            this.eventName = eventName;
        }

        //持久化到文件
        public void save() {
            ObjectOutputStream oos = null;
            try {
                String fname = mLogPath + "behav_" + interval + ".log";
                oos = new ObjectOutputStream(new FileOutputStream(new File(fname)));
                oos.writeObject(sessionId);
                oos.writeObject(eventName);
                oos.writeObject(eventContent);
                oos.writeObject(agent);
                oos.writeObject(extend);
                oos.writeLong(interval);
                oos.flush();
                oos.close();
            } catch (Exception e) {
                AppLog.d("", e);
            }finally {
                if( oos!=null ){
                    try {
                        oos.close();
                    }catch (Exception e){ AppLog.i("",e);}
                }
            }
        }

        //文件到对象
        public void load(String filename) {

            File file = new File(filename);
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(new FileInputStream(file));
                sessionId = (String) ois.readObject();
                eventName = (String) ois.readObject();
                eventContent = (String) ois.readObject();
                agent = (String) ois.readObject();
                extend = (String) ois.readObject();
                interval = ois.readLong();

            } catch (Exception e) {
                AppLog.d("", e);
            } finally {
                if( ois!=null ){
                    try {
                        ois.close();
                    }catch (Exception e){ AppLog.i("",e);}
                }
                file.delete();
            }
        }

        public JSONObject getJson() {

            JSONObject json = new JSONObject();
            try {
                json.put("sessionId", sessionId);
                json.put("eventName", eventName);
                json.put("eventContent", eventContent);
                json.put("agent", agent);
                json.put("extend", extend);
                json.put("interval", interval);
            } catch (Exception e) {
                AppLog.d(ErrTag.TAG_JSON, e);
            }
            return json;
        }

        void setExtend(String extend) {
            this.extend = extend;
        }
    }

}

