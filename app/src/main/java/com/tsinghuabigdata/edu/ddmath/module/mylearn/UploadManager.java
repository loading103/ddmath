package com.tsinghuabigdata.edu.ddmath.module.mylearn;

import android.content.Context;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.text.TextUtils;

import com.tsinghuabigdata.edu.ddmath.ZxApplication;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.MyTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppAsyncTask;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.event.UploadSchoolWorkEvent;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.bean.WaitWorkBean;
import com.tsinghuabigdata.edu.ddmath.requestHandler.MyLearnService;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 上传管理器
 */
public class UploadManager {

    //单例模式
    private static UploadManager mUploadManager;

    private HashMap<WaitWorkBean, UplaodTask> uploadHashMap = new HashMap<>();

    private UploadListener mUploadListener;
    private Context mContext;


    /**
     * 用户本地作业图片管理
     * @param context  上下文
     * @return dd
     */
    public static UploadManager getUploadManager( Context context ){
        if( mUploadManager == null ){
            mUploadManager = new UploadManager( context );
        }
        return mUploadManager;
    }
    public static UploadManager getUploadManager(){ return mUploadManager; }


    public void addUploadTask( WaitWorkBean bean ){

        UplaodTask uplaodTask;
        if( uploadHashMap.containsKey(bean) ){
            //如果已经存在，更新lister
            uplaodTask = uploadHashMap.get(bean);
            uplaodTask.setUploadListener( mUploadListener );
        }else{
            uplaodTask = new UplaodTask(bean,mUploadListener);
            uploadHashMap.put( bean, uplaodTask );
        }
        uplaodTask.startUploadData();

    }

    public void stopAllUploadTask(){
        Iterator iter = uploadHashMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            //Object key = entry.getKey();
            UplaodTask uplaodTask = (UplaodTask)entry.getValue();
            uplaodTask.stopUploadData();
        }
        uploadHashMap.clear();

