package com.tsinghuabigdata.edu.ddmath.module.mylearn.bean;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.text.TextUtils;

import com.tsinghuabigdata.edu.ddmath.ZxApplication;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.UploadImageListener;
import com.tsinghuabigdata.edu.ddmath.requestHandler.MyLearnService;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

/**
 * 作业图片结构
 */
public class UploadImage {

    public static final int ST_ERROR = -1;
    public static final int ST_NONE  = 0;
    public static final int ST_UING  = 1;
    public static final int ST_SUCC  = 2;

    public static final int TYPE_IMAGE  = 0;
    public static final int TYPE_ADDIMAGE  = 1;

    //传递
    private String localpath;       //图片保存在本地的路径

    //服务器返回
    private String url="";

    private boolean delfalg;        //是否删除的标志

    boolean editMode;

    private String fileId;

    //上传的状态
    private int uploadStatus = ST_NONE;       //-1:上传错误， 0：没有上传， 1：上传中 2：上传成功

    private int imagetype = TYPE_IMAGE;

    //关联的图片是否最后一个上传
    private boolean bLastUploadPage = false;

    private UploadImageListener mListener;

    //构造函数
    public UploadImage(int type){
        imagetype = type;
    }
    public UploadImage( MyLearnService service, String path ){

//        learnService = ZxApplication.getLearnService();
//        if( path!=null && !path.contains( AppConst.IMAGE_DIR) ){
//            this.url = path;
//            uploadStatus = ST_SUCC;
//            localpath = null;
//        }else{
            localpath = path;
            //判断是本地图片，则开始上传
            //startUpload();
//        }
        fileId = System.currentTimeMillis()+"";
    }
    public UploadImage( String path ){
        localpath = path;
        fileId = System.currentTimeMillis()+"";
    }

