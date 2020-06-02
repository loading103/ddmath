package com.tsinghuabigdata.edu.ddmath.module.errorbook;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.tsinghuabigdata.edu.ddmath.MVPModel.ErrorBookModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.event.RefreshStageviewEvent;
import com.tsinghuabigdata.edu.ddmath.event.UploadScorePlanEvent;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.bean.StageReviewBean;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.dialog.ShareDialog;
import com.tsinghuabigdata.edu.ddmath.module.myscore.ScoreEventID;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.ContextUtils;
import com.tsinghuabigdata.edu.ddmath.util.EventBusUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.PreferencesUtils;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;

import org.greenrobot.eventbus.EventBus;
import org.xutils.HttpManager;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.net.URLEncoder;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * PDF文件浏览界面
 */
public class PdfViewActivity extends RoboActivity {

    public final static String PARAM_BEAN     = "bean";

    @ViewInject(R.id.custom_toolbar)
    private WorkToolbar workToolbar;

    @ViewInject(R.id.pdfView)
    private PDFView pdfView;

    @ViewInject(R.id.loadingPager)
    private LoadingPager mLoadingPager;

    private ErrorBookModel errBookModel = new ErrorBookModel();

    private ProgressDialog progressDialog;

    //传递过来的参数
    private StageReviewBean stageReviewBean;

    //private String pdfId;
    private String title;
    private boolean isBuy;
    //private long createTime;

    private AtomicBoolean btnShare = new AtomicBoolean(false);
    //
    private String pdfUrl;
    private String filepath;

    private Callback.Cancelable httpManagerCancelable;

    private Context mContext;


//    public static void startPreviewPdfViewActivity( Context context, String recordId, String title, boolean isBuy, long createTime ){
//        if( context == null ) return;
//        Intent intent = new Intent(context, PdfViewActivity.class);
//        intent.putExtra(PdfViewActivity.PARAM_ID, recordId );
//        intent.putExtra(PdfViewActivity.PARAM_TITLE, title );
//        intent.putExtra(PdfViewActivity.PARAM_ISBUY, isBuy );
//        intent.putExtra(PdfViewActivity.PARAM_CREATETIME,createTime);
//        context.startActivity(intent);
//    }

    public static void startPreviewPdfViewActivity( Context context, StageReviewBean bean ){
        if( context == null ) return;
        Intent intent = new Intent(context, PdfViewActivity.class);
        intent.putExtra(PdfViewActivity.PARAM_BEAN, bean );
        context.startActivity(intent);
    }

    protected int getContentViewId() {
        return GlobalData.isPad()?R.layout.activity_ebook_pdfview:R.layout.activity_ebook_pdfview;
    }

    @Override
    protected void onCreate( Bundle savedInstanceState){
        transparent = true;
        super.onCreate(savedInstanceState);

        //设置全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView( getContentViewId() );

        x.view().inject(this);

        mContext = this;

        if( parseIntent() ){
            ToastUtils.show( mContext, "参数错误", Toast.LENGTH_SHORT );
            finish();
            return;
        }

        if( !checkUsrInfo() ){
            finish();
            return;
        }

        initView();

        //开始下载
        loadPdfUrl();

        filepath = ContextUtils.getExternalCacheDir( this, AppConst.APP_NAME) + "/tmp";
    }

    public void onLeftClick(){
        finish();
    }

