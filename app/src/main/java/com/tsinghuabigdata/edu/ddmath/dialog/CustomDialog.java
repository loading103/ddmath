package com.tsinghuabigdata.edu.ddmath.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import java.util.regex.Pattern;


/**
 * 错题回顾 Dialog
 */
public class CustomDialog extends Dialog implements View.OnClickListener {
    //
//    private FrameLayout mainLayout;


    private ImageView tipsImage;

    //普通模式
    private LinearLayout commonLayout;
    private TextView commonTextView;
    private Button leftBtn;
    private Button rightBtn;

    //输入数字模式
    private LinearLayout inputNumLayout;
    private EditText mEditText;
    private Button enterBtn;

    //新用户特权
    private LinearLayout userPriviledgeLayout;
    private ImageView wxcodeView;

    private OnClickListener leftBtnListener;
    private OnClickListener rightBtnListener;

//    public CustomDialog(Context context) {
//        super(context);
//        initData();
//    }

    public CustomDialog(Context context, int theme) {
        super(context, theme);
        initData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_custom_closebtn: {
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
            case R.id.dialog_custom_enterbtn: {
                //检查数字是否符合要求
                enterBtn();
                break;
            }
            default:
                break;
        }
    }

    private void enterBtn() {
        String data = mEditText.getText().toString();
        if (!TextUtils.isEmpty(data) && isNumerEX(data)) {
            if (leftBtnListener != null) leftBtnListener.onClick(this, Integer.valueOf(data));
            dismiss();
        }
    }

    private static boolean isNumerEX(String str) {
        //Pattern pattern = Pattern.compile("-?[0-9]+.?[0-9]+");
        Pattern pattern1 = Pattern.compile("[0-9]*");
        if (/*pattern.matcher(str).matches() ||*/ pattern1.matcher(str).matches()) {
            return true;
        } else {
            return false;
        }
    }

//    public void onBackPressed(){
//        if( leftBtnListener!=null ){
//            leftBtnListener.onClick( this, 0 );
//        }
//    }

//    /**
//     * 设置提示图片
//     */
//    public void setTipsImageView( int resid ){
//        tipsImageView.setImageDrawable( getContext().getResources().getDrawable( resid ));
//    }

    /**
     * 普通提示框
     *
     * @param message          文本信息
     * @param positiveListener 确认回调
     * @param negativeListener 取消回调
     */
    public void setData(String message, String poBtnName, String neBtnName, OnClickListener positiveListener, OnClickListener negativeListener) {

        inputNumLayout.setVisibility(View.GONE);
        commonLayout.setVisibility(View.VISIBLE);
        commonTextView.setText(message);

        leftBtnListener = negativeListener;
        rightBtnListener = positiveListener;

        leftBtn.setText(neBtnName);
        rightBtn.setText(poBtnName);
    }

    /**
     * 普通提示框
     *
     * @param message          文本信息
     * @param positiveListener 确认回调
     */
    public void setData(String message, String poBtnName, OnClickListener positiveListener) {

        inputNumLayout.setVisibility(View.GONE);
        commonLayout.setVisibility(View.VISIBLE);
        commonTextView.setText(message);

        leftBtnListener = positiveListener;
        rightBtnListener = null;

        leftBtn.setText(poBtnName);
        rightBtn.setVisibility(View.GONE);
    }

    /**
     * 普通提示框 无上面的小人  取消是白底蓝字  确认是白字蓝底
     * @param message          文本信息
     * @param positiveListener 确认回调
     */
    public void setCustomData(String message, String poBtnName, String neBtnName, OnClickListener positiveListener, OnClickListener negativeListener) {

        inputNumLayout.setVisibility(View.GONE);
        commonLayout.setVisibility(View.VISIBLE);
        commonTextView.setText(message);

        tipsImage.setVisibility(View.GONE);

        leftBtnListener = negativeListener;
        rightBtnListener = positiveListener;

        leftBtn.setText(neBtnName);
        rightBtn.setText(poBtnName);
        rightBtn.setTextColor(Color.WHITE);
        rightBtn.setBackground( getContext().getResources().getDrawable( R.drawable.bg_rect_blue_r24 ));
    }