    //预上传图片
    public void previewUploadLocalpath(String path) {
        if( localpath != null && localpath.equals(path) )
            return;
        this.localpath = path;

        //预上传
        if( !TextUtils.isEmpty(localpath) && new File(localpath).exists() ){
            if( ST_NONE == uploadStatus || ST_ERROR == uploadStatus ){      //未上传 或者上传错误
                startUpload();
            }else if( ST_SUCC == uploadStatus ){    //已上传成功，初始化状态
                url = "";
                uploadStatus = ST_NONE;
                startUpload();
            }else if( ST_UING == uploadStatus ){    //正在上传中，等待完成后重新
                //触发保存？？？
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //等待完成
                        while( ST_UING == uploadStatus ) SystemClock.sleep(100);

                        //初始化，在重新上传
                        url = "";
                        uploadStatus = ST_NONE;
                        startUpload();
                    }
                }).start();
            }
        }
    }

    public void setUploadStatus(int uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public void setUploadImageListener(UploadImageListener listener ){
        mListener = listener;
    }

    private Bitmap mBitmap;

    public Bitmap getBitmap( int width, int height ){
        if( mBitmap !=null ) return mBitmap;
        if( !TextUtils.isEmpty(localpath) ){
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inPreferredConfig = Bitmap.Config.RGB_565;
            opt.inPurgeable = true;
            opt.inInputShareable = true;
            opt.outWidth = width;
            opt.outHeight = height;
            mBitmap = BitmapFactory.decodeFile( localpath, opt );
        }else{
            mBitmap = null;
        }
        return mBitmap;
    }

    public void destroyBitmap(){
        if( mBitmap!=null ){
            mBitmap.recycle();
            mBitmap = null;
        }
    }

    //
    public JSONObject getJsonObject(){

        JSONObject json = new JSONObject();
        try {
            if( uploadStatus == ST_SUCC || uploadStatus == ST_ERROR ){
                json.put( "uploadStatus", uploadStatus );       //
            }else{
                json.put( "uploadStatus", ST_NONE );       //
            }
            json.put( "localpath", localpath );       //
            json.put( "url", url );       //
            json.put( "delfalg", delfalg );       //
            json.put( "fileId", fileId );       //
            json.put( "bLastUploadPage", bLastUploadPage );
        }catch (Exception e){
            AppLog.i( "", e );
        }

        return json;
    }
    //上传到服务器的数据结构
    public JSONObject getUploadJsonObject(){

        JSONObject json = new JSONObject();
        try {
            json.put( "path", url );       //
            json.put( "delete", delfalg );
        }catch (Exception e){
            AppLog.i( "", e );
        }
        return json;
    }

    public int getImagetype() {
        return imagetype;
    }

    public void setImagetype(int imagetype) {
        this.imagetype = imagetype;
    }

    //-------------------------------------------------------------------------------
    public String getLocalpath() {
        return localpath;
    }

    public void setLocalpath(String localpath) {
        this.localpath = localpath;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getUploadStatus(){
        return uploadStatus;
    }

    public int getUploadStatusNew() {
        if( ST_SUCC == uploadStatus && bLastUploadPage ){
            return ST_UING;
        }else
            return uploadStatus;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public boolean isDelfalg() {
        return delfalg;
    }

    public void setDelfalg(boolean delfalg) {
        this.delfalg = delfalg;
    }

    //-----------------------------------------------------------------------------
    public void startUpload(){
        for( UploadImage uploadImage : uploadlist ){
            if( uploadImage.equals( this ) ){
                return;
            }
        }
        callbackListener( uploadStatus );
        uploadlist.add( this );
        startUploadThread();
    }

    public void callbackListener( int status ){
        //AppLog.d("dfdfdfd callbackListener  = " + mListener );
        if( mListener!=null ){
            mListener.updateStatus( status );
        }
    }
    private void upload(){

        if( uploadStatus == ST_UING || uploadStatus == ST_SUCC )
            return;

        uploadStatus = ST_UING;
        callbackListener( uploadStatus );
//        new Thread(new Runnable() {
//            public void run() {
//            @Override

        LoginInfo loginInfo = AccountUtils.getLoginUser();
        if( loginInfo == null ){
            uploadStatus = ST_ERROR;
            callbackListener( uploadStatus );
            return;
        }

        MyLearnService learnService = ZxApplication.getLearnService();
        if( learnService == null ){
            uploadStatus = ST_ERROR;
            callbackListener( uploadStatus );
            return;
        }

        AppLog.d("dfdfdfd ,,, path = " + localpath );
        FileInputStream fis = null;
        try{
            File file = new File(localpath );
            fis = new FileInputStream( file );
            AppLog.d("dfdfdfd size = " + file.length()+",,,,avai="+ fis.available() + ",,,exist="+ file.exists()+ ",,, path = " + localpath );
        }catch (Exception e){
            AppLog.i( "", e );
        }
        if( fis == null ){
            uploadStatus = ST_ERROR;
            callbackListener( uploadStatus );
            return;
        }

        for( int i=0; i<3; i++ ){
            try{
                String path = learnService.uploadImage( fileId, loginInfo.getAccessToken(), fis, localpath );
                if( !TextUtils.isEmpty(path) ){
                    url = path;
                    uploadStatus = ST_SUCC;
                    callbackListener( uploadStatus );
                    fis.close();
                    return;
                }
                SystemClock.sleep(50);
            }catch (Exception e){
                AppLog.i( "", e );
            }
        }
        //
        try {
            fis.close();
        }catch (Exception e){
            AppLog.i( "", e );
        }
        uploadStatus = ST_ERROR;
        callbackListener( uploadStatus );
//            }
//        }).start();
    }

    //------------------------------------------------------------------
    //由于不能同时上传，现在改为依次上传
    private static ArrayList<UploadImage> uploadlist = new ArrayList<>();

    private static final int THREAD_IDLE = 0;
    private static final int THREAD_RUN = 1;

    private static int run_status = THREAD_IDLE;

    private static void startUploadThread(){
        if( run_status == THREAD_RUN )
            return;
        run_status = THREAD_RUN;

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    if( uploadlist.size() == 0 )
                        break;

                    AppLog.d("pppppp uploadlist size = " + uploadlist.size() );
                    long time = System.currentTimeMillis();
                    UploadImage uploadImage = uploadlist.get(0);
                    uploadImage.upload();
                    uploadlist.remove( uploadImage );
                    //AppLog.d("pppppp upload time = " + ( System.currentTimeMillis() - time ) + ",,,,status = " + uploadImage.getUploadStatus() );
                }
                run_status = THREAD_IDLE;
            }
        }).start();
    }

    public boolean isLastUploadPage() {
        return bLastUploadPage;
    }

    public void setLastUploadPage(boolean bLastUploadPage) {
        this.bLastUploadPage = bLastUploadPage;
    }

}