    public void onRightClick(){
        if( isBuy && !TextUtils.isEmpty(stageReviewBean.getPdfUrl()) ){
            showShareDialog();
        }else{
            shareUrl( stageReviewBean.getQuestionsId() );
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        //取消下载
        if( httpManagerCancelable!=null && !httpManagerCancelable.isCancelled() ){
            httpManagerCancelable.cancel();
        }

        if( !TextUtils.isEmpty(filepath) ){
            File file = new File(filepath);
            if( file.exists() ) file.delete();
        }
    }

    //----------------------------------------------------------------------
    private boolean checkUsrInfo(){
        LoginInfo loginInfo = AccountUtils.getLoginUser();
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if( loginInfo == null || detailinfo == null ){
            ToastUtils.showShort( mContext, "请登录" );
            return false;
        }
        return true;
    }
    private boolean parseIntent(){
        Intent intent = getIntent();
        if( intent.hasExtra(PARAM_BEAN) ) stageReviewBean = (StageReviewBean) intent.getSerializableExtra(PARAM_BEAN);
        if( stageReviewBean == null ) return false;
        title=stageReviewBean.getScoretitle();
        if(TextUtils.isEmpty(title)){
            title   = stageReviewBean.getTitle();
        }
        isBuy   = stageReviewBean.isHasPrivilege() || stageReviewBean.getDownloadStatus()>0;
        return TextUtils.isEmpty(title);
    }

    private void initView(){

        //标题
        workToolbar.setTitle( title );
        workToolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLeftClick();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRightClick();
            }
        });
    }

    private void loadPdfUrl(){
        UserDetailinfo userDetailinfo = AccountUtils.getUserdetailInfo();
        if( userDetailinfo==null ){
            ToastUtils.show( mContext, "请登录", Toast.LENGTH_SHORT );
            finish();
            return;
        }

        mLoadingPager.showLoading();
        errBookModel.queryStageReviewDetail( userDetailinfo.getStudentId(), stageReviewBean.getQuestionsId(), new RequestListener<String>(){

            @Override
            public void onSuccess(String url) {
                LoginInfo loginInfo = AccountUtils.getLoginUser();
                if( loginInfo == null ){
                    ToastUtils.show( mContext, "请登录", Toast.LENGTH_SHORT );
                    finish();
                    return;
                }
                if( TextUtils.isEmpty(url) ){
                    ToastUtils.show( mContext, "没有找到对应的资源", Toast.LENGTH_SHORT );
                    finish();
                }else{
                    pdfUrl = loginInfo.getFileServer()+url;
                    mLoadingPager.hideall();
                    loadPdfView();

                    //if( isBuy )
                        workToolbar.setRightTitleAndLeftDrawable("分享并下载",R.drawable.ic_share2);
                }
            }
            @Override
            public void onFail(HttpResponse<String> response, Exception ex) {
                mLoadingPager.showFault( ex );
            }
        });
    }

    private void loadPdfView(){

        progressDialog = new ProgressDialog(this);
        RequestParams requestParams = new RequestParams( pdfUrl );
        requestParams.setSaveFilePath( filepath );
        HttpManager httpManager = x.http();
        httpManagerCancelable = httpManager.get(requestParams, new Callback.ProgressCallback<File>() {
            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setMessage("开始加载。。。");
                progressDialog.show();
                progressDialog.setMax(100);
                progressDialog.setProgress(0);
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {

                progressDialog.setMessage("努力加载中。。。");
                progressDialog.setMax(100);
                long progress = current*100/total;
                progressDialog.setProgress((int) progress);
            }

            @Override
            public void onSuccess(File file ) {
                progressDialog.dismiss();

                showPdfView( file );
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ex.printStackTrace();
                ToastUtils.show( mContext, "下载失败，请检查网络和磁盘空间", Toast.LENGTH_SHORT);
                progressDialog.dismiss();
                finish();
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }

    //
//    private boolean showed = false;
//    private OnPageScrollListener pageScrollListener =  new OnPageScrollListener() {
//        @Override
//        public void onPageScrolled(int page, float positionOffset) {
//            //，只能查看部分内容，请先兑换。
//            if( !isBuy ){
//                if( page != 1 ) showed = false;
//                else if( positionOffset >= 1 && !showed ){
//                    showed = true;
//                    ToastUtils.show( mContext, "还未兑换此套题，只能查看部分内容，请先兑换。");
//                }
//            }
//        }
//    };
    private void showPdfView(File file){
        try{
            PDFView.Configurator configurator = pdfView.fromFile( file );
            //.fromUri( url.toURI() )
            //是否已购买,没有购买时只能预览两页
//            if( !isBuy ){
//                configurator.pages(0, 1); // all pages are displayed by default
//            }

            configurator.enableSwipe(true) // allows to block changing pages using swipe
                    .swipeHorizontal(false)
                    .enableDoubletap(true)
                    .defaultPage(0)
                    // allows to draw something on the current page, usually visible in the middle of the screen
                    //        .onDraw(onDrawListener)
                    // allows to draw something on all pages, separately for every page. Called only for visible pages
                    //        .onDrawAll(onDrawListener)
                    //        .onLoad(onLoadCompleteListener) // called after document is loaded and starts to be rendered
                    //        .onPageChange(onPageChangeListener)
                    //                   .onPageScroll( pageScrollListener )
                    //        .onError(onErrorListener)
                    //        .onPageError(onPageErrorListener)
                    //        .onRender(onRenderListener) // called after document is rendered for the first time
                    // called on single tap, return true if handled, false to toggle scroll handle visibility
                    //        .onTap(onTapListener)
                    .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
                    .password(null)
                    .scrollHandle(null)
                    .enableAntialiasing(true) // improve rendering a little bit on low-res screens
                    // spacing between pages in dp. To define spacing color, set view background
                    .spacing(2)
//                    .autoSpacing(false)
//                    .pageFitPolicy(FitPolicy.WIDTH)
                    .onRender(new OnRenderListener() {
                        @Override
                        public void onInitiallyRendered(int pages, float pageWidth, float pageHeight) {
                            pdfView.fitToWidth(0);
                        }
                    })
                    .onLoad(new OnLoadCompleteListener() {
                        @Override
                        public void loadComplete(int nbPages) {
                            pdfView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    pdfView.fitToWidth();
                                    AppLog.d("fffsdafads 1111");
                                }
                            },200);
                        }
                    })
                    //        .linkHandler(DefaultLinkHandler)
                    //       .pageFitPolicy(FitPolicy.WIDTH)
                    .load();
        }catch (Exception e){
            e.printStackTrace();
        }

        //判断是否分享下载过
        if( stageReviewBean.getDownloadStatus() == 0 && !PreferencesUtils.getBoolean(mContext,stageReviewBean.getQuestionsId(), false)){
            PreferencesUtils.putBoolean(mContext,stageReviewBean.getQuestionsId(), true);
            AlertManager.showCustomImageBtnDialog(mContext, "可去分享并下载《"+ title +"》，便于以后复习翻阅～", "开始分享并下载", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    shareUrl( stageReviewBean.getQuestionsId() );
                }
            }, null);
        }
    }

    //分享
    private void shareUrl( String recordId ){

        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if( detailinfo==null ) return;

        if( btnShare.get() ){
            ToastUtils.show( mContext, "请稍候...");
            return;
        }
        btnShare.set( true );

        ErrorBookModel bookModel = new ErrorBookModel();
        bookModel.queryStageReviewShare( detailinfo.getStudentId(), recordId, new RequestListener<String>() {
            @Override
            public void onSuccess(String path) {
                //
                if( TextUtils.isEmpty(path) ){
                    ToastUtils.show( mContext, R.string.nofind_practice );
                }  //开始分享
                else{
                    showShareDialog();
                }
                EventBus.getDefault().post(new RefreshStageviewEvent());
                if(!TextUtils.isEmpty(stageReviewBean.getFrom())){
                    EventBusUtils.postDelay(new UploadScorePlanEvent( "" ), new Handler());//定制学订正成功  就通知刷新
                }
                btnShare.set(false);
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                btnShare.set(false);
                if( 20236 == response.getCode() ){
                    ToastUtils.show( mContext, R.string.share_after_buy );
                }else{
                    ToastUtils.show( mContext, R.string.server_error );
                }
            }
        });
    }
    private void showShareDialog(){
        //LoginInfo loginInfo = AccountUtils.getLoginUser();
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if( detailinfo==null ) return;

        try {
            //String turl = loginInfo.getFileServer() + path + "?filename=" + URLEncoder.encode( detailinfo.getReallyName()+"_"+bean.getTitle()+".pdf","utf-8");
            String turl = pdfUrl + "?filename=" + URLEncoder.encode( detailinfo.getReallyName()+"_" + title+".pdf","utf-8");
            ShareDialog.showShaerDialog(mContext, title, turl, stageReviewBean.getCreateTime() )
                    .setScoreEventId(ScoreEventID.EVENT_DOWNLOAD_WRONG_BOOK)
                    .setContentId( stageReviewBean.getQuestionsId() );
        }catch (Exception e){
            AppLog.i( e.toString(), e);
        }
    }
}

