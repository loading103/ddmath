package com.tsinghuabigdata.edu.ddmath.module.ddwork;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.BaseActivity;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.LocalPageInfo;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.LocalQuestionInfo;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.QuestionRect;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.SelectErrorQuestionView;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkSplitView;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.ScWorkUtils;
import com.tsinghuabigdata.edu.ddmath.opencv.OpenCVHelper;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.BitmapUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.engine.config.TFAndroidConstants;
import com.tsinghuabigdata.engine.util.TFAndroidDetector;
import com.tsinghuabigdata.engine.util.TFProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 预览图片界面
 */
public class DDPreviewActivity extends BaseActivity implements View.OnClickListener,SelectErrorQuestionView.SelectListener  {

    //
    private ImageView photoView;
    private WorkSplitView splitView;

    private ProgressDialog mProgressDialog;
    private SelectErrorQuestionView selectErrorQuestionView;

    private Context mContext;
    private DDPreviewActivity mActivity;

    private LocalPageInfo localPageInfo;

    private int imageShowHeight = 0;
    private double image_rate = 1;          //高度方向

    //传递过来的参数
//    private String workId;        //作业ID
//    private int    pageIndex;     //页序号

    private String srcImagePath;            //图片路径
    private Bitmap mSrcBitmap;              //拍照原图

    private boolean bCropImage = false;

    private ArrayList<LocalQuestionInfo> questionInfoList;

    //
    private TFAndroidDetector tfAndroidDetector;
    private List<TFProvider.Recognition> resultList;
    private static boolean btfDetectorRun = false;

    @Override
    protected void onCreate( Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //设置全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(GlobalData.isPad()?R.layout.activity_ddwork_preview:R.layout.activity_ddwork_preview_phone);
        mContext = this;
        mActivity= this;
        tfAndroidDetector = TFAndroidDetector.getInstance( getAssets(), TFAndroidConstants.DetectType.judge_symbol_detect_rec );

        if( !parseIntent() ){
            ToastUtils.showShort( this, "参数错误" );
            finish();
            return;
        }

        initView();
        loadImage();
    }

    @Override
    public void onClick(View v) {
        if( v.getId() == R.id.ddwork_preview_enter ){

            if( AppConst.UPLOAD_TYPE_MARKED == localPageInfo.getUploadType() ){
                dealAnswerRect();
                selectErrorQuestionView.setData( localPageInfo.getQuestions(),resultList, AccountUtils.getExamFlag() );
                selectErrorQuestionView.setVisibility(View.VISIBLE);
            }else{
                dealEnter();
            }
        }else if( v.getId() == R.id.ddwork_preview_redo ){

            //long time = System.currentTimeMillis();
            //AppLog.d( "dsdffdsfgdsf  clarity = " + OpenCVHelper.clarityImage( srcImagePath, OpenCVHelper.MODE_CLARITY_1 ) );
            //AppLog.d( "dsdffdsfgdsf  time = " + ( System.currentTimeMillis()-time ) );

            redo();
        }
    }

    @Override
    public void onSelectEnter() {
        dealEnter();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if( selectErrorQuestionView.isShown() ){
            selectErrorQuestionView.setVisibility(View.GONE);
        }else{
            redo();
        }
    }

    //----------------------------------------------------------------------
    private void dealEnter(){
        mProgressDialog.show();

        //save
        if( cropImages() ){

            mProgressDialog.dismiss();
            //ToastUtils.showToastCenter( mContext, "图片处理成功" );

            //刷新放在 DDUploadActivity OnResume 里面执行
            //sendStartWaitUploadBoardcast();
            Intent intent = new Intent();
            intent.putExtra( "close", true );
            setResult( RESULT_OK, intent );

            finish();
        }else{
            mProgressDialog.dismiss();
            ToastUtils.showToastCenter( mContext, "图片处理失败，请先退出APP，重新进入再操作。" );
            bCropImage = false;
            redo();
        }
    }

    //返回重拍
    private void redo(){

        if( bCropImage ){
            ToastUtils.showShort( mContext, "正在处理图片，不能取消");
            return;
        }

        //先删除当前图片
        File file = new File(srcImagePath);
        boolean b = file.delete();
        AppLog.d( "delete file b = " + b);
        Intent intent = new Intent();
        intent.putExtra( "from", true );
        setResult( RESULT_OK, intent );

        finish();
    }

