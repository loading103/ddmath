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
public class DownDialogNew extends Dialog implements View.OnClickListener {

    private ImageView closeView;
    private Button leftBtn;
    private Button rightBtn;
    private View spaceView;

    private TextView mtvcontnt;
    private TextView mtvtitle;
    private LinearLayout mlldown;

    private OnClickListener leftBtnListener;
    private OnClickListener rightBtnListener;
    private OnClickListener closeBtnListener;


    public DownDialogNew(Context context, int theme) {
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

    public void setContentData(String message,  String content,String rightBtnName, OnClickListener positiveListener) {
        mlldown.setVisibility(View.VISIBLE);
        mtvtitle.setText(message);
        mtvcontnt.setText(content);
        leftBtnListener = null;
        rightBtnListener = positiveListener;

        leftBtn.setVisibility(View.GONE);
        spaceView.setVisibility( View.GONE );
        //按钮宽度可能不够
        rightBtn.setText(rightBtnName);
    }
    private void initData() {

        setContentView(GlobalData.isPad()?R.layout.dialog_down_layout:R.layout.dialog_down_layout_phone);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            if( window!=null ){
                window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }
        closeView =  findViewById(R.id.dialog_custom_closebtn);
        closeView.setOnClickListener(this);

        leftBtn =  findViewById(R.id.dialog_custom_leftbtn);
        spaceView = findViewById( R.id.dialog_custom_spaceview );
        rightBtn =  findViewById(R.id.dialog_custom_rightbtn);
        mtvcontnt = findViewById( R.id.tv_content );
        mtvtitle = findViewById( R.id.tv_title );
        mlldown = findViewById( R.id.ll_down_tip );
        leftBtn.setOnClickListener(this);
        rightBtn.setOnClickListener(this);

    }
}
