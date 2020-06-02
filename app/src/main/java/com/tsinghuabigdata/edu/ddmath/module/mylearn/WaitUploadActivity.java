package com.tsinghuabigdata.edu.ddmath.module.mylearn;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.event.UploadSchoolWorkEvent;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.adapter.WaitWorkAdapter;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.bean.UploadImage;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.bean.WaitWorkBean;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * 待上传图册
 */
@Deprecated
public class WaitUploadActivity extends RoboActivity implements UploadListener{

    private ListView mListView;
    private WorkToolbar workToolbar;
    private WaitWorkAdapter mWaitWorkAdapter;
    private LocalWorkManager mLocalWorkManager;

    private Context mContext;
    private Activity mActivity;

    @Override
    protected void onCreate( Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(GlobalData.isPad()?R.layout.activity_scwork_waitupload:R.layout.activity_scwork_waitupload_phone);

        mContext = this;
        mActivity = this;

        initView();
        if( !loadData() ){
            ToastUtils.showShort( mContext, "参数错误" );
            finish();
        }

        startAddImageReceiver();
    }

//    @Override
    public void onLeftClick(){
       quit();
    }

    @Override
    public void onBackPressed(){
        quit();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        //停止所有的上传监听
        stopUploadListener();

        //退出时，如果有未上传的，则去掉添加图片的bean
        removeAddImageBean();

        //
        stopAddImageReceiver();

        //退出时 释放图片
        destoryCacheImage();

        //保存数据列表
        LocalWorkManager localWorkManager = LocalWorkManager.getLocalWorkManager();
        if( localWorkManager != null ) localWorkManager.saveData();
    }

    @Override
    public void onSuccess(Object res) {
        updateWaitWorkStatus();
        //ZxApplication.setUploadonSuccess(true);
        //2秒后 删除上传成功的作业
        mHandler.sendEmptyMessageDelayed( MSG_DELSUCCESS_ITEM, 2000 );
    }

    @Override
    public void onFail(HttpResponse response, Exception ex) {
        updateWaitWorkStatus();

        if( ex.getMessage()!=null && ex.getMessage().contains("网络") ){
            ToastUtils.showToastCenter( mContext, getResources().getString( R.string.upload_failure_tips) );
        }else{
            ToastUtils.showToastCenter( mContext, getResources().getString( R.string.upload_failure_tips2) );
        }
    }

    @Override
    public void onStatusChange(){
    }

    private void updateWaitWorkStatus(){
        mHandler.sendEmptyMessage( MSG_UPDATE_ITEM );
    }

    private static final int MSG_UPDATE_ITEM = 1;
    private static final int MSG_DELSUCCESS_ITEM = 2;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case MSG_UPDATE_ITEM:{
                    //检查是否存在上传中的
                    mLocalWorkManager.updataUploadingWorkStatus();
                    mWaitWorkAdapter.setHasUploading( mLocalWorkManager.checkInUploading() );
                    mWaitWorkAdapter.notifyDataSetChanged();
                    break;
                }
                case MSG_DELSUCCESS_ITEM:{

                    ArrayList<WaitWorkBean> list = mLocalWorkManager.getWaitWorkList();
                    for( int index=list.size()-1; index>=0; index-- ){
                        WaitWorkBean workBean = list.get( index );
                        if( workBean.getUploadStatus() == WaitWorkBean.ST_UPLOADED ){
                            list.remove( workBean );
                            mWaitWorkAdapter.remove( workBean );

                            //释放图片
                            for( UploadImage image : workBean.getImageList() ){
                                image.destroyBitmap();
                            }
                        }
                    }
                    EventBus.getDefault().post(new UploadSchoolWorkEvent());
                    mWaitWorkAdapter.notifyDataSetChanged();

                    if( list.size() == 0 ){
                        //自动退出此界面
                        finish();
                    }else{
                        String title = "待上传图册（" + list.size() + "）";
                        workToolbar.setTitle( title );
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };
    //----------------------------------------------------------------------

    private void quit(){

        if( mLocalWorkManager.checkInEditMode() ){
            AlertManager.showCustomDialog( mContext, getResources().getString(R.string.editing_noquit), "知道了", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            } );
        }else{
            finish();
        }
    }