    //解析参数,必须在initView之前调用
    private boolean parseIntent(){

        Intent intent = getIntent();

        if( !intent.hasExtra( ScWorkUtils.IMAGE_TYPE) || !intent.hasExtra(ScWorkUtils.IMAGE_HANDLE) )
            return false;
        localPageInfo = AccountUtils.getLocalPageInfo();

//        workId     = intent.getStringExtra( DDWorkUtil.WORK_ID );
//        pageIndex  = intent.getIntExtra( DDWorkUtil.PAGEINDEX, 0 );

        //String imageType = intent.getStringExtra( ScWorkUtils.IMAGE_TYPE );

        /*if( ScWorkUtils.TYPE_BITMAP.equals( imageType )  ){
            int session = intent.getIntExtra(ScWorkUtils.IMAGE_HANDLE,-1);
            mSrcBitmap = ScWorkUtils.getBitmap( session );
            ScWorkUtils.removeBitmap( session );
        }else {*/
            srcImagePath = intent.getStringExtra( ScWorkUtils.IMAGE_HANDLE );
        /*}*/
        return !( localPageInfo.getQuestions() == null || localPageInfo.getQuestions().size() == 0 );
    }

    private void initView(){

        photoView = findViewById( R.id.ddwork_preview_photo );
        splitView = findViewById( R.id.ddwork_preview_splitview );

        //确认拍照
        Button finishBtn = findViewById( R.id.ddwork_preview_enter );
        finishBtn.setOnClickListener( this );

        //重拍
        Button redoBtn = findViewById( R.id.ddwork_preview_redo );
        redoBtn.setOnClickListener( this );

        mProgressDialog = new ProgressDialog( mContext );
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage( "图片处理中" );
        //
        selectErrorQuestionView = findViewById(R.id.lm_selectErrorQuestionView);
        selectErrorQuestionView.setSelectListener(this);
        selectErrorQuestionView.setLandScreen( false );

        if( AppConst.UPLOAD_TYPE_MARKED == localPageInfo.getUploadType() ){
            finishBtn.setText(  "下一步（标记错题）" );
        }
    }

