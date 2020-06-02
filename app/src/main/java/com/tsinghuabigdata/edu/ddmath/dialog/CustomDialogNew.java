package com.tsinghuabigdata.edu.ddmath.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.DensityUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;


/**
 *  新版 Dialog 用于替换 CustomImageBtnDialog
 */
public class CustomDialogNew extends Dialog implements View.OnClickListener {

    private ImageView closeView;
    private TextView commonTextView;
    private Button leftBtn;
    private Button rightBtn;
    private View spaceView;

    private LinearLayout btnTipsLayout;
    private LinearLayout btnIntriLayout;
    private TextView leftTipsView;
    private TextView rightTipsView;

    private TextView jszdTipsView;
    private TextView ptzdTipsView;

    private View bottomView;            //控制到底部的距离


    private OnClickListener leftBtnListener;
    private OnClickListener rightBtnListener;
    private OnClickListener closeBtnListener;

//    public CustomDialog(Context context) {
//        super(context);
//        initData();
//    }

    public CustomDialogNew(Context context, int theme) {
        super(context, theme);
        initData();
    }

    public void setCloseBtnListener(OnClickListener listener){
        closeBtnListener = listener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_custom_closebtn: {
                if( closeBtnListener!=null )closeBtnListener.onClick( this, 0);
                dismiss();
                break;
            }
            case R.id.dialog_custom_leftbtn: {
                if (leftBtnListener != null) leftBtnListener.onClick(this, 0);
                dismiss();
                break;
            }
            case R.id.dialog_custom_rightbtn: {
                if (rightBtnListener != null) rightBtnListener.onClick(this, 0);
                dismiss();
                break;
            }
            default:
                break;
        }
    }

    public void onBackPressed(){
        if( closeBtnListener!=null ){
            closeBtnListener.onClick( this, 0 );
        }
    }

    public Button getLeftBtn(){
        return leftBtn;
    }
    public Button getRightBtn(){
        return rightBtn;
    }

    /**
     * 普通提示框  两个按钮
     */
    public void setData(String message, String leftBtnName, String rightBtnName, OnClickListener leftListener, OnClickListener rightListener) {
        setData( message, leftBtnName,rightBtnName,null, leftListener,rightListener );
    }

    public void setData(String message, String leftBtnName, String rightBtnName, String leftTips, OnClickListener leftListener, OnClickListener rightListener) {
        setData( message, leftBtnName, rightBtnName, leftTips, null, leftListener, rightListener);
    }

    public void setData(String message, String leftBtnName, String rightBtnName, String leftTips, String rightTips, OnClickListener leftListener, OnClickListener rightListener) {

        commonTextView.setText(message);

        boolean hasTips = !TextUtils.isEmpty(leftTips) || !TextUtils.isEmpty(rightTips);

        btnTipsLayout.setVisibility( hasTips?View.VISIBLE:View.GONE );
        bottomView.setVisibility( hasTips?View.GONE:View.VISIBLE );

        if( !TextUtils.isEmpty(leftTips) )       leftTipsView.setText( leftTips );
        if( !TextUtils.isEmpty(rightTips) )      rightTipsView.setText( rightTips );

        if( !TextUtils.isEmpty(leftTips) && !TextUtils.isEmpty(rightTips) ){
            btnIntriLayout.setVisibility( View.VISIBLE );
            int h = DensityUtils.dp2px(getContext(), GlobalData.isPad()?60:40);
            commonTextView.setMinHeight( h );
            commonTextView.setMinimumHeight(h);

            String title  = "极速智能诊断：";
            String content  = "基于人脑记忆规律设计，让老师和学生当天获取诊断结果，并在当天订正错题，从而减轻第二天的课堂学习负担，提升学生的学习效率并帮助老师实现精准教学；根据全国实际使用师生的数据反馈，该模式可较传统模式效率提高数倍，从而显著提升学习平均成绩（详见官网）。";
            Spannable span = new SpannableString( title+content );
            span.setSpan( new ForegroundColorSpan( getContext().getResources().getColor(R.color.color_333333)), 0, title.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            jszdTipsView.setText( span );

            title  = "普通诊断：";
            content  = "老师和学生得到诊断结果的时间不确定，与传统教与学模式相比，学生的学习效率和成绩可以得到一定的提升，但效果弱于极速智能诊断。";
            span = new SpannableString( title+content );
            span.setSpan( new ForegroundColorSpan( getContext().getResources().getColor(R.color.color_333333)), 0, title.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            ptzdTipsView.setText( span );
        }

        leftBtnListener = leftListener;
        rightBtnListener = rightListener;

        leftBtn.setText(leftBtnName);
        rightBtn.setText(rightBtnName);
    }

    /**
     * 普通提示框   一个按钮
     *
     * @param message          文本信息
     * @param positiveListener 确认回调
     */
    public void setData(String message, String rightBtnName, OnClickListener positiveListener) {

        btnTipsLayout.setVisibility(View.GONE);

        commonTextView.setText(message);

        leftBtnListener = null;
        rightBtnListener = positiveListener;

        leftBtn.setVisibility(View.GONE);
        spaceView.setVisibility( View.GONE );

        //按钮宽度可能不够
        if( !TextUtils.isEmpty(rightBtnName) && rightBtnName.length() > 6 ) {
            int count = rightBtnName.length();
            int width = DensityUtils.dp2px( getContext(), GlobalData.isPad()?180:120) + count * DensityUtils.sp2px( getContext(), GlobalData.isPad()?22:14 );
            ViewGroup.LayoutParams layoutParams = rightBtn.getLayoutParams();
            layoutParams.width = width;
            rightBtn.setLayoutParams(layoutParams);
        }
        rightBtn.setText(rightBtnName);
    }

//    public void setRightBtnAttr(int width ) {
//        if (rightBtn != null) {
//            ViewGroup.LayoutParams layoutParams = rightBtn.getLayoutParams();
//            layoutParams.width = width;
//            rightBtn.setLayoutParams( layoutParams );
//        }
//    }

    public void setTextView( Spannable spannable ){
        commonTextView.setText(spannable);
    }
    public void setCloseViewVisibility( int visibility ){
        closeView.setVisibility( visibility );
    }
    //--------------------------------------------------------------------------
    private void initData() {

        setContentView(GlobalData.isPad()?R.layout.dialog_customnew_layout:R.layout.dialog_customnew_layout_phone);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            if( window!=null ){
                window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }
        closeView =  findViewById(R.id.dialog_custom_closebtn);
        closeView.setOnClickListener(this);

        commonTextView =  findViewById(R.id.dialog_custom_text);
        leftBtn =  findViewById(R.id.dialog_custom_leftbtn);
        spaceView = findViewById( R.id.dialog_custom_spaceview );
        rightBtn =  findViewById(R.id.dialog_custom_rightbtn);
        bottomView = findViewById( R.id.dialog_custom_bottomspace );

        leftBtn.setOnClickListener(this);
        rightBtn.setOnClickListener(this);

        btnTipsLayout = findViewById( R.id.layout_btn_tips );
        leftTipsView  = findViewById( R.id.dialog_custom_leftview );
        rightTipsView = findViewById( R.id.dialog_custom_rightview );
        btnIntriLayout = findViewById(R.id.layout_btn_intro);

        btnTipsLayout.setVisibility( View.GONE );       //默认不显示
        btnIntriLayout.setVisibility( View.GONE );

        jszdTipsView = findViewById(R.id.tv_jszd_tips);
        ptzdTipsView = findViewById(R.id.tv_ptzd_tips);
    }
}