    private void initView(){

        workToolbar = findViewById(R.id.work_toolbar);
        workToolbar.setTitle("待上传图册");
        workToolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        },null);

        mListView = findViewById( R.id.mylearn_waitupload_list );
        mWaitWorkAdapter = new WaitWorkAdapter( mActivity, 0 );
        mWaitWorkAdapter.setParentListView( mListView );

        mListView.setAdapter( mWaitWorkAdapter );

        View rootView = LayoutInflater.from(this).inflate( R.layout.view_list_footer, null, false );
        mListView.addFooterView( rootView );

        UploadManager manager = UploadManager.getUploadManager( mContext );
        manager.setUploadListener( this );
    }

    //加载的是本地保存的数据
    private boolean loadData(){

        mLocalWorkManager = LocalWorkManager.getLocalWorkManager();
        if( mLocalWorkManager == null ){
            LoginInfo loginInfo = AccountUtils.getLoginUser();
            if( loginInfo == null || TextUtils.isEmpty(loginInfo.getLoginName()) ){
                ToastUtils.showShort( getBaseContext(), "用户信息错误");
                return false;
            }
            mLocalWorkManager = LocalWorkManager.getLocalWorkManager( mContext, loginInfo.getLoginName());
        }

        ArrayList<WaitWorkBean> list = new ArrayList<>();
        list.addAll( mLocalWorkManager.getWaitWorkList() );

        //如果是未上传状态，且图片小于12张， 增加添加按钮
        for( WaitWorkBean workBean : list ){
            if( workBean.getUploadStatus() == WaitWorkBean.ST_UNUPLOAD && workBean.getImageCount() < AppConst.MAX_WORK_IMAGE ){
                workBean.addAddTypeImage();
            }else{
                workBean.removeAddTypeImage();
            }
        }

        //调整未上传作业的顺序
//        ArrayList<WaitWorkBean> unlist = new ArrayList<>();
//        for( WaitWorkBean workBean : list ){
//            if( workBean.getUploadStatus() == WaitWorkBean.ST_UNUPLOAD ){
//                unlist.add( workBean );
//            }
//        }
//
//        if( unlist.size() > 0 ){
//            list.removeAll( unlist );
//            list.addAll( 0, unlist );
//        }

        //检查是否存在上传中的
        mWaitWorkAdapter.setHasUploading( mLocalWorkManager.checkInUploading() );
        //
        mWaitWorkAdapter.addAll( list );
        mWaitWorkAdapter.notifyDataSetChanged();

        String title = "待上传图册（" + list.size() + "）";
        workToolbar.setTitle( title );
        return true;
    }

    //去掉未未上传状态下的 添加bean 信息
    private void removeAddImageBean(){

        ArrayList<WaitWorkBean> list = mLocalWorkManager.getWaitWorkList();

        //如果是未上传状态，且图片小于12张， 增加添加按钮
        for( int i=list.size()-1; i>=0; i-- ){
            WaitWorkBean workBean = list.get( i );
            if( workBean.getUploadStatus() == WaitWorkBean.ST_UNUPLOAD ){
                for( UploadImage imageBean : workBean.getImageList() ){
                    if( imageBean.getImagetype() == UploadImage.TYPE_ADDIMAGE ){
                        workBean.getImageList().remove( imageBean );
                        break;
                    }
                }
            }
            if( workBean.getImageList().size() == 0 )
                list.remove( workBean );
        }
        //sendBroadcast( new Intent(AppConst.ACTION_UPLOAD_SUCCESS) );

        AppLog.d("fdfdfdfdsf sss = " + list.size() );
    }

    private void destoryCacheImage(){
        ArrayList<WaitWorkBean> list = mLocalWorkManager.getWaitWorkList();
        for( WaitWorkBean workBean : list ){
            for( UploadImage imageBean : workBean.getImageList() ){
                imageBean.destroyBitmap();
            }
        }
    }

    private void stopUploadListener(){
        UploadManager uploadManager = UploadManager.getUploadManager( mContext );
        uploadManager.removeAllListener();
    }

    //--------------------------------------------------------------------------------------------------------
    private AddImageReceiver addImageReceiver;
    private void startAddImageReceiver(){
        addImageReceiver = new AddImageReceiver();
        IntentFilter intentFilter = new IntentFilter( AppConst.ACTION_SCWORK_ADD );
        registerReceiver(addImageReceiver, intentFilter);
        intentFilter = new IntentFilter( AppConst.ACTION_SCWORK_EDIT );
        registerReceiver(addImageReceiver, intentFilter);
    }
    private void stopAddImageReceiver(){
        unregisterReceiver(addImageReceiver);
    }

    /**
     * 监听添加图片
     */
    class AddImageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 监听消息
            if ( AppConst.ACTION_SCWORK_ADD.equals( intent.getAction() ) ){
                mWaitWorkAdapter.notifyDataSetChanged();
            }
        }
    }

}
