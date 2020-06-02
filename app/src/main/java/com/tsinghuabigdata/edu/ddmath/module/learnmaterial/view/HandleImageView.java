package com.tsinghuabigdata.edu.ddmath.module.learnmaterial.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.MyRelativeLayout;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.LMPreviewActivity;
import com.tsinghuabigdata.edu.ddmath.util.BitmapUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;

/*
*  图片被扩大了25% 四边都扩大，  调整框的位置
 */
public class HandleImageView extends RelativeLayout implements View.OnClickListener{

    private MyRelativeLayout mainLayout;
    private FrameLayout contentLayout;
    private LinearLayout changeQuestionLayout;
    private ImageView imageView;
    private CaptureView captureView;

    //private ImageView preTextView;
    //private ImageView nextTextView;
    private TextView currQuestionView;

    //关联控制
    private View relateView0;
    private View relateView1;
    private RelativeLayout parentLayout;
    private AnswerSplitView splitView;

    private LMPreviewActivity mActivity;

    public HandleImageView(Context context) {
        super(context);
        initView();
    }

    public HandleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    public HandleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    @Override
    public void onClick(View v) {
        if( v.getId() == R.id.view_crop_enter ){
            //显示关联View
            showRelateView( View.VISIBLE );
            setVisibility( GONE );
            parentLayout.setVisibility( GONE );
            
            //把调整后的区域更新到界面上面
            if( mActivity !=null ) mActivity.adjustAnswerRectCallBack( captureView.getCaptureRect() );

        }else if( v.getId() == R.id.view_crop_cancel ){
            doCancel();
        } else if( v.getId() == R.id.view_pre_question ){

            if( splitView==null ) return;
            //先保存当前的数据 把调整后的区域更新到界面上面
            if( mActivity !=null ) mActivity.adjustAnswerRectCallBack( captureView.getCaptureRect() );

            //切换显示上一题信息
            boolean succ = splitView.doPreRect();
            if( succ )
                splitView.showEditView();
            else
                ToastUtils.show( getContext(), "已经是第一张了" );
        } else if( v.getId() == R.id.view_next_question ){

            if( splitView==null ) return;
            //先保存当前的数据 把调整后的区域更新到界面上面
            if( mActivity !=null ) mActivity.adjustAnswerRectCallBack( captureView.getCaptureRect() );

            //ToastUtils.show( getContext(), "下一题" );
            boolean succ = splitView.doNextRect();
            if( succ )
                splitView.showEditView();
            else
                ToastUtils.show( getContext(), "已经是最后一张了" );
        }
    }

    public void setActivity( LMPreviewActivity activity){
        mActivity = activity;
    }

    public void setRelateView(View relateView0, View relateView1, RelativeLayout layout) {
        this.relateView0 = relateView0;
        this.relateView1 = relateView1;
        this.parentLayout=layout;
    }
    public void setSplitView( AnswerSplitView view){
        splitView = view;
    }
    public void setData(final Bitmap bitmap, final RectF rectF, final AnswerSplitView.ItemData itemData){

        //隐藏关联View
        showRelateView( View.INVISIBLE );

        setVisibility( VISIBLE );
        parentLayout.setVisibility( VISIBLE );

        if( splitView!=null ){
            //显示题目序号
            currQuestionView.setText( String.format( "第%s题",splitView.getQuestionNumInPaper() ) );
            //上一题按钮控制
            //preTextView.setEnabled( splitView.hasPreRect() );
            //下一题按钮
            //nextTextView.setEnabled( splitView.hasNextRect() );
        }

        //在子线程里面等待 布局完成后，
        new Thread(new Runnable() {
            @Override
            public void run() {

                while( mainLayout.getWidth() == 0 )
                    SystemClock.sleep(10);

                //主线程内设置
                post(new Runnable() {
                    @Override
                    public void run() {
                        //等比大小  居中显示
                        int maxw = mainLayout.getWidth();
                        int maxh = mainLayout.getHeight();
                        int height = BitmapUtils.showBestMaxBitmap( bitmap, maxw, maxh, contentLayout );
                        imageView.setImageBitmap( bitmap );

                        //在contentLayout marginTop基础减去 changeQuestionLayout 的高度
                        LayoutParams layoutParamsSrc = (LayoutParams)contentLayout.getLayoutParams();
                        LayoutParams layoutParamsDst = (LayoutParams)changeQuestionLayout.getLayoutParams();

                        int bottom = layoutParamsSrc.topMargin + height;
                        layoutParamsDst.topMargin    = bottom + 30;
                        layoutParamsDst.bottomMargin = bottom + 30 + changeQuestionLayout.getHeight();
                        if( layoutParamsDst.bottomMargin > maxh ){
                            int dis = layoutParamsDst.bottomMargin - maxh;
                            layoutParamsDst.topMargin    = layoutParamsDst.topMargin - dis;
                            layoutParamsDst.bottomMargin = layoutParamsDst.bottomMargin - dis;
                        }
                        //AppLog.d("fdsfdsf topMargin = " + layoutParamsDst.topMargin + ",,, bottomMargin = " + layoutParamsDst.bottomMargin + ",,, maxh = " + maxh );
                        changeQuestionLayout.setLayoutParams( layoutParamsDst );

                        captureView.setCaptureRect( rectF );
                        captureView.setRelateItemData( itemData );
                    }
                });
            }
        }).start();
    }

    public void doCancel(){
        //显示关联View
        showRelateView( View.VISIBLE );
        setVisibility( GONE );
        parentLayout.setVisibility( GONE );
    }
    //----------------------------------------------------------------------------

    void initView() {
        inflate( getContext(), GlobalData.isPad()?R.layout.view_handleimage: R.layout.view_handleimage_phone, this );

        mainLayout= (MyRelativeLayout) findViewById( R.id.view_main_layout );
        mainLayout.setInterceptEvent( true );
        contentLayout=(FrameLayout)findViewById( R.id.view_content_layout );
        imageView = (ImageView)findViewById( R.id.view_crop_image );
        captureView=(CaptureView)findViewById( R.id.view_crop_capture );

        changeQuestionLayout=(LinearLayout)findViewById( R.id.view_changequestion_layout );
        //上一题
        ImageView preTextView   = (ImageView)findViewById( R.id.view_pre_question );
        preTextView.setOnClickListener( this );
        //
        currQuestionView   = (TextView)findViewById( R.id.view_curr_question );
        //下一题
        ImageView nextTextView   = (ImageView)findViewById( R.id.view_next_question );
        nextTextView.setOnClickListener( this );


        TextView enterView = (TextView)findViewById(R.id.view_crop_enter);
        enterView.setOnClickListener( this );
        TextView cancelView = (TextView)findViewById(R.id.view_crop_cancel);
        cancelView.setOnClickListener( this );
    }


    private void showRelateView( int visible ){
        if( relateView0!=null ) relateView0.setVisibility( visible );
        if( relateView1!=null ) relateView1.setVisibility( visible );
    }

}
