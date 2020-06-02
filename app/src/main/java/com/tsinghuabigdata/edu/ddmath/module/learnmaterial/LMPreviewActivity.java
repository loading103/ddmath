package com.tsinghuabigdata.edu.ddmath.module.learnmaterial;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.BaseActivity;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.DDWorkManager;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.LocalPageInfo;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.LocalQuestionInfo;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.SelectErrorQuestionView;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.adapter.QuestionIndexAdapter;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.bean.AnswerAreaBean;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.bean.AreaBean;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.view.AnswerSplitView;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.view.HandleImageView;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.BitmapUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.view.HorizontalListView;
import com.tsinghuabigdata.engine.config.TFAndroidConstants;
import com.tsinghuabigdata.engine.util.TFAndroidDetector;
import com.tsinghuabigdata.engine.util.TFProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 原版教辅作业拍照预览/选题界面
 */
public class LMPreviewActivity extends BaseActivity implements View.OnClickListener,SelectErrorQuestionView.SelectListener {

    private static final String PARAM_PAGEINFO  = "pageInfo";
    private static final String PARAM_IMAGEPATH = "imagepath";
    private static final String PARAM_TEACHER   = "teacher";
    private static final String PARAM_BOOKRATE = "bookRate";

    private static final float IMG_OFFSET = 0.025f;
    private static boolean btfDetectorRun = false;

    public static void openActivityForResult( Activity activity, LocalPageInfo pageInfo, String imagePath, int resultCode, boolean teacher, float bookRate ){
        if( activity==null || pageInfo==null || TextUtils.isEmpty(imagePath) ) return;
        Intent intent = new Intent( activity, LMPreviewActivity.class);
        intent.putExtra( PARAM_PAGEINFO, true );
        intent.putExtra( PARAM_IMAGEPATH, imagePath );
        intent.putExtra( PARAM_TEACHER, teacher );
        intent.putExtra( PARAM_BOOKRATE, bookRate );
        activity.startActivityForResult( intent, resultCode );
    }

    //
    private ImageView photoView;            //拍照图片
    private AnswerSplitView splitView;      //切割图
    //private ListView listView;              //题号区

    private RelativeLayout handleLayout;
    private HandleImageView handleImageView;
    private SelectErrorQuestionView selectErrorQuestionView;

    //private LinearLayout qustionsLayout;

    //private TextView tipsView;
    //private RelativeLayout toolbarLayout;
    private ProgressDialog mProgressDialog;

    private Context mContext;
    private LMPreviewActivity mActivity;


    //private int imageShowHeight = 0;
    //private double image_rate = 1;          //高度方向

    //传递过来的参数
    private LocalPageInfo localPageInfo;
    private String srcImagePath;            //图片路径
    private boolean isTeacher;
    private float bookRate;

    private Bitmap mSrcBitmap;              //拍照原图

    private boolean bCropImage = false;

    private ArrayList<LocalQuestionInfo> questionInfoList;

    private QuestionIndexAdapter mAdapter;
    private long useTime;

    //
    private TFAndroidDetector tfAndroidDetector;
    private List<TFProvider.Recognition> resultList;

    @Override
    protected void onCreate( Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //设置全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if( !parseIntent() ){
            ToastUtils.showShort( this, "参数错误" );
            finish();
            return;
        }

        if( bookRate > 1 ){     //横版拍摄
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            setContentView(GlobalData.isPad()?R.layout.activity_lmmaterial_preview_land:R.layout.activity_lmmaterial_preview_land_phone);
        }else{      //竖版拍摄
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setContentView(GlobalData.isPad()?R.layout.activity_lmmaterial_preview:R.layout.activity_lmmaterial_preview_phone);
        }
        mContext = this;
        mActivity= this;
        tfAndroidDetector = TFAndroidDetector.getInstance( getAssets(), TFAndroidConstants.DetectType.judge_symbol_detect_rec );
        AppLog.d("------ tfAndroidDetector = " + tfAndroidDetector );