    /**
     * 输入数字
     *
     * @param message          显示消息
     * @param btnName          按钮名称
     * @param positiveListener btn名称
     */
    public void setInputNumberData(String message, String btnName, OnClickListener positiveListener) {

        inputNumLayout.setVisibility(View.VISIBLE);
        commonLayout.setVisibility(View.GONE);

        leftBtnListener = positiveListener;
        rightBtnListener = null;

        enterBtn.setText(btnName);
    }

    public void setTextView(Spannable span){
        commonLayout.setVisibility(View.VISIBLE);
        commonTextView.setText(span);
    }

    public void showUserPriviledgeView(){
        userPriviledgeLayout.setVisibility(View.VISIBLE);
    }
    //--------------------------------------------------------------------------
    private void initData() {

        setContentView(GlobalData.isPad()?R.layout.dialog_custom_layout:R.layout.dialog_custom_layout_phone);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            if( window!=null ){
                window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }
        //mainLayout = (FrameLayout)findViewById( R.id.dialog_qreview_mainlayout );
        ImageView closeView = findViewById(R.id.dialog_custom_closebtn);
        closeView.setOnClickListener(this);

        tipsImage  = findViewById(R.id.dialog_qreview_tipimage);
        tipsImage.setVisibility(View.GONE);

        //普通模式
        commonLayout = findViewById(R.id.dialog_custom_commonlayout);

        commonTextView = findViewById(R.id.dialog_custom_text);
        leftBtn =  findViewById(R.id.dialog_custom_leftbtn);
        rightBtn =  findViewById(R.id.dialog_custom_rightbtn);

        leftBtn.setOnClickListener(this);
        rightBtn.setOnClickListener(this);

        //输入数字模式
        inputNumLayout =  findViewById(R.id.dialog_custom_entercountlayout);
        mEditText =  findViewById(R.id.dialog_custom_edittext);
        mEditText.addTextChangedListener(new MyTextWatcher(mEditText));
        mEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View arg0, int keyCode, KeyEvent arg2) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    enterBtn();
                }
                return false;
            }
        });
        enterBtn =  findViewById(R.id.dialog_custom_enterbtn);
        enterBtn.setOnClickListener(this);
        enterBtn.setEnabled(false);

        //
        userPriviledgeLayout = findViewById(R.id.layout_newuser_priviledge);
        //wxcodeView = findViewById(R.id.iv_weixin_code);
    }

    public void setRightBtnAttr(int bgresid, int textcolresid) {
        if (rightBtn != null) {
            rightBtn.setBackgroundResource(bgresid);
            rightBtn.setTextColor(getContext().getResources().getColor(textcolresid));
        }
    }

    class MyTextWatcher implements TextWatcher {

        private CharSequence temp;
        private int editStart;
        private int editEnd;

        private EditText mEditText;
        private int max_count;

        /*public*/ MyTextWatcher(EditText editText) {
            this.mEditText = editText;
            max_count = 2;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            temp = s;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            editStart = mEditText.getSelectionStart();
            editEnd = mEditText.getSelectionEnd();
            if (temp.length() > max_count) {
                AlertManager.toast(getContext(), "已经超过最大输入字数限制!");
                s.delete(editStart - 1, editEnd);
                mEditText.setText(s);
                mEditText.setSelection(editStart);
            } else {
                String data = s.toString();
                if (data.length() == 0) {
                    enterBtn.setBackground(getContext().getResources().getDrawable(R.drawable.bg_rect_lightblue_r24));
                    enterBtn.setEnabled(false);
                } else {
                    enterBtn.setBackground(getContext().getResources().getDrawable(R.drawable.bg_rect_blue_r24));
                    enterBtn.setEnabled(true);
                }
            }
        }
    }

}
