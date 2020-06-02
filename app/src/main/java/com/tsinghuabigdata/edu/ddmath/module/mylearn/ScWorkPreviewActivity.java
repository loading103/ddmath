package com.tsinghuabigdata.edu.ddmath.module.mylearn;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.ZxApplication;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.entrance.LocalImageManager;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.bean.UploadImage;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.bean.WaitWorkBean;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.PicassoUtil;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * 预览图片界面
 */
public class ScWorkPreviewActivity extends Activity implements View.OnClickListener {

    //
    private ImageView photoView;
    private Context mContext;

    private String imagePath;
    private Bitmap mBitmap;

    //传递过来的参数
    private String imageType;
    private boolean isNextImage;
    private boolean isBroadCast;
    private String  reqfrom;
    //此次上传作业的ID，本地使用
    private String mTaskId;


    @Override
    protected void onCreate( Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //设置全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView( GlobalData.isPad()?R.layout.activity_scwork_preview: R.layout.activity_scwork_preview_phone );
        mContext = this;

        if( !parseIntent() ){
            ToastUtils.showShort( this, "参数错误" );
            finish();
            return;
        }
        initView();
    }

    @Override
    public void onClick(View v) {
        if( v.getId() == R.id.mylearn_finishbtn ){

            ToastUtils.showToastCenter( mContext, "已保存" );

            if( AppConst.TYPE_FROM_ENTRANCE.equals( reqfrom ) ){        //入学评测
                //先把图片加入作业列表
                addImageToEntranceList();

                //进入下一个页面
                if( isBroadCast ){
                    sendAddImageBoardcast();
                    sendCloseActivity();
                    finish();
//                }else{
//                    sendCloseActivity();
//                    finish();
//                    sendStartWaitUploadBoardcast();
                }

                //保存数据列表
                LocalImageManager localWorkManager = LocalImageManager.getLocalImageManager();
                if( localWorkManager != null ) localWorkManager.saveData();

            }else{  //默认 学校作业

                //先把图片加入作业列表
                addImageToWaitWork();

                //进入下一个页面
                if( isBroadCast ){
                    sendAddImageBoardcast();
                    sendCloseActivity();
                    finish();
                }else{
                    sendCloseActivity();
                    finish();
                    sendStartWaitUploadBoardcast();
                }

                //保存数据列表
                LocalWorkManager localWorkManager = LocalWorkManager.getLocalWorkManager();
                if( localWorkManager != null ) localWorkManager.saveData();
            }


        }else if(v.getId() == R.id.mylearn_nextbtn ){

            if( AppConst.TYPE_FROM_ENTRANCE.equals( reqfrom ) ){

                //在判断目前已经拍照几页
                if( canCameraEntranceImage() ){
                    String data = getResources().getString(R.string.many_image);
                    data = data.replace("12", AppConst.MAX_ENTRANCE_IMAGE+"" );
                    ToastUtils.showToastCenter( getBaseContext(), data );
                    return;
                }

                //先把图片加入作业列表
                addImageToEntranceList();

            }else {  //默认 学校作业
                //先把图片加入作业列表
                addImageToWaitWork();

                //在判断目前已经拍照几页
                if( hasManyImage() ){
                    ToastUtils.showToastCenter( getBaseContext(), getResources().getString(R.string.many_image) );
                    return;
                }
            }
            ToastUtils.showToastCenter( mContext, "已保存" );

            //先通知更新
            if( isBroadCast ){
                sendAddImageBoardcast();
            }

            sendCloseActivity();

            //拍照下一题
            Intent intent = new Intent( getBaseContext(), ScWorkCameraActivity.class );
            intent.putExtra( ScWorkUtils.IS_NEXT_IMAGE, true );
            intent.putExtra( ScWorkUtils.TASK_ID, mTaskId );
            intent.putExtra( ScWorkUtils.PARAM_BROADCAST, isBroadCast );
            intent.putExtra( ScWorkUtils.PARAM_FROM, reqfrom );
            startActivity( intent );
            finish();

        }else if( v.getId() == R.id.mylearn_preview_redo ){

            //先删除当前图片
            File file = new File(imagePath);
            file.delete();

            //进入新的拍照界面
//            Intent intent = new Intent( getBaseContext(), ScWorkCameraActivity.class );
//            intent.putExtra( ScWorkUtils.IS_NEXT_IMAGE, isNextImage );
//            intent.putExtra( ScWorkUtils.TASK_ID, mTaskId );
//            intent.putExtra( ScWorkUtils.PARAM_BROADCAST, isBroadCast );
//            startActivity( intent );

            finish();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if( mBitmap != null ){
            mBitmap.recycle();
            mBitmap = null;
        }
    }
    //----------------------------------------------------------------------
    //解析参数,必须在initView之前调用
    private boolean parseIntent(){

        Intent intent = getIntent();

        if( !intent.hasExtra(ScWorkUtils.IMAGE_TYPE) || !intent.hasExtra(ScWorkUtils.IMAGE_HANDLE) )
            return false;

        isNextImage = intent.getBooleanExtra( ScWorkUtils.IS_NEXT_IMAGE, false );
        isBroadCast = intent.getBooleanExtra( ScWorkUtils.PARAM_BROADCAST, false );
        mTaskId     = intent.getStringExtra( ScWorkUtils.TASK_ID );
        imageType   = intent.getStringExtra( ScWorkUtils.IMAGE_TYPE );
        reqfrom     = intent.getStringExtra( ScWorkUtils.PARAM_FROM );

        if( ScWorkUtils.TYPE_LOCAL.equals(imageType) ){             //本地图片路径
            imagePath = intent.getStringExtra( ScWorkUtils.IMAGE_HANDLE );
        }else if( ScWorkUtils.TYPE_BITMAP.equals( imageType ) ){    //bitmap对象
            int session = intent.getIntExtra(ScWorkUtils.IMAGE_HANDLE,-1);
            mBitmap = ScWorkUtils.getBitmap( session );
            ScWorkUtils.removeBitmap( session );
        }

        if( isNextImage ){      //是继续拍照
            mTaskId = intent.getStringExtra( ScWorkUtils.TASK_ID );
        }
        return true;
    }

    private void initView(){

        photoView = (ImageView)findViewById( R.id.mylearn_preview_photo );

        //结束拍照
        TextView finishBtn = (TextView)findViewById( R.id.mylearn_finishbtn );
        finishBtn.setOnClickListener( this );

        //再拍一张
        TextView nextBtn = (TextView)findViewById( R.id.mylearn_nextbtn );
        nextBtn.setOnClickListener( this );
        //重拍
        RelativeLayout redoBtn = (RelativeLayout) findViewById( R.id.mylearn_preview_redo );
        redoBtn.setOnClickListener( this );


        if( mBitmap != null ){
            photoView.setImageBitmap( mBitmap );
        }else if( !TextUtils.isEmpty(imagePath) ){             //本地图片路径

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mBitmap = PicassoUtil.getPicasso( mContext ).load(new File(imagePath) ).get();
                        showBitmap( mBitmap );
                    }catch (Exception e){
                        AppLog.d("",e);
                    }
                }
            }).start();

        }

    }

    private void showBitmap( final Bitmap bitmap ){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                photoView.setImageBitmap( bitmap );
            }
        });
    }

    //
    private String createWaitWorkBean(){
        LocalWorkManager localWorkManager = initLocalWorkMananger();
        if( localWorkManager == null ) return "";

        WaitWorkBean workBean = localWorkManager.getNotUploadWaitWork();
        if( workBean == null ){
            workBean = new WaitWorkBean();
            localWorkManager.addWaitWorkTask( workBean );
        }
        return workBean.getTaskId();
    }

    //
    private boolean addImageToWaitWork(){

        if( TextUtils.isEmpty(mTaskId) ){ //是第一张拍照,建立
            mTaskId = createWaitWorkBean();
        }

        LocalWorkManager localWorkManager = initLocalWorkMananger();
        if( localWorkManager == null ){
            return false;
        }
        WaitWorkBean workBean = localWorkManager.getWaitWorkTask( mTaskId );
        if( workBean == null ){
            ToastUtils.showShort( getBaseContext(), "LocalWorkManager workBean no find");
            return false;
        }
        //检查是否重复，保证不重复添加
        if( workBean.hasImagePath( imagePath) ) return true;
        //添加
        workBean.addUploadImage( imagePath );

        //如果超过最大页，删除添加的
        if( workBean.getImageList().size() > AppConst.MAX_WORK_IMAGE ){
            workBean.removeAddTypeImage();
        }
        return true;
    }

    private boolean addImageToEntranceList(){

        LocalImageManager localWorkManager = LocalImageManager.getLocalImageManager();
        if( localWorkManager == null ){
            return false;
        }

        //添加
        UploadImage image = new UploadImage( imagePath );
        localWorkManager.addUploadImage( image );

        //如果超过最大页，删除添加的
        if( localWorkManager.getImageList().size() > AppConst.MAX_ENTRANCE_IMAGE ){
            localWorkManager.removeAddTypeImage();
        }
        return true;
    }
    private boolean canCameraEntranceImage(){

        LocalImageManager localWorkManager = LocalImageManager.getLocalImageManager();
        if( localWorkManager == null ){
            return false;
        }
        //小于最大数量，可以
        if( localWorkManager.getImageCount() < AppConst.MAX_ENTRANCE_IMAGE )
            return false;

        //只能等于，判断有没有 添加模式
        ArrayList<UploadImage> list = localWorkManager.getImageList();
        for( UploadImage image : list ){
            if( image.getImagetype() == UploadImage.TYPE_ADDIMAGE )
                return false;
        }

        return true;
    }

    private boolean hasManyImage(){

        LocalWorkManager localWorkManager = initLocalWorkMananger();
        if( localWorkManager == null ){
            return false;
        }
        WaitWorkBean workBean = localWorkManager.getWaitWorkTask( mTaskId );
        if( workBean == null ){
            ToastUtils.showShort( getBaseContext(), "LocalWorkManager workBean no find");
            return false;
        }
        return workBean.getImageCount() >= AppConst.MAX_WORK_IMAGE;
    }

    private void sendCloseActivity(){
        sendBroadcast( new Intent( AppConst.ACTION_CLOSE_ACTIVITY ) );
    }
    private void sendAddImageBoardcast(){
        Intent it = new Intent( AppConst.ACTION_SCWORK_ADD );
        sendBroadcast( it );
    }
    private void sendStartWaitUploadBoardcast(){
        Intent it = new Intent( AppConst.ACTION_START_WAITUPLOAD );
        ZxApplication.getApplication().sendBroadcast( it );
    }

    private LocalWorkManager initLocalWorkMananger(){

        LocalWorkManager localWorkManager = LocalWorkManager.getLocalWorkManager();
        if( localWorkManager != null ){
            return localWorkManager;
        }

        LoginInfo loginInfo = AccountUtils.getLoginUser();
        if( loginInfo == null || TextUtils.isEmpty(loginInfo.getLoginName()) ){
            ToastUtils.showShort( getBaseContext(), "用户信息错误");
            return null;
        }
        return LocalWorkManager.getLocalWorkManager( mContext, loginInfo.getLoginName());
    }
}
