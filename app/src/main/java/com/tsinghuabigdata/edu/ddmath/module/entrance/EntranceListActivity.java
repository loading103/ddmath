package com.tsinghuabigdata.edu.ddmath.module.entrance;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.ZxApplication;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppAsyncTask;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.module.entrance.adapter.EntranceImageAdapter;
import com.tsinghuabigdata.edu.ddmath.module.entrance.adapter.EntranceWaitImageAdapter;
import com.tsinghuabigdata.edu.ddmath.module.entrance.bean.KnowledgeRecordBean;
import com.tsinghuabigdata.edu.ddmath.module.entrance.bean.UploadStatusBean;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.ModifyListener;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.UploadListener;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.bean.UploadImage;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.bean.WaitWorkBean;
import com.tsinghuabigdata.edu.ddmath.module.neteaseim.base.util.NetworkUtil;
import com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl.EntranceEvaluateImpl;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.util.WindowUtils;
import com.tsinghuabigdata.edu.ddmath.view.DragGridView;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;
import com.tsinghuabigdata.edu.ddmath.view.NetworkStatusView;
import com.tsinghuabigdata.edu.utils.MD5Util;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

/**
 * 入学评测待上传 列表
 */
@Deprecated
public class EntranceListActivity extends RoboActivity implements UploadListener<Boolean>, RefreshImageListener, View.OnClickListener, ModifyListener {

    //输入参数
    public final static String PARAM_EVAL_STATUS = "evaluatestatus";//0:新建 1：查看并可编辑 2:查看并不可编辑
    public final static String PARAM_WAIT_CLASSID= "classid";
    public final static String PARAM_UPLOAD_STATUS = "uploadstatus";
    public final static String PARAM_MODIFY_FLAG = "modifyflag";

    public final static int ST_NEW = 0;
    public final static int ST_EDIT = 1;
    public final static int ST_VIEW = 2;


    //输出参数
    public final static String PARAM_SUBMIT_STATUS = "submit";
    public final static String PARAM_SUBMIT_MODIFY = "modify";

    private LocalImageManager localImageManager;
    private UploadImageManager uploadImageManager;

    private EntranceWaitImageAdapter mWaitImageAdapter;
    private EntranceImageAdapter mImageAdapter;

    private UploadStatusBean uploadStatusBean;

    private Context mContext;
    private Activity mActivity;

    //Intent 参数
    private int evaluateStatus = ST_NEW;     //0: 新建 1:查看，可编辑 2：不可编辑
    private String classId;

    //上传结果
    private boolean bUploadCurrentStatus = false;       //本次上传状态
    private boolean bUploadStatus = false;
    private boolean bUploadModify = false;

    //解决查看 可编辑状态下，用户改变的标志
    private ListDataStatus listDataStatus;

    //
    private GetEvalRecordTask mGetEvalRecordTask;

    @ViewInject(R.id.work_toolbar)
    private WorkToolbar workToolbar;
    //View
    @ViewInject(R.id.entrance_main_scrollview)
    private ScrollView   mainScrollView;
    @ViewInject(R.id.loadingPager)
    private LoadingPager mLoadingPager;

    @ViewInject(R.id.mylearn_waitwork_mainlayout)
    private LinearLayout mainLayout;

    @ViewInject(R.id.mylearn_waitwork_uploadstatus)
    private TextView uploadStatusView;    //上传状态
    @ViewInject(R.id.mylearn_waitwork_estimateCount)
    private TextView estimateCountView;       //预估数量

    @ViewInject(R.id.mylearn_waitwork_unuploadlayout)
    private LinearLayout unUploadLayout;   //未上传
    @ViewInject(R.id.mylearn_waitwork_editbtn)
    private TextView editBtnView;          //编辑按钮
    @ViewInject(R.id.mylearn_waitwork_uploadbtn)
    private TextView uploadBtnView;        //