        initView();

        showData();
        loadImage();
    }

    @Override
    protected void onResume() {
        super.onResume();
        useTime = System.currentTimeMillis();
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppLog.caermaLog( useTime, "LmPreview", localPageInfo );
    }

    @Override
    public void onClick(View v) {

        //确认完成
        if( v.getId() == R.id.lm_preview_enter ){

            if( AppConst.UPLOAD_TYPE_MARKED == localPageInfo.getUploadType() ){

                if( !judgeSelectQuestion() ){
                    ToastUtils.showToastCenter( mContext, "还没有选择题目哦～" );
                    return;
                }

                dealAnswerRect();
                selectErrorQuestionView.setData( localPageInfo.getQuestions(), resultList, AccountUtils.getExamFlag() );
                selectErrorQuestionView.setVisibility(View.VISIBLE);
            }else{
                dealEnter();
            }
        }
        //重新拍照
        else if( v.getId() == R.id.lm_preview_recamera ){
            redo();
        }
    }

    @Override
    public void onSelectEnter() {
        dealEnter();
    }

    @Override
    public void onBackPressed() {
        if( handleImageView.isShown() ){
            handleImageView.doCancel();
        }else if( selectErrorQuestionView.isShown()){
            selectErrorQuestionView.setVisibility(View.GONE);
        }else{
            redo();
        }
    }
    
    //触发刷新SplitView
    public void refreshSplitView( String numInPaper, boolean select ){
        splitView.updateSelectItem( numInPaper, select );
    }

    //调整答案区域方框大小
    public void adjustAnswerRect( Rect zRect, RectF dstRect, AnswerSplitView.ItemData itemData ){
        //先计算图片的缩放比例
        //AppLog.d("klklmjjbkkk  width = " + mSrcBitmap.getWidth() + ",  height = " + mSrcBitmap.getHeight() + " gggg " + mSrcBitmap.getWidth()*1f/mSrcBitmap.getHeight() );
        //AppLog.d("klklmjjbkkk  width = " + splitView.getWidth() + ",  height = " + splitView.getHeight() + " gggg " + splitView.getWidth()*1f/splitView.getHeight());

        float rate = mSrcBitmap.getWidth() * 1f / splitView.getWidth();
        Rect zoomRect = new Rect();
        zoomRect.left = (int)(zRect.left*rate);
        zoomRect.right = (int)(zRect.right*rate);
        zoomRect.top = (int)(zRect.top*rate);
        zoomRect.bottom = (int)(zRect.bottom*rate);

        if( zoomRect.right > mSrcBitmap.getWidth() ){
            zoomRect.right = mSrcBitmap.getWidth();
        }
        if( zoomRect.bottom > mSrcBitmap.getHeight() )
            zoomRect.bottom = mSrcBitmap.getHeight();
        if( zoomRect.bottom <= zoomRect.top ){
            zoomRect.top = 0;
            zoomRect.bottom = mSrcBitmap.getHeight();
        }

        Bitmap bitmap = Bitmap.createBitmap( mSrcBitmap, zoomRect.left, zoomRect.top, zoomRect.width(), zoomRect.height() );

        handleLayout.setVisibility( View.VISIBLE );
        handleImageView.setData( bitmap, dstRect, itemData );
    }

    public void adjustAnswerRectCallBack( RectF dstRect ){
        splitView.updateData( dstRect );
    }
    //----------------------------------------------------------------------
    private boolean judgeSelectQuestion(){
        //判断是否选择了题目
        int count = mAdapter.getCount();
        boolean select = false;
        for( int i=0; i<count; i++ ){
            LocalQuestionInfo bean = mAdapter.getItem(i);
            if( bean!=null) select = select || bean.isSelectCache();
        }
        return select;
    }

    private void dealEnter(){
        mProgressDialog.show();

        //save
        if( cropImages() ){

            mProgressDialog.dismiss();
            //ToastUtils.showToastCenter( mContext, "图片处理成功" );

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
        AppLog.d("delete b = " + b );

        Intent intent = new Intent();
        intent.putExtra( "redo", true );
        setResult( RESULT_OK, intent );

        finish();
    }

    //解析参数,必须在initView之前调用
    private boolean parseIntent(){

        Intent intent = getIntent();
        boolean has = intent.getBooleanExtra( PARAM_PAGEINFO, false );
        if( has )
            localPageInfo = AccountUtils.getLocalPageInfo();
        srcImagePath  = intent.getStringExtra( PARAM_IMAGEPATH );
        isTeacher   = intent.getBooleanExtra( PARAM_TEACHER, false );
        bookRate    = intent.getFloatExtra( PARAM_BOOKRATE, -1);

        return localPageInfo!=null && !TextUtils.isEmpty(srcImagePath)&&bookRate>0;
    }

    private void initView(){

        photoView = findViewById( R.id.lm_preview_photo );
        splitView =  findViewById( R.id.lm_preview_answersplitview );
        splitView.setActivity( this );

        TextView tipsView = findViewById( R.id.lm_preview_tipsview );

        View scrollview  = findViewById( R.id.lm_lmanswer_list );
        scrollview.setVerticalScrollBarEnabled(false);
        if( scrollview instanceof ListView ){
            ((ListView)scrollview).setFastScrollEnabled(false);
        }

        LinearLayout qustionsLayout = findViewById( R.id.lm_lmanswer_layout );
        if( isTeacher ){
            qustionsLayout.setVisibility( View.GONE );                          //老师布置的作业，不显示题目列表
            tipsView.setText( mContext.getText( R.string.lm_preview_tips ));
        }

        //确认拍照
        TextView finishBtn = findViewById( R.id.lm_preview_enter );
        finishBtn.setOnClickListener( this );

        //重拍
        TextView redoBtn = findViewById( R.id.lm_preview_recamera );
        redoBtn.setOnClickListener( this );

        mAdapter = new QuestionIndexAdapter( mContext );
        if( scrollview instanceof ListView ){
            ((ListView)scrollview).setAdapter( mAdapter );
        }else if( scrollview instanceof HorizontalListView ){
            ((HorizontalListView)scrollview).setAdapter( mAdapter );
        }
        mAdapter.setRelateActivity( mActivity );

        handleLayout = findViewById( R.id.lm_preview_handlelayout );
        handleImageView = findViewById( R.id.lm_preview_handleimageview );

        LinearLayout toolbarLayout = findViewById( R.id.lm_preview_oplayout );

        handleImageView.setRelateView( toolbarLayout, tipsView, handleLayout );
        handleImageView.setActivity( this );
        handleImageView.setSplitView( splitView );

        mProgressDialog = new ProgressDialog( mContext );
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage( "图片处理中" );
        //
        selectErrorQuestionView = findViewById(R.id.lm_selectErrorQuestionView);
        selectErrorQuestionView.setSelectListener( this );
        selectErrorQuestionView.setLandScreen( bookRate > 1 );

        if( AppConst.UPLOAD_TYPE_MARKED == localPageInfo.getUploadType() ){
            finishBtn.setText( bookRate < 1 ? "下一步（标记错题）" : "下一步");
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
                            //初始化题目的原始范围
                            questionInfoList = localPageInfo.getQuestions();
                            splitView.setData( questionInfoList );
                        }
                    });
                    break;
                }
            }
        }).start();
    }

    //显示题目序号和答题框信息
    private void showData(){

        ArrayList<LocalQuestionInfo> list = localPageInfo.getQuestions();

        //更新题目序号
        mAdapter.clear();
        mAdapter.addAll( list );
        mAdapter.notifyDataSetChanged();

    }

    private void loadImage(){

        new AsyncTask<Void, Void, Object>() {

            @Override
            protected Object doInBackground(Void... params) {

                if( srcImagePath != null && (new File( srcImagePath )).exists() ){
                    mSrcBitmap = BitmapUtils.decodeBitmap( srcImagePath );
                }
                return mSrcBitmap;
            }

            @Override
            protected void onPostExecute(Object bitmap) {
                super.onPostExecute(bitmap);
                if(bitmap!=null) {
                    Bitmap target_bitmap = (Bitmap)bitmap;

                    if(splitView!=null)splitView.setVisibility(View.VISIBLE);

                    //
                    int maxheight = photoView.getHeight();
                    int maxwidth  = photoView.getWidth();
                    BitmapUtils.showBestMaxBitmap( target_bitmap, maxwidth, maxheight, photoView );
                    photoView.setImageBitmap(target_bitmap);
                    BitmapUtils.showBestMaxBitmap( target_bitmap, maxwidth, maxheight, splitView );  //保持photoView与splitView一样大小

                    setSplitViewDataThread();
                    
                    startDetector();
                } else {
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
                    long time = System.currentTimeMillis();
                    resultList = tfAndroidDetector.recBitmap(mSrcBitmap);
                    btfDetectorRun = false;
                    AppLog.d("------ tfAndroidDetector time = " + (System.currentTimeMillis()-time) );
                    //splitView.setTestData( resultList );
                }
            }
        }).start();
    }
    
    /**
     * 剪切题目答案图片
     * @return 完成情况
     */
    private boolean cropImages() {

        //imageShowHeight = splitView.getHeight();
        if( questionInfoList == null ) return false;

        bCropImage = true;

        localPageInfo.useCacheData();
        //保存答案切图
        for (int i = 0; i < questionInfoList.size(); i++) {
            LocalQuestionInfo questionInfo = questionInfoList.get(i);

            //没有被选中的题目不切
            if( !questionInfo.isSelect() ) continue;

            //先处理答题框
            //调整后的答题框
            ArrayList<AnswerAreaBean> answerAreaList = questionInfo.getAnswerAreaList();
            if( answerAreaList == null ) continue;

            //切图信息
            ArrayList<String> pathList = new ArrayList<>();
            ArrayList<AnswerSplitView.ItemData> list = splitView.getItemList( questionInfo.getChapterName(), questionInfo.getSectionName(), questionInfo.getIndexInPaper(), AnswerSplitView.ItemData.TYPE_ANSWER );
            int index = 0;
            for( AnswerSplitView.ItemData itemData : list ){

                if( index>= answerAreaList.size() ) continue;

                //处理相关数据 同步数据
                AnswerAreaBean answerAreaBean = answerAreaList.get(index);
                index++;

                answerAreaBean.setSerialNum(  itemData.getSerialNum()  );
                AreaBean areaBean = new AreaBean();
                answerAreaBean.setManualArea( areaBean );

                RectF rectF = itemData.getRateRect();
                areaBean.setX( rectF.left );
                areaBean.setY( rectF.top );
                areaBean.setWidth( rectF.width() );
                areaBean.setHeight( rectF.height() );

                //处理图片
                String imagepath = cropAnswerImage( rectF );
                if( TextUtils.isEmpty(imagepath) ) return false;
                pathList.add( imagepath );

            }
            //questionInfo.setAnswerAreaList( answerAreaList );
            questionInfo.setLocalpathList( pathList );

            //再处理辅助图
            list = splitView.getItemList( questionInfo.getChapterName(), questionInfo.getSectionName(), questionInfo.getIndexInPaper(), AnswerSplitView.ItemData.TYPE_FIGURE );
            answerAreaList = new ArrayList<>();
            for( AnswerSplitView.ItemData itemData : list ){

                //再处理相关数据 同步数据
                AnswerAreaBean answerAreaBean = new AnswerAreaBean();
                answerAreaList.add( answerAreaBean );
                answerAreaBean.setSerialNum( itemData.getSerialNum() );
                AreaBean areaBean = new AreaBean();
                answerAreaBean.setManualArea( areaBean );

                RectF rectF = itemData.getRateRect();
                areaBean.setX( rectF.left );
                areaBean.setY( rectF.top );
                areaBean.setWidth( rectF.width() );
                areaBean.setHeight( rectF.height() );
            }
            questionInfo.setFigureAreaList( answerAreaList );
        }
        //整张大图
        localPageInfo.setLocalpath( srcImagePath );

        //触动保存
        if( isTeacher ){
            DDWorkManager ddWorkManager = DDWorkManager.getDDWorkManager();
            if (ddWorkManager != null)
                ddWorkManager.saveData();
        }
        bCropImage = false;
        return true;
    }

    /**
     * 把题框处理到题目上面，供自动识别勾叉使用，这一处理，可以把用户自己调整的也包括进入
     */
    private void dealAnswerRect() {

        //imageShowHeight = splitView.getHeight();
        if( questionInfoList == null ) return;

        //保存答案切图
        for (LocalQuestionInfo questionInfo : questionInfoList ) {

            //没有被选中的题目不切
            if( !questionInfo.isSelectCache() ) continue;

            //先处理答题框
            //调整后的答题框
            ArrayList<RectF> answerAreaList = new ArrayList<>();
            //切图信息
            ArrayList<AnswerSplitView.ItemData> list = splitView.getItemList(  questionInfo.getChapterName(), questionInfo.getSectionName(),questionInfo.getIndexInPaper(), AnswerSplitView.ItemData.TYPE_ANSWER );
            for( AnswerSplitView.ItemData itemData : list ){
                RectF rectF = itemData.getRateRect();
                answerAreaList.add( rectF );
            }
            questionInfo.setAnswerAreaTmpList( answerAreaList );
        }
    }

    /**
     * 剪切一个问题的答案图片
     * @param rectF  rectF
     * @return  本地图片路径
     */
    private String cropAnswerImage( RectF rectF ){

        if( photoView==null || mSrcBitmap == null )
            return null;

        //计算显示后的缩放比例
        //calImageRate();
        float top       = rectF.top - IMG_OFFSET;
        if( top < 0 ) top = 0;
        float left      = rectF.left - IMG_OFFSET;
        if( left < 0 ) left = 0;

        float bottom    = rectF.bottom + IMG_OFFSET;
        if( bottom >1f ) bottom = 1f;
        float right    = rectF.right + IMG_OFFSET;
        if( right >1f ) right = 1f;

        //处理成相对图像的位置
        Rect dstRect = new Rect();
        dstRect.left  = (int) (left * mSrcBitmap.getWidth());
        dstRect.right = (int) (right * mSrcBitmap.getWidth() );
        dstRect.top   = (int) ( top * mSrcBitmap.getHeight() );
        dstRect.bottom= (int) ( bottom * mSrcBitmap.getHeight());

        if( dstRect.right > mSrcBitmap.getWidth() ){
            dstRect.right = mSrcBitmap.getWidth();
        }
        if( dstRect.bottom > mSrcBitmap.getHeight() )
            dstRect.bottom = mSrcBitmap.getHeight();

        if( dstRect.left >= dstRect.right ){
            dstRect.left = 0;
            if( dstRect.left >= dstRect.right ) return null;
        }
        if( dstRect.top >= dstRect.bottom ){
            dstRect.top = 0;
            if( dstRect.top >= dstRect.bottom ) return null;
        }
        String cropimagepath = DDWorkManager.getImagePath() + System.currentTimeMillis()+ AppConst.IMAGE_SUFFIX_NAME;
        boolean success = BitmapUtils.cropRectImage( dstRect, mSrcBitmap, cropimagepath, 1, mContext );
        return success? cropimagepath : null;
    }

}

