package com.tsinghuabigdata.edu.ddmath.dialog;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.AppUtils;
import com.tsinghuabigdata.edu.ddmath.util.DensityUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;


/**
 * 发现新版本Dialog
 */
public class UpgradeDialog extends BaseDialog implements View.OnClickListener {


    private TextView mTvInfo;
    private Button mBtnNow;
    private Button mBtnAfter;

    private UpradeListener mUpradeListener;


    public UpgradeDialog(Context context) {
        super(context);
        initData();
    }

    public UpgradeDialog(Context context, int theme) {
        super(context, theme);
        initData();
    }

    private void initData() {
        level = 3;
        if (GlobalData.isPad()){
            setContentView(R.layout.dialog_upgrade);
        }else {
            setContentView(R.layout.dialog_upgrade_phone);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            if( window!=null ){
                window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }
        mTvInfo = (TextView) findViewById(R.id.tv_info);
        mBtnNow = (Button) findViewById(R.id.btn_now);
        mBtnAfter = (Button) findViewById(R.id.btn_after);
        mBtnNow.setOnClickListener(this);
        mBtnAfter.setOnClickListener(this);

        setCancelable(false);
        setCanceledOnTouchOutside(false);

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        int w;
        int h;
        if (GlobalData.isPad()){
            w = 654;
            h = 543;
        }else {
            w = 367;
            h = 273;
        }
        lp.width = DensityUtils.dp2px(getContext(), w);
        lp.height = DensityUtils.dp2px(getContext(), h);
        lp.dimAmount = 0.5f;
        dialogWindow.setAttributes(lp);
        //        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_now:
                //先检查是否可以使用下载管理器
                if(AppUtils.isDownloadManagerAvailable( getContext())){
                    mUpradeListener.upradeNow();
                }else{
                    ToastUtils.showToastCenter( getContext(),"不能自动下载升级包，请去官网自行下载安装。" );

                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse("http://www.doudoushuxue.com/download.html");
                    intent.setData(content_url);
                    getContext().startActivity(intent);
                }
                finishDismiss();
                break;
            case R.id.btn_after:
                finishDismiss();
                mUpradeListener.upradeAfter();
                break;
            default:
                finishDismiss();
                break;
        }
    }

    public interface UpradeListener {
        void upradeNow();
        void upradeAfter();
    }

    public void setUpradeListener(UpradeListener upradeListener) {
        mUpradeListener = upradeListener;
    }

    /**
     * 普通提示框
     */
    public void setInfo(String message) {
        mTvInfo.setText(message);
    }

    public void setInforce(boolean inforce) {
        if (inforce){
            mBtnAfter.setVisibility(View.GONE);
        }
    }
}
