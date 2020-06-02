package com.tsinghuabigdata.edu.ddmath.module.mycenter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppAsyncTask;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.commons.image.ImageResource;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.event.EditHeaderEvent;
import com.tsinghuabigdata.edu.ddmath.module.login.model.LoginModel;
import com.tsinghuabigdata.edu.ddmath.requestHandler.MycenterReqService;
import com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl.MycenterReqImpl;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.BitmapUtils;
import com.tsinghuabigdata.edu.ddmath.util.FileUtil;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.view.CircleImageView;
import com.tsinghuabigdata.edu.ddmath.view.MyProgressDialog;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayInputStream;
import java.io.File;

/**
 * 更换用户头像界面
 */
public class EditUserHeaderActivity extends Activity implements View.OnClickListener {

    /**
     * 相机
     */
    private final static int CAMERA = 1;

    /**
     * 相册
     */
    private final static int SELECT_ALBUM = 2;

    /**
     * 剪切图片
     */
    private final static int CLIP = 3;

    /**
     * 修改系统头像
     */
    //private final static int SYSTEM_HEAD = 4;

    //
    //public final static int REQUEST_PICS   = 0;
    //public final static int REQUEST_CAMERA = 1;
    public final static int REQUEST_CROP   = 2;

    private Button mCameraBtn;
    private Button mPicsBtn;

    private Context mContext;

    private String          mImagePath;
    private String          mImageName;
    private String          mCameraName;
    private CircleImageView circleImageView;

    private MyProgressDialog mProgressBar;

    private Bitmap mHeadImage;
    private MycenterReqService mycenterReqService = new MycenterReqImpl();

    //用户头像
    private ImageView      headerImageView;
    //private ImageView      tipImageView;
    //private RelativeLayout mainLayout;

    //private Uri imgUri;

    private UploadHeadImageTask uploadHeadImageTask;
    private String              accessToken;
    private String              accountId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        mContext = this;

        //设置全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mImagePath = getExternalFilesDir(null).getPath() + AppConst.IMAGE_CACHE_DIR;
        File file = new File(mImagePath);
        if (!file.exists()) file.mkdirs();
        long t = System.currentTimeMillis();
        mImageName = t + ".jpg";
        mCameraName= t + "-camera.jpg";

        if (GlobalData.isPad()) {
            setContentView(R.layout.dialog_editheadimage_layout);
        } else {
            setContentView(R.layout.dialog_editheadimage_layout_mobile);
        }