    private void setSplitViewDataThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while( true ){
                    SystemClock.sleep( 50 );
                    if( splitView.getHeight() == 0 ) continue;

                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageShowHeight = splitView.getHeight();

                            //初始化题目的原始范围
                            questionInfoList = localPageInfo.getQuestions();
                            //Collections.sort( questionInfoList, new QuestionComparator() );

                            splitView.setSplitData( questionInfoList, imageShowHeight );
                        }
                    });
                    break;
                }
            }
        }).start();
    }

    private void startClarityImageThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if( TextUtils.isEmpty(srcImagePath) )
                    return;
                //long time = System.currentTimeMillis();
                double clarity = OpenCVHelper.clarityImage( srcImagePath, OpenCVHelper.MODE_CLARITY_1 );
                //AppLog.d("fdasggfdsg time="+(System.currentTimeMillis()-time)+",,,clarity = "+clarity);
                if( clarity < 1.1 ){
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showLong( mContext, "这张图片可能还不够清晰哦，建议你重新拍一张～");
                        }
                    });
                }
            }
        }).start();
    }

    private void loadImage(){

        new AsyncTask<Void, Void, Object>() {

            @Override
            protected Object doInBackground(Void... params) {

                if( srcImagePath != null && (new File( srcImagePath )).exists() ){
                    mSrcBitmap = BitmapUtils.decodeBitmap( srcImagePath );
//                }else if( mSrcBitmap != null ){
//                    target_bitmap = mSrcBitmap;
                }
                return mSrcBitmap;
            }

            @Override
            protected void onPostExecute(Object bitmap) {
                super.onPostExecute(bitmap);
                if(bitmap!=null) {

                    if(splitView!=null)splitView.setVisibility(View.VISIBLE);

                    photoView.setVisibility(View.VISIBLE);

                    Bitmap target_bitmap = (Bitmap)bitmap;
                    photoView.setImageBitmap(target_bitmap);

                    setSplitViewDataThread();
                    startClarityImageThread();
                    startDetector();
                } else {
                    //mLoadProgressBar.setVisibility(View.GONE);

                    AlertManager.toast( mContext, "图片获取出错" );
                    setResult(RESULT_CANCELED);
                    finish();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }
    //开始识别批阅结果的勾叉信息
    private void startDetector(){

        if( AppConst.UPLOAD_TYPE_MARKED  != localPageInfo.getUploadType() )
            return;
        //
        new Thread(new Runnable() {
            @Override
            public void run() {

                if( tfAndroidDetector!=null && !btfDetectorRun ){
                    btfDetectorRun = true;
                    resultList = tfAndroidDetector.recBitmap(mSrcBitmap);
                    btfDetectorRun = false;
                }
            }
        }).start();
    }

    /**
     * 剪切题目答案图片
     * @return 完成情况
     */
    private boolean cropImages() {

        imageShowHeight = splitView.getHeight();

        bCropImage = true;
        //先对图片进行局部
//        long tt = System.currentTimeMillis();
//        if( !dealEnhanceImage() ){
//            bCropImage = false;
//            return false;
//        }
//        AppLog.d(" DetechAynscTask enhance image ,time = " + (System.currentTimeMillis()-tt) );

        //保存答案切图
        //ArrayList<LocalQuestionInfo> questionList = localPageInfo.getQuestions();
        for (int i = 0; i < questionInfoList.size(); i++) {
            LocalQuestionInfo questionInfo = questionInfoList.get(i);

            WorkSplitView.ItemData itemData = splitView.getItemData(i * 2 + 1);
            if (itemData == null) return false;

            //tt = System.currentTimeMillis();
//            String qtype = questionInfo.getQuestionType();
//            if( !"choice".equals( qtype ) && !"mutichoice".equals( qtype ) ){      //选择题不处理
//                OpenCVHelper.enhanceImage( imagepath,imagepath, /*OpenCVHelper.MODE_ENHANCE_EXP|*/OpenCVHelper.MODE_REMOVE_NOISE|OpenCVHelper.MODE_GUASS_BLUR, 31, 9 );
//            }
            //AppLog.d(" DetechAynscTask enhance i="+i+",time = " + (System.currentTimeMillis()-tt) );

            //同步数据
            QuestionRect questionRect = questionInfo.getQuestionRect();
            if(questionRect==null) return false;

            float left = itemData.getLeft();
            float width = itemData.getWidth();
            //if( left < 0 || left >= 1 ) return false;
            //if( width < 0 || width+left>1  ) return false;
            questionRect.setX( left );
            questionRect.setWidth( width );

            float y = itemData.getTop()*1f/imageShowHeight;
            float h = (itemData.getBottom()-itemData.getTop())*1f/imageShowHeight;
            //if( y < 0 || y >= 1 ) return false;
            //if( h < 0 || h +y > 1 ) return false;
            questionRect.setY( y );
            questionRect.setHeight( h );

            String imagepath = cropAnswerImage( itemData );
            if( TextUtils.isEmpty(imagepath) ) return false;
            questionInfo.setLocalpath( imagepath );
        }

        //保存整图
//        String imagepath = DDWorkManager.getImagePath() + System.currentTimeMillis()+ AppConst.IMAGE_SUFFIX_NAME;
//        if( !BitmapUtils.compressImage( imagepath, mSrcBitmap, mContext ) )
//            return false;
        localPageInfo.setLocalpath( srcImagePath );
        localPageInfo.useCacheData();

        //触动保存
        DDWorkManager ddWorkManager = DDWorkManager.getDDWorkManager();
        if (ddWorkManager != null)
            ddWorkManager.saveData();

        bCropImage = false;
        return true;
    }

    /**
     * 把题框处理到题目上面，共自动识别勾叉使用，这一处理，可以把用户自己调整的也包括进入
     */
    private void dealAnswerRect() {

        //imageShowHeight = splitView.getHeight();
        if( questionInfoList == null ) return;

        for (int i = 0; i < questionInfoList.size(); i++) {
            LocalQuestionInfo questionInfo = questionInfoList.get(i);

            WorkSplitView.ItemData itemData = splitView.getItemData(i * 2 + 1);
            if (itemData == null) return;

            //同步数据
            RectF rectF = new RectF();
            QuestionRect questionRect = questionInfo.getQuestionRect();
            if(questionRect==null) return;
            rectF.left = itemData.getLeft();
            rectF.right = rectF.left + itemData.getWidth();

            float y = itemData.getTop()*1f/imageShowHeight;
            if( y < 0 || y >= 1 ) return;
            rectF.top =  y;
            float h = (itemData.getBottom()-itemData.getTop())*1f/imageShowHeight;
            if( h < 0 || h +y > 1 ) return;
            rectF.bottom = rectF.top + h;

            //先处理答题框
            //调整后的答题框
            ArrayList<RectF> answerAreaList = new ArrayList<>();
            answerAreaList.add( rectF );

            //题框信息
            questionInfo.setAnswerAreaTmpList( answerAreaList );
        }
    }
    //局部增强处理
//    private boolean dealEnhanceImage(){
//
//        calImageRate();
//
//        int buffer[] = new int[questionInfoList.size()*4];
//        int count = 0;
//        for (int i = 0; i < questionInfoList.size(); i++) {
//            String qtype = questionInfoList.get(i).getQuestionType();
//            if( "choice".equals( qtype ) || "mutichoice".equals( qtype ) )      //选择题不处理
//                continue;
//
//            WorkSplitView.ItemData itemData = splitView.getItemData(i * 2 + 1);
//            if (itemData == null) return false;
//
//            int left  = (int) (itemData.getLeft() * mSrcBitmap.getWidth());
//            int top   = (int)(itemData.getTop() * image_rate);
//
//            int width = (int) (itemData.getWidth() * mSrcBitmap.getWidth());
//            if( left + width > mSrcBitmap.getWidth() )
//                width = mSrcBitmap.getWidth() - left;
//
//            int height= (int)(itemData.getBottom() * image_rate) - top;
//
//            //
//            if( top > mSrcBitmap.getHeight() ){
//                top = mSrcBitmap.getHeight();
//            }
//            if( top+height > mSrcBitmap.getHeight() ){
//                height = mSrcBitmap.getHeight() - top;
//            }
//
//            if( height < 0 )
//                height = 1;
//
//            buffer[i*4]   = left;
//            buffer[i*4+1] = top;
//            buffer[i*4+2] = width;
//            buffer[i*4+3] = height;
//
//            count++;
//        }
//        if( count <= 0 )
//            return true;
//        return OpenCVHelper.regionEnhanceImage( srcImagePath, buffer, count*4, /*OpenCVHelper.MODE_ENHANCE_EXP|*/OpenCVHelper.MODE_REMOVE_NOISE|OpenCVHelper.MODE_GUASS_BLUR, 31, 9 );
//    }

    /**
     * 剪切一个问题的答案图片
     * @param itemData  itemData
     * @return  本地图片路径
     */
    private String cropAnswerImage( WorkSplitView.ItemData itemData ){

        if( photoView==null || mSrcBitmap == null )
            return null;

        //计算显示后的缩放比例
        calImageRate();

        //处理成相对图像的位置
        Rect dstRect = new Rect();
        dstRect.left = (int) (itemData.getLeft() * mSrcBitmap.getWidth());
        dstRect.top = (int)(itemData.getTop() * image_rate);

        dstRect.right = (int) (itemData.getWidth() * mSrcBitmap.getWidth())+dstRect.left;
        if( dstRect.left+dstRect.right > mSrcBitmap.getWidth() )
            dstRect.right = mSrcBitmap.getWidth() - dstRect.left;
        dstRect.bottom = (int)(itemData.getBottom() * image_rate);

        //
        if( dstRect.top > mSrcBitmap.getHeight() ){
            dstRect.top = mSrcBitmap.getHeight();
        }
        if( dstRect.bottom > mSrcBitmap.getHeight() ){
            dstRect.bottom = mSrcBitmap.getHeight();
        }

        if( dstRect.top >= dstRect.bottom )
            return null;

        String cropimagepath = DDWorkManager.getImagePath() + System.currentTimeMillis()+ AppConst.IMAGE_SUFFIX_NAME;
        boolean success = BitmapUtils.cropRectImage( dstRect, mSrcBitmap, cropimagepath, 1, mContext );
        return success? cropimagepath : null;
    }
    private void calImageRate(){


//        Rect rect = new Rect();
//
//        int margin = 0;     //4dp的margin  left right
//
//        int width = photoView.getWidth();
        int height = photoView.getHeight();

//        int cwidth = splitView.getWidth();
//        int cheight = splitView.getHeight();
//
//        int image_width = mSrcBitmap.getWidth();
        int image_height = mSrcBitmap.getHeight();

        image_rate = image_height *1.0 / height;

//        if( width == image_width && height == image_height ){  //两边都不满
//            photoView.getHitRect( rect );
//            image_rate = 1.0;
//        }else if( cwidth == width+2*margin ){    //宽度方向撑满，上下有偏移
//            int offx = (cheight - height)/2;
//            rect.left = margin;
//            rect.top  = offx ;
//            rect.right = cwidth - margin;
//            rect.bottom = cheight - offx;
//
//            image_rate = image_width *1.0 / width;
//        }else{
//            int offx = (cwidth - width)/2;
//            rect.left = offx;
//            rect.top  = 0 ;
//            rect.right = cwidth - offx;
//            rect.bottom = cheight;
//
//            image_rate = image_height *1.0 / height;
//
//        }
    }

}