    @ViewInject(R.id.mylearn_waitwork_uploadfaillayout)
    private LinearLayout uploadFailLayout;   //上传失败
    @ViewInject(R.id.mylearn_waitwork_deletebtn)
    private TextView deleteBtnView;          //全部删除按钮
    @ViewInject(R.id.mylearn_waitwork_reuploadbtn)
    private TextView reUploadBtnView;        //重新上传

    @ViewInject(R.id.mylearn_waitwork_editlayout)
    private LinearLayout editLayout;        //编辑模式
    @ViewInject(R.id.mylearn_waitwork_finishbtn)
    private TextView finishBtnView;          //
    @ViewInject(R.id.mylearn_waitwork_editTipsLayout)
    private RelativeLayout editTipsLayout;         //编辑提示
    //@ViewInject(R.id.mylearn_waitwork_uploadsucclayout)
    //private RelativeLayout uploadSuccLayout;       //上传成功提示
    @ViewInject(R.id.mylearn_waitwork_uploadinglayout)
    private LinearLayout uploadingLayout;           //上传中
    @ViewInject(R.id.mylearn_waitwork_networkstatusview)
    private NetworkStatusView networkStatusView;    //网络状态

    @ViewInject(R.id.mylearn_waitwork_gridview)
    public DragGridView gridView;

    @Override
    protected void onCreate( Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(GlobalData.isPad()?R.layout.activity_entrance_list:R.layout.activity_entrance_list_phone);

        x.view().inject( this );

        mContext = this;
        mActivity = this;

        parseIntent();

        initView();

        startAddImageReceiver();

        if( !loadData() ){
            ToastUtils.showShort( mContext, "参数错误" );
            finishQuit();
        }
    }

//    @Override
//    public void onLeftClick(){
//       quit();
//    }

    @Override
    public void onBackPressed(){
        quit();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        //
        stopAddImageReceiver();

        if( evaluateStatus == ST_NEW || evaluateStatus == ST_EDIT ){
            LocalImageManager localImageManager = LocalImageManager.getLocalImageManager();
            if( localImageManager==null ) return;
            if( bUploadStatus ){
                localImageManager.clearData();
            }else{
                //保存数据列表
                localImageManager.removeAddTypeImage();
                localImageManager.saveData();
                localImageManager.addUploadListener( null );
            }
        }
    }

    @Override
    public void onSuccess(Boolean res) {
        bUploadStatus = bUploadStatus||res;
        bUploadCurrentStatus = res;
        if( res ){
            ToastUtils.showToastCenter( mContext, "上传成功");
        }else{
            ToastUtils.showToastCenter( mContext, "上传失败");
        }
        uploadStatusBean.setUploadStatus( UploadStatusBean.ST_UPLOADED );
        updateItemStatus();
        updateUploadInfo();
    }

    @Override
    public void onFail(HttpResponse response, Exception ex) {

        uploadStatusBean.setUploadStatus( UploadStatusBean.ST_UPLOADFAIL );
        updateItemStatus();
        updateUploadInfo();

        if( ex.getMessage()!=null && ex.getMessage().contains("网络") ){
            ToastUtils.showToastCenter( mContext, getResources().getString( R.string.upload_failure_tips) );
        }else{
            ToastUtils.showToastCenter( mContext, getResources().getString( R.string.upload_server_norun) );
        }
    }

    @Override
    public void onStatusChange(){
        if( localImageManager!=null )localImageManager.markupLastUploadPage();
        updateItemStatus();
    }

    @Override
    public void onRefresh() {
        updateItemStatus();
        updateUploadInfo();
    }

    @Override
    public void addAddTypeImage() {
        if( localImageManager!=null ){
            localImageManager.addAddTypeImage();
            updateItemStatus();
        }
    }

    @Override
    public void removeAddTypeImage() {
        if( localImageManager!=null ){
            localImageManager.removeAddTypeImage();
            updateItemStatus();
        }
    }

