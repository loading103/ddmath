package com.tsinghuabigdata.edu.ddmath.module.ddwork.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.tsinghuabigdata.edu.ddmath.MVPModel.MyStudyModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.CorrectReasonView;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;


/**
 * 错题纠错 Dialog
 */
public class CorrectErrorDialog extends Dialog implements View.OnClickListener {

    private final static int MAX_COUNT = 100;
    //关闭按钮
    //private ImageView closeBtn;

    //题目信息
    private TextView qustionInofView;

    // 申述原因
    private CorrectReasonView correctReasonView;

    //
//    private EditText mEditText;

    //字数
//    private TextView wordCountView;

    //提交状态
    private TextView submitStatusView;

    //private Button cancelBtn;
    private Button enterBtn;

    private MyStudyModel myStudyModel;

    private OnClickListener cancelBtnListener;
    private OnClickListener enterBtnListener;

    private boolean isRevise;
    private JSONObject questionJson;     //保存题目相关信息

    private Dialog self;

    public CorrectErrorDialog(Context context) {
        super(context);
        initData();
    }

    public CorrectErrorDialog(Context context, int theme) {
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
                if (cancelBtnListener != null) cancelBtnListener.onClick(this, 0);
                dismiss();
                break;
            }
            case R.id.dialog_custom_rightbtn: {
                submitApply();
                break;
            }
            default:
                break;
        }
    }

    public void onBackPressed(){
        if( cancelBtnListener!=null ){
            cancelBtnListener.onClick( this, 0 );
        }
        dismiss();
    }

    /**
     * @param data             文本信息
     * @param positiveListener 确认回调
     * @param negativeListener 取消回调
    */
    public void setData(String data, boolean revise, JSONObject json, boolean fromErrQuestion, OnClickListener positiveListener, OnClickListener negativeListener) {
        if( !fromErrQuestion && !GlobalData.isPad() ){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        qustionInofView.setText( data );
        isRevise = revise;
        questionJson = json;
        cancelBtnListener = negativeListener;
        enterBtnListener = positiveListener;
    }

    //--------------------------------------------------------------------------
    private void initData() {
        self = this;
        setContentView(GlobalData.isPad()?R.layout.dialog_correcterror_layout:R.layout.dialog_correcterror_layout_phone);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            if( window!=null ){
                window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }

        ImageView closeView = (ImageView) findViewById(R.id.dialog_custom_closebtn);
        closeView.setOnClickListener(this);

        qustionInofView = (TextView) findViewById(R.id.dialog_custom_question);

//        mEditText = (EditText) findViewById(R.id.dialog_custom_edittext);
//        wordCountView = (TextView)findViewById( R.id.dialog_custom_workcount);
//        mEditText.addTextChangedListener(new MyTextWatcher(mEditText));
//        mEditText.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View arg0, int keyCode, KeyEvent arg2) {
//                if (keyCode == KeyEvent.KEYCODE_ENTER) {
//                    submitApply();
//                }
//                return false;
//            }
//        });

        correctReasonView = (CorrectReasonView) findViewById(R.id.correctReasonView);
        correctReasonView.setReasons( R.array.correct_reason );

        Button cancelBtn = (Button) findViewById(R.id.dialog_custom_leftbtn);
        enterBtn = (Button) findViewById(R.id.dialog_custom_rightbtn);

        submitStatusView = (TextView)findViewById( R.id.dialog_custom_submitstatus );

        cancelBtn.setOnClickListener(this);
        enterBtn.setOnClickListener(this);

        myStudyModel = new MyStudyModel();
    }

    private void submitApply(){

        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if( detailinfo == null ){
            if (cancelBtnListener != null) cancelBtnListener.onClick(this, 0);
            ToastUtils.showShort( getContext(), "请先登录!");
            dismiss();
            return;
        }
        if( questionJson == null ){
            dismiss();
            return;
        }

        if( TextUtils.isEmpty(correctReasonView.getSelectedData()) ){
            ToastUtils.show( getContext(), "请选择申述原因", Toast.LENGTH_SHORT );
            return;
        }

        //
//        questionJson.put("desc", mEditText.getText().toString().trim() );
        questionJson.put("desc", correctReasonView.getSelectedData() );
        questionJson.put("studentName", detailinfo.getReallyName() );
        questionJson.put("studentId", detailinfo.getStudentId() );

        enterBtn.setEnabled( false );
        setSubmitStatus( 1 );
        myStudyModel.applyCorrectErrorQuestion(detailinfo.getStudentId(), isRevise, questionJson.toJSONString(), new RequestListener() {
            @Override
            public void onSuccess(Object res) {
                if( res instanceof Boolean && (Boolean)res ){
                    if (enterBtnListener != null) enterBtnListener.onClick(self, 0);
                    ToastUtils.showToastCenter( getContext(), "提交成功！");
                    setSubmitStatus( 2 );
                    new Thread(new Runnable() {         //2s后自动关闭
                        public void run() {
                            SystemClock.sleep(2000);
                            if (self != null && self.isShowing()) {
                                self.dismiss();
                            }
                        }
                    }).start();
                }else{
                    ToastUtils.showToastCenter( getContext(), "提交失败，请重试！");
                    setSubmitStatus( 3 );
                    enterBtn.setEnabled( true );
                }
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                AlertManager.showErrorInfo( getContext(), ex );
                enterBtn.setEnabled( true );
                setSubmitStatus( 3 );
            }
        });
    }

    // 1：提交中  2：提交成功  3：提交失败
    private void setSubmitStatus( int status ){
        if( 1 == status ){
            submitStatusView.setVisibility( View.VISIBLE );
            submitStatusView.setText( "提交中..." );
            submitStatusView.setTextColor( getContext().getResources().getColor( R.color.color_66C9E1) );
        }else if( 2 == status ){
            submitStatusView.setVisibility( View.VISIBLE );
            submitStatusView.setText( "提交成功！" );
            submitStatusView.setTextColor( getContext().getResources().getColor( R.color.color_96C758) );
        }else if( 3 == status ){
            submitStatusView.setVisibility( View.VISIBLE );
            submitStatusView.setText( "提交失败，请重试！" );
            submitStatusView.setTextColor( getContext().getResources().getColor( R.color.color_DD7070) );
        }else{
            submitStatusView.setVisibility( View.INVISIBLE );
        }
    }

  /*  private class MyTextWatcher implements TextWatcher {

        private CharSequence temp;
        private int editStart;
        private int editEnd;

        private EditText mEditText;

        MyTextWatcher(EditText editText) {
            this.mEditText = editText;
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
            if (temp.length() > MAX_COUNT) {
                AlertManager.toast(getContext(), "已经超过最大输入字数限制!");
                s.delete(editStart - 1, editEnd);
                mEditText.setText(s);
                mEditText.setSelection(editStart);
            } else {
                wordCountView.setText( String.valueOf(s.length()) );
            }
        }
    }*/

}