        initView();
        loadData();


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.dialog_headimage_closebtn) {
            finish();
        } else if (v.getId() == R.id.dialog_headimage_camerabtn) {
            String data = mCameraBtn.getText().toString();
            if (data.equals("拍照") || data.equals("重新拍照")) {

                //启动相机
                File file = new File(mImagePath, mCameraName);
                if( file.exists() ) file.delete();
                ImageResource.openCamera( this, file, CAMERA);      //开始拍照
            } else {
                //相册
                ImageResource.openPhotoSel2(this,SELECT_ALBUM);     //选择相册
            }

        } else if (v.getId() == R.id.dialog_headimage_picsbtn) {

            String data = mPicsBtn.getText().toString();
            if (data.equals("保存")) {
                startUploadHeadImageTask();
            } else {
                //相册
                ImageResource.openPhotoSel2(this,SELECT_ALBUM);     //选择相册
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }
        //相册
        if ( requestCode == SELECT_ALBUM) {
            mCameraBtn.setText("重新选择");
            mPicsBtn.setText("保存");

            // 相册选择
            Uri uri = data.getData();
            if( uri!=null &&uri.toString().contains("com.miui.gallery.open")){
                uri = getImageContentUri( mContext, new File(getRealFilePath(mContext, uri)));
            }

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                String path = getPath(this, uri);
//                if (path != null)
//                    uri = FileUtil.uriFromFile(this,new File(path));
//            }
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
            //mHeadImage = BitmapFactory.decodeFile( mImagePath + "" + mImageName );//data.getParcelableExtra("data");
            setCropImg(data);
        }
        //相机
        else if ( requestCode == CAMERA) {
            File file = new File(mImagePath, mCameraName);
            if( !file.exists() ){
                ToastUtils.show( this, "请重新拍照" );
                return;
            }

            if (mCameraBtn.getText().toString().trim().equals("拍照")) {
                mCameraBtn.setText("重新拍照");
                mPicsBtn.setText("保存");
            }
            // 拍照
            Uri uri = FileUtil.uriFromFile( this, file);
            //进行图片裁剪
            ImageResource.openPhotoClip(this,uri,new File(mImagePath, mImageName), CLIP);
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        File file = new File(mImagePath, mImageName);
        if( file.exists() ) file.delete();
        file = new File(mImagePath, mCameraName);
        if( file.exists() ) file.delete();
    }

    //--------------------------------------------------------------------------------------

//    private void doCrop() {
//        Intent intent = new Intent("com.android.camera.action.CROP");
//        intent.setType("image/*");
//        intent.setData(imgUri);
//        intent.putExtra("outputX", 300);
//        intent.putExtra("outputY", 300);
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
//        intent.putExtra("scale", true);
//        intent.putExtra("return-data", true);
//        startActivityForResult(intent, REQUEST_CROP);
//    }

    /**
     * set the bitmap
     *
     * @param picdata
     */
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
            Bitmap mBitmap = BitmapFactory.decodeFile( path );//bundle.getParcelable("data");
            headerImageView.setImageBitmap(mBitmap);

            circleImageView.setImageBitmap(mBitmap);

            mHeadImage = mBitmap;
        }
    }

    private void initView() {

        mProgressBar = new MyProgressDialog(this);
        mProgressBar.setMessage("上传头像中...");

        headerImageView = (ImageView) findViewById(R.id.dialog_headimage_imageview);
        circleImageView = (CircleImageView) findViewById(R.id.dialog_headimage_circleimage);

        //ImageView tipImageView = (ImageView) findViewById(R.id.dialog_headimage_tipimage);
        //RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.dialog_headimage_mainlayout);

        ImageView closeImageView = (ImageView) findViewById(R.id.dialog_headimage_closebtn);
        closeImageView.setOnClickListener(this);

        mCameraBtn = (Button) findViewById(R.id.dialog_headimage_camerabtn);
        mCameraBtn.setOnClickListener(this);
        mPicsBtn = (Button) findViewById(R.id.dialog_headimage_picsbtn);
        mPicsBtn.setOnClickListener(this);

        accessToken = AccountUtils.getLoginUser().getAccessToken();
        accountId = AccountUtils.getLoginUser().getAccountId();
    }

    //加载当前头像图片
    private void loadData() {
        HeadImageUtils.setHeadImage(headerImageView, R.drawable.personalcenter_bg_popup_photochange);
        HeadImageUtils.setHeadImage(circleImageView, R.drawable.personalcenter_bg_popup_photochange);
    }

    private void startUploadHeadImageTask() {
        if (uploadHeadImageTask == null || uploadHeadImageTask.isComplete() || uploadHeadImageTask.isCancelled()) {
            uploadHeadImageTask = new UploadHeadImageTask();
            mProgressBar.show();
            uploadHeadImageTask.execute();
        }
    }

    /**
     * 将URI转为图片的路径
     *       
     * @param context
     *  @param uri
     *  @return
     **/
    public static String getRealFilePath(final Context context, final Uri uri){
        if( null == uri)
            return null;
        final String scheme = uri.getScheme();
        String data = null;
        if(scheme==null)
            data = uri.getPath();
        else if(ContentResolver.SCHEME_FILE.equals(scheme)){
            data = uri.getPath();
        }else if(ContentResolver.SCHEME_CONTENT.equals(scheme)){
            Cursor cursor = context.getContentResolver().query(uri,new String[]{ MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if(null != cursor){
                if(cursor.moveToFirst()){
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA);
                    if(index>-1){
                        data=cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /**
     *
     *  包成file文件转为URI    
     * */
    public static Uri getImageContentUri(Context context, File imageFile){
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ", new String[]{filePath}, null);
        if(cursor!=null&&cursor.moveToFirst()){
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        }else{
            if(imageFile.exists()){
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }
    //----------------------------------------------------------------------------------------
    class UploadHeadImageTask extends AppAsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doExecute(Void... params) throws Exception {
            LoginInfo loginInfo = AccountUtils.getLoginUser();
            if (loginInfo == null)
                throw new Exception("请登录");
            byte[] bytes = BitmapUtils.bitmap2Bytes(mHeadImage);
            return mycenterReqService.uploadHeadImage(loginInfo.getAccessToken(), new ByteArrayInputStream(bytes));
        }

        @Override
        protected void onResult(Boolean res) {
            if (isFinishing()) {
                return;
            }
            // 修改头像后加载学生信息
            //            mProgressBar.dismiss();
            //            AlertManager.toast(mContext, "上传成功");
            //            mProgressBar.setMessage("更新信息");
            //            mProgressBar.show();
            new LoginModel().queryUserdetailInfo(accessToken, accountId, new RequestListener<UserDetailinfo>() {
                @Override
                public void onSuccess(UserDetailinfo res) {
                    mProgressBar.dismiss();
                    AccountUtils.setUserdetailInfo(res);
                    AlertManager.toast(mContext, "上传成功");
                    EventBus.getDefault().post(new EditHeaderEvent());
                    finish();
                }

                @Override
                public void onFail(HttpResponse response, Exception ex) {
                    AlertManager.toast(mContext, "修改头像失败");
                    mProgressBar.dismiss();
                }
            });
        }

        @Override
        protected void onFailure(HttpResponse<Boolean> response, Exception ex) {
            AlertManager.toast(mContext, "修改头像失败");
            mProgressBar.dismiss();
        }

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