    @Override
    public void setEditMode(boolean editMode) {
        uploadStatusBean.setEditMode( editMode );
        if( localImageManager!=null ) localImageManager.setEditMode( editMode );
        gridView.setCanDrag( editMode );
        mWaitImageAdapter.notifyDataSetChanged();

        updateUploadInfo();
        updateItemStatus();
    }

    @Override
    public void onClick(View v) {

        if( v.getId() == R.id.mylearn_waitwork_editbtn ){   //编辑

            //改变状态
            uploadStatusBean.setUploadStatus( WaitWorkBean.ST_UPLOADEDIT );
            setEditMode( true );
            gridView.setCanDrag( true );
            removeAddTypeImage();      //删除 add action bean

        }else if( v.getId() == R.id.mylearn_waitwork_uploadbtn ){   //上传

            removeAddTypeImage();      //删除 add action bean
            //notifyDataSetChanged();

            //网络检测
            if( !NetworkUtil.isNetAvailable( ZxApplication.getApplication() ) ){
                ToastUtils.showToastCenter( mContext, mContext.getResources().getString( R.string.upload_failure_tips) );
                uploadStatusBean.setUploadStatus( WaitWorkBean.ST_UPLOADFAIL );

            }else{
                //启动上传
                uploadStatusBean.setUploadStatus( WaitWorkBean.ST_UPLOADING );
                uploadImageManager.startUploadData();

                //网络状态
                uploadingLayout.setVisibility( View.GONE );
                //networkStatusView.start( mActivity );
            }

        }else if( v.getId() == R.id.mylearn_waitwork_deletebtn ){   //全部删除

            AlertManager.showCustomDialog( mContext, mContext.getString(R.string.delete_allimage), "全部删除", "取消", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    //如何通知服务器相关图片被删除
                    localImageManager.clearData();
                    localImageManager.addAddTypeImage();
                    uploadStatusBean.setUploadStatus( WaitWorkBean.ST_UNUPLOAD );

                    updateItemStatus();
                    updateUploadInfo();
                    onUserChange();
                }
            },new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

        }else if( v.getId() == R.id.mylearn_waitwork_reuploadbtn ){   //重新上传

            //网络检测
            if( !NetworkUtil.isNetAvailable( ZxApplication.getApplication() ) ){
                ToastUtils.showToastCenter( mContext, mContext.getResources().getString( R.string.upload_failure_tips) );
                return;
            }
            //
            uploadStatusBean.setUploadStatus( WaitWorkBean.ST_UPLOADING );

            //启动上传
            uploadImageManager.startUploadData();

            //网络状态
            uploadingLayout.setVisibility( View.GONE );
            //networkStatusView.start( mActivity );

            //updateView();

        }else if( v.getId() == R.id.mylearn_waitwork_finishbtn ){       //完成编辑
            uploadStatusBean.setUploadStatus( WaitWorkBean.ST_UNUPLOAD );
            setEditMode( false );
            gridView.setCanDrag( false );
            addAddTypeImage();
            onUserChange();
        }

