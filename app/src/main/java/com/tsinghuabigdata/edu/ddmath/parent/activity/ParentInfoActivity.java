package com.tsinghuabigdata.edu.ddmath.parent.activity;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.commons.image.ImageResource;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.dialog.CustomDialog;
import com.tsinghuabigdata.edu.ddmath.module.login.LoginActivity;
import com.tsinghuabigdata.edu.ddmath.module.message.MessageUtils;
import com.tsinghuabigdata.edu.ddmath.parent.MVPModel.ParentCenterModel;
import com.tsinghuabigdata.edu.ddmath.parent.bean.ParentInfo;
import com.tsinghuabigdata.edu.ddmath.parent.dialog.ParentEditHeaderDialog;
import com.tsinghuabigdata.edu.ddmath.parent.event.ParentCenterEvent;
import com.tsinghuabigdata.edu.ddmath.parent.view.ParentToolbar;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.BitmapUtils;
import com.tsinghuabigdata.edu.ddmath.util.FileUtil;
import com.tsinghuabigdata.edu.ddmath.util.PicassoUtil;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.view.CircleImageView;
import com.tsinghuabigdata.edu.ddmath.view.MyProgressDialog;

import org.greenrobot.eventbus.EventBus;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.Locale;

/**
 * 家长端--家长信息
 */

public class ParentInfoActivity extends RoboActivity implements View.OnClickListener{


    //
    private final static int CAMERA = 1;            // 相机
    private final static int SELECT_ALBUM = 2;      // 相册
    private final static int CLIP = 3;              // 剪切图片
    private final static int UPDATE_PHONE = 4;      // 更新手机号


    @ViewInject(R.id.worktoolbar)
    private ParentToolbar workToolbar;

    @ViewInject(R.id.iv_children_nickname)
    private TextView nicknameView;
    @ViewInject(R.id.iv_children_headimage)
    private CircleImageView headImageView;
    @ViewInject(R.id.iv_binding_phone )
    private TextView phoneView;
    @ViewInject(R.id.btn_quit)
    private Button quitButton;

    private Context mContext;

    private String          mImagePath;
    private String          mImageName;
    private String          mCameraName;
    private Bitmap          mHeadImage;
    private MyProgressDialog mProgressBar;