        //保存列表到本地
        LocalWorkManager localWorkManager = LocalWorkManager.getLocalWorkManager();
        if( localWorkManager != null ) localWorkManager.saveData();

    }

    public void setUploadListener( UploadListener listener ){
        mUploadListener = listener;
        Iterator iter = uploadHashMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            //Object key = entry.getKey();
            UplaodTask uplaodTask = (UplaodTask)entry.getValue();
            uplaodTask.setUploadListener( listener );
        }
    }

    public void removeAllListener(){
        Iterator iter = uploadHashMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            //Object key = entry.getKey();
            UplaodTask uplaodTask = (UplaodTask)entry.getValue();
            uplaodTask.setUploadListener( null );
        }
    }

    //------------------------------------------------------------------------------
    //隐藏构造函数
    private UploadManager( Context context ){
        mContext = context;
    }

    //-------------------------------------------------------------------------------

    class UplaodTask {

        private WaitWorkBean mWaitWorkBean;
        private UploadListener mUploadListener;
        private SubmitQuestionTask mSubmitQuestionTask;

        public UplaodTask( WaitWorkBean bean, UploadListener listener ){
            mWaitWorkBean = bean;
            mUploadListener = listener;
        }

        public void setUploadListener(UploadListener listener){
            mUploadListener = listener;
        }

        /**
         * 上传先检查图片是否上传完成，再上传信息
         */
        public void startUploadData(){
            mWaitWorkBean.setUploadStatus( WaitWorkBean.ST_UPLOADING );
            if( mSubmitQuestionTask==null || mSubmitQuestionTask.isCancelled() || mSubmitQuestionTask.isComplete() ){
                mSubmitQuestionTask = new SubmitQuestionTask();
                mSubmitQuestionTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }

        //
        public void stopUploadData(){
            if( mSubmitQuestionTask!=null  ){
                mSubmitQuestionTask.cancel( true );
            }
        }

        class UploadImageListenerImpl implements UploadImageListener{
            @Override
            public void updateStatus(int status) {
                if( mUploadListener!=null ){
                    mUploadListener.onSuccess( true );
                }
            }
        }

        class SubmitQuestionTask extends AppAsyncTask<String, Void, Boolean> {

            public SubmitQuestionTask(){
            }
            @Override
            protected Boolean doExecute(String... params) throws Exception {

                LoginInfo loginInfo = AccountUtils.getLoginUser();
                UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
                if( loginInfo == null || !AccountUtils.hasClass() ){
                    throw new Exception("请登录");
                }
                MyLearnService service = ZxApplication.getLearnService();
                if( service==null ){
                    throw new Exception("没有启动 LearnService");
                }
                MyTutorClassInfo currentClassInfo = AccountUtils.getCurrentClassInfo();
                String accessToken = loginInfo.getAccessToken();
                String studentId   = detailinfo.getStudentId();
                String classId     = mWaitWorkBean.getClassId();

                //0, 先注册监听事件
                mWaitWorkBean.addUploadListener( new UploadImageListenerImpl() );

                mWaitWorkBean.markupLastUploadPage();
                //1，启动图片上传
                mWaitWorkBean.startUpload();

                //2，检测图片是否上传完成
                int index = 0;
                int count = mWaitWorkBean.getImageCount() * 1000;       //超时最大100s，一个最多需要检测1000次
                while( true ){
                    SystemClock.sleep(100);
                    index++;

                    //先判断是否都上传成功
                    if( mWaitWorkBean.canUpload() )
                        break;

                    //失败或者成功，上传动作完成
                    if( mWaitWorkBean.isFinishUpload() ){
                        throw new Exception("图片上传失败");
                    }

                    //超过时间，失败提示 300s
                    if( index > count ){
                        throw new Exception("图片上传失败");
                    }
                }

                //3，上传作业信息
                JSONArray jsonArray= mWaitWorkBean.getUploadImageJsonArray();

                //重试几次
                for( index=0; index<3;index++ ){
                    try {
                        String workId = service.submitWorkInfo( accessToken, studentId, classId, mWaitWorkBean.getEstimateCount(),  jsonArray );
                        if( !TextUtils.isEmpty(workId) )
                            return true;
                    }catch (Exception e){
                        AppLog.i("", e );
                    }
                }

                //失败
                throw new Exception("图片数据上传失败");
            }

            @Override
            protected void onResult(Boolean success) {
                mWaitWorkBean.setUploadStatus( success?WaitWorkBean.ST_UPLOADED:WaitWorkBean.ST_UPLOADFAIL );
                if(mUploadListener!=null){
                    mUploadListener.onSuccess( success );
                    if( success && mWaitWorkBean!=null )
                        mWaitWorkBean.clearMarkupLastUploadPage();
                }else{ //非待上传页面

                    LocalWorkManager localWorkManager = LocalWorkManager.getLocalWorkManager();
                    if( localWorkManager != null ){
                        ArrayList<WaitWorkBean> list = localWorkManager.getWaitWorkList();
                        for( int index=list.size()-1; index>=0; index-- ){
                            WaitWorkBean workBean = list.get( index );
                            if( workBean.getUploadStatus() == WaitWorkBean.ST_UPLOADED ){
                                list.remove( workBean );
                            }
                        }
                    }
                    //弹出上传成功提示框
                    ToastUtils.showToastUploadResult( ZxApplication.getApplication(), true );
                    EventBus.getDefault().post(new UploadSchoolWorkEvent());
                }

                //移除监听
                mWaitWorkBean.removeUploadListener();

                //
                LocalWorkManager localWorkManager = LocalWorkManager.getLocalWorkManager();
                if( localWorkManager != null ) localWorkManager.saveData();
            }

            @Override
            protected void onFailure(HttpResponse<Boolean> response, Exception ex) {
                mWaitWorkBean.setUploadStatus( WaitWorkBean.ST_UPLOADFAIL );
                if(mUploadListener!=null){
                    mUploadListener.onFail( response, ex );

                    //ToastUtils.showToastCenter( ZxApplication.getApplication(), ZxApplication.getApplication().getResources().getString(R.string.upload_failure_tips) );
                }else{ //非待上传页面
                    //弹出上传成功提示框
                    ToastUtils.showToastUploadResult( ZxApplication.getApplication(), false );

                    //删除上传成功的任务
                    LocalWorkManager localWorkManager = LocalWorkManager.getLocalWorkManager();
                    if( localWorkManager != null ) localWorkManager.removeSuccessBean();
                }

                //移除监听
                mWaitWorkBean.removeUploadListener();

                //
                LocalWorkManager localWorkManager = LocalWorkManager.getLocalWorkManager();
                if( localWorkManager != null ) localWorkManager.saveData();
            }
        }

    }

}