        updateUploadInfo();
        updateItemStatus();
    }


    @Override
    public void onModify( boolean modify ) {
        bUploadModify = bUploadModify||modify;
    }


    //------------------------------------------------------------------------------------------------------------------------

    private void parseIntent(){

        Intent intent = getIntent();
        evaluateStatus = intent.getIntExtra( PARAM_EVAL_STATUS, ST_NEW);
        classId        = intent.getStringExtra( PARAM_WAIT_CLASSID );
        bUploadStatus  = intent.getBooleanExtra( PARAM_UPLOAD_STATUS, false );
        bUploadModify  = intent.getBooleanExtra( PARAM_MODIFY_FLAG, false );

    }

    private void updateUploadInfo(){
        mHandler.sendEmptyMessage( MSG_UPDATE_STATUS );
    }
    private void updateItemStatus(){
        mHandler.sendEmptyMessage( MSG_UPDATE_ITEM );
    }

    private static final int MSG_UPDATE_STATUS = 1;
    private static final int MSG_UPDATE_ITEM = 2;
    //private static final int MSG_DELSUCCESS_ITEM = 2;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case MSG_UPDATE_STATUS:{
                    updateView();
                    break;
                }
                case MSG_UPDATE_ITEM:{
                    if( mWaitImageAdapter!=null ){
                        mWaitImageAdapter.notifyDataSetChanged();
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };
    //----------------------------------------------------------------------
//
    private void onUserChange(){
        final boolean finalChange = isUserChange();
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                uploadBtnView.setEnabled( finalChange );
            }
        });
    }

    private boolean isUserChange(){

        //新增本地图片
        boolean change = localImageManager.getLocalImageCount()>0;

        //网络图片数量是否变换

        //网络图片顺序是否变化
        if( !change ){
            String md5 = localImageManager.getUrlsMd5();

            change = !listDataStatus.md5.equals(md5);
        }
        return change;
    }

    private void quit(){

        //正在编辑
        if( uploadStatusBean.isEditMode() ){
            AlertManager.showCustomDialog( mContext, getResources().getString(R.string.editing_noquit), "知道了", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            } );
        } else if( uploadStatusBean.getUploadStatus() == UploadStatusBean.ST_UPLOADING ){       //正在上传
            AlertManager.showCustomDialog( mContext, getResources().getString(R.string.uploading_quittips), "知道了", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            } );
        }else if( evaluateStatus == ST_EDIT && isUserChange() && !bUploadCurrentStatus ) {
            AlertManager.showCustomDialog( mContext, getResources().getString(R.string.uploadedit_noquit), "放弃更新并退出", "留在此页", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finishQuit();
                }
            },new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            } );
        }else{
            finishQuit();
        }
    }

    private void finishQuit(){
        Intent intent = new Intent();
        intent.putExtra( PARAM_SUBMIT_STATUS, bUploadStatus);
        intent.putExtra( PARAM_SUBMIT_MODIFY, bUploadModify );
        setResult( RESULT_OK, intent );
        finish();
    }

    private void initView(){

        AppLog.d("dfdffddffddf evaluateStatus = " + evaluateStatus );
        if( evaluateStatus == ST_NEW || evaluateStatus == ST_EDIT ){
            workToolbar.setTitle( getResources().getString( R.string.eval_waitupload_title ) );
        }else{
            workToolbar.setTitle( getResources().getString( R.string.eval_uploaded_title ) );
        }
        workToolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quit();
            }
        }, null);

        mLoadingPager.setTargetView(mainScrollView);
        listDataStatus = new ListDataStatus();
        uploadImageManager = UploadImageManager.getUploadImageManager(this, classId);
        uploadImageManager.setUploadListener( this, this );

        Drawable delDrawable = getResources().getDrawable( R.drawable.ic_delete );

        gridView.setParentView( mainScrollView );
        gridView.setParentRectView( mainLayout );
        gridView.setDiverHeight(WindowUtils.dpToPixels( mContext, 36 ));
        gridView.setExcludeWidth( delDrawable.getIntrinsicWidth() );

        networkStatusView.getTextView().setTextColor( mContext.getResources().getColor(R.color.color_333333));

        mLoadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });

        deleteBtnView.setVisibility( View.GONE );

        editBtnView.setOnClickListener( this );
        uploadBtnView.setOnClickListener( this );
        deleteBtnView.setOnClickListener( this );
        reUploadBtnView.setOnClickListener( this );
        finishBtnView.setOnClickListener( this );

        estimateCountView.setVisibility( View.GONE );
    }

    private void updateView() {

        int status = uploadStatusBean.getUploadStatus();

        if( status == UploadStatusBean.ST_UNUPLOAD ){             //未上传

            mainLayout.setSelected( true );

            if( evaluateStatus == ST_NEW ){
                uploadStatusView.setText("未上传");
                uploadStatusView.setTextColor( mContext.getResources().getColor(R.color.color_48B8FF) );
                uploadStatusView.setBackgroundResource( R.drawable.bg_rect_unupload );
            }else{
                uploadStatusView.setText( isUserChange()?"待上传":"已上传");
                uploadStatusView.setTextColor( mContext.getResources().getColor(R.color.color_B49DEF) );
                uploadStatusView.setBackgroundResource( R.drawable.bg_rect_uploadold );
            }

            if( localImageManager.getImageList()!=null && localImageManager.getImageList().size()>1 ){
                unUploadLayout.setVisibility( View.VISIBLE );
            }else{
                unUploadLayout.setVisibility( View.GONE );
            }
            uploadFailLayout.setVisibility( View.GONE );
            editLayout.setVisibility( View.GONE );
            //estimateCountView.setVisibility( View.GONE );
            editTipsLayout.setVisibility( View.GONE );
            //uploadSuccLayout.setVisibility( View.GONE );
            uploadingLayout.setVisibility( View.GONE );

        }else if( status == UploadStatusBean.ST_UPLOADFAIL ){       //上传失败

            mainLayout.setSelected( false );
            //
            uploadStatusView.setText("上传失败");
            uploadStatusView.setTextColor( mContext.getResources().getColor(R.color.color_FF7555) );
            uploadStatusView.setBackgroundResource( R.drawable.bg_rect_uploadfail );

            unUploadLayout.setVisibility( View.GONE );
            uploadFailLayout.setVisibility( View.VISIBLE );
            editLayout.setVisibility( View.GONE );
            //estimateCountView.setVisibility( View.GONE );
            editTipsLayout.setVisibility( View.GONE );
            //uploadSuccLayout.setVisibility( View.GONE );
            uploadingLayout.setVisibility( View.GONE );
            networkStatusView.stop();

        }else if( status == UploadStatusBean.ST_UPLOADEDIT ){       //编辑状态

            mainLayout.setSelected( true );
            //
            if( evaluateStatus == ST_NEW ){
                uploadStatusView.setText("未上传");
                uploadStatusView.setTextColor( mContext.getResources().getColor(R.color.color_48B8FF) );
                uploadStatusView.setBackgroundResource( R.drawable.bg_rect_unupload );
            }else{
                uploadStatusView.setText(isUserChange()?"待上传":"已上传");
                uploadStatusView.setTextColor( mContext.getResources().getColor(R.color.color_B49DEF) );
                uploadStatusView.setBackgroundResource( R.drawable.bg_rect_uploadold );
            }

            unUploadLayout.setVisibility( View.GONE );
            uploadFailLayout.setVisibility( View.GONE );
            editLayout.setVisibility( View.VISIBLE );
            //estimateCountView.setVisibility( View.GONE );
            uploadingLayout.setVisibility( View.GONE );
            if( isTipShowed() ){
                editTipsLayout.setVisibility( View.GONE );
            }else{
                editTipsLayout.setVisibility( View.VISIBLE );
            }
            //uploadSuccLayout.setVisibility( View.GONE );

        }else if( status == UploadStatusBean.ST_UPLOADING ) {       //上传中

            mainLayout.setSelected(false);
            //
            uploadStatusView.setText("上传中");
            uploadStatusView.setTextColor( mContext.getResources().getColor(R.color.color_57C724) );

            uploadStatusView.setBackgroundResource(R.drawable.bg_rect_uploading);

            unUploadLayout.setVisibility(View.GONE);
            uploadFailLayout.setVisibility(View.GONE);
            editLayout.setVisibility(View.GONE);
            //estimateCountView.setVisibility(View.GONE);
            editTipsLayout.setVisibility( View.GONE );
            //uploadSuccLayout.setVisibility( View.GONE );
            uploadingLayout.setVisibility( View.GONE );

            //estimateCountView.setText(estimCount);

        }else if( status == UploadStatusBean.ST_UPLOADED ){       //上传成功

            mainLayout.setSelected( false );
            //
            uploadStatusView.setText("上传成功");
            uploadStatusView.setTextColor( mContext.getResources().getColor(R.color.color_57C724) );
            uploadStatusView.setBackgroundResource( R.drawable.bg_rect_uploading );

            unUploadLayout.setVisibility( View.GONE );
            uploadFailLayout.setVisibility( View.GONE );
            editLayout.setVisibility( View.GONE );
            estimateCountView.setVisibility( View.GONE );
            editTipsLayout.setVisibility( View.GONE );
            //uploadSuccLayout.setVisibility( View.VISIBLE );
            uploadingLayout.setVisibility( View.GONE );
            networkStatusView.stop();
//            if( uploadSuccLayout.getHeight() == 0 ){
//                ViewGroup.LayoutParams layoutParams = uploadSuccLayout.getLayoutParams();
//                layoutParams.height = mainLayout.getHeight();
//                uploadSuccLayout.setLayoutParams( layoutParams );
//            }

            //不可拖动， 不可长按
            gridView.setCanDrag( false );
        }else if( status == UploadStatusBean.ST_UPLOADOLD ){       //已上传

            uploadStatusView.setText(isUserChange()?"待上传":"已上传");
            uploadStatusView.setTextColor( mContext.getResources().getColor(R.color.color_B49DEF) );
            uploadStatusView.setBackgroundResource( R.drawable.bg_rect_uploadold );

            if( evaluateStatus == ST_EDIT ){      //退出状态, 可以上传

                mainLayout.setSelected( true );

                if( localImageManager.getImageList()!=null && localImageManager.getImageList().size()>1 ){
                    unUploadLayout.setVisibility( View.VISIBLE );
                }else{
                    unUploadLayout.setVisibility( View.GONE );
                }
                uploadFailLayout.setVisibility( View.GONE );
                editLayout.setVisibility( View.GONE );
                //estimateCountView.setVisibility( View.GONE );
                editTipsLayout.setVisibility( View.GONE );
                //uploadSuccLayout.setVisibility( View.GONE );
                uploadingLayout.setVisibility( View.GONE );
            }else{

                mainLayout.setSelected( false );
                //

                unUploadLayout.setVisibility( View.GONE );
                uploadFailLayout.setVisibility( View.GONE );
                editLayout.setVisibility( View.GONE );
                estimateCountView.setVisibility( View.GONE );
                editTipsLayout.setVisibility( View.GONE );
                //uploadSuccLayout.setVisibility( View.VISIBLE );
                uploadingLayout.setVisibility( View.GONE );
                //networkStatusView.stop();

                //不可拖动， 不可长按
                gridView.setCanDrag( false );
            }
        }

        //图片列表
        if( evaluateStatus == ST_NEW || evaluateStatus == ST_EDIT ){

            if(gridView.getAdapter() == null) {
                mWaitImageAdapter = new EntranceWaitImageAdapter( mContext );
                gridView.setAdapter( mWaitImageAdapter);
                mWaitImageAdapter.setData( localImageManager.getImageList(), uploadStatusBean, this );
            }
            mWaitImageAdapter.notifyDataSetChanged();

        }else{

            if(gridView.getAdapter() == null) {
                mImageAdapter = new EntranceImageAdapter( mContext );
                gridView.setAdapter( mImageAdapter);
                mImageAdapter.setData( localImageManager.getImageList() );
            }
            mImageAdapter.notifyDataSetChanged();
        }
    }

    //解决ScrollView 里面的内容超出屏幕后，自动到底部的问题
    private void updateScrollViewToTop(){
        uploadStatusView.setFocusable(true);
        uploadStatusView.setFocusableInTouchMode(true);
        uploadStatusView.requestFocus();
    }
    //加载的是本地保存的数据
    private boolean loadData(){
        LoginInfo loginInfo = AccountUtils.getLoginUser();
        if( loginInfo == null ) return false;

        localImageManager = LocalImageManager.getLocalImageManager(mContext, loginInfo.getLoginName() );
        uploadStatusBean  = new UploadStatusBean();
        uploadStatusBean.setViewStatus( evaluateStatus );

        if( evaluateStatus == ST_NEW ){      //没有知识分析,从本地获取内容

            //如果是未上传状态，且图片小于12张， 增加添加按钮
            if( localImageManager.getImageCount() < AppConst.MAX_ENTRANCE_IMAGE ){
                localImageManager.addAddTypeImage();
            }else{
                localImageManager.removeAddTypeImage();
            }
            //检查是否存在上次中的
            //mWaitWorkAdapter.setHasUploading( mLocalWorkManager.checkInUploading() );
            updateView();
            updateScrollViewToTop();
            mainScrollView.setVisibility(View.VISIBLE);
        }else{
            //已传，服务器获取信息
            if( mGetEvalRecordTask==null || mGetEvalRecordTask.isComplete() || mGetEvalRecordTask.isCancelled() ){
                mGetEvalRecordTask = new GetEvalRecordTask();
                mainScrollView.setVisibility(View.INVISIBLE);
                mGetEvalRecordTask.execute();
                mLoadingPager.showLoading();
            }
        }
        return true;
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
                updateItemStatus();
                updateUploadInfo();
                onUserChange();
            }
        }
    }
    //--------------------------------------------------------------------------------------------
    class GetEvalRecordTask extends AppAsyncTask<String, Void, KnowledgeRecordBean> {

        @Override
        protected KnowledgeRecordBean doExecute(String... params) throws Exception {

            UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
            if( detailinfo == null ){
                throw new Exception("请登录");
            }
            String studentId   = detailinfo.getStudentId();

            return (new EntranceEvaluateImpl()).queryKnowledgeEvaluate( studentId );
        }

        @Override
        protected void onResult(KnowledgeRecordBean bean) {

            //首页加载 错误 或者没有数据   显示无
            if( bean == null || bean.getImages() == null || bean.getImages().size() == 0 ){
                mLoadingPager.showEmpty();
                return;
            }
            ArrayList<String> list = bean.getImages();

//            mLoadingView.hideall();
            mainScrollView.setVisibility(View.VISIBLE);
            mLoadingPager.showTarget();
            localImageManager.clearData();

            //生成本地数据
            StringBuilder sb = new StringBuilder();
            for( String url : list ){
                UploadImage image = new UploadImage( UploadImage.TYPE_IMAGE );
                image.setUrl( url );
                image.setUploadStatus( UploadImage.ST_SUCC );
                localImageManager.addUploadImage( image );
                sb.append(url);
            }

            listDataStatus.count = list.size();
            listDataStatus.md5   = MD5Util.getMD5String( sb.toString() );
            if( evaluateStatus == ST_EDIT ){
                localImageManager.addAddTypeImage();
                uploadBtnView.setText("更新上传");

                ViewGroup.LayoutParams layoutParams = uploadBtnView.getLayoutParams();
                layoutParams.width =  WindowUtils.dpToPixels(mContext,GlobalData.isPad()?180:120);
                uploadBtnView.setLayoutParams( layoutParams );
            }

            uploadStatusBean.setUploadStatus( UploadStatusBean.ST_UPLOADOLD );

            updateView();

            //先设置不能上传
            uploadBtnView.setEnabled( false );

            updateScrollViewToTop();
        }

        @Override
        protected void onFailure(HttpResponse<KnowledgeRecordBean> response, Exception ex) {
            mLoadingPager.showFault(ex);
        }
    }

    /**
     * 判断新手引导也是否已经显示了
     */
    private boolean isTipShowed() {
        String tag = "entranceupload_edittips";
        SharedPreferences sp = mContext.getSharedPreferences(tag, Activity.MODE_PRIVATE);
        boolean showed = sp.getBoolean(tag, false);
        if( !showed ){
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean( tag, true);
            editor.apply();
        }
        return showed;
    }

    /**
     * 记录数据列表的状态
     */
    class ListDataStatus{
        private int count = 0;      //列表数量
        private String md5 = "";     //url 组成的md5  顺序
    }
}
