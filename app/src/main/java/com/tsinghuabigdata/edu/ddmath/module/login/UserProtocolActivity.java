package com.tsinghuabigdata.edu.ddmath.module.login;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.parent.view.ParentToolbar;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

public class UserProtocolActivity extends RoboActivity {

    public final static String PARAM_FROM_PARENT = "fromParent";

    @ViewInject(R.id.worktoolbar)
    private WorkToolbar toolbar;
    @ViewInject(R.id.parenttoolbar)
    private ParentToolbar parentToolbar;
    //private TextView tvProtocol;
    @ViewInject(R.id.pdfView)
    private PDFView pdfView;

    private boolean isFromParent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        super.onCreate(savedInstanceState);
        // 设置全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        isFromParent = getIntent().getBooleanExtra(PARAM_FROM_PARENT,false);
        setContentView(GlobalData.isPad() ? R.layout.activity_user_protocol : R.layout.activity_user_protocol_phone);

        x.view().inject(this);
        initView();
    }

    private void initView() {
        if( isFromParent ){
            toolbar.setVisibility( View.GONE );
            parentToolbar.setTitle("用户隐私与使用协议");
            parentToolbar.setClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }else{
            parentToolbar.setVisibility( View.GONE );
            toolbar.setTitle("用户隐私与使用协议");
            toolbar.setClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            },null);
        }
        showPdfView();
    }

    private void showPdfView(){
        try{
            PDFView.Configurator configurator = pdfView.fromAsset("protocol.pdf");
            //.fromUri( url.toURI() )
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
                    //.onPageScroll( pageScrollListener )
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
                    .spacing(0)
//                    .pageFitPolicy(FitPolicy.WIDTH)
                    .onLoad(new OnLoadCompleteListener() {
                        @Override
                        public void loadComplete(int nbPages) {
                            pdfView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    pdfView.fitToWidth();
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

    }

}