    //要上传的图片路径
    private String uploadImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_parent_info);
        x.view().inject( this );
        mContext = this;
        initView();
    }
    @Override
    public String getUmEventName() {
        return "parent_mycenter_info";
    }

    @Override
    public void onClick(View v) {
        switch ( v.getId() ){
            case R.id.layout_parent_headimage:
                ParentEditHeaderDialog dialog = new ParentEditHeaderDialog(mContext);
                dialog.setListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startCamera();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startPics();
                    }
                });
                dialog.show();
                break;
            case R.id.layout_parent_bindphone:{
                    ParentInfo parentInfo = AccountUtils.getParentInfo();
                    if( parentInfo!=null){
                        Intent intent = new Intent( mContext, ParentBindingPhoneActivity.class );
                        intent.putExtra( "phone", parentInfo.getPhoneNumber() );
                        startActivityForResult( intent, UPDATE_PHONE );
                    }
                break;
            }
            case R.id.layout_parent_pw:
                Intent intent = new Intent( mContext, ParentModifyPwActivity.class );
                mContext.startActivity( intent );
                break;
            case R.id.btn_quit:

                CustomDialog customDialog = AlertManager.showCustomDialog( mContext, "退出登录？", "退出", "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //清除登录信息
                        AccountUtils.setLoginParent( null );
                        //反初始化推送服务
                        MessageUtils.unIinitJPushSdk(mContext);

                        //关闭所有界面，跳转到登录界面
                        finishAll();

                        LoginActivity.openLoginActivity( mContext, LoginActivity.ROLE_PARENT );
                    }
                }, null);
                customDialog.setRightBtnAttr(R.drawable.bg_rect_blue_r24, R.color.white);
                break;
            default:
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }
        //相册
        if ( requestCode == SELECT_ALBUM) {
            // 相册选择
            Uri uri = data.getData();
            //进行图片裁剪
            ImageResource.openPhotoClip( this, uri, new File(mImagePath, mImageName), CLIP);
        }
        //剪切图片
        else if ( requestCode == CLIP) {
            // 剪切图片
            if (mHeadImage != null) {
                // 释放资源
                mHeadImage.recycle();
            }
            setCropImg(data);
        }
        //相机
        else if ( requestCode == CAMERA) {
            File file = new File(mImagePath, mCameraName);
            if( !file.exists() ){
                ToastUtils.show( this, "请重新拍照" );
                return;
            }
            // 拍照
            Uri uri = FileUtil.uriFromFile( this, file);
            //进行图片裁剪
            ImageResource.openPhotoClip(this,uri,new File(mImagePath, mImageName), CLIP);
        }
        //更新手机号
        else if( requestCode == UPDATE_PHONE ){
            if( data!=null && data.hasExtra("phone") ){
                phoneView.setText( data.getStringExtra("phone") );
            }
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        File file = new File(mImagePath, mImageName);
        if( file.exists() ){
            boolean b = file.delete();
            AppLog.d(" delete file = " + b );
        }
        file = new File(mImagePath, mCameraName);
        if( file.exists() ){
            boolean b = file.delete();
            AppLog.d(" delete file = " + b );
        }
    }

    public void startCamera(){
        //启动相机
        File file = new File(mImagePath, mCameraName);
        if( file.exists() ){
            boolean b = file.delete();
            AppLog.d(" delete file = " + b );
        }
        ImageResource.openCamera( this, file, CAMERA);      //开始拍照
    }
    public void startPics(){
        //相册
        ImageResource.openPhotoSel2(this,SELECT_ALBUM);     //选择相册
    }

    //----------------------------------------------------------------------------------------
    private void initView(){

        workToolbar.setTitle( "我的资料" );
        workToolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ParentInfo parentInfo = AccountUtils.getParentInfo();
        if( parentInfo!=null ){
            PicassoUtil.getPicasso(mContext).load(BitmapUtils.getUrlWithToken(parentInfo.getHeadImage())).error( R.drawable.aboutme_parenttouxiang ).into(headImageView);

            String name = "";
            if( parentInfo.getStudentInfos()!=null && parentInfo.getStudentInfos().size()>0) {
                name = parentInfo.getStudentInfos().get(0).getReallyName();
                if (TextUtils.isEmpty(name)) name = parentInfo.getStudentInfos().get(0).getNickName();
                if (TextUtils.isEmpty(name)) name = parentInfo.getStudentInfos().get(0).getPhone();
            }
            if( TextUtils.isEmpty(name) ) name = "";
                nicknameView.setText(  String.format(Locale.getDefault(),"%s家长",name)  );
            phoneView.setText( parentInfo.getPhoneNumber() );
        }

        View view = findViewById( R.id.layout_parent_headimage );
        if(view!=null)view.setOnClickListener( this );
        view = findViewById( R.id.layout_parent_bindphone );
        if(view!=null)view.setOnClickListener( this );
        view = findViewById( R.id.layout_parent_pw );
        if(view!=null)view.setOnClickListener( this );

        quitButton.setOnClickListener( this );

        File file  = getExternalFilesDir(null);
        mImagePath = (file==null?"":file.getPath()) + AppConst.IMAGE_CACHE_DIR;
        file = new File(mImagePath);
        if (!file.exists()){
            boolean b = file.mkdirs();
            AppLog.d(" delete mkdirs = " + b );
        }
        long time = System.currentTimeMillis();
        mImageName = time + ".jpg";
        mCameraName= time + "-camera.jpg";

        mProgressBar = new MyProgressDialog(this);
        mProgressBar.setMessage("上传头像中...");
    }

    private void setCropImg(Intent picdata) {
        //Bundle bundle = picdata.getExtras();
        if (null != picdata) {

            String path = picdata.getDataString();
            if(TextUtils.isEmpty(path) ) path = picdata.getAction();
            if( !TextUtils.isEmpty(path)){      //重新对剪切的图片进行了命名
                if( path.startsWith("file:////") ) path = path.replace("file:///","");
                else if( path.startsWith("file:///") ) path = path.replace("file://","");
            }else{
                path = mImagePath + "/"+ mImageName;
            }
            uploadImagePath = path;
            mHeadImage = BitmapFactory.decodeFile( path );//bundle.getParcelable("data");

            //开始上传图片
            startUploadHeadImageTask();
        }
    }

    private void startUploadHeadImageTask() {

       File file = new File(uploadImagePath);
       if( !file.exists() ){
           return;
       }

        mProgressBar.show();
        new ParentCenterModel().uploadHeadImage( uploadImagePath, new RequestListener() {
            @Override
            public void onSuccess(Object res) {
                if (isFinishing()) {
                    return;
                }
                // 修改头像后加载学生信息
                mProgressBar.dismiss();
                AlertManager.toast(mContext, "上传成功");

                headImageView.setImageBitmap(mHeadImage);

                //通知个人中心信息
                EventBus.getDefault().post( new ParentCenterEvent(ParentCenterEvent.TYPE_UPDATE_INFO) );
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                AlertManager.toast(mContext, "修改头像失败");
                mProgressBar.dismiss();
            }
        });
    }

    //以下是关键，原本uri返回的是file:///...来着的，android4.4返回的是content:///...
    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri, String selection,
                                        String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
