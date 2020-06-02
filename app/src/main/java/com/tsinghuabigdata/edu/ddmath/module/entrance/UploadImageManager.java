package com.tsinghuabigdata.edu.ddmath.module.entrance;

import android.content.Context;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppAsyncTask;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.ModifyListener;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.UploadImageListener;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.UploadListener;
import com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl.EntranceEvaluateImpl;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;

import java.util.ArrayList;


/**
 * 入学评测 图片上传管理器
 */
public class UploadImageManager {

    //单例模式
    private static UploadImageManager mUploadManager;

    private UploadListener mUploadListener;
    private ModifyListener mModifyListener;
    private SubmitQuestionTask mSubmitQuestionTask;

    private Context mContext;
    private String classId;

    /**
     * 用户本地作业图片管理
     * @param context  上下文
     * @return dd
     */
    public static UploadImageManager getUploadImageManager(Context context, String classId ){
        //if( mUploadManager == null ){
            mUploadManager = new UploadImageManager( context, classId );
        //}
        return mUploadManager;
    }
    //public static UploadImageManager getUploadImageManager(){ return mUploadManager; }

//    public void stopAllUploadTask(){
//        Iterator iter = uploadHashMap.entrySet().iterator();
//        while (iter.hasNext()) {
//            Map.Entry entry = (Map.Entry) iter.next();
//            //Object key = entry.getKey();
//            UplaodTask uplaodTask = (UplaodTask)entry.getValue();
//            uplaodTask.stopUploadData();
//        }
//        uploadHashMap.clear();
//
//        //保存列表到本地
//        LocalImageManager LocalImageManager = LocalImageManager.getLocalImageManager();
//        if( LocalImageManager != null ) LocalImageManager.saveData();
//
//    }

    public void setUploadListener( UploadListener listener, ModifyListener listener1 ){
        mUploadListener = listener;
        mModifyListener = listener1;
        //uplaodTask.setUploadListener( listener );
    }

//    public void removeAllListener(){
//        //uplaodTask.setUploadListener( null );
//    }

    //------------------------------------------------------------------------------
    //隐藏构造函数
    private UploadImageManager(Context context, String id ){
        mContext = context;
        classId  = id;
    }

    //---------------------------------------------------------------------------------


    /**
     * 上传先检查图片是否上传完成，再上传信息
     */
    public void startUploadData(){
        //mWaitWorkBean.setUploadStatus( WaitWorkBean.ST_UPLOADING );
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
            AppLog.d("dfdfdfd status = " + status );
            if( mUploadListener!=null ){
                mUploadListener.onStatusChange();
            }
        }
    }

    class SubmitQuestionTask extends AppAsyncTask<String, Void, Boolean> {

        private LocalImageManager localImageManager;
        public SubmitQuestionTask(){
            localImageManager = LocalImageManager.getLocalImageManager();
        }
        @Override
        protected Boolean doExecute(String... params) throws Exception {

            UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
            if( detailinfo == null || localImageManager == null ){
                throw new Exception("请登录");
            }

            String studentId   = detailinfo.getStudentId();

            //0, 先注册监听事件
            localImageManager.addUploadListener( new UploadImageListenerImpl() );

            //状态变化
            localImageManager.markupLastUploadPage();
            localImageManager.noticeStatusChange();

            //1，启动图片上传
            localImageManager.startUpload();

            //2，检测图片是否上传完成
            int index = 0;
            int count = localImageManager.getImageCount() * 1000;       //超时最大100s，一个最多需要检测1000次
            while( count!=0 ){
                SystemClock.sleep(100);
                index++;

                //先判断是否都上传成功
                if( localImageManager.uploadSuccess() )
                    break;

                //失败或者成功，上传动作完成
                if( localImageManager.isFinishUpload() ){
                    throw new Exception("图片上传失败");
                }

                //超过时间，失败提示 300s
                if( index > count ){
                    throw new Exception("图片上传失败");
                }
            }

            //3，上传作业信息
            EntranceEvaluateImpl service = new EntranceEvaluateImpl();
            ArrayList<String> data = localImageManager.getUploadData();

            //重试几次
            for( index=0; index<3;index++ ){
                try {
                    String workId = service.saveKnowledgeEvaluate( studentId, classId, data );
                    if( !TextUtils.isEmpty(workId) ){
                        //格式: { "enterId":"123123123123", "modify":false }
                        JSONObject json = JSON.parseObject( workId );
                        if( mModifyListener!=null )
                            mModifyListener.onModify( json.getBoolean("modify") );
                        return true;
                    }
                }catch (Exception e){
                    AppLog.i("", e );
                }
            }

            //失败
            throw new Exception("图片数据上传失败");
        }

        @Override
        protected void onResult(Boolean success) {
            //mWaitWorkBean.setUploadStatus( success?WaitWorkBean.ST_UPLOADED:WaitWorkBean.ST_UPLOADFAIL );
            if(mUploadListener!=null){
                if( success && localImageManager!=null )
                    localImageManager.clearMarkupLastUploadPage();
                mUploadListener.onSuccess( success );
//            }else{ //非待上传页面
//                LocalImageManager LocalImageManager = com.tsinghuabigdata.edu.ddmath.module.entrance.LocalImageManager.getLocalImageManager()
//                if( LocalImageManager != null ){
//                    ArrayList<WaitWorkBean> list = LocalImageManager.getWaitWorkList();
//                    for( int index=list.size()-1; index>=0; index-- ){
//                        WaitWorkBean workBean = list.get( index );
//                        if( workBean.getUploadStatus() == WaitWorkBean.ST_UPLOADED ){
//                            list.remove( workBean );
//                        }
//                    }
//                }
//                //弹出上传成功提示框
//                ToastUtils.showToastUploadResult( ZxApplication.getApplication(), true );
//
//                mContext.sendBroadcast( new Intent(AppConst.ACTION_UPLOAD_SUCCESS) );
            }

            //移除监听
            if( localImageManager!=null ) {
                localImageManager.addUploadListener(null);

                if( !success ){
                    localImageManager.saveData();
                }
            }
        }

        @Override
        protected void onFailure(HttpResponse<Boolean> response, Exception ex) {
            //mWaitWorkBean.setUploadStatus( WaitWorkBean.ST_UPLOADFAIL );
            if(mUploadListener!=null){
                mUploadListener.onFail( response, ex );

                //ToastUtils.showToastCenter( ZxApplication.getApplication(), ZxApplication.getApplication().getResources().getString(R.string.upload_failure_tips) );
//            }else{ //非待上传页面
//                //弹出上传成功提示框
//                ToastUtils.showToastUploadResult( ZxApplication.getApplication(), false );
//
//                //删除上传成功的任务
//                LocalImageManager LocalImageManager = LocalImageManager.getLocalImageManager();
//                if( LocalImageManager != null ) LocalImageManager.removeSuccessBean();
            }

            //移除监听
            if( localImageManager!=null ) {
                localImageManager.addUploadListener(null);

                localImageManager.saveData();
            }
        }
    }



}
